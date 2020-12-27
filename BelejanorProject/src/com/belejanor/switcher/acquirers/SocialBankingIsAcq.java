package com.belejanor.switcher.acquirers;

import java.util.List;
import java.util.concurrent.TimeUnit;

import com.belejanor.switcher.authorizations.FitIsAut;
import com.belejanor.switcher.cscoreswitch.wIso8583;
import com.belejanor.switcher.logger.Logger;
import com.belejanor.switcher.logger.LoggerConfig.TypeMonitor;
import com.belejanor.switcher.sqlservices.IsoRetrievalTransaction;
import com.belejanor.switcher.utils.Iterables;
import com.belejanor.switcher.utils.SerializationObject;
import com.belejanor.switcher.utils.StringUtils;

public class SocialBankingIsAcq {

	private Logger log;
	
	public SocialBankingIsAcq(){
		this.log = new Logger();
	}
	
	public wIso8583 getCtasFitForIdentification(wIso8583 iso){
		
		FitIsAut fitCore = new FitIsAut();
		wIso8583 isoClon = iso.cloneWiso(iso);
		try {
			
			isoClon.setISO_035_Track2("TCUENTASPERSONA");
			isoClon.setISO_115_ExtendedData("CCUENTA,NOMBRECUENTA,DESCRIPCION");
			isoClon = fitCore.QueryIterableGenericWithTable(isoClon);
			
			if(isoClon.getISO_039_ResponseCode().equals("000")){
				
				//iso.setISO_114_ExtendedData(isoClon.getISO_114_ExtendedData());
				@SuppressWarnings("unchecked")
				List<Iterables> it = (List<Iterables>) SerializationObject
    					.XMLToObject(isoClon.getISO_114_ExtendedData());
				String acum = StringUtils.Empty();
				for (Iterables ite : it) {
					
					acum += ite.getIterarors().get("CCUENTA") + "-" + ite.getIterarors().get("DESCRIPCION") + "|";
				}
				iso.setISO_114_ExtendedData(StringUtils.trimEnd(acum, "|"));
			}
				
			iso.setISO_039_ResponseCode(isoClon.getISO_039_ResponseCode());
			iso.setISO_039p_ResponseDetail(isoClon.getISO_039p_ResponseDetail());
			iso.setWsIso_LogStatus(isoClon.getWsIso_LogStatus());
		
			
		} catch (Exception e) {
			
			log.WriteLogMonitor("Error modulo SocialBankingIsAcq::getCtasFitForIdentification ", 
					TypeMonitor.error, e);
		}
		
		return iso;
	}
	
	public wIso8583 getInfoForRegister(wIso8583 iso){
		
		IsoRetrievalTransaction sql = null;
		try {
			
			sql = new IsoRetrievalTransaction();
			iso.getTickAut().reset();
			iso.getTickAut().start();
			iso = sql.getInfoSocialBankingRegister(iso);
			if(iso.getTickAut().isStarted())
				iso.getTickAut().stop();
			
			if(iso.getISO_039_ResponseCode().equals("000")){
				
				if(!iso.getISO_123_ExtendedData().equals("N/D")){
					
					if(iso.getISO_041_CardAcceptorID().trim().equalsIgnoreCase(iso.getISO_123_ExtendedData().trim())){
						
						iso.setISO_039_ResponseCode("000");
						iso.setISO_039p_ResponseDetail("VALIDACION REGISTRO, INFORMACION EXITOSA");
						
					}else{
						
						iso.setISO_039_ResponseCode("101");
						iso.setISO_039p_ResponseDetail("EL CORREO ELECTRONICO NO COINCIDE CON EL REGISTRADO EN LA INSTITUCION, "
								+ "ACERQUESE A ACTUALIZAR DATOS");
						return iso;
					}
					
				}else {
					
					iso.setISO_039_ResponseCode("100");
					iso.setISO_039p_ResponseDetail("EL SOCIO/CLIENTE NO REGISTRA NINGUN CORREO ELECTRONICO"
							+ ", O EL MISMO NO ES UNICO, ACERQUESE A ACTUALIZAR SUS DATOS");
					return iso;
				}
				
                if(!iso.getISO_124_ExtendedData().equals("N/D")){
					
					if(iso.getISO_002_PAN().equalsIgnoreCase(iso.getISO_124_ExtendedData().trim())){
						
						iso.setISO_039_ResponseCode("000");
						iso.setISO_039p_ResponseDetail("VALIDACION REGISTRO, INFORMACION EXITOSA");
						
					}else{
						
						iso.setISO_039_ResponseCode("201");
						iso.setISO_039p_ResponseDetail("EL NUMERO CELULAR NO COINCIDE CON EL REGISTRADO EN LA INSTITUCION, "
								+ "ACERQUESE A ACTUALIZAR DATOS");
						return iso;
					}
					
				}else {
					
					iso.setISO_039_ResponseCode("200");
					iso.setISO_039p_ResponseDetail("EL SOCIO/CLIENTE NO REGISTRA NINGUN NUMERO CELULAR"
							+ ", O EL MISMO NO ES UNICO, ACERQUESE A ACTUALIZAR SUS DATOS");
					return iso;
				}
			}		
			
		} catch (Exception e) {
			
			if(iso.getTickAut().isStarted())
				iso.getTickAut().stop();
			log.WriteLogMonitor("Error modulo SocialBankingIsAcq::getInfoForRegister ", 
					TypeMonitor.error, e);
		}finally {
			
			iso.setWsTempAut((iso.getTickAut().getTime(TimeUnit.MILLISECONDS)/1000.0));
		}
		
		return iso;
	}
}
