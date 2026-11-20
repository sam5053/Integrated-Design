<script setup lang="ts">
import { computed, ref } from 'vue'
import { loginByRole } from '@/services/authApi'
import { setCurrentUser } from '@/services/currentUser'

const account = ref('')
const password = ref('')
const showPassword = ref(false)
const loading = ref(false)
const errorMessage = ref('')

const canSubmit = computed(() => {
  return account.value.length > 0 && password.value.length >= 6 && !loading.value
})

async function handleLogin() {
  errorMessage.value = ''

  if (account.value.trim().length === 0) {
    errorMessage.value = '请输入用户名或手机号'
    return
  }

  if (password.value.length < 6) {
    errorMessage.value = '密码不能少于 6 位'
    return
  }

  loading.value = true

  try {
    const response = await loginByRole({
      account: account.value.trim(),
      password: password.value,
      requestedRole: 'BORROWER',
    })

    if (!response.userId || !response.role || !response.realNameStatus) {
      throw new Error('登录响应数据不完整')
    }

    setCurrentUser({
      userId: response.userId,
      phone: /^\d{1,11}$/.test(account.value.trim()) ? account.value.trim() : undefined,
      role: response.role,
      realNameStatus: response.realNameStatus,
    })

    uni.showToast({
      title: '登录成功',
      icon: 'success',
    })

    setTimeout(() => {
      uni.reLaunch({ url: '/pages/home/home' })
    }, 400)
  } catch (error) {
    errorMessage.value = error instanceof Error ? error.message : '登录失败'
  } finally {
    loading.value = false
  }
}

function goRegister() {
  uni.navigateTo({ url: '/pages/register/register' })
}
</script>

<template>
  <view class="page">
    <view class="header">
      <image class="logo" src="/static/brand-icon.png" mode="aspectFill" />
      <text class="brand">融信贷</text>
    </view>

    <view class="form">
      <text class="title">登录</text>
      <text class="hint">请输入用户名或手机号和密码</text>

      <text class="label">用户名或手机号</text>
      <input
        v-model="account"
        class="input"
        type="text"
        aria-label="用户名或手机号"
        :maxlength="64"
        placeholder="请输入用户名或手机号"
      />

      <text class="label">密码</text>
      <view class="password-box">
        <!-- #ifdef H5 -->
        <input
          v-model="password"
          class="password-input"
          :class="{ masked: !showPassword }"
          type="text"
          aria-label="密码"
          :maxlength="32"
          placeholder="请输入密码"
        />
        <!-- #endif -->
        <!-- #ifndef H5 -->
        <input
          v-model="password"
          class="password-input"
          type="text"
          aria-label="密码"
          :password="!showPassword"
          :maxlength="32"
          placeholder="请输入密码"
        />
        <!-- #endif -->
        <text class="toggle" @tap="showPassword = !showPassword">
          {{ showPassword ? '隐藏' : '显示' }}
        </text>
      </view>

      <text v-if="errorMessage" class="error">{{ errorMessage }}</text>

      <button class="button" :disabled="!canSubmit" @tap="handleLogin">
        {{ loading ? '登录中' : '登录' }}
      </button>

      <text class="register" @tap="goRegister">注册账户</text>
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
    calc(var(--status-bar-height) + 44rpx)
    48rpx
    100rpx;
  background: #123f36;
}

.logo {
  width: 76rpx;
  height: 76rpx;
  margin-right: 20rpx;
  border-radius: 14rpx;
}

.brand {
  font-size: 36rpx;
  font-weight: 700;
  color: #ffffff;
}

.form {
  margin: -50rpx 32rpx 0;
  padding: 40rpx 34rpx;
  border-radius: 18rpx;
  background: #ffffff;
}

.title {
  display: block;
  font-size: 42rpx;
  font-weight: 700;
  color: #172421;
}

.hint {
  display: block;
  margin: 12rpx 0 36rpx;
  font-size: 22rpx;
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

.password-box {
  height: 92rpx;
  display: flex;
  align-items: center;
  margin-bottom: 26rpx;
  padding: 0 22rpx;
  border: 1rpx solid #dbe4de;
  border-radius: 10rpx;
  background: #f9fbfa;
}

.password-input {
  height: 88rpx;
  flex: 1;
  font-size: 27rpx;
}

.masked {
  -webkit-text-security: disc;
}

.toggle {
  padding-left: 16rpx;
  font-size: 22rpx;
  color: #176b59;
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

.register {
  display: block;
  margin-top: 28rpx;
  text-align: center;
  font-size: 22rpx;
  color: #176b59;
}
</style>
