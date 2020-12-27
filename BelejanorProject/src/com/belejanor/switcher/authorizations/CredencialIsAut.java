package com.belejanor.switcher.authorizations;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import com.belejanor.switcher.cscoreswitch.wIso8583;
import com.belejanor.switcher.logger.Logger;
import com.belejanor.switcher.logger.LoggerConfig.TypeMonitor;
import com.belejanor.switcher.utils.FormatUtils;
import com.belejanor.switcher.utils.GeneralUtils;
import com.belejanor.switcher.utils.StringUtils;
import com.fitbank.middleware.main.ConsumerCredencialAutorizacion;

import _90._10._70._10.autorizacion_wsdl.AutorizacionStub.Autorizar;
import _90._10._70._10.autorizacion_wsdl.AutorizacionStub.Movimiento_type0;
import _90._10._70._10.autorizacion_wsdl.AutorizacionStub.Movimientos_type0;
import _90._10._70._10.autorizacion_wsdl.AutorizacionStub.RespuestaAutorizacion;

public class CredencialIsAut {
	
	private Logger log;
	
	public CredencialIsAut(){
		
		log = new Logger();
	}

	public wIso8583 QueryTC(wIso8583 iso){
		
		try {
			
			log.WriteLogMonitor("Entro en QueryTC ====>>>>   " + iso.getISO_011_SysAuditNumber(), TypeMonitor.monitor, null);
			
			Autorizar authCredencial = new Autorizar();
			authCredencial.setCanal(iso.getISO_120_ExtendedData());
			authCredencial.setTipoTransaccion(Arrays.asList
							(iso.getWsTransactionConfig().getProccodeParams().split("-")).get(0));
			authCredencial.setSubTipoTransaccion(Arrays.asList
							(iso.getWsTransactionConfig().getProccodeParams().split("-")).get(1));
			authCredencial.setSecuenciaTransaccion(iso.getISO_011_SysAuditNumber());
			authCredencial.setFechaTransaccion(FormatUtils.
					        DateToString(iso.getISO_012_LocalDatetime(), "yyyyMMdd"));
			authCredencial.setHoraTransaccion(FormatUtils.
			                DateToString(iso.getISO_012_LocalDatetime(), "HHmmss"));
			authCredencial.setTarjeta(iso.getISO_002_PAN().trim());
			authCredencial.setComercio(iso.getISO_121_ExtendedData());
			authCredencial.setTerminal(iso.getWsTransactionConfig().getIp());
			
			ConsumerCredencialAutorizacion service = 
					new ConsumerCredencialAutorizacion(iso.getISO_122_ExtendedData(), 
							(iso.getWsTransactionConfig().getProccodeTimeOutValue() + 5000), 
							iso.getWsTransactionConfig().getProccodeTimeOutValue());
			
			iso.getTickAut().reset();
			iso.getTickAut().start();
			RespuestaAutorizacion response = service.AutorizaConsultaSaldoConSinMovimientos(authCredencial);
			iso.getTickAut().stop();
			log.WriteLogMonitor("Respondio: Code Error: ====>>>>   " + response.getCodigoError(), TypeMonitor.monitor, null);
			
			if(service.isFlagError()){
				
				log.WriteLogMonitor("Entro a paso 1: ====>>>>   " , TypeMonitor.monitor, null);
			
				if(response.getCodigoError() == -1){
					
					log.WriteLogMonitor("Entro a paso 2: ====>>>>   " , TypeMonitor.monitor, null);
					StringBuilder valores = new StringBuilder();
					
					valores.append(StringUtils.padLeft(FormatUtils.StringToDecimalFormat
							      (response.getDisponibleConsumos()).
					              replace(",", "").replace(".", ""),12,"0" + "|"));
					valores.append(StringUtils.padLeft(FormatUtils.StringToDecimalFormat
							      (response.getDisponibleCuotas()).
				                  replace(",", "").replace(".", ""),12,"0" + "|"));
					valores.append(StringUtils.padLeft(FormatUtils.StringToDecimalFormat
								  (response.getDisponibleAdelantos()).
				                  replace(",", "").replace(".", ""),12,"0" + "|"));
					valores.append(StringUtils.padLeft(FormatUtils.StringToDecimalFormat
								  (response.getDisponiblePrestamos()).
				                  replace(",", "").replace(".", ""),12,"0" + "|"));
					valores.append(StringUtils.padLeft(FormatUtils.StringToDecimalFormat
							  (response.getSaldo()).
							  replace(",", "").replace(".", ""),12,"0" + "|"));
					valores.append(StringUtils.padLeft(FormatUtils.StringToDecimalFormat
								  (response.getSaldoEnDolares()).
								  replace(",", "").replace(".", ""),12,"0" + "|"));
					valores.append(StringUtils.padLeft(FormatUtils.StringToDecimalFormat
							      (response.getPagoMinimo()).
				                   replace(",", "").replace(".", ""),12,"0" + "|"));
					String fecha = response.getFechaVencimientoUltimaLiquidacion().
			                       replace("-", "").replace("/", "");
					valores.append(StringUtils.isNullOrEmpty(fecha)?"YYYYMMDD":fecha);
					
					iso.setISO_038_AutorizationNumber(response.getCodigoAutorizacion());
					iso.setISO_114_ExtendedData(response.getMensajeError());
					iso.setISO_115_ExtendedData(valores.toString());
					iso.setISO_039_ResponseCode("000");
					iso.setISO_039p_ResponseDetail("TRANSACCION EXITOSA");
					//Registro de Movimientos
					if(authCredencial.getSubTipoTransaccion() == "1"){
						
						Movimientos_type0 type0 = response.getMovimientos();
						Movimiento_type0[] mov = type0.getMovimiento();
						Thread t = new Thread(RegisterMovLaundryControl(mov));
						t.start();
					}
					
				}else {
					
					log.WriteLogMonitor("Entro a paso 3 ELSE: ====>>>>   " , TypeMonitor.monitor, null);
					iso.setISO_039_ResponseCode(StringUtils.
							 padLeft(String.valueOf(response.getCodigoError()),3,"0"));
					iso.setISO_039p_ResponseDetail(response.getMensajeRespuesta().toUpperCase());
				}
				iso.setWsIso_LogStatus(2);
				
			}else {
				
				log.WriteLogMonitor("Entro a paso 4 ELSE: ====>>>>   " , TypeMonitor.monitor, null);
				iso.setISO_039_ResponseCode("907");
				iso.setISO_039p_ResponseDetail(service.getDesError());
				iso.setWsIso_LogStatus(9);
				log.WriteLogMonitor("Error modulo CredencialIsAut::QueryTC " + service.getStackTrace(), TypeMonitor.error, null);
			}
			
		} catch (Exception e) {
			
			log.WriteLogMonitor("Entro a paso 5 ERROR!!!: ====>>>>   " + GeneralUtils.ExceptionToString("Error", e, true) , TypeMonitor.monitor, null);
			if(iso.getTickAut().isStarted())
				iso.getTickAut().stop();
			iso.setISO_039_ResponseCode("909");
			iso.setISO_039p_ResponseDetail(GeneralUtils.
					ExceptionToString("ERROR AUTORIZACION CREDENCIAL ", e, false));
		}finally {
			
			iso.setWsTempAut((iso.getTickAut().getTime(TimeUnit.MILLISECONDS)/1000.0));
		}
		
		return iso;
	}
	
	public Runnable RegisterMovLaundryControl(Movimiento_type0[] mov){
		
		Runnable run = null;
		try {
			
			run = new Runnable() {
				
				@Override
				public void run() {
					
					for (Movimiento_type0 movi : mov) {
						System.out.println(movi.getCodigoAutorizacion());
					}
					
				}
			};
			
		} catch (Exception e) {
			
		}
		return run;
	}
	
}
