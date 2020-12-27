package com.belejanor.switcher.parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import com.belejanor.switcher.asextreme.ExtremeRequest;
import com.belejanor.switcher.asextreme.Registro;
import com.belejanor.switcher.cscoreswitch.Iso8583;
import com.belejanor.switcher.cscoreswitch.wIso8583;
import com.belejanor.switcher.logger.Logger;
import com.belejanor.switcher.logger.LoggerConfig.TypeMonitor;
import com.belejanor.switcher.memcached.AdditionalIsoValues;
import com.belejanor.switcher.memcached.Config;
import com.belejanor.switcher.sqlservices.IsoRetrievalTransaction;
import com.belejanor.switcher.utils.FormatUtils;
import com.belejanor.switcher.utils.GeneralUtils;
import com.belejanor.switcher.utils.NumbersUtils;
import com.belejanor.switcher.utils.Ref;
import com.belejanor.switcher.utils.SerializationObject;
import com.belejanor.switcher.utils.StringUtils;


public class AlexParser {
	
	private Logger log;
	private String codError;
	private String desError;
	private double acumuladorValores;
	
	public String getCodError() {
		return codError;
	}

	public void setCodError(String codError) {
		this.codError = codError;
	}

	public String getDesError() {
		return desError;
	}

	public void setDesError(String desError) {
		this.desError = desError;
	}
	
	public double getAcumuladorValores() {
		return acumuladorValores;
	}

	public AlexParser(){
		
		this.codError = "999";
		this.desError = StringUtils.Empty();
		log = new Logger();
	}

