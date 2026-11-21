# 融信贷移动端

面向个人贷款用户的 uni-app 手机端，目前包含登录、注册、用户首页、实名认证和银行卡管理。

## 启动

```powershell
cd app
npm install
npm run dev:app
```

HBuilderX 导入 `app/dist/dev/app` 后运行到 Android App 基座。

## 演示账号

```text
用户名/手机号：13800138000
密码：Loan@1234
```

Mock 模式下注册成功后，可以使用新注册的用户名或手机号登录。

## 连接后端

复制 `.env.example` 为 `.env.local`：

```dotenv
VITE_USE_MOCK=false
VITE_API_BASE_URL=http://电脑局域网IP:8080
```

登录接口：

```http
POST /api/auth/login
Content-Type: application/json
```

```json
{
  "account": "13800138000",
  "password": "Loan@1234",
  "requestedRole": "BORROWER"
}
```

```json
{
  "loginStatus": "SUCCESS",
  "userId": "account-id",
  "role": "BORROWER",
  "realNameStatus": "UNVERIFIED",
  "realNameVerificationRequired": true
}
```

注册接口：

```http
POST /api/accounts/register
Content-Type: application/json
```

```json
{
  "username": "loan_user",
  "phone": "13800138000",
  "password": "Loan@1234"
}
```

```json
{
  "accountId": "account-id",
  "realNameStatus": "UNVERIFIED",
  "registrationStatus": "SUCCESS"
}
```

`registrationStatus` 还可能是 `USERNAME_ALREADY_EXISTS` 或 `PHONE_ALREADY_EXISTS`，页面会分别显示“用户名已存在”和“手机号已注册”。

实名认证接口：

```http
POST /api/identity/verify
Content-Type: application/json
```

```json
{
  "realName": "张三",
  "idNumber": "110101199001011234"
}
```

```json
{
  "status": "SUCCESS",
  "realNameStatus": "VERIFIED",
  "maskedIdNumber": "1101**********1234",
  "identityVersion": 0
}
```

实名认证的 `userId` 由后端从登录会话获取，不由前端提交。

## 源码

- `src/pages/login/login.vue`：登录页面。
- `src/pages/register/register.vue`：注册页面和确认密码校验。
- `src/pages/real-name/real-name.vue`：姓名和身份证号实名认证页面。
- `src/pages/home/home.vue`：用户首页。
- `src/pages/bank-card/bank-card.vue`：已绑定银行卡列表和新增入口。
- `src/pages/bank-card/bind.vue`：银行名称与银行卡号绑定页面。
- `src/services/authApi.ts`：登录、注册和实名认证请求。
- `src/services/bankCardApi.ts`：银行卡查询和绑定请求。
- `src/services/currentUser.ts`：只在当前运行内存中保存用户。
- `src/types/auth.ts`：前后端数据结构。
- `src/types/bankCard.ts`：银行卡接口数据结构。

App 关闭或重新启动后，内存中的当前用户会消失，因此必须重新登录。
