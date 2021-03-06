package com.belejanor.switcher.bimo.processor;

import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.ws.rs.WebApplicationException;

import com.belejanor.switcher.cscoreswitch.wIso8583;
import com.belejanor.switcher.logger.Logger;
import com.belejanor.switcher.logger.LoggerConfig.TypeMonitor;
import com.belejanor.switcher.memcached.MemoryGlobal;
import com.belejanor.switcher.utils.GeneralUtils;
import com.belejanor.switcher.utils.Ref;
import com.belejanor.switcher.utils.SerializationObject;
import com.belejanor.switcher.utils.StringUtils;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

import EDU.oswego.cs.dl.util.concurrent.TimeoutException;

public class ClientProcessorBimo {

	static {
	    disableSslVerification();
	}
	
	private Logger log;
	public ClientProcessorBimo() {
		
		super();
		log = new Logger();
	}
	
	@SuppressWarnings("unused")
	private SSLContext getSSLContext() {
        javax.net.ssl.TrustManager x509 = new javax.net.ssl.X509TrustManager() {

            @Override
            public void checkClientTrusted(java.security.cert.X509Certificate[] arg0, String arg1)
                    throws java.security.cert.CertificateException {
                return;
            }

            @Override
            public void checkServerTrusted(java.security.cert.X509Certificate[] arg0, String arg1)
                    throws java.security.cert.CertificateException {
                return;
            }

            @Override
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return null;
            }
        };
        SSLContext ctx = null;
        try {
            ctx = SSLContext.getInstance("SSL");
            ctx.init(null, new javax.net.ssl.TrustManager[] { x509 }, null);
        } catch (java.security.GeneralSecurityException ex) {
        }
        return ctx;
    }
	
	
	
	@SuppressWarnings("unused")
	private HostnameVerifier getHostnameVerifier() {
        return new HostnameVerifier() {

            @Override
            public boolean verify(String hostname, javax.net.ssl.SSLSession sslSession) {
                return true;
            }
        };
    }
	
	
	
	public Object getResponseDataClientRest(String packIso20022, Class<?> classNameOrigen, 
			Class<?> classNameRespuesta,Object documento, Ref<wIso8583> isoRef){
		
		int index = 0;
		String responseString = StringUtils.Empty();
		wIso8583 iso = isoRef.get();
		try {
			
			String urlAutorizadora = MemoryGlobal.UrlBIMOAutorizador.replace("XXXXXX", packIso20022)
					                                                .replace("&#xD", StringUtils.Empty());
			
			/*ClientConfig config = new DefaultClientConfig();*/
			
			
			//SSLContext context = Ssl.createContext();
			//if (context != null) {
				//config.getProperties().put(HTTPSProperties.PROPERTY_HTTPS_PROPERTIES,
				//		new HTTPSProperties(HttpsURLConnection.getDefaultHostnameVerifier(), context));
			//}
			
			/*config.getProperties().put(HTTPSProperties.PROPERTY_HTTPS_PROPERTIES,
                    new com.sun.jersey.client.urlconnection.HTTPSProperties(getHostnameVerifier(), getSSLContext()));*/
			
			iso.getTickAut().reset();
			iso.getTickAut().start();
			Client client = Client.create();
			client.setConnectTimeout(iso.getWsTransactionConfig().getProccodeTimeOutValue()/2);
			client.setReadTimeout(iso.getWsTransactionConfig().getProccodeTimeOutValue());
			WebResource resourse = 
								client.resource(urlAutorizadora);
			
			ClientResponse docRes =  resourse.type("application/xml")
					   .post(ClientResponse.class, classNameOrigen.cast(documento));
			if(iso.getTickAut().isStarted())
				iso.getTickAut().stop();
			
			if(docRes.getStatus() == 200){
				
				docRes.bufferEntity();
				responseString = docRes.getEntity(String.class);
				index = responseString.indexOf("camt.998.999");
				
				if(index > 0){
					
					com.belejanor.switcher.bimo.genericerror.Document doc =	 
							(com.belejanor.switcher.bimo.genericerror.Document) 
							SerializationObject.StringToObject(responseString, 
									com.belejanor.switcher.bimo.genericerror.Document.class);
					iso.setISO_039_ResponseCode("000");
					iso.setWsIso_LogStatus(2);
					iso.setWsISO_TranDatetimeResponse(new Date());
					return doc;
					
				}else{
					
					Object docR = classNameRespuesta.cast(SerializationObject
							     .StringToObject(responseString, classNameRespuesta));
					iso.setISO_039_ResponseCode("000");
					iso.setWsIso_LogStatus(2);
					iso.setWsISO_TranDatetimeResponse(new Date());
					return docR;
				}
			}else{
				
				iso.setISO_039_ResponseCode("200");
				iso.setISO_039p_ResponseDetail("ERROR HTTP SERVICE REST BIMO STATUS HTTP: " + docRes.getStatus());
				iso.setWsIso_LogStatus(8);
				return null;
			}
			
		} catch (Exception e) {
			
			if(iso.getTickAut().isStarted())
				iso.getTickAut().stop();
			if (e.getCause() instanceof SocketTimeoutException || e.getCause() instanceof TimeoutException) {
			   
			   iso.setISO_039_ResponseCode("907");
			   iso.setISO_039p_ResponseDetail(GeneralUtils.ExceptionToString("HA EXPIRADO EL TIEMPO DE ESPERA A "
			   		+ "SERVICIOS BIMO DE BANRED", e, true));
		       iso.setWsIso_LogStatus(9);
		       iso.setWsISO_FlagStoreForward(true);
			}else if (e.getCause() instanceof SocketException) {
				
				 iso.setISO_039_ResponseCode("906");
				 iso.setISO_039p_ResponseDetail(GeneralUtils.ExceptionToString("SERVICIO REST BIMO BANRED NO HABILITADO, SOCKET_EXCEPTION (CONNECTION RESET) "
				   		, e, true));
			     iso.setWsIso_LogStatus(9);
				
			}else if (e.getCause() instanceof ClientHandlerException) {
				
				 iso.setISO_039_ResponseCode("901");
				 iso.setISO_039p_ResponseDetail(GeneralUtils.ExceptionToString("NO SE PUDO TENER CONEXION CON SERVICIO BIMO BANRED (CLIENTHANDLER_EXCEPTION) "
				   		, e, true));
			     iso.setWsIso_LogStatus(9);
			}else if (e.getCause() instanceof WebApplicationException) {
				
				 iso.setISO_039_ResponseCode("904");
				 iso.setISO_039p_ResponseDetail(GeneralUtils.ExceptionToString("NO SE PUDO TENER CONEXION CON SERVICIO BIMO BANRED (WEBAPPLICATION_EXCEPTION) "
				   		, e, true));
			     iso.setWsIso_LogStatus(9);
			}else{
				
				iso.setISO_039_ResponseCode("909");
				iso.setISO_039p_ResponseDetail(GeneralUtils.ExceptionToString("ERROR EN PROCESOS", e, false));
			}
		
			log.WriteLogMonitor("Error modulo ClientProcessorBimo::getResponseDataClientRest ", 
					     TypeMonitor.error, e);
			return null;
			
		}finally {
			
			iso.setWsTempAut((iso.getTickAut().getTime(TimeUnit.MILLISECONDS)/1000.0));
			isoRef.set(iso);
		}
		
	}
	

	private static void disableSslVerification() {
		
		Logger log = new Logger();
	    try
	    {
	    
	    	log.WriteLogMonitor("^^^^^ Execute SSL no Validation Function......!!!!!!", TypeMonitor.monitor, null);
	        // Create a trust manager that does not validate certificate chains
	        TrustManager[] trustAllCerts = new TrustManager[] {new X509TrustManager() {
	            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
	                return null;
	            }
	            public void checkClientTrusted(X509Certificate[] certs, String authType) {
	            }
	            public void checkServerTrusted(X509Certificate[] certs, String authType) {
	            }
	        }
	        };

	        // Install the all-trusting trust manager
	        SSLContext sc = SSLContext.getInstance("SSL");
	        sc.init(null, trustAllCerts, new java.security.SecureRandom());
	        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

	        // Create all-trusting host name verifier
	        HostnameVerifier allHostsValid = new HostnameVerifier() {
	            public boolean verify(String hostname, SSLSession session) {
	                return true;
	            }
	        };

	        // Install the all-trusting host verifier
	        HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
	    } catch (NoSuchAlgorithmException e) {
	        e.printStackTrace();
	    } catch (KeyManagementException e) {
	        e.printStackTrace();
	    }
	}
}
