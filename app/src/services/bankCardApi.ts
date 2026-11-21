import { postJson, USE_MOCK } from '@/services/apiClient'
import type {
  BankCardSummary,
  BindBankCardRequest,
  BindBankCardResult,
  QueryBankCardsRequest,
  QueryBankCardsResult,
} from '@/types/bankCard'

interface MockBankCard {
  accountId: string
  cardNumber: string
  bankName: string
  maskedCardNumber: string
}

const runtime = globalThis as typeof globalThis & {
  __mockBankCards?: MockBankCard[]
  __mockVerifiedAccountIds?: Set<string>
}

const mockBankCards = runtime.__mockBankCards ?? []
runtime.__mockBankCards = mockBankCards

export async function queryBankCards(
  request: QueryBankCardsRequest,
): Promise<BankCardSummary[]> {
  let result: QueryBankCardsResult

  if (USE_MOCK) {
    await wait()

    result = request.userId.trim()
      ? {
          status: 'SUCCESS',
          bankCards: mockBankCards
            .filter((card) => card.accountId === request.userId)
            .map(({ bankName, maskedCardNumber }) => ({
              bankName,
              maskedCardNumber,
            })),
        }
      : { status: 'INVALID_INPUT', bankCards: [] }
  } else {
    result = await postJson<QueryBankCardsResult>('/api/bank-cards/query', request)
  }

  if (result.status !== 'SUCCESS') {
    throw new Error('登录状态异常，请重新登录')
  }

  return result.bankCards
}

export async function bindBankCard(
  request: BindBankCardRequest,
): Promise<BindBankCardResult> {
  let result: BindBankCardResult

  if (USE_MOCK) {
    await wait()

    if (!request.accountId.trim() || !request.cardNumber.trim() || !request.bankName.trim()) {
      result = failure('INVALID_INPUT')
    } else if (!runtime.__mockVerifiedAccountIds?.has(request.accountId)) {
      result = failure('IDENTITY_NOT_VERIFIED')
    } else if (mockBankCards.some((card) => card.cardNumber === request.cardNumber)) {
      result = failure('CARD_ALREADY_BOUND')
    } else {
      const cardId = `mock-card-${Date.now()}`
      const maskedCardNumber = maskCardNumber(request.cardNumber)

      mockBankCards.unshift({
        accountId: request.accountId,
        cardNumber: request.cardNumber,
        bankName: request.bankName,
        maskedCardNumber,
      })

      result = {
        status: 'SUCCESS',
        cardId,
        maskedCardNumber,
      }
    }
  } else {
    result = await postJson<BindBankCardResult>('/api/bank-cards/bind', request)
  }

  if (result.status !== 'SUCCESS') {
    throw new Error(bindingMessage(result.status))
  }

  return result
}

function maskCardNumber(cardNumber: string) {
  if (cardNumber.length <= 4) {
    return '**** **** **** ****'
  }

  return `**** **** **** ${cardNumber.slice(-4)}`
}

function bindingMessage(status: BindBankCardResult['status']) {
  const messages: Record<BindBankCardResult['status'], string> = {
    SUCCESS: '银行卡绑定成功',
    INVALID_INPUT: '请补齐银行名称和银行卡号',
    IDENTITY_NOT_VERIFIED: '请先完成实名认证',
    CARD_ALREADY_BOUND: '该银行卡已绑定',
  }

  return messages[status]
}

function failure(status: BindBankCardResult['status']): BindBankCardResult {
  return {
    status,
    cardId: null,
    maskedCardNumber: null,
  }
}

function wait() {
  return new Promise((resolve) => setTimeout(resolve, 300))
}