	public List<Iso8583> ExtremeMessageToIsoList(ExtremeRequest message){
		
		List<Iso8583> isoList = new ArrayList<>();
		Iso8583 iso = null;
		double acumValores = 0;
		int counter = 0;
		String CuentaOrigen = StringUtils.Empty();
		try {
			
			List<Registro> movements = message.getTables().getTransferencias().getRegistro();
			if(movements.size() >= 1) {
				
				String trnCode = message.getHeader().getValueTag("TrnCode");
				Config conf = new Config();
				conf = conf.getConfigSystem(trnCode);
				
				if(conf != null) {
					
					String Item = StringUtils.Empty();
					for (Registro reg : movements) {
					
						counter++;
					
						//////////////////////////////////////////////////////////////////////////////////////////////////////////
						iso = new Iso8583();
						if(message.getHeader().getValueTag("MsgType").equals("1") && 
								   message.getHeader().getValueTag("Reverse").equals("0")){
								iso.setISO_000_Message_Type("1200");
								iso.setISO_037_RetrievalReferenceNumber(getValueHeaderTag(message,"TraceId"));
						}else{
								iso.setISO_000_Message_Type("1400");
								iso.setISO_037_RetrievalReferenceNumber((String) getValueDataTag(message,"TraceIdOri"));
						}
						iso.setISO_030_ProcFee((Double.parseDouble(getValueHeaderTag(message,"ValueTrn"))));
						iso.setISO_003_ProcessingCode(Arrays.asList(conf.getCfg_Valor().split("-")).get(0));
						iso.setISO_BitMap(Arrays.asList(Arrays.asList(conf.getCfg_Valor().split("-"))
								   .get(1).split("\\|")).get(0));
						iso.setISO_024_NetworkId("555541");
						iso.setISO_018_MerchantType(StringUtils.padLeft(getValueHeaderTag(message,"ChannelId"), 4, "0"));
						iso.setISO_015_SettlementDatel(FormatUtils.StringToDate(getValueHeaderTag(message,"BussDate")
			                    , "yyyyMMdd"));
						String fecha = getValueHeaderTag(message,"RealDate");
						iso.setISO_007_TransDatetime(FormatUtils.StringToDateTrim(fecha
										                    , "yyyyMMddHH:mm:ss.SSS"));
						int fechaHoy = Integer.parseInt(FormatUtils.DateToString(new Date(), "yyyyMMdd"));
						int fecha007 = Integer.parseInt(FormatUtils.DateToString(iso.getISO_007_TransDatetime(), "yyyyMMdd"));
						if(fecha007 > fechaHoy || fecha007 < fechaHoy){
							
							this.codError = "21";
							this.desError = "FECHA REALDATE ES INVALIDA";
							return null;
						}
						
						iso.setISO_041_CardAcceptorID(getValueHeaderTag(message,"TerminalId"));
						iso.setISO_011_SysAuditNumber(getValueHeaderTag(message,"TraceId"));
						iso.setISO_043_CardAcceptorLoc(getValueHeaderTag(message,"TerminalSeq"));
						
						String TransactionId = (String) getValueDataTag(message, "TransactionId");
						iso.setISO_019_AcqCountryCode(TransactionId);
						iso.setISO_042_Card_Acc_ID_Code((String) getValueDataTag(message, "ChannelName"));
						/////////////////////////////////////////////////////////////////////////////////////////////////////////
						
						
						if(!StringUtils.IsNullOrEmpty(reg.getTransactionItemId())) {
							
							iso.setISO_011_SysAuditNumber(iso.getISO_011_SysAuditNumber() + "_" 
									+ TransactionId + "_" + reg.getTransactionItemId());
							Item = reg.getTransactionItemId();
							iso.setISO_052_PinBlock(Item);
							
						}else {
							
							this.codError = "21";
							this.desError = "CAMPO TRANSACTIONITEM ES NULO O VACIO";
							return null;
						}
						
						if(!StringUtils.IsNullOrEmpty(reg.getSubTransactionTypeId())){
							
							String regex = "(?:\\b|-)([0-9]{0,1}?|12)\\b";
							if(Pattern.matches(regex, reg.getSubTransactionTypeId())) {
								
								iso.setISO_090_OriginalData(reg.getSubTransactionTypeId());
								conf = conf.getConfigSystem("SubTransactionTypeId");
								if(conf != null) {
									
									if(conf.getCfg_Valor().indexOf(reg.getSubTransactionTypeId().trim()) == -1) {
										
										this.codError = "96";
										this.desError = "SUB TIPO DE TRANSACCION (SubTransactionTypeId): " + 
										reg.getSubTransactionTypeId() + " NO PERMITIDO";
										return null;
									}
									
								}else {
									
									this.codError = "96";
									this.desError = "NO SE PUDO RECUPERAR SUB TIPOS DE TRANSACCIONES PERMITIDAS (CONFIG)";
									return null;
								}
								
							}else {
								
								this.codError = "21";
								this.desError = "CAMPO SUBTRANSACTIONTYPEID ES INVALIDO, SOLO VALORES {0-12}, Item: " + Item;
								return null;
							}
							
						}else {
							
							this.codError = "21";
							this.desError = "CAMPO SUBTRANSACTIONTYPEID ES NULO O VACIO, Item: " + Item;
							return null;
						}
						
						if(!StringUtils.IsNullOrEmpty(reg.getTransactionFeatureId())){
							
							iso.setISO_090_OriginalData(iso.getISO_090_OriginalData() + 
									"|" + reg.getTransactionFeatureId());
						}
						
						if(!StringUtils.IsNullOrEmpty(reg.getCurrencyId())){
							
							iso.setISO_049_TranCurrCode(Double.parseDouble(reg.getCurrencyId()));
							
						}else {
							
							this.codError = "21";
							this.desError = "CAMPO GETCURRENCYID ES NULO O VACIO, Item:" + Item;
							return null;
						}
						
						if(!StringUtils.IsNullOrEmpty(reg.getValueDate())){
							
							iso.setISO_012_LocalDatetime(FormatUtils.StringToDate(reg.getValueDate()
		                    , "MM/dd/yyyy hh:mm:ss a"));
							//iso.setISO_012_LocalDatetime(FormatUtils.StringToDate(reg.getValueDate()
				             //       , "yyyyMMdd"));
							
							int fecha012 = Integer.parseInt(FormatUtils.DateToString(iso.getISO_012_LocalDatetime(), "yyyyMMdd"));
							if(fecha012 > fechaHoy || fecha012 < fechaHoy){
								
								this.codError = "21";
								this.desError = "FECHA VALUEDATE ES INVALIDA, Item: " + Item;
								return null;
							}
							
						}else {
							
							this.codError = "21";
							this.desError = "CAMPO VALUEDATE ES NULO O VACIO, Item:" + Item;
							return null;
						}
						
						if(!StringUtils.IsNullOrEmpty(reg.getTransactionTypeId())){
							
							String regex = "(?:\\b|-)([1-9]{1,2}?|9)\\b";
							if(Pattern.matches(regex, reg.getTransactionTypeId())) {
								
								iso.setISO_090_OriginalData(iso.getISO_090_OriginalData() + 
										"|" + reg.getTransactionTypeId());
								
								conf = conf.getConfigSystem("TransactionTypeId");
								if(conf != null) {
									
									if(conf.getCfg_Valor().indexOf(reg.getTransactionTypeId()) == -1) {
										
										this.codError = "96";
										this.desError = "TIPO DE TRANSACCION (TransactionTypeId): " + 
												reg.getTransactionTypeId() + " NO PERMITIDO";
										return null;
									}
									
								}else {
									
									this.codError = "96";
									this.desError = "NO SE PUDO RECUPERAR TIPOS DE TRANSACCIONES PERMITIDAS, (TransactionTypeId)  (CONFIG)";
									return null;
								}
								
								
							}else {
								
								this.codError = "21";
								this.desError = "CAMPO TRANSACTIONTYPEID ES INVALIDO, SOLO VALORES {1-9}, Item:" + Item;
								return null;
							}
							
						}else {
							
							this.codError = "21";
							this.desError = "CAMPO TRANSACTIONTYPEID ES NULO O VACIO, Item:" + Item;
							return null;
						}
						
						if(!StringUtils.IsNullOrEmpty(reg.getTransactionStatusId())){
							
							String regex = "(?:\\b|-)([1-7]{1,2}?|7)\\b";
							if(Pattern.matches(regex, reg.getTransactionTypeId())) {
								
								iso.setISO_104_TranDescription(reg.getTransactionStatusId());
								
							}else {
								
								this.codError = "21";
								this.desError = "CAMPO TRANSACTIONSTATUSID ES INVALIDO, SOLO VALORES {1-7}, Item:" + Item;
								return null;
							}
							
						}else {
							
							this.codError = "21";
							this.desError = "CAMPO TRANSACTIONSTATUSID ES NULO O VACIO, Item:" + Item;
							return null;
						}
						
						if(!StringUtils.IsNullOrEmpty(reg.getClientBankId())){
							
							iso.setISO_023_CardSeq(reg.getClientBankId());
						}
						
						if(!StringUtils.IsNullOrEmpty(reg.getDebitProductBankId())){
							
							iso.setISO_102_AccountID_1(reg.getDebitProductBankId());
							if(counter > 1) {
								if(!CuentaOrigen.equalsIgnoreCase(iso.getISO_102_AccountID_1())) {
								
									this.codError = "21";
									this.desError = "CAMPO DEBITPRODUCTBANKID (CUENTA ORIGEN"
											+ ") DEBE SER LA MISMA EN TODOS LOS ITEMS";
									return null;
								}
							}
							CuentaOrigen = iso.getISO_102_AccountID_1();
							
						}else {
							
							this.codError = "21";
							this.desError = "CAMPO DEBITPRODUCTBANKID ES NULO O VACIO, Item:" + Item;
							return null;
						}
						
						if(!StringUtils.IsNullOrEmpty(reg.getDebitProductTypeId())){
							
							String regex = "(?:\\b|-)([1-7]{1,2}?|7)\\b";
							if(Pattern.matches(regex, reg.getDebitProductTypeId())) {
								
								iso.setISO_022_PosEntryMode(reg.getDebitProductTypeId());
								
							}else {
								
								this.codError = "21";
								this.desError = "CAMPO CREDITPRODUCTID ES INVALIDO, SOLO VALORES {1-7}, Item:" + Item;
								return null;
							}
							
						}else {
							
							this.codError = "21";
							this.desError = "CAMPO CREDITPRODUCTID ES NULO O VACIO, Item:" + Item;
							return null;
						}
						
						
						if(!StringUtils.IsNullOrEmpty(reg.getCreditProductTypeId())){
							
							String regex = "(?:\\b|-)([1-7]{1,2}?|7)\\b";
							if(Pattern.matches(regex, reg.getCreditProductTypeId())) {
								
								iso.setISO_022_PosEntryMode(iso.getISO_022_PosEntryMode() + "|" 
													+ reg.getCreditProductTypeId());
								
							}else {
								
								this.codError = "21";
								this.desError = "CAMPO CREDITPRODUCTID ES INVALIDO, SOLO VALORES {1-7}, Item:" + Item;
								return null;
							}
							
							
							
						}else {
							
							this.codError = "21";
							this.desError = "CAMPO CREDITPRODUCTID ES NULO O VACIO, Item:" + Item;
							return null;
						}
						
						if(!StringUtils.IsNullOrEmpty(reg.getCreditProductBankId())){
							
							iso.setISO_103_AccountID_2(reg.getCreditProductBankId());
							
						}else {
							
							this.codError = "21";
							this.desError = "CAMPO CREDITTPRODUCTBANKID ES NULO O VACIO, Item:" + Item;
							return null;
						}
						
						if(!StringUtils.IsNullOrEmpty(reg.getAmount())){
							
							iso.setISO_004_AmountTransaction(Double.parseDouble(reg.getAmount()));
							
							if(iso.getISO_004_AmountTransaction() <= 0) {
								
								this.codError = "21";
								this.desError = "CAMPO AMOUNT NO PUEDE SER IGUAL O MENOR A 0, Item:" + Item;
								return null;
								
							}else{
								
								acumValores += iso.getISO_004_AmountTransaction();
							}
							
						}else {
							
							this.codError = "21";
							this.desError = "CAMPO AMOUNT ES NULO O VACIO, Item:" + Item;
							return null;
						}
						
						if(!StringUtils.IsNullOrEmpty(reg.getNotifyTo())) {
							
							iso.setISO_120_ExtendedData(reg.getNotifyTo());
							
						}else {
							
							iso.setISO_120_ExtendedData("|");
						}
						
						if(!StringUtils.IsNullOrEmpty(reg.getNotificationChannelId())){
							
							iso.setISO_120_ExtendedData(iso.getISO_120_ExtendedData() + "|" 
												+ reg.getNotificationChannelId());
							
						}else {
							
							reg.setNotificationChannelId("||");
						}
						
						if(!StringUtils.IsNullOrEmpty(reg.getDestinationDocumentId())){
							
							iso.setISO_121_ExtendedData(reg.getDestinationDocumentId());
							
						}else {
							
							this.codError = "21";
							this.desError = "CAMPO DESTINATIONDOCUMENTID ES NULO O VACIO, Item:" + Item;
							return null;
						}
						
						if(!StringUtils.IsNullOrEmpty(reg.getDestinationName())){
							
							iso.setISO_121_ExtendedData(iso.getISO_121_ExtendedData() + "|" + reg.getDestinationName());
							
						}else {
							
							this.codError = "21";
							this.desError = "CAMPO DESTINATIONDOCUMENTID ES NULO O VACIO, Item:" + Item;
							return null;
						}
						
						if(!StringUtils.IsNullOrEmpty(reg.getDestinationBank())){
							
							iso.setISO_121_ExtendedData(iso.getISO_121_ExtendedData() + "|" + reg.getDestinationBank());
						}
						
						if(!StringUtils.IsNullOrEmpty(reg.getDescription())){
							
							iso.setISO_034_PANExt(reg.getDescription());
							
						}else {
							
							this.codError = "21";
							this.desError = "CAMPO DESCRIPCION ES NULO O VACIO, Item:" + Item;
							return null;
						}
						
						if(!StringUtils.IsNullOrEmpty(reg.getBankRoutingNumber())){
							
							iso.setISO_033_FWDInsID(reg.getBankRoutingNumber()); 
						}
						
						if(!StringUtils.IsNullOrEmpty(reg.getSourceDocumentId())){
							
							iso.setISO_122_ExtendedData(reg.getSourceDocumentId());
							
						}else {
							
							this.codError = "21";
							this.desError = "CAMPO SOURCEDOCUMENTID ES NULO O VACIO, Item:" + Item;
							return null;
						}
						
						if(!StringUtils.IsNullOrEmpty(reg.getSourceName())){
							
							iso.setISO_122_ExtendedData(iso.getISO_122_ExtendedData() + "|" 
													+ reg.getSourceName());
							
						}else {
							
							this.codError = "21";
							this.desError = "CAMPO SOURCEDOCUMENTID ES NULO O VACIO, Item:" + Item;
							return null;
						}
						
						if(!StringUtils.IsNullOrEmpty(reg.getSourceBank())){
							
							iso.setISO_122_ExtendedData(iso.getISO_122_ExtendedData() + "|" 
													+ reg.getSourceBank());
						}
							
						if(!StringUtils.IsNullOrEmpty(reg.getRegulationAmountExceeded())){
							
							iso.setISO_035_Track2(reg.getRegulationAmountExceeded());
							
						}else {
							
							this.codError = "21";
							this.desError = "CAMPO REGULATIONAMOUNTEXCEEDED ES NULO O VACIO, Item:" + Item;
							return null;
						}
						
						if(!StringUtils.IsNullOrEmpty(reg.getSourceFunds())){
							
							iso.setISO_036_Track3(reg.getSourceFunds());
							
						}
						
						if(!StringUtils.IsNullOrEmpty(reg.getDestinationFunds())){
							
							iso.setISO_036_Track3(iso.getISO_036_Track3() + "|" + 
												  reg.getDestinationFunds());
							
						}
						
						if(!StringUtils.IsNullOrEmpty(reg.getUserDocumentId())){
							
							iso.setISO_002_PAN(reg.getUserDocumentId());
							
						}else {
							
							this.codError = "21";
							this.desError = "CAMPO USERDOCUMENTID ES NULO O VACIO, Item:" + Item;
							return null;
						}
						
						if(!StringUtils.IsNullOrEmpty(reg.getTransactionCost())){
							
							iso.setISO_006_BillAmount(Double.parseDouble(reg.getTransactionCost()));
							
						}else {
							
							this.codError = "21";
							this.desError = "CAMPO TRANSACTIONCOST ES NULO O VACIO, Item:" + Item;
							return null;
						}
						
						if(!StringUtils.IsNullOrEmpty(reg.getTransactionCostCurrencyId())){
							
							iso.setISO_051_CardCurrCode(Double.parseDouble(reg.getTransactionCost()));
						}
					
						
						if(!StringUtils.IsNullOrEmpty(reg.getExchangeRate())){
							
							iso.setISO_008_BillFeeAmount(Double.parseDouble(reg.getExchangeRate()));
							
						}else {
							
							this.codError = "21";
							this.desError = "CAMPO EXCHANGERATE ES NULO O VACIO, Item:" + Item;
							return null;
						}
						
						if(!StringUtils.IsNullOrEmpty(reg.getIsValid())){
							
							//if(reg.getIsValid().equalsIgnoreCase("TRUE")) {
								
								iso.setISO_123_ExtendedData(reg.getIsValid());
							
						}else {
							
							this.codError = "21";
							this.desError = "CAMPO ISVALID ES NULO O VACIO, Item:" + Item;
							return null;
						}
						
						if(!StringUtils.IsNullOrEmpty(reg.getValidationMessage())){
							
							iso.setISO_124_ExtendedData(reg.getValidationMessage());
						}
						
						isoList.add(iso);
						
						conf = new Config();
						conf = conf.getConfigSystem(trnCode);
						
					}//Fin for
					
					
					this.acumuladorValores = NumbersUtils.truncateDecimal(acumValores,2);
					this.codError = "000";
					this.desError = "TRANSACCION EXITOSA";
					
				}else {
					
					this.codError = "909";
					this.desError = "NO SE ENCUENTRA LA CONFIGURACION PARA EL TRNCODE: " + trnCode;
				}
				
			}else {
				
				this.codError = "116";
				this.desError = "NO SE ENCUENTRAN REGISTROS A PROCESAR";
			}
			
		} catch (Exception e) {
			
			this.codError = "909";
			this.desError =  GeneralUtils.ExceptionToString(null, e, false);
			log.WriteLogMonitor("Error Modulo " + this.getClass().getName() + "::ExtremeMessageToIsoList" , TypeMonitor.error, e);
		}
		return isoList;
	}
	
