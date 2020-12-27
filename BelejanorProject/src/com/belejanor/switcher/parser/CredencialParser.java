package com.belejanor.switcher.parser;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import com.belejanor.switcher.credencial.BalanceCredencialRequest;
import com.belejanor.switcher.credencial.DebitCredencialRequest;
import com.belejanor.switcher.credencial.MovementsCredencialRequest;
import com.belejanor.switcher.credencial.RevertCredencialRequest;
import com.belejanor.switcher.cscoreswitch.Iso8583;
import com.belejanor.switcher.logger.Logger;
import com.belejanor.switcher.logger.LoggerConfig.TypeMonitor;
import com.belejanor.switcher.memcached.MemoryGlobal;
import com.belejanor.switcher.memcached.MoneyIso4127;
import com.belejanor.switcher.utils.FormatUtils;
import com.belejanor.switcher.utils.GeneralUtils;
import com.belejanor.switcher.utils.StringUtils;

public class CredencialParser {
	
	private Logger log;
	
	public CredencialParser(){
		
		log = new Logger();
	}

    public <T> Iso8583 parseCredencialToIso (T obj){
		
    	Iso8583 iso = null;
		try {
			
			iso = new Iso8583();
			iso.setISO_039_ResponseCode("000");
			iso.setISO_000_Message_Type("1200");
			iso.setISO_012_LocalDatetime(new Date());
			iso.setISO_024_NetworkId("555533");  //Red Credencial
			iso.setISO_018_MerchantType("0007"); //Switch Externo
			//iso.setISO_024_NetworkId("555557"); //Por pruebas borrar
			//iso.setISO_018_MerchantType("0001"); //Por pruebas borrar
			
			switch (obj.getClass().getName()) {
			
			case "com.fitbank.middleware.credencial.DebitCredencialRequest":
				
				DebitCredencialRequest debit = (DebitCredencialRequest) obj;
				
				iso.setISO_002_PAN(debit.getTarjeta());
				if(iso.getISO_102_AccountID_1().startsWith("41"))
					iso.setISO_003_ProcessingCode("012000"); 
				else
					iso.setISO_003_ProcessingCode("011000"); 
				iso.setISO_004_AmountTransaction(debit.getMontoLIQ());
				iso.setISO_006_BillAmount(debit.getMonto());
				iso.setISO_103_AccountID_2(debit.getReferenciaCuentaEMI());
				iso.setISO_102_AccountID_1(debit.getIdentificadorCuentaEMI());
				iso.setISO_049_TranCurrCode(debit.getMonedaLIQ());
				iso.setISO_051_CardCurrCode(debit.getMoneda());
				iso.setISO_011_SysAuditNumber(GeneralUtils.GetSecuencial(12));
				iso.setISO_037_RetrievalReferenceNumber(StringUtils.
						 isNullOrEmpty(debit.getIdentificadorADQ())?"":debit.getIdentificadorADQ());
				iso.setISO_008_BillFeeAmount(debit.getComisionLIQ());
				iso.setISO_028_TranFeeAmount(debit.getComision());
				iso.setISO_022_PosEntryMode(debit.getOrigen());
				iso.setISO_120_ExtendedData(String.valueOf(debit.getMotivo()));
				iso.setISO_032_ACQInsID(debit.getAdquiriente());
				iso.setISO_033_FWDInsID(StringUtils.padLeft(MemoryGlobal.abaIfi.get(0),11,"0"));
				iso.setISO_043_CardAcceptorLoc(debit.getCiudad());
				iso.setISO_007_TransDatetime(debit.getFechaTransaccion());
				iso.setISO_041_CardAcceptorID(debit.getTerminal());
				iso.setISO_023_CardSeq(debit.getNombreComercio());
				iso.setISO_121_ExtendedData(debit.getIdentificador());
				
				break;
			case "com.fitbank.middleware.credencial.BalanceCredencialRequest":
				
				BalanceCredencialRequest balance = (BalanceCredencialRequest)obj;
				iso.setISO_102_AccountID_1(balance.getIdentificadorCuentaEMI());
				if(iso.getISO_102_AccountID_1().startsWith("41"))
					iso.setISO_003_ProcessingCode("312000");
				else
					iso.setISO_003_ProcessingCode("311000"); 
				iso.setISO_002_PAN(StringUtils.padRight(MemoryGlobal.abaIfi.get(0),19,"0"));
				iso.setISO_103_AccountID_2(balance.getReferenciaCuentaEMI());
				iso.setISO_011_SysAuditNumber(GeneralUtils.GetSecuencial(12));
				iso.setISO_037_RetrievalReferenceNumber(balance.getIdentificadorADQ());
				iso.setISO_022_PosEntryMode(balance.getOrigen());
				iso.setISO_049_TranCurrCode(840);
				iso.setISO_041_CardAcceptorID("TERM_SWITCH");
				iso.setISO_121_ExtendedData(balance.getIdentificador());
				
				break;
			case "com.fitbank.middleware.credencial.MovementsCredencialRequest":
				
				MovementsCredencialRequest movements = (MovementsCredencialRequest)obj;
				iso.setISO_002_PAN(StringUtils.padRight(MemoryGlobal.abaIfi.get(0),19,"0"));
				iso.setISO_003_ProcessingCode("314000");
				iso.setISO_103_AccountID_2(movements.getReferenciaCuentaEMI());
				iso.setISO_102_AccountID_1(movements.getIdentificadorCuentaEMI());
				iso.setISO_011_SysAuditNumber(GeneralUtils.GetSecuencial(12));
				iso.setISO_037_RetrievalReferenceNumber(movements.getIdentificadorADQ());
				iso.setISO_022_PosEntryMode(movements.getOrigen());
				iso.setISO_122_ExtendedData(movements.getIdentificador());
				
				
				break;
			case "com.fitbank.middleware.credencial.RevertCredencialRequest":
				
				RevertCredencialRequest revert = (RevertCredencialRequest)obj;
				iso.setISO_000_Message_Type("1400");
				iso.setISO_002_PAN(StringUtils.padRight(MemoryGlobal.abaIfi.get(0),19,"0"));
				if(iso.getISO_102_AccountID_1().startsWith("41"))
					iso.setISO_003_ProcessingCode("012000"); 
				else
					iso.setISO_003_ProcessingCode("011000");
				//iso.setISO_011_SysAuditNumber(revert.getIdentificador());
				iso.setISO_011_SysAuditNumber(GeneralUtils.GetSecuencial(12));
				iso.setISO_037_RetrievalReferenceNumber(revert.getIdentificadorADQ());
				iso.setISO_038_AutorizationNumber(revert.getIdentificadorEMI());//Para recuperar de la BDD
				//iso.setISO_044_AddRespData(revert.getIdentificadorEMI());
				iso.setISO_022_PosEntryMode(revert.getOrigen());
				iso.setISO_120_ExtendedData(String.valueOf(revert.getMotivoReversa()));
				iso.setISO_007_TransDatetime(revert.getFechaTransaccion());
				iso.setISO_004_AmountTransaction(revert.getMontoLIQ());
				iso.setISO_006_BillAmount(revert.getMonto());
				iso.setISO_008_BillFeeAmount(0.00000001);
				iso.setISO_028_TranFeeAmount(0.00000001);
				iso.setISO_032_ACQInsID("N/D");
				iso.setISO_033_FWDInsID(StringUtils.padLeft(MemoryGlobal.abaIfi.get(0),11,"0"));
				iso.setISO_043_CardAcceptorLoc("N/D");
				iso.setISO_041_CardAcceptorID("N/D");
				iso.setISO_049_TranCurrCode(revert.getMonedaLIQ());
				iso.setISO_051_CardCurrCode(revert.getMoneda());
				iso.setISO_102_AccountID_1("000000000000");
				iso.setISO_103_AccountID_2("000000000000");
				iso.setISO_023_CardSeq(revert.getNombreComercio());
				iso.setISO_123_ExtendedData(revert.getIdentificador());
				
				break;
			default:
				break;
			}
			
		} catch (Exception e) {
			
			iso.setISO_039_ResponseCode("909");
			iso.setISO_039p_ResponseDetail("ERROR EN PROCESOS " + 
			GeneralUtils.ExceptionToString("PARSER ISO ERROR:  ", e, false));
		}finally {
			
			if(iso.getISO_049_TranCurrCode() > 0) {
				
				iso.setISO_019_AcqCountryCode(MoneyIso4127.getAbrevMoneyFromCredencialCodeMoney
						(Arrays.asList(String.valueOf(iso.getISO_049_TranCurrCode()).split("\\.")).get(0)));
			}
		}
		return iso;
	}
    
