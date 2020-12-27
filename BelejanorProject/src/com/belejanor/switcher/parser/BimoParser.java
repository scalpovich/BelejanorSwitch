package com.belejanor.switcher.parser;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import javax.xml.datatype.XMLGregorianCalendar;

import com.belejanor.switcher.bimo.pacs.camt_998_211.AcctEnroll;
import com.belejanor.switcher.bimo.pacs.camt_998_211.Document;
import com.belejanor.switcher.bimo.pacs.camt_998_211.GroupHeader;
import com.belejanor.switcher.bimo.pacs.camt_998_211.HeaderData;
import com.belejanor.switcher.bimo.pacs.camt_998_211.MjeData;
import com.belejanor.switcher.bimo.pacs.camt_998_211.OrigIdData;
import com.belejanor.switcher.bimo.pacs.camt_998_211.Priority;
import com.belejanor.switcher.bimo.pacs.camt_998_211.ProprietaryMessageV02;
import com.belejanor.switcher.bimo.pacs.camt_998_211.RoRCod;
import com.belejanor.switcher.bimo.pacs.camt_998_211.ServiceData;
import com.belejanor.switcher.bimo.pacs.camt_998_211.ZBranchAndFinancialInstitutionIdentification;
import com.belejanor.switcher.bimo.pacs.camt_998_311.ActionBlk;
import com.belejanor.switcher.bimo.pacs.camt_998_311.BlkUser;
import com.belejanor.switcher.bimo.pacs.camt_998_321.ActiveCurrencyAndAmount;
import com.belejanor.switcher.bimo.pacs.camt_998_321.CategoryPurpose1Choice;
import com.belejanor.switcher.bimo.pacs.camt_998_321.CdtTrfTxInf;
import com.belejanor.switcher.bimo.pacs.camt_998_321.ContactDetails2;
import com.belejanor.switcher.bimo.pacs.camt_998_321.PartyIdentification43;
import com.belejanor.switcher.bimo.pacs.camt_998_321.PaymentTypeInformation21;
import com.belejanor.switcher.bimo.pacs.camt_998_371.QryPendPayments;
import com.belejanor.switcher.bimo.pacs.camt_998_381.ActiveOrHistoricCurrencyAndAmount;
import com.belejanor.switcher.bimo.pacs.camt_998_381.CollectRq;
import com.belejanor.switcher.bimo.pacs.camt_998_381.ContactDetails;
import com.belejanor.switcher.bimo.pacs.camt_998_381.ContentsV01;
import com.belejanor.switcher.bimo.pacs.camt_998_381.Envelope;
import com.belejanor.switcher.bimo.pacs.camt_998_381.FinancialInstitutionIdentification8;
import com.belejanor.switcher.bimo.pacs.camt_998_381.GenericFinancialIdentification1;
import com.belejanor.switcher.bimo.pacs.camt_998_381.PaymentIdentification;
import com.belejanor.switcher.bimo.pacs.camt_998_381.SupplementaryData;
import com.belejanor.switcher.bimo.pacs.camt_998_421.BranchAndFinancialInstitutionIdentification5;
import com.belejanor.switcher.bimo.pacs.camt_998_421.SettlementInstruction4;
import com.belejanor.switcher.bimo.pacs.camt_998_421.ValField;
import com.belejanor.switcher.bimo.pacs.camt_998_421.ZFinancialInstitutionIdentification;
import com.belejanor.switcher.bimo.pacs.camt_998_421.ZGenericFinancialIdentification;
import com.belejanor.switcher.bimo.pacs.camt_998_431.CollectRjct;
import com.belejanor.switcher.bimo.pacs.camt_998_431.ReasonRjct;
import com.belejanor.switcher.bimo.pacs.camt_998_441.BranchAndFinancialInstitutionIdentification6;
import com.belejanor.switcher.bimo.pacs.camt_998_441.QryPhonebyAcc;
import com.belejanor.switcher.bimo.pacs.camt_998_441.RequestPhoneOrAccount;
import com.belejanor.switcher.bimo.pacs.camt_998_461.ValidateOTP;
import com.belejanor.switcher.bridges.BridgeBanredBIMOAcq;
import com.belejanor.switcher.cscoreswitch.wIso8583;
import com.belejanor.switcher.logger.Logger;
import com.belejanor.switcher.logger.LoggerConfig.TypeMonitor;
import com.belejanor.switcher.memcached.MemoryGlobal;
import com.belejanor.switcher.pacs.camt_998_471.RegeneratePIN;
import com.belejanor.switcher.utils.FormatUtils;
import com.belejanor.switcher.utils.GeneralUtils;
import com.belejanor.switcher.utils.SerializationObject;
import com.belejanor.switcher.utils.StringUtils;

public class BimoParser {

	private Logger log;
	private String codError;
	private String desError;
	
	public BimoParser() {
		
		log = new Logger();
		this.codError = "000";
		this.desError = StringUtils.Empty();
	}
	
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

	public Object parsePackToIso(String trama, String IP){
		
		String pack = null;
		BridgeBanredBIMOAcq bridge = null;
		Object obj = null;
		try {
			
			bridge = new BridgeBanredBIMOAcq();
			int index = trama.toUpperCase().indexOf(":TYPE>");
			if(index > 0){
				
				index += ":TYPE>".length();
				pack = trama.substring(index, index + 12);
				if(pack.startsWith("camt") || pack.startsWith("pacs"))
					switch (pack.toLowerCase()) {
					case "camt.998.211":
						
						/*Enrolamiento*/
						com.belejanor.switcher.bimo.pacs.camt_998_211.Document document =
						(Document) SerializationObject.StringToObject(trama, com.belejanor.switcher.bimo.pacs.camt_998_211.Document.class);
						
						if(document != null){
							com.belejanor.switcher.bimo.pacs.camt_998_212.Document documentResponse
							                 = bridge.processEnrollPerson(document, IP);
							if(bridge.getCodError().equals("000")){
								
								obj = documentResponse;
								
							}else{
								
								this.codError = bridge.getCodError();
								this.desError = bridge.getDesError();
							}
						}else{
							
							this.codError = "070";
							this.desError = "ERROR AL SERIALIZAR MENSAJE ISO20022";
						}
						break;
					case "camt.998.221":
						
						/*DesEnrolamiento*/
						com.belejanor.switcher.bimo.pacs.camt_998_221.Document documentDes =
							(com.belejanor.switcher.bimo.pacs.camt_998_221.Document) 
						SerializationObject.StringToObject(trama, com.belejanor.switcher.bimo.pacs.camt_998_221.Document.class);
						
						if(documentDes != null){
							
							com.belejanor.switcher.bimo.pacs.camt_998_222.Document documentResponseDes
							                 = bridge.processDesenrollPerson(documentDes, IP);
							if(bridge.getCodError().equals("000")){
								
								obj = documentResponseDes;
								
							}else{
								
								this.codError = bridge.getCodError();
								this.desError = bridge.getDesError();
							}
							
						}else{
							
							this.codError = "070";
							this.desError = "ERROR AL SERIALIZAR MENSAJE ISO20022";
						}
						
						break;
					case "pacs.008.021":
						
						/*Credito*/
						com.belejanor.switcher.bimo.pacs.pacs_008_021.Document documentCred =
						(com.belejanor.switcher.bimo.pacs.pacs_008_021.Document) 
						SerializationObject.StringToObject(trama, com.belejanor.switcher.bimo.pacs.pacs_008_021.Document.class);
					
						if(documentCred != null){
							
							com.belejanor.switcher.bimo.pacs.pacs_002_022.Document documentResponseCred
							                 = bridge.processCredit(documentCred, IP);
							if(bridge.getCodError().equals("000")){
								
								obj = documentResponseCred;
								
							}else{
								
								this.codError = bridge.getCodError();
								this.desError = bridge.getDesError();
							}
							
						}else{
							
							this.codError = "070";
							this.desError = "ERROR AL SERIALIZAR MENSAJE ISO20022";
						}
						
						break;
					case "pacs.007.041":
						
						/*Reverso Credito*/
						com.belejanor.switcher.bimo.pacs.pacs_007_041.Document documentRevCred =
						(com.belejanor.switcher.bimo.pacs.pacs_007_041.Document) 
						SerializationObject.StringToObject(trama, com.belejanor.switcher.bimo.pacs.pacs_007_041.Document.class);
					
						if(documentRevCred != null){
							
							com.belejanor.switcher.bimo.pacs.pacs_002_042.Document documentResponseRevCred
							                 = bridge.processReverCredit(documentRevCred, IP);
							if(bridge.getCodError().equals("000")){
								
								obj = documentResponseRevCred;
								
							}else{
								
								this.codError = bridge.getCodError();
								this.desError = bridge.getDesError();
							}
							
						}else{
							
							this.codError = "070";
							this.desError = "ERROR AL SERIALIZAR MENSAJE ISO20022";
						}
						
						
						break;
						
					case "pacs.008.071":
						
						/*Debito*/
						com.belejanor.switcher.bimo.pacs.pacs_008_071.Document documentDeb =
						(com.belejanor.switcher.bimo.pacs.pacs_008_071.Document) 
						SerializationObject.StringToObject(trama, com.belejanor.switcher.bimo.pacs.pacs_008_071.Document.class);
					
						if(documentDeb != null){
							
							com.belejanor.switcher.bimo.pacs.pacs_002_072.Document documentResponseDeb
							                 = bridge.processDebit(documentDeb, IP);
							if(bridge.getCodError().equals("000")){
								
								obj = documentResponseDeb;
								
							}else{
								
								this.codError = bridge.getCodError();
								this.desError = bridge.getDesError();
							}
							
						}else{
							
							this.codError = "070";
							this.desError = "ERROR AL SERIALIZAR MENSAJE ISO20022";
						}
						
						break;
					case "pacs.007.051":
						
						/*Reverso Debito*/
						com.belejanor.switcher.bimo.pacs.pacs_007_051.Document documentRevDeb =
						(com.belejanor.switcher.bimo.pacs.pacs_007_051.Document) 
						SerializationObject.StringToObject(trama, com.belejanor.switcher.bimo.pacs.pacs_007_051.Document.class);
					
						if(documentRevDeb != null){
							
							com.belejanor.switcher.bimo.pacs.pacs_002_052.Document documentResponseRevDebit
							                 = bridge.processReverDebit(documentRevDeb, IP);
							if(bridge.getCodError().equals("000")){
								
								obj = documentResponseRevDebit;
								
							}else{
								
								this.codError = bridge.getCodError();
								this.desError = bridge.getDesError();
							}
							
						}else{
							
							this.codError = "070";
							this.desError = "ERROR AL SERIALIZAR MENSAJE ISO20022";
						}
						
						break;	
						
					case "camt.998.201":
						
					/*Consulta Saldo*/
					com.belejanor.switcher.bimo.pacs.camt_998_201.Document documentQy =
					(com.belejanor.switcher.bimo.pacs.camt_998_201.Document) 
				    SerializationObject.StringToObject(trama, com.belejanor.switcher.bimo.pacs.camt_998_201.Document.class);
					
					if(documentQy != null){
						
						com.belejanor.switcher.bimo.pacs.camt_998_202.Document documentResponseQy
						                 = bridge.processQueryBalance(documentQy, IP);
						if(bridge.getCodError().equals("000")){
							
							obj = documentResponseQy;
							
						}else{
							
							this.codError = bridge.getCodError();
							this.desError = bridge.getDesError();
						}
						
					}else{
						
						this.codError = "070";
						this.desError = "ERROR AL SERIALIZAR MENSAJE ISO20022";
					}
						
						break;
					case "camt.998.231":
						/*Generacion OTP OPCIONAL*/
						com.belejanor.switcher.bimo.pacs.camt_998_231.Document documentGOtp =
						(com.belejanor.switcher.bimo.pacs.camt_998_231.Document) 
					    SerializationObject.StringToObject(trama, com.belejanor.switcher.bimo.pacs.camt_998_231.Document.class);
						
						if(documentGOtp != null){
							
							com.belejanor.switcher.bimo.pacs.camt_998_232.Document documentResponseGOTP
							                 = bridge.processGenOtp(documentGOtp, IP);
							if(bridge.getCodError().equals("000")){
								
								obj = documentResponseGOTP;
								
							}else{
								
								this.codError = bridge.getCodError();
								this.desError = bridge.getDesError();
							}
							
						}else{
							
							this.codError = "070";
							this.desError = "ERROR AL SERIALIZAR MENSAJE ISO20022";
						}
						
						break;	
					case "camt.998.461":
						/*Se decidio en la Coop. no Validar el OTP, Banred lo hace*/
						this.codError = "057";
						this.desError = "TRANSACCION NO SOPORTADA";
						
						break;
					default:
						break;
				}
			}
		
		} catch (Exception e) {
			
			log.WriteLogMonitor("Error modulo BimoParser::parsePackToIso ", TypeMonitor.error, e);
			this.codError = "070";
			this.desError = "ERROR EN PROCESOS" + GeneralUtils.ExceptionToString(null, e, false);
			
		}
		return obj;
	}
	
