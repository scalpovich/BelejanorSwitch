package com.belejanor.switcher.parser;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CopyOnWriteArrayList;

import com.belejanor.switcher.cscoreswitch.Iso8583;
import com.belejanor.switcher.logger.Logger;
import com.belejanor.switcher.logger.LoggerConfig.TypeMonitor;
import com.belejanor.switcher.memcached.MemoryGlobal;
import com.belejanor.switcher.snp.spi.SnpOrdLotes;
import com.belejanor.switcher.snp.spi.SnpSPIOrdenante;
import com.belejanor.switcher.utils.FormatUtils;
import com.belejanor.switcher.utils.NumbersUtils;
import com.belejanor.switcher.utils.SerializationObject;
import com.belejanor.switcher.utils.StringUtils;

import urn.iso.std.iso20022.tech.xsd.pacs_002_001_05.DocumentRespuesta;
import urn.iso.std.iso20022.tech.xsd.pacs_004_001_04.DocumentReverso;
import urn.iso.std.iso20022.tech.xsd.pacs_008_001_04.DocumentTransferenciaSPI;

public class BceSPIParser implements Callable<Iso8583>{

	private Logger log;
	private String codError;
	private String desError; 
	private List<SnpSPIOrdenante> listTrxSpiOrd;
	private SnpOrdLotes snpLote;
	private String MsgIdLote;

	public BceSPIParser() {
		log = new Logger();
		listTrxSpiOrd = new ArrayList<>();
	} 
	
	public BceSPIParser(SnpOrdLotes snp) {
		
		this.snpLote = snp;
	}
	
    public BceSPIParser(SnpOrdLotes snp, String MsgIdLote) {
		
		this.snpLote = snp;
		this.MsgIdLote = MsgIdLote;
	}
	
	public String getCodError() {
		return codError;
	}
	public String getDesError() {
		return desError;
	}
	
	public List<SnpSPIOrdenante> getListTrxSpiOrd() {
		return listTrxSpiOrd;
	}

	public void setListTrxSpiOrd(List<SnpSPIOrdenante> listTrxSpiOrd) {
		this.listTrxSpiOrd = listTrxSpiOrd;
	}

	public Iso8583 parseDocumentRespuestafromBCE(DocumentRespuesta document){
		
		Iso8583 iso = null;
		try {
			
			iso = new Iso8583();
			iso.setISO_000_Message_Type("1200");
			iso.setISO_003_ProcessingCode("980056");
			iso.setISO_018_MerchantType("0005");
			iso.setISO_024_NetworkId("555777");
			if(document != null){
			
				if(document.getFIToFIPmtStsRpt() != null){
					
					if(document.getFIToFIPmtStsRpt().getGrpHdr() != null){
						
						if(!StringUtils.IsNullOrEmpty(document.getFIToFIPmtStsRpt().getGrpHdr().getMsgId())){
							
							iso.setISO_011_SysAuditNumber(document.getFIToFIPmtStsRpt().getGrpHdr().getMsgId());
							
							if(document.getFIToFIPmtStsRpt().getGrpHdr().getCreDtTm() != null){
								
								iso.setISO_012_LocalDatetime(document.getFIToFIPmtStsRpt().getGrpHdr()
										.getCreDtTm().toGregorianCalendar().getTime());
								
								if(document.getFIToFIPmtStsRpt().getGrpHdr().getInstgAgt() != null){
									
									if(document.getFIToFIPmtStsRpt().getGrpHdr().getInstgAgt().getFinInstnId() != null){
										
										if(!StringUtils.IsNullOrEmpty(document.getFIToFIPmtStsRpt()
												.getGrpHdr().getInstgAgt().getFinInstnId().getBICFI())){
											
											iso.setISO_032_ACQInsID(document.getFIToFIPmtStsRpt()
												.getGrpHdr().getInstgAgt().getFinInstnId().getBICFI());
											iso.setISO_041_CardAcceptorID(iso.getISO_032_ACQInsID());
											
											if(document.getFIToFIPmtStsRpt().getGrpHdr().getInstdAgt() != null){
											
												if(document.getFIToFIPmtStsRpt().getGrpHdr().getInstdAgt()
														                             .getFinInstnId() != null){
													
													if(!StringUtils.IsNullOrEmpty(document.getFIToFIPmtStsRpt().getGrpHdr().getInstdAgt()
														                             .getFinInstnId().getBICFI())){
														
														iso.setISO_033_FWDInsID(document.getFIToFIPmtStsRpt().getGrpHdr().getInstdAgt()
														                             .getFinInstnId().getBICFI());
														iso.setISO_002_PAN(iso.getISO_033_FWDInsID());
														
														if(document.getFIToFIPmtStsRpt().getOrgnlGrpInfAndSts() != null){
															
															if(!StringUtils.IsNullOrEmpty(document.getFIToFIPmtStsRpt()
																	            .getOrgnlGrpInfAndSts().getOrgnlMsgId())){
																
																iso.setISO_037_RetrievalReferenceNumber(document.getFIToFIPmtStsRpt()
																	            .getOrgnlGrpInfAndSts().getOrgnlMsgId());
																
																if(!StringUtils.IsNullOrEmpty(document.getFIToFIPmtStsRpt()
															            .getOrgnlGrpInfAndSts().getOrgnlMsgNmId())){
																	
																	iso.setISO_BitMap(document.getFIToFIPmtStsRpt()
															            .getOrgnlGrpInfAndSts().getOrgnlMsgNmId());
															        
																	//A veces viene muchos errores y no se presenta...
																	if(document.getFIToFIPmtStsRpt()
																            .getOrgnlGrpInfAndSts().getGrpSts() != null){
																		
																		iso.setISO_039_ResponseCode(document.getFIToFIPmtStsRpt()
																            .getOrgnlGrpInfAndSts().getGrpSts().name().toString());
																	 }
																		
																		/*Importante......*/
																	 if(document.getFIToFIPmtStsRpt().getTxInfAndSts() != null){
																		
																			/*Pongo el numero de errores en el ISO23*/
																			
																			iso.setISO_023_CardSeq(String.valueOf(document.getFIToFIPmtStsRpt()
																					.getTxInfAndSts().size()));
																			if(document.getFIToFIPmtStsRpt()
																					.getTxInfAndSts().size() > 0) {
																				/*Y cojo el Primer elemento si lo hay*/
																				if(!StringUtils.IsNullOrEmpty(document.getFIToFIPmtStsRpt().getTxInfAndSts().get(0)
																						.getOrgnlEndToEndId())){
																					
																					iso.setISO_120_ExtendedData(document.getFIToFIPmtStsRpt().getTxInfAndSts().get(0)
																						.getOrgnlEndToEndId());
																					
																					if(!StringUtils.IsNullOrEmpty(document.getFIToFIPmtStsRpt().getTxInfAndSts().get(0)
																							.getOrgnlTxId())){
																						
																						iso.setISO_121_ExtendedData(document.getFIToFIPmtStsRpt().getTxInfAndSts().get(0)
																								.getOrgnlTxId());
																						
																						if(!StringUtils.IsNullOrEmpty(iso.getISO_039_ResponseCode())) {
																							
																							iso.setISO_039_ResponseCode(document.getFIToFIPmtStsRpt()
																									.getTxInfAndSts().get(0).getTxSts().name());
																						}
																						
																						if(!StringUtils.IsNullOrEmpty(document.getFIToFIPmtStsRpt().getTxInfAndSts().get(0)
																								.getTxSts().name())){
																							iso.setISO_122_ExtendedData(document.getFIToFIPmtStsRpt().getTxInfAndSts().get(0)
																									.getTxSts().name());
																							
																							
																							if(document.getFIToFIPmtStsRpt().getTxInfAndSts().get(0).getStsRsnInf() != null){
																								
																								//Cuando es exitosa no viene la razon OJO
																								if(document.getFIToFIPmtStsRpt().getTxInfAndSts().get(0).
																										 getStsRsnInf().get(0).getRsn() != null){
																									
																									iso.setISO_123_ExtendedData(document.getFIToFIPmtStsRpt().getTxInfAndSts().get(0).
																										 getStsRsnInf().get(0).getRsn().getCd());
																								 }
																									
																									if(document.getFIToFIPmtStsRpt().getTxInfAndSts().get(0)
																										 .getStsRsnInf().get(0).getAddtlInf() != null){
																										
																										if(document.getFIToFIPmtStsRpt().getTxInfAndSts().get(0)
																												 .getStsRsnInf().get(0).getAddtlInf().size() > 0) {
																												iso.setISO_124_ExtendedData(document.getFIToFIPmtStsRpt().getTxInfAndSts().get(0)
																														 .getStsRsnInf().get(0).getAddtlInf().get(0));
																											
																												iso.setISO_039p_ResponseDetail(iso.getISO_124_ExtendedData());
																										}
																									}
																									/*Si el 039 es nulo o vacio pongo TRANSACCION EXITOSA*/
																									if(StringUtils.IsNullOrEmpty(iso.getISO_039p_ResponseDetail())){
																										
																										iso.setISO_039_ResponseCode("TRANSACCION EXITOSA");
																									}
																										
																										/*Pongo toda la trama en el 115 para posterior procesamiento*/
																										String trama = SerializationObject.ObjectToString(document, document.getClass());
																										iso.setISO_115_ExtendedData(trama);
																							}
																						}
																					}
																				}
																			}else {
																				iso.setISO_039_ResponseCode("101");
																			}
																		}
																	}
																}
															}
														}
													}
												}
											}
										}
									}
								}
							}
						}
					}
				}
			
		} catch (Exception e) {
			
			log.WriteLogMonitor("Error modulo BceSPIParser::parseDocumentRespuestafromBCE ", TypeMonitor.error, e);
			
		}finally {
		
			iso.setISO_090_OriginalData(iso.getISO_039_ResponseCode());
		}
		return iso;
	}
	
