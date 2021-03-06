package com.belejanor.switcher.cscoreswitch;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import com.belejanor.switcher.acquirers.AlexSoftIsAcq;
import com.belejanor.switcher.acquirers.CardProcessorIsAcq;
import com.belejanor.switcher.asextreme.ExtremeRequest;
import com.belejanor.switcher.bridges.BridgePagoDirectoBanred;
import com.belejanor.switcher.bridges.BridgeSPI_BCE;
import com.belejanor.switcher.bridges.BridgeVC_BCE;
import com.belejanor.switcher.bridges.CredencialBridge;
import com.belejanor.switcher.credencial.BalanceCredencialRequest;
import com.belejanor.switcher.credencial.BalanceCredencialResponse;
import com.belejanor.switcher.credencial.DebitCredencialRequest;
import com.belejanor.switcher.credencial.DebitCredencialResponse;
import com.belejanor.switcher.credencial.MovementsCredencialRequest;
import com.belejanor.switcher.credencial.MovementsCredencialResponse;
import com.belejanor.switcher.credencial.RevertCredencialRequest;
import com.belejanor.switcher.credencial.RevertCredencialResponse;
import com.belejanor.switcher.memcached.MemoryGlobal;
import com.belejanor.switcher.queueadmin.Queue;
import com.belejanor.switcher.queueadmin.typeMessage;
import com.belejanor.switcher.storeandforward.AdminProcessStoreAndForward;
import com.belejanor.switcher.structbanred.StructBanredMessage;
import com.belejanor.switcher.structbanred.TypeMessage;
import com.belejanor.switcher.cscoreswitch.Iso8583;
import urn.iso.std.iso20022.tech.xsd.pacs_002_001_05.DocumentRespuesta;
import urn.iso.std.iso20022.tech.xsd.pacs_003_001_04.DocumentRetiro;
import urn.iso.std.iso20022.tech.xsd.pacs_004_001_04.DocumentReverso;
import urn.iso.std.iso20022.tech.xsd.pacs_008_001_04.DocumentDeposito;
import urn.iso.std.iso20022.tech.xsd.pacs_008_001_04.DocumentTransferencia;


public class CallersAsync extends Thread {

	private ContainerIsoQueue<?> cont;
	private Message message;
	
	public CallersAsync(Message message){
		
		this.message = message;
		this.cont = new ContainerIsoQueue<>();
	}
	
