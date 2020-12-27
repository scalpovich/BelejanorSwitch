package com.belejanor.switcher.credencial;

public interface ITransactionBridgeCredencial {

	abstract DebitCredencialResponse trxDebit(DebitCredencialRequest debitRq, String IP);
	abstract RevertCredencialResponse trxRevertDebit(RevertCredencialRequest revertRq, String IP);
	abstract BalanceCredencialResponse trxBalance(BalanceCredencialRequest balanceRq, String IP);
	abstract MovementsCredencialResponse trxMovements(MovementsCredencialRequest movReq, String IP);
}
