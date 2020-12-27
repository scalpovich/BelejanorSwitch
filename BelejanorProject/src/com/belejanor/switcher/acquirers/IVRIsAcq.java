package com.belejanor.switcher.acquirers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.belejanor.switcher.authorizations.FitIsAut;
import com.belejanor.switcher.cscoreswitch.EngineCallableProcessor;
import com.belejanor.switcher.cscoreswitch.EngineProcessorTransactionsBackGround;
import com.belejanor.switcher.cscoreswitch.Iso8583;
import com.belejanor.switcher.cscoreswitch.TransactionConfiguration;
import com.belejanor.switcher.cscoreswitch.csProcess;
import com.belejanor.switcher.cscoreswitch.wIso8583;
import com.belejanor.switcher.logger.Logger;
import com.belejanor.switcher.logger.LoggerConfig.TypeMonitor;
import com.belejanor.switcher.memcached.Config;
import com.belejanor.switcher.sqlservices.IsoRetrievalTransaction;
import com.belejanor.switcher.utils.FormatUtils;
import com.belejanor.switcher.utils.GeneralUtils;
import com.belejanor.switcher.utils.Iterables;
import com.belejanor.switcher.utils.Ref;
import com.belejanor.switcher.utils.SerializationObject;
import com.belejanor.switcher.utils.StringUtils;

public class IVRIsAcq {

	private Logger log;
	
	public IVRIsAcq() {
		this.log = new Logger();
	}
	
	public wIso8583 getValoresPrestamo(wIso8583 iso) {
		
		try {
			
			iso.setISO_041_CardAcceptorID("1800-292929");
			List<wIso8583> isoList = new ArrayList<>();
			
			String[] cuentas = iso.getISO_121_ExtendedData().split("\\|");
			for (int i = 0; i < cuentas.length; i++) {
				
				wIso8583 isoClone = (wIso8583) iso.clone();
				isoClone.setWsTransactionConfig((TransactionConfiguration)iso.getWsTransactionConfig().CloneObject());
				isoClone.getWsTransactionConfig().setProccodeTransactionFit(Arrays.asList(isoClone.getWsTransactionConfig()
						.getProccodeTransactionFit().split("\\|")).get(1));
				isoClone.setISO_102_AccountID_1(cuentas[i]);
				isoClone.setISO_011_SysAuditNumber(GeneralUtils.GetSecuencialNumeric(8));
				isoList.add(isoClone);
			}	
			
			/*Executor Thread*/
			iso.getTickAut().reset();
			iso.getTickAut().start();
			EngineCallableProcessor<wIso8583> engine = new EngineCallableProcessor<>(5);
			for (wIso8583 wIso : isoList) {
				FitIsAut aut = new FitIsAut();
				engine.add(aut.callGetInfoValoresCtasPrestamo(wIso));
			}
			List<wIso8583> listIsoResponses = engine.goProcess();
			if(iso.getTickAut().isStarted())
				iso.getTickAut().stop();
			
			wIso8583 isoRes = new wIso8583();
			isoRes = listIsoResponses.stream()
					 .filter(p -> p.getISO_039_ResponseCode() != "000")
		             .findFirst().orElseGet(() -> null);

			if(isoRes == null){
				
				iso.setISO_039_ResponseCode("000");
				iso.setISO_039p_ResponseDetail("TRANSACCION EXITOSA");
				
				for (wIso8583 wIsor : listIsoResponses) {
					
					iso.setISO_114_ExtendedData(iso.getISO_114_ExtendedData() + wIsor.getISO_120_ExtendedData() + "^");
					iso.setISO_123_ExtendedData(iso.getISO_123_ExtendedData() + wIsor.getISO_123_ExtendedData() + "^");
				}
				
				iso.setISO_114_ExtendedData(StringUtils.trimEnd(iso.getISO_114_ExtendedData(),"^"));
				iso.setISO_123_ExtendedData(StringUtils.trimEnd(iso.getISO_123_ExtendedData(),"^"));
				
			}else{
				
				iso.setISO_039_ResponseCode(isoRes.getISO_039_ResponseCode());
				iso.setISO_039p_ResponseDetail(isoRes.getISO_039p_ResponseDetail());
			}
				
		} catch (Exception e) {
			if(iso.getTickAut().isStarted())
				iso.getTickAut().stop();
			log.WriteLogMonitor("Error modulo IVRIsAcq::getValoresPrestamo ", 
					TypeMonitor.error, e);
			iso.setISO_039_ResponseCode("909");
			iso.setISO_039p_ResponseDetail("ERROR EN PROCESOS " + GeneralUtils.ExceptionToString(null, e, false));
			
		}finally {
			
			iso.setWsTempAut((iso.getTickAut().getTime(TimeUnit.MILLISECONDS)/1000.0));
		}
		return iso;
	}
	
    public wIso8583 getValoresPrestamoReloaded(wIso8583 iso) {
		
		try {
			
			iso.setISO_041_CardAcceptorID("1800-292929");
			List<wIso8583> isoList = new ArrayList<>();
			
			String[] cuentas = iso.getISO_121_ExtendedData().split("\\|");
			String[] estados = iso.getISO_123_ExtendedData().split("\\|");
			for (int i = 0; i < cuentas.length; i++) {
				
				wIso8583 isoClone = (wIso8583) iso.clone();
				isoClone.setWsTransactionConfig((TransactionConfiguration)iso.getWsTransactionConfig().CloneObject());
				
				if(estados[i].equalsIgnoreCase("NML")) { /*Prestamos Vigentes*/
					
					isoClone.getWsTransactionConfig().setProccodeTransactionFit(Arrays.asList(isoClone.getWsTransactionConfig()
						.getProccodeTransactionFit().split("\\|")).get(2));
					
				}else {
					
					isoClone.getWsTransactionConfig().setProccodeTransactionFit(Arrays.asList(isoClone.getWsTransactionConfig()
							.getProccodeTransactionFit().split("\\|")).get(1));
				}
				
				isoClone.setISO_019_AcqCountryCode(estados[i]);
				isoClone.setISO_102_AccountID_1(cuentas[i]);
				isoClone.setISO_011_SysAuditNumber(GeneralUtils.GetSecuencialNumeric(8));
				isoList.add(isoClone);
			}	
			
			
			/*Executor Thread*/
			iso.getTickAut().reset();
			iso.getTickAut().start();
			EngineCallableProcessor<wIso8583> engine = new EngineCallableProcessor<>(5);
			
			for (wIso8583 wIso : isoList) {
				FitIsAut aut = new FitIsAut();
				if(wIso.getISO_019_AcqCountryCode().equals("NML")) {
					/*Si es vigente*/
					System.out.println("**** Entro por Vigente");
					engine.add(aut.callGetInfoValoresCtasPrestamoVigentes(wIso));
				}else {
					/*Si es vencido*/
					engine.add(aut.callGetInfoValoresCtasPrestamoVencido(wIso));
					System.out.println("**** Entro por NO Vigente");
				}
			}
			List<wIso8583> listIsoResponses = engine.goProcess();
			iso.setISO_123_ExtendedData(StringUtils.Empty());
			
			if(iso.getTickAut().isStarted())
				iso.getTickAut().stop();
			
			wIso8583 isoRes = new wIso8583();
			isoRes = listIsoResponses.stream()
					 .filter(p -> p.getISO_039_ResponseCode() != "000")
		             .findFirst().orElseGet(() -> null);

			if(isoRes == null){
				
				iso.setISO_114_ExtendedData(StringUtils.Empty());
				iso.setISO_039_ResponseCode("000");
				iso.setISO_039p_ResponseDetail("TRANSACCION EXITOSA");
				
				for (wIso8583 wIsor : listIsoResponses) {
					
					if(wIsor.getISO_019_AcqCountryCode().equals("NML")) {
						
						iso.setISO_114_ExtendedData(iso.getISO_114_ExtendedData() + wIsor.getISO_114_ExtendedData() + "^");
						iso.setISO_123_ExtendedData(iso.getISO_123_ExtendedData() + wIsor.getISO_123_ExtendedData() + "^");
						
					}else {
						
						iso.setISO_114_ExtendedData(iso.getISO_114_ExtendedData() + wIsor.getISO_120_ExtendedData() + "^");
						iso.setISO_123_ExtendedData(iso.getISO_123_ExtendedData() + wIsor.getISO_123_ExtendedData() + "^");
					}
				}
				
				iso.setISO_114_ExtendedData(StringUtils.trimEnd(iso.getISO_114_ExtendedData(),"^"));
				iso.setISO_123_ExtendedData(StringUtils.trimEnd(iso.getISO_123_ExtendedData(),"^"));
				
			}else{
				
				iso.setISO_039_ResponseCode(isoRes.getISO_039_ResponseCode());
				iso.setISO_039p_ResponseDetail(isoRes.getISO_039p_ResponseDetail());
			}
				
		} catch (Exception e) {
			if(iso.getTickAut().isStarted())
				iso.getTickAut().stop();
			log.WriteLogMonitor("Error modulo IVRIsAcq::getValoresPrestamo ", 
					TypeMonitor.error, e);
			iso.setISO_039_ResponseCode("909");
			iso.setISO_039p_ResponseDetail("ERROR EN PROCESOS " + GeneralUtils.ExceptionToString(null, e, false));
			
		}finally {
			
			iso.setWsTempAut((iso.getTickAut().getTime(TimeUnit.MILLISECONDS)/1000.0));
		}
		return iso;
	}
	
