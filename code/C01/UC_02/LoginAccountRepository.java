package UC_02;

/*账户登录信息仓储接口，数据库实现由外部提供。*/
public interface LoginAccountRepository {

    LoginAccount findByAccount(String account);
}