	/*-------------------------------------  PROCESOS CUANDO LA COOP ES ADQUIRIENTE  --------------------------------------------------*/
	public Document parseEnrollAutBimo(wIso8583 iso){
		
		Document documento = null;
	    XMLGregorianCalendar dateTrx = null;
        
		try {
			
			dateTrx = FormatUtils.getDateSystemXMLGregorian();
			documento = new Document();
			HeaderData hdr = new HeaderData();
			OrigIdData org = new OrigIdData();
			com.belejanor.switcher.bimo.pacs.camt_998_211.ServiceData srvData = 
					new ServiceData();
			srvData.setIdServ("Enrollment IFI RQ");
			srvData.setVersServ("1.0");
			org.setChannel("IFISBIMO"); //IFISBIMO
			org.setApp(iso.getISO_041_CardAcceptorID()); //VENT, APP, ETC.
			org.setService(srvData);
			org.setOtherId(iso.getISO_023_CardSeq()); //celular
			
			hdr.setOrigId(org);
			hdr.setSender(MemoryGlobal.IdBIMOEfi);
			hdr.setReceiver(MemoryGlobal.IdBIMOBanred);
			
			MjeData mge = new MjeData();
			mge.setType("camt.998.211");
			mge.setRoR(RoRCod.REQ);
			mge.setIdMge(MemoryGlobal.IdBIMOEfi + iso.getISO_011_SysAuditNumber()); //yyyyMMddHHmmss
			mge.setOpeDate(dateTrx);
			
			hdr.setMge(mge);
			hdr.setPssblDplct(false);
			hdr.setPrty(Priority.NORM);
			hdr.setCpyDplct("NORM");
			
			com.belejanor.switcher.bimo.pacs.camt_998_211.ProprietaryMessageV02 prtyMsg =
					new ProprietaryMessageV02();
			
			com.belejanor.switcher.bimo.pacs.camt_998_211.GroupHeader grhdr = new 
					GroupHeader();
			grhdr.setMsgId(MemoryGlobal.IdBIMOEfi + iso.getISO_011_SysAuditNumber()); //yyyyMMddHHmmss
			grhdr.setCreDtTm(dateTrx);
			grhdr.setNbOfTxs(BigInteger.valueOf(1));
			
			com.belejanor.switcher.bimo.pacs.camt_998_211.SettlementInstruction4 sttInf =
					new com.belejanor.switcher.bimo.pacs.camt_998_211.SettlementInstruction4();
			sttInf.setSttlmMtd(com.belejanor.switcher.bimo.pacs.camt_998_211
					          .SettlementMethod1Code.INDA);
			grhdr.setSttInf(sttInf);
			
			
			ZBranchAndFinancialInstitutionIdentification efiAcq = new ZBranchAndFinancialInstitutionIdentification();
			com.belejanor.switcher.bimo.pacs.camt_998_211.ZFinancialInstitutionIdentification efi = new 
					com.belejanor.switcher.bimo.pacs.camt_998_211.ZFinancialInstitutionIdentification();
			com.belejanor.switcher.bimo.pacs.camt_998_211.ZGenericFinancialIdentification zgen = new 
					com.belejanor.switcher.bimo.pacs.camt_998_211.ZGenericFinancialIdentification();
			zgen.setId(MemoryGlobal.IdBIMOEfi);
			efi.setOthr(zgen);
			efiAcq.setFinInstnId(efi);
			
			
			ZBranchAndFinancialInstitutionIdentification efiAut = new ZBranchAndFinancialInstitutionIdentification();
			com.belejanor.switcher.bimo.pacs.camt_998_211.ZFinancialInstitutionIdentification efi2 = new 
					com.belejanor.switcher.bimo.pacs.camt_998_211.ZFinancialInstitutionIdentification();
			com.belejanor.switcher.bimo.pacs.camt_998_211.ZGenericFinancialIdentification zgen2 = new 
					com.belejanor.switcher.bimo.pacs.camt_998_211.ZGenericFinancialIdentification();
			zgen2.setId(MemoryGlobal.IdBIMOBanred);
			efi2.setOthr(zgen2);
			efiAut.setFinInstnId(efi2);
			
			grhdr.setInstdAgt(efiAut);
			grhdr.setInstgAgt(efiAcq);
			
			com.belejanor.switcher.bimo.pacs.camt_998_211.AcctEnroll enroll = new AcctEnroll();
			enroll.setPartyNm(iso.getISO_034_PANExt());
			enroll.setPartyId(iso.getISO_002_PAN());
			enroll.setPartyDocType(iso.getISO_022_PosEntryMode().equalsIgnoreCase("CED")?"CC":"CC");
			enroll.setPINBimo(iso.getISO_052_PinBlock());
			enroll.setPartyPerfil(iso.getISO_115_ExtendedData().equals("NAT")?"PN":"MC");
			enroll.setPartyDocCtry(iso.getISO_019_AcqCountryCode().equalsIgnoreCase("EC")?"ECUATORIANA":"ECUATORIANA");
			enroll.setPartyPrdType("BIMO");
			enroll.setPartyPhone(iso.getISO_023_CardSeq()); //Celular
			enroll.setPartyEmail(iso.getISO_123_ExtendedData()); //Mail
			enroll.setPartyBrdDt(FormatUtils.getDateXMLGregorianSimple(iso.getISO_114_ExtendedData()));
			enroll.setAcctInfo(MemoryGlobal.IdBIMOEfi + "|" + iso.getISO_120_ExtendedData() 
			+ "|0"+ iso.getISO_124_ExtendedData() +"|" + iso.getISO_102_AccountID_1() + "|" + iso.getISO_121_ExtendedData()); //120 tipo cuenta, 121 moneda
			enroll.setGender(iso.getISO_122_ExtendedData()); //122 Genero
			enroll.setPartyIssueDate(FormatUtils.getDateXMLGregorianSimple(FormatUtils.DateToString(new Date(), "yyyy-MM-dd")));
			enroll.setSttlmDt(FormatUtils.getDateXMLGregorianSimple(FormatUtils
					   .DateToString(iso.getISO_015_SettlementDatel(), "yyyy-MM-dd")));
			
			prtyMsg.setGrpHdr(grhdr);
			prtyMsg.setAcctEnroll(enroll);
			
			documento.setHeader(hdr);
			documento.setPrtryMsg(prtyMsg);
			
			this.codError = "000";
		
		} catch (Exception e) {
			
			this.codError = "070";
			this.desError = "ERROR EN PROCESOS" + GeneralUtils.ExceptionToString(null, e, false);
			log.WriteLogMonitor("Error modulo BimoParser::parseEnrollAutBimo ", TypeMonitor.error, e);
		}
		return documento;
			
	}
	
