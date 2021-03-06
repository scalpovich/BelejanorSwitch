package com.belejanor.switcher.memcached;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.mail.Transport;
import org.apache.commons.dbcp2.BasicDataSource;
import com.belejanor.switcher.extetrnalprocess.ControlPersistence;
import com.belejanor.switcher.extetrnalprocess.ScheduleProcessor;
import com.belejanor.switcher.logger.Logger;
import com.belejanor.switcher.logger.LoggerConfig;
import com.belejanor.switcher.logger.LoggerConfig.TypeMonitor;
import com.belejanor.switcher.snp.spi.SnpOrdLotes;
import com.belejanor.switcher.sqlservices.DBCPDataSource;
import com.belejanor.switcher.utils.GeneralUtils;

public final class MemoryGlobal{
	
	public static BasicDataSource dataSourceBDD;
	public static ThreadLocal<Semaphore> semaforoLotes = new ThreadLocal<>();
	public static CountDownLatch semaphoreIniLotesSpi = null;
	public static CountDownLatch semaphoreEndLotesSpi = null;
	public static String currentPath;
	public static boolean flagSystemReady = true;
	public static Connection conn;
	public static List<TransactionConfig> ListTrxConfigMem;
	public static List<ValidTransaction> ListValidTransactionMem;
	public static List<ActionCodeTable> ListActionCodeTableMem;
	public static List<BatchDay> ListBatchDayTableMem;
	public static List<SnpOrdLotes> ListLotes;
	public static List<Config> ListConfigSystemMem;
	public static List<ErrorToIso> ListErrorsToIso;
	public static List<TrxCf_GRQ> ListTrxCf_GRQMem;
	public static List<TrxCf_Table> ListTrxCf_TableMem;
	public static List<TrxCf_Reg_Ctl> ListTrxCf_RegCtlMem;
	public static List<TrxCf_Join_Cri> ListTrxCf_JoinCriMem;
	public static List<TrxCf_Cam> ListTrxCf_Cam;
	public static List<TrxCf_Dep_Table> ListTrxCf_DepTable;
	public static List<TrxCf_Dep> ListTrxCf_Dep;
	public static List<TrxCf_DepCam> ListTrxCf_DepCam;
	public static List<MessageControl> ListMsgControlMem;
	public static List<AdditionalIsoValues> ListAdditionalIsoValuesMem;
	public static List<MoneyIso4127> ListMoneyIsoMem;
	public static List<User_Channel> ListUserChannel;
	public static List<ChannelsFit1> ListChannelsFit1Mem;
	public static List<DispositivosFit1> ListDisppsitivoFit1Fit1Mem;
	public static List<TransactionCommands> listTransactionCommands;
	public static List<TransactionNotifications> listTransactionNotifications;
	public static List<MailSmsTypes> listMailSms;
	public static Map<String, String> MapFacilito = new HashMap<>();
	public static Map<Integer, String> MapTypeTrxCredencial = new HashMap<>();
 	public static ScheduledExecutorService serviceSchedule = null;
 	public static ScheduledExecutorService serviceScheduleSnp = null;
	public static String commonCodSursal;
	public static String commonCodOficina;
	public static String commonRol;
	public static String commonIdioma;
	public static String commonArea;
	public static String commonTPP;
	public static String commonCompania;
	public static String commonSesion;
	public static String commonTrxReverso;
	public static String commonFechaContable;
	public static String bddConnectionString;
	public static String bddUserName;
	public static String bddPassword;
	public static double valorMinCta;
	public static int    bddMaxPoolSize;
	public static int    bddMinPoolSize;
	public static int    bddInitialPoolSize;
	public static String sessionSys;
	public static String IpUci;
	public static int portUci;
	public static int timeOutUci;
	public static String messageReverseUCI;
	public static String messageReverseUCIFit1;
	public static String ownNetwork;
	public static String ownChannel;
	public static String proccodeMessageControl;
	public static boolean sendMsgControlFlag;
	public static int sendMsgControlInterval;
	public static String urlBce;
	public static String urlBceAdmin;
	public static String ipBCEAdmin;
	public static int portBCEAdmin;
	public static String userBce;
	public static String pwdBce;
	public static String idiomBce;
	public static String urlFacilito;
	public static String urlFacilitoConfirm;
	public static boolean flagPutCert;
	public static boolean flagPutCertII;
	public static boolean flagSecuenceInReverse;
	public static boolean flagBatch;
	public static List<String> abaIfi;
	public static int portTCPServer;
	public static int portTCPServerTxt;
	public static int maxThreadsTCPServer;
	
	public static String queueNameSf;
	public static MessageProducer producerQueueSf;
	public static ObjectMessage messageQueueSf;
	public static Session sessionQueueSf;
	
	public static MessageProducer producerQueueExternal;
	public static Session sessionQueueExternal;
	public static String queueNameCoonecta;
	
