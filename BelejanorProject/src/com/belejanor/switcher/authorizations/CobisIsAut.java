package com.belejanor.switcher.authorizations;

import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import com.belejanor.switcher.cscoreswitch.AdminConcurrentMap;
import com.belejanor.switcher.cscoreswitch.TransactionConfiguration;
import com.belejanor.switcher.cscoreswitch.wIso8583;
import com.belejanor.switcher.implementations.AutorizationTransactions;
import com.belejanor.switcher.logger.Logger;
import com.belejanor.switcher.logger.LoggerConfig.TypeMonitor;
import com.belejanor.switcher.memcached.ActionCodeTable;
import com.belejanor.switcher.memcached.MemoryGlobal;
import com.belejanor.switcher.sqlservices.IsoRetrievalTransaction;
import com.belejanor.switcher.sqlservices.IsoSqlMaintenance;
import com.belejanor.switcher.sqlservices.typeBDD;
import com.belejanor.switcher.utils.FormatUtils;
import com.belejanor.switcher.utils.GeneralUtils;
import com.belejanor.switcher.utils.StringUtils;

public class CobisIsAut {

	private Logger log;
	
	public CobisIsAut() {
		log = new Logger();
	}
	
	@SuppressWarnings("null")
	protected ResultSet CobisCoreProcessor(wIso8583 iso, String methodName) {
		
		final ResultCobis resultCobis = null;
		ResultSet rs = null;
		try {
			
			Class<?> instanceClass = AutorizationTransactions.class;																			
			Object classInstance = instanceClass.getConstructor(new Class[]{}).newInstance();					
			Method methodToInvoke = instanceClass.getMethod(methodName, wIso8583.class);	
			final CountDownLatch semaphore = new CountDownLatch(1);
	    	
	    	iso.getTickAut().reset();
			iso.getTickAut().start();
	    	//Void
			methodToInvoke.invoke(classInstance, iso);
			Thread t = new Thread(new Runnable() {
				@Override
				public void run() {
					
					while (true) {
						synchronized(MemoryGlobal.concurrentIso) {
							if(MemoryGlobal.concurrentIso.containsKey(iso.getISO_011_SysAuditNumber())){
								resultCobis.setRs((ResultSet) MemoryGlobal.concurrentIso.get(iso.getISO_011_SysAuditNumber()));
								@SuppressWarnings("unused")
								AdminConcurrentMap map = new AdminConcurrentMap(iso.getISO_011_SysAuditNumber());
								semaphore.countDown();
								break;
							}
						}
					}
				}
			});
			t.start();
        	
			if(!semaphore.await(iso.getWsTransactionConfig().getProccodeTimeOutValue(), TimeUnit.MILLISECONDS)) {
				
				iso.setISO_039_ResponseCode("907");
				iso.setISO_039p_ResponseDetail("AUTORIZADOR COBIS NO DISPONIBLE");
			}
			else {
				
				rs = resultCobis.getRs();
			}
			
			
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		return rs;
	}
	
	public wIso8583 ExecuteDebitCobis(wIso8583 iso) {
		
		IsoRetrievalTransaction sql = null;
		try {
			
			final ResulIsoCobis result = new ResulIsoCobis();
			sql = new IsoRetrievalTransaction();
			final IsoRetrievalTransaction callerSql = sql;
			final CountDownLatch semaphore = new CountDownLatch(1);
			iso.getTickAut().reset();
			iso.getTickAut().start();
			Thread trDebit = new Thread(new Runnable() {
				
				public void run() {
					try {
						result.setIso(callerSql.debitCobisAuth(iso));
						semaphore.countDown();
						if(iso.getTickAut().isStarted())
							iso.getTickAut().stop();
						
					} catch (Exception e) {
						
						if(iso.getTickAut().isStarted())
							iso.getTickAut().stop();
						iso.setISO_039_ResponseCode("909");
						iso.setISO_039p_ResponseDetail("ERROR EN PROCESOS, " + GeneralUtils.ExceptionToString(null, e, false));
						iso.setISO_104_TranDescription("ERROR AL RECUPERAR RESPUESTA COBIS DEBITO, RUNNABLE INTERRUPTED EXCEPTION");
						semaphore.countDown();
					}
				}
			});
			
			trDebit.start();
			
			if(!semaphore.await(iso.getWsTransactionConfig().
					 getProccodeTimeOutValue(), TimeUnit.MILLISECONDS)){
				
				iso.setISO_039_ResponseCode("907");
				iso.setISO_039p_ResponseDetail("TRANSACCION EXPIRADA, TIMEOUT COBIS DEBITO");
				iso.setISO_104_TranDescription("HA EXPIRADO EL TIEMPO DE ESERA DE BELEJANOR-FINANSWITCH PARA ESTA OPERACION");
				iso.setWsIso_LogStatus(9);
				semaphore.countDown();
				
			}else {
				
				if(result.getIso() != null) {
					
					iso.setISO_039_ResponseCode(result.getIso().getISO_039_ResponseCode());
					iso.setISO_039p_ResponseDetail(result.getIso().getISO_039p_ResponseDetail());
					
				}else {
					
					iso.setISO_039_ResponseCode("301");
					iso.setISO_039p_ResponseDetail("PROBLEMAS AL RECUPERAR INFORMACION RESULTANTE COBIS DEBITO");
				}
				iso.setWsIso_LogStatus(2);
			}
			
			
		} catch (Exception e) {
		
			if(iso.getTickAut().isStarted())
				iso.getTickAut().stop();
			iso.setISO_039_ResponseCode("909");
			iso.setISO_039p_ResponseDetail("ERROR EN PROCESOS " + GeneralUtils.ExceptionToString(null, e, false));
			log.WriteLogMonitor("Error modulo CobisIsAut::ExecuteDebitCobis ", 
				     TypeMonitor.error, e);
		}finally {
			
			if(!iso.getISO_039_ResponseCode().equals("000")) {
				String [] errorParse =  ParseErrorsCodeIsoToCobis(iso);
				iso.setISO_039_ResponseCode(errorParse[0]);
				iso.setISO_039p_ResponseDetail(iso.getISO_039_ResponseCode() + " - " + 
													iso.getISO_039p_ResponseDetail());
			}
				
			iso.setWsTempAut((iso.getTickAut().getTime(TimeUnit.MILLISECONDS)/1000.0));
		}
		
		return iso;
	}
	
	public wIso8583 executeDebitCobisWithServicePay(wIso8583 iso) {
		
		try {
			iso.getTickAut().reset();
			iso.getTickAut().start();
				iso = ExecuteDebitCobis(iso);
			if(iso.getTickAut().isStarted())
				iso.getTickAut().stop();
			
			if(!iso.getISO_039_ResponseCode().equals("000")) {
				/*Recupero el XML_RUBRO*/
				iso.setISO_115_ExtendedData(iso.getISO_124_ExtendedData());
				iso.setISO_BitMap("REVER_DIRECT");
				iso.setWsISO_FlagStoreForward(true);
			}
			
			
		} catch (Exception e) {
			if(iso.getTickAut().isStarted())
				iso.getTickAut().stop();
			log.WriteLogMonitor("Error modulo CobisIsAut::executeDebitCobisWithServicePay ", 
				     TypeMonitor.error, e);
		}finally {
			
			iso.setWsTempAut((iso.getTickAut().getTime(TimeUnit.MILLISECONDS)/1000.0));
		}
		return iso;
	}
	
    public wIso8583 NotaCreditoDebitoBimo(wIso8583 iso) {
		
		IsoSqlMaintenance sql = null;
		try {
			//NO CORRESPONDE A NUESTRA INSTITUCION
			final ResulIsoCobis result = new ResulIsoCobis();
			sql = new IsoSqlMaintenance();
			final IsoSqlMaintenance callerSql = sql;
			final CountDownLatch semaphore = new CountDownLatch(1);
			iso.getTickAut().reset();
			iso.getTickAut().start();
			Thread trDebit = new Thread(new Runnable() {
				
				public void run() {
					try {
						 if(iso.getISO_000_Message_Type().startsWith("12"))
							result.setIso(callerSql.creditDebitCobisBimo(iso, typeBDD.Sybase));
						 else if (iso.getISO_000_Message_Type().startsWith("14")) {
							result.setIso(ProcessReverseCobis(iso));
						}	
						semaphore.countDown();
						if(iso.getTickAut().isStarted())
							iso.getTickAut().stop();
						
					} catch (Exception e) {
						
						if(iso.getTickAut().isStarted())
							iso.getTickAut().stop();
						iso.setISO_039_ResponseCode("909");
						iso.setISO_039p_ResponseDetail("ERROR EN PROCESOS, " + GeneralUtils.ExceptionToString(null, e, false));
						iso.setISO_104_TranDescription("ERROR AL RECUPERAR RESPUESTA COBIS DEBITO, RUNNABLE INTERRUPTED EXCEPTION");
						semaphore.countDown();
					}
				}
			});
			
			trDebit.start();
			
			if(!semaphore.await(iso.getWsTransactionConfig().
					 getProccodeTimeOutValue(), TimeUnit.MILLISECONDS)){
				
				iso.setISO_039_ResponseCode("907");
				iso.setISO_039p_ResponseDetail("TRANSACCION EXPIRADA, TIMEOUT COBIS DEBITO/CREDITO");
				iso.setISO_104_TranDescription("HA EXPIRADO EL TIEMPO DE ESERA DE BELEJANOR-FINANSWITCH PARA ESTA OPERACION");
				iso.setWsIso_LogStatus(9);
				semaphore.countDown();
				
			}else {
				
				if(result.getIso() != null) {
					
					iso.setISO_039_ResponseCode(result.getIso().getISO_039_ResponseCode());
					iso.setISO_039p_ResponseDetail(result.getIso().getISO_039p_ResponseDetail());
					
				}else {
					
					iso.setISO_039_ResponseCode("301");
					iso.setISO_039p_ResponseDetail("PROBLEMAS AL RECUPERAR INFORMACION RESULTANTE COBIS DEBITO");
				}
				iso.setWsIso_LogStatus(2);
			}
			
			
		} catch (Exception e) {
		
			if(iso.getTickAut().isStarted())
				iso.getTickAut().stop();
			iso.setISO_039_ResponseCode("909");
			iso.setISO_039p_ResponseDetail("ERROR EN PROCESOS " + GeneralUtils.ExceptionToString(null, e, false));
			log.WriteLogMonitor("Error modulo CobisIsAut::NotaCreditoDebitoBimo ", 
				     TypeMonitor.error, e);
		}finally {
			
			if(!iso.getISO_039_ResponseCode().equals("000")) {
				String [] errorParse =  ParseErrorsCodeIsoToCobis(iso);
				iso.setISO_039_ResponseCode(errorParse[0]);
				iso.setISO_039p_ResponseDetail(iso.getISO_039_ResponseCode() + " - " + 
													iso.getISO_039p_ResponseDetail());
			}
				
			iso.setWsTempAut((iso.getTickAut().getTime(TimeUnit.MILLISECONDS)/1000.0));
		}
		
		return iso;
	}
   
    protected wIso8583 ProcessReverseCobis(wIso8583 iso) {
    	
    	IsoRetrievalTransaction sql = null;
    	IsoSqlMaintenance man = null;
    	
    	try {
    		wIso8583 isoRever = (wIso8583) iso.clone();
    		isoRever.setWsTransactionConfig((TransactionConfiguration)iso.getWsTransactionConfig().CloneObject());
			sql = new IsoRetrievalTransaction();
    		isoRever = sql.RetrieveTransactionIso(isoRever, 
    				isoRever.getWsTransactionConfig().getProccodeReverFlag());
    		if(isoRever.getISO_039_ResponseCode().equals("000")) {
    			isoRever.setWsTransactionConfig((TransactionConfiguration)iso.getWsTransactionConfig().CloneObject());
    			man = new IsoSqlMaintenance();
    			iso = man.ReverCreditDebitCobisBimo(isoRever, typeBDD.Sybase);
    			iso.setWsIso_LogStatus(2);
    		}
    		iso.setISO_039_ResponseCode(isoRever.getISO_039_ResponseCode());
    		iso.setISO_039p_ResponseDetail(isoRever.getISO_039p_ResponseDetail());	
    		
		} catch (Exception e) {
			
			iso.setISO_039_ResponseCode("909");
			iso.setISO_039p_ResponseDetail("ERROR EN PROCESOS " + GeneralUtils.ExceptionToString(null, e, false));
			log.WriteLogMonitor("Error modulo CobisIsAut::ProcessReverseCobis ", 
				     TypeMonitor.error, e);
			
		}
    	return iso;
    }
    
    public wIso8583 QueryBalanceExe(wIso8583 iso) {
		
		IsoSqlMaintenance sql = null;
		try {

			final ResulIsoCobis result = new ResulIsoCobis();
			sql = new IsoSqlMaintenance();
			final IsoSqlMaintenance callerSql = sql;
			final CountDownLatch semaphore = new CountDownLatch(1);
			iso.getTickAut().reset();
			iso.getTickAut().start();
			Thread trDebit = new Thread(new Runnable() {
				
				public void run() {
					try {
						result.setIso(callerSql.balanceCobisBimo(iso, typeBDD.Sybase));
						semaphore.countDown();
						if(iso.getTickAut().isStarted())
							iso.getTickAut().stop();
						
					} catch (Exception e) {
						
						if(iso.getTickAut().isStarted())
							iso.getTickAut().stop();
						iso.setISO_039_ResponseCode("909");
						iso.setISO_039p_ResponseDetail("ERROR EN PROCESOS, " + GeneralUtils.ExceptionToString(null, e, false));
						iso.setISO_104_TranDescription("ERROR AL RECUPERAR RESPUESTA COBIS BALANCE, RUNNABLE INTERRUPTED EXCEPTION");
						semaphore.countDown();
					}
				}
			});
			
			trDebit.start();
			
			if(!semaphore.await(iso.getWsTransactionConfig().
					 getProccodeTimeOutValue(), TimeUnit.MILLISECONDS)){
				
				iso.setISO_039_ResponseCode("907");
				iso.setISO_039p_ResponseDetail("TRANSACCION EXPIRADA, TIMEOUT COBIS BALANCE");
				iso.setISO_104_TranDescription("HA EXPIRADO EL TIEMPO DE ESERA DE BELEJANOR-FINANSWITCH PARA ESTA OPERACION");
				iso.setWsIso_LogStatus(9);
				semaphore.countDown();
				
			}else {
				
				if(result.getIso() != null) {
					
					iso.setISO_039_ResponseCode(result.getIso().getISO_039_ResponseCode());
					iso.setISO_039p_ResponseDetail(result.getIso().getISO_039p_ResponseDetail());
					
				}else {
					
					iso.setISO_039_ResponseCode("301");
					iso.setISO_039p_ResponseDetail("PROBLEMAS AL RECUPERAR INFORMACION RESULTANTE COBIS DEBITO");
				}
				iso.setWsIso_LogStatus(2);
			}
			
			
		} catch (Exception e) {
		
			if(iso.getTickAut().isStarted())
				iso.getTickAut().stop();
			iso.setISO_039_ResponseCode("909");
			iso.setISO_039p_ResponseDetail("ERROR EN PROCESOS " + GeneralUtils.ExceptionToString(null, e, false));
			log.WriteLogMonitor("Error modulo CobisIsAut::QueryBalanceExe ", 
				     TypeMonitor.error, e);
		}finally {
			
			if(!iso.getISO_039_ResponseCode().equals("000")) {
				String [] errorParse =  ParseErrorsCodeIsoToCobis(iso);
				iso.setISO_039_ResponseCode(errorParse[0]);
				iso.setISO_039p_ResponseDetail(iso.getISO_039_ResponseCode() + " - " + 
													iso.getISO_039p_ResponseDetail());
			}
				
			iso.setWsTempAut((iso.getTickAut().getTime(TimeUnit.MILLISECONDS)/1000.0));
		}
		
		return iso;
	}
    
    public wIso8583 CreateFastPersonCobisExe(wIso8583 iso) {
    	
    	wIso8583 clonadowIso = null;
    	try {
			
    		iso = CreateFastPersonCobis(iso);
    		System.out.println(iso.getISO_039_ResponseCode() + "-" + iso.getISO_039p_ResponseDetail());
    		if(iso.getISO_039_ResponseCode().equals("001")) { //Cliente ya Existe
    			log = new Logger();
				log.WriteLogMonitor("===> Cliente ya existe, Se procede a Consultar Datos Persona.. ", TypeMonitor.monitor, null);
    			clonadowIso = (wIso8583) iso.clone();
        		clonadowIso.setWsTransactionConfig((TransactionConfiguration)iso.getWsTransactionConfig().CloneObject());
        		//Verifico si la persona tiene cuenta
        		clonadowIso = getDataEnroll(clonadowIso);
        		if(clonadowIso.getISO_039_ResponseCode().equals("001")) {//Ya tiene cuenta
        			iso.setISO_039_ResponseCode("998");
        			iso.setISO_039p_ResponseDetail("ACERCATE A UNA AGENCIA DE COOP. ANDALUCIA PARA AFILIARTE A BIMO");
        		}else if (clonadowIso.getISO_039_ResponseCode().equals("000") && 
        				  StringUtils.IsNullOrEmpty(iso.getISO_103_AccountID_2())) { //Existe la persona pero no tiene cuenta
        			if(!clonadowIso.getISO_124_ExtendedData().equals("0")) //Id persona no sea 0
        				iso.setISO_039_ResponseCode("000"); //Seteo para que vaya a crear Cuenta 
        			else {
        				iso.setISO_039_ResponseCode("909");
            			iso.setISO_039p_ResponseDetail("NO SE PUDO RECUPERAR ID CLIENTE DEVUELVE 0");
        			}
        				
				}
    		}
    		
		} catch (Exception e) {
			log.WriteLogMonitor("Error modulo CobisIsAut::CreateFastPersonFit1Exe ", 
				     TypeMonitor.error, e);
		}
    	return iso;
    }
    
    public wIso8583 CreateFastPersonCobis(wIso8583 iso) {
    	
    	IsoSqlMaintenance sql = null;
    	wIso8583 clonadowIso = null;
    	
		try {

			if(FormatUtils.getEdad("yyyy-MM-dd", iso.getISO_114_ExtendedData())[0] < 18){
	    		
    			iso.setISO_039_ResponseCode("522");
				iso.setISO_039p_ResponseDetail("NO SE PUEDE ENROLAR A UN MENOR DE EDAD");
				return iso;
    		}
			
			
			clonadowIso = (wIso8583) iso.clone();
    		clonadowIso.setWsTransactionConfig((TransactionConfiguration)iso.getWsTransactionConfig().CloneObject());
    		
    		final RequestIsoCobis request = new RequestIsoCobis();
    		request.setIso(clonadowIso);
    		
			final ResulIsoCobis result = new ResulIsoCobis();
			sql = new IsoSqlMaintenance();
			final IsoSqlMaintenance callerSql = sql;
			final CountDownLatch semaphore = new CountDownLatch(1);
			iso.getTickAut().reset();
			iso.getTickAut().start();
			Thread trDebit = new Thread(new Runnable() {
				
				public void run() {
					try {
						result.setIso(callerSql.createFastPersonCobisBimo(request.getIso(), typeBDD.Sybase));
						semaphore.countDown();
						if(iso.getTickAut().isStarted())
							iso.getTickAut().stop();
						
					} catch (Exception e) {
						
						if(iso.getTickAut().isStarted())
							iso.getTickAut().stop();
						iso.setISO_039_ResponseCode("909");
						iso.setISO_039p_ResponseDetail("ERROR EN PROCESOS, " + GeneralUtils.ExceptionToString(null, e, false));
						iso.setISO_104_TranDescription("ERROR AL RECUPERAR RESPUESTA COBIS CREATE PERSON, RUNNABLE INTERRUPTED EXCEPTION");
						semaphore.countDown();
					}
				}
			});
			
			trDebit.start();
			
			if(!semaphore.await(iso.getWsTransactionConfig().
					 getProccodeTimeOutValue(), TimeUnit.MILLISECONDS)){
				
				iso.setISO_039_ResponseCode("907");
				iso.setISO_039p_ResponseDetail("TRANSACCION EXPIRADA, TIMEOUT COBIS CREATE PERSON");
				iso.setISO_104_TranDescription("HA EXPIRADO EL TIEMPO DE ESERA DE BELEJANOR-FINANSWITCH PARA ESTA OPERACION");
				iso.setWsIso_LogStatus(9);
				semaphore.countDown();
				
			}else {
				
				if(result.getIso() != null) {
					
					iso.setISO_039_ResponseCode(result.getIso().getISO_039_ResponseCode());
					iso.setISO_039p_ResponseDetail(result.getIso().getISO_039p_ResponseDetail());
					iso.setISO_124_ExtendedData(result.getIso().getISO_124_ExtendedData()); //Codigo Persona
					
				}else {
					
					iso.setISO_039_ResponseCode("301");
					iso.setISO_039p_ResponseDetail("PROBLEMAS AL RECUPERAR INFORMACION RESULTANTE COBIS CREATE PERSON");
				}
				iso.setWsIso_LogStatus(2);
			}
			
			
		} catch (Exception e) {
		
			if(iso.getTickAut().isStarted())
				iso.getTickAut().stop();
			iso.setISO_039_ResponseCode("909");
			iso.setISO_039p_ResponseDetail("ERROR EN PROCESOS " + GeneralUtils.ExceptionToString(null, e, false));
			log.WriteLogMonitor("Error modulo CobisIsAut::CreateFastPersonCobisExe ", 
				     TypeMonitor.error, e);
		}finally {
			
			if(!iso.getISO_039_ResponseCode().equals("000")) {
				String [] errorParse =  ParseErrorsCodeIsoToCobis(iso);
				iso.setISO_039_ResponseCode(errorParse[0]);
				iso.setISO_039p_ResponseDetail(iso.getISO_039_ResponseCode() + " - " + 
													iso.getISO_039p_ResponseDetail());
			}
				
			iso.setWsTempAut((iso.getTickAut().getTime(TimeUnit.MILLISECONDS)/1000.0));
		}
		
		return iso;
    }
    
    public wIso8583 getDataEnroll(wIso8583 iso) {
		
		IsoSqlMaintenance sql = null;
		try {

			final ResulIsoCobis result = new ResulIsoCobis();
			sql = new IsoSqlMaintenance();
			final IsoSqlMaintenance callerSql = sql;
			final CountDownLatch semaphore = new CountDownLatch(1);
			iso.getTickAut().reset();
			iso.getTickAut().start();
			Thread trDebit = new Thread(new Runnable() {
				
				public void run() {
					try {
						result.setIso(callerSql.getDataEnrollCobisBimo(iso, typeBDD.Sybase));
						semaphore.countDown();
						if(iso.getTickAut().isStarted())
							iso.getTickAut().stop();
						
					} catch (Exception e) {
						
						if(iso.getTickAut().isStarted())
							iso.getTickAut().stop();
						iso.setISO_039_ResponseCode("909");
						iso.setISO_039p_ResponseDetail("ERROR EN PROCESOS, " + GeneralUtils.ExceptionToString(null, e, false));
						iso.setISO_104_TranDescription("ERROR AL RECUPERAR RESPUESTA COBIS GET DATA ENROLL, RUNNABLE INTERRUPTED EXCEPTION");
						semaphore.countDown();
					}
				}
			});
			
			trDebit.start();
			
			if(!semaphore.await(iso.getWsTransactionConfig().
					 getProccodeTimeOutValue(), TimeUnit.MILLISECONDS)){
				
				iso.setISO_039_ResponseCode("907");
				iso.setISO_039p_ResponseDetail("TRANSACCION EXPIRADA, TIMEOUT COBIS GET DATA ENROLL");
				iso.setISO_104_TranDescription("HA EXPIRADO EL TIEMPO DE ESERA DE BELEJANOR-FINANSWITCH PARA ESTA OPERACION");
				iso.setWsIso_LogStatus(9);
				semaphore.countDown();
				
			}else {
				
				if(result.getIso() != null) {
					
					iso.setISO_039_ResponseCode(result.getIso().getISO_039_ResponseCode());
					iso.setISO_039p_ResponseDetail(result.getIso().getISO_039p_ResponseDetail());
					
				}else {
					
					iso.setISO_039_ResponseCode("301");
					iso.setISO_039p_ResponseDetail("PROBLEMAS AL RECUPERAR INFORMACION RESULTANTE COBIS GET DATA ENROLL");
				}
				iso.setWsIso_LogStatus(2);
			}
			
			
		} catch (Exception e) {
		
			if(iso.getTickAut().isStarted())
				iso.getTickAut().stop();
			iso.setISO_039_ResponseCode("909");
			iso.setISO_039p_ResponseDetail("ERROR EN PROCESOS " + GeneralUtils.ExceptionToString(null, e, false));
			log.WriteLogMonitor("Error modulo CobisIsAut::getDataEnroll ", 
				     TypeMonitor.error, e);
		}finally {
			
			if(!iso.getISO_039_ResponseCode().equals("000")) {
				String [] errorParse =  ParseErrorsCodeIsoToCobis(iso);
				iso.setISO_039_ResponseCode(errorParse[0]);
				iso.setISO_039p_ResponseDetail(iso.getISO_039_ResponseCode() + " - " + 
													iso.getISO_039p_ResponseDetail());
			}
				
			iso.setWsTempAut((iso.getTickAut().getTime(TimeUnit.MILLISECONDS)/1000.0));
		}
		
		return iso;
	}
    
    public wIso8583 CreateBasicAccountCobisExe(wIso8583 iso) {
		
		IsoSqlMaintenance sql = null;
		try {

			final ResulIsoCobis result = new ResulIsoCobis();
			sql = new IsoSqlMaintenance();
			final IsoSqlMaintenance callerSql = sql;
			final CountDownLatch semaphore = new CountDownLatch(1);
			iso.getTickAut().reset();
			iso.getTickAut().start();
			Thread trDebit = new Thread(new Runnable() {
				
				public void run() {
					try {
						result.setIso(callerSql.createAccountCobisBimo(iso, typeBDD.Sybase));
						semaphore.countDown();
						if(iso.getTickAut().isStarted())
							iso.getTickAut().stop();
						
					} catch (Exception e) {
						
						if(iso.getTickAut().isStarted())
							iso.getTickAut().stop();
						iso.setISO_039_ResponseCode("909");
						iso.setISO_039p_ResponseDetail("ERROR EN PROCESOS, " + GeneralUtils.ExceptionToString(null, e, false));
						iso.setISO_104_TranDescription("ERROR AL RECUPERAR RESPUESTA CREATE ACCOUNT COBIS, RUNNABLE INTERRUPTED EXCEPTION");
						semaphore.countDown();
					}
				}
			});
			
			trDebit.start();
			
			if(!semaphore.await(iso.getWsTransactionConfig().
					 getProccodeTimeOutValue(), TimeUnit.MILLISECONDS)){
				
				iso.setISO_039_ResponseCode("907");
				iso.setISO_039p_ResponseDetail("TRANSACCION EXPIRADA, TIMEOUT CREATE ACCOUNT COBIS");
				iso.setISO_104_TranDescription("HA EXPIRADO EL TIEMPO DE ESERA DE BELEJANOR-FINANSWITCH PARA ESTA OPERACION");
				iso.setWsIso_LogStatus(9);
				semaphore.countDown();
				
			}else {
				
				if(result.getIso() != null) {
					
					iso.setISO_039_ResponseCode(result.getIso().getISO_039_ResponseCode());
					iso.setISO_039p_ResponseDetail(result.getIso().getISO_039p_ResponseDetail());
					
					if(iso.getISO_039_ResponseCode().equals("000")) {
						
						if(!StringUtils.IsNullOrEmpty(iso.getISO_103_AccountID_2())){
		    				
							/*Seteo los campos CC y 239*/
							iso.setISO_019_AcqCountryCode(iso.getISO_019_AcqCountryCode().equals("239")?"EC":"EC");
							iso.setISO_022_PosEntryMode(iso.getISO_022_PosEntryMode().equals("CC")?"CED":"PAS");
							/*-------------------------*/
							
		    				String acum = StringUtils.Empty();
		    				String[] arrayCta = iso.getISO_102_AccountID_1().split("\\|");
		    				arrayCta[1] = "40"; //40 Cuenta Basica
		    				arrayCta[3] = iso.getISO_103_AccountID_2();
		    				for (String string : arrayCta) {
								
		    					acum+=string + "|";
							}
		    				iso.setISO_124_ExtendedData(StringUtils.trimEnd(acum, "|"));
		    				//Vuelvo a acomodar 102 Account para grabar en tabla de enrolamientos internos
		    				iso.setISO_102_AccountID_1(Arrays.asList(iso.getISO_124_ExtendedData().split("\\|")).get(3));
		    				
		    			}else{
		    				
		    				iso.setISO_039_ResponseCode("100");
		        			iso.setISO_039p_ResponseDetail("COBIS HA RETORNADO PARAMETRO NULO EN CUENTA");
		    			}
					}
					
				}else {
					
					iso.setISO_039_ResponseCode("301");
					iso.setISO_039p_ResponseDetail("PROBLEMAS AL RECUPERAR INFORMACION RESULTANTE CREATE ACCOUNT COBIS");
				}
				iso.setWsIso_LogStatus(2);
			}
			
			
		} catch (Exception e) {
		
			if(iso.getTickAut().isStarted())
				iso.getTickAut().stop();
			iso.setISO_039_ResponseCode("909");
			iso.setISO_039p_ResponseDetail("ERROR EN PROCESOS " + GeneralUtils.ExceptionToString(null, e, false));
			log.WriteLogMonitor("Error modulo CobisIsAut::CreateBasicAccountCobisExe ", 
				     TypeMonitor.error, e);
		}finally {
			
			if(!iso.getISO_039_ResponseCode().equals("000")) {
				String [] errorParse =  ParseErrorsCodeIsoToCobis(iso);
				iso.setISO_039_ResponseCode(errorParse[0]);
				iso.setISO_039p_ResponseDetail(iso.getISO_039_ResponseCode() + " - " + 
													iso.getISO_039p_ResponseDetail());
			}
				
			iso.setWsTempAut((iso.getTickAut().getTime(TimeUnit.MILLISECONDS)/1000.0));
		}
		
		return iso;
	}
    
    public wIso8583 ExecuteSaldosCobis(wIso8583 iso) {
		
		IsoRetrievalTransaction sql = null;
		try {
			
			final ResulIsoCobis result = new ResulIsoCobis();
			sql = new IsoRetrievalTransaction();
			final IsoRetrievalTransaction callerSql = sql;
			final CountDownLatch semaphore = new CountDownLatch(1);
			iso.getTickAut().reset();
			iso.getTickAut().start();
			Thread trDebit = new Thread(new Runnable() {
				
				public void run() {
					try {
						result.setIso(callerSql.savingAccountCobisAuth(iso));
						semaphore.countDown();
						if(iso.getTickAut().isStarted())
							iso.getTickAut().stop();
						
					} catch (Exception e) {
						
						if(iso.getTickAut().isStarted())
							iso.getTickAut().stop();
						iso.setISO_039_ResponseCode("909");
						iso.setISO_039p_ResponseDetail("ERROR EN PROCESOS, " + GeneralUtils.ExceptionToString(null, e, false));
						iso.setISO_104_TranDescription("ERROR AL RECUPERAR RESPUESTA COBIS DEBITO, RUNNABLE INTERRUPTED EXCEPTION");
						semaphore.countDown();
					}
				}
			});
			
			trDebit.start();
			
			if(!semaphore.await(iso.getWsTransactionConfig().
					 getProccodeTimeOutValue(), TimeUnit.MILLISECONDS)){
				
				iso.setISO_039_ResponseCode("907");
				iso.setISO_039p_ResponseDetail("TRANSACCION EXPIRADA, TIMEOUT COBIS SALDOS");
				iso.setISO_104_TranDescription("HA EXPIRADO EL TIEMPO DE ESERA DE BELEJANOR-FINANSWITCH PARA ESTA OPERACION");
				iso.setWsIso_LogStatus(9);
				
			}else {
				
				if(result.getIso() != null) {
					
					iso.setISO_039_ResponseCode(result.getIso().getISO_039_ResponseCode());
					iso.setISO_039p_ResponseDetail(result.getIso().getISO_039p_ResponseDetail());
					
				}else {
					
					iso.setISO_039_ResponseCode("301");
					iso.setISO_039p_ResponseDetail("PROBLEMAS AL RECUPERAR INFORMACION RESULTANTE COBIS SALDOS");
				}
				iso.setWsIso_LogStatus(2);
			}
			
			
		} catch (Exception e) {
		
			if(iso.getTickAut().isStarted())
				iso.getTickAut().stop();
			iso.setISO_039_ResponseCode("909");
			iso.setISO_039p_ResponseDetail("ERROR EN PROCESOS " + GeneralUtils.ExceptionToString(null, e, false));
			log.WriteLogMonitor("Error modulo CobisIsAut::ExecuteSaldosCobis ", 
				     TypeMonitor.error, e);
		}finally {
			
			if(!iso.getISO_039_ResponseCode().equals("000")) {
			
					String [] errorParse =  ParseErrorsCodeIsoToCobis(iso);
					iso.setISO_039_ResponseCode(errorParse[0]);
					iso.setISO_039p_ResponseDetail(iso.getISO_039_ResponseCode() + " - " + 
														iso.getISO_039p_ResponseDetail());
			}
				
			iso.setWsTempAut((iso.getTickAut().getTime(TimeUnit.MILLISECONDS)/1000.0));
		}
		
		return iso;
	}
    
    public wIso8583 validateSavingsAccount(wIso8583 iso) {
    	
    	try {
    		iso.getTickAut().reset();
			iso.getTickAut().start();
    			iso = ExecuteSaldosCobis(iso);
    		if(iso.getTickAut().isStarted())
    			iso.getTickAut().stop();
    		if(iso.getISO_039_ResponseCode().equals("000")) {
    			
    			double valorMinimoEnCuenta = (iso.getISO_004_AmountTransaction() + iso.getISO_008_BillFeeAmount() + MemoryGlobal.valorMinCta);
    			if(valorMinimoEnCuenta > iso.getISO_028_TranFeeAmount()) {
    				iso.setISO_039_ResponseCode("116");
    				iso.setISO_039p_ResponseDetail("LA CUENTA: " + iso.getISO_102_AccountID_1() + 
    						" NO POSEE FONDOS DISPONIBLES PARA REALIZAR ESTA TRANSACCION");
    			}
    		}
    		
		} catch (Exception e) {
			if(iso.getTickAut().isStarted())
    			iso.getTickAut().stop();
			log.WriteLogMonitor("Error modulo CobisIsAut::validateSavingsAccount ", 
				     TypeMonitor.error, e);
		}finally {
			
			iso.setWsTempAut((iso.getTickAut().getTime(TimeUnit.MILLISECONDS)/1000.0));
		}
    	return iso;
    }
    
    protected String[] ParseErrorsCodeIsoToCobis(wIso8583 iso){
		String[] data = new String[]{ "100","TRANSACCION NO PUEDE SER PROCESADA" };
		try {
			
			if(!iso.isResponseBelejanor()) {
				ActionCodeTable codTable = new ActionCodeTable();
				codTable = codTable.getCodErrorFromFitCodeList(iso.getISO_039_ResponseCode());
				if(codTable != null){
					data[0] = codTable.getAct_codIso();
					data[1] = codTable.getAct_desIso();
				}
			}else {
				
				data = new String[]{iso.getISO_039_ResponseCode(),iso.getISO_039p_ResponseDetail() };
			}
			
		} catch (Exception e) {
			log.WriteLogMonitor("Error modulo FitCoreProcessor::ParseErrorsCodeIsoToCobis ", TypeMonitor.error, e);
			e.printStackTrace();
		}
		return data;
	}
}

class ResultCobis{
	
	private ResultSet rs;

	public ResultSet getRs() {
		return rs;
	}

	public void setRs(ResultSet rs) {
		this.rs = rs;
	}
}
class ResulIsoCobis{
	
	private wIso8583 iso;

	public wIso8583 getIso() {
		return iso;
	}

	public void setIso(wIso8583 iso) {
		this.iso = iso;
	}
	
	
}

class RequestIsoCobis{
	
	private wIso8583 iso;

	public wIso8583 getIso() {
		return iso;
	}

	public void setIso(wIso8583 iso) {
		this.iso = iso;
	}
	
	
}
