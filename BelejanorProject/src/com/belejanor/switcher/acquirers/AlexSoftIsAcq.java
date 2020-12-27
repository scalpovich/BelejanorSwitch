package com.belejanor.switcher.acquirers;

import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.belejanor.switcher.asextreme.Clients;
import com.belejanor.switcher.asextreme.ConsProducts;
import com.belejanor.switcher.asextreme.Cuotas;
import com.belejanor.switcher.asextreme.DField;
import com.belejanor.switcher.asextreme.Data;
import com.belejanor.switcher.asextreme.ExtremeReply;
import com.belejanor.switcher.asextreme.ExtremeRequest;
import com.belejanor.switcher.asextreme.FxTables;
import com.belejanor.switcher.asextreme.HField;
import com.belejanor.switcher.asextreme.Header;
import com.belejanor.switcher.asextreme.LastMovements;
import com.belejanor.switcher.asextreme.MassiveMovements;
import com.belejanor.switcher.asextreme.Payments;
import com.belejanor.switcher.asextreme.Products;
import com.belejanor.switcher.asextreme.Registro;
import com.belejanor.switcher.cscoreswitch.EngineCallableProcessor;
import com.belejanor.switcher.cscoreswitch.Iso8583;
import com.belejanor.switcher.cscoreswitch.csProcess;
import com.belejanor.switcher.cscoreswitch.wIso8583;
import com.belejanor.switcher.logger.Logger;
import com.belejanor.switcher.logger.LoggerConfig;
import com.belejanor.switcher.logger.LoggerConfig.TypeLog;
import com.belejanor.switcher.logger.LoggerConfig.TypeMonitor;
import com.belejanor.switcher.logger.LoggerConfig.TypeWriteLog;
import com.belejanor.switcher.memcached.Config;
import com.belejanor.switcher.parser.AlexParser;
import com.belejanor.switcher.sqlservices.IsoRetrievalTransaction;
import com.belejanor.switcher.utils.FormatUtils;
import com.belejanor.switcher.utils.GeneralUtils;
import com.belejanor.switcher.utils.Iterables;
import com.belejanor.switcher.utils.Ref;
import com.belejanor.switcher.utils.SerializationObject;
import com.belejanor.switcher.utils.StringUtils;

public class AlexSoftIsAcq {
	
	 private Logger log;
	 private String IP;
	 
	 public AlexSoftIsAcq(){
		 log = new Logger();
		 this.IP = "127.0.0.1";
	 }
	 public AlexSoftIsAcq(String Ip){
		 
		 this();
		 this.IP = Ip;
	 }
	
	@SuppressWarnings("static-access")
	public wIso8583 RetrieveStatusTransactionsTransfer(wIso8583 iso) {
		
		IsoRetrievalTransaction sql = null;
		SerializationObject serial = null;
		AlexParser parser = null;
		ExtremeRequest request = null;
		DecimalFormatSymbols simbolo = new DecimalFormatSymbols();
	    simbolo.setDecimalSeparator('.');
		DecimalFormat df = new DecimalFormat("0.00", simbolo);
		try {
			
			sql = new IsoRetrievalTransaction();
			Ref<wIso8583> refIso = new Ref<wIso8583>(iso);
			iso.getTickAut().reset();
			iso.getTickAut().start();
				List<Iterables> it = sql.RetrieveResultTransfersMasivasFinancoop(refIso);
			if(iso.getTickAut().isStarted())
				iso.getTickAut().stop();
				
			double acumValores = 0;
			if(refIso.get().getISO_039_ResponseCode().equals("000")) {
				
				if(it != null) {
					if(it.size() > 0) {
						
						request = (ExtremeRequest) serial.StringToObject(iso.getISO_114_ExtendedData(),ExtremeRequest.class);
						ExtremeReply reply = new ExtremeReply(request);
						reply.getHeader().setValueTag("RetCode", "0");
						reply.setData(null);
						
						Data data_ = new Data();
						List<DField> fields = new ArrayList<>();
						fields.add(new DField("Amount", "***", null));
						fields.add(new DField("BusinessMessage", "Retorno estado de Transacciones", null));
						fields.add(new DField("BackendCode", "0", null));
						fields.add(new DField("BackendReference", GeneralUtils.GetSecuencialNumeric(6), null));
						fields.add(new DField("ExecutionDate", FormatUtils.DateToString(new Date(), "yyyyMMdd HH:mm:ss.S"), null));
						fields.add(new DField("ExecutedSuccesfully", "True", null));
						fields.add(new DField("ValueDate", FormatUtils.DateToString(new Date(), "yyyyMMdd HH:mm:ss.S"), null));
						fields.add(new DField("ExchangeRateTransaction", "0", null));
						fields.add(new DField("ExchangeRateUSD", "0", null));
						data_.setDfield(fields);
						
						reply.setData(data_);
						
						FxTables tables = new FxTables();
						MassiveMovements movemments = new MassiveMovements();
						List<Registro> reg = new ArrayList<>();;
						for (Iterables ite : it) {
						
							acumValores += Double.parseDouble(ite.getIterarors().get("AMOUNT"));
							reg.add(new Registro(ite.getIterarors().get("TRANSACTIONITEMID"), ite.getIterarors().get("SUBTRANSACTIONTYPEID")
									,ite.getIterarors().get("TRANSACTIONFEATUREID"), ite.getIterarors().get("CURRENCYID"), 
									ite.getIterarors().get("VALUEDATE"), ite.getIterarors().get("TRANSACTIONTYPEID"), 
									ite.getIterarors().get("TRANSACTIONSTATUSID"), ite.getIterarors().get("CLIENTBANKID"), 
									ite.getIterarors().get("DEBITPRODUCTBANKID"), ite.getIterarors().get("DEBITPRODUCTTYPEID"),
									ite.getIterarors().get("DEBITCURRENCYID"), ite.getIterarors().get("CREDITPRODUCTBANKID"), 
									ite.getIterarors().get("CREDITPRODUCTTYPEID"), ite.getIterarors().get("CREDITCURRENCYID"), 
									ite.getIterarors().get("AMOUNT"), ite.getIterarors().get("NOTIFYTO"), 
									ite.getIterarors().get("NOTIFICATIONCHANNELID"), ite.getIterarors().get("DESTDOCUMENTNUMBER"), 
									ite.getIterarors().get("DESTINATIONNAME"), ite.getIterarors().get("DESTINATIONBANK"),
									ite.getIterarors().get("DESCRIPTION"), ite.getIterarors().get("BANKROUTINGNUMBER"), 
									ite.getIterarors().get("SOURCENAME"), ite.getIterarors().get("SOURCEBANK"), 
									ite.getIterarors().get("SOURCEDOCUMENTID"), ite.getIterarors().get("REGULATIONAMOUNTEXCEEDED"), 
									ite.getIterarors().get("SOURCEFUNDS"), ite.getIterarors().get("DESTINATIONFUNDS"), 
									ite.getIterarors().get("USERDOCUMENTID"), ite.getIterarors().get("TRANSACTIONCOST"), 
									ite.getIterarors().get("TRANSACTIONCOSTCURRENCYID"), ite.getIterarors().get("EXCHANGERATE"),
									ite.getIterarors().get("ISVALID"), ite.getIterarors().get("VALIDATIONMESSAGE")));
							
							movemments.setRegistro(reg);
								
						}
						
						
						tables.setTransferencias(movemments);
						reply.setTables(tables);
						
						serial = new SerializationObject();
						iso.setISO_115_ExtendedData(serial.ObjectToString(reply, ExtremeReply.class));
						iso.setISO_115_ExtendedData(iso.getISO_115_ExtendedData().replace("***", df.format(acumValores)));
						
					}else {
						
						if(request == null) 
							request = (ExtremeRequest) serial.StringToObject(iso.getISO_114_ExtendedData(),ExtremeRequest.class);
						iso.setISO_039_ResponseCode("909");
						iso.setISO_039p_ResponseDetail("PROBLEMAS AL RECUPERAR TRANSACCIONES, VALOR 0");
						parser = new AlexParser();
						parser.setCodError(iso.getISO_039_ResponseCode());
						parser.setDesError(iso.getISO_039p_ResponseDetail());
						iso.setISO_115_ExtendedData(SetDefaultResponseErrorString(request,parser));
					}
					
				}else {
					
					if(request == null) 
						request = (ExtremeRequest) serial.StringToObject(iso.getISO_114_ExtendedData(),ExtremeRequest.class);
					iso.setISO_039_ResponseCode("909");
					iso.setISO_039p_ResponseDetail("PROBLEMAS AL RECUPERAR TRANSACCIONES");
					parser = new AlexParser();
					parser.setCodError(iso.getISO_039_ResponseCode());
					parser.setDesError(iso.getISO_039p_ResponseDetail());
					iso.setISO_115_ExtendedData(SetDefaultResponseErrorString(request,parser));
				}
				
			}else {
				
				if(request == null) 
					request = (ExtremeRequest) serial.StringToObject(iso.getISO_114_ExtendedData(),ExtremeRequest.class);
				iso.setISO_039_ResponseCode(refIso.get().getISO_039_ResponseCode());
				iso.setISO_039p_ResponseDetail(refIso.get().getISO_039p_ResponseDetail());
				parser = new AlexParser();
				parser.setCodError(refIso.get().getISO_039_ResponseCode());
				parser.setDesError(refIso.get().getISO_039p_ResponseDetail());
				iso.setISO_115_ExtendedData(SetDefaultResponseErrorString(request,parser));
			}
				
		} catch (Exception e) {
			
			if(iso.getTickAut().isStarted())
				iso.getTickAut().stop();
			
			parser = new AlexParser();
			parser.setCodError("96");
			parser.setDesError(GeneralUtils.ExceptionToString(null, e, false));
			iso.setISO_115_ExtendedData(SetDefaultResponseErrorString(request,parser));
			log.WriteLogMonitor("Error Modulo " + this.getClass().getName() + "::RetrieveStatusTransactionsTransfer" , TypeMonitor.error, e);
			
		}finally {
			
			iso.setWsTempAut((iso.getTickAut().getTime(TimeUnit.MILLISECONDS)/1000.0));
		}
		
		return iso;
	}
	 
