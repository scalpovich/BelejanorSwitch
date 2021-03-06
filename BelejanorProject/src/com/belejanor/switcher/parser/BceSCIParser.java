package com.belejanor.switcher.parser;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.belejanor.switcher.cscoreswitch.Iso8583;
import com.belejanor.switcher.logger.Logger;
import com.belejanor.switcher.logger.LoggerConfig.TypeMonitor;
import com.belejanor.switcher.memcached.MemoryGlobal;
import com.belejanor.switcher.snp.spi.SnpOrdLotes;
import com.belejanor.switcher.snp.spi.SnpSPIOrdenante;
import com.belejanor.switcher.utils.FormatUtils;
import com.belejanor.switcher.utils.NumbersUtils;
import com.belejanor.switcher.utils.StringUtils;

import urn.iso.std.iso20022.tech.xsd.pacs_003_001_04.ChargeBearerType1Code;
import urn.iso.std.iso20022.tech.xsd.pacs_003_001_04.DocumentCobroSCI;
import urn.iso.std.iso20022.tech.xsd.pacs_003_001_04.SettlementMethod2Code;


public class BceSCIParser {

	private Logger log;
	private String codError;
	private String desError; 
	private List<SnpSPIOrdenante> listTrxSpiOrd;
	@SuppressWarnings("unused")
	private SnpOrdLotes snpLote;
	@SuppressWarnings("unused")
	private String MsgIdLote;
	
	public BceSCIParser() {
		
		log = new Logger();
		listTrxSpiOrd = new ArrayList<>();
	}
	
    public BceSCIParser(SnpOrdLotes snp) {
		
		this.snpLote = snp;
	}
	
