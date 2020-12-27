package com.belejanor.switcher.parser;

import java.util.Date;

import com.belejanor.switcher.cscoreswitch.Iso8583;
import com.belejanor.switcher.cscoreswitch.TransactionConfiguration;
import com.belejanor.switcher.cscoreswitch.wIso8583;
import com.belejanor.switcher.logger.Logger;
import com.belejanor.switcher.logger.LoggerConfig;
import com.belejanor.switcher.logger.LoggerConfig.TypeMonitor;
import com.belejanor.switcher.memcached.MemoryGlobal;
import com.belejanor.switcher.memcached.TransactionConfig;
import com.belejanor.switcher.sqlservices.IsoRetrievalTransaction;
import com.belejanor.switcher.utils.GeneralUtils;
import com.belejanor.switcher.utils.StringUtils;

import urn.iso.std.iso20022.tech.xsd.pacs_003_001_04.DocumentRetiro;
import urn.iso.std.iso20022.tech.xsd.pacs_004_001_04.DocumentReverso;
import urn.iso.std.iso20022.tech.xsd.pacs_008_001_04.DocumentDeposito;
import urn.iso.std.iso20022.tech.xsd.pacs_008_001_04.DocumentTransferencia;

public class BceVCParser {
	
	
	private Logger log;
	public BceVCParser(){
		
		log = new Logger();
	}
	public Iso8583 parseDocumentDepositoVC(DocumentDeposito documentoDeposito){
		
		Iso8583 iso = new Iso8583();
		String campo = null;
		try {
			
			if(documentoDeposito != null){
				
			    ///ESTRUCTURA GRPHDR
				if(documentoDeposito.getFIToFICstmrCdtTrf().getGrpHdr() != null){
					
					if(!StringUtils.IsNullOrEmpty(documentoDeposito.getFIToFICstmrCdtTrf().getGrpHdr().getMsgId())){
						if(documentoDeposito.getFIToFICstmrCdtTrf().getGrpHdr().getMsgId().length() != 32){
							
							campo = "Campo [FIToFICstmrCdtTrf.GrpHdr.MsgId] es invalido"
							        .toUpperCase();
							iso.setISO_039_ResponseCode("905");
							iso.setISO_039p_ResponseDetail(campo);
							iso.setISO_104_TranDescription("RC02");
							return iso;
							
						}else
							iso.setISO_011_SysAuditNumber(documentoDeposito.getFIToFICstmrCdtTrf().getGrpHdr().getMsgId());
					}else{
						campo = "Valor unico de la transaccion [FIToFICstmrCdtTrf.GrpHdr.MsgId] es nulo o vacio"
						        .toUpperCase();
						iso.setISO_039_ResponseCode("905");
						iso.setISO_039p_ResponseDetail(campo);
						iso.setISO_104_TranDescription("RC02");
						return iso;
					}
					if(documentoDeposito.getFIToFICstmrCdtTrf().getGrpHdr().getCreDtTm() != null){
						
						campo = "Fecha Transaccion [FIToFICstmrCdtTrf.GrpHdr.CreDtTm] es nulo"
						        .toUpperCase();
						Date date = documentoDeposito.getFIToFICstmrCdtTrf().getGrpHdr()
								.getCreDtTm().toGregorianCalendar().getTime();
						
						if(date.before(new Date())){
							iso.setISO_012_LocalDatetime(date);	
						}else{
							campo = "Fecha Transaccion [FIToFICstmrCdtTrf.GrpHdr.CreDtTm] es invalido"
							        .toUpperCase();
							iso.setISO_039_ResponseCode("905");
							iso.setISO_039p_ResponseDetail(campo);
							iso.setISO_104_TranDescription("DT01");
							return iso;
						}
						
					}else{
						campo = "Estructura FIToFICstmrCdtTrf.GrpHdr.CreDtTm es nula"
						        .toUpperCase();
						iso.setISO_039_ResponseCode("905");
						iso.setISO_039p_ResponseDetail(campo);
						iso.setISO_104_TranDescription("DT01");
						return iso;
					}
					if(!StringUtils.IsNullOrEmpty(documentoDeposito.getFIToFICstmrCdtTrf().getGrpHdr().getNbOfTxs())){
						
						int numberTrx = Integer.parseInt(documentoDeposito.getFIToFICstmrCdtTrf()
								        .getGrpHdr().getNbOfTxs());
						
						if(numberTrx > 0 && numberTrx <= 1){
							
							iso.setISO_022_PosEntryMode(String.valueOf(numberTrx));
							
						}else{
							
							campo = "Numero de Transacciones [FIToFICstmrCdtTrf.GrpHdr.NbOfTxs] invalido, "
									+ "(No se soporta mas de una transaccion)"
							        .toUpperCase();
							iso.setISO_039_ResponseCode("905");
							iso.setISO_039p_ResponseDetail(campo);
							iso.setISO_104_TranDescription("AM19");
							return iso;
						}
						
					}else {
						
						campo = "Numero de Transacciones [FIToFICstmrCdtTrf.GrpHdr.NbOfTxs] es nulo o vacio"
						        .toUpperCase();
						iso.setISO_039_ResponseCode("905");
						iso.setISO_039p_ResponseDetail(campo);
						iso.setISO_104_TranDescription("AM19");
						return iso;
					}
					if(documentoDeposito.getFIToFICstmrCdtTrf().getGrpHdr().getCtrlSum() != null){
						
						campo = "Monto de la Transaccion [FIToFICstmrCdtTrf.GrpHdr.CtrlSum] debe ser mayor a 0"
								.toUpperCase();
						Double monto = documentoDeposito.getFIToFICstmrCdtTrf().getGrpHdr()
								       .getCtrlSum().doubleValue();
						if(monto > 0){
							
							iso.setISO_004_AmountTransaction(monto);
							
						}else {
							
							iso.setISO_039_ResponseCode("905");
							iso.setISO_039p_ResponseDetail(campo);
							iso.setISO_104_TranDescription("AM01");
							return iso;
						}
						
					}else{
						
						campo = "Monto de la Transaccion [FIToFICstmrCdtTrf.GrpHdr.CtrlSum] es nulo"
						        .toUpperCase();
						iso.setISO_039_ResponseCode("905");
						iso.setISO_039p_ResponseDetail(campo);
						iso.setISO_104_TranDescription("AM01");
						return iso;
					}
					
					if(documentoDeposito.getFIToFICstmrCdtTrf().getGrpHdr().getSttlmInf().getSttlmMtd() != null){
						
						campo = "Campo [FIToFICstmrCdtTrf.GrpHdr.SttlmInf.SttlmMtd] es invalido"
						        .toUpperCase();
						if(documentoDeposito.getFIToFICstmrCdtTrf().getGrpHdr().getSttlmInf().getSttlmMtd()
								.value().equalsIgnoreCase("CLRG")){
							
							iso.setISO_114_ExtendedData("CLRG");
							
						}else {
							
							iso.setISO_039_ResponseCode("905");
							iso.setISO_039p_ResponseDetail(campo);
							return iso;
						}
	
					}else{
						
						campo = "Estructura [FIToFICstmrCdtTrf.GrpHdr.SttlmInf.SttlmMtd] es nula"
						        .toUpperCase();
						iso.setISO_039_ResponseCode("905");
						iso.setISO_039p_ResponseDetail(campo);
						return iso;
					}
					
					if(documentoDeposito.getFIToFICstmrCdtTrf().getGrpHdr().getInstgAgt()
							.getFinInstnId() != null){
						
						if(!StringUtils.IsNullOrEmpty(documentoDeposito.getFIToFICstmrCdtTrf().getGrpHdr().getInstgAgt()
								.getFinInstnId().getBICFI())){
							
							iso.setISO_032_ACQInsID(documentoDeposito.getFIToFICstmrCdtTrf().getGrpHdr().getInstgAgt()
									.getFinInstnId().getBICFI());
							
						}else{
							
							campo = "Entidad ordenante [FIToFICstmrCdtTrf.GrpHdr.InstgAgt.FinInstnId.getBICFI] es nula o vacia"
							        .toUpperCase();
							iso.setISO_039_ResponseCode("905");
							iso.setISO_039p_ResponseDetail(campo);
							iso.setISO_104_TranDescription("AGNT");
							return iso;
						}
						
					}else {
						
						campo = "Estructura Entidad ordenante [FIToFICstmrCdtTrf.GrpHdr.InstgAgt.FinInstnId] es nula"
						        .toUpperCase();
						iso.setISO_039_ResponseCode("905");
						iso.setISO_039p_ResponseDetail(campo);
						iso.setISO_104_TranDescription("AGNT");
						return iso;
					}
					if(documentoDeposito.getFIToFICstmrCdtTrf().getGrpHdr().getInstgAgt()
							.getBrnchId() != null){
						
						if(!StringUtils.IsNullOrEmpty(documentoDeposito.getFIToFICstmrCdtTrf().getGrpHdr().getInstgAgt()
							.getBrnchId().getId())){
							
							iso.setISO_041_CardAcceptorID(documentoDeposito.getFIToFICstmrCdtTrf().getGrpHdr()
									.getInstgAgt()
									.getBrnchId().getId());
							
						}else{
							
							campo = "Agencia Entidad Ordenante [FIToFICstmrCdtTrf.GrpHdr.InstgAgt.BrnchId.Id] es nula"
							        .toUpperCase();
							iso.setISO_039_ResponseCode("905");
							iso.setISO_039p_ResponseDetail(campo);
							iso.setISO_104_TranDescription("AGNT");
							return iso;
						}
						
					}else{
						
						campo = "Estructura Agencia Entidad Ordenante [FIToFICstmrCdtTrf.GrpHdr.InstgAgt.BrnchId] es nula"
						        .toUpperCase();
						iso.setISO_039_ResponseCode("905");
						iso.setISO_039p_ResponseDetail(campo);
						iso.setISO_104_TranDescription("AGNT");
						return iso;
					}
					if(documentoDeposito.getFIToFICstmrCdtTrf().getGrpHdr().getInstdAgt()
							.getFinInstnId() != null){
						
						if(!StringUtils.IsNullOrEmpty(documentoDeposito.getFIToFICstmrCdtTrf().getGrpHdr().getInstdAgt()
								.getFinInstnId().getBICFI())){
							
							iso.setISO_BitMap(documentoDeposito.getFIToFICstmrCdtTrf().getGrpHdr().getInstdAgt()
									.getFinInstnId().getBICFI());
							
						}else{
							
							campo = "Entidad Intermedia [FIToFICstmrCdtTrf.GrpHdr.InstdAgt.FinInstnId.BICFI] es nula o vacia"
							        .toUpperCase();
							iso.setISO_039_ResponseCode("905");
							iso.setISO_039p_ResponseDetail(campo);
							iso.setISO_104_TranDescription("RC11");
							return iso;
						}
							
					}else {
						
						campo = "Estructura Entidad Intermedia [FIToFICstmrCdtTrf.GrpHdr.InstdAgt.FinInstnId] es nula"
						        .toUpperCase();
						iso.setISO_039_ResponseCode("905");
						iso.setISO_039p_ResponseDetail(campo);
						iso.setISO_104_TranDescription("RC11");
						return iso;
					}
					////////// ESTRUCTURA CDTTRFXINF
					
					if(documentoDeposito.getFIToFICstmrCdtTrf().getCdtTrfTxInf().get(0).getPmtId() != null){
						
						if(!StringUtils.IsNullOrEmpty(documentoDeposito.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
								.get(0).getPmtId().getEndToEndId())){
							
							iso.setISO_023_CardSeq(documentoDeposito.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
								.get(0).getPmtId().getEndToEndId());	
							
						}else {
							
							campo = "Numero de Papeleta [FIToFICstmrCdtTrf.CdtTrfTxInf.PmtId.EndToEndId] es nula"
							        .toUpperCase();
							iso.setISO_039_ResponseCode("905");
							iso.setISO_039p_ResponseDetail(campo);
							iso.setISO_104_TranDescription("FF08");
							return iso;
						}
						
						if(!StringUtils.IsNullOrEmpty(documentoDeposito.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
								.get(0).getPmtId().getTxId())){
							
							iso.setISO_037_RetrievalReferenceNumber(documentoDeposito.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
								.get(0).getPmtId().getTxId());	
							
						}else {
							
							campo = "Estructura Numero unico Trx.(Entidad Ordenante) [FIToFICstmrCdtTrf.CdtTrfTxInf.PmtId.TxId] es nula"
							        .toUpperCase();
							iso.setISO_039_ResponseCode("905");
							iso.setISO_039p_ResponseDetail(campo);
							iso.setISO_104_TranDescription("RC04");
							return iso;
						}
						
						if(!StringUtils.IsNullOrEmpty(documentoDeposito.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
								.get(0).getPmtId().getClrSysRef())){
							iso.setISO_090_OriginalData(documentoDeposito.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
								.get(0).getPmtId().getClrSysRef());
							
						}else{
							
							campo = "Campo [FIToFICstmrCdtTrf.CdtTrfTxInf.PmtId.ClrSysRef] es nulo o vacio"
							        .toUpperCase();
							iso.setISO_039_ResponseCode("905");
							iso.setISO_039p_ResponseDetail(campo);
							return iso;
						}
						
					}else{
						
						campo = "Estructura [FIToFICstmrCdtTrf.CdtTrfTxInf.PmtId] es nula"
						        .toUpperCase();
						iso.setISO_039_ResponseCode("905");
						iso.setISO_039p_ResponseDetail(campo);
						return iso;
					}
					
					if(documentoDeposito.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
							.get(0).getIntrBkSttlmAmt() != null){
						
						if(!StringUtils.IsNullOrEmpty(documentoDeposito.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
								.get(0).getIntrBkSttlmAmt().getCcy())){
							iso.setISO_049_TranCurrCode(documentoDeposito.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
								.get(0).getIntrBkSttlmAmt().getCcy().equalsIgnoreCase("USD")?840:0);
							
						}else{
							
							campo = "Moneda de la Transaccion [FIToFICstmrCdtTrf.CdtTrfTxInf"
									+ ".IntrBkSttlmAmt.Ccy] es vacia o nula"
							        .toUpperCase();
							iso.setISO_039_ResponseCode("905");
							iso.setISO_039p_ResponseDetail(campo);
							iso.setISO_104_TranDescription("AC09");
							return iso;
						}
						double val = documentoDeposito.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
								.get(0).getIntrBkSttlmAmt().getValue().doubleValue();
						if(val > 0){
							
							iso.setISO_006_BillAmount(val);
							
						}else {
							
							campo = "Monto de la Transaccion [FIToFICstmrCdtTrf"
									+ ".CdtTrfTxInf.IntrBkSttlmAmt.Value] es vacia o nula"
							        .toUpperCase();
							iso.setISO_039_ResponseCode("905");
							iso.setISO_039p_ResponseDetail(campo);
							iso.setISO_104_TranDescription("AM01");
							return iso;
						}
					
						
					}else{
						
						campo = "Estructura [FIToFICstmrCdtTrf.CdtTrfTxInf.IntrBkSttlmAmt] es nula"
						        .toUpperCase();
						iso.setISO_039_ResponseCode("905");
						iso.setISO_039p_ResponseDetail(campo);
						return iso;
					}
					if(documentoDeposito.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
							.get(0).getIntrBkSttlmDt() != null){
						
						Date dateBusiness = documentoDeposito.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
								.get(0).getIntrBkSttlmDt().toGregorianCalendar().getTime();
						
						if(dateBusiness.before(new Date())){
							
							iso.setISO_015_SettlementDatel(dateBusiness);	
						}else{
							campo = "Fecha Contable de la Trx. [FIToFICstmrCdtTrf.CdtTrfTxInf.IntrBkSttlmDt] es invalido"
							        .toUpperCase();
							iso.setISO_039_ResponseCode("905");
							iso.setISO_039p_ResponseDetail(campo);
							return iso;
						}
					}else{
						
						campo = "Fecha Contable de la Trx. [FIToFICstmrCdtTrf.CdtTrfTxInf.IntrBkSttlmDt] es nula"
						        .toUpperCase();
						iso.setISO_039_ResponseCode("905");
						iso.setISO_039p_ResponseDetail(campo);
						return iso;
					}
					if(documentoDeposito.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
							.get(0).getChrgBr() != null){
						
						iso.setISO_035_Track2(documentoDeposito.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
								.get(0).getChrgBr().value());
						
					}else {
						campo = "Estructura [FIToFICstmrCdtTrf.CdtTrfTxInf.ChrgBr] es nula"
						        .toUpperCase();
						iso.setISO_039_ResponseCode("905");
						iso.setISO_039p_ResponseDetail(campo);
						return iso;
					}
					///SUBSESTRUCTURA DBTR (ATOS DE LA PERSONA QUIEN ESTA REAIZNDO EL DEPOSITO) 
					if(documentoDeposito.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
							.get(0).getDbtr() != null){
						
						if(!StringUtils.IsNullOrEmpty(documentoDeposito.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
								.get(0).getDbtr().getNm())){
							
							iso.setISO_034_PANExt(documentoDeposito.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
									.get(0).getDbtr().getNm());
							
						}else{
							campo = "Nombres del depositante [FIToFICstmrCdtTrf.CdtTrfTxInf.Dbtr.Nm] es nulo o vacio"
							        .toUpperCase();
							iso.setISO_039_ResponseCode("905");
							iso.setISO_039p_ResponseDetail(campo);
							iso.setISO_104_TranDescription("BE21");
							return iso;
						}
						if(documentoDeposito.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
								.get(0).getDbtr().getId() != null){
							if(documentoDeposito.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
									.get(0).getDbtr().getId().getPrvtId() != null){
								
								if(documentoDeposito.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
										.get(0).getDbtr().getId().getPrvtId().getOthr() != null){
									if(!StringUtils.IsNullOrEmpty(documentoDeposito.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
											.get(0).getDbtr().getId().getPrvtId().getOthr().get(0).getId())){
										
										iso.setISO_002_PAN(documentoDeposito.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
											.get(0).getDbtr().getId().getPrvtId().getOthr().get(0).getId());
										
										if(documentoDeposito.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
												.get(0).getDbtr().getId().getPrvtId().getOthr()
												.get(0).getSchmeNm() != null){
											if(!StringUtils.IsNullOrEmpty(documentoDeposito.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
													.get(0).getDbtr().getId().getPrvtId().getOthr()
													.get(0).getSchmeNm().getCd())){
												
												iso.setISO_055_EMV(documentoDeposito.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
													.get(0).getDbtr().getId().getPrvtId().getOthr()
													.get(0).getSchmeNm().getCd());
												
											}else {
												
												campo = "Tipo de Identificacion del depositante [FIToFICstmrCdtTrf.CdtTrfTxInf.Dbtr.Id.PrvtId.Othr."
														+ "SchmeNm.Cd] es nulo o vacio"
												        .toUpperCase();
												iso.setISO_039_ResponseCode("905");
												iso.setISO_039p_ResponseDetail(campo);
												iso.setISO_104_TranDescription("BE15");
												return iso;
											}
											
										}else{
											
											campo = "Estructura Tipo Identificacion depositante [FIToFICstmrCdtTrf.CdtTrfTxInf.Dbtr.Id.PrvtId.Othr."
													+ "SchmeNm] es nulo"
											        .toUpperCase();
											iso.setISO_039_ResponseCode("905");
											iso.setISO_039p_ResponseDetail(campo);
											iso.setISO_104_TranDescription("BE15");
											return iso;
										}
										
									}else{
										
										campo = "Nro. de Identificacion del Depositante [FIToFICstmrCdtTrf.CdtTrfTxInf"
												+ ".Dbtr.Id.PrvtId.Othr.Id] es nulo o vacio"
										        .toUpperCase();
										iso.setISO_039_ResponseCode("905");
										iso.setISO_039p_ResponseDetail(campo);
										return iso;
									}
									
								}else{
									campo = "Estructura Nro. de Identificacion del Depositante [FIToFICstmrCdtTrf"
											+ ".CdtTrfTxInf.Dbtr.Id.PrvtId.Othr] es nulo"
									        .toUpperCase();
									iso.setISO_039_ResponseCode("905");
									iso.setISO_039p_ResponseDetail(campo);
									return iso;
								}
								
							}else{
								
								campo = "Estructura [FIToFICstmrCdtTrf.CdtTrfTxInf.Dbtr.Id.PrvtId] es nula"
								        .toUpperCase();
								iso.setISO_039_ResponseCode("905");
								iso.setISO_039p_ResponseDetail(campo);
								return iso;
							}
							
						}else {
							campo = "Estructura identificacion del depositante [FIToFICstmrCdtTrf"
									+ ".CdtTrfTxInf.Dbtr.Id] es nulo"
							        .toUpperCase();
							iso.setISO_039_ResponseCode("905");
							iso.setISO_039p_ResponseDetail(campo);
							return iso;
						}
						
						
					}else{
						
						campo = "Estructura Informacion del Depositante [FIToFICstmrCdtTrf.CdtTrfTxInf.Dbtr] es nula"
						        .toUpperCase();
						iso.setISO_039_ResponseCode("905");
						iso.setISO_039p_ResponseDetail(campo);
						return iso;
					}
					///SUBSTRUCTURA DBTRAGT (DATOS DE LA ENTIDAD ADQUIRIENTE)
					String efi_age = null;
					if(documentoDeposito.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
							.get(0).getDbtrAgt() != null){
						if(documentoDeposito.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
								.get(0).getDbtrAgt().getFinInstnId() != null){
							if(!StringUtils.IsNullOrEmpty(documentoDeposito.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
								.get(0).getDbtrAgt().getFinInstnId().getBICFI())){
								
								efi_age = documentoDeposito.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
										.get(0).getDbtrAgt().getFinInstnId().getBICFI();
							if(documentoDeposito.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
									.get(0).getDbtrAgt().getBrnchId() != null){
								
								if(!StringUtils.IsNullOrEmpty(documentoDeposito.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
									.get(0).getDbtrAgt().getBrnchId().getId())){
									
									efi_age += "|" + documentoDeposito.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
											.get(0).getDbtrAgt().getBrnchId().getId();
									iso.setISO_120_ExtendedData(efi_age);
									
								}else{
									
									campo = "Agencia de la Entidad Ordentante [FIToFICstmrCdtTrf."
											+ "CdtTrfTxInf.DbtrAgt.BrnchId.Id] es vacio o nulo"
									        .toUpperCase();
									iso.setISO_039_ResponseCode("905");
									iso.setISO_039p_ResponseDetail(campo);
									return iso;
								}
								
							}else{
								
								campo = "Agencia de la Entidad Ordentante [FIToFICstmrCdtTrf."
										+ "CdtTrfTxInf.DbtrAgt.BrnchId] es nula"
								        .toUpperCase();
								iso.setISO_039_ResponseCode("905");
								iso.setISO_039p_ResponseDetail(campo);
								return iso;
							}
								
						}else {
							
							campo = "Codigo Entidad Ordentante [FIToFICstmrCdtTrf.CdtTrfTxInf"
									+ ".DbtrAgt.FinInstnId.BICFI] es nula"
							        .toUpperCase();
							iso.setISO_039_ResponseCode("905");
							iso.setISO_039p_ResponseDetail(campo);
							iso.setISO_104_TranDescription("AGNT");
							return iso;
						}
							
						}else{
							
							campo = "Estructura Entidad Ordentante [FIToFICstmrCdtTrf."
									+ "CdtTrfTxInf.DbtrAgt.FinInstnId] es nula"
							        .toUpperCase();
							iso.setISO_039_ResponseCode("905");
							iso.setISO_039p_ResponseDetail(campo);
							iso.setISO_104_TranDescription("AGNT");
							return iso;
						}
						
					}else{
						
						campo = "Estructura con informacion de la Entidad Ordenante "
								+ "[FIToFICstmrCdtTrf.CdtTrfTxInf.DbtrAgt] es nula"
						        .toUpperCase();
						iso.setISO_039_ResponseCode("905");
						iso.setISO_039p_ResponseDetail(campo);
						return iso;
					}
					///SUBSTRUCTURA DBTRAGTACCT (NUMERO DE CUENTA DE LA EFI ADQUIRIENTE EN EL BCE)
					
					if(documentoDeposito.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
							.get(0).getDbtrAgtAcct() != null){
						
						if(documentoDeposito.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
								.get(0).getDbtrAgtAcct().getId() != null){
							if(documentoDeposito.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
									.get(0).getDbtrAgtAcct().getId().getOthr() != null){
								
								if(!StringUtils.IsNullOrEmpty(documentoDeposito.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
									.get(0).getDbtrAgtAcct().getId().getOthr().getId())){
									
									iso.setISO_121_ExtendedData(documentoDeposito.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
									.get(0).getDbtrAgtAcct().getId().getOthr().getId());
									
								}else{
									
									campo = "Numero de Cuenta de la Entidad Ordenante [FIToFICstmrCdtTrf."
											+ "CdtTrfTxInf.DbtrAgtAcct.Id.Othr.Id] es vacia o nula"
									        .toUpperCase();
									iso.setISO_039_ResponseCode("905");
									iso.setISO_039p_ResponseDetail(campo);
									iso.setISO_104_TranDescription("AC01");
									return iso;
								}
								
							}else{
								
								campo = "Numero de Cuenta de la Entidad Ordenante "
										+ "[FIToFICstmrCdtTrf.CdtTrfTxInf.DbtrAgtAcct.Id.Othr] es nulo"
								        .toUpperCase();
								iso.setISO_039_ResponseCode("905");
								iso.setISO_039p_ResponseDetail(campo);
								iso.setISO_104_TranDescription("AC01");
								return iso;
							}
							
						}else{
							
							campo = "Estructura Numero de Cuenta de la Entidad Ordenante "
									+ "[FIToFICstmrCdtTrf.CdtTrfTxInf.DbtrAgtAcct.Id] es nulo"
							        .toUpperCase();
							iso.setISO_039_ResponseCode("905");
							iso.setISO_039p_ResponseDetail(campo);
							iso.setISO_104_TranDescription("AC01");
							return iso;
						}
						
						if(documentoDeposito.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
								.get(0).getDbtrAgtAcct().getTp() != null){
							
							if(!StringUtils.IsNullOrEmpty(documentoDeposito.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
								.get(0).getDbtrAgtAcct().getTp().getPrtry())){
								
								iso.setISO_121_ExtendedData(iso.getISO_121_ExtendedData() + "|" +  documentoDeposito.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
										.get(0).getDbtrAgtAcct().getTp().getPrtry());
								
							}else{
								
								campo = "Tipo de Cuenta de la entidad Ordenante "
										+ "[FIToFICstmrCdtTrf.CdtTrfTxInf.DbtrAgtAcct.Tp.Prtry] es nula o vacia"
								        .toUpperCase();
								iso.setISO_039_ResponseCode("905");
								iso.setISO_039p_ResponseDetail(campo);
								iso.setISO_104_TranDescription("AC12");
								return iso;
							}
							
						}else{
							
							campo = "Tipo de Cuenta de la entidad Ordenante [FIToFICstmrCdtTrf."
									+ "CdtTrfTxInf.DbtrAgtAcct.Tp] es nula"
							        .toUpperCase();
							iso.setISO_039_ResponseCode("905");
							iso.setISO_039p_ResponseDetail(campo);
							iso.setISO_104_TranDescription("AC12");
							return iso;
						}
						
						if(!StringUtils.IsNullOrEmpty(documentoDeposito.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
								.get(0).getDbtrAgtAcct().getCcy())){
							
							iso.setISO_121_ExtendedData(iso.getISO_121_ExtendedData() + "|" +  
							        documentoDeposito.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
									.get(0).getDbtrAgtAcct().getCcy());
							
						}else{
							
							campo = "Cod. Moneda de la Cta. Efi Ordenante [FIToFICstmrCdtTrf."
									+ "CdtTrfTxInf.DbtrAgtAcct.Ccy] es nulo o vacio"
							        .toUpperCase();
							iso.setISO_039_ResponseCode("905");
							iso.setISO_039p_ResponseDetail(campo);
							iso.setISO_104_TranDescription("AC09");
							return iso;
						}
						
					}else{
						
						campo = "Estructura informacion de la Cuenta del BCE de la "
								+ "EFI Ordentante [FIToFICstmrCdtTrf.CdtTrfTxInf.DbtrAgtAcct] es nula"
						        .toUpperCase();
						iso.setISO_039_ResponseCode("905");
						iso.setISO_039p_ResponseDetail(campo);
						iso.setISO_104_TranDescription("AC01");
						return iso;
					}
					///SUBSTRUCTURA CDTRAGT (DATOS DE LA ENTIDAD AUTORIZADORA)
					
					if(documentoDeposito.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
							.get(0).getCdtrAgt() != null){
						
						if(documentoDeposito.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
								.get(0).getCdtrAgt().getFinInstnId() != null){
							
							if(!StringUtils.IsNullOrEmpty(documentoDeposito.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
									.get(0).getCdtrAgt().getFinInstnId().getBICFI())){
								
								if(documentoDeposito.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
										.get(0).getCdtrAgt().getFinInstnId().getBICFI()
										.equalsIgnoreCase(MemoryGlobal.BCE_Efi_VC)){
									
									iso.setISO_033_FWDInsID(MemoryGlobal.BCE_Efi_VC);
									
								}else{
									
									campo = "Cod. EFI Receptora [FIToFICstmrCdtTrf.CdtTrfTxInf."
											+ "CdtrAgt.FinInstnId.BICFI] "
											+ "no coincide con la asignada a nuestra institucion"
									        .toUpperCase();
									iso.setISO_039_ResponseCode("905");
									iso.setISO_039p_ResponseDetail(campo);
									return iso;
								}
								
							}else{
								
								campo = "Cod. EFI Receptora [FIToFICstmrCdtTrf.CdtTrfTxInf"
										+ ".CdtrAgt.FinInstnId.BICFI] es nula o vacia"
								        .toUpperCase();
								iso.setISO_039_ResponseCode("905");
								iso.setISO_039p_ResponseDetail(campo);
								return iso;
							}
							
						}else{
							
							campo = "Estructura EFI Receptora [FIToFICstmrCdtTrf."
									+ "CdtTrfTxInf.CdtrAgt.FinInstnId] es nulo"
							        .toUpperCase();
							iso.setISO_039_ResponseCode("905");
							iso.setISO_039p_ResponseDetail(campo);
							return iso;
						}
						
					}else{
						
						campo = "Estructura con informacion de la EFI Recepora "
								+ "[FIToFICstmrCdtTrf.CdtTrfTxInf.CdtrAgt] es nula"
						        .toUpperCase();
						iso.setISO_039_ResponseCode("905");
						iso.setISO_039p_ResponseDetail(campo);
						return iso;
					}
					
					///SUBSTRUCTURA Cdtr (DATOS DEL CUENTAHABIENTE ENTIDAD AUTORIZADORA)
									
					if(documentoDeposito.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
							.get(0).getCdtr() != null){
						
						if(!StringUtils.IsNullOrEmpty(documentoDeposito.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
							.get(0).getCdtr().getNm())){
							
							iso.setISO_115_ExtendedData(documentoDeposito.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
									.get(0).getCdtr().getNm());
						if(documentoDeposito.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
									.get(0).getCdtr().getId() != null){
							
							if(documentoDeposito.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
									.get(0).getCdtr().getId().getOrgId() != null){
								
								if(documentoDeposito.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
										.get(0).getCdtr().getId().getOrgId().getOthr() != null){
									
									if(!StringUtils.IsNullOrEmpty(documentoDeposito.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
											.get(0).getCdtr().getId().getOrgId().getOthr().get(0).getId())){
										
										iso.setISO_115_ExtendedData(iso.getISO_115_ExtendedData() + "|" + documentoDeposito
												.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
												.get(0).getCdtr().getId().getOrgId().getOthr().get(0).getId());
										if(documentoDeposito.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
												.get(0).getCdtr().getId().getOrgId().getOthr().get(0).getSchmeNm() != null){
											
											if(!StringUtils.IsNullOrEmpty(documentoDeposito.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
												.get(0).getCdtr().getId().getOrgId().getOthr().get(0).getSchmeNm().getCd())){
												
												iso.setISO_115_ExtendedData(iso.getISO_115_ExtendedData() + "|" + documentoDeposito
														.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
														.get(0).getCdtr().getId().getOrgId().getOthr().get(0)
														.getSchmeNm().getCd());
												
											}else {
												
												campo = "Tipo de identificacion del cuentahabiente (Persona Juridica) receptor [FIToFICstmrCdtTrf."
														+ "CdtTrfTxInf.Cdtr.Id.OrgId"
														+ ".Othr.Id.SchmeNm.Cd] es nulo o vacio"
												        .toUpperCase();
												iso.setISO_039_ResponseCode("905");
												iso.setISO_039p_ResponseDetail(campo);
												iso.setISO_104_TranDescription("BE15");
												return iso;
											}
											
										}else{
											
											campo = "Tipo de identificacion del cuentahabiente receptor (Persona Juridica) "
													+ "[FIToFICstmrCdtTrf.CdtTrfTxInf.Cdtr.Id.OrgId"
													+ ".Othr.Id.SchmeNm] es nulo"
											        .toUpperCase();
											iso.setISO_039_ResponseCode("905");
											iso.setISO_039p_ResponseDetail(campo);
											iso.setISO_104_TranDescription("BE15");
											return iso;
										}
										
									}else{
										
										campo = "Nro. de Identificacion del cuentahabiente receptor (Persona Juridica) "
												+ "[FIToFICstmrCdtTrf.CdtTrfTxInf.Cdtr.Id.OrgId.Othr.Id] "
												+ "es nulo o vacio"
										        .toUpperCase();
										iso.setISO_039_ResponseCode("905");
										iso.setISO_039p_ResponseDetail(campo);
										return iso;
									}
									
								}else{
									
									campo = "Estrucura Cuentahabiente receptor (Persona Juridica) [FIToFICstmrCdtTrf."
											+ "CdtTrfTxInf.Cdtr.Id.OrgId.Othr] es nulo"
									        .toUpperCase();
									iso.setISO_039_ResponseCode("905");
									iso.setISO_039p_ResponseDetail(campo);
									return iso;
								}
								
							}else{
								
								if(documentoDeposito.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
										.get(0).getCdtr().getId().getPrvtId() != null){
									
									if(documentoDeposito.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
											.get(0).getCdtr().getId().getPrvtId().getOthr() != null){
										
										if(!StringUtils.IsNullOrEmpty(documentoDeposito.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
												.get(0).getCdtr().getId().getPrvtId().getOthr().get(0).getId())){
											
											iso.setISO_115_ExtendedData(iso.getISO_115_ExtendedData() + "|" + documentoDeposito.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
													.get(0).getCdtr().getId().getPrvtId().getOthr().get(0).getId());
											
											if(documentoDeposito.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
													.get(0).getCdtr().getId().getPrvtId().getOthr().get(0).getSchmeNm() != null){
												
												if(!StringUtils.IsNullOrEmpty(documentoDeposito.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
													.get(0).getCdtr().getId().getPrvtId().getOthr().get(0).getSchmeNm().getCd())){
													
													iso.setISO_115_ExtendedData(iso.getISO_115_ExtendedData() + "|" + documentoDeposito.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
															.get(0).getCdtr().getId().getPrvtId().getOthr().get(0).getSchmeNm().getCd());
													
												}else {
													
													campo = "Tipop Identidicacion Cuentahabiente (Persona Natural) receptor [FIToFICstmrCdtTrf."
															+ "CdtTrfTxInf.Cdtr.Id.PrvtId.Othr.id] es nulo"
													        .toUpperCase();
													iso.setISO_039_ResponseCode("905");
													iso.setISO_039p_ResponseDetail(campo);
													return iso;
												}
												
											}else{
												
												campo = "Estrucura Cuentahabiente (Persona Natural) receptor [FIToFICstmrCdtTrf."
														+ "CdtTrfTxInf.Cdtr.Id.PrvtId.Othr.SchmeNm] es nulo"
												        .toUpperCase();
												iso.setISO_039_ResponseCode("905");
												iso.setISO_039p_ResponseDetail(campo);
												return iso;
											}
											
										}else{
											
											campo = "Identidicacion Cuentahabiente (Persona Natural) receptor [FIToFICstmrCdtTrf."
													+ "CdtTrfTxInf.Cdtr.Id.PrvtId.Othr.id] es nulo"
											        .toUpperCase();
											iso.setISO_039_ResponseCode("905");
											iso.setISO_039p_ResponseDetail(campo);
											return iso;
										}
										
										
									}else{
										
										campo = "Estrucura Cuentahabiente (Persona Natural) receptor [FIToFICstmrCdtTrf."
												+ "CdtTrfTxInf.Cdtr.Id.PrvtId.Othr] es nulo"
										        .toUpperCase();
										iso.setISO_039_ResponseCode("905");
										iso.setISO_039p_ResponseDetail(campo);
										return iso;
									}
									
								}else{
									
									campo = "Info Datos Cuentahabiente receptor (Natural o Juridica) [FIToFICstmrCdtTrf."
											+ "CdtTrfTxInf.Cdtr.Id] es nula"
									        .toUpperCase();
									iso.setISO_039_ResponseCode("905");
									iso.setISO_039p_ResponseDetail(campo);
									return iso;
								}						
							}
							
						}else{
							
							campo = "Estrucura Cuentahabiente receptor [FIToFICstmrCdtTrf."
									+ "CdtTrfTxInf.Cdtr.Id] es nula"
							        .toUpperCase();
							iso.setISO_039_ResponseCode("905");
							iso.setISO_039p_ResponseDetail(campo);
							return iso;
						}
							
						}else{
							
							campo = "Nombres del Cuentahabiente Receptor [FIToFICstmrCdtTrf"
									+ ".CdtTrfTxInf.Cdtr.Nm] son nulos o vacios"
							        .toUpperCase();
							iso.setISO_039_ResponseCode("905");
							iso.setISO_039p_ResponseDetail(campo);
							iso.setISO_104_TranDescription("BE21");
							return iso;
						}
						
					}else{
						
						campo = "Estructura informacion Cuentahabiente receptor "
								+ "[FIToFICstmrCdtTrf.CdtTrfTxInf.Cdtr] es nulo"
						        .toUpperCase();
						iso.setISO_039_ResponseCode("905");
						iso.setISO_039p_ResponseDetail(campo);
						return iso;
					}
					
					///SUBSTRUCTURA CdtrAcct (DATOS DE LA CUENTA ACREDITANTE EN LA EFI AUTORIZADORA)
					if(documentoDeposito.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
							.get(0).getCdtrAcct() != null){
						
						if(documentoDeposito.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
								.get(0).getCdtrAcct().getId() != null){
							
							if(documentoDeposito.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
									.get(0).getCdtrAcct().getId().getOthr() != null){
								
								if(!StringUtils.IsNullOrEmpty(documentoDeposito.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
									.get(0).getCdtrAcct().getId().getOthr().getId())){
									
									iso.setISO_102_AccountID_1(documentoDeposito.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
											.get(0).getCdtrAcct().getId().getOthr().getId());
									
								}else {
									
									campo = "Numero de Cuenta a Acreditar [FIToFICstmrCdtTrf."
											+ "CdtTrfTxInf.CdtrAcct.Id.Othr.Id] es nula o vacia"
									        .toUpperCase();
									iso.setISO_039_ResponseCode("905");
									iso.setISO_039p_ResponseDetail(campo);
									iso.setISO_104_TranDescription("AC01");
									return iso;
								}
								
							}else{
								
								campo = "Numero de Cuenta a Acreditar [FIToFICstmrCdtTrf."
										+ "CdtTrfTxInf.CdtrAcct.Id.Othr] es nulo"
								        .toUpperCase();
								iso.setISO_039_ResponseCode("905");
								iso.setISO_039p_ResponseDetail(campo);
								iso.setISO_104_TranDescription("AC01");
								return iso;
							}
							
						}else{
							
							campo = "Numero de Cuenta a Acreditar [FIToFICstmrCdtTrf."
									+ "CdtTrfTxInf.CdtrAcct.Id] es nulo"
							        .toUpperCase();
							iso.setISO_039_ResponseCode("905");
							iso.setISO_039p_ResponseDetail(campo);
							iso.setISO_104_TranDescription("AC01");
							return iso;
						}
						if(documentoDeposito.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
								.get(0).getCdtrAcct().getTp() != null){
							
							if(!StringUtils.IsNullOrEmpty(documentoDeposito.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
									.get(0).getCdtrAcct().getTp().getPrtry())){
								
								if(documentoDeposito.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
										.get(0).getCdtrAcct().getTp().getPrtry().equals("01")){
									iso.setISO_003_ProcessingCode("202000");
								}else if (documentoDeposito.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
										.get(0).getCdtrAcct().getTp().getPrtry().equals("02")) {
									iso.setISO_003_ProcessingCode("201000");
								}else {
									
									campo = "Tipo de la Cuenta Acreditante [FIToFICstmrCdtTrf."
											+ "CdtTrfTxInf.CdtrAcct.Tp] es nula"
									        .toUpperCase();
									iso.setISO_039_ResponseCode("905");
									iso.setISO_039p_ResponseDetail(campo);
									return iso;
								}
								
							}else {
								
								campo = "Tipo de la Cuenta Acreditante [FIToFICstmrCdtTrf."
										+ "CdtTrfTxInf.CdtrAcct.Tp.Prtry] no es valida"
								        .toUpperCase();
								iso.setISO_039_ResponseCode("905");
								iso.setISO_039p_ResponseDetail(campo);
								return iso;
							}
							
						}else {
							
							campo = "Tipo de la Cuenta Acreditante [FIToFICstmrCdtTrf"
									+ ".CdtTrfTxInf.CdtrAcct.Tp] es nula"
							        .toUpperCase();
							iso.setISO_039_ResponseCode("905");
							iso.setISO_039p_ResponseDetail(campo);
							return iso;
						}
						if(!StringUtils.IsNullOrEmpty(documentoDeposito.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
								.get(0).getCdtrAcct().getCcy())){
							
							iso.setISO_121_ExtendedData(documentoDeposito.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
								.get(0).getCdtrAcct().getCcy());
							
						}else {
							
							campo = "Moneda de la cuenta Acreditante [FIToFICstmrCdtTrf."
									+ "CdtTrfTxInf.CdtrAcct.Ccy] es nulo"
							        .toUpperCase();
							iso.setISO_039_ResponseCode("905");
							iso.setISO_039p_ResponseDetail(campo);
							iso.setISO_104_TranDescription("AC09");
							return iso;
						}
						
					}else{
						
						campo = "Moneda de la cuenta Acreditante [FIToFICstmrCdtTrf."
								+ "CdtTrfTxInf.CdtrAcct] es nulo"
						        .toUpperCase();
						iso.setISO_039_ResponseCode("905");
						iso.setISO_039p_ResponseDetail(campo);
						iso.setISO_104_TranDescription("AC09");
						return iso;
					}
					///SUBSTRUCTURA InstrForNxtAgt (DATOS DE LA VENTANILLA CAJERO)
					
					if(documentoDeposito.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
							.get(0).getInstrForNxtAgt() != null){
						if(documentoDeposito.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
							.get(0).getInstrForNxtAgt().get(0).getInstrInf() != null){
							
							if(!StringUtils.IsNullOrEmpty(documentoDeposito.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
							.get(0).getInstrForNxtAgt().get(0).getInstrInf())){
								
								iso.setISO_042_Card_Acc_ID_Code(documentoDeposito.getFIToFICstmrCdtTrf()
										.getCdtTrfTxInf()
										.get(0).getInstrForNxtAgt().get(0).getInstrInf());
								
							}else{
								
								campo = "Info Caja EFI Ordenante [FIToFICstmrCdtTrf.CdtTrfTxInf"
										+ ".InstrForNxtAgt.InstrInf] es nulo o vacio"
								        .toUpperCase();
								iso.setISO_039_ResponseCode("905");
								iso.setISO_039p_ResponseDetail(campo);
								return iso;
							}
							
						}else{
							
							campo = "Info Caja EFI Ordenante [FIToFICstmrCdtTrf.CdtTrfTxInf"
									+ ".InstrForNxtAgt.InstrInf] es nulo"
							        .toUpperCase();
							iso.setISO_039_ResponseCode("905");
							iso.setISO_039p_ResponseDetail(campo);
							return iso;
						}
						
					}else{
						
						campo = "Info Caja EFI Ordenante [FIToFICstmrCdtTrf"
								+ ".CdtTrfTxInf.InstrForNxtAgt] es nula"
						        .toUpperCase();
						iso.setISO_039_ResponseCode("905");
						iso.setISO_039p_ResponseDetail(campo);
						return iso;
					}
					
					////////////
					iso.setISO_000_Message_Type("1200");
					iso.setISO_018_MerchantType("0005");
					iso.setISO_024_NetworkId("555557");
					iso.setISO_039_ResponseCode("000");
					
				}else {
					
					campo = "Region [FIToFICstmrCdtTrf.GrpHdr] es nula"
							.toUpperCase();
					iso.setISO_039_ResponseCode("905");
					iso.setISO_039p_ResponseDetail(campo);
					return iso;
				}
				
			}else{
				
				iso.setISO_039_ResponseCode("905");
				iso.setISO_039p_ResponseDetail("TRAMA DEPOSITO ES NULA");
				return iso;
			}
			
		} catch (Exception e) {
			
			log.WriteLogMonitor("Error modulo BceVCParser::parseDocumentDepositoVC ", TypeMonitor.error, e);
			iso.setISO_039_ResponseCode("905");
			iso.setISO_039p_ResponseDetail("ERROR " + campo + " " + GeneralUtils.ExceptionToString("INVALIDO ", e, true));
		}
		
		return iso;
	}
    public Iso8583 parseDocumentRetiroVC(DocumentRetiro documentRetiro){
		
		Iso8583 iso = new Iso8583();
		String campo = null;
		try {
			
			if(documentRetiro != null){
				
			    ///ESTRUCTURA GRPHDR
				if(documentRetiro.getFIToFICstmrDrctDbt().getGrpHdr() != null){
					
					if(!StringUtils.IsNullOrEmpty(documentRetiro.getFIToFICstmrDrctDbt().getGrpHdr().getMsgId())){
						
						if(documentRetiro.getFIToFICstmrDrctDbt().getGrpHdr().getMsgId().length() != 32){
							campo = "Campo [FIToFICstmrDrctDbt.GrpHdr.MsgId] es invalido"
							        .toUpperCase();
							iso.setISO_039_ResponseCode("905");
							iso.setISO_039p_ResponseDetail(campo);
							iso.setISO_104_TranDescription("RC02");
							return iso;
							
						}else
							iso.setISO_011_SysAuditNumber(documentRetiro.getFIToFICstmrDrctDbt().getGrpHdr().getMsgId());
					}else{
						campo = "Valor unico de la transaccion [FIToFICstmrDrctDbt.GrpHdr.MsgId] es nulo o vacio"
						        .toUpperCase();
						iso.setISO_039_ResponseCode("905");
						iso.setISO_039p_ResponseDetail(campo);
						iso.setISO_104_TranDescription("RC02");
						return iso;
					}
					if(documentRetiro.getFIToFICstmrDrctDbt().getGrpHdr().getCreDtTm() != null){
						
						campo = "Fecha Transaccion [FIToFICstmrDrctDbt.GrpHdr.CreDtTm] es nulo"
						        .toUpperCase();
						Date date = documentRetiro.getFIToFICstmrDrctDbt().getGrpHdr()
								.getCreDtTm().toGregorianCalendar().getTime();
						
						if(date.before(new Date())){
							iso.setISO_012_LocalDatetime(date);	
						}else{
							campo = "Fecha Transaccion [FIToFICstmrDrctDbt.GrpHdr.CreDtTm] es invalido"
							        .toUpperCase();
							iso.setISO_039_ResponseCode("905");
							iso.setISO_039p_ResponseDetail(campo);
							iso.setISO_104_TranDescription("DT01");
							return iso;
						}
						
					}else{
						campo = "Estructura FIToFICstmrDrctDbt.GrpHdr.CreDtTm es nula"
						        .toUpperCase();
						iso.setISO_039_ResponseCode("905");
						iso.setISO_039p_ResponseDetail(campo);
						iso.setISO_104_TranDescription("DT01");
						return iso;
					}
					if(!StringUtils.IsNullOrEmpty(documentRetiro.getFIToFICstmrDrctDbt().getGrpHdr().getNbOfTxs())){
						
						campo = "Numero de Transacciones no es numerico"
						        .toUpperCase();
						int numberTrx = Integer.parseInt(documentRetiro.getFIToFICstmrDrctDbt()
								        .getGrpHdr().getNbOfTxs());
						
						if(numberTrx > 0 && numberTrx <= 1){
							
							iso.setISO_022_PosEntryMode(String.valueOf(numberTrx));
							
						}else{
							
							campo = "Numero de Transacciones [FIToFICstmrCdtTrf.GrpHdr.NbOfTxs] invalido, "
									+ "(No se soporta mas de una transaccion)"
							        .toUpperCase();
							iso.setISO_039_ResponseCode("905");
							iso.setISO_039p_ResponseDetail(campo);
							iso.setISO_104_TranDescription("AM19");
							return iso;
						}
						
					}else {
						
						campo = "Numero de Transacciones [FIToFICstmrCdtTrf.GrpHdr.NbOfTxs] es nulo o vacio"
						        .toUpperCase();
						iso.setISO_039_ResponseCode("905");
						iso.setISO_039p_ResponseDetail(campo);
						iso.setISO_104_TranDescription("AM19");
						return iso;
					}
					///Usuario Codigo del Cajero
					if(documentRetiro.getFIToFICstmrDrctDbt().getGrpHdr().getAuthstn()!= null){
						
						campo = "Campo FIToFICstmrDrctDbt/GrpHdr/Authstn ";
						if(!StringUtils.IsNullOrEmpty(documentRetiro.getFIToFICstmrDrctDbt().getGrpHdr().
								getAuthstn().get(0).getPrtry())){
							iso.setISO_019_AcqCountryCode(documentRetiro.getFIToFICstmrDrctDbt().getGrpHdr().
									getAuthstn().get(0).getPrtry());
						}else{
							
							campo = "Codigo del cajero [FIToFICstmrDrctDbt.GrpHdr.Authstn.Prtry(0)] es nulo o vacio"
							        .toUpperCase();
							iso.setISO_039_ResponseCode("905");
							iso.setISO_039p_ResponseDetail(campo);
							return iso;
						}
						//OTP TRANSACCION
						if(!StringUtils.IsNullOrEmpty(documentRetiro.getFIToFICstmrDrctDbt().getGrpHdr().
								getAuthstn().get(1).getPrtry())){
							iso.setISO_052_PinBlock(documentRetiro.getFIToFICstmrDrctDbt().getGrpHdr().
									getAuthstn().get(1).getPrtry());
						}else{
							
							campo = "OTP Transaccion [FIToFICstmrDrctDbt.GrpHdr.Authstn.Prtry(1)] es nulo o vacio"
							        .toUpperCase();
							iso.setISO_039_ResponseCode("905");
							iso.setISO_039p_ResponseDetail(campo);
							return iso;
						}
			
						
					}else{
						campo = "Codigo del cajero [FIToFICstmrDrctDbt.GrpHdr.Authstn] es nulo"
						        .toUpperCase();
						iso.setISO_039_ResponseCode("905");
						iso.setISO_039p_ResponseDetail(campo);
						return iso;
					}
					
					if(documentRetiro.getFIToFICstmrDrctDbt().getGrpHdr().getCtrlSum() != null){
						
						campo = "Monto de la Transaccion [FIToFICstmrDrctDbt.GrpHdr.CtrlSum] debe ser mayor a 0"
								.toUpperCase();
						Double monto = documentRetiro.getFIToFICstmrDrctDbt().getGrpHdr()
								       .getCtrlSum().doubleValue();
						if(monto > 0){
							
							iso.setISO_004_AmountTransaction(monto);
							
						}else {
							
							iso.setISO_039_ResponseCode("905");
							iso.setISO_039p_ResponseDetail(campo);
							iso.setISO_104_TranDescription("AM01");
							return iso;
						}
						
					}else{
						
						campo = "Monto de la Transaccion [FIToFICstmrDrctDbt.GrpHdr.CtrlSum] es nulo"
						        .toUpperCase();
						iso.setISO_039_ResponseCode("905");
						iso.setISO_039p_ResponseDetail(campo);
						return iso;
					}
					
					if(documentRetiro.getFIToFICstmrDrctDbt().getGrpHdr().getSttlmInf().getSttlmMtd() != null){
						
						campo = "Campo [FIToFICstmrDrctDbt.GrpHdr.SttlmInf.SttlmMtd] es invalido"
						        .toUpperCase();
						if(documentRetiro.getFIToFICstmrDrctDbt().getGrpHdr().getSttlmInf().getSttlmMtd()
								.value().equalsIgnoreCase("CLRG")){
							iso.setISO_114_ExtendedData("CLRG");
							
							if(documentRetiro.getFIToFICstmrDrctDbt().getGrpHdr().
									getSttlmInf().getClrSys() != null){
								
								if(!StringUtils.IsNullOrEmpty(documentRetiro.getFIToFICstmrDrctDbt().getGrpHdr().
									getSttlmInf().getClrSys().getCd())){
									
									iso.setISO_114_ExtendedData(iso.getISO_114_ExtendedData() + "|" + 
												documentRetiro.getFIToFICstmrDrctDbt().getGrpHdr().
												getSttlmInf().getClrSys().getCd());
									
								}else{
									
									campo = "Campo [FIToFICstmrDrctDbt.GrpHdr.SttlmInf.ClrSys.Cd] es nulo o vacio"
									        .toUpperCase();
									iso.setISO_039_ResponseCode("905");
									iso.setISO_039p_ResponseDetail(campo);
									return iso;
								}
								
							}else{
								
								campo = "Estructura [FIToFICstmrDrctDbt.GrpHdr.SttlmInf.ClrSys] es nula"
								        .toUpperCase();
								iso.setISO_039_ResponseCode("905");
								iso.setISO_039p_ResponseDetail(campo);
								return iso;
							}
							
						}else {
							
							iso.setISO_039_ResponseCode("905");
							iso.setISO_039p_ResponseDetail(campo);
							return iso;
						}
	
					}else{
						
						campo = "Estructura [FIToFICstmrDrctDbt.GrpHdr.SttlmInf.SttlmMtd] es nula"
						        .toUpperCase();
						iso.setISO_039_ResponseCode("905");
						iso.setISO_039p_ResponseDetail(campo);
						return iso;
					}
					
					if(documentRetiro.getFIToFICstmrDrctDbt().getGrpHdr().getInstgAgt()
							.getFinInstnId() != null){
						
						if(!StringUtils.IsNullOrEmpty(documentRetiro.getFIToFICstmrDrctDbt().getGrpHdr().getInstgAgt()
								.getFinInstnId().getBICFI())){
							
							iso.setISO_032_ACQInsID(documentRetiro.getFIToFICstmrDrctDbt().getGrpHdr().getInstgAgt()
									.getFinInstnId().getBICFI());
							
						}else{
							
							campo = "Entidad ordenante [FIToFICstmrDrctDbt.GrpHdr.InstgAgt.FinInstnId.getBICFI] es nula o vacia"
							        .toUpperCase();
							iso.setISO_039_ResponseCode("905");
							iso.setISO_039p_ResponseDetail(campo);
							iso.setISO_104_TranDescription("AGNT");
							return iso;
						}
						
					}else {
						
						campo = "Estructura Entidad ordenante [FIToFICstmrDrctDbt.GrpHdr.InstgAgt.FinInstnId] es nula"
						        .toUpperCase();
						iso.setISO_039_ResponseCode("905");
						iso.setISO_039p_ResponseDetail(campo);
						iso.setISO_104_TranDescription("AGNT");
						return iso;
					}
					if(documentRetiro.getFIToFICstmrDrctDbt().getGrpHdr().getInstgAgt()
							.getBrnchId() != null){
						
						if(!StringUtils.IsNullOrEmpty(documentRetiro.getFIToFICstmrDrctDbt().getGrpHdr().getInstgAgt()
							.getBrnchId().getId())){
							
							iso.setISO_041_CardAcceptorID(documentRetiro.getFIToFICstmrDrctDbt().getGrpHdr()
									.getInstgAgt()
									.getBrnchId().getId());
							
						}else{
							
							campo = "Agencia Entidad Ordenante [FIToFICstmrDrctDbt().GrpHdr.InstgAgt.BrnchId.Id] es nula"
							        .toUpperCase();
							iso.setISO_039_ResponseCode("905");
							iso.setISO_039p_ResponseDetail(campo);
							iso.setISO_104_TranDescription("AGNT");
							return iso;
						}
						
					}else{
						
						campo = "Estructura Agencia Entidad Ordenante [FIToFICstmrDrctDbt.GrpHdr.InstgAgt.BrnchId] es nula"
						        .toUpperCase();
						iso.setISO_039_ResponseCode("905");
						iso.setISO_039p_ResponseDetail(campo);
						iso.setISO_104_TranDescription("AGNT");
						return iso;
					}
					if(documentRetiro.getFIToFICstmrDrctDbt().getGrpHdr().getInstdAgt()
							.getFinInstnId() != null){
						
						if(!StringUtils.IsNullOrEmpty(documentRetiro.getFIToFICstmrDrctDbt().getGrpHdr().getInstdAgt()
								.getFinInstnId().getBICFI())){
							
							iso.setISO_BitMap(documentRetiro.getFIToFICstmrDrctDbt().getGrpHdr().getInstdAgt()
									.getFinInstnId().getBICFI());
							
						}else{
							
							campo = "Entidad Intermedia [FIToFICstmrCdtTrf.GrpHdr.InstdAgt.FinInstnId.BICFI] es nula o vacia"
							        .toUpperCase();
							iso.setISO_039_ResponseCode("905");
							iso.setISO_039p_ResponseDetail(campo);
							iso.setISO_104_TranDescription("RC11");
							return iso;
						}
							
					}else {
						
						campo = "Estructura Entidad Intermedia [FIToFICstmrCdtTrf.GrpHdr.InstdAgt.FinInstnId] es nula"
						        .toUpperCase();
						iso.setISO_039_ResponseCode("905");
						iso.setISO_039p_ResponseDetail(campo);
						iso.setISO_104_TranDescription("RC11");
						return iso;
					}
					////////// ESTRUCTURA DRCDBTTXINF[ARREGLO]
					
					if(documentRetiro.getFIToFICstmrDrctDbt().getDrctDbtTxInf().get(0).getPmtId() != null){
						
						campo = "Numero de Papeleta [FIToFICstmrDrctDbt.DrctDbtTxInf.PmtId.EndToEndId] es nula"
						        .toUpperCase();
						if(!StringUtils.IsNullOrEmpty(documentRetiro.getFIToFICstmrDrctDbt().getDrctDbtTxInf()
								.get(0).getPmtId().getEndToEndId())){
							
							iso.setISO_023_CardSeq(documentRetiro.getFIToFICstmrDrctDbt().getDrctDbtTxInf()
								.get(0).getPmtId().getEndToEndId());
							
						}else {
							
							iso.setISO_039_ResponseCode("905");
							iso.setISO_039p_ResponseDetail(campo);
							iso.setISO_104_TranDescription("FF08");
							return iso;
						}
						
						if(!StringUtils.IsNullOrEmpty(documentRetiro.getFIToFICstmrDrctDbt().getDrctDbtTxInf()
								.get(0).getPmtId().getTxId())){
							
							campo = "Estructura Numero unico Trx.(Entidad Ordenante) [FIToFICstmrDrctDbt.DrctDbtTxInf.PmtId.TxId] es nula"
							        .toUpperCase();
							iso.setISO_037_RetrievalReferenceNumber(documentRetiro.getFIToFICstmrDrctDbt().getDrctDbtTxInf()
								.get(0).getPmtId().getTxId());	
							
						}else {
							
							iso.setISO_039_ResponseCode("905");
							iso.setISO_039p_ResponseDetail(campo);
							iso.setISO_104_TranDescription("RC04");
							return iso;
						}
						if(!StringUtils.IsNullOrEmpty(documentRetiro.getFIToFICstmrDrctDbt().getDrctDbtTxInf()
								.get(0).getPmtId().getClrSysRef())){
							iso.setISO_090_OriginalData(documentRetiro.getFIToFICstmrDrctDbt().getDrctDbtTxInf()
								.get(0).getPmtId().getClrSysRef());
							
						}else{
							
							campo = "Campo [FIToFICstmrDrctDbt.DrctDbtTxInf.PmtId.ClrSysRef] es nulo o vacio"
							        .toUpperCase();
							iso.setISO_039_ResponseCode("905");
							iso.setISO_039p_ResponseDetail(campo);
							return iso;
						}
						
					}else{
						
						campo = "Estructura [FIToFICstmrDrctDbt.DrctDbtTxInf.PmtId] es nula"
						        .toUpperCase();
						iso.setISO_039_ResponseCode("905");
						iso.setISO_039p_ResponseDetail(campo);
						return iso;
					}
					if(documentRetiro.getFIToFICstmrDrctDbt().getDrctDbtTxInf()
							.get(0).getIntrBkSttlmAmt() != null){
						
						if(!StringUtils.IsNullOrEmpty(documentRetiro.getFIToFICstmrDrctDbt().getDrctDbtTxInf()
								.get(0).getIntrBkSttlmAmt().getCcy())){
							
							campo = "Moneda de la Transaccion [FIToFICstmrDrctDbt.DrctDbtTxInf.IntrBkSttlmAmt.Ccy] es vacia o nula"
							        .toUpperCase();
							iso.setISO_049_TranCurrCode(documentRetiro.getFIToFICstmrDrctDbt().getDrctDbtTxInf()
								.get(0).getIntrBkSttlmAmt().getCcy().equalsIgnoreCase("USD")?840:0);
							
						}else{
							
							iso.setISO_039_ResponseCode("905");
							iso.setISO_039p_ResponseDetail(campo);
							iso.setISO_104_TranDescription("AC09");
							return iso;
						}
						double val = documentRetiro.getFIToFICstmrDrctDbt().getDrctDbtTxInf()
								.get(0).getIntrBkSttlmAmt().getValue().doubleValue();
						if(val > 0){
							
							iso.setISO_006_BillAmount(val);
							
						}else {
							
							campo = "Monto de la Transaccion [FIToFICstmrDrctDbt.DrctDbtTxInf"
									+ ".IntrBkSttlmAmt.Value] es vacia o nula"
							        .toUpperCase();
							iso.setISO_039_ResponseCode("905");
							iso.setISO_039p_ResponseDetail(campo);
							iso.setISO_104_TranDescription("AM01");
							return iso;
						}
					
						
					}else{
						
						campo = "Estructura [FIToFICstmrDrctDbt.DrctDbtTxInf.IntrBkSttlmAmt] es nula"
						        .toUpperCase();
						iso.setISO_039_ResponseCode("905");
						iso.setISO_039p_ResponseDetail(campo);
						return iso;
					}
					//Fecha Contable de la transaccion
					/*if(documentRetiro.getFIToFICstmrDrctDbt().getDrctDbtTxInf()
							.get(0).getIntrBkSttlmDt() != null){
						
						Date dateBusiness = documentRetiro.getFIToFICstmrDrctDbt().getDrctDbtTxInf()
								.get(0).getIntrBkSttlmDt().toGregorianCalendar().getTime();
						
						if(dateBusiness.before(new Date())){
							
							iso.setISO_015_SettlementDatel(dateBusiness);	
						}else{
							campo = "Fecha Contable de la Trx. [FIToFICstmrCdtTrf.CdtTrfTxInf.IntrBkSttlmDt] es invalido"
							        .toUpperCase();
							iso.setISO_039_ResponseCode("905");
							iso.setISO_039p_ResponseDetail(campo);
							return iso;
						}
					}else{
						
						campo = "Fecha Contable de la Trx. [FIToFICstmrCdtTrf.CdtTrfTxInf.IntrBkSttlmDt] es nula"
						        .toUpperCase();
						iso.setISO_039_ResponseCode("905");
						iso.setISO_039p_ResponseDetail(campo);
						return iso;
					}*/
					if(documentRetiro.getFIToFICstmrDrctDbt().getDrctDbtTxInf()
							.get(0).getChrgBr() != null){
						
						iso.setISO_035_Track2(documentRetiro.getFIToFICstmrDrctDbt().getDrctDbtTxInf()
								.get(0).getChrgBr().value());
						
					}else {
						campo = "Estructura [FIToFICstmrDrctDbt.DrctDbtTxInf.ChrgBr] es nula"
						        .toUpperCase();
						iso.setISO_039_ResponseCode("905");
						iso.setISO_039p_ResponseDetail(campo);
						return iso;
					}
					///SUBSESTRUCTURA CDTR (DATOS DE LA PERSONA QUIEN ESTA REALIZANDO EL RETIRO) 
					if(documentRetiro.getFIToFICstmrDrctDbt().getDrctDbtTxInf()
							.get(0).getCdtr() != null){
						
						if(!StringUtils.IsNullOrEmpty(documentRetiro.getFIToFICstmrDrctDbt().getDrctDbtTxInf()
								.get(0).getCdtr().getNm())){
							
							campo = "Nombres del depositante [FIToFICstmrDrctDbt.DrctDbtTxInf.Cdtr.Nm] es nulo o vacio"
							        .toUpperCase();
							iso.setISO_034_PANExt(documentRetiro.getFIToFICstmrDrctDbt().getDrctDbtTxInf()
									.get(0).getCdtr().getNm());
							
						}else{
							
							iso.setISO_039_ResponseCode("905");
							iso.setISO_039p_ResponseDetail(campo);
							iso.setISO_104_TranDescription("BE21");
							return iso;
						}
						if(documentRetiro.getFIToFICstmrDrctDbt().getDrctDbtTxInf()
								.get(0).getCdtr().getId() != null){
							if(documentRetiro.getFIToFICstmrDrctDbt().getDrctDbtTxInf()
									.get(0).getCdtr().getId().getPrvtId() != null){
								
								if(documentRetiro.getFIToFICstmrDrctDbt().getDrctDbtTxInf()
										.get(0).getCdtr().getId().getPrvtId().getOthr() != null){
									if(!StringUtils.IsNullOrEmpty(documentRetiro.getFIToFICstmrDrctDbt().getDrctDbtTxInf()
											.get(0).getCdtr().getId().getPrvtId().getOthr().get(0).getId())){
										
										iso.setISO_002_PAN(documentRetiro.getFIToFICstmrDrctDbt().getDrctDbtTxInf()
												.get(0).getCdtr().getId().getPrvtId().getOthr().get(0).getId());
										
										if(documentRetiro.getFIToFICstmrDrctDbt().getDrctDbtTxInf()
												.get(0).getCdtr().getId().getPrvtId().getOthr()
												.get(0).getSchmeNm() != null){
											if(!StringUtils.IsNullOrEmpty(documentRetiro.getFIToFICstmrDrctDbt().getDrctDbtTxInf()
													.get(0).getCdtr().getId().getPrvtId().getOthr()
													.get(0).getSchmeNm().getCd())){
												
												iso.setISO_055_EMV(documentRetiro.getFIToFICstmrDrctDbt().getDrctDbtTxInf()
														.get(0).getCdtr().getId().getPrvtId().getOthr()
													.get(0).getSchmeNm().getCd());
												
												if(documentRetiro.getFIToFICstmrDrctDbt().getDrctDbtTxInf()
												.get(0).getCdtr().getCtctDtls() != null){
													
													if(!StringUtils.IsNullOrEmpty(documentRetiro.getFIToFICstmrDrctDbt().getDrctDbtTxInf()
															.get(0).getCdtr().getCtctDtls().getMobNb())){
														iso.setISO_036_Track3(documentRetiro.getFIToFICstmrDrctDbt().getDrctDbtTxInf()
															.get(0).getCdtr().getCtctDtls().getMobNb());
														
														
													}else{
														
														campo = "Campo [FIToFICstmrDrctDbt.DrctDbtTxInf.Cdtr"
																+ ".CtctDtlsMobNb] es nulo o vacio"
														        .toUpperCase();
														iso.setISO_039_ResponseCode("905");
														iso.setISO_039p_ResponseDetail(campo);
														return iso;
													}
													
												}else{
													
													campo = "Estructura [FIToFICstmrDrctDbt.DrctDbtTxInf.Cdtr"
															+ ".CtctDtls] es nula"
													        .toUpperCase();
													iso.setISO_039_ResponseCode("905");
													iso.setISO_039p_ResponseDetail(campo);
													return iso;
												}
												
											}else {
												
												campo = "Tipo de Identificacion de la persona que realiza el retiro "
														+ "[FIToFICstmrDrctDbt.DrctDbtTxInf.Cdtr.Id.PrvtId.Othr."
														+ "SchmeNm.Cd] es nulo o vacio"
												        .toUpperCase();
												iso.setISO_039_ResponseCode("905");
												iso.setISO_039p_ResponseDetail(campo);
												iso.setISO_104_TranDescription("BE15");
												return iso;
											}
											
										}else{
											
											campo = "Estructura Tipo Identificacion de la persona quien realiza el retiro"
													+ " [FIToFICstmrDrctDbt.DrctDbtTxInf.Cdtr.Id.PrvtId.Othr."
													+ "SchmeNm] es nulo"
											        .toUpperCase();
											iso.setISO_039_ResponseCode("905");
											iso.setISO_039p_ResponseDetail(campo);
											return iso;
										}
										
									}else{
										
										campo = "Nro. de Identificacion del Depositante [FIToFICstmrDrctDbt.DrctDbtTxInf"
												+ ".Cdtr.Id.PrvtId.Othr.Id] es nulo o vacio"
										        .toUpperCase();
										iso.setISO_039_ResponseCode("905");
										iso.setISO_039p_ResponseDetail(campo);
										return iso;
									}
									
								}else{
									campo = "Estructura Nro. de Identificacion del Depositante [FIToFICstmrDrctDbt"
											+ ".DrctDbtTxInf.Cdtr.Id.PrvtId.Othr] es nulo"
									        .toUpperCase();
									iso.setISO_039_ResponseCode("905");
									iso.setISO_039p_ResponseDetail(campo);
									return iso;
								}
								
							}else{
								
								//Implementacion de retiro de una persona natural por una cuenta juridica
								if(documentRetiro.getFIToFICstmrDrctDbt().getDrctDbtTxInf()
										.get(0).getCdtr().getId().getOrgId() != null){
									
									if(documentRetiro.getFIToFICstmrDrctDbt().getDrctDbtTxInf()
											.get(0).getCdtr().getId().getOrgId().getOthr() != null){
										
										if(!StringUtils.IsNullOrEmpty(documentRetiro.getFIToFICstmrDrctDbt().getDrctDbtTxInf()
												.get(0).getCdtr().getId().getOrgId().getOthr().get(0).getId())){
											
											iso.setISO_002_PAN(documentRetiro.getFIToFICstmrDrctDbt().getDrctDbtTxInf()
													.get(0).getCdtr().getId().getOrgId().getOthr().get(0).getId());
											if(documentRetiro.getFIToFICstmrDrctDbt().getDrctDbtTxInf()
													.get(0).getCdtr().getId().getOrgId().getOthr().get(0).getSchmeNm() != null){
												
												if(!StringUtils.IsNullOrEmpty(documentRetiro.getFIToFICstmrDrctDbt().getDrctDbtTxInf()
														.get(0).getCdtr().getId().getOrgId().getOthr().get(0).getSchmeNm().getCd())){
													if(!documentRetiro.getFIToFICstmrDrctDbt().getDrctDbtTxInf()
															.get(0).getCdtr().getId().getOrgId().getOthr().get(0).
															getSchmeNm().getCd().equalsIgnoreCase("TXID")){
														
														iso.setISO_055_EMV("TXID");
														
														/***************************Validacion UltimateDebitor****************************/
														
														if(documentRetiro.getFIToFICstmrDrctDbt().getDrctDbtTxInf().get(0)
																.getUltmtCdtr() != null){
															
															if(!StringUtils.IsNullOrEmpty(documentRetiro.getFIToFICstmrDrctDbt().getDrctDbtTxInf().get(0)
																	.getUltmtCdtr().getNm())){
																
																iso.setISO_122_ExtendedData(documentRetiro.getFIToFICstmrDrctDbt().getDrctDbtTxInf().get(0)
																	.getUltmtCdtr().getNm());
																if(documentRetiro.getFIToFICstmrDrctDbt().getDrctDbtTxInf().get(0)
																		.getUltmtCdtr().getId() != null){
																	
																	if(documentRetiro.getFIToFICstmrDrctDbt().getDrctDbtTxInf().get(0)
																			.getUltmtCdtr().getId().getPrvtId() != null){
																		
																		if(documentRetiro.getFIToFICstmrDrctDbt().getDrctDbtTxInf().get(0)
																				.getUltmtCdtr().getId().getPrvtId().getOthr() != null){
																			
																			if(!StringUtils.IsNullOrEmpty(documentRetiro.getFIToFICstmrDrctDbt().
																					 getDrctDbtTxInf().get(0)
																					.getUltmtCdtr().getId().getPrvtId().getOthr().get(0).getId())){
																				
																				if(documentRetiro.getFIToFICstmrDrctDbt().
																						getDrctDbtTxInf().get(0).getUltmtCdtr().getId().getPrvtId()
																						.getOthr().get(0).getSchmeNm()!=null){
																					
																					if(!StringUtils.IsNullOrEmpty(documentRetiro.getFIToFICstmrDrctDbt().
																							getDrctDbtTxInf().get(0).getUltmtCdtr().getId().getPrvtId()
																							.getOthr().get(0).getSchmeNm().getCd())){
																						
																						if(documentRetiro.getFIToFICstmrDrctDbt().
																								getDrctDbtTxInf().get(0).getUltmtCdtr().getId().getPrvtId()
																								.getOthr().get(0).getSchmeNm().getCd().equalsIgnoreCase("NIDN")
																						|| documentRetiro.getFIToFICstmrDrctDbt().
																							getDrctDbtTxInf().get(0).getUltmtCdtr().getId().getPrvtId()
																							.getOthr().get(0).getSchmeNm().getCd().equalsIgnoreCase("CCPT")){
																							
																							iso.setISO_122_ExtendedData(iso.getISO_122_ExtendedData() + "|" + documentRetiro.getFIToFICstmrDrctDbt().
																							getDrctDbtTxInf().get(0).getUltmtCdtr().getId().getPrvtId()
																							.getOthr().get(0).getSchmeNm().getCd());
																							
																							if(documentRetiro.getFIToFICstmrDrctDbt().
																									getDrctDbtTxInf().get(0).getUltmtCdtr().getCtctDtls() != null){
																								
																								if(!StringUtils.IsNullOrEmpty(documentRetiro.getFIToFICstmrDrctDbt().
																									getDrctDbtTxInf().get(0).getUltmtCdtr().getCtctDtls().getMobNb())){
																									
																									iso.setISO_122_ExtendedData(iso.getISO_122_ExtendedData() + "|" + 
																									        documentRetiro.getFIToFICstmrDrctDbt().
																											getDrctDbtTxInf().get(0).getUltmtCdtr().getCtctDtls().getMobNb());
																									
																								}else{
																									
																									campo = "Campo [FIToFICstmrDrctDbt.DrctDbtTxInf.Cdtr"
																											+ ".UltmtCdtr.CtctDtls.MobNb] (Numero movil Persona natural "
																											+ "representante) es incorrecto"
																									        .toUpperCase();
																									iso.setISO_039_ResponseCode("905");
																									iso.setISO_039p_ResponseDetail(campo);
																									return iso;
																								}
																								
																							}else{
																								
																								campo = "Estructura [FIToFICstmrDrctDbt.DrctDbtTxInf.Cdtr"
																										+ ".UltmtCdtr.CtctDtls] es nula"
																								        .toUpperCase();
																								iso.setISO_039_ResponseCode("905");
																								iso.setISO_039p_ResponseDetail(campo);
																								return iso;
																							}
																							
																						}else{
																							
																							campo = "Campo [FIToFICstmrDrctDbt.DrctDbtTxInf.Cdtr"
																									+ ".UltmtCdtr.Id.PrvtId.Othr.SchmeNm.Cd] (Tipo. Id Persona natural "
																									+ "representante) es incorrecto"
																							        .toUpperCase();
																							iso.setISO_039_ResponseCode("905");
																							iso.setISO_039p_ResponseDetail(campo);
																							iso.setISO_104_TranDescription("CH16");
																							return iso;
																						}
																						
																					}else{
																						
																						campo = "Campo [FIToFICstmrDrctDbt.DrctDbtTxInf.Cdtr"
																								+ ".UltmtCdtr.Id.PrvtId.Othr.SchmeNm.Cd] (Tipo. Id Persona natural "
																								+ "representante) es nula o vacia"
																						        .toUpperCase();
																						iso.setISO_039_ResponseCode("905");
																						iso.setISO_039p_ResponseDetail(campo);
																						return iso;
																					}
																					
																				}else{
																					
																					campo = "Estructura [FIToFICstmrDrctDbt.DrctDbtTxInf.Cdtr"
																							+ ".UltmtCdtr.Id.PrvtId.Othr.SchmeNm] es nula"
																					        .toUpperCase();
																					iso.setISO_039_ResponseCode("905");
																					iso.setISO_039p_ResponseDetail(campo);
																					return iso;
																				}
																				
																			}else{
																				
																				campo = "Campo [FIToFICstmrDrctDbt.DrctDbtTxInf.Cdtr"
																						+ ".UltmtCdtr.Id.PrvtId.Othr.Id] (Nro. Id Persona natural "
																						+ "representante) es nula o vacia"
																				        .toUpperCase();
																				iso.setISO_039_ResponseCode("905");
																				iso.setISO_039p_ResponseDetail(campo);
																				return iso;
																			}
																			
																		}else{
																			
																			campo = "Estructura [FIToFICstmrDrctDbt.DrctDbtTxInf.Cdtr"
																					+ ".UltmtCdtr.Id.PrvtId.Othr] es nulo"
																			        .toUpperCase();
																			iso.setISO_039_ResponseCode("905");
																			iso.setISO_039p_ResponseDetail(campo);
																			return iso;
																		}
																		
																	}else{
																		
																		campo = "Estructura [FIToFICstmrDrctDbt.DrctDbtTxInf.Cdtr"
																				+ ".UltmtCdtr.Id.PrvtId] es nulo"
																		        .toUpperCase();
																		iso.setISO_039_ResponseCode("905");
																		iso.setISO_039p_ResponseDetail(campo);
																		return iso;
																	}
																	
																}else{
																	
																	campo = "Estructura [FIToFICstmrDrctDbt.DrctDbtTxInf.Cdtr"
																			+ ".UltmtCdtr.Id] es nulo"
																	        .toUpperCase();
																	iso.setISO_039_ResponseCode("905");
																	iso.setISO_039p_ResponseDetail(campo);
																	return iso;
																}
																
															}else{
																
																campo = "Campo [FIToFICstmrDrctDbt.DrctDbtTxInf.Cdtr"
																		+ ".UltmtCdtr.Nm] es nulo o vacio"
																        .toUpperCase();
																iso.setISO_039_ResponseCode("905");
																iso.setISO_039p_ResponseDetail(campo);
																return iso;
															}
															
														}else{
															
															campo = "Estructura [FIToFICstmrDrctDbt.DrctDbtTxInf.Cdtr"
																	+ ".UltmtCdtr] es nulo"
															        .toUpperCase();
															iso.setISO_039_ResponseCode("905");
															iso.setISO_039p_ResponseDetail(campo);
															return iso;
														}
														
														/***************************End Validacion UltimateDebitor************************/
														
														
													}else{
														
														campo = "Estructura [FIToFICstmrDrctDbt.DrctDbtTxInf.Cdtr.Id."
																+ "OrgId.Othr.IdSchmeNm.Cd]"
																+ "(Tipo Id Cuentahabienye) es incorrecto"
														        .toUpperCase();
																iso.setISO_039_ResponseCode("905");
																iso.setISO_039p_ResponseDetail(campo);
																iso.setISO_104_TranDescription("CH16");
																return iso;
													}
													
												}else{
													
													campo = "Estructura [FIToFICstmrDrctDbt.DrctDbtTxInf.Cdtr.Id."
															+ "OrgId.Othr.IdSchmeNm.Cd] "
															+ "(Número Id Cuentahabienye) es nulo o vacío"
													        .toUpperCase();
															iso.setISO_039_ResponseCode("905");
															iso.setISO_039p_ResponseDetail(campo);
															return iso;
												}
												
											}else{
												
												campo = "Estructura [FIToFICstmrDrctDbt.DrctDbtTxInf.Cdtr.Id.OrgId"
														+ ".Othr.SchmeNm] es nula"
												        .toUpperCase();
														iso.setISO_039_ResponseCode("905");
														iso.setISO_039p_ResponseDetail(campo);
														return iso;
											}
											
										}else{
											
											campo = "Estructura [FIToFICstmrDrctDbt.DrctDbtTxInf.Cdtr.Id.OrgId.Othr.Id] "
													+ "(Número Id Cuentahabienye) es nulo o vacío"
											        .toUpperCase();
													iso.setISO_039_ResponseCode("905");
													iso.setISO_039p_ResponseDetail(campo);
													return iso;
										}
										
									}else{
										
										campo = "Estructura [FIToFICstmrDrctDbt.DrctDbtTxInf.Cdtr.Id.OrgId.Othr] es nula"
										        .toUpperCase();
												iso.setISO_039_ResponseCode("905");
												iso.setISO_039p_ResponseDetail(campo);
												return iso;
									}
									
								}else{
									
									campo = "Estructura [FIToFICstmrDrctDbt.DrctDbtTxInf.Cdtr.Id.PrvtId/OrgId] es nula"
							        .toUpperCase();
									iso.setISO_039_ResponseCode("905");
									iso.setISO_039p_ResponseDetail(campo);
									return iso;
								}
								
							}
							
						}else {
							campo = "Estructura identificacion de la persona que retira [FIToFICstmrDrctDbt"
									+ ".DrctDbtTxInf.Cdtr.Id] es nulo"
							        .toUpperCase();
							iso.setISO_039_ResponseCode("905");
							iso.setISO_039p_ResponseDetail(campo);
							return iso;
						}
						
						
					}else{
						
						campo = "Estructura Informacion de la persona que retira "
								+ "[FIToFICstmrDrctDbt.DrctDbtTxInf.Cdtr] es nula"
						        .toUpperCase();
						iso.setISO_039_ResponseCode("905");
						iso.setISO_039p_ResponseDetail(campo);
						return iso;
					}
					///SUBSTRUCTURA DBTRAGT (DATOS DE LA ENTIDAD ADQUIRIENTE)
					String efi_age = null;
					if(documentRetiro.getFIToFICstmrDrctDbt().getDrctDbtTxInf()
							.get(0).getCdtrAgt() != null){
						if(documentRetiro.getFIToFICstmrDrctDbt().getDrctDbtTxInf()
								.get(0).getCdtrAgt().getFinInstnId() != null){
							if(!StringUtils.IsNullOrEmpty(documentRetiro.getFIToFICstmrDrctDbt().getDrctDbtTxInf()
									.get(0).getCdtrAgt().getFinInstnId().getBICFI())){
								
								efi_age = documentRetiro.getFIToFICstmrDrctDbt().getDrctDbtTxInf()
										.get(0).getCdtrAgt().getFinInstnId().getBICFI();
							//Aqui validar el codigo BICFI de la entidad ordenante (frente a un catalogo de BICFIS) ojo
							if(documentRetiro.getFIToFICstmrDrctDbt().getDrctDbtTxInf()
									.get(0).getCdtrAgt().getBrnchId() != null){
								
								if(!StringUtils.IsNullOrEmpty(documentRetiro.getFIToFICstmrDrctDbt().getDrctDbtTxInf()
										.get(0).getCdtrAgt().getBrnchId().getId())){
									
									efi_age += "|" + documentRetiro.getFIToFICstmrDrctDbt().getDrctDbtTxInf()
											.get(0).getCdtrAgt().getBrnchId().getId();
									iso.setISO_120_ExtendedData(efi_age);
									
								}else{
									
									campo = "Agencia de la Entidad Ordentante [FIToFICstmrDrctDbt."
											+ "DrctDbtTxInf.CdtrAgt.BrnchId.Id] es vacio o nulo"
									        .toUpperCase();
									iso.setISO_039_ResponseCode("905");
									iso.setISO_039p_ResponseDetail(campo);
									iso.setISO_104_TranDescription("AGNT");
									return iso;
								}
								
							}else{
								
								campo = "Agencia de la Entidad Ordentante [FIToFICstmrDrctDbt."
										+ "DrctDbtTxInf.CdtrAgt.BrnchId] es nula"
								        .toUpperCase();
								iso.setISO_039_ResponseCode("905");
								iso.setISO_039p_ResponseDetail(campo);
								iso.setISO_104_TranDescription("AGNT");
								return iso;
							}
								
						}else {
							
							campo = "Codigo Entidad Ordentante [FIToFICstmrDrctDbt.DrctDbtTxInf"
									+ ".CdtrAgt.FinInstnId.BICFI] es nula"
							        .toUpperCase();
							iso.setISO_039_ResponseCode("905");
							iso.setISO_039p_ResponseDetail(campo);
							iso.setISO_104_TranDescription("AGNT");
							return iso;
						}
							
						}else{
							
							campo = "Estructura Entidad Ordentante [FIToFICstmrDrctDbt."
									+ "DrctDbtTxInf.CdtrAgt.FinInstnId] es nula"
							        .toUpperCase();
							iso.setISO_039_ResponseCode("905");
							iso.setISO_039p_ResponseDetail(campo);
							iso.setISO_104_TranDescription("AGNT");
							return iso;
						}
						
					}else{
						
						campo = "Estructura con informacion de la Entidad Ordenante "
								+ "[FIToFICstmrDrctDbt.DrctDbtTxInf.FinInstnId] es nula"
						        .toUpperCase();
						iso.setISO_039_ResponseCode("905");
						iso.setISO_039p_ResponseDetail(campo);
						iso.setISO_104_TranDescription("AGNT");
						return iso;
					}
					///SUBSTRUCTURA DBTRAGTACCT (NUMERO DE CUENTA DE LA EFI ADQUIRIENTE EN EL BCE)
					
					if(documentRetiro.getFIToFICstmrDrctDbt().getDrctDbtTxInf()
							.get(0).getCdtrAgtAcct() != null){
						
						if(documentRetiro.getFIToFICstmrDrctDbt().getDrctDbtTxInf()
								.get(0).getCdtrAgtAcct().getId() != null){
							if(documentRetiro.getFIToFICstmrDrctDbt().getDrctDbtTxInf()
									.get(0).getCdtrAgtAcct().getId().getOthr() != null){
								
								if(!StringUtils.IsNullOrEmpty(documentRetiro.getFIToFICstmrDrctDbt()
										.getDrctDbtTxInf()
										.get(0).getCdtrAgtAcct().getId().getOthr().getId())){
									
									iso.setISO_121_ExtendedData(documentRetiro.getFIToFICstmrDrctDbt()
											.getDrctDbtTxInf()
											.get(0).getCdtrAgtAcct().getId().getOthr().getId());
									
								}else{
									
									campo = "Numero de Cuenta de la Entidad Ordenante [FIToFICstmrDrctDbt."
											+ "DrctDbtTxInf.CdtrAgtAcct.Id.Othr.Id] es vacia o nula"
									        .toUpperCase();
									iso.setISO_039_ResponseCode("905");
									iso.setISO_039p_ResponseDetail(campo);
									iso.setISO_104_TranDescription("AC01");
									return iso;
								}
								
							}else{
								
								campo = "Numero de Cuenta de la Entidad Ordenante "
										+ "[FIToFICstmrDrctDbt.DrctDbtTxInf.CdtrAgtAcct.Id.Othr] es nulo"
								        .toUpperCase();
								iso.setISO_039_ResponseCode("905");
								iso.setISO_039p_ResponseDetail(campo);
								iso.setISO_104_TranDescription("AC01");
								return iso;
							}
							
						}else{
							
							campo = "Estructura Numero de Cuenta de la Entidad Ordenante "
									+ "[FIToFICstmrDrctDbt.DrctDbtTxInf.CdtrAgtAcct.Id] es nulo"
							        .toUpperCase();
							iso.setISO_039_ResponseCode("905");
							iso.setISO_039p_ResponseDetail(campo);
							iso.setISO_104_TranDescription("AC01");
							return iso;
						}
						
						if(documentRetiro.getFIToFICstmrDrctDbt().getDrctDbtTxInf()
								.get(0).getCdtrAgtAcct().getTp() != null){
							
							if(!StringUtils.IsNullOrEmpty(documentRetiro.getFIToFICstmrDrctDbt().getDrctDbtTxInf()
									.get(0).getCdtrAgtAcct().getTp().getPrtry())){
								
								iso.setISO_121_ExtendedData(iso.getISO_121_ExtendedData() + "|" +  
								        documentRetiro.getFIToFICstmrDrctDbt().getDrctDbtTxInf()
										.get(0).getCdtrAgtAcct().getTp().getPrtry());
								
							}else{
								
								campo = "Tipo de Cuenta de la entidad Ordenante "
										+ "[FIToFICstmrDrctDbt.DrctDbtTxInf.CdtrAgtAcct.Tp.Prtry] es nula o vacia"
								        .toUpperCase();
								iso.setISO_039_ResponseCode("905");
								iso.setISO_039p_ResponseDetail(campo);
								iso.setISO_104_TranDescription("AC12");
								return iso;
							}
							
						}else{
							
							campo = "Tipo de Cuenta de la entidad Ordenante [FIToFICstmrDrctDbt."
									+ "DrctDbtTxInf.CdtrAcct.Tp] es nula"
							        .toUpperCase();
							iso.setISO_039_ResponseCode("905");
							iso.setISO_039p_ResponseDetail(campo);
							iso.setISO_104_TranDescription("AC12");
							return iso;
						}
						
						if(!StringUtils.IsNullOrEmpty(documentRetiro.getFIToFICstmrDrctDbt().getDrctDbtTxInf()
								.get(0).getCdtrAgtAcct().getCcy())){
							
							iso.setISO_121_ExtendedData(iso.getISO_121_ExtendedData() + "|" +  
							        documentRetiro.getFIToFICstmrDrctDbt().getDrctDbtTxInf()
									.get(0).getCdtrAgtAcct().getCcy());
							
						}else{
							
							campo = "Cod. Moneda de la Cta. Efi Ordenante [FIToFICstmrDrctDbt."
									+ "DrctDbtTxInf.CdtrAcct.Ccy] es nulo o vacio"
							        .toUpperCase();
							iso.setISO_039_ResponseCode("905");
							iso.setISO_039p_ResponseDetail(campo);
							iso.setISO_104_TranDescription("AC09");
							return iso;
						}
						
					}else{
						
						campo = "Estructura informacion de la Cuenta del BCE de la "
								+ "EFI Ordentante [FIToFICstmrDrctDbt.DrctDbtTxInf.CdtrAcct] es nula"
						        .toUpperCase();
						iso.setISO_039_ResponseCode("905");
						iso.setISO_039p_ResponseDetail(campo);
						iso.setISO_104_TranDescription("AC01");
						return iso;
					}
					///SUBSTRUCTURA DBTRAGT (DATOS DE LA ENTIDAD AUTORIZADORA)
					
					if(documentRetiro.getFIToFICstmrDrctDbt().getDrctDbtTxInf()
							.get(0).getDbtrAgt() != null){
						
						if(documentRetiro.getFIToFICstmrDrctDbt().getDrctDbtTxInf()
								.get(0).getDbtrAgt().getFinInstnId() != null){
							
							if(!StringUtils.IsNullOrEmpty(documentRetiro.getFIToFICstmrDrctDbt().getDrctDbtTxInf()
									.get(0).getDbtrAgt().getFinInstnId().getBICFI())){
								
								if(documentRetiro.getFIToFICstmrDrctDbt().getDrctDbtTxInf()
										.get(0).getDbtrAgt().getFinInstnId().getBICFI()
										.equalsIgnoreCase(MemoryGlobal.BCE_Efi_VC)){
									
									iso.setISO_033_FWDInsID(MemoryGlobal.BCE_Efi_VC);
									
								}else{
									
									campo = "Cod. EFI Receptora [FIToFICstmrDrctDbt.DrctDbtTxInf."
											+ "DbtrAgt.FinInstnId.BICFI] "
											+ "no coincide con la asignada a nuestra institucion"
									        .toUpperCase();
									iso.setISO_039_ResponseCode("905");
									iso.setISO_039p_ResponseDetail(campo);
									return iso;
								}
								
							}else{
								
								campo = "Cod. EFI Receptora [FIToFICstmrDrctDbt.DrctDbtTxInf"
										+ ".DbtrAgt.FinInstnId.BICFI] es nula o vacia"
								        .toUpperCase();
								iso.setISO_039_ResponseCode("905");
								iso.setISO_039p_ResponseDetail(campo);
								return iso;
							}
							
						}else{
							
							campo = "Estructura EFI Receptora [FIToFICstmrDrctDbt."
									+ "DrctDbtTxInf.DbtrAgt.FinInstnId] es nulo"
							        .toUpperCase();
							iso.setISO_039_ResponseCode("905");
							iso.setISO_039p_ResponseDetail(campo);
							return iso;
						}
						
					}else{
						
						campo = "Estructura con informacion de la EFI Recepora "
								+ "[FIToFICstmrDrctDbt.DrctDbtTxInf.DbtrAgt] es nula"
						        .toUpperCase();
						iso.setISO_039_ResponseCode("905");
						iso.setISO_039p_ResponseDetail(campo);
						return iso;
					}
					
					///SUBSTRUCTURA Dbtr (DATOS DEL CUENTAHABIENTE (DEBITANTE) ENTIDAD AUTORIZADORA)
									
					if(documentRetiro.getFIToFICstmrDrctDbt().getDrctDbtTxInf()
							.get(0).getDbtr() != null){
						
						if(!StringUtils.IsNullOrEmpty(documentRetiro.getFIToFICstmrDrctDbt().getDrctDbtTxInf()
								.get(0).getDbtr().getNm())){
							
							if(documentRetiro.getFIToFICstmrDrctDbt().getDrctDbtTxInf()
									.get(0).getDbtr().getNm().length() > 10 && documentRetiro.getFIToFICstmrDrctDbt().getDrctDbtTxInf()
									.get(0).getDbtr().getNm().length() < 140){//validacion ordinaria
								iso.setISO_115_ExtendedData(documentRetiro.getFIToFICstmrDrctDbt().getDrctDbtTxInf()
										.get(0).getDbtr().getNm());
							}else{
								
								campo = "Longitud Nombre (Persona Juridica) receptor [FIToFICstmrDrctDbt."
										+ "Dbtr.DbtrCtctDtls.Nm"
										+ "es invalido"
								        .toUpperCase();
								iso.setISO_039_ResponseCode("905");
								iso.setISO_039p_ResponseDetail(campo);
								return iso;
							}
							
						if(documentRetiro.getFIToFICstmrDrctDbt().getDrctDbtTxInf()
								.get(0).getDbtr().getId() != null){
							
							if(documentRetiro.getFIToFICstmrDrctDbt().getDrctDbtTxInf()
									.get(0).getDbtr().getId().getOrgId() != null){
								//Persona Juridica
								
								if(documentRetiro.getFIToFICstmrDrctDbt().getDrctDbtTxInf()
										.get(0).getDbtr().getId().getOrgId().getOthr() != null){
									
									if(!StringUtils.IsNullOrEmpty(documentRetiro.getFIToFICstmrDrctDbt().getDrctDbtTxInf()
											.get(0).getDbtr().getId().getOrgId().getOthr().get(0).getId())){
										
										iso.setISO_115_ExtendedData(iso.getISO_115_ExtendedData() + "|" + documentRetiro
												.getFIToFICstmrDrctDbt().getDrctDbtTxInf()
												.get(0).getDbtr().getId().getOrgId().getOthr().get(0).getId());
									    
										
										if(documentRetiro.getFIToFICstmrDrctDbt().getDrctDbtTxInf()
												.get(0).getDbtr().getId().getOrgId().getOthr().get(0).getSchmeNm() != null){
											
											if(!StringUtils.IsNullOrEmpty(documentRetiro.getFIToFICstmrDrctDbt().getDrctDbtTxInf()
													.get(0).getDbtr().getId().getOrgId().getOthr().get(0).getSchmeNm().getCd())){
												
												iso.setISO_115_ExtendedData(iso.getISO_115_ExtendedData() + "|" + documentRetiro
														.getFIToFICstmrDrctDbt().getDrctDbtTxInf()
														.get(0).getDbtr().getId().getOrgId().getOthr().get(0)
														.getSchmeNm().getCd());
												
												if(documentRetiro.getFIToFICstmrDrctDbt().getDrctDbtTxInf()
														.get(0).getDbtr().getCtctDtls() != null){
													
													if(!StringUtils.IsNullOrEmpty(documentRetiro.getFIToFICstmrDrctDbt().getDrctDbtTxInf()
														.get(0).getDbtr().getCtctDtls().getMobNb())){
														
														iso.setISO_115_ExtendedData(iso.getISO_115_ExtendedData() + "|" 
														      + documentRetiro.getFIToFICstmrDrctDbt().getDrctDbtTxInf()
																.get(0).getDbtr().getCtctDtls().getMobNb());
														
													}else{
														
														campo = "Numero de celular (Persona Juridica) receptor [FIToFICstmrDrctDbt."
																+ "DrctDbtTxInf.DbtrCtctDtls.MobNb"
																+ "es nulo o vacio"
														        .toUpperCase();
														iso.setISO_039_ResponseCode("905");
														iso.setISO_039p_ResponseDetail(campo);
														return iso;
													}
													
												}else{
													
													campo = "Numero de celular (Persona Juridica) receptor [FIToFICstmrDrctDbt."
															+ "DrctDbtTxInf.DbtrCtctDtls"
															+ "es nulo o vacio"
													        .toUpperCase();
													iso.setISO_039_ResponseCode("905");
													iso.setISO_039p_ResponseDetail(campo);
													return iso;
												}
												
											}else {
												
												campo = "Tipo de cuenta del cuentahabiente (Persona Juridica) receptor [FIToFICstmrDrctDbt."
														+ "DrctDbtTxInf.Dbtr.Id.OrgId"
														+ ".Othr.Id.SchmeNm.Cd] es nulo o vacio"
												        .toUpperCase();
												iso.setISO_039_ResponseCode("905");
												iso.setISO_039p_ResponseDetail(campo);
												iso.setISO_104_TranDescription("AC12");
												return iso;
											}
											
										}else{
											
											campo = "Tipo de cuenta del cuentahabiente receptor (Persona Juridica) "
													+ "[FIToFICstmrDrctDbt.DrctDbtTxInf.Dbtr.Id.OrgId"
													+ ".Othr.Id.SchmeNm] es nulo"
											        .toUpperCase();
											iso.setISO_039_ResponseCode("905");
											iso.setISO_039p_ResponseDetail(campo);
											iso.setISO_104_TranDescription("AC12");
											return iso;
										}
										
									}else{
										
										campo = "Nro. de Identificacion del cuentahabiente receptor (Persona Juridica) "
												+ "[FIToFICstmrDrctDbt.DrctDbtTxInf.Dbtr.Id.OrgId.Othr.Id] "
												+ "es nulo o vacio"
										        .toUpperCase();
										iso.setISO_039_ResponseCode("905");
										iso.setISO_039p_ResponseDetail(campo);
										return iso;
									}
									
								}else{
									
									campo = "Estrucura Cuentahabiente receptor (Persona Juridica) [FIToFICstmrDrctDbt."
											+ "DrctDbtTxInf.Dbtr.Id.OrgId.Othr] es nulo"
									        .toUpperCase();
									iso.setISO_039_ResponseCode("905");
									iso.setISO_039p_ResponseDetail(campo);
									return iso;
								}
								
							}else{
								
								if(documentRetiro.getFIToFICstmrDrctDbt().getDrctDbtTxInf()
										.get(0).getDbtr().getId().getPrvtId() != null){
									
									if(documentRetiro.getFIToFICstmrDrctDbt().getDrctDbtTxInf()
											.get(0).getDbtr().getId().getPrvtId().getOthr() != null){
										
										if(!StringUtils.IsNullOrEmpty(documentRetiro.getFIToFICstmrDrctDbt().getDrctDbtTxInf()
												.get(0).getDbtr().getId().getPrvtId().getOthr().get(0).getId())){
											
											iso.setISO_115_ExtendedData(iso.getISO_115_ExtendedData() + "|" + documentRetiro.
													 getFIToFICstmrDrctDbt().getDrctDbtTxInf()
													.get(0).getDbtr().getId().getPrvtId().getOthr().get(0).getId());
											
											if(documentRetiro.getFIToFICstmrDrctDbt().getDrctDbtTxInf()
													.get(0).getDbtr().getId().getPrvtId().getOthr().get(0).getSchmeNm() != null){
												
												if(!StringUtils.IsNullOrEmpty(documentRetiro.getFIToFICstmrDrctDbt().getDrctDbtTxInf()
														.get(0).getDbtr().getId().getPrvtId().getOthr().get(0).getSchmeNm().getCd())){
													
													iso.setISO_115_ExtendedData(iso.getISO_115_ExtendedData() + "|" + 
													         documentRetiro.getFIToFICstmrDrctDbt().getDrctDbtTxInf()
															.get(0).getDbtr().getId().getPrvtId().getOthr().get(0).getSchmeNm().getCd());
													
													
													if(documentRetiro.getFIToFICstmrDrctDbt().getDrctDbtTxInf()
															.get(0).getDbtr().getCtctDtls() != null){
														
														if(!StringUtils.IsNullOrEmpty(documentRetiro.getFIToFICstmrDrctDbt().getDrctDbtTxInf()
															.get(0).getDbtr().getCtctDtls().getMobNb())){
															
															iso.setISO_115_ExtendedData(iso.getISO_115_ExtendedData() + "|" 
															      + documentRetiro.getFIToFICstmrDrctDbt().getDrctDbtTxInf()
																	.get(0).getDbtr().getCtctDtls().getMobNb());
															
															
														}else{
															
															campo = "Numero de celular (Persona Natural) receptor [FIToFICstmrDrctDbt."
																	+ "DrctDbtTxInf.DbtrCtctDtls.MobNb"
																	+ "es nulo o vacio"
															        .toUpperCase();
															iso.setISO_039_ResponseCode("905");
															iso.setISO_039p_ResponseDetail(campo);
															return iso;
														}
														
													}else{
														
														campo = "Numero de celular (Persona Natural) receptor [FIToFICstmrDrctDbt."
																+ "DrctDbtTxInf.DbtrCtctDtls"
																+ "es nulo o vacio"
														        .toUpperCase();
														iso.setISO_039_ResponseCode("905");
														iso.setISO_039p_ResponseDetail(campo);
														return iso;
													}
													
														
												}else {
													
													campo = "Tipop Identidicacion Cuentahabiente (Persona Natural) receptor [FIToFICstmrDrctDbt."
															+ "DrctDbtTxInf.Dbtr.Id.PrvtId.Othr.id] es nulo"
													        .toUpperCase();
													iso.setISO_039_ResponseCode("905");
													iso.setISO_039p_ResponseDetail(campo);
													return iso;
												}
												
											}else{
												
												campo = "Estrucura Cuentahabiente (Persona Natural) receptor [FIToFICstmrDrctDbt."
														+ "DrctDbtTxInf.Dbtr.Id.PrvtId.Othr.SchmeNm] es nulo"
												        .toUpperCase();
												iso.setISO_039_ResponseCode("905");
												iso.setISO_039p_ResponseDetail(campo);
												return iso;
											}
											
										}else{
											
											campo = "Identidicacion Cuentahabiente (Persona Natural) receptor [FIToFICstmrDrctDbt."
													+ "DrctDbtTxInf.Dbtr.Id.PrvtId.Othr.id] es nulo"
											        .toUpperCase();
											iso.setISO_039_ResponseCode("905");
											iso.setISO_039p_ResponseDetail(campo);
											return iso;
										}
										
										
									}else{
										
										campo = "Estrucura Cuentahabiente (Persona Natural) receptor [FIToFICstmrDrctDbt."
												+ "DrctDbtTxInf.Dbtr.Id.PrvtId.Othr] es nulo"
										        .toUpperCase();
										iso.setISO_039_ResponseCode("905");
										iso.setISO_039p_ResponseDetail(campo);
										return iso;
									}
									
								}else{
									
									campo = "Info Datos Cuentahabiente receptor (Natural o Juridica) [FIToFICstmrDrctDbt."
											+ "DrctDbtTxInf.Dbtr.Id] es nula"
									        .toUpperCase();
									iso.setISO_039_ResponseCode("905");
									iso.setISO_039p_ResponseDetail(campo);
									return iso;
								}						
							}
							
						}else{
							
							campo = "Estrucura Cuentahabiente receptor [FIToFICstmrDrctDbt."
									+ "DrctDbtTxInf.Dbtr.Id] es nula"
							        .toUpperCase();
							iso.setISO_039_ResponseCode("905");
							iso.setISO_039p_ResponseDetail(campo);
							return iso;
						}
							
						}else{
							
							campo = "Nombres del Cuentahabiente Receptor [FIToFICstmrDrctDbt"
									+ ".DrctDbtTxInf.Dbtr.Nm] son nulos o vacios"
							        .toUpperCase();
							iso.setISO_039_ResponseCode("905");
							iso.setISO_039p_ResponseDetail(campo);
							iso.setISO_104_TranDescription("BE21");
							return iso;
						}
						
					}else{
						
						campo = "Estructura informacion Cuentahabiente receptor "
								+ "[FIToFICstmrDrctDbt.DrctDbtTxInf.Dbtr] es nulo"
						        .toUpperCase();
						iso.setISO_039_ResponseCode("905");
						iso.setISO_039p_ResponseDetail(campo);
						return iso;
					}
					
					///SUBSTRUCTURA DbtrAcct (DATOS DE LA CUENTA DEBITANTE EN LA EFI AUTORIZADORA)
					if(documentRetiro.getFIToFICstmrDrctDbt().getDrctDbtTxInf()
							.get(0).getDbtrAcct() != null){
						
						if(documentRetiro.getFIToFICstmrDrctDbt().getDrctDbtTxInf()
								.get(0).getDbtrAcct().getId() != null){
							
							if(documentRetiro.getFIToFICstmrDrctDbt().getDrctDbtTxInf()
									.get(0).getDbtrAcct().getId().getOthr() != null){
								
								if(!StringUtils.IsNullOrEmpty(documentRetiro.getFIToFICstmrDrctDbt().getDrctDbtTxInf()
										.get(0).getDbtrAcct().getId().getOthr().getId())){
									
									iso.setISO_102_AccountID_1(documentRetiro.getFIToFICstmrDrctDbt().getDrctDbtTxInf()
											.get(0).getDbtrAcct().getId().getOthr().getId());
									
								}else {
									
									campo = "Numero de Cuenta a Debitar [FIToFICstmrDrctDbt."
											+ "DrctDbtTxInf.DbtrAcct.Id.Othr.Id] es nula o vacia"
									        .toUpperCase();
									iso.setISO_039_ResponseCode("905");
									iso.setISO_039p_ResponseDetail(campo);
									iso.setISO_104_TranDescription("AC01");
									return iso;
								}
								
							}else{
								
								campo = "Numero de Cuenta a Debitar [FIToFICstmrDrctDbt."
										+ "DrctDbtTxInf.DbtrAcct.Id.Othr] es nulo"
								        .toUpperCase();
								iso.setISO_039_ResponseCode("905");
								iso.setISO_039p_ResponseDetail(campo);
								iso.setISO_104_TranDescription("AC01");
								return iso;
							}
							
						}else{
							
							campo = "Numero de Cuenta a Acreditar [FIToFICstmrDrctDbt."
									+ "DrctDbtTxInf.DbtrAcct.Id] es nulo"
							        .toUpperCase();
							iso.setISO_039_ResponseCode("905");
							iso.setISO_039p_ResponseDetail(campo);
							iso.setISO_104_TranDescription("AC01");
							return iso;
						}
						if(documentRetiro.getFIToFICstmrDrctDbt().getDrctDbtTxInf()
								.get(0).getDbtrAcct().getTp() != null){
							
							if(!StringUtils.IsNullOrEmpty(documentRetiro.getFIToFICstmrDrctDbt()
									.getDrctDbtTxInf()
									.get(0).getDbtrAcct().getTp().getPrtry())){
								
								if(documentRetiro.getFIToFICstmrDrctDbt()
										.getDrctDbtTxInf()
										.get(0).getDbtrAcct().getTp().getPrtry().equals("01")){
									iso.setISO_003_ProcessingCode("012000");
								}else if (documentRetiro.getFIToFICstmrDrctDbt()
										.getDrctDbtTxInf()
										.get(0).getDbtrAcct().getTp().getPrtry().equals("02")) {
									iso.setISO_003_ProcessingCode("011000");
								}else {
									
									campo = "Tipo de la Cuenta Debitante [FIToFICstmrDrctDbt."
											+ "DrctDbtTxInf.DbtrAcct.Tp] es nula"
									        .toUpperCase();
									iso.setISO_039_ResponseCode("905");
									iso.setISO_039p_ResponseDetail(campo);
									return iso;
								}
								
							}else {
								
								campo = "Tipo de la Cuenta Acreditante [FIToFICstmrDrctDbt."
										+ "DrctDbtTxInf.DbtrAcct.Tp.Prtry] no es valida"
								        .toUpperCase();
								iso.setISO_039_ResponseCode("905");
								iso.setISO_039p_ResponseDetail(campo);
								return iso;
							}
							
						}else {
							
							campo = "Tipo de la Cuenta Acreditante [FIToFICstmrDrctDbt"
									+ ".DrctDbtTxInf.DbtrAcct.Tp] es nula"
							        .toUpperCase();
							iso.setISO_039_ResponseCode("905");
							iso.setISO_039p_ResponseDetail(campo);
							return iso;
						}
						if(!StringUtils.IsNullOrEmpty(documentRetiro.getFIToFICstmrDrctDbt()
								.getDrctDbtTxInf()
								.get(0).getDbtrAcct().getCcy())){
							
							iso.setISO_121_ExtendedData(documentRetiro.getFIToFICstmrDrctDbt()
									.getDrctDbtTxInf()
									.get(0).getDbtrAcct().getCcy());
							
						}else {
							
							campo = "Moneda de la cuenta Debitante [FIToFICstmrDrctDbt."
									+ "DrctDbtTxInf.DbtrAcct.Ccy] es nula"
							        .toUpperCase();
							iso.setISO_039_ResponseCode("905");
							iso.setISO_039p_ResponseDetail(campo);
							iso.setISO_104_TranDescription("AC09");
							return iso;
						}
						
					}else{
						
						campo = "Moneda de la cuenta Acreditante [FIToFICstmrDrctDbt."
								+ "DrctDbtTxInf.DbtrAcct] es nula"
						        .toUpperCase();
						iso.setISO_039_ResponseCode("905");
						iso.setISO_039p_ResponseDetail(campo);
						iso.setISO_104_TranDescription("AC09");
						return iso;
					}
					
					////////////
					iso.setISO_000_Message_Type("1200");
					iso.setISO_018_MerchantType("0005");
					iso.setISO_024_NetworkId("555557");
					iso.setISO_039_ResponseCode("000");
					
				}else {
					
					campo = "Region [FIToFICstmrDrctDbt.GrpHdr] es nula"
							.toUpperCase();
					iso.setISO_039_ResponseCode("905");
					iso.setISO_039p_ResponseDetail(campo);
					return iso;
				}
				
			}else{
				
				iso.setISO_039_ResponseCode("905");
				iso.setISO_039p_ResponseDetail("TRAMA RETIRO ES NULA");
				return iso;
			}
			
		} catch (Exception e) {
			
			log.WriteLogMonitor("Error modulo BceVCParser::parseDocumentRetiroVC ", TypeMonitor.error, e);
			iso.setISO_039_ResponseCode("905");
			iso.setISO_039p_ResponseDetail("ERROR " + campo + " " + GeneralUtils.ExceptionToString("INVALIDO ", e, true));
		}
		
		return iso;
	}
    public Iso8583 parseDocumentTransferenciaVC(DocumentTransferencia documentTransferencia){
		
		Iso8583 iso = new Iso8583();
		String campo = null;
		try {
			
			if(documentTransferencia != null){
				
			    ///ESTRUCTURA GRPHDR
				if(documentTransferencia.getFIToFICstmrCdtTrf().getGrpHdr() != null){
					
					if(!StringUtils.IsNullOrEmpty(documentTransferencia.getFIToFICstmrCdtTrf().getGrpHdr().getMsgId())){
						if(documentTransferencia.getFIToFICstmrCdtTrf().getGrpHdr().getMsgId().length() != 32){
							
							campo = "Campo [FIToFICstmrCdtTrf.GrpHdr.MsgId] es invalido"
							        .toUpperCase();
							iso.setISO_039_ResponseCode("905");
							iso.setISO_039p_ResponseDetail(campo);
							iso.setISO_104_TranDescription("RC02");
							return iso;
							
						}else
							iso.setISO_011_SysAuditNumber(documentTransferencia.getFIToFICstmrCdtTrf().getGrpHdr().getMsgId());
					}else{
						campo = "Valor unico de la transaccion [FIToFICstmrCdtTrf.GrpHdr.MsgId] es nulo o vacio"
						        .toUpperCase();
						iso.setISO_039_ResponseCode("905");
						iso.setISO_039p_ResponseDetail(campo);
						iso.setISO_104_TranDescription("RC02");
						return iso;
					}
					if(documentTransferencia.getFIToFICstmrCdtTrf().getGrpHdr().getCreDtTm() != null){
						
						campo = "Fecha Transaccion [FIToFICstmrCdtTrf.GrpHdr.CreDtTm] es nulo"
						        .toUpperCase();
						Date date = documentTransferencia.getFIToFICstmrCdtTrf().getGrpHdr()
								.getCreDtTm().toGregorianCalendar().getTime();
						
						if(date.before(new Date())){
							iso.setISO_012_LocalDatetime(date);	
						}else{
							campo = "Fecha Transaccion [FIToFICstmrCdtTrf.GrpHdr.CreDtTm] es invalido"
							        .toUpperCase();
							iso.setISO_039_ResponseCode("905");
							iso.setISO_039p_ResponseDetail(campo);
							iso.setISO_104_TranDescription("DT01");
							return iso;
						}
						
					}else{
						campo = "Estructura FIToFICstmrCdtTrf.GrpHdr.CreDtTm es nula"
						        .toUpperCase();
						iso.setISO_039_ResponseCode("905");
						iso.setISO_039p_ResponseDetail(campo);
						iso.setISO_104_TranDescription("DT01");
						return iso;
					}
					if(!StringUtils.IsNullOrEmpty(documentTransferencia.getFIToFICstmrCdtTrf().getGrpHdr().getNbOfTxs())){
						
						int numberTrx = Integer.parseInt(documentTransferencia.getFIToFICstmrCdtTrf()
								        .getGrpHdr().getNbOfTxs());
						
						if(numberTrx > 0 && numberTrx <= 1){
							
							iso.setISO_022_PosEntryMode(String.valueOf(numberTrx));
							
						}else{
							
							campo = "Numero de Transacciones [FIToFICstmrCdtTrf.GrpHdr.NbOfTxs] invalido, "
									+ "(No se soporta mas de una transaccion)"
							        .toUpperCase();
							iso.setISO_039_ResponseCode("905");
							iso.setISO_039p_ResponseDetail(campo);
							iso.setISO_104_TranDescription("AM19");
							return iso;
						}
						
					}else {
						
						campo = "Numero de Transacciones [FIToFICstmrCdtTrf.GrpHdr.NbOfTxs] es nulo o vacio"
						        .toUpperCase();
						iso.setISO_039_ResponseCode("905");
						iso.setISO_039p_ResponseDetail(campo);
						iso.setISO_104_TranDescription("AM19");
						return iso;
					}
					if(documentTransferencia.getFIToFICstmrCdtTrf().getGrpHdr().getCtrlSum() != null){
						
						campo = "Monto de la Transaccion [FIToFICstmrCdtTrf.GrpHdr.CtrlSum] debe ser mayor a 0"
								.toUpperCase();
						Double monto = documentTransferencia.getFIToFICstmrCdtTrf().getGrpHdr()
								       .getCtrlSum().doubleValue();
						if(monto > 0){
							
							iso.setISO_004_AmountTransaction(monto);
							
						}else {
							
							iso.setISO_039_ResponseCode("905");
							iso.setISO_039p_ResponseDetail(campo);
							iso.setISO_104_TranDescription("AM01");
							return iso;
						}
						
					}else{
						
						campo = "Monto de la Transaccion [FIToFICstmrCdtTrf.GrpHdr.CtrlSum] es nulo"
						        .toUpperCase();
						iso.setISO_039_ResponseCode("905");
						iso.setISO_039p_ResponseDetail(campo);
						return iso;
					}
					
					if(documentTransferencia.getFIToFICstmrCdtTrf().getGrpHdr().getIntrBkSttlmDt() != null){
						
						iso.setISO_015_SettlementDatel(documentTransferencia.getFIToFICstmrCdtTrf()
								.getGrpHdr().getIntrBkSttlmDt().toGregorianCalendar().getTime());
						
					}else{
						campo = "fecha Contable de la Transaccion [FIToFICstmrCdtTrf"
								+ ".GrpHdr.IntrBkSttlmDt] es nula"
						        .toUpperCase();
						iso.setISO_039_ResponseCode("905");
						iso.setISO_039p_ResponseDetail(campo);
						return iso;
					}
					
					if(documentTransferencia.getFIToFICstmrCdtTrf().getGrpHdr()
							.getSttlmInf().getSttlmMtd() != null){
						
						campo = "Estructura [FIToFICstmrCdtTrf.GrpHdr.SttlmInf.SttlmMtd] es invalido"
						        .toUpperCase();
						if(documentTransferencia.getFIToFICstmrCdtTrf().getGrpHdr().getSttlmInf().getSttlmMtd()
								.value().equalsIgnoreCase("CLRG")){
							
							iso.setISO_114_ExtendedData("CLRG");
							
							if(documentTransferencia.getFIToFICstmrCdtTrf().getGrpHdr()
									.getSttlmInf().getClrSys() != null){
								
								if(!StringUtils.IsNullOrEmpty(documentTransferencia.getFIToFICstmrCdtTrf().getGrpHdr()
										.getSttlmInf().getClrSys().getCd())){
									
									iso.setISO_114_ExtendedData(iso.getISO_114_ExtendedData() + "|" + 
									     documentTransferencia.getFIToFICstmrCdtTrf().getGrpHdr()
										.getSttlmInf().getClrSys().getCd());
									
								}else{
									
									campo = "Estructura [FIToFICstmrCdtTrf.GrpHdr.SttlmInf.ClrSys.Cd] es mandatorio"
									        .toUpperCase();
									iso.setISO_039_ResponseCode("905");
									iso.setISO_039p_ResponseDetail(campo);
									return iso;
								}
								
							}else{
								
								campo = "Estructura [FIToFICstmrCdtTrf.GrpHdr.SttlmInf.ClrSys] es nula"
								        .toUpperCase();
								iso.setISO_039_ResponseCode("905");
								iso.setISO_039p_ResponseDetail(campo);
								return iso;
							}
							
						}else {
							
							iso.setISO_039_ResponseCode("905");
							iso.setISO_039p_ResponseDetail(campo);
							return iso;
						}
	
					}else{
						
						campo = "Estructura [FIToFICstmrCdtTrf.GrpHdr.SttlmInf.SttlmMtd] es nula"
						        .toUpperCase();
						iso.setISO_039_ResponseCode("905");
						iso.setISO_039p_ResponseDetail(campo);
						return iso;
					}
					//BIC ORDENANTE IFI
					if(documentTransferencia.getFIToFICstmrCdtTrf().getGrpHdr().getInstgAgt()
							.getFinInstnId() != null){
						
						if(!StringUtils.IsNullOrEmpty(documentTransferencia.getFIToFICstmrCdtTrf().getGrpHdr().getInstgAgt()
								.getFinInstnId().getBICFI())){
							
							iso.setISO_032_ACQInsID(documentTransferencia.getFIToFICstmrCdtTrf().getGrpHdr().getInstgAgt()
									.getFinInstnId().getBICFI());
							if(documentTransferencia.getFIToFICstmrCdtTrf().getGrpHdr().getInstgAgt()
									.getBrnchId() != null){
								if(!StringUtils.IsNullOrEmpty(documentTransferencia.getFIToFICstmrCdtTrf().getGrpHdr().getInstgAgt()
									.getBrnchId().getId())){
									
									iso.setISO_043_CardAcceptorLoc(documentTransferencia.getFIToFICstmrCdtTrf()
									.getGrpHdr().getInstgAgt()
									.getBrnchId().getId());
									
								}else{
									
									campo = "Agencia en Entidad ordenante [FIToFICstmrCdtTrf.GrpHdr"
											+ ".InstgAgt.BrnchId.Id] es mandatoria "
									        .toUpperCase();
									iso.setISO_039_ResponseCode("905");
									iso.setISO_039p_ResponseDetail(campo);
									iso.setISO_104_TranDescription("AGNT");
									return iso;
								}
								
							}else{
								
								campo = "Agencia en Entidad ordenante [FIToFICstmrCdtTrf.GrpHdr"
										+ ".InstgAgt.BrnchId] es nula "
								        .toUpperCase();
								iso.setISO_039_ResponseCode("905");
								iso.setISO_039p_ResponseDetail(campo);
								iso.setISO_104_TranDescription("AGNT");
								return iso;
							}
							
						}else{
							
							campo = "Entidad ordenante [FIToFICstmrCdtTrf.GrpHdr.InstgAgt.FinInstnId.getBICFI] es nula o vacia"
							        .toUpperCase();
							iso.setISO_039_ResponseCode("905");
							iso.setISO_039p_ResponseDetail(campo);
							iso.setISO_104_TranDescription("AGNT");
							return iso;
						}
						
					}else {
						
						campo = "Estructura Entidad ordenante [FIToFICstmrCdtTrf.GrpHdr.InstgAgt.FinInstnId] es nula"
						        .toUpperCase();
						iso.setISO_039_ResponseCode("905");
						iso.setISO_039p_ResponseDetail(campo);
						iso.setISO_104_TranDescription("AGNT");
						return iso;
					}
					
					if(documentTransferencia.getFIToFICstmrCdtTrf().getGrpHdr().getInstdAgt()
							.getFinInstnId() != null){
						
						if(!StringUtils.IsNullOrEmpty(documentTransferencia.getFIToFICstmrCdtTrf()
								.getGrpHdr().getInstdAgt()
								.getFinInstnId().getBICFI())){
							
							iso.setISO_BitMap(documentTransferencia.getFIToFICstmrCdtTrf()
									.getGrpHdr().getInstdAgt()
									.getFinInstnId().getBICFI());
							
						}else{
							
							campo = "Entidad Intermedia [FIToFICstmrCdtTrf.GrpHdr"
									+ ".InstdAgt.FinInstnId.BICFI] es nula o vacia"
							        .toUpperCase();
							iso.setISO_039_ResponseCode("905");
							iso.setISO_039p_ResponseDetail(campo);
							iso.setISO_104_TranDescription("RC11");
							return iso;
						}
							
					}else {
						
						campo = "Estructura Entidad Intermedia [FIToFICstmrCdtTrf.GrpHdr.InstdAgt.FinInstnId] es nula"
						        .toUpperCase();
						iso.setISO_039_ResponseCode("905");
						iso.setISO_039p_ResponseDetail(campo);
						iso.setISO_104_TranDescription("RC11");
						return iso;
					}
					////////// ESTRUCTURA CDTTRFXINF
					
					if(documentTransferencia.getFIToFICstmrCdtTrf().getCdtTrfTxInf().get(0).getPmtId() != null){
						
						if(!StringUtils.IsNullOrEmpty(documentTransferencia.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
								.get(0).getPmtId().getEndToEndId())){
							
							iso.setISO_023_CardSeq(documentTransferencia.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
								.get(0).getPmtId().getEndToEndId());	
							
						}else {
							
							campo = "Numero de Papeleta [FIToFICstmrCdtTrf.CdtTrfTxInf.PmtId.EndToEndId] es nula"
							        .toUpperCase();
							iso.setISO_039_ResponseCode("905");
							iso.setISO_039p_ResponseDetail(campo);
							iso.setISO_104_TranDescription("FF08");
							return iso;
						}
						
						if(!StringUtils.IsNullOrEmpty(documentTransferencia.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
								.get(0).getPmtId().getTxId())){
							
							iso.setISO_037_RetrievalReferenceNumber(documentTransferencia.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
								.get(0).getPmtId().getTxId());	
							
						}else {
							
							campo = "Estructura Numero unico Trx.(Entidad Ordenante) [FIToFICstmrCdtTrf.CdtTrfTxInf.PmtId.TxId] es nula"
							        .toUpperCase();
							iso.setISO_039_ResponseCode("905");
							iso.setISO_039p_ResponseDetail(campo);
							iso.setISO_104_TranDescription("RC04");
							return iso;
						}
						
						if(!StringUtils.IsNullOrEmpty(documentTransferencia.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
								.get(0).getPmtId().getClrSysRef())){
							
							iso.setISO_090_OriginalData(documentTransferencia.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
								.get(0).getPmtId().getClrSysRef());	
							
						}else {
							
							campo = "Campo (Entidad Ordenante) [FIToFICstmrCdtTrf.CdtTrfTxInf.PmtId.ClrSysRef] es nula"
							        .toUpperCase();
							iso.setISO_039_ResponseCode("905");
							iso.setISO_039p_ResponseDetail(campo);
							iso.setISO_104_TranDescription("RC04");
							return iso;
						}
						
					}else{
						
						campo = "Estructura [FIToFICstmrCdtTrf.CdtTrfTxInf.PmtId] es nula"
						        .toUpperCase();
						iso.setISO_039_ResponseCode("905");
						iso.setISO_039p_ResponseDetail(campo);
						return iso;
					}
					if(documentTransferencia.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
							.get(0).getIntrBkSttlmAmt() != null){
						
						if(!StringUtils.IsNullOrEmpty(documentTransferencia.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
								.get(0).getIntrBkSttlmAmt().getCcy())){
							iso.setISO_049_TranCurrCode(documentTransferencia.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
								.get(0).getIntrBkSttlmAmt().getCcy().equalsIgnoreCase("USD")?840:0);
							
						}else{
							
							campo = "Moneda de la Transaccion [FIToFICstmrCdtTrf.CdtTrfTxInf.IntrBkSttlmAmt.Ccy] es vacia o nula"
							        .toUpperCase();
							iso.setISO_039_ResponseCode("905");
							iso.setISO_039p_ResponseDetail(campo);
							iso.setISO_104_TranDescription("AC09");
							return iso;
						}
						double val = documentTransferencia.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
								.get(0).getIntrBkSttlmAmt().getValue().doubleValue();
						if(val > 0){
							
							iso.setISO_006_BillAmount(val);
							
						}else {
							
							campo = "Monto de la Transaccion [FIToFICstmrCdtTrf.CdtTrfTxInf.IntrBkSttlmAmt.Value] es vacia o nula"
							        .toUpperCase();
							iso.setISO_039_ResponseCode("905");
							iso.setISO_039p_ResponseDetail(campo);
							iso.setISO_104_TranDescription("AM01");
							return iso;
						}
					
						
					}else{
						
						campo = "Estructura [FIToFICstmrCdtTrf.CdtTrfTxInf.IntrBkSttlmAmt] es nula"
						        .toUpperCase();
						iso.setISO_039_ResponseCode("905");
						iso.setISO_039p_ResponseDetail(campo);
						return iso;
					}
					//Fecha Contable en esta Seccion
					/*if(documentoDeposito.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
							.get(0).getIntrBkSttlmDt() != null){
						
						Date dateBusiness = documentoDeposito.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
								.get(0).getIntrBkSttlmDt().toGregorianCalendar().getTime();
						
						if(dateBusiness.before(new Date())){
							
							iso.setISO_015_SettlementDatel(dateBusiness);	
						}else{
							campo = "Fecha Contable de la Trx. [FIToFICstmrCdtTrf.CdtTrfTxInf.IntrBkSttlmDt] es invalido"
							        .toUpperCase();
							iso.setISO_039_ResponseCode("905");
							iso.setISO_039p_ResponseDetail(campo);
							return iso;
						}
					}else{
						
						campo = "Fecha Contable de la Trx. [FIToFICstmrCdtTrf.CdtTrfTxInf.IntrBkSttlmDt] es nula"
						        .toUpperCase();
						iso.setISO_039_ResponseCode("905");
						iso.setISO_039p_ResponseDetail(campo);
						return iso;
					}*/
					if(documentTransferencia.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
							.get(0).getChrgBr() != null){
						
						iso.setISO_035_Track2(documentTransferencia.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
								.get(0).getChrgBr().value());
						
					}else {
						campo = "Estructura [FIToFICstmrCdtTrf.CdtTrfTxInf.ChrgBr] es nula"
						        .toUpperCase();
						iso.setISO_039_ResponseCode("905");
						iso.setISO_039p_ResponseDetail(campo);
						return iso;
					}
					///SUBSESTRUCTURA DBTR (ATOS DE LA PERSONA QUIEN ESTA REALIZANDO LA TRANSFERENCIA) 
					if(documentTransferencia.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
							.get(0).getDbtr() != null){
						
						if(!StringUtils.IsNullOrEmpty(documentTransferencia.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
								.get(0).getDbtr().getNm())){
							
							iso.setISO_034_PANExt(documentTransferencia.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
									.get(0).getDbtr().getNm());
							
						}else{
							campo = "Nombres del Tercero (Transferencista) [FIToFICstmrCdtTrf.CdtTrfTxInf.Dbtr.Nm] es nulo o vacio"
							        .toUpperCase();
							iso.setISO_039_ResponseCode("905");
							iso.setISO_039p_ResponseDetail(campo);
							iso.setISO_104_TranDescription("BE21");
							return iso;
						}
						if(documentTransferencia.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
								.get(0).getDbtr().getId() != null){
							if(documentTransferencia.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
									.get(0).getDbtr().getId().getPrvtId() != null){
								
								if(documentTransferencia.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
										.get(0).getDbtr().getId().getPrvtId().getOthr() != null){
									if(!StringUtils.IsNullOrEmpty(documentTransferencia.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
											.get(0).getDbtr().getId().getPrvtId().getOthr().get(0).getId())){
										
										iso.setISO_002_PAN(documentTransferencia.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
											.get(0).getDbtr().getId().getPrvtId().getOthr().get(0).getId());
										
										if(documentTransferencia.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
												.get(0).getDbtr().getId().getPrvtId().getOthr()
												.get(0).getSchmeNm() != null){
											if(!StringUtils.IsNullOrEmpty(documentTransferencia.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
													.get(0).getDbtr().getId().getPrvtId().getOthr()
													.get(0).getSchmeNm().getCd())){
												
												iso.setISO_055_EMV(documentTransferencia.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
													.get(0).getDbtr().getId().getPrvtId().getOthr()
													.get(0).getSchmeNm().getCd());
												
											}else {
												
												campo = "Tipo de Identificacion del depositante [FIToFICstmrCdtTrf.CdtTrfTxInf.Dbtr.Id.PrvtId.Othr."
														+ "SchmeNm.Cd] es nulo o vacio"
												        .toUpperCase();
												iso.setISO_039_ResponseCode("905");
												iso.setISO_039p_ResponseDetail(campo);
												iso.setISO_104_TranDescription("BE15");
												return iso;
											}
											
										}else{
											
											campo = "Estructura Tipo Identificacion depositante [FIToFICstmrCdtTrf.CdtTrfTxInf.Dbtr.Id.PrvtId.Othr."
													+ "SchmeNm] es nulo"
											        .toUpperCase();
											iso.setISO_039_ResponseCode("905");
											iso.setISO_039p_ResponseDetail(campo);
											return iso;
										}
										
									}else{
										
										campo = "Nro. de Identificacion del Depositante [FIToFICstmrCdtTrf.CdtTrfTxInf"
												+ ".Dbtr.Id.PrvtId.Othr.Id] es mandatorio"
										        .toUpperCase();
										iso.setISO_039_ResponseCode("905");
										iso.setISO_039p_ResponseDetail(campo);
										return iso;
									}
									
								}else{
									campo = "Estructura Nro. de Identificacion del Depositante [FIToFICstmrCdtTrf"
											+ ".CdtTrfTxInf.Dbtr.Id.PrvtId.Othr] es nulo"
									        .toUpperCase();
									iso.setISO_039_ResponseCode("905");
									iso.setISO_039p_ResponseDetail(campo);
									return iso;
								}
								
							}else{
								
								campo = "Estructura [FIToFICstmrCdtTrf.CdtTrfTxInf.Dbtr.Id.PrvtId] es nula"
								        .toUpperCase();
								iso.setISO_039_ResponseCode("905");
								iso.setISO_039p_ResponseDetail(campo);
								return iso;
							}
							
						}else {
							campo = "Estructura identificacion del depositante [FIToFICstmrCdtTrf"
									+ ".CdtTrfTxInf.Dbtr.Id] es nulo"
							        .toUpperCase();
							iso.setISO_039_ResponseCode("905");
							iso.setISO_039p_ResponseDetail(campo);
							return iso;
						}
						
						
					}else{
						
						campo = "Estructura Informacion del Depositante [FIToFICstmrCdtTrf.CdtTrfTxInf.Dbtr] es nula"
						        .toUpperCase();
						iso.setISO_039_ResponseCode("905");
						iso.setISO_039p_ResponseDetail(campo);
						return iso;
					}
					//SE AUMENTA EN TRANSFERENCIA ESTRUCTURA DbtrAcct (NUMERO DE CUENTA DEL TERCERO)
					if(documentTransferencia.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
							.get(0).getDbtrAcct() != null){
						
						if(documentTransferencia.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
								.get(0).getDbtrAcct().getId() != null){
							
							if(documentTransferencia.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
									.get(0).getDbtrAcct().getId().getOthr() != null){
								
								if(!StringUtils.IsNullOrEmpty(documentTransferencia.getFIToFICstmrCdtTrf()
									.getCdtTrfTxInf()
									.get(0).getDbtrAcct().getId().getOthr().getId())){
									
									//CUENTA DEBITANTE
									iso.setISO_102_AccountID_1(documentTransferencia.getFIToFICstmrCdtTrf()
									.getCdtTrfTxInf()
									.get(0).getDbtrAcct().getId()
									.getOthr().getId());
									
									if(documentTransferencia.getFIToFICstmrCdtTrf()
											.getCdtTrfTxInf()
											.get(0).getDbtrAcct().getTp() != null){
										
										if(!StringUtils.IsNullOrEmpty(documentTransferencia.getFIToFICstmrCdtTrf()
											.getCdtTrfTxInf()
											.get(0).getDbtrAcct().getTp().getPrtry())){
											
											String tipCta = documentTransferencia.getFIToFICstmrCdtTrf()
													.getCdtTrfTxInf()
													.get(0).getDbtrAcct().getTp().getPrtry();
											
											switch (tipCta) {
											case "01":
												//CORRIENTES
												iso.setISO_003_ProcessingCode("0120"); //Se completa luego
												break;
											case "02":
												//AHORROS
												iso.setISO_003_ProcessingCode("0110"); //Se completa luego
												break;
											default:
												
												campo = "Tipo de Cuenta de la persona Ordenante "
														+ "(Quien hace la Transferencia)"
														+ " [FIToFICstmrCdtTrf.CdtTrfTxInf."
														+ "DbtrAcct.Tp.Prtry] es invalida"
												        .toUpperCase();
												iso.setISO_039_ResponseCode("905");
												iso.setISO_039p_ResponseDetail(campo);
												iso.setISO_104_TranDescription("AC12");
												return iso;
											}
											
										}else{
											
											campo = "Tipo de Cuenta de la persona Ordenante "
													+ "(Quien hace la Transferencia)"
													+ " [FIToFICstmrCdtTrf.CdtTrfTxInf."
													+ "DbtrAcct.Tp.Prtry] es mandatorio"
											        .toUpperCase();
											iso.setISO_039_ResponseCode("905");
											iso.setISO_039p_ResponseDetail(campo);
											iso.setISO_104_TranDescription("AC12");
											return iso;
										}
										
									}else{
										
										campo = "Estructura Tipo de Cuenta de la persona Ordenante "
												+ "(Quien hace la Transferencia)"
												+ " [FIToFICstmrCdtTrf.CdtTrfTxInf.DbtrAcct.Tp] es nula"
										        .toUpperCase();
										iso.setISO_039_ResponseCode("905");
										iso.setISO_039p_ResponseDetail(campo);
										iso.setISO_104_TranDescription("AC12");
										return iso;
									}
									
								}else{
									
									campo = "Numero de Cuenta de la persona Ordenante "
											+ "(Quien hace la Transferencia)"
											+ " [FIToFICstmrCdtTrf.CdtTrfTxInf.DbtrAcct.Id.Othr.Id] es mandatorio"
									        .toUpperCase();
									iso.setISO_039_ResponseCode("905");
									iso.setISO_039p_ResponseDetail(campo);
									iso.setISO_104_TranDescription("AC01");
									return iso;
								}
								
							}else {
								
								campo = "Estructura Informacion de la Cuenta de la persona Ordenante"
										+ " [FIToFICstmrCdtTrf.CdtTrfTxInf.DbtrAcct.Id.Othr] es nula"
								        .toUpperCase();
								iso.setISO_039_ResponseCode("905");
								iso.setISO_039p_ResponseDetail(campo);
								iso.setISO_104_TranDescription("AC01");
								return iso;
							}
							
						}else{
							
							campo = "Estructura Informacion de la Cuenta de la persona Ordenante"
									+ " [FIToFICstmrCdtTrf.CdtTrfTxInf.DbtrAcct.Id] es nula"
							        .toUpperCase();
							iso.setISO_039_ResponseCode("905");
							iso.setISO_039p_ResponseDetail(campo);
							iso.setISO_104_TranDescription("AC01");
							return iso;
						}
						
					}else{
						
						campo = "Estructura Informacion de la Cuenta de la persona Ordenante"
								+ " [FIToFICstmrCdtTrf.CdtTrfTxInf.DbtrAcct] es nula"
						        .toUpperCase();
						iso.setISO_039_ResponseCode("905");
						iso.setISO_039p_ResponseDetail(campo);
						iso.setISO_104_TranDescription("AC01");
						return iso;
					}
					
					///SUBSTRUCTURA DBTRAGT (DATOS DE LA ENTIDAD ADQUIRIENTE)
					String efi_age = null;
					if(documentTransferencia.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
							.get(0).getDbtrAgt() != null){
						if(documentTransferencia.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
								.get(0).getDbtrAgt().getFinInstnId() != null){
							if(!StringUtils.IsNullOrEmpty(documentTransferencia.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
								.get(0).getDbtrAgt().getFinInstnId().getBICFI())){
								
								efi_age = documentTransferencia.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
										.get(0).getDbtrAgt().getFinInstnId().getBICFI();
							if(documentTransferencia.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
									.get(0).getDbtrAgt().getBrnchId() != null){
								
								if(!StringUtils.IsNullOrEmpty(documentTransferencia.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
									.get(0).getDbtrAgt().getBrnchId().getId())){
									
									efi_age += "|" + documentTransferencia.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
											.get(0).getDbtrAgt().getBrnchId().getId();
									iso.setISO_120_ExtendedData(efi_age);
									
								}else{
									
									campo = "Agencia de la Entidad Ordentante [FIToFICstmrCdtTrf."
											+ "CdtTrfTxInf.DbtrAgt.BrnchId.Id] es vacio o nulo"
									        .toUpperCase();
									iso.setISO_039_ResponseCode("905");
									iso.setISO_039p_ResponseDetail(campo);
									iso.setISO_104_TranDescription("AGNT");
									return iso;
								}
								
							}else{
								
								campo = "Agencia de la Entidad Ordentante [FIToFICstmrCdtTrf."
										+ "CdtTrfTxInf.DbtrAgt.BrnchId] es nula"
								        .toUpperCase();
								iso.setISO_039_ResponseCode("905");
								iso.setISO_039p_ResponseDetail(campo);
								iso.setISO_104_TranDescription("AGNT");
								return iso;
							}
								
						}else {
							
							campo = "Codigo Entidad Ordentante [FIToFICstmrCdtTrf.CdtTrfTxInf"
									+ ".DbtrAgt.FinInstnId.BICFI] es nula"
							        .toUpperCase();
							iso.setISO_039_ResponseCode("905");
							iso.setISO_039p_ResponseDetail(campo);
							iso.setISO_104_TranDescription("AGNT");
							return iso;
						}
							
						}else{
							
							campo = "Estructura Entidad Ordentante [FIToFICstmrCdtTrf."
									+ "CdtTrfTxInf.DbtrAgt.FinInstnId] es nula"
							        .toUpperCase();
							iso.setISO_039_ResponseCode("905");
							iso.setISO_039p_ResponseDetail(campo);
							iso.setISO_104_TranDescription("AGNT");
							return iso;
						}
						
					}else{
						
						campo = "Estructura con informacion de la Entidad Ordenante "
								+ "[FIToFICstmrCdtTrf.CdtTrfTxInf.DbtrAgt] es nula"
						        .toUpperCase();
						iso.setISO_039_ResponseCode("905");
						iso.setISO_039p_ResponseDetail(campo);
						iso.setISO_104_TranDescription("AGNT");
						return iso;
					}
					///SUBSTRUCTURA DBTRAGTACCT (NUMERO DE CUENTA DE LA EFI ADQUIRIENTE EN EL BCE)
					
					if(documentTransferencia.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
							.get(0).getDbtrAgtAcct() != null){
						
						if(documentTransferencia.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
								.get(0).getDbtrAgtAcct().getId() != null){
							if(documentTransferencia.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
									.get(0).getDbtrAgtAcct().getId().getOthr() != null){
								
								if(!StringUtils.IsNullOrEmpty(documentTransferencia.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
									.get(0).getDbtrAgtAcct().getId().getOthr().getId())){
									
									iso.setISO_121_ExtendedData(documentTransferencia.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
									.get(0).getDbtrAgtAcct().getId().getOthr().getId());
									
								}else{
									
									campo = "Numero de Cuenta de la Entidad Ordenante [FIToFICstmrCdtTrf."
											+ "CdtTrfTxInf.DbtrAgtAcct.Id.Othr.Id] es vacia o nula"
									        .toUpperCase();
									iso.setISO_039_ResponseCode("905");
									iso.setISO_039p_ResponseDetail(campo);
									iso.setISO_104_TranDescription("AC01");
									return iso;
								}
								
							}else{
								
								campo = "Numero de Cuenta de la Entidad Ordenante "
										+ "[FIToFICstmrCdtTrf.CdtTrfTxInf.DbtrAgtAcct.Id.Othr] es nulo"
								        .toUpperCase();
								iso.setISO_039_ResponseCode("905");
								iso.setISO_039p_ResponseDetail(campo);
								iso.setISO_104_TranDescription("AC01");
								return iso;
							}
							
						}else{
							
							campo = "Estructura Numero de Cuenta de la Entidad Ordenante "
									+ "[FIToFICstmrCdtTrf.CdtTrfTxInf.DbtrAgtAcct.Id] es nulo"
							        .toUpperCase();
							iso.setISO_039_ResponseCode("905");
							iso.setISO_039p_ResponseDetail(campo);
							iso.setISO_104_TranDescription("AC01");
							return iso;
						}
						
						if(documentTransferencia.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
								.get(0).getDbtrAgtAcct().getTp() != null){
							
							if(!StringUtils.IsNullOrEmpty(documentTransferencia.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
								.get(0).getDbtrAgtAcct().getTp().getPrtry())){
								
								iso.setISO_121_ExtendedData(iso.getISO_121_ExtendedData() + "|" +  documentTransferencia.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
										.get(0).getDbtrAgtAcct().getTp().getPrtry());
								
							}else{
								
								campo = "Tipo de Cuenta de la entidad Ordenante "
										+ "[FIToFICstmrCdtTrf.CdtTrfTxInf.DbtrAgtAcct.Tp.Prtry] es nula o vacia"
								        .toUpperCase();
								iso.setISO_039_ResponseCode("905");
								iso.setISO_039p_ResponseDetail(campo);
								iso.setISO_104_TranDescription("AC12");
								return iso;
							}
							
						}else{
							
							campo = "Tipo de Cuenta de la entidad Ordenante [FIToFICstmrCdtTrf."
									+ "CdtTrfTxInf.DbtrAgtAcct.Tp] es nula"
							        .toUpperCase();
							iso.setISO_039_ResponseCode("905");
							iso.setISO_039p_ResponseDetail(campo);
							iso.setISO_104_TranDescription("AC12");
							return iso;
						}
						
						if(!StringUtils.IsNullOrEmpty(documentTransferencia.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
								.get(0).getDbtrAgtAcct().getCcy())){
							
							iso.setISO_121_ExtendedData(iso.getISO_121_ExtendedData() + "|" +  
									documentTransferencia.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
									.get(0).getDbtrAgtAcct().getCcy());
							
						}else{
							
							campo = "Cod. Moneda de la Cta. Efi Ordenante [FIToFICstmrCdtTrf."
									+ "CdtTrfTxInf.DbtrAgtAcct.Ccy] es nulo o vacio"
							        .toUpperCase();
							iso.setISO_039_ResponseCode("905");
							iso.setISO_039p_ResponseDetail(campo);
							iso.setISO_104_TranDescription("AC09");
							return iso;
						}
						
					}else{
						
						campo = "Estructura informacion de la Cuenta del BCE de la "
								+ "EFI Ordentante [FIToFICstmrCdtTrf.CdtTrfTxInf.DbtrAgtAcct] es nula"
						        .toUpperCase();
						iso.setISO_039_ResponseCode("905");
						iso.setISO_039p_ResponseDetail(campo);
						iso.setISO_104_TranDescription("AC01");
						return iso;
					}
					///SUBSTRUCTURA CDTRAGT (DATOS DE LA ENTIDAD AUTORIZADORA)
					
					if(documentTransferencia.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
							.get(0).getCdtrAgt() != null){
						
						if(documentTransferencia.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
								.get(0).getCdtrAgt().getFinInstnId() != null){
							
							if(!StringUtils.IsNullOrEmpty(documentTransferencia.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
									.get(0).getCdtrAgt().getFinInstnId().getBICFI())){
								
								if(documentTransferencia.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
										.get(0).getCdtrAgt().getFinInstnId().getBICFI()
										.equalsIgnoreCase(MemoryGlobal.BCE_Efi_VC)){
									
									iso.setISO_033_FWDInsID(MemoryGlobal.BCE_Efi_VC);
									
								}else{
									
									campo = "Cod. EFI Receptora [FIToFICstmrCdtTrf.CdtTrfTxInf."
											+ "CdtrAgt.FinInstnId.BICFI] "
											+ "no coincide con la asignada a nuestra institucion"
									        .toUpperCase();
									iso.setISO_039_ResponseCode("905");
									iso.setISO_039p_ResponseDetail(campo);
									return iso;
								}
								
							}else{
								
								campo = "Cod. EFI Receptora [FIToFICstmrCdtTrf.CdtTrfTxInf"
										+ ".CdtrAgt.FinInstnId.BICFI] es nula o vacia"
								        .toUpperCase();
								iso.setISO_039_ResponseCode("905");
								iso.setISO_039p_ResponseDetail(campo);
								return iso;
							}
							
						}else{
							
							campo = "Estructura EFI Receptora [FIToFICstmrCdtTrf."
									+ "CdtTrfTxInf.CdtrAgt.FinInstnId] es nulo"
							        .toUpperCase();
							iso.setISO_039_ResponseCode("905");
							iso.setISO_039p_ResponseDetail(campo);
							return iso;
						}
						
					}else{
						
						campo = "Estructura con informacion de la EFI Recepora "
								+ "[FIToFICstmrCdtTrf.CdtTrfTxInf.CdtrAgt] es nula"
						        .toUpperCase();
						iso.setISO_039_ResponseCode("905");
						iso.setISO_039p_ResponseDetail(campo);
						return iso;
					}
					
					///SUBSTRUCTURA Cdtr (DATOS DEL CUENTAHABIENTE ENTIDAD AUTORIZADORA)
									
					if(documentTransferencia.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
							.get(0).getCdtr() != null){
						
						if(!StringUtils.IsNullOrEmpty(documentTransferencia.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
							.get(0).getCdtr().getNm())){
							
							iso.setISO_115_ExtendedData(documentTransferencia.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
									.get(0).getCdtr().getNm());
						if(documentTransferencia.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
									.get(0).getCdtr().getId() != null){
							
							if(documentTransferencia.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
									.get(0).getCdtr().getId().getOrgId() != null){
								
								if(documentTransferencia.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
										.get(0).getCdtr().getId().getOrgId().getOthr() != null){
									
									if(!StringUtils.IsNullOrEmpty(documentTransferencia.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
											.get(0).getCdtr().getId().getOrgId().getOthr().get(0).getId())){
										
										iso.setISO_115_ExtendedData(iso.getISO_115_ExtendedData() + "|" + documentTransferencia
												.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
												.get(0).getCdtr().getId().getOrgId().getOthr().get(0).getId());
										if(documentTransferencia.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
												.get(0).getCdtr().getId().getOrgId().getOthr().get(0).getSchmeNm() != null){
											
											if(!StringUtils.IsNullOrEmpty(documentTransferencia.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
												.get(0).getCdtr().getId().getOrgId().getOthr().get(0).getSchmeNm().getCd())){
												
												iso.setISO_115_ExtendedData(iso.getISO_115_ExtendedData() + "|" + documentTransferencia
														.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
														.get(0).getCdtr().getId().getOrgId().getOthr().get(0)
														.getSchmeNm().getCd());
												
											}else {
												
												campo = "Tipo de Identificacion del cuentahabiente (Persona Juridica) receptor [FIToFICstmrCdtTrf."
														+ "CdtTrfTxInf.Cdtr.Id.OrgId"
														+ ".Othr.Id.SchmeNm.Cd] es nulo o vacio"
												        .toUpperCase();
												iso.setISO_039_ResponseCode("905");
												iso.setISO_039p_ResponseDetail(campo);
												iso.setISO_104_TranDescription("BE15");
												return iso;
											}
											
										}else{
											
											campo = "Tipo de identificacion del cuentahabiente receptor (Persona Juridica) "
													+ "[FIToFICstmrCdtTrf.CdtTrfTxInf.Cdtr.Id.OrgId"
													+ ".Othr.Id.SchmeNm] es nulo"
											        .toUpperCase();
											iso.setISO_039_ResponseCode("905");
											iso.setISO_039p_ResponseDetail(campo);
											iso.setISO_104_TranDescription("BE15");
											return iso;
										}
										
									}else{
										
										campo = "Nro. de Identificacion del cuentahabiente receptor (Persona Juridica) "
												+ "[FIToFICstmrCdtTrf.CdtTrfTxInf.Cdtr.Id.OrgId.Othr.Id] "
												+ "es nulo o vacio"
										        .toUpperCase();
										iso.setISO_039_ResponseCode("905");
										iso.setISO_039p_ResponseDetail(campo);
										return iso;
									}
									
								}else{
									
									campo = "Estrucura Cuentahabiente receptor (Persona Juridica) [FIToFICstmrCdtTrf."
											+ "CdtTrfTxInf.Cdtr.Id.OrgId.Othr] es nulo"
									        .toUpperCase();
									iso.setISO_039_ResponseCode("905");
									iso.setISO_039p_ResponseDetail(campo);
									return iso;
								}
								
							}else{
								
								if(documentTransferencia.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
										.get(0).getCdtr().getId().getPrvtId() != null){
									
									if(documentTransferencia.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
											.get(0).getCdtr().getId().getPrvtId().getOthr() != null){
										
										if(!StringUtils.IsNullOrEmpty(documentTransferencia.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
												.get(0).getCdtr().getId().getPrvtId().getOthr().get(0).getId())){
											
											iso.setISO_115_ExtendedData(iso.getISO_115_ExtendedData() + "|" + documentTransferencia.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
													.get(0).getCdtr().getId().getPrvtId().getOthr().get(0).getId());
											
											if(documentTransferencia.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
													.get(0).getCdtr().getId().getPrvtId().getOthr().get(0).getSchmeNm() != null){
												
												if(!StringUtils.IsNullOrEmpty(documentTransferencia.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
													.get(0).getCdtr().getId().getPrvtId().getOthr().get(0).getSchmeNm().getCd())){
													
													iso.setISO_115_ExtendedData(iso.getISO_115_ExtendedData() + "|" + documentTransferencia.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
															.get(0).getCdtr().getId().getPrvtId().getOthr().get(0).getSchmeNm().getCd());
													
												}else {
													
													campo = "Tipo Identidicacion Cuentahabiente (Persona Natural) receptor [FIToFICstmrCdtTrf."
															+ "CdtTrfTxInf.Cdtr.Id.PrvtId.Othr.id] es nulo"
													        .toUpperCase();
													iso.setISO_039_ResponseCode("905");
													iso.setISO_039p_ResponseDetail(campo);
													return iso;
												}
												
											}else{
												
												campo = "Estructura Cuentahabiente (Persona Natural) receptor [FIToFICstmrCdtTrf."
														+ "CdtTrfTxInf.Cdtr.Id.PrvtId.Othr.SchmeNm] es nulo"
												        .toUpperCase();
												iso.setISO_039_ResponseCode("905");
												iso.setISO_039p_ResponseDetail(campo);
												return iso;
											}
											
										}else{
											
											campo = "Identidicacion Cuentahabiente (Persona Natural) receptor [FIToFICstmrCdtTrf."
													+ "CdtTrfTxInf.Cdtr.Id.PrvtId.Othr.id] es nulo"
											        .toUpperCase();
											iso.setISO_039_ResponseCode("905");
											iso.setISO_039p_ResponseDetail(campo);
											return iso;
										}
										
										
									}else{
										
										campo = "Estrucura Cuentahabiente (Persona Natural) receptor [FIToFICstmrCdtTrf."
												+ "CdtTrfTxInf.Cdtr.Id.PrvtId.Othr] es nulo"
										        .toUpperCase();
										iso.setISO_039_ResponseCode("905");
										iso.setISO_039p_ResponseDetail(campo);
										return iso;
									}
									
								}else{
									
									campo = "Info Datos Cuentahabiente receptor (Natural o Juridica) [FIToFICstmrCdtTrf."
											+ "CdtTrfTxInf.Cdtr.Id] es nula"
									        .toUpperCase();
									iso.setISO_039_ResponseCode("905");
									iso.setISO_039p_ResponseDetail(campo);
									return iso;
								}						
							}
							
						}else{
							
							campo = "Estrucura Cuentahabiente receptor [FIToFICstmrCdtTrf."
									+ "CdtTrfTxInf.Cdtr.Id] es nula"
							        .toUpperCase();
							iso.setISO_039_ResponseCode("905");
							iso.setISO_039p_ResponseDetail(campo);
							return iso;
						}
							
						}else{
							
							campo = "Nombres del Cuentahabiente Receptor [FIToFICstmrCdtTrf"
									+ ".CdtTrfTxInf.Cdtr.Nm] son nulos o vacios"
							        .toUpperCase();
							iso.setISO_039_ResponseCode("905");
							iso.setISO_039p_ResponseDetail(campo);
							iso.setISO_104_TranDescription("BE21");
							return iso;
						}
						
					}else{
						
						campo = "Estructura informacion Cuentahabiente receptor "
								+ "[FIToFICstmrCdtTrf.CdtTrfTxInf.Cdtr] es nulo"
						        .toUpperCase();
						iso.setISO_039_ResponseCode("905");
						iso.setISO_039p_ResponseDetail(campo);
						return iso;
					}
					
					///SUBSTRUCTURA CdtrAcct (DATOS DE LA CUENTA ACREDITANTE EN LA EFI AUTORIZADORA)
					if(documentTransferencia.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
							.get(0).getCdtrAcct() != null){
						
						if(documentTransferencia.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
								.get(0).getCdtrAcct().getId() != null){
							
							if(documentTransferencia.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
									.get(0).getCdtrAcct().getId().getOthr() != null){
								
								if(!StringUtils.IsNullOrEmpty(documentTransferencia.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
									.get(0).getCdtrAcct().getId().getOthr().getId())){
									
									iso.setISO_103_AccountID_2(documentTransferencia.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
											.get(0).getCdtrAcct().getId().getOthr().getId());
									
								}else {
									
									campo = "Numero de Cuenta a Acreditar [FIToFICstmrCdtTrf."
											+ "CdtTrfTxInf.CdtrAcct.Id.Othr.Id] es nula o vacia"
									        .toUpperCase();
									iso.setISO_039_ResponseCode("905");
									iso.setISO_039p_ResponseDetail(campo);
									iso.setISO_104_TranDescription("AC01");
									return iso;
								}
								
							}else{
								
								campo = "Numero de Cuenta a Acreditar [FIToFICstmrCdtTrf."
										+ "CdtTrfTxInf.CdtrAcct.Id.Othr] es nulo"
								        .toUpperCase();
								iso.setISO_039_ResponseCode("905");
								iso.setISO_039p_ResponseDetail(campo);
								iso.setISO_104_TranDescription("AC01");
								return iso;
							}
							
						}else{
							
							campo = "Numero de Cuenta a Acreditar [FIToFICstmrCdtTrf."
									+ "CdtTrfTxInf.CdtrAcct.Id] es nulo"
							        .toUpperCase();
							iso.setISO_039_ResponseCode("905");
							iso.setISO_039p_ResponseDetail(campo);
							iso.setISO_104_TranDescription("AC01");
							return iso;
						}
						if(documentTransferencia.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
								.get(0).getCdtrAcct().getTp() != null){
							
							if(!StringUtils.IsNullOrEmpty(documentTransferencia.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
									.get(0).getCdtrAcct().getTp().getPrtry())){
								
								if(documentTransferencia.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
										.get(0).getCdtrAcct().getTp().getPrtry().equals("01")){
									iso.setISO_003_ProcessingCode(iso.getISO_003_ProcessingCode() + "20");
								}else if (documentTransferencia.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
										.get(0).getCdtrAcct().getTp().getPrtry().equals("02")) {
									iso.setISO_003_ProcessingCode(iso.getISO_003_ProcessingCode() + "10");
								}else {
									
									campo = "Tipo de la Cuenta Acreditante [FIToFICstmrCdtTrf."
											+ "CdtTrfTxInf.CdtrAcct.Tp] es nula o no es valida"
									        .toUpperCase();
									iso.setISO_039_ResponseCode("905");
									iso.setISO_039p_ResponseDetail(campo);
									return iso;
								}
								
							}else {
								
								campo = "Tipo de la Cuenta Acreditante [FIToFICstmrCdtTrf."
										+ "CdtTrfTxInf.CdtrAcct.Tp.Prtry] no es valida"
								        .toUpperCase();
								iso.setISO_039_ResponseCode("905");
								iso.setISO_039p_ResponseDetail(campo);
								return iso;
							}
							
						}else {
							
							campo = "Tipo de la Cuenta Acreditante [FIToFICstmrCdtTrf"
									+ ".CdtTrfTxInf.CdtrAcct.Tp] es nula"
							        .toUpperCase();
							iso.setISO_039_ResponseCode("905");
							iso.setISO_039p_ResponseDetail(campo);
							return iso;
						}
						if(!StringUtils.IsNullOrEmpty(documentTransferencia.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
								.get(0).getCdtrAcct().getCcy())){
							
							iso.setISO_121_ExtendedData(documentTransferencia.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
								.get(0).getCdtrAcct().getCcy());
							
						}else {
							
							campo = "Moneda de la cuenta Acreditante [FIToFICstmrCdtTrf."
									+ "CdtTrfTxInf.CdtrAcct.Ccy] es nulo"
							        .toUpperCase();
							iso.setISO_039_ResponseCode("905");
							iso.setISO_039p_ResponseDetail(campo);
							iso.setISO_104_TranDescription("AC09");
							return iso;
						}
						
					}else{
						
						campo = "Moneda de la cuenta Acreditante [FIToFICstmrCdtTrf."
								+ "CdtTrfTxInf.CdtrAcct] es nulo"
						        .toUpperCase();
						iso.setISO_039_ResponseCode("905");
						iso.setISO_039p_ResponseDetail(campo);
						iso.setISO_104_TranDescription("AC09");
						return iso;
					}
					///SUBSTRUCTURA InstrForNxtAgt (DATOS DE LA VENTANILLA CAJERO)
					
					if(documentTransferencia.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
							.get(0).getInstrForNxtAgt() != null){
						if(documentTransferencia.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
							.get(0).getInstrForNxtAgt().get(0).getInstrInf() != null){
							
							if(!StringUtils.IsNullOrEmpty(documentTransferencia.getFIToFICstmrCdtTrf().getCdtTrfTxInf()
							.get(0).getInstrForNxtAgt().get(0).getInstrInf())){
								
								iso.setISO_042_Card_Acc_ID_Code(documentTransferencia.getFIToFICstmrCdtTrf()
										.getCdtTrfTxInf()
										.get(0).getInstrForNxtAgt().get(0).getInstrInf());
								
							}else{
								
								campo = "Info Caja EFI Ordenante [FIToFICstmrCdtTrf.CdtTrfTxInf"
										+ ".InstrForNxtAgt.InstrInf] es nulo o vacio"
								        .toUpperCase();
								iso.setISO_039_ResponseCode("905");
								iso.setISO_039p_ResponseDetail(campo);
								return iso;
							}
							
						}else{
							
							campo = "Info Caja EFI Ordenante [FIToFICstmrCdtTrf.CdtTrfTxInf"
									+ ".InstrForNxtAgt.InstrInf] es nulo"
							        .toUpperCase();
							iso.setISO_039_ResponseCode("905");
							iso.setISO_039p_ResponseDetail(campo);
							return iso;
						}
						
					}else{
						
						campo = "Info Caja EFI Ordenante [FIToFICstmrCdtTrf"
								+ ".CdtTrfTxInf.InstrForNxtAgt] es nula"
						        .toUpperCase();
						iso.setISO_039_ResponseCode("905");
						iso.setISO_039p_ResponseDetail(campo);
						return iso;
					}
					
					////////////
					iso.setISO_000_Message_Type("1200");
					iso.setISO_018_MerchantType("0005");
					iso.setISO_024_NetworkId("555557");
					iso.setISO_039_ResponseCode("000");
					
				}else {
					
					campo = "Region [FIToFICstmrCdtTrf.GrpHdr] es nula"
							.toUpperCase();
					iso.setISO_039_ResponseCode("905");
					iso.setISO_039p_ResponseDetail(campo);
					return iso;
				}
				
			}else{
				
				iso.setISO_039_ResponseCode("905");
				iso.setISO_039p_ResponseDetail("TRAMA TRANSFERENCIA ELECTRONICA ES NULA");
				return iso;
			}
			
		} catch (Exception e) {
			
			log.WriteLogMonitor("Error modulo BceVCParser::parseDocumentDepositoVC ", TypeMonitor.error, e);
			iso.setISO_039_ResponseCode("905");
			iso.setISO_039p_ResponseDetail("ERROR " + campo + " " + GeneralUtils.ExceptionToString("INVALIDO ", e, true));
		}
		
		return iso;
	}
    public Iso8583 parseDocumentReversoTecnicoVC(DocumentReverso documentReverso, String IP){
    	Iso8583 iso = new Iso8583();
		String campo = null;
    	try {
			
    		if(documentReverso != null){
    			
    			if(documentReverso.getPmtRtr() != null){
    				
    				if(documentReverso.getPmtRtr().getGrpHdr() != null){
    					
    					if(!StringUtils.IsNullOrEmpty(documentReverso.getPmtRtr().getGrpHdr().getMsgId())){
    						if(documentReverso.getPmtRtr().getGrpHdr().getMsgId().length() != 32){
    							
    							campo = "Fecha Transaccion [FIToFICstmtTrf.GrpHdr.MsgId] es invalido"
        						        .toUpperCase();
    							iso.setISO_039_ResponseCode("905");
								iso.setISO_039p_ResponseDetail(campo);
								iso.setISO_104_TranDescription("AM01");
								return iso;
    							
    						}else
    							iso.setISO_011_SysAuditNumber(documentReverso.getPmtRtr().getGrpHdr().getMsgId());
    						
    						if(documentReverso.getPmtRtr().getGrpHdr().getCreDtTm() != null){
    							
    							campo = "Fecha Transaccion [FIToFICstmtTrf.GrpHdr.CreDtTm] es nulo"
        						        .toUpperCase();
        						Date date = documentReverso.getPmtRtr().getGrpHdr().getCreDtTm()
        								    .toGregorianCalendar().getTime();
        						
        						if(date.before(new Date())){
        							
        							iso.setISO_012_LocalDatetime(date);
        							
        							if(!StringUtils.IsNullOrEmpty(documentReverso.getPmtRtr().getGrpHdr().getNbOfTxs())){
        								
        								int numberTrx = Integer.parseInt(documentReverso.getPmtRtr().
        										      getGrpHdr().getNbOfTxs());
        								
        								if(numberTrx > 0 && numberTrx <= 1){
        									
        									iso.setISO_022_PosEntryMode(String.valueOf(numberTrx));
        									
        									if(documentReverso.getPmtRtr().getGrpHdr().isGrpRtr() != null){
        										
        										iso.setISO_036_Track3(String.valueOf(documentReverso.getPmtRtr()
        												.getGrpHdr().isGrpRtr()));
        										
        										if(documentReverso.getPmtRtr().getGrpHdr().getCtrlSum() != null){
        											
        											campo = "Monto de la Transaccion [PmtRtr.GrpHdr.CtrlSum] debe ser mayor a 0"
        													.toUpperCase();
        											Double monto = documentReverso.getPmtRtr()
        													.getGrpHdr().getCtrlSum().doubleValue();
        											if(monto > 0){
        												
        												iso.setISO_004_AmountTransaction(monto);
        												
        											}else {
        												
        												iso.setISO_039_ResponseCode("905");
        												iso.setISO_039p_ResponseDetail(campo);
        												iso.setISO_104_TranDescription("AM01");
        												return iso;
        											}
        											
        										}else{
        											
        											campo = "Monto Original de la Transaccion [PmtRtr."
															+ "OrgnlGrpInf.OrgnlMsgNmId] es nulo"
													        .toUpperCase();
													iso.setISO_039_ResponseCode("905");
													iso.setISO_039p_ResponseDetail(campo);
													return iso;
        										}
        										
        										if(documentReverso.getPmtRtr().getGrpHdr().getTtlRtrdIntrBkSttlmAmt() != null){
        											
        											if(!StringUtils.IsNullOrEmpty(documentReverso.getPmtRtr().
        													getGrpHdr().getTtlRtrdIntrBkSttlmAmt().getCcy())){
        												
        												iso.setISO_051_CardCurrCode(documentReverso.getPmtRtr().
            													getGrpHdr().getTtlRtrdIntrBkSttlmAmt().getCcy()
            													.equalsIgnoreCase("USD")?840:0);
        												if(documentReverso.getPmtRtr().getGrpHdr().getSttlmInf() != null){
        													
        													if(!StringUtils.IsNullOrEmpty(documentReverso.getPmtRtr()
        															.getGrpHdr().getSttlmInf().getSttlmMtd().value())){
        														
        														iso.setISO_114_ExtendedData("CLRG");
        														
        														if(documentReverso.getPmtRtr()
            															.getGrpHdr().getInstgAgt() != null){
        															
        															if(documentReverso.getPmtRtr()
                															.getGrpHdr().getInstgAgt().getFinInstnId() != null){
        																
        																if(!StringUtils.IsNullOrEmpty(documentReverso.getPmtRtr()
                															.getGrpHdr().getInstgAgt().getFinInstnId().getBICFI())){
        																	
        																	iso.setISO_BitMap((documentReverso.getPmtRtr()
                															.getGrpHdr().getInstgAgt().getFinInstnId().getBICFI()));
        																	
        																	iso.setISO_032_ACQInsID(iso.getISO_BitMap());
        																	
        																	if(documentReverso.getPmtRtr()
                        															.getGrpHdr().getInstdAgt() != null){
        																		
        																		if(documentReverso.getPmtRtr()
                            															.getGrpHdr().getInstdAgt().getFinInstnId() != null){
        																			
        																			if(!StringUtils.IsNullOrEmpty(documentReverso.getPmtRtr()
                            															.getGrpHdr().getInstdAgt().getFinInstnId().getBICFI())){
        																				
        																				if(documentReverso.getPmtRtr()
                                    															.getGrpHdr().getInstdAgt().getFinInstnId().getBICFI()
                                    															.equalsIgnoreCase(MemoryGlobal.BCE_Efi_VC)){
        																					
        																					iso.setISO_033_FWDInsID(documentReverso.getPmtRtr()
    		                            															.getGrpHdr().getInstdAgt().getFinInstnId().getBICFI());
        																					
        																					//// Estructura OrgnlGrpInf
        																					if(documentReverso.getPmtRtr().getOrgnlGrpInf() != null){
        																						
        																						if(!StringUtils.IsNullOrEmpty(documentReverso.getPmtRtr()
        																								.getOrgnlGrpInf().getOrgnlMsgId())){
        																							
        																							iso.setISO_044_AddRespData(documentReverso.getPmtRtr()
        																								.getOrgnlGrpInf().getOrgnlMsgId());
        																							
        																							iso.setISO_044_AddRespData(iso.getISO_044_AddRespData()
        																									.substring(0,30));
        																							
        																							if(!StringUtils.IsNullOrEmpty(documentReverso.getPmtRtr()
            																								.getOrgnlGrpInf().getOrgnlMsgNmId())){
        																								
        																								switch (documentReverso.getPmtRtr()
                																								.getOrgnlGrpInf().getOrgnlMsgNmId()
                																								) {
																										case "pacs.008.001.04":
																											
																											if(iso.getISO_044_AddRespData().startsWith("DVC")){							
																												iso.setISO_003_ProcessingCode("201000"); 
																											}else{
																												iso.setISO_003_ProcessingCode("011010");
																											}
																											break;
																										case "pacs.003.001.04":
																											iso.setISO_003_ProcessingCode("011000");
																											break;
																										
																										default:
																											
																											campo = "Pack Original de la Transaccion [PmtRtr."
	                                        																		+ "OrgnlGrpInf.OrgnlMsgNmId] no reconocido"
	                                            															        .toUpperCase();
	                                            															iso.setISO_039_ResponseCode("905");
	                                            															iso.setISO_039p_ResponseDetail(campo);
	                                            															return iso;
																											
																										}
        																								
        																								if(documentReverso.getPmtRtr()
                																								.getOrgnlGrpInf().getOrgnlCreDtTm() != null){
        																									
        																									iso.setISO_013_LocalDate(documentReverso.getPmtRtr()
                																								.getOrgnlGrpInf().getOrgnlCreDtTm().
                																								toGregorianCalendar().getTime());
        																									
        																									if(documentReverso.getPmtRtr()
                    																								.getOrgnlGrpInf().getRtrRsnInf() != null){
        																										
        																										if(documentReverso.getPmtRtr()
                        																								.getOrgnlGrpInf().getRtrRsnInf().get(0)
                        																								.getOrgtr()!= null){
        																											
        																											if(documentReverso.getPmtRtr()
                            																								.getOrgnlGrpInf().getRtrRsnInf().get(0)
                            																								.getOrgtr().getId() != null){
        																												
        																												if(documentReverso.getPmtRtr()
                                																								.getOrgnlGrpInf().getRtrRsnInf().get(0)
                                																								.getOrgtr().getId().getOrgId() != null){
        																													
        																													if(!StringUtils.IsNullOrEmpty(documentReverso.getPmtRtr()
                                																								.getOrgnlGrpInf().getRtrRsnInf().get(0)
                                																								.getOrgtr().getId().getOrgId().getAnyBIC())){
        																														
        																														if(!documentReverso.getPmtRtr()
                                        																								.getOrgnlGrpInf().getRtrRsnInf().get(0)
                                        																								.getOrgtr().getId().getOrgId().getAnyBIC()
                                        																								.equals(iso.getISO_032_ACQInsID())){
        																															
        																															iso.setISO_120_ExtendedData(documentReverso.getPmtRtr()
                                        																								.getOrgnlGrpInf().getRtrRsnInf().get(0)
                                        																								.getOrgtr().getId().getOrgId().getAnyBIC());
        																														}
        																														
        																														if(documentReverso.getPmtRtr()
                                        																								.getOrgnlGrpInf().getRtrRsnInf().get(0).getRsn() != null){
        																															
        																															if(!StringUtils.IsNullOrEmpty(documentReverso.getPmtRtr()
                                        																								.getOrgnlGrpInf().getRtrRsnInf().get(0).getRsn().getCd())){
        																																
        																																iso.setISO_121_ExtendedData(documentReverso.getPmtRtr()
                                        																								.getOrgnlGrpInf().getRtrRsnInf().get(0).getRsn().getCd());
        																																
        																																if(documentReverso.getPmtRtr()
                                                																								.getOrgnlGrpInf().getRtrRsnInf().get(0).getAddtlInf() != null){
        																																	
        																																	if(!StringUtils.IsNullOrEmpty(documentReverso.getPmtRtr()
                                                    																								.getOrgnlGrpInf().getRtrRsnInf().get(0)
                                                    																								.getAddtlInf().get(0))){
        																																		
        																																		iso.setISO_121_ExtendedData(iso.getISO_121_ExtendedData() + "|" + documentReverso.getPmtRtr()
                                                    																								.getOrgnlGrpInf().getRtrRsnInf().get(0)
                                                    																								.getAddtlInf().get(0));
        																																		
        																																		iso.setISO_000_Message_Type("1400");
                																																iso.setISO_018_MerchantType("0005");
                																																iso.setISO_024_NetworkId("555557");
                																																
                																																
        																																		wIso8583 isoRetrieve = new wIso8583();
        																																		isoRetrieve.setISO_000_Message_Type("1200");
        																																		isoRetrieve.setISO_044_AddRespData(iso.getISO_044_AddRespData());
        																																		isoRetrieve.setISO_024_NetworkId(iso.getISO_024_NetworkId());
        																																		isoRetrieve.setISO_018_MerchantType(iso.getISO_018_MerchantType());
        																																		isoRetrieve.setISO_004_AmountTransaction(iso.getISO_004_AmountTransaction());
        																												
                																																IsoRetrievalTransaction sql = new IsoRetrievalTransaction();
        																																		isoRetrieve = sql.RetrieveTransactionIso(isoRetrieve, 4);
        																																		if(isoRetrieve.getISO_039_ResponseCode().equals("000")){
        																																			
        																																			iso.setISO_002_PAN(isoRetrieve.getISO_002_PAN());
        																																			iso.setISO_003_ProcessingCode(isoRetrieve.getISO_003_ProcessingCode());
        																																			iso.setISO_041_CardAcceptorID(StringUtils.IsNullOrEmpty(isoRetrieve.getISO_041_CardAcceptorID())
        																																					?"XXXXXXXXXXX"
        																																					:isoRetrieve.getISO_041_CardAcceptorID());
        																																			iso.setISO_042_Card_Acc_ID_Code(isoRetrieve.getISO_042_Card_Acc_ID_Code());
        																																			iso.setISO_102_AccountID_1(isoRetrieve.getISO_102_AccountID_1());
        																																			iso.setISO_103_AccountID_2(isoRetrieve.getISO_103_AccountID_2());
        																																			iso.setISO_023_CardSeq(isoRetrieve.getISO_023_CardSeq());
        																																			iso.setISO_037_RetrievalReferenceNumber(iso.getISO_037_RetrievalReferenceNumber());
        																																			
        																																		}else{
        																																			
        																																			campo = "Transaccion Original (MsgId: " + documentReverso.getPmtRtr()
        									        																								.getOrgnlGrpInf().getOrgnlMsgId() + ") no existe"
                                                                                        															        .toUpperCase();
                                                                                        															iso.setISO_039_ResponseCode("905");
                                                                                        															iso.setISO_039p_ResponseDetail(campo.toUpperCase());
                                                                                        															
                                                                                        															final wIso8583 iiPrint = isoRetrieve;
                                                                                        															Thread tt = new Thread(new Runnable() {
																																						
																																						@Override
																																						public void run() {
																																							
																																							
																																							iiPrint.setWsTransactionConfig(new TransactionConfiguration
		            																																				(new TransactionConfig(iso.getISO_003_ProcessingCode(), 
		            																																				Integer.parseInt(iso.getISO_024_NetworkId()), 
		            																																				iso.getISO_018_MerchantType(), -1)));
																																							iiPrint.getWsTransactionConfig().setIp(IP);
		                                                                                        															LoggerConfig.WriteMonitor(iiPrint, iso);
																																							
																																						}
																																					});
                                                                                        															tt.start();
                                                                                        															
                                                                                        															return iso;
        																																		}
                																																
                																																iso.setISO_039_ResponseCode("000");
        																																		
        																																	}else{
        																																		
        																																		campo = "Detalle Error del Reverso [PmtRtr.OrgnlGrpInf."
                                                                                																		+ "RtrRsnInf[].AddtlInf[]] es mandatorio"
                                                                                    															        .toUpperCase();
                                                                                    															iso.setISO_039_ResponseCode("905");
                                                                                    															iso.setISO_039p_ResponseDetail(campo);
                                                                                    															return iso;
        																																	}
        																																	
        																																}else{
        																																	
        																																	campo = "Estructura Detalle Error del Reverso [PmtRtr.OrgnlGrpInf."
                                                                            																		+ "RtrRsnInf[].AddtlInf[]] es nula"
                                                                                															        .toUpperCase();
                                                                                															iso.setISO_039_ResponseCode("905");
                                                                                															iso.setISO_039p_ResponseDetail(campo);
                                                                                															return iso;
        																																}
        																																
        																																
        																															}else{
        																																
        																																campo = "Codigo Id Error del Reverso [PmtRtr.OrgnlGrpInf."
                                                                        																		+ "RtrRsnInf[].Rsn.Cd] es mandatoria"
                                                                            															        .toUpperCase();
                                                                            															iso.setISO_039_ResponseCode("905");
                                                                            															iso.setISO_039p_ResponseDetail(campo);
                                                                            															return iso;
        																															}
        																															
        																														}else{
        																															
        																															campo = "Estructura Razon del Reverso [PmtRtr.OrgnlGrpInf."
                                                                    																		+ "RtrRsnInf[].Rsn] es mandatoria"
                                                                        															        .toUpperCase();
                                                                        															iso.setISO_039_ResponseCode("905");
                                                                        															iso.setISO_039p_ResponseDetail(campo);
                                                                        															return iso;
        																														}
        																														
        																													}else{
        																														
        																														campo = "Estructura Entidad Originaria Reverso [PmtRtr.OrgnlGrpInf."
                                                                																		+ "RtrRsnInf[].Orgtr.IdOrgId.AnyBIC] es mandatoria"
                                                                    															        .toUpperCase();
                                                                    															iso.setISO_039_ResponseCode("905");
                                                                    															iso.setISO_039p_ResponseDetail(campo);
                                                                    															iso.setISO_104_TranDescription("AGNT");
                                                                    															return iso;
        																													}
        																													
        																												}else{
        																													
        																													campo = "Estructura Entidad Originaria Reverso [PmtRtr.OrgnlGrpInf."
                                                            																		+ "RtrRsnInf[].Orgtr.IdOrgId] es nula"
                                                                															        .toUpperCase();
                                                                															iso.setISO_039_ResponseCode("905");
                                                                															iso.setISO_039p_ResponseDetail(campo);
                                                                															iso.setISO_104_TranDescription("AGNT");
                                                                															return iso;
        																												}
        																												
        																											}else{
        																												
        																												campo = "Estructura Entidad Originaria Reverso [PmtRtr.OrgnlGrpInf."
                                                        																		+ "RtrRsnInf[].Orgtr.Id] es nula"
                                                            															        .toUpperCase();
                                                            															iso.setISO_039_ResponseCode("905");
                                                            															iso.setISO_039p_ResponseDetail(campo);
                                                            															iso.setISO_104_TranDescription("AGNT");
                                                            															return iso;
        																											}
        																											
        																										}else{
        																											
        																											campo = "Estructura Entidad Originaria Reverso [PmtRtr.OrgnlGrpInf."
                                                    																		+ "RtrRsnInf[].Orgtr] es nula"
                                                        															        .toUpperCase();
                                                        															iso.setISO_039_ResponseCode("905");
                                                        															iso.setISO_039p_ResponseDetail(campo);
                                                        															iso.setISO_104_TranDescription("AGNT");
                                                        															return iso;
        																										}
        																										
        																									}else{
        																										
        																										campo = "Estructura Entidad Originaria Reverso [PmtRtr.OrgnlGrpInf."
                                                																		+ "RtrRsnInf] es nula"
                                                    															        .toUpperCase();
                                                    															iso.setISO_039_ResponseCode("905");
                                                    															iso.setISO_039p_ResponseDetail(campo);
                                                    															iso.setISO_104_TranDescription("AGNT");
                                                    															return iso;
        																									}
        																									
        																								}else{
        																									
        																									campo = "Fecha Original de la Transaccion [PmtRtr.OrgnlGrpInf."
                                            																		+ "OrgnlCreDtTm] es nula"
                                                															        .toUpperCase();
                                                															iso.setISO_039_ResponseCode("905");
                                                															iso.setISO_039p_ResponseDetail(campo);
                                                															return iso;
        																								}
        																								
        																							}else{
        																								
        																								campo = "Pack Original de la Transaccion [PmtRtr.GrpHdr."
                                        																		+ "OrgnlGrpInf.OrgnlMsgNmId] es mandatorio"
                                            															        .toUpperCase();
                                            															iso.setISO_039_ResponseCode("905");
                                            															iso.setISO_039p_ResponseDetail(campo);
                                            															return iso;
        																							}
        																							
        																							
        																						}else{
        																							
        																							campo = "Mensaje Id Original [PmtRtr.GrpHdr."
                                    																		+ "OrgnlGrpInf.OrgnlMsgId] es mandatorio"
                                        															        .toUpperCase();
                                        															iso.setISO_039_ResponseCode("905");
                                        															iso.setISO_039p_ResponseDetail(campo);
                                        															return iso;
        																						}
        																						
        																					}else{
        																						
        																						campo = "Estructura Entidad Autorizadora [PmtRtr.GrpHdr."
                                																		+ "InstgAgt.FinInstnId.BICFI] no coincide con nuestra Institucion"
                                    															        .toUpperCase();
                                    															iso.setISO_039_ResponseCode("905");
                                    															iso.setISO_039p_ResponseDetail(campo);
                                    															iso.setISO_104_TranDescription("AGNT");
                                    															return iso;
        																					}
        																					
        																				}else{
        																					
        																					campo = "Entidad Autorizadora [PmtRtr.GrpHdr."
                            																		+ "InstgAgt.FinInstnId.BICFI] no coincide con nuestra Institucion"
                                															        .toUpperCase();
                                															iso.setISO_039_ResponseCode("905");
                                															iso.setISO_039p_ResponseDetail(campo);
                                															iso.setISO_104_TranDescription("AGNT");
                                															return iso;
        																				}
		        																				
        																				
        																			}else{
        																				
        																				campo = "Estructura Entidad Autorizadora [PmtRtr.GrpHdr."
                        																		+ "InstgAgt.FinInstnId.BICFI] es mandatoria"
                            															        .toUpperCase();
                            															iso.setISO_039_ResponseCode("905");
                            															iso.setISO_039p_ResponseDetail(campo);
                            															iso.setISO_104_TranDescription("AGNT");
                            															return iso;
        																			}
        																			
        																		}else{
        																			
        																			campo = "Estructura Entidad Autorizadora [PmtRtr.GrpHdr."
                    																		+ "InstgAgt.FinInstnId] es nula"
                        															        .toUpperCase();
                        															iso.setISO_039_ResponseCode("905");
                        															iso.setISO_039p_ResponseDetail(campo);
                        															iso.setISO_104_TranDescription("AGNT");
                        															return iso;
        																		}
        																		
        																	}else{
        																		
        																		campo = "Estructura Entidad Autorizadora [PmtRtr.GrpHdr."
                																		+ "InstgAgt] es nula"
                    															        .toUpperCase();
                    															iso.setISO_039_ResponseCode("905");
                    															iso.setISO_039p_ResponseDetail(campo);
                    															iso.setISO_104_TranDescription("AGNT");
                    															return iso;
        																	}
        																	
        																}else{
        																	
        																	campo = "Estructura Entidad Intermedia [PmtRtr.GrpHdr."
            																		+ "InstdAgt.FinInstnId.BICFI] es mandatoria"
                															        .toUpperCase();
                															iso.setISO_039_ResponseCode("905");
                															iso.setISO_039p_ResponseDetail(campo);
                															iso.setISO_104_TranDescription("RC11");
                															return iso;
        																}
        																
        															}else{
        																
        																campo = "Estructura Entidad Intermedia [PmtRtr.GrpHdr."
        																		+ "InstdAgt.FinInstnId] es nula"
            															        .toUpperCase();
            															iso.setISO_039_ResponseCode("905");
            															iso.setISO_039p_ResponseDetail(campo);
            															iso.setISO_104_TranDescription("RC11");
            															return iso;
        															}
        															
        														}else{
        															
        															campo = "Estructura Entidad Intermedia [PmtRtr.GrpHdr.InstdAgt] es nula"
        															        .toUpperCase();
        															iso.setISO_039_ResponseCode("905");
        															iso.setISO_039p_ResponseDetail(campo);
        															iso.setISO_104_TranDescription("RC11");
        															return iso;
        														}
        														
        													}else{
        														
        														campo = "Campo [PmtRtr.GrpHdr.SttlmInf.SttlmMtd] es mandatorio"
                            									        .toUpperCase();
                            									iso.setISO_039_ResponseCode("905");
                            									iso.setISO_039p_ResponseDetail(campo);
                            									return iso;
        													}
        													
        												}else{
        												
        													campo = "Estructura [PmtRtr.GrpHdr.SttlmInf] es nula"
                        									        .toUpperCase();
                        									iso.setISO_039_ResponseCode("905");
                        									iso.setISO_039p_ResponseDetail(campo);
                        									return iso;
        												}
        												
        											}else{
        												
        												campo = "Moneda de la Transaccion [PmtRtr.GrpHdr.TtlRtrdIntrBkSttlmAmt.Ccy] es mandatoria"
                    									        .toUpperCase();
                    									iso.setISO_039_ResponseCode("905");
                    									iso.setISO_039p_ResponseDetail(campo);
                    									iso.setISO_104_TranDescription("AC09");
                    									return iso;
        												
        											}
        											        											
        										}else{
        											
        											campo = "Estructura [PmtRtr.GrpHdr.TtlRtrdIntrBkSttlmAmt] es nula"
                									        .toUpperCase();
                									iso.setISO_039_ResponseCode("905");
                									iso.setISO_039p_ResponseDetail(campo);
                									return iso;
        										}
        										
        									}else{
        										
        										campo = "Estructura [PmtRtr.GrpHdr.GrpRtr] es nula"
            									        .toUpperCase();
            									iso.setISO_039_ResponseCode("905");
            									iso.setISO_039p_ResponseDetail(campo);
            									return iso;
        									}	
        									
        								}else{
        									
        									campo = "Numero de Transacciones [PmtRtr.GrpHdr.NbOfTxs] invalido, "
        											+ "(No se soporta mas de una transaccion)"
        									        .toUpperCase();
        									iso.setISO_039_ResponseCode("905");
        									iso.setISO_039p_ResponseDetail(campo);
        									iso.setISO_104_TranDescription("AM19");
        									return iso;
        								}
        								
        							}else {
        								
        								campo = "Numero de Transacciones [FIToFICstmrCdtTrf.GrpHdr.NbOfTxs] es nulo o vacio"
        								        .toUpperCase();
        								iso.setISO_039_ResponseCode("905");
        								iso.setISO_039p_ResponseDetail(campo);
        								iso.setISO_104_TranDescription("AM19");
        								return iso;
        							}
        
        						}else{
        							
        							campo = "Fecha Transaccion [PmtRtr.GrpHdr.CreDtTm] es invalido"
        							        .toUpperCase();
        							iso.setISO_039_ResponseCode("905");
        							iso.setISO_039p_ResponseDetail(campo);
        							return iso;
        						}
    							
    						}else{
    							
    							campo = "Fecha de la transaccion [PmtRtr.GrpHdr.CreDtTm] es nula";
                				iso.setISO_039_ResponseCode("905");
                				iso.setISO_039p_ResponseDetail(campo);
                				iso.setISO_104_TranDescription("DT01");
                				return iso;
    						}
    						
    					}else{
    						
    						campo = "Mensaje unico transaccion [PmtRtr.GrpHdr.MsgId] es mandatorio";
            				iso.setISO_039_ResponseCode("905");
            				iso.setISO_039p_ResponseDetail(campo);
            				return iso;
    					}
    					
    				}else{
    					
    					campo = "Estructura Reverso [PmtRtr.GrpHdr] es nula";
        				iso.setISO_039_ResponseCode("905");
        				iso.setISO_039p_ResponseDetail(campo);
        				return iso;
    				}
    				
    			}else{
    				
    				campo = "Estructura Reverso [PmtRtr] es nula";
    				iso.setISO_039_ResponseCode("905");
    				iso.setISO_039p_ResponseDetail(campo);
    				return iso;
    			}
    			/////
    			
    		}else{
    			
    			iso.setISO_039_ResponseCode("905");
				iso.setISO_039p_ResponseDetail("TRAMA REVERSO ES NULA");
				return iso;
    		}
    		
		} catch (Exception e) {
			
			log.WriteLogMonitor("Error modulo BceVCParser::parseDocumentReversoTecnicoVC ", TypeMonitor.error, e);
			iso.setISO_039_ResponseCode("905");
			iso.setISO_039p_ResponseDetail("ERROR " + campo + " " + GeneralUtils.ExceptionToString("INVALIDO ", e, true));
		}
    	
    	return iso;
    }
}

