package com.belejanor.switcher.asextreme.implementations;

import java.util.List;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.xml.ws.WebServiceContext;
import org.apache.cxf.phase.PhaseInterceptorChain;
import org.apache.cxf.transport.http.AbstractHTTPDestination;

import com.belejanor.switcher.acquirers.AlexSoftIsAcq;
import com.belejanor.switcher.asextreme.ExtremeReply;
import com.belejanor.switcher.asextreme.ExtremeRequest;
import com.belejanor.switcher.cscoreswitch.Iso8583;
import com.belejanor.switcher.cscoreswitch.csProcess;
import com.belejanor.switcher.cscoreswitch.wIso8583;
import com.belejanor.switcher.logger.Logger;
import com.belejanor.switcher.logger.LoggerConfig.TypeLog;
import com.belejanor.switcher.logger.LoggerConfig.TypeMonitor;
import com.belejanor.switcher.logger.LoggerConfig.TypeWriteLog;
import com.belejanor.switcher.parser.AlexParser;
import com.belejanor.switcher.sqlservices.IsoRetrievalTransaction;
import com.belejanor.switcher.sqlservices.IsoSqlMaintenance;
import com.belejanor.switcher.utils.GeneralUtils;
import com.belejanor.switcher.utils.SerializationObject;
import com.belejanor.switcher.utils.StringUtils;
import com.belejanor.switcher.validatefinantial.RulesValidation;

@Path("/AuthorizationsFinancoop")
@Consumes(MediaType.APPLICATION_XML)
@Produces(MediaType.APPLICATION_XML)
public class ServiceFitBankFinancoop {
	
	@SuppressWarnings("static-access")
	@POST
	@Consumes(MediaType.APPLICATION_XML)
	@Produces(MediaType.APPLICATION_XML)
	public Object TransactionRequest(ExtremeRequest request) {
		
		Logger log = new Logger();
		AlexParser parser = null;
		AlexSoftIsAcq alexAcq = null;
		IsoSqlMaintenance sql = null;
		IsoRetrievalTransaction sqlQuery = null;
		List<Iso8583> isoList = null;
		Iso8583 isoConsul = null;
		csProcess processor = null;
		SerializationObject serialization = null;
		try {
			
			parser = new AlexParser();
			alexAcq = new AlexSoftIsAcq();
			
			if(request.getHeader().getValueTag("TrnCode").equals("130017")) {
				
				isoList = parser.ExtremeMessageToIsoList(request);
				if(parser.getCodError().equals("000")) {
				
					final List<Iso8583> IsoLst1 = isoList;
					
					//Validacion de Fondos en la cuenta para transferencias
					Iso8583 isoValidateSaldo = (Iso8583) isoList.get(0).clone();
					wIso8583 wiso = new wIso8583(isoValidateSaldo, ObtainIpClient());
					//Validaciones Trx
					RulesValidation validator = new RulesValidation();
					wiso = validator.ValidateProcessor(wiso);
					
					if(!wiso.getISO_039_ResponseCode().equals("000")){
						
						/*Respuesta Asincrona inmediata a Financoop*/
						parser.setCodError("96");
						parser.setDesError(wiso.getISO_039p_ResponseDetail());
						return alexAcq.SetDefaultResponseError(request, parser);
					}
					
					wiso.setISO_102_AccountID_1(isoList.get(0).getISO_102_AccountID_1());
					sqlQuery = new IsoRetrievalTransaction();
					wiso = sqlQuery.RetrieveSavingsAccountFit1(wiso);
					if(wiso.getISO_039_ResponseCode().equals("000")) {
						
						double saldo = Double.parseDouble(wiso.getISO_121_ExtendedData());
						if(parser.getAcumuladorValores() > saldo) {
							
							parser.setCodError("51");
							parser.setDesError("LA CUENTA ORIGEN NO POSEE FONDOS DISPONIBLES");
							return alexAcq.SetDefaultResponseError(request, parser);
						}
						
					}else {
						
						/*Respuesta Asincrona inmediata a Financoop*/
						parser.setCodError("96");
						parser.setDesError("NO SE PUDO VALIDAR EL SADO DE LA CUENTA ORIGEN..." + wiso.getISO_039p_ResponseDetail());
						return alexAcq.SetDefaultResponseError(request, parser);
					}
					
					sql = new IsoSqlMaintenance();
					sql.insertMassiveFinacoopIII(isoList);
					if(sql.getCodeSqlError() == 0) {
					
						final AlexSoftIsAcq acq = new AlexSoftIsAcq();
						Thread t = new Thread(() -> {
						    
							/*Llamada asyncrona a procesamiento de transferencias*/
							acq.ProcessingMasiveTransfer(IsoLst1);
						});
						t.start();
						
						/*Respuesta Asincrona inmediata a Financoop*/
						parser.setCodError("000");
						parser.setDesError("PROCESO EJECUTADO EXITOSAMENTE");
						
					}else if (sql.getCodeSqlError() == 1) {
						
						/*Respuesta Asincrona inmediata a Financoop*/
						parser.setCodError("96");
						parser.setDesError("EXISTEN ELEMENTOS NO UNICOS " + sql.getCodeSqlError() + " - " 
											+ sql.getDescriptionSqlError());
						
					}else {
						
						parser.setCodError("96");
						parser.setDesError("ERROR EN BDD: " + sql.getCodeSqlError() + " - " 
											+ sql.getDescriptionSqlError());
					}
					
					return alexAcq.SetDefaultResponseError(request, parser);
					
				}else {
					
					return alexAcq.SetDefaultResponseError(request, parser);
				}
				
			}else if(request.getHeader().getValueTag("TrnCode").equals("130021")) {
				
				isoConsul = new Iso8583();
				isoConsul = parser.ExtremeMessageToIsoHeader(request);
				
				serialization = new SerializationObject();
				isoConsul.setISO_114_ExtendedData(serialization.ObjectToString(request, ExtremeRequest.class));
				log.WriteLog(isoConsul.getISO_114_ExtendedData(), TypeLog.alexsoft, TypeWriteLog.file);
				if(isoConsul.getISO_039_ResponseCode().equals("000")) {
					
					processor = new csProcess();
					isoConsul = processor.ProcessTransactionMain(isoConsul, ObtainIpClient());
					
					if(isoConsul.getISO_039_ResponseCode().equals("000")) {
						
						serialization = new SerializationObject();
						ExtremeReply responseCon = (ExtremeReply) serialization.StringToObject(isoConsul.getISO_115_ExtendedData(), 
								ExtremeReply.class);
						log.WriteLog(isoConsul.getISO_115_ExtendedData(), TypeLog.alexsoft, TypeWriteLog.file);
						return responseCon;
						
					}else {
						
						parser.setCodError(isoConsul.getISO_039_ResponseCode());
						parser.setDesError(isoConsul.getISO_039p_ResponseDetail());
						return alexAcq.SetDefaultResponseError(request, parser);
					}
					
				}else {
					
					parser.setCodError(isoConsul.getISO_039_ResponseCode());
					parser.setDesError(isoConsul.getISO_039p_ResponseDetail());
					return alexAcq.SetDefaultResponseError(request, parser);
				}
				
			}else {
				
				parser.setCodError("96");
				parser.setDesError("TRANSACCION TRNCODE INVALIDA");
				return alexAcq.SetDefaultResponseError(request, parser);
			}
			
		} catch (Exception e) {
		
			parser.setCodError("999");
			parser.setDesError(GeneralUtils.ExceptionToString("ERROR EN PROCESOS.", e, false));
			log.WriteLogMonitor("Error modulo ServiceFitBankFinancoop::TransactionRequest ", TypeMonitor.error, e);
			GenericError genError = new GenericError("999",GeneralUtils.ExceptionToString("ERROR EN PROCESOS.", e, false));
			log.WriteOptimizeLog(genError, TypeLog.alexsoft, GenericError.class, true);
			return genError;
			
		}finally {
			
			if(request.getHeader().getValueTag("TrnCode").equals("130017")) {
				final List<Iso8583> isoListTh = isoList;
				final AlexParser parseTh = parser;
				final ExtremeRequest rqTh = request;
				Thread tt = new Thread(() -> {
					/*Llamada transaccion Iso8583 de registro*/
					RegisterTrxTransfer(isoListTh,parseTh,rqTh);
				});
				tt.start();
			}
		}
		
	}
	
