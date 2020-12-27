package com.belejanor.switcher.control;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import com.belejanor.switcher.cscoreswitch.TransactionConfiguration;
import com.belejanor.switcher.cscoreswitch.wIso8583;
import com.belejanor.switcher.logger.Logger;
import com.belejanor.switcher.logger.LoggerConfig.TypeMonitor;
import com.belejanor.switcher.memcached.MemoryGlobal;
import com.belejanor.switcher.memcached.MessageControl;
import com.belejanor.switcher.memcached.TransactionConfig;
import com.belejanor.switcher.memcached.ValidTransaction;
import com.belejanor.switcher.utils.GeneralUtils;

public class ControlMessage implements Callable<wIso8583>{

	private boolean flagError;
	private String desError;
	private Logger log;
	private wIso8583 isoClass;
	
	public ControlMessage() {
		this.flagError = true;
		this.desError = "";
		log = new Logger();
	}
	public ControlMessage(wIso8583 iso){
		this();
		this.isoClass = iso;
	}
	
	public wIso8583 DynamicControlMessage(wIso8583 iso){
		try {
			
			@SuppressWarnings("unused")
			ControlMessage ctrl = new ControlMessage(iso);
			List<wIso8583> isoRes = new ArrayList<>();
			List<wIso8583> isoList = getIsoControl();
			
			if(this.flagError){
		
				ExecutorService pool = Executors.newFixedThreadPool(isoList.size());
				Set<Future<wIso8583>> setter = new HashSet<Future<wIso8583>>();
				
				for (wIso8583 wiso : isoList) {
					
					Class<?> instanceClass = Class.forName(wiso.getISO_115_ExtendedData());
					Object isoInstance = instanceClass.getConstructor(new Class[] {wIso8583.class})
							             .newInstance(new Object[] { wiso });
					@SuppressWarnings("unchecked")
					Callable<wIso8583> callable = (Callable<wIso8583>) isoInstance;
					Future<wIso8583> future = pool.submit(callable);
					setter.add(future);
				}
				pool.shutdown();
				iso.getTickAut().start();
				for (Future<wIso8583> futureResponse : setter) {
					isoRes.add((wIso8583)futureResponse.get());
				}
				iso.getTickAut().stop();
				wIso8583 ii = null;
				
					ii =    isoRes.stream().
							 filter(p -> p.getISO_039_ResponseCode() != "000")
						     .findFirst().orElseGet(() -> null);
					
					if(ii == null){
					    
				    	iso.setISO_039_ResponseCode("000");
						iso.setISO_039p_ResponseDetail("TRANSACCION EXITOSA");
						iso.setWsIso_LogStatus(2);
				    }
				    else {
				    	
				    	iso.setISO_039_ResponseCode(ii.getISO_039_ResponseCode());
						iso.setISO_039p_ResponseDetail(ii.getISO_039p_ResponseDetail());
						iso.setWsIso_LogStatus(ii.getWsIso_LogStatus());
					}
				}
			
			else{
				iso.setISO_039_ResponseCode("909");
				iso.setISO_039p_ResponseDetail(this.desError);
			}
				
		} catch (Exception e) {
			if(iso.getTickAut().isStarted())
				iso.getTickAut().stop();
			iso.setISO_039_ResponseCode("909");
			iso.setISO_039p_ResponseDetail(GeneralUtils.ExceptionToString("ERROR EN PROCESOS: ", e, false));
			log.WriteLogMonitor("ERROR Modulo ControlMessage::DynamicControlMessage", TypeMonitor.error, e);
		}finally {
			iso.setWsTempAut((iso.getTickAut().getTime(TimeUnit.MILLISECONDS)/1000.0));
		}
		return iso;
	}