	public Iso8583 parseDocumentoReverso(DocumentReverso document) {
		
		Iso8583 iso = null;
		try {
			
			iso = new Iso8583();
			iso.setISO_000_Message_Type("1200");
			iso.setISO_041_CardAcceptorID("********* MENSAJE REVERSO TECNICO **********");
			iso.setISO_003_ProcessingCode("680056");
			iso.setISO_018_MerchantType("0005");
			iso.setISO_024_NetworkId("555777");
			
			if(document != null) {
				
				if(document.getPmtRtr() != null) {
					
					if(document.getPmtRtr().getGrpHdr() != null) {
						
						if(!StringUtils.IsNullOrEmpty(document.getPmtRtr().getGrpHdr().getMsgId())) {
							
							iso.setISO_011_SysAuditNumber(document.getPmtRtr().getGrpHdr().getMsgId());
						}
						
						if(document.getPmtRtr().getGrpHdr().getCreDtTm() != null) {
							
							iso.setISO_012_LocalDatetime(document.getPmtRtr().getGrpHdr()
									.getCreDtTm().toGregorianCalendar().getTime());
						}
						
						if(!StringUtils.IsNullOrEmpty(document.getPmtRtr().getGrpHdr().getNbOfTxs())) {
							
							iso.setISO_006_BillAmount(Double.parseDouble(document.getPmtRtr()
									.getGrpHdr().getNbOfTxs()));
						}
						
						if(document.getPmtRtr().getGrpHdr().getCtrlSum() != null) {
							
							iso.setISO_008_BillFeeAmount(document.getPmtRtr().getGrpHdr()
									.getCtrlSum().doubleValue());
						}
						if(document.getPmtRtr().getGrpHdr().getTtlRtrdIntrBkSttlmAmt() != null) {
							
							if(document.getPmtRtr().getGrpHdr().getTtlRtrdIntrBkSttlmAmt().getValue() != null) {
								
								iso.setISO_004_AmountTransaction(document.getPmtRtr().getGrpHdr()
										.getTtlRtrdIntrBkSttlmAmt().getValue().doubleValue());
							}
						}
						if(document.getPmtRtr().getGrpHdr().getIntrBkSttlmDt() != null) {
							
							iso.setISO_013_LocalDate(document.getPmtRtr().getGrpHdr().getIntrBkSttlmDt()
									.toGregorianCalendar().getTime());
						}
						if(document.getPmtRtr().getGrpHdr().getInstgAgt() != null) {
							
							if(document.getPmtRtr().getGrpHdr().getInstgAgt().getFinInstnId() != null) {
								
								if(!StringUtils.IsNullOrEmpty(document.getPmtRtr().getGrpHdr()
										.getInstgAgt().getFinInstnId().getBICFI())) {
									
									iso.setISO_032_ACQInsID(document.getPmtRtr().getGrpHdr()
										.getInstgAgt().getFinInstnId().getBICFI());
								}
							}
						}
						
						if(document.getPmtRtr().getGrpHdr().getInstdAgt() != null) {
							
							if(document.getPmtRtr().getGrpHdr().getInstdAgt().getFinInstnId() != null) {
								
								if(!StringUtils.IsNullOrEmpty(document.getPmtRtr().getGrpHdr().getInstdAgt()
										.getFinInstnId().getBICFI())) {
									
									iso.setISO_033_FWDInsID(document.getPmtRtr().getGrpHdr().getInstdAgt()
											.getFinInstnId().getBICFI());
								}
							}
						}
						
						if(document.getPmtRtr().getOrgnlGrpInf() != null) {
							
							if(!StringUtils.IsNullOrEmpty(document.getPmtRtr().getOrgnlGrpInf().getOrgnlMsgId())) {
								
								iso.setISO_037_RetrievalReferenceNumber(document.getPmtRtr()
										.getOrgnlGrpInf().getOrgnlMsgId());
							}
							if(!StringUtils.IsNullOrEmpty(document.getPmtRtr().getOrgnlGrpInf().getOrgnlMsgNmId())) {
								
								iso.setISO_BitMap(document.getPmtRtr().getOrgnlGrpInf().getOrgnlMsgNmId());
							}
							if(document.getPmtRtr().getOrgnlGrpInf().getOrgnlCreDtTm() != null) {
								
								iso.setISO_007_TransDatetime(document.getPmtRtr().getOrgnlGrpInf()
										.getOrgnlCreDtTm().toGregorianCalendar().getTime());
							}
						}
						
						if(document.getPmtRtr().getTxInf() != null) {
							
							iso.setISO_023_CardSeq(String.valueOf(document.getPmtRtr().getTxInf().size()));
						}
						/*Serializo toda la trama de reversos*/
						String trama = SerializationObject.ObjectToString(document, document.getClass());
						iso.setISO_115_ExtendedData(trama);
					}
				}
			}
			
		} catch (Exception e) {
			
			log.WriteLogMonitor("Error modulo BceSPIParser::parseDocumentRespuestafromBCE ", TypeMonitor.error, e);
			
		}finally {
			
			iso.setISO_090_OriginalData("RVER");
			iso.setISO_055_EMV("REVERSO");
			iso.setISO_002_PAN(iso.getISO_033_FWDInsID());
		}
		
		return iso;
	}
	