	public wIso8583 getSaldosCtasVista(wIso8583 iso) {
		
		try {
			
			iso.setISO_041_CardAcceptorID("1800-292929");
			List<wIso8583> isoList = new ArrayList<>();
			
			String[] cuentas = iso.getISO_121_ExtendedData().split("\\|");		
			for (int i = 0; i < cuentas.length; i++) {
			
				wIso8583 isoClone = (wIso8583) iso.clone();
				isoClone.setWsTransactionConfig((TransactionConfiguration)iso.getWsTransactionConfig().CloneObject());
				isoClone.getWsTransactionConfig().setProccodeTransactionFit(Arrays.asList(isoClone.getWsTransactionConfig()
						.getProccodeTransactionFit().split("\\|")).get(1));
				isoClone.setISO_102_AccountID_1(cuentas[i]);
				isoClone.setISO_011_SysAuditNumber(GeneralUtils.GetSecuencialNumeric(8));
				isoList.add(isoClone);
			}	
			
			/*Executor Thread*/
			iso.getTickAut().reset();
			iso.getTickAut().start();
			EngineCallableProcessor<wIso8583> engine = new EngineCallableProcessor<>(5);
			for (wIso8583 wIso : isoList) {
				FitIsAut aut = new FitIsAut();
				engine.add(aut.callQueryBalance(wIso));
			}
			List<wIso8583> listIsoResponses = engine.goProcess();
			if(iso.getTickAut().isStarted())
				iso.getTickAut().stop();
			
			wIso8583 isoRes = new wIso8583();
			isoRes = listIsoResponses.stream()
					 .filter(p -> p.getISO_039_ResponseCode() != "000")
		             .findFirst().orElseGet(() -> null);

			if(isoRes == null){
				
				iso.setISO_039_ResponseCode("000");
				iso.setISO_039p_ResponseDetail("TRANSACCION EXITOSA");
				iso.setISO_121_ExtendedData(StringUtils.Empty());
				
				for (wIso8583 wIsor : listIsoResponses) {
					
					iso.setISO_034_PANExt(wIsor.getISO_034_PANExt());
					iso.setISO_120_ExtendedData(iso.getISO_120_ExtendedData() + wIsor.getISO_102_AccountID_1() + "|");
					iso.setISO_121_ExtendedData(iso.getISO_121_ExtendedData() +  wIsor.getISO_054_AditionalAmounts() + "|");
					
				}
				 
				iso.setISO_120_ExtendedData(StringUtils.trimEnd(iso.getISO_120_ExtendedData(),"|"));
				iso.setISO_121_ExtendedData(StringUtils.trimEnd(iso.getISO_121_ExtendedData(),"|"));
				
			}else{
				
				iso.setISO_039_ResponseCode(isoRes.getISO_039_ResponseCode());
				iso.setISO_039p_ResponseDetail(isoRes.getISO_039p_ResponseDetail());
			}
				
		} catch (Exception e) {
			if(iso.getTickAut().isStarted())
				iso.getTickAut().stop();
			log.WriteLogMonitor("Error modulo IVRIsAcq::getSaldosCtasVista ", 
					TypeMonitor.error, e);
			iso.setISO_039_ResponseCode("909");
			iso.setISO_039p_ResponseDetail("ERROR EN PROCESOS " + GeneralUtils.ExceptionToString(null, e, false));
			
		}finally {
			
			iso.setWsTempAut((iso.getTickAut().getTime(TimeUnit.MILLISECONDS)/1000.0));
		}
		return iso;
	}
	
	public wIso8583 getInfoInversiones(wIso8583 iso) {
		
		try {
			
			iso.setISO_041_CardAcceptorID("1800-292929");
			if(iso.getISO_039_ResponseCode().equals("000")) {
				iso.getTickAut().reset();
				iso.getTickAut().start();
				@SuppressWarnings("unchecked")
				List<Iterables> it = (List<Iterables>) SerializationObject
						.XMLToObject(iso.getISO_114_ExtendedData());
				if(it != null) {
				
					/*Cambio por parametrizacion campo 90, ahora se mandan todos si necesita 
					 * filtrar descomentar las lineas de abajo*/
					/*List<Iterables> filterVigentes = it.stream()
							                        .filter(p -> p.getIterarors().get("DESCRIPCIONESTATUSCUENTA")
							                        .equalsIgnoreCase("VIGENTE") && 
							                         p.getIterarors().get("CGRUPOPRODUCTO||tcuentas1.CPRODUCTO")
							                        .equalsIgnoreCase(iso.getISO_090_OriginalData()))
							                        .peek(Objects::requireNonNull)
							    					.collect(Collectors.toList());*/
					
					List<Iterables> filterVigentes = it.stream()
	                        .filter(p -> p.getIterarors().get("DESCRIPCIONESTATUSCUENTA")
	                        .equalsIgnoreCase("VIGENTE"))
	                        .peek(Objects::requireNonNull)
	    					.collect(Collectors.toList());
					
					if(filterVigentes != null && filterVigentes.size() > 0) {
						
						String acum = StringUtils.Empty();
						for (Iterables ite : filterVigentes) {
							
							iso.setISO_023_CardSeq(String.valueOf(filterVigentes.size()));
							iso.setISO_034_PANExt(ite.getIterarors().get("NOMBRELEGAL"));
							acum += ite.getIterarors().get("CCUENTA") + "|" + ite.getIterarors().get("MONTO") + "|" +
									ite.getIterarors().get("TASA") + "%|" + ite.getIterarors().get("FVENCIMIENTO").substring(0, 10) + "^";
							iso.setISO_121_ExtendedData(ite.getIterarors().get("DESCRIPCIONPRODUCTO"));
						}
						iso.setISO_114_ExtendedData(null);
						iso.setISO_120_ExtendedData(StringUtils.trimEnd(acum, "^"));
						
						if(iso.getTickAut().isStarted())
							iso.getTickAut().stop();
						
					}else {
						
						iso.setISO_039_ResponseCode("116");
						iso.setISO_039p_ResponseDetail("LA CEDULA: " + iso.getISO_002_PAN() + 
								" NO TIENE INVERSIONES VIGENTES DEL PRODUCTO: " + iso.getISO_090_OriginalData()  );
					}
							                        
				}
			}
			
		} catch (Exception e) {
			
			if(iso.getTickAut().isStarted())
				iso.getTickAut().stop();
			
			log.WriteLogMonitor("Error modulo IVRIsAcq::getInfoInversiones ", 
					TypeMonitor.error, e);
			iso.setISO_039_ResponseCode("909");
			iso.setISO_039p_ResponseDetail("ERROR EN PROCESOS " + GeneralUtils.ExceptionToString(null, e, false));
			
		}finally {
			
			iso.setISO_114_ExtendedData(null);
			iso.setWsTempAut((iso.getTickAut().getTime(TimeUnit.MILLISECONDS)/1000.0));
		}
		
		return iso;
	}
	
    public wIso8583 getInfoInversionesReloaded(wIso8583 iso) {
		
    	IsoRetrievalTransaction sql = null;
    	FitIsAut aut = null;
    	double timeAutAcum = 0;
    	List<wIso8583> isoList = new ArrayList<>();
    	wIso8583 isoCont = null;
		try {
			
			iso.setISO_041_CardAcceptorID("1800-292929");
			iso.getTickAut().reset();
			iso.getTickAut().start();
				sql = new IsoRetrievalTransaction();
				iso = sql.RetrieveCuentasInversiones(iso);
			if(iso.getISO_039_ResponseCode().equals("000")) {
			
				if(iso.getTickAut().isStarted())
					iso.getTickAut().stop();
				//timeAutAcum += iso.getWsTempBDD();
				iso.setWsTempAut((iso.getTickAut().getTime(TimeUnit.MILLISECONDS)/1000.0));
				timeAutAcum += (iso.getTickAut().getTime(TimeUnit.MILLISECONDS)/1000.0);
				
				String[] cuentas = iso.getISO_102_AccountID_1().split("\\|");
				for (int i = 0; i < cuentas.length; i++) {
					
					wIso8583 isoClone = (wIso8583) iso.clone();
					isoClone.setWsTransactionConfig((TransactionConfiguration)iso.getWsTransactionConfig().CloneObject());
					isoClone.setISO_102_AccountID_1(cuentas[i]);
					isoClone.setISO_011_SysAuditNumber(GeneralUtils.GetSecuencialNumeric(8));
					isoList.add(isoClone);
				}	
				
				isoCont = new wIso8583();
				isoCont.getTickAut().reset();
				isoCont.getTickAut().start();
				
					EngineCallableProcessor<wIso8583> engine = new EngineCallableProcessor<>(5);
					for (wIso8583 wIso : isoList) {
						aut = new FitIsAut();
						engine.add(aut.callInfoVencimientoDPFs(wIso));
					}
					List<wIso8583> listIsoResponses = engine.goProcess();
				if(isoCont.getTickAut().isStarted())
					isoCont.getTickAut().stop();
				
				double valor = (isoCont.getTickAut().getTime(TimeUnit.MILLISECONDS)/1000.0);
				timeAutAcum += (valor);
				
				wIso8583 isoRes = new wIso8583();
				isoRes = listIsoResponses.stream()
						 .filter(p -> p.getISO_039_ResponseCode() != "000")
			             .findFirst().orElseGet(() -> null);

				if(isoRes == null){
					
					iso.setISO_039_ResponseCode("000");
					iso.setISO_039p_ResponseDetail("TRANSACCION EXITOSA");
					
					for (wIso8583 wIsor : listIsoResponses) {
						
						iso.setISO_120_ExtendedData(iso.getISO_120_ExtendedData() + wIsor.getISO_120_ExtendedData() + "^");
						iso.setISO_121_ExtendedData(wIsor.getISO_121_ExtendedData());
						iso.setISO_104_TranDescription(wIsor.getISO_104_TranDescription());
						
					}
					iso.setISO_023_CardSeq(String.valueOf(listIsoResponses.size()));
					iso.setISO_120_ExtendedData(StringUtils.trimEnd(iso.getISO_120_ExtendedData(),"^"));
					
				}else{
					
					iso.setISO_039_ResponseCode(isoRes.getISO_039_ResponseCode());
					iso.setISO_039p_ResponseDetail(isoRes.getISO_039p_ResponseDetail());
				}
					
			}else {
				
				if(iso.getTickAut().isStarted())
					iso.getTickAut().stop();
				timeAutAcum = iso.getWsTempAut();
			}
			
		} catch (Exception e) {
			
			if(iso.getTickAut().isStarted())
				iso.getTickAut().stop();
			if(isoCont.getTickAut().isStarted())
				isoCont.getTickAut().stop();
			
			log.WriteLogMonitor("Error modulo IVRIsAcq::getInfoInversionesReloaded ", 
					TypeMonitor.error, e);
			iso.setISO_039_ResponseCode("909");
			iso.setISO_039p_ResponseDetail("ERROR EN PROCESOS " + GeneralUtils.ExceptionToString(null, e, false));
			
		}finally {
			
			iso.setWsTempAut(timeAutAcum);
		}
		
		return iso;
	}
	
