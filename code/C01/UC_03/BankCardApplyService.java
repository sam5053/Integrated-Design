package UC_03;

import java.util.ArrayList;
import java.util.List;

/*银行卡查询应用服务。用户默认已实名，不重复读取实名认证档案。*/
public class BankCardApplyService implements BankCardApplyUseCase {
    private final BankCardRepository bankCardRepository;

    public BankCardApplyService(BankCardRepository bankCardRepository) {
        this.bankCardRepository = bankCardRepository;
    }

    @Override
    public BankCardApplyResult queryBankCards(
            BankCardApplyCommand command) {
        if (!isValid(command)) {
            return BankCardApplyResult.failure(
                    BankCardApplyStatus.INVALID_INPUT
            );
        }

        List<BankCard> bankCards =
                bankCardRepository.findByUserId(command.userId);
        List<BankCardSummary> summaries = new ArrayList<>();
        if (bankCards != null) {
            for (BankCard bankCard : bankCards) {
                if (bankCard == null) {
                    continue;
                }
                summaries.add(BankCardSummary.from(bankCard));
            }
        }

        return BankCardApplyResult.success(summaries);
    }

    private boolean isValid(BankCardApplyCommand command) {
        return command != null
                && command.userId != null
                && !command.userId.isBlank();
    }
}