	public wIso8583 RegisterTransferMasive(wIso8583 iso) {
		
		try {
			iso.getTickAut().reset();
			iso.getTickAut().start();
			iso.setISO_039_ResponseCode(Arrays.asList(iso.getISO_120_ExtendedData().split("\\|")).get(0));
			iso.setISO_039p_ResponseDetail(Arrays.asList(iso.getISO_120_ExtendedData().split("\\|")).get(1));
			iso.setWsIso_LogStatus(2);
			iso.setISO_041_CardAcceptorID("127.0.0.1");
			if(iso.getTickAut().isStarted())
				iso.getTickAut().stop();
			
		} catch (Exception e) {
			
			if(iso.getTickAut().isStarted())
				iso.getTickAut().stop();
			log.WriteLogMonitor("Error Modulo " + this.getClass().getName() + "::RegisterTransferMasive" , TypeMonitor.error, e);
		}finally {
			
			iso.setWsTempAut((iso.getTickAut().getTime(TimeUnit.MILLISECONDS)/1000.0));
		}
		
		return iso;
	}
	 
	public void ProcessingMasiveTransfer(List<Iso8583> isoList) {
		
		try {
			
			
			EngineCallableProcessor<Iso8583> engine = new EngineCallableProcessor<>(20);
			for (Iso8583 iso : isoList) {
				csProcess proc = new csProcess(iso, "127.0.0.1");
				engine.add(proc);
			}
			List<Iso8583> listIsoProc = engine.goProcess();
		
			List<Iso8583> ProccesorOK = listIsoProc.stream()
						               .filter(p -> p.getISO_039_ResponseCode().equals("000"))
						               .peek(Objects::requireNonNull)
						               .collect(Collectors.toList());
			System.out.println("Total Transacciones OK:    " + ProccesorOK.size() + "\n" +
					           "Total Transacciones ERROR: " + (isoList.size() - ProccesorOK.size()));
			
			/*for (Iso8583 iso : isoList) {
			
				iso = processor.ProcessTransactionMain(iso, "127.0.0.1");
				break;
			}*/
			
		} catch (Exception e) {
			
			log.WriteLogMonitor("Error Modulo " + this.getClass().getName() + "::ProcessTransactionAlexSoft" , TypeMonitor.error, e);
		}
	}
	 
	public String ProcessTransactionAlexSoft(ExtremeRequest req){
		
		String response = null;
		Thread t = null;
		wIso8583 wiso = new wIso8583();
		
		try {
			
			AlexParser parser = new AlexParser();
			Iso8583 iso = parser.ExtremeMessageToIsoHeader(req);
			Iso8583 isoRespaldo = (Iso8583) iso.clone();
			if(iso.getISO_039_ResponseCode().equals("000")){
				
				csProcess processor = new csProcess();
				iso = processor.ProcessTransactionMain(iso, this.IP);
				if(iso.getISO_039_ResponseCode().equals("000")){
					
					ContainerStruct struct = new ContainerStruct();
					struct.setExtremeRequest(req);
					struct.setIso(iso);
					
					Class<?> instanceClass = Class.forName("com.fitbank.middleware.acquirers.AlexSoftIsAcq");																			
					Object classInstance = instanceClass.getConstructor(new Class[]{}).newInstance();					
					Method methodToInvoke = instanceClass.getMethod(isoRespaldo.getISO_BitMap(), ContainerStruct.class);					
					response = (String) methodToInvoke.invoke(classInstance, struct);
					
				}else{
					
					
					t = new Thread(new LoggerConfig().WriteMonitorRun(wiso, iso));
					t.start();
					response = SetDefaultResponseError(req, iso);
				}
				
			}else{
				
				t = new Thread(new LoggerConfig().WriteMonitorRun(wiso, iso));
				t.start();
				response = SetDefaultResponseError(req, iso);
			}
			
		} catch (Exception e) {
			
			log.WriteLogMonitor("Error Modulo " + this.getClass().getName() + "::ProcessTransactionAlexSoft" , TypeMonitor.error, e);
		}
		
		return response;
	}
	
	public String AlexQueryClientDocument(ContainerStruct struct){
		
		String response = null;
		Iso8583 iso = null;
		ExtremeRequest req = null;
		try {
			
			iso = struct.getIso();
			req = struct.getExtremeRequest();
			ExtremeReply reply = new ExtremeReply(req);
			reply.getHeader().setValueTag("RetCode", "0");	
			reply.setData(null);
			FxTables tables = new FxTables();
			Clients client = new Clients();
			String[] data = iso.getISO_114_ExtendedData().split("\\|");
			List<Registro> reg = new ArrayList<>();
			String doc = null;
			if(data[6].equalsIgnoreCase("RUC"))
				doc = "1";
			else if (data[6].equalsIgnoreCase("PAS")) {
				doc = "10";
			}else if (data[6].equalsIgnoreCase("CED")) {
				doc = "3";
			}
			
			log.WriteLogMonitor("Antes Valor oficina... ", TypeMonitor.monitor, null);
			String valorOficina = "1";
			String bitacora = StringUtils.Empty();
			
			if(data.length == 10)
				valorOficina = data[9].trim();
			else {
				
				bitacora = " No se encontro valor... se pondra por defecto: 1";
				valorOficina = "1";
			}
			
			log.WriteLogMonitor("Valor oficina... |" + valorOficina + "|" + bitacora, TypeMonitor.monitor, null);
			reg.add(new Registro(valorOficina, 
				   data[0], data[1], data[2].equalsIgnoreCase("NAT")?"1":"2" , 
				   iso.getISO_002_PAN(), doc, 
				   data[3].trim(), data[4].trim(), StringUtils.isNullOrEmpty(data[5].trim())?"":data[5], 
				   StringUtils.isNullOrEmpty(data[7].trim())?"":data[7].trim(), 
				   StringUtils.isNullOrEmpty(data[8].trim())?"":data[8].trim()));
			client.setRegistro(reg);
			tables.setClientes(client);
			reply.setTables(tables);
			response = SerializationObject.ObjectToString(reply);
		
		} catch (Exception e) {
			
			iso.setISO_039_ResponseCode("999");
			iso.setISO_039p_ResponseDetail(GeneralUtils.ExceptionToString("ERROR EN PROCESOS", e, false));
			response = SetDefaultResponseError(req, iso);
			log.WriteLogMonitor("Error Modulo " + this.getClass().getName() + "::AlexQueryClientDocument" , TypeMonitor.error, e);
		}
		return response;
	}
	
	public String AlexQueryOfficialAccount(ContainerStruct struct){
		
		String response = null;
		Iso8583 iso = null;
		ExtremeRequest req = null;
		try {
			
			iso = struct.getIso();
			req = struct.getExtremeRequest();
			ExtremeReply reply = new ExtremeReply(req);
			reply.getHeader().setValueTag("RetCode", "0");	
			reply.setData(null);
			String[] data = iso.getISO_114_ExtendedData().split("\\|");
			Data data_ = new Data();
			List<DField> fields = new ArrayList<>();
			fields.add(new DField("fullname", data[0], null));
			fields.add(new DField("Picture", " ", null));
			fields.add(new DField("mail", StringUtils.IsNullOrEmpty(data[1])?" ":data[1], null));
			fields.add(new DField("telefono", StringUtils.IsNullOrEmpty(data[2])?" ":data[2], null));
			data_.setDfield(fields);
			reply.setData(data_);
			response = SerializationObject.ObjectToString(reply);
		
		} catch (Exception e) {
			
			iso.setISO_039_ResponseCode("999");
			iso.setISO_039p_ResponseDetail(GeneralUtils.ExceptionToString("ERROR EN PROCESOS", e, false));
			response = SetDefaultResponseError(req, iso);
			log.WriteLogMonitor("Error Modulo " + this.getClass().getName() + "::AlexQueryOfficialAccount" , TypeMonitor.error, e);
		}
		return response;
	}
	
	public String AlexTransferTrx(ContainerStruct struct){
		
		String response = null;
		Iso8583 iso = null;
		ExtremeRequest req = null;
		try {
			
			iso = struct.getIso();
			req = struct.getExtremeRequest();
			ExtremeReply reply = new ExtremeReply(req);
			reply.getHeader().setValueTag("RetCode", "0");	
			reply.setData(null);
			Data data_ = new Data();
			List<DField> fields = new ArrayList<>();
			fields.add(new DField("IsError", iso.getISO_039_ResponseCode().equals("000")?"false":"true", null));
			fields.add(new DField("RetMsg", iso.getISO_039p_ResponseDetail(), null));
			fields.add(new DField("BackendReference", Arrays.asList(iso.getISO_011_SysAuditNumber().
								  split("\\_")).get(0), null));
			fields.add(new DField("BackendCode", iso.getISO_039_ResponseCode(), null));
			fields.add(new DField("TransactionId", Arrays.asList(iso.getISO_011_SysAuditNumber().
					              split("\\_")).get(0), null));
			data_.setDfield(fields);
			reply.setData(data_);
			response = SerializationObject.ObjectToString(reply);
			
		} catch (Exception e) {
			
			iso.setISO_039_ResponseCode("999");
			iso.setISO_039p_ResponseDetail(GeneralUtils.ExceptionToString("ERROR EN PROCESOS", e, false));
			response = SetDefaultResponseError(req, iso);
			log.WriteLogMonitor("Error Modulo " + this.getClass().getName() + "::AlexTransferTrx" , TypeMonitor.error, e);
		}
		return response;
	}
	