	public static String queueNameIni;
	public static MessageProducer producerQueueIni;
	public static ObjectMessage messageQueueIni;
	public static Session sessionQueueIni;
	
	
	public static String queueNameProcessor;
	public static MessageProducer producerQueueProcessor;
	public static ObjectMessage messageQueueProcessor;
	public static Session sessionQueueProcessor;
	
	
	public static String queueNameProcessorExtern;
	public static MessageProducer producerQueueProcessorExtern;
	public static ObjectMessage messageQueueProcessorExtern;
	public static Session sessionQueueProcessorExtern;
	
	
	public static Map<String,Object> concurrentIso;
	public static boolean flagUseQueueInit;
	public static String pwdCertI;
	public static String pwdCertII;
	public static String nameCertBceI;
	public static String nameCertBceII;
	public static String nameSSLTCPFile; 
	public static String passSSLTCPFile;
	public static int typeTCPServerService;
	public static boolean flagUseTCPSSL;
	public static String facilitoABA;
	public static String facilitoTOKEN;
	public static String facilitoTRACE_SW;
	public static String facilitoUSUARIO;
	public static String facilitoCLAVE;
	public static String facilitoCODIGO_AUTORIZACION;
	public static String facilitoNUMERO_CONTRATO;
	public static String facilitoSEGURIDAD;
	public static boolean flagCertficatefacilito;
	public static String facilitoNameCert;
	public static String facilitoPasswordCert;
	public static String endPointURLDepoRetVc;
	public static String endPointURLTransferVc;
	public static String userWsBCEVc;
	public static String ipAcqBCEVc;
	public static String ipSocketVc;
	public static int portSocketVc;
	public static String nameCertVc;
	public static String passwordCertVc;
	public static String BCE_Efi_VC;
	public static String BICFI_Bce;
	public static String AccountConciliationVc;
	
	public static String fit1ContextType;
	public static String fit1JnpUrl; 
	public static String fit1JnpContextFactory;
	public static String fit1RmiUrl;
	public static String fit1RmiContextFactory;
	public static String fit1RmiUsername;
	public static String fit1RmiPassword;
	public static    int fit1timeout;
	
	public static String fit1FlagConnection;
	public static String fit1IpConnectionRemote;
	public static int fit1PortConnectionRemote;
	
	public static String codCoonectaCoop;
	
	public static String IdBIMOEfi; 
	public static String IdBIMOBanred;
	public static String UrlBIMOAutorizador;
	
	public static String UrlSpiBCE;
	public static String UrlSpiUserBCE;
	public static String UrlSpiNameCertBCE;
	public static String UrlSpiPasswordBCE;
	public static String UrlSpiCodeEfi_BCE;
	public static String UrlSpiCodeSwitch_BCE;
	public static String UrlSpiAccountEfi_BCE;
	public static String UrlSpiIpAdressBCE;
	public static String UrlSpiIpSocketBCE;
	public static String UrlSpiPortSocketBCE;
	public static int UrlNumberThreadsExecutorSPI;
	
	public static String UrlSpiBCENotificaciones;
	public static String URLSpiTagFechaIni;
	public static String URLSpiTagFechaEnd;
	
	public static boolean SnpExecuteScheduleLotesFlag;
	public static int SnpScheduleLotesInterval;
	
	public static String ServipagosCodeEfi;
	public static double ServipagosMontoConsep = 0; 
	
	
	public static String UrlSciBCE;
	public static String UrlSciUserBCE;
	public static String UrlSciNameCertBCE;
	public static String UrlSciPasswordBCE;
	public static String UrlSciIpAdressBCE;
	public static String UrlSciIpSocketBCE;
	public static String UrlSciPortSocketBCE;
	public static int UrlNumberThreadsExecutorSCI;
	public static String UrlSciBCENotificaciones;
	public static String URLSciTagFechaIni;
	public static String URLSciTagFechaEnd;
	public static boolean SnpExecuteScheduleLotesFlagSCI;
	public static int SnpScheduleLotesIntervalSCI;
	
	public static boolean MailFlag;
	public static String MailDomain;
	public static String MailHost;
	public static String MailPort;
	public static String MailUser;
	public static String MailPassword;
	
	public static String SMSUrl;
	public static String SMSPath;
	public static String SMSParam;
	public static boolean SMSFlagZeroIni;
	
	public static javax.mail.Session sessionMail;
	public static Transport transportMail = null;
	
	public static String restAction;
	public static String restUserAuth;
	public static String restUserPassw;
	public static String restUserRest;
	public static String restUrlProsupply;
	public static String restMediaType;
	
	public static String BanredPagoDirectoAbaOrdenante;
	public static String BanredPagoDirectoAbaReceptor;
	public static String BanredPagoFIReceptor;
	public static String BanredPagoFIOrdenante;
	public static String nameCoop;
	
