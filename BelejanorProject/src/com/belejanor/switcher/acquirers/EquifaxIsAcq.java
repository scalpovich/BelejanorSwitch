package com.belejanor.switcher.acquirers;

import java.util.concurrent.TimeUnit;

import com.belejanor.switcher.cscoreswitch.wIso8583;
import com.belejanor.switcher.logger.Logger;
import com.belejanor.switcher.logger.LoggerConfig.TypeMonitor;
import com.belejanor.switcher.sqlservices.IsoRetrievalTransaction;
import com.belejanor.switcher.sqlservices.IsoSqlMaintenance;
import com.belejanor.switcher.struct.equifax.RegisterData;
import com.belejanor.switcher.utils.GeneralUtils;
import com.belejanor.switcher.utils.SerializationObject;
import com.belejanor.switcher.utils.StringUtils;

public class EquifaxIsAcq {

	private Logger log;
	
	public EquifaxIsAcq() {
		
		log = new Logger();
	}
	
	public wIso8583 getDataEquifax(wIso8583 iso) {
		
		IsoRetrievalTransaction sql = null;
		try {
			iso.setISO_041_CardAcceptorID("EQUIFAX");
			sql = new IsoRetrievalTransaction();
			
			iso.getTickAut().reset();
			iso.getTickAut().start();
				iso = sql.GetDataEquifax(iso);
			if(iso.getTickAut().isStarted())
				iso.getTickAut().stop();
				
		} catch (Exception e) {
			
			if(iso.getTickAut().isStarted())
				iso.getTickAut().stop();
			iso.setISO_039_ResponseCode("909");
			iso.setISO_039p_ResponseDetail(GeneralUtils.ExceptionToString("ERROR EN PROCESOS", e, true));
			log.WriteLogMonitor("Error modulo EquifaxIsAcq::getDataEquifax ", TypeMonitor.error, e);
			
		}finally {
		
			iso.setWsTempAut((iso.getTickAut().getTime(TimeUnit.MILLISECONDS)/1000.0));
		}
		
		return iso;
	}
	
	public wIso8583 registerEvaluationEquifax(wIso8583 iso) {
		
		IsoSqlMaintenance sql = null;
		wIso8583 isoClone = null;
		RegisterData data = null;
		int responseSQL = 0;
		try {
			
			isoClone = (wIso8583) iso.clone();
			sql = new IsoSqlMaintenance();
			
			data = (RegisterData) SerializationObject.StringToObject(iso.getISO_115_ExtendedData(), RegisterData.class);
			if(data != null) {
				
				iso.getTickAut().reset();
				iso.getTickAut().start();
					responseSQL = sql.InsertDataEvaluationEquifax(isoClone, data);
				if(iso.getTickAut().isStarted())
					iso.getTickAut().stop();
				
				if(responseSQL != 0) {
					
					if(responseSQL == 1) {
					
						iso.setISO_039_ResponseCode("500");
						iso.setISO_039p_ResponseDetail("REGISTRO DUPLICADO");
					}else {
						
						iso.setISO_039_ResponseCode(StringUtils.padLeft(String.valueOf(responseSQL),3,"0"));
						iso.setISO_039p_ResponseDetail("ERROR EN BDD: Code: " + responseSQL + " - " + sql.getDescriptionSqlError());
					}
					
				}else {
					
					iso.setISO_039_ResponseCode("000");
					iso.setISO_039p_ResponseDetail("TRANSACCION EXITOSA");
				}
				
			}else {
				
				iso.setISO_039_ResponseCode("908");
				iso.setISO_039p_ResponseDetail("ERROR AL DESEREALIZAR DATA REQUERIMIENTO");
			}
			
			
		} catch (Exception e) {
			
			if(iso.getTickAut().isStarted())
				iso.getTickAut().stop();
			iso.setISO_039_ResponseCode("909");
			iso.setISO_039p_ResponseDetail(GeneralUtils.ExceptionToString("ERROR EN PROCESOS", e, true));
			log.WriteLogMonitor("Error modulo EquifaxIsAcq::registerEvaluationEquifax ", TypeMonitor.error, e);
			
		}finally {
		
			iso.setWsTempAut((iso.getTickAut().getTime(TimeUnit.MILLISECONDS)/1000.0));
		}
		
		return iso;
	}
}
