package UC_03;

/*实名认证用例接口。*/
public interface VerifyIdentityUseCase {

    VerifyIdentityResult verifyIdentity(VerifyIdentityCommand command);
}