	@SuppressWarnings("static-access")
	public static boolean LoadParamsConfig(){
		LoggerConfig log = new LoggerConfig();
		try {
			
			
	        Properties p = new Properties();
	        
	        p.load(new FileInputStream(currentPath + "config.properties"));
	        commonCodSursal = p.getProperty("commonSucursal");
	        commonCodOficina = p.getProperty("commonCodOficina");
	        commonRol = p.getProperty("commonRol");
	        commonIdioma = p.getProperty("commonIdioma");
	        commonArea = p.getProperty("commonArea");
	        commonTPP = p.getProperty("commonTPP");
	        commonCompania = p.getProperty("commonCompania");        
	        commonSesion = p.getProperty("commonSesion");
	        commonTrxReverso = p.getProperty("commonTrxReverso");	        
	        commonFechaContable = p.getProperty("commonFechaContable");
	        valorMinCta = Double.parseDouble(p.getProperty("valorMinimoCta"));
	        bddConnectionString = p.getProperty("bddconnectionurl");
	        bddUserName = p.getProperty("bdduser");
	        bddPassword = p.getProperty("bddpassword");	 
	        sessionSys = GeneralUtils.GetSecuencial(32);
	        IpUci = p.getProperty("ipUci");	 
	        portUci =  Integer.parseInt(p.getProperty("portUci"));	
	        timeOutUci = Integer.parseInt(p.getProperty("timeOutUci"));
	        messageReverseUCI = p.getProperty("uciReverseTrx");
	        messageReverseUCIFit1 = p.getProperty("uciReverseTrxFit1");
	        bddMaxPoolSize = Integer.parseInt(p.getProperty("MaxPoolSize"));
	        bddMinPoolSize = Integer.parseInt(p.getProperty("MinPoolSize"));
	        bddInitialPoolSize = Integer.parseInt(p.getProperty("InitialPoolSize"));
	        ownNetwork = p.getProperty("ownNetwork");
	        ownChannel = p.getProperty("ownChannel");
	        proccodeMessageControl = p.getProperty("proccodeMsgControl");
	        sendMsgControlFlag = Boolean.parseBoolean(p.getProperty("sendMsgControlFlag"));
	        sendMsgControlInterval = Integer.parseInt(p.getProperty("sendMsgControlInterval"));
	        urlBce = p.getProperty("urlServiceBce");
	        userBce = p.getProperty("userNameBce");
	        pwdBce = p.getProperty("pwdBce");
	        idiomBce = p.getProperty("idiomBce");
	        urlFacilito = p.getProperty("serviceFacilito");
	        urlFacilitoConfirm = p.getProperty("serviceFacilitoConfirm");
	        flagPutCert = Boolean.parseBoolean(p.getProperty("flagPutCertificate"));
	        flagPutCertII = Boolean.parseBoolean(p.getProperty("flagPutCertificateII"));
	        queueNameSf = p.getProperty("queueStoreForward");
	        queueNameIni = p.getProperty("queueNameIni");
	        queueNameProcessor = p.getProperty("queueNameProcessor");
	        queueNameCoonecta = p.getProperty("queueCoonecta");
	        concurrentIso =  new ConcurrentHashMap<>();
	        flagUseQueueInit = Boolean.parseBoolean(p.getProperty("flagUSeQueueInit"));
	        flagSecuenceInReverse = Boolean.parseBoolean(p.getProperty("flagRandomSecuenceRever"));
	        portTCPServer = Integer.parseInt(p.getProperty("TCPServerPort"));
	        portTCPServerTxt = Integer.parseInt(p.getProperty("TCPServerTextPort"));
	        maxThreadsTCPServer = Integer.parseInt(p.getProperty("TCPServerNroThreads"));
	        pwdCertI = p.getProperty("pwdCertI");
	        pwdCertII = p.getProperty("pwdCertII");
	        urlBceAdmin = p.getProperty("urlServiceBceAdmin");
	        ipBCEAdmin = p.getProperty("ipBCEServiceAdmin");
	        portBCEAdmin = Integer.parseInt(p.getProperty("portBCEServiceAdmin"));
	        nameCertBceI = p.getProperty("nameCertBceI");
	        nameCertBceII = p.getProperty("nameCertBceII");
	        nameSSLTCPFile = p.getProperty("SSLTCPFile");
	        passSSLTCPFile = p.getProperty("SSLFilePassword");
	        typeTCPServerService = Integer.parseInt(p.getProperty("TypeTCPServer"));	
	        flagUseTCPSSL = Boolean.parseBoolean(p.getProperty("TCPServerUseSSL"));
	        facilitoABA = p.getProperty("facilitoABA");
	        facilitoTOKEN = p.getProperty("facilitoTOKEN");
	        facilitoTRACE_SW = p.getProperty("facilitoTRACE_SW");
	        facilitoUSUARIO = p.getProperty("facilitoUSUARIO");
	        facilitoCLAVE = p.getProperty("facilitoCLAVE");
	        facilitoCODIGO_AUTORIZACION = p.getProperty("facilitoCODIGO_AUTORIZACION");
	        facilitoNUMERO_CONTRATO = p.getProperty("facilitoNUMERO_CONTRATO");
	        facilitoSEGURIDAD = p.getProperty("facilitoSEGURIDAD");
	        flagCertficatefacilito = Boolean.parseBoolean(p.getProperty("flagCertficatefacilito"));
	        flagBatch = Boolean.parseBoolean(p.getProperty("snp.scheduler.batch.flag"));
	        facilitoPasswordCert = p.getProperty("facilitoPasswordCert");
	        facilitoNameCert =  p.getProperty("facilitoNameCert");
	        endPointURLDepoRetVc = p.getProperty("endPointURLDepoRetVc");
	        endPointURLTransferVc = p.getProperty("endPointURLTransferVc");
	        userWsBCEVc = p.getProperty("userWsBCEVc");
	        ipAcqBCEVc = p.getProperty("ipAcqBCEVc");
	        ipSocketVc = p.getProperty("ipSocket");
	        portSocketVc = Integer.parseInt(p.getProperty("portSocketVc"));
	        nameCertVc = p.getProperty("nameCertVc");
	        passwordCertVc = p.getProperty("passwordCertVc");
	        BCE_Efi_VC =  p.getProperty("BCE_VC_Efi_Id");
	        BICFI_Bce = p.getProperty("BICFI_Bce");
	        AccountConciliationVc = p.getProperty("AccountConciliationVc");
	        abaIfi =  Pattern.compile(",")
	        		  .splitAsStream(p.getProperty("abaIfi"))
	        		  .collect(Collectors.toList());
	        
	        fit1ContextType = p.getProperty("fit1.context.type");
	        fit1JnpUrl = p.getProperty("fit1.jnp.url"); 
	        fit1JnpContextFactory = p.getProperty("fit1.jnp.contextFactory");
	        fit1RmiUrl = p.getProperty("fit1.rmi.url");
	        fit1RmiContextFactory = p.getProperty("fit1.rmi.contextFactory");
	        fit1RmiUsername = p.getProperty("fit1.rmi.username");
	        fit1RmiPassword = p.getProperty("fit1.rmi.password");
	        fit1timeout = Integer.parseInt(p.getProperty("fit1.timeout"));

	        
	       fit1FlagConnection = p.getProperty("fit1.connection");
	       fit1IpConnectionRemote = p.getProperty("fit1.ip.connectionRemote");
	       fit1PortConnectionRemote =  Integer.parseInt(p.getProperty("fit1.port.connectionRemote"));
	       
	       codCoonectaCoop = p.getProperty("codCoonectaCoop");
	       
	       IdBIMOEfi = p.getProperty("Id.BIMO.Efi"); 
	   	   IdBIMOBanred = p.getProperty("Id.BIMO.Banred"); 
	   	   UrlBIMOAutorizador = p.getProperty("Url.BIMO.Autorizador");
	   	   nameCoop = p.getProperty("Id.BIMO.Name"); 
	   	   
		   UrlSpiBCE = p.getProperty("snp.url.spi.bce");
		   UrlSpiUserBCE = p.getProperty("snp.user.spi.bce");
		   UrlSpiNameCertBCE = p.getProperty("snp.nameCert.bce");
		   UrlSpiPasswordBCE = p.getProperty("snp.password.bce");
		   UrlSpiCodeEfi_BCE = p.getProperty("snp.code.efi.bce");
		   UrlSpiCodeSwitch_BCE = p.getProperty("snp.code.switch.bce");
		   UrlSpiAccountEfi_BCE = p.getProperty("snp.account.efi.bce");
		   UrlSpiIpAdressBCE = p.getProperty("snp.ipAddres.bce");
		   UrlSpiIpSocketBCE = p.getProperty("snp.ipSocket.bce");
		   UrlSpiPortSocketBCE = p.getProperty("snp.port.socket.bce");
		   UrlNumberThreadsExecutorSPI = Integer.parseInt(p.getProperty("snp.number.threads.spi.receptor"));
		   
		   UrlSpiBCENotificaciones = p.getProperty("snp.url.spi.notificaciones");
		   URLSpiTagFechaIni =   p.getProperty("snp.url.spi.not.tagFechaIni");
		   URLSpiTagFechaEnd =  p.getProperty("snp.url.spi.not.tagFechaEnd");
		   
		   SnpExecuteScheduleLotesFlag = Boolean.parseBoolean(p.getProperty("snp.scheduler.lotes.flag"));
		   SnpScheduleLotesInterval = Integer.parseInt(p.getProperty("snp.scheduler.lotes.timer.interval"));
		   
		   ServipagosCodeEfi = p.getProperty("servipag.code.efi");
		   ServipagosMontoConsep = Double.parseDouble(p.getProperty("servipag.monto.consep"));
		   
		   
		   UrlSciBCE = p.getProperty("snp.url.sci.bce");
		   UrlSciUserBCE = p.getProperty("snp.user.sci.bce");
		   UrlSciNameCertBCE = p.getProperty("snp.nameCert.sci.bce");
		   UrlSciPasswordBCE = p.getProperty("snp.password.sci.bce");
		   UrlSciIpAdressBCE = p.getProperty("snp.ipAddres.sci.bce");
		   UrlSciIpSocketBCE = p.getProperty("snp.ipSocket.sci.bce");
		   UrlSciPortSocketBCE = p.getProperty("snp.port.socket.sci.bce");
		   UrlNumberThreadsExecutorSCI = Integer.parseInt(p.getProperty("snp.number.threads.sci.receptor"));
		   UrlSciBCENotificaciones = p.getProperty("snp.url.sci.notificaciones");
		   URLSciTagFechaIni = p.getProperty("snp.url.spi.not.tagFechaIni.sci");
		   URLSciTagFechaEnd = p.getProperty("snp.url.spi.not.tagFechaEnd.sci");
		   SnpExecuteScheduleLotesFlagSCI = Boolean.parseBoolean(p.getProperty("snp.scheduler.lotes.flag.sci"));
		   SnpScheduleLotesIntervalSCI = Integer.parseInt(p.getProperty("snp.scheduler.lotes.timer.interval.sci"));
		   
		   MailFlag = Boolean.parseBoolean(p.getProperty("mail.29.flag"));
		   MailDomain = p.getProperty("mail.domain");
		   MailHost = p.getProperty("mail.host");
		   MailPort = p.getProperty("mail.port");
		   MailUser = p.getProperty("mail.user");
		   MailPassword = p.getProperty("mail.password");
		   
		   
		   SMSUrl = p.getProperty("sms.url");
		   SMSPath = p.getProperty("sms.path");
		   SMSParam = p.getProperty("sms.param");
		   SMSFlagZeroIni = Boolean.parseBoolean(p.getProperty("sms.flag.zero.ini"));
		   
		   restAction = p.getProperty("rest.action");
		   restUserAuth = p.getProperty("rest.userAuth");
		   restUserPassw = p.getProperty("rest.passAuth");
		   restUserRest = p.getProperty("rest.userRest");
		   restUrlProsupply = p.getProperty("rest.URLProsupply");
		   restMediaType = p.getProperty("rest.mediaType");
		   
		   BanredPagoDirectoAbaOrdenante = p.getProperty("pg.banred.aba.ordenante");
		   BanredPagoDirectoAbaReceptor = p.getProperty("pg.banred.aba.receptor");
		   BanredPagoFIOrdenante  = p.getProperty("pg.banred.fi.ordenante");
		   BanredPagoFIReceptor = p.getProperty("pg.banred.fi.receptor");
		   
	        getTypeTrxCredencial();
	        getErrorsFacilito();
	        if(MailFlag)
	        	chargeMail29();
	        
	        return true;	
	        
		} catch (IOException ex){
			log.WriteMonitor("ERROR Modulo MemoryGlobal::LoadParamsConfig()", TypeMonitor.error, ex);
			return false;
		} catch (Exception e) {			
			log.WriteMonitor("ERROR General Modulo MemoryGlobal::LoadParamsConfig()", TypeMonitor.error, e);
			return false;
		}
	}
	