	public com.belejanor.switcher.bimo.pacs.camt_998_421.Document 
	                                                parseValidaUsuarioAutBimo(wIso8583 iso){
		 
		com.belejanor.switcher.bimo.pacs.camt_998_421.Document documento = null;
	    XMLGregorianCalendar dateTrx = null;
        String randomSecu = GeneralUtils.GetSecuencialNumeric(14);
		try {
			
			dateTrx = FormatUtils.getDateSystemXMLGregorian();
			documento = new com.belejanor.switcher.bimo.pacs.camt_998_421.Document();
			com.belejanor.switcher.bimo.pacs.camt_998_421.HeaderData hdr = new 
					com.belejanor.switcher.bimo.pacs.camt_998_421.HeaderData();
			com.belejanor.switcher.bimo.pacs.camt_998_421.OrigIdData org = new  
					com.belejanor.switcher.bimo.pacs.camt_998_421.OrigIdData();
			com.belejanor.switcher.bimo.pacs.camt_998_421.ServiceData srvData = 
					new com.belejanor.switcher.bimo.pacs.camt_998_421.ServiceData();
						
			srvData.setIdServ("Valida Campo RQ");
			srvData.setVersServ("1");
			org.setChannel("IFISBIMO"); //IFISBIMO
			org.setApp(iso.getISO_041_CardAcceptorID()); //VENT, APP, ETC.
			org.setService(srvData);
			org.setOtherId(iso.getISO_023_CardSeq()); //celular
			
			hdr.setOrigId(org);
			hdr.setSender(MemoryGlobal.IdBIMOEfi);
			hdr.setReceiver(MemoryGlobal.IdBIMOBanred);
			
			com.belejanor.switcher.bimo.pacs.camt_998_421.MjeData mge = new 
					com.belejanor.switcher.bimo.pacs.camt_998_421.MjeData();
			mge.setType("camt.998.421");
			mge.setRoR(com.belejanor.switcher.bimo.pacs.camt_998_421.RoRCod.REQ);
			//mge.setIdMge(iso.getISO_011_SysAuditNumber()); //yyyyMMddHHmmss
			mge.setIdMge(randomSecu);
			mge.setOpeDate(dateTrx);
			
			hdr.setMge(mge);
			hdr.setPssblDplct(false);
			hdr.setPrty(com.belejanor.switcher.bimo.pacs.camt_998_421.Priority.NORM);
			hdr.setCpyDplct("NORM");
			
			com.belejanor.switcher.bimo.pacs.camt_998_421.ProprietaryMessageV02 prtyMsg =
					new com.belejanor.switcher.bimo.pacs.camt_998_421.ProprietaryMessageV02();
			
			com.belejanor.switcher.bimo.pacs.camt_998_421.GroupHeader grhdr = new 
					 com.belejanor.switcher.bimo.pacs.camt_998_421.GroupHeader();
			//grhdr.setMsgId(iso.getISO_011_SysAuditNumber()); //yyyyMMddHHmmss
			grhdr.setMsgId(randomSecu);
			grhdr.setCreDtTm(dateTrx);
			grhdr.setNbOfTxs(BigInteger.valueOf(1));
			
			com.belejanor.switcher.bimo.pacs.camt_998_421.SettlementInstruction4 sttInf =
					new SettlementInstruction4();
			sttInf.setSttlmMtd(com.belejanor.switcher.bimo.pacs.camt_998_421
					          .SettlementMethod1Code.INDA);
			grhdr.setSttInf(sttInf);
			
			
			BranchAndFinancialInstitutionIdentification5 efiAcq = new 
					                  BranchAndFinancialInstitutionIdentification5();
			com.belejanor.switcher.bimo.pacs.camt_998_421.ZFinancialInstitutionIdentification zAcq = new 
					ZFinancialInstitutionIdentification();
			com.belejanor.switcher.bimo.pacs.camt_998_421.ZGenericFinancialIdentification zgen = new 
					ZGenericFinancialIdentification();
			zgen.setId(MemoryGlobal.IdBIMOEfi);
			zAcq.setOthr(zgen);
			efiAcq.setFinInstnId(zAcq);
			
			
			BranchAndFinancialInstitutionIdentification5 efiAut = new 
	                  BranchAndFinancialInstitutionIdentification5();
			com.belejanor.switcher.bimo.pacs.camt_998_421.ZFinancialInstitutionIdentification zAut = new 
				ZFinancialInstitutionIdentification();
			com.belejanor.switcher.bimo.pacs.camt_998_421.ZGenericFinancialIdentification zgenAut = new 
				ZGenericFinancialIdentification();
			zgenAut.setId(MemoryGlobal.IdBIMOBanred);
			zAut.setOthr(zgenAut);
			efiAut.setFinInstnId(zAut);
			
			grhdr.setInstdAgt(efiAut);
			grhdr.setInstgAgt(efiAcq);
			
			ValField valueConsul = new ValField();
			valueConsul.setFieldType("TELEFONO");
			valueConsul.setFieldValue(iso.getISO_023_CardSeq()); //Telefono
			valueConsul.setSttlmDt(FormatUtils.getDateXMLGregorianSimple(FormatUtils
					   .DateToString(iso.getISO_015_SettlementDatel(), "yyyy-MM-dd")));
			
			prtyMsg.setGrpHdr(grhdr);
			prtyMsg.setValField(valueConsul);
			
			documento.setHeader(hdr);
			documento.setPrtryMsg(prtyMsg);
			
			this.codError = "000";
		
		} catch (Exception e) {
			
			this.codError = "070";
			this.desError = "ERROR EN PROCESOS" + GeneralUtils.ExceptionToString(null, e, false);
			log.WriteLogMonitor("Error modulo BimoParser::parseValidaUsuarioAutBimo ", TypeMonitor.error, e);
		}
		return documento;
		
	}
	public com.belejanor.switcher.bimo.pacs.camt_998_441.Document 
    								parseValidaTelefonoCtaAutBimo(wIso8583 iso){

		com.belejanor.switcher.bimo.pacs.camt_998_441.Document documento = null;
		XMLGregorianCalendar dateTrx = null;
		String randomSecu = GeneralUtils.GetSecuencialNumeric(14);
		
		try {
		
			dateTrx = FormatUtils.getDateSystemXMLGregorian();
			documento = new com.belejanor.switcher.bimo.pacs.camt_998_441.Document();
			com.belejanor.switcher.bimo.pacs.camt_998_441.HeaderData hdr = new 
						com.belejanor.switcher.bimo.pacs.camt_998_441.HeaderData();
			com.belejanor.switcher.bimo.pacs.camt_998_441.OrigIdData org = new  
						com.belejanor.switcher.bimo.pacs.camt_998_441.OrigIdData();
			com.belejanor.switcher.bimo.pacs.camt_998_441.ServiceData srvData =  new 
						com.belejanor.switcher.bimo.pacs.camt_998_441.ServiceData();
			
			srvData.setIdServ("Valida Campo RQ");
			srvData.setVersServ("1");
			org.setChannel("IFISBIMO"); //IFISBIMO
			org.setApp(iso.getISO_041_CardAcceptorID()); //VENT, APP, ETC.
			org.setService(srvData);
			org.setOtherId(iso.getISO_023_CardSeq()); //celular
			
			hdr.setOrigId(org);
			hdr.setSender(MemoryGlobal.IdBIMOEfi);
			hdr.setReceiver(MemoryGlobal.IdBIMOBanred);
			
			com.belejanor.switcher.bimo.pacs.camt_998_441.MjeData mge = new 
			com.belejanor.switcher.bimo.pacs.camt_998_441.MjeData();
			mge.setType("camt.998.441");
			mge.setRoR(com.belejanor.switcher.bimo.pacs.camt_998_441.RoRCod.REQ);
			//mge.setIdMge(iso.getISO_011_SysAuditNumber()); //yyyyMMddHHmmss
			mge.setIdMge(randomSecu);
			mge.setOpeDate(dateTrx);
			
			hdr.setMge(mge);
			hdr.setPssblDplct(false);
			hdr.setPrty(com.belejanor.switcher.bimo.pacs.camt_998_441.Priority.NORM);
			hdr.setCpyDplct("NORM");
			
			com.belejanor.switcher.bimo.pacs.camt_998_441.ProprietaryMessageV02 prtyMsg =
			new com.belejanor.switcher.bimo.pacs.camt_998_441.ProprietaryMessageV02();
			
			com.belejanor.switcher.bimo.pacs.camt_998_441.GroupHeader grhdr = new 
			com.belejanor.switcher.bimo.pacs.camt_998_441.GroupHeader();
			//grhdr.setMsgId(iso.getISO_011_SysAuditNumber()); //yyyyMMddHHmmss
			grhdr.setMsgId(randomSecu);
			grhdr.setCreDtTm(dateTrx);
			grhdr.setNbOfTxs(BigInteger.valueOf(1));
			
			com.belejanor.switcher.bimo.pacs.camt_998_441.SettlementInstruction4 sttInf =
			new com.belejanor.switcher.bimo.pacs.camt_998_441.SettlementInstruction4();
			sttInf.setSttlmMtd(com.belejanor.switcher.bimo.pacs.camt_998_441
			.SettlementMethod1Code.INDA);
			grhdr.setSttInf(sttInf);
			
			
			BranchAndFinancialInstitutionIdentification6 efiAcq = new 
					BranchAndFinancialInstitutionIdentification6();
			com.belejanor.switcher.bimo.pacs.camt_998_441.ZFinancialInstitutionIdentification zAcq = new 
					com.belejanor.switcher.bimo.pacs.camt_998_441.ZFinancialInstitutionIdentification();
			com.belejanor.switcher.bimo.pacs.camt_998_441.ZGenericFinancialIdentification zgen = new 
					com.belejanor.switcher.bimo.pacs.camt_998_441.ZGenericFinancialIdentification();
			zgen.setId(MemoryGlobal.IdBIMOEfi);
			zAcq.setOthr(zgen);
			efiAcq.setFinInstnId(zAcq);
			
			BranchAndFinancialInstitutionIdentification6 efiAut = new 
					BranchAndFinancialInstitutionIdentification6();
			com.belejanor.switcher.bimo.pacs.camt_998_441.ZFinancialInstitutionIdentification zAut = new 
					com.belejanor.switcher.bimo.pacs.camt_998_441.ZFinancialInstitutionIdentification();
			com.belejanor.switcher.bimo.pacs.camt_998_441.ZGenericFinancialIdentification zgenAut = new 
					com.belejanor.switcher.bimo.pacs.camt_998_441.ZGenericFinancialIdentification();
			zgenAut.setId(MemoryGlobal.IdBIMOBanred);
			zAut.setOthr(zgenAut);
			efiAut.setFinInstnId(zAut);
			
			grhdr.setInstdAgt(efiAut);
			grhdr.setInstgAgt(efiAcq);
			
			QryPhonebyAcc qry = new QryPhonebyAcc();
			qry.setRequestPhoneOrAccount(RequestPhoneOrAccount.PHONE);
			qry.setValue(iso.getISO_023_CardSeq());
			qry.setSttlmDt(FormatUtils.getDateXMLGregorianSimple(FormatUtils
					   .DateToString(iso.getISO_015_SettlementDatel(), "yyyy-MM-dd")));
			
			prtyMsg.setGrpHdr(grhdr);
			prtyMsg.setQryPhonebyAcc(qry);
			
			documento.setHeader(hdr);
			documento.setPrtryMsg(prtyMsg);
			
			this.codError = "000";
		
		} catch (Exception e) {
		
			this.codError = "070";
			this.desError = "ERROR EN PROCESOS" + GeneralUtils.ExceptionToString(null, e, false);
			log.WriteLogMonitor("Error modulo BimoParser::parseValidaUsuarioAutBimo ", TypeMonitor.error, e);
		}
		return documento;

	}
	
