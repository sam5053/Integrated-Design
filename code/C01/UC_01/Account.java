package UC_01;
import java.time.Instant;
import java.util.UUID;

public class Account {
    /*账户类，对每个属性提供了getset方法，以及带参构造方法 */
    private final String accountId;
    private final String username;
    private final String password;
    private final String phone;
    private  RealNameStatus realNameStatus;
    private final Instant createdAt;


    public void setRealNameStatus(RealNameStatus realNameStatus) {
        this.realNameStatus = realNameStatus;
    }

    public String getAccountId() {
        return accountId;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getPhone() {
        return phone;
    }

    public RealNameStatus getRealNameStatus() {
        return realNameStatus;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Account(String username, String password, String phone) {
        /*带参构造方法，生成账户ID，默认未实名认证，提供实例化时间 */
        this.accountId = UUID.randomUUID().toString();
        this.username = username;
        this.password = password;
        this.phone = phone;
        this.realNameStatus = RealNameStatus.UNVERIFIED;
        this.createdAt = Instant.now();
    }

}
