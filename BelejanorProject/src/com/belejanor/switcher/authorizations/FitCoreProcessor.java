package com.belejanor.switcher.authorizations;

import java.util.Iterator;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import com.belejanor.switcher.cscoreswitch.wIso8583;
import com.belejanor.switcher.logger.Logger;
import com.belejanor.switcher.logger.LoggerConfig.TypeLog;
import com.belejanor.switcher.logger.LoggerConfig.TypeMonitor;
import com.belejanor.switcher.logger.LoggerConfig.TypeWriteLog;
import com.belejanor.switcher.memcached.ActionCodeTable;
import com.belejanor.switcher.memcached.MemoryGlobal;
import com.belejanor.switcher.parser.FitParser;
import com.belejanor.switcher.storeandforward.AdminProcessStoreAndForward;
import com.belejanor.switcher.utils.GeneralUtils;
import com.belejanor.switcher.utils.StringUtils;
import com.fitbank.dto.management.Detail;
import com.fitbank.dto.management.Field;
import com.fitbank.uci.client.UCIClient;

public class FitCoreProcessor{
	
	private wIso8583 iso;
	private Logger log;

	public wIso8583 getIso() {
		return iso;
	}
	
	public void setIso(wIso8583 iso) {
		this.iso = iso;
	}

	public FitCoreProcessor(){
		this.log = new Logger();
	}
	public FitCoreProcessor(wIso8583 iso){
		this();
		this.iso = iso;
	}
	
    public  Detail ProcessingTransactionFitCore(){
		
		Detail ResponseDetail = null;
		
		Detail detail = new Detail();
		try {
			
			FitParser parser = new FitParser();
			
			if(this.iso.getISO_000_Message_Type().startsWith("14"))
				detail = parser.parseReversoFitBank(this.iso);
			else
				detail = parser.parseFitbankTransaction(this.iso);
			
			if(parser.isFlagError()){
				
				if(detail != null){
					
					final Detail detailUci = detail;
					System.out.println("Se imprime -----> " +  detailUci.toXml()); //ojo borrar
					log.WriteLog(detail, TypeLog.debug, TypeWriteLog.file);
					final ContainerUCI container = new ContainerUCI();
					container.setDet(detailUci);
					container.setIso(iso);
					
					final CountDownLatch semaphore = new CountDownLatch(1);
					iso.getTickAut().reset();
					iso.getTickAut().start();
					Thread tUci = new Thread(new Runnable() {
						
						@Override
						public void run() {
							
							try {
								
								container.setDet(SendUciTransaction(container.getDet()));
								semaphore.countDown();
								if(iso.getTickAut().isStarted())
									iso.getTickAut().stop();
								
							} catch (InterruptedException e) {
								
								if(iso.getTickAut().isStarted())
									iso.getTickAut().stop();
								iso.setISO_039_ResponseCode("909");
								iso.setISO_039p_ResponseDetail("ERROR EN PROCESOS, " + GeneralUtils.ExceptionToString(null, e, false));
								iso.setISO_104_TranDescription("ERROR AL RECUPERAR RESPUESTA FIT1, RUNNABLE INTERRUPTED EXCEPTION");
								semaphore.countDown();
							}
						}
					}); 
					tUci.start();
					
					boolean flagTimeout = false;
					if(!semaphore.await(this.iso.getWsTransactionConfig().
							 getProccodeTimeOutValue(), TimeUnit.MILLISECONDS)){
						
						flagTimeout = true;
						this.iso.setISO_039_ResponseCode("907");
						this.iso.setISO_039p_ResponseDetail("TRANSACCION EXPIRADA, TIMEOUT UCI");
						this.iso.setISO_104_TranDescription("HA EXPIRADO EL TIEMPO DE ESERA DE FITSWITCH PARA ESTA OPERACION");
						this.iso.setWsIso_LogStatus(9);
						this.iso.setWsISO_FlagStoreForward(true);
						this.iso.setISO_044_AddRespData(StringUtils.IsNullOrEmpty(detail.getMessageId())?"0000"
								               :detail.getMessageId());
					}
					
					ResponseDetail = container.getDet();
					
				    if(!flagTimeout){ 
				    	
						if(ResponseDetail.getResponse() != null){
							
							if(ResponseDetail.getResponse().getCode().trim().equals("0")){
								
								this.iso.setISO_039_ResponseCode("000");
								this.iso.setISO_039p_ResponseDetail("TRANSACCION EXITOSA");
								this.iso.setISO_104_TranDescription("LA TRANSACCION HA SIDO COMPLETADA SATISFACTORIAMENTE");
								
								Field f = asStream(ResponseDetail.getFields().iterator(), true)
										 .filter(x -> x.getName().equalsIgnoreCase("NUMEROMENSAJE_ORIGINAL"))
										 .findFirst().orElseGet(()-> null);
								
								if(f != null){
									
									this.iso.setISO_044_AddRespData(StringUtils.isNullOrEmpty(f.getValue().toString()) 
											       ? ResponseDetail.getMessageId() : f.getValue().toString());
								}	
								else
									this.iso.setISO_044_AddRespData(ResponseDetail.getMessageId());
							}
							else{
							
								
								Thread t = new Thread(new AdminProcessStoreAndForward().
										EvaluateStoreAndForwardCodesThread(iso, ResponseDetail.getResponse().getCode().trim()));
								t.start();
								
								String [] errorParse =  ParseErrorsCodeIsoToFit(ResponseDetail.getResponse().getCode().trim());
								this.iso.setISO_039_ResponseCode(errorParse[0]);
								this.iso.setISO_039p_ResponseDetail(ResponseDetail.getResponse().getCode() + " - " + 
																	ResponseDetail.getResponse().getUserMessage().toUpperCase());
								this.iso.setISO_104_TranDescription(errorParse[1]);
								this.iso.setISO_044_AddRespData(ResponseDetail.getMessageId());
								
								//OJO POR SIMULACION
								//this.iso.setISO_039_ResponseCode("000");
								//this.iso.setISO_039p_ResponseDetail("TRANSACCION EXITOSA");
							
							}
							
							this.iso.setWsIso_LogStatus(2);	
							log.WriteLog(ResponseDetail, TypeLog.debug, TypeWriteLog.file);
						}
						else{
							
							this.iso.setISO_039_ResponseCode("909");
							this.iso.setISO_039p_ResponseDetail("SE HA RECIBIDO UNA RESPUESTA NULA POR PARTE DE CORE FITBANK");
						}
				    }else{
				    	/*Imprime Detail cuando hubo timeout*/
				    	if(ResponseDetail != null){
				    		log.WriteLog(ResponseDetail, TypeLog.debug, TypeWriteLog.file);
				    	}else{
				    		log.WriteLog("No se recibe respuesta FitBank, Secuencial " + iso.getISO_011_SysAuditNumber(), TypeLog.debug, TypeWriteLog.file);
				    	}
				    }
				}
			}
			else{
				
				if(this.iso.getISO_000_Message_Type().startsWith("14"))
					this.iso.setISO_039_ResponseCode(parser.getCodErrorIso());
				else
					this.iso.setISO_039_ResponseCode("908");
				this.iso.setISO_039p_ResponseDetail(parser.getErrorParser());
			}
			
		}
		catch (Exception e) {
			
			if(this.iso.getTickAut().isStarted())
				this.iso.getTickAut().stop();
			this.iso.setISO_039_ResponseCode("909");
			this.iso.setISO_039p_ResponseDetail(GeneralUtils.ExceptionToString("ERROR MODULO AUTORIZACION ", e, false));
			log.WriteLogMonitor("Error modulo FitCoreProcessor::ProcessingTransactionFitCore ", TypeMonitor.error, e);
			
		}finally {
			this.iso.setWsTempAut((this.iso.getTickAut().getTime(TimeUnit.MILLISECONDS)/1000.0));
		}
		
		return ResponseDetail;
		
	}
	
