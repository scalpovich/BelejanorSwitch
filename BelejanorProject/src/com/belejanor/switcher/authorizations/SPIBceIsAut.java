package com.belejanor.switcher.authorizations;

import java.util.List;
import java.util.concurrent.TimeUnit;
import com.belejanor.switcher.cscoreswitch.Iso8583;
import com.belejanor.switcher.cscoreswitch.csProcess;
import com.belejanor.switcher.cscoreswitch.wIso8583;
import com.belejanor.switcher.logger.Logger;
import com.belejanor.switcher.logger.LoggerConfig.TypeLog;
import com.belejanor.switcher.logger.LoggerConfig.TypeMonitor;
import com.belejanor.switcher.memcached.MemoryGlobal;
import com.belejanor.switcher.sqlservices.IsoSqlMaintenance;
import com.belejanor.switcher.utils.GeneralUtils;
import com.belejanor.switcher.utils.StringUtils;
import com.belejanor.switcher.client.ConsumerClientWs;

public class SPIBceIsAut {
	
	private Logger log;
	private List<Iso8583> isoList;
	public SPIBceIsAut(){
		
	    log = new Logger();
	}
	
	public List<Iso8583> getIsoList() {
		return isoList;
	}

	public void setIsoList(List<Iso8583> isoList) {
		this.isoList = isoList;
	}

	public wIso8583 SendRequestSPIOrd(wIso8583 iso){
		
		ConsumerClientWs consumer = null;
		Iso8583 isoResponse = null;
		
		try {
		     iso.setWsTempAut(0);	 
			 isoResponse = new Iso8583();
			 consumer = new ConsumerClientWs(MemoryGlobal.UrlSpiBCE, 
					    MemoryGlobal.UrlSpiUserBCE, MemoryGlobal.UrlSpiIpAdressBCE, MemoryGlobal.UrlSpiIpSocketBCE, 
					    Integer.parseInt(MemoryGlobal.UrlSpiPortSocketBCE),
					    (iso.getWsTransactionConfig().getProccodeTimeOutValue()/2), iso.getWsTransactionConfig().getProccodeTimeOutValue(), 
					    (MemoryGlobal.currentPath + MemoryGlobal.UrlSpiNameCertBCE),
					    MemoryGlobal.UrlSpiPasswordBCE, MemoryGlobal.UrlSpiCodeEfi_BCE, MemoryGlobal.UrlSpiCodeSwitch_BCE, 
					    MemoryGlobal.UrlSpiAccountEfi_BCE);
			 
			 consumer.setMethodCreditName_SPI("recibirSolicitudOPI");
			 consumer.setNamespacePack_SPI("urn:iso:std:iso:20022:tech:xsd:pacs.008.001.04");
			 consumer.setPrefixPack_SPI("sol");
			 consumer.setNamespaceUrl_SPI("http://www.bce.fin.ec/snp/spi/SolicitudPagoInterbancarioSNPWS");
			 consumer.setPrefixUrl_SPI("urn");
			 consumer.setParamMethodName_SPI("documentoSolicitud");
			 
			 iso.getTickAut().reset();
		     iso.getTickAut().start();
			 isoResponse = consumer.executeSPI(iso, StringUtils.Empty());
			 if(iso.getTickAut().isStarted())
				iso.getTickAut().stop();
			 
			 iso.setISO_039_ResponseCode(isoResponse.getISO_039_ResponseCode());
			 iso.setISO_039p_ResponseDetail(isoResponse.getISO_039p_ResponseDetail());
			 iso.setISO_104_TranDescription(StringUtils.IsNullOrEmpty(isoResponse.getISO_104_TranDescription())?
					                        "N/D":isoResponse.getISO_104_TranDescription());
			 iso.setISO_121_ExtendedData(StringUtils.IsNullOrEmpty(isoResponse.getISO_121_ExtendedData())?
                                            "N/D":isoResponse.getISO_121_ExtendedData());
			 iso.setISO_122_ExtendedData(StringUtils.IsNullOrEmpty(isoResponse.getISO_122_ExtendedData())?
                                            "N/D":isoResponse.getISO_122_ExtendedData());
			 iso.setISO_123_ExtendedData(StringUtils.IsNullOrEmpty(isoResponse.getISO_123_ExtendedData())?
                                            "N/D":isoResponse.getISO_123_ExtendedData());
			 iso.setWsIso_LogStatus(2);
			 
			 //log.WriteLog(consumer.getXmlIn(), TypeLog.bceaut, TypeWriteLog.file);
			 log.WriteOptimizeLog(consumer.getXmlIn(), TypeLog.bceaut, String.class, false, 0);
			 
			 //log.WriteLog(consumer.getXmlResponse(), TypeLog.bceaut, TypeWriteLog.file);
			 log.WriteOptimizeLog(consumer.getXmlResponse(), TypeLog.bceaut, String.class, false, 0);
			  
			
		} catch (Exception e) {
		
			 
			 if(iso.getTickAut().isStarted())
					iso.getTickAut().stop();
			//log.WriteLog(iso.getISO_039p_ResponseDetail(), TypeLog.bceaut, TypeWriteLog.file);
			
			log.WriteOptimizeLog(iso.getISO_039p_ResponseDetail(), TypeLog.bceaut, String.class, false, 0);
			
			log.WriteLogMonitor("Error modulo SPIBceIsAut::SendRequestSPIOrd ", TypeMonitor.error, e);
			log.WriteLogMonitor("Error en Web Service BCE, Secuencial: " + iso.getISO_011_SysAuditNumber() + " Error: " + 
			                    consumer.getExceptionDetail(), TypeMonitor.error, e);
			//generateReversoSPI_ORD(iso);
			iso.setWsISO_FlagStoreForward(true);
			log.WriteLogMonitor("*** Se ha producido un error Servicio Web de BCE (0), se aplicara REVERSO ***", TypeMonitor.monitor, null);
			
		}finally {
			
			if(iso.getISO_039_ResponseCode().startsWith("9")){
				log.WriteLogMonitor("*** Se ha producido un error Servicio Web de BCE (1), se aplicara REVERSO ***", TypeMonitor.monitor, null);
				log.WriteLogMonitor("Error en Web Service BCE, Secuencial: " + iso.getISO_011_SysAuditNumber() + " Error: " + 
	                    consumer.getExceptionDetail(), TypeMonitor.error, null);
				//generateReversoSPI_ORD(iso);
				iso.setWsISO_FlagStoreForward(true);
			}
			
			iso.setWsTempAut((iso.getTickAut().getTime(TimeUnit.MILLISECONDS)/1000.0));
		}
		
		return iso;
	}
	