	public List<Iso8583> parseDocumentSolicitud(DocumentTransferenciaSPI snp, String IP, String ResAlBCE){
		
		List<Iso8583> isoList = null;
		Iso8583 iso = null;
		String prefixAccDeb = StringUtils.Empty();
		String prefixAccAcred = StringUtils.Empty();
		HashSet<String> validateEndtoEndId = null;
		HashSet<String> validateTxId= null;
		double validateMonto = 0;
		int validaNroTrx = 0; 
		DecimalFormatSymbols simbolo = new DecimalFormatSymbols();
	    simbolo.setDecimalSeparator('.');
		DecimalFormat df = new DecimalFormat("0.00",simbolo);
		try {
		
			iso = new Iso8583();
			isoList = new CopyOnWriteArrayList<>();
			iso.setISO_BitMap("LOW");
			iso.setISO_000_Message_Type("1200");
			iso.setISO_024_NetworkId("555777");
			iso.setISO_018_MerchantType("0007");
			iso.setISO_043_CardAcceptorLoc(ResAlBCE);
			
			if(snp != null) {
			
				if(snp.getFIToFICstmrCdtTrf()!= null) {
					
					if(snp.getFIToFICstmrCdtTrf().getGrpHdr() != null) {
						
						validateEndtoEndId = new HashSet<>();
						validateTxId = new HashSet<>();
							
						if(!StringUtils.IsNullOrEmpty(snp.getFIToFICstmrCdtTrf().getGrpHdr().getMsgId())) {
							
							/*MSGID*/
							iso.setISO_037_RetrievalReferenceNumber(snp.getFIToFICstmrCdtTrf()
									.getGrpHdr().getMsgId());
							iso.setISO_036_Track3(IP);
						}else {
							
							this.codError = "CH21"; 
							this.desError = "El elemento es ausente"
									+ ". /FIToFICstmrCdtTrf/GrpHdr/MsgId";
							return null;
						}
						if(snp.getFIToFICstmrCdtTrf().getGrpHdr().getCreDtTm() != null) {
							
							/*CreDtTm*/
							iso.setISO_007_TransDatetime(snp.getFIToFICstmrCdtTrf().getGrpHdr()
									.getCreDtTm() .toGregorianCalendar().getTime());
						}else {
							
							this.codError = "CH21"; 
							this.desError = "El elemento es ausente"
									+ ". /FIToFICstmrCdtTrf/GrpHdr/CreDtTm";
							return null;
						}
						/*NbOfTxs*/
						if(!StringUtils.IsNullOrEmpty(snp.getFIToFICstmrCdtTrf().getGrpHdr().getNbOfTxs())) {
							
							if(NumbersUtils.isNumeric(snp.getFIToFICstmrCdtTrf().getGrpHdr().getNbOfTxs())) {
								if(Integer.parseInt(snp.getFIToFICstmrCdtTrf().getGrpHdr().getNbOfTxs()) > 0) {
									
									iso.setISO_006_BillAmount(Double.parseDouble(snp.getFIToFICstmrCdtTrf()
												  .getGrpHdr().getNbOfTxs()));
								}else {
									
									this.codError = "AM18"; 
									this.desError = "El número de transacciones es invalido o esta ausente."
											+ ". /FIToFICstmrCdtTrf/GrpHdr/NbOfTxs";
									return null;
								}
							}else {
								
								this.codError = "CH16"; 
								this.desError = "El número de transacciones es invalido o esta ausente."
										+ ". /FIToFICstmrCdtTrf/GrpHdr/NbOfTxs";
								return null;
							}
						}else {
							
							this.codError = "AM18"; 
							this.desError = "El número de transacciones es invalido o esta ausente."
									+ ". /FIToFICstmrCdtTrf/GrpHdr/NbOfTxs";
							return null;
						}
						/*CtrlSum*/
						if(snp.getFIToFICstmrCdtTrf().getGrpHdr().getCtrlSum() != null) {
							
							if(snp.getFIToFICstmrCdtTrf().getGrpHdr().getCtrlSum()
										.compareTo(BigDecimal.ZERO) > 0) {
								
								iso.setISO_008_BillFeeAmount(snp.getFIToFICstmrCdtTrf().getGrpHdr()
										.getCtrlSum().doubleValue());
							}else {
								
								this.codError = "AM01"; 
								this.desError = "El monto especificado en el mensaje es igual a cero"
										+ ". /FIToFICstmrCdtTrf/GrpHdr/CtrlSum";
								return null;
							}
						}else {
							
							this.codError = "CH21"; 
							this.desError = "El elemento es ausente"
									+ ". /FIToFICstmrCdtTrf/GrpHdr/CtrlSum";
							return null;
						}
					}else {
						
						this.codError = "CH21"; 
						this.desError = "El elemento es ausente"
								+ ". /FIToFICstmrCdtTrf/GrpHdr";
						return null;
					}
					
					if(snp.getFIToFICstmrCdtTrf().getCdtTrfTxInf() != null) {
					
						if(snp.getFIToFICstmrCdtTrf().getCdtTrfTxInf().size() > 0) {
					
							
							for (int i = 0; i < snp.getFIToFICstmrCdtTrf().getCdtTrfTxInf().size(); i++) {
								
								try {
									/*PmtId*/
									if(snp.getFIToFICstmrCdtTrf().getCdtTrfTxInf().get(i).getPmtId() != null) {
										
										/*InstrId*/
										if(!StringUtils.IsNullOrEmpty(snp.getFIToFICstmrCdtTrf()
												.getCdtTrfTxInf().get(i).getPmtId().getInstrId())) {
											iso.setISO_023_CardSeq(snp.getFIToFICstmrCdtTrf()
													.getCdtTrfTxInf().get(i).getPmtId().getInstrId());
										}
										/*EndToEndId*/
										if(!StringUtils.IsNullOrEmpty(snp.getFIToFICstmrCdtTrf()
												.getCdtTrfTxInf().get(i).getPmtId().getEndToEndId())) {
										
											iso.setISO_011_SysAuditNumber(snp.getFIToFICstmrCdtTrf()
												.getCdtTrfTxInf().get(i).getPmtId().getEndToEndId());
											validateEndtoEndId.add(iso.getISO_011_SysAuditNumber());
											
										}else {
											
											this.codError = "FF08";
											this.desError = "El identificador de inicio a fin (campo EndToEndIdentification) esta ausente"
													+ " o es invalido. /CdtTrfTxInf/PmtId/EndToEndId {Iteracion "+ (i + 1) +"}";
											return null;
										}
										/*TxId*/
										if(!StringUtils.IsNullOrEmpty(snp.getFIToFICstmrCdtTrf() 
												.getCdtTrfTxInf().get(i).getPmtId().getTxId())) {
										
											if(NumbersUtils.isNumeric(snp.getFIToFICstmrCdtTrf() 
												.getCdtTrfTxInf().get(i).getPmtId().getTxId())) {
												
												iso.setISO_090_OriginalData(snp.getFIToFICstmrCdtTrf()
														.getCdtTrfTxInf().get(i).getPmtId().getTxId());
													validateTxId.add(iso.getISO_090_OriginalData());
											}else {
												
												this.codError = "CH16";
												this.desError = "El elemento es invalido"
														+ ". /CdtTrfTxInf/PmtId/TxId {Iteracion "+ (i+1) + "}";
												return null;
											}
												
										}else {
											
											this.codError = "CH21";
											this.desError = "El elemento es ausente"
													+ ". /CdtTrfTxInf/PmtId/TxId {Iteracion "+ (i+1) + "}";
											return null;
										}
										/*ClrSysRef*/
										if(!StringUtils.IsNullOrEmpty(snp.getFIToFICstmrCdtTrf()
												.getCdtTrfTxInf().get(i).getPmtId().getClrSysRef())) {
											iso.setISO_055_EMV(snp.getFIToFICstmrCdtTrf()
												.getCdtTrfTxInf().get(i).getPmtId().getClrSysRef());
										}else {
											
											this.codError = "CH21";
											this.desError = "El elemento es ausente"
													+ ". /CdtTrfTxInf/PmtId/ClrSysRef {Iteracion "+ (i+1) + "}";
											return null;
										}
										
									}else {
										
										this.codError = "CH21"; 
										this.desError = "El elemento es ausente"
												+ ". /CdtTrfTxInf/PmtId {Iteracion "+ (i+1) + "}";
										return null;
									}
									
									/*ESTABLECE SI LA TRANSACCION ES MAYORISTA*/
									if(snp.getFIToFICstmrCdtTrf().getCdtTrfTxInf().get(i).getPmtTpInf() != null) {
									
										if(snp.getFIToFICstmrCdtTrf().getCdtTrfTxInf().get(i)
												             .getPmtTpInf().getInstrPrty() != null) {
											
											if(snp.getFIToFICstmrCdtTrf().getCdtTrfTxInf().get(i)
										             .getPmtTpInf().getInstrPrty().name().equals("HIGH")) {
												
												iso.setISO_BitMap("HIGH");
												
											}else {
												iso.setISO_BitMap("LOW");
											}
											
										}else {
											
											this.codError = "CH21"; 
											this.desError = "El elemento es ausente"
													+ ". /CdtTrfTxInf/PmtTpInf/InstrPrty {Iteracion "+ (i+1) + "}";
											return null;
										}
										
									}else {
										
										iso.setISO_BitMap("LOW");
									}
									
									/*IntrBkSttlmAmt*/
									if(snp.getFIToFICstmrCdtTrf()
											.getCdtTrfTxInf().get(i).getIntrBkSttlmAmt() != null) {
										
										/*Valor $*/
										if(snp.getFIToFICstmrCdtTrf()
												.getCdtTrfTxInf().get(i).getIntrBkSttlmAmt().getValue() != null) {
											
											if(snp.getFIToFICstmrCdtTrf()
													.getCdtTrfTxInf().get(i).getIntrBkSttlmAmt()
													.getValue().compareTo(BigDecimal.ZERO) > 0) {
												
												iso.setISO_004_AmountTransaction(snp.getFIToFICstmrCdtTrf()
														.getCdtTrfTxInf().get(i).getIntrBkSttlmAmt().getValue()
														.doubleValue());
												
												validateMonto+=iso.getISO_004_AmountTransaction();
												
											}else {
												
												this.codError = "AM01"; 
												this.desError = "El monto especificado es igual a 0"
														+ ". /CdtTrfTxInf/IntrBkSttlmAmt {Iteracion "+ (i+1) + "}";
												return null;
											}
											
										}else {
											
											this.codError = "CH21"; 
											this.desError = "El elemento es ausente"
													+ ". /CdtTrfTxInf/IntrBkSttlmAmt {Iteracion "+ (i+1) + "}";
											return null;
										}
										/*Moneda $*/
										if(!StringUtils.IsNullOrEmpty(snp.getFIToFICstmrCdtTrf()
												.getCdtTrfTxInf().get(i).getIntrBkSttlmAmt().getCcy())) {
											
											iso.setISO_049_TranCurrCode(snp.getFIToFICstmrCdtTrf()
												.getCdtTrfTxInf().get(i).getIntrBkSttlmAmt().getCcy()=="USD"?840:0);
										}else {
											
											this.codError = "AM11"; 
											this.desError = "El tipo de moneda es invalido o esta ausente"
													+ ". /CdtTrfTxInf/IntrBkSttlmAmt/Ccy {Iteracion "+ (i+1) + "}";
											return null;
										}
									}else {
										
										this.codError = "CH21"; 
										this.desError = "El elemento es ausente"
												+ ". /CdtTrfTxInf/IntrBkSttlmAmt {Iteracion "+ (i+1) + "}";
										return null;
									}
									/*IntrBkSttlmDt Fecha Contable o de Programacion*/
									if(snp.getFIToFICstmrCdtTrf()
											.getCdtTrfTxInf().get(i).getIntrBkSttlmDt() != null) {
										
										iso.setISO_015_SettlementDatel((snp.getFIToFICstmrCdtTrf()
											.getCdtTrfTxInf().get(i).getIntrBkSttlmDt()
											.toGregorianCalendar().getTime()));
									}
									
									/*DBTR Nombre, tipo y numero de documento del Debitante*/
											
									if(snp.getFIToFICstmrCdtTrf().getCdtTrfTxInf().get(i).getDbtr() != null) {
										
										/*Nombre*/
										if(!StringUtils.IsNullOrEmpty(snp.getFIToFICstmrCdtTrf()
												.getCdtTrfTxInf().get(i).getDbtr().getNm())) {
											
											iso.setISO_034_PANExt(snp.getFIToFICstmrCdtTrf()
												.getCdtTrfTxInf().get(i).getDbtr().getNm());
										}else {
											
											this.codError = "BE08"; 
											this.desError = "El nombre del deudor no esta presente."
													+ ". /CdtTrfTxInf/Dbtr/Nm {Iteracion "+ (i+1) + "}";
											return null;
										}
										/*Id*/
										if(snp.getFIToFICstmrCdtTrf()
												.getCdtTrfTxInf().get(i).getDbtr().getId() != null) {
											
											if(snp.getFIToFICstmrCdtTrf()
													.getCdtTrfTxInf().get(i).getDbtr().getId().getPrvtId() != null) {
												//Personas naturales
												if(snp.getFIToFICstmrCdtTrf().getCdtTrfTxInf().get(i)
														.getDbtr().getId().getPrvtId().getOthr() != null) {
													
													if(!StringUtils.IsNullOrEmpty(snp.getFIToFICstmrCdtTrf().getCdtTrfTxInf().get(i)
														.getDbtr().getId().getPrvtId().getOthr().get(0).getId())) {
														
														iso.setISO_002_PAN(snp.getFIToFICstmrCdtTrf().getCdtTrfTxInf().get(i)
														.getDbtr().getId().getPrvtId().getOthr().get(0).getId());
													}else {
														
														this.codError = "CH21"; 
														this.desError = "El elemento es ausente"
																+ ". /CdtTrfTxInf/Dbtr/Id/PrvtId/Othr/Id {Iteracion "+ (i + 1) + "}";
														return null;
													}
													if(snp.getFIToFICstmrCdtTrf().getCdtTrfTxInf().get(i)
															.getDbtr().getId().getPrvtId().getOthr().get(0).getSchmeNm() != null) {
														
														String tipoDoc = StringUtils.Empty();
														if(!StringUtils.IsNullOrEmpty(snp.getFIToFICstmrCdtTrf().getCdtTrfTxInf().get(i)
															.getDbtr().getId().getPrvtId().getOthr().get(0).getSchmeNm().getCd())) {
															
															tipoDoc = snp.getFIToFICstmrCdtTrf().getCdtTrfTxInf().get(i)
																	.getDbtr().getId().getPrvtId().getOthr().get(0)
																	.getSchmeNm().getCd();
															
															switch (tipoDoc) {
															case "TXID":
																	iso.setISO_022_PosEntryMode("RUC");
																break;
															case "NIDN":
																	iso.setISO_022_PosEntryMode("CED");
																break;
															case "ARNU":
															case "CCPT":
																iso.setISO_022_PosEntryMode("PAS");
															break;
															default:
																
																this.codError = "BE15"; 
																this.desError = "Tipo de identificación no es valido o esta ausente."
																		+ ". /CdtTrfTxInf/Dbtr/Id/PrvtId/Othr/SchmeNm/Cd {Iteracion "+ (i + 1) + "}";
																return null;
															}
														}else {
															
															this.codError = "BE15"; 
															this.desError = "Tipo de identificación no es valido o esta ausente."
																	+ ". /CdtTrfTxInf/Dbtr/Id/PrvtId/Othr/SchmeNm/Cd {Iteracion "+ (i + 1) + "}";
															return null;
														}
													}else {
														
														this.codError = "CH21"; 
														this.desError = "El elemento es ausente"
																+ ". /CdtTrfTxInf/Dbtr/Id/PrvtId/Othr/SchmeNm {Iteracion "+ (i + 1) + "}";
														return null;
													}
												}else {
													
													this.codError = "CH21"; 
													this.desError = "El elemento es ausente"
															+ ". /CdtTrfTxInf/Dbtr/Id/PrvtId/Othr {Iteracion "+ (i + 1) + "}";
													return null;
												}
											}else {
												//Personas Juridicas
												if(snp.getFIToFICstmrCdtTrf()
												.getCdtTrfTxInf().get(i).getDbtr().getId().getOrgId() != null) {
													
													if(snp.getFIToFICstmrCdtTrf()
															.getCdtTrfTxInf().get(i).getDbtr().getId()
															.getOrgId().getOthr() != null) {
														
														if(!StringUtils.IsNullOrEmpty(snp.getFIToFICstmrCdtTrf()
															.getCdtTrfTxInf().get(i).getDbtr().getId()
															.getOrgId().getOthr().get(0).getId())) {
															
															iso.setISO_002_PAN(snp.getFIToFICstmrCdtTrf()
															.getCdtTrfTxInf().get(i).getDbtr().getId()
															.getOrgId().getOthr().get(0).getId());
															
														}else {
															
															this.codError = "CH21"; 
															this.desError = "El elemento es ausente"
																	+ ". /CdtTrfTxInf/Dbtr/Id/OrgId/Othr/Id {Iteracion "+ (i + 1) + "}";
															return null;
														}
														String tipDocJur = StringUtils.Empty();
														if(snp.getFIToFICstmrCdtTrf()
																.getCdtTrfTxInf().get(i).getDbtr().getId()
																.getOrgId().getOthr().get(0).getSchmeNm() != null) {
															
															if(!StringUtils.IsNullOrEmpty(snp.getFIToFICstmrCdtTrf()
																.getCdtTrfTxInf().get(i).getDbtr().getId()
																.getOrgId().getOthr().get(0).getSchmeNm().getCd())) {
															
																tipDocJur = snp.getFIToFICstmrCdtTrf()
																		.getCdtTrfTxInf().get(i).getDbtr().getId()
																		.getOrgId().getOthr().get(0).getSchmeNm().getCd();
																
																switch (tipDocJur) {
																case "TXID":
																		iso.setISO_022_PosEntryMode("RUC");
																	break;
																case "NIDN":
																		iso.setISO_022_PosEntryMode("CED");
																	break;
																case "ARNU":
																case "CCPT":
																	iso.setISO_022_PosEntryMode("PAS");
																break;
																default:
																	this.codError = "BE15"; 
																	this.desError = "Tipo de identificación no es valido o esta ausente."
																			+ ". /CdtTrfTxInf/Dbtr/Id/OrgId/Othr/SchmeNm/Cd {Iteracion "+ (i + 1) + "}";
																	return null;
																}
															}else {
																
																this.codError = "BE15"; 
																this.desError = "Tipo de identificación no es valido o esta ausente."
																		+ ". /CdtTrfTxInf/Dbtr/Id/OrgId/Othr/SchmeNm/Cd {Iteracion "+ (i + 1) + "}";
																return null;
															}
														}else {
															
															this.codError = "CH21"; 
															this.desError = "El elemento es ausente"
																	+ ". /CdtTrfTxInf/Dbtr/Id/OrgId/Othr/SchmeNm {Iteracion "+ (i + 1) + "}";
															return null;
														}
													}else {
														
														this.codError = "CH21"; 
														this.desError = "El elemento es ausente"
																+ ". /CdtTrfTxInf/Dbtr/Id/OrgId/Othr {Iteracion "+ (i + 1) + "}";
														return null;
													}
												}else {
													
													this.codError = "CH21"; 
													this.desError = "El elemento es ausente"
															+ ". /CdtTrfTxInf/Dbtr/Id/OrgId {Iteracion "+ (i + 1) + "}";
													return null;
												}
											}
										}else {
											
											this.codError = "CH21"; 
											this.desError = "El elemento es ausente"
													+ ". /CdtTrfTxInf/Dbtr/Id {Iteracion "+ (i + 1) + "}";
											return null;
										}
									}else {
										
										this.codError = "CH21"; 
										this.desError = "El elemento es ausente"
												+ ". /CdtTrfTxInf/Dbtr {Iteracion "+ (i+1) + "}";
										return null;
									}
									
									/*Numero de cuenta de la persona Debitante*/
									if(snp.getFIToFICstmrCdtTrf().getCdtTrfTxInf().get(i).getDbtrAcct() != null) {
										
										if(snp.getFIToFICstmrCdtTrf().getCdtTrfTxInf().get(i).getDbtrAcct()
												.getId() != null) {
											
											if(snp.getFIToFICstmrCdtTrf().getCdtTrfTxInf().get(i).getDbtrAcct()
													.getId().getOthr() != null) {
												if(!StringUtils.IsNullOrEmpty(snp.getFIToFICstmrCdtTrf().getCdtTrfTxInf().get(i).getDbtrAcct()
														.getId().getOthr().getId())) {
													
													iso.setISO_102_AccountID_1(snp.getFIToFICstmrCdtTrf().getCdtTrfTxInf().get(i).getDbtrAcct()
														.getId().getOthr().getId());
												}else {
													
													this.codError = "AC02"; 
													this.desError = "El número de cuenta del deudor es invalido o esta ausente."
															+ ". /CdtTrfTxInf/DbtrAcct/Id/Othr {Iteracion "+ (i+1) + "}";
													return null;
												}
												if(snp.getFIToFICstmrCdtTrf().getCdtTrfTxInf().get(i).getDbtrAcct().getTp() != null) {
													
													if(!StringUtils.IsNullOrEmpty(snp.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
															.get(i).getDbtrAcct().getTp().getPrtry())) {
														
														prefixAccDeb = snp.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
																.get(i).getDbtrAcct().getTp().getPrtry();
													
														prefixAccDeb = prefixAccDeb.equals("02")?"10":"20";
														if(!(prefixAccDeb != "02" || prefixAccDeb != "02")) {
															
															this.codError = "AC13"; 
															this.desError = "Tipo de cuenta del deudor invalida o ausente."
																	+ ". /CdtTrfTxInf/DbtrAcct/Id/Othr {Iteracion "+ (i+1) + "}";
															return null;
														}
													}
												}else {
													
													this.codError = "AC13"; 
													this.desError = "Tipo de cuenta del deudor invalida o ausente."
															+ ". /CdtTrfTxInf/DbtrAcct/Id/Othr {Iteracion "+ (i+1) + "}";
													return null;
													
												}
												//Implementar moneda 
											}else {
												
												this.codError = "CH21"; 
												this.desError = "El elemento es ausente"
														+ ". /CdtTrfTxInf/DbtrAcct/Id/Othr {Iteracion "+ (i+1) + "}";
												return null;
											}
										}else {
											
											this.codError = "CH21"; 
											this.desError = "El elemento es ausente"
													+ ". /CdtTrfTxInf/DbtrAcct/Id {Iteracion "+ (i+1) + "}";
											return null;
										}
									}else {
										
										this.codError = "CH21"; 
										this.desError = "El elemento es ausente"
												+ ". /CdtTrfTxInf/DbtrAcct {Iteracion "+ (i+1) + "}";
										return null;
									}
									
									/*DbtrAgt Institucion ORDENANTE*/
									if(snp.getFIToFICstmrCdtTrf().getCdtTrfTxInf().get(i).getDbtrAgt() != null) {
										
										if(snp.getFIToFICstmrCdtTrf().getCdtTrfTxInf().get(i).getDbtrAgt().getFinInstnId() != null) {
											
											if(!StringUtils.IsNullOrEmpty(snp.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
													.get(i).getDbtrAgt().getFinInstnId().getBICFI())) {
												
												iso.setISO_032_ACQInsID(snp.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
														.get(i).getDbtrAgt().getFinInstnId().getBICFI());
												
											}else {
												
												this.codError = "RC05"; 
												this.desError = "El identificador BIC es inválido o está ausente."
														+ ". /CdtTrfTxInf/DbtrAgt/FinInstnId/BICFI {Iteracion "+ (i+1) + "}";
												return null;
											}
										}else {
											
											this.codError = "CH21"; 
											this.desError = "El elemento es ausente"
													+ ". /CdtTrfTxInf/DbtrAgt/FinInstnId {Iteracion "+ (i+1) + "}";
											return null;
										}
										//Branch
										if(snp.getFIToFICstmrCdtTrf().getCdtTrfTxInf().get(i).getDbtrAgt().getBrnchId() != null) {
											
											if(!StringUtils.IsNullOrEmpty(snp.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
													.get(i).getDbtrAgt().getBrnchId().getId())) {
												
												iso.setISO_041_CardAcceptorID(snp.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
													.get(i).getDbtrAgt().getBrnchId().getId());
											}else {
												
												this.codError = "CH21"; 
												this.desError = "El elemento es ausente"
														+ ". /CdtTrfTxInf/DbtrAgt/BrnchId/Id {Iteracion "+ (i+1) + "}";
												return null;
											}
										}else {
											
											this.codError = "CH21"; 
											this.desError = "El elemento es ausente"
													+ ". /CdtTrfTxInf/DbtrAgt/BrnchId {Iteracion "+ (i+1) + "}";
											return null;
										}
									}else {
										
										this.codError = "CH21"; 
										this.desError = "El elemento es ausente"
												+ ". /CdtTrfTxInf/DbtrAgt {Iteracion "+ (i+1) + "}";
										return null;
									}
									
									/*DbtrAgtAcct cuenta corriente del debitante en el BCE*/
									if(snp.getFIToFICstmrCdtTrf().getCdtTrfTxInf().get(i).getDbtrAgtAcct() != null) {
									
										if(snp.getFIToFICstmrCdtTrf().getCdtTrfTxInf().get(i).getDbtrAgtAcct().getId() != null) {
											
											if(snp.getFIToFICstmrCdtTrf().getCdtTrfTxInf().get(i).getDbtrAgtAcct()
													.getId().getOthr() != null) {
												
												if(!StringUtils.IsNullOrEmpty(snp.getFIToFICstmrCdtTrf().getCdtTrfTxInf().get(i).getDbtrAgtAcct()
														.getId().getOthr().getId())) {
													
													iso.setISO_120_ExtendedData(snp.getFIToFICstmrCdtTrf().getCdtTrfTxInf().get(i).getDbtrAgtAcct()
														.getId().getOthr().getId());
												}else {
													
													this.codError = "CH21"; 
													this.desError = "El elemento es ausente"
															+ ". /CdtTrfTxInf/DbtrAgtAcct/Id/Othr/id {Iteracion "+ (i+1) + "}";
													return null;
												}
											}else {
												
												this.codError = "CH21"; 
												this.desError = "El elemento es ausente"
														+ ". /CdtTrfTxInf/DbtrAgtAcct/Id/Othr {Iteracion "+ (i+1) + "}";
												return null;
											}
										}else {
											
											this.codError = "CH21"; 
											this.desError = "El elemento es ausente"
													+ ". /CdtTrfTxInf/DbtrAgtAcct/Id {Iteracion "+ (i+1) + "}";
											return null;
										}
										//Implementar tipo de cuenta
										//Implementar tipo moneda
									}else {
										
										this.codError = "CH21"; 
										this.desError = "El elemento es ausente"
												+ ". /CdtTrfTxInf/DbtrAgtAcct {Iteracion "+ (i+1) + "}";
										return null;
									}
									
									/*CdtrAgtAcct Cuenta corriente de la RECEPTORA en el BCE*/
									if(snp.getFIToFICstmrCdtTrf().getCdtTrfTxInf().get(i).getCdtrAgtAcct() != null) {
										
										if(snp.getFIToFICstmrCdtTrf().getCdtTrfTxInf().get(i)
												                 .getCdtrAgtAcct().getId() != null) {
											
											if(snp.getFIToFICstmrCdtTrf().getCdtTrfTxInf().get(i)
									                 .getCdtrAgtAcct().getId().getOthr() != null) {
												
												if(!StringUtils.IsNullOrEmpty(snp.getFIToFICstmrCdtTrf().getCdtTrfTxInf().get(i)
									                 .getCdtrAgtAcct().getId().getOthr().getId())) {
													
													iso.setISO_122_ExtendedData(snp.getFIToFICstmrCdtTrf().getCdtTrfTxInf().get(i)
									                 .getCdtrAgtAcct().getId().getOthr().getId());
													
												}else {
													
													this.codError = "CH21"; 
													this.desError = "El elemento es ausente"
															+ ". /CdtTrfTxInf/CdtrAgtAcct/Id/Othr/Id {Iteracion "+ (i+1) + "}";
													return null;
												}
												
											}else {
												
												this.codError = "CH21"; 
												this.desError = "El elemento es ausente"
														+ ". /CdtTrfTxInf/CdtrAgtAcct/Id/Othr {Iteracion "+ (i+1) + "}";
												return null;
											}
											
										}else {
											
											this.codError = "CH21"; 
											this.desError = "El elemento es ausente"
													+ ". /CdtTrfTxInf/CdtrAgtAcct/Id {Iteracion "+ (i+1) + "}";
											return null;
										}
										
									}else {
										
										this.codError = "CH21"; 
										this.desError = "El elemento es ausente"
												+ ". /CdtTrfTxInf/CdtrAgtAcct {Iteracion "+ (i+1) + "}";
										return null;
									}
									
									/*CdtrAgt Institucion RECEPTORA*/
									if(snp.getFIToFICstmrCdtTrf().getCdtTrfTxInf().get(i).getCdtrAgt() != null) {
										
										if(snp.getFIToFICstmrCdtTrf().getCdtTrfTxInf().get(i).getCdtrAgt().getFinInstnId() != null) {
											
											if(!StringUtils.IsNullOrEmpty(snp.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
													.get(i).getCdtrAgt().getFinInstnId().getBICFI())) {
												iso.setISO_033_FWDInsID(snp.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
														.get(i).getCdtrAgt().getFinInstnId().getBICFI());
												
												if(!iso.getISO_033_FWDInsID().equals(MemoryGlobal.UrlSpiCodeEfi_BCE)) {
													
													this.codError = "RC05"; 
													this.desError = "El identificador BIC es inválido o esta ausente."
															+ ". /CdtTrfTxInf/CdtrAgt/FinInstnId {Iteracion "+ (i+1) + "}";
													return null;
												}
												
											}else {
												
												this.codError = "RC05"; 
												this.desError = "El identificador BIC es inválido o esta ausente."
														+ ". /CdtTrfTxInf/CdtrAgt/FinInstnId {Iteracion "+ (i+1) + "}";
												return null;
											}
										}else {
											
											this.codError = "CH21"; 
											this.desError = "El elemento es ausente"
													+ ". /CdtTrfTxInf/CdtrAgt/FinInstnId {Iteracion "+ (i+1) + "}";
											return null;
										}
									}else {
										
										this.codError = "CH21"; 
										this.desError = "El elemento es ausente"
												+ ". /CdtTrfTxInf/CdtrAgt {Iteracion "+ (i+1) + "}";
										return null;
									}
									
									/*Cdtr Nombres de la Persona Receptora*/
									if(snp.getFIToFICstmrCdtTrf().getCdtTrfTxInf().get(i).getCdtr() != null) {
										
										if(!StringUtils.IsNullOrEmpty(snp.getFIToFICstmrCdtTrf().getCdtTrfTxInf().get(i).getCdtr().getNm())) {
											
											iso.setISO_114_ExtendedData(snp.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
													.get(i).getCdtr().getNm());
										}else {
											
											this.codError = "BE22"; 
											this.desError = "El nombre del acreedor es invalido o ausente"
													+ ". /CdtTrfTxInf/Cdtr/Nm {Iteracion "+ (i+1) + "}";
											return null;
										}
										if(snp.getFIToFICstmrCdtTrf().getCdtTrfTxInf().get(i).getCdtr().getId() != null) {
											
											
											if(snp.getFIToFICstmrCdtTrf().getCdtTrfTxInf().get(i).getCdtr().getId().getPrvtId() != null) {
												
												//Personas naturales
												if(snp.getFIToFICstmrCdtTrf().getCdtTrfTxInf().get(i)
														.getCdtr().getId().getPrvtId().getOthr() != null) {
													
													if(!StringUtils.IsNullOrEmpty(snp.getFIToFICstmrCdtTrf().getCdtTrfTxInf().get(i)
														.getCdtr().getId().getPrvtId().getOthr().get(0).getId())) {
														
														iso.setISO_123_ExtendedData(snp.getFIToFICstmrCdtTrf().getCdtTrfTxInf().get(i)
														.getCdtr().getId().getPrvtId().getOthr().get(0).getId());
													}else {
														
														this.codError = "CH11"; 
														this.desError = "El valor en el identificador del acreedor es incorrecto."
																+ ". /CdtTrfTxInf/Cdtr/Id/PrvtId/Othr/Id {Iteracion "+ (i+1) + "}";
														return null;
													}
													if(snp.getFIToFICstmrCdtTrf().getCdtTrfTxInf().get(i)
															.getCdtr().getId().getPrvtId().getOthr().get(0).getSchmeNm() != null) {
														
														String tipoDoc = StringUtils.Empty();
														if(!StringUtils.IsNullOrEmpty(snp.getFIToFICstmrCdtTrf().getCdtTrfTxInf().get(i)
															.getCdtr().getId().getPrvtId().getOthr().get(0).getSchmeNm().getCd())) {
															
															tipoDoc = snp.getFIToFICstmrCdtTrf().getCdtTrfTxInf().get(i)
																	.getCdtr().getId().getPrvtId().getOthr().get(0)
																	.getSchmeNm().getCd();
															
															switch (tipoDoc) {
															case "TXID":
																	iso.setISO_035_Track2("RUC");
																break;
															case "NIDN":
																	iso.setISO_035_Track2("CED");
																break;
															case "ARNU":
															case "CCPT":
																iso.setISO_035_Track2("PAS");
															break;
															default:
																
																this.codError = "BE15"; 
																this.desError = "Tipo de identificación no es valido o esta ausente."
																		+ ". /CdtTrfTxInf/Cdtr/Id/PrvtId/Othr/SchmeNm {Iteracion "+ (i+1) + "}";
																return null;
															}
														}else {
															
															this.codError = "BE15"; 
															this.desError = "Tipo de identificación no es valido o esta ausente."
																	+ ". /CdtTrfTxInf/Cdtr/Id/PrvtId/Othr/SchmeNm {Iteracion "+ (i+1) + "}";
															return null;
														}
													}else {
														
														this.codError = "CH21"; 
														this.desError = "El elemento es ausente"
																+ ". /CdtTrfTxInf/Cdtr/Id/PrvtId/Othr/SchmeNm {Iteracion "+ (i+1) + "}";
														return null;
													}
												}else {
													
													this.codError = "CH21"; 
													this.desError = "El elemento es ausente"
															+ ". /CdtTrfTxInf/Cdtr/Id/PrvtId/Othr {Iteracion "+ (i+1) + "}";
													return null;
												}
											}else {
												//Personas Juridicas
												
												if(snp.getFIToFICstmrCdtTrf()
												.getCdtTrfTxInf().get(i).getCdtr().getId().getOrgId() != null) {
													
													if(snp.getFIToFICstmrCdtTrf()
															.getCdtTrfTxInf().get(i).getCdtr().getId()
															.getOrgId().getOthr() != null) {
														
														if(!StringUtils.IsNullOrEmpty(snp.getFIToFICstmrCdtTrf()
															.getCdtTrfTxInf().get(i).getCdtr().getId()
															.getOrgId().getOthr().get(0).getId())) {
															
															iso.setISO_123_ExtendedData(snp.getFIToFICstmrCdtTrf()
															.getCdtTrfTxInf().get(i).getCdtr().getId()
															.getOrgId().getOthr().get(0).getId());
														}else {
															
															this.codError = "CH11"; 
															this.desError = "El valor en el identificador del acreedor es incorrecto."
																	+ ". /CdtTrfTxInf/Cdtr/Id/OrgId/Othr/Id {Iteracion "+ (i+1) + "}";
															return null;
														}
														String tipDocJur = StringUtils.Empty();
														if(snp.getFIToFICstmrCdtTrf()
																.getCdtTrfTxInf().get(i).getCdtr().getId()
																.getOrgId().getOthr().get(0).getSchmeNm() != null) {
															
															if(!StringUtils.IsNullOrEmpty(snp.getFIToFICstmrCdtTrf()
																.getCdtTrfTxInf().get(i).getCdtr().getId()
																.getOrgId().getOthr().get(0).getSchmeNm().getCd())) {
															
																tipDocJur = snp.getFIToFICstmrCdtTrf()
																		.getCdtTrfTxInf().get(i).getCdtr().getId()
																		.getOrgId().getOthr().get(0).getSchmeNm().getCd();
																
																switch (tipDocJur) {
																case "TXID":
																		iso.setISO_035_Track2("RUC");
																	break;
																case "NIDN":
																		iso.setISO_035_Track2("CED");
																	break;
																case "ARNU":
																case "CCPT":
																	iso.setISO_035_Track2("PAS");
																break;
																default:
																	this.codError = "BE15"; 
																	this.desError = "Tipo de identificación no es valido o esta ausente."
																			+ ". /CdtTrfTxInf/Cdtr/Id/OrgId/Othr/SchmeNm/Cd {Iteracion "+ (i+1) + "}";
																	return null;
																}
															}else {
																
																this.codError = "BE15"; 
																this.desError = "Tipo de identificación no es valido o esta ausente."
																		+ ". /CdtTrfTxInf/Cdtr/Id/OrgId/Othr/SchmeNm/Cd {Iteracion "+ (i+1) + "}";
																return null;
															}
														}else {
															
															this.codError = "CH21"; 
															this.desError = "El elemento es ausente"
																	+ ". /CdtTrfTxInf/Cdtr/Id/OrgId/Othr/SchmeNm {Iteracion "+ (i+1) + "}";
															return null;
														}
													}else {
														
														this.codError = "CH21"; 
														this.desError = "El elemento es ausente"
																+ ". /CdtTrfTxInf/Cdtr/Id/OrgId/Othr {Iteracion "+ (i+1) + "}";
														return null;
													}
												}else {
													
													this.codError = "CH21"; 
													this.desError = "El elemento es ausente"
															+ ". /CdtTrfTxInf/Cdtr/Id/OrgId {Iteracion "+ (i+1) + "}";
													return null;
												}
											}
										}else {
											
											this.codError = "CH21"; 
											this.desError = "El elemento es ausente"
													+ ". /CdtTrfTxInf/Cdtr/Id {Iteracion "+ (i+1) + "}";
											return null;
										}
										
									}else {
										
										this.codError = "CH21"; 
										this.desError = "El elemento es ausente"
												+ ". /CdtTrfTxInf/Cdtr {Iteracion "+ (i+1) + "}";
										return null;
									}
									
									/*CdtrAcct Datos de la Cuenta de la Persona Receptora*/
									if(snp.getFIToFICstmrCdtTrf().getCdtTrfTxInf().get(i).getCdtrAcct() != null) {
										
										if(snp.getFIToFICstmrCdtTrf().getCdtTrfTxInf().get(i).getCdtrAcct().getId() != null) {
											
											if(snp.getFIToFICstmrCdtTrf().getCdtTrfTxInf().get(i).getCdtrAcct().getId().getOthr() != null) {
												
												if(!StringUtils.IsNullOrEmpty(snp.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
														.get(i).getCdtrAcct().getId().getOthr().getId())) {
													
													if(NumbersUtils.isNumeric(snp.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
															.get(i).getCdtrAcct().getId().getOthr().getId())) {
														
															iso.setISO_103_AccountID_2(snp.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
															.get(i).getCdtrAcct().getId().getOthr().getId());
													}else {
														
														this.codError = "AC03"; 
														this.desError = "El número de cuenta del acreedor es invalido o esta ausente."
																+ ". /CdtTrfTxInf/CdtrAcct/Id/Othr/Id {Iteracion "+ (i+1) + "}";
														return null;
													}
													
												}else {
													
													this.codError = "AC03"; 
													this.desError = "El número de cuenta del acreedor es invalido o esta ausente."
															+ ". /CdtTrfTxInf/CdtrAcct/Id/Othr/Id {Iteracion "+ (i+1) + "}";
													return null;
												}
											}else {
												
												this.codError = "CH21"; 
												this.desError = "El elemento es ausente"
														+ ". /CdtTrfTxInf/CdtrAcct/Id/Othr {Iteracion "+ (i+1) + "}";
												return null;
											}
										}else {
											
											this.codError = "CH21"; 
											this.desError = "El elemento es ausente"
													+ ". /CdtTrfTxInf/CdtrAcct/Id {Iteracion "+ (i+1) + "}";
											return null;
										}
										
										if(snp.getFIToFICstmrCdtTrf().getCdtTrfTxInf().get(i).getCdtrAcct().getTp() != null) {
											
											if(!StringUtils.IsNullOrEmpty(snp.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
													.get(i).getCdtrAcct().getTp().getPrtry())) {
												
												prefixAccAcred = snp.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
														.get(i).getCdtrAcct().getTp().getPrtry();
												prefixAccAcred = prefixAccAcred.equals("02")?"10":"20";
											}else {
												
												this.codError = "AC14"; 
												this.desError = "Tipo de cuenta del acreedor invalida o ausente."
														+ ". /CdtTrfTxInf/CdtrAcct/Tp/Prtry {Iteracion "+ (i+1) + "}";
												return null;
											}
											
										}else {
											
											this.codError = "CH21"; 
											this.desError = "El elemento es ausente"
													+ ". /CdtTrfTxInf/CdtrAcct/Tp {Iteracion "+ (i+1) + "}";
											return null;
										}
										//Implementar moneda
										if(!StringUtils.IsNullOrEmpty(snp.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
												.get(i).getCdtrAcct().getCcy())) {
											iso.setISO_051_CardCurrCode(snp.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
												.get(i).getCdtrAcct().getCcy()=="USD"?840:0);
											
										}else {
											
											this.codError = "AC09"; 
											this.desError = "La moneda de la cuenta es invalida o ausente"
													+ ". /CdtTrfTxInf/CdtrAcct/Tp {Iteracion "+ (i+1) + "}";
											return null;
										}
									}else {
										
										this.codError = "CH21"; 
										this.desError = "El elemento es ausente"
												+ ". /CdtTrfTxInf/CdtrAcct {Iteracion "+ (i+1) + "}";
										return null;
									}
									
									/* Purp Razon de la transferencia*/
									if(snp.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
											.get(i).getPurp() != null) {
										if(!StringUtils.IsNullOrEmpty(snp.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
											.get(i).getPurp().getPrtry())) {
											
											iso.setISO_121_ExtendedData(snp.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
											.get(i).getPurp().getPrtry());
										}else {
											
											this.codError = "CH21"; 
											this.desError = "El elemento es ausente"
													+ ". /CdtTrfTxInf/Purp/Prtry {Iteracion "+ (i+1) + "}";
											return null;
										}
									}else {
										
										this.codError = "CH21"; 
										this.desError = "El elemento es ausente"
												+ ". /CdtTrfTxInf/Purp {Iteracion "+ (i+1) + "}";
										return null;
									}
									validaNroTrx ++;	
								}finally {
									
									if(!StringUtils.IsNullOrEmpty(prefixAccDeb) && !StringUtils.IsNullOrEmpty(prefixAccAcred)) {
										
										iso.setISO_003_ProcessingCode("71" + prefixAccDeb + prefixAccAcred);
									}
										
								}
							
								isoList.add(iso);
								
								if(snp.getFIToFICstmrCdtTrf().getCdtTrfTxInf().size() > 1) {
									
									Iso8583 isoClone = (Iso8583) iso.clone();
									iso = new Iso8583();
									/*iso.setISO_000_Message_Type("1200");
									iso.setISO_024_NetworkId("555777");
									iso.setISO_018_MerchantType("0007");*/
									iso = isoClone;
									iso.setISO_115_ExtendedData(StringUtils.Empty());//Por serialziacion
									
								}
								
							} //Fin For
							
							/*Validacion unicos EndtoEndId*/
							if(validateEndtoEndId != null) {
								
								if(validateEndtoEndId.size() != validaNroTrx) {
									
									this.codError = "DU04"; 
									this.desError = "El identificador de inicio a fin (campo EndToEndIdentification) no es unico";
									return null;
								}
							}
							/*Validacion unicos TxId*/
							if(validateTxId != null) {
								
								if(validateTxId.size() != validaNroTrx) {
									
									this.codError = "DU03"; 
									this.desError = "La transacción no es unica (TxId)";
									return null;
								}
							}
							
							/*Validacion Sumatoria valores*/
							String sumatoria = df.format(isoList.get(0).getISO_008_BillFeeAmount());
							if(!df.format(validateMonto).equals(sumatoria)){
								
								this.codError = "AM10"; 
								this.desError = "La suma de los montos enviados no coincide con la suma de control (campo ControlSum).";
								return null;
							}
							/*Validacion Nro. de Transacciones*/
							if(NumbersUtils.truncateDecimal(validaNroTrx, 2) != NumbersUtils.truncateDecimal(isoList.get(0).getISO_006_BillAmount(),2)) {
								
								this.codError = "AM18"; 
								this.desError = "El numero total de transacciones no coincide";
								return null;
							}
							
						}else{//Size > 0
							
							this.codError = "CH21"; 
							this.desError = "El elemento es ausente"
									+ ". /FIToFICstmrCdtTrf/CdtTrfTxInf. Varios elementos";
							return null;
						}
					}else {
						
						this.codError = "CH21"; 
						this.desError = "El elemento es ausente"
								+ ". /FIToFICstmrCdtTrf/CdtTrfTxInf";
						return null;
					}
				}else {
					
					this.codError = "CH21"; 
					this.desError = "El elemento es ausente"
							+ ". /FIToFICstmrCdtTrf";
					return null;
				}
			}else {
				
				this.codError = "CH21"; 
				this.desError = "El elemento es ausente"
						+ ". urn:iso:std:iso:20022:tech:xsd:pacs.008.001.04";
				return null;
			}
			
		} catch (Exception e) {
				
			log.WriteLogMonitor("Error modulo BceSPIParser::parseDocumentSolicitud ", TypeMonitor.error, e);
			this.codError = "TA01";
			this.desError = "La transmision del archivo no fue exitosa, tuvo que ser abortada (por razones tecnicas).";
			
		}finally {
			
			if(isoList != null && isoList.size() > 1) {
				
				//Collections.sort(isoList, (p1 , p2) -> p1.getISO_BitMap().compareTo(p2.getISO_BitMap()));
			}
		}
		return isoList;
	}
   
