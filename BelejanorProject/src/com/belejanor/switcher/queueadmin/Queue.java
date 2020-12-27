package com.belejanor.switcher.queueadmin;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.belejanor.switcher.memcached.MemoryGlobal;

public class Queue {
	
	
	@SuppressWarnings("static-access")
	public  void SendMessage(typeMessage type, Object obj, int prioridad, long seconds){
		
		@SuppressWarnings("unused")
		AdminQueueMessage queue = null;
		if(type == type.initialMessage){
			queue = new AdminQueueMessage(MemoryGlobal.queueNameIni, 
					    MemoryGlobal.sessionQueueIni, MemoryGlobal.producerQueueIni,
					    obj, prioridad,0, seconds);
		}else if (type == type.storeAndForwardType) {
			queue = new AdminQueueMessage(MemoryGlobal.queueNameSf, 
				    MemoryGlobal.sessionQueueSf, MemoryGlobal.producerQueueIni,
				    obj, prioridad,0, seconds);
		}else if (type == type.externalMessageTCP) {
			
			queue = new AdminQueueMessage(MemoryGlobal.queueNameCoonecta, 
				    MemoryGlobal.sessionQueueExternal, MemoryGlobal.producerQueueExternal,
				    obj, prioridad,1, seconds);
		}else if (type == type.processor) {
		
			queue = new AdminQueueMessage(MemoryGlobal.queueNameProcessor, 
				    MemoryGlobal.sessionQueueProcessor, MemoryGlobal.producerQueueProcessor,
				    obj, prioridad,0, seconds);
		}
		 
	}
	@SuppressWarnings("static-access")
	public  void ReceiveMessage(typeMessage type){
		
		ExecutorService threadPool = Executors.newFixedThreadPool(20);
		try {
			
			threadPool.execute(new Runnable() {
				
				@Override
				public void run() {
					
					@SuppressWarnings("unused")
					ReceiveAsyncMessage message = null;
					if(type == type.initialMessage){
						
						message = new ReceiveAsyncMessage(MemoryGlobal.queueNameIni, 
								  MemoryGlobal.sessionQueueIni);
					}else if (type == type.storeAndForwardType) {
					
						message = new ReceiveAsyncMessage(MemoryGlobal.queueNameSf, 
								  MemoryGlobal.sessionQueueSf);
						
					}else if (type == type.processor) {
						
						message = new ReceiveAsyncMessage(MemoryGlobal.queueNameProcessor, 
								  MemoryGlobal.sessionQueueProcessor);
					}
				}
			});
			
		} finally {
			
			threadPool.shutdown();
		}
			
		
	}
}
