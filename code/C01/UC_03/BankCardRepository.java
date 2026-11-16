package UC_03;

/*银行卡仓储接口，数据库实现由外部提供。*/
public interface BankCardRepository {

    boolean existsByCardNumber(String cardNumber);

    BankCard save(BankCard bankCard);
}
