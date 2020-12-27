package com.belejanor.switcher.implementations;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import javax.annotation.Resource;
import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.servlet.http.HttpServletRequest;
import javax.xml.ws.WebServiceContext;
//import org.apache.cxf.interceptor.InInterceptors;
import org.apache.cxf.phase.PhaseInterceptorChain;
import org.apache.cxf.transport.http.AbstractHTTPDestination;

import com.belejanor.switcher.cscoreswitch.AdminConcurrentMap;
import com.belejanor.switcher.cscoreswitch.ContainerIsoQueue;
import com.belejanor.switcher.cscoreswitch.IProcessTransaction;
import com.belejanor.switcher.cscoreswitch.Iso8583;
import com.belejanor.switcher.cscoreswitch.csProcess;
import com.belejanor.switcher.logger.Logger;
import com.belejanor.switcher.logger.LoggerConfig.TypeMonitor;
import com.belejanor.switcher.memcached.MemoryGlobal;
import com.belejanor.switcher.queueadmin.Queue;
import com.belejanor.switcher.queueadmin.typeMessage;
import com.belejanor.switcher.utils.GeneralUtils;

//@InInterceptors(interceptors = "com.fitbank.middleware.cscoreswitch.BasicAuthAuthorizationInterceptor")
@WebService(targetNamespace = "http://implementations.middleware.fitbank.com/", portName = "ProcessTransactionIsoPort", serviceName = "ProcessTransactionIsoService")
public class ProcessTransactionIso implements IProcessTransaction {

	
	@WebMethod(operationName = "processtransaction", action = "urn:Processtransaction")
	@Override
	public Iso8583 processtransaction(Iso8583 iso) {
		
		if(!MemoryGlobal.flagUseQueueInit){
			
			csProcess process = new csProcess();
			Logger log = null;
			//log = new Logger();
			//log.WriteLogMonitor("ENTRO <<SIN>> QUEUE INIT!!!!......", TypeMonitor.monitor, null);
			String IP = "Undefined";
			
			try {
				IP = ObtainIpClient();
			} catch (Exception e) {			
				log = new Logger();
				log.WriteLogMonitor("Error modulo ProcessTransactionIso::processtransaction [ImplementsWeb] ", TypeMonitor.error, e);
			}
			return process.ProcessTransactionMain(iso, IP);
			
		}else{
			
			//Logger log = new Logger();
			//log.WriteLogMonitor("ENTRO POR QUEUE INIT!!!!......", TypeMonitor.monitor, null);
			final Response<Iso8583> IsoRes = new Response<Iso8583>();
			try {
				
				ContainerIsoQueue<Iso8583> cont = new ContainerIsoQueue<>(iso ,ObtainIpClient());
				cont.setSecuencial(iso.getISO_011_SysAuditNumber());
				Queue queue = new Queue();
				queue.SendMessage(typeMessage.initialMessage, cont, 1, 0);
				IsoRes.setSecuencial(iso.getISO_011_SysAuditNumber());
				
				final CountDownLatch semaphore = new CountDownLatch(1);
				Thread t = new Thread(new Runnable() {
				@Override
				public void run() {
					while (true) {
						synchronized(MemoryGlobal.concurrentIso) {
							if(MemoryGlobal.concurrentIso.containsKey(IsoRes.getSecuencial())){
								IsoRes.setObject((Iso8583) MemoryGlobal.concurrentIso.get(IsoRes.getSecuencial()));
								@SuppressWarnings("unused")
								AdminConcurrentMap map = new AdminConcurrentMap(IsoRes.getSecuencial());
								semaphore.countDown();
								break;
							}
						}
					}
				}
				});
				t.start();
				
				if(!semaphore.await(70000, TimeUnit.MILLISECONDS)){
					
					iso.setISO_039_ResponseCode("910");
					iso.setISO_039p_ResponseDetail("HA EXPIRADOEL TIEMPO DE RESPUESTA (DESDE ITERADOR)");
					
				}else
					
					iso = (Iso8583) IsoRes.getObject();
				
			} catch (Exception e) {
				
				e.printStackTrace();
				
				iso.setISO_039_ResponseCode("909");
				iso.setISO_039p_ResponseDetail(GeneralUtils.ExceptionToString("ERROR RESPONSE ISO "
						,e, false));
			}
			return iso;
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

}
