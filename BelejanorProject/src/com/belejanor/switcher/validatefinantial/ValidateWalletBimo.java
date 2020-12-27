package com.belejanor.switcher.validatefinantial;

import java.util.concurrent.TimeUnit;

import com.belejanor.switcher.cscoreswitch.wIso8583;
import com.belejanor.switcher.logger.Logger;
import com.belejanor.switcher.logger.LoggerConfig.TypeMonitor;
import com.belejanor.switcher.sqlservices.IsoRetrievalTransaction;
import com.belejanor.switcher.sqlservices.typeBDD;
import com.belejanor.switcher.utils.GeneralUtils;
import com.belejanor.switcher.utils.StringUtils;

public class ValidateWalletBimo {

	private Logger log= null;
	public ValidateWalletBimo() {
		
		log = new Logger();
	}
	public wIso8583 validateBimoWallet(wIso8583 isoReq){
		
		IsoRetrievalTransaction sql = null;
		wIso8583 iso = isoReq.cloneWiso(isoReq);
		try {
			
			sql = new IsoRetrievalTransaction();
			switch (iso.getISO_BitMap()) {
			case "camt.998.211":
				if(StringUtils.IsNullOrEmpty(iso.getISO_124_ExtendedData())){
					
					iso.setISO_BitMap("V_AUT");
					iso.setISO_114_ExtendedData("0");//Status
					
				}else{
					
					iso.setISO_BitMap("E");
					iso.setISO_114_ExtendedData("1");//Status
				}
				break;
			case "camt.998.221":
					iso.setISO_BitMap("D");
					iso.setISO_114_ExtendedData("-1");//Status
				break;
			case "ONLY":
				iso.setISO_BitMap("V_ONLY");
				iso.setISO_114_ExtendedData("1");//Status
			break;
			case "camt_998_321.A":
			case "camt.998.211.A":
				if(iso.getCommandCounter() == 0){
					
					iso.setISO_BitMap("V");
					iso.setISO_114_ExtendedData("0");//Status
					
				}else if(iso.getCommandCounter() == 4){
					
					iso.setISO_BitMap("E");
					iso.setISO_114_ExtendedData("1");//Status
				}
				break;
			case "camt.998.311.A":
				
					switch (iso.getISO_090_OriginalData().trim()) {
					case "D":
						iso.setISO_BitMap("D");
						iso.setISO_114_ExtendedData("-1");
						break;
					case "B":
						//iso.setISO_BitMap("B");
						//iso.setISO_114_ExtendedData("0");
						isoReq.setISO_039_ResponseCode("000");
						return isoReq;
						//break;
					case "A":
						//iso.setISO_BitMap("A");
						//iso.setISO_114_ExtendedData("1");
						isoReq.setISO_039_ResponseCode("000");
						return isoReq;
						//break;
					default:
						break;
					}
				break;
				
			case "0000000000000000":
				
				iso.setISO_BitMap("C_MOVIL");
				iso.setISO_114_ExtendedData("1");
				
				break;
			case "camt.998.888.A":
				
				iso.setISO_BitMap("C");
				iso.setISO_114_ExtendedData("0");
				
			break;
			
			case "pacs.007.051":
			case "pacs.007.041":
			case "pacs.008.071":
			case "pacs.008.021":
				
				iso.setISO_BitMap("V_FINAN");
				iso.setISO_114_ExtendedData("0");//Status
				
			break;
			
			case "camt.998.201":
			 
			if(iso.getCommandCounter() == 0){
				
				iso.setISO_BitMap("V_FINAN");
				iso.setISO_114_ExtendedData("0");//Status
				
			}else{
				
				iso.setISO_BitMap("C_MOV");
				iso.setISO_114_ExtendedData("0");//Status
			}
				
			break;
			default:
				
				iso.setISO_039_ResponseCode("070");
				iso.setISO_039p_ResponseDetail("NO EXISTE INFORMACION NECESARIA PARA EJECUTAR LA TRANSACCION");
				break;
			}
			
			isoReq.getTickAut().reset();
			isoReq.getTickAut().start();
				iso = sql.MantenainceBIMOWalletSQL(iso, typeBDD.Sybase);
			if(isoReq.getTickAut().isStarted())
				isoReq.getTickAut().stop();
			
		} catch (Exception e) {
			
			if(isoReq.getTickAut().isStarted())
				isoReq.getTickAut().stop();
			log.WriteLogMonitor("Error modulo ValidateWalletBimo::validateBimoWallet", TypeMonitor.error, e);
			isoReq.setISO_039_ResponseCode("909");
			isoReq.setISO_039p_ResponseDetail(GeneralUtils.ExceptionToString("ERROR EN PROCESOS"
					, e, false));
			
		}finally {
			
			isoReq.setISO_039_ResponseCode(iso.getISO_039_ResponseCode());
			isoReq.setISO_039p_ResponseDetail(iso.getISO_039p_ResponseDetail());
			isoReq.setWsIso_LogStatus(iso.getWsIso_LogStatus());
			isoReq.setWsTempAut((isoReq.getTickAut().getTime(TimeUnit.MILLISECONDS)/1000.0));
			isoReq.setISO_044_AddRespData(iso.getISO_044_AddRespData());
		}
		return isoReq;
	}
}
