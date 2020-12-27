package com.belejanor.switcher.bridges;

import java.util.concurrent.Callable;

import com.belejanor.switcher.acquirers.CredencialIsAcq;
import com.belejanor.switcher.credencial.BalanceCredencialRequest;
import com.belejanor.switcher.credencial.BalanceCredencialResponse;
import com.belejanor.switcher.credencial.DebitCredencialRequest;
import com.belejanor.switcher.credencial.DebitCredencialResponse;
import com.belejanor.switcher.credencial.ITransactionBridgeCredencial;
import com.belejanor.switcher.credencial.MovementsCredencialRequest;
import com.belejanor.switcher.credencial.MovementsCredencialResponse;
import com.belejanor.switcher.credencial.RevertCredencialRequest;
import com.belejanor.switcher.credencial.RevertCredencialResponse;
import com.belejanor.switcher.cscoreswitch.Iso8583;
import com.belejanor.switcher.cscoreswitch.csProcess;
import com.belejanor.switcher.parser.CredencialParser;

public class CredencialBridge<T> implements ITransactionBridgeCredencial, Callable<T>{
	
	private Object clase;
	private String IP;
	
	public CredencialBridge() {
		
		
	}
	public CredencialBridge(Object clase,  String IP) {
		
		this.clase = clase;
		this.IP = IP;
	}

	@Override
	public DebitCredencialResponse trxDebit(DebitCredencialRequest debitRq, String IP) {
		CredencialIsAcq crdAcq = new CredencialIsAcq();
		return crdAcq.trxDebit(debitRq, IP);
	}

	@Override
	public RevertCredencialResponse trxRevertDebit(RevertCredencialRequest revertRq, String IP) {
		
		CredencialIsAcq crdAcq = new CredencialIsAcq();
		return crdAcq.trxReverse(revertRq, IP);
	}

	@Override
	public BalanceCredencialResponse trxBalance(BalanceCredencialRequest balanceRq, String IP) {
		
		CredencialIsAcq crdAcq = new CredencialIsAcq();
		return crdAcq.trxBalanceQuery(balanceRq, IP);
	}

	@Override
	public MovementsCredencialResponse trxMovements(MovementsCredencialRequest movReq, String IP) {
		CredencialIsAcq crdAcq = new CredencialIsAcq();
		return crdAcq.trxMovementsAccount(movReq, IP);
	}

	@SuppressWarnings("unchecked")
	@Override
	public T call() throws Exception {
		
		T bean = (T) this.clase;
		T response = null;
			
		if (bean.getClass().equals(DebitCredencialRequest.class)) {
			DebitCredencialRequest obj = (DebitCredencialRequest)bean;
			response = (T) trxDebit(obj, this.IP);
			
		}else if (bean.getClass().equals(RevertCredencialRequest.class)) {
			RevertCredencialRequest obj = (RevertCredencialRequest)bean;
			response = (T) trxRevertDebit(obj, this.IP);
			
		}else if (bean.getClass().equals(BalanceCredencialRequest.class)) {
			BalanceCredencialRequest obj = (BalanceCredencialRequest)bean;
			response = (T) trxBalance(obj, this.IP);
			
		}else if (bean.getClass().equals(MovementsCredencialRequest.class)) {
			MovementsCredencialRequest obj = (MovementsCredencialRequest)bean;
			response = (T) trxMovements(obj, this.IP);
		}
		return response;
	}
	public String QueryBalanceTCWhithAndWithoutMovements(String IsoText){
	
		CredencialParser parser = new CredencialParser();
		Iso8583 iso = new Iso8583();
		iso = parser.IsoFitStringToIsoObject(IsoText);
		if(iso.getISO_039_ResponseCode().equals("000")){
			
			csProcess processor = new csProcess();
			iso = processor.ProcessTransactionMain(iso, "127.0.0.1");
		}
		return parser.IsoObjectToIsoText(iso);
	}
	
}
