package com.belejanor.switcher.acquirers;

import com.belejanor.switcher.cscoreswitch.Iso8583;
import com.belejanor.switcher.cscoreswitch.Iso8583Binary;
import com.belejanor.switcher.cscoreswitch.csProcess;
import com.belejanor.switcher.logger.Logger;
import com.belejanor.switcher.logger.LoggerConfig.TypeMonitor;
import com.belejanor.switcher.parser.ParseIso1toIso2;

public class CardProcessorIsAcq {
	
	private Logger log;
	
	public CardProcessorIsAcq() {
		super();
		log = new Logger();
	}

	public Iso8583Binary ProcessTransactionBinary(Iso8583Binary isoBin, String IP){
		
		try {
			
			Iso8583 iso = new Iso8583(isoBin);
			if(iso.getISO_039_ResponseCode().equals("960")){
				
				csProcess processor = new csProcess();
				iso = processor.ProcessTransactionMain(iso, IP);
				ParseIso1toIso2 parser = new ParseIso1toIso2();
				isoBin = parser.parseIso2ToIso1(iso, isoBin);
				
			}else{
				
				isoBin = Iso8583Binary.GenericError();
			}
			
		} catch (Exception e) {
			
			isoBin = Iso8583Binary.GenericError();
			log.WriteLogMonitor("Error Modulo" + this.getClass().getName() + "::ProcessTransactionBinary"
					, TypeMonitor.error, e);
		}
		return isoBin;
		
	}
}

