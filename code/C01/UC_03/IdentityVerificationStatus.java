package UC_03;

/*实名认证结果状态，用于替代自定义异常返回失败原因。*/
public enum IdentityVerificationStatus {
    SUCCESS,
    INVALID_INPUT,
    IDENTITY_ALREADY_BOUND,
    EXTERNAL_REJECTED,
    PENDING_CONFIRMATION
}
