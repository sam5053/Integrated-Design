<script setup lang="ts">
import { computed, ref } from 'vue'
import { verifyIdentity } from '@/services/authApi'
import { getCurrentUser, setCurrentUser } from '@/services/currentUser'

const name = ref('')
const idCard = ref('')
const loading = ref(false)
const errorMessage = ref('')

const canSubmit = computed(() => {
  return name.value.trim().length > 0 && idCard.value.length > 0 && !loading.value
})

async function submit() {
  errorMessage.value = ''

  if (!name.value.trim()) {
    errorMessage.value = '请输入姓名'
    return
  }

  if (!/^\d{17}[\dXx]$/.test(idCard.value)) {
    errorMessage.value = '请输入正确的身份证号'
    return
  }

  const user = getCurrentUser()
  if (!user) {
    uni.reLaunch({ url: '/pages/login/login' })
    return
  }

  loading.value = true

  try {
    const response = await verifyIdentity({
      accountId: user.userId,
      realName: name.value.trim(),
      idNumber: idCard.value.toUpperCase(),
    })

    setCurrentUser({
      ...user,
      realNameStatus: response.realNameStatus,
    })

    uni.showToast({
      title: '实名认证成功',
      icon: 'success',
    })

    setTimeout(() => {
      uni.reLaunch({ url: '/pages/home/home' })
    }, 500)
  } catch (error) {
    errorMessage.value = error instanceof Error ? error.message : '实名认证失败'
  } finally {
    loading.value = false
  }
}

function goBack() {
  uni.reLaunch({ url: '/pages/home/home' })
}
</script>

<template>
  <view class="page">
    <view class="header">
      <text class="back" @tap="goBack">返回</text>
      <text class="title">实名认证</text>
      <view class="header-space"></view>
    </view>

    <view class="form">
      <text class="label">姓名</text>
      <input
        v-model="name"
        class="input"
        type="text"
        aria-label="姓名"
        :maxlength="20"
        placeholder="请输入姓名"
      />

      <text class="label">身份证号</text>
      <input
        v-model="idCard"
        class="input"
        type="idcard"
        aria-label="身份证号"
        :maxlength="18"
        placeholder="请输入身份证号"
      />

      <text v-if="errorMessage" class="error">{{ errorMessage }}</text>

      <button class="button" :disabled="!canSubmit" @tap="submit">
        {{ loading ? '认证中' : '提交认证' }}
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
  margin: -50rpx 32rpx 0;
  padding: 40rpx 34rpx;
  border-radius: 18rpx;
  background: #ffffff;
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

.error {
  display: block;
  margin-bottom: 20rpx;
  font-size: 22rpx;
  color: #bd3d32;
}

.button {
  width: 100%;
  height: 94rpx;
  display: flex;
  align-items: center;
  justify-content: center;
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