	public wIso8583 manUserWEB(wIso8583 iso) {
		
		FitIsAut auth = null;
		try {
			
			iso.setISO_041_CardAcceptorID("1800-292929");
			wIso8583 isoClone = (wIso8583) iso.clone();
			isoClone.setWsTransactionConfig((TransactionConfiguration)iso.getWsTransactionConfig().CloneObject());
			isoClone.getWsTransactionConfig().setProccodeTransactionFit(Arrays.asList(isoClone.getWsTransactionConfig()
					.getProccodeTransactionFit().split("\\|")).get(1));
			
			auth = new FitIsAut();
			iso.getTickAut().reset();
			iso.getTickAut().start();
				isoClone = auth.ExecuteStandarTransaction(isoClone);
			if(iso.getTickAut().isStarted())
				iso.getTickAut().stop();
			
			iso.setISO_039_ResponseCode(isoClone.getISO_039_ResponseCode());
			iso.setISO_039p_ResponseDetail(isoClone.getISO_039p_ResponseDetail());
			iso.setWsIso_LogStatus(isoClone.getWsIso_LogStatus());
			
		}catch (Exception e) {
			
			if(iso.getTickAut().isStarted())
				iso.getTickAut().stop();
			log.WriteLogMonitor("Error modulo IVRIsAcq::manUserWEB ", 
					TypeMonitor.error, e);
			iso.setISO_039_ResponseCode("909");
			iso.setISO_039p_ResponseDetail("ERROR EN PROCESOS " + GeneralUtils.ExceptionToString(null, e, false));
			
		}finally {
			
			if(iso.getISO_039_ResponseCode().equals("000")) {
				
				iso.setAuxiliarArrayValues(new String[] { "name|" + FormatUtils.DateToString(new Date(), "yyyy/MM/dd") + "|" 
						+ FormatUtils.DateToString(new Date(), "HH:mm:ss")
						,"#|" + FormatUtils.DateToString(new Date(), "yyyy/MM/dd") + "|" + FormatUtils.DateToString(new Date(), "HH:mm")});
			}
			
			iso.setWsTempAut((iso.getTickAut().getTime(TimeUnit.MILLISECONDS)/1000.0));
		}
		
		return iso;
	}
	
    public wIso8583 filterCtaVista(wIso8583 iso) {
		
		try {
			
			iso.setISO_041_CardAcceptorID("1800-292929");
			if(iso.getISO_039_ResponseCode().equals("000")) {
				iso.getTickAut().reset();
				iso.getTickAut().start();
				@SuppressWarnings("unchecked")
				List<Iterables> it = (List<Iterables>) SerializationObject
						.XMLToObject(iso.getISO_114_ExtendedData());
				if(it != null) {
				
					
					List<Iterables> filterVigentes = it.stream()
	                        .filter(p -> p.getIterarors().get("CGRUPOPRODUCTO||tc0.CPRODUCTO")
	                        .equalsIgnoreCase(iso.getISO_115_ExtendedData()))
	                        .peek(Objects::requireNonNull)
	    					.collect(Collectors.toList());
					
					
					if(filterVigentes != null) {
						
						if(filterVigentes.size() > 0) {
							
							iso.setISO_102_AccountID_1(filterVigentes.get(0).getIterarors().get("CCUENTA"));
						}
						
					}else {
						iso.setISO_039_ResponseCode("116");
						iso.setISO_039p_ResponseDetail("EL CLIENTE NO TIENE ESE PRODUCTO DE CUENTA VISTA");
					}
					
				}else {
					
					iso.setISO_039_ResponseCode("116");
					iso.setISO_039p_ResponseDetail("EL CLIENTE NO TIENE CUENTAS A LA VISTA");
				}
			}
							
			if(iso.getTickAut().isStarted())
				iso.getTickAut().stop();
			
		}catch (Exception e) {
			
			if(iso.getTickAut().isStarted())
				iso.getTickAut().stop();
			log.WriteLogMonitor("Error modulo IVRIsAcq::filterCtaVista ", 
					TypeMonitor.error, e);
			iso.setISO_039_ResponseCode("909");
			iso.setISO_039p_ResponseDetail("ERROR EN PROCESOS " + GeneralUtils.ExceptionToString(null, e, false));
			
		}finally {
			
			iso.setWsTempAut((iso.getTickAut().getTime(TimeUnit.MILLISECONDS)/1000.0));
		}
		
		return iso;
	}
    
    public wIso8583 processorMantenainceVista(wIso8583 iso) {
    	
    	Iso8583 iso8583 = null;
    	List<Iso8583> isos = new ArrayList<>();
    	try {
			if(iso.getISO_039_ResponseCode().equals("000")) {
	    		List<wIso8583> listIso = getCtaVistaList(iso);
	    		
	    		if(listIso != null) {
	    			
	    			if(listIso.size() > 0) {
	    				
	    				EngineCallableProcessor<Iso8583> engine = null;
	    				iso8583 = new Iso8583();
	    				iso8583.setISO_000_Message_Type("1200");
	    				iso8583.setISO_003_ProcessingCode("640881");
	    				iso8583.setISO_018_MerchantType("0010");
	    				iso8583.setISO_024_NetworkId("555551");
	    				
	    				for (wIso8583 wIso : listIso) {
							
	    					Iso8583 isoClone = (Iso8583) iso8583.clone();
	    					isoClone.setISO_002_PAN(wIso.getISO_002_PAN());
	    					isoClone.setISO_011_SysAuditNumber(GeneralUtils.GetSecuencialNumeric(8));
	    					isoClone.setISO_034_PANExt(wIso.getISO_034_PANExt());
	    					isoClone.setISO_035_Track2(wIso.getISO_035_Track2());
	    					isoClone.setISO_023_CardSeq(wIso.getISO_023_CardSeq());
	    					isoClone.setISO_090_OriginalData(wIso.getISO_090_OriginalData());
	    					isoClone.setISO_102_AccountID_1(wIso.getISO_102_AccountID_1());
	    					isoClone.setISO_115_ExtendedData(wIso.getISO_115_ExtendedData());
	    					isos.add(isoClone);
	    				}
	    				
	    				/*Procesamiento de Preguntas*/
	    	    		iso.getTickAut().reset();
	    				iso.getTickAut().start();
	    	    		engine = new EngineCallableProcessor<>(5);
	    	    		String IP = "127.0.0.1";
	    	    		for (Iso8583 is : isos) {
	    	    		
	    	    			csProcess proc = new csProcess(is, IP);
	    					engine.add(proc);
	    	    		}
	    	    		List<Iso8583> listIsoResponses = engine.goProcess();
	    	    		
	    	    		if(iso.getTickAut().isStarted())
	    					iso.getTickAut().stop();
	    	    		
	    	    		Iso8583 isoR = listIsoResponses.stream()
	    	    					   .filter(p -> !p.getISO_039_ResponseCode().equals("000"))
	    	    					   .findFirst().orElseGet(() -> null);
	    				
	    	    		if(isoR == null) {
	    	    			
	    	    			iso.setISO_039_ResponseCode("000");
	        				iso.setISO_039p_ResponseDetail("TRANSACCION EXITOSA");
	        				
	        				for (Iso8583 isom : listIsoResponses) {
								
	        					   iso.setISO_034_PANExt(isom.getISO_034_PANExt());
	        					   iso.setISO_035_Track2(isom.getISO_035_Track2());
	        					   iso.setISO_102_AccountID_1(iso.getISO_102_AccountID_1() + isom.getISO_102_AccountID_1() + "|");
	        					   iso.setISO_120_ExtendedData(iso.getISO_120_ExtendedData() + isom.getISO_120_ExtendedData() + "|");
	        					   iso.setISO_121_ExtendedData(iso.getISO_121_ExtendedData() + isom.getISO_121_ExtendedData() + "|");
	        					   iso.setISO_122_ExtendedData(iso.getISO_122_ExtendedData() + isom.getISO_122_ExtendedData() + "|");
	        					  
							}
	        				
	        				iso.setISO_102_AccountID_1(StringUtils.trimEnd(iso.getISO_102_AccountID_1(), "|"));
	        				iso.setISO_120_ExtendedData(StringUtils.trimEnd(iso.getISO_120_ExtendedData(), "|"));
	        				iso.setISO_121_ExtendedData(StringUtils.trimEnd(iso.getISO_121_ExtendedData(), "|"));
	        				iso.setISO_122_ExtendedData(StringUtils.trimEnd(iso.getISO_122_ExtendedData(), "|"));
	        				iso.setWsIso_LogStatus(2);
	        				
	    	    		}else {
	    	    			
	    	    			iso.setISO_039_ResponseCode(isoR.getISO_039_ResponseCode());
	        				iso.setISO_039p_ResponseDetail(isoR.getISO_039p_ResponseDetail());
	    	    		}
	    	    		
	    			}else {
	    				
	    				iso.setISO_039_ResponseCode("116");
	    				iso.setISO_039p_ResponseDetail("EL CLIENTE NO TIENE ESE PRODUCTO DE CUENTA VISTA");
	    			}
	    			
	    		}else {
	    			
	    			iso.setISO_039_ResponseCode("116");
					iso.setISO_039p_ResponseDetail("EL CLIENTE NO TIENE ESE PRODUCTO DE CUENTA VISTA");
	    		}
			}
    		
		} catch (Exception e) {
			
			
			if(iso.getTickAut().isStarted())
				iso.getTickAut().stop();
			log.WriteLogMonitor("Error modulo IVRIsAcq::processorMantenainceVista ", 
					TypeMonitor.error, e);
			iso.setISO_039_ResponseCode("909");
			iso.setISO_039p_ResponseDetail("ERROR EN PROCESOS " + GeneralUtils.ExceptionToString(null, e, false));
			
		}finally {
			
			iso.setISO_114_ExtendedData(null);
			
			if(iso.getISO_039_ResponseCode().equals("000")) {
				
				iso.setAuxiliarArrayValues(new String[] { "name|" + FormatUtils.DateToString(new Date(), "yyyy/MM/dd") + "|" 
						+ FormatUtils.DateToString(new Date(), "HH:mm:ss"),
				        "#|" + FormatUtils.DateToString(new Date(), "yyyy/MM/dd") + "|" + FormatUtils.DateToString(new Date(), "HH:mm")});
			}
			
			iso.setWsTempAut((iso.getTickAut().getTime(TimeUnit.MILLISECONDS)/1000.0));
		}
    	return iso;
    }
    