	private static void getErrorsFacilito(){
		
		MapFacilito.put("89", "Error producido por los datos en los parametros al codificar un mensaje ISO ".toUpperCase());
		MapFacilito.put("91", "El autorizador de la transacci?n no tiene una conexi?n activa con el sistema".toUpperCase());
		MapFacilito.put("92", "Tiempo de espera agotado (Router)".toUpperCase());
		MapFacilito.put("93", "El modulo Web concluy? la transaccion por tiempo de espera excedido".toUpperCase());
		MapFacilito.put("94", "Un m?dulo Base del sistema no est? disponible (Routers) ".toUpperCase());
		MapFacilito.put("95", "Existe un problema con la cola del sistema (No existe/No tiene permisos) ".toUpperCase());
		MapFacilito.put("96", "Surgi? un problema al esperar por una respuesta a una transaccion por su identificador".toUpperCase());
		MapFacilito.put("97", "Los datos de autentificaci?n no est?n correctos".toUpperCase());
		MapFacilito.put("98", "Los datos utilizados en el ruteo de la transacci?n no est?n correctos".toUpperCase());
		MapFacilito.put("22", "Servicio no posee Reverso".toUpperCase());
		MapFacilito.put("87", "Referencia ya ha sido cancelada".toUpperCase());
		MapFacilito.put("089", "Error producido por los datos en los parametros al codificar un mensaje ISO ".toUpperCase());
		MapFacilito.put("091", "El autorizador de la transacci?n no tiene una conexi?n activa con el sistema".toUpperCase());
		MapFacilito.put("092", "Tiempo de espera agotado (Router)".toUpperCase());
		MapFacilito.put("093", "El modulo Web concluy? la transaccion por tiempo de espera excedido".toUpperCase());
		MapFacilito.put("094", "Un m?dulo Base del sistema no est? disponible (Routers) ".toUpperCase());
		MapFacilito.put("095", "Existe un problema con la cola del sistema (No existe/No tiene permisos) ".toUpperCase());
		MapFacilito.put("096", "Surgi? un problema al esperar por una respuesta a una transaccion por su identificador".toUpperCase());
		MapFacilito.put("097", "Los datos de autentificaci?n no est?n correctos".toUpperCase());
		MapFacilito.put("098", "Los datos utilizados en el ruteo de la transacci?n no est?n correctos".toUpperCase());
		MapFacilito.put("022", "Servicio no posee Reverso".toUpperCase());
		MapFacilito.put("087", "Referencia ya ha sido cancelada".toUpperCase());
	}
	
