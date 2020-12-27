package com.belejanor.switcher.bridges;

import com.belejanor.switcher.banred.pagodirecto.struct.TrxConsultaPagoDirectoRequest;
import com.belejanor.switcher.banred.pagodirecto.struct.TrxConsultaPagoDirectoResponse;
import com.belejanor.switcher.banred.pagodirecto.struct.TrxMessageControlRequest;
import com.belejanor.switcher.banred.pagodirecto.struct.TrxMessageControlResponse;
import com.belejanor.switcher.banred.pagodirecto.struct.TrxTransferenciaPagoDirectoRequest;
import com.belejanor.switcher.banred.pagodirecto.struct.TrxTransferenciaPagoDirectoResponse;
import com.belejanor.switcher.cscoreswitch.Iso8583;
import com.belejanor.switcher.cscoreswitch.csProcess;
import com.belejanor.switcher.logger.Logger;
import com.belejanor.switcher.logger.LoggerConfig.TypeMonitor;
import com.belejanor.switcher.parser.PagoDirectoParser;
import com.belejanor.switcher.structbanred.StructBanredMessage;
import com.belejanor.switcher.structbanred.TypeMessage;
import com.belejanor.switcher.utils.StringUtils;

public class BridgePagoDirectoBanred {

	private Logger log;
	private String codError;
	private String desError;
	
	public BridgePagoDirectoBanred() {
	
		super();
		log = new Logger();
		this.codError = "000";
		this.desError = StringUtils.Empty();
	}

	public String getCodError() {
		return codError;
	}

	public String getDesError() {
		return desError;
	}
	