    public String AlexGetComission(ContainerStruct struct){
		
		String response = null;
		Iso8583 iso = null;
		ExtremeRequest req = null;
		try {
			
			DecimalFormatSymbols simbolo = new DecimalFormatSymbols();
		    simbolo.setDecimalSeparator('.');
			DecimalFormat df = new DecimalFormat("0.00", simbolo);
			iso = struct.getIso();
			req = struct.getExtremeRequest();
			ExtremeReply reply = new ExtremeReply(req);
			reply.getHeader().setValueTag("RetCode", "0");	
			reply.setData(null);
			Data data_ = new Data();
			List<DField> fields = new ArrayList<>();
			fields.add(new DField("CostAmount", df.format(iso.getISO_006_BillAmount()) , "f"));
			fields.add(new DField("CostCurrencyId", "840", null));
			data_.setDfield(fields);
			reply.setData(data_);
			response = SerializationObject.ObjectToString(reply);
		
		} catch (Exception e) {
			
			iso.setISO_039_ResponseCode("999");
			iso.setISO_039p_ResponseDetail(GeneralUtils.ExceptionToString("ERROR EN PROCESOS", e, false));
			response = SetDefaultResponseError(req, iso);
			log.WriteLogMonitor("Error Modulo " + this.getClass().getName() + "::AlexGetComission" , TypeMonitor.error, e);
		}
		return response;
	}
	
	public String AlexQueryAccountDetail(ContainerStruct struct){
		
		String response = null;
		Iso8583 iso = null;
		ExtremeRequest req = null;
		try {
			
			iso = struct.getIso();
			req = struct.getExtremeRequest();
			ExtremeReply reply = new ExtremeReply(req);
			
			DecimalFormatSymbols simbolo=new DecimalFormatSymbols();
		    simbolo.setDecimalSeparator('.');
			DecimalFormat df = new DecimalFormat("0.00",simbolo);
			reply.getHeader().setValueTag("RetCode", "0");	
			reply.setData(null);
			String[] data = iso.getISO_114_ExtendedData().split("\\|");
			Data data_ = new Data();
			List<DField> fields = new ArrayList<>();
			fields.add(new DField("Officername", StringUtils.IsNullOrEmpty(data[2])?"":data[2], null));
			fields.add(new DField("AvailableBalance", StringUtils.IsNullOrEmpty(data[3])?"0.00":df.format(Double.parseDouble(data[3])), "f"));
			fields.add(new DField("Balance24", StringUtils.IsNullOrEmpty(data[4])?"0.00":df.format(Double.parseDouble(data[4])), "f"));
			fields.add(new DField("Balance48", StringUtils.IsNullOrEmpty(data[5])?"0.00":df.format(Double.parseDouble(data[5])), "f"));
			fields.add(new DField("Balance48More", StringUtils.IsNullOrEmpty(data[6])?"0.00":df.format(Double.parseDouble(data[6])), "f"));
			fields.add(new DField("MonthlyBalance", StringUtils.IsNullOrEmpty(data[7])?"0.00":df.format(Double.parseDouble(data[7])), "f"));
			fields.add(new DField("PendChecks", StringUtils.IsNullOrEmpty(data[8])?"0":data[8], "i32"));
			fields.add(new DField("ChecksToday", StringUtils.IsNullOrEmpty(data[9])?"0":data[9], "i32"));
			fields.add(new DField("ChecksTomorroy", StringUtils.IsNullOrEmpty(data[10])?"0":data[10], "i32"));
			fields.add(new DField("CancelChecks", StringUtils.IsNullOrEmpty(data[11])?"0":data[11], "i32"));
			fields.add(new DField("CertifiedChecks", StringUtils.IsNullOrEmpty(data[12])?"0":data[12], "i32"));
			fields.add(new DField("RejectedChecks", StringUtils.IsNullOrEmpty(data[13])?"0":data[13], "i32"));
			fields.add(new DField("BlockedAmount", StringUtils.IsNullOrEmpty(data[14])?"0.00":df.format(Double.parseDouble(data[14])), "f"));
			fields.add(new DField("MovOfMonths", StringUtils.IsNullOrEmpty(data[15])?"0":data[15], "i32"));
			fields.add(new DField("ChecksDrawn", StringUtils.IsNullOrEmpty(data[16])?"0":data[16], "i32"));
			fields.add(new DField("Overdrafts", StringUtils.IsNullOrEmpty(data[17])?"0.00":df.format(Double.parseDouble(data[17])), "f"));
			fields.add(new DField("BranchName", StringUtils.IsNullOrEmpty(data[18])?"":data[18], null));
			fields.add(new DField("OwnerName", StringUtils.IsNullOrEmpty(data[20])?"":data[20], null));
			fields.add(new DField("retMsg", iso.getISO_039p_ResponseDetail(), null));
			
			List<HField> dField = new ArrayList<>();
			Header hdr = reply.getHeader();
			dField = hdr.getHfield();
			dField.add(new HField("AccountId1", iso.getISO_102_AccountID_1(), null));
			dField.add(new HField("AccountNum1", iso.getISO_102_AccountID_1(), null));
			dField.add(new HField("ValueTrn", StringUtils.IsNullOrEmpty(data[1])?"0.00":df.format(Double.parseDouble(data[1])), "f"));
			hdr.setHfield(dField);
			reply.setHeader(hdr);
			
			
			data_.setDfield(fields);
			reply.setData(data_);
			response = SerializationObject.ObjectToString(reply);
		
		} catch (Exception e) {
			
			iso.setISO_039_ResponseCode("999");
			iso.setISO_039p_ResponseDetail(GeneralUtils.ExceptionToString("ERROR EN PROCESOS", e, false));
			response = SetDefaultResponseError(req, iso);
			log.WriteLogMonitor("Error Modulo " + this.getClass().getName() + "::AlexQueryAccountDetail" , TypeMonitor.error, e);
		}
		return response;
	}
	
	public String AlexQueryObtainProducts(ContainerStruct struct){ //Posicion consolidada
		
		String response = null;
		Iso8583 iso = null;
		ExtremeRequest req = null;
		try {
			
			iso = struct.getIso();
			req = struct.getExtremeRequest();
			ExtremeReply reply = new ExtremeReply(req);
			reply.getHeader().setValueTag("RetCode", "0");	
			reply.setData(null);
			FxTables tables = new FxTables();
			Products productsObjetc = new Products();
			List<Registro> reg = new ArrayList<>();
			@SuppressWarnings("unchecked")
			List<Iterables> it = (List<Iterables>) SerializationObject.XMLToObject(iso.getISO_114_ExtendedData());
			String producto = null,  cCuenta = null, cStatusCuenta = null, cProducto = null, dProducto = null, cMoneda = null;
			for (Iterables iterables : it) {
				
				for (int i = 0; i < iso.getISO_115_ExtendedData().split("\\,").length; i++) {
					
					producto = iterables.getIterarors().
							get(Arrays.asList(iso.getISO_115_ExtendedData().split("\\,")).get(0));
					cCuenta = iterables.getIterarors().
							get(Arrays.asList(iso.getISO_115_ExtendedData().split("\\,")).get(1));
					cStatusCuenta= iterables.getIterarors().
							get(Arrays.asList(iso.getISO_115_ExtendedData().split("\\,")).get(2));
					cProducto = iterables.getIterarors().
							get(Arrays.asList(iso.getISO_115_ExtendedData().split("\\,")).get(3));
					dProducto = iterables.getIterarors().
							get(Arrays.asList(iso.getISO_115_ExtendedData().split("\\,")).get(4));
					cMoneda = iterables.getIterarors().
							get(Arrays.asList(iso.getISO_115_ExtendedData().split("\\,")).get(5));
					
				}
				String tpr =  producto.substring(0,2);
				String canTorigen = cProducto.substring(0,1);
				String canTrx = null;
				if(tpr.equals("04") || tpr.equals("03")){
					   cStatusCuenta = cStatusCuenta.equals("001") ? "0" : cStatusCuenta.equals("002") ? "1" :
					   cStatusCuenta.equals("003") ? "3" : cStatusCuenta.equals("004") ? "2" :
					   cStatusCuenta.equals("005") ? "0":"0";
					   if(canTorigen.equals("4")){
						
						   Config config = new Config();
						   config = config.getConfigSystem("SRC_" + iso.getISO_003_ProcessingCode() + "_" +
								   						   iso.getISO_018_MerchantType() + "_"   +
								   						   iso.getISO_024_NetworkId());
						   
						   if(config != null){
							   
							   final String codProd = cProducto;
							   /*if(Arrays.stream(config.getCfg_Valor().split("\\,"))
									   .anyMatch(x -> x.equalsIgnoreCase(codProd)))
								   canTrx = "1";
							   else
								   canTrx = "0";*/
							   String aux = Arrays.stream(config.getCfg_Valor().split("\\,"))
									   .filter(x -> x.contains(codProd))
									   .findFirst().orElseGet(() -> null);
							   if(!StringUtils.IsNullOrEmpty(aux)){
								   
								   switch (aux.substring(0,2)) {
									case "DC":
										 canTrx = "1";
										break;
									case "CC":
										 canTrx = "2";
										break;	
									case "DD":
										 canTrx = "3";
										break;
									case "XX":
										 canTrx = "0";
										break;
									default:
										canTrx = "4";
										break;
									}
							   }else{
								   
								   if(codProd.startsWith("4"))
								   {
									    iso.setISO_039_ResponseCode("999");
										iso.setISO_039p_ResponseDetail("ERROR, NO SE PUEDE RECUPERAR PERMISOS "
												+ "EN (CTAS. DEBITANTES/ACREDITANTES)");
										return response = SetDefaultResponseError(req, iso);
								   }else{
									   
									   canTrx = "0";
								   }
									
									
							   }
							   
						   }else {
							   
							    iso.setISO_039_ResponseCode("999");
								iso.setISO_039p_ResponseDetail("ERROR, NO SE HA PODIDO RECUPERAR EL TIPO DE CUENTAS "
										+ "VISTA HABILITADAS PARA MANTENIMIENTO EN DEBITANTES/ACREDITANTES");
								return response = SetDefaultResponseError(req, iso);
						   }
							
						}else if(canTorigen.equals("3"))
							canTrx = "0";
					   
				}else if (tpr.equals("05")) {
					
					    cStatusCuenta = cStatusCuenta.equals("001") ? "0" : cStatusCuenta.equals("002") ? "1" :
						cStatusCuenta.equals("003") ? "0" : cStatusCuenta.equals("004") ? "3" :
					    cStatusCuenta.equals("005") ? "3":"3";
					    canTrx = "0";
					    
				}else if (tpr.equals("06")) {
					
						cStatusCuenta = cStatusCuenta.equals("001") ? "0" : cStatusCuenta.equals("002") ? "0" :
						cStatusCuenta.equals("003") ? "1" : cStatusCuenta.equals("004") ? "3" :
					    cStatusCuenta.equals("005") ? "0" :  cStatusCuenta.equals("006") ? "3" : 
					    cStatusCuenta.equals("007") ? "3" : "3";
						canTrx = "0";
				}
				
				tpr = tpr.equals("03") ? "2" : tpr.equals("04") ? "2" : tpr.equals("05") ? "4" : tpr.equals("06") ? "5" : "2";
				reg.add(new Registro(cCuenta, cCuenta, cStatusCuenta, tpr, dProducto, canTrx, cMoneda.equals("USD") ? "840":"000"));
			}
			productsObjetc.setRegistro(reg);
			tables.setProductos(productsObjetc);
			reply.setTables(tables);
			response = SerializationObject.ObjectToString(reply);
		
		} catch (Exception e) {
			
			iso.setISO_039_ResponseCode("999");
			iso.setISO_039p_ResponseDetail(GeneralUtils.ExceptionToString("ERROR EN PROCESOS", e, false));
			response = SetDefaultResponseError(req, iso);
			log.WriteLogMonitor("Error Modulo " + this.getClass().getName() + "::AlexQueryObtainProducts" , TypeMonitor.error, e);
		}
		return response;
		
	}
	