	@SuppressWarnings("static-access")
	private static void chargeMail29() {
		
		try {
			
			Properties properties = new Properties();
	        properties.setProperty("mail.smtp.host", MailHost); 
	        properties.setProperty("mail.smtp.starttls.enable", "true");
	        
	        properties.setProperty("mail.smtp.port", MailPort);
	        properties.setProperty("mail.smtp.user", MailUser);
	        properties.setProperty("mail.smtp.auth", "true");
	                                
	        sessionMail = javax.mail.Session.getInstance(properties);
	        transportMail = sessionMail.getTransport("smtp");
	        if(!transportMail.isConnected())
	        	transportMail.connect(MailUser, MailPassword);
			
		} catch (Exception e) {
			
			LoggerConfig log = new LoggerConfig();
			log.WriteMonitor("ERROR Modulo chargeMail29()" + e.getMessage() , TypeMonitor.error, e);
		}
			
	}
	
    private static void getTypeTrxCredencial(){
		
		MapTypeTrxCredencial.put(1, "Autorizaci?n Compra".toUpperCase());
		MapTypeTrxCredencial.put(3, "Autorizaci?n Anulaci?n".toUpperCase());
		MapTypeTrxCredencial.put(11, "Autorizaci?n Devoluci?n".toUpperCase());
		MapTypeTrxCredencial.put(35, "Autorizaci?n Cr?dito/Carga Gen?rico".toUpperCase());
		MapTypeTrxCredencial.put(17, "Reversa autorizaci?n Compra".toUpperCase());
		MapTypeTrxCredencial.put(19, "Reversa autorizaci?n Anulaci?n".toUpperCase());
		MapTypeTrxCredencial.put(27, "Reversa autorizaci?n Devoluci?n".toUpperCase());
		MapTypeTrxCredencial.put(51, "Reverso de Autorizaci?n Cr?dito - Carga Gen?rico".toUpperCase());
		MapTypeTrxCredencial.put(49, "Reverso de Autorizaci?n D?bito - Descarga Gen?rico".toUpperCase());
		MapTypeTrxCredencial.put(65, "Transferencias entre Cuentas (D?bito)".toUpperCase());
		MapTypeTrxCredencial.put(81, "Reversa Transferencias entre Cuentas (D?bito)".toUpperCase());
		MapTypeTrxCredencial.put(67, "Transferencias entre Cuentas (Cr?dito)".toUpperCase());
		MapTypeTrxCredencial.put(83, "Reversa Transferencias entre Cuentas (Cr?dito)".toUpperCase());
		MapTypeTrxCredencial.put(129, "Consulta Saldos - Disponibles y Movimientos por Tarjeta".toUpperCase());
		MapTypeTrxCredencial.put(513, "Asignaci?n de PIN Inicial".toUpperCase());
		MapTypeTrxCredencial.put(257, "Cambio de PIN".toUpperCase());
		MapTypeTrxCredencial.put(529, "Rerversa Asignaci?n PIN inicial".toUpperCase());
		MapTypeTrxCredencial.put(273, "Reversa cambio de PIN".toUpperCase());
		MapTypeTrxCredencial.put(1027, "Pago de Resumen de Cuenta".toUpperCase());
		MapTypeTrxCredencial.put(1025, "Pago de Servicios con Tarjeta".toUpperCase());
		MapTypeTrxCredencial.put(1043, "Reversa de Pago de Resumen de Cuenta".toUpperCase());
		MapTypeTrxCredencial.put(1041, "Reversa de Pago de Servicios con Tarjeta".toUpperCase());
		
	}
	
