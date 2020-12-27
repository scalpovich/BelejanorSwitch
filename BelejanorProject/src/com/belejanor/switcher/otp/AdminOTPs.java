package com.belejanor.switcher.otp;

import java.util.Date;

import com.belejanor.switcher.cscoreswitch.wIso8583;
import com.belejanor.switcher.logger.Logger;
import com.belejanor.switcher.logger.LoggerConfig.TypeMonitor;
import com.belejanor.switcher.sqlservices.IsoRetrievalTransaction;
import com.belejanor.switcher.utils.FormatUtils;
import com.belejanor.switcher.utils.GeneralUtils;
import com.belejanor.switcher.utils.NumbersUtils;
import com.belejanor.switcher.utils.StringUtils;
import com.belejanor.switcher.utils.FormatUtils.TypeTemp;

public class AdminOTPs {

	private Logger log;
	
	public AdminOTPs() {
		log = new Logger();
	}
	
	enum typeGenOTP { Numeric, AlphaNumeric };
	enum typeTimeOTP {Days, Hours, Minutes, Seconds}
	public wIso8583 GenerateOTP(wIso8583 iso) {
		
		OTP otp = null;
		typeTimeOTP typeTimeGetter = null;
		IsoRetrievalTransaction sql = null;
		try {
			
			otp = new OTP();
			
			switch (iso.getISO_022_PosEntryMode()) {
			case "D":
				typeTimeGetter = typeTimeOTP.Days;
				break;
			case "H":
				typeTimeGetter = typeTimeOTP.Hours;
				break;
			case "M":
				typeTimeGetter = typeTimeOTP.Minutes;
				break;
			case "S":
				typeTimeGetter = typeTimeOTP.Seconds;
				break;
			default:
				break;
			}
			
			otp = GenerateOTP(iso.getISO_002_PAN(), iso.getISO_023_CardSeq().equalsIgnoreCase("A")?
					typeGenOTP.AlphaNumeric:typeGenOTP.Numeric, typeTimeGetter, Integer.parseInt(iso.getISO_120_ExtendedData()), 
					Integer.parseInt(iso.getISO_121_ExtendedData()), iso.getISO_090_OriginalData().equalsIgnoreCase("Y")?true:false);
			
			if(otp.getError().equals("OK")) {
				
				otp.setParam(iso.getISO_002_PAN());
				sql = new IsoRetrievalTransaction();
				iso = sql.RetrieveAdminOTP(otp, iso);
				if(iso.getISO_039_ResponseCode().equals("000")) {
					
					iso.setISO_052_PinBlock(otp.getOtpCode());
					iso.setISO_037_RetrievalReferenceNumber(otp.getPinBlock());
				}
				
			}else {
				
				iso.setISO_039_ResponseCode("100");
				iso.setISO_039p_ResponseDetail(otp.getError());
			}
			
		} catch (Exception e) {
			
			iso.setISO_039_ResponseCode("909");
			iso.setISO_039p_ResponseDetail("ERROR EN PROCESOS " + GeneralUtils.ExceptionToString(null, e, false));
			
			log.WriteLogMonitor("Error cerrar conexion Modulo" + this.getClass().getName() + "::GenerateOTP"
					, TypeMonitor.error, e);
		}
		return iso;
	}
	
