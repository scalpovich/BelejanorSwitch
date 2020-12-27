package com.belejanor.switcher.authorizations;

import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import com.belejanor.switcher.banred.pagodirecto.struct.TrxConsultaPagoDirectoRequest;
import com.belejanor.switcher.banred.pagodirecto.struct.TrxConsultaPagoDirectoResponse;
import com.belejanor.switcher.banred.pagodirecto.struct.TrxTransferenciaPagoDirectoRequest;
import com.belejanor.switcher.banred.pagodirecto.struct.TrxTransferenciaPagoDirectoResponse;
import com.belejanor.switcher.cscoreswitch.AdminConcurrentMap;
import com.belejanor.switcher.cscoreswitch.ContainerIsoQueue;
import com.belejanor.switcher.cscoreswitch.Iso8583;
import com.belejanor.switcher.cscoreswitch.TransactionConfiguration;
import com.belejanor.switcher.cscoreswitch.wIso8583;
import com.belejanor.switcher.logger.Logger;
import com.belejanor.switcher.logger.LoggerConfig.TypeMonitor;
import com.belejanor.switcher.memcached.MemoryGlobal;
import com.belejanor.switcher.parser.PagoDirectoParser;
import com.belejanor.switcher.queueadmin.Queue;
import com.belejanor.switcher.queueadmin.typeMessage;
import com.belejanor.switcher.structbanred.StructBanredMessage;
import com.belejanor.switcher.structbanred.TypeMessage;
import com.belejanor.switcher.utils.FormatUtils;
import com.belejanor.switcher.utils.GeneralUtils;
import com.belejanor.switcher.utils.StringUtils;

public class BanredPagoDirectoIsAut {

private Logger log;
	
	public BanredPagoDirectoIsAut() {
		log = new Logger();
	}
	
	public wIso8583 ProcessTransactionAutBanred(wIso8583 iso) {
		
		
		StructBanredMessage banredMsg = new StructBanredMessage();
		String trama = "TR01853008052168016800163+0000'!30812700000000000000000000000000000+00000000000000000+022CAMACHO CALVA FRANKLIN DAVID   02000036085701420787FRANKLIN CAMACHO                        00043669000436692016081500000000000000917661440";
		
		ContainerIsoQueue<Object> cont = null;
		
		try {
			
			banredMsg.setBodyBytesMessage(trama.getBytes());
			banredMsg.setBodyMessage(trama);
			banredMsg.setTypeMessage(TypeMessage.RQ);
			banredMsg.setSecuentialMessage("521680");
			banredMsg.setProccodeMessage("0163");
			
			cont = new ContainerIsoQueue<>(banredMsg , "127.0.0.1");
	    	cont.setIso(banredMsg);
	    	cont.setSecuencial(banredMsg.getSecuentialMessage());
			
	    	Queue queue = new Queue();
			queue.SendMessage(typeMessage.processor, cont, 1, 0);
	    	
			final Response res = new Response();
	    	final CountDownLatch semaphore = new CountDownLatch(1);
			Thread t = new Thread(new Runnable() {
				@Override
				public void run() {
					
					while (true) {
						synchronized(MemoryGlobal.concurrentIso) {
							if(MemoryGlobal.concurrentIso.containsKey(banredMsg.getSecuentialMessage())){
								res.setMsgBanredStruct((StructBanredMessage) MemoryGlobal.concurrentIso.get(banredMsg.getSecuentialMessage()));
								@SuppressWarnings("unused")
								AdminConcurrentMap map = new AdminConcurrentMap(res.getMsgBanredStruct().getSecuentialMessage());
								semaphore.countDown();
								break;
							}
						}
					}
				}
			});
			t.start();
        	
			if(!semaphore.await(30000, TimeUnit.MILLISECONDS))
				System.out.println("909TIEMPO EXPIRADO");
			
			iso.setISO_120_ExtendedData(res.getMsgBanredStruct().getBodyMessage());
			iso.setISO_039_ResponseCode("000");
			iso.setISO_039p_ResponseDetail("TRANSACCION EXITOSA");
			System.out.println("TRAMA RECIBIDA POR BANRED: " + res.getMsgBanredStruct().getBodyMessage());
			
			
		} catch (Exception e) {
			
			log.WriteLogMonitor("Error modulo BanredPagoDirectoIsAut::ProcessTransactionAutBanred ", 
				     TypeMonitor.error, e);
		}
		
		return iso;
	}
	
