package com.belejanor.switcher.bimo.processor;

import com.belejanor.switcher.cscoreswitch.ProcessCommands;
import com.belejanor.switcher.cscoreswitch.wIso8583;

public class ProcessorBimo {

	public ProcessorBimo() {
		
		super();
	}
	
	public wIso8583 executeBimoTransactions(wIso8583 iso){
		
		ProcessCommands processor = new ProcessCommands();
		return  processor.executeCommands(iso);
	}
	
}
