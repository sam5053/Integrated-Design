package UC_01;

public interface RegisterAccountUseCase {
    /*用例接口，定义了注册账户的操作，接收注册命令并返回注册结果 */
    RegisterAccountResult registerAccount(RegisterAccountCommand command);
}
