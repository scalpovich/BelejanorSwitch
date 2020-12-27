package com.belejanor.switcher.bridges;

import com.belejanor.switcher.acquirers.BanredIsAcq;
import com.belejanor.switcher.acquirers.ITranBIMOAcq;
import com.belejanor.switcher.bimo.pacs.camt_998_212.Document;
import com.belejanor.switcher.logger.Logger;
import com.belejanor.switcher.logger.LoggerConfig.TypeMonitor;
import com.belejanor.switcher.utils.GeneralUtils;
import com.belejanor.switcher.utils.StringUtils;

public class BridgeBanredBIMOAcq implements ITranBIMOAcq{

	private Logger log;
	private String codError;
	private String desError;
	
	public BridgeBanredBIMOAcq() {
	
		super();
		log = new Logger();
		this.codError = "000";
		this.desError = StringUtils.Empty();
	}
	
	public String getCodError() {
		return codError;
	}

	public void setCodError(String codError) {
		this.codError = codError;
	}

	public String getDesError() {
		return desError;
	}

	public void setDesError(String desError) {
		this.desError = desError;
	}

	@Override
	public Document processEnrollPerson(com.belejanor.switcher.bimo.pacs.camt_998_211.Document document
			   , String IP){
		
		Document response = null;
		BanredIsAcq acq = new BanredIsAcq();
		acq.setIp(IP);
		try {
			
			response = acq.processEnrollPerson(document);
			if(!acq.getCodError().equals("000")){
				
				this.codError = acq.getCodError();
				this.desError = acq.getDesError();
			}
			
			
		} catch (Exception e) {
			
			this.codError = "070";
			this.desError = GeneralUtils.ExceptionToString("ERROR EN PROCESOS", e, false);
			log.WriteLogMonitor("Error modulo BridgeBanredBIMOAcq::@processEnrollPerson ", TypeMonitor.error, e);
		}
		return response;
	}

	@Override
	public com.belejanor.switcher.bimo.pacs.camt_998_222.Document processDesenrollPerson(
			com.belejanor.switcher.bimo.pacs.camt_998_221.Document document
			      , String IP) {
		
		com.belejanor.switcher.bimo.pacs.camt_998_222.Document response = null;
		BanredIsAcq acq = new BanredIsAcq();
		acq.setIp(IP);
		
		try {
			
			response = acq.processDesenrollPerson(document);
			if(!acq.getCodError().equals("000")){
				
				this.codError = acq.getCodError();
				this.desError = acq.getDesError();
			}
			
		} catch (Exception e) {
			
			this.codError = "070";
			this.desError = GeneralUtils.ExceptionToString("ERROR EN PROCESOS", e, false);
			log.WriteLogMonitor("Error modulo BridgeBanredBIMOAcq::@processDesenrollPerson ", TypeMonitor.error, e);
		}
		return response;
	}

	@Override
	public com.belejanor.switcher.bimo.pacs.camt_998_202.Document processQueryBalance(
			com.belejanor.switcher.bimo.pacs.camt_998_201.Document document, String IP) {
		
		com.belejanor.switcher.bimo.pacs.camt_998_202.Document response = null;
		BanredIsAcq acq = new BanredIsAcq();
		acq.setIp(IP);
		
		try {
			
			response = acq.processQueryBalance(document);
			if(!acq.getCodError().equals("000")){
				
				this.codError = acq.getCodError();
				this.desError = acq.getDesError();
			}
			
		} catch (Exception e) {
			
			this.codError = "070";
			this.desError = GeneralUtils.ExceptionToString("ERROR EN PROCESOS", e, false);
			log.WriteLogMonitor("Error modulo BridgeBanredBIMOAcq::@processQueryBalance ", TypeMonitor.error, e);
		}
		return response;
	}

