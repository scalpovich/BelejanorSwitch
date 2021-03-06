package com.belejanor.switcher.authorizations;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import org.apache.commons.lang3.time.StopWatch;

import com.belejanor.switcher.cscoreswitch.EngineCallableProcessor;
import com.belejanor.switcher.cscoreswitch.Iso8583;
import com.belejanor.switcher.cscoreswitch.TransactionConfiguration;
import com.belejanor.switcher.cscoreswitch.csProcess;
import com.belejanor.switcher.cscoreswitch.wIso8583;
import com.belejanor.switcher.fit1struct.DetailFit1;
import com.belejanor.switcher.logger.Logger;
import com.belejanor.switcher.logger.LoggerConfig.TypeMonitor;
import com.belejanor.switcher.memcached.AdditionalIsoValues;
import com.belejanor.switcher.memcached.TransactionConfig;
import com.belejanor.switcher.sqlservices.IsoRetrievalTransaction;
import com.belejanor.switcher.utils.FormatUtils;
import com.belejanor.switcher.utils.GeneralUtils;
import com.belejanor.switcher.utils.Iterables;
import com.belejanor.switcher.utils.NumbersUtils;
import com.belejanor.switcher.utils.Ref;
import com.belejanor.switcher.utils.SerializationObject;
import com.belejanor.switcher.utils.StringUtils;
import com.fitbank.dto.management.Detail;
import com.fitbank.dto.management.Record;
import com.fitbank.dto.management.Table;

public class FitIsAut implements Callable<wIso8583>{
	
	private wIso8583 iso;
	private Logger log;
	
	public FitIsAut(){
		
	}
	public FitIsAut(wIso8583 iso){
		this.iso = iso;
		this.log = new Logger();
	}
	
	public wIso8583 getIso() {
		return iso;
	}
	public void setIso(wIso8583 iso) {
		this.iso = iso;
	}
	public wIso8583 ExecuteDebitTransaction(wIso8583 iso){
		
		iso.setISO_104_TranDescription("Debito ATM ");
		iso.setISO_120_ExtendedData("Esto es una prueba campo 120");
		FitCoreProcessor processor = new FitCoreProcessor(iso);
		@SuppressWarnings("unused")
		Detail response = processor.ProcessingTransactionFitCore();
		return iso;
	}
	
	public wIso8583 ExecuteValidateCtaTrx(wIso8583 iso){
		
		try {
			
			FitCoreProcessor processor = new FitCoreProcessor(iso);
			Detail response = processor.ProcessingTransactionFitCore();
			
			if(response != null){
				
			if(response.getResponse() != null){	
				if(response.getResponse().getCode().equals("0")){
					Table t = GeneralUtils.asStream(response.getTables().iterator(), true)
							  .filter(p -> p.getName().equalsIgnoreCase("TCUENTA"))
							  .findFirst().orElseGet(()-> null);
					
					if(t != null){
						
						Record r = GeneralUtils.asStream(t.getRecords().iterator(), true)
								   .filter(p -> p.getNumber() == 0)
								   .findFirst().orElseGet(()-> null);
						
						boolean valor = true;
						if(r != null){
							
							valor = GeneralUtils.asStream(r.getFields().iterator(), true)
									  .filter(g -> g.getValue() != null)
									  .noneMatch(g -> g.getValue().equals(iso.getISO_102_AccountID_1()));
							
								if(!valor ){
									iso.setISO_039_ResponseCode("000");
									iso.setISO_039p_ResponseDetail("TRANSACCION EXITOSA");
									return iso;
								}
						}
					}
					iso.setISO_039_ResponseCode("214");
					iso.setISO_039p_ResponseDetail("CUENTA " + iso.getISO_102_AccountID_1() + " NO EXISTE");
				}
			  }
			}
				
		} catch (Exception e) {
			
			iso.setISO_039_ResponseCode("909");
			iso.setISO_039p_ResponseDetail(GeneralUtils.ExceptionToString("ERROR EN PROCESOS ", e, false));	
		}
		return iso;
	}
	
	public wIso8583 ExecuteValidateCtaTrxFit1_PDE(wIso8583 iso){
		
		try {
			
			IsoRetrievalTransaction sql = new IsoRetrievalTransaction();
			wIso8583 isoQry = (wIso8583) iso.clone();
			iso.getTickAut().reset();
			iso.getTickAut().start();
			isoQry = sql.getValidateAccountFit1_PDE(isoQry);
			if(iso.getTickAut().isStarted())
				iso.getTickAut().stop();
			if(isoQry.getISO_039_ResponseCode().equals("000")){
				
				String prefijo = iso.getISO_124_ExtendedData().equalsIgnoreCase("CA")?"AHORROS":"CORRIENTE";
				
				if(isoQry.getISO_120_ExtendedData().equalsIgnoreCase(prefijo)){
					
					if(isoQry.getISO_121_ExtendedData().equals("002")){
						
						iso.setISO_039_ResponseCode("000");
						iso.setISO_039p_ResponseDetail("TRANSACCION EXITOSA");
						
					}else{
						
						iso.setISO_039_ResponseCode("214");
						iso.setISO_039p_ResponseDetail("LA CUENTA SE ENCUENTRA EN ESTADO: " + isoQry.getISO_122_ExtendedData());
					}
					
				}else{
					
					iso.setISO_039_ResponseCode("214");
					iso.setISO_039p_ResponseDetail("TIPO DE CUENTA INCORRECTO");
				}
				
			}else{
				
				iso.setISO_039_ResponseCode(isoQry.getISO_039_ResponseCode());
				iso.setISO_039p_ResponseDetail(isoQry.getISO_039p_ResponseDetail());
			}
			iso.setWsIso_LogStatus(2);
			
		} catch (Exception e) {
			
			if(iso.getTickAut().isStarted())
				iso.getTickAut().stop();
			iso.setISO_039_ResponseCode("909");
			iso.setISO_039p_ResponseDetail(GeneralUtils.ExceptionToString("ERROR EN PROCESOS ", e, false));	
			
		}finally {
			
			iso.setWsTempAut((iso.getTickAut().getTime(TimeUnit.MILLISECONDS)/1000.0));
		}
		return iso;
	}
	
	public wIso8583 ExecuteQValidateCtaTrx(wIso8583 iso){
		
		try {

			iso.getTickAut().reset();
			iso.getTickAut().start();
			IsoRetrievalTransaction sql = new IsoRetrievalTransaction();
			iso = sql.executeQueryValidAccount(new Ref<wIso8583>(iso));
			if(iso.getTickAut().isStarted())
				iso.getTickAut().stop();
			iso.setWsIso_LogStatus(2);
			
		} catch (Exception e) {
			
			if(iso.getTickAut().isStarted())
				iso.getTickAut().stop();
			iso.setISO_039_ResponseCode("909");
			iso.setISO_039p_ResponseDetail(GeneralUtils.ExceptionToString("ERROR EN PROCESOS ", e, false));	
			
		}finally {
			
			iso.setWsTempAut((iso.getTickAut().getTime(TimeUnit.MILLISECONDS)/1000.0));
		}
		return iso;
	}
	
	public wIso8583 ExecuteStandarTrx(wIso8583 iso){
		
		iso = addIsoAdditionalRows(new Ref<wIso8583>(iso));
		FitCoreProcessor processor = new FitCoreProcessor(iso);
		Detail response = processor.ProcessingTransactionFitCore();
		iso.setFitBankDetail(response);
		return iso;
	}
	
	public wIso8583 ExecuteStandarTransaction(wIso8583 iso){
		
		iso = addIsoAdditionalRows(new Ref<wIso8583>(iso));
		FitCoreProcessor processor = new FitCoreProcessor(iso);
		Detail response = processor.ProcessingTransactionFitCore();
		iso.setFitBankDetail(response);
		return iso;
	}
	
	public wIso8583 DepositoReciclador(wIso8583 iso){
		
		iso = addIsoAdditionalRows(new Ref<wIso8583>(iso));
		Fit1CoreProcessor processor = new Fit1CoreProcessor(iso);
		@SuppressWarnings("unused")
		DetailFit1 response = processor.ProcessingTransactionFit1Core("DepositoReciclador");
		return iso;
	}
	
    public wIso8583 TransferenciaSPI_ORD_FIT1(wIso8583 iso){
		
		iso = addIsoAdditionalRows(new Ref<wIso8583>(iso));
		Fit1CoreProcessor processor = new Fit1CoreProcessor(iso);
		@SuppressWarnings("unused")
		DetailFit1 response = processor.ProcessingTransactionFit1Core("TransferenciaSPI_ORD");
		return iso;
	}
    
    public wIso8583 NotaCreditoDebitoBimo(wIso8583 iso){
		
		iso = addIsoAdditionalRows(new Ref<wIso8583>(iso));
		Fit1CoreProcessor processor = new Fit1CoreProcessor(iso);
		@SuppressWarnings("unused")
		DetailFit1 response = processor.ProcessingTransactionFit1Core("NotaCreditoDebitoBIMO");
		return iso;
	}
    
    public wIso8583 ExecuteTransferMasiveAlex(wIso8583 iso){
		
		iso = addIsoAdditionalRows(new Ref<wIso8583>(iso));
		Fit1CoreProcessor processor = new Fit1CoreProcessor(iso);
		@SuppressWarnings("unused")
		DetailFit1 response = processor.ProcessingTransactionFit1Core("TransferenciasInternas");
		return iso;
	}
    
	public wIso8583 RetiroAtmCoonecta(wIso8583 iso){
		
		iso = addIsoAdditionalRows(new Ref<wIso8583>(iso));
		Fit1CoreProcessor processor = new Fit1CoreProcessor(iso);
		@SuppressWarnings("unused")
		DetailFit1 response = processor.ProcessingTransactionFit1Core("MantenimientoCanalesFit1");
		return iso;
	}
	
	public wIso8583 ExecuteTransferTrxAlex(wIso8583 iso){
		
		wIso8583 clonadowIso = null;
		try {
			
			clonadowIso = (wIso8583) iso.clone();
			
			clonadowIso.setISO_122_ExtendedData("047100MAN001");
			clonadowIso.setISO_123_ExtendedData("TRANSFERENCIA ENTRE CUENTAS INTERNAS");
			clonadowIso.setISO_124_ExtendedData("CA");
			
		} catch (CloneNotSupportedException e) {
			
			iso.setISO_039_ResponseCode("000");
			iso.setISO_039p_ResponseDetail("ERROR AL CLONAR OBJETO wISO: " + GeneralUtils.ExceptionToString("", e, true));
			return iso;
		}
		clonadowIso.setWsTransactionConfig((TransactionConfiguration)iso.getWsTransactionConfig().CloneObject());
		
		FitCoreProcessor processor = new FitCoreProcessor(clonadowIso);
		@SuppressWarnings("unused")
		Detail response = processor.ProcessingTransactionFitCore();
		iso.setISO_039_ResponseCode(clonadowIso.getISO_039_ResponseCode());
		iso.setISO_039p_ResponseDetail(clonadowIso.getISO_039p_ResponseDetail());
		iso.setISO_044_AddRespData(clonadowIso.getISO_044_AddRespData());
		iso.setWsIso_LogStatus(clonadowIso.getWsIso_LogStatus());
		iso.setWsTempAut(clonadowIso.getWsTempAut());
		return iso;
	}
	
	public wIso8583 ExecuteTransferTrxAlexCommerce(wIso8583 iso){
		
		try {
		
			IsoRetrievalTransaction sql = new IsoRetrievalTransaction();
			wIso8583 isoClon = iso.cloneWiso(iso);
			isoClon = sql.RetrieveCommerceSQL(isoClon);
			if(isoClon.getISO_039_ResponseCode().equals("000"))
				iso = ExecuteTransferTrxAlex(iso);
			else{
				
				iso.setISO_039_ResponseCode(isoClon.getISO_039_ResponseCode());
				iso.setISO_039p_ResponseDetail(isoClon.getISO_039p_ResponseDetail());
				iso.setWsIso_LogStatus(isoClon.getWsIso_LogStatus());
			}
				
		} catch (Exception e) {
			
			iso.setISO_039_ResponseCode("909");
			iso.setISO_039p_ResponseDetail("ERROR EN PROCESOS: " + GeneralUtils.ExceptionToString("", e, true));
			return iso;
		}
		
		return iso;
	}
	
	public wIso8583 ExecuteTransferTrxSPIAlex(wIso8583 iso){
		
		try {
			
			if(iso.getISO_055_EMV().startsWith("000")){
				
				iso.setISO_055_EMV(String.valueOf(Integer.parseInt(iso.getISO_055_EMV())));
				if(!iso.getISO_055_EMV().equals("-1")){
					
					FitCoreProcessor processor = new FitCoreProcessor(iso);
					Detail response = processor.ProcessingTransactionFitCore();
					iso.setFitBankDetail(response);
					
				}else{
					
					iso.setISO_039_ResponseCode("908");
					iso.setISO_039p_ResponseDetail("INSTITUCION FINANCIERA RECEPTORA NO SOPORTADA EN FITBANK");
				}
			}
			
		} catch (Exception e) {
			
			iso.setISO_039_ResponseCode("909");
			iso.setISO_039p_ResponseDetail("ERROR EN PROCESOS: " + GeneralUtils.ExceptionToString("", e, false));
			
		}
		return iso;
	}
	
	public wIso8583 QuerySimpleDocument(wIso8583 iso){
		
		try {
		
			FitCoreProcessor processor = new FitCoreProcessor(iso);
			Detail response = processor.ProcessingTransactionFitCore();
			StringBuilder build = new StringBuilder();
			
			if(response != null){
			
			 if(response.getResponse() != null){	
				if(response.getResponse().getCode().equals("0")){
					
					ExecutorService executor = Executors.newFixedThreadPool(iso.getISO_115_ExtendedData()
											   .split("\\,").length);
					try {
						
						List<Future<Object>> list = new ArrayList<Future<Object>>();
						for (int i = 0; i < iso.getISO_115_ExtendedData()
												   .split("\\,").length; i++) {
							Callable<Object> obj = new FitQueryables(Arrays.asList(iso.getISO_115_ExtendedData()
					   				.split("\\,")).get(i), response); 
							 Future<Object> future = executor.submit(obj);
							 list.add(future);
							
						}
						for(Future<Object> fut : list){
				            try {
				                
				            	build.append( String.valueOf(fut.get()).equalsIgnoreCase("null")?"|"
				            			    :String.valueOf(fut.get()) + "|");
				                
				            } catch (Exception e) {
				            	
				            	log.WriteLogMonitor("Error modulo FitIsAut::QuerySimpleDocument ", TypeMonitor.error, e);
				            }
				        }
						iso.setISO_114_ExtendedData(StringUtils.trimEnd(build.toString(),"|"));
						
					} finally {
						
						executor.shutdown();
					}
			    } 
			 }
				
		  }
		} catch (Exception e) {
			
			iso.setISO_039_ResponseCode("909");
			iso.setISO_039p_ResponseDetail(GeneralUtils.ExceptionToString("ERROR EN PROCESOS ", e, false));
		}
		
		return iso;
	}
	
	public wIso8583 QueryLast5Movements(wIso8583 iso){
		
		try {
			
			FitCoreProcessor processor = new FitCoreProcessor(iso);
			Detail response = processor.ProcessingTransactionFitCore();
			FitQueryables qry = null;
			if(response != null){
				
			 if(response.getResponse() != null){	
				 
					if(response.getResponse().getCode().equals("0")){
						qry = new FitQueryables();
						List<Iterables> it = qry.QueryDetailFitBankIterator("TMOVIMIENTOS", 5, 
							       new String[]{ "NUMEROMENSAJE","DEBITOCREDITO","VALORMONEDACUENTA",
							    		         "CMONEDA_MOVIMIENTO", "FREAL","CTERMINAL"}, response);
					
					if(it != null){
						
						if(it.size() > 0){
							
							iso.setISO_114_ExtendedData(
									SerializationObject.ObjectToXML((Serializable) it));						
						}else {
							
							iso.setISO_039_ResponseCode("116");
							iso.setISO_039p_ResponseDetail("LA CUENTA " + iso.getISO_102_AccountID_1() 
							 + " NO TIENE MOVIMIENTOS REGISTRADOS");
						}
					
					}else {
						
						iso.setISO_039_ResponseCode("909");
						iso.setISO_039p_ResponseDetail(qry.getError());
					}
				 }
			 }
		  }
		} catch (Exception e) {
			
			iso.setISO_039_ResponseCode("909");
			iso.setISO_039p_ResponseDetail(GeneralUtils.ExceptionToString("ERROR EN PROCESOS ", e, false));
		}
		return iso;
	}
	
	public wIso8583 QueryBalance(wIso8583 iso){
		
		try {
			
			iso = addIsoAdditionalRows(new Ref<wIso8583>(iso));
			FitCoreProcessor processor = new FitCoreProcessor(iso);
			Detail response = processor.ProcessingTransactionFitCore();
			
			if(response != null){
				
				if(iso.getISO_039_ResponseCode().equals("000")){
					if(response.getResponse().getCode().equals("0")){
						
					   FitQueryables fitSaldoDisponible = new FitQueryables("SALDOS", 0, "SDISPONIBLE", response);
					   FitQueryables fitSaldoContable = new FitQueryables("SALDOS", 0, "SCONTABLE", response);
					   FitQueryables fitMoneda = new FitQueryables("TCUENTA", 0, "CMONEDA", response);
					   fitSaldoDisponible.join();
					   fitSaldoContable.join();
					   fitMoneda.join();
					   double sDisponible = Double.parseDouble((String) fitSaldoDisponible.getReturnValue());
					   double sContable =  Double.parseDouble(String.valueOf(fitSaldoContable.getReturnValue()));
					   String codMoneda = (String) fitMoneda.getReturnValue();
					   
					    iso.setISO_054_AditionalAmounts((iso.getISO_003_ProcessingCode().substring(2,4)
					    	.equals("10")?"10":"20") 
					    	+ "01" + (StringUtils.IsNullOrEmpty(codMoneda) ? "840" : (getCodMoneda(codMoneda))) 
					    	+ (sContable >= 0 ?"C":"D") + String.format("%013.2f", sContable).replace(",", "").replace(".", "")
					    	+ (iso.getISO_003_ProcessingCode().substring(2,4).equals("10")?"10":"20") + "02" 
					    	+ (StringUtils.IsNullOrEmpty(codMoneda) ? "840":
					    	  ((getCodMoneda(codMoneda))) + (sDisponible >= 0 ?"C":"D") 
					    	+ String.format("%013.2f", sDisponible).replace(",", "").replace(".", "")));
					}
				}
			}
		} catch (Exception e) {
			
			iso.setISO_039_ResponseCode("909");
			iso.setISO_039p_ResponseDetail(GeneralUtils.ExceptionToString("ERROR EN PROCESOS ", e, false));	
		}
		return iso;
	}
	
