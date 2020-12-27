package com.belejanor.switcher.cscoreswitch;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import com.belejanor.switcher.logger.Logger;
import com.belejanor.switcher.logger.LoggerConfig.TypeMonitor;
import com.belejanor.switcher.memcached.AdditionalIsoValues;
import com.belejanor.switcher.memcached.TransactionNotifications;
import com.belejanor.switcher.memcached.ValidTransaction;
import com.belejanor.switcher.utils.FormatUtils;
import com.belejanor.switcher.utils.GeneralUtils;
import com.belejanor.switcher.utils.Ref;
import com.belejanor.switcher.utils.StringUtils;
import com.belejanor.switcher.cscoreswitch.Iso8583;

public class EngineProcessorTransactionsBackGround {

 private Logger log;
 private wIso8583 wiso;

 public EngineProcessorTransactionsBackGround() {
	super();
	log = new Logger();
 }
 
 public EngineProcessorTransactionsBackGround(wIso8583  iso) {
	 
	 this();
	 wiso = iso;
 }
	
 public wIso8583 ExecutionIndividualTransaction(wIso8583 iso, Ref<wIso8583> isoResponse) {
		
	wIso8583 isoClone = null;
	csProcess processor = null;
	ValidTransaction camposValidTrx = null;
	List<ValidTransaction> listValidTrx = null;
	String Proccode,CanalCod = "NULL";
	int NetId = -1;
	double AmmountDebit = -1;
	try {
		
		isoClone = (wIso8583) iso.clone();
		isoClone.setWsTransactionConfig((TransactionConfiguration)iso.getWsTransactionConfig().CloneObject());
		/*Llamada a ValidTransaction para ver campos V_CONTROL (campos adicionales)*/
		/*Comodines si comienza con:
		 *  -- campos calculables
		 *  ** valores fijos 
		 *  com. es comando*/
		 camposValidTrx = new ValidTransaction();
		 /*Estos campos deben ser llamados del campo Transaction Configuration 
		  * ProccodeTransactionFit de la Transaccion que la llama*/
		 Proccode = Arrays.asList(iso.getWsTransactionConfig().getProccodeTransactionFit().split("\\|")).get(0);
		 CanalCod = Arrays.asList(iso.getWsTransactionConfig().getProccodeTransactionFit().split("\\|")).get(1);
		 NetId = Integer.parseInt(Arrays.asList(iso.getWsTransactionConfig().getProccodeTransactionFit().split("\\|")).get(2));
		 AmmountDebit = Double.parseDouble(Arrays.asList(iso.getWsTransactionConfig()
				              .getProccodeTransactionFit().split("\\|")).get(3));
		 listValidTrx = camposValidTrx.getValidTrxfigListObject(Proccode, NetId, CanalCod, AmmountDebit);
		 if(listValidTrx != null) {
			 
			 if(listValidTrx.size() > 0) {
				 
				 Iso8583 isoTrx = new Iso8583();
				 /*Asigno los valores de Iso8583 para la transaccion*/
				 isoTrx = AssignRowsIso8583(listValidTrx, new Iso8583(iso));
				 if(iso.isRecursive())
					 isoTrx.setISO_BitMap("NONE");
				 if(isoTrx != null) {
					 
					 /*Ejecuto la transaccion*/
					 processor = new csProcess();
					 isoTrx = processor.ProcessTransactionMain(isoTrx, "127.0.0.1");
					 /*Seteo en el wiso Referencia TODOS los campos resultantes, tomar en cuenta que
					  * se crea un metodo parser en la clase wIso8583 para setear a un Iso8583 
					  * (Ver la parte en codifo new wIso8583(isoTrx)) */
					 wIso8583 isoRes = new wIso8583();
					 isoRes = isoRes.Iso8583ToWiso8583(isoTrx);
					 isoResponse.set(isoRes);
					 
				 }else {
					 
					 iso.setISO_039_ResponseCode("108");
					 iso.setISO_039p_ResponseDetail("NO SE PUEDE EJECUTAR PROCESO EN BACKGROUND, ISO8583 ES NULO, ERROR REFLEXIVO");
				 }
				 
			 }else {
				
				 iso.setISO_039_ResponseCode("075");
				 iso.setISO_039p_ResponseDetail("NO SE PUEDE EJECUTAR PROCESO EN BACKGROUND, " +
				 "NO EXISTE CAMPOS VALID_TRANSACTION PARA LA TRX.");
			 }
			 
		 }else {
			 
			 iso.setISO_039_ResponseCode("100");
			 iso.setISO_039p_ResponseDetail("NO EXISTE TRANSACCION BACKGROUND A EJECUTAR CON P003: " 
			 + Proccode + " P018: " + CanalCod + " P024: " + String.valueOf(NetId) + " P004: " + String.valueOf(AmmountDebit));
		 }
		
		
	} catch (Exception e) {
		
		iso.setISO_039_ResponseCode("909");
		iso.setISO_039p_ResponseDetail("ERROR EN PROCESOS " + GeneralUtils.ExceptionToString(null, e, false));
		log.WriteLogMonitor("Error Modulo" + this.getClass().getName() + "::ExecutionIndividualTransaction"
				, TypeMonitor.error, e);
	}finally {
		
		iso.setWsTempAut((iso.getTickAut().getTime(TimeUnit.MILLISECONDS)/1000.0));
	}
	
	return iso;
 }
 
 
 public void ExecuteTrxsInBackGround(wIso8583 iso) {
	 
	 wIso8583 isoClone = null;
	 TransactionNotifications trxNotif = null;
	 List<TransactionNotifications> listTrxNotif = null;
	 String Proccode = "NULL",CanalCod = "NULL";
	 int NetId = -1;
	 double AmmountDebit = -1;
	 try {
		 
		 isoClone = (wIso8583) iso.clone();
	     isoClone.setWsTransactionConfig((TransactionConfiguration)iso.getWsTransactionConfig().CloneObject());
	     isoClone = addIsoAdditionalRows(new Ref<wIso8583>(iso));
	     
		 trxNotif = new TransactionNotifications();
		 Proccode = iso.getISO_003_ProcessingCode();
		 CanalCod = iso.getISO_018_MerchantType();
		 NetId = Integer.parseInt(iso.getISO_024_NetworkId());
		 AmmountDebit = -1;
		 
		 listTrxNotif = trxNotif.getTransactionNotificationsObject(Proccode, CanalCod, NetId, AmmountDebit);
		 
		 if(listTrxNotif != null) {
			 
			if(listTrxNotif.size() > 0) {
				
				List<Iso8583> iso8583ListTrxExec = new ArrayList<>();
				String _Proccode = "NULL",_CanalCod = "NULL";
				int _NetId = -1;
				double _AmmountDebit = -1;
				int counter = 0;
				for (TransactionNotifications trxNot : listTrxNotif) {
					  isoClone =  addRowsIsoToParentTransaction(listTrxNotif, isoClone, counter);
					 _Proccode = Arrays.asList(trxNot.getTran_process().split("\\|")).get(0);
					 _CanalCod = Arrays.asList(trxNot.getTran_process().split("\\|")).get(1);
					 _NetId = Integer.parseInt(Arrays.asList(trxNot.getTran_process().split("\\|")).get(2));
					 _AmmountDebit = Double.parseDouble(Arrays.asList(trxNot.getTran_process().split("\\|")).get(3));
					
					Iso8583 iso8583 = new Iso8583();
					iso8583 = getIso8583IndividualTransaction(isoClone, _Proccode, 
							_CanalCod, _NetId, _AmmountDebit);
					iso8583ListTrxExec.add(iso8583);
					counter ++;
				}
				
				if(iso8583ListTrxExec != null) {
					
					if(iso8583ListTrxExec.size() > 0) {
						
						EngineCallableProcessor<Iso8583> engine = new 
								EngineCallableProcessor<>((iso8583ListTrxExec.size() * 2));
						String IP = "127.0.0.1";
						for (Iso8583 isom : iso8583ListTrxExec) {
							
							csProcess proc = new csProcess(isom, IP);
							engine.add(proc);
						}
						@SuppressWarnings("unused")
						List<Iso8583> listIsoProc = engine.goProcess();
					}
				}
				
			}else {
				
				log.WriteLogMonitor("<<BackGroundProcesor>> La transaccion padre Trx: " + Proccode 
			 			 + " Canal: " + CanalCod + " Red: " + NetId + ", no posee transacciones hijas a ejecutar"
			    , TypeMonitor.monitor, null);
			}
			 
		 }else {
			 
			 log.WriteLogMonitor("<<BackGroundProcesor>> Problemas al recuperar Transacciones a ejecutar para la transaccion padre Trx: " + Proccode 
					 			 + " Canal: " + CanalCod + " Red: " + NetId
					 , TypeMonitor.monitor, null);
		 }
		 
		
	} catch (Exception e) {
		
		log.WriteLogMonitor("<<BackGroundProcesor>> Error en Procesos, revise logs de transacciones padre: " + Proccode 
	 			 + " Canal: " + CanalCod + " Red: " + NetId
	 , TypeMonitor.monitor, null);
		log.WriteLogMonitor("Error Modulo" + this.getClass().getName() + "::ExecuteTrxsInBackGround"
				, TypeMonitor.error, e);
	}
 }
	
