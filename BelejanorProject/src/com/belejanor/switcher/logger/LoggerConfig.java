package com.belejanor.switcher.logger;

import java.io.File;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.tempuri.ConfirmaTransaccionStub;
import org.tempuri.ConfirmaTransaccionStub.ConfirmacionTransaccionResponse;
import org.tempuri.ConfirmaTransaccionStub.DatosConfirmacion;
import org.tempuri.Service1Stub.Reply_Structure_SBD;
import org.tempuri.Service1Stub.Request_Structure_SBD;

import com.belejanor.switcher.cscoreswitch.Iso8583;
import com.belejanor.switcher.cscoreswitch.wIso8583;
import com.belejanor.switcher.memcached.MemoryGlobal;
import com.belejanor.switcher.utils.GeneralUtils;
import com.belejanor.switcher.utils.SerializationObject;
import com.belejanor.switcher.utils.StringUtils;
import com.fitbank.dto.management.Detail;

import urn.iso.std.iso20022.tech.xsd.pacs_002_001_05.DocumentRespuesta;
import urn.iso.std.iso20022.tech.xsd.pacs_003_001_04.DocumentRetiro;
import urn.iso.std.iso20022.tech.xsd.pacs_004_001_04.DocumentReverso;
import urn.iso.std.iso20022.tech.xsd.pacs_008_001_04.DocumentDeposito;
import urn.iso.std.iso20022.tech.xsd.pacs_008_001_04.DocumentTransferencia;
import urn.iso.std.iso20022.tech.xsd.pacs_008_001_04.DocumentTransferenciaSPI;
import us.inswitch.mts.webservices.bce.MTSServiceEFIStub.DTORequestAccountAssociation;
import us.inswitch.mts.webservices.bce.MTSServiceEFIStub.DTORequestAccountAssociationConfirm;
import us.inswitch.mts.webservices.bce.MTSServiceEFIStub.DTORequestAccountDissociation;
import us.inswitch.mts.webservices.bce.MTSServiceEFIStub.DTOResponseAccountAssociation;
import us.inswitch.mts.webservices.bce.MTSServiceEFIStub.DTOResponseAccountAssociationConfirm;
import us.inswitch.mts.webservices.bce.MTSServiceEFIStub.DTOResponseAccountDissociation;


public class LoggerConfig extends Thread {

	public enum TypeMonitor{monitor, error};
	public enum TypeLog { debug, report, monitor, bceaut, bceacq, facilito, alexsoft, isoBin, brdAut, brdAcq, srvPgAcq, equifxAcq };
	public enum TypeWriteLog { consoleAndFile, console, file };
	static final Logger debugLog = Logger.getLogger("debugLogger");
	static final Logger resultLog = Logger.getLogger("reportsLogger");
	static final Logger rootLog = Logger.getLogger(LoggerConfig.class);
	static final Logger consoleLog = Logger.getLogger("consoleLogger");
	static final Logger monitorLog = Logger.getLogger("monitorsLogger");
	static final Logger errorLog = Logger.getLogger("errorsLogger");
	static final Logger bceAutLog = Logger.getLogger("bceLogger");
	static final Logger bceAcqLog = Logger.getLogger("bceAcqLogger");
	static final Logger facilAutLog = Logger.getLogger("facilitoAutLogger");
	static final Logger alexAcqLog = Logger.getLogger("alexAcqLogger");
	static final Logger isoBinLog = Logger.getLogger("iso8583BinLogger");
	static final Logger banredAutLog = Logger.getLogger("banredAutLogger");
	static final Logger banredAcqLog = Logger.getLogger("banredAcqLogger");
	static final Logger servipagosAcqLog = Logger.getLogger("servipagosAcqLogger");
	static final Logger equifaxAcqLog = Logger.getLogger("equifaxAcqLogger");
	
	
	private Object mensaje;	
	private TypeLog typeLog;
	private TypeWriteLog typeWriteLog;
	private Class<?> clazz;
	private boolean isSerializable;
	private int idaVuelta = 0;
	
