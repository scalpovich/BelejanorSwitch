package com.belejanor.switcher.authorizations;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import org.apache.axis2.databinding.types.UnsignedByte;
import org.tempuri.ConfirmaTransaccionStub.DatosConfirmacion;
import org.tempuri.ConfirmaTransaccionStub.Reply_SW;
import org.tempuri.Service1Stub.Reply_Structure_SBD;
import org.tempuri.Service1Stub.Request_Structure_SBD;
import com.belejanor.switcher.cscoreswitch.wIso8583;
import com.belejanor.switcher.logger.Logger;
import com.belejanor.switcher.logger.LoggerConfig.TypeLog;
import com.belejanor.switcher.logger.LoggerConfig.TypeMonitor;
import com.belejanor.switcher.logger.LoggerConfig.TypeWriteLog;
import com.belejanor.switcher.memcached.Config;
import com.belejanor.switcher.memcached.MemoryGlobal;
import com.belejanor.switcher.utils.FormatUtils;
import com.belejanor.switcher.utils.GeneralUtils;
import com.belejanor.switcher.utils.StringUtils;
import com.fitbank.middleware.facilito.ConsultaTransaccionesFacilito;
import com.fitbank.middleware.facilito.ConsumeServiceFacilito;

public class FacilitoPay {
	
	private Logger log;
	
	public FacilitoPay(){
		
		log = new Logger();
	}

