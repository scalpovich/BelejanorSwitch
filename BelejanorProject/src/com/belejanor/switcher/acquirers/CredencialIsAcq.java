package com.belejanor.switcher.acquirers;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.belejanor.switcher.credencial.BalanceCredencialRequest;
import com.belejanor.switcher.credencial.BalanceCredencialResponse;
import com.belejanor.switcher.credencial.DebitCredencialRequest;
import com.belejanor.switcher.credencial.DebitCredencialResponse;
import com.belejanor.switcher.credencial.Movements;
import com.belejanor.switcher.credencial.MovementsCredencialRequest;
import com.belejanor.switcher.credencial.MovementsCredencialResponse;
import com.belejanor.switcher.credencial.Movimientos;
import com.belejanor.switcher.credencial.RevertCredencialRequest;
import com.belejanor.switcher.credencial.RevertCredencialResponse;
import com.belejanor.switcher.cscoreswitch.Iso8583;
import com.belejanor.switcher.cscoreswitch.csProcess;
import com.belejanor.switcher.memcached.MoneyIso4127;
import com.belejanor.switcher.parser.CredencialParser;
import com.belejanor.switcher.sqlservices.IsoRetrievalTransaction;
import com.belejanor.switcher.utils.GeneralUtils;
import com.belejanor.switcher.utils.Iterables;
import com.belejanor.switcher.utils.SerializationObject;
import com.belejanor.switcher.utils.StringUtils;

public class CredencialIsAcq {

	
	public BalanceCredencialResponse trxBalanceQuery(BalanceCredencialRequest debitRq, String IP) {
		
		BalanceCredencialResponse res = null;
		Iso8583 iso = new Iso8583();
		csProcess processor = new csProcess();
		IsoRetrievalTransaction sql = null;
		String IdentificadorOriginal = debitRq.getIdentificador();
		try {
			
			sql = new IsoRetrievalTransaction();
			res = new BalanceCredencialResponse();
			CredencialParser parser = new CredencialParser();
			iso = parser.parseCredencialToIso(debitRq);
			
			iso = sql.executeValidationTDDSaiBank(iso);
			if(iso.getISO_039_ResponseCode().equals("000")) {
			
				if(iso.getISO_090_OriginalData().equalsIgnoreCase("ACT")) {
					iso = processor.ProcessTransactionMain(iso, IP);
					res = new BalanceCredencialResponse();
					if(iso.getISO_039_ResponseCode().equals("000")){
						res.setSaldoDisponible(Double.parseDouble
						(iso.getISO_054_AditionalAmounts().substring(28,40))/100);
					}
					
				}else {
					
					iso.setISO_039_ResponseCode("300");
					iso.setISO_039p_ResponseDetail("LA TARJETA NO ESTA ACTIVA");
				}
			}
			
			res.setCodigoRespuesta(getHomologaError(iso));
			res.setMensajeRespuesta(iso.getISO_039p_ResponseDetail().replace("<*>", ""));
			res.setFechaOperacion(iso.getISO_012_LocalDatetime());
			//res.setIdentificador(iso.getISO_011_SysAuditNumber());
			res.setIdentificador(iso.getISO_121_ExtendedData());
			
		} catch (Exception e) {
			
			res = new BalanceCredencialResponse();
			res.setCodigoRespuesta(909);
			res.setMensajeRespuesta(GeneralUtils.ExceptionToString("ERROR EN PROCESOS ", e, false));
			res.setFechaOperacion(iso.getISO_012_LocalDatetime() == null ? new Date(): iso.getISO_012_LocalDatetime());
			//res.setIdentificador(debitRq.getIdentificador());
			res.setIdentificador(iso.getISO_121_ExtendedData());
		}finally {
			
			res.setIdentificador(IdentificadorOriginal);
		}
		return res;
	}
	
