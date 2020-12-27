package com.belejanor.switcher.implementations;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;
import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.servlet.http.HttpServletRequest;
import javax.xml.ws.WebServiceContext;
import org.apache.cxf.phase.PhaseInterceptorChain;
import org.apache.cxf.transport.http.AbstractHTTPDestination;

import com.belejanor.switcher.credencial.BalanceCredencialRequest;
import com.belejanor.switcher.credencial.BalanceCredencialResponse;
import com.belejanor.switcher.credencial.DebitCredencialRequest;
import com.belejanor.switcher.credencial.DebitCredencialResponse;
import com.belejanor.switcher.credencial.ITransactionsCredencial;
import com.belejanor.switcher.credencial.MovementsCredencialRequest;
import com.belejanor.switcher.credencial.MovementsCredencialResponse;
import com.belejanor.switcher.credencial.RevertCredencialRequest;
import com.belejanor.switcher.credencial.RevertCredencialResponse;
import com.belejanor.switcher.cscoreswitch.AdminConcurrentMap;
import com.belejanor.switcher.cscoreswitch.ContainerIsoQueue;
import com.belejanor.switcher.memcached.MemoryGlobal;
import com.belejanor.switcher.queueadmin.Queue;
import com.belejanor.switcher.queueadmin.typeMessage;


@WebService(targetNamespace = "http://implementations.middleware.fitbank.com/", portName = "AutorizationTransactionsPort", serviceName = "AutorizationTransactionsService")
public class AutorizationTransactions implements ITransactionsCredencial {

	@WebMethod(operationName = "debitAccount", action = "urn:DebitAccount")
	@Override
	public DebitCredencialResponse debitAccount(DebitCredencialRequest DebitCredencialRequest) {
	
		DebitCredencialResponse response = new DebitCredencialResponse();
		Object responses = SendQueueInitial(DebitCredencialRequest);
		response =(DebitCredencialResponse) responses;
		return response;
	}

	@WebMethod(operationName = "revertDebitAccount", action = "urn:RevertDebitAccount")
	@Override
	public RevertCredencialResponse revertDebitAccount(RevertCredencialRequest RevertCredencialRequest) {
		
		RevertCredencialResponse response = new RevertCredencialResponse();
		Object responses = SendQueueInitial(RevertCredencialRequest);
		response =(RevertCredencialResponse) responses;
		return response;
	}

	@WebMethod(operationName = "balanceAccount", action = "urn:BalanceAccount")
	@Override
	public BalanceCredencialResponse balanceAccount(BalanceCredencialRequest BalanceCredencialRequest) {
		BalanceCredencialResponse response = new BalanceCredencialResponse();
		Object responses = SendQueueInitial(BalanceCredencialRequest);
		response =(BalanceCredencialResponse) responses;
		return response;
	}

	@WebMethod(operationName = "movementsAccount", action = "urn:MovementsAccount")
	@Override
	public MovementsCredencialResponse movementsAccount(MovementsCredencialRequest MovementsCredencialRequest) {
		MovementsCredencialResponse response = new MovementsCredencialResponse();
		Object responses = SendQueueInitial(MovementsCredencialRequest);
		response =(MovementsCredencialResponse) responses;
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
	
	@SuppressWarnings("unchecked")
	protected <T> T SendQueueInitial(T obj){

		T response;
		final Response<T> r = new Response<>();
		
		try {
			
			
			ContainerIsoQueue<T> cont = new ContainerIsoQueue<>(obj ,ObtainIpClient());
			Queue queue = new Queue();
			String secuencial = "";
			
		
			if(obj.getClass().equals(DebitCredencialRequest.class)){
				DebitCredencialRequest d = (DebitCredencialRequest)obj;
				secuencial = d.getIdentificador();
				r.setSecuencial(secuencial);
				cont.setSecuencial(secuencial);
				//CredencialBridge<T> cred = new CredencialBridge<>();
				//response = (T) cred.trxDebit(d, "127.0.0.1");
				
			}
		
			if(obj.getClass().equals(RevertCredencialRequest.class)){
				RevertCredencialRequest d = (RevertCredencialRequest)obj;
				secuencial = d.getIdentificador();
				r.setSecuencial(secuencial);
				cont.setSecuencial(secuencial);
			}
			
			if(obj.getClass().equals(BalanceCredencialRequest.class)){
				BalanceCredencialRequest d = (BalanceCredencialRequest)obj;
				secuencial = d.getIdentificador();
				r.setSecuencial(secuencial);
				cont.setSecuencial(secuencial);
			}
			
			if(obj.getClass().equals(MovementsCredencialRequest.class)){
				MovementsCredencialRequest d = (MovementsCredencialRequest)obj;
				secuencial = d.getIdentificador();
				r.setSecuencial(secuencial);
				cont.setSecuencial(secuencial);
			}
			queue.SendMessage(typeMessage.initialMessage, cont, 1, 0);
			
			final CountDownLatch semaphore = new CountDownLatch(1);
			Thread t = new Thread(new Runnable() {
				@Override
				public void run() {
					while (true) {
						synchronized(MemoryGlobal.concurrentIso) {
							if(MemoryGlobal.concurrentIso.containsKey(r.getSecuencial())){
								r.setObject((T) MemoryGlobal.concurrentIso.get(r.getSecuencial()));
								@SuppressWarnings("unused")
								AdminConcurrentMap map = new AdminConcurrentMap(r.getSecuencial());
								semaphore.countDown();
								break;
							}
						}
					}
				}
			});
			t.start();
	    	
			if(!semaphore.await(50000, TimeUnit.MILLISECONDS))
				return null;
			
		} catch (Exception e) {
			System.err.println(e);
			return null;
		}
		
		response = r.getObject();
		
		return response;
	}
	
}