    private List<wIso8583> getCtaVistaList(wIso8583 iso) {
		
    	List<wIso8583> isoList = new ArrayList<>();
		try {
			
			iso.setISO_041_CardAcceptorID("1800-292929");
			if(iso.getISO_039_ResponseCode().equals("000")) {
				iso.getTickAut().reset();
				iso.getTickAut().start();
				@SuppressWarnings("unchecked")
				List<Iterables> it = (List<Iterables>) SerializationObject
						.XMLToObject(iso.getISO_114_ExtendedData());
				if(it != null) {
				
					/*Filtraje del producto OJO Si piden cambiar a que se BLOQUEEN TODOS LOS PRODUCTOS*/
					/*Requerimiento del 19/11/2018 Marianela Reascos solicita bloquear los productos
					 * Ahorros Corriente, Ahorro Cliente, Ahorro Angelito*/
					List<Iterables> filterVigentes = it.stream()
	                        .filter(p -> p.getIterarors().get("CGRUPOPRODUCTO||tc0.CPRODUCTO")
	                        .equalsIgnoreCase("0101") ||  p.getIterarors().get("CGRUPOPRODUCTO||tc0.CPRODUCTO")
	                        .equalsIgnoreCase("0107") ||  p.getIterarors().get("CGRUPOPRODUCTO||tc0.CPRODUCTO")
	                        .equalsIgnoreCase("0106"))
	                        .peek(Objects::requireNonNull)
	    					.collect(Collectors.toList());
					
					if(filterVigentes != null) {
						
						if(filterVigentes.size() > 0) {
							
							for (int i = 0; i < filterVigentes.size(); i++) {
								
								wIso8583 isoClone = (wIso8583) iso.clone();
								isoClone.setWsTransactionConfig((TransactionConfiguration)iso.getWsTransactionConfig().CloneObject());
								isoClone.setISO_023_CardSeq(String.valueOf(filterVigentes.size()));
								isoClone.setISO_034_PANExt(filterVigentes.get(i).getIterarors().get("NOMBRECUENTA"));
								isoClone.setISO_035_Track2(iso.getISO_035_Track2() + filterVigentes.get(i).getIterarors().get("DESCRIPCION") + "|");
								isoClone.setISO_102_AccountID_1(filterVigentes.get(i).getIterarors().get("CCUENTA"));
								isoList.add(isoClone);
							}
							iso.setISO_035_Track2(StringUtils.trimEnd(iso.getISO_035_Track2(), "|"));
						}
						
					}else {
						
						iso.setISO_039_ResponseCode("116");
						iso.setISO_039p_ResponseDetail("EL CLIENTE NO TIENE ESE PRODUCTO DE CUENTA VISTA");
					}
					
				}else {
					
					iso.setISO_039_ResponseCode("116");
					iso.setISO_039p_ResponseDetail("EL CLIENTE NO TIENE CUENTAS A LA VISTA");
				}
			}
							
			if(iso.getTickAut().isStarted())
				iso.getTickAut().stop();
			
		}catch (Exception e) {
			
			iso.setISO_039_ResponseCode("909");
			iso.setISO_039p_ResponseDetail("ERROR EN PROCESOS " + GeneralUtils.ExceptionToString(null, e, false));
			if(iso.getTickAut().isStarted())
				iso.getTickAut().stop();
			log.WriteLogMonitor("Error modulo IVRIsAcq::getCtaVistaList ", 
					TypeMonitor.error, e);
		}finally {
			
			iso.setWsTempAut((iso.getTickAut().getTime(TimeUnit.MILLISECONDS)/1000.0));
		}
		
		return isoList;
	}
	
    public wIso8583 manCtasVista(wIso8583 iso) {
		
		FitIsAut auth = null;
		try {
			
			iso.setISO_041_CardAcceptorID("1800-292929");
			if(iso.getISO_039_ResponseCode().equals("000")) {
				
				wIso8583 isoClone = (wIso8583) iso.clone();
				isoClone.setWsTransactionConfig((TransactionConfiguration)iso.getWsTransactionConfig().CloneObject());
				isoClone.getWsTransactionConfig().setProccodeTransactionFit(Arrays.asList(isoClone.getWsTransactionConfig()
						.getProccodeTransactionFit().split("\\|")).get(2));
				
				auth = new FitIsAut();
				iso.getTickAut().reset();
				iso.getTickAut().start();
					isoClone = auth.ExecuteStandarTransaction(isoClone);
				if(iso.getTickAut().isStarted())
					iso.getTickAut().stop();
				
				iso.setISO_039_ResponseCode(isoClone.getISO_039_ResponseCode());
				iso.setISO_039p_ResponseDetail(isoClone.getISO_039p_ResponseDetail());
				iso.setWsIso_LogStatus(isoClone.getWsIso_LogStatus());
			}
			
		}catch (Exception e) {
			
			if(iso.getTickAut().isStarted())
				iso.getTickAut().stop();
			log.WriteLogMonitor("Error modulo IVRIsAcq::manCtasVista ", 
					TypeMonitor.error, e);
			iso.setISO_039_ResponseCode("909");
			iso.setISO_039p_ResponseDetail("ERROR EN PROCESOS " + GeneralUtils.ExceptionToString(null, e, false));
			
		}finally {
			
			iso.setISO_114_ExtendedData(null);
			iso.setWsTempAut((iso.getTickAut().getTime(TimeUnit.MILLISECONDS)/1000.0));
		}
		
		return iso;
	}
	
    public wIso8583 EvaluatePreEnrolamiento(wIso8583 iso) {
    	
    	try {
			
    		iso.setISO_041_CardAcceptorID("1800-292929");
    		if(iso.getISO_039_ResponseCode().equals("000")) {
    			
    			iso.getTickAut().reset();
				iso.getTickAut().start();
    			if(iso.getISO_123_ExtendedData().equals("N/D") && iso.getISO_124_ExtendedData().equals("N/D")) {
    				
    				iso.setISO_039_ResponseCode("116");
    				iso.setISO_039p_ResponseDetail("CLIENTE NO POSEE CORREO ELECTRONICO NI CELULAR");
    				iso.setISO_104_TranDescription("EL SOCIO/CLIENTE DEBERA ACERCARSE A AGENCIA ACTUALIZAR SUS DATOS");
    				
    			}else {
    				
    				iso.setISO_039_ResponseCode("000");
    				iso.setISO_039p_ResponseDetail("TRANSACCION EXITOSA");
    			}
    			iso.setWsIso_LogStatus(2);
    			if(iso.getTickAut().isStarted())
    				iso.getTickAut().stop();
    		}
    		
		} catch (Exception e) {
			
			if(iso.getTickAut().isStarted())
				iso.getTickAut().stop();
			log.WriteLogMonitor("Error modulo IVRIsAcq::getCtaVistaList ", 
					TypeMonitor.error, e);
			iso.setISO_039_ResponseCode("909");
			iso.setISO_039p_ResponseDetail("ERROR EN PROCESOS " + GeneralUtils.ExceptionToString(null, e, false));
			
		}finally {
			
			iso.setISO_123_ExtendedData(null);
			iso.setISO_124_ExtendedData(null);
			iso.setWsTempAut((iso.getTickAut().getTime(TimeUnit.MILLISECONDS)/1000.0));
		}
    	return iso;
    }
    
    public wIso8583 EnrolamientoIVR(wIso8583 iso) {
    	
    	EngineProcessorTransactionsBackGround engineBack = null;
    	try {
			
    		iso.setISO_041_CardAcceptorID("1800-292929");
    		if(iso.getISO_039_ResponseCode().equals("000")) {
    			
    			iso.getTickAut().reset();
				iso.getTickAut().start();
    			if(iso.getISO_123_ExtendedData().equals("N/D") && iso.getISO_124_ExtendedData().equals("N/D")) {
    				
    				iso.setISO_039_ResponseCode("116");
    				iso.setISO_039p_ResponseDetail("CLIENTE NO POSEE CORREO ELECTRONICO NI CELULAR");
    				iso.setISO_104_TranDescription("EL SOCIO/CLIENTE DEBERA ACERCARSE A AGENCIA ACTUALIZAR SUS DATOS");
    				
    			}else {
    				
    				engineBack = new EngineProcessorTransactionsBackGround();
    				Ref<wIso8583> refResponse = new Ref<>(iso);
    				/*Se ejecuta una transaccion INDIVIDUAL en este caso la GENERACION de OTP*/
    				iso = engineBack.ExecutionIndividualTransaction(iso, refResponse);
    				iso.setISO_039_ResponseCode(refResponse.get().getISO_039_ResponseCode());
    				iso.setISO_039p_ResponseDetail(refResponse.get().getISO_039p_ResponseDetail());
    				iso.setISO_052_PinBlock(refResponse.get().getISO_052_PinBlock());
    				iso.setISO_037_RetrievalReferenceNumber(refResponse.get().getISO_037_RetrievalReferenceNumber());
    				/*Alisto el mensaje para envio de SMS y MAIL*/
    				if(iso.getISO_039_ResponseCode().equals("000")) {
    					
    					iso.setAuxiliarArrayValues(new String[] { "name|" + iso.getISO_052_PinBlock() + "|" + 
    					refResponse.get().getISO_121_ExtendedData(),
    					"#|" + iso.getISO_052_PinBlock() + "|" + refResponse.get().getISO_121_ExtendedData()});
    				}
    				
    			}
    			iso.setWsIso_LogStatus(2);
    			if(iso.getTickAut().isStarted())
    				iso.getTickAut().stop();
    		}
    		
    		
		} catch (Exception e) {
			
			if(iso.getTickAut().isStarted())
				iso.getTickAut().stop();
			log.WriteLogMonitor("Error modulo IVRIsAcq::getCtaVistaList ", 
					TypeMonitor.error, e);
			iso.setISO_039_ResponseCode("909");
			iso.setISO_039p_ResponseDetail("ERROR EN PROCESOS " + GeneralUtils.ExceptionToString(null, e, false));
			
		}finally {
			
			iso.setWsTempAut((iso.getTickAut().getTime(TimeUnit.MILLISECONDS)/1000.0));
		}
    	return iso;
    }
    