	public DebitCredencialResponse trxDebit(DebitCredencialRequest debitRq, String IP){
		
		DebitCredencialResponse res = null;
		Iso8583 iso = new Iso8583();
		csProcess processor = new csProcess();
		IsoRetrievalTransaction sql = null;
		String identificadorOriginal = debitRq.getIdentificador();
		try {
			
			sql = new IsoRetrievalTransaction();
			res = new DebitCredencialResponse();
			CredencialParser parser = new CredencialParser();
					
			iso = parser.parseCredencialToIso(debitRq);
			/*Valido el estado de la tarjeta ACT es activo*/
			iso = sql.executeValidationTDDSaiBank(iso);
			if(iso.getISO_039_ResponseCode().equals("000")) {
				
				if(iso.getISO_090_OriginalData().equalsIgnoreCase("ACT")) {
					iso = processor.ProcessTransactionMain(iso, IP);
					if(iso.getISO_039_ResponseCode().equals("000")){
						res.setSaldoAnterior(Double.parseDouble
						(iso.getISO_054_AditionalAmounts().substring(28,40))/100);
						res.setSaldoPosterior(Math.rint((res.getSaldoAnterior() - iso.getISO_004_AmountTransaction())*100)/100);
					}
				}else {
					
					iso.setISO_039_ResponseCode("300");
					iso.setISO_039p_ResponseDetail("LA TARJETA NO ESTA ACTIVA");
				}
			}
								
			res.setCodigoRespuesta(getHomologaError(iso));
			res.setMensajeRespuesta(iso.getISO_039p_ResponseDetail().replace("<*>", ""));
			res.setFechaOperacion(iso.getISO_012_LocalDatetime());
			//res.setIdentificador(iso.getISO_011_SysAuditNumber());
			res.setIdentificador(iso.getISO_121_ExtendedData());
			res.setIdentificadorEMI(iso.getISO_037_RetrievalReferenceNumber());
			res.setFechaOperacion(iso.getISO_007_TransDatetime());
			res.setMoneda((int)iso.getISO_051_CardCurrCode());
			res.setIdentificadorEMI(iso.getISO_038_AutorizationNumber());
			
		}catch (Exception e) {
			
			res = new DebitCredencialResponse();
			res.setCodigoRespuesta(909);
			res.setMensajeRespuesta(GeneralUtils.ExceptionToString("ERROR EN PROCESOS ", e, false));
			res.setFechaOperacion(iso.getISO_012_LocalDatetime() == null ? new Date(): iso.getISO_012_LocalDatetime());
			res.setIdentificador(debitRq.getIdentificador());
			res.setIdentificadorEMI(iso.getISO_037_RetrievalReferenceNumber());
			//res.setIdentificador(iso.getISO_011_SysAuditNumber());
			res.setIdentificador(iso.getISO_121_ExtendedData());
			res.setMoneda((int)iso.getISO_051_CardCurrCode());
			
		}finally {
			
			res.setIdentificador(identificadorOriginal);
		}	
		
		return res;
	}
	
	@SuppressWarnings("unchecked")
	public MovementsCredencialResponse trxMovementsAccount(MovementsCredencialRequest movReq, String IP){
		
		MovementsCredencialResponse response = null;
		Iso8583 iso = new Iso8583();
		csProcess processor = new csProcess();
		String IdentificadorOriginal = movReq.getIdentificador();
		IsoRetrievalTransaction sql = null;
		try {
			
			sql = new IsoRetrievalTransaction();
			response = new MovementsCredencialResponse();
			CredencialParser parser = new CredencialParser();
			iso = parser.parseCredencialToIso(movReq);
			
			iso = sql.executeValidationTDDSaiBank(iso);
			if(iso.getISO_039_ResponseCode().equals("000")) {
			
				if(iso.getISO_090_OriginalData().equalsIgnoreCase("ACT")) {
					iso = processor.ProcessTransactionMain(iso, IP);
					Movimientos mov_ = null;
					if(iso.getISO_039_ResponseCode().equals("000")){
						
						List<Iterables> lst = new ArrayList<>();
						lst = (List<Iterables>) 
					    SerializationObject.XMLToObject(iso.getISO_114_ExtendedData());
						List<Movements> movements = new ArrayList<Movements>();
						for (Iterables it : lst) {
							
							Movements mov = new Movements();
							DateFormat fechaHora = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
							Date date = fechaHora.parse(it.getIterarors().get("FREAL"));
							mov.setFecha(date);
							
							mov.setMoneda(StringUtils.IsNullOrEmpty(MoneyIso4127.getCodMoneyFromFitCodeMoney
										  (it.getIterarors().get("CMONEDA_MOVIMIENTO")))
									      ? 840 : Integer.parseInt(MoneyIso4127.getCodMoneyFromFitCodeMoney
									       (it.getIterarors().get("CMONEDA_MOVIMIENTO"))));
							mov.setTerminal(it.getIterarors().get("CTERMINAL"));
							mov.setIdentificador(it.getIterarors().get("NUMEROMENSAJE"));
							mov.setMonto(Double.parseDouble(it.getIterarors().get("VALORMONEDACUENTA")));
							if(it.getIterarors().get("DEBITOCREDITO").equalsIgnoreCase("C"))
								mov.setTipoMovimiento("CREDITO");
							else
								mov.setTipoMovimiento("DEBITO");
							movements.add(mov);
						}
						mov_ = new Movimientos();
						mov_.setMovimiento(movements);
						response.setMov(mov_);
					}else
						response.setMov(new Movimientos());
				}else {
					
					iso.setISO_039_ResponseCode("300");
					iso.setISO_039p_ResponseDetail("LA TARJETA NO ESTA ACTIVA");
				}
			}
			
			response.setFechaOperacion(iso.getISO_012_LocalDatetime());
			response.setCodigoRespuesta(getHomologaError(iso));
			response.setMensajeRespuesta(iso.getISO_039p_ResponseDetail());
			//response.setIdentificador(iso.getISO_011_SysAuditNumber());
			response.setIdentificador(iso.getISO_121_ExtendedData());
			
		} catch (Exception e) {
			
			e.printStackTrace();
		}finally {
			
			response.setIdentificador(IdentificadorOriginal);
		}
		return response;
	}
	