	private String[] ParseErrorsCodeIsoToFit(String errorFit){
		String[] data = new String[]{ "308","" };
		try {
			ActionCodeTable codTable = new ActionCodeTable();
			codTable = codTable.getCodErrorFromFitCodeList(errorFit);
			if(codTable != null){
				data[0] = codTable.getAct_codIso();
				data[1] = codTable.getAct_desIso();
			}
		} catch (Exception e) {
			log.WriteLogMonitor("Error modulo FitCoreProcessor::ParseErrorsCodeIsoToFit ", TypeMonitor.error, e);
			e.printStackTrace();
		}
		return data;
	}
	private Detail SendUciTransaction(Detail det) throws InterruptedException{
		
		//Thread.sleep(40000);
		Detail detResponse = UCIClient.send(det, MemoryGlobal.IpUci, MemoryGlobal.portUci, MemoryGlobal.timeOutUci);
		return detResponse;
		
	}
	protected static <T> Stream<T> asStream(Iterator<T> sourceIterator) {
        return asStream(sourceIterator, false);
    }

	protected static <T> Stream<T> asStream(Iterator<T> sourceIterator, boolean parallel) {
        Iterable<T> iterable = () -> sourceIterator;
        return StreamSupport.stream(iterable.spliterator(), parallel);
    }
	
}
class ContainerUCI{
	
	private Detail det;
	private wIso8583 iso;

	public Detail getDet() {
		return det;
	}

	public void setDet(Detail det) {
		this.det = det;
	}

	public wIso8583 getIso() {
		return iso;
	}

	public void setIso(wIso8583 iso) {
		this.iso = iso;
	}
	
	
}
