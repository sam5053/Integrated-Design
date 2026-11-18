package UC_03;

/*边界类，接收实名认证、银行卡绑定和银行卡查询请求。*/
public class IdentityAndBankCardController {
    private final VerifyIdentityUseCase verifyIdentityUseCase;
    private final BindBankCardUseCase bindBankCardUseCase;
    private final BankCardApplyUseCase bankCardApplyUseCase;

    public IdentityAndBankCardController(
            VerifyIdentityUseCase verifyIdentityUseCase,
            BindBankCardUseCase bindBankCardUseCase,
            BankCardApplyUseCase bankCardApplyUseCase) {
        this.verifyIdentityUseCase = verifyIdentityUseCase;
        this.bindBankCardUseCase = bindBankCardUseCase;
        this.bankCardApplyUseCase = bankCardApplyUseCase;
    }

    public VerifyIdentityResult verifyIdentity(
            VerifyIdentityCommand command) {
        return verifyIdentityUseCase.verifyIdentity(command);
    }

    public BindBankCardResult bindBankCard(
            BindBankCardCommand command) {
        return bindBankCardUseCase.bindBankCard(command);
    }

    public BankCardApplyResult queryBankCards(
            BankCardApplyCommand command) {
        return bankCardApplyUseCase.queryBankCards(command);
    }
}