	public com.belejanor.switcher.bimo.pacs.camt_998_311.Document 
										parseAdminWalletAutBimo(wIso8583 iso){

		com.belejanor.switcher.bimo.pacs.camt_998_311.Document documento = null;
		XMLGregorianCalendar dateTrx = null;
		
		try {
		
			dateTrx = FormatUtils.getDateSystemXMLGregorian();
			documento = new com.belejanor.switcher.bimo.pacs.camt_998_311.Document();
			com.belejanor.switcher.bimo.pacs.camt_998_311.HeaderData hdr = new 
					com.belejanor.switcher.bimo.pacs.camt_998_311.HeaderData();
			com.belejanor.switcher.bimo.pacs.camt_998_311.OrigIdData org = new  
					com.belejanor.switcher.bimo.pacs.camt_998_311.OrigIdData();
			com.belejanor.switcher.bimo.pacs.camt_998_311.ServiceData srvData =  new 
					com.belejanor.switcher.bimo.pacs.camt_998_311.ServiceData();
			
			//Campo variable Desenrolamiento, Activacion, desactivacion
			switch (iso.getISO_090_OriginalData().trim()) {
			case "D":
				srvData.setIdServ("Unenrollement User RQ");
				break;
			case "A":
				srvData.setIdServ("Des-Block User RQ");
				break;
			case "B":
				srvData.setIdServ("Block User RQ");
				break;
			default:
				break;
			}
			
			srvData.setVersServ("1.0");
			org.setChannel("IFISBIMO"); //IFISBIMO
			org.setApp(iso.getISO_041_CardAcceptorID()); //VENT, APP, ETC.
			org.setService(srvData);
			org.setOtherId(iso.getISO_023_CardSeq()); //celular
			
			hdr.setOrigId(org);
			hdr.setSender(MemoryGlobal.IdBIMOEfi);
			hdr.setReceiver(MemoryGlobal.IdBIMOBanred);
			
			com.belejanor.switcher.bimo.pacs.camt_998_311.MjeData mge = new 
			com.belejanor.switcher.bimo.pacs.camt_998_311.MjeData();
			mge.setType("camt.998.311");
			mge.setRoR(com.belejanor.switcher.bimo.pacs.camt_998_311.RoRCod.REQ);
			mge.setIdMge(MemoryGlobal.IdBIMOEfi + iso.getISO_011_SysAuditNumber()); //yyyyMMddHHmmss
			mge.setOpeDate(dateTrx);
			
			hdr.setMge(mge);
			hdr.setPssblDplct(false);
			hdr.setPrty(com.belejanor.switcher.bimo.pacs.camt_998_311.Priority.NORM);
			hdr.setCpyDplct("NORM");
			
			com.belejanor.switcher.bimo.pacs.camt_998_311.ProprietaryMessageV02 prtyMsg =
			new com.belejanor.switcher.bimo.pacs.camt_998_311.ProprietaryMessageV02();
			
			com.belejanor.switcher.bimo.pacs.camt_998_311.GroupHeader grhdr = new 
			com.belejanor.switcher.bimo.pacs.camt_998_311.GroupHeader();
			grhdr.setMsgId(MemoryGlobal.IdBIMOEfi + iso.getISO_011_SysAuditNumber()); //yyyyMMddHHmmss
			grhdr.setCreDtTm(dateTrx);
			grhdr.setNbOfTxs(BigInteger.valueOf(1));
			
			com.belejanor.switcher.bimo.pacs.camt_998_311.SettlementInstruction4 sttInf =
			new com.belejanor.switcher.bimo.pacs.camt_998_311.SettlementInstruction4();
			sttInf.setSttlmMtd(com.belejanor.switcher.bimo.pacs.camt_998_311
			.SettlementMethod1Code.INDA);
			grhdr.setSttInf(sttInf);
			
			
			com.belejanor.switcher.bimo.pacs.camt_998_311.BranchAndFinancialInstitutionIdentification6 efiAcq = new 
					com.belejanor.switcher.bimo.pacs.camt_998_311.BranchAndFinancialInstitutionIdentification6();
			com.belejanor.switcher.bimo.pacs.camt_998_311.ZFinancialInstitutionIdentification zAcq = new 
			com.belejanor.switcher.bimo.pacs.camt_998_311.ZFinancialInstitutionIdentification();
			com.belejanor.switcher.bimo.pacs.camt_998_311.ZGenericFinancialIdentification zgen = new 
			com.belejanor.switcher.bimo.pacs.camt_998_311.ZGenericFinancialIdentification();
			zgen.setId(MemoryGlobal.IdBIMOEfi);
			zAcq.setOthr(zgen);
			efiAcq.setFinInstnId(zAcq);
			
			com.belejanor.switcher.bimo.pacs.camt_998_311.BranchAndFinancialInstitutionIdentification6 efiAut = new 
					com.belejanor.switcher.bimo.pacs.camt_998_311.BranchAndFinancialInstitutionIdentification6();
			com.belejanor.switcher.bimo.pacs.camt_998_311.ZFinancialInstitutionIdentification zAut = new 
			com.belejanor.switcher.bimo.pacs.camt_998_311.ZFinancialInstitutionIdentification();
			com.belejanor.switcher.bimo.pacs.camt_998_311.ZGenericFinancialIdentification zgenAut = new 
			com.belejanor.switcher.bimo.pacs.camt_998_311.ZGenericFinancialIdentification();
			zgenAut.setId(MemoryGlobal.IdBIMOBanred);
			zAut.setOthr(zgenAut);
			efiAut.setFinInstnId(zAut);
			
			grhdr.setInstdAgt(efiAut);
			grhdr.setInstgAgt(efiAcq);
			
			BlkUser admin = new BlkUser();
			admin.setPartyPhone(iso.getISO_023_CardSeq());
			
			switch (iso.getISO_090_OriginalData().trim()) {
			case "D":
				admin.setAction(ActionBlk.DESENROLAMIENTO);
				break;
			case "A":
				admin.setAction(ActionBlk.DESBLOQUEO);
				break;
			case "B":
				admin.setAction(ActionBlk.BLOQUEO);
				break;
			default:
				break;
			}
			admin.setPartyPhone(iso.getISO_023_CardSeq());
			admin.setPartyNm(iso.getISO_034_PANExt());
			admin.setPartyId(iso.getISO_002_PAN());
			admin.setPartyDocType(iso.getISO_022_PosEntryMode().equalsIgnoreCase("CED")?"CC":"CC");
			admin.setSttlmDt(FormatUtils.getDateXMLGregorianSimple(FormatUtils
					   .DateToString(iso.getISO_015_SettlementDatel(), "yyyy-MM-dd")));
			
			prtyMsg.setGrpHdr(grhdr);
			prtyMsg.setBlckUser(admin);
			
			documento.setHeader(hdr);
			documento.setPrtryMsg(prtyMsg);
			
			this.codError = "000";
		
		} catch (Exception e) {
		
			this.codError = "070";
			this.desError = "ERROR EN PROCESOS" + GeneralUtils.ExceptionToString(null, e, false);
			log.WriteLogMonitor("Error modulo BimoParser::parseValidaUsuarioAutBimo ", TypeMonitor.error, e);
		}
		
		return documento;
	}
	
