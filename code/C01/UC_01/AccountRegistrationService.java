package UC_01;

public class AccountRegistrationService implements RegisterAccountUseCase {
    /*实现类，处理注册逻辑，调用仓储保存账户信息 */
    private final AccountRepository accountRepository;

    public AccountRegistrationService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public RegisterAccountResult registerAccount(RegisterAccountCommand command){
        RegisterAccountResult result = new RegisterAccountResult();
        
        if (accountRepository.existsByUsername(command.username)) {
            /* 用户名已存在 */
            result.registrationStatus = RegistrationStatus.USERNAME_ALREADY_EXISTS;
            return result;
        }

        if (accountRepository.existsByPhone(command.phone)) {
            /* 手机号已存在 */
            result.registrationStatus = RegistrationStatus.PHONE_ALREADY_EXISTS;
            return result;
        }

        Account account = new Account(command.username, command.password, command.phone);
        accountRepository.save(account);

        result.accountId = account.getAccountId();
        result.realNameStatus = account.getRealNameStatus();
        result.registrationStatus = RegistrationStatus.SUCCESS;
        return result;
    }
}