	@SuppressWarnings("static-access")
	public Iso8583 ExtremeMessageToIsoHeader(ExtremeRequest message){
		
		Iso8583 iso = new Iso8583();
		try {
			
			Config conf = new Config();
			conf = conf.getConfigSystem(message.getHeader().getValueTag("TrnCode"));
			if(conf != null){
				
				if(message.getHeader().getValueTag("MsgType").equals("1") && 
				   message.getHeader().getValueTag("Reverse").equals("0")){
					iso.setISO_000_Message_Type("1200");
					iso.setISO_037_RetrievalReferenceNumber(getValueHeaderTag(message,"TraceId"));
				}else{
					iso.setISO_000_Message_Type("1400");
					iso.setISO_037_RetrievalReferenceNumber((String) getValueDataTag(message,"TraceIdOri"));
				}
				
				iso.setISO_003_ProcessingCode(Arrays.asList(conf.getCfg_Valor().split("-")).get(0));
				iso.setISO_BitMap(Arrays.asList(Arrays.asList(conf.getCfg_Valor().split("-"))
						   .get(1).split("\\|")).get(0));
				iso.setISO_024_NetworkId("555541");
				iso.setISO_004_AmountTransaction(Double.parseDouble(StringUtils.IsNullOrEmpty(getValueHeaderTag(message,"ValueTrn"))?"0":
					getValueHeaderTag(message,"ValueTrn")));
				iso.setISO_032_ACQInsID(getValueHeaderTag(message,"EntityId"));
				iso.setISO_033_FWDInsID(getValueHeaderTag(message,"EntityIdDest"));
				iso.setISO_018_MerchantType(StringUtils.padLeft(getValueHeaderTag(message,"ChannelId"), 4, "0"));
				//iso.setISO_007_TransDatetime(new Date());
				String fecha = getValueHeaderTag(message,"RealDate");
				//iso.setISO_012_LocalDatetime(FormatUtils.StringToDateTrim(fecha
				//		                    , "yyyyMMddHH:mm:ss.SSS"));
				
				iso.setISO_007_TransDatetime(FormatUtils.StringToDateTrim(fecha
								                    , "yyyyMMddHH:mm:ss.SSS"));
				iso.setISO_012_LocalDatetime(new Date());
				iso.setISO_041_CardAcceptorID(getValueHeaderTag(message,"TerminalId"));
				iso.setISO_011_SysAuditNumber(getValueHeaderTag(message,"TraceId"));
				iso.setISO_043_CardAcceptorLoc(getValueHeaderTag(message,"TerminalSeq"));
				iso.setISO_034_PANExt(getValueHeaderTag(message,"UserId"));
				iso.setISO_042_Card_Acc_ID_Code(getValueHeaderTag(message,"OfficeId"));
				iso.setISO_015_SettlementDatel(FormatUtils.StringToDate(getValueHeaderTag(message,"BussDate")
						                    , "yyyyMMdd"));
				iso.setISO_036_Track3(getValueHeaderTag(message,"SubTrnCode"));
				iso.setISO_115_ExtendedData(Arrays.asList(Arrays.asList(conf.getCfg_Valor().split("-"))
						   		.get(1).split("\\|")).get(1));
				iso.setISO_102_AccountID_1(getValueHeaderTag(message,"AccountNum1"));
				iso.setISO_103_AccountID_2(StringUtils.IsNullOrEmpty(getValueHeaderTag(message,"AccountNum2"))?getValueHeaderTag(message,"CardNumber")
						:getValueHeaderTag(message,"AccountNum2"));
				/*Obtencion de campos Iso del Data (dinamico)*/
				iso = addIsoAdditionalRowsAlex(new Ref<Iso8583>(iso), message);
				if(iso.getISO_003_ProcessingCode().equals("323009")){ //Consulta cuotas prestamos
					
					switch (iso.getISO_055_EMV()) {
					case "2":
						iso.setISO_055_EMV("1");
						break;
					case "0":
						iso.setISO_055_EMV("0");
						break;
					case "1":
						iso.setISO_055_EMV("3");
						break;
					default:
						iso.setISO_055_EMV("0");
						break;
					}
				}
				
				if(iso.getISO_003_ProcessingCode().equals("011040")){//Pago de Comercios
					
					IsoRetrievalTransaction sql = new IsoRetrievalTransaction();
					wIso8583 wiso = new wIso8583(iso, "127.0.0.1");
					wiso = sql.RetrieveCommerceSQL(wiso);
					if(wiso.getISO_039_ResponseCode().equals("000")){
						
						iso.setISO_023_CardSeq(wiso.getISO_023_CardSeq());
						iso.setISO_103_AccountID_2(wiso.getISO_103_AccountID_2());
						iso.setISO_022_PosEntryMode(wiso.getISO_022_PosEntryMode());
						
					}else{
					
						iso.setISO_039_ResponseCode(wiso.getISO_039_ResponseCode());
						iso.setISO_039p_ResponseDetail(wiso.getISO_039p_ResponseDetail());
					}
				}
				
				if(iso.getISO_003_ProcessingCode().startsWith("811")){ //SPI
					
					String dateCont = FormatUtils.DateToString(iso.getISO_015_SettlementDatel(), "yyyy-MM-dd") 
							          + " 00:00:00";
					iso.setISO_015_SettlementDatel(FormatUtils.StringToDate(dateCont,"yyyy-MM-dd HH:mm:ss"));
					/*Nuevos Campos Diana*/
					double cost = Double.parseDouble((String) getValueDataTag(message,"TranCost"));
					iso.setISO_006_BillAmount(cost);
					double currCodeCost = Double.parseDouble((String) getValueDataTag(message,"TransCostCurrencyId"));
					iso.setISO_049_TranCurrCode(currCodeCost);
					/*End Nuevos Campos Diana*/
					IsoRetrievalTransaction sql = new IsoRetrievalTransaction();
					wIso8583 wiso = new wIso8583();
					wiso.setISO_055_EMV(iso.getISO_055_EMV());
					wiso = sql.RetrieveEFI_SPI_SQL(wiso);
					if(wiso.getISO_039_ResponseCode().equals("000")){
						iso.setISO_055_EMV("000" + wiso.getISO_055_EMV());
						iso.setISO_120_ExtendedData(iso.getISO_120_ExtendedData().replace("|", "-").toUpperCase());
						iso.setISO_035_Track2(iso.getISO_035_Track2().replace("|", "-").toUpperCase());
						switch (Arrays.asList(iso.getISO_120_ExtendedData().split("\\-")).get(0)) {
						case "3":
							iso.setISO_120_ExtendedData(iso.getISO_120_ExtendedData().replace("3-", "CED-"));
							break;
						case "10":
							iso.setISO_120_ExtendedData(iso.getISO_120_ExtendedData().replace("10-", "PAS-"));
							break;
						case "1":
							iso.setISO_120_ExtendedData(iso.getISO_120_ExtendedData().replace("1-", "RUC-"));
							break;
						default:
							break;
						}
						switch (Arrays.asList(iso.getISO_035_Track2().split("\\-")).get(0)) {
						case "3":
							iso.setISO_035_Track2(iso.getISO_035_Track2().replace("3-", "CED-"));
							break;
						case "10":
							iso.setISO_035_Track2(iso.getISO_035_Track2().replace("10-", "PAS-"));
							break;
						case "1":
							iso.setISO_035_Track2(iso.getISO_035_Track2().replace("1-", "RUC-"));
							break;
						default:
							break;
						}
						/*iso.setISO_023_CardSeq(iso.getISO_023_CardSeq().equals("1")?"02":
							 iso.getISO_023_CardSeq().equals("2")?"01":
							 iso.getISO_023_CardSeq().equals("3")?"04":"");*/
						switch (iso.getISO_023_CardSeq()) {
						case "1":
							iso.setISO_023_CardSeq("1");
							break;
						case "2":
							iso.setISO_023_CardSeq("2");
							break;
						case "3":
							iso.setISO_023_CardSeq("4");
							break;
						default:
							break;
						}
						
						iso.setISO_122_ExtendedData(iso.getISO_122_ExtendedData().toUpperCase());
						iso.setISO_123_ExtendedData(iso.getISO_123_ExtendedData().toUpperCase());
						iso.setISO_124_ExtendedData(iso.getISO_124_ExtendedData().toUpperCase());
						
					}else{
						
						iso.setISO_039_ResponseCode(wiso.getISO_039_ResponseCode());
						iso.setISO_039p_ResponseDetail(wiso.getISO_039p_ResponseDetail());
						return iso;
					}
				}
				
				iso.setISO_124_ExtendedData(iso.getISO_124_ExtendedData().replace("--", "-"));
				if(!StringUtils.IsNullOrEmpty(iso.getISO_121_ExtendedData())){
					if(StringUtils.IsNumber(iso.getISO_121_ExtendedData()))
						
						iso.setISO_121_ExtendedData(FormatUtils.DateToString(iso.getISO_121_ExtendedData(), "yyyyMMdd", "dd-MM-yyyy"));
					
					else{
						
						iso.setISO_121_ExtendedData(iso.getISO_121_ExtendedData().length() > 50 ? 
					    iso.getISO_121_ExtendedData().substring(0,50):iso.getISO_121_ExtendedData());
					}
				}
				if(!StringUtils.IsNullOrEmpty(iso.getISO_122_ExtendedData())){
					if(iso.getISO_003_ProcessingCode().equals("314001"))
						iso.setISO_122_ExtendedData(iso.getISO_122_ExtendedData().substring(0,8));
					if(StringUtils.IsNumber(iso.getISO_122_ExtendedData()))
						iso.setISO_122_ExtendedData(FormatUtils.DateToString(iso.getISO_122_ExtendedData(), "yyyyMMdd", "dd-MM-yyyy"));
				}
				
				/*Consulta Transferencias Masivas*/
				
				if(iso.getISO_003_ProcessingCode().equals("311019")) {
					
					SerializationObject serializate = new SerializationObject();
					iso.setISO_002_PAN("127.0.0.1");
					iso.setISO_019_AcqCountryCode((String) getValueDataTag(message, "TransactionBackEndReference"));
					iso.setISO_120_ExtendedData((String) getValueDataTag(message, "ChannelName"));
					iso.setISO_114_ExtendedData(serializate.ObjectToString(message, ExtremeRequest.class));
				}
				
				/*----------------------------------*/
				
				iso.setISO_039_ResponseCode("000");
				
				
			}else {
				
				iso.setISO_039_ResponseCode("909");
				iso.setISO_039p_ResponseDetail("NO EXISTE LA CONFIGURACION (CONFIG_TABLE) PARA EL TRNCODE: " 
				+ message.getHeader().getValueTag("TrnCode"));
			}
			
		} catch (Exception e) {
			
			iso.setISO_039_ResponseCode("909");
			iso.setISO_039p_ResponseDetail(GeneralUtils.ExceptionToString("ERROR EN PROCESOS ", e, false));
			log.WriteLogMonitor("Error Modulo " + this.getClass().getName() + "::ExtremeMessageToIso" , TypeMonitor.error, e);
		}
		return iso;
	}
	protected String getValueHeaderTag(ExtremeRequest message, String value){
		
		String data = "";
		try {
			
			data  = StringUtils.IsNullOrEmpty(message.getHeader().getValueTag(value))?
					"":message.getHeader().getValueTag(value);
			
		} catch (Exception e) {
			
			log.WriteLogMonitor("Error Modulo " + this.getClass().getName() + "::getValueHeaderTag" , TypeMonitor.error, e);
		}
		return data;
	}
	
