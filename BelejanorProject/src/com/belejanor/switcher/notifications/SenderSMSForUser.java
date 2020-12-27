package com.belejanor.switcher.notifications;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import com.belejanor.switcher.cscoreswitch.wIso8583;
import com.belejanor.switcher.logger.Logger;
import com.belejanor.switcher.logger.LoggerConfig.TypeMonitor;
import com.belejanor.switcher.memcached.MailSmsTypes;
import com.belejanor.switcher.memcached.MemoryGlobal;
import com.belejanor.switcher.utils.GeneralUtils;
import com.belejanor.switcher.utils.NumbersUtils;
import com.belejanor.switcher.utils.Ref;
import com.belejanor.switcher.utils.StringUtils;

public class SenderSMSForUser {

	private Logger log;
	
	public SenderSMSForUser() {
	
		log = new Logger();
	}
	
	@SuppressWarnings("unused")
	public wIso8583 sendSMS(wIso8583 iso) {
		
		iso.setWsTempAut(0);
		MailSmsTypes sms = null;
		final String Message = null;
		try {
			
			if(!StringUtils.IsNullOrEmpty(iso.getISO_124_ExtendedData())) {
				/*El if que viene a continuacion lo pongo cuando se envia correos por correo no por cedula
				 * para rellenar el campo parametro en el Monitor*/
				iso.setISO_041_CardAcceptorID(iso.getISO_124_ExtendedData());
				if(StringUtils.IsNullOrEmpty(iso.getISO_002_PAN()))
					iso.setISO_102_AccountID_1(iso.getISO_124_ExtendedData());
				if(NumbersUtils.isNumeric(iso.getISO_124_ExtendedData()) && 
						iso.getISO_124_ExtendedData().length() == 10 && 
						iso.getISO_124_ExtendedData().startsWith("09")) {
					
					/*Quita el 0 del inicio*/
					if(MemoryGlobal.SMSFlagZeroIni)
						iso.setISO_124_ExtendedData(StringUtils.TrimStart(iso.getISO_124_ExtendedData(), "0"));
					
					sms = new MailSmsTypes();
					sms = sms.getMailSmsTypes(Integer.parseInt(iso.getISO_090_OriginalData()), "SMS");
					if(sms != null) {
					
						
						final SMSSender senderSms = new SMSSender();
						
						ContainerSMS containerSMS = new ContainerSMS();
						
						String[] isoCorresponse = null;
						
						if(!StringUtils.IsNullOrEmpty(sms.getType_iso_corresponse())) 
							isoCorresponse = sms.getType_iso_corresponse().split("\\^");
						
						if(isoCorresponse != null) {
							
							if(Arrays.asList(iso.getISO_035_Track2().split("\\|")).size()
									== isoCorresponse.length) {
								
								String[] isoValues = new String[isoCorresponse.length];
								for (int i = 0; i < isoCorresponse.length; i++) { 
									if(i==0) {	
										isoValues[i] = iso.getISO_124_ExtendedData();
									}else
										isoValues[i] = (String) GeneralUtils.getValue(isoCorresponse[i], iso);
									switch (isoValues[i]) {
									case "NAME":
									case "name":
										isoValues[i] = iso.getISO_122_ExtendedData();
										break;
									case "ID":
									case "id":
										isoValues[i] = iso.getISO_002_PAN();
										break;
									default:
										break;
									}
								}
								
								if(isoValues != null) {
									
									containerSMS.setMensaje(MessageFormat.format(sms.getType_text_message(), (Object[])isoValues));
									
								}else {
									
									iso.setISO_039_ResponseCode("103");
									iso.setISO_039p_ResponseDetail("NO EXISTEN VALORES REFLEXIVOS, CORRESPONDIENTES DE CORREO A ENVIAR");
									return iso;
								}
								
							}else {
								
								iso.setISO_039_ResponseCode("104");
								iso.setISO_039p_ResponseDetail("EL NUMERO DE VALORES A ENVIAR NO CORRESPONDE "
										+ "CON LOS REGISTRADOS PARA EL TIPO: " + iso.getISO_090_OriginalData());
								return iso;
							}
							
						 }else {
							 
							 containerSMS.setMensaje(sms.getType_text_message());
						}
						
						Ref<RespuestaSMS> refError = new Ref<>();
						containerSMS.setErrorMensaje(refError);
						containerSMS.setIso(iso);
						
						final CountDownLatch semaphore = new CountDownLatch(1);
						iso.getTickAut().reset();
						iso.getTickAut().start();
						Thread tSender = new Thread(new Runnable() {
							
							@Override
							public void run() {
								
								senderSms.sendSMS(containerSMS.getMensaje(), containerSMS.getErrorMensaje(), 
										containerSMS.getIso());
								semaphore.countDown();
								if(iso.getTickAut().isStarted())
									iso.getTickAut().stop();
							}
						});
						tSender.start();
						
						if(semaphore.await(iso.getWsTransactionConfig().
								 getProccodeTimeOutValue(), TimeUnit.MILLISECONDS)){
							
							if(containerSMS.getErrorMensaje().get().getCod_respuesta().equals("100") &&
									containerSMS.getErrorMensaje().get().getDes_respuesta().toUpperCase().contains("OK")) {
							
								iso.setISO_039_ResponseCode("000");
								iso.setISO_039p_ResponseDetail("TRANSACCION EXITOSA");
								iso.setISO_120_ExtendedData(containerSMS.getErrorMensaje().get().getId_transaccion());
								
							}else {
								
								iso.setISO_039_ResponseCode("115");
								iso.setISO_039p_ResponseDetail("NO SE HA PODIDO ENVIAR SMS:" + 
								containerSMS.getErrorMensaje().get().getCod_respuesta() + " - " +
								containerSMS.getErrorMensaje().get().getDes_respuesta());
								
							}
						
						}else {
							
							iso.setISO_039_ResponseCode("907");
							iso.setISO_039p_ResponseDetail("HA EXPIRADO EL TIEMPO DE ESPERA DE ENVIO DEL SMS");
							
						}
					
					}else {
						
						iso.setISO_039_ResponseCode("100");
						iso.setISO_039p_ResponseDetail("NO SE HA PODIDO RECUPERAR INFORMACION DEL "
								+ "TIPO DE SMS A ENVIAR (" + iso.getISO_090_OriginalData() + ")");
					}
				}else {
					
					iso.setISO_039_ResponseCode("102");
					iso.setISO_039p_ResponseDetail("EL NUMERO CELULAR ES INVALIDO: " + iso.getISO_124_ExtendedData());
				}
				
			}else {
				
				iso.setISO_039_ResponseCode("101");
				iso.setISO_039p_ResponseDetail("NO EXISTE NUMERO DE CELULAR PARA ENVIO DE SMS");
			}
			
			
		} catch (Exception e) {
			
			log.WriteLogMonitor("Error Modulo" + this.getClass().getName() + "::sendSMS"
					, TypeMonitor.error, e);
		}
		return iso;
	}
	
}

class ContainerSMS{
	
	private String mensaje;
	private Ref<RespuestaSMS> errorMensaje;
	private wIso8583 iso;
	
	public ContainerSMS() {
		
		this.errorMensaje = new Ref<>();
	}

	public String getMensaje() {
		return mensaje;
	}

	public void setMensaje(String mensaje) {
		this.mensaje = mensaje;
	}

	public Ref<RespuestaSMS> getErrorMensaje() {
		return errorMensaje;
	}

	public void setErrorMensaje(Ref<RespuestaSMS> errorMensaje) {
		this.errorMensaje = errorMensaje;
	}

	public wIso8583 getIso() {
		return iso;
	}

	public void setIso(wIso8583 iso) {
		this.iso = iso;
	}

}