    public wIso8583 SendRequestSPIOrdMasivo(List<Iso8583> isoList, wIso8583 iso){
    	
    	/*Seccion para cambiar campo 114 por 124*/
    	for (Iso8583 isos : isoList) {
			
    		isos.setISO_124_ExtendedData(null);
			isos.setISO_114_ExtendedData(isos.getISO_036_Track3());
			//isos.setISO_036_Track3(null);
		}
    	
		
		ConsumerClientWs consumer = null;
		Iso8583 isoResponse = null;
		log.WriteLogMonitor("============ INGRESA AL ENVIO ====>>>>>> BCE" , TypeMonitor.monitor, null);
		try {
		     iso.setWsTempAut(0);	 
			 isoResponse = new Iso8583();
			 consumer = new ConsumerClientWs(MemoryGlobal.UrlSpiBCE, 
					    MemoryGlobal.UrlSpiUserBCE, MemoryGlobal.UrlSpiIpAdressBCE, MemoryGlobal.UrlSpiIpSocketBCE, 
					    Integer.parseInt(MemoryGlobal.UrlSpiPortSocketBCE),
					    (iso.getWsTransactionConfig().getProccodeTimeOutValue()/2), iso.getWsTransactionConfig().getProccodeTimeOutValue(), 
					    (MemoryGlobal.currentPath + MemoryGlobal.UrlSpiNameCertBCE),
					    MemoryGlobal.UrlSpiPasswordBCE, MemoryGlobal.UrlSpiCodeEfi_BCE, MemoryGlobal.UrlSpiCodeSwitch_BCE, 
					    MemoryGlobal.UrlSpiAccountEfi_BCE);
			 
			 consumer.setMethodCreditName_SPI("recibirSolicitudOPI");
			 consumer.setNamespacePack_SPI("urn:iso:std:iso:20022:tech:xsd:pacs.008.001.04");
			 consumer.setPrefixPack_SPI("sol");
			 consumer.setNamespaceUrl_SPI("http://www.bce.fin.ec/snp/spi/SolicitudPagoInterbancarioSNPWS");
			 consumer.setPrefixUrl_SPI("urn");
			 consumer.setParamMethodName_SPI("documentoSolicitud");
			 
			 iso.getTickAut().reset();
		     iso.getTickAut().start();
			 isoResponse = consumer.executeSPIMasivo(isoList, StringUtils.Empty());
			 if(iso.getTickAut().isStarted())
				iso.getTickAut().stop();
			 
			 iso.setISO_039_ResponseCode(isoResponse.getISO_039_ResponseCode());
			 iso.setISO_039p_ResponseDetail(isoResponse.getISO_039p_ResponseDetail());
			 iso.setISO_104_TranDescription(StringUtils.IsNullOrEmpty(isoResponse.getISO_104_TranDescription())?
					                        "N/D":isoResponse.getISO_104_TranDescription());
			 iso.setISO_121_ExtendedData(StringUtils.IsNullOrEmpty(isoResponse.getISO_121_ExtendedData())?
                                            "N/D":isoResponse.getISO_121_ExtendedData());
			 iso.setISO_122_ExtendedData(StringUtils.IsNullOrEmpty(isoResponse.getISO_122_ExtendedData())?
                                            "N/D":isoResponse.getISO_122_ExtendedData());
			 iso.setISO_123_ExtendedData(StringUtils.IsNullOrEmpty(isoResponse.getISO_123_ExtendedData())?
                                            "N/D":isoResponse.getISO_123_ExtendedData());
			 iso.setWsIso_LogStatus(2);
			 
			 log.WriteOptimizeLog(consumer.getXmlIn(), TypeLog.bceaut, String.class, false, 0);
			 
			 log.WriteOptimizeLog(consumer.getXmlResponse(), TypeLog.bceaut, String.class, false, 0);
			  
			
		} catch (Exception e) {
		
			 
			 if(iso.getTickAut().isStarted())
					iso.getTickAut().stop();
			
			
			log.WriteOptimizeLog(iso.getISO_039p_ResponseDetail(), TypeLog.bceaut, String.class, false, 0);
			iso.setISO_124_ExtendedData("true");
			log.WriteLogMonitor("Error modulo SPIBceIsAut::SendRequestSPIOrd ", TypeMonitor.error, e);
			log.WriteLogMonitor("Error en Web Service BCE, Secuencial: " + iso.getISO_011_SysAuditNumber() + " Error: " + 
			                    consumer.getExceptionDetail(), TypeMonitor.error, e);

			log.WriteLogMonitor("*** Se ha producido un error Servicio Web de BCE (0), se aplicara REVERSO EN <<LOTES>> ***", TypeMonitor.monitor, null);
			
		}finally {
			
			if(iso.getISO_039_ResponseCode().startsWith("9")){
				iso.setISO_124_ExtendedData("true");
				log.WriteLogMonitor("*** Se ha producido un error Servicio Web de BCE (1), se aplicara REVERSO EN <<LOTES>> ***", TypeMonitor.monitor, null);
				log.WriteLogMonitor("Error en Web Service BCE, Secuencial: " + iso.getISO_011_SysAuditNumber() + " Error: " + 
	                    consumer.getExceptionDetail(), TypeMonitor.error, null);
				
			}
			
			iso.setWsTempAut((iso.getTickAut().getTime(TimeUnit.MILLISECONDS)/1000.0));
		}
		
		return iso;
	}
	
