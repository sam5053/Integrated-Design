package UC_01;

public class AccountRegistrationController {

    /*边界类，处理注册请求，将注册信息传递给用例 */
    private final RegisterAccountUseCase registerAccountUseCase;

    public AccountRegistrationController(RegisterAccountUseCase registerAccountUseCase) {
        /*构造方法，初始化注册用例 */
        this.registerAccountUseCase = registerAccountUseCase;
    }

    public RegisterAccountResult registerAccount(RegisterAccountCommand command) {
        /*处理注册请求，调用注册用例 */
        return registerAccountUseCase.registerAccount(command);
    }
}
