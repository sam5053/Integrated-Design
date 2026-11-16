package UC_03;

import java.time.Instant;
import java.util.UUID;

/*银行卡实体*/
public class BankCard {
    private final String cardId;
    private final String userId;
    private final String cardNumber;
    private final String bankName;
    private final Instant boundAt;

    public BankCard(
            String userId,
            String cardNumber,
            String bankName) {
        this.cardId = UUID.randomUUID().toString();
        this.userId = userId;
        this.cardNumber = cardNumber;
        this.bankName = bankName;
        this.boundAt = Instant.now();
    }

    public String getCardId() {
        return cardId;
    }

    public String getUserId() {
        return userId;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public String getBankName() {
        return bankName;
    }

    public Instant getBoundAt() {
        return boundAt;
    }

    public String getMaskedCardNumber() {
        return maskCardNumber(cardNumber);
    }

    public static String maskCardNumber(String value) {
        if (value == null || value.length() <= 4) {
            return "**** **** **** ****";
        }
        return "**** **** **** " + value.substring(value.length() - 4);
    }
}