    public Iso8583 IsoFitStringToIsoObject(String isoText){
    	
    	Iso8583 iso = new Iso8583();
    	try {
			
    		if(isoText.length() == 75){
    			
	    		SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
	    		iso.setISO_000_Message_Type(isoText.substring(0,4));
	    		iso.setISO_002_PAN(isoText.substring(4,23).trim());
	    		iso.setISO_003_ProcessingCode(isoText.substring(23,29).trim());
	    		iso.setISO_007_TransDatetime(new Date());
	    		iso.setISO_011_SysAuditNumber(isoText.substring(29,35));
				iso.setISO_012_LocalDatetime(format.parse(isoText.substring(35, 49)));
				format = new SimpleDateFormat("yyyyMMdd");
				iso.setISO_015_SettlementDatel(format.parse(isoText.substring(49, 57)));
				iso.setISO_018_MerchantType(isoText.substring(57, 61));
				iso.setISO_024_NetworkId(isoText.substring(61, 67));
				iso.setISO_041_CardAcceptorID(isoText.substring(67, 71));
				iso.setISO_042_Card_Acc_ID_Code(isoText.substring(71, 75));
				iso.setISO_039_ResponseCode("000");
				
    		}else {
				
    			iso.setISO_039_ResponseCode("808");
    			iso.setISO_039p_ResponseDetail("LONGITUD TRAMA INVALIDA");
			}
    		
		} catch (Exception e) {
			
			iso.setISO_039_ResponseCode("809");
			iso.setISO_039p_ResponseDetail("ERROR EN PROCEOS, " + e.getMessage().toUpperCase());
			log.WriteLogMonitor("Error modulo CredencialParser::IsoFitStringToIsoObject ", TypeMonitor.error, e);
		}
    	return iso;
    }
    public String IsoObjectToIsoText(Iso8583 iso){
    	
    	StringBuilder isoText = new StringBuilder();
    	try {
			
    		if(iso.getISO_039_ResponseCode().startsWith("80")){
    			isoText.append(iso.getISO_000_Message_Type().replace("00", "10"));
    		}else {
    			isoText.append(iso.getISO_000_Message_Type());
			}
    		
    		isoText.append(StringUtils.padRight(iso.getISO_002_PAN(),19," "));
    		isoText.append(StringUtils.padRight(iso.getISO_003_ProcessingCode(),6,"0"));
    		isoText.append(StringUtils.padRight(iso.getISO_011_SysAuditNumber(),6,"0"));
    		isoText.append(FormatUtils.DateToString(iso.getISO_012_LocalDatetime(), "yyyyMMddHHmmss"));
    		isoText.append(FormatUtils.DateToString(iso.getISO_015_SettlementDatel(), "yyyyMMdd"));
    		isoText.append(StringUtils.padRight(iso.getISO_018_MerchantType(),4,"0"));
    		isoText.append(StringUtils.padRight(iso.getISO_024_NetworkId(),6,"0"));
    		isoText.append(StringUtils.padRight(iso.getISO_039_ResponseCode(),3,"0"));
    		isoText.append(StringUtils.padRight(iso.getISO_039p_ResponseDetail(),200," ")
    				.substring(0,200));
    		isoText.append(StringUtils.padRight(iso.getISO_041_CardAcceptorID(),4," "));
    		isoText.append(StringUtils.padRight(iso.getISO_042_Card_Acc_ID_Code(),4," "));
    		isoText.append(iso.getISO_115_ExtendedData().replace("|", ""));
    		
		} catch (Exception e) {
			isoText.append("ERROR EN PROCESOS");
			log.WriteLogMonitor("Error modulo CredencialParser::IsoObjectToIsoText ", TypeMonitor.error, e);
		}
    	return isoText.toString();
    }
}