    public BceSCIParser(SnpOrdLotes snp, String MsgIdLote) {
		
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
	
    @SuppressWarnings("unused")
	public List<Iso8583> parseDocumentSolicitud(DocumentCobroSCI snp, String IP, String ResAlBCE){
		
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
			
				if(snp.getFIToFICstmrDrctDbt()!= null) {
					
					if(snp.getFIToFICstmrDrctDbt().getGrpHdr() != null) {
						
						validateEndtoEndId = new HashSet<>();
						validateTxId = new HashSet<>();
							
						if(!StringUtils.IsNullOrEmpty(snp.getFIToFICstmrDrctDbt().getGrpHdr().getMsgId())) {
							
							/*MSGID*/
							iso.setISO_037_RetrievalReferenceNumber(snp.getFIToFICstmrDrctDbt()
									.getGrpHdr().getMsgId());
							iso.setISO_036_Track3(IP);
						}else {
							
							this.codError = "CH21"; 
							this.desError = "El elemento es ausente"
									+ ". /FIToFICstmrDrctDbt/GrpHdr/MsgId";
							return null;
						}
						if(snp.getFIToFICstmrDrctDbt().getGrpHdr().getCreDtTm() != null) {
							
							/*CreDtTm*/
							iso.setISO_007_TransDatetime(snp.getFIToFICstmrDrctDbt().getGrpHdr()
									.getCreDtTm() .toGregorianCalendar().getTime());
						}else {
							
							this.codError = "CH21"; 
							this.desError = "El elemento es ausente"
									+ ". /FIToFICstmrDrctDbt/GrpHdr/CreDtTm";
							return null;
						}
						/*Authstn*/
						if(snp.getFIToFICstmrDrctDbt().getGrpHdr().getAuthstn() != null) {
							
							if(snp.getFIToFICstmrDrctDbt().getGrpHdr().getAuthstn().get(0).getCd() != null) {
								
								if(!StringUtils.IsNullOrEmpty(snp.getFIToFICstmrDrctDbt().getGrpHdr().getAuthstn()
										.get(0).getCd().value())) {
									
									iso.setISO_054_AditionalAmounts(snp.getFIToFICstmrDrctDbt().getGrpHdr().getAuthstn()
										.get(0).getCd().value() + "|");
									
								}else {
									
									this.codError = "CH21"; 
									this.desError = "El elemento es ausente"
											+ ". /FIToFICstmrDrctDbt/GrpHdr/Authstn/Cd";
									return null;
								}
								
							}else {
								
								this.codError = "CH21"; 
								this.desError = "El elemento es ausente"
										+ ". /FIToFICstmrDrctDbt/GrpHdr/Authstn/Cd";
								return null;
							}
							
						}else {
							
							this.codError = "CH21"; 
							this.desError = "El elemento es ausente"
									+ ". /FIToFICstmrDrctDbt/GrpHdr/Authstn";
							return null;
						}
						
						/*NbOfTxs*/
						if(!StringUtils.IsNullOrEmpty(snp.getFIToFICstmrDrctDbt().getGrpHdr().getNbOfTxs())) {
							
							if(NumbersUtils.isNumeric(snp.getFIToFICstmrDrctDbt().getGrpHdr().getNbOfTxs())) {
								if(Integer.parseInt(snp.getFIToFICstmrDrctDbt().getGrpHdr().getNbOfTxs()) > 0) {
									
									iso.setISO_006_BillAmount(Double.parseDouble(snp.getFIToFICstmrDrctDbt()
												  .getGrpHdr().getNbOfTxs()));
								}else {
									
									this.codError = "AM18"; 
									this.desError = "El n?mero de transacciones es invalido o esta ausente."
											+ ". /FIToFICstmrDrctDbt/GrpHdr/NbOfTxs";
									return null;
								}
							}else {
								
								this.codError = "CH16"; 
								this.desError = "El n?mero de transacciones es invalido o esta ausente."
										+ ". /FIToFICstmrDrctDbt/GrpHdr/NbOfTxs";
								return null;
							}
						}else {
							
							this.codError = "AM18"; 
							this.desError = "El n?mero de transacciones es invalido o esta ausente."
									+ ". /FIToFICstmrDrctDbt/GrpHdr/NbOfTxs";
							return null;
						}
						/*CtrlSum*/
						if(snp.getFIToFICstmrDrctDbt().getGrpHdr().getCtrlSum() != null) {
							
							if(snp.getFIToFICstmrDrctDbt().getGrpHdr().getCtrlSum()
										.compareTo(BigDecimal.ZERO) > 0) {
								
								iso.setISO_008_BillFeeAmount(snp.getFIToFICstmrDrctDbt().getGrpHdr()
										.getCtrlSum().doubleValue());
							}else {
								
								this.codError = "AM01"; 
								this.desError = "El monto especificado en el mensaje es igual a cero"
										+ ". /FIToFICstmrDrctDbt/GrpHdr/CtrlSum";
								return null;
							}
						}else {
							
							this.codError = "CH21"; 
							this.desError = "El elemento es ausente"
									+ ". /FIToFICstmrDrctDbt/GrpHdr/CtrlSum";
							return null;
						}
						
						if(snp.getFIToFICstmrDrctDbt().getGrpHdr().getSttlmInf() == null){
							
							this.codError = "CH21"; 
							this.desError = "El elemento es ausente"
									+ ". /FIToFICstmrDrctDbt/GrpHdr/SttlmInf";
							return null;
						}
						
						if(snp.getFIToFICstmrDrctDbt().getGrpHdr().getSttlmInf().getSttlmMtd() == null) {
							
							this.codError = "CH21"; 
							this.desError = "El elemento es ausente"
									+ ". /FIToFICstmrDrctDbt/GrpHdr/SttlmInf/SttlmMtd";
							return null;
						}
						
						if(snp.getFIToFICstmrDrctDbt().getGrpHdr().getSttlmInf().getSttlmMtd() != SettlementMethod2Code.CLRG){
							
							this.codError = "CH21"; 
							this.desError = "El elemento es invalido"
									+ ". /FIToFICstmrDrctDbt/GrpHdr/SttlmInf/SttlmMtd";
							return null;
						}
						
						if(snp.getFIToFICstmrDrctDbt().getGrpHdr().getSttlmInf().getClrSys() == null) {
							
							this.codError = "CH21"; 
							this.desError = "El elemento es ausente"
									+ ". /FIToFICstmrDrctDbt/GrpHdr/SttlmInf/ClrSys";
							return null;
						}
						
						if(StringUtils.IsNullOrEmpty(snp.getFIToFICstmrDrctDbt().getGrpHdr()
								.getSttlmInf().getClrSys().getCd())) {
							
							this.codError = "CH21"; 
							this.desError = "El elemento es ausente"
									+ ". /FIToFICstmrDrctDbt/GrpHdr/SttlmInf/ClrSys/Cd";
							return null;
						}else if (!snp.getFIToFICstmrDrctDbt().getGrpHdr()
								.getSttlmInf().getClrSys().getCd().equalsIgnoreCase("BCE")) {
							
							this.codError = "CH21"; 
							this.desError = "El elemento es invalido"
									+ ". /FIToFICstmrDrctDbt/GrpHdr/SttlmInf/ClrSys/Cd";
							return null;
						} 
						
						/*InstgAgt*/
						if(snp.getFIToFICstmrDrctDbt().getGrpHdr().getInstgAgt() != null) {
							
							if(snp.getFIToFICstmrDrctDbt().getGrpHdr().getInstgAgt()
									.getFinInstnId() != null) {
								
								if(!StringUtils.IsNullOrEmpty(snp.getFIToFICstmrDrctDbt().getGrpHdr().getInstgAgt()
										.getFinInstnId().getBICFI())) {
									
									iso.setISO_054_AditionalAmounts(iso.getISO_054_AditionalAmounts() + snp.getFIToFICstmrDrctDbt()
									        .getGrpHdr().getInstgAgt()
											.getFinInstnId().getBICFI());
									
								}else {
									
									this.codError = "CH21"; 
									this.desError = "El elemento es ausente"
											+ ". /FIToFICstmrDrctDbt/GrpHdr/InstgAgt/FinInstnId/BICFI";
									return null;
								}
								
							}else {
								
								this.codError = "CH21"; 
								this.desError = "El elemento es ausente"
										+ ". /FIToFICstmrDrctDbt/GrpHdr/InstgAgt/FinInstnId";
								return null;
							}
							
							if(snp.getFIToFICstmrDrctDbt().getGrpHdr().getInstgAgt()
									.getBrnchId() != null) {
								
								if(!StringUtils.IsNullOrEmpty(snp.getFIToFICstmrDrctDbt().getGrpHdr().getInstgAgt()
										.getBrnchId().getId())) {
									iso.setISO_054_AditionalAmounts(iso.getISO_054_AditionalAmounts() + "|" + 
										snp.getFIToFICstmrDrctDbt().getGrpHdr().getInstgAgt()
										.getBrnchId().getId());
									
								}else {
									
									this.codError = "CH21"; 
									this.desError = "El elemento es ausente"
											+ ". /FIToFICstmrDrctDbt/GrpHdr/InstgAgt/BrnchId/Id";
									return null;
								}
								
							}else {
								
								this.codError = "CH21"; 
								this.desError = "El elemento es ausente"
										+ ". /FIToFICstmrDrctDbt/GrpHdr/InstgAgt/BrnchId";
								return null;
							}
							
						}else {
							
							this.codError = "CH21"; 
							this.desError = "El elemento es ausente"
									+ ". /FIToFICstmrDrctDbt/GrpHdr/InstgAgt";
							return null;
						}
						
						/*InstdAgt*/
						if(snp.getFIToFICstmrDrctDbt().getGrpHdr().getInstdAgt() != null) {
						
							if(snp.getFIToFICstmrDrctDbt().getGrpHdr().getInstdAgt().getFinInstnId() !=  null) {
								
								if(!StringUtils.IsNullOrEmpty(snp.getFIToFICstmrDrctDbt().getGrpHdr()
										.getInstdAgt().getFinInstnId().getBICFI())) {
									
									if(snp.getFIToFICstmrDrctDbt().getGrpHdr()
											.getInstdAgt().getFinInstnId().getBICFI()
											.equalsIgnoreCase(MemoryGlobal.UrlSpiCodeSwitch_BCE)) {
										
										iso.setISO_054_AditionalAmounts(iso.getISO_054_AditionalAmounts() + "|" +
												snp.getFIToFICstmrDrctDbt().getGrpHdr()
												.getInstdAgt().getFinInstnId().getBICFI());
										
									}else {
										
										this.codError = "CH21"; 
										this.desError = "El elemento es invalido"
												+ ". /FIToFICstmrDrctDbt/GrpHdr/InstdAgt/FinInstnId/BICFI";
										return null;
									}
									
								}else {
									
									this.codError = "CH21"; 
									this.desError = "El elemento es ausente"
											+ ". /FIToFICstmrDrctDbt/GrpHdr/InstdAgt/FinInstnId/BICFI";
									return null;
								}
								
							}else {
								
								this.codError = "CH21"; 
								this.desError = "El elemento es ausente"
										+ ". /FIToFICstmrDrctDbt/GrpHdr/InstdAgt/FinInstnId";
								return null;
							}
							
						}else {
						
							this.codError = "CH21"; 
							this.desError = "El elemento es ausente"
									+ ". /FIToFICstmrDrctDbt/GrpHdr/InstdAgt";
							return null;
						}
						
					}else {
						
						this.codError = "CH21"; 
						this.desError = "El elemento es ausente"
								+ ". /FIToFICstmrDrctDbt/GrpHdr";
						return null;
					}
					
					
					boolean flagFechasProgramadas = false;
					/*SECTOR DrctDbtTxInf*/
					if(snp.getFIToFICstmrDrctDbt().getDrctDbtTxInf() != null) {
					
						if(snp.getFIToFICstmrDrctDbt().getDrctDbtTxInf().size() > 0) {
							
							
							for (int i = 0; i < snp.getFIToFICstmrDrctDbt().getDrctDbtTxInf().size(); i++) {
								
								try {
									/*PmtId*/
									if(snp.getFIToFICstmrDrctDbt().getDrctDbtTxInf().get(i).getPmtId() != null) {
										
										/*InstrId*/
										if(!StringUtils.IsNullOrEmpty(snp.getFIToFICstmrDrctDbt()
												.getDrctDbtTxInf().get(i).getPmtId().getInstrId())) {
											iso.setISO_023_CardSeq(snp.getFIToFICstmrDrctDbt()
													.getDrctDbtTxInf().get(i).getPmtId().getInstrId());
										}
										/*EndToEndId*/
										if(!StringUtils.IsNullOrEmpty(snp.getFIToFICstmrDrctDbt()
												.getDrctDbtTxInf().get(i).getPmtId().getEndToEndId())) {
										
											iso.setISO_011_SysAuditNumber(snp.getFIToFICstmrDrctDbt()
												.getDrctDbtTxInf().get(i).getPmtId().getEndToEndId());
											validateEndtoEndId.add(iso.getISO_011_SysAuditNumber());
											
										}else {
											
											this.codError = "FF08";
											this.desError = "El identificador de inicio a fin (campo EndToEndIdentification) esta ausente"
													+ " o es invalido. /DrctDbtTxInf/PmtId/EndToEndId {Iteracion "+ (i + 1) +"}";
											return null;
										}
										/*TxId*/
										if(!StringUtils.IsNullOrEmpty(snp.getFIToFICstmrDrctDbt() 
												.getDrctDbtTxInf().get(i).getPmtId().getTxId())) {
										
											if(NumbersUtils.isNumeric(snp.getFIToFICstmrDrctDbt() 
												.getDrctDbtTxInf().get(i).getPmtId().getTxId())) {
												
												iso.setISO_090_OriginalData(snp.getFIToFICstmrDrctDbt()
														.getDrctDbtTxInf().get(i).getPmtId().getTxId());
													validateTxId.add(iso.getISO_090_OriginalData());
											}else {
												
												this.codError = "CH16";
												this.desError = "El elemento es invalido"
														+ ". /DrctDbtTxInf/PmtId/TxId {Iteracion "+ (i+1) + "}";
												return null;
											}
												
										}else {
											
											this.codError = "CH21";
											this.desError = "El elemento es ausente"
													+ ". /DrctDbtTxInf/PmtId/TxId {Iteracion "+ (i+1) + "}";
											return null;
										}
										/*ClrSysRef*/
										/*if(!StringUtils.IsNullOrEmpty(snp.getFIToFICstmrDrctDbt()
												.getDrctDbtTxInf().get(i).getPmtId().getClrSysRef())) {
											iso.setISO_055_EMV(snp.getFIToFICstmrDrctDbt()
												.getDrctDbtTxInf().get(i).getPmtId().getClrSysRef());
										}else {
											
											this.codError = "CH21";
											this.desError = "El elemento es ausente"
													+ ". /DrctDbtTxInf/PmtId/ClrSysRef {Iteracion "+ (i+1) + "}";
											return null;
										}*/	
										
									}else {
										
										this.codError = "CH21"; 
										this.desError = "El elemento es ausente"
												+ ". /DrctDbtTxInf/PmtId {Iteracion "+ (i+1) + "}";
										return null;
									}
									
									/*ESTABLECE SI LA TRANSACCION ES MAYORISTA O MINORISTA*/
									if(snp.getFIToFICstmrDrctDbt().getDrctDbtTxInf().get(i).getPmtTpInf() != null) {
									
										if(snp.getFIToFICstmrDrctDbt().getDrctDbtTxInf().get(i)
												             .getPmtTpInf().getInstrPrty() != null) {
											
											if(snp.getFIToFICstmrDrctDbt().getDrctDbtTxInf().get(i)
										             .getPmtTpInf().getInstrPrty().name().equals("HIGH")) {
												
												iso.setISO_BitMap("HIGH");
												
											}else {
												iso.setISO_BitMap("LOW");
											}
											
										}else {
											
											this.codError = "CH21"; 
											this.desError = "El elemento es ausente"
													+ ". /DrctDbtTxInf/PmtTpInf/InstrPrty {Iteracion "+ (i+1) + "}";
											return null;
										}
										
									}else {
										
										iso.setISO_BitMap("LOW");
									}
									
									/*IntrBkSttlmAmt*/
									if(snp.getFIToFICstmrDrctDbt()
											.getDrctDbtTxInf().get(i).getIntrBkSttlmAmt() != null) {
										
										/*Valor $*/
										if(snp.getFIToFICstmrDrctDbt()
												.getDrctDbtTxInf().get(i).getIntrBkSttlmAmt().getValue() != null) {
											
											if(snp.getFIToFICstmrDrctDbt()
													.getDrctDbtTxInf().get(i).getIntrBkSttlmAmt()
													.getValue().compareTo(BigDecimal.ZERO) > 0) {
												
												iso.setISO_004_AmountTransaction(snp.getFIToFICstmrDrctDbt()
														.getDrctDbtTxInf().get(i).getIntrBkSttlmAmt().getValue()
														.doubleValue());
												
												validateMonto+=iso.getISO_004_AmountTransaction();
												
											}else {
												
												this.codError = "AM01"; 
												this.desError = "El monto especificado es igual a 0"
														+ ". /DrctDbtTxInf/IntrBkSttlmAmt {Iteracion "+ (i+1) + "}";
												return null;
											}
											
										}else {
											
											this.codError = "CH21"; 
											this.desError = "El elemento es ausente"
													+ ". /DrctDbtTxInf/IntrBkSttlmAmt {Iteracion "+ (i+1) + "}";
											return null;
										}
										/*Moneda $*/
										if(!StringUtils.IsNullOrEmpty(snp.getFIToFICstmrDrctDbt()
												.getDrctDbtTxInf().get(i).getIntrBkSttlmAmt().getCcy())) {
											
											iso.setISO_049_TranCurrCode(snp.getFIToFICstmrDrctDbt()
												.getDrctDbtTxInf().get(i).getIntrBkSttlmAmt().getCcy()=="USD"?840:0);
										}else {
											
											this.codError = "AM11"; 
											this.desError = "El tipo de moneda es invalido o esta ausente"
													+ ". /DrctDbtTxInf/IntrBkSttlmAmt/Ccy {Iteracion "+ (i+1) + "}";
											return null;
										}
									}else {
										
										this.codError = "CH21"; 
										this.desError = "El elemento es ausente"
												+ ". /DrctDbtTxInf/IntrBkSttlmAmt {Iteracion "+ (i+1) + "}";
										return null;
									}
									/*IntrBkSttlmDt Fecha Contable o de Programacion*/
									if(snp.getFIToFICstmrDrctDbt()
											.getDrctDbtTxInf().get(i).getIntrBkSttlmDt() != null) {
										
										iso.setISO_015_SettlementDatel((snp.getFIToFICstmrDrctDbt()
											.getDrctDbtTxInf().get(i).getIntrBkSttlmDt()
											.toGregorianCalendar().getTime()));
									}
									
									
									/*ChrgBr*/
									if(snp.getFIToFICstmrDrctDbt()
											.getDrctDbtTxInf().get(i).getChrgBr() != null){
										
										if(snp.getFIToFICstmrDrctDbt()
												.getDrctDbtTxInf().get(i).getChrgBr() == ChargeBearerType1Code.SLEV) {
											
											iso.setISO_054_AditionalAmounts(iso.getISO_054_AditionalAmounts() + "|SLEV");
											
										}else {
											
											this.codError = "CH21"; 
											this.desError = "El elemento es invalido"
													+ ". /DrctDbtTxInf/ChrgBr {Iteracion "+ (i+1) + "}";
											return null;
										}
										
									}else {
										
										this.codError = "CH21"; 
										this.desError = "El elemento es ausente"
												+ ". /DrctDbtTxInf/ChrgBr {Iteracion "+ (i+1) + "}";
										return null;
									}
									
									/*ReqdColltnDt*/
									if(snp.getFIToFICstmrDrctDbt()
											.getDrctDbtTxInf().get(i).getReqdColltnDt() != null) {
										
										String fechaReq = FormatUtils.DateToString(snp.getFIToFICstmrDrctDbt()
												.getDrctDbtTxInf().get(i).getReqdColltnDt().toGregorianCalendar().getTime(),"yyyy-MM-dd");
										iso.setISO_055_EMV(fechaReq);
										flagFechasProgramadas = true;
									}
									
									/*DrctDbtTx*/	
									if(snp.getFIToFICstmrDrctDbt()
											.getDrctDbtTxInf().get(i).getDrctDbtTx() != null) {
										
										if(snp.getFIToFICstmrDrctDbt()
												.getDrctDbtTxInf().get(i).getDrctDbtTx().getMndtRltdInf() != null) {
											
											/*FrstColltnDt*/
											if(snp.getFIToFICstmrDrctDbt()
													.getDrctDbtTxInf().get(i).getDrctDbtTx().getMndtRltdInf()
													.getFrstColltnDt() != null) {
												
												iso.setISO_013_LocalDate(snp.getFIToFICstmrDrctDbt()
													.getDrctDbtTxInf().get(i).getDrctDbtTx().getMndtRltdInf()
													.getFrstColltnDt().toGregorianCalendar().getTime());
												
												if(flagFechasProgramadas) {
													
													Date fechReqDate = FormatUtils.StringToDate(iso.getISO_055_EMV(),"yyyy-MM-dd");
													//if(fechReqDate.compareTo(iso.getISO_013_LocalDate()) != 0) {
													if(!FormatUtils.DateToString(iso.getISO_013_LocalDate(),"yyyy-MM-dd")
															.equals(iso.getISO_055_EMV())) {
														
														this.codError = "TA01"; 
														this.desError = "El campo fecha "
																+ ". /DrctDbtTxInf/DrctDbtTx/ReqdColltnDt es diferente"
																+ " a  /DrctDbtTxInf/DrctDbtTx/MndtRltdInf/FrstColltnDt {Iteracion "+ (i+1) + "}";
														return null;
													}
													
												}else {
													
													if(!(FormatUtils.DateToString(iso.getISO_013_LocalDate(),"yyyy-MM-dd")
															.equals((FormatUtils.DateToString(iso.getISO_007_TransDatetime(),"yyyy-MM-dd"))))) {
														
														this.codError = "TA01"; 
														this.desError = "El campo fecha "
																+ ". /DrctDbtTxInf/DrctDbtTx/MndtRltdInf/FrstColltnDt es diferente"
																+ " a  /GrpHdr/IntrBkSttlmDt {Iteracion "+ (i+1) + "}";
														return null;
													}
												}
												
											}else {
												
												this.codError = "CH21"; 
												this.desError = "El elemento es ausente"
														+ ". /DrctDbtTxInf/DrctDbtTx/MndtRltdInf/FrstColltnDt {Iteracion "+ (i+1) + "}";
												return null;
											}
											
											/*FnlColltnDt*/
											if(snp.getFIToFICstmrDrctDbt()
													.getDrctDbtTxInf().get(i).getDrctDbtTx().getMndtRltdInf()
													.getFnlColltnDt() != null) {
												
												if(snp.getFIToFICstmrDrctDbt()
														.getDrctDbtTxInf().get(i).getDrctDbtTx().getMndtRltdInf()
														.getFnlColltnDt().toGregorianCalendar().getTime().compareTo(iso.getISO_013_LocalDate()) > 0) {
													
													this.codError = "TA01"; 
													this.desError = "El campo fecha "
															+ ". /DrctDbtTxInf/DrctDbtTx/MndtRltdInf/FnlColltnDt es mayor que"
															+ " /DrctDbtTxInf/DrctDbtTx/MndtRltdInf/FrstColltnDt {Iteracion "+ (i+1) + "}";
													return null;
													
												}else {
													
													iso.setISO_054_AditionalAmounts(iso.getISO_054_AditionalAmounts() + "|" + snp.getFIToFICstmrDrctDbt()
													.getDrctDbtTxInf().get(i).getDrctDbtTx().getMndtRltdInf()
													.getFnlColltnDt() );
												}
												
											}else {
												
												this.codError = "CH21"; 
												this.desError = "El elemento es ausente"
														+ ". /DrctDbtTxInf/DrctDbtTx/MndtRltdInf/FnlColltnDt {Iteracion "+ (i+1) + "}";
												return null;
											}
											
										}else {
											
											this.codError = "CH21"; 
											this.desError = "El elemento es ausente"
													+ ". /DrctDbtTxInf/DrctDbtTx/MndtRltdInf {Iteracion "+ (i+1) + "}";
											return null;
										}
										
									}else {
										
										this.codError = "CH21"; 
										this.desError = "El elemento es ausente"
												+ ". /DrctDbtTxInf/DrctDbtTx {Iteracion "+ (i+1) + "}";
										return null;
									}
									
									/*Cdtr Nombres de la Persona ORDENANTE del Cobro*/
									if(snp.getFIToFICstmrDrctDbt().getDrctDbtTxInf().get(i).getCdtr() != null) {
										
										if(!StringUtils.IsNullOrEmpty(snp.getFIToFICstmrDrctDbt().getDrctDbtTxInf().get(i).getCdtr().getNm())) {
											
											iso.setISO_034_PANExt(snp.getFIToFICstmrDrctDbt().getDrctDbtTxInf()
													.get(i).getCdtr().getNm());
										}else {
											
											this.codError = "BE22"; 
											this.desError = "El nombre del acreedor es invalido o ausente"
													+ ". /DrctDbtTxInf/Cdtr/Nm {Iteracion "+ (i+1) + "}";
											return null;
										}
										if(snp.getFIToFICstmrDrctDbt().getDrctDbtTxInf().get(i).getCdtr().getId() != null) {
											
											
											if(snp.getFIToFICstmrDrctDbt().getDrctDbtTxInf().get(i).getCdtr().getId().getPrvtId() != null) {
												
												//Personas naturales
												if(snp.getFIToFICstmrDrctDbt().getDrctDbtTxInf().get(i)
														.getCdtr().getId().getPrvtId().getOthr() != null) {
													
													if(!StringUtils.IsNullOrEmpty(snp.getFIToFICstmrDrctDbt().getDrctDbtTxInf().get(i)
														.getCdtr().getId().getPrvtId().getOthr().get(0).getId())) {
														
														iso.setISO_002_PAN(snp.getFIToFICstmrDrctDbt().getDrctDbtTxInf().get(i)
														.getCdtr().getId().getPrvtId().getOthr().get(0).getId());
													}else {
														
														this.codError = "CH11"; 
														this.desError = "El valor en el identificador del acreedor es incorrecto."
																+ ". /DrctDbtTxInf/Cdtr/Id/PrvtId/Othr/Id {Iteracion "+ (i+1) + "}";
														return null;
													}
													if(snp.getFIToFICstmrDrctDbt().getDrctDbtTxInf().get(i)
															.getCdtr().getId().getPrvtId().getOthr().get(0).getSchmeNm() != null) {
														
														String tipoDoc = StringUtils.Empty();
														if(!StringUtils.IsNullOrEmpty(snp.getFIToFICstmrDrctDbt().getDrctDbtTxInf().get(i)
															.getCdtr().getId().getPrvtId().getOthr().get(0).getSchmeNm().getCd())) {
															
															tipoDoc = snp.getFIToFICstmrDrctDbt().getDrctDbtTxInf().get(i)
																	.getCdtr().getId().getPrvtId().getOthr().get(0)
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
																this.desError = "Tipo de identificaci?n no es valido o esta ausente."
																		+ ". /DrctDbtTxInf/Cdtr/Id/PrvtId/Othr/SchmeNm {Iteracion "+ (i+1) + "}";
																return null;
															}
														}else {
															
															this.codError = "BE15"; 
															this.desError = "Tipo de identificaci?n no es valido o esta ausente."
																	+ ". /DrctDbtTxInf/Cdtr/Id/PrvtId/Othr/SchmeNm {Iteracion "+ (i+1) + "}";
															return null;
														}
													}else {
														
														this.codError = "CH21"; 
														this.desError = "El elemento es ausente"
																+ ". /DrctDbtTxInf/Cdtr/Id/PrvtId/Othr/SchmeNm {Iteracion "+ (i+1) + "}";
														return null;
													}
												}else {
													
													this.codError = "CH21"; 
													this.desError = "El elemento es ausente"
															+ ". /DrctDbtTxInf/Cdtr/Id/PrvtId/Othr {Iteracion "+ (i+1) + "}";
													return null;
												}
											}else {
												//Personas Juridicas
												
												if(snp.getFIToFICstmrDrctDbt()
												.getDrctDbtTxInf().get(i).getCdtr().getId().getOrgId() != null) {
													
													if(snp.getFIToFICstmrDrctDbt()
															.getDrctDbtTxInf().get(i).getCdtr().getId()
															.getOrgId().getOthr() != null) {
														
														if(!StringUtils.IsNullOrEmpty(snp.getFIToFICstmrDrctDbt()
															.getDrctDbtTxInf().get(i).getCdtr().getId()
															.getOrgId().getOthr().get(0).getId())) {
															
															iso.setISO_002_PAN(snp.getFIToFICstmrDrctDbt()
															.getDrctDbtTxInf().get(i).getCdtr().getId()
															.getOrgId().getOthr().get(0).getId());
														}else {
															
															this.codError = "CH11"; 
															this.desError = "El valor en el identificador del acreedor es incorrecto."
																	+ ". /DrctDbtTxInf/Cdtr/Id/OrgId/Othr/Id {Iteracion "+ (i+1) + "}";
															return null;
														}
														String tipDocJur = StringUtils.Empty();
														if(snp.getFIToFICstmrDrctDbt()
																.getDrctDbtTxInf().get(i).getCdtr().getId()
																.getOrgId().getOthr().get(0).getSchmeNm() != null) {
															
															if(!StringUtils.IsNullOrEmpty(snp.getFIToFICstmrDrctDbt()
																.getDrctDbtTxInf().get(i).getCdtr().getId()
																.getOrgId().getOthr().get(0).getSchmeNm().getCd())) {
															
																tipDocJur = snp.getFIToFICstmrDrctDbt()
																		.getDrctDbtTxInf().get(i).getCdtr().getId()
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
																	this.desError = "Tipo de identificaci?n no es valido o esta ausente."
																			+ ". /DrctDbtTxInf/Cdtr/Id/OrgId/Othr/SchmeNm/Cd {Iteracion "+ (i+1) + "}";
																	return null;
																}
															}else {
																
																this.codError = "BE15"; 
																this.desError = "Tipo de identificaci?n no es valido o esta ausente."
																		+ ". /DrctDbtTxInf/Cdtr/Id/OrgId/Othr/SchmeNm/Cd {Iteracion "+ (i+1) + "}";
																return null;
															}
														}else {
															
															this.codError = "CH21"; 
															this.desError = "El elemento es ausente"
																	+ ". /DrctDbtTxInf/Cdtr/Id/OrgId/Othr/SchmeNm {Iteracion "+ (i+1) + "}";
															return null;
														}
													}else {
														
														this.codError = "CH21"; 
														this.desError = "El elemento es ausente"
																+ ". /DrctDbtTxInf/Cdtr/Id/OrgId/Othr {Iteracion "+ (i+1) + "}";
														return null;
													}
												}else {
													
													this.codError = "CH21"; 
													this.desError = "El elemento es ausente"
															+ ". /DrctDbtTxInf/Cdtr/Id/OrgId {Iteracion "+ (i+1) + "}";
													return null;
												}
											}
										}else {
											
											this.codError = "CH21"; 
											this.desError = "El elemento es ausente"
													+ ". /DrctDbtTxInf/Cdtr/Id {Iteracion "+ (i+1) + "}";
											return null;
										}
										
									}else {
										
										this.codError = "CH21"; 
										this.desError = "El elemento es ausente"
												+ ". /DrctDbtTxInf/Cdtr {Iteracion "+ (i+1) + "}";
										return null;
									}
									
									/*CdtrAcct Datos de la Cuenta de la Persona Ordenante del Cobro*/
									if(snp.getFIToFICstmrDrctDbt().getDrctDbtTxInf().get(i).getCdtrAcct() != null) {
										
										if(snp.getFIToFICstmrDrctDbt().getDrctDbtTxInf().get(i).getCdtrAcct().getId() != null) {
											
											if(snp.getFIToFICstmrDrctDbt().getDrctDbtTxInf().get(i).getCdtrAcct().getId().getOthr() != null) {
												
												if(!StringUtils.IsNullOrEmpty(snp.getFIToFICstmrDrctDbt().getDrctDbtTxInf()
														.get(i).getCdtrAcct().getId().getOthr().getId())) {
													
													if(NumbersUtils.isNumeric(snp.getFIToFICstmrDrctDbt().getDrctDbtTxInf()
															.get(i).getCdtrAcct().getId().getOthr().getId())) {
														
															iso.setISO_102_AccountID_1(snp.getFIToFICstmrDrctDbt().getDrctDbtTxInf()
															.get(i).getCdtrAcct().getId().getOthr().getId());
													}else {
														
														this.codError = "AC03"; 
														this.desError = "El n?mero de cuenta del acreedor es invalido o esta ausente."
																+ ". /DrctDbtTxInf/CdtrAcct/Id/Othr/Id {Iteracion "+ (i+1) + "}";
														return null;
													}
													
												}else {
													
													this.codError = "AC03"; 
													this.desError = "El n?mero de cuenta del acreedor es invalido o esta ausente."
															+ ". /DrctDbtTxInf/CdtrAcct/Id/Othr/Id {Iteracion "+ (i+1) + "}";
													return null;
												}
											}else {
												
												this.codError = "CH21"; 
												this.desError = "El elemento es ausente"
														+ ". /DrctDbtTxInf/CdtrAcct/Id/Othr {Iteracion "+ (i+1) + "}";
												return null;
											}
										}else {
											
											this.codError = "CH21"; 
											this.desError = "El elemento es ausente"
													+ ". /DrctDbtTxInf/CdtrAcct/Id {Iteracion "+ (i+1) + "}";
											return null;
										}
										
										if(snp.getFIToFICstmrDrctDbt().getDrctDbtTxInf().get(i).getCdtrAcct().getTp() != null) {
											
											if(!StringUtils.IsNullOrEmpty(snp.getFIToFICstmrDrctDbt().getDrctDbtTxInf()
													.get(i).getCdtrAcct().getTp().getPrtry())) {
												
												prefixAccDeb = snp.getFIToFICstmrDrctDbt().getDrctDbtTxInf()
														.get(i).getCdtrAcct().getTp().getPrtry();
												prefixAccDeb = prefixAccDeb.equals("02")?"10":"20";
											}else {
												
												this.codError = "AC14"; 
												this.desError = "Tipo de cuenta del acreedor invalida o ausente."
														+ ". /DrctDbtTxInf/CdtrAcct/Tp/Prtry {Iteracion "+ (i+1) + "}";
												return null;
											}
											
										}else {
											
											this.codError = "CH21"; 
											this.desError = "El elemento es ausente"
													+ ". /DrctDbtTxInf/CdtrAcct/Tp {Iteracion "+ (i+1) + "}";
											return null;
										}
										//Implementar moneda
										if(!StringUtils.IsNullOrEmpty(snp.getFIToFICstmrDrctDbt().getDrctDbtTxInf()
												.get(i).getCdtrAcct().getCcy())) {
											iso.setISO_051_CardCurrCode(snp.getFIToFICstmrDrctDbt().getDrctDbtTxInf()
												.get(i).getCdtrAcct().getCcy()=="USD"?840:0);
											
										}else {
											
											this.codError = "AC09"; 
											this.desError = "La moneda de la cuenta es invalida o ausente"
													+ ". /DrctDbtTxInf/CdtrAcct/Tp {Iteracion "+ (i+1) + "}";
											return null;
										}
									}else {
										
										this.codError = "CH21"; 
										this.desError = "El elemento es ausente"
												+ ". /DrctDbtTxInf/CdtrAcct {Iteracion "+ (i+1) + "}";
										return null;
									}
									
									/*CdtrAgt Datos Institucion ORDENANTE*/
									if(snp.getFIToFICstmrDrctDbt().getDrctDbtTxInf().get(i).getCdtrAgt() != null) {
										
										if(snp.getFIToFICstmrDrctDbt().getDrctDbtTxInf().get(i).getCdtrAgt().getFinInstnId() != null) {
											
											if(!StringUtils.IsNullOrEmpty(snp.getFIToFICstmrDrctDbt().getDrctDbtTxInf()
													.get(i).getCdtrAgt().getFinInstnId().getBICFI())) {
												iso.setISO_032_ACQInsID(snp.getFIToFICstmrDrctDbt().getDrctDbtTxInf()
														.get(i).getCdtrAgt().getFinInstnId().getBICFI());
												
											}else {
												
												this.codError = "RC05"; 
												this.desError = "El identificador BIC es inv?lido o esta ausente."
														+ ". /DrctDbtTxInf/CdtrAgt/FinInstnId {Iteracion "+ (i+1) + "}";
												return null;
											}
										}else {
											
											this.codError = "CH21"; 
											this.desError = "El elemento es ausente"
													+ ". /DrctDbtTxInf/CdtrAgt/FinInstnId {Iteracion "+ (i+1) + "}";
											return null;
										}
										
										//Branch jcol
										if(snp.getFIToFICstmrDrctDbt().getDrctDbtTxInf().get(i).getCdtrAgt().getBrnchId() != null) {
											
											if(!StringUtils.IsNullOrEmpty(snp.getFIToFICstmrDrctDbt().getDrctDbtTxInf()
													.get(i).getCdtrAgt().getBrnchId().getId())) {
												
												iso.setISO_041_CardAcceptorID(snp.getFIToFICstmrDrctDbt().getDrctDbtTxInf()
													.get(i).getCdtrAgt().getBrnchId().getId());
											}else {
												
												this.codError = "CH21"; 
												this.desError = "El elemento es ausente"
														+ ". /DrctDbtTxInf/CdtrAgt/BrnchId/Id {Iteracion "+ (i+1) + "}";
												return null;
											}
										}else {
											
											this.codError = "CH21"; 
											this.desError = "El elemento es ausente"
													+ ". /DrctDbtTxInf/CdtrAgt/BrnchId {Iteracion "+ (i+1) + "}";
											return null;
										}
										
									}else {
										
										this.codError = "CH21"; 
										this.desError = "El elemento es ausente"
												+ ". /DrctDbtTxInf/CdtrAgt {Iteracion "+ (i+1) + "}";
										return null;
									}
									
									
									/*CdtrAgtAcct Cuenta corriente de la RECEPTORA en el BCE*/
									if(snp.getFIToFICstmrDrctDbt().getDrctDbtTxInf().get(i).getCdtrAgtAcct() != null) {
										
										if(snp.getFIToFICstmrDrctDbt().getDrctDbtTxInf().get(i)
												                 .getCdtrAgtAcct().getId() != null) {
											
											if(snp.getFIToFICstmrDrctDbt().getDrctDbtTxInf().get(i)
									                 .getCdtrAgtAcct().getId().getOthr() != null) {
												
												if(!StringUtils.IsNullOrEmpty(snp.getFIToFICstmrDrctDbt().getDrctDbtTxInf().get(i)
									                 .getCdtrAgtAcct().getId().getOthr().getId())) {
													
													iso.setISO_122_ExtendedData(snp.getFIToFICstmrDrctDbt().getDrctDbtTxInf().get(i)
									                 .getCdtrAgtAcct().getId().getOthr().getId());
													
												}else {
													
													this.codError = "CH21"; 
													this.desError = "El elemento es ausente"
															+ ". /DrctDbtTxInf/CdtrAgtAcct/Id/Othr/Id {Iteracion "+ (i+1) + "}";
													return null;
												}
												
											}else {
												
												this.codError = "CH21"; 
												this.desError = "El elemento es ausente"
														+ ". /DrctDbtTxInf/CdtrAgtAcct/Id/Othr {Iteracion "+ (i+1) + "}";
												return null;
											}
											
										}else {
											
											this.codError = "CH21"; 
											this.desError = "El elemento es ausente"
													+ ". /DrctDbtTxInf/CdtrAgtAcct/Id {Iteracion "+ (i+1) + "}";
											return null;
										}
										
									}else {
										
										this.codError = "CH21"; 
										this.desError = "El elemento es ausente"
												+ ". /DrctDbtTxInf/CdtrAgtAcct {Iteracion "+ (i+1) + "}";
										return null;
									}
									
									/*DBTR Nombre, tipo y numero de documento del Debitante u RECEPTOR*/
									if(snp.getFIToFICstmrDrctDbt().getDrctDbtTxInf().get(i).getDbtr() != null) {
										
										/*Nombre*/
										if(!StringUtils.IsNullOrEmpty(snp.getFIToFICstmrDrctDbt()
												.getDrctDbtTxInf().get(i).getDbtr().getNm())) {
											
											iso.setISO_114_ExtendedData(snp.getFIToFICstmrDrctDbt()
												.getDrctDbtTxInf().get(i).getDbtr().getNm());
										}else {
											
											this.codError = "BE08"; 
											this.desError = "El nombre del deudor no esta presente."
													+ ". /DrctDbtTxInf/Dbtr/Nm {Iteracion "+ (i+1) + "}";
											return null;
										}
										/*Id*/
										if(snp.getFIToFICstmrDrctDbt()
												.getDrctDbtTxInf().get(i).getDbtr().getId() != null) {
											
											if(snp.getFIToFICstmrDrctDbt()
													.getDrctDbtTxInf().get(i).getDbtr().getId().getPrvtId() != null) {
												//Personas naturales
												if(snp.getFIToFICstmrDrctDbt().getDrctDbtTxInf().get(i)
														.getDbtr().getId().getPrvtId().getOthr() != null) {
													
													if(!StringUtils.IsNullOrEmpty(snp.getFIToFICstmrDrctDbt().getDrctDbtTxInf().get(i)
														.getDbtr().getId().getPrvtId().getOthr().get(0).getId())) {
														
														iso.setISO_123_ExtendedData(snp.getFIToFICstmrDrctDbt().getDrctDbtTxInf().get(i)
														.getDbtr().getId().getPrvtId().getOthr().get(0).getId());
													}else {
														
														this.codError = "CH21"; 
														this.desError = "El elemento es ausente"
																+ ". /DrctDbtTxInf/Dbtr/Id/PrvtId/Othr/Id {Iteracion "+ (i + 1) + "}";
														return null;
													}
													if(snp.getFIToFICstmrDrctDbt().getDrctDbtTxInf().get(i)
															.getDbtr().getId().getPrvtId().getOthr().get(0).getSchmeNm() != null) {
														
														String tipoDoc = StringUtils.Empty();
														if(!StringUtils.IsNullOrEmpty(snp.getFIToFICstmrDrctDbt().getDrctDbtTxInf().get(i)
															.getDbtr().getId().getPrvtId().getOthr().get(0).getSchmeNm().getCd())) {
															
															tipoDoc = snp.getFIToFICstmrDrctDbt().getDrctDbtTxInf().get(i)
																	.getDbtr().getId().getPrvtId().getOthr().get(0)
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
																this.desError = "Tipo de identificaci?n no es valido o esta ausente."
																		+ ". /DrctDbtTxInf/Dbtr/Id/PrvtId/Othr/SchmeNm/Cd {Iteracion "+ (i + 1) + "}";
																return null;
															}
														}else {
															
															this.codError = "BE15"; 
															this.desError = "Tipo de identificaci?n no es valido o esta ausente."
																	+ ". /DrctDbtTxInf/Dbtr/Id/PrvtId/Othr/SchmeNm/Cd {Iteracion "+ (i + 1) + "}";
															return null;
														}
													}else {
														
														this.codError = "CH21"; 
														this.desError = "El elemento es ausente"
																+ ". /DrctDbtTxInf/Dbtr/Id/PrvtId/Othr/SchmeNm {Iteracion "+ (i + 1) + "}";
														return null;
													}
												}else {
													
													this.codError = "CH21"; 
													this.desError = "El elemento es ausente"
															+ ". /DrctDbtTxInf/Dbtr/Id/PrvtId/Othr {Iteracion "+ (i + 1) + "}";
													return null;
												}
											}else {
												//Personas Juridicas
												if(snp.getFIToFICstmrDrctDbt()
												.getDrctDbtTxInf().get(i).getDbtr().getId().getOrgId() != null) {
													
													if(snp.getFIToFICstmrDrctDbt()
															.getDrctDbtTxInf().get(i).getDbtr().getId()
															.getOrgId().getOthr() != null) {
														
														if(!StringUtils.IsNullOrEmpty(snp.getFIToFICstmrDrctDbt()
															.getDrctDbtTxInf().get(i).getDbtr().getId()
															.getOrgId().getOthr().get(0).getId())) {
															
															iso.setISO_123_ExtendedData(snp.getFIToFICstmrDrctDbt()
															.getDrctDbtTxInf().get(i).getDbtr().getId()
															.getOrgId().getOthr().get(0).getId());
															
														}else {
															
															this.codError = "CH21"; 
															this.desError = "El elemento es ausente"
																	+ ". /DrctDbtTxInf/Dbtr/Id/OrgId/Othr/Id {Iteracion "+ (i + 1) + "}";
															return null;
														}
														String tipDocJur = StringUtils.Empty();
														if(snp.getFIToFICstmrDrctDbt()
																.getDrctDbtTxInf().get(i).getDbtr().getId()
																.getOrgId().getOthr().get(0).getSchmeNm() != null) {
															
															if(!StringUtils.IsNullOrEmpty(snp.getFIToFICstmrDrctDbt()
																.getDrctDbtTxInf().get(i).getDbtr().getId()
																.getOrgId().getOthr().get(0).getSchmeNm().getCd())) {
															
																tipDocJur = snp.getFIToFICstmrDrctDbt()
																		.getDrctDbtTxInf().get(i).getDbtr().getId()
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
																	this.desError = "Tipo de identificaci?n no es valido o esta ausente."
																			+ ". /DrctDbtTxInf/Dbtr/Id/OrgId/Othr/SchmeNm/Cd {Iteracion "+ (i + 1) + "}";
																	return null;
																}
															}else {
																
																this.codError = "BE15"; 
																this.desError = "Tipo de identificaci?n no es valido o esta ausente."
																		+ ". /DrctDbtTxInf/Dbtr/Id/OrgId/Othr/SchmeNm/Cd {Iteracion "+ (i + 1) + "}";
																return null;
															}
														}else {
															
															this.codError = "CH21"; 
															this.desError = "El elemento es ausente"
																	+ ". /DrctDbtTxInf/Dbtr/Id/OrgId/Othr/SchmeNm {Iteracion "+ (i + 1) + "}";
															return null;
														}
													}else {
														
														this.codError = "CH21"; 
														this.desError = "El elemento es ausente"
																+ ". /DrctDbtTxInf/Dbtr/Id/OrgId/Othr {Iteracion "+ (i + 1) + "}";
														return null;
													}
												}else {
													
													this.codError = "CH21"; 
													this.desError = "El elemento es ausente"
															+ ". /DrctDbtTxInf/Dbtr/Id/OrgId {Iteracion "+ (i + 1) + "}";
													return null;
												}
											}
											/*Telefono y Mail del RECEPTOR*/
											if(snp.getFIToFICstmrDrctDbt()
													.getDrctDbtTxInf().get(i).getDbtr().getCtctDtls() != null) {
												
												if(!StringUtils.IsNullOrEmpty(snp.getFIToFICstmrDrctDbt()
													.getDrctDbtTxInf().get(i).getDbtr().getCtctDtls().getMobNb())) {
													
													iso.setISO_054_AditionalAmounts(iso.getISO_054_AditionalAmounts() + "|" +
															snp.getFIToFICstmrDrctDbt()
													.getDrctDbtTxInf().get(i).getDbtr().getCtctDtls().getMobNb());
													
												}else {
													
													this.codError = "CH21"; 
													this.desError = "El elemento es ausente"
															+ ". /DrctDbtTxInf/Dbtr/CtctDtls/MobNb {Iteracion "+ (i + 1) + "}";
													return null;
												}
												

												if(!StringUtils.IsNullOrEmpty(snp.getFIToFICstmrDrctDbt()
													.getDrctDbtTxInf().get(i).getDbtr().getCtctDtls().getEmailAdr())) {
													
													iso.setISO_054_AditionalAmounts(iso.getISO_054_AditionalAmounts() + "|" +
															snp.getFIToFICstmrDrctDbt()
													.getDrctDbtTxInf().get(i).getDbtr().getCtctDtls().getEmailAdr());
													
												}else {
													
													this.codError = "CH21"; 
													this.desError = "El elemento es ausente"
															+ ". /DrctDbtTxInf/Dbtr/CtctDtls/EmailAdr {Iteracion "+ (i + 1) + "}";
													return null;
												}
												
											}else {
												
												this.codError = "CH21"; 
												this.desError = "El elemento es ausente"
														+ ". /DrctDbtTxInf/Dbtr/CtctDtls {Iteracion "+ (i + 1) + "}";
												return null;
											}
											
										}else {
											
											this.codError = "CH21"; 
											this.desError = "El elemento es ausente"
													+ ". /DrctDbtTxInf/Dbtr/Id {Iteracion "+ (i + 1) + "}";
											return null;
										}
									}else {
										
										this.codError = "CH21"; 
										this.desError = "El elemento es ausente"
												+ ". /DrctDbtTxInf/Dbtr {Iteracion "+ (i+1) + "}";
										return null;
									}
									
									/*DbtrAcct Numero de cuenta de la persona Debitante*/
									if(snp.getFIToFICstmrDrctDbt().getDrctDbtTxInf().get(i).getDbtrAcct() != null) {
										
										if(snp.getFIToFICstmrDrctDbt().getDrctDbtTxInf().get(i).getDbtrAcct()
												.getId() != null) {
											
											if(snp.getFIToFICstmrDrctDbt().getDrctDbtTxInf().get(i).getDbtrAcct()
													.getId().getOthr() != null) {
												if(!StringUtils.IsNullOrEmpty(snp.getFIToFICstmrDrctDbt().getDrctDbtTxInf().get(i).getDbtrAcct()
														.getId().getOthr().getId())) {
													
													iso.setISO_103_AccountID_2(snp.getFIToFICstmrDrctDbt().getDrctDbtTxInf().get(i).getDbtrAcct()
														.getId().getOthr().getId());
												}else {
													
													this.codError = "AC02"; 
													this.desError = "El n?mero de cuenta del deudor es invalido o esta ausente."
															+ ". /DrctDbtTxInf/DbtrAcct/Id/Othr {Iteracion "+ (i+1) + "}";
													return null;
												}
												if(snp.getFIToFICstmrDrctDbt().getDrctDbtTxInf().get(i).getDbtrAcct().getTp() != null) {
													
													if(!StringUtils.IsNullOrEmpty(snp.getFIToFICstmrDrctDbt().getDrctDbtTxInf()
															.get(i).getDbtrAcct().getTp().getPrtry())) {
														
														prefixAccAcred = snp.getFIToFICstmrDrctDbt().getDrctDbtTxInf()
																.get(i).getDbtrAcct().getTp().getPrtry();
													
														prefixAccAcred = prefixAccAcred.equals("02")?"10":"20";
														if(!(prefixAccDeb != "02" || prefixAccDeb != "02")) {
															
															this.codError = "AC13"; 
															this.desError = "Tipo de cuenta del deudor invalida o ausente."
																	+ ". /DrctDbtTxInf/DbtrAcct/Id/Othr {Iteracion "+ (i+1) + "}";
															return null;
														}
													}
												}else {
													
													this.codError = "AC13"; 
													this.desError = "Tipo de cuenta del deudor invalida o ausente."
															+ ". /DrctDbtTxInf/DbtrAcct/Id/Othr {Iteracion "+ (i+1) + "}";
													return null;
													
												}
												//Implementar moneda 
											}else {
												
												this.codError = "CH21"; 
												this.desError = "El elemento es ausente"
														+ ". /DrctDbtTxInf/DbtrAcct/Id/Othr {Iteracion "+ (i+1) + "}";
												return null;
											}
										}else {
											
											this.codError = "CH21"; 
											this.desError = "El elemento es ausente"
													+ ". /DrctDbtTxInf/DbtrAcct/Id {Iteracion "+ (i+1) + "}";
											return null;
										}
									}else {
										
										this.codError = "CH21"; 
										this.desError = "El elemento es ausente"
												+ ". /DrctDbtTxInf/DbtrAcct {Iteracion "+ (i+1) + "}";
										return null;
									}
									
									/*DbtrAgt Institucion RECEPTORA*/
									if(snp.getFIToFICstmrDrctDbt().getDrctDbtTxInf().get(i).getDbtrAgt() != null) {
										
										if(snp.getFIToFICstmrDrctDbt().getDrctDbtTxInf().get(i).getDbtrAgt().getFinInstnId() != null) {
											
											if(!StringUtils.IsNullOrEmpty(snp.getFIToFICstmrDrctDbt().getDrctDbtTxInf()
													.get(i).getDbtrAgt().getFinInstnId().getBICFI())) {
												
												iso.setISO_033_FWDInsID(snp.getFIToFICstmrDrctDbt().getDrctDbtTxInf()
														.get(i).getDbtrAgt().getFinInstnId().getBICFI());
												
												if(!iso.getISO_033_FWDInsID().equalsIgnoreCase(MemoryGlobal.UrlSpiCodeEfi_BCE)) {
													
													this.codError = "RC05"; 
													this.desError = "El identificador BIC es inv?lido."
															+ ". /DrctDbtTxInf/DbtrAgt/FinInstnId/BICFI {Iteracion "+ (i+1) + "}";
													return null;
												}
												
											}else {
												
												this.codError = "RC05"; 
												this.desError = "El identificador BIC es inv?lido o est? ausente."
														+ ". /DrctDbtTxInf/DbtrAgt/FinInstnId/BICFI {Iteracion "+ (i+1) + "}";
												return null;
											}
										}else {
											
											this.codError = "CH21"; 
											this.desError = "El elemento es ausente"
													+ ". /DrctDbtTxInf/DbtrAgt/FinInstnId {Iteracion "+ (i+1) + "}";
											return null;
										}
									}else {
										
										this.codError = "CH21"; 
										this.desError = "El elemento es ausente"
												+ ". /DrctDbtTxInf/DbtrAgt {Iteracion "+ (i+1) + "}";
										return null;
									}
									
									/*DbtrAgtAcct cuenta corriente del debitante en el BCE*/
									/*if(snp.getFIToFICstmrDrctDbt().getDrctDbtTxInf().get(i).getDbtrAgtAcct() != null) {
									
										if(snp.getFIToFICstmrDrctDbt().getDrctDbtTxInf().get(i).getDbtrAgtAcct().getId() != null) {
											
											if(snp.getFIToFICstmrDrctDbt().getDrctDbtTxInf().get(i).getDbtrAgtAcct()
													.getId().getOthr() != null) {
												
												if(!StringUtils.IsNullOrEmpty(snp.getFIToFICstmrDrctDbt().getDrctDbtTxInf().get(i).getDbtrAgtAcct()
														.getId().getOthr().getId())) {
													
													iso.setISO_120_ExtendedData(snp.getFIToFICstmrDrctDbt().getDrctDbtTxInf().get(i).getDbtrAgtAcct()
														.getId().getOthr().getId());
												}else {
													
													this.codError = "CH21"; 
													this.desError = "El elemento es ausente"
															+ ". /DrctDbtTxInf/DbtrAgtAcct/Id/Othr/id {Iteracion "+ (i+1) + "}";
													return null;
												}
											}else {
												
												this.codError = "CH21"; 
												this.desError = "El elemento es ausente"
														+ ". /DrctDbtTxInf/DbtrAgtAcct/Id/Othr {Iteracion "+ (i+1) + "}";
												return null;
											}
										}else {
											
											this.codError = "CH21"; 
											this.desError = "El elemento es ausente"
													+ ". /DrctDbtTxInf/DbtrAgtAcct/Id {Iteracion "+ (i+1) + "}";
											return null;
										}
										//Implementar tipo de cuenta
										//Implementar tipo moneda
									}else {
										
										this.codError = "CH21"; 
										this.desError = "El elemento es ausente"
												+ ". /DrctDbtTxInf/DbtrAgtAcct {Iteracion "+ (i+1) + "}";
										return null;
									}*/
									
									/* Purp Razon de la transferencia*/
									if(snp.getFIToFICstmrDrctDbt().getDrctDbtTxInf()
											.get(i).getPurp() != null) {
										if(!StringUtils.IsNullOrEmpty(snp.getFIToFICstmrDrctDbt().getDrctDbtTxInf()
											.get(i).getPurp().getPrtry())) {
											
											iso.setISO_121_ExtendedData(snp.getFIToFICstmrDrctDbt().getDrctDbtTxInf()
											.get(i).getPurp().getPrtry());
										}else {
											
											this.codError = "CH21"; 
											this.desError = "El elemento es ausente"
													+ ". /DrctDbtTxInf/Purp/Prtry {Iteracion "+ (i+1) + "}";
											return null;
										}
									}else {
										
										this.codError = "CH21"; 
										this.desError = "El elemento es ausente"
												+ ". /DrctDbtTxInf/Purp {Iteracion "+ (i+1) + "}";
										return null;
									}
									validaNroTrx ++;	
								}finally {
									
									if(!StringUtils.IsNullOrEmpty(prefixAccDeb) && !StringUtils.IsNullOrEmpty(prefixAccAcred)) {
										
										iso.setISO_003_ProcessingCode("41"+ prefixAccDeb + prefixAccAcred);
									}
										
								}
							
								isoList.add(iso);
								
								if(snp.getFIToFICstmrDrctDbt().getDrctDbtTxInf().size() > 1) {
									
									Iso8583 isoClone = (Iso8583) iso.clone();
									iso = new Iso8583();
									iso = isoClone;
									iso.setISO_115_ExtendedData(StringUtils.Empty());//Por serialziacion
									
								}
								
								flagFechasProgramadas = false;
								
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
									this.desError = "La transacci?n no es unica (TxId)";
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
									+ ". /FIToFICstmrDrctDbt/DrctDbtTxInf. Varios elementos";
							return null;
						}
					}else {
						
						this.codError = "CH21"; 
						this.desError = "El elemento es ausente"
								+ ". /FIToFICstmrDrctDbt/DrctDbtTxInf";
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
						+ ". urn:iso:std:iso:20022:tech:xsd:pacs.003.001.04";
				return null;
			}
			
		} catch (Exception e) {
				
			log.WriteLogMonitor("Error modulo BceSCIParser::parseDocumentSolicitud ", TypeMonitor.error, e);
			this.codError = "TA01";
			this.desError = "La transmision del archivo no fue exitosa, tuvo que ser abortada (por razones tecnicas).";
			
		}finally {
			
			if(isoList != null && isoList.size() > 1) {
				
				//Collections.sort(isoList, (p1 , p2) -> p1.getISO_BitMap().compareTo(p2.getISO_BitMap()));
			}
		}
		return isoList;
	}
	
}
