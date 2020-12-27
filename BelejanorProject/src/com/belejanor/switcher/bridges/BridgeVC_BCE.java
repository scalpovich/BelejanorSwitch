package com.belejanor.switcher.bridges;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Random;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import com.belejanor.switcher.authorizations.ITransactionsVCBridge;
import com.belejanor.switcher.cscoreswitch.Iso8583;
import com.belejanor.switcher.cscoreswitch.csProcess;
import com.belejanor.switcher.logger.Logger;
import com.belejanor.switcher.logger.LoggerConfig.TypeLog;
import com.belejanor.switcher.logger.LoggerConfig.TypeMonitor;
import com.belejanor.switcher.logger.LoggerConfig.TypeWriteLog;
import com.belejanor.switcher.memcached.MemoryGlobal;
import com.belejanor.switcher.parser.BceVCParser;
import com.belejanor.switcher.utils.FormatUtils;
import com.belejanor.switcher.utils.StringUtils;

import urn.iso.std.iso20022.tech.xsd.pacs_002_001_05.BranchAndFinancialInstitutionIdentification5;
import urn.iso.std.iso20022.tech.xsd.pacs_002_001_05.DocumentRespuesta;
import urn.iso.std.iso20022.tech.xsd.pacs_002_001_05.FIToFIPaymentStatusReportV05;
import urn.iso.std.iso20022.tech.xsd.pacs_002_001_05.FinancialInstitutionIdentification8;
import urn.iso.std.iso20022.tech.xsd.pacs_002_001_05.GroupHeader53;
import urn.iso.std.iso20022.tech.xsd.pacs_002_001_05.OrganisationIdentification8;
import urn.iso.std.iso20022.tech.xsd.pacs_002_001_05.OriginalGroupHeader1;
import urn.iso.std.iso20022.tech.xsd.pacs_002_001_05.Party11Choice;
import urn.iso.std.iso20022.tech.xsd.pacs_002_001_05.PartyIdentification43;
import urn.iso.std.iso20022.tech.xsd.pacs_002_001_05.PaymentTransaction43;
import urn.iso.std.iso20022.tech.xsd.pacs_002_001_05.StatusReason6Choice;
import urn.iso.std.iso20022.tech.xsd.pacs_002_001_05.StatusReasonInformation9;
import urn.iso.std.iso20022.tech.xsd.pacs_002_001_05.TransactionGroupStatus3Code;
import urn.iso.std.iso20022.tech.xsd.pacs_002_001_05.TransactionIndividualStatus3Code;
import urn.iso.std.iso20022.tech.xsd.pacs_003_001_04.DocumentRetiro;
import urn.iso.std.iso20022.tech.xsd.pacs_004_001_04.DocumentReverso;
import urn.iso.std.iso20022.tech.xsd.pacs_008_001_04.DocumentDeposito;
import urn.iso.std.iso20022.tech.xsd.pacs_008_001_04.DocumentTransferencia;

public class BridgeVC_BCE implements ITransactionsVCBridge{
	
	private String SecuencialTrx;
	private Logger log;
	
	public String getSecuencialTrx() {
		return SecuencialTrx;
	}

	public void setSecuencialTrx(String secuencialTrx) {
		SecuencialTrx = secuencialTrx;
	}

	public BridgeVC_BCE(){
		
		this.log = new  Logger();
	}
	
	@Override
	public DocumentRespuesta ProcesaDepositoVC_BCE(DocumentDeposito documentDeposito, String IP) {
		
		DocumentRespuesta document = null;
		Iso8583 iso = null;
		log.WriteLog(documentDeposito, TypeLog.bceacq, TypeWriteLog.file);
		try {
			BceVCParser parser = new BceVCParser();
			iso = parser.parseDocumentDepositoVC(documentDeposito);
			if(iso.getISO_039_ResponseCode().equals("000")){
				
				csProcess processor = new csProcess();
				iso = processor.ProcessTransactionMain(iso, IP);
				if(iso.getISO_039_ResponseCode().equals("000")){
					
					document = processResponseGeneric(iso, documentDeposito,null, 
							TransactionGroupStatus3Code.ACSC, TransactionIndividualStatus3Code.ACSC);
					
				}else{
					
					document = processResponseGeneric(iso, documentDeposito,"TA01", 
							TransactionGroupStatus3Code.RJCT, TransactionIndividualStatus3Code.RJCT);
				}
				
			}else{
				
				if(StringUtils.IsNullOrEmpty(iso.getISO_104_TranDescription()))
					iso.setISO_104_TranDescription("CH21");
				document = new DocumentRespuesta();
				document = processResponseGeneric(iso, documentDeposito,iso.getISO_104_TranDescription(), 
						TransactionGroupStatus3Code.RJCT, TransactionIndividualStatus3Code.RJCT);
			}
			
		} catch (Exception e) {
			
			log.WriteLogMonitor("Error modulo BridgeVC_BCE::ProcesaDepositoVC_BCE ", TypeMonitor.error, e);
			
		}finally {
			this.setSecuencialTrx(iso.getISO_011_SysAuditNumber());
		}
		
		log.WriteLog(document, TypeLog.bceacq, TypeWriteLog.file);
		return document;
	}

