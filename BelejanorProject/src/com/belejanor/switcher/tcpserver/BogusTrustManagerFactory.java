package com.belejanor.switcher.tcpserver;

import java.security.InvalidAlgorithmParameterException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import javax.net.ssl.ManagerFactoryParameters;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactorySpi;
import javax.net.ssl.X509TrustManager;

public class BogusTrustManagerFactory extends TrustManagerFactorySpi {

	
	static final X509TrustManager X509 = new X509TrustManager() {
	    public void checkClientTrusted(X509Certificate[] x509Certificates,
	            String s) throws CertificateException {
	    }

	    public void checkServerTrusted(X509Certificate[] x509Certificates,
	            String s) throws CertificateException {
	    }

	    public X509Certificate[] getAcceptedIssuers() {
	        return new X509Certificate[0];
	    }
	};
	
	static final TrustManager[] X509_MANAGERS = new TrustManager[] { X509 };
	
	public BogusTrustManagerFactory() {
		
	}
	@Override
	 protected TrustManager[] engineGetTrustManagers() {
	     return X509_MANAGERS;
	 }

	 @Override
	 protected void engineInit(KeyStore keystore) throws KeyStoreException {
	     // noop
	 }

	 @Override
	 protected void engineInit(ManagerFactoryParameters managerFactoryParameters)
	         throws InvalidAlgorithmParameterException {
	     // noop
	 }
}