	public wIso8583 RegisterResposeSPI(wIso8583 iso){
		
		int SqlValue = 0;
		IsoSqlMaintenance sql = null;
		try {
			
			iso.setWsTempAut(0);
            sql = new IsoSqlMaintenance();
            iso.getTickAut().reset();
		    iso.getTickAut().start();
				SqlValue = sql.InsertSPIOrdenante(iso);
			if(iso.getTickAut().isStarted())
				iso.getTickAut().stop();
			if(SqlValue == 0){
				
				iso.setISO_039_ResponseCode("000");
				iso.setISO_039p_ResponseDetail("TRANSACCION EXITOSA");
				
			}else{
				
				iso.setISO_039_ResponseCode("308");
				iso.setISO_039p_ResponseDetail("NO SE PUDO INGRESAR EL REGISTRO, PROBLEMAS EN BDD, SQLERROR: " + SqlValue);
			}
			
		} catch (Exception e) {
			if(iso.getTickAut().isStarted())
				iso.getTickAut().stop();
			
			//log.WriteLog(iso.getISO_039p_ResponseDetail(), TypeLog.bceaut, TypeWriteLog.file);
			log.WriteOptimizeLog(iso.getISO_039p_ResponseDetail(), TypeLog.bceaut, String.class, false, 0);
			log.WriteLogMonitor("Error modulo SPIBceIsAut::SendRequestSPIOrd ", TypeMonitor.error, e);
			
		}finally {
			
			iso.setWsTempAut((iso.getTickAut().getTime(TimeUnit.MILLISECONDS)/1000.0));
		}
		return iso;
	}
	