	public com.belejanor.switcher.bimo.pacs.camt_998_321.Document 
													parseTransferenciaBimo(wIso8583 iso){
		com.belejanor.switcher.bimo.pacs.camt_998_321.Document documento = null;
		XMLGregorianCalendar dateTrx = null;
		
		try {
		
			dateTrx = FormatUtils.getDateSystemXMLGregorian();
			documento = new com.belejanor.switcher.bimo.pacs.camt_998_321.Document();
			com.belejanor.switcher.bimo.pacs.camt_998_321.HeaderData hdr = new 
					com.belejanor.switcher.bimo.pacs.camt_998_321.HeaderData();
			com.belejanor.switcher.bimo.pacs.camt_998_321.OrigIdData org = new  
					com.belejanor.switcher.bimo.pacs.camt_998_321.OrigIdData();
			com.belejanor.switcher.bimo.pacs.camt_998_321.ServiceData srvData =  new 
					com.belejanor.switcher.bimo.pacs.camt_998_321.ServiceData();
			
			srvData.setIdServ("Transferer RQ");
			srvData.setVersServ("1.0");
			org.setChannel("IFISBIMO"); //IFISBIMO
			org.setApp(iso.getISO_041_CardAcceptorID()); //VENT, APP, ETC.
			org.setService(srvData);
			org.setOtherId(iso.getISO_023_CardSeq()); //celular
			
			hdr.setOrigId(org);
			hdr.setSender(MemoryGlobal.IdBIMOEfi);
			hdr.setReceiver(MemoryGlobal.IdBIMOBanred);
			
			String secuencialOne = GeneralUtils.GetSecuencialNumeric(10);
			com.belejanor.switcher.bimo.pacs.camt_998_321.MjeData mge = new 
					com.belejanor.switcher.bimo.pacs.camt_998_321.MjeData();
			mge.setType("camt.998.321");
			mge.setRoR(com.belejanor.switcher.bimo.pacs.camt_998_321.RoRCod.REQ);
			mge.setIdMge(MemoryGlobal.IdBIMOEfi + iso.getISO_011_SysAuditNumber() 
							+  secuencialOne); //yyyyMMddHHmmss				
			mge.setOpeDate(dateTrx);
			
			hdr.setMge(mge);
			hdr.setPssblDplct(false);
			hdr.setPrty(com.belejanor.switcher.bimo.pacs.camt_998_321.Priority.NORM);
			hdr.setCpyDplct("NORM");
			
			com.belejanor.switcher.bimo.pacs.camt_998_321.ProprietaryMessageV02 prtyMsg =
					new com.belejanor.switcher.bimo.pacs.camt_998_321.ProprietaryMessageV02();
			
			com.belejanor.switcher.bimo.pacs.camt_998_321.GroupHeader grhdr =  
					new com.belejanor.switcher.bimo.pacs.camt_998_321.GroupHeader();
			grhdr.setMsgId(MemoryGlobal.IdBIMOEfi +  iso.getISO_011_SysAuditNumber()
						    +  secuencialOne); //yyyyMMddHHmmss
			grhdr.setCreDtTm(dateTrx);
			grhdr.setNbOfTxs(BigInteger.valueOf(1));
			
			com.belejanor.switcher.bimo.pacs.camt_998_321.SettlementInstruction4 sttInf =
					new com.belejanor.switcher.bimo.pacs.camt_998_321.SettlementInstruction4();
			sttInf.setSttlmMtd(com.belejanor.switcher.bimo.pacs.camt_998_321
			.SettlementMethod1Code.INDA);
			grhdr.setSttInf(sttInf);
			
			
			com.belejanor.switcher.bimo.pacs.camt_998_321.BranchAndFinancialInstitutionIdentification6 efiAcq = new 
					com.belejanor.switcher.bimo.pacs.camt_998_321.BranchAndFinancialInstitutionIdentification6();
			com.belejanor.switcher.bimo.pacs.camt_998_321.ZFinancialInstitutionIdentification zAcq = new 
					com.belejanor.switcher.bimo.pacs.camt_998_321.ZFinancialInstitutionIdentification();
			com.belejanor.switcher.bimo.pacs.camt_998_321.ZGenericFinancialIdentification zgen = new 
					com.belejanor.switcher.bimo.pacs.camt_998_321.ZGenericFinancialIdentification();
			zgen.setId(MemoryGlobal.IdBIMOEfi);
			zAcq.setOthr(zgen);
			efiAcq.setFinInstnId(zAcq);
			
			com.belejanor.switcher.bimo.pacs.camt_998_321.BranchAndFinancialInstitutionIdentification6 efiAut = new 
					com.belejanor.switcher.bimo.pacs.camt_998_321.BranchAndFinancialInstitutionIdentification6();
			com.belejanor.switcher.bimo.pacs.camt_998_321.ZFinancialInstitutionIdentification zAut = new 
					com.belejanor.switcher.bimo.pacs.camt_998_321.ZFinancialInstitutionIdentification();
			com.belejanor.switcher.bimo.pacs.camt_998_321.ZGenericFinancialIdentification zgenAut = new 
					com.belejanor.switcher.bimo.pacs.camt_998_321.ZGenericFinancialIdentification();
			zgenAut.setId(MemoryGlobal.IdBIMOBanred);
			zAut.setOthr(zgenAut);
			efiAut.setFinInstnId(zAut);
			
			grhdr.setInstdAgt(efiAut);
			grhdr.setInstgAgt(efiAcq);
		
			/*Comienza estructura*/
			com.belejanor.switcher.bimo.pacs.camt_998_321.CdtTrfTxInf crtrx = 
						new CdtTrfTxInf();
			com.belejanor.switcher.bimo.pacs.camt_998_321.PaymentIdentification3 pymt = 
					    new com.belejanor.switcher.bimo.pacs.camt_998_321.PaymentIdentification3();
			String secuencia = StringUtils.Empty();
			secuencia = GeneralUtils.GetSecuencialNumeric(12);
			
			pymt.setEndToEndId(MemoryGlobal.IdBIMOEfi + FormatUtils.DateToString(new Date()
					, "yyyyMMddHHmmss") + secuencia);
		
		   /*Ojo cuando se envia el Iso122 FitSwitch supone que se esta haciendo un pago a una solicitu de cobro
		    * Caso contrario se trata como una transaccion de pago (Transferencia) comun*/
		   if(StringUtils.IsNullOrEmpty(iso.getISO_122_ExtendedData()))
				pymt.setTxId(secuencia);
		   else{
			   
			   pymt.setTxId(iso.getISO_122_ExtendedData());
		   }
			crtrx.setPmtId(pymt);
		
			crtrx.setPmtId(pymt);
			
			com.belejanor.switcher.bimo.pacs.camt_998_321.PaymentTypeInformation21 pymType = 
					new PaymentTypeInformation21();
			com.belejanor.switcher.bimo.pacs.camt_998_321.CategoryPurpose1Choice catPurp = 
					new CategoryPurpose1Choice();
			
			 /*Ojo cuando se envia el Iso122 FitSwitch supone que se esta haciendo un pago a una solicitu de cobro
			    * Caso contrario se trata como una transaccion de pago (Transferencia) comun*/
			if(StringUtils.IsNullOrEmpty(iso.getISO_122_ExtendedData()))
				catPurp.setPrtry("PAGO");
			else {
				catPurp.setPrtry("COBROP2P");
			}
			
			pymType.setCtgyPurp(catPurp);
			
			crtrx.setPmtTpInf(pymType);
			
			com.belejanor.switcher.bimo.pacs.camt_998_321.ActiveCurrencyAndAmount actAmm = 
					new ActiveCurrencyAndAmount();
			actAmm.setCcy(iso.getISO_120_ExtendedData());
			actAmm.setValue(BigDecimal.valueOf(iso.getISO_004_AmountTransaction()));
			
			crtrx.setIntrBkSttlmAmt(actAmm);
			crtrx.setIntrBkSttlmDt(FormatUtils.getDateXMLGregorianSimple(FormatUtils
					   .DateToString(iso.getISO_015_SettlementDatel(), "yyyy-MM-dd")));
			
			/*Telefono Origen*/
			com.belejanor.switcher.bimo.pacs.camt_998_321.PartyIdentification43 orgn = 
					new PartyIdentification43();
			com.belejanor.switcher.bimo.pacs.camt_998_321.ContactDetails2 contactsOrgn = 
					new ContactDetails2();
			contactsOrgn.setPhneNb(iso.getISO_023_CardSeq());
			orgn.setAccount(MemoryGlobal.IdBIMOEfi +  "|00||" + iso.getISO_023_CardSeq() + "|USD");
			orgn.setCtctDtls(contactsOrgn);
			
			crtrx.setOrgn(orgn);
			
			/*Telefono Destino*/
			com.belejanor.switcher.bimo.pacs.camt_998_321.PartyIdentification43 destn = 
					new PartyIdentification43();
			com.belejanor.switcher.bimo.pacs.camt_998_321.ContactDetails2 contactsDest = 
					new ContactDetails2();
			contactsDest.setPhneNb(iso.getISO_103_AccountID_2());
			destn.setAccount("000|00||" + iso.getISO_103_AccountID_2() + "|USD");
			destn.setCtctDtls(contactsDest);
			
			crtrx.setDst(destn);
			
			/*Motivo de la Transferencia*/
			com.belejanor.switcher.bimo.pacs.camt_998_321.SupplementaryData1 spplData = 
					new com.belejanor.switcher.bimo.pacs.camt_998_321.SupplementaryData1();
			com.belejanor.switcher.bimo.pacs.camt_998_321.SupplementaryDataEnvelope1 envlp = 
					new com.belejanor.switcher.bimo.pacs.camt_998_321.SupplementaryDataEnvelope1();
			com.belejanor.switcher.bimo.pacs.camt_998_321.ContentsV01 conts = 
					new com.belejanor.switcher.bimo.pacs.camt_998_321.ContentsV01();
			conts.setCustRef(iso.getISO_121_ExtendedData()); 
			spplData.setEnvlp(envlp);
			envlp.setCnts(conts);
			
			crtrx.setSplmtryData(spplData);
			
			prtyMsg.setGrpHdr(grhdr);
			prtyMsg.setCdtTrfTxInf(crtrx);
			
			documento.setHeader(hdr);
			documento.setPrtryMsg(prtyMsg);
			
			this.codError = "000";
		
		} catch (Exception e) {
		
			this.codError = "070";
			this.desError = "ERROR EN PROCESOS" + GeneralUtils.ExceptionToString(null, e, false);
			log.WriteLogMonitor("Error modulo BimoParser::parseTransferenciaBimo ", TypeMonitor.error, e);
		}
		
		return documento;
	}
	
	
	
