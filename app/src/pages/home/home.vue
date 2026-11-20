<script setup lang="ts">
import { computed, ref } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { clearCurrentUser, getCurrentUser } from '@/services/currentUser'
import type { CurrentUser } from '@/types/auth'

const user = ref<CurrentUser | null>(null)
const verified = computed(() => user.value?.realNameStatus === 'VERIFIED')

onShow(() => {
  user.value = getCurrentUser()

  if (!user.value) {
    uni.reLaunch({ url: '/pages/login/login' })
  }
})

function openIdentity() {
  if (verified.value) return
  uni.navigateTo({ url: '/pages/real-name/real-name' })
}

function openLoan() {
  if (!verified.value) {
    uni.showToast({ title: '请先完成实名认证', icon: 'none' })
    return
  }

  uni.showToast({ title: '贷款申请功能暂未开放', icon: 'none' })
}

function openMyLoans() {
  uni.showToast({ title: '我的贷款功能暂未开放', icon: 'none' })
}

function logout() {
  uni.showModal({
    title: '退出登录',
    content: '确定退出当前账号吗？',
    success(result) {
      if (!result.confirm) return

      clearCurrentUser()
      uni.reLaunch({ url: '/pages/login/login' })
    },
  })
}
</script>

<template>
  <view class="page">
    <view class="header">
      <view class="brand">
        <image class="logo" src="/static/brand-icon.png" mode="aspectFill" />
        <text class="brand-name">融信贷</text>
      </view>
      <text class="logout" @tap="logout">退出</text>
    </view>

    <view class="content">
      <view class="user-card">
        <text class="label">用户名</text>
        <text class="username">{{ user?.phone || user?.userId }}</text>
      </view>

      <view v-if="!verified" class="notice">
        <text>该账户尚未完成实名认证</text>
      </view>

      <text class="section-title">常用功能</text>

      <view class="menu">
        <view
          class="menu-item"
          :class="{ disabled: verified }"
          @tap="openIdentity"
        >
          <view class="item-icon">证</view>
          <view class="item-text">
            <text class="item-title">实名认证</text>
            <text class="item-desc">
              {{ verified ? '已完成实名认证' : '认证后可以申请贷款' }}
            </text>
          </view>
          <text v-if="verified" class="check">✓</text>
          <text v-else class="arrow">›</text>
        </view>

        <view class="menu-item" @tap="openLoan">
          <view class="item-icon">借</view>
          <view class="item-text">
            <text class="item-title">贷款申请</text>
            <text class="item-desc">提交贷款申请</text>
          </view>
          <text class="arrow">›</text>
        </view>

        <view class="menu-item" @tap="openMyLoans">
          <view class="item-icon">账</view>
          <view class="item-text">
            <text class="item-title">我的贷款</text>
            <text class="item-desc">查看贷款和还款信息</text>
          </view>
          <text class="arrow">›</text>
        </view>
      </view>
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
  justify-content: space-between;
  padding:
    calc(var(--status-bar-height) + 30rpx)
    40rpx
    100rpx;
  background: #123f36;
}

.brand {
  display: flex;
  align-items: center;
}

.logo {
  width: 70rpx;
  height: 70rpx;
  margin-right: 16rpx;
  border-radius: 14rpx;
}

.brand-name {
  font-size: 34rpx;
  font-weight: 700;
  color: #ffffff;
}

.logout {
  padding: 12rpx 20rpx;
  font-size: 21rpx;
  color: #ffffff;
  border: 1rpx solid rgba(255, 255, 255, 0.3);
  border-radius: 10rpx;
}

.content {
  position: relative;
  margin-top: -60rpx;
  padding: 0 32rpx 40rpx;
}

.user-card,
.menu {
  border-radius: 16rpx;
  background: #ffffff;
}

.user-card {
  display: flex;
  flex-direction: column;
  padding: 30rpx;
}

.label {
  font-size: 21rpx;
  color: #83908b;
}

.username {
  margin-top: 10rpx;
  font-size: 38rpx;
  font-weight: 700;
  color: #172421;
}

.notice {
  margin-top: 18rpx;
  padding: 20rpx;
  font-size: 22rpx;
  color: #9b642f;
  border-radius: 12rpx;
  background: #fff4e8;
}

.section-title {
  display: block;
  margin: 34rpx 0 18rpx;
  font-size: 28rpx;
  font-weight: 700;
  color: #172421;
}

.menu {
  overflow: hidden;
}

.menu-item {
  display: flex;
  align-items: center;
  min-height: 116rpx;
  padding: 0 24rpx;
  border-bottom: 1rpx solid #edf1ee;
}

.menu-item:last-child {
  border-bottom: 0;
}

.menu-item.disabled {
  opacity: 0.72;
}

.item-icon {
  width: 66rpx;
  height: 66rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24rpx;
  font-weight: 700;
  color: #176b59;
  border-radius: 12rpx;
  background: #e9f4ef;
}

.item-text {
  flex: 1;
  display: flex;
  flex-direction: column;
  margin-left: 18rpx;
}

.item-title {
  font-size: 25rpx;
  font-weight: 600;
  color: #172421;
}

.item-desc {
  margin-top: 6rpx;
  font-size: 20rpx;
  color: #83908b;
}

.arrow {
  font-size: 34rpx;
  color: #9aa6a2;
}

.check {
  font-size: 28rpx;
  font-weight: 700;
  color: #20a36f;
}
</style>