	@Override
	public DocumentRespuesta ProcesaRetiroVC_BCE(DocumentRetiro documentRetiro, String IP) {
		
		DocumentRespuesta document = null;
		Iso8583 iso = null;
		log.WriteLog(documentRetiro, TypeLog.bceacq, TypeWriteLog.file);
		try {
			BceVCParser parser = new BceVCParser();
			iso = parser.parseDocumentRetiroVC(documentRetiro);
			if(iso.getISO_039_ResponseCode().equals("000")){
				
				csProcess processor = new csProcess();
				iso = processor.ProcessTransactionMain(iso, IP);
				if(iso.getISO_039_ResponseCode().equals("000")){
					
					document = processResponseGeneric(iso, documentRetiro,null, 
							TransactionGroupStatus3Code.ACSC, TransactionIndividualStatus3Code.ACSC);
					
				}else{
					
					document = processResponseGeneric(iso, documentRetiro,"TA01", 
							TransactionGroupStatus3Code.RJCT, TransactionIndividualStatus3Code.RJCT);
				}
				
			}else{
				if(StringUtils.IsNullOrEmpty(iso.getISO_104_TranDescription()))
					iso.setISO_104_TranDescription("CH21");
				document = new DocumentRespuesta();
				document = processResponseGeneric(iso, documentRetiro,iso.getISO_104_TranDescription(), 
						TransactionGroupStatus3Code.RJCT, TransactionIndividualStatus3Code.RJCT);
			}
			
		} catch (Exception e) {
			
			log.WriteLogMonitor("Error modulo BridgeVC_BCE::ProcesaRetiroVC_BCE ", TypeMonitor.error, e);
			
		}finally {
			this.setSecuencialTrx(iso.getISO_011_SysAuditNumber());
		}
		log.WriteLog(document, TypeLog.bceacq, TypeWriteLog.file);
		return document;
	}

	@Override
	public DocumentRespuesta ProcesarreversoVC_BCE(DocumentReverso documentReverso, String IP) {
		
		DocumentRespuesta document = null;
		Iso8583 iso = null;
		log.WriteLog(documentReverso, TypeLog.bceacq, TypeWriteLog.file);
		try {
			BceVCParser parser = new BceVCParser();
			iso = parser.parseDocumentReversoTecnicoVC(documentReverso, IP);
			if(iso.getISO_039_ResponseCode().equals("000")){
				
				csProcess processor = new csProcess();
				iso = processor.ProcessTransactionMain(iso, IP);
				if(iso.getISO_039_ResponseCode().equals("000")){
					
					document = processResponseGeneric(iso, documentReverso,null, 
							TransactionGroupStatus3Code.ACSC, TransactionIndividualStatus3Code.ACSC);
					
				}else{
					
					document = processResponseGeneric(iso, documentReverso,"TA01", 
							TransactionGroupStatus3Code.RJCT, TransactionIndividualStatus3Code.RJCT);
				}
				
			}else{
				if(StringUtils.IsNullOrEmpty(iso.getISO_104_TranDescription()))
					iso.setISO_104_TranDescription("CH21");
				document = new DocumentRespuesta();
				document = processResponseGeneric(iso, documentReverso,iso.getISO_104_TranDescription(), 
						TransactionGroupStatus3Code.RJCT, TransactionIndividualStatus3Code.RJCT);
			}
			
		} catch (Exception e) {
			
			log.WriteLogMonitor("Error modulo BridgeVC_BCE::ProcesarreversoVC_BCE ", TypeMonitor.error, e);
			
		}finally {
			this.setSecuencialTrx(iso.getISO_011_SysAuditNumber());
		}
		log.WriteLog(document, TypeLog.bceacq, TypeWriteLog.file);
		return document;
	}
	@Override
	public DocumentRespuesta ProcesarTransferenciaVC_BCE(DocumentTransferencia documentTransferencia, String IP) {
		DocumentRespuesta document = null;
		Iso8583 iso = null;
		log.WriteLog(documentTransferencia, TypeLog.bceacq, TypeWriteLog.file);
		try {
			BceVCParser parser = new BceVCParser();
			iso = parser.parseDocumentTransferenciaVC(documentTransferencia);
			if(iso.getISO_039_ResponseCode().equals("000")){
				
				csProcess processor = new csProcess();
				iso = processor.ProcessTransactionMain(iso, IP);
				if(iso.getISO_039_ResponseCode().equals("000")){
					
					document = processResponseGeneric(iso, documentTransferencia,null, 
							TransactionGroupStatus3Code.ACSC, TransactionIndividualStatus3Code.ACSC);
					
				}else{
					
					document = processResponseGeneric(iso, documentTransferencia,"TA01", 
							TransactionGroupStatus3Code.RJCT, TransactionIndividualStatus3Code.RJCT);
				}
				
			}else{
				if(StringUtils.IsNullOrEmpty(iso.getISO_104_TranDescription()))
					iso.setISO_104_TranDescription("CH21");
				document = new DocumentRespuesta();
				document = processResponseGeneric(iso, documentTransferencia,iso.getISO_104_TranDescription(), 
						TransactionGroupStatus3Code.RJCT, TransactionIndividualStatus3Code.RJCT);
			}
			
		} catch (Exception e) {
			
			log.WriteLogMonitor("Error modulo BridgeVC_BCE::ProcesarTransferenciaVC_BCE ", TypeMonitor.error, e);
			
		}finally {
			this.setSecuencialTrx(iso.getISO_011_SysAuditNumber());
		}
		log.WriteLog(document, TypeLog.bceacq, TypeWriteLog.file);
		return document;
	}
	
