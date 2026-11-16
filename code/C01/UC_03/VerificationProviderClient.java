package UC_03;

/*C11.2 外部实名核验端口。*/
public interface VerificationProviderClient {

    ExternalVerificationResult verifyIdentity(
            String realName,
            String idNumber);
            
}