	public com.belejanor.switcher.bimo.pacs.camt_998_431.Document 
						                            parseRechazarSolicituAutBimo(wIso8583 iso){
		
		com.belejanor.switcher.bimo.pacs.camt_998_431.Document documento = null;
		XMLGregorianCalendar dateTrx = null;
		
		try {
		
			dateTrx = FormatUtils.getDateSystemXMLGregorian();
			documento = new com.belejanor.switcher.bimo.pacs.camt_998_431.Document();
			com.belejanor.switcher.bimo.pacs.camt_998_431.HeaderData hdr = new 
					com.belejanor.switcher.bimo.pacs.camt_998_431.HeaderData();
			com.belejanor.switcher.bimo.pacs.camt_998_431.OrigIdData org = new  
					com.belejanor.switcher.bimo.pacs.camt_998_431.OrigIdData();
			com.belejanor.switcher.bimo.pacs.camt_998_431.ServiceData srvData =  new 
					com.belejanor.switcher.bimo.pacs.camt_998_431.ServiceData();
			
			srvData.setIdServ("Rechazar Cobro RQ");
			srvData.setVersServ("1.0");
			org.setChannel("IFISBIMO"); //IFISBIMO
			org.setApp(iso.getISO_041_CardAcceptorID()); //VENT, APP, ETC.
			org.setService(srvData);
			org.setOtherId(iso.getISO_023_CardSeq()); //celular
			
			hdr.setOrigId(org);
			hdr.setSender(MemoryGlobal.IdBIMOEfi);
			hdr.setReceiver(MemoryGlobal.IdBIMOBanred);
			
			String secuencialOne = GeneralUtils.GetSecuencialNumeric(10);
			com.belejanor.switcher.bimo.pacs.camt_998_431.MjeData mge = new 
					com.belejanor.switcher.bimo.pacs.camt_998_431.MjeData();
			mge.setType("camt.998.431");
			mge.setRoR(com.belejanor.switcher.bimo.pacs.camt_998_431.RoRCod.REQ);
			mge.setIdMge(MemoryGlobal.IdBIMOEfi + "_" + iso.getISO_011_SysAuditNumber() 
							+ "_" + secuencialOne); //yyyyMMddHHmmss				
			mge.setOpeDate(dateTrx);
			
			hdr.setMge(mge);
			hdr.setPssblDplct(false);
			hdr.setPrty(com.belejanor.switcher.bimo.pacs.camt_998_431.Priority.NORM);
			hdr.setCpyDplct("NORM");
			
			com.belejanor.switcher.bimo.pacs.camt_998_431.ProprietaryMessageV02 prtyMsg =
					new com.belejanor.switcher.bimo.pacs.camt_998_431.ProprietaryMessageV02();
			
			com.belejanor.switcher.bimo.pacs.camt_998_431.GroupHeader grhdr =  
					new com.belejanor.switcher.bimo.pacs.camt_998_431.GroupHeader();
			grhdr.setMsgId(MemoryGlobal.IdBIMOEfi + "_" + iso.getISO_011_SysAuditNumber()
						    + "_" + secuencialOne); //yyyyMMddHHmmss
			grhdr.setCreDtTm(dateTrx);
			grhdr.setNbOfTxs(BigInteger.valueOf(1));
			
			com.belejanor.switcher.bimo.pacs.camt_998_431.SettlementInstruction4 sttInf =
					new com.belejanor.switcher.bimo.pacs.camt_998_431.SettlementInstruction4();
			sttInf.setSttlmMtd(com.belejanor.switcher.bimo.pacs.camt_998_431
			.SettlementMethod1Code.INDA);
			grhdr.setSttInf(sttInf);
			
			
			com.belejanor.switcher.bimo.pacs.camt_998_431.BranchAndFinancialInstitutionIdentification5 efiAcq = new 
					com.belejanor.switcher.bimo.pacs.camt_998_431.BranchAndFinancialInstitutionIdentification5();
			com.belejanor.switcher.bimo.pacs.camt_998_431.ZFinancialInstitutionIdentification zAcq = new 
					com.belejanor.switcher.bimo.pacs.camt_998_431.ZFinancialInstitutionIdentification();
			com.belejanor.switcher.bimo.pacs.camt_998_431.ZGenericFinancialIdentification zgen = new 
					com.belejanor.switcher.bimo.pacs.camt_998_431.ZGenericFinancialIdentification();
			zgen.setId(MemoryGlobal.IdBIMOEfi);
			zAcq.setOthr(zgen);
			efiAcq.setFinInstnId(zAcq);
			
			com.belejanor.switcher.bimo.pacs.camt_998_431.BranchAndFinancialInstitutionIdentification5 efiAut = new 
					com.belejanor.switcher.bimo.pacs.camt_998_431.BranchAndFinancialInstitutionIdentification5();
			com.belejanor.switcher.bimo.pacs.camt_998_431.ZFinancialInstitutionIdentification zAut = new 
					com.belejanor.switcher.bimo.pacs.camt_998_431.ZFinancialInstitutionIdentification();
			com.belejanor.switcher.bimo.pacs.camt_998_431.ZGenericFinancialIdentification zgenAut = new 
					com.belejanor.switcher.bimo.pacs.camt_998_431.ZGenericFinancialIdentification();
			zgenAut.setId(MemoryGlobal.IdBIMOBanred);
			zAut.setOthr(zgenAut);
			efiAut.setFinInstnId(zAut);
			
			grhdr.setInstdAgt(efiAut);
			grhdr.setInstgAgt(efiAcq);
		
			/*Comienza estructura Propia de la Trx*/
			com.belejanor.switcher.bimo.pacs.camt_998_431.CollectRjct collect = 
					new CollectRjct();
			/*Importante el EndToEnd es el Codigo enviado en la CONSULTA LISTA DE COBROS OJO*/
			collect.setEndToEnd(iso.getISO_121_ExtendedData());
			collect.setReason(ReasonRjct.CANCEL);
		
			collect.setSttlmDt(FormatUtils.getDateXMLGregorianSimple(FormatUtils
					   .DateToString(iso.getISO_015_SettlementDatel(), "yyyy-MM-dd")));
			/*End estructura Propia de la Trx*/
			
			prtyMsg.setGrpHdr(grhdr);
			prtyMsg.setCollectRjct(collect);
			
			documento.setHeader(hdr);
			documento.setPrtryMsg(prtyMsg);
			
			this.codError = "000";
		
		} catch (Exception e) {
		
			this.codError = "070";
			this.desError = "ERROR EN PROCESOS" + GeneralUtils.ExceptionToString(null, e, false);
			log.WriteLogMonitor("Error modulo BimoParser::parseRechazarSolicituAutBimo ", TypeMonitor.error, e);
		}
		
		return documento;
	}
	
	public com.belejanor.switcher.pacs.camt_998_471.Document 
											parseReestablecePinAutBimo(wIso8583 iso){

			com.belejanor.switcher.pacs.camt_998_471.Document documento = null;
			XMLGregorianCalendar dateTrx = null;
			
			try {
			
				dateTrx = FormatUtils.getDateSystemXMLGregorian();
				documento = new com.belejanor.switcher.pacs.camt_998_471.Document();
				com.belejanor.switcher.pacs.camt_998_471.HeaderData hdr = new 
						com.belejanor.switcher.pacs.camt_998_471.HeaderData();
				com.belejanor.switcher.pacs.camt_998_471.OrigIdData org = new  
						com.belejanor.switcher.pacs.camt_998_471.OrigIdData();
				com.belejanor.switcher.pacs.camt_998_471.ServiceData srvData =  new 
						com.belejanor.switcher.pacs.camt_998_471.ServiceData();
				
				srvData.setIdServ("Regenerar contraseña RQ");
				srvData.setVersServ("1.0");
				org.setChannel("IFISBIMO"); //IFISBIMO
				org.setApp(iso.getISO_041_CardAcceptorID()); //VENT, APP, ETC.
				org.setService(srvData);
				org.setOtherId(iso.getISO_023_CardSeq()); //celular
				
				hdr.setOrigId(org);
				hdr.setSender(MemoryGlobal.IdBIMOEfi);
				hdr.setReceiver(MemoryGlobal.IdBIMOBanred);
				
				com.belejanor.switcher.pacs.camt_998_471.MjeData mge = new 
				com.belejanor.switcher.pacs.camt_998_471.MjeData();
				mge.setType("camt.998.471");
				mge.setRoR(com.belejanor.switcher.pacs.camt_998_471.RoRCod.REQ);
				mge.setIdMge(iso.getISO_011_SysAuditNumber()); //yyyyMMddHHmmss				
				mge.setOpeDate(dateTrx);
				
				hdr.setMge(mge);
				hdr.setPssblDplct(false);
				hdr.setPrty(com.belejanor.switcher.pacs.camt_998_471.Priority.NORM);
				hdr.setCpyDplct("NORM");
				
				com.belejanor.switcher.pacs.camt_998_471.ProprietaryMessageV02 prtyMsg =
						new com.belejanor.switcher.pacs.camt_998_471.ProprietaryMessageV02();
				
				com.belejanor.switcher.pacs.camt_998_471.GroupHeader grhdr = new 
				com.belejanor.switcher.pacs.camt_998_471.GroupHeader();
				grhdr.setMsgId(iso.getISO_011_SysAuditNumber()); //yyyyMMddHHmmss
				grhdr.setCreDtTm(dateTrx);
				grhdr.setNbOfTxs(BigInteger.valueOf(1));
				
				com.belejanor.switcher.pacs.camt_998_471.SettlementInstruction4 sttInf =
						new com.belejanor.switcher.pacs.camt_998_471.SettlementInstruction4();
				sttInf.setSttlmMtd(com.belejanor.switcher.pacs.camt_998_471
				.SettlementMethod1Code.INDA);
				grhdr.setSttInf(sttInf);
				
				
				com.belejanor.switcher.pacs.camt_998_471.BranchAndFinancialInstitutionIdentification6 efiAcq = new 
						com.belejanor.switcher.pacs.camt_998_471.BranchAndFinancialInstitutionIdentification6();
				com.belejanor.switcher.pacs.camt_998_471.ZFinancialInstitutionIdentification zAcq = new 
						com.belejanor.switcher.pacs.camt_998_471.ZFinancialInstitutionIdentification();
				com.belejanor.switcher.pacs.camt_998_471.ZGenericFinancialIdentification zgen = new 
						com.belejanor.switcher.pacs.camt_998_471.ZGenericFinancialIdentification();
				zgen.setId(MemoryGlobal.IdBIMOEfi);
				zAcq.setOthr(zgen);
				efiAcq.setFinInstnId(zAcq);
				
				com.belejanor.switcher.pacs.camt_998_471.BranchAndFinancialInstitutionIdentification6 efiAut = new 
						com.belejanor.switcher.pacs.camt_998_471.BranchAndFinancialInstitutionIdentification6();
				com.belejanor.switcher.pacs.camt_998_471.ZFinancialInstitutionIdentification zAut = new 
						com.belejanor.switcher.pacs.camt_998_471.ZFinancialInstitutionIdentification();
				com.belejanor.switcher.pacs.camt_998_471.ZGenericFinancialIdentification zgenAut = new 
						com.belejanor.switcher.pacs.camt_998_471.ZGenericFinancialIdentification();
				zgenAut.setId(MemoryGlobal.IdBIMOBanred);
				zAut.setOthr(zgenAut);
				efiAut.setFinInstnId(zAut);
				
				grhdr.setInstdAgt(efiAut);
				grhdr.setInstgAgt(efiAcq);
				
				RegeneratePIN pin = new RegeneratePIN();
				pin.setPhone(iso.getISO_023_CardSeq());
				pin.setSttlmDt(FormatUtils.getDateXMLGregorianSimple(FormatUtils
				.DateToString(iso.getISO_015_SettlementDatel(), "yyyy-MM-dd")));
				
				prtyMsg.setGrpHdr(grhdr);
				prtyMsg.setRegeneratePIN(pin);
				
				documento.setHeader(hdr);
				documento.setPrtryMsg(prtyMsg);
				
				this.codError = "000";
			
			} catch (Exception e) {
			
				this.codError = "070";
				this.desError = "ERROR EN PROCESOS" + GeneralUtils.ExceptionToString(null, e, false);
				log.WriteLogMonitor("Error modulo BimoParser::parseReestablecePinAutBimo ", TypeMonitor.error, e);
			}
			
			return documento;
	}
	