	public void executeTrx(){
		
		try {
				
			ObjectMessage objectMessage = (ObjectMessage) message;
			cont = (ContainerIsoQueue<?>) objectMessage.getObject();
			
			  if(cont.getIso().getClass().equals(Iso8583.class)){
				
				Iso8583 iso = (Iso8583)cont.getIso();
				ExecutorService pool = Executors.newFixedThreadPool(30);
				Callable<Iso8583> exec = new csProcess(iso, cont.getIP());
				Future<Iso8583> futureIso = pool.submit(exec);
				Iso8583 isoRes = futureIso.get();
				MemoryGlobal.concurrentIso.put(isoRes.
						     getISO_011_SysAuditNumber(), isoRes);
				pool.shutdown();
				System.out.println("Antes: " + MemoryGlobal.concurrentIso.size());
				
			}else if(cont.getIso().getClass().equals(wIso8583.class)){
				
				//STORE AND FORWARD
				wIso8583 iso = (wIso8583)cont.getIso();
				AdminProcessStoreAndForward sf = new AdminProcessStoreAndForward();
				Thread t = new Thread(sf.ProccessStoreAndForwardThreading(iso));
				t.start();
				
			}else if(cont.getIso() instanceof String){
				
				String msg = (String)cont.getIso();
			    CredencialBridge<String> coon = new CredencialBridge<String>();
			    String response = coon.QueryBalanceTCWhithAndWithoutMovements(msg);
			    String secuencial =  response.substring(35, 49) + "_" + response.substring(29,35);
			    MemoryGlobal.concurrentIso.put(secuencial, response);
			   
			}else if (cont.getIso().getClass().equals(DebitCredencialRequest.class)) {
				
				 Object res = putResponseClass(new DebitCredencialResponse(),cont);
				 DebitCredencialResponse obj = (DebitCredencialResponse) res;
				 MemoryGlobal.concurrentIso.put(obj.getIdentificador(), obj);
				 
			}else if (cont.getIso().getClass().equals(BalanceCredencialRequest.class)) {
				
				 Object res = putResponseClass(new BalanceCredencialResponse(),cont);
				 BalanceCredencialResponse obj = (BalanceCredencialResponse) res;
				 MemoryGlobal.concurrentIso.put(obj.getIdentificador(), obj);
				 
				 
			}else if (cont.getIso().getClass().equals(RevertCredencialRequest.class)) {
				
				 Object res = putResponseClass(new RevertCredencialResponse(),cont);
				 RevertCredencialResponse obj = (RevertCredencialResponse) res;
				 MemoryGlobal.concurrentIso.put(obj.getIdentificador(), obj);
				 
			}else if (cont.getIso().getClass().equals(MovementsCredencialRequest.class)) {
				
				 Object res = putResponseClass(new MovementsCredencialResponse(), cont);
				 MovementsCredencialResponse obj = (MovementsCredencialResponse) res;
				 MemoryGlobal.concurrentIso.put(obj.getIdentificador(), obj);
			}else if (cont.getIso().getClass().equals(ExtremeRequest.class)) {
				
				ExtremeRequest req = (ExtremeRequest) cont.getIso();
				AlexSoftIsAcq procAlex = new AlexSoftIsAcq(cont.getIP());
				String response = procAlex.ProcessTransactionAlexSoft(req);
				MemoryGlobal.concurrentIso.put(req.getHeader().getValueTag("TraceId") + "_" + 
				req.getHeader().getValueTag("TerminalSeq"), response);
				
			}else if (cont.getIso().getClass().equals(DocumentDeposito.class)) {
				
				DocumentDeposito documentoDeposito = (DocumentDeposito) cont.getIso();
				DocumentRespuesta documentorespuesta = null;
				BridgeVC_BCE bridge = new BridgeVC_BCE();
				documentorespuesta = bridge.ProcesaDepositoVC_BCE(documentoDeposito, cont.getIP());
				ContainerIsoQueue<DocumentRespuesta> strcuRes = new 
						ContainerIsoQueue<DocumentRespuesta>(documentorespuesta, "127.0.0.1");
				strcuRes.setSecuencial(bridge.getSecuencialTrx());
				MemoryGlobal.concurrentIso.put(strcuRes.getSecuencial()
					        , strcuRes);	
				
			}else if (cont.getIso().getClass().equals(DocumentRetiro.class)) {
				
				DocumentRetiro documentRetiro = (DocumentRetiro) cont.getIso();
				DocumentRespuesta documentorespuesta = null;
				BridgeVC_BCE bridge = new BridgeVC_BCE();
				documentorespuesta = bridge.ProcesaRetiroVC_BCE(documentRetiro, cont.getIP());
				ContainerIsoQueue<DocumentRespuesta> strcuRes = new 
						ContainerIsoQueue<DocumentRespuesta>(documentorespuesta, "127.0.0.1");
				strcuRes.setSecuencial(bridge.getSecuencialTrx());
				MemoryGlobal.concurrentIso.put(strcuRes.getSecuencial()
				        , strcuRes);	
				
			}else if (cont.getIso().getClass().equals(DocumentReverso.class)){
				
				DocumentReverso documentReverso = (DocumentReverso) cont.getIso();
				DocumentRespuesta documentorespuesta = null;
				BridgeVC_BCE bridge = new BridgeVC_BCE();
				documentorespuesta = bridge.ProcesarreversoVC_BCE(documentReverso, cont.getIP());
				ContainerIsoQueue<DocumentRespuesta> strcuRes = new 
						ContainerIsoQueue<DocumentRespuesta>(documentorespuesta, "127.0.0.1");
				strcuRes.setSecuencial(bridge.getSecuencialTrx());
				MemoryGlobal.concurrentIso.put(strcuRes.getSecuencial()
				        , strcuRes);
				
			}else if(cont.getIso().getClass().equals(DocumentTransferencia.class)){
				
				DocumentTransferencia documentTransferencia = (DocumentTransferencia) cont.getIso();
				DocumentRespuesta documentorespuesta = null;
				BridgeVC_BCE bridge = new BridgeVC_BCE();
				documentorespuesta = bridge.ProcesarTransferenciaVC_BCE(documentTransferencia, cont.getIP());
				ContainerIsoQueue<DocumentRespuesta> strcuRes = new 
						ContainerIsoQueue<DocumentRespuesta>(documentorespuesta, "127.0.0.1");
				strcuRes.setSecuencial(bridge.getSecuencialTrx());
				MemoryGlobal.concurrentIso.put(strcuRes.getSecuencial()
				        , strcuRes);
				
			}else if(cont.getIso().getClass().equals(Iso8583Binary.class)){
				
				Iso8583Binary isoBin = (Iso8583Binary) cont.getIso();
				Iso8583Binary isoRes = null;
				CardProcessorIsAcq processor = new CardProcessorIsAcq();
				isoRes = processor.ProcessTransactionBinary(isoBin, cont.getIP());
				if(isoRes.getDe11_STAN().equals("000000")){
					MemoryGlobal.concurrentIso.put(isoBin.getDe11_STAN(), isoRes);
					
				}else
					MemoryGlobal.concurrentIso.put(isoRes.getDe11_STAN(), isoRes);
			}else if (cont.getIso().getClass().equals(DocumentRespuesta.class)) {
			
				DocumentRespuesta  docResponse = (DocumentRespuesta) cont.getIso();
				//MemoryGlobal.semaphoreIniLotesSpi.await();
				BridgeSPI_BCE bridge = new BridgeSPI_BCE();
				bridge.ProcessEnqueueTransaction(docResponse, cont.getIP());
			
			}else if (cont.getIso().getClass().equals(StructBanredMessage.class)) {
				
				BridgePagoDirectoBanred bridge = null;
				StructBanredMessage docResponseBanred = (StructBanredMessage) cont.getIso();
				if(docResponseBanred.getTypeMessage() == TypeMessage.RQ) {
					
					/*Cuando es Requerimiento osea Banred Envia*/
					bridge = new BridgePagoDirectoBanred();
					docResponseBanred = bridge.processTransactions(docResponseBanred, cont.getIP());
					docResponseBanred.setBodyMessageResponse(new String(docResponseBanred.getBodyBytesMessageResponse()));
					
					cont = new ContainerIsoQueue<>(docResponseBanred , "127.0.0.1");
					cont.setSecuencial(docResponseBanred.getSecuentialMessage());
					
					Queue queue = new Queue();
					queue.SendMessage(typeMessage.processor, cont, 1, 0);
					
				}else {
					
					/*Cuando es Respuesta es decir Banred queda a la espera de la respuesta
					 * exisita algun metodo en con un thread en espera, esperando el secuencial para expirar 
					 * y mandar a la cola de respuestas*/
					MemoryGlobal.concurrentIso.put(docResponseBanred.getSecuentialMessage(), docResponseBanred);
				}
			}
			
		} catch (Exception e) {
			
			e.printStackTrace();
		}	
	}

	protected <T> T putResponseClass(T obj, ContainerIsoQueue<?> cont){
		
		ExecutorService pool = null;
		try {
			
			 pool = Executors.newFixedThreadPool(30);
			 Callable<T> exec = new 
			 CredencialBridge<T>(cont.getIso(), cont.getIP());
			 Future<T> future = pool.submit(exec);
			 obj = (T) future.get();
			 return obj;
			
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("ERROR!!!!!!!!!!!! EN putResponseClass--------------" + e.getMessage());
			return null;
		}finally {
			
			pool.shutdown();
		}
	}
	
	@Override
	public void run() {
		
		executeTrx();
	}
	
}
