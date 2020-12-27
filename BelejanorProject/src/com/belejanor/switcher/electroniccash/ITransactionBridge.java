package com.belejanor.switcher.electroniccash;

import com.belejanor.switcher.electroniccashres.DTOResponseCredit;
import com.belejanor.switcher.electroniccashres.DTOResponseDebit;
import com.belejanor.switcher.electroniccashres.DTOResponseIsValidAccount;
import com.belejanor.switcher.electroniccashres.DTOResponseRevert;

public interface ITransactionBridge {

	abstract DTOResponseCredit CreditBridge(DTORequestCredit credit, String Ip);
	abstract DTOResponseRevert RevertBridge(DTORequestRevert rever, String Ip);
	abstract DTOResponseDebit DebitBridge(DTORequestDebit debit, String Ip);
	abstract DTOResponseIsValidAccount ValidAccountBridge(DTORequestIsValidAccount valid, String Ip);
}