	public List<SnpSPIOrdenante> parseRespuestasSNP_SPI_BCE_Async(DocumentRespuesta document){
		
		List<SnpSPIOrdenante> snpSpiList = new ArrayList<>();
		@SuppressWarnings("unused")
		String campo = null;
		try {
			
			SnpSPIOrdenante snp = null;
			if(document != null){
				
				snp = new SnpSPIOrdenante();
				if(document.getFIToFIPmtStsRpt() != null){
					
					if(document.getFIToFIPmtStsRpt().getGrpHdr() != null){
						
						if(!StringUtils.IsNullOrEmpty(document.getFIToFIPmtStsRpt().getGrpHdr().getMsgId())){
							
							/*MSGID*/
							snp.setMsgid_last_be(document.getFIToFIPmtStsRpt().getGrpHdr().getMsgId());
							
							if(document.getFIToFIPmtStsRpt().getGrpHdr().getCreDtTm() != null){
								
								snp.setCredttm(document.getFIToFIPmtStsRpt().getGrpHdr().getCreDtTm()
										.toGregorianCalendar().getTime());
								
								if(document.getFIToFIPmtStsRpt().getOrgnlGrpInfAndSts() != null){
									
									if(!StringUtils.IsNullOrEmpty(document.getFIToFIPmtStsRpt()
											             .getOrgnlGrpInfAndSts().getOrgnlMsgId())){
										/*ORIGINAL_MSGID*/
										if(document.getFIToFIPmtStsRpt()
									             .getOrgnlGrpInfAndSts().getOrgnlMsgId().startsWith("SPI"))
											snp.setOrdenante(true);
										else
											snp.setOrdenante(false);
											
										snp.setMsgid(document.getFIToFIPmtStsRpt()
											             .getOrgnlGrpInfAndSts().getOrgnlMsgId());
										
										if(document.getFIToFIPmtStsRpt().getTxInfAndSts() != null){
											
											for (int i = 0; i < document.getFIToFIPmtStsRpt().getTxInfAndSts().size(); i++) {
											
												if(!StringUtils.IsNullOrEmpty(document.getFIToFIPmtStsRpt()
														                .getTxInfAndSts().get(i).getOrgnlEndToEndId())){
													/*ENDTOENDID*/
													snp.setEndtoendid(document.getFIToFIPmtStsRpt()
															.getTxInfAndSts().get(i).getOrgnlEndToEndId());
													
													if(!StringUtils.IsNullOrEmpty(document.getFIToFIPmtStsRpt()
											                .getTxInfAndSts().get(i).getOrgnlTxId())){
														/*TXID*/
														snp.setTxid(document.getFIToFIPmtStsRpt()
											                .getTxInfAndSts().get(i).getOrgnlTxId());
														
														if(document.getFIToFIPmtStsRpt()
												                .getTxInfAndSts().get(i).getTxSts() != null){
															/*TxSts  ACSP o ACSC como ORDENANTES y ACCP como Receptor */
															snp.setStatus_bce(document.getFIToFIPmtStsRpt()
													                .getTxInfAndSts().get(i).getTxSts().name().toString());
															
															if(document.getFIToFIPmtStsRpt().getTxInfAndSts().get(i).getStsRsnInf() != null){
																
																if(document.getFIToFIPmtStsRpt().getTxInfAndSts().get(i).
																		 getStsRsnInf().get(0).getRsn() != null){
																	
																	snp.setCod_error_auth(document.getFIToFIPmtStsRpt().getTxInfAndSts().get(i).
																		 getStsRsnInf().get(0).getRsn().getCd());
																	
																	if(document.getFIToFIPmtStsRpt().getTxInfAndSts().get(i)
																		 .getStsRsnInf().get(0).getAddtlInf() != null){
																		
																		snp.setDes_error_auth(document.getFIToFIPmtStsRpt().getTxInfAndSts().get(i)
																				 .getStsRsnInf().get(0).getAddtlInf().get(0));
																	}
																}
															}
															
														}
													}
												}
												
												snpSpiList.add(snp);
												
												if(document.getFIToFIPmtStsRpt().getTxInfAndSts().size() > 1){
													
													SnpSPIOrdenante snpClone = (SnpSPIOrdenante) snp.clone();
													snp = new SnpSPIOrdenante();
													snp = snpClone;
												}
													
											}//Fin FOR
										}
									}
								}
							}
						}
					}
				}
				
			}else{
				
				this.codError = "909";
			}
			
		} catch (Exception e) {
		
			log.WriteLogMonitor("Error modulo BceSPIParser::parseRespuestasSNP_SPI_BCE_Async ", TypeMonitor.error, e);
		}
		
		return snpSpiList;
	}
	
