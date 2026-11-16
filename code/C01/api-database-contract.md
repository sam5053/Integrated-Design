# C01 账户与身份模块接口文档

适用用例：

- UC-01 注册账户
- UC-02 登录与身份分流
- UC-03 实名认证与银行卡绑定

文档版本：2026-11-16  
依据：`code/C01/UC_01`、`UC_02`、`UC_03` 当前 Java 类与接口。

> 当前 Java 工程只包含控制器边界类，没有 Spring MVC 注解、HTTP 会话实现或数据库实现类。因此本文中的 URL、HTTP 状态码和 JSON 字段名属于前后端接入契约；请求与响应业务字段严格依据当前 Command、Result 和 Entity 类。

## 1. 通用约定

### 1.1 请求格式

- 基础路径建议：`/api`
- Content-Type：`application/json; charset=UTF-8`
- 时间格式：ISO-8601，例如 `2026-11-16T10:30:00Z`
- 枚举值：使用 Java 枚举名称，区分大小写
- 表示“无值”的字段：返回 JSON `null`

### 1.2 HTTP 状态建议

| HTTP 状态 | 使用场景 |
|---|---|
| `200 OK` | 请求已被业务正常处理，响应体中的业务状态可能成功也可能失败 |
| `400 Bad Request` | JSON 格式错误、字段类型错误 |
| `500 Internal Server Error` | 未处理的基础设施故障 |

当前代码不使用自定义异常，业务失败通过响应中的枚举状态表示，因此业务失败建议仍返回 `200`，由前端根据状态字段处理。

### 1.3 当前重要限制

- UC-02 当前没有返回会话 Token，也未定义后续请求的认证头格式。
- UC-03 的 `userId` 应来自可信登录会话，不应由浏览器自行提交。
- UC-03 外部核验适配器当前是占位实现，会返回 `UNKNOWN`；接入真实服务前，实名认证结果通常为 `PENDING_CONFIRMATION`。
- UC-03 当前代码没有银行卡归属核验、默认卡和审计功能。
- 当前密码字段未做摘要或加密，数据库直接对应当前模型的 `password` 字段。这是现状，不是生产安全建议。

## 2. UC-01 注册账户

### 2.1 创建账户

`POST /api/accounts/register`

请求体：

```json
{
  "username": "loan_user",
  "password": "123456",
  "phone": "13800138000"
}
```

字段说明：

| 字段 | 类型 | 必填 | 说明 |
|---|---|---|---|
| `username` | string | 是 | 登录用户名，必须唯一 |
| `password` | string | 是 | 当前按原值保存 |
| `phone` | string | 是 | 手机号，必须唯一 |

成功响应：

```json
{
  "accountId": "9d7f34f8-74cf-4cc4-a05c-a40a0d66c3cc",
  "realNameStatus": "UNVERIFIED",
  "registrationStatus": "SUCCESS"
}
```

用户名重复：

```json
{
  "accountId": null,
  "realNameStatus": null,
  "registrationStatus": "USERNAME_ALREADY_EXISTS"
}
```

手机号重复：

```json
{
  "accountId": null,
  "realNameStatus": null,
  "registrationStatus": "PHONE_ALREADY_EXISTS"
}
```

状态说明：

| `registrationStatus` | 前端处理 |
|---|---|
| `SUCCESS` | 进入登录页 |
| `USERNAME_ALREADY_EXISTS` | 提示用户名已占用，可引导登录 |
| `PHONE_ALREADY_EXISTS` | 提示手机号已注册，可引导登录 |
| `INVALID_INPUT` | 枚举已定义，但当前服务没有主动返回该状态 |

当前服务未校验空值，前端仍需进行必填项检查；数据库 `NOT NULL` 与唯一约束是最终防线。

## 3. UC-02 登录与身份分流

### 3.1 按角色登录

`POST /api/auth/login`

请求体：

```json
{
  "account": "loan_user",
  "password": "123456",
  "requestedRole": "BORROWER"
}
```

字段说明：