	/*METODO QUE ENVIA LAS TRANSACCIONES HACIA BCE CON LA RESPUESTA (CONSUME EL JAR EXTERNO DE CONEXION CON BCE)*/
    public wIso8583 SendRequestSPINotifications(wIso8583 iso, List<Iso8583> isoList){
		
		ConsumerClientWs consumer = null;
		Iso8583 isoResponse = null;
		
		/*For provisional*/
		if(isoList != null) {
			for (Iso8583 is : isoList) {
				
				System.out.println(is.getISO_039_ResponseCode() + " ==  \n" + 
				is.getISO_039p_ResponseDetail() + "\n  124==> " + is.getISO_124_ExtendedData()
				+ "\n " + is.getISO_055_EMV());
			}
		}
		try {
		     iso.setWsTempAut(0);	 
			 isoResponse = new Iso8583();
			 consumer = new ConsumerClientWs(MemoryGlobal.UrlSpiBCENotificaciones, 
					    MemoryGlobal.UrlSpiUserBCE, MemoryGlobal.UrlSpiIpAdressBCE, MemoryGlobal.UrlSpiIpSocketBCE, 
					    Integer.parseInt(MemoryGlobal.UrlSpiPortSocketBCE),
					    (iso.getWsTransactionConfig().getProccodeTimeOutValue()/2), iso.getWsTransactionConfig().getProccodeTimeOutValue(), 
					    (MemoryGlobal.currentPath + MemoryGlobal.UrlSpiNameCertBCE),
					    MemoryGlobal.UrlSpiPasswordBCE, MemoryGlobal.UrlSpiCodeEfi_BCE, MemoryGlobal.UrlSpiCodeSwitch_BCE, 
					    MemoryGlobal.UrlSpiAccountEfi_BCE);
			 
			consumer.setMethodCreditName_SPI("recibirRespuestaServiciosSNP");
			consumer.setNamespacePack_SPI("urn:iso:std:iso:20022:tech:xsd:pacs.002.001.05");
			consumer.setPrefixPack_SPI("con");
			consumer.setNamespaceUrl_SPI("http://www.bce.fin.ec/wsdl/spr/spi/ConfirmacionWS");
			consumer.setPrefixUrl_SPI("urn");
			consumer.setParamMethodName_SPI("documentoRespuestaSolicitud");
			consumer.setTagRespuestaFechaBCE_ini(MemoryGlobal.URLSpiTagFechaIni);
			consumer.setTagRespuestaFechaBCE_end(MemoryGlobal.URLSpiTagFechaEnd);
			 
			 iso.getTickAut().reset();
		     iso.getTickAut().start();
		    
			 isoResponse = consumer.executeNotificacionRespuestaAlBCE(isoList, iso, StringUtils.Empty());
		    
			 if(iso.getTickAut().isStarted())
				iso.getTickAut().stop();
			 
			 iso.setISO_039_ResponseCode(isoResponse.getISO_039_ResponseCode());
			 iso.setISO_039p_ResponseDetail(isoResponse.getISO_039p_ResponseDetail());
			 iso.setISO_104_TranDescription(StringUtils.IsNullOrEmpty(isoResponse.getISO_104_TranDescription())?
					                        "N/D":isoResponse.getISO_104_TranDescription());
			 iso.setISO_121_ExtendedData(StringUtils.IsNullOrEmpty(isoResponse.getISO_121_ExtendedData())?
                                            "N/D":isoResponse.getISO_121_ExtendedData());
			 iso.setISO_122_ExtendedData(StringUtils.IsNullOrEmpty(isoResponse.getISO_122_ExtendedData())?
                                            "N/D":isoResponse.getISO_122_ExtendedData());
			 iso.setISO_123_ExtendedData(StringUtils.IsNullOrEmpty(isoResponse.getISO_123_ExtendedData())?
                                            "N/D":isoResponse.getISO_123_ExtendedData());
			 iso.setWsIso_LogStatus(2);
			 
			 //log.WriteLog(consumer.getXmlIn(), TypeLog.bceaut, TypeWriteLog.file);
			 log.WriteOptimizeLog(consumer.getXmlIn(), TypeLog.bceaut, String.class, false, 0);
			 //log.WriteLog(consumer.getXmlResponse(), TypeLog.bceaut, TypeWriteLog.file);
			 log.WriteOptimizeLog(consumer.getXmlResponse(), TypeLog.bceaut, String.class, false, 0);
			  
			
		} catch (Exception e) {
		
			 
			 if(iso.getTickAut().isStarted())
					iso.getTickAut().stop();
			//log.WriteLog(iso.getISO_039p_ResponseDetail(), TypeLog.bceaut, TypeWriteLog.file);
			log.WriteOptimizeLog(iso.getISO_039p_ResponseDetail(), TypeLog.bceaut, String.class, false, 0);
			log.WriteLogMonitor("Error modulo SPIBceIsAut::SendRequestSPINotifications ", TypeMonitor.error, e);
			log.WriteLogMonitor("Error en Web Service BCE, Secuencial: " + iso.getISO_011_SysAuditNumber() + " Error: " + 
			                    consumer.getExceptionDetail(), TypeMonitor.error, e);
			//generateReversoSPI_ORD(iso);
			iso.setWsISO_FlagStoreForward(true);
			log.WriteLogMonitor("*** Se ha producido un error Servicio Web de BCE (0), se aplicara REVERSO ***", TypeMonitor.monitor, null);
			
		}finally {
			
			if(iso.getISO_039_ResponseCode().startsWith("9")){
				log.WriteLogMonitor("*** Se ha producido un error Servicio Web de BCE Notificaciones (1), se aplicara REVERSO ***", TypeMonitor.monitor, null);
				log.WriteLogMonitor("Error en Web Service BCE, Secuencial: " + iso.getISO_011_SysAuditNumber() + " Error: " + 
	                    consumer.getExceptionDetail(), TypeMonitor.error, null);
				//generateReversoSPI_ORD(iso);
				iso.setWsISO_FlagStoreForward(true);
			}
			
			iso.setWsTempAut((iso.getTickAut().getTime(TimeUnit.MILLISECONDS)/1000.0));
		}
		
		return iso;
	}
	
