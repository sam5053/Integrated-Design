package UC_03;


/*银行卡绑定应用服务。*/
public class BankCardService implements BindBankCardUseCase {
    private final BankCardRepository bankCardRepository;
    private final IdentityProfileRepository identityRepository;

    public BankCardService(
            BankCardRepository bankCardRepository,
            IdentityProfileRepository identityRepository) {
        this.bankCardRepository = bankCardRepository;
        this.identityRepository = identityRepository;
    }

    @Override
    public BindBankCardResult bindBankCard(BindBankCardCommand command) {
        if (!isValid(command)) {//银行卡信息不正确
            return BindBankCardResult.failure(
                    BankCardBindingStatus.INVALID_INPUT
            );
        }

        IdentityProfile profile =
                identityRepository.findByUserId(command.userId);
        if (profile == null || !profile.isVerified()) {//用户未实名认证
            return BindBankCardResult.failure(
                    BankCardBindingStatus.IDENTITY_NOT_VERIFIED
            );
        }

        if (bankCardRepository.existsByCardNumber(command.cardNumber)) {//银行卡已绑定
            return BindBankCardResult.failure(
                    BankCardBindingStatus.CARD_ALREADY_BOUND
            );
        }

        BankCard bankCard = new BankCard(
                command.userId,
                command.cardNumber,
                command.bankName
        );
        BankCard savedCard = bankCardRepository.save(bankCard);

        return BindBankCardResult.success(savedCard);
    }


    private boolean isValid(BindBankCardCommand command) {
        return command != null
                && command.userId != null
                && !command.userId.isBlank()
                && command.cardNumber != null
                && !command.cardNumber.isBlank()
                && command.bankName != null
                && !command.bankName.isBlank();
    }

}