 public Iso8583 getIso8583IndividualTransaction(wIso8583 iso, String procCode, String canalCod, int netId, double ammount) {
		
	wIso8583 isoClone = null;
	Iso8583 isoTrx = null;
	ValidTransaction camposValidTrx = null;
	List<ValidTransaction> listValidTrx = null;
	String Proccode,CanalCod = "NULL";
	int NetId = -1;
	double AmmountDebit = -1;
	try {
		
		 isoClone = (wIso8583) iso.clone();
		 isoClone.setWsTransactionConfig((TransactionConfiguration)iso.getWsTransactionConfig().CloneObject());
		 camposValidTrx = new ValidTransaction();
		
		 Proccode = procCode;
		 CanalCod = canalCod;
		 NetId = netId;
		 AmmountDebit = ammount;
		 listValidTrx = camposValidTrx.getValidTrxfigListObject(Proccode, NetId, CanalCod, AmmountDebit);
		 if(listValidTrx != null) {
			 
			 if(listValidTrx.size() > 0) {
				 
				 isoTrx = new Iso8583();
				 /*Asigno los valores de Iso8583 para la transaccion*/
				 isoTrx = AssignRowsIso8583(listValidTrx, new Iso8583(iso));
				 
			 }else {
				
				 iso.setISO_039_ResponseCode("075");
				 iso.setISO_039p_ResponseDetail("NO SE PUEDE EJECUTAR PROCESO EN BACKGROUND, " +
				 "NO EXISTE CAMPOS VALID_TRANSACTION PARA LA TRX.");
			 }
			 
		 }else {
			 
			 iso.setISO_039_ResponseCode("100");
			 iso.setISO_039p_ResponseDetail("NO EXISTE TRANSACCION BACKGROUND A EJECUTAR CON P003: " 
			 + Proccode + " P018: " + CanalCod + " P024: " + String.valueOf(NetId) + " P004: " + String.valueOf(AmmountDebit));
		 }
		
		
	} catch (Exception e) {
		
		iso.setISO_039_ResponseCode("909");
		iso.setISO_039p_ResponseDetail("ERROR EN PROCESOS " + GeneralUtils.ExceptionToString(null, e, false));
		log.WriteLogMonitor("Error Modulo" + this.getClass().getName() + "::getIso8583IndividualTransaction"
				, TypeMonitor.error, e);
	}finally {
		
		iso.setWsTempAut((iso.getTickAut().getTime(TimeUnit.MILLISECONDS)/1000.0));
	}
	
	return isoTrx;
	
 }
	 
 
 public Iso8583 AssignRowsIso8583(List<ValidTransaction> listValidTrx, Iso8583 iso) {
		
		Iso8583 isoOrigen = new Iso8583();
		try {
			
			Class<?> _class = Iso8583.class;
			Class<?> c = isoOrigen.getClass();
			
            Field properties[] = _class.getDeclaredFields();
            Field origenIsoField = null;
            for (int i = 0; i < properties.length; i++) {
                Field field = properties[i];
                for (ValidTransaction valid : listValidTrx) {
        			
                	if(!field.getName().contains("$SWITCH_TABLE$")) {
	                	if(field.getName().contains(valid.getV_campoiso())) {
	                		
	                		origenIsoField = c.getDeclaredField(field.getName());
	                		origenIsoField.setAccessible(true);
	                		origenIsoField.set(isoOrigen, getValue(valid.getV_control(),iso));
	                		break;
	                	}
                	}
        		}
            }
			
		} catch (Exception e) {
			
			log.WriteLogMonitor("Error Modulo" + this.getClass().getName() + "::AssignRowsIso8583"
					, TypeMonitor.error, e);
		}
		return isoOrigen;
	}
	
