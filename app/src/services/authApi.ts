import type {
  LoginCommand,
  LoginResult,
  RegisterAccountCommand,
  RegisterAccountResult,
  VerifyIdentityRequest,
  VerifyIdentityResult,
} from '@/types/auth'

const API_BASE_URL = (import.meta.env.VITE_API_BASE_URL ?? '').replace(/\/$/, '')
const USE_MOCK = import.meta.env.VITE_USE_MOCK !== 'false'
const reservedUsernames = new Set(['13800138000'])
const reservedPhones = new Set(['13800138000'])

const runtime = globalThis as typeof globalThis & {
  __mockAccount?: {
    accountId: string
    username: string
    phone: string
    password: string
    realNameStatus: 'UNVERIFIED' | 'VERIFIED'
  }
}

const mockAccount = runtime.__mockAccount ?? {
  accountId: 'mock-account-001',
  username: '13800138000',
  phone: '13800138000',
  password: 'Loan@1234',
  realNameStatus: 'UNVERIFIED' as const,
}

runtime.__mockAccount = mockAccount

export async function loginByRole(command: LoginCommand): Promise<LoginResult> {
  if (USE_MOCK) {
    await wait()

    if (command.account !== mockAccount.phone && command.account !== mockAccount.username) {
      throw new Error('账号不存在')
    }

    if (command.password !== mockAccount.password) {
      throw new Error('密码错误')
    }

    if (command.requestedRole !== 'BORROWER') {
      throw new Error('登录角色不匹配')
    }

    return {
      loginStatus: 'SUCCESS',
      userId: mockAccount.accountId,
      role: 'BORROWER',
      realNameStatus: mockAccount.realNameStatus,
      realNameVerificationRequired: mockAccount.realNameStatus === 'UNVERIFIED',
    }
  }

  const result = await sendRequest<LoginResult>({
    url: `${apiBase()}/api/auth/login`,
    method: 'POST',
    data: command,
  })

  if (result.loginStatus !== 'SUCCESS') {
    throw new Error(loginMessage(result.loginStatus))
  }

  return result
}

export async function registerAccount(
  command: RegisterAccountCommand,
): Promise<RegisterAccountResult> {
  let result: RegisterAccountResult

  if (USE_MOCK) {
    await wait()

    if (command.username === mockAccount.username || reservedUsernames.has(command.username)) {
      result = {
        accountId: null,
        realNameStatus: null,
        registrationStatus: 'USERNAME_ALREADY_EXISTS',
      }
    } else if (command.phone === mockAccount.phone || reservedPhones.has(command.phone)) {
      result = {
        accountId: null,
        realNameStatus: null,
        registrationStatus: 'PHONE_ALREADY_EXISTS',
      }
    } else {
      mockAccount.accountId = `mock-${Date.now()}`
      mockAccount.username = command.username
      mockAccount.phone = command.phone
      mockAccount.password = command.password
      mockAccount.realNameStatus = 'UNVERIFIED'

      result = {
        accountId: mockAccount.accountId,
        realNameStatus: 'UNVERIFIED',
        registrationStatus: 'SUCCESS',
      }
    }
  } else {
    result = await sendRequest<RegisterAccountResult>({
      url: `${apiBase()}/api/accounts/register`,
      method: 'POST',
      data: command,
    })
  }

  if (result.registrationStatus !== 'SUCCESS') {
    throw new Error(registerMessage(result.registrationStatus))
  }

  return result
}

export async function verifyIdentity(
  request: VerifyIdentityRequest,
): Promise<VerifyIdentityResult> {
  if (USE_MOCK) {
    await wait()
    mockAccount.realNameStatus = 'VERIFIED'

    return {
      status: 'SUCCESS',
      realNameStatus: 'VERIFIED',
      maskedIdNumber: `${request.idNumber.slice(0, 4)}**********${request.idNumber.slice(-4)}`,
    }
  }

  const result = await sendRequest<VerifyIdentityResult>({
    url: `${apiBase()}/api/identity/verify`,
    method: 'POST',
    data: request,
  })

  if (result.status !== 'SUCCESS') {
    throw new Error(identityMessage(result.status))
  }

  return result
}

function apiBase() {
  if (!API_BASE_URL) {
    throw new Error('请先在 .env.local 中配置后端地址')
  }

  return API_BASE_URL
}

function sendRequest<T>(options: UniApp.RequestOptions): Promise<T> {
  return new Promise((resolve, reject) => {
    uni.request({
      ...options,
      header: {
        'Content-Type': 'application/json',
        ...options.header,
      },
      success(response) {
        if (response.statusCode >= 200 && response.statusCode < 300) {
          resolve(response.data as T)
          return
        }

        reject(new Error(`请求失败：${response.statusCode}`))
      },
      fail() {
        reject(new Error('网络请求失败，请检查后端地址和网络'))
      },
    })
  })
}

function loginMessage(status: LoginResult['loginStatus']) {
  const messages: Record<LoginResult['loginStatus'], string> = {
    SUCCESS: '登录成功',
    INVALID_INPUT: '请填写账号、密码和登录角色',
    ACCOUNT_NOT_FOUND: '账号不存在',
    PASSWORD_INCORRECT: '密码错误',
    ROLE_MISMATCH: '登录角色不匹配',
  }

  return messages[status]
}

function registerMessage(status: RegisterAccountResult['registrationStatus']) {
  const messages: Record<RegisterAccountResult['registrationStatus'], string> = {
    SUCCESS: '注册成功',
    USERNAME_ALREADY_EXISTS: '用户名已存在',
    PHONE_ALREADY_EXISTS: '手机号已注册',
    INVALID_INPUT: '注册信息不完整',
  }

  return messages[status]
}

function identityMessage(status: VerifyIdentityResult['status']) {
  const messages: Record<VerifyIdentityResult['status'], string> = {
    SUCCESS: '实名认证成功',
    INVALID_INPUT: '姓名或身份证号不正确',
    IDENTITY_ALREADY_BOUND: '该身份证号已绑定其他账户',
    EXTERNAL_REJECTED: '实名认证未通过',
    PENDING_CONFIRMATION: '实名认证待确认，请稍后重试',
  }

  return messages[status]
}

function wait() {
  return new Promise((resolve) => setTimeout(resolve, 500))
}
