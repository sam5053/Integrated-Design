package UC_03;

/*外部实名的标准化结果。*/
public class ExternalVerificationResult {
    private final VerificationResultStatus status;//外部服务返回的标准化状态，便于应用服务处理

    public ExternalVerificationResult(
            VerificationResultStatus status) {
        this.status = status;
    }

    public VerificationResultStatus getStatus() {
        return status;
    }
}