    public wIso8583 ValidaUserIVRExe(wIso8583 iso) {
    	
    	wIso8583 isoClone = null;
    	IsoRetrievalTransaction sql = null;
    	try {
			
    		isoClone = (wIso8583) iso.clone();
    		if(iso.getISO_003_ProcessingCode().equals("585099"))
    			isoClone.setISO_003_ProcessingCode("700001");
    		else
    			isoClone.setISO_003_ProcessingCode("540001");
    		sql = new IsoRetrievalTransaction();
    		iso.getTickAut().reset();
			iso.getTickAut().start();
				isoClone = sql.RetrieveAdminIVR(isoClone);
			if(iso.getTickAut().isStarted())
				iso.getTickAut().stop();
			iso.setISO_039_ResponseCode(isoClone.getISO_039_ResponseCode());
    		iso.setISO_039p_ResponseDetail(isoClone.getISO_039p_ResponseDetail());
    		iso.setWsIso_LogStatus(isoClone.getWsIso_LogStatus());
    		
		} catch (Exception e) {
			
			if(iso.getTickAut().isStarted())
				iso.getTickAut().stop();
			log.WriteLogMonitor("Error modulo IVRIsAcq::ValidaUserIVRExe ", 
					TypeMonitor.error, e);
			iso.setISO_039_ResponseCode("909");
			iso.setISO_039p_ResponseDetail("ERROR EN PROCESOS " + GeneralUtils.ExceptionToString(null, e, false));
			
		}finally {
			
			iso.setWsTempAut((iso.getTickAut().getTime(TimeUnit.MILLISECONDS)/1000.0));
		}
    	return iso;
    }
    
    public wIso8583 getNameUserIVR(wIso8583 iso) {
    	
    	IsoRetrievalTransaction sql = null;
    	iso.setISO_041_CardAcceptorID("1800-292929");
    	try {
			
    		sql = new IsoRetrievalTransaction();
    		iso.getTickAut().reset();
			iso.getTickAut().start();
				iso = sql.getDataIvrCallCenter(iso);
			if(iso.getTickAut().isStarted())
				iso.getTickAut().stop();
    		
		} catch (Exception e) {
			
			if(iso.getTickAut().isStarted())
				iso.getTickAut().stop();
			log.WriteLogMonitor("Error modulo IVRIsAcq::getNameUserIVR ", 
					TypeMonitor.error, e);
			iso.setISO_039_ResponseCode("909");
			iso.setISO_039p_ResponseDetail("ERROR EN PROCESOS " + GeneralUtils.ExceptionToString(null, e, false));
			
		}finally {
			
			iso.setWsTempAut((iso.getTickAut().getTime(TimeUnit.MILLISECONDS)/1000.0));
		}
    	return iso;
    }
    
    public wIso8583 ProcessBDD_IVR_ENROLL(wIso8583 iso) {
    	
    	IsoRetrievalTransaction sql = null;
    	wIso8583 isoClone = null;
    	try {
			
    		iso.setISO_041_CardAcceptorID("1800-292929");
    		if(iso.getISO_039_ResponseCode().equals("000")) {
    			
    			sql = new IsoRetrievalTransaction();
    			isoClone = (wIso8583) iso.clone();
    			isoClone.setISO_034_PANExt(iso.getISO_122_ExtendedData());
    			isoClone.setISO_052_PinBlock(iso.getISO_037_RetrievalReferenceNumber());
    			iso.getTickAut().reset();
				iso.getTickAut().start();
    				isoClone = sql.RetrieveAdminIVR(isoClone);
    			if(iso.getTickAut().isStarted())
    				iso.getTickAut().stop();
    			iso.setISO_039_ResponseCode(isoClone.getISO_039_ResponseCode());
    			iso.setISO_039p_ResponseDetail(isoClone.getISO_039p_ResponseDetail());
    			iso.setWsIso_LogStatus(isoClone.getWsIso_LogStatus());
    		}
    		
		} catch (Exception e) {
			
			if(iso.getTickAut().isStarted())
				iso.getTickAut().stop();
			log.WriteLogMonitor("Error modulo IVRIsAcq::ProcessBDD_IVR_ENROLL ", 
					TypeMonitor.error, e);
			iso.setISO_039_ResponseCode("909");
			iso.setISO_039p_ResponseDetail("ERROR EN PROCESOS " + GeneralUtils.ExceptionToString(null, e, false));
			
		}finally {
		
			iso.setWsTempAut((iso.getTickAut().getTime(TimeUnit.MILLISECONDS)/1000.0));
		}
    	
    	return iso;
    }
    
    public wIso8583 ProcessBDD_IVR_LOGIN(wIso8583 iso) {
    	
    	IsoRetrievalTransaction sql = null;
    	EngineProcessorTransactionsBackGround engineBack = null;
    	try {
			
    		iso.setISO_041_CardAcceptorID("1800-292929");
			sql = new IsoRetrievalTransaction();
			iso.getTickAut().reset();
			iso.getTickAut().start();
				iso = sql.RetrieveAdminIVR(iso);
				/*OJO Valido la caducidad del OTP 
				 * ojo cuando es activo y es primera vez que lo usa*/
				if(iso.getISO_120_ExtendedData().equalsIgnoreCase("A") &&
						iso.getISO_121_ExtendedData().equalsIgnoreCase("Y")) {
					
					wIso8583 isoClone = (wIso8583) iso.clone();
					Ref<wIso8583> refResponse = new Ref<>(isoClone);
					/*Se ejecuta una transaccion INDIVIDUAL en este caso para VALIDACION DE PINBLOCK TIPO OTP*/
					engineBack = new EngineProcessorTransactionsBackGround();
					iso = engineBack.ExecutionIndividualTransaction(isoClone, refResponse);
					iso.setISO_039_ResponseCode(refResponse.get().getISO_039_ResponseCode());
					iso.setISO_039p_ResponseDetail(refResponse.get().getISO_039p_ResponseDetail());
					
				}
				
			if(iso.getTickAut().isStarted())
				iso.getTickAut().stop();
    		
		} catch (Exception e) {
			
			if(iso.getTickAut().isStarted())
				iso.getTickAut().stop();
			log.WriteLogMonitor("Error modulo IVRIsAcq::ProcessBDD_IVR_LOGIN ", 
					TypeMonitor.error, e);
			iso.setISO_039_ResponseCode("909");
			iso.setISO_039p_ResponseDetail("ERROR EN PROCESOS " + GeneralUtils.ExceptionToString(null, e, false));
			
		}finally {
		
			if(iso.getISO_039_ResponseCode().equals("000")) {
				
				iso.setAuxiliarArrayValues(new String[] { "name|" + FormatUtils.DateToString(new Date(), "yyyy/MM/dd") + "|" 
						+ FormatUtils.DateToString(new Date(), "HH:mm:ss")
						,"#|" + FormatUtils.DateToString(new Date(), "yyyy/MM/dd") + "|" + FormatUtils.DateToString(new Date(), "HH:mm")});
			}else if(iso.getISO_039_ResponseCode().equals("123")) {
				
				try {
					
					final wIso8583 isoSend = (wIso8583) iso.clone();
					final wIso8583 isoSend2 = (wIso8583) iso.clone();
					Thread tSendMailBlq = new Thread(new Runnable() {
						
						@Override
						public void run() {
							
							SendMailForBlq(isoSend);
						}
					});
					
					Thread tSendSMSBlq = new Thread(new Runnable() {
						
						@Override
						public void run() {
							
							SendSMSForBlq(isoSend2);
						}
					});
					tSendMailBlq.start();
					tSendSMSBlq.start();
					
				} catch (Exception e2) {}
				
			}
			
			iso.setWsTempAut((iso.getTickAut().getTime(TimeUnit.MILLISECONDS)/1000.0));
		}
    	
    	return iso;
    }
    
    protected void SendMailForBlq(wIso8583 iso) {
    	
    	csProcess processor = null;
    	Iso8583 iso8583 = new Iso8583();
    	try {
			iso8583.setISO_000_Message_Type("1200");
			iso8583.setISO_002_PAN(iso.getISO_002_PAN());
			iso8583.setISO_003_ProcessingCode("610002");
			iso8583.setISO_011_SysAuditNumber(GeneralUtils.GetSecuencialNumeric(11));
			iso8583.setISO_018_MerchantType("IVRMAILBLOQUEO");
			iso8583.setISO_024_NetworkId("555551");
			iso8583.setISO_035_Track2("name|" + FormatUtils.DateToString(new Date(), "yyyy/MM/dd") + "|" 
						+ FormatUtils.DateToString(new Date(), "HH:mm:ss"));
			iso8583.setISO_036_Track3("NOTIFICACION BLOQUEO IVR");
			iso8583.setISO_041_CardAcceptorID("1800-292929");
			iso8583.setISO_090_OriginalData("5");
    		processor = new csProcess();
    		iso8583 = processor.ProcessTransactionMain(iso8583, "127.0.0.1");
    		
		} catch (Exception e) {
		
			log.WriteLogMonitor("Error modulo IVRIsAcq::SendMailForBlq ", 
					TypeMonitor.error, e);
		}
    }
    
    protected void SendSMSForBlq(wIso8583 iso) {
    	
    	csProcess processor = null;
    	Iso8583 iso8583 = new Iso8583();
    	try {
			iso8583.setISO_000_Message_Type("1200");
			iso8583.setISO_002_PAN(iso.getISO_002_PAN());
			iso8583.setISO_003_ProcessingCode("610001");
			iso8583.setISO_011_SysAuditNumber(GeneralUtils.GetSecuencialNumeric(11));
			iso8583.setISO_018_MerchantType("IVRSMSBLOQUEO");
			iso8583.setISO_024_NetworkId("555551");
			iso8583.setISO_035_Track2("#|" + FormatUtils.DateToString(new Date(), "yyyy/MM/dd") + "|" 
						+ FormatUtils.DateToString(new Date(), "HH:mm"));
			iso8583.setISO_041_CardAcceptorID("1800-292929");
			iso8583.setISO_090_OriginalData("4");
    		processor = new csProcess();
    		iso8583 = processor.ProcessTransactionMain(iso8583, "127.0.0.1");
    		
		} catch (Exception e) {
		
			log.WriteLogMonitor("Error modulo IVRIsAcq::SendSMSForBlq ", 
					TypeMonitor.error, e);
		}
    }
    