	public List<SnpSPIOrdenante> parseReversosSNP_SPI_BCE_Async(DocumentReverso document){
		List<SnpSPIOrdenante> snpSpiList = new ArrayList<>();
		try {
			SnpSPIOrdenante snp = null;
			if(document != null) {
				
				snp = new SnpSPIOrdenante();
				if(document.getPmtRtr() != null) {
					
					if(document.getPmtRtr().getGrpHdr() != null) {
					
						if(!StringUtils.IsNullOrEmpty(document.getPmtRtr().getGrpHdr().getMsgId())) {
							
							/*MsgId*/
							snp.setMsgid_last_be(document.getPmtRtr().getGrpHdr().getMsgId());
						}
						if(document.getPmtRtr().getGrpHdr().getCreDtTm() != null) {
							
							snp.setDate_last_bce(document.getPmtRtr().getGrpHdr().getCreDtTm()
									.toGregorianCalendar().getTime());
						}
						
					}
				}
				/*Informacion Original*/
				if(document.getPmtRtr().getOrgnlGrpInf() != null){
					
					if(!StringUtils.IsNullOrEmpty(document.getPmtRtr().getOrgnlGrpInf().getOrgnlMsgId())) {
						
						if(document.getPmtRtr().getOrgnlGrpInf().getOrgnlMsgId().startsWith("SPI"))
							snp.setOrdenante(true);
						else
							snp.setOrdenante(true);
						
						snp.setMsgid(document.getPmtRtr().getOrgnlGrpInf().getOrgnlMsgId());
					}
					
					if(document.getPmtRtr().getOrgnlGrpInf().getOrgnlCreDtTm() != null) {
						
						snp.setCredttm(document.getPmtRtr().getOrgnlGrpInf().getOrgnlCreDtTm()
								.toGregorianCalendar().getTime());
					}
				}
				
				/*COMIENZA EL FOR*/
				if(document.getPmtRtr().getTxInf() != null) {
					
					for (int i = 0; i < document.getPmtRtr().getTxInf().size(); i++) {
						
						if((document.getPmtRtr().getTxInf().get(i).getOrgnlGrpInf() != null)){
							
							
							if(!StringUtils.IsNullOrEmpty(document.getPmtRtr().getTxInf().get(i)
									.getOrgnlGrpInf().getOrgnlMsgId())) {
								
								if(document.getPmtRtr().getTxInf().get(i)
										.getOrgnlGrpInf().getOrgnlMsgId().startsWith("SPI"))
								   snp.setOrdenante(true);
								else
								   snp.setOrdenante(false);
							}
							
							if(document.getPmtRtr().getTxInf().get(i).getOrgnlGrpInf().getOrgnlCreDtTm() != null) {
								
								snp.setCredttm(document.getPmtRtr().getTxInf().get(i).getOrgnlGrpInf()
										.getOrgnlCreDtTm().toGregorianCalendar().getTime());
							}
						}
						
						if(!StringUtils.IsNullOrEmpty(document.getPmtRtr().getTxInf().get(i).getOrgnlInstrId())) {
							
							snp.setInstrid(document.getPmtRtr().getTxInf().get(i).getOrgnlInstrId());
						}
						
						if(!StringUtils.IsNullOrEmpty(document.getPmtRtr().getTxInf().get(i).getOrgnlEndToEndId())) {
							
							snp.setEndtoendid(document.getPmtRtr().getTxInf().get(i).getOrgnlEndToEndId());
						}
						
						if(!StringUtils.IsNullOrEmpty(document.getPmtRtr().getTxInf().get(i).getOrgnlTxId())) {
							
							snp.setTxid(document.getPmtRtr().getTxInf().get(i).getOrgnlTxId());
						}
						
						if(document.getPmtRtr().getTxInf().get(i).getRtrdInstdAmt() != null) {
							
							if(document.getPmtRtr().getTxInf().get(i).getRtrdInstdAmt().getValue() != null) {
								
								snp.setAmmount(document.getPmtRtr().getTxInf().get(i)
										.getRtrdInstdAmt().getValue().doubleValue());
							}
						}
						
						if(document.getPmtRtr().getTxInf().get(i).getRtrRsnInf() != null) {
							
							if(document.getPmtRtr().getTxInf().get(i).getRtrRsnInf().get(0).getRsn() != null) {
								
								if(!StringUtils.IsNullOrEmpty(document.getPmtRtr().getTxInf()
										.get(i).getRtrRsnInf().get(0).getRsn().getCd())) {
								
									snp.setCod_error_auth(document.getPmtRtr().getTxInf()
											.get(i).getRtrRsnInf().get(0).getRsn().getCd());
									
									snp.setStatus_reason_bce(snp.getCod_error_auth());
								}
								
								if(document.getPmtRtr().getTxInf().get(i).getRtrRsnInf().get(0).getAddtlInf() != null) {
									
									if(!StringUtils.IsNullOrEmpty(document.getPmtRtr().getTxInf().
											get(i).getRtrRsnInf().get(0).getAddtlInf().get(0).toString())) {
										
										snp.setDes_error_auth(document.getPmtRtr().getTxInf().
												get(i).getRtrRsnInf().get(0).getAddtlInf().get(0).toString());
										
										snp.setStatus_reason_bce(snp.getStatus_reason_bce() + " - " + snp.getDes_error_auth());
									}
								}
								
							}
						}
						snp.setStatus_bce("RVRS");
						snpSpiList.add(snp);
						
						if(document.getPmtRtr().getTxInf().size() > 1){
							
							SnpSPIOrdenante snpClone = (SnpSPIOrdenante) snp.clone();
							snp = new SnpSPIOrdenante();
							snp = snpClone;
						}
						 
					}//FIN FOR
				}
				
			}else {
				
				this.codError = "909";
			}
			
		} catch (Exception e) {
			
			log.WriteLogMonitor("Error modulo BceSPIParser::parseReversosSNP_SPI_BCE_Async ", TypeMonitor.error, e);
		}
		return snpSpiList;
	}
	