	public wIso8583 DebitForTransferPayDirectPayBanred(wIso8583 iso) {
		
		FitIsAut aut = null;
		try {
			
			if(iso.getISO_003_ProcessingCode().startsWith("31"))
				iso.setISO_003_ProcessingCode(iso.getISO_003_ProcessingCode().replace("31", "01"));
			
			aut = new FitIsAut();
			wIso8583 isoClone = (wIso8583) iso.clone();
			isoClone.setWsTransactionConfig((TransactionConfiguration)iso.getWsTransactionConfig().CloneObject());
			
			iso.getTickAut().reset();
			iso.getTickAut().start();
				isoClone = aut.ExecuteDebitCreditFit1(isoClone);
				if(iso.getTickAut().isStarted())
					iso.getTickAut().stop();
			if(isoClone.getISO_039_ResponseCode().equals("000")){
			
				iso.setISO_044_AddRespData(isoClone.getISO_044_AddRespData());
			}
			iso.setWsIso_LogStatus(isoClone.getWsIso_LogStatus());
			iso.setISO_039_ResponseCode(isoClone.getISO_039_ResponseCode());
			iso.setISO_039p_ResponseDetail(isoClone.getISO_039p_ResponseDetail());
			
		} catch (Exception e) {
			
			if(iso.getTickAut().isStarted())
				iso.getTickAut().stop();
			log.WriteLogMonitor("Error modulo BanredPagoDirectoIsAut::DebitForTransferPayDirectPayBanred ", 
				     TypeMonitor.error, e);
		}finally {
		
			iso.setWsTempAut((iso.getTickAut().getTime(TimeUnit.MILLISECONDS)/1000.0));
		}
		return iso;
	}
	
