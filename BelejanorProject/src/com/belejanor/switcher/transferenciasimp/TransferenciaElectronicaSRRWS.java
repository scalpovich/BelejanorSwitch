package com.belejanor.switcher.transferenciasimp;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;
import javax.jws.WebService;
import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.ws.WebServiceContext;
import org.apache.cxf.phase.PhaseInterceptorChain;
import org.apache.cxf.transport.http.AbstractHTTPDestination;

import com.belejanor.switcher.bridges.BridgeVC_BCE;
import com.belejanor.switcher.cscoreswitch.AdminConcurrentMap;
import com.belejanor.switcher.cscoreswitch.ContainerIsoQueue;
import com.belejanor.switcher.cscoreswitch.ICoreClassT;
import com.belejanor.switcher.memcached.MemoryGlobal;
import com.belejanor.switcher.queueadmin.Queue;
import com.belejanor.switcher.queueadmin.typeMessage;
import com.belejanor.switcher.utils.GeneralUtils;
import com.belejanor.switcher.utils.StringUtils;
import com.belejanor.switcher.ventanillaimp.Responses;

import urn.iso.std.iso20022.tech.xsd.SRRFaultException.Error;
import urn.iso.std.iso20022.tech.xsd.pacs_002_001_05.DocumentRespuesta;
import urn.iso.std.iso20022.tech.xsd.pacs_008_001_04.DocumentTransferencia;

@WebService(targetNamespace = "http://www.bce.fin.ec/wsdl/srr/TransferenciaElectronicaSRRWS", 
portName = "TransferenciaElectronicaSRRWSPort", 
serviceName = "TransferenciaElectronicaSRRWSService")
@XmlRootElement(namespace = "http://www.bce.fin.ec/wsdl/srr/TransferenciaElectronicaSRRWS")
@XmlType(namespace = "http://www.bce.fin.ec/wsdl/srr/TransferenciaElectronicaSRRWS")
public class TransferenciaElectronicaSRRWS implements ICoreClassT{

	public DocumentRespuesta ejecutarTransferenciaElectronica(DocumentTransferencia documentoSolicitud) throws Error {
		
		DocumentRespuesta response = null;
		if(!MemoryGlobal.flagUseQueueInit){
			
			BridgeVC_BCE bridge = new BridgeVC_BCE();
			response = bridge.ProcesarTransferenciaVC_BCE(documentoSolicitud, ObtainIpClient());
			
		}else{
			
			Responses res = new Responses();
			final CountDownLatch semaphore = new CountDownLatch(1);
			
				ContainerIsoQueue<DocumentTransferencia> cont = new ContainerIsoQueue<>(documentoSolicitud , ObtainIpClient());
				Queue queue = new Queue();
				queue.SendMessage(typeMessage.initialMessage, cont, 1, 0);
				
				if(!StringUtils.IsNullOrEmpty(documentoSolicitud.getFIToFICstmrCdtTrf().getGrpHdr().getMsgId())){
					if(documentoSolicitud.getFIToFICstmrCdtTrf().getGrpHdr().getMsgId().length() > 32)
						throw new Error("FF2", "Error, el campo FIToFICstmrCdtTrf/GrpHdr/MsgId es invalido");
				}else
					throw new Error("FF2", "Error, el campo FIToFICstmrCdtTrf/GrpHdr/MsgId es nulo o vacio");
				
				String Secuencial = documentoSolicitud.getFIToFICstmrCdtTrf().getGrpHdr().getMsgId();
				
				Thread t = new Thread(new Runnable() {
					
					public void run() {
						
						while (true) {
							synchronized(MemoryGlobal.concurrentIso) {
								if(MemoryGlobal.concurrentIso.containsKey(Secuencial)){
									res.setResponse((DocumentRespuesta) MemoryGlobal.concurrentIso.get(Secuencial));
									@SuppressWarnings("unused")
									AdminConcurrentMap map = new AdminConcurrentMap(Secuencial);
									semaphore.countDown();
									break;
								}
							}
						}
					}
				});
				t.start();
				try {
					if(!semaphore.await(50000, TimeUnit.MILLISECONDS))
						res.setResponse(null);
				} catch (InterruptedException e) {
					
					throw new Error("FF2",GeneralUtils.ExceptionToString(null, e, true));
				}
				response = res.getResponse();
		}
		return response;
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
}