	public Iso8583 parseSnpLotesToIso8583(SnpOrdLotes snp) {
		
		Iso8583 iso = null;
		try {
			
			iso = new Iso8583();
			iso.setISO_BitMap("LOTES");
			iso.setISO_000_Message_Type("1200");
			iso.setISO_002_PAN(snp.getOrd_identificacion().trim());
			iso.setISO_003_ProcessingCode("81" + (snp.getOrd_tipo_cta().trim().equals("AHO")? "10":"20") 
					   + (snp.getRecep_tipo_cta().trim().equals("AHO")?"10":"20"));
			iso.setISO_004_AmountTransaction(snp.getValor_transferencia());
			iso.setISO_007_TransDatetime(FormatUtils.StringToDate(snp.getFecha().trim(), "yyyy-MM-dd HH:mm:ss"));
			iso.setISO_012_LocalDatetime(FormatUtils.StringToDate(snp.getFecha().trim(), "yyyy-MM-dd HH:mm:ss"));
			iso.setISO_011_SysAuditNumber(snp.getNum_referencia());
			iso.setISO_015_SettlementDatel(FormatUtils.StringToDate(snp.getFecha().trim(), "yyyy-MM-dd"));
			iso.setISO_018_MerchantType("0005");
			iso.setISO_022_PosEntryMode(snp.getOrd_tipo_identificacion().trim());
			iso.setISO_024_NetworkId("555777");
			iso.setISO_032_ACQInsID(MemoryGlobal.UrlSpiCodeEfi_BCE);
			iso.setISO_033_FWDInsID(snp.getRecep_institucion());
			iso.setISO_034_PANExt(snp.getOrd_nombres().toUpperCase().trim());
			iso.setISO_035_Track2(snp.getRecep_tipo_identificacion());
			iso.setISO_041_CardAcceptorID("1"); //Toca aumentar en la tabla el canal....
			iso.setISO_042_Card_Acc_ID_Code("1"); //Toca aumentar en la tabla la terminal....
			iso.setISO_043_CardAcceptorLoc("OFICINA MATRIZ"); //Toca aumentar en la tabla la descripcion terminal....
			iso.setISO_051_CardCurrCode(840);
			iso.setISO_102_AccountID_1(snp.getOrd_numero_cuenta().trim());
			iso.setISO_103_AccountID_2(snp.getRecep_numero_cuenta());
			iso.setISO_114_ExtendedData(snp.getRecep_nombres());
			iso.setISO_115_ExtendedData(snp.getRecep_identificacion());
			iso.setISO_120_ExtendedData("1");
			iso.setISO_121_ExtendedData(snp.getMotivo_transferencia());
			iso.setISO_039_ResponseCode("100");
			
		} catch (Exception e) {
		
			log.WriteLogMonitor("Error modulo BceSPIParser::parseSnpLotesToIso8583 ", TypeMonitor.error, e);
		}
		
		return iso;
	}
	
