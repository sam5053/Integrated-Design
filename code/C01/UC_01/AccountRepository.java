package UC_01;

public interface AccountRepository {
    /*账户仓储接口，定义了账户的持久化操作 */
    public boolean existsByUsername(String username);
    public boolean existsByPhone(String phone);
    public Account save(Account account);//save时添加账户角色为Borrower
}