| 字段 | 类型 | 必填 | 说明 |
|---|---|---|---|
| `account` | string | 是 | 用户名或手机号 |
| `password` | string | 是 | 登录密码 |
| `requestedRole` | enum | 是 | `BORROWER` 或 `REVIEWER` |

登录成功：

```json
{
  "loginStatus": "SUCCESS",
  "userId": "9d7f34f8-74cf-4cc4-a05c-a40a0d66c3cc",
  "role": "BORROWER",
  "realNameStatus": "UNVERIFIED",
  "realNameVerificationRequired": true
}
```

登录失败：

```json
{
  "loginStatus": "ROLE_MISMATCH",
  "userId": null,
  "role": null,
  "realNameStatus": null,
  "realNameVerificationRequired": false
}
```

状态说明：

| `loginStatus` | 前端处理 |
|---|---|
| `SUCCESS` | 按 `role` 进入贷款人首页或审核员工作台 |
| `INVALID_INPUT` | 提示补齐账号、密码和入口角色 |
| `ACCOUNT_NOT_FOUND` | 提示账号不存在 |
| `PASSWORD_INCORRECT` | 提示密码错误 |
| `ROLE_MISMATCH` | 提示选择正确登录入口 |

### 3.2 前端需注意

`realNameVerificationRequired` 是由 `isRealNameVerificationRequired()` 派生的响应字段。当前实现只判断 `role == BORROWER`，没有判断实名状态，因此贷款人即使用户已经实名，也可能返回 `true`。目前前端若需要准确判断，应使用 `realNameStatus == "UNVERIFIED"`。

当前 `LoginResult` 没有 `sessionToken` 字段。后续请求如何证明登录身份，需要由认证会话或网关另行定义。

## 4. UC-03 实名认证

### 4.1 提交实名认证

`POST /api/identity/verify`

外部请求体：

```json
{
  "realName": "张三",
  "idNumber": "110101199001011234"
}
```

字段说明：

| 字段 | 类型 | 必填 | 说明 |
|---|---|---|---|
| `realName` | string | 是 | 真实姓名 |
| `idNumber` | string | 是 | 身份证号，必须唯一归属一个账户 |

`userId` 存在于 Java `VerifyIdentityCommand` 中，但应由后端控制器从可信登录会话填充，不应由前端直接提交。

成功响应：

```json
{
  "status": "SUCCESS",
  "realNameStatus": "VERIFIED",
  "maskedIdNumber": "1101**********1234",
  "identityVersion": 0
}
```

失败响应示例：

```json
{
  "status": "IDENTITY_ALREADY_BOUND",
  "realNameStatus": "UNVERIFIED",
  "maskedIdNumber": null,
  "identityVersion": 0
}
```

状态说明：

| `status` | 含义 | 前端处理 |
|---|---|---|
| `SUCCESS` | 实名认证成功 | 展示脱敏证件号和成功状态 |
| `INVALID_INPUT` | 用户标识、姓名或证件号为空 | 提示修改字段 |
| `IDENTITY_ALREADY_BOUND` | 证件号已绑定其他账户 | 提示身份冲突 |
| `EXTERNAL_REJECTED` | 外部实名核验拒绝 | 提示检查姓名和证件号 |
| `PENDING_CONFIRMATION` | 外部服务返回未知或占位适配器未配置 | 提示稍后重试 |

`identityVersion` 当前没有赋值逻辑，始终为 `0`。前端暂时不应依赖该字段。

## 5. UC-03 银行卡绑定

### 5.1 绑定银行卡

`POST /api/bank-cards/bind`

外部请求体：

```json
{
  "cardNumber": "6222021234567890",
  "bankName": "示例银行"
}
```

字段说明：

| 字段 | 类型 | 必填 | 说明 |
|---|---|---|---|
| `cardNumber` | string | 是 | 银行卡号，当前要求全局唯一 |
| `bankName` | string | 是 | 银行名称 |

`userId` 同样应由服务端登录会话填充。

成功响应：

