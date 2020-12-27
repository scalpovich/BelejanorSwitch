package com.belejanor.switcher.extetrnalprocess;

import com.belejanor.switcher.cscoreswitch.Iso8583;
import com.belejanor.switcher.cscoreswitch.csProcess;
import com.belejanor.switcher.logger.Logger;
import com.belejanor.switcher.logger.LoggerConfig.TypeMonitor;
import com.belejanor.switcher.memcached.MemoryGlobal;
import com.belejanor.switcher.utils.StringUtils;

public class ControlPersistence {
	
	private Logger log;
	public ControlPersistence(){
		this.log = new Logger();
	}

	public Runnable executeMessageControl(){
		
		Runnable runnable = new Runnable() {
			
			@Override
			public void run() {
				
				log.WriteLogMonitor("Inicia mensaje de Control....", TypeMonitor.monitor, null);
				csProcess process = new csProcess();
				Iso8583 iso = new Iso8583("1800", MemoryGlobal.proccodeMessageControl, 
												  MemoryGlobal.ownChannel, MemoryGlobal.ownNetwork);
				iso.setISO_002_PAN(StringUtils.padRight(MemoryGlobal.abaIfi.get(0), 16, "0"));
				iso = process.ProcessTransactionMain(iso, "127.0.0.1");			
			}
		};
		return runnable;		
	}
}