	@SuppressWarnings("unused")
	private DocumentRespuesta processResponseGeneric2(Iso8583 iso, Object documentBCE, 
			String StatusReason, TransactionGroupStatus3Code typeA, TransactionIndividualStatus3Code typeB){
		DocumentRespuesta document = new DocumentRespuesta();
		String pack = null;
		Random randomGenerator = new Random();
		int randomInt = randomGenerator.nextInt(99);
		try {
			
			if(documentBCE instanceof DocumentDeposito || documentBCE instanceof DocumentTransferencia){
				pack = "pacs.008.001.04";
			}else if (documentBCE instanceof DocumentRetiro) {
				pack= "pacs.003.001.01";
			}
			GroupHeader53 ghdr = new GroupHeader53();
			ghdr.setMsgId("RET" + MemoryGlobal.BCE_Efi_VC + FormatUtils.DateToString(new Date(), 
					"yyyyMMddHHmmssSSSSS") + String.valueOf(randomInt));
			GregorianCalendar gcal = (GregorianCalendar) GregorianCalendar.getInstance();
		    XMLGregorianCalendar xgcal = DatatypeFactory.newInstance()
		            .newXMLGregorianCalendar(gcal);
			ghdr.setCreDtTm(xgcal);
			
			FIToFIPaymentStatusReportV05 status = new FIToFIPaymentStatusReportV05();
			status.setGrpHdr(ghdr);
			document.setFIToFIPmtStsRpt(status);
			
			OriginalGroupHeader1 originalHeader = new OriginalGroupHeader1();
			originalHeader.setOrgnlMsgId(StringUtils.IsNullOrEmpty(iso.getISO_011_SysAuditNumber())?"N/D"
					          :iso.getISO_011_SysAuditNumber());
			originalHeader.setGrpSts(typeA);////
			originalHeader.setOrgnlMsgNmId(pack);
			XMLGregorianCalendar xmlDate = null;
			GregorianCalendar gc = new GregorianCalendar();
			gc.setTime(iso.getISO_012_LocalDatetime());
			xmlDate = DatatypeFactory.newInstance().newXMLGregorianCalendar(gc);
			originalHeader.setOrgnlCreDtTm(xmlDate);
			originalHeader.setOrgnlNbOfTxs(iso.getISO_022_PosEntryMode());
			
			BigDecimal dec = new BigDecimal(iso.getISO_004_AmountTransaction());
			dec = dec.setScale(2, RoundingMode.CEILING);
			
			originalHeader.setOrgnlCtrlSum(dec);
			document.getFIToFIPmtStsRpt().setOrgnlGrpInfAndSts(originalHeader);
			
			PaymentTransaction43 pay = new PaymentTransaction43();
			pay.setOrgnlEndToEndId(StringUtils.IsNullOrEmpty(iso.getISO_023_CardSeq())?"N/D"
					 :iso.getISO_023_CardSeq());
			pay.setOrgnlTxId(StringUtils.IsNullOrEmpty(iso.getISO_037_RetrievalReferenceNumber())?"N/D"
					:iso.getISO_037_RetrievalReferenceNumber());
			pay.setTxSts(typeB);////
			
			if(!typeA.equals(TransactionGroupStatus3Code.ACSC)){
				StatusReason6Choice staRz = new StatusReason6Choice();
				staRz.setCd(StatusReason);
				StatusReasonInformation9 stat = new StatusReasonInformation9();
				
				Party11Choice pr11 = new Party11Choice();
				OrganisationIdentification8 org = new OrganisationIdentification8();
				org.setAnyBIC(MemoryGlobal.BCE_Efi_VC);
				pr11.setOrgId(org);
				PartyIdentification43 party = new PartyIdentification43();
				party.setId(pr11);
				stat.setOrgtr(party); //aquiiiiiiiiiiiiii
				
				
				stat.setRsn(staRz);
				List<String> resultadoMensaje = new ArrayList<>();
				resultadoMensaje.add(iso.getISO_039p_ResponseDetail().replace("<*>", "")
						             .replace("<**>", "").toUpperCase());
				stat.setAddtlInf(resultadoMensaje);
				List<StatusReasonInformation9> lStat = new ArrayList<>();
				lStat.add(stat);
				pay.setStsRsnInf(lStat);
				
			}
		
			List<PaymentTransaction43> listPayment = new ArrayList<>();
			listPayment.add(pay);
			document.getFIToFIPmtStsRpt().setTxInfAndSts(listPayment);
		
		} catch (Exception e) {
			e.printStackTrace();
			log.WriteLogMonitor("Error modulo BridgeVC_BCE::processResponseInvalid ", TypeMonitor.error, e);
		}
		return document;
	}
	
	
	private DocumentRespuesta processResponseGeneric(Iso8583 iso, Object documentBCE, 
			String StatusReason, TransactionGroupStatus3Code typeA, TransactionIndividualStatus3Code typeB){
		DocumentRespuesta document = new DocumentRespuesta();
		String pack = null;
		Random randomGenerator = new Random();
		int randomInt = randomGenerator.nextInt(99);
		try {
			
			if(documentBCE instanceof DocumentDeposito || documentBCE instanceof DocumentTransferencia){
				pack = "pacs.008.001.04";
			}else if (documentBCE instanceof DocumentRetiro) {
				pack= "pacs.003.001.01";
			}
        ////////////////////////////////////////////////////////////INIT GRP_HDR///////////////////////////////////
			GroupHeader53 ghdr = new GroupHeader53();
			ghdr.setMsgId("RET" + MemoryGlobal.BCE_Efi_VC + FormatUtils.DateToString(new Date(), 
					"yyyyMMddHHmmssSSSSS") + String.valueOf(randomInt));
			GregorianCalendar gcal = (GregorianCalendar) GregorianCalendar.getInstance();
		    XMLGregorianCalendar xgcal = DatatypeFactory.newInstance()
		            .newXMLGregorianCalendar(gcal);
			ghdr.setCreDtTm(xgcal);
			
			BranchAndFinancialInstitutionIdentification5 orgAcq = new BranchAndFinancialInstitutionIdentification5();
			FinancialInstitutionIdentification8 finantialAcq = new FinancialInstitutionIdentification8();
			finantialAcq.setBICFI (MemoryGlobal.BCE_Efi_VC);
			orgAcq.setFinInstnId(finantialAcq);
			ghdr.setInstgAgt(orgAcq);
			
			
			BranchAndFinancialInstitutionIdentification5 orgAut = new BranchAndFinancialInstitutionIdentification5();
			FinancialInstitutionIdentification8 finantialAut = new FinancialInstitutionIdentification8();
			finantialAut.setBICFI(MemoryGlobal.BICFI_Bce);
			orgAut.setFinInstnId(finantialAut);
			ghdr.setInstdAgt(orgAut);
			
			FIToFIPaymentStatusReportV05 response = new FIToFIPaymentStatusReportV05();
			response.setGrpHdr(ghdr);
			
			document.setFIToFIPmtStsRpt(response);
			
			////////////////////////////////////////////////////////////FIN GRP_HDR///////////////////////////////////
			
			//////////////////////////////////////////////////////INIT OrgnlGrpInfAndSt //////////////////////////////
			
			OriginalGroupHeader1 originalHeader = new OriginalGroupHeader1();
			originalHeader.setOrgnlMsgId(StringUtils.IsNullOrEmpty(iso.getISO_011_SysAuditNumber())?"N/D"
					          :iso.getISO_011_SysAuditNumber());
			originalHeader.setGrpSts(typeA);////
			originalHeader.setOrgnlMsgNmId(pack);
			XMLGregorianCalendar xmlDate = null;
			GregorianCalendar gc = new GregorianCalendar();
			gc.setTime(iso.getISO_012_LocalDatetime());
			xmlDate = DatatypeFactory.newInstance().newXMLGregorianCalendar(gc);
			originalHeader.setOrgnlCreDtTm(xmlDate);
			originalHeader.setOrgnlNbOfTxs(iso.getISO_022_PosEntryMode());
			BigDecimal dec = new BigDecimal(iso.getISO_004_AmountTransaction());
			dec = dec.setScale(2, RoundingMode.CEILING);
			
			originalHeader.setOrgnlCtrlSum(dec);
			document.getFIToFIPmtStsRpt().setOrgnlGrpInfAndSts(originalHeader);
			
			PaymentTransaction43 pay = new PaymentTransaction43();
			pay.setOrgnlEndToEndId(StringUtils.IsNullOrEmpty(iso.getISO_023_CardSeq())?"N/D"
					 :iso.getISO_023_CardSeq());
			pay.setOrgnlTxId(StringUtils.IsNullOrEmpty(iso.getISO_037_RetrievalReferenceNumber())?"N/D"
					:iso.getISO_037_RetrievalReferenceNumber());
			pay.setTxSts(typeB);
			
			if(!typeA.equals(TransactionGroupStatus3Code.ACSC)){ //Si la transaccion fue exitosa
				
				originalHeader.setGrpSts(typeA); //RJCT transacccion denegada
		
				StatusReasonInformation9 statusInfoSTRUCT = new StatusReasonInformation9();
				PartyIdentification43 prtyId = new PartyIdentification43();
				Party11Choice prty11 = new Party11Choice();
				OrganisationIdentification8 org = new OrganisationIdentification8();
				org.setAnyBIC(MemoryGlobal.BCE_Efi_VC);
				prty11.setOrgId(org);
				prtyId.setId(prty11);
				
				statusInfoSTRUCT.setOrgtr(prtyId);
				StatusReason6Choice status6 = new StatusReason6Choice();
				status6.setCd(StatusReason);
				statusInfoSTRUCT.setRsn(status6);
				
				
				List<String> desRazonList =  new ArrayList<>();
				desRazonList.add(iso.getISO_039p_ResponseDetail().replace("<*>", "")
			             .replace("<**>", "").toUpperCase());
				statusInfoSTRUCT.setAddtlInf(desRazonList);
				
				List<StatusReasonInformation9> listStatusRsnInf = new ArrayList<>();
				listStatusRsnInf.add(statusInfoSTRUCT);
				originalHeader.setStsRsnInf(listStatusRsnInf);
				
				document.getFIToFIPmtStsRpt().setOrgnlGrpInfAndSts(originalHeader);
				
			}else{
				
				document.getFIToFIPmtStsRpt().setOrgnlGrpInfAndSts(originalHeader);
				
				PaymentTransaction43 pyTrx43 = new  PaymentTransaction43();
				pyTrx43.setOrgnlEndToEndId(StringUtils.IsNullOrEmpty(iso.getISO_023_CardSeq())?"N/D"
					 :iso.getISO_023_CardSeq());
				pyTrx43.setOrgnlTxId(StringUtils.IsNullOrEmpty(iso.getISO_037_RetrievalReferenceNumber())?"N/D"
						:iso.getISO_037_RetrievalReferenceNumber());
				pyTrx43.setTxSts(typeB);
				
				
				PartyIdentification43 party = new PartyIdentification43();
				Party11Choice party11 = new Party11Choice();
				OrganisationIdentification8 orgAcp = new OrganisationIdentification8();
				orgAcp.setAnyBIC(MemoryGlobal.BCE_Efi_VC);
				party11.setOrgId(orgAcp);
				party.setId(party11);
				
				StatusReasonInformation9 stat9 = new StatusReasonInformation9();
				stat9.setOrgtr(party);
				
				List<StatusReasonInformation9> listStat9 = new ArrayList<>();
				listStat9.add(stat9);
				pyTrx43.setStsRsnInf(listStat9);
				
				pyTrx43.setClrSysRef(iso.getISO_090_OriginalData());
				
				List<PaymentTransaction43> listPayment = new ArrayList<>();
				listPayment.add(pyTrx43);
				
				document.getFIToFIPmtStsRpt().setTxInfAndSts(listPayment);
				
			}
		
		} catch (Exception e) {
			e.printStackTrace();
			log.WriteLogMonitor("Error modulo BridgeVC_BCE::processResponseInvalid ", TypeMonitor.error, e);
		}
		return document;
	}

	
}
