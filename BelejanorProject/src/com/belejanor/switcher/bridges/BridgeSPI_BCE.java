package com.belejanor.switcher.bridges;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.belejanor.switcher.acquirers.SPIBceIsAcq;
import com.belejanor.switcher.authorizations.ITransactionsSPIBridge;
import com.belejanor.switcher.authorizations.SPIBceIsAut;
import com.belejanor.switcher.cscoreswitch.ContainerIsoQueue;
import com.belejanor.switcher.cscoreswitch.Iso8583;
import com.belejanor.switcher.cscoreswitch.csProcess;
import com.belejanor.switcher.cscoreswitch.wIso8583;
import com.belejanor.switcher.logger.Logger;
import com.belejanor.switcher.logger.LoggerConfig.TypeLog;
import com.belejanor.switcher.logger.LoggerConfig.TypeMonitor;
import com.belejanor.switcher.logger.LoggerConfig.TypeWriteLog;
import com.belejanor.switcher.memcached.MemoryGlobal;
import com.belejanor.switcher.parser.BceSPIParser;
import com.belejanor.switcher.queueadmin.Queue;
import com.belejanor.switcher.queueadmin.typeMessage;
import com.belejanor.switcher.utils.FormatUtils;
import com.belejanor.switcher.utils.GeneralUtils;
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
import urn.iso.std.iso20022.tech.xsd.pacs_002_001_05.StatusReasonInformation9;
import urn.iso.std.iso20022.tech.xsd.pacs_002_001_05.TransactionGroupStatus3Code;
import urn.iso.std.iso20022.tech.xsd.pacs_004_001_04.DocumentReverso;
import urn.iso.std.iso20022.tech.xsd.pacs_008_001_04.DocumentTransferenciaSPI;

public class BridgeSPI_BCE implements ITransactionsSPIBridge{

	private String SecuencialTrx;
	private Logger log;
	private String codError;
	private String desError;
	
	public BridgeSPI_BCE() {
		log = new Logger();
	}
	
	
	public String getSecuencialTrx() {
		return SecuencialTrx;
	}