	public com.belejanor.switcher.bimo.pacs.camt_998_381.Document 
											parseSolicitudCobroAutBimo(wIso8583 iso){

			com.belejanor.switcher.bimo.pacs.camt_998_381.Document documento = null;
			XMLGregorianCalendar dateTrx = null;
			
			try {
			
				dateTrx = FormatUtils.getDateSystemXMLGregorian();
				documento = new com.belejanor.switcher.bimo.pacs.camt_998_381.Document();
				com.belejanor.switcher.bimo.pacs.camt_998_381.HeaderData hdr = new 
						com.belejanor.switcher.bimo.pacs.camt_998_381.HeaderData();
				com.belejanor.switcher.bimo.pacs.camt_998_381.OrigIdData org = new  
						com.belejanor.switcher.bimo.pacs.camt_998_381.OrigIdData();
				com.belejanor.switcher.bimo.pacs.camt_998_381.ServiceData srvData =  new 
						com.belejanor.switcher.bimo.pacs.camt_998_381.ServiceData();
				
				srvData.setIdServ("Solicitud Cobro RQ");
				srvData.setVersServ("1.0");
				org.setChannel("IFISBIMO"); //IFISBIMO
				org.setApp(iso.getISO_041_CardAcceptorID()); //VENT, APP, ETC.
				org.setService(srvData);
				org.setOtherId(iso.getISO_023_CardSeq()); //celular
				
				hdr.setOrigId(org);
				hdr.setSender(MemoryGlobal.IdBIMOEfi);
				hdr.setReceiver(MemoryGlobal.IdBIMOBanred);
				
				com.belejanor.switcher.bimo.pacs.camt_998_381.MjeData mge = new 
						com.belejanor.switcher.bimo.pacs.camt_998_381.MjeData();
				mge.setType("camt.998.381");
				mge.setRoR(com.belejanor.switcher.bimo.pacs.camt_998_381.RoRCod.REQ);
				mge.setIdMge(iso.getISO_011_SysAuditNumber()); //yyyyMMddHHmmss				
				mge.setOpeDate(dateTrx);
				
				hdr.setMge(mge);
				hdr.setPssblDplct(false);
				hdr.setPrty(com.belejanor.switcher.bimo.pacs.camt_998_381.Priority.NORM);
				hdr.setCpyDplct("NORM");
				
				com.belejanor.switcher.bimo.pacs.camt_998_381.ProprietaryMessageV02 prtyMsg =
						new com.belejanor.switcher.bimo.pacs.camt_998_381.ProprietaryMessageV02();
				
				com.belejanor.switcher.bimo.pacs.camt_998_381.GroupHeader grhdr = new 
						com.belejanor.switcher.bimo.pacs.camt_998_381.GroupHeader();
				grhdr.setMsgId(iso.getISO_011_SysAuditNumber()); //yyyyMMddHHmmss
				grhdr.setCreDtTm(dateTrx);
				grhdr.setNbOfTxs(BigInteger.valueOf(1));
				
				com.belejanor.switcher.bimo.pacs.camt_998_381.SettlementInstruction4 sttInf =
						new com.belejanor.switcher.bimo.pacs.camt_998_381.SettlementInstruction4();
				sttInf.setSttlmMtd(com.belejanor.switcher.bimo.pacs.camt_998_381
						.SettlementMethod1Code.INDA);
				grhdr.setSttInf(sttInf);
				
				
				com.belejanor.switcher.bimo.pacs.camt_998_381.BranchAndFinancialInstitutionIdentification5 efiAcq = new 
						com.belejanor.switcher.bimo.pacs.camt_998_381.BranchAndFinancialInstitutionIdentification5();
				FinancialInstitutionIdentification8 zAcq = new FinancialInstitutionIdentification8();
				GenericFinancialIdentification1 zgen = new GenericFinancialIdentification1();
				zgen.setId(MemoryGlobal.IdBIMOEfi);
				zAcq.setOthr(zgen);
				efiAcq.setFinInstnId(zAcq);
				
				com.belejanor.switcher.bimo.pacs.camt_998_381.BranchAndFinancialInstitutionIdentification5 efiAut = new 
						com.belejanor.switcher.bimo.pacs.camt_998_381.BranchAndFinancialInstitutionIdentification5();
				FinancialInstitutionIdentification8 zAut = new FinancialInstitutionIdentification8();
				GenericFinancialIdentification1 zgen2 = new GenericFinancialIdentification1();
				zgen2.setId(MemoryGlobal.IdBIMOBanred);
				zAut.setOthr(zgen);
				efiAut.setFinInstnId(zAut);
				
				grhdr.setInstdAgt(efiAut);
				grhdr.setInstgAgt(efiAcq);
				
				
				CollectRq collect = new CollectRq();
			    PaymentIdentification pym = new PaymentIdentification();
			    pym.setEndToEndId(iso.getISO_037_RetrievalReferenceNumber()); //Secuencial de 7
				pym.setTxId("0000000");
				collect.setPmtId(pym);
				ActiveOrHistoricCurrencyAndAmount ammount = new ActiveOrHistoricCurrencyAndAmount();
				ammount.setCcy(iso.getISO_120_ExtendedData()); //Codigo de la Moneda
				ammount.setValue(BigDecimal.valueOf(iso.getISO_004_AmountTransaction())); //Monto del Cobro
				collect.setIntrBkSttlmAmt(ammount);
			
				ContactDetails contactOrg = new ContactDetails();
				contactOrg.setPhneNb(iso.getISO_023_CardSeq()); //Celular Ordenante
				collect.setOrgn(contactOrg);
				
				ContactDetails contactDest = new ContactDetails();
				contactDest.setPhneNb(iso.getISO_103_AccountID_2()); //Celular Destino
				collect.setDst(contactDest);
				collect.setSttlmDt(FormatUtils.getDateXMLGregorianSimple(FormatUtils
				.DateToString(iso.getISO_015_SettlementDatel(), "yyyy-MM-dd")));
				
				SupplementaryData supp = new SupplementaryData();
				Envelope env = new Envelope();
				ContentsV01 cont = new ContentsV01();
				cont.setCustRef(iso.getISO_121_ExtendedData());
				env.setCnts(cont);
				supp.setEnvlp(env);
				
				collect.setSplmtryData(supp);
				
				prtyMsg.setGrpHdr(grhdr);
				prtyMsg.setCollectRq(collect);
				
				documento.setHeader(hdr);
				documento.setPrtryMsg(prtyMsg);
				
				this.codError = "000";
			
			} catch (Exception e) {
			
				this.codError = "070";
				this.desError = "ERROR EN PROCESOS" + GeneralUtils.ExceptionToString(null, e, false);
				log.WriteLogMonitor("Error modulo BimoParser::parseReestablecePinAutBimo ", TypeMonitor.error, e);
			}
			
				return documento;
	}
	
