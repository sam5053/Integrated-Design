package UC_03;

import UC_01.RealNameStatus;

/*实名认证结果。*/
public class VerifyIdentityResult {
    public IdentityVerificationStatus status;
    public RealNameStatus realNameStatus;
    public String maskedIdNumber;

    public static VerifyIdentityResult failure(IdentityVerificationStatus status) {
        VerifyIdentityResult result = new VerifyIdentityResult();
        result.status = status;
        result.realNameStatus = RealNameStatus.UNVERIFIED;
        return result;
    }

    public static VerifyIdentityResult success(IdentityProfile profile) {
        VerifyIdentityResult result = new VerifyIdentityResult();
        result.status = IdentityVerificationStatus.SUCCESS;
        result.realNameStatus = profile.getRealNameStatus();
        result.maskedIdNumber = profile.getMaskedIdNumber();
        return result;
    }
}