```json
{
  "status": "SUCCESS",
  "cardId": "ed82d8ec-d136-4e52-9486-5945ed59f9b4",
  "maskedCardNumber": "**** **** **** 7890"
}
```

失败响应：

```json
{
  "status": "IDENTITY_NOT_VERIFIED",
  "cardId": null,
  "maskedCardNumber": null
}
```

状态说明：

| `status` | 含义 | 前端处理 |
|---|---|---|
| `SUCCESS` | 银行卡已保存 | 展示脱敏卡号 |
| `INVALID_INPUT` | 用户标识、卡号或银行名称为空 | 提示补齐字段 |
| `IDENTITY_NOT_VERIFIED` | 当前用户尚未实名认证 | 引导先完成实名认证 |
| `CARD_ALREADY_BOUND` | 卡号已被绑定 | 提示银行卡已存在 |

当前实现没有银行卡归属核验、默认卡解析和银行卡审计。前端不应期望响应中出现 `defaultCardId`。

## 6. 前端推荐调用顺序

```text
1. POST /api/accounts/register
2. POST /api/auth/login
3. POST /api/identity/verify
4. POST /api/bank-cards/bind
```

约束：

- 未注册成功的账户不能登录。
- 未实名账户不能绑定银行卡。
- 银行卡绑定成功前必须存在当前用户的实名档案。
- 所有业务结果都应检查状态字段，不能只依赖 HTTP 200。

## 7. 数据库接口

当前数据库至少需要三张表：

以下 DDL 以 PostgreSQL 为例，其他数据库需替换冲突处理语法和时间类型。

```text
user_account
identity_profile
bank_card

user_account 1 ─── 0..1 identity_profile
user_account 1 ─── 0..* bank_card
```

### 7.1 `user_account`

用于注册、登录、角色和实名状态查询。

```sql
CREATE TABLE user_account (
    account_id       VARCHAR(36) PRIMARY KEY,
    username         VARCHAR(64) NOT NULL UNIQUE,
    password         VARCHAR(255) NOT NULL,
    phone            VARCHAR(32) NOT NULL UNIQUE,
    role             VARCHAR(20) NOT NULL DEFAULT 'BORROWER',
    real_name_status VARCHAR(20) NOT NULL DEFAULT 'UNVERIFIED',
    created_at       TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT chk_user_account_role
        CHECK (role IN ('BORROWER', 'REVIEWER')),
    CONSTRAINT chk_user_account_real_name_status
        CHECK (real_name_status IN ('UNVERIFIED', 'VERIFIED'))
);
```

字段对应关系：

| Java 字段 | 数据库列 | 约束 |
|---|---|---|
| `accountId` | `account_id` | 主键 |
| `username` | `username` | 唯一、非空 |
| `password` | `password` | 非空 |
| `phone` | `phone` | 唯一、非空 |
| `role` | `role` | 默认 `BORROWER` |
| `realNameStatus` | `real_name_status` | 默认 `UNVERIFIED` |
| `createdAt` | `created_at` | 非空 |

`Account` 类本身没有 `role` 字段，但登录模型 `LoginAccount` 需要角色，且仓储注释要求注册账户默认设置为 `BORROWER`，因此数据库需要保存角色。

### 7.2 `identity_profile`

用于保存实名结果。

```sql
CREATE TABLE identity_profile (
    user_id            VARCHAR(36) PRIMARY KEY,
    real_name          VARCHAR(100) NOT NULL,
    id_number          VARCHAR(32) NOT NULL UNIQUE,
    real_name_status   VARCHAR(20) NOT NULL DEFAULT 'VERIFIED',
    verified_at        TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT fk_identity_profile_user
        FOREIGN KEY (user_id) REFERENCES user_account(account_id),
    CONSTRAINT chk_identity_profile_status
        CHECK (real_name_status IN ('UNVERIFIED', 'VERIFIED'))
);
```

数据库操作：