    public Iso8583 parseSnpLotesToIso8583_V2(SnpOrdLotes snp, String msgId) {
		
		Iso8583 iso = null;
		try {
			
			iso = new Iso8583();
			iso.setISO_BitMap("LOTES");
			iso.setISO_000_Message_Type("1200");
			iso.setISO_002_PAN(snp.getOrd_identificacion().trim());
			iso.setISO_003_ProcessingCode("81" + (snp.getOrd_tipo_cta().trim().equals("AHO")? "10":"20") 
					   + (snp.getRecep_tipo_cta().trim().equals("AHO")?"10":"20"));
			iso.setISO_004_AmountTransaction(snp.getValor_transferencia());
			iso.setISO_007_TransDatetime(FormatUtils.StringToDate(snp.getFecha().trim(), "yyyy-MM-dd HH:mm:ss"));
			iso.setISO_012_LocalDatetime(FormatUtils.StringToDate(snp.getFecha().trim(), "yyyy-MM-dd HH:mm:ss"));
			iso.setISO_011_SysAuditNumber(snp.getNum_referencia());
			iso.setISO_015_SettlementDatel(FormatUtils.StringToDate(snp.getFecha().trim(), "yyyy-MM-dd"));
			iso.setISO_018_MerchantType("0003");
			iso.setISO_022_PosEntryMode(snp.getOrd_tipo_identificacion().trim());
			iso.setISO_024_NetworkId("555777");
			iso.setISO_032_ACQInsID(MemoryGlobal.UrlSpiCodeEfi_BCE);
			iso.setISO_033_FWDInsID(snp.getRecep_institucion());
			iso.setISO_034_PANExt(snp.getOrd_nombres().toUpperCase().trim());
			iso.setISO_035_Track2(snp.getRecep_tipo_identificacion());
			iso.setISO_041_CardAcceptorID("1"); //Toca aumentar en la tabla el canal....
			iso.setISO_042_Card_Acc_ID_Code("1"); //Toca aumentar en la tabla la terminal....
			iso.setISO_043_CardAcceptorLoc("OFICINA MATRIZ"); //Toca aumentar en la tabla la descripcion terminal....
			iso.setISO_051_CardCurrCode(840);
			iso.setISO_090_OriginalData(msgId);
			iso.setISO_102_AccountID_1(snp.getOrd_numero_cuenta().trim());
			iso.setISO_103_AccountID_2(snp.getRecep_numero_cuenta());
			iso.setISO_114_ExtendedData(snp.getRecep_nombres());
			iso.setISO_115_ExtendedData(snp.getRecep_identificacion());
			iso.setISO_120_ExtendedData("1");
			iso.setISO_121_ExtendedData(snp.getMotivo_transferencia());
			iso.setISO_039_ResponseCode("100");
			
			
		} catch (Exception e) {
		
			log.WriteLogMonitor("Error modulo BceSPIParser::parseSnpLotesToIso8583 ", TypeMonitor.error, e);
		}finally {
		
			iso.setISO_124_ExtendedData(SerializationObject.ObjectToString(iso, Iso8583.class));
		}
		
		return iso;
	}

	@Override
	public Iso8583 call() throws Exception {
		
		return parseSnpLotesToIso8583(this.snpLote);
	}
	
	public Callable<Iso8583> callableLotesV2(){
	
		Callable<Iso8583> callable = new Callable<Iso8583>() {
			
			@Override
			public Iso8583 call() throws Exception {
				
				return parseSnpLotesToIso8583_V2(snpLote,MsgIdLote);
			}
		};
		
		return callable;
	}
	
}
