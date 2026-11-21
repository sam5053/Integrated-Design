<script setup lang="ts">
import { computed, ref } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { bindBankCard } from '@/services/bankCardApi'
import { getCurrentUser } from '@/services/currentUser'

const bankName = ref('')
const cardNumber = ref('')
const loading = ref(false)
const errorMessage = ref('')

const canSubmit = computed(() => {
  return (
    bankName.value.trim().length > 0 &&
    cardNumber.value.replace(/\s/g, '').length > 0 &&
    cardNumber.value.replace(/\s/g, '').length <= 32 &&
    !loading.value
  )
})

onShow(() => {
  const user = getCurrentUser()

  if (!user) {
    uni.reLaunch({ url: '/pages/login/login' })
    return
  }

  if (user.realNameStatus !== 'VERIFIED') {
    uni.showToast({ title: '请先完成实名认证', icon: 'none' })
    setTimeout(() => {
      uni.redirectTo({ url: '/pages/real-name/real-name' })
    }, 400)
  }
})

async function submit() {
  errorMessage.value = ''

  if (!bankName.value.trim()) {
    errorMessage.value = '请输入银行名称'
    return
  }

  const normalizedCardNumber = cardNumber.value.replace(/\s/g, '')

  if (!/^\d{1,32}$/.test(normalizedCardNumber)) {
    errorMessage.value = '请输入正确的银行卡号'
    return
  }

  const user = getCurrentUser()
  if (!user) {
    uni.reLaunch({ url: '/pages/login/login' })
    return
  }

  loading.value = true

  try {
    await bindBankCard({
      accountId: user.userId,
      cardNumber: normalizedCardNumber,
      bankName: bankName.value.trim(),
    })

    uni.showToast({
      title: '绑定成功',
      icon: 'success',
    })

    setTimeout(() => {
      uni.navigateBack()
    }, 400)
  } catch (error) {
    errorMessage.value = error instanceof Error ? error.message : '银行卡绑定失败'
  } finally {
    loading.value = false
  }
}

function goBack() {
  uni.navigateBack()
}
</script>

<template>
  <view class="page">
    <view class="header">
      <text class="back" @tap="goBack">返回</text>
      <text class="title">新增银行卡</text>
      <view class="header-space"></view>
    </view>

    <view class="form">
      <text class="form-title">填写银行卡信息</text>
      <text class="form-hint">请绑定已完成实名认证的本人银行卡</text>

      <text class="label">开户银行</text>
      <input
        v-model="bankName"
        class="input"
        type="text"
        aria-label="开户银行"
        :maxlength="100"
        placeholder="例如：中国工商银行"
      />

      <text class="label">银行卡号</text>
      <input
        v-model="cardNumber"
        class="input"
        type="number"
        aria-label="银行卡号"
        :maxlength="32"
        placeholder="请输入银行卡号"
      />

      <text class="security-note">完整卡号仅用于提交绑定，页面只展示脱敏卡号</text>
      <text v-if="errorMessage" class="error">{{ errorMessage }}</text>

      <button class="button" :disabled="!canSubmit" @tap="submit">
        {{ loading ? '绑定中' : '确认绑定' }}
      </button>
    </view>
  </view>
</template>

<style scoped>
.page {
  min-height: 100vh;
  background: #f4f7f5;
}

.header {
  display: flex;
  align-items: center;
  padding:
    calc(var(--status-bar-height) + 30rpx)
    40rpx
    100rpx;
  background: #123f36;
}

.back {
  width: 100rpx;
  font-size: 22rpx;
  color: #ffffff;
}

.title {
  flex: 1;
  text-align: center;
  font-size: 34rpx;
  font-weight: 700;
  color: #ffffff;
}

.header-space {
  width: 100rpx;
}

.form {
  margin: -54rpx 32rpx 0;
  padding: 40rpx 34rpx;
  border-radius: 16rpx;
  background: #ffffff;
}

.form-title {
  display: block;
  font-size: 34rpx;
  font-weight: 700;
  color: #172421;
}

.form-hint {
  display: block;
  margin: 10rpx 0 34rpx;
  font-size: 21rpx;
  color: #83908b;
}

.label {
  display: block;
  margin-bottom: 12rpx;
  font-size: 24rpx;
  color: #45534f;
}

.input {
  height: 92rpx;
  margin-bottom: 26rpx;
  padding: 0 22rpx;
  font-size: 27rpx;
  border: 1rpx solid #dbe4de;
  border-radius: 10rpx;
  background: #f9fbfa;
}

.security-note {
  display: block;
  padding: 18rpx 20rpx;
  font-size: 20rpx;
  line-height: 1.6;
  color: #65736e;
  border-radius: 10rpx;
  background: #f2f7f4;
}

.error {
  display: block;
  margin-top: 18rpx;
  font-size: 22rpx;
  color: #bd3d32;
}

.button {
  width: 100%;
  height: 94rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-top: 30rpx;
  font-size: 27rpx;
  font-weight: 600;
  color: #ffffff;
  border-radius: 10rpx;
  background: #123f36;
}

.button[disabled] {
  color: #9aa6a2;
  background: #e8eeeb;
}
</style>