	@Override
	public com.belejanor.switcher.bimo.pacs.pacs_002_022.Document processCredit(
			com.belejanor.switcher.bimo.pacs.pacs_008_021.Document document, String IP) {
		
		com.belejanor.switcher.bimo.pacs.pacs_002_022.Document response = null;
		BanredIsAcq acq = new BanredIsAcq();
		acq.setIp(IP);
		
		try {
			
			response = acq.processCredit(document);
			if(!acq.getCodError().equals("000")){
				
				this.codError = acq.getCodError();
				this.desError = acq.getDesError();
			}
			
		} catch (Exception e) {
			
			this.codError = "070";
			this.desError = GeneralUtils.ExceptionToString("ERROR EN PROCESOS", e, false);
			log.WriteLogMonitor("Error modulo BridgeBanredBIMOAcq::@processCredit ", TypeMonitor.error, e);
		}
		return response;
	}

	@Override
	public com.belejanor.switcher.bimo.pacs.pacs_002_042.Document processReverCredit(
			com.belejanor.switcher.bimo.pacs.pacs_007_041.Document document, String IP) {
		
		com.belejanor.switcher.bimo.pacs.pacs_002_042.Document response = null;
		BanredIsAcq acq = new BanredIsAcq();
		acq.setIp(IP);
		
		try {
			
			response = acq.processReverCredit(document);
			if(!acq.getCodError().equals("000")){
				
				this.codError = acq.getCodError();
				this.desError = acq.getDesError();
			}
			
		} catch (Exception e) {
			
			this.codError = "070";
			this.desError = GeneralUtils.ExceptionToString("ERROR EN PROCESOS", e, false);
			log.WriteLogMonitor("Error modulo BridgeBanredBIMOAcq::@processReverCredit ", TypeMonitor.error, e);
		}
		return response;
	}

	@Override
	public com.belejanor.switcher.bimo.pacs.pacs_002_072.Document processDebit(
			com.belejanor.switcher.bimo.pacs.pacs_008_071.Document document, String IP) {
		
		com.belejanor.switcher.bimo.pacs.pacs_002_072.Document response = null;
		BanredIsAcq acq = new BanredIsAcq();
		acq.setIp(IP);
		
		try {
			
			response = acq.processDebit(document);
			if(!acq.getCodError().equals("000")){
				
				this.codError = acq.getCodError();
				this.desError = acq.getDesError();
			}
			
		} catch (Exception e) {
			
			this.codError = "070";
			this.desError = GeneralUtils.ExceptionToString("ERROR EN PROCESOS", e, false);
			log.WriteLogMonitor("Error modulo BridgeBanredBIMOAcq::@processDebit ", TypeMonitor.error, e);
		}
		return response;
	}

	@Override
	public com.belejanor.switcher.bimo.pacs.pacs_002_052.Document processReverDebit(
					com.belejanor.switcher.bimo.pacs.pacs_007_051.Document document, String IP) {
		
		com.belejanor.switcher.bimo.pacs.pacs_002_052.Document response = null;
		BanredIsAcq acq = new BanredIsAcq();
		acq.setIp(IP);
		
		try {
			
			response = acq.processReverDebit(document);
			if(!acq.getCodError().equals("000")){
				
				this.codError = acq.getCodError();
				this.desError = acq.getDesError();
			}
			
		} catch (Exception e) {
			
			this.codError = "070";
			this.desError = GeneralUtils.ExceptionToString("ERROR EN PROCESOS", e, false);
			log.WriteLogMonitor("Error modulo BridgeBanredBIMOAcq::@processReverDebit ", TypeMonitor.error, e);
		}
		return response;
	}

	@Override
	public com.belejanor.switcher.bimo.pacs.camt_998_232.Document processGenOtp(
			com.belejanor.switcher.bimo.pacs.camt_998_231.Document document, String IP) {
		
		com.belejanor.switcher.bimo.pacs.camt_998_232.Document response = null;
		BanredIsAcq acq = new BanredIsAcq();
		acq.setIp(IP);
		
		try {
			
			response = acq.processGenerateOTP(document);
			if(!acq.getCodError().equals("000")){
				
				this.codError = acq.getCodError();
				this.desError = acq.getDesError();
			}
			
		} catch (Exception e) {
			
			this.codError = "070";
			this.desError = GeneralUtils.ExceptionToString("ERROR EN PROCESOS", e, false);
			log.WriteLogMonitor("Error modulo BridgeBanredBIMOAcq::@processGenOtp ", TypeMonitor.error, e);
		}
		return response;
	}

}