	public LoggerConfig(){
		
	}
	public LoggerConfig(final Object mensaje, TypeLog typeLog, TypeWriteLog typeWriteLog ){
		this.mensaje = mensaje;
		this.typeLog = typeLog;
		this.typeWriteLog = typeWriteLog;
	}
	public LoggerConfig (final Object mensaje, Class<?> classe, TypeLog typeLog, boolean isSerializable){
		this.mensaje = mensaje;
		this.typeLog = typeLog;
		this.clazz = classe;
		this.isSerializable = isSerializable;
	}
	public LoggerConfig (final Object mensaje, Class<?> classe, TypeLog typeLog, boolean isSerializable, int idaVuelta){
		this.mensaje = mensaje;
		this.typeLog = typeLog;
		this.clazz = classe;
		this.isSerializable = isSerializable;
		this.idaVuelta = idaVuelta;
	}
	
	public void InitLoggerService(ServletConfig config){
		try {
			System.out.println("Inicia Configuracion de Log4j!!!!!!");
			System.out.println("Clase GlobalServletInit esta incializando log4j...");
			String log4jLocation = config.getInitParameter("log4j-properties-location");			
			ServletContext sc = config.getServletContext();
			if(sc != null){
				
				String splitter = "\\/";
		        String [] pather = MemoryGlobal.currentPath.split(splitter);
		        String _path = MemoryGlobal.currentPath.replace(pather[pather.length -1] + "/", "");
				String webAppPath = _path;//"D:/MiddlewareFitBankJava/slnMiddlewareFitBank/WebContent/";				
				String log4jProp = webAppPath + log4jLocation;
				File yoMamaYesThisSaysYoMama = new File(log4jProp);
				if (yoMamaYesThisSaysYoMama.exists()) {
					System.out.println("Se ha inicializado Log4j en el siguiente Path: " + log4jProp);
					PropertyConfigurator.configure(log4jProp);
					System.out.println("Proceso inicializacion Log4j exitoso...");	
				} else {
					System.err.println("*** " + log4jProp + " Archivo de propiedades no encontrado!!!");					
				}
			}						
			System.out.println("Termina configuracion Log4j.....");						
		} catch (Exception e) {			
			System.out.println("Error al inicial componente log4j: " + e.getMessage());			
		}
	}
	public static void writeTest3(final Object msgs, final TypeLog tl, final TypeWriteLog tw){
		Object msg = null;
		if(!(msgs instanceof Detail))
			msg = SerializationObject.ObjectToString(msgs);
		else 
			msg = msgs;
		
		switch (tw) {
		case consoleAndFile:
			if(tl == TypeLog.report)
				resultLog.info(msg);				
			else{
				Detail det = (Detail)msg;
				try {
					debugLog.info(det.toXml());
				} catch (Exception e) {}
			}
			if(msg instanceof Detail){
				Detail det = (Detail)msg;
				try {
					consoleLog.info(det.toXml());
				} catch (Exception e) {}
			}else{
				consoleLog.info(msg);
			}
			break;
		case console:
			consoleLog.info(msg);
			break;
		case file:
			if(tl == TypeLog.report)
				resultLog.info(msg);				
			else{
				Detail det = (Detail)msg;
				try {
					debugLog.info(det.toXml());
				} catch (Exception e){}
			}
			break;			
		default:
			break;
		}		
	}
	public static void writeTest(final Object msgs, final TypeLog tl, final TypeWriteLog tw){
		Object msg = null;
		
			if(!(msgs instanceof Detail)){
				if(msgs!= null){
					if(msgs instanceof DTORequestAccountAssociation || msgs instanceof DTOResponseAccountAssociation 
					   || msgs instanceof DTORequestAccountAssociationConfirm || msgs instanceof DTOResponseAccountAssociationConfirm
					   || msgs instanceof DTORequestAccountDissociation || msgs instanceof DTOResponseAccountDissociation
					   )				
						msg = SerializationObject.ObjectToXML((Serializable) msgs);
					else if (msgs instanceof String) {
						msg = msgs;
					}else if (msgs.getClass().equals(Reply_Structure_SBD.class) || msgs.getClass().equals(Request_Structure_SBD.class) 
							|| msgs.getClass().equals(ConfirmaTransaccionStub.class) || msgs.getClass().equals(ConfirmacionTransaccionResponse.class) ||
							msgs.getClass().equals(ConfirmacionTransaccionResponse.class) || msgs.getClass().equals(DatosConfirmacion.class)
							|| msgs.getClass().equals(ConfirmaTransaccionStub.ConfirmacionTransaccionResponse.class) ||
							msgs.getClass().equals(ConfirmaTransaccionStub.ConfirmacionTransaccion.class) || 
							msgs.getClass().equals(ConfirmaTransaccionStub.Reply_SW.class) ){
						msg = SerializationObject.ObjectToXML((Serializable) msgs);//SerializationObject.ObjectToStringII(msgs);
					}else if(msgs instanceof DocumentRespuesta || msgs instanceof DocumentDeposito || msgs instanceof DocumentReverso
							   || msgs instanceof DocumentRetiro || msgs instanceof DocumentTransferencia || msgs instanceof DocumentTransferenciaSPI){
						msg = SerializationObject.ObjectToString(msgs);
					}else{
						if(msgs.getClass().getName().equalsIgnoreCase("org.tempuri.ConfirmaTransaccionStub$Reply_SW"))
							SerializationObject.ObjectToStringII(msgs);
						else 
							msg = SerializationObject.ObjectToString(msgs);
					}
				}else {
					msg = "***** No se ha recibido respuesta ****";
				}
			}
			else 
				msg = msgs;
		
		switch (tw) {
		case consoleAndFile:
			
			if(tl == TypeLog.report){
				resultLog.info(msg);
				consoleLog.info(msg);
			}else if (tl == TypeLog.debug) {
				Detail det = (Detail)msg;
				try {
					debugLog.info(det.toXml());
					consoleLog.info(det.toXml());
				} catch (Exception e) {}
			}else if (tl == TypeLog.bceaut) {
				bceAutLog.info(msg);
				consoleLog.info(msg);
			}else if (tl == TypeLog.bceacq) {
				bceAcqLog.info(msg);
				consoleLog.info(msg);
			}else if (tl == TypeLog.facilito) {
				facilAutLog.info(msg);
				consoleLog.info(msg);
			}
			break;
		case console:
			consoleLog.info(msg);
			break;
		case file:
			if(tl == TypeLog.report){
				resultLog.info(msg);
			}else if (tl == TypeLog.debug) {
				if(msg instanceof String){
					String msgD = (String)msg;
					debugLog.info(msgD);
				}else{
					Detail det = (Detail)msg;
					try {
						debugLog.info(det.toXml());
					} catch (Exception e) {}
				}
					
			}else if (tl == TypeLog.bceaut) {
				bceAutLog.info(msg);
			}else if (tl == TypeLog.bceacq) {
				bceAcqLog.info(msg);
			}else if (tl == TypeLog.facilito) {
				facilAutLog.info(msg);
			}else if (tl == TypeLog.alexsoft) {
				alexAcqLog.info(msg);
			}else if (tl == TypeLog.isoBin) {
				isoBinLog.info(msg);
			}
			break;			
		default:
			break;
		}		
	}
	public static void WriteMonitor(wIso8583 iso, Iso8583 iso8583){
		
		try {
			
			System.out.println("Va una");
			String tipoMsg, red, canal, terminal, trx, ip, param;
			tipoMsg = red = canal = terminal = trx = ip = param = null;
			int sep = 12;
			StringBuilder monitorData = new StringBuilder();
			if(iso != null)
				monitorData.append("\n\n******************** INICIA TRANSACCION (" + iso.getWsISO_TranDatetime() + ") ******************************\n" + "\n");
			else
				monitorData.append("\n\n******************** INICIA TRANSACCION (" + new Date() + ") ******************************n" + "\n");
				
			if(iso8583.getISO_000_Message_Type().startsWith("12")){
				tipoMsg = "TRANSACCION NORMAL (1200)";
			}
			else if (iso8583.getISO_000_Message_Type().startsWith("14")) {
				tipoMsg = "**** REVERSO TRANSACCION (1400) ****";
			}else if (iso8583.getISO_000_Message_Type().startsWith("18")) {
				tipoMsg = "**** MENSAJE CONTROL (1800) ****";
			}else{
				tipoMsg = iso8583.getISO_000_Message_Type();
			}
			
			if(iso.getWsTransactionConfig() != null){
				
				red = iso.getWsTransactionConfig().getNet_Descripcion();
				canal = iso8583.getISO_018_MerchantType() + " - " + iso.getWsTransactionConfig().getCanal_Des();
				terminal = iso.getISO_041_CardAcceptorID();
				trx = iso.getWsTransactionConfig().getProccodeDescription();
			}
			else{
				
				red = iso8583.getISO_024_NetworkId();
				canal = iso8583.getISO_018_MerchantType();
				terminal = iso8583.getISO_041_CardAcceptorID() == null ? "N/D" : iso8583.getISO_041_CardAcceptorID();
				trx = iso8583.getISO_003_ProcessingCode();
			}
			ip = iso.getWsTransactionConfig() ==  null ? iso8583.getISO_124_ExtendedData(): iso.getWsTransactionConfig().getIp();
			param = iso8583.getISO_102_AccountID_1() == null || iso8583.getISO_102_AccountID_1().isEmpty() ? iso8583.getISO_002_PAN():
																						     iso8583.getISO_102_AccountID_1();
			monitorData.append(StringUtils.padRight("RED:", sep, " ") + red + "\n");
			monitorData.append(StringUtils.padRight("CANAL:", sep, " ") + canal + "\n");
			monitorData.append(StringUtils.padRight("TERMINAL:", sep, " ") + terminal + "\n");
			monitorData.append(StringUtils.padRight("TIPO:", sep, " ") + tipoMsg + "\n");
			monitorData.append(StringUtils.padRight("TRX:", sep, " ") + trx + "\n");
			monitorData.append(StringUtils.padRight("SECUENCIAL:", sep, " ") + iso8583.getISO_011_SysAuditNumber() + "\n");
			monitorData.append(StringUtils.padRight("PARAMETRO:", sep, " ") + param + "\n");
			monitorData.append(StringUtils.padRight("VALOR:", sep, " ") + "$" + iso8583.getISO_004_AmountTransaction() + "\n");
			monitorData.append(StringUtils.padRight("RESPUESTA:", sep, " ") + iso8583.getISO_039_ResponseCode() + " - " + 
							  iso8583.getISO_039p_ResponseDetail() + "\n");
			monitorData.append(StringUtils.padRight("IP ORIGEN:", sep, " ") + ip + "\n");
			monitorData.append(StringUtils.padRight("SESION_SYS:", sep, " ") + MemoryGlobal.sessionSys + "\n");
			
			Date date = null;
			SimpleDateFormat formatter = null;
			if(iso != null){
				
				
				date = new Date((long)(iso.getWsTempTrx() * 1000));
		        formatter = new SimpleDateFormat("mm:ss.SSS");
		        String formatterMid = formatter.format(date);
		        
		        date = new Date((long)(iso.getWsTempBDD() * 1000));
		        formatter = new SimpleDateFormat("mm:ss.SSS");
		        String formatterBdd = formatter.format(date);
		        
		        date = new Date((long)(iso.getWsTempAut() * 1000));
		        formatter = new SimpleDateFormat("mm:ss.SSS");
		        String formatterAut = formatter.format(date);
		        
		        
		        monitorData.append(StringUtils.padRight("TIME:", sep, " ") + "Trx: " + formatterMid + "  Aut: " + formatterAut 
		        		+ "  Bdd: " + formatterBdd + "  LogStatus: " + iso.getWsIso_LogStatus());
		        
			}
			monitorData.append("\n\n**********   **********   **********   **********   **********   **********   **********   **********\n\n");
			monitorLog.info(monitorData.toString());
			

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void WriteMonitorStoreAndForward(wIso8583 iso){
		
		
		StringBuilder monitorData = new StringBuilder();
		try {
			
			String tipoMsg, param;
			tipoMsg = param = null;
			int sep = 12;
			if(iso != null)
				monitorData.append("\n\n==================== STORE AND FORWARD (" + iso.getWsISO_TranDatetime() + ") ========================\n" + "\n");
			else
				monitorData.append("\n\n==================== STORE AND FORWARD (" + new Date() + ") ========================n" + "\n");
			
			if(iso.getISO_000_Message_Type().startsWith("12")){
				tipoMsg = "TRANSACCION NORMAL (1200)";
			}
			else if (iso.getISO_000_Message_Type().startsWith("14")) {
				tipoMsg = "**** REVERSO TRANSACCION (1400) ****";
			}else if (iso.getISO_000_Message_Type().startsWith("18")) {
				tipoMsg = "**** MENSAJE CONTROL (1800) ****";
			}else{
				tipoMsg = iso.getISO_000_Message_Type();
			}
			
			    param = iso.getISO_102_AccountID_1() == null || iso.getISO_102_AccountID_1().isEmpty() ? iso.getISO_002_PAN():
			     iso.getISO_102_AccountID_1();
			    
				monitorData.append(StringUtils.padRight("RED:", sep, " ") + iso.getWsTransactionConfig().getNet_Descripcion() + "\n");
				monitorData.append(StringUtils.padRight("CANAL:", sep, " ") + iso.getWsTransactionConfig().getCanal_Des() + "\n");
				monitorData.append(StringUtils.padRight("TERMINAL:", sep, " ") + iso.getISO_041_CardAcceptorID() + "\n");
				monitorData.append(StringUtils.padRight("TIPO:", sep, " ") + tipoMsg + "\n");
				monitorData.append(StringUtils.padRight("TRX:", sep, " ") + iso.getWsTransactionConfig().getProccodeDescription() + "\n");
				monitorData.append(StringUtils.padRight("SECUENCIAL:", sep, " ") + iso.getISO_011_SysAuditNumber() + "\n");
				monitorData.append(StringUtils.padRight("PARAMETRO:", sep, " ") + param + "\n");
				monitorData.append(StringUtils.padRight("VALOR:", sep, " ") + "$" + iso.getISO_004_AmountTransaction() + "\n");
				monitorData.append(StringUtils.padRight("RESPUESTA:", sep, " ") + iso.getISO_039_ResponseCode() + " - " + 
				iso.getISO_039p_ResponseDetail() + "\n");
				monitorData.append(StringUtils.padRight("REINTENTO:", sep, " ") + iso.getWsIsoSF_CountVisualizer() + "\n");
				monitorData.append(StringUtils.padRight("SESION_SYS:", sep, " ") + MemoryGlobal.sessionSys + "\n");
				
				Date date = null;
				SimpleDateFormat formatter = null;
				if(iso != null){
					
					date = new Date(iso.getTickMidd() == null ? 0L :iso.getTickMidd().getTime(TimeUnit.MILLISECONDS));
			        formatter = new SimpleDateFormat("mm:ss.SSS");
			        String formatterMid = formatter.format(date);
			        
			        date = new Date(iso.getTickBdd() == null ? 0L : iso.getTickBdd().getTime(TimeUnit.MILLISECONDS));
			        formatter = new SimpleDateFormat("mm:ss.SSS");
			        String formatterBdd = formatter.format(date);
			        
			        date = new Date(iso.getTickAut() == null ? 0L : iso.getTickAut().getTime(TimeUnit.MILLISECONDS));
			        formatter = new SimpleDateFormat("mm:ss.SSS");
			        String formatterAut = formatter.format(date);
			        
			        monitorData.append(StringUtils.padRight("TIME:", sep, " ") + "Trx: " + formatterMid + "  Aut: " + formatterAut 
			        		+ "  Bdd: " + formatterBdd + "  LogStatus: " + iso.getWsIso_LogStatus());
			        
				}
				monitorData.append("\n\n==========   ==========   ==========   ==========   ==========   ==========   ==========   ==========\n\n");
				monitorLog.info(monitorData.toString());
			

		} catch (Exception e) {
			
			e.printStackTrace();
		}
	}
	
	public static void WriteMonitor(String msg, TypeMonitor type, Exception ex){
		
		if(type == TypeMonitor.monitor)
			monitorLog.info(msg);
		else{
			if(ex != null)
				errorLog.error(GeneralUtils.ExceptionToString(msg, ex, false));
			else
				errorLog.error(msg);
		}
	}
	
	public static void WriteLogExtern(final Object msg, TypeLog tl, TypeWriteLog tw) throws InterruptedException{
		//Thread.sleep(10000);
		ExecutorService es = Executors.newFixedThreadPool(3);       
		@SuppressWarnings("unused")
		Future<?> future = es.submit(new Callable<Object>() {
        			@Override
                    public Object call() throws Exception {        				
        				LoggerConfig.writeTest(msg, tl, tw);
                        return null;
                    }
                }); 
	}
	@Override
	public void run() {
		try {			
			writeTest(this.mensaje, this.typeLog, this.typeWriteLog);
		} catch (Exception e) {			
			e.printStackTrace();
		}
	}
	public Runnable WriteLogRunnable(final Object msg, final TypeLog tl, final TypeWriteLog tw){
		Runnable runnable = new Runnable() {			
			@Override
			public void run() {				
				try {
					writeTest(msg, tl, tw);
				} catch (Exception e) {					
					e.printStackTrace();
				}
			}
		};
		return runnable;
	}
	public Runnable WriteMonitorRun(wIso8583 iso, Iso8583 iso8583){
		
		Runnable runnable = new Runnable() {			
			@Override
			public void run() {				
				try {
					WriteMonitor(iso, iso8583);
				} catch (Exception e) {					
					e.printStackTrace();
				}
			}
		};
		return runnable;
	}
	public Runnable WriteMonitorRun(String msg, TypeMonitor type, Exception ex){
		
		Runnable runnable = new Runnable() {			
			@Override
			public void run() {				
				try {
					WriteMonitor(msg, type, ex);
				} catch (Exception e) {					
					e.printStackTrace();
				}
			}
		};
		return runnable;
	}
	public Runnable WriteMonitorStoreAndForwardAsycn(wIso8583 iso){
		
		Runnable r = new Runnable() {
			
			@Override
			public void run() {
				
				WriteMonitorStoreAndForward(iso);
			}
		};
		
		return r;
	}
	public Runnable WriteOptimizeLogging(){
		
		
		Runnable r = new Runnable() {
			
			@Override
			public void run() {
				
			 String xml = null;
			 if(isSerializable)
				 xml = SerializationObject.ObjectToString(mensaje, clazz);
			 else
				 xml = (String) mensaje;
		
				switch (typeLog) {
				case bceacq:
					bceAcqLog.info(xml);
					break;
				case bceaut:
					bceAutLog.info(xml);
					break;
				case debug:
					debugLog.info(xml);
					break;
				case brdAcq:
					banredAcqLog.info(xml);
					break;
				case brdAut:
					banredAutLog.info(xml);
					break;
				case report:
					if(idaVuelta == 0)
					  resultLog.info(xml);
					else
				      resultLog.info(xml
				    		  .replace(">1200<", ">1210<")
				    		  .replace(">1400<", ">1410<")
				    		  .replace(">1800<", ">1810<"));
					break; //porsia borrar
				case srvPgAcq:
					servipagosAcqLog.info(xml);
					break;
				case equifxAcq:
					equifaxAcqLog.info(xml);
					break;
				case alexsoft:
					alexAcqLog.info(xml);
					break;
				default:
					break;
				}
			}
		};
		return r;
	}
}