	public String AlexQueryLoanCredit(ContainerStruct struct){
		
		String response = null;
		Iso8583 iso = null;
		ExtremeRequest req = null;
		try {
			
			iso = struct.getIso();
			req = struct.getExtremeRequest();
			ExtremeReply reply = new ExtremeReply(req);
			reply.getHeader().setValueTag("RetCode", "0");	
			reply.setData(null);
			
			@SuppressWarnings("unchecked")
			List<Iterables> it = (List<Iterables>) SerializationObject.XMLToObject(iso.getISO_114_ExtendedData());
			
			DecimalFormatSymbols simbolo=new DecimalFormatSymbols();
		    simbolo.setDecimalSeparator('.');
			DecimalFormat df = new DecimalFormat("0.00",simbolo);
			
			Data data_ = new Data();
			List<DField> fields = new ArrayList<>();
			@SuppressWarnings("unused")
			String saldo = null,  fvencimiento = null, mora = null, estadoCta = null, subCuenta = null, 
				   interes = null, capital = null, otrosCargos = null;
			for (Iterables iterables : it) {
				
				for (int i = 0; i < iso.getISO_115_ExtendedData().split("\\,").length; i++) {
					
					saldo = iterables.getIterarors().
							get(Arrays.asList(iso.getISO_115_ExtendedData().split("\\,")).get(0));
					estadoCta = iterables.getIterarors().
							get(Arrays.asList(iso.getISO_115_ExtendedData().split("\\,")).get(6));
					mora = iterables.getIterarors().
							get(Arrays.asList(iso.getISO_115_ExtendedData().split("\\,")).get(5));
					fvencimiento = iterables.getIterarors().
							get(Arrays.asList(iso.getISO_115_ExtendedData().split("\\,")).get(3));
					subCuenta = iterables.getIterarors().
							get(Arrays.asList(iso.getISO_115_ExtendedData().split("\\,")).get(1));
					interes = iterables.getIterarors().
							get(Arrays.asList(iso.getISO_115_ExtendedData().split("\\,")).get(4));
					capital = iterables.getIterarors().
							get(Arrays.asList(iso.getISO_115_ExtendedData().split("\\,")).get(2));
					otrosCargos = iterables.getIterarors().
							get(Arrays.asList(iso.getISO_115_ExtendedData().split("\\,")).get(7));
					
				}
				double total_ = 0;
				double cuota_ = Double.parseDouble(capital);
				double mora_ =  Double.parseDouble(mora);
				double cargos_ = Double.parseDouble(otrosCargos);
				double interes_ = Double.parseDouble(interes);
				total_ = cuota_ + mora_ + cargos_ + interes_;
				
				fields.add(new DField("FeeNumber", iso.getISO_002_PAN(), "i32"));
				fields.add(new DField("PrincipalAmount", StringUtils.IsNullOrEmpty(capital)?"0.00":df.format(Double.parseDouble(capital)), "f"));
				fields.add(new DField("DueDate", StringUtils.IsNullOrEmpty(fvencimiento)?"null"
						         :FormatUtils.DateToString(fvencimiento, "yyyy-MM-dd HH:mm:ss.S", "yyyyMMdd"), "f"));
				fields.add(new DField("OverdueAmount", StringUtils.IsNullOrEmpty(mora)?"0.00":df.format(Double.parseDouble(mora)), "f"));
				fields.add(new DField("TotalAmount", df.format(total_), "f"));
				fields.add(new DField("FeeStatusId", StringUtils.IsNullOrEmpty(estadoCta)?" ":estadoCta, "b"));
				fields.add(new DField("RetMsg", iso.getISO_039p_ResponseDetail(), null));
			}
			
			List<HField> dField = new ArrayList<>();
			Header hdr = reply.getHeader();
			dField = hdr.getHfield();
			dField.add(new HField("ValueTrn", StringUtils.IsNullOrEmpty(saldo)?"0.00":df.format(Double.parseDouble(saldo)), "f"));
			hdr.setHfield(dField);
			reply.setHeader(hdr);
			
			data_.setDfield(fields);
			reply.setData(data_);
			response = SerializationObject.ObjectToString(reply);
		
		} catch (Exception e) {
			
			iso.setISO_039_ResponseCode("999");
			iso.setISO_039p_ResponseDetail(GeneralUtils.ExceptionToString("ERROR EN PROCESOS", e, false));
			response = SetDefaultResponseError(req, iso);
			log.WriteLogMonitor("Error Modulo " + this.getClass().getName() + "::AlexQueryLoanCredit" , TypeMonitor.error, e);
		}
		return response;
		
	}
	
    public String AlexQueryLoanCreditIterator(ContainerStruct struct){
		
		String response = null;
		Iso8583 iso = null;
		ExtremeRequest req = null;
		try {
			
			iso = struct.getIso();
			req = struct.getExtremeRequest();
			ExtremeReply reply = new ExtremeReply(req);
			reply.getHeader().setValueTag("RetCode", "0");	
			reply.setData(null);
			
			@SuppressWarnings("unchecked")
			List<Iterables> it = (List<Iterables>) SerializationObject.XMLToObject(iso.getISO_114_ExtendedData());
			FxTables tables = new FxTables();
			Cuotas cuotas = new Cuotas();
			List<Registro> reg = new ArrayList<>();
			DecimalFormatSymbols simbolo=new DecimalFormatSymbols();
		    simbolo.setDecimalSeparator('.');
			DecimalFormat df = new DecimalFormat("0.00",simbolo);
			
			Data data_ = new Data();
			List<DField> fields = new ArrayList<>();
			String saldo = null,  fvencimiento = null, mora = null, estadoCta = null, subCuenta = null, 
				   interes = null, capital = null, otrosCargos = null;
			
			int counter = Integer.parseInt(Arrays.asList(iso.getISO_124_ExtendedData().split("\\-")).get(0));
			for (Iterables iterables : it) {
				
				
				for (int i = 0; i < iso.getISO_115_ExtendedData().split("\\,").length; i++) {
					
					saldo = iterables.getIterarors().
							get(Arrays.asList(iso.getISO_115_ExtendedData().split("\\,")).get(0));
					estadoCta = iterables.getIterarors().
							get(Arrays.asList(iso.getISO_115_ExtendedData().split("\\,")).get(6));
					mora = iterables.getIterarors().
							get(Arrays.asList(iso.getISO_115_ExtendedData().split("\\,")).get(5));
					fvencimiento = iterables.getIterarors().
							get(Arrays.asList(iso.getISO_115_ExtendedData().split("\\,")).get(3));
					subCuenta = iterables.getIterarors().
							get(Arrays.asList(iso.getISO_115_ExtendedData().split("\\,")).get(1));
					interes = iterables.getIterarors().
							get(Arrays.asList(iso.getISO_115_ExtendedData().split("\\,")).get(4));
					capital = iterables.getIterarors().
							get(Arrays.asList(iso.getISO_115_ExtendedData().split("\\,")).get(2));
					otrosCargos = iterables.getIterarors().
							get(Arrays.asList(iso.getISO_115_ExtendedData().split("\\,")).get(7));
					
				}
				
				switch (estadoCta) {
				case "":
					
					break;

				default:
					break;
				}
				
				
				double total_ = 0;
				double cuota_ = Double.parseDouble(StringUtils.IsNullOrEmpty(capital.replace("null", ""))?"0":capital);
				double mora_ =  Double.parseDouble(StringUtils.IsNullOrEmpty(mora.replace("null", ""))?"0":mora);
				double cargos_ = Double.parseDouble(StringUtils.IsNullOrEmpty(otrosCargos.replace("null", ""))?"0":otrosCargos);
				double interes_ = Double.parseDouble(StringUtils.IsNullOrEmpty(interes.replace("null", ""))?"0":interes);
				total_ = cuota_ + mora_ + cargos_ + interes_;
				
				String NroCuota= "";
				if(iso.getISO_055_EMV().equals("0")){
					NroCuota = String.valueOf((counter + 1));
				}else {
					NroCuota = subCuenta;
				}
				
				reg.add(new Registro(df.format(Double.parseDouble(saldo)), NroCuota, df.format(Double.parseDouble(capital)), 
						FormatUtils.DateToString(fvencimiento, "yyyy-MM-dd HH:mm:ss.S", "yyyyMMdd"), df.format(Double.parseDouble(interes)), 
						df.format(Double.parseDouble(StringUtils.IsNullOrEmpty(mora.replace("null", ""))?"0":mora)), estadoCta, df.format(Double.parseDouble(otrosCargos)), 
						df.format(total_)));
				
				cuotas.setRegistro(reg);
				counter++;
			}
			fields.add(new DField("TotalReg", iso.getISO_123_ExtendedData(), "i32"));
			fields.add(new DField("RetMsg", iso.getISO_039p_ResponseDetail(), null));
			tables.setCuotas(cuotas);
			reply.setTables(tables);
			data_.setDfield(fields);
			reply.setData(data_);
			response = SerializationObject.ObjectToString(reply);
		
		} catch (Exception e) {
			
			iso.setISO_039_ResponseCode("999");
			iso.setISO_039p_ResponseDetail(GeneralUtils.ExceptionToString("ERROR EN PROCESOS", e, false));
			response = SetDefaultResponseError(req, iso);
			log.WriteLogMonitor("Error Modulo " + this.getClass().getName() + "::AlexQueryLoanCreditIterator" , TypeMonitor.error, e);
		}
		return response;
		
	}
	