	/*======================================Metodos Reflectivos ==============================================================*/
	protected static Object getValue(Object value, Iso8583 iso){
		Logger log = null;
		Object obj = null;
		String comidin = StringUtils.Empty();
		try {
			
			comidin = String.valueOf(value);
			if(comidin.length() >= 4)
				comidin = String.valueOf(value).substring(0,4);
			else {
				comidin = String.valueOf(value);
			}
			
			switch (comidin) {
			case "com.":
				
				if(String.valueOf(value).contains("wIso8583") && String.valueOf(value).contains("ISO_")) {
					if(String.valueOf(value).contains("[")){
						
						String IsoRow =  String.valueOf(value).split("\\[")[0];
						String valorIso = (String) getIsoValueOf(String.valueOf(IsoRow), iso);
						String partIso = valorIso.split("\\" +String.valueOf(value).split("\\[")[1].substring(0, 1))
								         [Integer.parseInt(String.valueOf(value).split("\\[")[1].substring(1, 2))];
						obj = partIso;
					}
					else
						obj = getIsoValueOf(String.valueOf(value), iso);
					
				}else{
					
					String [] classAndMethod = String.valueOf(value).split("\\|");
					String [] params = classAndMethod[1].split("\\,");
					
					String messageClass = classAndMethod[0];				
					List<String> aa = Arrays.asList(messageClass.split("\\."));
					String methodName = aa.get(aa.size() -1);
					String classname = messageClass.replace("." + methodName, "");
					
					Class<?> instanceClass = Class.forName(classname);																			
					Object classInstance = instanceClass.getConstructor(new Class[]{}).newInstance();					
					Method methodToInvoke = instanceClass.getMethod(methodName, Object.class);					
					obj =  methodToInvoke.invoke(classInstance,(Object[]) params);
				}
				break;
			default:
				obj = value;
				break;
			}
				
		} catch (Exception e) {
			log = new Logger();
			log.WriteLogMonitor("Error modulo EngineProcessorTransactionsBackGround::getValue ", TypeMonitor.error, e);
			e.printStackTrace();
		}
		return obj;
	}
	
