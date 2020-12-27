package com.belejanor.switcher.bridges;

import com.belejanor.switcher.cscoreswitch.Iso8583;
import com.belejanor.switcher.cscoreswitch.csProcess;
import com.belejanor.switcher.logger.Logger;
import com.belejanor.switcher.logger.LoggerConfig.TypeMonitor;
import com.belejanor.switcher.parser.EquifaxParser;
import com.belejanor.switcher.struct.equifax.ConsultaDatosPersona;
import com.belejanor.switcher.struct.equifax.ConsultaDatosPersonaRespuesta;
import com.belejanor.switcher.struct.equifax.RegisterData;
import com.belejanor.switcher.struct.equifax.ResponseRegisterDataEquifax;

public class BridgeEquifax {

	private Logger log;
	private String codError;
	private String desError;
	
	public  BridgeEquifax() {
		
		log = new Logger();
		this.codError = "000";
		this.desError = "TRANSACCION DIRECCIONADA";
	}
	
	public String getCodError() {
		return codError;
	}

	public String getDesError() {
		return desError;
	}

	public ConsultaDatosPersonaRespuesta procesaConsultaEquifax(ConsultaDatosPersona data, String IP) {
		
		Iso8583 iso = null;
		ConsultaDatosPersonaRespuesta dataResponse = null;
		EquifaxParser parser = null;
		csProcess processor = new csProcess();
		try {
			
			parser = new EquifaxParser();
			iso = new Iso8583();
			iso = parser.parserGetDataEquifax(data);
			if(parser.getCodError().equals("000")) {
				
				iso = processor.ProcessTransactionMain(iso, IP);
				if(iso.getISO_039_ResponseCode().equals("000")) {
					
					parser = new EquifaxParser();
					dataResponse = parser.buildResponseToEquifax(iso);
					if(parser.getCodError().equals("999")) {
						
						this.codError = "999";
						this.desError = parser.getDesError();
					}
					
				}else {
					
					this.codError = "999";
					this.desError = iso.getISO_039p_ResponseDetail();
				}
				
			}else if (parser.getCodError().equals("999")) {
				
				this.codError = "999";
				this.desError = parser.getDesError();
				
			} else {
				
				this.codError = "999";
				this.desError = iso.getISO_039p_ResponseDetail();
			}
			
		} catch (Exception e) {
		
			log.WriteLogMonitor("Error modulo BridgeEquifax::procesaConsultaEquifax ", TypeMonitor.error, e);
		}
		return dataResponse;
	}
	
    public ResponseRegisterDataEquifax procesaRegistroEquifax(RegisterData data, String IP) {
		
		Iso8583 iso = null;
		ResponseRegisterDataEquifax dataResponse = null;
		EquifaxParser parser = null;
		csProcess processor = new csProcess();
		try {
			
			parser = new EquifaxParser();
			iso = new Iso8583();
			iso = parser.parserRegisterDataEquifax(data);
			if(parser.getCodError().equals("000")) {
				
				dataResponse = new ResponseRegisterDataEquifax();
				iso = processor.ProcessTransactionMain(iso, IP);
				dataResponse.setCodError(iso.getISO_039_ResponseCode());
				dataResponse.setDesError(iso.getISO_039p_ResponseDetail());
				
			}else if (parser.getCodError().equals("999")) {
				
				this.codError = "999";
				this.desError = parser.getDesError();
				
			} else {
				
				dataResponse = new ResponseRegisterDataEquifax();
				dataResponse.setCodError(iso.getISO_039_ResponseCode());
				dataResponse.setDesError(iso.getISO_039p_ResponseDetail());
			}
			
		} catch (Exception e) {
		
			log.WriteLogMonitor("Error modulo BridgeEquifax::procesaregistroEquifax ", TypeMonitor.error, e);
		}
		return dataResponse;
	}
	
}