    public String AlexQueryPayCredit(ContainerStruct struct){
		
		String response = null;
		Iso8583 iso = null;
		ExtremeRequest req = null;
		try {
			
			iso = struct.getIso();
			req = struct.getExtremeRequest();
			ExtremeReply reply = new ExtremeReply(req);
			reply.getHeader().setValueTag("RetCode", "0");	
			reply.setData(null);
			
			@SuppressWarnings("unchecked")
			List<Iterables> it = (List<Iterables>) SerializationObject.XMLToObject(iso.getISO_114_ExtendedData());
			FxTables tables = new FxTables();
			Payments pagos = new Payments();
			List<Registro> reg = new ArrayList<>();
			DecimalFormatSymbols simbolo=new DecimalFormatSymbols();
		    simbolo.setDecimalSeparator('.');
			DecimalFormat df = new DecimalFormat("0.00",simbolo);
			
			Data data_ = new Data();
			List<DField> fields = new ArrayList<>();
			String saldo = null,  fpago = null, interesVencido = null, subCuenta = null, 
				   interes = null, capital = null, otrosCargos = null, total = null;
			
			for (Iterables iterables : it) {
				
				
				for (int i = 0; i < iso.getISO_115_ExtendedData().split("\\,").length; i++) {
					
					saldo = iterables.getIterarors().
							get(Arrays.asList(iso.getISO_115_ExtendedData().split("\\,")).get(0));
					interesVencido = iterables.getIterarors().
							get(Arrays.asList(iso.getISO_115_ExtendedData().split("\\,")).get(4));
					fpago = iterables.getIterarors().
							get(Arrays.asList(iso.getISO_115_ExtendedData().split("\\,")).get(5));
					subCuenta = iterables.getIterarors().
							get(Arrays.asList(iso.getISO_115_ExtendedData().split("\\,")).get(1));
					interes = iterables.getIterarors().
							get(Arrays.asList(iso.getISO_115_ExtendedData().split("\\,")).get(2));
					capital = iterables.getIterarors().
							get(Arrays.asList(iso.getISO_115_ExtendedData().split("\\,")).get(6));
					otrosCargos = iterables.getIterarors().
							get(Arrays.asList(iso.getISO_115_ExtendedData().split("\\,")).get(3));
					total = iterables.getIterarors().
							get(Arrays.asList(iso.getISO_115_ExtendedData().split("\\,")).get(7));
					
				}
				double total_ = Double.parseDouble(total);
				/*double cuota_ = Double.parseDouble(capital);
				double intVenc_ =  Double.parseDouble(interesVencido);
				double cargos_ = Double.parseDouble(otrosCargos);
				double intNorm_ = Double.parseDouble(interes);
				total_ = cuota_ + intVenc_ + cargos_ + intNorm_;*/
				
				reg.add(new Registro(0, df.format(Double.parseDouble(saldo)), subCuenta, "3", df.format(Double.parseDouble(interes)), 
						df.format(Double.parseDouble(otrosCargos)), df.format(Double.parseDouble(interesVencido)), 
						FormatUtils.DateToString(fpago, "yyyy-MM-dd HH:mm:ss.S", "yyyyMMdd"), df.format(Double.parseDouble(capital)), 
						df.format(total_)));
				
				pagos.setRegistro(reg);
			}
			fields.add(new DField("TotalReg", iso.getISO_123_ExtendedData(), "i32"));
			fields.add(new DField("RetMsg", iso.getISO_039p_ResponseDetail(), null));
			tables.setPagos(pagos);
			reply.setTables(tables);
			data_.setDfield(fields);
			reply.setData(data_);
			response = SerializationObject.ObjectToString(reply);
		
		} catch (Exception e) {
			
			iso.setISO_039_ResponseCode("999");
			iso.setISO_039p_ResponseDetail(GeneralUtils.ExceptionToString("ERROR EN PROCESOS", e, false));
			response = SetDefaultResponseError(req, iso);
			log.WriteLogMonitor("Error Modulo " + this.getClass().getName() + "::AlexQueryPayCredit" , TypeMonitor.error, e);
		}
		return response;
		
	}
    
    public String AlexQueryFiveMovements(ContainerStruct struct){
		
		String response = null;
		Iso8583 iso = null;
		ExtremeRequest req = null;
		try {
			
			iso = struct.getIso();
			req = struct.getExtremeRequest();
			ExtremeReply reply = new ExtremeReply(req);
			reply.getHeader().setValueTag("RetCode", "0");	
			reply.setData(null);
			
			@SuppressWarnings("unchecked")
			List<Iterables> it = (List<Iterables>) SerializationObject.XMLToObject(iso.getISO_114_ExtendedData());
			FxTables tables = new FxTables();
			LastMovements movimientos = new LastMovements();
			List<Registro> reg = new ArrayList<>();
			DecimalFormatSymbols simbolo=new DecimalFormatSymbols();
		    simbolo.setDecimalSeparator('.');
			DecimalFormat df = new DecimalFormat("0.00",simbolo);
			
			Data data_ = new Data();
			List<DField> fields = new ArrayList<>();
			@SuppressWarnings("unused")
			String movId = null,  ccuenta = null, fechaMov = null, monto = null, 
				   esDebito = null, saldoPosterior = null, tipoMov = null, descripTipMov = null, 
				   checkId = null, descripcion = null;
			
			int counter = 0;
			for (Iterables iterables : it) {
				
				counter++;
				for (int i = 0; i < iso.getISO_115_ExtendedData().split("\\,").length; i++) {
					
					movId = iterables.getIterarors().
							get(Arrays.asList(iso.getISO_115_ExtendedData().split("\\,")).get(0));
					ccuenta = iterables.getIterarors().
							get(Arrays.asList(iso.getISO_115_ExtendedData().split("\\,")).get(1));
					fechaMov = iterables.getIterarors().
							get(Arrays.asList(iso.getISO_115_ExtendedData().split("\\,")).get(2));
					monto = iterables.getIterarors().
							get(Arrays.asList(iso.getISO_115_ExtendedData().split("\\,")).get(4));
					esDebito = iterables.getIterarors().
							get(Arrays.asList(iso.getISO_115_ExtendedData().split("\\,")).get(5));
					saldoPosterior = iterables.getIterarors().
							get(Arrays.asList(iso.getISO_115_ExtendedData().split("\\,")).get(6));
					tipoMov = iterables.getIterarors().
							get(Arrays.asList(iso.getISO_115_ExtendedData().split("\\,")).get(5));
					descripTipMov = iterables.getIterarors().
							get(Arrays.asList(iso.getISO_115_ExtendedData().split("\\,")).get(5));
					checkId = iterables.getIterarors().
							get(Arrays.asList(iso.getISO_115_ExtendedData().split("\\,")).get(7));
					descripcion = iterables.getIterarors().
							get(Arrays.asList(iso.getISO_115_ExtendedData().split("\\,")).get(3));
					
				}
				
				reg.add(new Registro(String.valueOf(counter), ccuenta, FormatUtils.DateToString(fechaMov, "yyyy-MM-dd HH:mm:ss.SSSSSS", "yyyyMMdd"), 
						descripcion.toUpperCase(), df.format(Double.parseDouble(monto)), esDebito.equalsIgnoreCase("C")?"False":"True", 
						df.format(Double.parseDouble(saldoPosterior)), tipoMov.equalsIgnoreCase("C")?"2":"3", 
						tipoMov.equalsIgnoreCase("C")?"NOTA DE CREDITO":"NOTA DE DEBITO", 
						checkId.equalsIgnoreCase("null")?"0":checkId));
				
				
				movimientos.setRegistro(reg);
			}
			tables.setMovimientos(movimientos);
			reply.setTables(tables);
			data_.setDfield(fields);
			reply.setData(data_);
			response = SerializationObject.ObjectToString(reply);
		
		} catch (Exception e) {
			
			iso.setISO_039_ResponseCode("999");
			iso.setISO_039p_ResponseDetail(GeneralUtils.ExceptionToString("ERROR EN PROCESOS", e, false));
			response = SetDefaultResponseError(req, iso);
			log.WriteLogMonitor("Error Modulo " + this.getClass().getName() + "::AlexQueryFiveMovements" , TypeMonitor.error, e);
		}
		return response;
		
	}
    