	public wIso8583 ExecuteTran(wIso8583 iso){
		
		try {
			
			System.out.println("***115 en FacilitoPay" + iso.getISO_115_ExtendedData());
			iso.setISO_124_ExtendedData(iso.getISO_115_ExtendedData());
			putCertificate();
			
			ConsumeServiceFacilito service = new ConsumeServiceFacilito(MemoryGlobal.urlFacilito, 
					 10000, iso.getWsTransactionConfig().getProccodeTimeOutValue());
			
			Config cnf = new Config();
			cnf = cnf.getConfigSystem(iso.getISO_018_MerchantType());
			if(cnf != null){
				
				Request_Structure_SBD req = new Request_Structure_SBD();
				req.setTRACE_SW(MemoryGlobal.facilitoTRACE_SW);
				req.setTokenData(MemoryGlobal.facilitoTOKEN);
				req.setAba_adquirente(Integer.parseInt(MemoryGlobal.facilitoABA));
				req.setCodigo_autorizacion(MemoryGlobal.facilitoCODIGO_AUTORIZACION);
				req.setCodigo_terminal(Arrays.asList(cnf.getCfg_Valor().split("-")).get(1));
				req.setCodigo_transaccion(Integer.parseInt(iso.getISO_003_ProcessingCode()));
				
				
				if(StringUtils.IsNullOrEmpty(iso.getISO_122_ExtendedData().trim()))
					req.setDato_factura("0");
				else
					req.setDato_factura(iso.getISO_122_ExtendedData());
				req.setFecha_transaccion(FormatUtils.DateToString(iso.getISO_012_LocalDatetime(), "yyyyMMdd"));
				req.setHora_transaccion(FormatUtils.DateToString(iso.getISO_012_LocalDatetime(), "HHmmss"));
				req.setImpuestos(0);
				UnsignedByte un = new UnsignedByte();
				un.setValue(0L);
				req.setIndicador_reverso(un);
				req.setMoneda((short)iso.getISO_049_TranCurrCode());
				req.setNumero_contrato(MemoryGlobal.facilitoNUMERO_CONTRATO);
				req.setNumero_factura("0");
				req.setOperador(1);
				req.setProducto(iso.getISO_024_NetworkId());
				
				String supa = "<XML_ADD><NUM_CUOTAS>0</NUM_CUOTAS><ID_ORDEN>"+ iso.getISO_055_EMV() +"</ID_ORDEN><TIPO_PAGO>23</TIPO_PAGO></XML_ADD> ";
				
				/*Pensiones alimenticias SUPA Consulta*/
				if(iso.getISO_003_ProcessingCode().equals("310127"))
					req.setXML_ADD(supa);
				
				req.setReferencia(iso.getISO_002_PAN());
				req.setSbd_password(MemoryGlobal.facilitoCLAVE);
				req.setSbd_usuario(MemoryGlobal.facilitoUSUARIO);
				req.setSecuencia_adquirente(Integer.parseInt(iso.getISO_011_SysAuditNumber()));
				req.setSecuencia_switch(0);
				req.setSeguridad("1");
				UnsignedByte un_ = new UnsignedByte();
				un_.setValue(Long.parseLong(Arrays.asList(cnf.getCfg_Valor().split("-")).get(0)));
				req.setServicio_bancario(un_);
				req.isValor_pagadoSpecified();
				req.setValor_pagado(iso.getISO_004_AmountTransaction());
				req.isValor_subtotalSpecified();
				req.setValor_subtotal(iso.getISO_008_BillFeeAmount());
				req.isValor_totalSpecified();
				double valor_total = FormatUtils.TruncateDouble((iso.getISO_004_AmountTransaction() + 
				           iso.getISO_008_BillFeeAmount()), 2);
				req.setValor_total(valor_total);
				
				if(iso.getISO_003_ProcessingCode().startsWith("0")){
					req.setDato_factura(iso.getISO_121_ExtendedData());
					req.setXML_DATA(StringUtils.stripEspecial(iso.getISO_115_ExtendedData()));
					
					/*Pensiones alimenticias SUPA PAGO*/
					if(iso.getISO_003_ProcessingCode().equals("010127")) {
						if(!StringUtils.IsNullOrEmpty(iso.getISO_114_ExtendedData())) {
							req.setXML_ADD(iso.getISO_114_ExtendedData());
						}
					}
				}
				Reply_Structure_SBD response = new Reply_Structure_SBD();
				log.WriteLog(req, TypeLog.facilito, TypeWriteLog.file);
			
				
				if(iso.getISO_000_Message_Type().equals("1400")){
					if(Arrays.asList(iso.getISO_090_OriginalData().split("-")).get(0).equals("07")){
						UnsignedByte unR = new UnsignedByte();
						unR.setValue(07L);
						req.setIndicador_reverso(unR);
						UnsignedByte unB = new UnsignedByte();
						unB.setValue(Long.parseLong(Arrays.asList(cnf.getCfg_Valor().split("-")).get(0)));
						req.setServicio_bancario(unB);
					}else{
						
						UnsignedByte unR = new UnsignedByte();
						unR.setValue(02L);
						req.setIndicador_reverso(unR);
					}
					if(!iso.getISO_BitMap().equals("REVER_DIRECT")) {
						iso = ConfirmaTransaccion(iso);
						if(iso.getISO_039_ResponseCode().equals("62")){
							iso.setISO_039_ResponseCode(StringUtils.padLeft(iso.getISO_039_ResponseCode(), 3, "0"));
							return iso;
						}
					}
				}
				iso.getTickAut().reset();
				iso.getTickAut().start();
				response = service.ExecuteServiceFacilito(req);
				iso.getTickAut().stop();
				if(service.isFlagError()){
			
					iso.setISO_039_ResponseCode(response.getCODIGO_RESULTADO());
					if(StringUtils.IsNullOrEmpty(response.getMENSAJE())){
						iso.setISO_039p_ResponseDetail(GetCodeError(response.getCODIGO_RESULTADO()));
					}
					else{
						iso.setISO_039p_ResponseDetail(response.getMENSAJE());
					}
					iso.setWsIso_LogStatus(2);
					if(response.getCODIGO_RESULTADO().equals("000")){
						
						int cod = Integer.parseInt(iso.getISO_011_SysAuditNumber());
						if(response.getSECUENCIA_ADQUIRENTE().equalsIgnoreCase( String.valueOf(cod))){	
							iso.setISO_034_PANExt(response.getCEDULARUC());
							iso.setISO_004_AmountTransaction(response.getVALOR_TOTAL() == 0 ? 
									iso.getISO_004_AmountTransaction():response.getVALOR_TOTAL());
							iso.setISO_008_BillFeeAmount(response.getCOMISION() == 0 ? 
									iso.getISO_004_AmountTransaction():response.getCOMISION());
							if(iso.getISO_003_ProcessingCode().startsWith("3"))
								iso.setISO_044_AddRespData(response.getNOMBRE());
							else
								iso.setISO_044_AddRespData(response.getCOD_PAGO());
							
							//if(iso.getISO_003_ProcessingCode().equals("010127"))
								
							
							iso.setISO_037_RetrievalReferenceNumber(response.getSECUENCIA_SWITCH());
							iso.setISO_038_AutorizationNumber(StringUtils.IsNullOrEmpty(response.getCODIGO_AUTORIZACION()) ? "S/D" 
															:  response.getCODIGO_AUTORIZACION());
							iso.setISO_114_ExtendedData(response.getXML_ADD());
							iso.setISO_115_ExtendedData(response.getXML_DATA());
							iso.setISO_123_ExtendedData(response.getXML_FACT());
						}
						else{
							
							iso.setISO_039_ResponseCode("906");
							iso.setISO_039p_ResponseDetail("SECUENCIAL RESPUESTA NO COINCIDE CON EL DE REQUERIIMIENTO");
						}
					}else {
						
						if(StringUtils.IsNullOrEmpty(response.getMENSAJE())){
							iso.setISO_039p_ResponseDetail(GetCodeError(response.getCODIGO_RESULTADO()));
						}
						else{
							
							iso.setISO_039p_ResponseDetail(response.getMENSAJE());
						}	
						if(evaluateReverseType(response.getCODIGO_RESULTADO().trim()) 
								&& iso.getISO_003_ProcessingCode().startsWith("0")){
							iso.setWsISO_FlagStoreForward(true);
							log.WriteLogMonitor("FLAG ==> 3 " + MemoryGlobal.currentPath, TypeMonitor.monitor, null);
						}
					}
				}else{
					
					log.WriteLogMonitor("FLAG ==> 1 " + MemoryGlobal.currentPath, TypeMonitor.monitor, null);
					iso.setISO_039_ResponseCode("908");
					iso.setISO_039p_ResponseDetail(service.getDesError());
					if(service.getDesError().contains("TIMED")){
						iso.setWsIso_LogStatus(9);
						if(iso.getISO_003_ProcessingCode().startsWith("0")){
							iso.setWsISO_FlagStoreForward(true);
							iso.setISO_090_OriginalData("07-02");
							log.WriteLogMonitor("FLAG ==> 2 " + MemoryGlobal.currentPath, TypeMonitor.monitor, null);
						}
					}
				}
				
				log.WriteLog(response, TypeLog.facilito, TypeWriteLog.file);
				
			}else {
				
				iso.setISO_039_ResponseCode("909");
				iso.setISO_039p_ResponseDetail("ERROR EN PROCESOS, NO SE HA PODIDO RECUPERAR LA TERMINAL PARA EL CANAL " + iso.getISO_018_MerchantType() );
				
				log.WriteLog("Error Respuesta - Sec: " + iso.getISO_011_SysAuditNumber() + "  " 
				+ "ERROR EN PROCESOS, NO SE HA PODIDO RECUPERAR LA TERMINAL PARA EL CANAL " 
						+ iso.getISO_018_MerchantType(), TypeLog.facilito, TypeWriteLog.file);
			}
					
		} catch (Exception e) {
			
			if(iso.getTickAut().isStarted())
				iso.getTickAut().stop();
			iso.setISO_039_ResponseCode("909");
			iso.setISO_039p_ResponseDetail(GeneralUtils.ExceptionToString("ERROR CONSULTA: ", e, true));
			log.WriteLog("Error response - Sec: " + iso.getISO_011_SysAuditNumber() + " ---> " 
			+ iso.getISO_039p_ResponseDetail(), TypeLog.facilito, TypeWriteLog.file);
			log.WriteLogMonitor("Error modulo FacilitoPay::ExecuteTran ", TypeMonitor.error, e);
			
		}finally {
			
			iso.setWsTempAut((iso.getTickAut().getTime(TimeUnit.MILLISECONDS)/1000.0));
		}
		return iso;
	}
	
