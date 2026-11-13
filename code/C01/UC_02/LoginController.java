package UC_02;

/*边界类，接收登录请求并调用登录用例。*/
public class LoginController {
    private final LoginUseCase loginUseCase;

    public LoginController(LoginUseCase loginUseCase) {
        this.loginUseCase = loginUseCase;
    }

    public LoginResult loginByRole(LoginCommand command) {
        return loginUseCase.loginByRole(command);
    }
}