    public String AlexQueryMovements(ContainerStruct struct){
		
		String response = null;
		Iso8583 iso = null;
		ExtremeRequest req = null;
		try {
			
			iso = struct.getIso();
			req = struct.getExtremeRequest();
			ExtremeReply reply = new ExtremeReply(req);
			reply.getHeader().setValueTag("RetCode", "0");	
			reply.setData(null);
			
			@SuppressWarnings("unchecked")
			List<Iterables> it = (List<Iterables>) SerializationObject.XMLToObject(iso.getISO_114_ExtendedData());
			FxTables tables = new FxTables();
			LastMovements movimientos = new LastMovements();
			List<Registro> reg = new ArrayList<>();
			DecimalFormatSymbols simbolo=new DecimalFormatSymbols();
		    simbolo.setDecimalSeparator('.');
			DecimalFormat df = new DecimalFormat("0.00",simbolo);
			
			Data data_ = new Data();
			List<DField> fields = new ArrayList<>();
			@SuppressWarnings("unused")
			String movId = null,  ccuenta = null, fechaMov = null, monto = null, 
				   esDebito = null, saldoPosterior = null, tipoMov = null, descripTipMov = null, 
				   checkId = null, descripcion = null;
			
			int counter = 0;
			for (Iterables iterables : it) {
				
				counter++;
				for (int i = 0; i < iso.getISO_115_ExtendedData().split("\\,").length; i++) {
					
					movId = iterables.getIterarors().
							get(Arrays.asList(iso.getISO_115_ExtendedData().split("\\,")).get(0));
					ccuenta = iterables.getIterarors().
							get(Arrays.asList(iso.getISO_115_ExtendedData().split("\\,")).get(1));
					fechaMov = iterables.getIterarors().
							get(Arrays.asList(iso.getISO_115_ExtendedData().split("\\,")).get(2));
					monto = iterables.getIterarors().
							get(Arrays.asList(iso.getISO_115_ExtendedData().split("\\,")).get(4));
					esDebito = iterables.getIterarors().
							get(Arrays.asList(iso.getISO_115_ExtendedData().split("\\,")).get(5));
					saldoPosterior = iterables.getIterarors().
							get(Arrays.asList(iso.getISO_115_ExtendedData().split("\\,")).get(6));
					tipoMov = iterables.getIterarors().
							get(Arrays.asList(iso.getISO_115_ExtendedData().split("\\,")).get(5));
					descripTipMov = iterables.getIterarors().
							get(Arrays.asList(iso.getISO_115_ExtendedData().split("\\,")).get(5));
					checkId = iterables.getIterarors().
							get(Arrays.asList(iso.getISO_115_ExtendedData().split("\\,")).get(7));
					descripcion = iterables.getIterarors().
							get(Arrays.asList(iso.getISO_115_ExtendedData().split("\\,")).get(3));
					
				}
				
				reg.add(new Registro(String.valueOf(counter), ccuenta, FormatUtils.DateToString(fechaMov, "yyyy-MM-dd HH:mm:ss.SSSSSS", "yyyyMMdd"), 
						descripcion.toUpperCase(), df.format(Double.parseDouble(monto)), esDebito.equalsIgnoreCase("C")?"False":"True", 
						df.format(Double.parseDouble(saldoPosterior)), tipoMov.equalsIgnoreCase("C")?"2":"3", 
						tipoMov.equalsIgnoreCase("C")?"NOTA DE CREDITO":"NOTA DE DEBITO", 
						checkId.equalsIgnoreCase("null")?"0":checkId));
				
				
				movimientos.setRegistro(reg);
			}
			fields.add(new DField("TotalReg", iso.getISO_123_ExtendedData(), "i32"));
			fields.add(new DField("RetMsg", iso.getISO_039p_ResponseDetail(), null));
			tables.setMovimientos(movimientos);
			reply.setTables(tables);
			data_.setDfield(fields);
			reply.setData(data_);
			response = SerializationObject.ObjectToString(reply);
		
		} catch (Exception e) {
			
			iso.setISO_039_ResponseCode("999");
			iso.setISO_039p_ResponseDetail(GeneralUtils.ExceptionToString("ERROR EN PROCESOS", e, false));
			response = SetDefaultResponseError(req, iso);
			log.WriteLogMonitor("Error Modulo " + this.getClass().getName() + "::AlexQueryMovements" , TypeMonitor.error, e);
		}
		return response;
		
	}
    
	public String AlexQueryConsolidateProduct(ContainerStruct struct){
		
		String response = null;
		Iso8583 iso = null;
		ExtremeRequest req = null;
		try {
			
			iso = struct.getIso();
			req = struct.getExtremeRequest();
			ExtremeReply reply = new ExtremeReply(req);
			reply.getHeader().setValueTag("RetCode", "0");	
			reply.setData(null);
			FxTables tables = new FxTables();
			ConsProducts productsObjetc = new ConsProducts();
			List<Registro> reg = new ArrayList<>();
			@SuppressWarnings("unchecked")
			List<Iterables> it = (List<Iterables>) SerializationObject.XMLToObject(iso.getISO_114_ExtendedData());
			@SuppressWarnings("unused")
			String producto = null,  cCuenta = null, cStatusCuenta = null, saldoMonedaCuenta = null, dProducto = null, cMoneda = null,
				   saldoMonedaOficial = null, tasaAnticipada = null, fVencimiento = null, cuotasPagadas = null,
				   totalCuotas = null, fVencimientoCuota = null, nombreLegal = null, dSucursal = null, dOficina = null,
				   tpr = null, canTrx = "0", cpProducto = null;
			
			DecimalFormatSymbols simbolo=new DecimalFormatSymbols();
		    simbolo.setDecimalSeparator('.');
			DecimalFormat df = new DecimalFormat("0.00",simbolo);	 
			
			Config config = new Config();
			   config = config.getConfigSystem("TRG_" + iso.getISO_003_ProcessingCode() + "_" +
					   						   iso.getISO_018_MerchantType() + "_"   +
					   						   iso.getISO_024_NetworkId());
			if(config == null){
				
				 iso.setISO_039_ResponseCode("999");
					iso.setISO_039p_ResponseDetail("ERROR, NO SE HA PODIDO RECUPERAR EL TIPO DE CUENTAS "
							+ "VISTA HABILITADAS PARA MANTENIMIENTO (CTAS. ACREDITANTES)");
				return response = SetDefaultResponseError(req, iso);
			}
			
			for (Iterables iterables : it) {
				
				for (int i = 0; i < iso.getISO_115_ExtendedData().split("\\,").length; i++) {
					
					producto = iterables.getIterarors().
							get(Arrays.asList(iso.getISO_115_ExtendedData().split("\\,")).get(0));
					dProducto = iterables.getIterarors().
							get(Arrays.asList(iso.getISO_115_ExtendedData().split("\\,")).get(1));
					cCuenta= iterables.getIterarors().
							get(Arrays.asList(iso.getISO_115_ExtendedData().split("\\,")).get(2));
					saldoMonedaOficial = df.format(Double.parseDouble(iterables.getIterarors().
							get(Arrays.asList(iso.getISO_115_ExtendedData().split("\\,")).get(3))));
					cMoneda = iterables.getIterarors().
							get(Arrays.asList(iso.getISO_115_ExtendedData().split("\\,")).get(4));
					saldoMonedaCuenta = df.format(Double.parseDouble(iterables.getIterarors().
							get(Arrays.asList(iso.getISO_115_ExtendedData().split("\\,")).get(5))));
					tasaAnticipada = df.format(Double.parseDouble(iterables.getIterarors().
							get(Arrays.asList(iso.getISO_115_ExtendedData().split("\\,")).get(6))));
					fVencimiento = iterables.getIterarors().
							get(Arrays.asList(iso.getISO_115_ExtendedData().split("\\,")).get(7));
					cuotasPagadas = iterables.getIterarors().
							get(Arrays.asList(iso.getISO_115_ExtendedData().split("\\,")).get(8));
					totalCuotas = iterables.getIterarors().
							get(Arrays.asList(iso.getISO_115_ExtendedData().split("\\,")).get(9));
					fVencimientoCuota = iterables.getIterarors().
							get(Arrays.asList(iso.getISO_115_ExtendedData().split("\\,")).get(10));
					nombreLegal = iterables.getIterarors().
							get(Arrays.asList(iso.getISO_115_ExtendedData().split("\\,")).get(11));
					dSucursal = iterables.getIterarors().
							get(Arrays.asList(iso.getISO_115_ExtendedData().split("\\,")).get(12));
					dOficina = iterables.getIterarors().
							get(Arrays.asList(iso.getISO_115_ExtendedData().split("\\,")).get(13));
					cpProducto = iterables.getIterarors().
							get(Arrays.asList(iso.getISO_115_ExtendedData().split("\\,")).get(14));
					
					if(!fVencimiento.equalsIgnoreCase("null"))	
						fVencimiento = FormatUtils.DateToString(fVencimiento, "yyyy-MM-dd HH:mm:ss.S", "yyyyMMdd");
					else
						fVencimiento = " ";
					
					if(!fVencimientoCuota.equalsIgnoreCase("null"))
						fVencimientoCuota = FormatUtils.DateToString(fVencimientoCuota, "yyyy-MM-dd HH:mm:ss.S", "yyyyMMdd");
					else
						fVencimientoCuota = " ";
					//ojo
					tpr =  cpProducto.substring(0,1);
					/*if(tpr.equals("4")){
						 canTrx = "1";  
					}  
					else if (tpr.equals("5")) 
						    canTrx = "0";
					else if (tpr.equals("6")) 
							canTrx = "0";
					else if (tpr.equals("3")) {
						canTrx = "0";
					}*/
					
					
					tpr = tpr.equals("3") ? "2" : tpr.equals("4") ? "2" : tpr.equals("5") ? "4" : tpr.equals("6") ? "5" : "2";	
				}
			   
				final String codProd = cpProducto;
				   /*if(Arrays.stream(config.getCfg_Valor().split("\\,"))
						   .anyMatch(x -> x.equalsIgnoreCase(codProd)))
					   canTrx = "1";
				   else
					   canTrx = "0";*/
				   String aux = Arrays.stream(config.getCfg_Valor().split("\\,"))
						   .filter(x -> x.contains(codProd))
						   .findFirst().orElseGet(() -> null);
				   if(!StringUtils.IsNullOrEmpty(aux)){
					   
					   switch (aux.substring(0,2)) {
						case "DC":
							 canTrx = "1";
							break;
						case "CC":
							 canTrx = "2";
							break;	
						case "DD":
							 canTrx = "3";
							break;
						case "XX":
							 canTrx = "0";
							break;
						default:
							canTrx = "4";
							break;
						}
				   }else{
					   
					   /*iso.setISO_039_ResponseCode("999");
					   iso.setISO_039p_ResponseDetail("ERROR, NO SE PUEDE RECUPERAR PERMISOS "
								+ "EN (CTAS. DEBITANTES/ACREDITANTES)");
						return response = SetDefaultResponseError(req, iso);*/
					   canTrx = "0";
				   }
					   
				reg.add(new Registro(iso.getISO_002_PAN(), cCuenta, tpr, 
						 dProducto, cCuenta, cMoneda.equalsIgnoreCase("USD")?"840":"000", 
						 cMoneda.equalsIgnoreCase("USD")?"840":"000", saldoMonedaOficial, fVencimiento, 
						 cuotasPagadas, totalCuotas, fVencimientoCuota, 
						 nombreLegal, dSucursal, canTrx, 
						 dOficina, cCuenta, saldoMonedaCuenta, tasaAnticipada));
			}
			productsObjetc.setRegistro(reg);
			tables.setConsolidadoProductos(productsObjetc);
			reply.setTables(tables);
			response = SerializationObject.ObjectToString(reply);
		
		} catch (Exception e) {
			
			iso.setISO_039_ResponseCode("999");
			iso.setISO_039p_ResponseDetail(GeneralUtils.ExceptionToString("ERROR EN PROCESOS", e, false));
			response = SetDefaultResponseError(req, iso);
			log.WriteLogMonitor("Error Modulo " + this.getClass().getName() + "::AlexQueryConsolidateProduct" , TypeMonitor.error, e);
		}
		return response.replace("CurrencyId=", "Moneda=")
				.replace("CuotasTotal", "CuotasTotales")
		        .replace("ProdNumber", "ProductNumber");
	}