	@Resource  
	WebServiceContext wsContext; 
	private String ObtainIpClient() {
		try {
			
			org.apache.cxf.message.Message message = PhaseInterceptorChain.getCurrentMessage();
			HttpServletRequest request = (HttpServletRequest)message.get(AbstractHTTPDestination.HTTP_REQUEST);
			return request.getRemoteAddr();
			
		} catch (Exception e) {
			return "undefined-error";
		}
		
	}
	
	private void RegisterTrxTransfer(List<Iso8583> isoList, AlexParser parser, ExtremeRequest request) {
		
		Logger log = null;
		Iso8583 iso = new Iso8583();
		csProcess procesor = new csProcess();
		String xml = StringUtils.Empty();
		
		try {

			iso.setISO_000_Message_Type("1200");
			iso.setISO_003_ProcessingCode("911011");
			iso.setISO_018_MerchantType("0003");
			iso.setISO_024_NetworkId("555541");
			iso.setISO_011_SysAuditNumber(GeneralUtils.GetSecuencialNumeric(11));
			iso.setISO_023_CardSeq(isoList == null ? "-1":String.valueOf(isoList.size()));
			iso.setISO_004_AmountTransaction(parser.getCodError().equals("000")?parser.getAcumuladorValores():0);
			iso.setISO_102_AccountID_1(parser.getCodError().equals("000")?isoList.get(0).getISO_102_AccountID_1()
					:StringUtils.Empty());
			iso.setISO_037_RetrievalReferenceNumber(parser.getCodError().equals("000")? isoList.get(0).getISO_019_AcqCountryCode()
					:StringUtils.Empty());
			iso.setISO_022_PosEntryMode(parser.getCodError().equals("000") ? isoList.get(0).getISO_037_RetrievalReferenceNumber()
					:StringUtils.Empty());
			iso.setISO_120_ExtendedData(parser.getCodError() + "|" + parser.getDesError());
			iso.setISO_002_PAN(parser.getCodError().equals("000") ? isoList.get(0).getISO_002_PAN()
					:"N/D");
			
			if(request != null) {
				
				try {
					
					xml = SerializationObject.ObjectToString(request, ExtremeRequest.class);
					iso.setISO_114_ExtendedData(xml);
					log = new Logger();
					log.WriteLog(xml, TypeLog.alexsoft, TypeWriteLog.file);
					
				} catch (Exception e) {
					
					log = new Logger();
					log.WriteLogMonitor("Error modulo ServiceFitBankFinancoop::RegisterTrxTransfer[Serialization] ", TypeMonitor.error, e);
				}
			}
			
			iso = procesor.ProcessTransactionMain(iso, "127.0.0.1");
			
		} catch (Exception e) {
			
			log = new Logger();
			log.WriteLogMonitor("Error modulo ServiceFitBankFinancoop::RegisterTrxTransfer ", TypeMonitor.error, e);
		}
	}
}
