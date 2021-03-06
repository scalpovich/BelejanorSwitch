package com.belejanor.switcher.authorizations;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import com.belejanor.switcher.logger.Logger;
import com.belejanor.switcher.logger.LoggerConfig.TypeMonitor;
import com.belejanor.switcher.utils.GeneralUtils;
import com.belejanor.switcher.utils.Iterables;
import com.fitbank.dto.management.Detail;
import com.fitbank.dto.management.DetailField;
import com.fitbank.dto.management.Field;
import com.fitbank.dto.management.Record;

public class FitQueryables extends Thread implements Callable<Object> {

	private Object returnValue;
	private String nameTable;
	private int numberReg;
	private String nameField;
	private Detail fitBank;
	private Logger log;
	private boolean state;
	private String error;
	private String aliasField;
	
	public boolean isState() {
		return state;
	}

	public void setState(boolean state) {
		this.state = state;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public Object getReturnValue() {
		return returnValue;
	}

	public void setReturnValue(Object returnValue) {
		this.returnValue = returnValue;
	}

	public FitQueryables(String nameTable, int numberReg, String nameField, Detail FitBank){
		
		this.nameTable = nameTable;
		this.numberReg = numberReg;
		this.nameField = nameField;
		this.fitBank = FitBank;
		this.log = new Logger();
		this.start();
	}
	public FitQueryables(String nameTable, int numberReg, String nameField, String aliasField, Detail FitBank){
		
		this.nameTable = nameTable;
		this.numberReg = numberReg;
		this.nameField = nameField;
		this.fitBank = FitBank;
		this.aliasField = aliasField;
		this.log = new Logger();
		
	}
	public FitQueryables(){
		
		this.state = true;
		this.log = new Logger();
		this.error = "TRANSACCION EXITOSA";
	}

	public FitQueryables(String nameField, Detail FitBank){
		
		this();
		this.nameField = nameField;
		this.fitBank = FitBank;
	}
	
	private Object QueryDetailFitbank(String nameTable, int numberReg, String nameField, Detail FitBank){
		
		Object value = null;
		try {
			
			value = (Object) GeneralUtils.asStream(GeneralUtils.asStream
					   (GeneralUtils.asStream(FitBank.getTables().iterator(), true)
					  .filter(p -> p.getName().equalsIgnoreCase(nameTable))
					  .findFirst().orElseGet(()-> null).getRecords().iterator(), true)
					  .filter(x -> x.getNumber() == numberReg)
					  .findFirst().orElseGet(()-> null).getFields().iterator(), true)
					  .filter(y -> y.getName().equalsIgnoreCase(nameField))
					  .findFirst().orElseGet(()-> null).getValue();
			
		} catch (Exception e) {
			
			log.WriteLogMonitor("Error modulo FitQueryables::QueryDetailFitbank ", TypeMonitor.error, e);
		}
		
		return value;
	}
	
    public synchronized Object QueryDetailFitbank(String nameTable, int numberReg, String nameField, String aliasField, Detail FitBank){
		
		Object value = null;
		try {
			
			System.out.println("vino registro: " + nameField + "  alias " + aliasField);
			value = (Object) GeneralUtils.asStream(GeneralUtils.asStream
					   (GeneralUtils.asStream(FitBank.getTables().iterator(), true)
					  .filter(p -> p.getName().equalsIgnoreCase(nameTable))
					  .findFirst().orElseGet(()-> null).getRecords().iterator(), true)
					  .filter(x -> x.getNumber() == numberReg)
					  .findFirst().orElseGet(()-> null).getFields().iterator(), true)
					  .filter(y -> y.getName().equalsIgnoreCase(nameField) &&  y.getAlias().equalsIgnoreCase(aliasField))
					  .findFirst().orElseGet(()-> null)
					  .getValue();
			
		} catch (Exception e) {
			
			log.WriteLogMonitor("Error modulo FitQueryables::QueryDetailFitbank ", TypeMonitor.error, e);
		}
		
		return value;
	}
	
	public Object QueryDetailFitCTL(String nameField, Detail FitBank){
		
		Object value = null;
		try {
			
			Field field = GeneralUtils.asStream(FitBank.getFields().iterator(), true)		
										 .filter(x -> x.getName().equalsIgnoreCase(nameField))
										 .findFirst().orElseGet(()-> null);
			if(field != null)
				value = field.getValue();
					
		} catch (Exception e) {
			
			log.WriteLogMonitor("Error modulo FitQueryables::QueryDetailFitCTL ", TypeMonitor.error, e);
		}
		return value;
	}
	
    public List<Iterables> QueryDetailFitBankIterator(String nameTable, int numberIterators, String[] rowsNames, Detail FitBank){
		
		List<Iterables> iter = null;
		try {
			
			List<Record> rc = (List<Record>) GeneralUtils.asStream
							  (FitBank.getTables().iterator(), true)
					          .filter(a -> a.getName().equalsIgnoreCase(nameTable))
					          .findFirst().orElseGet(() -> null).getRecords();
			
			iter = new ArrayList<Iterables>();
			if(rc != null){
				if(rc.size() > 0){
					
					
					for (Record record : rc) {
						
						Iterables it = new Iterables();
						for (String r : rowsNames) {
							
							Object et = GeneralUtils.asStream(record.getFields().iterator())
								       .filter(a -> a.getName().equalsIgnoreCase(r))
								       .findFirst().orElseGet(()-> null);
							
							if(et != null)
								
								et = ((Field) et).getName();
							
							else{
								
								this.state = false;
								this.error = "NO SE PUDO RECUPERAR EL TAG: \"" + r + "\" DEL DETAIL FITBANK (NO EXISTE)";
								return null;
							}
							
							Object va =  GeneralUtils.asStream(record.getFields().iterator())
								        .filter(a -> a.getName().equalsIgnoreCase(r))
								        .findFirst().orElseGet(()-> null);
							
							if(va != null)
								
								va = ((DetailField) va).getValue();
							
							else{
								
								this.state = false;
								this.error = "NO SE PUDO RECUPERAR EL VALOR DEL TAG: \"" + r + "\" DEL DETAIL FITBANK (SIN VALOR)";
								return null;
							}
						    
						    if(!(et == null && va == null))
								it.addMapper(String.valueOf(et), String.valueOf(va));
						
						}
						iter.add(it);
					}
				}
			}	  
		} catch (Exception e) {
			
			this.state = false;
			this.error = GeneralUtils.ExceptionToString("ERROR QueryDetailFitBankIterator ", e, true);
			log.WriteLogMonitor("Error modulo FitQueryables::QueryDetailFitBankIterator ", TypeMonitor.error, e);
		}
		return iter;
	}

    @Override
	public void run() {
		
		this.returnValue = QueryDetailFitbank(this.nameTable, this.numberReg, 
							this.nameField, this.fitBank);
	}
    
    public Runnable runQueryFieldsWithAlias() {
    	
    	Runnable r = new Runnable() {
			
			@Override
			public void run() {
				
				returnValue = QueryDetailFitbank(nameTable, numberReg, 
						 nameField, aliasField, fitBank);
			}
		};
		
		return r;
    }

	@Override
	public Object call() throws Exception {
		
		return QueryDetailFitCTL(this.nameField, this.fitBank);
	}
	
	

	

	
}