	public RevertCredencialResponse trxReverse(RevertCredencialRequest revertRq, String IP){
		
		RevertCredencialResponse res = null;
		Iso8583 iso = new Iso8583();
		csProcess processor = new csProcess();
		String IdentificadorOriginal = revertRq.getIdentificador();
		
		try {
			
			res = new RevertCredencialResponse();
			CredencialParser parser = new CredencialParser();
			iso = parser.parseCredencialToIso(revertRq);
			iso = processor.ProcessTransactionMain(iso, IP);
			res.setCodigoRespuesta(getHomologaError(iso));
			res.setMensajeRespuesta(iso.getISO_039p_ResponseDetail().replace("<*>", ""));
			res.setFechaOperacion(iso.getISO_012_LocalDatetime());
			//res.setIdentificador(iso.getISO_011_SysAuditNumber());
			res.setIdentificador(iso.getISO_121_ExtendedData());
			res.setIdentificadorEMI(iso.getISO_037_RetrievalReferenceNumber());
			res.setFechaOperacion(iso.getISO_007_TransDatetime());
			res.setIdentificadorEMI(iso.getISO_038_AutorizationNumber());
			
		}catch (Exception e) {
			
			res = new RevertCredencialResponse();
			res.setCodigoRespuesta(909);
			res.setMensajeRespuesta(GeneralUtils.ExceptionToString("ERROR EN PROCESOS ", e, false));
			res.setFechaOperacion(iso.getISO_012_LocalDatetime() == null ? new Date(): iso.getISO_012_LocalDatetime());
			res.setIdentificador(revertRq.getIdentificador());
			res.setIdentificadorEMI(iso.getISO_037_RetrievalReferenceNumber());
			//res.setIdentificador(iso.getISO_011_SysAuditNumber());
			res.setIdentificador(iso.getISO_121_ExtendedData());
			
		}finally {
			
			res.setIdentificador(IdentificadorOriginal);
		}	
		
		return res;
	}
	
    protected int getHomologaError(Iso8583 iso){
		
		int error = -11;
		try {
			
			switch (iso.getISO_039_ResponseCode()) {
			
			case "000":
				error = -1;
				break;
			case "114":
			case "218":
			case "214":
				if(iso.getISO_003_ProcessingCode().startsWith("31"))
					error = 303;
				else if(iso.getISO_003_ProcessingCode().startsWith("314"))
					error = 403;
				else
					error = 103;
				break;
			case "116":
			case "216":
			case "507":
				if(iso.getISO_003_ProcessingCode().startsWith("31"))
					error = 305;
				else if(iso.getISO_003_ProcessingCode().startsWith("314"))
					error = 405;
				else
					error = 108;
				break;
			case "120":
			case "123":
				error = 104;
				break;
			case "907":
				if(iso.getISO_003_ProcessingCode().startsWith("31"))
					error = 107;
				else if(iso.getISO_003_ProcessingCode().startsWith("314"))
					error = 404;
				else
					error = 304;
				break;
			case "215":
				error = 103;
				break;
			case "601":
				error = 205;
				break;
			case "606":
				error = 204;
				break;
			case "602":
				error = 205;
				break;
			case "300":
				error = 102;
				break;
			default:
				error = 191;//Integer.parseInt(iso.getISO_039_ResponseCode());
				break;
			}
			
		} catch (Exception e) {
			
			return 191;
		}
		return error;
	}
}