	public com.belejanor.switcher.bimo.pacs.camt_998_371.Document 
									        parseListarPagosCobrosAutBimo(wIso8583 iso){

		com.belejanor.switcher.bimo.pacs.camt_998_371.Document documento = null;
		XMLGregorianCalendar dateTrx = null;
		
		try {
		
			dateTrx = FormatUtils.getDateSystemXMLGregorian();
			documento = new com.belejanor.switcher.bimo.pacs.camt_998_371.Document();
			com.belejanor.switcher.bimo.pacs.camt_998_371.HeaderData hdr = new 
					com.belejanor.switcher.bimo.pacs.camt_998_371.HeaderData();
			com.belejanor.switcher.bimo.pacs.camt_998_371.OrigIdData org = new  
					com.belejanor.switcher.bimo.pacs.camt_998_371.OrigIdData();
			com.belejanor.switcher.bimo.pacs.camt_998_371.ServiceData srvData =  new 
					com.belejanor.switcher.bimo.pacs.camt_998_371.ServiceData();
			
			srvData.setIdServ("Consulta de Cobros Pendientes RQ");
			srvData.setVersServ("1.0");
			org.setChannel("IFISBIMO"); //IFISBIMO
			org.setApp(iso.getISO_041_CardAcceptorID()); //VENT, APP, ETC.
			org.setService(srvData);
			org.setOtherId(iso.getISO_023_CardSeq()); //celular
			
			hdr.setOrigId(org);
			hdr.setSender(MemoryGlobal.IdBIMOEfi);
			hdr.setReceiver(MemoryGlobal.IdBIMOBanred);
			
			com.belejanor.switcher.bimo.pacs.camt_998_371.MjeData mge = new 
					com.belejanor.switcher.bimo.pacs.camt_998_371.MjeData();
			mge.setType("camt.998.371");
			mge.setRoR(com.belejanor.switcher.bimo.pacs.camt_998_371.RoRCod.REQ);
			mge.setIdMge(iso.getISO_011_SysAuditNumber()); //yyyyMMddHHmmss				
			mge.setOpeDate(dateTrx);
			
			hdr.setMge(mge);
			hdr.setPssblDplct(false);
			hdr.setPrty(com.belejanor.switcher.bimo.pacs.camt_998_371.Priority.NORM);
			hdr.setCpyDplct("NORM");
			
			com.belejanor.switcher.bimo.pacs.camt_998_371.ProprietaryMessageV02 prtyMsg =
					new  com.belejanor.switcher.bimo.pacs.camt_998_371.ProprietaryMessageV02();
			
			com.belejanor.switcher.bimo.pacs.camt_998_371.GroupHeader grhdr = new 
					com.belejanor.switcher.bimo.pacs.camt_998_371.GroupHeader();
			grhdr.setMsgId(iso.getISO_011_SysAuditNumber()); //yyyyMMddHHmmss
			grhdr.setCreDtTm(dateTrx);
			grhdr.setNbOfTxs(BigInteger.valueOf(1));
			
			com.belejanor.switcher.bimo.pacs.camt_998_371.SettlementInstruction4 sttInf =
					new com.belejanor.switcher.bimo.pacs.camt_998_371.SettlementInstruction4();
			sttInf.setSttlmMtd(com.belejanor.switcher.bimo.pacs.camt_998_371
					.SettlementMethod1Code.INDA);
			grhdr.setSttInf(sttInf);
			
			
			com.belejanor.switcher.bimo.pacs.camt_998_371.BranchAndFinancialInstitutionIdentification5 efiAcq = new 
					com.belejanor.switcher.bimo.pacs.camt_998_371.BranchAndFinancialInstitutionIdentification5();
			com.belejanor.switcher.bimo.pacs.camt_998_371.FinancialInstitutionIdentification8 zAcq = new 
					com.belejanor.switcher.bimo.pacs.camt_998_371.FinancialInstitutionIdentification8();
			com.belejanor.switcher.bimo.pacs.camt_998_371.GenericFinancialIdentification1 zgen = new 
					com.belejanor.switcher.bimo.pacs.camt_998_371.GenericFinancialIdentification1();
			zgen.setId(MemoryGlobal.IdBIMOEfi);
			zAcq.setOthr(zgen);
			efiAcq.setFinInstnId(zAcq);
			
			com.belejanor.switcher.bimo.pacs.camt_998_371.BranchAndFinancialInstitutionIdentification5 efiAut = new 
					com.belejanor.switcher.bimo.pacs.camt_998_371.BranchAndFinancialInstitutionIdentification5();
			com.belejanor.switcher.bimo.pacs.camt_998_371.FinancialInstitutionIdentification8 zAut = new 
					com.belejanor.switcher.bimo.pacs.camt_998_371.FinancialInstitutionIdentification8();
			com.belejanor.switcher.bimo.pacs.camt_998_371.GenericFinancialIdentification1 zgen2 = new 
					com.belejanor.switcher.bimo.pacs.camt_998_371.GenericFinancialIdentification1();
			zgen2.setId(MemoryGlobal.IdBIMOBanred);
			zAut.setOthr(zgen2);
			efiAut.setFinInstnId(zAut);
			
			
			grhdr.setInstdAgt(efiAut);
			grhdr.setInstgAgt(efiAcq);
			
			QryPendPayments qry = new QryPendPayments();
			qry.setPhone(iso.getISO_023_CardSeq());
			if(iso.getISO_120_ExtendedData().equals("P")) //Tipo Operacion Pagos, Cobros
				qry.setOperation("PAYMENTS");
			else 
				qry.setOperation("COLLECTIONS");
			qry.setFilter(iso.getISO_121_ExtendedData());//PND|PAY|RJCT|DUE|ALL Filtro -> Pendientes, Pagadas, Rechazadas, Expiradas, Todas
			qry.setSttlmDt(FormatUtils.getDateXMLGregorianSimple(FormatUtils
					.DateToString(iso.getISO_015_SettlementDatel(), "yyyy-MM-dd")));
			
			
			prtyMsg.setGrpHdr(grhdr);
			prtyMsg.setQryPendPayments(qry);
			
			documento.setHeader(hdr);
			documento.setPrtryMsg(prtyMsg);
			
			this.codError = "000";
		
		} catch (Exception e) {
		
			this.codError = "070";
			this.desError = "ERROR EN PROCESOS" + GeneralUtils.ExceptionToString(null, e, false);
			log.WriteLogMonitor("Error modulo BimoParser::parseReestablecePinAutBimo ", TypeMonitor.error, e);
		}
		
	    return documento;
	}
	
	public com.belejanor.switcher.bimo.pacs.camt_998_461.Document 
    													parsevalidarOTPAutBimo(wIso8583 iso){
		
		com.belejanor.switcher.bimo.pacs.camt_998_461.Document documento = null;
		XMLGregorianCalendar dateTrx = null;
		
		try {
		
			dateTrx = FormatUtils.getDateSystemXMLGregorian();
			documento = new com.belejanor.switcher.bimo.pacs.camt_998_461.Document();
			com.belejanor.switcher.bimo.pacs.camt_998_461.HeaderData hdr = new 
					com.belejanor.switcher.bimo.pacs.camt_998_461.HeaderData();
			com.belejanor.switcher.bimo.pacs.camt_998_461.OrigIdData org = new  
					com.belejanor.switcher.bimo.pacs.camt_998_461.OrigIdData();
			com.belejanor.switcher.bimo.pacs.camt_998_461.ServiceData srvData =  new 
					com.belejanor.switcher.bimo.pacs.camt_998_461.ServiceData();
			
			srvData.setIdServ("Validar OTP RQ");
			srvData.setVersServ("1.0");
			org.setChannel("IFISBIMO"); //IFISBIMO
			org.setApp(iso.getISO_041_CardAcceptorID()); //VENT, APP, ETC.
			org.setService(srvData);
			org.setOtherId(iso.getISO_023_CardSeq()); //celular
			
			hdr.setOrigId(org);
			hdr.setSender(MemoryGlobal.IdBIMOEfi);
			hdr.setReceiver(MemoryGlobal.IdBIMOBanred);
			
			com.belejanor.switcher.bimo.pacs.camt_998_461.MjeData mge = new 
					com.belejanor.switcher.bimo.pacs.camt_998_461.MjeData();
			mge.setType("camt.998.461");
			mge.setRoR(com.belejanor.switcher.bimo.pacs.camt_998_461.RoRCod.REQ);
			mge.setIdMge(iso.getISO_011_SysAuditNumber()); //yyyyMMddHHmmss				
			mge.setOpeDate(dateTrx);
			
			hdr.setMge(mge);
			hdr.setPssblDplct(false);
			hdr.setPrty(com.belejanor.switcher.bimo.pacs.camt_998_461.Priority.NORM);
			hdr.setCpyDplct("NORM");
			
			com.belejanor.switcher.bimo.pacs.camt_998_461.ProprietaryMessageV02 prtyMsg =
					new  com.belejanor.switcher.bimo.pacs.camt_998_461.ProprietaryMessageV02();
			
			com.belejanor.switcher.bimo.pacs.camt_998_461.GroupHeader grhdr = new 
					com.belejanor.switcher.bimo.pacs.camt_998_461.GroupHeader();
			grhdr.setMsgId(iso.getISO_011_SysAuditNumber()); //yyyyMMddHHmmss
			grhdr.setCreDtTm(dateTrx);
			grhdr.setNbOfTxs(BigInteger.valueOf(1));
			
			com.belejanor.switcher.bimo.pacs.camt_998_461.SettlementInstruction4 sttInf =
					new com.belejanor.switcher.bimo.pacs.camt_998_461.SettlementInstruction4();
			sttInf.setSttlmMtd(com.belejanor.switcher.bimo.pacs.camt_998_461
					.SettlementMethod1Code.INDA);
			grhdr.setSttInf(sttInf);
			
			
			com.belejanor.switcher.bimo.pacs.camt_998_461.BranchAndFinancialInstitutionIdentification6 efiAcq = new 
					com.belejanor.switcher.bimo.pacs.camt_998_461.BranchAndFinancialInstitutionIdentification6();
			com.belejanor.switcher.bimo.pacs.camt_998_461.ZFinancialInstitutionIdentification zAcq = new 
					com.belejanor.switcher.bimo.pacs.camt_998_461.ZFinancialInstitutionIdentification();
			com.belejanor.switcher.bimo.pacs.camt_998_461.ZGenericFinancialIdentification zgen = new 
					com.belejanor.switcher.bimo.pacs.camt_998_461.ZGenericFinancialIdentification();
			zgen.setId(MemoryGlobal.IdBIMOEfi);
			zAcq.setOthr(zgen);
			efiAcq.setFinInstnId(zAcq);
			
			com.belejanor.switcher.bimo.pacs.camt_998_461.BranchAndFinancialInstitutionIdentification6 efiAut = new 
					com.belejanor.switcher.bimo.pacs.camt_998_461.BranchAndFinancialInstitutionIdentification6();
			com.belejanor.switcher.bimo.pacs.camt_998_461.ZFinancialInstitutionIdentification zAut = new 
					com.belejanor.switcher.bimo.pacs.camt_998_461.ZFinancialInstitutionIdentification();
			com.belejanor.switcher.bimo.pacs.camt_998_461.ZGenericFinancialIdentification zgen2 = new 
					com.belejanor.switcher.bimo.pacs.camt_998_461.ZGenericFinancialIdentification();
			zgen2.setId(MemoryGlobal.IdBIMOBanred);
			zAut.setOthr(zgen2);
			efiAut.setFinInstnId(zAut);
			
			
			grhdr.setInstdAgt(efiAut);
			grhdr.setInstgAgt(efiAcq);
			
			/*Comienzo estructura Propia*/
			
			com.belejanor.switcher.bimo.pacs.camt_998_461.ValidateOTP validation = 
					new ValidateOTP();
			validation.setPhone(iso.getISO_023_CardSeq());
			validation.setOTP(iso.getISO_052_PinBlock());
			com.belejanor.switcher.bimo.pacs.camt_998_461.ActiveOrHistoricCurrencyAndAmount ammountclass =
					new com.belejanor.switcher.bimo.pacs.camt_998_461.ActiveOrHistoricCurrencyAndAmount();
			ammountclass.setCcy(iso.getISO_120_ExtendedData());
			ammountclass.setValue(BigDecimal.valueOf(iso.getISO_004_AmountTransaction()));
			
			validation.setAmount(ammountclass);
			validation.setSttlmDt(FormatUtils.getDateXMLGregorianSimple(FormatUtils
					.DateToString(iso.getISO_015_SettlementDatel(), "yyyy-MM-dd")));
			
			/*End estructura Propia*/
			prtyMsg.setGrpHdr(grhdr);
			prtyMsg.setValidateOTP(validation);
			
			documento.setHeader(hdr);
			documento.setPrtryMsg(prtyMsg);
			
			this.codError = "000";
		
		} catch (Exception e) {
		
			this.codError = "070";
			this.desError = "ERROR EN PROCESOS" + GeneralUtils.ExceptionToString(null, e, false);
			log.WriteLogMonitor("Error modulo BimoParser::parsevalidarOTPAutBimo ", TypeMonitor.error, e);
		}
		
	    return documento;
		
	}
	
}
