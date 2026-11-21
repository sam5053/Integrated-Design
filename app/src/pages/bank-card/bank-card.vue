<script setup lang="ts">
import { ref } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { queryBankCards } from '@/services/bankCardApi'
import { getCurrentUser } from '@/services/currentUser'
import type { BankCardSummary } from '@/types/bankCard'

const cards = ref<BankCardSummary[]>([])
const verified = ref(false)
const loading = ref(false)
const errorMessage = ref('')

onShow(async () => {
  const user = getCurrentUser()

  if (!user) {
    uni.reLaunch({ url: '/pages/login/login' })
    return
  }

  verified.value = user.realNameStatus === 'VERIFIED'
  if (!verified.value) {
    cards.value = []
    return
  }

  loading.value = true
  errorMessage.value = ''

  try {
    cards.value = await queryBankCards({ userId: user.userId })
  } catch (error) {
    errorMessage.value = error instanceof Error ? error.message : '银行卡加载失败'
  } finally {
    loading.value = false
  }
})

function goBack() {
  uni.navigateBack()
}

function addCard() {
  if (!verified.value) {
    uni.showToast({ title: '请先完成实名认证', icon: 'none' })
    return
  }

  uni.navigateTo({ url: '/pages/bank-card/bind' })
}

function openIdentity() {
  uni.navigateTo({ url: '/pages/real-name/real-name' })
}
</script>

<template>
  <view class="page">
    <view class="header">
      <text class="back" @tap="goBack">返回</text>
      <text class="title">银行卡</text>
      <view class="header-space"></view>
    </view>

    <view class="content">
      <view class="section-heading">
        <text class="section-title">已绑定银行卡</text>
        <text class="card-count">{{ cards.length }} 张</text>
      </view>

      <view v-if="loading" class="state-card">
        <text class="state-title">正在加载</text>
        <text class="state-desc">请稍候</text>
      </view>

      <view v-else-if="errorMessage" class="state-card">
        <text class="state-title">加载失败</text>
        <text class="state-desc">{{ errorMessage }}</text>
      </view>

      <view v-else-if="!verified" class="state-card">
        <view class="state-mark">证</view>
        <text class="state-title">尚未完成实名认证</text>
        <text class="state-desc">完成认证后即可绑定本人银行卡</text>
        <text class="state-action" @tap="openIdentity">去实名认证</text>
      </view>

      <view v-else-if="cards.length === 0" class="state-card">
        <view class="state-mark empty-mark">卡</view>
        <text class="state-title">还没有绑定银行卡</text>
        <text class="state-desc">绑定后可用于贷款申请和还款</text>
      </view>

      <view v-else class="card-list">
        <view v-for="card in cards" :key="`${card.bankName}-${card.maskedCardNumber}`" class="bank-card">
          <view class="card-top">
            <view class="bank-mark">{{ card.bankName.slice(0, 1) }}</view>
            <view class="bank-info">
              <text class="bank-name">{{ card.bankName }}</text>
              <text class="card-type">银行卡</text>
            </view>
          </view>
          <text class="card-number">{{ card.maskedCardNumber }}</text>
        </view>
      </view>

      <button class="add-button" @tap="addCard">
        <text class="plus">+</text>
        <text>新增银行卡</text>
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

.content {
  margin-top: 28rpx;
  padding: 0 32rpx 48rpx;
}

.section-heading {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 6rpx;
}

.section-title {
  font-size: 27rpx;
  font-weight: 700;
  color: #172421;
}

.card-count {
  font-size: 21rpx;
  color: #71807a;
}

.card-list {
  margin-top: 18rpx;
}

.bank-card,
.state-card {
  border: 1rpx solid #e3eae6;
  border-radius: 14rpx;
  background: #ffffff;
}

.bank-card {
  margin-bottom: 18rpx;
  padding: 28rpx;
}

.card-top {
  display: flex;
  align-items: center;
}

.bank-mark,
.state-mark {
  width: 70rpx;
  height: 70rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 27rpx;
  font-weight: 700;
  color: #ffffff;
  border-radius: 12rpx;
  background: #176b59;
}

.bank-info {
  display: flex;
  flex: 1;
  flex-direction: column;
  margin-left: 18rpx;
}

.bank-name {
  font-size: 27rpx;
  font-weight: 600;
  color: #172421;
}

.card-type {
  margin-top: 5rpx;
  font-size: 20rpx;
  color: #83908b;
}

.card-number {
  display: block;
  margin-top: 30rpx;
  font-size: 32rpx;
  font-weight: 600;
  letter-spacing: 2rpx;
  color: #24332f;
}

.state-card {
  display: flex;
  align-items: center;
  flex-direction: column;
  margin-top: 18rpx;
  padding: 52rpx 34rpx;
}

.empty-mark {
  color: #176b59;
  background: #e9f4ef;
}

.state-title {
  margin-top: 22rpx;
  font-size: 27rpx;
  font-weight: 600;
  color: #172421;
}

.state-desc {
  margin-top: 10rpx;
  text-align: center;
  font-size: 21rpx;
  line-height: 1.6;
  color: #83908b;
}

.state-action {
  margin-top: 22rpx;
  font-size: 22rpx;
  color: #176b59;
}

.add-button {
  width: 100%;
  height: 94rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-top: 30rpx;
  font-size: 26rpx;
  font-weight: 600;
  color: #ffffff;
  border-radius: 10rpx;
  background: #123f36;
}

.plus {
  margin-right: 10rpx;
  font-size: 34rpx;
  font-weight: 400;
}
</style>
