package com.belejanor.switcher.authorizations;

import java.util.List;
import java.util.concurrent.TimeUnit;

import com.belejanor.switcher.cscoreswitch.Iso8583;
import com.belejanor.switcher.cscoreswitch.wIso8583;
import com.belejanor.switcher.logger.Logger;
import com.belejanor.switcher.logger.LoggerConfig.TypeLog;
import com.belejanor.switcher.logger.LoggerConfig.TypeMonitor;
import com.belejanor.switcher.memcached.MemoryGlobal;
import com.belejanor.switcher.utils.StringUtils;
import com.belejanor.switcher.client.ConsumerClientWs;

public class SCIBceIsAut {

	private Logger log;
	private List<Iso8583> isoList;
	public SCIBceIsAut(){
		
	    log = new Logger();
	}
	public List<Iso8583> getIsoList() {
		return isoList;
	}
	public void setIsoList(List<Iso8583> isoList) {
		this.isoList = isoList;
	}
	
    public wIso8583 SendRequestSCINotifications(wIso8583 iso, List<Iso8583> isoList){
		
		ConsumerClientWs consumer = null;
		Iso8583 isoResponse = null;
		
		try {
		     iso.setWsTempAut(0);	 
			 isoResponse = new Iso8583();
			 consumer = new ConsumerClientWs(MemoryGlobal.UrlSciBCENotificaciones, 
					    MemoryGlobal.UrlSciUserBCE, MemoryGlobal.UrlSciIpAdressBCE, MemoryGlobal.UrlSciIpSocketBCE, 
					    Integer.parseInt(MemoryGlobal.UrlSciPortSocketBCE),
					    (iso.getWsTransactionConfig().getProccodeTimeOutValue()/2), iso.getWsTransactionConfig().getProccodeTimeOutValue(), 
					    (MemoryGlobal.currentPath + MemoryGlobal.UrlSciNameCertBCE),
					    MemoryGlobal.UrlSciPasswordBCE, MemoryGlobal.UrlSpiCodeEfi_BCE, MemoryGlobal.UrlSpiCodeSwitch_BCE, 
					    MemoryGlobal.UrlSpiAccountEfi_BCE);
			 
			consumer.setMethodCreditName_SPI("recibirRespuestaServiciosSNPSCI");
			consumer.setNamespacePack_SPI("urn:iso:std:iso:20022:tech:xsd:pacs.002.001.05");
			consumer.setPrefixPack_SPI("con");
			consumer.setNamespaceUrl_SPI("http://www.bce.fin.ec/wsdl/spr/sci/ConfirmacionWS");
			consumer.setPrefixUrl_SPI("urn");
			consumer.setParamMethodName_SPI("documentoRespuestaSolicitud");
			consumer.setTagRespuestaFechaBCE_ini(MemoryGlobal.URLSciTagFechaIni);
			consumer.setTagRespuestaFechaBCE_end(MemoryGlobal.URLSciTagFechaEnd);
			 
			 iso.getTickAut().reset();
		     iso.getTickAut().start();
		    
			 isoResponse = consumer.executeNotificacionRespuestaAlBCE(isoList, iso, StringUtils.Empty());
			 log.WriteLogMonitor("*** ISO POSTERIOR AL ENVIO  *** " + isoResponse.getISO_039_ResponseCode() + " - " 
			 + isoResponse.getISO_039p_ResponseDetail() , TypeMonitor.monitor, null);
			 
		    
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
			
			 log.WriteLogMonitor("*** ISO POSTERIOR AL ENVIO  *** " + isoResponse.getISO_039_ResponseCode() + " - " 
					 + isoResponse.getISO_039p_ResponseDetail() , TypeMonitor.monitor, null);
			 
			log.WriteOptimizeLog(iso.getISO_039p_ResponseDetail(), TypeLog.bceaut, String.class, false, 0);
			log.WriteLogMonitor("Error modulo SCIBceIsAut::SendRequestSCINotifications ", TypeMonitor.error, e);
			log.WriteLogMonitor("Error en Web Service BCE, Secuencial: " + iso.getISO_011_SysAuditNumber() + " Error: " + 
			                    consumer.getExceptionDetail(), TypeMonitor.error, e);
			//generateReversoSPI_ORD(iso);
			iso.setWsISO_FlagStoreForward(true);
			log.WriteLogMonitor("*** Se ha producido un error Servicio Web SCI de BCE (0), se aplicara REVERSO ***", TypeMonitor.monitor, null);
			
		}finally {
			
			if(iso.getISO_039_ResponseCode().startsWith("9") || iso.getISO_039_ResponseCode().startsWith("1")){
				log.WriteLogMonitor("*** Se ha producido un error Servicio Web de BCE Notificaciones SCI (1), se aplicara REVERSO ***", TypeMonitor.monitor, null);
				log.WriteLogMonitor("Error en Web Service BCE, Secuencial: " + iso.getISO_011_SysAuditNumber() + " Error: " + 
	                    consumer.getExceptionDetail(), TypeMonitor.error, null);
				//generateReversoSPI_ORD(iso);
				iso.setWsISO_FlagStoreForward(true);
			}
			
			iso.setWsTempAut((iso.getTickAut().getTime(TimeUnit.MILLISECONDS)/1000.0));
		}
		
		return iso;
	}
}
