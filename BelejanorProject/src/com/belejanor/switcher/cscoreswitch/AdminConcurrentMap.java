package com.belejanor.switcher.cscoreswitch;

import com.belejanor.switcher.memcached.MemoryGlobal;

public class AdminConcurrentMap extends Thread {
	
	private String key;
	
	public AdminConcurrentMap(String key) {
		
		this.key = key;
		this.start();
	}
	public void DeleteKey(){
		
		try {
			Thread.sleep(5000);
			MemoryGlobal.concurrentIso.remove(this.key);
			System.out.println("Despues: " + MemoryGlobal.concurrentIso.size());
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		
		DeleteKey();
	}
	
}