    private static Logger logg;
	@SuppressWarnings("static-access")
	public static boolean OpenConnBDD(){	
		LoggerConfig log = new LoggerConfig();
		try {			
			   
			   DBCPDataSource dbc = new DBCPDataSource();
			   MemoryGlobal.dataSourceBDD = dbc.getDataSource(MemoryGlobal.dataSourceBDD);
			   conn = dbc.getConnection();
			   
			   return true;
			   
		}catch (SQLException e) {
			
			log.WriteMonitor("ERROR Modulo SQLSERVER::OpenConnBDD()" + e.getErrorCode() , TypeMonitor.error, e);
			logg.WriteLogMonitor("ERROR Modulo SQLSERVER::OpenConnBDD() " + e.getErrorCode() + "-------------->>>" + GeneralUtils.ExceptionToString(null, e, true), TypeMonitor.monitor, null);
			return false;
		} catch (Exception e) {
			log.WriteMonitor("ERROR Modulo MemoryGlobal::OpenConnBDD()", TypeMonitor.error, e);
			return false;			
		}
	}
	@SuppressWarnings("static-access")
	public static boolean LoadMemory(){
		
		LoggerConfig log = new LoggerConfig();
		try {
			
			/*AdditionalIsoValues addIso = new AdditionalIsoValues();
			addIso.start();
			
			ValidTransaction vTrx = new ValidTransaction();
			vTrx.start();

			TransactionConfig trxConfig = new TransactionConfig();
			trxConfig.start();
			
			ActionCodeTable actionCodeErrors = new ActionCodeTable();
			actionCodeErrors.start();
			
			Config configSys = new Config();
			configSys.start();
			
			TrxCf_GRQ grqProcess = new TrxCf_GRQ();
			Thread objTrxCf_grq = new Thread(grqProcess.getDataGrq());
			objTrxCf_grq.start();
			
			TrxCf_Table tableProcess = new TrxCf_Table();
			Thread objTrxCf_table = new Thread(tableProcess.getDataTable());
			objTrxCf_table.start();
			
			TrxCf_Dep_Table tableCamProcess =  new TrxCf_Dep_Table();
			Thread objTtxCfCamTable = new Thread(tableCamProcess.getDataTableCam());
			objTtxCfCamTable.start();
			
			TrxCf_Reg_Ctl regCtlProcess = new TrxCf_Reg_Ctl();
			Thread objTrxCf_regCtl = new Thread(regCtlProcess.getDataRegCtl());
			objTrxCf_regCtl.start();
			
			TrxCf_Join_Cri regJoinCriProcess = new TrxCf_Join_Cri();
			Thread objTrxJoinCri = new Thread(regJoinCriProcess.getDataJoinCri());
			objTrxJoinCri.start();
			
			TrxCf_Cam regCamProcess = new TrxCf_Cam();
			Thread objTrxCfCam = new Thread(regCamProcess.getDataCam());
			objTrxCfCam.start();
			
			TrxCf_Dep regDepProcess = new TrxCf_Dep();
			Thread objTrxCfDep = new Thread(regDepProcess.getDataDep());
			objTrxCfDep.start();
			
			MessageControl MsgCtrlProcess = new MessageControl();
			Thread objMsgCtrl = new Thread(MsgCtrlProcess.getDataMsgCtrl());
			objMsgCtrl.start();
			
			TrxCf_DepCam regDepCamProcess = new TrxCf_DepCam();
			Thread objTrxCfDepCam = new Thread(regDepCamProcess.getDataDepCam());
			objTrxCfDepCam.start();
			
			TransactionCommands regTransactionCommands = new TransactionCommands();
			Thread objTrxCommands = new Thread(regTransactionCommands.getDataTransactionCommands());
			objTrxCommands.start();
			
			ErrorToIso errorsToIso = new ErrorToIso();
			errorsToIso.start();
			
			User_Channel usrChannel = new User_Channel();
			usrChannel.start();
			
			MoneyIso4127 regMoney = new MoneyIso4127();
			regMoney.start();
			
			
			TransactionNotifications trxNotifs = new TransactionNotifications();
			Thread objrxNotifs = new Thread(trxNotifs.getDataTransactionNotifications());
			objrxNotifs.start();
			
			MailSmsTypes trxMailSms = new MailSmsTypes();
			Thread objtrxMailSms = new Thread(trxMailSms.getDataMailSmsTypes());
			objtrxMailSms.start();
			
			//Ojo para desactivar MemoryGlobal.fit1FlagConnection debe estar en "N"
			//Thread trchannFit1 = null;
			//Thread trdispoFit1 = null;
			if(MemoryGlobal.fit1FlagConnection.equalsIgnoreCase("L") ||
					MemoryGlobal.fit1FlagConnection.equalsIgnoreCase("R")){ 
				
				log.WriteMonitor("Cargando Configuraciones para FinanSwitch_COBIS Version 1...", TypeMonitor.monitor, null);
				//ChannelsFit1 channFit1 = new ChannelsFit1();
				//DispositivosFit1 dispoFit1 = new DispositivosFit1();
				//trchannFit1 = new Thread(channFit1.getDataChannelsFit1());
				//trdispoFit1 = new Thread(dispoFit1.getDataDispisitivosFit1());
				//trchannFit1.start();
				//trdispoFit1.start();
				
			}
			
			addIso.join();
			vTrx.join();
			trxConfig.join();
			actionCodeErrors.join();
			configSys.join();
			objTrxCf_grq.join();
			objTrxCf_table.join();
			objTtxCfCamTable.join();
			objTrxCf_regCtl.join();
			objTrxJoinCri.join();
			objTrxCfCam.join();
			objTrxCfDep.join();
			objMsgCtrl.join();
			objTrxCommands.join();
			objTrxCfDepCam.join();
			errorsToIso.join();
			usrChannel.join();
			objrxNotifs.join();
			objtrxMailSms.join();*/
			
			AdditionalIsoValues addIso = new AdditionalIsoValues();
			addIso.start();
			addIso.join();
			
			ValidTransaction vTrx = new ValidTransaction();
			vTrx.start();
			vTrx.join();

			TransactionConfig trxConfig = new TransactionConfig();
			trxConfig.start();
			trxConfig.join();
			
			ActionCodeTable actionCodeErrors = new ActionCodeTable();
			actionCodeErrors.start();
			actionCodeErrors.join();
			
			
			Config configSys = new Config();
			configSys.start();
			configSys.join();
			
			TrxCf_GRQ grqProcess = new TrxCf_GRQ();
			Thread objTrxCf_grq = new Thread(grqProcess.getDataGrq());
			objTrxCf_grq.start();
			objTrxCf_grq.join();
			
			TrxCf_Table tableProcess = new TrxCf_Table();
			Thread objTrxCf_table = new Thread(tableProcess.getDataTable());
			objTrxCf_table.start();
			objTrxCf_table.join();
			
			TrxCf_Dep_Table tableCamProcess =  new TrxCf_Dep_Table();
			Thread objTtxCfCamTable = new Thread(tableCamProcess.getDataTableCam());
			objTtxCfCamTable.start();
			objTtxCfCamTable.join();
			
			TrxCf_Reg_Ctl regCtlProcess = new TrxCf_Reg_Ctl();
			Thread objTrxCf_regCtl = new Thread(regCtlProcess.getDataRegCtl());
			objTrxCf_regCtl.start();
			objTrxCf_regCtl.join();
			
			TrxCf_Join_Cri regJoinCriProcess = new TrxCf_Join_Cri();
			Thread objTrxJoinCri = new Thread(regJoinCriProcess.getDataJoinCri());
			objTrxJoinCri.start();
			objTrxJoinCri.join();
			
			TrxCf_Cam regCamProcess = new TrxCf_Cam();
			Thread objTrxCfCam = new Thread(regCamProcess.getDataCam());
			objTrxCfCam.start();
			objTrxCfCam.join();
			
			TrxCf_Dep regDepProcess = new TrxCf_Dep();
			Thread objTrxCfDep = new Thread(regDepProcess.getDataDep());
			objTrxCfDep.start();
			objTrxCfDep.join();
			
			MessageControl MsgCtrlProcess = new MessageControl();
			Thread objMsgCtrl = new Thread(MsgCtrlProcess.getDataMsgCtrl());
			objMsgCtrl.start();
			objMsgCtrl.join();
			
			TrxCf_DepCam regDepCamProcess = new TrxCf_DepCam();
			Thread objTrxCfDepCam = new Thread(regDepCamProcess.getDataDepCam());
			objTrxCfDepCam.start();
			objTrxCfDepCam.join();
			
			TransactionCommands regTransactionCommands = new TransactionCommands();
			Thread objTrxCommands = new Thread(regTransactionCommands.getDataTransactionCommands());
			objTrxCommands.start();
			objTrxCommands.join();
			
			ErrorToIso errorsToIso = new ErrorToIso();
			errorsToIso.start();
			errorsToIso.join();
			
			User_Channel usrChannel = new User_Channel();
			usrChannel.start();
			usrChannel.join();
			
			MoneyIso4127 regMoney = new MoneyIso4127();
			regMoney.start();
			regMoney.join();
			
			TransactionNotifications trxNotifs = new TransactionNotifications();
			Thread objrxNotifs = new Thread(trxNotifs.getDataTransactionNotifications());
			objrxNotifs.start();
			objrxNotifs.join();
			
			MailSmsTypes trxMailSms = new MailSmsTypes();
			Thread objtrxMailSms = new Thread(trxMailSms.getDataMailSmsTypes());
			objtrxMailSms.start();
			objtrxMailSms.join();
			
			BatchDay trxBatch = new BatchDay();
			trxBatch.start();
			trxBatch.join();
			
			//Ojo para desactivar MemoryGlobal.fit1FlagConnection debe estar en "N"
			//Thread trchannFit1 = null;
			//Thread trdispoFit1 = null;
			if(MemoryGlobal.fit1FlagConnection.equalsIgnoreCase("L") ||
					MemoryGlobal.fit1FlagConnection.equalsIgnoreCase("R")){ 
				
				log.WriteMonitor("Cargando Configuraciones para FinanSwitch_COBIS Version 1...", TypeMonitor.monitor, null);
				//ChannelsFit1 channFit1 = new ChannelsFit1();
				//DispositivosFit1 dispoFit1 = new DispositivosFit1();
				//trchannFit1 = new Thread(channFit1.getDataChannelsFit1());
				//trdispoFit1 = new Thread(dispoFit1.getDataDispisitivosFit1());
				//trchannFit1.start();
				//trdispoFit1.start(); [MEM_CAHED]
				
			}
			
			if(MemoryGlobal.fit1FlagConnection.equalsIgnoreCase("L") ||
					MemoryGlobal.fit1FlagConnection.equalsIgnoreCase("R")){ 
				//trchannFit1.join();
				//trdispoFit1.join();
				
			}
			//regMoney.join();
			
			
			return true;
			
		} catch (Exception e) {
			log.WriteMonitor("ERROR Modulo MemoryGlobal::LoadMemory() ", TypeMonitor.error, e);
			return true;
		}
	}
    public static void InitMessageControl(){
		
		ControlPersistence ctrlPersistense = new ControlPersistence();
		ScheduleProcessor schedule = new ScheduleProcessor();
		
		Thread t = new Thread(new Runnable() {
			
			@Override
			public void run() {
				
				schedule.ExecuteProcessPersistence(serviceSchedule, 
							ctrlPersistense.executeMessageControl(), 
							TimeUnit.SECONDS, MemoryGlobal.sendMsgControlInterval);
			}
		});
		t.start();
	}
	
}
