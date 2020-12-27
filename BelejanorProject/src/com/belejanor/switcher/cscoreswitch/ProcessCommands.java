package com.belejanor.switcher.cscoreswitch;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

import com.belejanor.switcher.logger.Logger;
import com.belejanor.switcher.logger.LoggerConfig.TypeMonitor;
import com.belejanor.switcher.memcached.TransactionCommands;
import com.belejanor.switcher.utils.GeneralUtils;

public class ProcessCommands {

	private Logger log;
	private String codError;
	private String desError;
	
	public ProcessCommands() {
		log = new Logger();
		this.codError = "000";
		this.desError = "TRANSACCION EXITOSA";
	}
	
	public String getCodError() {
		return codError;
	}

	public String getDesError() {
		return desError;
	}
	
	
	@SuppressWarnings("rawtypes")
	private List<TransactionCommands> getCommands(wIso8583 iso){
		
		TransactionCommands processor = new TransactionCommands();
		List<TransactionCommands> commands = null;
		@SuppressWarnings("unchecked")
		final ContainerProcessor<List<TransactionCommands>> container = new ContainerProcessor();
		
		try {
			
			commands = processor.getTransactionCommandsObject(iso.getISO_003_ProcessingCode()
					                  , iso.getISO_018_MerchantType(), Integer.parseInt(iso.getISO_024_NetworkId())
					                  , iso.getWsTransactionConfig().getAmmountDebit());
			
			commands = commands.stream()
					.filter(a -> a.getActivo() == 1) //Solo comandos activos
					.peek(Objects::requireNonNull)
					.collect(Collectors.toList());
			
			container.setObject(commands);
			
			if(commands != null){
				
				Thread t = new Thread(new Runnable() {
					
					@Override
					public void run() {

						Collections.sort(container.getObject(), (p1, p2) -> 
						Integer.compare(p1.getOrden(), p2.getOrden()));
					}
				});
				
				t.start();
				t.join();
				
			}else{
				
				this.codError = "073";
				this.desError = "NO SE HA PODIDO RECUPERAR LISTA DE COMANDOS A EJECUTAR EN LA TRANSACCION";
			}
			
		} catch (Exception e) {
			
			this.codError = "070";
			this.desError = GeneralUtils.ExceptionToString("ERROR EN PROCESOS ", e, false);
			log.WriteLogMonitor("Error modulo ProcessCommands::getCommands ", TypeMonitor.error, e);
			
		}
		
		return container.getObject();
	}
	
   @SuppressWarnings("rawtypes")
   private List<TransactionCommands> getCommandsMantenaince(wIso8583 iso){
		
		TransactionCommands processor = new TransactionCommands();
		List<TransactionCommands> commands = null;
		@SuppressWarnings("unchecked")
		final ContainerProcessor<List<TransactionCommands>> container = new ContainerProcessor();
		
		try {
			
			commands = processor.getTransactionCommandsObject(iso.getISO_003_ProcessingCode()
					                  , iso.getISO_018_MerchantType(), Integer.parseInt(iso.getISO_024_NetworkId())
					                  , iso.getWsTransactionConfig().getAmmountDebit());
			
			commands = commands.stream()
					.filter(a -> a.getRetorno().equalsIgnoreCase("M")) //Solo comandos activos
					.peek(Objects::requireNonNull)
					.collect(Collectors.toList());
			
			container.setObject(commands);			
			
		} catch (Exception e) {
			
			this.codError = "070";
			this.desError = GeneralUtils.ExceptionToString("ERROR EN PROCESOS ", e, false);
			log.WriteLogMonitor("Error modulo ProcessCommands::getCommandsMantenaince ", TypeMonitor.error, e);
			
		}
		
		return container.getObject();
	}
	