	protected Object getValueDataTag(ExtremeRequest message, String value){
		
		Object data = "";
		try {
			
			data  = StringUtils.IsNullOrEmpty(message.getData().getValueTag(value))?
					"":message.getData().getValueTag(value);
			
		} catch (Exception e) {
			
			log.WriteLogMonitor("Error Modulo " + this.getClass().getName() + "::getValueHeaderTag" , TypeMonitor.error, e);
		}
		return data;
	}
	private Iso8583 addIsoAdditionalRowsAlex(Ref<Iso8583> isoReference, ExtremeRequest message){
    	
    	try {
			
    		Iso8583 iso = isoReference.get();
    		AdditionalIsoValues addIsoObject = new AdditionalIsoValues();
    		List<AdditionalIsoValues> list = addIsoObject.getAdditionalIsoValuesListObject(iso.getISO_003_ProcessingCode()
    								  , Integer.parseInt(iso.getISO_024_NetworkId()), iso.getISO_018_MerchantType()
    								  , -1);
    		
    		
    		AdditionalIsoValues pageIndex = list.stream().filter(x -> x.getIso_value()
    				                        .toUpperCase().contains("INDEX"))
    				                        .findFirst().orElseGet(() -> null);
    				                       
    		
    		AdditionalIsoValues pageSize = list.stream().filter(x -> x.getIso_value()
    				                        .toUpperCase().contains("SIZE"))
						                    .findFirst().orElseGet(() -> null);
						                  
    		String acum = "";
    		String aux = "";
    		if(pageIndex != null){
    			aux = (String) getValueDataTag(message, pageIndex.getIso_value().replace("*", ""));
    			acum += StringUtils.IsNullOrEmpty(aux) ? "0-": aux + "-";
    		}
    		if(pageSize != null){
	    		aux = (String) getValueDataTag(message, pageSize.getIso_value().replace("*", ""));
	    		acum += StringUtils.IsNullOrEmpty(aux) ? "-1": aux;
    		}
    		java.lang.reflect.Field isoField = null;
    		boolean flag = false;
    		for (AdditionalIsoValues additionalIsoValues : list) {
    			
    			Iso8583 ISO = new Iso8583();
				Class<?> isoClass = (Class<?>) ISO.getClass();
				
				if(additionalIsoValues.getIso_row().startsWith("*")){
					      isoField = isoClass.
  					      getDeclaredField(additionalIsoValues.getIso_row().replace("*", ""));
					      flag = true;
				}else {
					      isoField = isoClass.
	  					      getDeclaredField(additionalIsoValues.getIso_row());
					      flag = false;
				}
    			
    			isoField.setAccessible(true);
    			Object obj = null;
    			if(additionalIsoValues.getIso_value().startsWith("**")){
    				if(flag)
    					obj = acum;
    				else
    					obj = (String)(additionalIsoValues.getIso_value()).replace("**", "");
    				
    			}else
    				obj = (Object)getValueDataTag(message, additionalIsoValues.getIso_value());
    			double d = 0;
    			Date date = null;
    			if(isoField.getType().getName().toUpperCase().contains("DOUBLE")){
    				String val = (String) obj;
    				val = StringUtils.IsNullOrEmpty(val)?"0":val;
    				d = Double.parseDouble(val);
    				isoField.set(iso, d);
    			}else if(isoField.getType().getName().toUpperCase().contains("DATE")){
    				
    				if(addIsoObject.getIso_value().startsWith("**"))
    					date = (Date)obj;
    				else
    					date = (Date)getValueDataTag(message, additionalIsoValues.getIso_value());
    				isoField.set(iso, date);
    				
    			}else {
    				isoField.set(iso, obj);
				}	
			}
    		
    		isoReference.set(iso);
    		
		} catch (Exception e) {
			
			log.WriteLogMonitor("Error modulo " + this.getClass().getName() + "::addIsoAdditionalRowsAlex", TypeMonitor.error, e);
		}
    	return isoReference.get();
    }
}
