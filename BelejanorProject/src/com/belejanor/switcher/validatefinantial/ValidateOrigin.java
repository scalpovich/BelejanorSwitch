package com.belejanor.switcher.validatefinantial;

import java.util.Arrays;
import java.util.concurrent.Callable;

import com.belejanor.switcher.cscoreswitch.wIso8583;
import com.belejanor.switcher.utils.GeneralUtils;
import com.belejanor.switcher.utils.StringUtils;

public class ValidateOrigin {
	
	private wIso8583 iso;
	
	public ValidateOrigin(wIso8583 iso){
		this.iso = iso;
	}
	
	private wIso8583 ValidateIP(){
		
		wIso8583 isoRes = new wIso8583();
		try {
			
			String[] ips = this.iso.getWsTransactionConfig().getValidIp().split(","); 
			String Ip = Arrays.stream(ips).
					    filter(p -> p.equals(this.iso.getWs_IP()))
					    .findFirst()
					    .orElse(null);
			
			if(!StringUtils.IsNullOrEmpty(Ip))
				isoRes.setISO_039_ResponseCode("000");
			else{
				isoRes.setISO_039_ResponseCode("904");
				isoRes.setISO_039p_ResponseDetail("IP SWITCH CANAL-RED NO RECONOCIDA (" + this.iso.getWs_IP() + "), TRANSACCION DENEGADA");
			}
				
		} catch (Exception e) {
			
			isoRes.setISO_039_ResponseCode("909");
			isoRes.setISO_039p_ResponseDetail(GeneralUtils.ExceptionToString("ERROR VALIDACION IP ", e, false));
		}
		return isoRes;
	}
	
	private wIso8583 ValidateChannelNetworkTrx(wIso8583 iso){
		
		wIso8583 isoRes = new wIso8583();
		
		try {
			
			if(iso.getWsTransactionConfig().getProccodestatus() != 1){
				
				isoRes.setISO_039_ResponseCode("904");
				isoRes.setISO_039p_ResponseDetail("TRANSACCION PROCCODE: "+ iso.getWsTransactionConfig().getProccodeDescription() + 
												   ", RECHAZADA POR SWITCH, TRANSACCION DENEGADA");
			}else if (iso.getWsTransactionConfig().getNet_Status() != 1){
				
				isoRes.setISO_039_ResponseCode("904");
				isoRes.setISO_039p_ResponseDetail("RED: "+ iso.getWsTransactionConfig().getNet_Descripcion() + 
												   ", RECHAZADA POR SWITCH, TRANSACCION DENEGADA");
			}else if (iso.getWsTransactionConfig().getCanal_status() != 1) {
				
				isoRes.setISO_039_ResponseCode("904");
				isoRes.setISO_039p_ResponseDetail("CANAL: "+ iso.getWsTransactionConfig().getCanal_Des() + 
												   ", RECHAZADA POR SWITCH, TRANSACCION DENEGADA");
			}else if (iso.getWsTransactionConfig().getTrx_status() != 1) {
				isoRes.setISO_039_ResponseCode("904");
				isoRes.setISO_039p_ResponseDetail("TRANSACCION: "+ iso.getWsTransactionConfig().getProccodeDescription() + ", RED: " 
												   + iso.getWsTransactionConfig().getNet_Descripcion()  + ", CANAL: " +
												   iso.getWsTransactionConfig().getCanal_Des() +
												   ", RECHAZADA POR SWITCH, TRANSACCION DENEGADA");
			}else {
				
				isoRes.setISO_039_ResponseCode("000");
			}
			
		} catch (Exception e) {
			
			isoRes.setISO_039_ResponseCode("909");
			isoRes.setISO_039p_ResponseDetail(GeneralUtils.ExceptionToString("ERROR VALIDACION IP ", e, false));
		}
		return isoRes;
	}
	
	public Callable<wIso8583> ValidateIp(){
		Callable<wIso8583> callable = new Callable<wIso8583>() {
			
			@Override
			public wIso8583 call() throws Exception {
				
				return ValidateIP();
			}
		};
		return callable;
	}
	
	public Callable<wIso8583> ValidateChannelNetworkTrx(){
		Callable<wIso8583> callable = new Callable<wIso8583>() {
			
			@Override
			public wIso8583 call() throws Exception {
				
				return ValidateChannelNetworkTrx(iso);
			}
		};
		return callable;
	}
	
}
