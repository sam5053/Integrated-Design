package UC_02;

import UC_01.RealNameStatus;

/*登录结果，成功时返回会话Token、用户标识、可信角色和实名状态。*/
public class LoginResult {
    public LoginStatus loginStatus;
    public String userId;
    public UserRole role;
    public RealNameStatus realNameStatus;

    public boolean isRealNameVerificationRequired() {
        return loginStatus == LoginStatus.SUCCESS
                && role == UserRole.BORROWER;
    }
}