	/*TRANSFERENCIAS (AFECTACION) TARJETA CREDITO O CUENTAS AHORRO Y CORRIENTE*/
	public wIso8583 ProcessingTransactionPAYDirectPay(wIso8583 iso) {
		
		StructBanredMessage banredMsgRQ = null;
		StructBanredMessage banredMsgRS = null;
		Iso8583 iso8583 = null;
		PagoDirectoParser parser = null;
		TrxTransferenciaPagoDirectoRequest rq = null;
		TrxTransferenciaPagoDirectoResponse rs = null;
		ContainerIsoQueue<Object> cont = null;
		
		try {
			
			if(iso.getISO_003_ProcessingCode().startsWith("31"))
				iso.setISO_003_ProcessingCode(iso.getISO_003_ProcessingCode().replace("31", "01"));
			
			iso8583 = new Iso8583(iso);
			rq = new TrxTransferenciaPagoDirectoRequest();
			rq = rq.Iso8583ToTrxTransferenciaPagoDirectoRequest(iso8583);
			/*Si la operacion de parseo de ISO8583 a TrxConsultaPagoDirectoRequest fue correcta*/
			if(rq.getUser_request_defined_data() != null) {
				
				parser = new PagoDirectoParser();
				byte[] bytesMessage = parser.TrxTransferenciaPagoDirectoRequestToByte(rq);
				
				/*Si la operacion de parsero de TrxConsultaPagoDirectoRequest a ByteArray fue exitosa*/
				if(parser.getCodError().equals("000")) {
					
					banredMsgRQ = new StructBanredMessage();
					banredMsgRQ.setBodyBytesMessage(bytesMessage);
					banredMsgRQ.setBodyMessage(new String(bytesMessage));
					banredMsgRQ.setTypeMessage(TypeMessage.RQ);
					banredMsgRQ.setSecuentialMessage(iso.getISO_011_SysAuditNumber());
					banredMsgRQ.setProccodeMessage(rq.getTransaction_code_S9004().toString()
							                       .replace("+", StringUtils.Empty())
							                       .replace("-", StringUtils.Empty()));
					
					
					cont = new ContainerIsoQueue<>(banredMsgRQ , "127.0.0.1");
			    	cont.setIso(banredMsgRQ);
			    	cont.setSecuencial(banredMsgRQ.getSecuentialMessage());
					
			    	Queue queue = new Queue();
					queue.SendMessage(typeMessage.processor, cont, 1, 0);
			    	
					final Response res = new Response();
			    	final CountDownLatch semaphore = new CountDownLatch(1);
			    	
			    	iso.getTickAut().reset();
					iso.getTickAut().start();
			    	final String secuencialTrx = banredMsgRQ.getSecuentialMessage() + banredMsgRQ.getProccodeMessage();
					Thread t = new Thread(new Runnable() {
						@Override
						public void run() {
							
							while (true) {
								synchronized(MemoryGlobal.concurrentIso) {
									if(MemoryGlobal.concurrentIso.containsKey(secuencialTrx)){
										res.setMsgBanredStruct((StructBanredMessage) MemoryGlobal.concurrentIso.get(secuencialTrx));
										@SuppressWarnings("unused")
										AdminConcurrentMap map = new AdminConcurrentMap(res.getMsgBanredStruct().getSecuentialMessage());
										semaphore.countDown();
										break;
									}
								}
							}
						}
					});
					t.start();
		        	
					if(!semaphore.await(iso.getWsTransactionConfig().getProccodeTimeOutValue(), TimeUnit.MILLISECONDS)) {
						
						System.out.println("907PAGODRECTO_TIEMPO EXPIRADO ORDENANTE!!!");
						iso.setISO_039_ResponseCode("907");
						iso.setISO_039p_ResponseDetail("BANRED NO DISPONIBLE... TRANSACCION TRANSFERENCIA EXPIRADA");
						iso.setWsIso_LogStatus(9);
					}
					else {
						/*Procesamiento de Respuesta*/
						iso.setWsIso_LogStatus(2);
						banredMsgRS = new StructBanredMessage();
						banredMsgRS = res.getMsgBanredStruct();
						byte[] messageResponse = banredMsgRS.getBodyBytesMessage(); // o porbar con banredMsgRS.getBodyBytesMessageResponse()
						rs = new TrxTransferenciaPagoDirectoResponse();
						parser = new PagoDirectoParser();
						
						rs = parser.bytesToTrxTransferenciaPagoDirectoResponse(messageResponse);
						if(parser.getCodError().equals("000")) {
							
							/*Homologo el codigo de error*/
							
							iso.setISO_039_ResponseCode(Arrays.asList(parser.HomologaErrorOnTwoToIso8583
														(rs.getResult_code_9004())).get(0));
							iso.setISO_039p_ResponseDetail(Arrays.asList(parser.HomologaErrorOnTwoToIso8583
														(rs.getResult_code_9004())).get(1));
							iso.setISO_104_TranDescription(rs.getResult_code_9004());
								
						}else {
						
							iso.setISO_039_ResponseCode(parser.getCodError());
							iso.setISO_039p_ResponseDetail(parser.getDesError() + ", PARSE(ByteArray[] to TrxTransferenciaPagoDirectoRequest )");
						}
					}
					if(iso.getTickAut().isStarted())
						iso.getTickAut().stop();
					
				}else {
					
					iso.setISO_039_ResponseCode(parser.getCodError());
					iso.setISO_039p_ResponseDetail(parser.getDesError() + ", PARSE(TrxTransferenciaPagoDirectoRequest To ByteArray[])");
				}
				
			}else {
				
				iso.setISO_039_ResponseCode("908");
				iso.setISO_039p_ResponseDetail("ERROR AL CONVERTIR TRAMA ISO8583 A ON2 (TrxTransferenciaPagoDirectoRequest)");
			}
			
		} catch (Exception e) {
			
			if(iso.getTickAut().isStarted())
				iso.getTickAut().stop();
			iso.setISO_039_ResponseCode("909");
			iso.setISO_039p_ResponseDetail("ERROR EN PROCESOS " + GeneralUtils.ExceptionToString(null, e, false));
			log.WriteLogMonitor("Error modulo BanredPagoDirectoIsAut::ProcessingTransactionPAYDirectPay ", 
				     TypeMonitor.error, e);
		}finally {
			
			iso.setWsTempAut((iso.getTickAut().getTime(TimeUnit.MILLISECONDS)/1000.0));
		}
		return iso;
	}
	