	public void generateReversoSPI_ORD(wIso8583 iso){
		Iso8583 isoRever = null;
		csProcess processor = null;
		String secuencial = StringUtils.Empty();
		try {
			  if(iso.getListTrxAfectCommands().size() > 0){
				
				  for (int i = 0; i < iso.getListTrxAfectCommands().size(); i++) {
					
					    secuencial = iso.getListTrxAfectCommands().get(i); 
						isoRever = new Iso8583(iso);
						isoRever.setISO_000_Message_Type("1400");
						isoRever.setISO_011_SysAuditNumber(GeneralUtils.GetSecuencial(iso.getISO_011_SysAuditNumber().length()));
						isoRever.setISO_090_OriginalData(secuencial);
						processor = new csProcess();
						isoRever = processor.ProcessTransactionMain(isoRever, iso.getWs_IP());
						log.WriteLogMonitor("*** Execute REVERSE (NO RESPONSE BCE) ******\n Secuencial: " + secuencial, TypeMonitor.monitor, null);
				  }
				
			  }else{
				  
				  log.WriteLogMonitor("*** No se puede Ejecutar REVERSO AUTOMATICO {SecuentialArrayNoFound}: " + iso.getISO_011_SysAuditNumber(), TypeMonitor.monitor, null);
			  }
		} catch (Exception e) {
			log.WriteLogMonitor("Error modulo SPIBceIsAut::generateReversoSPI_ORD ", TypeMonitor.error, e);
		}
	}
	
	public wIso8583 ProccesAckBCENotifications(wIso8583 iso) {
		
		try {
			
			iso.setISO_039_ResponseCode("000");
			iso.setISO_039p_ResponseDetail("TRANSACCION EXITOSA");
			iso.setWsIso_LogStatus(2);
			
		} catch (Exception e) {
			
			log.WriteLogMonitor("Error modulo SPIBceIsAut::generateReversoSPI_ORD ", TypeMonitor.error, e);
		}
		return iso;
	}
}