	public List<wIso8583> getIsoControl(){
		
		List<wIso8583> listIso = new ArrayList<>();
		List<ValidTransaction> lValidTrx = null;
		try {
			
			int rows = MemoryGlobal.ListMsgControlMem.size();
			wIso8583 iso = null;
			if(MemoryGlobal.ListMsgControlMem != null &&  rows > 0){
				
				for (MessageControl m : MemoryGlobal.ListMsgControlMem) {
					
					if(m.getType_control().equalsIgnoreCase("T")){
						
						ValidTransaction validTrx = new ValidTransaction();
						lValidTrx = validTrx.getValidTrxfigListObject(m.getProccode(), Integer.parseInt(m.getNet_id())
									                                  ,m.getCanal_cod(), m.getAmmountdebit());
						
						if(lValidTrx != null && lValidTrx.size() > 0){
							
							iso = new wIso8583();
							for (ValidTransaction vtrx : lValidTrx) {
								
								String nameIso = vtrx.getV_campoiso();
								String valueBdd = vtrx.getV_control();
								
								Field IsoRow = Arrays.stream(iso.getClass().getSuperclass().getDeclaredFields())
											  .filter(p -> p.getName().contains(nameIso))
											  .findFirst().get();
								IsoRow.setAccessible(true);
								Object obj = getValueIsoRow(IsoRow, iso, valueBdd);
								IsoRow.set(iso, obj);
								
							}
							/** Add Transaction Configuration*/
							TransactionConfiguration trxConf = new TransactionConfiguration
										(new TransactionConfig(iso.getISO_003_ProcessingCode(), 
										 Integer.parseInt(iso.getISO_024_NetworkId()), iso.getISO_018_MerchantType(),
										 -1));
							iso.setWsTransactionConfig(trxConf);
							iso.setISO_115_ExtendedData(m.getMessage_class());
							iso.setISO_114_ExtendedData(m.getDetail());
							iso.setISO_122_ExtendedData(String.valueOf(m.getActive_control()));
							if(m.getActive_control() == 0)
								iso.setISO_115_ExtendedData("com.fitbank.middleware.control.ControlMessage");
							if(iso != null)
							  listIso.add(iso);
						}
						
					}else {
						
						iso = new wIso8583();
						iso.setISO_000_Message_Type("1200");
						iso.setISO_003_ProcessingCode(m.getProccode());
						iso.setISO_012_LocalDatetime(new Date());
						iso.setISO_007_TransDatetime(new Date());
						iso.setISO_015_SettlementDatel(new Date());
						iso.setISO_018_MerchantType(m.getCanal_cod());
						iso.setISO_024_NetworkId(m.getCanal_cod());
						iso.setISO_114_ExtendedData(m.getDetail());
						iso.setISO_115_ExtendedData(m.getMessage_class());
						iso.setISO_122_ExtendedData(String.valueOf(m.getActive_control()));	
						if(m.getActive_control() == 0)
							iso.setISO_115_ExtendedData("com.fitbank.middleware.control.ControlMessage");
						listIso.add(iso);
					}
				}
			}
			else{
				this.desError = "NO EXISTE REDES A MONITOREAR ";
				this.flagError = false;
			}
			
		} catch (Exception e) {
			this.flagError = false;
			this.desError = GeneralUtils.ExceptionToString("Error: Iso Refection ", e, false);
			log.WriteLogMonitor("ERROR Modulo ControlMessage::getValueIsoRow", TypeMonitor.error, e);
		}
		return listIso;
	}
	@SuppressWarnings("deprecation")
	private Object getValueIsoRow(Field fieldIso, Object objIso, String vbdd){
		Object valor = new Object();
		try {
			String campo = null;
			switch (fieldIso.getType().toString()) {
			case "class java.lang.String":
				campo = vbdd;
				if (campo.equalsIgnoreCase("Randomize"))
                    valor = GeneralUtils.GetSecuencial(6);
                else
                    valor = campo;
				break;
			case "class java.lang.Double":
				campo = vbdd;
				if(campo.equalsIgnoreCase("Randomize"))
					valor = Double.parseDouble(GeneralUtils.GetSecuencial(6));
				else
					valor = Double.parseDouble(campo);
				break;
			case "class java.util.Date":
				campo = vbdd;
				if(campo.equalsIgnoreCase("Datetime.Now"))
					valor = new Date();
				else
					valor = Date.parse(campo);
				break;
			case "int":
				campo = (String)fieldIso.get(objIso);
				if(campo.equalsIgnoreCase("Randomize"))
					valor = Integer.parseInt(GeneralUtils.GetSecuencial(6));
				else
					valor = Integer.parseInt(campo);
				break;
			default:
				break;
			}
			
		} catch (Exception e) {
			valor = "NULL";
			log.WriteLogMonitor("ERROR Modulo ControlMessage::getValueIsoRow", TypeMonitor.error, e);
		}
		return valor;
	}
	public wIso8583 TranOK(wIso8583 iso)
    {
        iso.setISO_039_ResponseCode("000");
        return iso;
    }


	@Override
	public wIso8583 call() throws Exception {
		return TranOK(this.isoClass);
	}
	
	
}
