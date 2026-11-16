package UC_03;

import java.time.Instant;

/*实名认证应用服务。*/
public class IdentityService implements VerifyIdentityUseCase {
    private final IdentityProfileRepository identityRepository;
    private final VerificationProviderClient providerClient;

    public IdentityService(
            IdentityProfileRepository identityRepository,
            VerificationProviderClient providerClient){
        this.identityRepository = identityRepository;
        this.providerClient = providerClient;
    }

    @Override
    public VerifyIdentityResult verifyIdentity(//判断实名信息并保存至数据库
            VerifyIdentityCommand command) {
        if (!isValid(command)) {//实名信息不正确
            return VerifyIdentityResult.failure(
                    IdentityVerificationStatus.INVALID_INPUT
            );
        }

        if (identityRepository.existsByIdNumberOtherUser(//检查身份证号是否已被其他用户绑定
                command.idNumber,
                command.userId)) {
            return VerifyIdentityResult.failure(
                    IdentityVerificationStatus.IDENTITY_ALREADY_BOUND
            );
        }

        ExternalVerificationResult externalResult =//调用外部服务验证实名信息
                providerClient.verifyIdentity(
                        command.realName,
                        command.idNumber
                );

        VerificationResultStatus verificationStatus =//获取外部服务返回的验证状态
                externalResult == null
                        ? VerificationResultStatus.UNKNOWN
                        : externalResult.getStatus();

        if (verificationStatus == VerificationResultStatus.UNKNOWN//外部服务返回未知状态
                || verificationStatus == null) {
            return VerifyIdentityResult.failure(
                    IdentityVerificationStatus.PENDING_CONFIRMATION
            );
        }

        if (verificationStatus == VerificationResultStatus.REJECTED) {//外部服务返回拒绝
            return VerifyIdentityResult.failure(
                    IdentityVerificationStatus.EXTERNAL_REJECTED
            );
        }

        IdentityProfile profile =
                identityRepository.findByUserId(command.userId);
        if (profile == null) {
            profile = new IdentityProfile(command.userId);
        }

        profile.markVerified(//创建实名档案、标记实名信息为已验证
                command.realName,
                command.idNumber,
                Instant.now()
        );
        IdentityProfile savedProfile = identityRepository.save(profile);//存储至数据库

        return VerifyIdentityResult.success(savedProfile);
    }

    private boolean isValid(VerifyIdentityCommand command) {
        return command != null
                && command.userId != null
                && !command.userId.isBlank()
                && command.realName != null
                && !command.realName.isBlank()
                && command.idNumber != null
                && !command.idNumber.isBlank();
    }
}
