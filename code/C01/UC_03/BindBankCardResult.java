package UC_03;

/*银行卡绑定结果，仅向前端返回脱敏卡号。*/
public class BindBankCardResult {
    public BankCardBindingStatus status;
    public String cardId;
    public String maskedCardNumber;

    public static BindBankCardResult failure(BankCardBindingStatus status) {
        BindBankCardResult result = new BindBankCardResult();
        result.status = status;
        return result;
    }

    public static BindBankCardResult success(BankCard card) {
        BindBankCardResult result = new BindBankCardResult();
        result.status = BankCardBindingStatus.SUCCESS;
        result.cardId = card.getCardId();
        result.maskedCardNumber = card.getMaskedCardNumber();
        return result;
    }
}