    protected static Object getIsoValueOf(String IsoFieldClass, Iso8583 objIso){
		
		Logger log = null;
		
		try {
								
			Object obj = null;
			List<String> clase = Arrays.asList(IsoFieldClass.split("\\."));
			String nomClass = IsoFieldClass.replace("." + clase.get(clase.size() - 1),"");
			Class<?> isoClass = Class.forName(nomClass);
			
			obj = getField(isoClass,  clase.get(clase.size() - 1), objIso);
			
			if(obj instanceof Date){
				String fechaFormat = null;
				Date fec = (Date)obj;
				if(clase.get(clase.size() - 1).equalsIgnoreCase("ISO_015_SettlementDatel"))
				  if(objIso.getISO_003_ProcessingCode().startsWith("81") && objIso.getISO_024_NetworkId().equals("555541"))
					  fechaFormat = FormatUtils.DateToString(fec, "yyyy-MM-dd HH-mm-ss");
				  else
					  fechaFormat = FormatUtils.DateToString(fec, "yyyy-MM-dd");
				else	
					fechaFormat = FormatUtils.DateToString(fec, "yyyy-MM-dd HH:mm:ss");
				obj = fechaFormat;
			}
			return obj;
			
		} catch (Exception e) {
			log = new Logger();
			log.WriteLogMonitor("Error modulo EngineProcessorTransactionsBackGround::getIsoValueOf ", TypeMonitor.error, e);
			return null;
		}
	}
    
