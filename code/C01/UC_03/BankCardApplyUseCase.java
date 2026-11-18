package UC_03;

/*银行卡查询用例接口。*/
public interface BankCardApplyUseCase {

    BankCardApplyResult queryBankCards(BankCardApplyCommand command);
}
