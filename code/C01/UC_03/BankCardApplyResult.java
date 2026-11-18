package UC_03;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/*银行卡查询结果，仅向前端返回银行名称和脱敏卡号。*/
public class BankCardApplyResult {
    public BankCardApplyStatus status;
    public List<BankCardSummary> bankCards;

    public static BankCardApplyResult failure(BankCardApplyStatus status) {
        BankCardApplyResult result = new BankCardApplyResult();
        result.status = status;
        result.bankCards = Collections.emptyList();
        return result;
    }

    public static BankCardApplyResult success(
            List<BankCardSummary> bankCards) {
        BankCardApplyResult result = new BankCardApplyResult();
        result.status = BankCardApplyStatus.SUCCESS;
        result.bankCards = Collections.unmodifiableList(
                new ArrayList<>(bankCards)
        );
        return result;
    }
}
