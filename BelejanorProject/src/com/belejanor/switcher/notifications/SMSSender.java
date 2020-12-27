package com.belejanor.switcher.notifications;

import com.belejanor.switcher.cscoreswitch.wIso8583;
import com.belejanor.switcher.logger.Logger;
import com.belejanor.switcher.logger.LoggerConfig.TypeMonitor;
import com.belejanor.switcher.memcached.MemoryGlobal;
import com.belejanor.switcher.utils.GeneralUtils;
import com.belejanor.switcher.utils.Ref;
import com.belejanor.switcher.utils.SerializationObject;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.security.cert.X509Certificate;

public class SMSSender {

	static {
	    disableSslVerification();
	}
	
	private Logger log;
	
	public SMSSender() {
		
		log = new Logger();
	}
	
	@SuppressWarnings("deprecation")
	public void sendSMS(String message, Ref<RespuestaSMS> ref, wIso8583 iso) {
		
		RespuestaSMS res = new RespuestaSMS();
		
		try {
			
			Client client = Client.create();
			client.setConnectTimeout(iso.getWsTransactionConfig().getProccodeTimeOutValue()/2);
			client.setReadTimeout(iso.getWsTransactionConfig().getProccodeTimeOutValue());
			
			WebResource resourse = client.resource(MemoryGlobal.SMSUrl);
			ClientResponse docRes = resourse.path(MemoryGlobal.SMSPath)
									.queryParam(MemoryGlobal.SMSParam,message)
									.get(ClientResponse.class);
			
			if(docRes.getStatus() == 200){
				
				docRes.bufferEntity();
				String responseString = docRes.getEntity(String.class);
				res = (RespuestaSMS) SerializationObject.StringToObject(responseString,RespuestaSMS.class);
				if(res == null){
					
					res = new RespuestaSMS();
					res.setCod_respuesta("100");
					res.setDes_respuesta("ERROR EN PROCESOS SERIALIZACION MENSAJES SMS");
					res.setId_transaccion("-1");
				}
				
			}else {
				
				res = new RespuestaSMS();
				res.setCod_respuesta(String.valueOf(docRes.getStatus()));
				res.setDes_respuesta(docRes.getResponseStatus().toString());
				res.setId_transaccion("-1");
			}
			
			
		} catch (Exception e) {
			
			res = new RespuestaSMS();
			res.setCod_respuesta("909");
			res.setDes_respuesta(GeneralUtils.ExceptionToString(null, e, false));
			res.setId_transaccion("-1");
			log.WriteLogMonitor("Error cerrar conexion Modulo" + this.getClass().getName() + "::sendSMS"
					, TypeMonitor.error, e);
		}finally {
			
			ref.set(res);
		}
	}
	
    public static void disableSslVerification() {
		
	    try
	    {
	    
	    	
	        // Create a trust manager that does not validate certificate chains
	        TrustManager[] trustAllCerts = new TrustManager[] {new X509TrustManager() {
	            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
	                return null;
	            }
	            @SuppressWarnings("unused")
				public void checkClientTrusted(X509Certificate[] certs, String authType) {
	            }
	            @SuppressWarnings("unused")
				public void checkServerTrusted(X509Certificate[] certs, String authType) {
	            }
				@Override
				public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType)
						throws CertificateException {
					// TODO Auto-generated method stub
					
				}
				@Override
				public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType)
						throws CertificateException {
					// TODO Auto-generated method stub
					
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