	private String getCodMoneda(String moneda){
		String ret = StringUtils.Empty();
		try {
			
			switch (moneda) {
			case "USD":
				ret = "840";
				break;
			case "EUR":
				ret = "978";
				break;
			default:
				ret = "000";
				break;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return ret;
	}
	
	public wIso8583 DebitWithBalance(wIso8583 iso){
		
		try {
			
			if(iso.getISO_000_Message_Type().startsWith("12")){
				wIso8583 isoConsul  = new wIso8583();
				//isoConsul = (wIso8583) iso.clone();
				isoConsul = iso.cloneWiso(iso);
				isoConsul.setISO_003_ProcessingCode("311000");
				isoConsul.setISO_011_SysAuditNumber(GeneralUtils.GetSecuencial(8));
				isoConsul.setWsTransactionConfig(new TransactionConfiguration
												(new TransactionConfig("311000", 
											    Integer.parseInt(iso.getISO_024_NetworkId()), 
											    iso.getISO_018_MerchantType(), -1)));
				isoConsul = QueryBalance(isoConsul);
				if(isoConsul.getISO_039_ResponseCode().equals("000")){
					
					String rubro = StringUtils.Empty();
					if(iso.getISO_022_PosEntryMode().equalsIgnoreCase("MASTER_ATM")) {
						
						rubro = Arrays.asList(iso.getWsTransactionConfig().getProccodeParams().split("-")).get(0);
						iso.getWsTransactionConfig().setProccodeParams(rubro);
						
					}else {
						
						rubro = Arrays.asList(iso.getWsTransactionConfig().getProccodeParams().split("-")).get(1);
						iso.getWsTransactionConfig().setProccodeParams(rubro);
					}
					
					iso = ExecuteStandarTransaction(iso);
					
					if(iso.getISO_039_ResponseCode().equals("000")){
						iso.setISO_054_AditionalAmounts(isoConsul.getISO_054_AditionalAmounts());
						iso.setISO_038_AutorizationNumber(GeneralUtils.GetSecuencial(16).toUpperCase());
					}
					
				}else{
					
					iso.setISO_039_ResponseCode(isoConsul.getISO_039_ResponseCode());
					iso.setISO_039p_ResponseDetail(isoConsul.getISO_039p_ResponseDetail());
				}
				
				iso.setWsTempAut(iso.getWsTempAut() + isoConsul.getWsTempAut());
					
			}else {
				
				iso = ExecuteStandarTransaction(iso);
			}
			
		} catch (Exception e) {
			
			iso.setISO_039_ResponseCode("909");
			iso.setISO_039p_ResponseDetail(GeneralUtils.ExceptionToString("ERROR EN PROCESOS ", e, false));
		}
		return iso;
	}
	
	public wIso8583 ExecuteQueryTransaction(wIso8583 iso){
		try {
			Thread.sleep(2500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		iso.setISO_039_ResponseCode("000");
		iso.setISO_039p_ResponseDetail("TRANSACCION EXITOSA");
		iso.setISO_054_AditionalAmounts("84002999900000000000000000000000000000000000");		
		iso.setWsIso_LogStatus(2);
		return iso;
	}
	
	@Override
	public wIso8583 call() throws Exception {
		return ExecuteStandarTransaction(this.iso);
	}
	
	public wIso8583 ExecuteDebitCreditFit1(wIso8583 iso){
		
		iso = addIsoAdditionalRows(new Ref<wIso8583>(iso));
		Fit1CoreProcessor processor = new Fit1CoreProcessor(iso);
		@SuppressWarnings("unused")
		DetailFit1 response = processor.ProcessingTransactionFit1Core("NotaCreditoDebito");
		
		return iso;

	}
	
    public wIso8583 ExecuteDebitCreditFit1WithFee(wIso8583 iso) {
		
    	try {
			
    		if(iso.getISO_000_Message_Type().startsWith("12")) {
    			
	    		if(iso.getISO_003_ProcessingCode().startsWith("01")) {
					List<Iso8583> isoList = new ArrayList<>();
					EngineCallableProcessor<Iso8583> engine = null;
					try {
						
						Iso8583 isos = new Iso8583();
						isos.setISO_000_Message_Type("1200");
						isos.setISO_018_MerchantType("0003");
						isos.setISO_024_NetworkId("555522");
						isos.setISO_124_ExtendedData(null);
						isos.setISO_037_RetrievalReferenceNumber(iso.getISO_011_SysAuditNumber());
						isos.setISO_102_AccountID_1(iso.getISO_102_AccountID_1());
						
						for (int i = 0; i < 2; i++) {
							
							Iso8583 isoClone = (Iso8583) isos.clone();
							isoClone.setISO_011_SysAuditNumber(GeneralUtils
									.GetSecuencialNumeric(12));
							isoClone.setISO_002_PAN(iso.getISO_023_CardSeq());
							
							if(i==0) {
								
								isoClone.setISO_003_ProcessingCode(iso.getISO_003_ProcessingCode());
								isoClone.setISO_004_AmountTransaction(iso.getISO_004_AmountTransaction());
								isoClone.setISO_041_CardAcceptorID("DEBITO VALOR");
								
							}else {
								
								isoClone.setISO_003_ProcessingCode("011000");
								isoClone.setISO_041_CardAcceptorID("DEBITO COMISION");
								/*Se asigna la comision*/
								isoClone.setISO_004_AmountTransaction(Double.parseDouble(Arrays.asList(iso.getWsTransactionConfig()
										.getProccodeTransactionFit().split("\\|")).get(1)));
								
								
							}
							if(isoClone.getISO_004_AmountTransaction() > 0) //If que determina si la comision es 0
								isoList.add(isoClone);
						}
						
						iso.getTickAut().reset();
						iso.getTickAut().start();
						engine = new EngineCallableProcessor<>(4);
						String IP = "127.0.0.1";
						for (Iso8583 Iso : isoList) {
							
							csProcess proc = new csProcess(Iso, IP);
							engine.add(proc);
							
						}
						List<Iso8583> listResponse = engine.goProcess();
						if(iso.getTickAut().isStarted())
							iso.getTickAut().stop();
						
						Iso8583 iso8583 = listResponse.stream()
								  .filter(p -> !p.getISO_039_ResponseCode().equals("000"))
								  .findFirst().orElseGet(() -> null);
						
						if(iso8583 == null) {
							
							iso.setISO_039_ResponseCode("000");
							iso.setISO_039p_ResponseDetail("TRANSACCION EXITOSA");
							
						}else {
							
							iso.setISO_039_ResponseCode(iso8583.getISO_039_ResponseCode());
							iso.setISO_039p_ResponseDetail(iso8583.getISO_039p_ResponseDetail());
							
							Thread tRever = new Thread(new Runnable() {
								
								@Override
								public void run() {
									
									for (Iso8583 iso2 : listResponse) {
										
										if(iso2.getISO_039_ResponseCode().equals("000")) {
											
											iso2.setISO_000_Message_Type("1400");
											csProcess proc = new csProcess();
											iso2 = proc.ProcessTransactionMain(iso2, "127.0.0.1");
										}
									}
								}
							});
							
							tRever.start();
						}
						iso.setWsIso_LogStatus(2);
						
					} catch (Exception e) {
						
						if(iso.getTickAut().isStarted())
							iso.getTickAut().stop();
						
						iso.setISO_039_ResponseCode("909");
						iso.setISO_039p_ResponseDetail("ERROR EN PROCESOS, " + GeneralUtils.ExceptionToString(null, e, false));
						log.WriteLogMonitor("Error modulo FitIsAut::ExecuteDebitCreditFit1WithFee (Debit)", TypeMonitor.error, e);
						
					}finally {
					
						iso.setWsTempAut((iso.getTickAut().getTime(TimeUnit.MILLISECONDS)/1000.0));
					}
					
				}else {
					/*-------------------------------SI ES CREDITO---------------------------------------*/
					List<Iso8583> IsoReverList = new ArrayList<>();
					try {
						
						Iso8583 isoc = new Iso8583();
						isoc.setISO_000_Message_Type("1200");
						isoc.setISO_003_ProcessingCode("201000");
						isoc.setISO_004_AmountTransaction(iso.getISO_004_AmountTransaction());
						isoc.setISO_011_SysAuditNumber(GeneralUtils.GetSecuencialNumeric(12));
						isoc.setISO_002_PAN(iso.getISO_023_CardSeq());
						isoc.setISO_018_MerchantType("0003");
						isoc.setISO_024_NetworkId("555522");
						isoc.setISO_037_RetrievalReferenceNumber(iso.getISO_011_SysAuditNumber());
						isoc.setISO_041_CardAcceptorID("CREDITO VALOR");
						isoc.setISO_124_ExtendedData(null);
						isoc.setISO_102_AccountID_1(iso.getISO_102_AccountID_1());
						IsoReverList.add(isoc);
						
						iso.getTickAut().reset();
						iso.getTickAut().start();
						csProcess processorC = new csProcess();
						isoc = processorC.ProcessTransactionMain(isoc, "127.0.0.1");
						/*Ejecuto el debito de la COMISION*/
						if(isoc.getISO_039_ResponseCode().equals("000")) {
							
							/*Determino si la comision es 0*/
							if(Double.parseDouble(Arrays.asList(iso.getWsTransactionConfig()
									.getProccodeTransactionFit().split("\\|")).get(1)) > 0){
								
								Iso8583 isod = (Iso8583) isoc.clone();
								isod.setISO_000_Message_Type("1200");//Quitar esta linea para probar reversos
								isod.setISO_003_ProcessingCode("011000");
								isod.setISO_004_AmountTransaction(Double.parseDouble(Arrays.asList(iso.getWsTransactionConfig()
											.getProccodeTransactionFit().split("\\|")).get(1)));
								isod.setISO_011_SysAuditNumber(GeneralUtils.GetSecuencialNumeric(12));
								isod.setISO_002_PAN(iso.getISO_023_CardSeq());
								isod.setISO_041_CardAcceptorID("DEBITO COMISION");
								IsoReverList.add(isod);
								
								csProcess processorD = new csProcess();
								isod = processorD.ProcessTransactionMain(isod, "127.0.0.1");
								
								if(isod.getISO_039_ResponseCode().equals("000")) {
									
									iso.setISO_039_ResponseCode("000");
									iso.setISO_039p_ResponseDetail("TRANSACCION EXITOSA");
									iso.setWsIso_LogStatus(2);
									
								}else {
									
									iso.setISO_039_ResponseCode(isod.getISO_039_ResponseCode());
									iso.setISO_039p_ResponseDetail(isod.getISO_039p_ResponseDetail());
									
									Thread tRever = new Thread(new Runnable() {
										
										@Override
										public void run() {
											
											try {
												
												EngineCallableProcessor<Iso8583> engine = new EngineCallableProcessor<>(4);
												String IP = "127.0.0.1";
												for (Iso8583 Iso : IsoReverList) {
													Iso.setISO_000_Message_Type("1400");
													csProcess proc = new csProcess(Iso, IP);
													engine.add(proc);
												}
												@SuppressWarnings("unused")
												List<Iso8583> listResponse = engine.goProcess();
												
											} catch (Exception e) {
												
												log.WriteLogMonitor("Error modulo FitIsAut::ExecuteDebitCreditFit1WithFee (Reversos) "
														, TypeMonitor.error, e);
											}
										}
									});
									tRever.start();	
								}
							}
							
							if(iso.getTickAut().isStarted())
								iso.getTickAut().stop();
						}else {
							
							iso.setISO_039_ResponseCode(isoc.getISO_039_ResponseCode());
							iso.setISO_039p_ResponseDetail(isoc.getISO_039p_ResponseDetail());
							iso.setWsIso_LogStatus(2);
						}
						
					} catch (Exception e) {
						
						if(iso.getTickAut().isStarted())
							iso.getTickAut().stop();
						
						iso.setISO_039_ResponseCode("909");
						iso.setISO_039p_ResponseDetail("ERROR EN PROCESOS, " + GeneralUtils.ExceptionToString(null, e, false));
						log.WriteLogMonitor("Error modulo FitIsAut::ExecuteDebitCreditFit1WithFee (Credit) ", TypeMonitor.error, e);
						
					}finally {
						
						iso.setWsTempAut((iso.getTickAut().getTime(TimeUnit.MILLISECONDS)/1000.0));
					}
					
				}
	    		
    		}else {
    			
    			Fit1CoreProcessor processor = new Fit1CoreProcessor(iso);
    			@SuppressWarnings("unused")
    			DetailFit1 response = processor.ProcessingTransactionFit1Core("NotaCreditoDebito");
    			iso = processor.getIso();

    		}
    		
		} finally {
			
			/*Pongo a mano el response de Fit1 debido a multiple trxs*/
			iso.setISO_044_AddRespData(GeneralUtils.GetSecuencialNumeric(8));
		}
			
		
		return iso;
	}
	
	public wIso8583 QueryIterableGeneric(wIso8583 iso){
		
		try {
			
			FitCoreProcessor processor = new FitCoreProcessor(iso);
			Detail response = processor.ProcessingTransactionFitCore();
			FitQueryables qry = null;
			if(response != null){
				
				 if(response.getResponse() != null){
					if(response.getResponse().getCode().equals("0")){
						qry = new FitQueryables();
						List<Iterables> it = qry.QueryDetailFitBankIterator("TCUENTASPERSONA", -1, 
							       (String[])iso.getISO_115_ExtendedData().split("\\,") , response);
					
					if(it != null){
						
						if(it.size() > 0){
							
							iso.setISO_114_ExtendedData(
									SerializationObject.ObjectToXML((Serializable) it));						
						}else {
							
							iso.setISO_039_ResponseCode("116");
							iso.setISO_039p_ResponseDetail("EL SOCIO/CLIENTE: " + iso.getISO_002_PAN()
							 + " NO POSEE PRODUCTOS REGISTRADOS EN LA INSTITUCION FINANCIERA");
						}
					
					}else {
						
						iso.setISO_039_ResponseCode("909");
						iso.setISO_039p_ResponseDetail(qry.getError());
					}
				 }
			  }
		  }
		} catch (Exception e) {
			
			iso.setISO_039_ResponseCode("909");
			iso.setISO_039p_ResponseDetail(GeneralUtils.ExceptionToString("ERROR EN PROCESOS ", e, false));
		}
		return iso;
	}

    public wIso8583 QueryIterableGenericWithTable(wIso8583 iso){
		
		try {
			
			FitCoreProcessor processor = new FitCoreProcessor(iso);
			Detail response = processor.ProcessingTransactionFitCore();
			FitQueryables qry = null;
			if(response != null){
				 if(response.getResponse() != null){
					 
					if(response.getResponse().getCode().equals("0")){
						qry = new FitQueryables();
						List<Iterables> it = qry.QueryDetailFitBankIterator(iso.getISO_035_Track2(), -1, 
							       (String[])iso.getISO_115_ExtendedData().split("\\,") , response);
					
					if(it != null){
						
						if(it.size() > 0){
							
							iso.setISO_114_ExtendedData(
									SerializationObject.ObjectToXML((Serializable) it));	
							FitQueryables fiter = new FitQueryables();
							iso.setISO_123_ExtendedData(StringUtils.IsNullOrEmpty((String) 
									fiter.QueryDetailFitCTL("TOTAL_REGISTROS", response))?"S/D"
									 :(String) fiter.QueryDetailFitCTL("TOTAL_REGISTROS", response));
							
						}else {
							
							iso.setISO_039_ResponseCode("116");
							iso.setISO_039p_ResponseDetail("EL SOCIO/CLIENTE: " + iso.getISO_002_PAN()
							 + " NO POSEE "+ iso.getISO_035_Track2() +" REGISTRADOS EN LA INSTITUCION FINANCIERA");
						}
					
					}else {
						
						iso.setISO_039_ResponseCode("909");
						iso.setISO_039p_ResponseDetail(qry.getError());
					}
				 }
			  }
				
		  }
		} catch (Exception e) {
			
			iso.setISO_039_ResponseCode("909");
			iso.setISO_039p_ResponseDetail(GeneralUtils.ExceptionToString("ERROR EN PROCESOS ", e, false));
		}

		return iso;
	}
    
    public wIso8583 DebitCreditWithValidationClient(wIso8583 iso){
    	

    	try {
			
    		if(iso.getISO_000_Message_Type().startsWith("12")){
    			
    			wIso8583 isoConsul  = new wIso8583();

				isoConsul = iso.cloneWiso(iso);
				isoConsul.setISO_003_ProcessingCode("600002");
				isoConsul.setISO_011_SysAuditNumber(GeneralUtils.GetSecuencial(8));
				isoConsul.setISO_124_ExtendedData(isoConsul.getISO_115_ExtendedData());
				isoConsul.setISO_035_Track2("TCUENTA");
				isoConsul.setISO_115_ExtendedData("IDENTIFICACION,CTIPOIDENTIFICACION"
						+ ",NOMBRELEGAL,NUMEROTELEFONO");
				isoConsul.setISO_033_FWDInsID("XXXXXXXXXXX");
				isoConsul.setISO_114_ExtendedData(StringUtils.Empty());
				isoConsul.setWsTransactionConfig(new TransactionConfiguration
												(new TransactionConfig("600002", 
											    Integer.parseInt(iso.getISO_024_NetworkId()), 
											    iso.getISO_018_MerchantType(), -1)));
				isoConsul = getDataClientforBCE_VC(isoConsul);
				if(isoConsul.getISO_039_ResponseCode().equals("000")){
					
					iso = addIsoAdditionalRows(new Ref<wIso8583>(iso));
					boolean flag = Boolean.parseBoolean(iso.getISO_124_ExtendedData());
					isoConsul = validatePersonBCE_VCE(iso.getISO_115_ExtendedData(), isoConsul, flag);
					if(isoConsul.getISO_039_ResponseCode().equals("000")){

					
						iso = ExecuteStandarTransaction(iso);
						iso.setWsTempAut(iso.getWsTempAut() + isoConsul.getWsTempAut());
						
					}else{
						
						iso.setISO_039_ResponseCode(isoConsul.getISO_039_ResponseCode());
						iso.setISO_039p_ResponseDetail("[VALIDACION] " + isoConsul.getISO_039p_ResponseDetail());
						iso.setWsTempAut(isoConsul.getWsTempAut());
						iso.setWsIso_LogStatus(isoConsul.getWsIso_LogStatus());


					}
					
				}else{
					
					iso.setISO_039_ResponseCode(isoConsul.getISO_039_ResponseCode());
					iso.setISO_039p_ResponseDetail("[CONSULTA VALIDACION] " + isoConsul.getISO_039p_ResponseDetail());
					iso.setISO_044_AddRespData(isoConsul.getISO_044_AddRespData());
					iso.setISO_104_TranDescription(isoConsul.getISO_104_TranDescription());
					iso.setWsTempAut(isoConsul.getWsTempAut());
					iso.setWsIso_LogStatus(isoConsul.getWsIso_LogStatus());
				}
    			
    		}else{
    			
    			iso = ExecuteStandarTransaction(iso);
    		}
    		
		} catch (Exception e) {
			
			iso.setISO_039_ResponseCode("909");
			iso.setISO_039p_ResponseDetail("ERROR EN PROCESOS " + GeneralUtils.ExceptionToString(null, e, false));
		}
    	return iso;
    }
    
    class ISO{
    	private wIso8583 iso;

		public wIso8583 getIso() {
			return iso;
		}

		public void setIso(wIso8583 iso) {
			this.iso = iso;
		}
    	
    }
    
    public wIso8583 TransferWithValidationClient(wIso8583 iso){
    	
    	try {
			
    		ISO iso8583 = new ISO();
    		iso8583.setIso(iso);
    		StopWatch timmer = new StopWatch();
    		double timmerDouble = 0;
    		
    		if(iso.getISO_000_Message_Type().startsWith("12")){
    			
    			wIso8583[] isoConsul  = new wIso8583[2];
    			
    			Thread t1 = new Thread(new Runnable() {
					@Override
					public void run() {
						
						isoConsul[0] = iso8583.getIso().cloneWiso(iso8583.getIso());
						isoConsul[0].setISO_003_ProcessingCode("600002");
						isoConsul[0].setISO_011_SysAuditNumber(GeneralUtils.GetSecuencial(8));
						isoConsul[0].setISO_124_ExtendedData(isoConsul[0].getISO_115_ExtendedData());
						isoConsul[0].setISO_035_Track2("TCUENTA");
						isoConsul[0].setISO_115_ExtendedData("IDENTIFICACION,CTIPOIDENTIFICACION"
								+ ",NOMBRELEGAL,NUMEROTELEFONO");
						isoConsul[0].setISO_033_FWDInsID("XXXXXXXXXXX");
						isoConsul[0].setISO_114_ExtendedData(StringUtils.Empty());
						isoConsul[0].setWsTransactionConfig(new TransactionConfiguration
														(new TransactionConfig("600002", 
													    Integer.parseInt(iso8583.getIso().getISO_024_NetworkId()), 
													    iso8583.getIso().getISO_018_MerchantType(), -1)));
						isoConsul[0] = getDataClientforBCE_VC(isoConsul[0]);
					}
				});
    			
    			Thread t2 = new Thread(new Runnable() {
					@Override
					public void run() {
						
						isoConsul[1] = iso8583.getIso().cloneWiso(iso8583.getIso());
						isoConsul[1].setISO_003_ProcessingCode("600002");
						isoConsul[1].setISO_011_SysAuditNumber(GeneralUtils.GetSecuencial(8));
						isoConsul[1].setISO_124_ExtendedData(isoConsul[1].getISO_115_ExtendedData());
						isoConsul[1].setISO_035_Track2("TCUENTA");
						isoConsul[1].setISO_102_AccountID_1(iso8583.getIso().getISO_103_AccountID_2());
						isoConsul[1].setISO_115_ExtendedData("IDENTIFICACION,CTIPOIDENTIFICACION"
								+ ",NOMBRELEGAL,NUMEROTELEFONO");
						isoConsul[1].setISO_033_FWDInsID("XXXXXXXXXXX");
						isoConsul[1].setISO_114_ExtendedData(StringUtils.Empty());
						isoConsul[1].setWsTransactionConfig(new TransactionConfiguration
														(new TransactionConfig("600002", 
													    Integer.parseInt(iso8583.getIso().getISO_024_NetworkId()), 
													    iso8583.getIso().getISO_018_MerchantType(), -1)));
						isoConsul[1] = getDataClientforBCE_VC(isoConsul[1]);
					}
				});
    			timmer.reset();
    			timmer.start();
				t1.start(); t2.start(); t1.join(); t2.join();
				if(timmer.isStarted())
					timmer.stop();
				timmerDouble =  (timmer.getTime(TimeUnit.MILLISECONDS)/1000.0);
				
				if(isoConsul[0].getISO_039_ResponseCode().equals("000") && 
						isoConsul[1].getISO_039_ResponseCode().equals("000")){
					
					iso = addIsoAdditionalRows(new Ref<wIso8583>(iso));
					boolean flag = Boolean.parseBoolean(iso.getISO_124_ExtendedData());
					isoConsul[0] = validatePersonBCE_VCE(iso.getISO_034_PANExt() + "|" + iso.getISO_002_PAN() + "|" 
					               + iso.getISO_055_EMV(), isoConsul[0], flag);
					isoConsul[1] = validatePersonBCE_VCE(iso.getISO_115_ExtendedData(), isoConsul[1], flag);
					if(isoConsul[0].getISO_039_ResponseCode().equals("000") &&
							isoConsul[1].getISO_039_ResponseCode().equals("000")){
					
						iso = ExecuteStandarTransaction(iso);
						iso.setWsTempAut(iso.getWsTempAut() + timmerDouble);
						
					}else{
						
						if(!isoConsul[0].getISO_039_ResponseCode().equals("000")){
							
							iso.setISO_039_ResponseCode(isoConsul[0].getISO_039_ResponseCode());
							iso.setISO_039p_ResponseDetail( isoConsul[0].getISO_039p_ResponseDetail() 
									                       + " (ORDENANTE)");
							iso.setISO_044_AddRespData(isoConsul[0].getISO_044_AddRespData());
							iso.setISO_104_TranDescription(isoConsul[0].getISO_104_TranDescription());
							iso.setWsIso_LogStatus(isoConsul[0].getWsIso_LogStatus());
							System.out.println("Tiempo " + isoConsul[0].getWsTempAut());
							
						}else if (!isoConsul[1].getISO_039_ResponseCode().equals("000")) {
							
							iso.setISO_039_ResponseCode(isoConsul[1].getISO_039_ResponseCode());
							iso.setISO_039p_ResponseDetail(isoConsul[1].getISO_039p_ResponseDetail()
															+ " (RECEPTOR)");
							iso.setISO_044_AddRespData(isoConsul[1].getISO_044_AddRespData());
							iso.setISO_104_TranDescription(isoConsul[1].getISO_104_TranDescription());
							iso.setWsIso_LogStatus(isoConsul[1].getWsIso_LogStatus());
						}
						iso.setWsTempAut(timmerDouble);
						
					}
					
				}else{
					
					if(!isoConsul[0].getISO_039_ResponseCode().equals("000")){
						
						iso.setISO_039_ResponseCode(isoConsul[0].getISO_039_ResponseCode());
						iso.setISO_039p_ResponseDetail("[CONSULTA VALIDACION] " + 
						               isoConsul[0].getISO_039p_ResponseDetail());
						iso.setWsIso_LogStatus(isoConsul[0].getWsIso_LogStatus());
						
					}else if (!isoConsul[1].getISO_039_ResponseCode().equals("000")) {
						
						iso.setISO_039_ResponseCode(isoConsul[1].getISO_039_ResponseCode());
						iso.setISO_039p_ResponseDetail("[CONSULTA VALIDACION] " + 
						                isoConsul[1].getISO_039p_ResponseDetail());
						iso.setWsIso_LogStatus(isoConsul[1].getWsIso_LogStatus());
					}
					iso.setWsTempAut(timmerDouble);
				}

				
    			
    		}else{
    			
    			iso = ExecuteStandarTransaction(iso);
    		}
    		
		} catch (Exception e) {
			
			iso.setISO_039_ResponseCode("909");
			iso.setISO_039p_ResponseDetail("ERROR EN PROCESOS " + GeneralUtils.ExceptionToString(null, e, false));
		}
    	return iso;
    }
    
    @SuppressWarnings("unchecked")
	public wIso8583 getDataClientforBCE_VC(wIso8583 iso){
    	
    	try {

    		iso = QueryIterableGenericWithTable(iso);
    		if(iso.getISO_039_ResponseCode().equals("000")){
    			
    			List<Iterables> it = (List<Iterables>) SerializationObject
    					.XMLToObject(iso.getISO_114_ExtendedData());
    			iso.setISO_114_ExtendedData(StringUtils.Empty());
    			String a = StringUtils.Empty();
    			for (Iterables iterables : it) {
    				
    				for (int i = 0; i < iso.getISO_115_ExtendedData().split("\\,").length; i++) {
    					
    					iso.setISO_114_ExtendedData(iso.getISO_114_ExtendedData() + "|" + iterables.getIterarors().
							get(Arrays.asList(iso.getISO_115_ExtendedData().split("\\,")).get(i)));
    				}
    			}
    			System.out.println(a);
    			iso.setISO_114_ExtendedData(StringUtils.TrimStart(iso.getISO_114_ExtendedData(),"|"));
    			if(Arrays.asList(iso.getISO_114_ExtendedData().split("\\|")).size() != 4){
    				
    				iso.setISO_114_ExtendedData(iso.getISO_114_ExtendedData() + "|+593-0900000000");
    			}
    			
    		}
    		
		} catch (Exception e) {
			
			iso.setISO_039_ResponseCode("909");
			iso.setISO_039p_ResponseDetail(GeneralUtils.ExceptionToString("ERROR EN PROCESOS ", e, false));
		}
    	return iso;
    }
    
    private wIso8583 validatePersonBCE_VCE(String data, wIso8583 iso, boolean validateMovil){
    	
    	String nombres, TipoId, documentId, celu = null;
    	nombres = Arrays.asList(data.split("\\|")).get(0);
    	TipoId = Arrays.asList(data.split("\\|")).get(2);
    	documentId = Arrays.asList(data.split("\\|")).get(1);
    	if(validateMovil)
    		celu = Arrays.asList(data.split("\\|")).get(3);
    	try {
    		String firstName = StringUtils.Empty();
			String lastName = StringUtils.Empty();
    		if(nombres.toUpperCase().contains("LN=") || nombres.toUpperCase().contains("FN=")){
    			String noms[] = nombres.split("\\=");
    			if(noms[0].equalsIgnoreCase("FN")){
 
    				firstName = noms[1].replace(" LN", StringUtils.Empty())
    						.replace(" FN", StringUtils.Empty());
    				lastName = noms[2].trim();   				
    			}else{
    				
    				lastName = noms[1].replace(" LN", StringUtils.Empty())
    						.replace(" FN", StringUtils.Empty());
    				firstName = noms[2].trim();   
    			}
    		}else{
    			
    			String nomsII[] = nombres.split("\\s+");
    			if(nomsII.length > 1){
    				
    				firstName = nomsII[0];
    				lastName = nomsII[1];
    			}
    		}
    		int coincidencias = 0;
    		String nombresBDD = Arrays.asList(iso.getISO_114_ExtendedData().split("\\|")).get(2);
    		String nombresBDDArray[] = nombresBDD.split("\\s+");
    		for (int i = 0; i < nombresBDDArray.length; i++) {
				
    			if(nombresBDDArray[i].equalsIgnoreCase(firstName.toUpperCase()))
    				coincidencias++;
    			if(nombresBDDArray[i].equalsIgnoreCase(lastName.toUpperCase()))
    				coincidencias++;
			}
    		if(coincidencias < 2){

    			iso.setISO_039_ResponseCode("115");
    			iso.setISO_039p_ResponseDetail("NOMBRES DEL CUENTAHIENTE NO COINCIDEN "
    					+ "CON LOS REGISTRADOS EN EL SISTEMA");
    			return iso;
    		}
    		
    		switch (TipoId.toUpperCase()) {
			case "NIDN":
				TipoId = "CED";
				break;
			case "TXID":
				TipoId = "RUC";
				break;
			case "CCPT":
				TipoId = "PAS";
				break;
			case "ARNU":
				TipoId = "F";
				break;	
				
			default:
				break;
			}
			if(!Arrays.asList(iso.getISO_114_ExtendedData().split("\\|")).get(1).equals(TipoId)){
				
				iso.setISO_039_ResponseCode("115");
    			iso.setISO_039p_ResponseDetail("TIPO DE IDENTIFICACION DEL CUENTAHABIENTE NO COINCIDE "
    					+ "CON EL REGISTRADO EN EL SISTEMA");
    			return iso;
			}
    		
			if(!Arrays.asList(iso.getISO_114_ExtendedData().split("\\|")).get(0).equals(documentId)){
				
				iso.setISO_039_ResponseCode("115");
    			iso.setISO_039p_ResponseDetail("NRO. DE IDENTIFICACION DEL CUENTAHABIENTE NO COINCIDE "
    					+ "CON EL REGISTRADO EN EL SISTEMA");
    			return iso;
			}
			if(validateMovil){
				if(celu.startsWith("+593")){
					
					if(celu.contains("-")){
						
						if(!Arrays.asList(iso.getISO_114_ExtendedData().split("\\|")).get(3).
								equals(Arrays.asList(celu.split("\\-")).get(1).trim())){
							iso.setISO_039_ResponseCode("115");
			    			iso.setISO_039p_ResponseDetail("NRO. CELULAR DEL CUENTAHABIENTE NO COINCIDE "
			    					+ "CON EL REGISTRADO EN EL SISTEMA");
			    			return iso;
						}
					}
				}else{
					iso.setISO_039_ResponseCode("115");
	    			iso.setISO_039p_ResponseDetail("FORMATO NUMERO CELULAR DEL CUENTAHABIENTE "
	    					+ "NO ES EL ADECUADO EL CORRECTO ES (+593-09XXXXXXXX)");
	    			return iso;
				}
			}
			iso.setISO_039_ResponseCode("000");

    		
		} catch (Exception e) {
		
			iso.setISO_039_ResponseCode("909");
			iso.setISO_039p_ResponseDetail("ERROR AL VALIDAR DATOS DEL CUENTAHABIENTE");
			log.WriteLogMonitor("Error modulo FitIsAut::validatePersonBCE_VCE ", TypeMonitor.error, e);


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
			
			log.WriteLogMonitor("Error modulo FitIsAut::addIsoAdditionalRows ", TypeMonitor.error, e);
		}
    	return isoReference.get();
    }
    
    public wIso8583 addIsoAdditionalRows(wIso8583 isoReference){
    	
    	wIso8583 iso = null;
    	try {
			
    		iso = isoReference;
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
    		
    		
		} catch (Exception e) {
			
			log.WriteLogMonitor("Error modulo FitIsAut::addIsoAdditionalRows ", TypeMonitor.error, e);
		}
    	return iso;
    }
    
	public wIso8583 CreateFastPersonFit1(wIso8583 iso){
			
		iso = addIsoAdditionalRows(new Ref<wIso8583>(iso));
		try {
			
			if(FormatUtils.getEdad("yyyy-MM-dd", iso.getISO_114_ExtendedData())[0] < 18) {
				
				iso.setISO_039_ResponseCode("522");
				iso.setISO_039p_ResponseDetail("NO SE PUEDE ENROLAR A UN MENOR DE EDAD");
				return iso;
			}
			
			Fit1CoreProcessor processor = new Fit1CoreProcessor(iso);
			DetailFit1 response = processor.ProcessingTransactionFit1Core("CreaPersonaRapida");
			String cPersonaSignada = StringUtils.Empty();
			if(response != null && processor.getIso().getISO_039_ResponseCode().equals("000")) {
				
				cPersonaSignada = response.getRespuesta().getCampoNumero()
						          .stream()
						          .filter(p -> p.contains("ASIGNADO"))
						          .findFirst().orElseGet(() -> null);
				
				if(!StringUtils.IsNullOrEmpty(cPersonaSignada)) {
					
					iso.setISO_124_ExtendedData(String.valueOf(Arrays.asList(cPersonaSignada.trim()
							                    .split(" ")).get(3).trim()));//Asignacion cpersona
					iso.setISO_039_ResponseCode("000");
					iso.setISO_039p_ResponseDetail("TRANSACCION EXITOSA");
					
				}else {
					
					iso.setISO_039_ResponseCode("909");
					iso.setISO_039p_ResponseDetail("ERROR EN PROCESOS, NO SE PUDO RECUPERAR EL CAMPO CPERSONA");
				}
			}
			
    		iso.setWsIso_LogStatus(processor.getIso().getWsIso_LogStatus());
			iso.setWsTempAut(processor.getIso().getWsTempAut());
			
		} catch (Exception e) {
			
			log = new Logger();
			log.WriteLogMonitor("Error modulo FitIsAut::CreateFastPersonFit1 ", TypeMonitor.error, e);
			iso.setISO_039_ResponseCode("909");
			iso.setISO_039p_ResponseDetail("ERROR EN PROCESOS " + GeneralUtils.ExceptionToString(null, e, false));
		}
		
		return iso;
	}
    
    public wIso8583 CreateAccountFit1(wIso8583 iso){
    	if(iso.getISO_102_AccountID_1().indexOf("|") != -1){
	    	try {
				
	    		iso = addIsoAdditionalRows(new Ref<wIso8583>(iso));
	    		Fit1CoreProcessor processor = new Fit1CoreProcessor(iso);
				DetailFit1 response = processor.ProcessingTransactionFit1Core("CreaCuentaBasica");
				String cCuenta = StringUtils.Empty();
				if(response != null && processor.getIso().getISO_039_ResponseCode().equals("000")) {
					
					cCuenta = response.getRespuesta().getCampoNumero()
					          .stream()
					          .filter(p -> NumbersUtils.isNumeric(p))
					          .findFirst().orElseGet(() -> null);
					if(!StringUtils.isNullOrEmpty(cCuenta)) {
						
						String acum = StringUtils.Empty();
	    				String[] arrayCta = iso.getISO_102_AccountID_1().split("\\|");
	    				arrayCta[1] = "10";
	    				arrayCta[3] = cCuenta;
	    				for (String string : arrayCta) {
							
	    					acum+=string + "|";
						}
	    				iso.setISO_124_ExtendedData(StringUtils.trimEnd(acum, "|"));
	    				//Vuelvo a acomodar 102 Account para grabar en tabla de enrolamientos internos
	    				iso.setISO_102_AccountID_1(Arrays.asList(iso.getISO_124_ExtendedData().split("\\|")).get(3));
						
					}else {
						
						iso.setISO_039_ResponseCode("100");
	        			iso.setISO_039p_ResponseDetail("NO SE HA PODIDO RECUPERAR EL NUMERO DE CUENTA XML FITBANK");
					}
				}
				
				iso.setWsIso_LogStatus(processor.getIso().getWsIso_LogStatus());
				iso.setWsTempAut(processor.getIso().getWsTempAut());
				
			} catch (Exception e) {
				iso.setISO_039_ResponseCode("070");
				iso.setISO_039p_ResponseDetail(GeneralUtils.ExceptionToString("ERROR EN PROCESOS", e, false));
				log = new Logger();
				log.WriteLogMonitor("Error modulo FitIsAut::CreateAccountFit1 ", TypeMonitor.error, e);
			}
    	}else {
    		
    		log = new Logger();
			log.WriteLogMonitor("===> Cliente ya posee Cuenta Basica, Se procede a recuperar ccuenta... ", TypeMonitor.monitor, null);
    		
    		iso.setISO_039_ResponseCode("000");
    		iso.setISO_039p_ResponseDetail("TRANSACCION EXITOSA");
    	}
    	
    	return iso;
    }
    
    public wIso8583 CreateFastPersonFit3(wIso8583 iso){
    	
    	wIso8583 clonadowIso = null;
    	try {
			
    		if(FormatUtils.getEdad("yyyy-MM-dd", iso.getISO_114_ExtendedData())[0] < 18){
    		
    			iso.setISO_039_ResponseCode("522");
				iso.setISO_039p_ResponseDetail("NO SE PUEDE ENROLAR A UN MENOR DE EDAD");
				return iso;
    		}
    		
    		clonadowIso = (wIso8583) iso.clone();
    		clonadowIso.setWsTransactionConfig((TransactionConfiguration)iso.getWsTransactionConfig().CloneObject());
    		
    		clonadowIso.getWsTransactionConfig().setProccodeTransactionFit(Arrays.asList(clonadowIso.getWsTransactionConfig()
    				.getProccodeTransactionFit().split("\\|")).get(0));
    		
    		FitCoreProcessor processor = new FitCoreProcessor(clonadowIso);
    		Detail response = processor.ProcessingTransactionFitCore();
    		
    		if(clonadowIso.getISO_039_ResponseCode().equals("000")){
    			
    			FitQueryables cpersona = new FitQueryables("TPERSONA", 0, "CPERSONA", response);
    			cpersona.join();
    			
    			if(cpersona.getReturnValue() != null){
    				
    				/*iso.setISO_120_ExtendedData(iso.getISO_120_ExtendedData() + "|" 
    				                + cpersona.getReturnValue());*/
    				iso.setISO_124_ExtendedData(String.valueOf(cpersona.getReturnValue()));//Aasignacion cpersona
    				iso.setISO_039_ResponseCode("000");
    				iso.setISO_039p_ResponseDetail("TRANSACCION EXITOSA");
    				
    			}else{
    				
    				iso.setISO_039_ResponseCode("909");
    				iso.setISO_039p_ResponseDetail("ERROR EN PROCESOS, NO SE PUDO RECUPERAR EL CAMPO CPERSONA");
    			}
    			
    		}else{
    			
    			iso.setISO_039_ResponseCode(clonadowIso.getISO_039_ResponseCode());
    			iso.setISO_039p_ResponseDetail(clonadowIso.getISO_039p_ResponseDetail());
    		}
    		iso.setFitBankDetail(response);
    		iso.setWsIso_LogStatus(clonadowIso.getWsIso_LogStatus());
			iso.setWsTempAut(clonadowIso.getWsTempAut());
    		
		} catch (Exception e) {
			
			log = new Logger();
			log.WriteLogMonitor("Error modulo FitIsAut::CreateFastPersonFit3 ", TypeMonitor.error, e);
			iso.setISO_039_ResponseCode("909");
			iso.setISO_039p_ResponseDetail("ERROR EN PROCESOS " + GeneralUtils.ExceptionToString(null, e, false));
			
		}
    	return iso;
    }
    
    public wIso8583 CreateAccountFit3(wIso8583 iso){
    	
    	if(iso.getISO_102_AccountID_1().indexOf("|") != -1){
	    	wIso8583 clonadowIso = null;
	    	try {
				
	    		clonadowIso = (wIso8583) iso.clone();
	    		clonadowIso.setWsTransactionConfig((TransactionConfiguration)iso.getWsTransactionConfig().CloneObject());
	    		
	    		clonadowIso.getWsTransactionConfig().setProccodeTransactionFit(Arrays.asList(clonadowIso.getWsTransactionConfig()
	    				.getProccodeTransactionFit().split("\\|")).get(1));
	    		
	    		FitCoreProcessor processor = new FitCoreProcessor(clonadowIso);
	    		
				Detail response = processor.ProcessingTransactionFitCore();
	    		
	    		if(clonadowIso.getISO_039_ResponseCode().equals("000")){
	    			
	    			FitQueryables ccuenta = new FitQueryables("TSOLICITUD", 0, "CCUENTA", response);
	    			ccuenta.join();
	    			
	    			if(ccuenta.getReturnValue() != null){
	    				
	    				String acum = StringUtils.Empty();
	    				String[] arrayCta = iso.getISO_102_AccountID_1().split("\\|");
	    				arrayCta[1] = "10";
	    				arrayCta[3] = String.valueOf(ccuenta.getReturnValue());
	    				for (String string : arrayCta) {
							
	    					acum+=string + "|";
						}
	    				iso.setISO_124_ExtendedData(StringUtils.trimEnd(acum, "|"));
	    				//Vuelvo a acomodar 102 Account para grabar en tabla de enrolamientos internos
	    				iso.setISO_102_AccountID_1(Arrays.asList(iso.getISO_124_ExtendedData().split("\\|")).get(3));
	    				
	    			}else{
	    				
	    				iso.setISO_039_ResponseCode("100");
	        			iso.setISO_039p_ResponseDetail("NO SE HA PODIDO RECUPERAR EL NUMERO DE CUENTA DETAIL FITBANK");
	    			}
	    			
	    		}else{
	    			
	    			iso.setISO_039_ResponseCode(clonadowIso.getISO_039_ResponseCode());
	    			iso.setISO_039p_ResponseDetail(clonadowIso.getISO_039p_ResponseDetail());
	    		}
	    		iso.setWsIso_LogStatus(clonadowIso.getWsIso_LogStatus());
				iso.setWsTempAut(clonadowIso.getWsTempAut());
	    		
			} catch (Exception e) {
				
				iso.setISO_039_ResponseCode("070");
				iso.setISO_039p_ResponseDetail(GeneralUtils.ExceptionToString("ERROR EN PROCESOS", e, false));
				log = new Logger();
				log.WriteLogMonitor("Error modulo FitIsAut::CreateAccountFit1 ", TypeMonitor.error, e);
			}
    	}else{
    		
    		log = new Logger();
			log.WriteLogMonitor("===> Cliente ya posee Cuenta Basica, Se procede a recuperar ccuenta... ", TypeMonitor.monitor, null);
    		
    		iso.setISO_039_ResponseCode("000");
    		iso.setISO_039p_ResponseDetail("TRANSACCION EXITOSA");
    	}
    	return iso;
    }
    
    public wIso8583 CreateFastPersonFit3Exe(wIso8583 iso){
    	
    	final List<wIso8583> isoAux = new ArrayList<>();
		List<wIso8583> isoResAux = new ArrayList<>();
		ExecutorService executor = null;
    	try {
			
    		iso = CreateFastPersonFit3(iso);
    		if(!iso.getISO_039_ResponseCode().equals("000")){
    		
    			Detail det = iso.getFitBankDetail();
    			if(det != null){
    			
    				if(det.getResponse() != null){
	    				/*Cuando ya existe una persona*/
	    				if(det.getResponse().getCode().equalsIgnoreCase("BDD-00001")){
	    					
	    					wIso8583 isoClone = iso.cloneWiso(iso);
	    					isoClone.setWsTransactionConfig((TransactionConfiguration)iso.getWsTransactionConfig().CloneObject());
	    					
	    					log = new Logger();
	    					log.WriteLogMonitor("===> Cliente ya existe, Se procede a recuperar Cpersona... ", TypeMonitor.monitor, null);
	    					//Aqui el cambio ojo paralelo el cpersona y obtencion de la cuenta basica si la tiene
	    					
	    					wIso8583 isoCloneCtas = iso.cloneWiso(iso);
	    					isoCloneCtas.setWsTransactionConfig((TransactionConfiguration)iso.getWsTransactionConfig().CloneObject());
	    					
	    					isoAux.add(isoClone);
	    					isoAux.add(isoCloneCtas);
	    					
	    					executor = Executors.newWorkStealingPool();
	    					
	    					List<Callable<wIso8583>> callables = Arrays.asList(
	    					        () -> validateBasicAccount(isoAux.get(0)),
	    					        () -> getCpersona(isoAux.get(1))
	    					);
	    					
	    					isoResAux = (List<wIso8583>) executor.invokeAll(callables)
	    						    .stream()
	    						    .map(future -> {
	    						        try {
	    						            return future.get();
	    						        }
	    						        catch (Exception e) {
	    						            throw new IllegalStateException(e);
	    						        }
	    						    })
	    						    .peek(Objects::requireNonNull)
	    						    .collect(Collectors.toList());
	    					
	    					if(isoResAux != null){
	    						
	    						wIso8583 res  = isoResAux.stream()
	    						          .filter(a -> !a.getISO_039_ResponseCode().equals("000"))
	    						          .findFirst().orElseGet(()-> null);
	    						
	    						if(res != null){
	    							
	    							iso.setISO_039_ResponseCode(res.getISO_039_ResponseCode());
		    						iso.setISO_039p_ResponseDetail(res.getISO_039p_ResponseDetail() + " (1)");
		    						return iso;
	    						}
	    						
	    						String ccuenta = isoResAux.get(0).getISO_102_AccountID_1();
	    						String cpersona = isoResAux.get(1).getISO_124_ExtendedData();
	    						
	    						if(ccuenta.indexOf('|') == -1){
		    						String acum = StringUtils.Empty();
		    	    				String[] arrayCta = iso.getISO_102_AccountID_1().split("\\|");
		    	    				arrayCta[1] = "10";
		    	    				arrayCta[3] = String.valueOf(ccuenta);
		    	    				for (String string : arrayCta) {
		    							
		    	    					acum+=string + "|";
		    						}
		    	    				iso.setISO_124_ExtendedData(StringUtils.trimEnd(acum, "|"));
		    	    				//Vuelvo a acomodar 102 Account para grabar en tabla de enrolamientos internos
		    	    				iso.setISO_102_AccountID_1(Arrays.asList(iso.getISO_124_ExtendedData().split("\\|")).get(3));
	    						}else{
	    							
	    							iso.setISO_124_ExtendedData(cpersona);
	    						}
	    						
	    						iso.setISO_039_ResponseCode("000");
	    						iso.setISO_039p_ResponseDetail("TRANSACCION EXITOSA");
 	    						
	    					}else{
	    						
	    						iso.setISO_039_ResponseCode("909");
	    						iso.setISO_039p_ResponseDetail("ERROR AL VALIDAR PROCESO CLIENTE - CUENTA BASICA - 2");
	    						return iso;
	    					}
	    					
	    					
	    					/*if(isoClone.getISO_039_ResponseCode().equals("000")){
	    						
	    						iso.setISO_039_ResponseCode("000");
	    						iso.setISO_039p_ResponseDetail("TRANSACCION EXITOSA");
	    						iso.setISO_124_ExtendedData(isoClone.getISO_124_ExtendedData());
	    						
	    					}else {
								
	    						iso.setISO_039_ResponseCode(isoClone.getISO_039_ResponseCode());
	    						iso.setISO_039p_ResponseDetail(isoClone.getISO_039p_ResponseDetail());
							}*/
	    				}
    				}
    			}
    		}
		} catch (Exception e) {
			
			iso.setISO_039_ResponseCode("070");
			iso.setISO_039p_ResponseDetail(GeneralUtils.ExceptionToString("ERROR EN PROCESOS", e, false));
			log = new Logger();
			log.WriteLogMonitor("Error modulo FitIsAut::CreateFastPersonFit3Exe ", TypeMonitor.error, e);
		}
    	return iso;
    }
    
    public wIso8583 CreateFastPersonFit1Exe(wIso8583 iso){
    	
    	final List<wIso8583> isoAux = new ArrayList<>();
		List<wIso8583> isoResAux = new ArrayList<>();
		ExecutorService executor = null;
    	try {
			
    		iso = CreateFastPersonFit1(iso);
    		if(!iso.getISO_039_ResponseCode().equals("000")){
    		
				/*Cuando ya existe una persona*/
				if(iso.getISO_039p_ResponseDetail().contains("504052")){
					
					wIso8583 isoClone = iso.cloneWiso(iso);
					isoClone.setWsTransactionConfig((TransactionConfiguration)iso.getWsTransactionConfig().CloneObject());
					
					log = new Logger();
					log.WriteLogMonitor("===> Cliente ya existe, Se procede a recuperar Cpersona... ", TypeMonitor.monitor, null);
					//Aqui el cambio ojo paralelo el cpersona y obtencion de la cuenta basica si la tiene
					wIso8583 isoCloneCtas = iso.cloneWiso(iso);
					isoCloneCtas.setWsTransactionConfig((TransactionConfiguration)iso.getWsTransactionConfig().CloneObject());
					
					isoAux.add(isoClone);
					isoAux.add(isoCloneCtas);
					
					executor = Executors.newWorkStealingPool();
					
					List<Callable<wIso8583>> callables = Arrays.asList(
					        () -> validateBasicAccountFit1(isoAux.get(0)),
					        () -> getCpersonaFit1(isoAux.get(1))
					);
					
					isoResAux = (List<wIso8583>) executor.invokeAll(callables)
						    .stream()
						    .map(future -> {
						        try {
						            return future.get();
						        }
						        catch (Exception e) {
						            throw new IllegalStateException(e);
						        }
						    })
						    .peek(Objects::requireNonNull)
						    .collect(Collectors.toList());
					
					if(isoResAux != null){
						
						wIso8583 res  = isoResAux.stream()
						          .filter(a -> !a.getISO_039_ResponseCode().equals("000"))
						          .findFirst().orElseGet(()-> null);
						
						if(res != null){
							
							iso.setISO_039_ResponseCode(res.getISO_039_ResponseCode());
    						iso.setISO_039p_ResponseDetail(res.getISO_039p_ResponseDetail() + " (1)");
    						return iso;
						}
						
						String ccuenta = isoResAux.get(0).getISO_102_AccountID_1();
						String cpersona = isoResAux.get(1).getISO_124_ExtendedData();
						
						if(ccuenta.indexOf('|') == -1){
    						String acum = StringUtils.Empty();
    	    				String[] arrayCta = iso.getISO_102_AccountID_1().split("\\|");
    	    				arrayCta[1] = "10";
    	    				arrayCta[3] = String.valueOf(ccuenta);
    	    				for (String string : arrayCta) {
    							
    	    					acum+=string + "|";
    						}
    	    				iso.setISO_124_ExtendedData(StringUtils.trimEnd(acum, "|"));
    	    				//Vuelvo a acomodar 102 Account para grabar en tabla de enrolamientos internos
    	    				iso.setISO_102_AccountID_1(Arrays.asList(iso.getISO_124_ExtendedData().split("\\|")).get(3));
						}else{
							
							iso.setISO_124_ExtendedData(cpersona);
						}
						
						iso.setISO_039_ResponseCode("000");
						iso.setISO_039p_ResponseDetail("TRANSACCION EXITOSA");
						
					}else{
						
						iso.setISO_039_ResponseCode("909");
						iso.setISO_039p_ResponseDetail("ERROR AL VALIDAR PROCESO CLIENTE - CUENTA BASICA - 2");
						return iso;
					}
				}
    		}
		} catch (Exception e) {
			
			iso.setISO_039_ResponseCode("070");
			iso.setISO_039p_ResponseDetail(GeneralUtils.ExceptionToString("ERROR EN PROCESOS", e, false));
			log = new Logger();
			log.WriteLogMonitor("Error modulo FitIsAut::CreateFastPersonFit1Exe ", TypeMonitor.error, e);
		}
    	return iso;
    }
    
    public wIso8583 QueryBalanceExe(wIso8583 iso){
    	
    	try {
		
    		wIso8583 isoClone = iso.cloneWiso(iso);
    		isoClone.setWsTransactionConfig((TransactionConfiguration)iso.getWsTransactionConfig().CloneObject());
    		isoClone = QueryBalance(isoClone);
    		if(isoClone.getISO_039_ResponseCode().equals("000")){ /*Puse recien valida que no exista timeout*/
	    		if(isoClone.getISO_039_ResponseCode().equals("000")){
	    			
	    			double saldo = Double.parseDouble(isoClone.getISO_054_AditionalAmounts().substring(8,20))/100;
	    			iso.setISO_054_AditionalAmounts(String.valueOf(saldo));
	    			iso.setISO_104_TranDescription(isoClone.getISO_054_AditionalAmounts().substring(7,8));
	    		}else{
	    			
	    			iso.setISO_039_ResponseCode(isoClone.getISO_039_ResponseCode());
	    			iso.setISO_039p_ResponseDetail(isoClone.getISO_039p_ResponseDetail());
	    		}
	    		iso.setWsIso_LogStatus(isoClone.getWsIso_LogStatus());
				iso.setWsTempAut(isoClone.getWsTempAut());
				
    		}else{
    			
    			iso.setISO_039_ResponseCode(isoClone.getISO_039_ResponseCode());
    			iso.setISO_039p_ResponseDetail(isoClone.getISO_039p_ResponseDetail());
    			iso.setWsIso_LogStatus(isoClone.getWsIso_LogStatus());
    		}
    		
		} catch (Exception e) {
			
			iso.setISO_039_ResponseCode("070");
			iso.setISO_039p_ResponseDetail(GeneralUtils.ExceptionToString("ERROR EN PROCESOS", e, false));
			log = new Logger();
			log.WriteLogMonitor("Error modulo FitIsAut::QueryBalanceExe ", TypeMonitor.error, e);
		}
    	return iso;
    }
    
    public wIso8583 QueryBalanceFit1Exe(wIso8583 iso){
    	
    	IsoRetrievalTransaction sql = null;
    	try {
		
    		sql = new IsoRetrievalTransaction();
    		wIso8583 isoClone = iso.cloneWiso(iso);
    		isoClone = sql.RetrieveSavingsAccountFit1(isoClone);
    		if(isoClone.getISO_039_ResponseCode().equals("000")){ /*Puse recien valida que no exista timeout*/
	    		if(isoClone.getISO_039_ResponseCode().equals("000")){
	    			
	    			double saldo = Double.parseDouble(isoClone.getISO_121_ExtendedData());
	    			iso.setISO_054_AditionalAmounts(String.valueOf(saldo));
	    			iso.setISO_104_TranDescription(saldo > 0 ? "D":"C");
	    		}else{
	    			
	    			iso.setISO_039_ResponseCode(isoClone.getISO_039_ResponseCode());
	    			iso.setISO_039p_ResponseDetail(isoClone.getISO_039p_ResponseDetail());
	    		}
	    		iso.setWsIso_LogStatus(isoClone.getWsIso_LogStatus());
				iso.setWsTempAut(isoClone.getWsTempAut());
				
    		}else{
    			
    			iso.setISO_039_ResponseCode(isoClone.getISO_039_ResponseCode());
    			iso.setISO_039p_ResponseDetail(isoClone.getISO_039p_ResponseDetail());
    			iso.setWsIso_LogStatus(isoClone.getWsIso_LogStatus());
    		}
    		
		} catch (Exception e) {
			
			iso.setISO_039_ResponseCode("070");
			iso.setISO_039p_ResponseDetail(GeneralUtils.ExceptionToString("ERROR EN PROCESOS", e, false));
			log = new Logger();
			log.WriteLogMonitor("Error modulo FitIsAut::QueryBalanceFit1Exe ", TypeMonitor.error, e);
		}
    	return iso;
    }
     
    private wIso8583 getCpersona(wIso8583 iso){
    	
    	try {
			
    		
    		iso.getWsTransactionConfig().setProccodeTransactionFit("01-0003-01-CON_BIMO");
    		iso.setISO_011_SysAuditNumber(GeneralUtils.GetSecuencial(iso.getISO_011_SysAuditNumber().length()));
    		FitCoreProcessor processor = new FitCoreProcessor(iso);
    		Detail response = processor.ProcessingTransactionFitCore();
    		if(iso.getISO_039_ResponseCode().equals("000")){
    			
    			FitQueryables cpersona = new FitQueryables("TPERSONA", 0, "CPERSONA", response);
    			cpersona.join();
    			if(cpersona.getReturnValue() != null){
    				
    				iso.setISO_124_ExtendedData(String.valueOf(cpersona.getReturnValue()));//Aasignacion cpersona
    				iso.setISO_039_ResponseCode("000");
    				iso.setISO_039p_ResponseDetail("TRANSACCION EXITOSA");
    				
    			}else{
    				
    				iso.setISO_039_ResponseCode("100");
    				iso.setISO_039p_ResponseDetail("LA PERSONA NO EXISTE");
    			}
    		}
    		
		} catch (Exception e) {
			
			iso.setISO_039_ResponseCode("070");
			iso.setISO_039p_ResponseDetail(GeneralUtils.ExceptionToString("ERROR EN PROCESOS", e, false));
			log = new Logger();
			log.WriteLogMonitor("Error modulo FitIsAut::getCpersona ", TypeMonitor.error, e);
		}
    	
    	return iso;
    }
    
    public wIso8583 getUserWebinfoExe(wIso8583 iso) {
    	
    	iso = addIsoAdditionalRows(new Ref<wIso8583>(iso));
    	wIso8583 isoClone = null;
    	try {
			
    		isoClone = (wIso8583) iso.clone();
			isoClone.setWsTransactionConfig((TransactionConfiguration)iso.getWsTransactionConfig().CloneObject());
			isoClone.getWsTransactionConfig().setProccodeTransactionFit(Arrays.asList(isoClone.getWsTransactionConfig()
					.getProccodeTransactionFit().split("\\|")).get(0));
			iso.getTickAut().reset();
			iso.getTickAut().start();
				isoClone = getUserWebinfo(isoClone);
			if(iso.getTickAut().isStarted())
				iso.getTickAut().stop();
			if(isoClone.getISO_039_ResponseCode().equals("000")) {
				
				iso.setISO_023_CardSeq(isoClone.getISO_023_CardSeq());
				iso.setISO_034_PANExt(isoClone.getISO_034_PANExt());
				iso.setISO_090_OriginalData(isoClone.getISO_090_OriginalData());
				iso.setISO_055_EMV(isoClone.getISO_055_EMV());
				iso.setISO_035_Track2(isoClone.getISO_035_Track2());
				iso.setISO_036_Track3(isoClone.getISO_036_Track3());
				iso.setISO_120_ExtendedData(isoClone.getISO_120_ExtendedData());
				iso.setISO_121_ExtendedData(isoClone.getISO_121_ExtendedData());
				iso.setISO_122_ExtendedData(isoClone.getISO_122_ExtendedData());
				iso.setISO_123_ExtendedData(isoClone.getISO_123_ExtendedData());
				iso.setISO_124_ExtendedData(isoClone.getISO_124_ExtendedData());
				

				iso.setISO_039_ResponseCode("000");
				iso.setISO_039p_ResponseDetail("TRANSACCION EXITOSA");
				
			}else {
				
				iso.setISO_039_ResponseCode(isoClone.getISO_039_ResponseCode());
				iso.setISO_039p_ResponseDetail(isoClone.getISO_039p_ResponseDetail());
			}
			iso.setWsIso_LogStatus(isoClone.getWsIso_LogStatus());
			
		} catch (Exception e) {
			
			if(iso.getTickAut().isStarted())
				iso.getTickAut().stop();
			log = new Logger();
			log.WriteLogMonitor("Error modulo FitIsAut::getUserWebinfoExe ", TypeMonitor.error, e);
			
		}finally {
			
			iso.setWsTempAut((iso.getTickAut().getTime(TimeUnit.MILLISECONDS)/1000.0));
		}
    	
    	return iso;
    }
    
    public wIso8583 getUserWebinfo(wIso8583 iso){
    	
    	iso = addIsoAdditionalRows(new Ref<wIso8583>(iso));
    	try {
			
    		FitCoreProcessor processor = new FitCoreProcessor(iso);
    		Detail response = processor.ProcessingTransactionFitCore();
    		iso.setISO_041_CardAcceptorID("001");
    		
    		
    		Object value = (Object) GeneralUtils.asStream
					   (GeneralUtils.asStream(response.getTables().iterator(), true)
					  .filter(p -> p.getName().equalsIgnoreCase("TPERSONA"))
					  .findFirst().orElseGet(()-> null).getRecords().iterator(), true)
					  .filter(x -> x.getNumber() == 0)
					  .findFirst().orElseGet(()-> null);

    		
    		if(iso.getISO_039_ResponseCode().equals("000") && value != null){
    			
    			FitQueryables cpersona = new FitQueryables("TPERSONA", 0, "CPERSONA", response);
    			FitQueryables cusuario = new FitQueryables("TPERSONA", 0, "CODIGOUSUARIO", response);
    			FitQueryables cestatusUsuario = new FitQueryables("TPERSONA", 0, "CESTATUSUSUARIO", response);
    			FitQueryables nombreUsuario = new FitQueryables("TPERSONA", 0, "NOMBRELEGAL", response);
    			FitQueryables codigoCaja = new FitQueryables("TPERSONA", 0, "CODIGOCAJA", response);
    			FitQueryables ctTipoBanca = new FitQueryables("TPERSONA", 0, "CTIPOBANCA", response);
    			
    			
    			FitQueryables versionControlTP0 = new FitQueryables("TPERSONA", 0, "VERSIONCONTROL", "tp0", response);
    			FitQueryables versionControlTU0 = new FitQueryables("TPERSONA", 0, "VERSIONCONTROL", "tu0", response);
    			FitQueryables versionControlTMU0 = new FitQueryables("TPERSONA", 0, "VERSIONCONTROL", "tmu0", response);
    			FitQueryables codUsuarioTu0 = new FitQueryables("TPERSONA", 0, "CUSUARIO", "tu0", response);
    			FitQueryables codUsuarioTmu0 = new FitQueryables("TPERSONA", 0, "CUSUARIO", "tmu0", response);
    			
    			Thread vTp0 = new Thread(versionControlTP0.runQueryFieldsWithAlias());
    			Thread vTu0 = new Thread(versionControlTU0.runQueryFieldsWithAlias());
    			Thread vTmu0 = new Thread(versionControlTMU0.runQueryFieldsWithAlias());
    			Thread uTu0 = new Thread(codUsuarioTu0.runQueryFieldsWithAlias());
    			Thread uTmu0 = new Thread(codUsuarioTmu0.runQueryFieldsWithAlias());
    			
    			vTp0.start();
    			vTu0.start();
    			uTu0.start();
    			uTmu0.start();
    			vTmu0.start();
    			ctTipoBanca.join();
    			codigoCaja.join();
    			cpersona.join();
    			cusuario.join();
    			cestatusUsuario.join();
    			nombreUsuario.join();
    			vTp0.join();
    			vTu0.join();
    			vTmu0.join();
    			uTu0.join();
    			uTmu0.join();
    			
    			if(cpersona.getReturnValue() != null){
    				
    				iso.setISO_023_CardSeq(String.valueOf(ctTipoBanca.getReturnValue()));
    				iso.setISO_034_PANExt(String.valueOf(nombreUsuario.getReturnValue()));
    				iso.setISO_090_OriginalData(String.valueOf(versionControlTU0.getReturnValue()));
    				iso.setISO_035_Track2(String.valueOf(versionControlTMU0.getReturnValue()));
    				iso.setISO_036_Track3(String.valueOf(codigoCaja.getReturnValue()));
    				iso.setISO_055_EMV(String.valueOf(codUsuarioTu0.getReturnValue()));
    				iso.setISO_120_ExtendedData(String.valueOf(cusuario.getReturnValue()));
    				iso.setISO_121_ExtendedData(String.valueOf(cestatusUsuario.getReturnValue()));
    				iso.setISO_122_ExtendedData(String.valueOf(codUsuarioTmu0.getReturnValue()));
    				iso.setISO_123_ExtendedData(String.valueOf(versionControlTP0.getReturnValue()));
    				iso.setISO_124_ExtendedData(String.valueOf(cpersona.getReturnValue()));//Aasignacion cpersona
    				iso.setISO_039_ResponseCode("000");
    				iso.setISO_039p_ResponseDetail("TRANSACCION EXITOSA");
    				
    			}else{
    				
    				iso.setISO_039_ResponseCode("116");
    				iso.setISO_039p_ResponseDetail("LA PERSONA CON CEDULA "+ iso.getISO_002_PAN() +" NO TIENE USUARIO WEB");
    			}
    		}else {
    			
    			iso.setISO_039_ResponseCode("116");
				iso.setISO_039p_ResponseDetail("LA PERSONA CON CEDULA "+ iso.getISO_002_PAN() +" NO TIENE USUARIO WEB");
    		}
    		
		} catch (Exception e) {
			
			iso.setISO_039_ResponseCode("070");
			iso.setISO_039p_ResponseDetail(GeneralUtils.ExceptionToString("ERROR EN PROCESOS", e, false));
			log = new Logger();
			log.WriteLogMonitor("Error modulo FitIsAut::getUserWebinfo ", TypeMonitor.error, e);
		}
    	
    	return iso;
    }
    
    public wIso8583 getCpersonaExe(wIso8583 iso) {
    	
    	wIso8583 isoClone = null;
    	try {
			
    		isoClone = (wIso8583) iso.clone();
    		isoClone.setWsTransactionConfig((TransactionConfiguration)iso.getWsTransactionConfig().CloneObject());
    		iso.getTickAut().reset();
			iso.getTickAut().start();
    			isoClone = getCpersona(isoClone);
    		if(iso.getTickAut().isStarted())
				iso.getTickAut().stop();
    		if(isoClone.getISO_039_ResponseCode().equals("000")) {
    			
    			iso.setISO_124_ExtendedData(isoClone.getISO_124_ExtendedData());
    			
    		}else {
    			
    			iso.setISO_039_ResponseCode(isoClone.getISO_039_ResponseCode());
    			iso.setISO_039p_ResponseDetail(isoClone.getISO_039p_ResponseDetail());
    		}
    		
		} catch (Exception e) {
			if(iso.getTickAut().isStarted())
				iso.getTickAut().stop();
			iso.setISO_039_ResponseCode("909");
			iso.setISO_039p_ResponseDetail(GeneralUtils.ExceptionToString("ERROR EN PROCESOS", e, false));
			log = new Logger();
			log.WriteLogMonitor("Error modulo FitIsAut::getCpersonaExe ", TypeMonitor.error, e);
			
		}finally {
			
			iso.setWsTempAut((iso.getTickAut().getTime(TimeUnit.MILLISECONDS)/1000.0));
		}
    	
    	return iso;
    }
    
    public wIso8583 getCpersonaExeQ(wIso8583 iso) {
    	
    	wIso8583 isoClone = null;
    	IsoRetrievalTransaction sql = null;
    	try {
			
    		sql = new IsoRetrievalTransaction();
    		isoClone = (wIso8583) iso.clone();
    		isoClone.setWsTransactionConfig((TransactionConfiguration)iso.getWsTransactionConfig().CloneObject());
    		iso.getTickAut().reset();
			iso.getTickAut().start();
    			isoClone = sql.getCpersonaFit3(isoClone);
    		if(iso.getTickAut().isStarted())
				iso.getTickAut().stop();
    		if(isoClone.getISO_039_ResponseCode().equals("000")) {
    			
    			iso.setISO_124_ExtendedData(isoClone.getISO_124_ExtendedData());
    			
    		}else {
    			
    			iso.setISO_039_ResponseCode(isoClone.getISO_039_ResponseCode());
    			iso.setISO_039p_ResponseDetail(isoClone.getISO_039p_ResponseDetail());
    		}
    		
		} catch (Exception e) {
			if(iso.getTickAut().isStarted())
				iso.getTickAut().stop();
			iso.setISO_039_ResponseCode("909");
			iso.setISO_039p_ResponseDetail(GeneralUtils.ExceptionToString("ERROR EN PROCESOS", e, false));
			log = new Logger();
			log.WriteLogMonitor("Error modulo FitIsAut::getCpersonaExeQ ", TypeMonitor.error, e);
			
		}finally {
			
			iso.setWsTempAut((iso.getTickAut().getTime(TimeUnit.MILLISECONDS)/1000.0));
		}
    	
    	return iso;
    }
    
    private wIso8583 getCpersonaFit1(wIso8583 iso){
    	
    	IsoRetrievalTransaction sql = null;
    	try {
			
    		sql =  new IsoRetrievalTransaction();
    		iso = sql.getCpersonaFit1(iso);
    		
		} catch (Exception e) {
			
			iso.setISO_039_ResponseCode("070");
			iso.setISO_039p_ResponseDetail(GeneralUtils.ExceptionToString("ERROR EN PROCESOS", e, false));
			log = new Logger();
			log.WriteLogMonitor("Error modulo FitIsAut::getCpersonaFit1 ", TypeMonitor.error, e);
		}
    	return iso;
    }
    
    private wIso8583 validateBasicAccount(wIso8583 iso){
    	
    	try {
			
    		iso = getCtasTipoFit(iso);
    		if(iso.getISO_039_ResponseCode().equals("000") || iso.getISO_039_ResponseCode().equals("100")){
    			iso.setISO_039_ResponseCode("000");
    		}
    		
		} catch (Exception e) {
			
			iso.setISO_039_ResponseCode("070");
			iso.setISO_039p_ResponseDetail(GeneralUtils.ExceptionToString("ERROR EN PROCESOS DE ENROLAMIENTO NO BANCARIZADO", e, false));
		}
    	return iso;
    }
    
    private wIso8583 validateBasicAccountFit1(wIso8583 iso) {
    	
    	try {
				
	    		iso = getCtasTipoFit1(iso);
	    		if(iso.getISO_039_ResponseCode().equals("000") || iso.getISO_039_ResponseCode().equals("100")){
	    			iso.setISO_039_ResponseCode("000");
	    		}
	    		
			} catch (Exception e) {
				
				iso.setISO_039_ResponseCode("070");
				iso.setISO_039p_ResponseDetail(GeneralUtils.ExceptionToString("ERROR EN PROCESOS DE ENROLAMIENTO NO BANCARIZADO", e, false));
			}
	    	return iso;
	}
    
	private wIso8583 getCtasTipoFit1(wIso8583 iso) {
		
		IsoRetrievalTransaction sql = null;
		wIso8583 isoClone = null;
		try {
			
			sql = new IsoRetrievalTransaction();
			
			isoClone = (wIso8583)iso.clone();
			isoClone.setWsTransactionConfig((TransactionConfiguration)iso.getWsTransactionConfig().CloneObject());
			
			isoClone = sql.getAccountsBasicBimoFit1(isoClone);
			if(isoClone.getISO_039_ResponseCode().equals("000")) {
				
				iso.setISO_102_AccountID_1(isoClone.getISO_102_AccountID_1());
				iso.setISO_039_ResponseCode("000");
				iso.setISO_039p_ResponseDetail("TRANSACCION EXITOSA");
				
			}else {
			
				iso.setISO_039_ResponseCode(isoClone.getISO_039_ResponseCode());
				iso.setISO_039p_ResponseDetail(isoClone.getISO_039p_ResponseDetail());
			}
			
			iso.setWsIso_LogStatus(isoClone.getWsIso_LogStatus());
			iso.setWsISO_TranDatetimeResponse(isoClone.getWsISO_TranDatetimeResponse());
			
		} catch (Exception e) {
			
			iso.setISO_039_ResponseCode("070");
			iso.setISO_039p_ResponseDetail(GeneralUtils.ExceptionToString("ERROR EN PROCESOS", e, false));
			log = new Logger();
			log.WriteLogMonitor("Error modulo FitIsAut::getCtasTipoFit1 ", TypeMonitor.error, e);
		}
		return iso;
	}
	
	public wIso8583 getValorAPagarPrestExe(wIso8583 iso) {
		
		wIso8583 isoClone = null;
		try {
			
			isoClone = (wIso8583) iso.clone();
			isoClone.setWsTransactionConfig((TransactionConfiguration)iso.getWsTransactionConfig().CloneObject());
			iso.getTickAut().reset();
			iso.getTickAut().start();
			isoClone = getInfoValoresPrestamo(isoClone);
			if(iso.getTickAut().isStarted())
				iso.getTickAut().stop();
			if(isoClone.getISO_039_ResponseCode().equals("000")) {
				
				@SuppressWarnings("unchecked")
				List<Iterables> it = (List<Iterables>) SerializationObject
    					.XMLToObject(isoClone.getISO_114_ExtendedData());
				String valorTotalPago, nroCuota, fechaVcto = StringUtils.Empty();
				
				if(it != null) {
				
					String valorAPagar = it.stream()
							 .filter(p -> p.getIterarors().get("ESTATUS").equalsIgnoreCase("Vigente"))
							 .findFirst().orElseGet(() -> null) == null ? StringUtils.Empty() : 
						       it.stream()
							 .filter(p -> p.getIterarors().get("ESTATUS").equalsIgnoreCase("Vigente"))
							 .findFirst().orElseGet(() -> null)
							 .getIterarors().get("TOTAL");
						
				   int longIt = 0;	 
				   if(StringUtils.IsNullOrEmpty(valorAPagar)) { //No hay registros vigentes
					   
					   /*El Prestamo esta totalmente vencido en todas sus coutas*/
					   longIt = it.size();
					   valorTotalPago = isoClone.getISO_115_ExtendedData();
					   fechaVcto = it.get(longIt -1).getIterarors().get("FVENCIMIENTO");
					   nroCuota = it.get(longIt -1).getIterarors().get("CUOTA");
					   
				   }else {
					   
					   longIt = it.size();
					   
					   /*El Credito se esta pagando correctamente*/
					   if(longIt == 2) {
						   
						   nroCuota = it.stream()
									 .filter(p -> p.getIterarors().get("ESTATUS").equalsIgnoreCase("Vigente"))
									 .findFirst().orElseGet(() -> null)
									 .getIterarors().get("CUOTA");
						   
						   fechaVcto = it.stream()
									 .filter(p -> p.getIterarors().get("ESTATUS").equalsIgnoreCase("Vigente"))
									 .findFirst().orElseGet(() -> null)
									 .getIterarors().get("FVENCIMIENTO");
						   
						   valorTotalPago = it.stream()
									 .filter(p -> p.getIterarors().get("ESTATUS").equalsIgnoreCase("Vigente"))
									 .findFirst().orElseGet(() -> null)
									 .getIterarors().get("TOTAL");
					   }else {
					  /*El Credito tiene algunas coutas atrasadas*/
						   
						   longIt = it.size();
						   valorTotalPago = isoClone.getISO_115_ExtendedData();
						   
						   nroCuota = it.stream()
									 .filter(p -> p.getIterarors().get("ESTATUS").equalsIgnoreCase("Vigente"))
									 .findFirst().orElseGet(() -> null)
									 .getIterarors().get("CUOTA");
						   
						   fechaVcto = it.stream()
									 .filter(p -> p.getIterarors().get("ESTATUS").equalsIgnoreCase("Vigente"))
									 .findFirst().orElseGet(() -> null)
									 .getIterarors().get("FVENCIMIENTO");
						   
					   }
					   
				   }
					
					iso.setISO_120_ExtendedData(isoClone.getISO_102_AccountID_1() + "|" + nroCuota + "|" + valorTotalPago + "|" + fechaVcto);
					iso.setISO_123_ExtendedData(isoClone.getISO_102_AccountID_1() + "|" + isoClone.getISO_123_ExtendedData());
					
				}else {
					
					iso.setISO_039_ResponseCode("909");
					iso.setISO_039p_ResponseDetail("ERROR AL PROCESAR INFORMACION OBTENIDA DE PRESTAMOS");
				}
				
			}else {
				
				iso.setISO_039_ResponseCode(isoClone.getISO_039_ResponseCode());
				iso.setISO_039p_ResponseDetail(isoClone.getISO_039p_ResponseDetail());
			}
			
			
		} catch (Exception e) {
			
			if(iso.getTickAut().isStarted())
				iso.getTickAut().stop();
			iso.setISO_039_ResponseCode("909");
			iso.setISO_039p_ResponseDetail(GeneralUtils.ExceptionToString("ERROR EN PROCESOS", e, false));
			log = new Logger();
			log.WriteLogMonitor("Error modulo FitIsAut::getValorAPagarPrestExe ", TypeMonitor.error, e);
			
		}finally {
			iso.setWsTempAut((iso.getTickAut().getTime(TimeUnit.MILLISECONDS)/1000.0));
		}
		
		return iso;
	}
	
    public wIso8583 getCtasPrestamosPersonaExe(wIso8583 iso) {
		
		wIso8583 isoClone = null;
		try {
			
			isoClone = (wIso8583) iso.clone();
			isoClone.setWsTransactionConfig((TransactionConfiguration)iso.getWsTransactionConfig().CloneObject());
			isoClone.getWsTransactionConfig().setProccodeTransactionFit(Arrays.asList(isoClone.getWsTransactionConfig()
					.getProccodeTransactionFit().split("\\|")).get(0));
			iso.getTickAut().reset();
			iso.getTickAut().start();
			isoClone = getCtasPrestamosPersona(isoClone);
			if(iso.getTickAut().isStarted())
				iso.getTickAut().stop();
			if(isoClone.getISO_039_ResponseCode().equals("000")) {
				
				@SuppressWarnings("unchecked")
				List<Iterables> it = (List<Iterables>) SerializationObject
    					.XMLToObject(isoClone.getISO_114_ExtendedData());
				if(it != null) {
				
					/*Filtracion de Cuentas Credito*/
					/*Cambio temporal ya no se filtre por productos en credito pero queda lista la opcion*/
					/*List<Iterables> filterProducto =  it.stream()
	                        .filter(p -> p.getIterarors().get("CGRUPOPRODUCTO||tcuenta0.CPRODUCTO")
	                        .equalsIgnoreCase(iso.getISO_090_OriginalData()))
	                        .peek(Objects::requireNonNull)
	                        .collect(Collectors.toList());*/
					List<Iterables> filterProducto  = it;
					
					
	                if(filterProducto != null && filterProducto.size() > 0) {        
						for (Iterables iterables : filterProducto) {
							
							iso.setISO_023_CardSeq(String.valueOf(filterProducto.size()));
							iso.setISO_121_ExtendedData(iso.getISO_121_ExtendedData() + iterables.getIterarors().get("CCUENTA") + "|");
							/*Cambio temporal ya no se filtre por productos en credito pero queda lista la opcion*/
							//iso.setISO_122_ExtendedData(iterables.getIterarors().get("DESCRIPCION"));
							iso.setISO_034_PANExt(iterables.getIterarors().get("NOMBRELEGAL"));
							iso.setISO_122_ExtendedData(iso.getISO_122_ExtendedData() + iterables.getIterarors().get("DESCRIPCION") + "|");
							/*2018/11/13 Aumente*/
							/*iso.setISO_123_ExtendedData(iso.getISO_123_ExtendedData() + iterables.getIterarors().get("CCUENTA") + "|" + 
							iterables.getIterarors().get("CESTATUSCUENTA") + "|" 
									+ iterables.getIterarors().get("CCONDICIONOPERATIVA") + "^");*/
							iso.setISO_123_ExtendedData(iso.getISO_123_ExtendedData() + iterables.getIterarors().get("CCONDICIONOPERATIVA") + "|");
						}
						iso.setISO_121_ExtendedData(StringUtils.trimEnd(iso.getISO_121_ExtendedData(),"|"));
						iso.setISO_123_ExtendedData(StringUtils.trimEnd(iso.getISO_123_ExtendedData(),"|"));
						
						/*Cambio temporal ya no se filtre por productos en credito pero queda lista la opcion*/
						iso.setISO_034_PANExt(StringUtils.trimEnd(iso.getISO_034_PANExt(),"|"));
						iso.setISO_122_ExtendedData(StringUtils.trimEnd(iso.getISO_122_ExtendedData(),"|"));
						
	                }else {
	                	
	                	iso.setISO_039_ResponseCode("116");
						iso.setISO_039p_ResponseDetail("LA PERSONA CON CEDULA: " + iso.getISO_002_PAN() 
						+ " NO TIENE CREDITOS DEL PRODUCTO: " +  iso.getISO_090_OriginalData());
	                }
					
				}else {
					
					iso.setISO_039_ResponseCode("909");
					iso.setISO_039p_ResponseDetail("ERROR AL PROCESAR INFORMACION OBTENIDA DE CUENTAS DE PRESTAMOS DE LA PERSONA");
				}
				
			}else {
				
				iso.setISO_039_ResponseCode(isoClone.getISO_039_ResponseCode());
				iso.setISO_039p_ResponseDetail(isoClone.getISO_039p_ResponseDetail());
			}
			
			
		} catch (Exception e) {
			
			if(iso.getTickAut().isStarted())
				iso.getTickAut().stop();
			iso.setISO_039_ResponseCode("909");
			iso.setISO_039p_ResponseDetail(GeneralUtils.ExceptionToString("ERROR EN PROCESOS", e, false));
			log = new Logger();
			log.WriteLogMonitor("Error modulo FitIsAut::getValorAPagarPrestExe ", TypeMonitor.error, e);
			
		}finally {
			iso.setWsTempAut((iso.getTickAut().getTime(TimeUnit.MILLISECONDS)/1000.0));
		}
		
		return iso;
	}
    
    public wIso8583 getGarantiasPrestamoExe(wIso8583 iso) {
		
    	iso = addIsoAdditionalRows(new Ref<wIso8583>(iso));
		wIso8583 isoClone = null;
		try {
			
			isoClone = (wIso8583) iso.clone();
			isoClone.setWsTransactionConfig((TransactionConfiguration)iso.getWsTransactionConfig().CloneObject());
			
			iso.getTickAut().reset();
			iso.getTickAut().start();
			isoClone = getGarantiasPrestamo(isoClone);
			if(iso.getTickAut().isStarted())
				iso.getTickAut().stop();
			if(isoClone.getISO_039_ResponseCode().equals("000")) {
				
				@SuppressWarnings("unchecked")
				List<Iterables> it = (List<Iterables>) SerializationObject
    					.XMLToObject(isoClone.getISO_114_ExtendedData());
				if(it != null) {
				
					for (Iterables iterables : it) {
						
						iso.setISO_121_ExtendedData(iso.getISO_121_ExtendedData() + iterables.getIterarors().get("CCUENTA") + "|");
					}
					
					iso.setISO_121_ExtendedData(StringUtils.trimEnd(iso.getISO_121_ExtendedData(),"|"));
					iso.setISO_039_ResponseCode("000");
					iso.setISO_039p_ResponseDetail("TRANSACCION EXITOSA");
					
				}else {
					
					iso.setISO_039_ResponseCode("909");
					iso.setISO_039p_ResponseDetail("ERROR AL PROCESAR INFORMACION OBTENIDA DE GARANTIAS DE PRESTAMOS DE LA PERSONA");
				}
				
			}else {
				
				iso.setISO_039_ResponseCode(isoClone.getISO_039_ResponseCode());
				iso.setISO_039p_ResponseDetail(isoClone.getISO_039p_ResponseDetail());
			}
			
			
		} catch (Exception e) {
			
			if(iso.getTickAut().isStarted())
				iso.getTickAut().stop();
			iso.setISO_039_ResponseCode("909");
			iso.setISO_039p_ResponseDetail(GeneralUtils.ExceptionToString("ERROR EN PROCESOS", e, false));
			log = new Logger();
			log.WriteLogMonitor("Error modulo FitIsAut::getGarantiasPrestamoExe ", TypeMonitor.error, e);
			
		}finally {
			iso.setWsTempAut((iso.getTickAut().getTime(TimeUnit.MILLISECONDS)/1000.0));
		}
		
		return iso;
	}
	
    public wIso8583 getInfoValoresPrestamo(wIso8583 iso) {
    	
    	iso.getTickAut().reset();
		iso.getTickAut().start();
	    	FitCoreProcessor processor = new FitCoreProcessor(iso);
			Detail response = processor.ProcessingTransactionFitCore();
		if(iso.getTickAut().isStarted())
			iso.getTickAut().stop();
		FitQueryables qry = null;
    	try {
			
			if(response != null){
				
			 if(response.getResponse() != null){	
				 
					if(response.getResponse().getCode().equals("0")){
						
						qry = new FitQueryables();
						Object valor = qry.QueryDetailFitCTL("SALDOVENCIDO", response);
						/*Pongo los valores del CTL SALDOVENCIDO cuando el prestamo esta vencido totalmente*/
						iso.setISO_115_ExtendedData(String.valueOf(valor));
						
						
						qry = new FitQueryables();
						Object cuenta = qry.QueryDetailFitCTL("CCUENTA", response);
						/*Pongo los valores del CTL SALDOVENCIDO cuando el prestamo esta vencido totalmente*/
						iso.setISO_120_ExtendedData(String.valueOf(cuenta));
						
						qry = new FitQueryables();
						List<Iterables> it = qry.QueryDetailFitBankIterator("E-CUOTAS", 40, 
							       new String[]{ "CUOTA","FVENCIMIENTO","ESTATUS","TOTAL",}, response);
					
					if(it != null){
						
						if(it.size() > 0){
							
							iso.setISO_114_ExtendedData(
									SerializationObject.ObjectToXML((Serializable) it));						
						}else {
							
							iso.setISO_039_ResponseCode("116");
							iso.setISO_039p_ResponseDetail("LA CUENTA: " + iso.getISO_102_AccountID_1()
							 + " NO POSEE INFORMACION A VISUALIZAR");
						}
					
					}else {
						
						iso.setISO_039_ResponseCode("909");
						iso.setISO_039p_ResponseDetail(qry.getError());
					}
					
					/*Caculo total del Prestamo a la fecha*/
					iso.setISO_123_ExtendedData(ValorTotalPrestamo(response));
				 }
			 }
		  }
    		
    		
		} catch (Exception e) {
			
			if(iso.getTickAut().isStarted())
				iso.getTickAut().stop();
			
			iso.setISO_039_ResponseCode("909");
			iso.setISO_039p_ResponseDetail(GeneralUtils.ExceptionToString("ERROR EN PROCESOS", e, false));
			log = new Logger();
			log.WriteLogMonitor("Error modulo FitIsAut::getInfoValoresPrestamo ", TypeMonitor.error, e);
		}finally {
			
			iso.setWsTempAut((iso.getTickAut().getTime(TimeUnit.MILLISECONDS)/1000.0));
		}
    	return iso;
    }
    
    private String ValorTotalPrestamo(Detail detail) {
    	
    	String data = "0.00";
    	DecimalFormatSymbols simbolo = new DecimalFormatSymbols();
	    simbolo.setDecimalSeparator('.');
		DecimalFormat df = new DecimalFormat("0.00", simbolo);
    	
    	try {
			
    		FitQueryables qry = null;
    		qry = new FitQueryables();
			List<Iterables> it = qry.QueryDetailFitBankIterator("SALDOS", 40, 
				       new String[]{ "SALDOVIGENTE","SALDOVENCIDO","SALDOPORVENCER",}, detail);
			if(it != null) {
				
				double saldoVigente = 0, saldoVencido = 0, saldoPorVencer = 0, total = 0;
				for (Iterables iter : it) {
					
					saldoVigente += iter.getIterarors().get("SALDOVIGENTE").equalsIgnoreCase("null") ? 0 
							       : Double.parseDouble(iter.getIterarors().get("SALDOVIGENTE"));
					
					saldoVencido += iter.getIterarors().get("SALDOVENCIDO").equalsIgnoreCase("null") ? 0 
						       : Double.parseDouble(iter.getIterarors().get("SALDOVENCIDO"));
					
					saldoPorVencer += iter.getIterarors().get("SALDOPORVENCER").equalsIgnoreCase("null") ? 0 
						       : Double.parseDouble(iter.getIterarors().get("SALDOPORVENCER"));
					
				}
				total = saldoVigente + saldoVencido + saldoPorVencer;
				data = df.format(total);
			}
    		
		} catch (Exception e) {
		
			log = new Logger();
			log.WriteLogMonitor("Error modulo FitIsAut::ValorTotalPrestamo ", TypeMonitor.error, e);
		}
    	return data;
    }
    
    private wIso8583 getCtasTipoFit(wIso8583 iso){
    	
    	try {
			
    		
    		iso.getWsTransactionConfig().setProccodeTransactionFit("01-0003-01-CON_GETCTA");
    		iso.setISO_011_SysAuditNumber(GeneralUtils.GetSecuencial(iso.getISO_011_SysAuditNumber().length()));
    		FitCoreProcessor processor = new FitCoreProcessor(iso);
    		Detail response = processor.ProcessingTransactionFitCore();
    		if(iso.getISO_039_ResponseCode().equals("000")){
    			
    			if(response != null){
	    			FitQueryables ccuenta = new FitQueryables("TCUENTASPERSONA", 0, "CCUENTA", response);
	    			ccuenta.join();
	    			if(ccuenta.getReturnValue() != null){
	    				
	    				iso.setISO_102_AccountID_1(String.valueOf(ccuenta.getReturnValue()));//Aasignacion ccuenta
	    				iso.setISO_039_ResponseCode("000");
	    				iso.setISO_039p_ResponseDetail("TRANSACCION EXITOSA");
	    				
	    			}else{
	    				
	    				iso.setISO_039_ResponseCode("100");
	    				iso.setISO_039p_ResponseDetail("SOCIO CLIENTE NO POSEE CUENTA DEL TIPO CONSULTADO");
	    			}
    			}else{
    				
    				iso.setISO_039_ResponseCode("909");
    				iso.setISO_039p_ResponseDetail("ERROR AL OBTENER CUENTAS DEL CLIENTE");
    			}
    		}
    		
		} catch (Exception e) {
			
			iso.setISO_039_ResponseCode("070");
			iso.setISO_039p_ResponseDetail(GeneralUtils.ExceptionToString("ERROR EN PROCESOS", e, false));
			log = new Logger();
			log.WriteLogMonitor("Error modulo FitIsAut::getCtasTipoFit ", TypeMonitor.error, e);
		}
    	
    	return iso;
    }
    
    public wIso8583 getCtasPrestamosPersona(wIso8583 iso) {
    	
    	iso = addIsoAdditionalRows(new Ref<wIso8583>(iso));
    	FitCoreProcessor processor = new FitCoreProcessor(iso);
		Detail response = processor.ProcessingTransactionFitCore();
		FitQueryables qry = null;
    	try {
			
			if(response != null){
				
			 if(response.getResponse() != null){	
				 
					if(response.getResponse().getCode().equals("0")){
						qry = new FitQueryables();
						List<Iterables> it = qry.QueryDetailFitBankIterator("TCUENTA", 20, 
							       new String[]{"CGRUPOPRODUCTO||tcuenta0.CPRODUCTO", "CCUENTA","DESCRIPCION","NOMBRELEGAL","CESTATUSCUENTA"
							    		   ,"CCONDICIONOPERATIVA"}, response);
					
					if(it != null){
						
						if(it.size() > 0){
							
							iso.setISO_114_ExtendedData(
									SerializationObject.ObjectToXML((Serializable) it));						
						}else {
							
							iso.setISO_039_ResponseCode("116");
							iso.setISO_039p_ResponseDetail("LA CEDULA: " + iso.getISO_002_PAN()
							 + " NO TIENE PRESTAMOS VIGENTES O VENCIDOS");
						}
					
					}else {
						
						iso.setISO_039_ResponseCode("909");
						iso.setISO_039p_ResponseDetail(qry.getError());
					}
				 }
			 }
		  }
    		
    		
		} catch (Exception e) {
			iso.setISO_039_ResponseCode("909");
			iso.setISO_039p_ResponseDetail(GeneralUtils.ExceptionToString("ERROR EN PROCESOS", e, false));
			log = new Logger();
			log.WriteLogMonitor("Error modulo FitIsAut::getCtasPrestamosPersona ", TypeMonitor.error, e);
		}
    	return iso;
    }
    
    public wIso8583 getGarantiasPrestamo(wIso8583 iso) {
    	
    	FitCoreProcessor processor = new FitCoreProcessor(iso);
		Detail response = processor.ProcessingTransactionFitCore();
		FitQueryables qry = null;
    	try {
			
			if(response != null){
				
			 if(response.getResponse() != null){	
				 
					if(response.getResponse().getCode().equals("0")){
						qry = new FitQueryables();
						List<Iterables> it = qry.QueryDetailFitBankIterator("TCUENTASPERSONA", 20, 
							       new String[]{ "CCUENTA"}, response);
					
					if(it != null){
						
						if(it.size() > 0){
							
							iso.setISO_114_ExtendedData(
									SerializationObject.ObjectToXML((Serializable) it));						
						}else {
							
							iso.setISO_039_ResponseCode("116");
							iso.setISO_039p_ResponseDetail("LA CEDULA: " + iso.getISO_002_PAN()
							 + " NO POSEE GARANTIA DE PRESTAMOS");
						}
					
					}else {
						
						iso.setISO_039_ResponseCode("909");
						iso.setISO_039p_ResponseDetail(qry.getError());
					}
				 }
			 }
		  }
    		
    		
		} catch (Exception e) {
			iso.setISO_039_ResponseCode("909");
			iso.setISO_039p_ResponseDetail(GeneralUtils.ExceptionToString("ERROR EN PROCESOS", e, false));
			log = new Logger();
			log.WriteLogMonitor("Error modulo FitIsAut::getGarantiasPrestamo ", TypeMonitor.error, e);
		}
    	return iso;
    }
    
    public wIso8583 getInversionesPersona(wIso8583 iso) {
    	
    	iso = addIsoAdditionalRows(new Ref<wIso8583>(iso));
    	FitCoreProcessor processor = new FitCoreProcessor(iso);
		Detail response = processor.ProcessingTransactionFitCore();
		FitQueryables qry = null;
    	try {
			
			if(response != null){
				
			 if(response.getResponse() != null){	
				 
					if(response.getResponse().getCode().equals("0")){
						qry = new FitQueryables();
						List<Iterables> it = qry.QueryDetailFitBankIterator("VCUENTASPLAZOFIJO", 20, 
							       new String[]{"CGRUPOPRODUCTO||tcuentas1.CPRODUCTO", "CCUENTA","NOMBRELEGAL","MONTO","PLAZO","TASA"
							    		   ,"DESCRIPCIONESTATUSCUENTA","CCONDICIONOPERATIVA","FAPERTURA",
							    		   "FVENCIMIENTO","DESCRIPCIONPRODUCTO"}, response);
					
					if(it != null){
						
						if(it.size() > 0){
							
							iso.setISO_114_ExtendedData(
									SerializationObject.ObjectToXML((Serializable) it));						
						}else {
							
							iso.setISO_039_ResponseCode("116");
							iso.setISO_039p_ResponseDetail("LA CEDULA: " + iso.getISO_002_PAN()
							 + " NO TIENE INVERSIONES A PLAZO FIJO");
						}
					
					}else {
						
						iso.setISO_039_ResponseCode("909");
						iso.setISO_039p_ResponseDetail(qry.getError());
					}
				 }
			 }
		  }
    		
    		
		} catch (Exception e) {
			iso.setISO_039_ResponseCode("909");
			iso.setISO_039p_ResponseDetail(GeneralUtils.ExceptionToString("ERROR EN PROCESOS", e, false));
			log = new Logger();
			log.WriteLogMonitor("Error modulo FitIsAut::getInversionesPersona ", TypeMonitor.error, e);
		}
    	return iso;
    }
    
    public wIso8583 getInfoVencimientoPlazo(wIso8583 iso) {
    	
    	FitCoreProcessor processor = new FitCoreProcessor(iso);
		Detail response = processor.ProcessingTransactionFitCore();
		FitQueryables qry = null;
    	try {
			
			if(response != null){
				
			 if(response.getResponse() != null){	
				 
					if(response.getResponse().getCode().equals("0")){
						
						qry = new FitQueryables();
						List<Iterables> it = qry.QueryDetailFitBankIterator("TCUENTAPLAZO", 20, 
							       new String[]{ "CCUENTA","MONTO","PLAZO","FVENCIMIENTO"}, response);
					
					if(it != null){
						
						if(it.size() > 0){
							
							
							iso.setISO_120_ExtendedData(it.get(0).getIterarors().get("CCUENTA") + "|" +
									it.get(0).getIterarors().get("MONTO") + "|0%|" +it.get(0).getIterarors()
									.get("FVENCIMIENTO").substring(0, 10));
							iso.setISO_121_ExtendedData("DEPOSITOS A PLAZO FIJO");
							
						}else {
							
							iso.setISO_039_ResponseCode("100");
							iso.setISO_039p_ResponseDetail("PROBLEMAS AL RECUPERAR INVERSIONES DE LA CEDULA " + iso.getISO_002_PAN());
						}
					
					}else {
						
						iso.setISO_039_ResponseCode("909");
						iso.setISO_039p_ResponseDetail(qry.getError().toUpperCase());
					}
				 }
			 }
		  }
    		
    		
		} catch (Exception e) {
			iso.setISO_039_ResponseCode("909");
			iso.setISO_039p_ResponseDetail(GeneralUtils.ExceptionToString("ERROR EN PROCESOS", e, false));
			log = new Logger();
			log.WriteLogMonitor("Error modulo FitIsAut::getInfoVencimientoPlazo ", TypeMonitor.error, e);
		}
    	return iso;
    }
    
    
    public wIso8583 getCtasVistaPersona2(wIso8583 iso) {
    	
    	FitCoreProcessor processor = new FitCoreProcessor(iso);
		Detail response = processor.ProcessingTransactionFitCore();
		FitQueryables qry = null;
    	try {
			
			if(response != null){
				
			 if(response.getResponse() != null){	
				 
					if(response.getResponse().getCode().equals("0")){
						
						qry = new FitQueryables();
						List<Iterables> it = qry.QueryDetailFitBankIterator("TCUENTASPERSONA", 20, 
							       new String[]{ "CCUENTA","NOMBRECUENTA","DESCRIPCION","CGRUPOPRODUCTO||tc0.CPRODUCTO"
							    		   ,"CESTATUSCUENTA"}, response);
					
					if(it != null){
						
						if(it.size() > 0){
							
							iso.setISO_114_ExtendedData(
									SerializationObject.ObjectToXML((Serializable) it));						
						}else {
							
							iso.setISO_039_ResponseCode("116");
							iso.setISO_039p_ResponseDetail("LA CEDULA: " + iso.getISO_002_PAN()
							 + " NO TIENE CUENTAS A LA VISTA");
						}
					
					}else {
						
						iso.setISO_039_ResponseCode("909");
						iso.setISO_039p_ResponseDetail(qry.getError());
					}
				 }
			 }
		  }
    		
    		
		} catch (Exception e) {
			iso.setISO_039_ResponseCode("909");
			iso.setISO_039p_ResponseDetail(GeneralUtils.ExceptionToString("ERROR EN PROCESOS", e, false));
			log = new Logger();
			log.WriteLogMonitor("Error modulo FitIsAut::getCtasVistaPersona2 ", TypeMonitor.error, e);
		}
    	return iso;
    }
    
    public wIso8583 getCtasVistaPersona2Exe(wIso8583 iso) {
    	
    	wIso8583 isoClone = null;
    	try {
			
    		isoClone = (wIso8583) iso.clone();
    		isoClone.setWsTransactionConfig((TransactionConfiguration)iso.getWsTransactionConfig().CloneObject());
    		isoClone.getWsTransactionConfig().setProccodeTransactionFit(Arrays.asList(isoClone.getWsTransactionConfig()
    				.getProccodeTransactionFit().split("\\|")).get(0));
    		
    		iso.getTickAut().reset();
			iso.getTickAut().start();
			isoClone = getCtasVistaPersona2(isoClone);
			if(iso.getTickAut().isStarted())
				iso.getTickAut().stop();
			if(isoClone.getISO_039_ResponseCode().equals("000")) {
				
				iso.setISO_114_ExtendedData(isoClone.getISO_114_ExtendedData());
				
			}else {
				
				iso.setISO_039_ResponseCode(isoClone.getISO_039_ResponseCode());
				iso.setISO_039p_ResponseDetail(isoClone.getISO_039p_ResponseDetail());
			}
				
		} catch (Exception e) {
			
			if(iso.getTickAut().isStarted())
				iso.getTickAut().stop();
			iso.setISO_039_ResponseCode("909");
			iso.setISO_039p_ResponseDetail(GeneralUtils.ExceptionToString("ERROR EN PROCESOS", e, false));
			log = new Logger();
			log.WriteLogMonitor("Error modulo FitIsAut::getCtasVistaPersona2Exe ", TypeMonitor.error, e);
		}finally {
			
			iso.setWsTempAut((iso.getTickAut().getTime(TimeUnit.MILLISECONDS)/1000.0));
		}
    	
    	return iso;
    }
    
    
    public wIso8583 getCtasVistaPersona(wIso8583 iso) {
    	
    	iso = addIsoAdditionalRows(new Ref<wIso8583>(iso));
    	FitCoreProcessor processor = new FitCoreProcessor(iso);
		Detail response = processor.ProcessingTransactionFitCore();
		FitQueryables qry = null;
    	try {
			
			if(response != null){
				
			 if(response.getResponse() != null){	
				 
					if(response.getResponse().getCode().equals("0")){
						qry = new FitQueryables();
						List<Iterables> it = qry.QueryDetailFitBankIterator("TCUENTASPERSONA", 20, 
							       new String[]{ "CCUENTA","NOMBRECUENTA","DESCRIPCION",
							    		   "CGRUPOPRODUCTO||tc0.CPRODUCTO","CESTATUSCUENTA"}, response);
					
					if(it != null){
						
						if(it.size() > 0){
							
							iso.setISO_114_ExtendedData(
									SerializationObject.ObjectToXML((Serializable) it));						
						}else {
							
							iso.setISO_039_ResponseCode("116");
							iso.setISO_039p_ResponseDetail("LA CEDULA: " + iso.getISO_002_PAN()
							 + " NO TIENE CUENTAS A LA VISTA");
						}
					
					}else {
						
						iso.setISO_039_ResponseCode("909");
						iso.setISO_039p_ResponseDetail(qry.getError());
					}
				 }
			 }
		  }
    		
    		
		} catch (Exception e) {
			iso.setISO_039_ResponseCode("909");
			iso.setISO_039p_ResponseDetail(GeneralUtils.ExceptionToString("ERROR EN PROCESOS", e, false));
			log = new Logger();
			log.WriteLogMonitor("Error modulo FitIsAut::getCtasVistaPersona ", TypeMonitor.error, e);
		}
    	return iso;
    }
    
    public wIso8583 getCtasVistaPersonaExe(wIso8583 iso) {
		
		wIso8583 isoClone = null;
		try {
			
			isoClone = (wIso8583) iso.clone();
			isoClone.setWsTransactionConfig((TransactionConfiguration)iso.getWsTransactionConfig().CloneObject());
			isoClone.getWsTransactionConfig().setProccodeTransactionFit(Arrays.asList(isoClone.getWsTransactionConfig()
					.getProccodeTransactionFit().split("\\|")).get(0));
			iso.getTickAut().reset();
			iso.getTickAut().start();
			isoClone = getCtasVistaPersona(isoClone);
			if(iso.getTickAut().isStarted())
				iso.getTickAut().stop();
			if(isoClone.getISO_039_ResponseCode().equals("000")) {
				
				@SuppressWarnings("unchecked")
				List<Iterables> it = (List<Iterables>) SerializationObject
    					.XMLToObject(isoClone.getISO_114_ExtendedData());
				if(it != null) {
				
					/*Filtracion de cuentas vista por producto JCOL, 2018/11/21 a peticion de Marianela Reascos 
					 * se filtran los estados 002 vigente y 003 inmovilizada*/
					List<Iterables> filterProducto =  it.stream()
	                        .filter(p -> p.getIterarors().get("CGRUPOPRODUCTO||tc0.CPRODUCTO")
	                        .equalsIgnoreCase(iso.getISO_090_OriginalData()) 
	                         && (p.getIterarors().get("CESTATUSCUENTA").equals("002") 
	                         || p.getIterarors().get("CESTATUSCUENTA").equals("003")))
	                        .peek(Objects::requireNonNull)
	                        .collect(Collectors.toList());
					
					if(filterProducto != null && filterProducto.size() > 0) {
						
						for (Iterables iterables : filterProducto) {
							
							iso.setISO_023_CardSeq(String.valueOf(filterProducto.size()));
							iso.setISO_121_ExtendedData(iso.getISO_121_ExtendedData() + iterables.getIterarors().get("CCUENTA") + "|");
							iso.setISO_034_PANExt(iterables.getIterarors().get("NOMBRECUENTA"));
							iso.setISO_122_ExtendedData(iterables.getIterarors().get("DESCRIPCION"));
						}
						
						iso.setISO_121_ExtendedData(StringUtils.trimEnd(iso.getISO_121_ExtendedData(),"|"));
						
					}else {
						
						iso.setISO_039_ResponseCode("116");
						iso.setISO_039p_ResponseDetail("LA PERSONA CON IDENTIFICACION: " + iso.getISO_002_PAN() 
						+ " NO TIENE PRODUCTOS VISTA: " + iso.getISO_090_OriginalData());
					}
					
				}else {
					
					iso.setISO_039_ResponseCode("909");
					iso.setISO_039p_ResponseDetail("ERROR AL PROCESAR INFORMACION OBTENIDA DE CUENTAS VISTA DE LA PERSONA");
				}
				
			}else {
				
				iso.setISO_039_ResponseCode(isoClone.getISO_039_ResponseCode());
				iso.setISO_039p_ResponseDetail(isoClone.getISO_039p_ResponseDetail());
			}
			
			
		} catch (Exception e) {
			
			if(iso.getTickAut().isStarted())
				iso.getTickAut().stop();
			iso.setISO_039_ResponseCode("909");
			iso.setISO_039p_ResponseDetail(GeneralUtils.ExceptionToString("ERROR EN PROCESOS", e, false));
			log = new Logger();
			log.WriteLogMonitor("Error modulo FitIsAut::getCtasVistaPersonaExe ", TypeMonitor.error, e);
			
		}finally {
			iso.setWsTempAut((iso.getTickAut().getTime(TimeUnit.MILLISECONDS)/1000.0));
		}
		
		return iso;
	}
    
    public wIso8583 QueryClientDocument(wIso8583 iso) {
		
    	iso = addIsoAdditionalRows(new Ref<wIso8583>(iso));
    	IsoRetrievalTransaction sql = null;
		try {
			
			iso.setISO_023_CardSeq(iso.getISO_002_PAN());
			sql = new IsoRetrievalTransaction();
			iso.getTickAut().reset();
			iso.getTickAut().start();
				iso = sql.getInfoSocialBankingRegister(iso);
			if(iso.getTickAut().isStarted())
				iso.getTickAut().stop();
			
		} catch (Exception e) {
			
			if(iso.getTickAut().isStarted())
				iso.getTickAut().stop();
			iso.setISO_039_ResponseCode("909");
			iso.setISO_039p_ResponseDetail(GeneralUtils.ExceptionToString("ERROR EN PROCESOS", e, false));
			log = new Logger();
			log.WriteLogMonitor("Error modulo FitIsAut::QueryClientDocument ", TypeMonitor.error, e);
			
		}finally {
			
			iso.setWsTempAut((iso.getTickAut().getTime(TimeUnit.MILLISECONDS)/1000.0));
		}
		return iso;
	}
	
    public wIso8583 getInfoBasicaPersona(wIso8583 iso) {
    	
    	FitCoreProcessor processor = new FitCoreProcessor(iso);
		Detail response = processor.ProcessingTransactionFitCore();
		FitQueryables qry = null;
    	try {
			
			if(response != null){
				
			 if(response.getResponse() != null){	
				 
					if(response.getResponse().getCode().equals("0")){
						qry = new FitQueryables();
						List<Iterables> it = qry.QueryDetailFitBankIterator("TNATURALINFORMACIONBASICA", 20, 
							       new String[]{ "GENERO","CESTADOCIVIL","FNACIMIENTO","CTIPOVIVIENDA"}, response);
					
					if(it != null){
						
						if(it.size() > 0){
							
							iso.setISO_114_ExtendedData(
									SerializationObject.ObjectToXML((Serializable) it));						
						}else {
							
							iso.setISO_039_ResponseCode("116");
							iso.setISO_039p_ResponseDetail("LA CEDULA: " + iso.getISO_002_PAN()
							 + " NO EXISTE EN EL SISTEMA");
						}
					
					}else {
						
						iso.setISO_039_ResponseCode("909");
						iso.setISO_039p_ResponseDetail(qry.getError());
					}
				 }
			 }
		  }
    		
    		
		} catch (Exception e) {
			iso.setISO_039_ResponseCode("909");
			iso.setISO_039p_ResponseDetail(GeneralUtils.ExceptionToString("ERROR EN PROCESOS", e, false));
			log = new Logger();
			log.WriteLogMonitor("Error modulo FitIsAut::getInfoBasicaPersona ", TypeMonitor.error, e);
		}
    	return iso;
    }
    
    public wIso8583 getInfoBasicaPersonaExe(wIso8583 iso) {
		
    	iso = addIsoAdditionalRows(new Ref<wIso8583>(iso));
		wIso8583 isoClone = null;
		try {
			
			isoClone = (wIso8583) iso.clone();
			isoClone.setWsTransactionConfig((TransactionConfiguration)iso.getWsTransactionConfig().CloneObject());
			iso.getTickAut().reset();
			iso.getTickAut().start();
			isoClone = getInfoBasicaPersona(isoClone);
			if(iso.getTickAut().isStarted())
				iso.getTickAut().stop();
			if(isoClone.getISO_039_ResponseCode().equals("000")) {
				
				@SuppressWarnings("unchecked")
				List<Iterables> it = (List<Iterables>) SerializationObject
    					.XMLToObject(isoClone.getISO_114_ExtendedData());
				if(it != null) {
				
					for (Iterables iterables : it) {
						
						iso.setISO_115_ExtendedData(iso.getISO_115_ExtendedData() + iterables.getIterarors().get("GENERO") + "|" +
								iterables.getIterarors().get("CESTADOCIVIL") + "|" + iterables.getIterarors().get("FNACIMIENTO") + "|"
								+ iterables.getIterarors().get("CTIPOVIVIENDA") + "|");
						//iso.setISO_034_PANExt(iterables.getIterarors().get("NOMBRECUENTA"));
					}
					
					iso.setISO_115_ExtendedData(StringUtils.trimEnd(iso.getISO_115_ExtendedData(),"|"));
					
				}else {
					
					iso.setISO_039_ResponseCode("909");
					iso.setISO_039p_ResponseDetail("ERROR AL PROCESAR INFORMACION BASICA DE LA PERSONA");
				}
				
			}else {
				
				iso.setISO_039_ResponseCode(isoClone.getISO_039_ResponseCode());
				iso.setISO_039p_ResponseDetail(isoClone.getISO_039p_ResponseDetail());
			}
			
			
		} catch (Exception e) {
			
			if(iso.getTickAut().isStarted())
				iso.getTickAut().stop();
			iso.setISO_039_ResponseCode("909");
			iso.setISO_039p_ResponseDetail(GeneralUtils.ExceptionToString("ERROR EN PROCESOS", e, false));
			log = new Logger();
			log.WriteLogMonitor("Error modulo FitIsAut::getInfoBasicaPersonaExe ", TypeMonitor.error, e);
			
		}finally {
			iso.setWsTempAut((iso.getTickAut().getTime(TimeUnit.MILLISECONDS)/1000.0));
		}
		
		return iso;
	}
    
    public wIso8583 ExecuteStandarTransactionSPI_Exe(wIso8583 iso) {
		
		try {
			
			iso = addIsoAdditionalRows(new Ref<wIso8583>(iso));
			wIso8583 isoClone = null;
			isoClone = (wIso8583) iso.clone();
			isoClone.setWsTransactionConfig((TransactionConfiguration)iso.getWsTransactionConfig().CloneObject());
			
			/*Asigno la cuenta contable del BCE a acreditar (debito al DEPVEF y crdito a la cuenta nostro)*/
			isoClone.setISO_103_AccountID_2(iso.getISO_052_PinBlock()); //52 por que en el addiotional_iso_values puse la cuenta contable
			
			iso.getTickAut().reset();
			iso.getTickAut().start();
			isoClone = ExecuteStandarTransaction(isoClone);
			if(iso.getTickAut().isStarted())
				iso.getTickAut().stop();
			
			iso.setISO_039_ResponseCode(isoClone.getISO_039_ResponseCode());
			iso.setISO_039p_ResponseDetail(isoClone.getISO_039p_ResponseDetail());
			iso.setWsIso_LogStatus(isoClone.getWsIso_LogStatus());
			iso.setWsISO_TranDatetimeResponse(isoClone.getWsISO_TranDatetimeResponse());
			iso.setISO_044_AddRespData(isoClone.getISO_044_AddRespData());
			
		} catch (Exception e) {
			
			if(iso.getTickAut().isStarted())
				iso.getTickAut().stop();
			iso.setISO_039_ResponseCode("909");
			iso.setISO_039p_ResponseDetail(GeneralUtils.ExceptionToString("ERROR EN PROCESOS", e, false));
			log = new Logger();
			log.WriteLogMonitor("Error modulo FitIsAut::ExecuteStandarTransactionSPI_Exe ", TypeMonitor.error, e);
			
		}finally {
			
			iso.setWsTempAut((iso.getTickAut().getTime(TimeUnit.MILLISECONDS)/1000.0));
		}
		
		return iso;
	}    
    
    public wIso8583 getTablaCuotasPrestamo(wIso8583 iso) {
    	
    	iso = addIsoAdditionalRows(new Ref<wIso8583>(iso));
    	FitCoreProcessor processor = new FitCoreProcessor(iso);
		Detail response = processor.ProcessingTransactionFitCore();
		FitQueryables qry = null;
    	try {
			
			if(response != null){
				
			 if(response.getResponse() != null){	
				 
					if(response.getResponse().getCode().equals("0")){
						qry = new FitQueryables();
						List<Iterables> it = qry.QueryDetailFitBankIterator("TCUENTACUOTAS", 200, 
							       new String[]{ "CCUENTA","SUBCUENTA","CAPITALREDUCIDO","CAPITAL",
							    		   "INTERES","SEGURO","CARGO","FVENCIMIENTO",
							    		   "FPAGO","FABONO","COMISION"}, response);
					
					if(it != null){
						
						if(it.size() > 0){
							
							iso.setISO_114_ExtendedData(
									SerializationObject.ObjectToXML((Serializable) it));						
						}else {
							
							iso.setISO_039_ResponseCode("116");
							iso.setISO_039p_ResponseDetail("LA CUENTA PRESTAMO: " + iso.getISO_102_AccountID_1()
							 + " NO TIENE TABLA DE PAGOS");
						}
					
					}else {
						
						iso.setISO_039_ResponseCode("909");
						iso.setISO_039p_ResponseDetail(qry.getError());
					}
				 }
			 }
		  }
    		
    		
		} catch (Exception e) {
			iso.setISO_039_ResponseCode("909");
			iso.setISO_039p_ResponseDetail(GeneralUtils.ExceptionToString("ERROR EN PROCESOS", e, false));
			log = new Logger();
			log.WriteLogMonitor("Error modulo FitIsAut::getTablaCuotasPrestamo ", TypeMonitor.error, e);
		}
    	return iso;
    }
    
    public wIso8583 getTablaCuotasPrestamoExe(wIso8583 iso) {
		
    	iso = addIsoAdditionalRows(new Ref<wIso8583>(iso));
		wIso8583 isoClone = null;
    	DecimalFormatSymbols simbolo = new DecimalFormatSymbols();
	    simbolo.setDecimalSeparator('.');
		DecimalFormat df = new DecimalFormat("0.00", simbolo);
		try {
			
			isoClone = (wIso8583) iso.clone();
			isoClone.setWsTransactionConfig((TransactionConfiguration)iso.getWsTransactionConfig().CloneObject());
			iso.getTickAut().reset();
			iso.getTickAut().start();
			isoClone = getTablaCuotasPrestamo(isoClone);
			if(iso.getTickAut().isStarted())
				iso.getTickAut().stop();
			if(isoClone.getISO_039_ResponseCode().equals("000")) {
				
				@SuppressWarnings("unchecked")
				List<Iterables> it = (List<Iterables>) SerializationObject
    					.XMLToObject(isoClone.getISO_114_ExtendedData());
				if(it != null) {
				
					Iterables filterProducto =  it.stream()
	                        .filter(p -> p.getIterarors().get("FPAGO")
	                        .equalsIgnoreCase("null"))
	                        .findFirst().orElseGet(()->null);
					
					if(filterProducto != null) {
					

						String couta = filterProducto.getIterarors().get("SUBCUENTA");
						
						Iterables filterCuotaPagar =  it.stream()
		                        .filter(p -> p.getIterarors().get("SUBCUENTA")
		                        .equalsIgnoreCase(couta))
		                        .findFirst().orElseGet(()->null);
						
						if(filterCuotaPagar != null) {
							
							double Sumatoria = 0;
							
							Sumatoria = Double.parseDouble(StringUtils.IsNullOrEmpty(filterCuotaPagar.getIterarors().get("CAPITAL"))
									    		?"0":filterCuotaPagar.getIterarors().get("CAPITAL")) + 
									    Double.parseDouble(StringUtils.IsNullOrEmpty(filterCuotaPagar.getIterarors().get("INTERES"))
									    		?"0": filterCuotaPagar.getIterarors().get("INTERES")) +
									    Double.parseDouble(StringUtils.IsNullOrEmpty(filterCuotaPagar.getIterarors().get("COMISION"))
									    		?"0":filterCuotaPagar.getIterarors().get("COMISION")) +
									    Double.parseDouble(StringUtils.IsNullOrEmpty(filterCuotaPagar.getIterarors().get("SEGURO"))
									    		?"0":filterCuotaPagar.getIterarors().get("SEGURO")) +
									    Double.parseDouble(StringUtils.IsNullOrEmpty(filterCuotaPagar.getIterarors().get("CARGO"))
									    		?"0":filterCuotaPagar.getIterarors().get("CARGO"));
							
							iso.setISO_114_ExtendedData(filterCuotaPagar.getIterarors().get("CCUENTA") + "|" +
									filterCuotaPagar.getIterarors().get("SUBCUENTA") + "|" + df.format(Sumatoria) + "|" +
									filterCuotaPagar.getIterarors().get("FVENCIMIENTO").substring(0, 10)
									);
							iso.setISO_123_ExtendedData(filterCuotaPagar.getIterarors().get("CCUENTA") + "|" 
									+ filterCuotaPagar.getIterarors().get("CAPITALREDUCIDO"));
							
							iso.setISO_039_ResponseCode("000");
							iso.setISO_039p_ResponseDetail("TRANSACCION EXITOSA");
							
							
						}else {
							
							iso.setISO_039_ResponseCode("100");
							iso.setISO_039p_ResponseDetail("LA CUENTA: " + iso.getISO_102_AccountID_1() 
								+ " NO SE PUDO OBTENER SU INFORMACION");
						}
						
					}else {
						
						iso.setISO_039_ResponseCode("116");
						iso.setISO_039p_ResponseDetail("LA CUENTA: " + iso.getISO_102_AccountID_1() 
							+ " NO TIENE CUENTAS PENDIENTES DE PAGO");
					}
					iso.setWsIso_LogStatus(2);
					
				}else {
					
					iso.setISO_039_ResponseCode("909");
					iso.setISO_039p_ResponseDetail("ERROR AL PROCESAR INFORMACION TABLA DE PRESTAMOS");
				}
				
			}else {
				
				iso.setISO_039_ResponseCode(isoClone.getISO_039_ResponseCode());
				iso.setISO_039p_ResponseDetail(isoClone.getISO_039p_ResponseDetail());
			}
			
			
		} catch (Exception e) {
			
			if(iso.getTickAut().isStarted())
				iso.getTickAut().stop();
			iso.setISO_039_ResponseCode("909");
			iso.setISO_039p_ResponseDetail(GeneralUtils.ExceptionToString("ERROR EN PROCESOS", e, false));
			log = new Logger();
			log.WriteLogMonitor("Error modulo FitIsAut::getInfoBasicaPersonaExe ", TypeMonitor.error, e);
			
		}finally {
			iso.setWsTempAut((iso.getTickAut().getTime(TimeUnit.MILLISECONDS)/1000.0));
		}
		
		return iso;
	}
    
    public wIso8583 getCtasVistaCondOperativa(wIso8583 iso) {
    	
    	iso = addIsoAdditionalRows(new Ref<wIso8583>(iso));
    	try {
			
    		FitCoreProcessor processor = new FitCoreProcessor(iso);
    		iso.getTickAut().reset();
			iso.getTickAut().start();
    			Detail response = processor.ProcessingTransactionFitCore();
    		if(iso.getTickAut().isStarted())
    			iso.getTickAut().stop();
    	    iso.setISO_041_CardAcceptorID("001");
    		
    		
    		Object value = (Object) GeneralUtils.asStream
					   (GeneralUtils.asStream(response.getTables().iterator(), true)
					  .filter(p -> p.getName().equalsIgnoreCase("TCUENTA"))
					  .findFirst().orElseGet(()-> null).getRecords().iterator(), true)
					  .filter(x -> x.getNumber() == 0)
					  .findFirst().orElseGet(()-> null);
    		
    		if(iso.getISO_039_ResponseCode().equals("000") && value != null){
    			
    			FitQueryables ccondicionOperativa = new FitQueryables("TCUENTA", 0, "CCONDICIONOPERATIVA", response);
    			FitQueryables desCondicionOperativa = new FitQueryables("TCUENTA", 0, "DESCRIPCION", response);
    			FitQueryables cSubsistema = new FitQueryables("TCUENTA", 0, "CSUBSISTEMA", response);
    			FitQueryables cCuenta = new FitQueryables("TCUENTA", 0, "CCUENTA", response);
    			FitQueryables versionControl = new FitQueryables("TCUENTA", 0, "VERSIONCONTROL", response);
    			
    			ccondicionOperativa.join();
    			desCondicionOperativa.join();
    			cSubsistema.join();
    			cCuenta.join();
    			versionControl.join();
    			
    			if(cCuenta.getReturnValue() != null){
    				
    				iso.setISO_102_AccountID_1(String.valueOf(cCuenta.getReturnValue()));
    				iso.setISO_120_ExtendedData(String.valueOf(ccondicionOperativa.getReturnValue()).equals("null")?null
    											:String.valueOf(ccondicionOperativa.getReturnValue()));
    				iso.setISO_121_ExtendedData(String.valueOf(cSubsistema.getReturnValue()));
    				iso.setISO_122_ExtendedData(String.valueOf(desCondicionOperativa.getReturnValue().equals("null")?null
    											:String.valueOf(desCondicionOperativa.getReturnValue())));
    				iso.setISO_123_ExtendedData(String.valueOf(versionControl.getReturnValue()));
    				iso.setISO_039_ResponseCode("000");
    				iso.setISO_039p_ResponseDetail("TRANSACCION EXITOSA");
    				
    			}else{
    				
    				iso.setISO_039_ResponseCode("116");
    				iso.setISO_039p_ResponseDetail("LA CUENTA: "+ iso.getISO_102_AccountID_1() +" NO EXISTE EN EL SISTEMA");
    			}
    		}else {
    			
    			iso.setISO_039_ResponseCode("308");
				iso.setISO_039p_ResponseDetail(response.getResponse().getCode() + " - " 
    			+ response.getResponse().getUserMessage());
    		}
    		
		} catch (Exception e) {
			if(iso.getTickAut().isStarted())
    			iso.getTickAut().stop();
			iso.setISO_039_ResponseCode("070");
			iso.setISO_039p_ResponseDetail(GeneralUtils.ExceptionToString("ERROR EN PROCESOS", e, false));
			log = new Logger();
			log.WriteLogMonitor("Error modulo FitIsAut::getCtasVistaCondOperativa ", TypeMonitor.error, e);
			
		}finally {
			
			iso.setWsTempAut((iso.getTickAut().getTime(TimeUnit.MILLISECONDS)/1000.0));
		}
    	
    	return iso;
    }
    
    public wIso8583 getCtasVistaCondOperativaExe(wIso8583 iso) {
    	
    	iso = addIsoAdditionalRows(new Ref<wIso8583>(iso));
    	wIso8583 isoClone = null;
    	try {
			
    		if(iso.getISO_039_ResponseCode().equals("000")) {
	    		isoClone = (wIso8583) iso.clone();
				isoClone.setWsTransactionConfig((TransactionConfiguration)iso.getWsTransactionConfig().CloneObject());
				isoClone.getWsTransactionConfig().setProccodeTransactionFit(Arrays.asList(isoClone.getWsTransactionConfig()
						.getProccodeTransactionFit().split("\\|")).get(1));
				iso.getTickAut().reset();
				iso.getTickAut().start();
					isoClone = getCtasVistaCondOperativa(isoClone);
				if(iso.getTickAut().isStarted())
					iso.getTickAut().stop();
				if(isoClone.getISO_039_ResponseCode().equals("000")) {
					
					iso.setISO_120_ExtendedData(isoClone.getISO_120_ExtendedData());
					iso.setISO_121_ExtendedData(isoClone.getISO_121_ExtendedData());
					iso.setISO_122_ExtendedData(isoClone.getISO_122_ExtendedData());
					iso.setISO_123_ExtendedData(isoClone.getISO_123_ExtendedData());
	
					iso.setISO_039_ResponseCode("000");
					iso.setISO_039p_ResponseDetail("TRANSACCION EXITOSA");
					
				}else {
					
					iso.setISO_039_ResponseCode(isoClone.getISO_039_ResponseCode());
					iso.setISO_039p_ResponseDetail(isoClone.getISO_039p_ResponseDetail());
				}
				iso.setWsIso_LogStatus(isoClone.getWsIso_LogStatus());
    		}
			
		} catch (Exception e) {
			
			if(iso.getTickAut().isStarted())
				iso.getTickAut().stop();
			log = new Logger();
			log.WriteLogMonitor("Error modulo FitIsAut::getCtasVistaCondOperativaExe ", TypeMonitor.error, e);
			
		}finally {
			
			iso.setWsTempAut((iso.getTickAut().getTime(TimeUnit.MILLISECONDS)/1000.0));
		}
    	
    	return iso;
    }
   
    public Callable<wIso8583> callGetInfoValoresCtasPrestamoVigentes(wIso8583 iso){
    	
    	Callable<wIso8583> callable = new Callable<wIso8583>() {
			
			@Override
			public wIso8583 call() throws Exception {
				
				return getTablaCuotasPrestamoExe(iso);
			}
		};
		return callable;
    }
    
    public Callable<wIso8583> callGetInfoValoresCtasPrestamoVencido(wIso8583 iso){
        
    	Callable<wIso8583> callable = new Callable<wIso8583>() {
			
			@Override
			public wIso8583 call() throws Exception {
				
				return getValorAPagarPrestExe(iso);
			}
		};
		return callable;
    }
    
    public Callable<wIso8583> callGetInfoValoresCtasPrestamo(wIso8583 iso){
    
    	Callable<wIso8583> callable = new Callable<wIso8583>() {
			
			@Override
			public wIso8583 call() throws Exception {
				
				return getValorAPagarPrestExe(iso);
			}
		};
		return callable;
    }
    
    public Callable<wIso8583> callQueryBalance(wIso8583 iso){
        
    	Callable<wIso8583> callable = new Callable<wIso8583>() {
			
			@Override
			public wIso8583 call() throws Exception {
				
				return QueryBalance(iso);
			}
		};
		return callable;
    }
    
    public Callable<wIso8583> callInfoVencimientoDPFs(wIso8583 iso){
        
    	Callable<wIso8583> callable = new Callable<wIso8583>() {
			
			@Override
			public wIso8583 call() throws Exception {
				
				return getInfoVencimientoPlazo(iso);
			}
		};
		return callable;
    }
    
}