	protected wIso8583 ConfirmaTransaccion(wIso8583 iso){
		
		try {
			
			putCertificate();
			
			Config cnf = new Config();
			cnf = cnf.getConfigSystem(iso.getISO_018_MerchantType());
			if(cnf != null){
				
					ConsultaTransaccionesFacilito service = new 
							ConsultaTransaccionesFacilito(MemoryGlobal.urlFacilitoConfirm, 
							15000, iso.getWsTransactionConfig().getProccodeTimeOutValue());
					
					DatosConfirmacion data = new DatosConfirmacion();
					data.setSecuencia_Adquirente(iso.getISO_011_SysAuditNumber());
					data.setFecha(FormatUtils.DateToString(iso.getISO_012_LocalDatetime(), "yyyyMMdd"));
					data.setReferencia(iso.getISO_002_PAN());
					data.setProducto(iso.getISO_024_NetworkId());
					data.setTerminal(Arrays.asList(cnf.getCfg_Valor().split("-")).get(1));
					data.setABA(MemoryGlobal.facilitoABA);
					data.setType_Trn(iso.getISO_003_ProcessingCode());
					iso.getTickAut().start();
					log.WriteLog(data, TypeLog.facilito, TypeWriteLog.file);
					Reply_SW res = service.ConfirmaTransacciones(data);
					iso.getTickAut().stop();
					if(service.isFlagError()){
						iso.setISO_039_ResponseCode(res.getCODIGO_RESULTADO());
						iso.setISO_039p_ResponseDetail(res.getMENSAJE());
						log.WriteLogMonitor("Se ejecuta Confirmacion de TRX: [Sec: " + iso.getISO_011_SysAuditNumber() + "] " 
						+ res.getCODIGO_RESULTADO() + " - " + res.getMENSAJE() , TypeMonitor.monitor, null);
						log.WriteLog(res, TypeLog.facilito, TypeWriteLog.file);
					}else {
						
						iso.setISO_039_ResponseCode("906");
						iso.setISO_039p_ResponseDetail(service.getDesError());
						if(service.getDesError().contains("TIMED")){
							log.WriteLog("Time Out Confimacion Sec: " +  iso.getISO_011_SysAuditNumber(), TypeLog.facilito, TypeWriteLog.file);
							iso.setWsIso_LogStatus(9);
							iso.setWsISO_FlagStoreForward(true);
						}
					}
			}else{
				
				iso.setISO_039_ResponseCode("909");
				iso.setISO_039p_ResponseDetail("ERROR EN PROCESOS, NO SE HA PODIDO RECUPERAR LA TERMINAL PARA EL CANAL " + iso.getISO_018_MerchantType() );
				
				log.WriteLog("Error Respuesta - Sec: " + iso.getISO_011_SysAuditNumber() + "  " 
						+ "ERROR EN PROCESOS, NO SE HA PODIDO RECUPERAR LA TERMINAL PARA EL CANAL " 
								+ iso.getISO_018_MerchantType(), TypeLog.facilito, TypeWriteLog.file);
			}
			
		} catch (Exception e) {
			
			if(iso.getTickAut().isStarted())
				iso.getTickAut().stop();
			iso.setISO_039_ResponseCode("909");
			iso.setISO_039p_ResponseDetail(GeneralUtils.ExceptionToString("ERROR EN EPROCESOS ", e, true));
			log.WriteLogMonitor("Error modulo FacilitoPay::ConfirmaTransaccion ", TypeMonitor.error, e);
			
			log.WriteLog("Error response - Sec: " + iso.getISO_011_SysAuditNumber() + " ---> " 
					+ iso.getISO_039p_ResponseDetail(), TypeLog.facilito, TypeWriteLog.file);
					log.WriteLogMonitor("Error modulo FacilitoPay::ExecuteTran ", TypeMonitor.error, e);
			
		}finally {
			
			iso.setWsTempAut((iso.getTickAut().getTime(TimeUnit.MILLISECONDS)/1000.0));
		}
		
		return iso;
	}
	private String GetCodeError(String key){
		
		String valor = "NO EXISTE DESCRIPCION DEL ERROR";
		try {
			valor = MemoryGlobal.MapFacilito.get(key);
			if(StringUtils.IsNullOrEmpty(valor)){
				valor = "ERROR COD: " + key + ", NO EXISTE DESCRIPCION PARA ESTE ERROR DE FACILITO";
			}
			
		} catch (Exception e) {
			
			log.WriteLogMonitor("Error modulo FacilitoPay::GetCodeError ", TypeMonitor.error, e);
		}
		
		return valor;
	}
	protected boolean evaluateReverseType(String cod){
		
		String [] codes = new String[] { "91","92","93","94","95","96","091","092","093","094","095","096"};
		String firstResult = Arrays.stream(codes)
                .filter(x -> x.equals(cod))
                .findFirst()
                .orElse(null);
        if(StringUtils.IsNullOrEmpty(firstResult))
        	return false;
        else
        	return true;
	}
	protected void putCertificate(){
		  
		  if(MemoryGlobal.flagCertficatefacilito){
			  
		  	 String path = MemoryGlobal.currentPath;
		  	 log.WriteLogMonitor("Current Path [Facilito_Cert] ===> " + path + MemoryGlobal.facilitoNameCert, TypeMonitor.monitor, null);
		     System.setProperty("javax.net.ssl.keyStore", path + MemoryGlobal.facilitoNameCert);
	         System.setProperty("javax.net.ssl.keyStorePassword", MemoryGlobal.facilitoPasswordCert);
	         System.setProperty("javax.net.ssl.keyStoreType", "PKCS12");
	      }
	 }
	
}