    public wIso8583 getNotificationQuestionDesafio(wIso8583 iso) {
    	
    	IsoRetrievalTransaction sql = null;
    	try {
			
    		iso.setISO_041_CardAcceptorID("1800-292929");
			sql = new IsoRetrievalTransaction();
			iso.getTickAut().reset();
			iso.getTickAut().start();
				iso = sql.RetrieveAdminIVR(iso);
			if(iso.getTickAut().isStarted())
				iso.getTickAut().stop();
    		
		} catch (Exception e) {
			
			if(iso.getTickAut().isStarted())
				iso.getTickAut().stop();
			log.WriteLogMonitor("Error modulo IVRIsAcq::getNotificationQuestionDesafio ", 
					TypeMonitor.error, e);
			iso.setISO_039_ResponseCode("909");
			iso.setISO_039p_ResponseDetail("ERROR EN PROCESOS " + GeneralUtils.ExceptionToString(null, e, false));
			
		}finally {
		
			iso.setWsTempAut((iso.getTickAut().getTime(TimeUnit.MILLISECONDS)/1000.0));
		}
    	
    	return iso;
    }
    
   public wIso8583 Process_IVR_ChangePin(wIso8583 iso) {
    	
    	IsoRetrievalTransaction sql = null;
    	EngineProcessorTransactionsBackGround engineBack = null;
    	try {
			
    		iso.setISO_041_CardAcceptorID("1800-292929");
    		wIso8583 isoClone = (wIso8583) iso.clone();
    		/*Pongo la bandera de recursividad en TRUE para que no ejecute comandos asociados
    		 * en este caso el SMS del LOGIN*/
    		isoClone.setRecursive(true);
    		iso.getTickAut().reset();
			iso.getTickAut().start();
    		engineBack = new EngineProcessorTransactionsBackGround();
			Ref<wIso8583> refResponse = new Ref<>(isoClone);
			/*Se ejecuta una transaccion INDIVIDUAL en este caso la LOGIN IVR*/
			iso = engineBack.ExecutionIndividualTransaction(isoClone, refResponse);
			iso.setISO_039_ResponseCode(refResponse.get().getISO_039_ResponseCode());
			iso.setISO_039p_ResponseDetail(refResponse.get().getISO_039p_ResponseDetail());
    		iso.setISO_120_ExtendedData(refResponse.get().getISO_120_ExtendedData());
    		iso.setISO_121_ExtendedData(refResponse.get().getISO_121_ExtendedData());
			
    		if(iso.getISO_039_ResponseCode().equals("000")) {
				sql = new IsoRetrievalTransaction();
				iso = sql.RetrieveAdminIVR(iso);
				/*Para envio de Correos*/
				iso.setAuxiliarArrayValues(new String[] { "name|" + FormatUtils.DateToString(new Date(), "yyyy/MM/dd") + "|" 
				+ FormatUtils.DateToString(new Date(), "HH:mm:ss")
				,"#|" + FormatUtils.DateToString(new Date(), "yyyy/MM/dd") + "|" + FormatUtils.DateToString(new Date(), "HH:mm")});
    		}
			if(iso.getTickAut().isStarted())
				iso.getTickAut().stop();
    		
		} catch (Exception e) {
			
			if(iso.getTickAut().isStarted())
				iso.getTickAut().stop();
			log.WriteLogMonitor("Error modulo IVRIsAcq::Process_IVR_ChangePin ", 
					TypeMonitor.error, e);
			iso.setISO_039_ResponseCode("909");
			iso.setISO_039p_ResponseDetail("ERROR EN PROCESOS " + GeneralUtils.ExceptionToString(null, e, false));
			
		}finally {
		
			iso.setWsTempAut((iso.getTickAut().getTime(TimeUnit.MILLISECONDS)/1000.0));
		}
    	
    	return iso;
    }
    
   public wIso8583 Process_IVR_ForgottenPin(wIso8583 iso) {
   	
   	EngineProcessorTransactionsBackGround engineBack = null;
   	try {
			
   		iso.setISO_041_CardAcceptorID("1800-292929");
   		wIso8583 isoClone = (wIso8583) iso.clone();
   		
   		iso.getTickAut().reset();
			iso.getTickAut().start();
   		engineBack = new EngineProcessorTransactionsBackGround();
			Ref<wIso8583> refResponse = new Ref<>(isoClone);
			/*Se ejecuta una transaccion INDIVIDUAL en este caso la de ENROLAMIENTO*/
			iso = engineBack.ExecutionIndividualTransaction(isoClone, refResponse);
			iso.setISO_039_ResponseCode(refResponse.get().getISO_039_ResponseCode());
			iso.setISO_039p_ResponseDetail(refResponse.get().getISO_039p_ResponseDetail());
			iso.setISO_037_RetrievalReferenceNumber(refResponse.get().getISO_037_RetrievalReferenceNumber());
			iso.setISO_052_PinBlock(refResponse.get().getISO_052_PinBlock());
			if(iso.getTickAut().isStarted())
				iso.getTickAut().stop();
   		
		} catch (Exception e) {
			
			if(iso.getTickAut().isStarted())
				iso.getTickAut().stop();
			log.WriteLogMonitor("Error modulo IVRIsAcq::ProcessBDD_IVR_LOGIN ", 
					TypeMonitor.error, e);
			iso.setISO_039_ResponseCode("909");
			iso.setISO_039p_ResponseDetail("ERROR EN PROCESOS " + GeneralUtils.ExceptionToString(null, e, false));
			
		}finally {
		
			iso.setWsTempAut((iso.getTickAut().getTime(TimeUnit.MILLISECONDS)/1000.0));
		}
   	
   	return iso;
   }
   
   public wIso8583 getDesafio1(wIso8583 iso) {
		
		try {
			
			iso.setISO_041_CardAcceptorID("1800-292929");
			if(iso.getISO_039_ResponseCode().equals("000")) {
				iso.getTickAut().reset();
				iso.getTickAut().start();
				@SuppressWarnings("unchecked")
				List<Iterables> it = (List<Iterables>) SerializationObject
						.XMLToObject(iso.getISO_114_ExtendedData());
				if(it != null) {
				
					List<Iterables> filterVigentes = it.stream()
							                        .filter(p -> p.getIterarors().get("CESTATUSCUENTA")
							                        .equalsIgnoreCase("003") || p.getIterarors().get("CESTATUSCUENTA")
							                        .equalsIgnoreCase("004"))
							                        .peek(Objects::requireNonNull)
							    					.collect(Collectors.toList());
					if(filterVigentes != null) {
						
						if(filterVigentes.size() > 0) 
							iso.setISO_090_OriginalData("1");
						else
							iso.setISO_090_OriginalData("0");
						
					}else 
						iso.setISO_090_OriginalData("0");			                        
				}else 
					iso.setISO_090_OriginalData("0");
			}else 
				iso.setISO_090_OriginalData("0");
			
			if(iso.getTickAut().isStarted())
				iso.getTickAut().stop();
			
		}catch (Exception e) {
			
			if(iso.getTickAut().isStarted())
				iso.getTickAut().stop();
			log.WriteLogMonitor("Error modulo IVRIsAcq::getDesafio1 ", 
					TypeMonitor.error, e);
		}finally {
			
			iso.setISO_039_ResponseCode("000");
			iso.setISO_039p_ResponseDetail("TRANSACCION EXITOSA");
			iso.setISO_114_ExtendedData(null);
			iso.setWsTempAut((iso.getTickAut().getTime(TimeUnit.MILLISECONDS)/1000.0));
		}
		
		return iso;
   }
   
   public wIso8583 getDesafio1_(wIso8583 iso) {
		
	   IsoRetrievalTransaction sql = null;
		try {
			
			iso.setISO_041_CardAcceptorID("1800-292929");
			if(iso.getISO_039_ResponseCode().equals("000")) {
				
				sql = new IsoRetrievalTransaction();
				iso.getTickAut().reset();
				iso.getTickAut().start();
				
				iso = sql.getCountCtasPrestamos(iso);
				if(iso.getISO_039_ResponseCode().equals("000")) {
					int nroInv = Integer.parseInt(iso.getISO_023_CardSeq());
					if(nroInv > 0)
						iso.setISO_090_OriginalData("1");
					else
						iso.setISO_090_OriginalData("0");
				}else {
					
					iso.setISO_090_OriginalData("0");
				}
			}else 
				iso.setISO_090_OriginalData("0");
			
			if(iso.getTickAut().isStarted())
				iso.getTickAut().stop();
			
		}catch (Exception e) {
			
			if(iso.getTickAut().isStarted())
				iso.getTickAut().stop();
			log.WriteLogMonitor("Error modulo IVRIsAcq::getDesafio1_ ", 
					TypeMonitor.error, e);
		}finally {
			
			iso.setISO_039_ResponseCode("000");
			iso.setISO_039p_ResponseDetail("TRANSACCION EXITOSA");
			iso.setISO_114_ExtendedData(null);
			iso.setWsTempAut((iso.getTickAut().getTime(TimeUnit.MILLISECONDS)/1000.0));
		}
		
		return iso;
	}
   
   public wIso8583 getDesafio2(wIso8583 iso) {
		
		try {
			
			iso.setISO_041_CardAcceptorID("1800-292929");
			if(iso.getISO_039_ResponseCode().equals("000")) {
				iso.getTickAut().reset();
				iso.getTickAut().start();
				@SuppressWarnings("unchecked")
				List<Iterables> it = (List<Iterables>) SerializationObject
						.XMLToObject(iso.getISO_114_ExtendedData());
				if(it != null) {
				
					List<Iterables> filterVigentes = it.stream()
							                        .filter(p -> p.getIterarors().get("DESCRIPCIONESTATUSCUENTA")
							                        .equalsIgnoreCase("VIGENTE"))
							                        .peek(Objects::requireNonNull)
							    					.collect(Collectors.toList());
					if(filterVigentes != null) {
						
						if(filterVigentes.size() > 0)
							iso.setISO_090_OriginalData("1");
						else
							iso.setISO_090_OriginalData("0");
						
					}else 
						iso.setISO_090_OriginalData("0");			                        
				}else 
					iso.setISO_090_OriginalData("0");
			}else 
				iso.setISO_090_OriginalData("0");
			
			if(iso.getTickAut().isStarted())
				iso.getTickAut().stop();
			
		}catch (Exception e) {
			
			if(iso.getTickAut().isStarted())
				iso.getTickAut().stop();
			log.WriteLogMonitor("Error modulo IVRIsAcq::getDesafio2 ", 
					TypeMonitor.error, e);
		}finally {
			
			iso.setISO_039_ResponseCode("000");
			iso.setISO_039p_ResponseDetail("TRANSACCION EXITOSA");
			iso.setISO_114_ExtendedData(null);
			iso.setWsTempAut((iso.getTickAut().getTime(TimeUnit.MILLISECONDS)/1000.0));
		}
		
		return iso;
	}
   
