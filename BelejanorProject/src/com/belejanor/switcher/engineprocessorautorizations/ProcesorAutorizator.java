package com.belejanor.switcher.engineprocessorautorizations;

import java.sql.ResultSet;
import com.belejanor.switcher.cscoreswitch.wIso8583;

public class ProcesorAutorizator {

	private OnEventListener mListener; // listener field 
	  
    public void registerOnGeekEventListener(OnEventListener mListener) 
    { 
        this.mListener = mListener; 
    } 
    
    public void processDebitoCobisStuff(wIso8583 iso) {
    	 new Thread(new Runnable() { 
             public void run() 
             { 
            	 ResultSet rs = null;
                 System.out.println("Realizando operación {debito Cobis} en tarea asíncrona"); 
                 if (mListener != null) { 
                     mListener.onResultTransactionCobis(iso,rs); //este es como el alive semaforo
                 } 
             } 
         }).start(); 
    }
}
