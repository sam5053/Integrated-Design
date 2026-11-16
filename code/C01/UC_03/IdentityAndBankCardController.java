package UC_03;

/*边界类，接收实名认证和银行卡绑定请求。*/
public class IdentityAndBankCardController {
    private final VerifyIdentityUseCase verifyIdentityUseCase;
    private final BindBankCardUseCase bindBankCardUseCase;

    public IdentityAndBankCardController(
            VerifyIdentityUseCase verifyIdentityUseCase,
            BindBankCardUseCase bindBankCardUseCase) {
        this.verifyIdentityUseCase = verifyIdentityUseCase;
        this.bindBankCardUseCase = bindBankCardUseCase;
    }

    public VerifyIdentityResult verifyIdentity(
            VerifyIdentityCommand command) {
        return verifyIdentityUseCase.verifyIdentity(command);
    }

    public BindBankCardResult bindBankCard(
            BindBankCardCommand command) {
        return bindBankCardUseCase.bindBankCard(command);
    }
}