   public wIso8583 getDesafio2_(wIso8583 iso) {
		
	   IsoRetrievalTransaction sql = null;
		try {
			
			iso.setISO_041_CardAcceptorID("1800-292929");
			if(iso.getISO_039_ResponseCode().equals("000")) {
				
				sql = new IsoRetrievalTransaction();
				iso.getTickAut().reset();
				iso.getTickAut().start();
				
				iso = sql.getCountCtasInversiones(iso);
				if(iso.getISO_039_ResponseCode().equals("000")) {
					int nroInv = Integer.parseInt(iso.getISO_023_CardSeq());
					if(nroInv > 0)
						iso.setISO_090_OriginalData("1");
					else
						iso.setISO_090_OriginalData("0");
				}else {
					
					iso.setISO_090_OriginalData("0");
				}
			}else 
				iso.setISO_090_OriginalData("0");
			
			if(iso.getTickAut().isStarted())
				iso.getTickAut().stop();
			
		}catch (Exception e) {
			
			if(iso.getTickAut().isStarted())
				iso.getTickAut().stop();
			log.WriteLogMonitor("Error modulo IVRIsAcq::getDesafio2_ ", 
					TypeMonitor.error, e);
		}finally {
			
			iso.setISO_039_ResponseCode("000");
			iso.setISO_039p_ResponseDetail("TRANSACCION EXITOSA");
			iso.setISO_114_ExtendedData(null);
			iso.setWsTempAut((iso.getTickAut().getTime(TimeUnit.MILLISECONDS)/1000.0));
		}
		
		return iso;
	}
    
   public wIso8583 getDesafio3(wIso8583 iso) {
		
		try {
			
			iso.setISO_041_CardAcceptorID("1800-292929");
			if(iso.getISO_039_ResponseCode().equals("000")) {
				iso.getTickAut().reset();
				iso.getTickAut().start();
				iso.setISO_090_OriginalData("1");
			}else 
				iso.setISO_090_OriginalData("0");
			
			if(iso.getTickAut().isStarted())
				iso.getTickAut().stop();
			
		}catch (Exception e) {
			
			if(iso.getTickAut().isStarted())
				iso.getTickAut().stop();
			log.WriteLogMonitor("Error modulo IVRIsAcq::getDesafio3 ", 
					TypeMonitor.error, e);
		}finally {
			
			iso.setISO_039_ResponseCode("000");
			iso.setISO_039p_ResponseDetail("TRANSACCION EXITOSA");
			iso.setISO_114_ExtendedData(null);
			iso.setWsTempAut((iso.getTickAut().getTime(TimeUnit.MILLISECONDS)/1000.0));
		}
		
		return iso;
	}
    
   public wIso8583 getDesafio4(wIso8583 iso) {
		
		try {
			
			iso.setISO_041_CardAcceptorID("1800-292929");
			
				iso.getTickAut().reset();
				iso.getTickAut().start();
			
				iso.setISO_090_OriginalData("0");
				
				if(iso.getTickAut().isStarted())
					iso.getTickAut().stop();
			
		}catch (Exception e) {
			
			if(iso.getTickAut().isStarted())
				iso.getTickAut().stop();
			log.WriteLogMonitor("Error modulo IVRIsAcq::getDesafio3 ", 
					TypeMonitor.error, e);
		}finally {
			
			iso.setISO_039_ResponseCode("000");
			iso.setISO_039p_ResponseDetail("TRANSACCION EXITOSA");
			iso.setISO_114_ExtendedData(null);
			iso.setWsTempAut((iso.getTickAut().getTime(TimeUnit.MILLISECONDS)/1000.0));
		}
		
		return iso;
	}
    
   public wIso8583 getDesafio5(wIso8583 iso) {
		
		try {
			
			iso.setISO_041_CardAcceptorID("1800-292929");
			if(iso.getISO_039_ResponseCode().equals("000")) {
				iso.getTickAut().reset();
				iso.getTickAut().start();
				@SuppressWarnings("unchecked")
				List<Iterables> it = (List<Iterables>) SerializationObject
						.XMLToObject(iso.getISO_114_ExtendedData());
				if(it != null) {
				
					if(it.size() > 1) {
						
						iso.setISO_090_OriginalData("1");
						
					}else 
						iso.setISO_090_OriginalData("0");			                        
				}else 
					iso.setISO_090_OriginalData("0");
			}else 
				iso.setISO_090_OriginalData("0");
			
			if(iso.getTickAut().isStarted())
				iso.getTickAut().stop();
			
		}catch (Exception e) {
			
			if(iso.getTickAut().isStarted())
				iso.getTickAut().stop();
			log.WriteLogMonitor("Error modulo IVRIsAcq::getDesafio5 ", 
					TypeMonitor.error, e);
		}finally {
			
			iso.setISO_039_ResponseCode("000");
			iso.setISO_039p_ResponseDetail("TRANSACCION EXITOSA");
			iso.setISO_114_ExtendedData(null);
			iso.setWsTempAut((iso.getTickAut().getTime(TimeUnit.MILLISECONDS)/1000.0));
		}
		
		return iso;
	}
    
   public wIso8583 getDesafio6(wIso8583 iso) {
		
    	boolean escenario1 = false;
    	boolean escenario2 = false;
    	
		try {
			
			iso.setISO_041_CardAcceptorID("1800-292929");
			if(iso.getISO_039_ResponseCode().equals("000")) {
				iso.getTickAut().reset();
				iso.getTickAut().start();
				@SuppressWarnings("unchecked")
				List<Iterables> it = (List<Iterables>) SerializationObject
						.XMLToObject(iso.getISO_114_ExtendedData());
				if(it != null) {
				
					List<Iterables> filterVigentes = it.stream()
							                        .filter(p -> p.getIterarors().get("CESTATUSCUENTA")
							                        .equalsIgnoreCase("003") && p.getIterarors().get("CCONDICIONOPERATIVA")
							                        .equalsIgnoreCase("CBR") || p.getIterarors().get("CCONDICIONOPERATIVA")
							                        .equalsIgnoreCase("LEG"))
							                        .peek(Objects::requireNonNull)
							    					.collect(Collectors.toList());
					
					List<Iterables> filterVencidos = it.stream()
	                        .filter(p -> p.getIterarors().get("CESTATUSCUENTA")
	                        .equalsIgnoreCase("004"))
	                        .peek(Objects::requireNonNull)
	    					.collect(Collectors.toList());
					
					if(filterVigentes != null) {
						if(filterVigentes.size() > 0)
							escenario1 = true;
					}
					
					if(filterVencidos != null) {
						
						if(filterVencidos.size() > 0)
							escenario2 = true;	
					}
					
					if(escenario1 || escenario2)
						iso.setISO_090_OriginalData("1");
					
					if(!escenario1 && !escenario2)
						iso.setISO_090_OriginalData("0");
					
				}else 
					iso.setISO_090_OriginalData("0");
			}else 
				iso.setISO_090_OriginalData("0");
			
			if(iso.getTickAut().isStarted())
				iso.getTickAut().stop();
			
		}catch (Exception e) {
			
			if(iso.getTickAut().isStarted())
				iso.getTickAut().stop();
			log.WriteLogMonitor("Error modulo IVRIsAcq::getDesafio6 ", 
					TypeMonitor.error, e);
		}finally {
			
			iso.setISO_039_ResponseCode("000");
			iso.setISO_039p_ResponseDetail("TRANSACCION EXITOSA");
			iso.setISO_114_ExtendedData(null);
			iso.setWsTempAut((iso.getTickAut().getTime(TimeUnit.MILLISECONDS)/1000.0));
		}
		
		return iso;
	}
    
   public wIso8583 getDesafio7(wIso8583 iso) {
		
		try {
			
			iso.setISO_041_CardAcceptorID("1800-292929");
			
				iso.getTickAut().reset();
				iso.getTickAut().start();
			
				if(iso.getISO_039_ResponseCode().equals("000")) {
					
					String anoNac = Arrays.asList(iso.getISO_115_ExtendedData()
							.split("\\|")).get(2).substring(0, 4);
					iso.setISO_090_OriginalData(anoNac);
					
				}else {
					
					iso.setISO_090_OriginalData("0000");
				}
				
				if(iso.getTickAut().isStarted())
					iso.getTickAut().stop();
			
		}catch (Exception e) {
			
			if(iso.getTickAut().isStarted())
				iso.getTickAut().stop();
			log.WriteLogMonitor("Error modulo IVRIsAcq::getDesafio7 ", 
					TypeMonitor.error, e);
		}finally {
			
			iso.setISO_039_ResponseCode("000");
			iso.setISO_039p_ResponseDetail("TRANSACCION EXITOSA");
			iso.setISO_114_ExtendedData(null);
			iso.setWsTempAut((iso.getTickAut().getTime(TimeUnit.MILLISECONDS)/1000.0));
		}
		
		return iso;
	}
    
