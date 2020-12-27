package com.belejanor.switcher.extetrnalprocess;

import com.belejanor.switcher.logger.Logger;
import com.belejanor.switcher.logger.LoggerConfig.TypeMonitor;
import com.belejanor.switcher.snp.spi.ProcessorLotes;

public class SnpScheduledExecuteLot {

	private Logger log;
	public SnpScheduledExecuteLot(){
		this.log = new Logger();
	}

	public Runnable executeScheduleLotSnp(){
		
		ProcessorLotes processor = new ProcessorLotes();
		Runnable runnable = new Runnable() {
			
			@Override
			public void run() {
				
				log.WriteLogMonitor("<<<< Inicia proceso de Lotes SNP SPI....>>>>", TypeMonitor.monitor, null);

				Thread tproc = new Thread(() -> {
				   
					//processor.ExecuteProcessSNPLoptes();
					processor.ExecuteProcessSNPLoptes_V2();
				});
				
				tproc.start();
				
			}
		};
		return runnable;		
	}
}
