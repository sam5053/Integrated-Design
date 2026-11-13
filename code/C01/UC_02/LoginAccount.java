package UC_02;

import UC_01.RealNameStatus;

/*登录所需的账户信息快照，由账户仓储查询得到。*/
public class LoginAccount {
    private final String userId;
    private final String account;
    private final String password;
    private final UserRole role;
    private final RealNameStatus realNameStatus;

    public LoginAccount(String userId,String account,String password,UserRole role,RealNameStatus realNameStatus) {
        this.userId = userId;
        this.account = account;
        this.password = password;
        this.role = role;
        this.realNameStatus = realNameStatus;
    }

    public String getUserId() {
        return userId;
    }

    public String getAccount() {
        return account;
    }

    public String getPassword() {
        return password;
    }

    public UserRole getRole() {
        return role;
    }

    public RealNameStatus getRealNameStatus() {
        return realNameStatus;
    }
}
