package com.belejanor.switcher.electroniccash;

import com.belejanor.switcher.electroniccashres.DTOResponseCredit;
import com.belejanor.switcher.electroniccashres.DTOResponseDebit;
import com.belejanor.switcher.electroniccashres.DTOResponseIsValidAccount;
import com.belejanor.switcher.electroniccashres.DTOResponseRevert;

public interface ITransactions {

	abstract DTOResponseCredit credit(DTORequestCredit DTORequestCredit);
	abstract DTOResponseRevert revert(DTORequestRevert DTORequestRevert);
	abstract DTOResponseDebit  debit(DTORequestDebit DTORequestDebit);
	abstract DTOResponseIsValidAccount isValidAccount(DTORequestIsValidAccount DTORequestIsValidAccount);
}
