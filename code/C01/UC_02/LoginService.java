package UC_02;

import java.util.Objects;

/*登录服务，实现账户、密码和角色校验*/
public class LoginService implements LoginUseCase {
    private final LoginAccountRepository accountRepository;

    public LoginService(LoginAccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }
    

    @Override
    public LoginResult loginByRole(LoginCommand command) {
        if (!isValid(command)) {
            return failure(LoginStatus.INVALID_INPUT);
        }

        LoginAccount account = accountRepository.findByAccount(command.account);

        if (account == null) {
            return failure(LoginStatus.ACCOUNT_NOT_FOUND);
        }

        if (!Objects.equals(account.getPassword(), command.password)) {
            return failure(LoginStatus.PASSWORD_INCORRECT);
        }

        if (account.getRole() != command.requestedRole) {
            return failure(LoginStatus.ROLE_MISMATCH);
        }


        LoginResult result = new LoginResult();
        result.loginStatus = LoginStatus.SUCCESS;
        result.userId = account.getUserId();
        result.role = account.getRole();
        result.realNameStatus = account.getRealNameStatus();
        return result;
    }

    private boolean isValid(LoginCommand command) {
        return command != null
                && command.account != null
                && !command.account.isBlank()
                && command.password != null
                && !command.password.isBlank()
                && command.requestedRole != null;
    }

    private LoginResult failure(LoginStatus status) {
        LoginResult result = new LoginResult();
        result.loginStatus = status;
        return result;
    }

}