   public wIso8583 getDesafio8(wIso8583 iso) {
		
		try {
			
			iso.setISO_041_CardAcceptorID("1800-292929");
			
				iso.getTickAut().reset();
				iso.getTickAut().start();
			
				if(iso.getISO_039_ResponseCode().equals("000")) {
					
					if(!iso.getISO_124_ExtendedData().equals("N/D")) {
						
						if(iso.getISO_124_ExtendedData().length() == 10) {
							
							String num4Cel = iso.getISO_124_ExtendedData().substring(6);
							iso.setISO_090_OriginalData(num4Cel);
							
						}else {
							
							iso.setISO_090_OriginalData("0000");
						}
						
					}else {
						
						iso.setISO_090_OriginalData("0000");
					}
					
				}else {
					
					iso.setISO_090_OriginalData("0000");
				}
				
				if(iso.getTickAut().isStarted())
					iso.getTickAut().stop();
			
		}catch (Exception e) {
			
			if(iso.getTickAut().isStarted())
				iso.getTickAut().stop();
			log.WriteLogMonitor("Error modulo IVRIsAcq::getDesafio7 ", 
					TypeMonitor.error, e);
		}finally {
			
			iso.setISO_039_ResponseCode("000");
			iso.setISO_039p_ResponseDetail("TRANSACCION EXITOSA");
			iso.setISO_114_ExtendedData(null);
			iso.setWsTempAut((iso.getTickAut().getTime(TimeUnit.MILLISECONDS)/1000.0));
		}
		
		return iso;
	}
    
   public wIso8583 getDesafio9(wIso8583 iso) {
		
		try {
			
			iso.setISO_041_CardAcceptorID("1800-292929");
			
				iso.getTickAut().reset();
				iso.getTickAut().start();
			
				if(iso.getISO_039_ResponseCode().equals("000")) {
					
					if(!iso.getISO_123_ExtendedData().equals("N/D")) {
						
						if(iso.getISO_123_ExtendedData().matches ("^.*\\d.*$")) {
							
							iso.setISO_090_OriginalData("1");
						}else {
							
							iso.setISO_090_OriginalData("0");
						}
						
					}else {
						
						iso.setISO_090_OriginalData("0");
					}
					
				}else {
					
					iso.setISO_090_OriginalData("0");
				}
				
				if(iso.getTickAut().isStarted())
					iso.getTickAut().stop();
			
		}catch (Exception e) {
			
			if(iso.getTickAut().isStarted())
				iso.getTickAut().stop();
			log.WriteLogMonitor("Error modulo IVRIsAcq::getDesafio7 ", 
					TypeMonitor.error, e);
		}finally {
			
			iso.setISO_039_ResponseCode("000");
			iso.setISO_039p_ResponseDetail("TRANSACCION EXITOSA");
			iso.setISO_114_ExtendedData(null);
			iso.setWsTempAut((iso.getTickAut().getTime(TimeUnit.MILLISECONDS)/1000.0));
		}
		
		return iso;
	}
    
   public wIso8583 getDesafio10(wIso8583 iso) {
		
		try {
			
			iso.setISO_041_CardAcceptorID("1800-292929");
			
				iso.getTickAut().reset();
				iso.getTickAut().start();
			
				if(iso.getISO_039_ResponseCode().equals("000")) {
					
					if(!StringUtils.IsNullOrEmpty(iso.getISO_121_ExtendedData())) {
						
						String [] valid = iso.getISO_121_ExtendedData().split("\\|");
						if(valid.length > 0) {
							
							iso.setISO_090_OriginalData("1");
							
						}else {
							
							iso.setISO_090_OriginalData("0");
						}
					}else {
						
						iso.setISO_090_OriginalData("0");
					}
					
				}else {
					
					iso.setISO_090_OriginalData("0");
				}
				
				if(iso.getTickAut().isStarted())
					iso.getTickAut().stop();
			
		}catch (Exception e) {
			
			if(iso.getTickAut().isStarted())
				iso.getTickAut().stop();
			log.WriteLogMonitor("Error modulo IVRIsAcq::getDesafio7 ", 
					TypeMonitor.error, e);
		}finally {
			
			iso.setISO_039_ResponseCode("000");
			iso.setISO_039p_ResponseDetail("TRANSACCION EXITOSA");
			iso.setISO_114_ExtendedData(null);
			iso.setWsTempAut((iso.getTickAut().getTime(TimeUnit.MILLISECONDS)/1000.0));
		}
		
		return iso;
	}
    
   public wIso8583 getProcessQuestionsIVR(wIso8583 iso) {
    	
    	Config conf = null;
    	Iso8583 iso8583 = null;
    	List<Iso8583> listIso = new ArrayList<>();
		EngineCallableProcessor<Iso8583> engine = null;
		iso.setISO_041_CardAcceptorID("1800-292929");
    	try {
			
    		conf = new Config();
    		conf = conf.getConfigSystem("IVR_QUESTIONS");
    		if(conf != null) {
    		
    			iso8583 = new Iso8583();
    			iso8583.setISO_000_Message_Type("1200");
    			iso8583.setISO_002_PAN(iso.getISO_002_PAN());
    			iso8583.setISO_018_MerchantType("0010");
    			iso8583.setISO_024_NetworkId("555551");
    			
	    		String [] preguntasArr = getPreguntasDesafioRandom(Integer.parseInt(iso.getISO_037_RetrievalReferenceNumber()), 
	    				Arrays.asList(conf.getCfg_Valor().split("\\|")).size());
	    		
	    		for (int i = 0; i < preguntasArr.length; i++) {
					
	    			final int pivot = i;
	    			
	    			String trx = Arrays.stream(conf.getCfg_Valor().split("\\|"))
					   .filter(x -> x.contains(preguntasArr[pivot] + "-"))
					   .findFirst().orElseGet(() -> null);
	    			
	    			if(!StringUtils.IsNullOrEmpty(trx)) {
	    				
	    				Iso8583 iso8583Clone = (Iso8583) iso8583.clone();
	    				iso8583Clone.setISO_003_ProcessingCode(Arrays.asList(trx.split("\\-")).get(1));
	    				iso8583Clone.setISO_011_SysAuditNumber(GeneralUtils.GetSecuencialNumeric(8));
	    				iso8583Clone.setISO_BitMap((preguntasArr[pivot]));
	    				listIso.add(iso8583Clone);
		    				
		    		}else {
		    			
		    			iso.setISO_039_ResponseCode("100");
		    			iso.setISO_039p_ResponseDetail("NO SE ENCONTRO LA PREGUNTA: " + preguntasArr[pivot]);
		    			break;
		    		}
				}//Fin For
	    		
	    		/*Procesamiento de Preguntas*/
	    		iso.getTickAut().reset();
				iso.getTickAut().start();
	    		engine = new EngineCallableProcessor<>(5);
	    		String IP = "127.0.0.1";
	    		for (Iso8583 is : listIso) {
	    		
	    			csProcess proc = new csProcess(is, IP);
					engine.add(proc);
	    		}
	    		
	    		List<Iso8583> listIsoResponses = engine.goProcess();
	    		if(iso.getTickAut().isStarted())
					iso.getTickAut().stop();
	    		
	    		
	    		
	    		for (Iso8583 iso8 : listIsoResponses) {
					
	    			iso.setISO_120_ExtendedData(iso.getISO_120_ExtendedData() +  iso8.getISO_BitMap() + "-" + iso8.getISO_090_OriginalData() + "|");
				}
	    		
	    		iso.setISO_120_ExtendedData(StringUtils.trimEnd(iso.getISO_120_ExtendedData(), "|"));
	    		iso.setISO_039_ResponseCode("000");
	    		iso.setISO_039p_ResponseDetail("TRANSACCION EXITOSA");
	    		
    		}else {
    			
    			iso.setISO_039_ResponseCode("908");
    			iso.setISO_039p_ResponseDetail("NO SE PUDO RECUPERAR INFORMACION IVR PREGUNTAS DESAFIO");
    		}
    		
		} catch (Exception e) {
			
			if(iso.getTickAut().isStarted())
				iso.getTickAut().stop();
			log.WriteLogMonitor("Error modulo IVRIsAcq::getProcessQuestionsIVR ", 
					TypeMonitor.error, e);
			iso.setISO_039_ResponseCode("909");
			iso.setISO_039p_ResponseDetail("ERROR EN PROCESOS " + GeneralUtils.ExceptionToString(null, e, false));
			
		}finally {
		
			iso.setWsTempAut((iso.getTickAut().getTime(TimeUnit.MILLISECONDS)/1000.0));
		}
    	
    	return iso;
    }
    
    public String[] getPreguntasDesafioRandom(int numeroPreguntas, int totPreguntas) {
    	
    	String [] preguntas = new String[numeroPreguntas];
    	try {
			
    		String numero = String.valueOf(numberAleatorioDesafio(totPreguntas));
    		if(numeroPreguntas == 1) {
    			preguntas[0] = numero.substring(0,1);
    			return preguntas;
    		}
    		
    		for (int i = 0; i < numero.length(); i++) {
				
    			String m = String.valueOf(numero.charAt(i));
    			if(m.equals("0")) {
    				String rangeFin = String.valueOf(totPreguntas);
    				preguntas[i] = rangeFin;
    			}else {
    				
    				preguntas[i] = m;
    			}
			}
    		
		} catch (Exception e) {
			
			log.WriteLogMonitor("Error modulo IVRIsAcq::getPreguntasDesafioRandom ", 
					TypeMonitor.error, e);
		}
    	
    	return preguntas;
    }
    
    protected int numberAleatorioDesafio(int totPreguntas) {
    	
    	int valor = 0;
		do {
			//valor = obtenerAleatorio(111, 999);
			valor = obtenerAleatorio(0, totPreguntas -1);
		} while (validaRepetidos(valor));
		
		return valor;
    }
    
    protected int obtenerAleatorio2(int desde, int hasta){
    		
	      return (int)( Math.random() * ( hasta - desde + 1 ) ) + desde;
	} 
    public static int obtenerAleatorio(int desde, int hasta){
		
		  String acum = "";
		  int num = 0;
		  for (int i = 0; i < 3; i++) {
			  num = (int) (Math.random() * ( hasta - desde + 1 ) ) + desde;
			  if(i == 0 && num == 0) {
				  num = 1;
			  }
			  acum+=String.valueOf(num);
		  }
		  return Integer.parseInt(acum);
	}  
    protected boolean validaRepetidos(long number) {
		
		 boolean res = false;
	     int[] cont = new int[10]; 
	     int digito;
	     while(number > 0) {
	         digito = (int) (number % 10);
	         cont[digito]++;
	         number /= 10;
	     }
	     for(digito=0;digito<10;digito++) {
	         if(cont[digito]>1) {
	            res = true;
	            break;
	         }
	     }
	     
	     return res;
	}
    
}
