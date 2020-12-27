package com.belejanor.switcher.notifications;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import com.belejanor.switcher.cscoreswitch.wIso8583;
import com.belejanor.switcher.logger.Logger;
import com.belejanor.switcher.logger.LoggerConfig.TypeMonitor;
import com.belejanor.switcher.memcached.MailSmsTypes;
import com.belejanor.switcher.memcached.MemoryGlobal;
import com.belejanor.switcher.utils.FormatUtils;
import com.belejanor.switcher.utils.GeneralUtils;
import com.belejanor.switcher.utils.Ref;
import com.belejanor.switcher.utils.StringUtils;

public class SenderMailForUser {

	private Logger log;
	
	public SenderMailForUser() {
		log = new Logger();
	}
	
	@SuppressWarnings("unused")
	public wIso8583 sendMail(wIso8583 iso) {
		
		iso.setWsTempAut(0);
		
		MailSmsTypes mail = null;
		final String bodyMessage = null;
		iso.setISO_041_CardAcceptorID(MemoryGlobal.MailDomain);
		try {
			
			if(!StringUtils.IsNullOrEmpty(iso.getISO_123_ExtendedData())) {
				/*El if que viene a continuacion lo pongo cuando se envia correos por correo no por cedula
				 * para rellenar el campo parametro en el Monitor*/
				if(StringUtils.IsNullOrEmpty(iso.getISO_002_PAN()))
					iso.setISO_102_AccountID_1(iso.getISO_123_ExtendedData());
				if(FormatUtils.checkMail(iso.getISO_123_ExtendedData())) {
					mail = new MailSmsTypes();
					mail = mail.getMailSmsTypes(Integer.parseInt(iso.getISO_090_OriginalData()), "MAIL");
					if(mail != null) {
					
						final MailSender senderMail = new MailSender(MemoryGlobal.MailDomain, MemoryGlobal.MailHost, 
								MemoryGlobal.MailPort, MemoryGlobal.MailUser, MemoryGlobal.MailPassword);
						
						ContainerMail containerMail = new ContainerMail();
						
						String[] isoCorresponse = null;
						
						if(!StringUtils.IsNullOrEmpty(mail.getType_iso_corresponse())) 
							isoCorresponse = mail.getType_iso_corresponse().split("\\^");
						
						if(isoCorresponse != null) {
							
							if(Arrays.asList(iso.getISO_035_Track2().split("\\|")).size()
									== isoCorresponse.length) {
								String[] isoValues = new String[isoCorresponse.length];
								for (int i = 0; i < isoCorresponse.length; i++) { 
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
									
									containerMail.setCuerpoMensaje(MessageFormat.format(mail.getType_text_message(), (Object[])isoValues));
									
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
							 
							 containerMail.setCuerpoMensaje(mail.getType_text_message());
						}
						
						ArrayList<String> to = new ArrayList<>();
						to.add(iso.getISO_123_ExtendedData());
						containerMail.setParaMensaje(to);
						Ref<String> refError = new Ref<String>(StringUtils.Empty());
						containerMail.setErrorMensaje(refError);
						containerMail.setAsuntoMensaje(iso.getISO_036_Track3());
						
						final CountDownLatch semaphore = new CountDownLatch(1);
						iso.getTickAut().reset();
						iso.getTickAut().start();
						Thread tSender = new Thread(new Runnable() {
							
							@Override
							public void run() {
								
								senderMail.sendEmail(containerMail.getParaMensaje(), containerMail.getAsuntoMensaje(),
													 containerMail.getCuerpoMensaje(), containerMail.getErrorMensaje());
								semaphore.countDown();
								if(iso.getTickAut().isStarted())
									iso.getTickAut().stop();
							}
						});
						tSender.start();
						
						if(semaphore.await(iso.getWsTransactionConfig().
								 getProccodeTimeOutValue(), TimeUnit.MILLISECONDS)){
							
							if(containerMail.getErrorMensaje().get().equals("OK")) {
								
								iso.setISO_039_ResponseCode("000");
								iso.setISO_039p_ResponseDetail("TRANSACCION EXITOSA");
								
							}else {
								
								iso.setISO_039_ResponseCode("115");
								iso.setISO_039p_ResponseDetail("NO SE HA PODIDO ENVIAR MAIL:" + 
								containerMail.getErrorMensaje().get());
								
							}
						
						}else {
							
							iso.setISO_039_ResponseCode("907");
							iso.setISO_039p_ResponseDetail("HA EXPIRADO EL TIEMPO DE ESPERA DE ENVIO DEL MENSAJE");
							
						}
					
					}else {
						
						iso.setISO_039_ResponseCode("100");
						iso.setISO_039p_ResponseDetail("NO SE HA PODIDO RECUPERAR INFORMACION DEL "
								+ "TIPO DE CORREO ELECTRONICO A ENVIAR (" + iso.getISO_090_OriginalData() + ")");
					}
				}else {
					
					iso.setISO_039_ResponseCode("102");
					iso.setISO_039p_ResponseDetail("EL CORREO ELECTRONICO ES INVALIDO: " + iso.getISO_123_ExtendedData());
				}
				
			}else {
				
				iso.setISO_039_ResponseCode("101");
				iso.setISO_039p_ResponseDetail("NO EXISTE CORREO ELECTRONICO A ENVIAR");
			}
			
		} catch (Exception e) {
			
			if(iso.getTickAut().isStarted())
				iso.getTickAut().stop();
			
			iso.setISO_039_ResponseCode("909");
			iso.setISO_039p_ResponseDetail("ERROR EN PROCESOS " + GeneralUtils.ExceptionToString(null, e, false));
			
			log.WriteLogMonitor("Error Modulo" + this.getClass().getName() + "::sendMail"
					, TypeMonitor.error, e);
		}finally {
			
			if(iso.getISO_039_ResponseCode().equals("000"))
				iso.setWsTempAut((iso.getTickAut().getTime(TimeUnit.MILLISECONDS)/1000.0));
		}
		
		return iso;
	}
	
	public wIso8583 sendMailAsync(wIso8583 iso) {
		
		try {
			
			Thread tMail = new Thread(() -> {
			    
				sendMail(iso);
			});
			
			tMail.start();
			iso.setISO_102_AccountID_1(iso.getISO_002_PAN());
			iso.setISO_039_ResponseCode("000");
			iso.setISO_039p_ResponseDetail("CORREO ELECTRONICO ENVIADO ASINCRONICAMENTE");
			
		} catch (Exception e) {
			
			iso.setISO_039_ResponseCode("909");
			iso.setISO_039p_ResponseDetail("ERROR EN PROCESOS " + GeneralUtils.ExceptionToString(null, e, false));
			
			log.WriteLogMonitor("Error Modulo" + this.getClass().getName() + "::sendMailAsync"
					, TypeMonitor.error, e);
		}
		
		return iso;
	}
}

class ContainerMail{
	
	private String cuerpoMensaje;
	private String asuntoMensaje;
	private Ref<String> errorMensaje;
	private ArrayList<String> paraMensaje;
	
	public ContainerMail() {
		
		this.errorMensaje = new Ref<String>(StringUtils.Empty());
		this.paraMensaje = new ArrayList<>();
	}

	public String getCuerpoMensaje() {
		return cuerpoMensaje;
	}

	public void setCuerpoMensaje(String cuerpoMensaje) {
		this.cuerpoMensaje = cuerpoMensaje;
	}

	public String getAsuntoMensaje() {
		return asuntoMensaje;
	}

	public void setAsuntoMensaje(String asuntoMensaje) {
		this.asuntoMensaje = asuntoMensaje;
	}

	public Ref<String> getErrorMensaje() {
		return errorMensaje;
	}

	public void setErrorMensaje(Ref<String> errorMensaje) {
		this.errorMensaje = errorMensaje;
	}

	public ArrayList<String> getParaMensaje() {
		return paraMensaje;
	}

	public void setParaMensaje(ArrayList<String> paraMensaje) {
		this.paraMensaje = paraMensaje;
	}
	
}
