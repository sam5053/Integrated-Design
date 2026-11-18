package UC_02;

import UC_01.RealNameStatus;

/*登录所需的账户信息快照，由账户仓储查询得到。*/
public class LoginAccount {
    private final String userId;
    private final String username;
    private final String phone;
    private final String password;
    private final UserRole role;
    private final RealNameStatus realNameStatus;

    public LoginAccount(String userId,String username,String phone,String password,UserRole role,RealNameStatus realNameStatus) {
        this.userId = userId;
        this.username = username;
        this.phone = phone;
        this.password = password;
        this.role = role;
        this.realNameStatus = realNameStatus;
    }

    public String getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getPhone() {
        return phone;
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
