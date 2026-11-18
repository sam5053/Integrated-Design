package UC_03;

import java.util.List;

/*银行卡仓储接口，数据库实现由外部提供。*/
public interface BankCardRepository {

    boolean existsByCardNumber(String cardNumber);

    BankCard save(BankCard bankCard);

    List<BankCard> findByUserId(String userId);
}
