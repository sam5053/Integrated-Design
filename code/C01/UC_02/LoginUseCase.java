package UC_02;

/*UC-02 登录与身份分流用例接口。*/
public interface LoginUseCase {

    LoginResult loginByRole(LoginCommand command);
}
