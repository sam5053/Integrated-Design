package UC_03;

/*实名资料仓储接口。*/
public interface IdentityProfileRepository {

    boolean existsByIdNumberOtherUser(String idNumber, String userId);

    IdentityProfile findByUserId(String userId);

    IdentityProfile save(IdentityProfile profile);
}
