export type UserRole = 'BORROWER' | 'REVIEWER'

export type RealNameStatus = 'UNVERIFIED' | 'VERIFIED'

export type RegistrationStatus =
  | 'SUCCESS'
  | 'USERNAME_ALREADY_EXISTS'
  | 'PHONE_ALREADY_EXISTS'
  | 'INVALID_INPUT'

export type LoginStatus =
  | 'SUCCESS'
  | 'INVALID_INPUT'
  | 'ACCOUNT_NOT_FOUND'
  | 'PASSWORD_INCORRECT'
  | 'ROLE_MISMATCH'

export type IdentityVerificationStatus =
  | 'SUCCESS'
  | 'INVALID_INPUT'
  | 'IDENTITY_ALREADY_BOUND'
  | 'EXTERNAL_REJECTED'
  | 'PENDING_CONFIRMATION'

export interface RegisterAccountCommand {
  username: string
  password: string
  phone: string
}

export interface RegisterAccountResult {
  accountId: string | null
  realNameStatus: RealNameStatus | null
  registrationStatus: RegistrationStatus
}

export interface LoginCommand {
  account: string
  password: string
  requestedRole: UserRole
}

export interface LoginResult {
  loginStatus: LoginStatus
  userId: string | null
  role: UserRole | null
  realNameStatus: RealNameStatus | null
  realNameVerificationRequired: boolean
}

export interface VerifyIdentityCommand {
  userId: string
  realName: string
  idNumber: string
}

export interface VerifyIdentityRequest {
  realName: string
  idNumber: string
}

export interface VerifyIdentityResult {
  status: IdentityVerificationStatus
  realNameStatus: RealNameStatus
  maskedIdNumber: string | null
  identityVersion?: number
}

export interface CurrentUser {
  userId: string
  phone?: string
  role: UserRole
  realNameStatus: RealNameStatus
}
