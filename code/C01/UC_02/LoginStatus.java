package UC_02;

/*登录结果状态，用于替代异常传递失败原因。*/
public enum LoginStatus {
    SUCCESS,
    INVALID_INPUT,
    ACCOUNT_NOT_FOUND,
    PASSWORD_INCORRECT,
    ROLE_MISMATCH
}