```sql
-- 检查证件号是否被其他账户绑定
SELECT EXISTS (
    SELECT 1
    FROM identity_profile
    WHERE id_number = ?
      AND user_id <> ?
);

-- 查询当前用户实名档案
SELECT *
FROM identity_profile
WHERE user_id = ?;

-- 新增或更新实名档案
INSERT INTO identity_profile (
    user_id,
    real_name,
    id_number,
    real_name_status,
    verified_at
) VALUES (?, ?, ?, 'VERIFIED', ?)
ON CONFLICT (user_id) DO UPDATE SET
    real_name = EXCLUDED.real_name,
    id_number = EXCLUDED.id_number,
    real_name_status = EXCLUDED.real_name_status,
    verified_at = EXCLUDED.verified_at;
```

### 7.3 `bank_card`

用于保存用户银行卡。当前没有默认卡字段和有效性状态字段。

```sql
CREATE TABLE bank_card (
    card_id       VARCHAR(36) PRIMARY KEY,
    user_id       VARCHAR(36) NOT NULL,
    card_number   VARCHAR(32) NOT NULL UNIQUE,
    bank_name     VARCHAR(100) NOT NULL,
    bound_at      TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT fk_bank_card_user
        FOREIGN KEY (user_id) REFERENCES user_account(account_id)
);

CREATE INDEX idx_bank_card_user_id
    ON bank_card(user_id);
```

数据库操作：

```sql
-- 检查银行卡是否已经绑定
SELECT EXISTS (
    SELECT 1
    FROM bank_card
    WHERE card_number = ?
);

-- 保存银行卡
INSERT INTO bank_card (
    card_id,
    user_id,
    card_number,
    bank_name,
    bound_at
) VALUES (?, ?, ?, ?, ?);

-- 当前仓储声明但服务未调用
SELECT *
FROM bank_card
WHERE user_id = ?;
```

`BankCardRepository.findValidByUserId()` 当前没有调用者，数据库也没有“有效性”字段。实现该接口时目前只能返回用户全部银行卡，或等待业务规则补充后再增加状态字段。

## 8. 登录数据库查询

`LoginAccountRepository.findByAccount(account)` 需要同时支持用户名和手机号：

```sql
SELECT
    account_id,
    username,
    password,
    role,
    real_name_status
FROM user_account
WHERE username = ?
   OR phone = ?;
```

应用层使用同一个值填充两个参数。由于 `username` 和 `phone` 都有唯一约束，正常情况下最多返回一条记录。

## 9. 事务与跨表一致性

### 9.1 注册事务

需在同一个事务中完成：

1. 检查用户名唯一性。
2. 检查手机号唯一性。
3. 插入 `user_account`。

仅依赖先查询后插入不能防止并发重复，两个唯一约束必须保留。

### 9.2 实名认证事务

当前 Java 服务只调用 `IdentityProfileRepository.save()`，没有直接更新 `user_account.real_name_status`。为了让后续登录立即看到已实名状态，数据库实现层必须在同一个事务中：

1. 检查 `identity_profile.id_number` 是否已属于其他账户。
2. 新增或更新 `identity_profile`。
3. 更新 `user_account.real_name_status = 'VERIFIED'`。

当前仓储接口尚未定义第 3 步，这是代码与数据库集成时需要补齐的缺口。

### 9.3 银行卡绑定事务

银行卡绑定需要：

1. 确认当前用户实名状态为 `VERIFIED`。
2. 检查 `bank_card.card_number` 唯一性。
3. 插入 `bank_card`。

`card_number` 唯一约束用于防止并发重复绑定。

## 10. 当前未定义的接口或字段

以下内容在旧版设计中存在，但当前 UC-01、UC-02、UC-03 代码中没有实现，前端和数据库不应按旧版接口预留：

- 验证码发送与校验接口
- 注册审计接口
- 登录会话 Token 字段
- 登录失败次数与账户锁定
- 实名授权确认和授权摘要
- 实名失败编号 `IDENTITY_001` 至 `IDENTITY_003`
- 银行卡与本人归属核验
- 银行卡默认卡字段
- 银行卡绑定审计
- 密码摘要或加密字段