	/*CONSULTAS TARJETA CREDITO O CUENTAS AHORRO Y CORRIENTE*/
	public wIso8583 ProcessingTransactionQueryDirectPay(wIso8583 iso) {
		
		StructBanredMessage banredMsgRQ = null;
		StructBanredMessage banredMsgRS = null;
		Iso8583 iso8583 = null;
		PagoDirectoParser parser = null;
		TrxConsultaPagoDirectoRequest rq = null;
		TrxConsultaPagoDirectoResponse rs = null;
		ContainerIsoQueue<Object> cont = null;
		
		try {
			
			if(iso.getISO_003_ProcessingCode().startsWith("01"))
				iso.setISO_003_ProcessingCode(iso.getISO_003_ProcessingCode().replace("01", "31"));
			
			iso8583 = new Iso8583(iso);
			rq = new TrxConsultaPagoDirectoRequest();
			rq = rq.Iso8583ToTrxConsultaPagoDirectoRequest(iso8583);
			/*Si la operacion de parseo de ISO8583 a TrxConsultaPagoDirectoRequest fue correcta*/
			if(rq.getUser_request_defined_data() != null) {
				
				parser = new PagoDirectoParser();
				byte[] bytesMessage = parser.TrxConsultaPagoDirectoRequestToByte(rq);
				
				/*Si la operacion de parsero de TrxConsultaPagoDirectoRequest a ByteArray fue exitosa*/
				if(parser.getCodError().equals("000")) {
					
					banredMsgRQ = new StructBanredMessage();
					banredMsgRQ.setBodyBytesMessage(bytesMessage);
					banredMsgRQ.setBodyMessage(new String(bytesMessage));
					banredMsgRQ.setTypeMessage(TypeMessage.RQ);
					banredMsgRQ.setSecuentialMessage(iso.getISO_011_SysAuditNumber());
					banredMsgRQ.setProccodeMessage(rq.getTransaction_code_S9004().toString()
							                       .replace("+", StringUtils.Empty())
							                       .replace("-", StringUtils.Empty()));
					
					
					cont = new ContainerIsoQueue<>(banredMsgRQ , "127.0.0.1");
			    	cont.setIso(banredMsgRQ);
			    	cont.setSecuencial(banredMsgRQ.getSecuentialMessage());
					
			    	Queue queue = new Queue();
					queue.SendMessage(typeMessage.processor, cont, 1, 0);
			    	
					final Response res = new Response();
			    	final CountDownLatch semaphore = new CountDownLatch(1);
			    	
			    	iso.getTickAut().reset();
					iso.getTickAut().start();
			    	final String secuencialTrx = banredMsgRQ.getSecuentialMessage() + banredMsgRQ.getProccodeMessage();
					Thread t = new Thread(new Runnable() {
						@Override
						public void run() {
							
							while (true) {
								synchronized(MemoryGlobal.concurrentIso) {
									if(MemoryGlobal.concurrentIso.containsKey(secuencialTrx)){
										res.setMsgBanredStruct((StructBanredMessage) MemoryGlobal.concurrentIso.get(secuencialTrx));
										@SuppressWarnings("unused")
										AdminConcurrentMap map = new AdminConcurrentMap(res.getMsgBanredStruct().getSecuentialMessage());
										semaphore.countDown();
										break;
									}
								}
							}
						}
					});
					t.start();
		        	
					if(!semaphore.await(iso.getWsTransactionConfig().getProccodeTimeOutValue(), TimeUnit.MILLISECONDS)) {
						
						System.out.println("907PAGODRECTO_TIEMPO EXPIRADO ORDENANTE!!!");
						iso.setISO_039_ResponseCode("907");
						iso.setISO_039p_ResponseDetail("BANRED NO DISPONIBLE... TRANSACCION CONSULTA EXPIRADA");
						iso.setWsIso_LogStatus(9);
					}
					else {
						/*Procesamiento de Respuesta*/
						iso.setWsIso_LogStatus(2);
						banredMsgRS = new StructBanredMessage();
						banredMsgRS = res.getMsgBanredStruct();
						byte[] messageResponse = banredMsgRS.getBodyBytesMessage(); // o porbar con banredMsgRS.getBodyBytesMessageResponse()
						rs = new TrxConsultaPagoDirectoResponse();
						parser = new PagoDirectoParser();
						
						rs = parser.bytesToTrxConsultaPagoDirectoResponse(messageResponse);
						if(parser.getCodError().equals("000")) {
							
							/*Homologo el codigo de error*/
							
							iso.setISO_039_ResponseCode(Arrays.asList(parser.HomologaErrorOnTwoToIso8583
														(rs.getResultCode_9004())).get(0));
							iso.setISO_039p_ResponseDetail(Arrays.asList(parser.HomologaErrorOnTwoToIso8583
									(rs.getResultCode_9004())).get(1));
							iso.setISO_104_TranDescription(rs.getResultCode_9004());
							
							/*Si es consulta de tarjeta de Credito*/
							if(iso.getISO_003_ProcessingCode().substring(4, 6).equals("30")) {
								
								/*Pago Minimo*/
								iso.setISO_006_BillAmount(Double.parseDouble(rs.getUser_repply_defined_data()
														 .getReppdr_min_payment_9008())/100);
								/*Pago Total*/
								iso.setISO_008_BillFeeAmount(Double.parseDouble(rs.getUser_repply_defined_data()
														 .getReppdr_tot_payment_9008())/100);
								/*Fecha limite de Pago*/
								iso.setISO_013_LocalDate(FormatUtils.StringToDate(rs.getUser_repply_defined_data()
														 .getReppdr_limit_date_9008(), "yyyyMMdd"));
							}
								
						}else {
						
							iso.setISO_039_ResponseCode(parser.getCodError());
							iso.setISO_039p_ResponseDetail(parser.getDesError() + ", PARSE(ByteArray[] to TrxConsultaPagoDirectoRequest )");
						}
					}
					if(iso.getTickAut().isStarted())
						iso.getTickAut().stop();
					
				}else {
					
					iso.setISO_039_ResponseCode(parser.getCodError());
					iso.setISO_039p_ResponseDetail(parser.getDesError() + ", PARSE(TrxConsultaPagoDirectoRequest To ByteArray[])");
				}
				
			}else {
				
				iso.setISO_039_ResponseCode("908");
				iso.setISO_039p_ResponseDetail("ERROR AL CONVERTIR TRAMA ISO8583 A ON2 (TrxConsultaPagoDirectoRequest)");
			}
			
		} catch (Exception e) {
			
			if(iso.getTickAut().isStarted())
				iso.getTickAut().stop();
			iso.setISO_039_ResponseCode("909");
			iso.setISO_039p_ResponseDetail("ERROR EN PROCESOS " + GeneralUtils.ExceptionToString(null, e, false));
			log.WriteLogMonitor("Error modulo BanredPagoDirectoIsAut::ProcessingTransactionQueryDirectPay ", 
				     TypeMonitor.error, e);
		}finally {
			
			iso.setWsTempAut((iso.getTickAut().getTime(TimeUnit.MILLISECONDS)/1000.0));
			//Por pruebas OJO BORRAR
			//iso.setISO_039_ResponseCode("000");
		}
		return iso;
	}
	
	
}
class Response{
	
	private StructBanredMessage msgBanredStruct;

	public StructBanredMessage getMsgBanredStruct() {
		return msgBanredStruct;
	}

	public void setMsgBanredStruct(StructBanredMessage msgBanredStruct) {
		this.msgBanredStruct = msgBanredStruct;
	}
	
	
}