    protected static Object getField(Class<?> clazz, String fieldName, Object obj) {
	    Class<?> tmpClass = clazz;
	    do {
	        try {
	            Field f = tmpClass.getDeclaredField(fieldName);
	            f.setAccessible(true);
	            return f.get(obj);
	        } catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException e) {
	            tmpClass = tmpClass.getSuperclass();
	        }
	    } while (tmpClass != null);
	    throw new RuntimeException("Campo '" + fieldName + "' no se encuentra en la clase: " + clazz);
	}
	
    protected wIso8583 addRowsIsoToParentTransaction(List<TransactionNotifications> listTrxNotif, wIso8583 iso, int counter) {
    	
    	try {
			
    		for (TransactionNotifications txnot : listTrxNotif) {
				
    			if(!(StringUtils.IsNullOrEmpty(txnot.getParams1()) && StringUtils.IsNullOrEmpty(txnot.getParams2()))) {
    				
    				Class<?> isoClass = iso.getClass();
        			java.lang.reflect.Field isoField = isoClass.getSuperclass().getDeclaredField(txnot.getParams1());
        			isoField.setAccessible(true);
        			int indice = counter;//Integer.parseInt(Arrays.asList(txnot.getParams2().split("\\-")).get(1));
        			String[] valor = (String[]) GeneralUtils.getValue(Arrays.asList(txnot.getParams2().split("\\-")).get(0),iso);
        			isoField.set(iso, (Object)valor[indice]);
        			break;
    			}
			}
    		
		} catch (Exception e) {
			log.WriteLogMonitor("Error modulo EngineProcessorTransactionsBackGround::addRowsIsoToParentTransaction Proccode:" + iso.getISO_003_ProcessingCode(), TypeMonitor.error, e);
		}
    	return iso;
    }
    
    public wIso8583 addIsoAdditionalRows(Ref<wIso8583> isoReference){
    	
    	try {
			
    		wIso8583 iso = isoReference.get();
    		AdditionalIsoValues addIsoObject = new AdditionalIsoValues();
    		List<AdditionalIsoValues> list = addIsoObject.getAdditionalIsoValuesListObject(iso.getISO_003_ProcessingCode()
    								  , Integer.parseInt(iso.getISO_024_NetworkId()), iso.getISO_018_MerchantType()
    								  , -1);
    		for (AdditionalIsoValues additionalIsoValues : list) {
    			
    			Class<?> isoClass = iso.getClass();
    			java.lang.reflect.Field isoField = isoClass.getSuperclass().getDeclaredField(additionalIsoValues.getIso_row());
    			isoField.setAccessible(true);
    			isoField.set(iso, additionalIsoValues.getIso_value());
			}
    		
    		isoReference.set(iso);
    		
		} catch (Exception e) {
			
			log.WriteLogMonitor("Error modulo EngineProcessorTransactionsBackGround::addIsoAdditionalRows ", TypeMonitor.error, e);
		}
    	return isoReference.get();
    }
    
    Runnable runProcessorBackGround() {
    	
    	Runnable r = new Runnable() {
			
			@Override
			public void run() {
				
				ExecuteTrxsInBackGround(wiso);
			}
		};
		return r;
    }
}