	public void setSecuencialTrx(String secuencialTrx) {
		SecuencialTrx = secuencialTrx;
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


	@Override
	public DocumentRespuesta ProcesaSolicitudTransferenciaSPI(DocumentTransferenciaSPI documentSolicitud, String IP) {
		
		DocumentRespuesta doc = null;
		String ResAlBCE = StringUtils.Empty();
		log.WriteLogMonitor("[[[ Se recibe Transacciones SPI de Recepcion ]]]", TypeMonitor.monitor, null);
		try {
			
			ResAlBCE = "RET" + MemoryGlobal.UrlSpiCodeEfi_BCE + FormatUtils.DateToString(new Date(), "yyyyMMddHHmmss") 
            + GeneralUtils.GetSecuencialNumeric(7);
			
			doc = new DocumentRespuesta();
			
				final String responseAlBCE = ResAlBCE;
				Thread t = new Thread(new Runnable() {
					
					@Override
					public void run() {
						
						TransitionValidate(documentSolicitud,IP,responseAlBCE);
					}
				});
				
				t.start();
				
				FIToFIPaymentStatusReportV05 fito = new FIToFIPaymentStatusReportV05();
				GroupHeader53 grp = new GroupHeader53();
				grp.setMsgId(ResAlBCE);
				grp.setCreDtTm(FormatUtils.DateToXMLGregorian(new Date()));
				
				BranchAndFinancialInstitutionIdentification5 instg = new BranchAndFinancialInstitutionIdentification5();
				FinancialInstitutionIdentification8 fiAut = new FinancialInstitutionIdentification8();
				fiAut.setBICFI(MemoryGlobal.UrlSpiCodeEfi_BCE);
				instg.setFinInstnId(fiAut);
				
				
				BranchAndFinancialInstitutionIdentification5 instd = new BranchAndFinancialInstitutionIdentification5();
				FinancialInstitutionIdentification8 fiAcq = new FinancialInstitutionIdentification8();
				fiAcq.setBICFI(MemoryGlobal.UrlSpiCodeSwitch_BCE);
				instd.setFinInstnId(fiAcq);
				
				grp.setInstdAgt(instd);
				grp.setInstgAgt(instg);
				
				OriginalGroupHeader1 orginData = new OriginalGroupHeader1();
				orginData.setOrgnlMsgId(documentSolicitud.getFIToFICstmrCdtTrf().getGrpHdr().getMsgId());
				orginData.setOrgnlMsgNmId("pacs.008.001.04");
				orginData.setGrpSts(TransactionGroupStatus3Code.RCVD);
				
				
				StatusReasonInformation9 status = new StatusReasonInformation9();
				PartyIdentification43 prty = new PartyIdentification43();
				Party11Choice choice = new Party11Choice();
				OrganisationIdentification8 org8 = new OrganisationIdentification8();
				org8.setAnyBIC(MemoryGlobal.UrlSpiCodeEfi_BCE);
				choice.setOrgId(org8);
				prty.setId(choice);
				status.setOrgtr(prty);
				List<StatusReasonInformation9> lisStatus = new ArrayList<>();
				lisStatus.add(status);
				
				doc.setFIToFIPmtStsRpt(fito);
				doc.getFIToFIPmtStsRpt().setGrpHdr(grp);
				
				doc.getFIToFIPmtStsRpt().setOrgnlGrpInfAndSts(orginData);
				doc.getFIToFIPmtStsRpt().getOrgnlGrpInfAndSts().setStsRsnInf(lisStatus);
				this.codError = "000";
				
				log.WriteLog(doc, TypeLog.bceacq, TypeWriteLog.file);
				log.WriteLogMonitor("[[[ Se RESPONDE Transacciones SPI de Recepcion RCVD ]]]", TypeMonitor.monitor, null);
		
			
		} catch (Exception e) {
		
			this.codError = "TA01";
			this.desError = GeneralUtils.ExceptionToString("ERROR EN PROCESOS", e, false);
			log.WriteLogMonitor("Error modulo BridgeSPI_BCE::ProcesaSolicitudTransferenciaSPI ", TypeMonitor.error, e);
		}
		
		return doc;
	}

	private void TransitionValidate(DocumentTransferenciaSPI documentSolicitud, String IP, String resAlABCE) {
		
		
		log.WriteLogMonitor("*** ENTRO AL HILO", TypeMonitor.monitor, null);
		List<Iso8583> isoList = null;
		log.WriteLog(documentSolicitud, TypeLog.bceacq, TypeWriteLog.file);
		BceSPIParser parser = null;
		SPIBceIsAcq spi = new SPIBceIsAcq();
		Iso8583 isoCentinelaGeneric = null;
		wIso8583 wIsoCentinelaGeneric = null;
		SPIBceIsAut autorizator = null;
		csProcess processor = null;
		try {
			
			
			isoList = new CopyOnWriteArrayList<>();
			parser = new BceSPIParser();
			isoList = parser.parseDocumentSolicitud(documentSolicitud, IP, resAlABCE);
			final List<Iso8583> iso = isoList;
			if(isoList != null) {
				
				spi.processTrxSpiReceptor(iso);
				
			}else {
				
				//Aqui proceso de envio a BCE RESPUESTA GENERICA CON ERROR
				isoCentinelaGeneric = new Iso8583();
				
				isoCentinelaGeneric.setISO_000_Message_Type("1200");
				isoCentinelaGeneric.setISO_003_ProcessingCode("880056");
				isoCentinelaGeneric.setISO_006_BillAmount(0.1);
				isoCentinelaGeneric.setISO_008_BillFeeAmount(0.1);
				isoCentinelaGeneric.setISO_011_SysAuditNumber(StringUtils.IsNullOrEmpty(documentSolicitud
						.getFIToFICstmrCdtTrf().getGrpHdr().getMsgId())?GeneralUtils.GetSecuencial(8):documentSolicitud
								.getFIToFICstmrCdtTrf().getGrpHdr().getMsgId());
				isoCentinelaGeneric.setISO_BitMap("ERROR");
				isoCentinelaGeneric.setISO_013_LocalDate(new Date());
				isoCentinelaGeneric.setISO_041_CardAcceptorID("**** SPI ERROR GENERICO ****");
				isoCentinelaGeneric.setISO_018_MerchantType("0005");
				isoCentinelaGeneric.setISO_024_NetworkId("555777");
				isoCentinelaGeneric.setISO_032_ACQInsID(MemoryGlobal.UrlSpiCodeSwitch_BCE);
				isoCentinelaGeneric.setISO_033_FWDInsID(MemoryGlobal.UrlSpiCodeEfi_BCE);
				isoCentinelaGeneric.setISO_120_ExtendedData(parser.getCodError());
				isoCentinelaGeneric.setISO_121_ExtendedData(parser.getDesError());
				isoCentinelaGeneric.setISO_002_PAN(parser.getCodError() + " - " + parser.getDesError());
				isoCentinelaGeneric.setISO_055_EMV("RJCT");
				
				wIsoCentinelaGeneric = new wIso8583(isoCentinelaGeneric, "127.0.0.1");
				autorizator = new SPIBceIsAut();
				wIsoCentinelaGeneric = autorizator.SendRequestSPINotifications(wIsoCentinelaGeneric, isoList);
				if(!wIsoCentinelaGeneric.getISO_039_ResponseCode().equals("000")) {
					wIsoCentinelaGeneric.setISO_013_LocalDate(FormatUtils.StringToDate("1980-11-29", "YYYY-MM-DD"));
				}
				isoCentinelaGeneric = new Iso8583(wIsoCentinelaGeneric);
				//Listo para ejecutar la Trx csProcess
				processor = new csProcess();
				isoCentinelaGeneric = processor.ProcessTransactionMain(isoCentinelaGeneric, "127.0.0.1"); 
				
			}
			
		} catch (Exception e) {
			log.WriteLogMonitor("Error modulo BridgeSPI_BCE::TransitionValidate ", TypeMonitor.error, e);
		}
	}
	
	@Override
	public Date ProcesaRespuestaTransferenciaSPI(DocumentRespuesta documentoRespuestaSolicitud, String IP) {
		
		try {
			final DocumentRespuesta doc = documentoRespuestaSolicitud;
			final String ip = IP;
			Thread tProc = new Thread(() -> {
				
				/*try {
					Thread.sleep(40000);
				} catch (InterruptedException e) {}*/
				
				
				/*Iso8583 iso = null;
				log.WriteLog(documentoRespuestaSolicitud, TypeLog.bceacq, TypeWriteLog.file);
				BceSPIParser parser = new BceSPIParser();
				iso = parser.parseDocumentRespuestafromBCE(documentoRespuestaSolicitud);
				csProcess processor = new csProcess();
				iso = processor.ProcessTransactionMain(iso, IP);*/
				
				ContainerIsoQueue<DocumentRespuesta> cont = new ContainerIsoQueue<>(doc , ip);
				Queue queue = new Queue();
				queue.SendMessage(typeMessage.initialMessage, cont, 1, 5);
			
			});
			tProc.start();
			
			return new Date();
			
		} catch (Exception e) {
		
			log.WriteLogMonitor("Error modulo BridgeSPI_BCE::ProcesaRespuestaTransferenciaSPI ", TypeMonitor.error, e);
			return new Date();
		}
	}
	
	public void ProcessEnqueueTransaction(DocumentRespuesta documentoRespuestaSolicitud, String IP) {
		
		try {
			
			Iso8583 iso = null;
			log.WriteLog(documentoRespuestaSolicitud, TypeLog.bceacq, TypeWriteLog.file);
			BceSPIParser parser = new BceSPIParser();
			iso = parser.parseDocumentRespuestafromBCE(documentoRespuestaSolicitud);
			csProcess processor = new csProcess();
			iso = processor.ProcessTransactionMain(iso, IP);
			
		} catch (Exception e) {
			log.WriteLogMonitor("Error modulo BridgeSPI_BCE::ProcessEnqueueTransaction ", TypeMonitor.error, e);
		}
	}

	@Override
	public DocumentRespuesta ProcesoRespuestaReversosTecnicosSPI(DocumentReverso documentoReverso, String IP) {
		
		DocumentRespuesta doc = null;
		String ResAlBCE = StringUtils.Empty();
		log.WriteLogMonitor("[[[ Se recibe Transacciones SPI de **** REVERSO **** ]]]", TypeMonitor.monitor, null);
		try {
				
			ResAlBCE = "RET" + MemoryGlobal.UrlSpiCodeEfi_BCE + FormatUtils.DateToString(new Date(), "yyyyMMddHHmmss") 
            + GeneralUtils.GetSecuencialNumeric(7);
			
			doc = new DocumentRespuesta();
			
				Thread t = new Thread(new Runnable() {
					
					@Override
					public void run() {
						
						Thread tProc = new Thread(() -> {
							
							Iso8583 iso = null;
							log.WriteLog(documentoReverso, TypeLog.bceacq, TypeWriteLog.file);
							BceSPIParser parser = new BceSPIParser();
							iso = parser.parseDocumentoReverso(documentoReverso);
							csProcess processor = new csProcess();
							iso = processor.ProcessTransactionMain(iso, IP);
							System.out.println(iso.getISO_039_ResponseCode() + "  " + iso.getISO_039p_ResponseDetail());
						
						});
						tProc.start();
					}
				});
				
				t.start();
				
				FIToFIPaymentStatusReportV05 fito = new FIToFIPaymentStatusReportV05();
				GroupHeader53 grp = new GroupHeader53();
				grp.setMsgId(ResAlBCE);
				grp.setCreDtTm(FormatUtils.DateToXMLGregorian(new Date()));
				
				BranchAndFinancialInstitutionIdentification5 instg = new BranchAndFinancialInstitutionIdentification5();
				FinancialInstitutionIdentification8 fiAut = new FinancialInstitutionIdentification8();
				fiAut.setBICFI(MemoryGlobal.UrlSpiCodeEfi_BCE);
				instg.setFinInstnId(fiAut);
				
				
				BranchAndFinancialInstitutionIdentification5 instd = new BranchAndFinancialInstitutionIdentification5();
				FinancialInstitutionIdentification8 fiAcq = new FinancialInstitutionIdentification8();
				fiAcq.setBICFI(MemoryGlobal.UrlSpiCodeSwitch_BCE);
				instd.setFinInstnId(fiAcq);
				
				grp.setInstdAgt(instd);
				grp.setInstgAgt(instg);
				
				OriginalGroupHeader1 orginData = new OriginalGroupHeader1();
				
				orginData.setOrgnlMsgId(documentoReverso.getPmtRtr().getGrpHdr().getMsgId());
				orginData.setOrgnlMsgNmId("pacs.004.001.04");
				orginData.setGrpSts(TransactionGroupStatus3Code.RCVD);
				
				
				StatusReasonInformation9 status = new StatusReasonInformation9();
				PartyIdentification43 prty = new PartyIdentification43();
				Party11Choice choice = new Party11Choice();
				OrganisationIdentification8 org8 = new OrganisationIdentification8();
				org8.setAnyBIC(MemoryGlobal.UrlSpiCodeEfi_BCE);
				choice.setOrgId(org8);
				prty.setId(choice);
				status.setOrgtr(prty);
				List<StatusReasonInformation9> lisStatus = new ArrayList<>();
				lisStatus.add(status);
				
				doc.setFIToFIPmtStsRpt(fito);
				doc.getFIToFIPmtStsRpt().setGrpHdr(grp);
				
				doc.getFIToFIPmtStsRpt().setOrgnlGrpInfAndSts(orginData);
				doc.getFIToFIPmtStsRpt().getOrgnlGrpInfAndSts().setStsRsnInf(lisStatus);
				this.codError = "000";
				
				log.WriteLog(doc, TypeLog.bceacq, TypeWriteLog.file);
				log.WriteLogMonitor("[[[ Se RESPONDE Transacciones SPI de Recepcion RCVD *** REVERSO *** ]]]", TypeMonitor.monitor, null);
			
		} catch (Exception e) {
			
			log.WriteLogMonitor("Error modulo BridgeSPI_BCE::ProcesoRespuestaReversosTecnicosSPI ", TypeMonitor.error, e);
			this.codError = "TA01";
			this.desError = GeneralUtils.ExceptionToString("ERROR EN PROCESOS", e, false);
		}
		return doc;
	}

}
