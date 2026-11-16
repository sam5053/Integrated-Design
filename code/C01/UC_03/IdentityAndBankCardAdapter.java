package UC_03;

/*C11.2 外部适配器占位实现，后续替换为实际服务调用。(可忽略)*/
public class IdentityAndBankCardAdapter
        implements VerificationProviderClient {

    @Override
    public ExternalVerificationResult verifyIdentity(
            String realName,
            String idNumber) {
        return notConfigured();
    }


    private ExternalVerificationResult notConfigured() {
        return new ExternalVerificationResult(
                VerificationResultStatus.UNKNOWN
        );
    }
}
