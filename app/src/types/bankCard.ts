export type BankCardBindingStatus =
  | 'SUCCESS'
  | 'INVALID_INPUT'
  | 'IDENTITY_NOT_VERIFIED'
  | 'CARD_ALREADY_BOUND'

export type BankCardQueryStatus = 'SUCCESS' | 'INVALID_INPUT'

export interface BindBankCardRequest {
  accountId: string
  cardNumber: string
  bankName: string
}

export interface BindBankCardResult {
  status: BankCardBindingStatus
  cardId: string | null
  maskedCardNumber: string | null
}

export interface QueryBankCardsRequest {
  userId: string
}

export interface BankCardSummary {
  bankName: string
  maskedCardNumber: string
}

export interface QueryBankCardsResult {
  status: BankCardQueryStatus
  bankCards: BankCardSummary[]
}