	public StructBanredMessage processTransactions(StructBanredMessage messageBanred, String IP) {
		
		Iso8583 iso = null;
		PagoDirectoParser parser = null;
		csProcess processor = null;
		try {
			
			parser = new PagoDirectoParser();
			switch (messageBanred.getProccodeMessage()) {
			/*Mensaje de Control*/
			case "2099":
			case "2001":
				
				TrxMessageControlRequest req = parser.convertBytesToMessageControlRequest(messageBanred);
				try {
					
					if(parser.getCodError().equals("000")) {
						
						parser = new PagoDirectoParser();
						iso = parser.convertTrxMessageControlRequestToIso8583(req);
						if(parser.getCodError().equals("000")) {
							
							processor = new csProcess();
							iso = processor.ProcessTransactionMain(iso, IP);
							
							TrxMessageControlResponse res = new TrxMessageControlResponse();
							res.setControl_dependent_data(req.getControl_dependent_data());
							res.setMessage_control_group(req.getMessage_control_group());
							res.setResult_code_9004(parser.HomologaErrorIso8583ToOnTwo(iso));
							
							parser = new PagoDirectoParser();
							byte[] resbytes = parser.TrxMessageControlResponseToBytes(res);
							if(parser.getCodError().equals("000")) {
								
								messageBanred.setBodyBytesMessageResponse(resbytes);
								
							}else {
								
								messageBanred.setBodyBytesMessageResponse("909 SYSTEM MAL FUNCTION".getBytes());
								log.WriteLogMonitor("Error parsear TrxMessageControlResponse to byte[] " + parser.getDesError()
												, TypeMonitor.error, null);
							}
							
							
						}else {
							
							messageBanred.setBodyBytesMessageResponse("909 SYSTEM MAL FUNCTION".getBytes());
							log.WriteLogMonitor("Error parsear TrxMessageControlRequest to Iso8583 " + parser.getDesError()
											, TypeMonitor.error, null);
						}
						
					}else {
						
						messageBanred.setBodyBytesMessageResponse("909 SYSTEM MAL FUNCTION".getBytes());
						log.WriteLogMonitor("Error parsear StructBanredMessage to TrxMessageControlRequest " + parser.getDesError()
											, TypeMonitor.error, null);
					}
					
				} finally {
					
					/*Cambio el mensaje a mandar a la cola a Response*/
					messageBanred.setTypeMessage(TypeMessage.RS);
				}	
				break;
			/*Consultas de Pago Directo (Tarjetas de Credito y Cuentas de Ahorro o corriente)*/	
			case "0163":
				
					TrxConsultaPagoDirectoRequest conReq = parser.convertBytesToTrxConsultaPagoDirectoRequest(messageBanred);
					try {
						
						if(parser.getCodError().equals("000")) {
							
							parser = new PagoDirectoParser();
							iso = parser.convertTrxConsultaPagoDirectoRequestToIso8583(conReq);
							
							if(parser.getCodError().equals("000")) {
								
								processor = new csProcess();
								iso = processor.ProcessTransactionMain(iso, IP);
								
								TrxConsultaPagoDirectoResponse reponse_ = new TrxConsultaPagoDirectoResponse(parser.HomologaErrorIso8583ToOnTwo(iso));
								parser = new PagoDirectoParser();
								reponse_ = parser.Iso8583ToTrxConsultaPagoDirectoResponse(conReq, iso);
								
								if(parser.getCodError().equals("000")) {
									
									parser = new PagoDirectoParser();
									byte[] resBytes = parser.TrxConsultaPagoDirectoResponseToByte(reponse_);
									
									if(parser.getCodError().equals("000")) {
										
										messageBanred.setBodyBytesMessageResponse(resBytes);
										
									}else {
										
										messageBanred.setBodyBytesMessageResponse("909 SYSTEM MAL FUNCTION".getBytes());
										log.WriteLogMonitor("Error parsear Respuesta TrxConsultaPagoDirectoResponse to ByteArray " + parser.getDesError()
														, TypeMonitor.error, null);
									}
									
								}else {
									
									messageBanred.setBodyBytesMessageResponse("909 SYSTEM MAL FUNCTION".getBytes());
									log.WriteLogMonitor("Error parsear Respuesta Iso8583 to TrxConsultaPagoDirectoResponse" + parser.getDesError()
													, TypeMonitor.error, null);
								}
								
							}else {
								
								messageBanred.setBodyBytesMessageResponse("909 SYSTEM MAL FUNCTION".getBytes());
								log.WriteLogMonitor("Error parsear TrxConsultaPagoDirectoRequest to Iso8583 " + parser.getDesError()
												, TypeMonitor.error, null);
							}
							
						}else {
							
							messageBanred.setBodyBytesMessageResponse("909 SYSTEM MAL FUNCTION".getBytes());
							log.WriteLogMonitor("Error parsear StructBanredMessage to TrxConsultaPagoDirectoRequest " + parser.getDesError()
												, TypeMonitor.error, null);
						}
						
					} finally {
						/*Cambio el mensaje a mandar a la cola a Response*/
						messageBanred.setTypeMessage(TypeMessage.RS);
					}
				break;
			case "0539": //Transferencia a Cuenta de Ahorro y su reverso CONDICIONAL
			case "0439": //Transferencia a Cuenta Corriente y su reverso CONDICIONAL
			case "0239": //Transferecncia a Tarjeta de Credito y su reverso CONDICIONAL
			case "0524": //Reverso REAL a Transferencia cuenta de Ahorro
			case "0424": //Reverso REAL a Transferencia cuenta corriente
			case "0224": //Reverso REAL a Transferencia a tarjeta de credito
				
				
				TrxTransferenciaPagoDirectoRequest pagReq = parser.convertBytesToTrxTransferenciaPagoDirectoRequest(messageBanred);
				try {
					
					if(parser.getCodError().equals("000")) {
						
						parser = new PagoDirectoParser();
						iso = parser.convertTrxTransferenciaPagoDirectoRequestToIso8583(pagReq);
						
						if(parser.getCodError().equals("000")) {
							
							processor = new csProcess();
							iso = processor.ProcessTransactionMain(iso, IP);
							
							TrxTransferenciaPagoDirectoResponse reponse_ = new TrxTransferenciaPagoDirectoResponse(parser.HomologaErrorIso8583ToOnTwo(iso));
							parser = new PagoDirectoParser();
							reponse_ = parser.Iso8583ToTrxTransferenciaPagoDirectoResponse(pagReq, iso);
							
							if(parser.getCodError().equals("000")) {
								
								parser = new PagoDirectoParser();
								byte[] resBytes = parser.TrxTransferenciaPagoDirectoResponseToByte(reponse_);
								
								if(parser.getCodError().equals("000")) {
									
									messageBanred.setBodyBytesMessageResponse(resBytes);
									
								}else {
									
									messageBanred.setBodyBytesMessageResponse("909 SYSTEM MAL FUNCTION".getBytes());
									log.WriteLogMonitor("Error parsear Respuesta TrxTransferenciaPagoDirectoResponse to ByteArray " + parser.getDesError()
													, TypeMonitor.error, null);
								}
								
							}else {
								
								messageBanred.setBodyBytesMessageResponse("909 SYSTEM MAL FUNCTION".getBytes());
								log.WriteLogMonitor("Error parsear Respuesta Iso8583 to TrxTransferenciaPagoDirectoResponse" + parser.getDesError()
												, TypeMonitor.error, null);
							}
							
						}else {
							
							messageBanred.setBodyBytesMessageResponse("909 SYSTEM MAL FUNCTION".getBytes());
							log.WriteLogMonitor("Error parsear TrxTransferenciaPagoDirectoRequest to Iso8583 " + parser.getDesError()
											, TypeMonitor.error, null);
						}
						
					}else {
						
						messageBanred.setBodyBytesMessageResponse("909 SYSTEM MAL FUNCTION".getBytes());
						log.WriteLogMonitor("Error parsear StructBanredMessage to TrxTransferenciaPagoDirectoRequest " + parser.getDesError()
											, TypeMonitor.error, null);
					}
					
				} finally {
					/*Cambio el mensaje a mandar a la cola a Response*/
					messageBanred.setTypeMessage(TypeMessage.RS);
				}
				
				break;
			default:
				break;
			}
			
			
		} catch (Exception e) {
			
			log.WriteLogMonitor("Error modulo BridgePagoDirectoBanred::processTransactions ", TypeMonitor.error, e);
		}
		return messageBanred;
	}
}