    public String AlexQueryCreditDetail(ContainerStruct struct){
		
		String response = null;
		Iso8583 iso = null;
		ExtremeRequest req = null;
		try {
			
			iso = struct.getIso();
			req = struct.getExtremeRequest();
			ExtremeReply reply = new ExtremeReply(req);
			
			DecimalFormatSymbols simbolo=new DecimalFormatSymbols();
		    simbolo.setDecimalSeparator('.');
			DecimalFormat df = new DecimalFormat("0.00",simbolo);
			reply.getHeader().setValueTag("RetCode", "0");	
			reply.setData(null);
			String[] data = iso.getISO_114_ExtendedData().split("\\|");
			Data data_ = new Data();
			List<DField> fields = new ArrayList<>();
			fields.add(new DField("CurrentRate", StringUtils.IsNullOrEmpty(data[2])?"0.00":df.format(Double.parseDouble(data[2])), "f"));
			fields.add(new DField("FeesDue", StringUtils.IsNullOrEmpty(data[3])?"0":data[3], "i32"));
			fields.add(new DField("FeesDueInterest", StringUtils.IsNullOrEmpty(data[4])?"0.00":df.format(Double.parseDouble(data[4])), "f"));
			fields.add(new DField("FeesDueOthers", StringUtils.IsNullOrEmpty(data[5])?"0.00":df.format(Double.parseDouble(data[5])), "f"));
			fields.add(new DField("FeesDueOverdue", StringUtils.IsNullOrEmpty(data[6])?"0.00":df.format(Double.parseDouble(data[6])), "f"));
			fields.add(new DField("FeesDuePrincipal", StringUtils.IsNullOrEmpty(data[7])?"0.00":df.format(Double.parseDouble(data[7])), "f"));
			
			String sts = null;
			String statusP = StringUtils.IsNullOrEmpty(data[8])?"0":data[8];
			switch (statusP) {
				
			case "0":
			case "001":
			case "002":
			case "005":
				 sts = "0";
				break;
			case "003":
				sts = "1";
			    break;
			case "004":
			case "006":
				sts = "3";
			case "007":
				sts = "2";
			    break;
			default:
				break;
			}
			
			fields.add(new DField("LoanStatusId", sts, "i8"));
			fields.add(new DField("NextFee_DueDate", StringUtils.IsNullOrEmpty(data[9])?"null":
				       FormatUtils.DateToString(data[9],"yyyy-MM-dd HH:mm:ss.S","yyyMMdd"), "d"));
			fields.add(new DField("NextFee_FeeNum", StringUtils.IsNullOrEmpty(data[10])?"0":data[10], "i32"));
			fields.add(new DField("NextFeeInterest", StringUtils.IsNullOrEmpty(data[11])?"0.00":df.format(Double.parseDouble(data[11])), "f"));
			fields.add(new DField("NextFeeOthers", StringUtils.IsNullOrEmpty(data[12])?"0.00":df.format(Double.parseDouble(data[12])), "f"));
			fields.add(new DField("NextFee_OverdueAmount", StringUtils.IsNullOrEmpty(data[13])?"0.00":df.format(Double.parseDouble(data[13])), "f"));
			fields.add(new DField("NextFeePrincipal", StringUtils.IsNullOrEmpty(data[14])?"0.00":df.format(Double.parseDouble(data[14])), "f"));
			fields.add(new DField("OriginalAmount",  StringUtils.IsNullOrEmpty(data[15])?"0.00":df.format(Double.parseDouble(data[15])), "f"));
			fields.add(new DField("OverdueDays", StringUtils.IsNullOrEmpty(data[16])?"0":data[16], "i32"));
			fields.add(new DField("PaidFees", StringUtils.IsNullOrEmpty(data[17])?"0":data[17], "i32"));
			fields.add(new DField("PayOffBalance", StringUtils.IsNullOrEmpty(data[18])?"0.00":df.format(Double.parseDouble(data[18])), "f"));
			fields.add(new DField("PrePaymentAmount", StringUtils.IsNullOrEmpty(data[19])?"0.00":df.format(Double.parseDouble(data[19])), "f"));
			fields.add(new DField("ProductBankId", StringUtils.IsNullOrEmpty(data[0])?"":data[0], null));
			fields.add(new DField("Term", StringUtils.IsNullOrEmpty(data[20])?"0":data[20], "i32"));
			fields.add(new DField("retMsg", iso.getISO_039p_ResponseDetail(), null));
			
			List<HField> dField = new ArrayList<>();
			Header hdr = reply.getHeader();
			dField = hdr.getHfield();
			dField.add(new HField("AccountId1", iso.getISO_102_AccountID_1(), null));
			dField.add(new HField("AccountNum1", iso.getISO_102_AccountID_1(), null));
			dField.add(new HField("ValueTrn", StringUtils.IsNullOrEmpty(data[18])?"0.00":df.format(Double.parseDouble(data[18])), "f"));
			hdr.setHfield(dField);
			reply.setHeader(hdr);
			
			data_.setDfield(fields);
			reply.setData(data_);
			response = SerializationObject.ObjectToString(reply);
		
		} catch (Exception e) {
			
			iso.setISO_039_ResponseCode("999");
			iso.setISO_039p_ResponseDetail(GeneralUtils.ExceptionToString("ERROR EN PROCESOS", e, false));
			response = SetDefaultResponseError(req, iso);
			log.WriteLogMonitor("Error Modulo " + this.getClass().getName() + "::AlexQueryCreditDetail" , TypeMonitor.error, e);
		}
		return response;
	}
    
