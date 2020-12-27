package com.belejanor.switcher.acquirers;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Date;
import java.util.concurrent.ThreadLocalRandom;

import com.belejanor.switcher.authorizations.FitIsAut;
import com.belejanor.switcher.cscoreswitch.wIso8583;
import com.belejanor.switcher.logger.Logger;
import com.belejanor.switcher.logger.LoggerConfig.TypeMonitor;
import com.belejanor.switcher.utils.FormatUtils;
import com.belejanor.switcher.utils.FormatUtils.TypeTemp;


public class ServipagosIsAcq {

	private Logger log;
	
	public ServipagosIsAcq() {
		
		log = new Logger();
	}
	
	public wIso8583 procesaRetiro(wIso8583 iso) {
		
		FitIsAut fit = null;
		try {
			
			fit = new FitIsAut();
			iso = fit.ExecuteDebitCreditFit1(iso);
			
		} catch (Exception e) {
			
			log.WriteLogMonitor("Error Modulo " + this.getClass().getName() + "::procesaRetiro" , TypeMonitor.error, e);
		}
		return iso;
	}
	
	public wIso8583 procesaDeposito(wIso8583 iso) {
		
		FitIsAut fit = null;
		try {
			
			fit = new FitIsAut();
			iso = fit.ExecuteDebitCreditFit1(iso);
			
		} catch (Exception e) {
			
			log.WriteLogMonitor("Error Modulo " + this.getClass().getName() + "::procesaDeposito" , TypeMonitor.error, e);
		}
		return iso;
	}
	
	public wIso8583 procesaConsultaCreditos(wIso8583 iso) {
		
		DecimalFormatSymbols simbolo = new DecimalFormatSymbols();
	    simbolo.setDecimalSeparator('.');
		DecimalFormat df = new DecimalFormat("0.00", simbolo);
		try {
		
			/*Simulacion*/
			Date fechaCuotaPorVencer = FormatUtils.sumarRestarHorasFecha(new Date(), TypeTemp.dias, ThreadLocalRandom.current().nextInt(5, 20));
			long numeroA = ThreadLocalRandom.current().nextInt(12000, 87000 + 1);
			double numeroConvert = numeroA;
			double valorA = (double) (numeroConvert/100);
			String cedulaDuenioCredito = "1100542529";
			String nombreDuenioCredito = "JIMENEZ ROMERO GLORIA MARIA DEL CISNE";
			String sFechaCuotaPorVencer = FormatUtils.DateToString(fechaCuotaPorVencer, "yyyy-MM-dd"); 
			double valorCuotaPorVencer =  0;
			String numOperacion = "06" + String.valueOf(ThreadLocalRandom.current().nextInt(22222222, 999999999 + 1));
			
			
			//Valor Coutas por vencer
			iso.setISO_103_AccountID_2(df.format(valorA));
			//Total valor a pagar
			iso.setISO_054_AditionalAmounts(df.format(valorA));
			//Valor Coutas Vencidas
			iso.setISO_090_OriginalData(df.format(valorCuotaPorVencer));
			iso.setISO_102_AccountID_1(numOperacion);
			//Fecha CoutaPorVencer
			iso.setISO_114_ExtendedData(sFechaCuotaPorVencer);
			iso.setISO_115_ExtendedData(cedulaDuenioCredito);
			iso.setISO_122_ExtendedData(nombreDuenioCredito);
			iso.setISO_123_ExtendedData("CREDITO NORMAL CONSUMO");
			iso.setISO_124_ExtendedData("1");
			
			iso.setISO_039_ResponseCode("000");
			iso.setISO_039p_ResponseDetail("TRANSACCION EXITOSA");
			
			
		} catch (Exception e) {
			log.WriteLogMonitor("Error Modulo " + this.getClass().getName() + "::procesaConsultaCreditos" , TypeMonitor.error, e);
		}
		
		return iso;
	}
}