	public OTP GenerateOTP(String parametro, typeGenOTP typeGen, typeTimeOTP typeTime,  int longitud, int caducidad, boolean flagPinBlock) {
		
		OTP otp = null;
		com.belejanor.switcher.crypto.GetPinBlock  genPinblock = null;
		String otpGenerado = StringUtils.Empty();
		try {
			
			otp = new OTP();
			if(longitud >= 4  && longitud <= 32) {
			
				Date fecIni = new Date();
				otp.setFechaGeneracion(fecIni);
				if(typeGen == typeGenOTP.AlphaNumeric)
					otpGenerado = GeneralUtils.GetSecuencial(longitud);
				else
					otpGenerado = GeneralUtils.GetSecuencialNumeric(longitud);
				
				otp.setOtpCode(otpGenerado);
					
				if(flagPinBlock) {
					
					if(NumbersUtils.isNumeric(otp.getOtpCode()) && otp.getOtpCode().length() == 5) {
						genPinblock = new com.belejanor.switcher.crypto.GetPinBlock();
						otp.setPinBlock(genPinblock.getPinBlock(otp.getOtpCode()));
					}else {
						otp.setError("ERROR EN PROCESOS CRIPTOGRAFICOS LONGITUD DE PIN Y TIPO DATO PIN INCORRECTOS (CUANDO FLAG = Y)");
					}
				}
									
				switch (typeTime) {
				case Days:
					otp.setFechaExpiracion(FormatUtils.sumarRestarHorasFecha(fecIni, TypeTemp.dias, caducidad));
					break;
				case Hours:
					otp.setFechaExpiracion(FormatUtils.sumarRestarHorasFecha(fecIni, TypeTemp.horas, caducidad));
					break;
				case Minutes:
					otp.setFechaExpiracion(FormatUtils.sumarRestarHorasFecha(fecIni, TypeTemp.minutos, caducidad));
					break;
				case Seconds:
					otp.setFechaExpiracion(FormatUtils.sumarRestarHorasFecha(fecIni, TypeTemp.segundos, caducidad));
					break;
				default:
					break;
				}
				
			}else {
				
				otp.setError("LA LONGITUD DEL OTP DEBE ESTAR ENTRE 4 Y 32 BYTES");
			}
			
		} catch (Exception e) {
			
			otp.setError(GeneralUtils.ExceptionToString("ERROR AL GENERAR OTP ", e, false));
			log.WriteLogMonitor("Error cerrar conexion Modulo" + this.getClass().getName() + "::GenerateOTP"
					, TypeMonitor.error, e);
		}
		return otp;
	}
	
	public wIso8583 ValidateOTP(wIso8583 iso) {
		
		IsoRetrievalTransaction sql = null;
		OTP otp = new OTP();
		try {
			
			otp.setOtpCode(iso.getISO_052_PinBlock());
			otp.setCanal(iso.getISO_018_MerchantType());
			otp.setParam(iso.getISO_002_PAN());
			otp.setFechaExpiracion(new Date());
			otp.setFechaGeneracion(new Date());
			
			sql = new IsoRetrievalTransaction();
			iso = sql.RetrieveAdminOTP(otp, iso);
			
			String OTPs = null;
			if(iso.getISO_039_ResponseCode().equals("000")) {
			
				if(iso.getISO_090_OriginalData().equalsIgnoreCase("Y"))
					/*Cuando es Pinblock*/
					OTPs = iso.getISO_123_ExtendedData();
				else
					OTPs = iso.getISO_120_ExtendedData();
				
				if(StringUtils.IsNullOrEmpty(OTPs)) {
					
					iso.setISO_039_ResponseCode("100");
					iso.setISO_039p_ResponseDetail("ERROR EN PROCESO BANDERA VALIDACION OTP (Y/N) CAMPO ISO_090 ESCOJA BIEN OPCION");
					return iso;
				}
				
				long fechaHoyComparar = Long.parseLong(FormatUtils.DateToString(new Date(), "yyyyMMddHHmmss"));
				long fechaCaducidad = Long.parseLong(iso.getISO_122_ExtendedData());
				int reintentos = Integer.parseInt(iso.getISO_023_CardSeq());
				int permitidos = Integer.parseInt(iso.getISO_035_Track2());
				
				if(reintentos <= permitidos) {
					
					if(OTPs.equals(otp.getOtpCode())) {
						
						if(fechaHoyComparar > fechaCaducidad) {
							
							iso.setISO_039_ResponseCode("300");
							iso.setISO_039p_ResponseDetail("OTP CADUCADO");
						}else {
							
							iso.setISO_039_ResponseCode("000");
							iso.setISO_039p_ResponseDetail("TRANSACCION EXITOSA");
						}
						
					}else {
						
						iso.setISO_039_ResponseCode("117");
						iso.setISO_039p_ResponseDetail("CLAVE-OTP INVALIDO");
					}
					
				}else {
					
					iso.setISO_039_ResponseCode("800");
					iso.setISO_039p_ResponseDetail("REINTENTOS DE CLAVE-OTP CADUCADOS");
				}
			}
			
		} catch (Exception e) {
			
			iso.setISO_039_ResponseCode("909");
			iso.setISO_039p_ResponseDetail("ERROR EN PROCESOS, " + GeneralUtils.ExceptionToString(null, e, false));
			log.WriteLogMonitor("Error cerrar conexion Modulo" + this.getClass().getName() + "::ValidateOTP"
					, TypeMonitor.error, e);
		}finally {
			
			iso.setISO_121_ExtendedData(null);
			iso.setISO_122_ExtendedData(null);
			iso.setISO_123_ExtendedData(null);
		}
		
		return iso;
	}
}


