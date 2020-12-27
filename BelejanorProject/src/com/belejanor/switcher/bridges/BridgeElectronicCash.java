package com.belejanor.switcher.bridges;

import com.belejanor.switcher.acquirers.ElectronicCashIsAcq;
import com.belejanor.switcher.electroniccash.DTORequestCredit;
import com.belejanor.switcher.electroniccash.DTORequestDebit;
import com.belejanor.switcher.electroniccash.DTORequestIsValidAccount;
import com.belejanor.switcher.electroniccash.DTORequestRevert;
import com.belejanor.switcher.electroniccash.ITransactionBridge;
import com.belejanor.switcher.electroniccashres.DTOResponseCredit;
import com.belejanor.switcher.electroniccashres.DTOResponseDebit;
import com.belejanor.switcher.electroniccashres.DTOResponseIsValidAccount;
import com.belejanor.switcher.electroniccashres.DTOResponseRevert;


public class BridgeElectronicCash implements ITransactionBridge{
	
	private String IP;
	
	public BridgeElectronicCash() {
		
	}
	public BridgeElectronicCash(String IP) {
		this.IP = IP;
	}

	public String getIP() {
		return IP;
	}
	public void setIP(String iP) {
		IP = iP;
	}
	@Override
	public DTOResponseCredit CreditBridge(DTORequestCredit credit, String Ip) {
		
		ElectronicCashIsAcq cashAcq = new ElectronicCashIsAcq();
		return cashAcq.Credit(credit, Ip);
	}

	@Override
	public DTOResponseRevert RevertBridge(DTORequestRevert rever, String Ip) {
		System.out.println("DTOResponseRevert RevertBridge....(2)");
		ElectronicCashIsAcq cashAcq = new ElectronicCashIsAcq();
		return cashAcq.Revert(rever, Ip);
	}

	@Override
	public DTOResponseDebit DebitBridge(DTORequestDebit debit, String Ip) {
		
		ElectronicCashIsAcq cashAcq = new ElectronicCashIsAcq();
		return cashAcq.Debit(debit, Ip);
	}

	@Override
	public DTOResponseIsValidAccount ValidAccountBridge(DTORequestIsValidAccount valid, String Ip) {
		
		ElectronicCashIsAcq cashAcq = new ElectronicCashIsAcq();
		return cashAcq.Valid(valid, Ip);
	}
	
}