    public String AlexQueryThirdProducts(ContainerStruct struct){
		
		String response = null;
		Iso8583 iso = null;
		ExtremeRequest req = null;
		try {
			
			iso = struct.getIso();
			req = struct.getExtremeRequest();
			ExtremeReply reply = new ExtremeReply(req);
			
			reply.getHeader().setValueTag("RetCode", "0");	
			reply.setData(null);
			String[] data = iso.getISO_114_ExtendedData().split("\\|");
			Data data_ = new Data();
			List<DField> fields = new ArrayList<>();
			fields.add(new DField("ClientsId", StringUtils.IsNullOrEmpty(data[0])?" ":data[0], null));
			fields.add(new DField("TPProductNumber", StringUtils.IsNullOrEmpty(data[1])?" ":data[1], null));
			fields.add(new DField("ProductAlias", StringUtils.IsNullOrEmpty(data[3])?" ":data[3], null));
			fields.add(new DField("CurrencyIdInt", StringUtils.IsNullOrEmpty(data[6])?" ":data[6]
					   .equalsIgnoreCase("USD")?"840":data[6], null));
			fields.add(new DField("IdTransactionSub", "1", "i32"));
			/*Ojo aqui poner en el futuro una tabla de configuracion para otorgar permisos a las cuentas (internas, externas)*/
			fields.add(new DField("ThirdPartyProdType", "1", "i32"));
			String ttp = null;
			String tipoProd = data[2].substring(0, 1);
			if(tipoProd.equals("4") || tipoProd.equals("3")){
				ttp = "2";
			}else if (tipoProd.equals("5")) {
				ttp = "4";
			}else if (tipoProd.equals("6")) {
				ttp = "5";
			}
			fields.add(new DField("ProductTypeId",ttp, "i32"));
			fields.add(new DField("OwnerName", StringUtils.IsNullOrEmpty(data[7])?" ":data[7], null));
			fields.add(new DField("OwnerCountryId", StringUtils.IsNullOrEmpty(data[8])?" ":data[8], null));
			fields.add(new DField("OwnerEmail", StringUtils.IsNullOrEmpty(data[9])?" ":data[9], null));
			fields.add(new DField("OwnerCity", StringUtils.IsNullOrEmpty(data[10])?" ":data[10], null));
			fields.add(new DField("OwnerAddress", StringUtils.IsNullOrEmpty(data[12])?" ":data[12], null));
			fields.add(new DField("ProdOwnerDocId", StringUtils.IsNullOrEmpty(data[4])?" ":data[4], null));
			fields.add(new DField("ProdOwnerDocType", StringUtils.IsNullOrEmpty(data[5])?"0"
					   :data[5].equals("CED")?"3":data.equals("PAS")?"10":data.equals("RUC")?"1":"1", "i32"));
			fields.add(new DField("OwnerPhoneNumber", StringUtils.IsNullOrEmpty(data[11])?" ":data[11], null));
			fields.add(new DField("ProdUserDocId", StringUtils.IsNullOrEmpty(data[4])?" ":data[4], null));
			fields.add(new DField("ProdUserDocType", StringUtils.IsNullOrEmpty(data[5])?" "
					   :data[5].equals("CED")?"3":data.equals("PAS")?"10":data.equals("RUC")?"1":"1", null));
			fields.add(new DField("BankId", "1", "i32"));
			fields.add(new DField("BankCountryId", "0", null));
			fields.add(new DField("BankDescription", " ", null));
			fields.add(new DField("BankRoutingCode", " ", null));
			fields.add(new DField("BankHeadquarters", " ", null));
			
			fields.add(new DField("RetMsg", iso.getISO_039p_ResponseDetail(), null));
			data_.setDfield(fields);
			reply.setData(data_);
			response = SerializationObject.ObjectToString(reply);
		
		} catch (Exception e) {
			
			iso.setISO_039_ResponseCode("999");
			iso.setISO_039p_ResponseDetail(GeneralUtils.ExceptionToString("ERROR EN PROCESOS", e, false));
			response = SetDefaultResponseError(req, iso);
			log.WriteLogMonitor("Error Modulo " + this.getClass().getName() + "::AlexQueryThirdProducts" , TypeMonitor.error, e);
		}
		return response;
	}
	
	public String SetDefaultResponseError(ExtremeRequest req, Iso8583 iso){
		
		String data = iso.getISO_039p_ResponseDetail();
		try {
			
			ExtremeReply reply = new ExtremeReply(req);
			reply.getHeader().setValueTag("RetCode", HomologaErrores(String.valueOf(Integer.parseInt(iso.getISO_039_ResponseCode())),iso));
			List<DField> dfield = reply.getData().getDfield();
			dfield = new ArrayList<>();
			dfield.add(new DField("RetMsg", iso.getISO_039p_ResponseDetail()));
			reply.getData().setDfield(dfield);
			data = SerializationObject.ObjectToString(reply);
			
		} catch (Exception e) {
			
			e.printStackTrace();
		}
		return data;
	}
	
	public Object SetDefaultResponseError(ExtremeRequest req, AlexParser parser){
		
		String xml = StringUtils.Empty();
		String backEnd = StringUtils.Empty();
		backEnd = req.getData().getValueTag("TransactionId");
		try {
			
			ExtremeReply reply = new ExtremeReply(req);
			reply.getHeader().setValueTag("RetCode", parser.getCodError().equals("000")?"0":parser.getCodError());
			List<DField> dfield = reply.getData().getDfield();
			dfield = new ArrayList<>();
			dfield.add(new DField("IsError", parser.getCodError().equals("000")?"false":"true"));
			dfield.add(new DField("RetMsg", parser.getDesError()));
			String secu4 = StringUtils.IsNullOrEmpty(backEnd)?GeneralUtils.GetSecuencialNumeric(4):backEnd;
			dfield.add(new DField("BackendReference",secu4));
			dfield.add(new DField("BackendCode",parser.getCodError().equals("000")?"0":"906"));
			dfield.add(new DField("TransactionId",secu4));
			reply.getData().setDfield(dfield);
			
			if(reply != null) {
				
				xml = SerializationObject.ObjectToString(reply, ExtremeReply.class);
				log.WriteLog(xml, TypeLog.alexsoft, TypeWriteLog.file);
			}
			
			return reply;
			
		} catch (Exception e) {
			e.printStackTrace();
			return e;
		}
	}
	
	private String SetDefaultResponseErrorString(ExtremeRequest req, AlexParser parser){
		
		String xml = StringUtils.Empty();
		try {
			
			ExtremeReply reply = new ExtremeReply(req);
			reply.getHeader().setValueTag("RetCode", parser.getCodError().equals("000")?"0":parser.getCodError());
			List<DField> dfield = reply.getData().getDfield();
			dfield = new ArrayList<>();
			dfield.add(new DField("IsError", parser.getCodError().equals("000")?"false":"true"));
			dfield.add(new DField("RetMsg", parser.getDesError()));
			String secu4 = GeneralUtils.GetSecuencialNumeric(4);
			dfield.add(new DField("BackendReference",secu4));
			dfield.add(new DField("BackendCode",parser.getCodError().equals("000")?"0":"906"));
			dfield.add(new DField("TransactionId",secu4));
			reply.getData().setDfield(dfield);
			
			if(reply != null) {
				
				xml = SerializationObject.ObjectToString(reply, ExtremeReply.class);
				
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return xml;
	}
	
	private static String HomologaErrores(String codError, Iso8583 iso){
		
		String codeErrorReturn = "96";
		if(codError.length() < 2)
			return codError;
		try {
			
			switch (codError) {
			case "123": //Monto invalido
					codeErrorReturn = "13";
				break;
			case "078":
					codeErrorReturn = "12";
			case "120":
			case "216":
			case "215":
			case "220":
					codeErrorReturn = "7";
				break;
			case "214":
				    codeErrorReturn = "1";
			break;
			case "116":
					codeErrorReturn = "51";
				break;
			case "118":
				codeErrorReturn = "1";
			break;
			case "219":
					codeErrorReturn = "14"; //Cuenta no existe
				break;
			case "308":
					codeErrorReturn = "96";
				break;
			case "905": //Validaciones FitSwitch
					codeErrorReturn = "21";
				break;
			case "907": //Core no responde, TIMEOUT
					codeErrorReturn = "8908";
				break;
			default:
				if(codError.startsWith("9"))
					codeErrorReturn = "96";
				break;
			}
			
		} catch (Exception e) {
			
			e.printStackTrace();
		}
		return codeErrorReturn;
	}
	
	public wIso8583 getTarifarioFinancoop(wIso8583 iso){
		
		IsoRetrievalTransaction sql = null;
		try {
			
			wIso8583 isoClone = iso.cloneWiso(iso);
			isoClone.setISO_BitMap("ONLY_FEATURE");
				sql = new IsoRetrievalTransaction();
			iso.getTickAut().reset();
			iso.getTickAut().start();
				isoClone = sql.RetrieveCOST_FINANCOOP(isoClone);
			if(iso.getTickAut().isStarted())
				iso.getTickAut().stop();
			
			if(isoClone.getISO_039_ResponseCode().equals("000")){
				
				iso.setISO_006_BillAmount(isoClone.getISO_006_BillAmount());
				iso.setISO_039_ResponseCode("000");
				iso.setISO_039p_ResponseDetail("TRANSACCION EXITOSA");
				
			}else{
				
				iso.setISO_039_ResponseCode(isoClone.getISO_039_ResponseCode());
				iso.setISO_039p_ResponseDetail(isoClone.getISO_039p_ResponseDetail());
				iso.setWsIso_LogStatus(isoClone.getWsIso_LogStatus());
			}
		} catch (Exception e) {
			
			if(iso.getTickAut().isStarted())
				iso.getTickAut().stop();
			iso.setISO_039_ResponseCode("999");
			iso.setISO_039p_ResponseDetail(GeneralUtils.ExceptionToString("ERROR EN PROCESOS", e, false));
			log.WriteLogMonitor("Error Modulo " + this.getClass().getName() + "::getTarifarioFinancoop" , TypeMonitor.error, e);
		}
		return iso;
	}
	
  }
    class ContainerStruct{
	
	public ContainerStruct(){
		
	}
	private Iso8583 iso;
	private ExtremeRequest extremeRequest;
	public Iso8583 getIso() {
		return iso;
	}
	public void setIso(Iso8583 iso) {
		this.iso = iso;
	}
	public ExtremeRequest getExtremeRequest() {
		return extremeRequest;
	}
	public void setExtremeRequest(ExtremeRequest extremeRequest) {
		this.extremeRequest = extremeRequest;
	}
}