	public wIso8583 executeCommands(wIso8583 iso){
		
		List<TransactionCommands> commandList = null;
		double temp = 0;
		double tempIni = 0;
		int cont = 0;
		String SecuencialResp = null;
		SecuencialResp = iso.getISO_011_SysAuditNumber();
		try {
			
			ProcessCommands procCommands = new ProcessCommands();
			commandList = procCommands.getCommands(iso);
			if(procCommands.getCodError().equals("000")){
				
				if(commandList.size() > 0){
					
					for (TransactionCommands trxCommands : commandList) {
					
					   iso.setCommandCounter(cont);
					   if(trxCommands.getSecurandom() > 0){
							
							int longOriginalIso11 = iso.getISO_011_SysAuditNumber().length();
							String secu = GeneralUtils.GetSecuencialNumeric(longOriginalIso11);
							iso.setISO_011_SysAuditNumber(secu);
							iso.setSecuencialCommandExecutor(secu);
							
						}else{
							
							iso.setISO_011_SysAuditNumber(SecuencialResp);
							iso.setSecuencialCommandExecutor(SecuencialResp);
						}
					   
						String messageClass = trxCommands.getCommand();					
						List<String> aa = Arrays.asList(messageClass.split("\\."));
						String methodName = aa.get(aa.size() -1);
						String classname = messageClass.replace("." + methodName, "");
						
						Class<?> instanceClass = Class.forName(classname);																			
						Object classInstance = instanceClass.getConstructor(new Class[]{}).newInstance();					
						Method methodToInvoke = instanceClass.getMethod(methodName, wIso8583.class);	
						
						iso = (wIso8583) methodToInvoke.invoke(classInstance, iso);
						temp += iso.getWsTempAut();
						tempIni = temp - tempIni;
						log.WriteLogMonitor("*** Execute Command [" + cont + "]\nInvoke: " + messageClass + "\nStatus: " 
						+ iso.getISO_039_ResponseCode() + " ---> Temp: " + ""+(tempIni) + "   "
								+ "Secuencial: " + iso.getISO_011_SysAuditNumber() , TypeMonitor.monitor, null);
						
						
						
						if(!iso.getISO_039_ResponseCode().equals("000")){
							iso.setISO_039p_ResponseDetail("[" + cont + "] " + iso.getISO_039p_ResponseDetail());
							iso.setISO_011_SysAuditNumber(SecuencialResp);
							break;
						}
						cont++;
					}
				
				}else{
					
					iso.setISO_039_ResponseCode("100");
					iso.setISO_039p_ResponseDetail("LA TRANSACCION: " + iso.getISO_003_ProcessingCode() + 
							" NO TIENE COMANDOS A EJECUTAR");
				}
				
			}else{
				
				iso.setISO_039_ResponseCode(procCommands.getCodError());
				iso.setISO_039p_ResponseDetail(procCommands.getDesError() + " [" + cont + "]");
			}
			
		} catch (Exception e) {
			
			iso.setISO_039_ResponseCode("070");
			iso.setISO_039p_ResponseDetail(GeneralUtils.ExceptionToString("ERROR EN PROCESOS ", e, false));
			log.WriteLogMonitor("Error modulo ProcessCommands::executeCommands ", TypeMonitor.error, e);
			
		}finally {
			
			iso.setWsTempAut(temp);
		}
		return iso;
	}
    public wIso8583 executeIndependentCommands(wIso8583 iso){
		
		List<TransactionCommands> commandList = null;
		double temp = 0;
		int cont = 0;
		String SecuencialOriginal = null;
		SecuencialOriginal = iso.getISO_011_SysAuditNumber();
		boolean sf_flag = false;
		try {
			
			if(iso.getISO_000_Message_Type().startsWith("12")){
				ProcessCommands procCommands = new ProcessCommands();
				commandList = procCommands.getCommands(iso);
				if(procCommands.getCodError().equals("000")){
					
					if(commandList.size() > 0){
						List<String> listCommandsMantenaince = new ArrayList<>();
						for (TransactionCommands trxCommands : commandList) {
						   double [] temporizador = new double[commandList.size()];
						   iso.setCommandCounter(cont);
						   if(trxCommands.getSecurandom() > 0){
								
								int longOriginalIso11 = iso.getISO_011_SysAuditNumber().length();
								String secu = GeneralUtils.GetSecuencialNumeric(longOriginalIso11);
								iso.setISO_011_SysAuditNumber(secu);
								iso.setSecuencialCommandExecutor(secu);
								
							}else{
									
								iso.setISO_011_SysAuditNumber(SecuencialOriginal);
								iso.setSecuencialCommandExecutor(SecuencialOriginal);
							}
						   
						    listCommandsMantenaince.add(SecuencialOriginal);
						    iso.setListTrxAfectCommands(listCommandsMantenaince);
						   
							String messageClass = trxCommands.getCommand();					
							List<String> aa = Arrays.asList(messageClass.split("\\."));
							String methodName = aa.get(aa.size() -1);
							String classname = messageClass.replace("." + methodName, "");
							
							Class<?> instanceClass = Class.forName(classname);																			
							Object classInstance = instanceClass.getConstructor(new Class[]{}).newInstance();					
							Method methodToInvoke = instanceClass.getMethod(methodName, wIso8583.class);	
							
							iso = (wIso8583) methodToInvoke.invoke(classInstance, iso);
							temp += iso.getWsTempAut();
							temporizador[cont] = iso.getWsTempAut();
							log.WriteLogMonitor("*** Execute Command [" + cont + "]\nInvoke: " + messageClass + "\nStatus: " 
							+ iso.getISO_039_ResponseCode() + " ---> Temp: " + ""+(temporizador[cont])
							+ " \nSecuancial: " + iso.getISO_011_SysAuditNumber(), TypeMonitor.monitor, null);
							
							if(!iso.getISO_039_ResponseCode().equals("000")){
								iso.setISO_039p_ResponseDetail("[" + cont + "] " + iso.getISO_039p_ResponseDetail());
								//aqui hice el cambio de las colas 2018-10-17 (no funcionaba cuando daba error la 
								//cola y diccionario estatico se quedaban esperando la respuesta) ojo el break siempre ha estado
								//ojo SI ES ERROR vuelvo a poner el secuencial original
								iso.setISO_011_SysAuditNumber(SecuencialOriginal);
								break;
							}
							cont++;
							/*Linea que evalua SF*/
							sf_flag = iso.getWsISO_FlagStoreForward();
							if(sf_flag)
								System.out.println("**SI SF---> " +  sf_flag);
							else
								System.out.println("**NO SF---> " +  sf_flag);
						}
					
					}else{
						
						iso.setISO_039_ResponseCode("100");
						iso.setISO_039p_ResponseDetail("LA TRANSACCION: " + iso.getISO_003_ProcessingCode() + 
								" NO TIENE COMANDOS A EJECUTAR");
					}
					
				}else{
					
					iso.setISO_039_ResponseCode(procCommands.getCodError());
					iso.setISO_039p_ResponseDetail(procCommands.getDesError() + " [" + cont + "]");
				}
				
			}else{
				
				//REVERSOS
				cont = 0;
				ProcessCommands procCommands = new ProcessCommands();
				commandList = procCommands.getCommandsMantenaince(iso);
				if(procCommands.getCodError().equals("000")){
					
					if(commandList.size() > 0){
						
						for (TransactionCommands trxCommands : commandList) {
						   double [] temporizador = new double[commandList.size()];
						    iso.setCommandCounter(cont);
						  
						   
						    iso.setISO_011_SysAuditNumber(SecuencialOriginal);
						    iso.setSecuencialCommandExecutor(SecuencialOriginal);
						    
						    
							String messageClass = trxCommands.getCommand();					
							List<String> aa = Arrays.asList(messageClass.split("\\."));
							String methodName = aa.get(aa.size() -1);
							String classname = messageClass.replace("." + methodName, "");
							
							Class<?> instanceClass = Class.forName(classname);																			
							Object classInstance = instanceClass.getConstructor(new Class[]{}).newInstance();					
							Method methodToInvoke = instanceClass.getMethod(methodName, wIso8583.class);	
							
							iso = (wIso8583) methodToInvoke.invoke(classInstance, iso);
							temp += iso.getWsTempAut();
							temporizador[cont] = iso.getWsTempAut();
							log.WriteLogMonitor("*** Execute Command [" + cont + "]\nInvoke: " + messageClass + "\nStatus: " 
							+ iso.getISO_039_ResponseCode() + " ---> Temp: " + ""+(temporizador[cont]), TypeMonitor.monitor, null);
							
							if(!iso.getISO_039_ResponseCode().equals("000")){
								iso.setISO_039p_ResponseDetail("[" + cont + "] " + iso.getISO_039p_ResponseDetail());
								break;
							}
							cont++;
						}
					
					}else{
						
						iso.setISO_039_ResponseCode("100");
						iso.setISO_039p_ResponseDetail("LA TRANSACCION: " + iso.getISO_003_ProcessingCode() + 
								" NO TIENE COMANDOS A EJECUTAR PARA REVERSO");
					}
				}
			}
			
		} catch (Exception e) {
			
			iso.setISO_039_ResponseCode("070");
			iso.setISO_039p_ResponseDetail(GeneralUtils.ExceptionToString("ERROR EN PROCESOS ", e, false));
			log.WriteLogMonitor("Error modulo ProcessCommands::executeIndependentCommands ", TypeMonitor.error, e);
			
		}finally {
			
			iso.setWsTempAut(temp);
		}
		return iso;
	}
    
    public Callable<wIso8583> processorCallables(wIso8583 iso){
    	
    	Callable<wIso8583> callable = new Callable<wIso8583>() {
			
			@Override
			public wIso8583 call() throws Exception {
				
				return executeIndependentCommands(iso);
			}
		};
		return callable;
    }
}


class ContainerProcessor<T>{
	
	private T object;

	public ContainerProcessor() {
		
	}
	
	public ContainerProcessor(T object) {
		
		this.object = object;
	}
	
	public T getObject() {
		return object;
	}

	public void setObject(T object) {
		this.object = object;
	}
	
	
}
