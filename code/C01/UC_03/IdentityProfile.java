package UC_03;

import UC_01.RealNameStatus;

import java.time.Instant;

/*实名资料，认证成功后才提升为 VERIFIED。*/
public class IdentityProfile {
    private final String userId;
    private String realName;
    private String idNumber;
    private RealNameStatus realNameStatus;
    private Instant verifiedAt;

    public IdentityProfile(String userId) {
        this.userId = userId;
        this.realNameStatus = RealNameStatus.UNVERIFIED;
    }

    public void markVerified(
            String realName,
            String idNumber,
            Instant verifiedAt) {
        this.realName = realName;
        this.idNumber = idNumber;
        this.verifiedAt = verifiedAt;
        this.realNameStatus = RealNameStatus.VERIFIED;
    }

    public String getUserId() {
        return userId;
    }

    public String getRealName() {
        return realName;
    }

    public String getIdNumber() {
        return idNumber;
    }

    public RealNameStatus getRealNameStatus() {
        return realNameStatus;
    }

    public Instant getVerifiedAt() {
        return verifiedAt;
    }

    public boolean isVerified() {
        return realNameStatus == RealNameStatus.VERIFIED;
    }

    public String getMaskedIdNumber() {
        return maskIdNumber(idNumber);
    }

    public static String maskIdNumber(String value) {
        if (value == null || value.length() <= 8) {
            return "********";
        }
        int visibleLength = 4;
        return value.substring(0, visibleLength)
                + "*".repeat(value.length() - visibleLength * 2)
                + value.substring(value.length() - visibleLength);
    }
}
