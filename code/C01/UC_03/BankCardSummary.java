package UC_03;

/*银行卡查询返回项，不暴露完整卡号。*/
public class BankCardSummary {
    public final String bankName;
    public final String maskedCardNumber;

    public BankCardSummary(String bankName, String maskedCardNumber) {
        this.bankName = bankName;
        this.maskedCardNumber = maskedCardNumber;
    }

    public static BankCardSummary from(BankCard bankCard) {
        return new BankCardSummary(
                bankCard.getBankName(),
                bankCard.getMaskedCardNumber()
        );
    }
}
