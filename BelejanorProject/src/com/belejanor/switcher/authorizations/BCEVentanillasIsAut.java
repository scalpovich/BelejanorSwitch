package com.belejanor.switcher.authorizations;

import java.util.concurrent.TimeUnit;
import com.belejanor.switcher.cscoreswitch.Iso8583;
import com.belejanor.switcher.cscoreswitch.wIso8583;
import com.belejanor.switcher.logger.Logger;
import com.belejanor.switcher.logger.LoggerConfig.TypeLog;
import com.belejanor.switcher.logger.LoggerConfig.TypeMonitor;
import com.belejanor.switcher.logger.LoggerConfig.TypeWriteLog;
import com.belejanor.switcher.memcached.MemoryGlobal;
import com.belejanor.switcher.utils.GeneralUtils;
import com.belejanor.switcher.utils.StringUtils;
import com.belejanor.switcher.client.ConsumerClientWs;

public class BCEVentanillasIsAut {
	
	private Logger log;

	public BCEVentanillasIsAut() {
		
		log = new Logger();
	}
	
	public wIso8583 executeDepositoVC(wIso8583 iso){
		
		try {
			
			ConsumerClientWs consumer = new ConsumerClientWs(MemoryGlobal.endPointURLDepoRetVc, 
					MemoryGlobal.userWsBCEVc, MemoryGlobal.ipAcqBCEVc, MemoryGlobal.ipSocketVc, MemoryGlobal.portSocketVc, 
					(iso.getWsTransactionConfig().getProccodeTimeOutValue() / 2)  , 
					iso.getWsTransactionConfig().getProccodeTimeOutValue(), 
					MemoryGlobal.currentPath + MemoryGlobal.nameCertVc, MemoryGlobal.passwordCertVc,
				    MemoryGlobal.BCE_Efi_VC, MemoryGlobal.BICFI_Bce, MemoryGlobal.AccountConciliationVc);
			
			Iso8583 ISO = new Iso8583(iso);
			iso.getTickAut().reset();
			iso.getTickAut().start();
			ISO = consumer.executeDepositoVC(iso, StringUtils.Empty());
			iso.getTickAut().stop();
			log.WriteLog(consumer.getXmlIn(), TypeLog.bceaut, TypeWriteLog.file);
			log.WriteLog(consumer.getXmlResponse(), TypeLog.bceaut, TypeWriteLog.file);
			iso.setISO_039_ResponseCode(ISO.getISO_039_ResponseCode());
			iso.setISO_039p_ResponseDetail(ISO.getISO_039p_ResponseDetail());
			iso.setISO_038_AutorizationNumber(StringUtils.IsNullOrEmpty(ISO.getISO_038_AutorizationNumber())
					        ?StringUtils.Empty():ISO.getISO_038_AutorizationNumber());
			iso.setISO_104_TranDescription(StringUtils.IsNullOrEmpty(ISO.getISO_104_TranDescription())
					        ?StringUtils.Empty():ISO.getISO_104_TranDescription());
			iso.setWsIso_LogStatus(2);
			
		} catch (Exception e) {
			
			if(iso.getTickAut().isStarted())
				iso.getTickAut().stop();
			
			iso.setISO_039_ResponseCode("909");
			iso.setISO_039p_ResponseDetail(GeneralUtils.ExceptionToString(null, e, true));
			log.WriteLog(iso.getISO_039p_ResponseDetail(), TypeLog.bceaut, TypeWriteLog.file);
			log.WriteLogMonitor("Error modulo BCEVentanillasIsAut::executeDepositoVC ", TypeMonitor.error, e);
			
		}finally {
			
			iso.setWsTempAut((iso.getTickAut().getTime(TimeUnit.MILLISECONDS)/1000.0));
			iso.setISO_120_ExtendedData(String.valueOf(iso.getWsIso_LogStatus()));
		}
		
		return iso;
	}
	public wIso8583 executeTransferenciaVC(wIso8583 iso){
		
		try {
			
			
			ConsumerClientWs consumer = new ConsumerClientWs(MemoryGlobal.endPointURLTransferVc, 
					MemoryGlobal.userWsBCEVc, MemoryGlobal.ipAcqBCEVc, MemoryGlobal.ipSocketVc, MemoryGlobal.portSocketVc, 
					(iso.getWsTransactionConfig().getProccodeTimeOutValue() / 2)  , 
					iso.getWsTransactionConfig().getProccodeTimeOutValue(), 
					MemoryGlobal.currentPath + MemoryGlobal.nameCertVc, MemoryGlobal.passwordCertVc,
				    MemoryGlobal.BCE_Efi_VC, MemoryGlobal.BICFI_Bce, MemoryGlobal.AccountConciliationVc);
			
			Iso8583 ISO = new Iso8583(iso);
			iso.getTickAut().reset();
			iso.getTickAut().start();
			ISO = consumer.executeTransferenciaVC(iso, StringUtils.Empty());
			iso.getTickAut().stop();
			log.WriteLog(consumer.getXmlIn(), TypeLog.bceaut, TypeWriteLog.file);
			log.WriteLog(consumer.getXmlResponse(), TypeLog.bceaut, TypeWriteLog.file);
			iso.setISO_039_ResponseCode(ISO.getISO_039_ResponseCode());
			iso.setISO_039p_ResponseDetail(ISO.getISO_039p_ResponseDetail());
			iso.setISO_038_AutorizationNumber(StringUtils.IsNullOrEmpty(ISO.getISO_038_AutorizationNumber())
					        ?StringUtils.Empty():ISO.getISO_038_AutorizationNumber());
			iso.setISO_104_TranDescription(StringUtils.IsNullOrEmpty(ISO.getISO_104_TranDescription())
					        ?StringUtils.Empty():ISO.getISO_104_TranDescription());
			iso.setWsIso_LogStatus(2);
			
		} catch (Exception e) {
			
			if(iso.getTickAut().isStarted())
				iso.getTickAut().stop();
			
			iso.setISO_039_ResponseCode("909");
			iso.setISO_039p_ResponseDetail(GeneralUtils.ExceptionToString(null, e, true));
			log.WriteLog(iso.getISO_039p_ResponseDetail(), TypeLog.bceaut, TypeWriteLog.file);
			log.WriteLogMonitor("Error modulo BCEVentanillasIsAut::executeTrandferenciaVC ", TypeMonitor.error, e);
			
		}finally {
			
			iso.setWsTempAut((iso.getTickAut().getTime(TimeUnit.MILLISECONDS)/1000.0));
			iso.setISO_120_ExtendedData(String.valueOf(iso.getWsIso_LogStatus()));
		}
		
		return iso;
	}
	public wIso8583 executeRetiroVC(wIso8583 iso){
		
		try {
			
			
			ConsumerClientWs consumer = new ConsumerClientWs(MemoryGlobal.endPointURLDepoRetVc, 
					MemoryGlobal.userWsBCEVc, MemoryGlobal.ipAcqBCEVc, MemoryGlobal.ipSocketVc, MemoryGlobal.portSocketVc, 
					(iso.getWsTransactionConfig().getProccodeTimeOutValue() / 2)  , 
					iso.getWsTransactionConfig().getProccodeTimeOutValue(), 
					MemoryGlobal.currentPath + MemoryGlobal.nameCertVc, MemoryGlobal.passwordCertVc,
				    MemoryGlobal.BCE_Efi_VC, MemoryGlobal.BICFI_Bce, MemoryGlobal.AccountConciliationVc);
			
			Iso8583 ISO = new Iso8583(iso);
			iso.getTickAut().reset();
			iso.getTickAut().start();
			ISO = consumer.executeRetiroVC(iso, StringUtils.Empty());
			iso.getTickAut().stop();
			log.WriteLog(consumer.getXmlIn(), TypeLog.bceaut, TypeWriteLog.file);
			log.WriteLog(consumer.getXmlResponse(), TypeLog.bceaut, TypeWriteLog.file);
			iso.setISO_039_ResponseCode(ISO.getISO_039_ResponseCode());
			iso.setISO_039p_ResponseDetail(ISO.getISO_039p_ResponseDetail());
			iso.setISO_038_AutorizationNumber(StringUtils.IsNullOrEmpty(ISO.getISO_038_AutorizationNumber())
					        ?StringUtils.Empty():ISO.getISO_038_AutorizationNumber());
			iso.setISO_104_TranDescription(StringUtils.IsNullOrEmpty(ISO.getISO_104_TranDescription())
					        ?StringUtils.Empty():ISO.getISO_104_TranDescription());
			iso.setWsIso_LogStatus(2);
			
		} catch (Exception e) {
			
			if(iso.getTickAut().isStarted())
				iso.getTickAut().stop();
			iso.setISO_039_ResponseCode("909");
			iso.setISO_039p_ResponseDetail(GeneralUtils.ExceptionToString(null, e, true));
			log.WriteLog(iso.getISO_039p_ResponseDetail(), TypeLog.bceaut, TypeWriteLog.file);
			log.WriteLogMonitor("Error modulo BCEVentanillasIsAut::executeRetiroVC ", TypeMonitor.error, e);
			
		}finally {
			
			iso.setWsTempAut((iso.getTickAut().getTime(TimeUnit.MILLISECONDS)/1000.0));
			iso.setISO_120_ExtendedData(String.valueOf(iso.getWsIso_LogStatus()));
		}
		
		return iso;
	}
}

