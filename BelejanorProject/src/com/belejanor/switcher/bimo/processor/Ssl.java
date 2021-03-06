package com.belejanor.switcher.bimo.processor;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Certificate;
import java.security.KeyStore;
import java.security.cert.CertificateFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

@SuppressWarnings("deprecation")
public class Ssl {

	private static KeyStore keyStore;
	private static CertificateFactory certificateFactory;
	private static TrustManagerFactory trustManagerFactory;

	static {
		try {
			certificateFactory = CertificateFactory.getInstance("X.509");
			keyStore = loadKeyStore();
			trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
			try (InputStream stream = WebRequests.class.getResourceAsStream("D:\\BIMO\\CertificadosProvistosBanred\\BimoBanred.cer")) {
				addCertificate("BimoBanred", stream);
			}
		} catch (Exception e) {
			certificateFactory = null;
			keyStore = null;
			trustManagerFactory = null;
			e.printStackTrace();
		}
	}

	static SSLContext createContext() {
		if (trustManagerFactory == null)
			return null;
		try {
			trustManagerFactory.init(keyStore);
			SSLContext context = SSLContext.getInstance("TLS");
			context.init(null, trustManagerFactory.getTrustManagers(), null);
			return context;
		} catch (Exception e) {
			return null;
		}
	}

	public static void addCertificate(String name, InputStream stream) {
		try {
			System.out.println("************ " + name);
			Certificate certificate = (Certificate) certificateFactory.generateCertificate(stream);
			keyStore.setCertificateEntry(name, (java.security.cert.Certificate) certificate);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static KeyStore loadKeyStore() throws Exception {
		KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
		Path path = Paths.get(System.getProperty("java.home"), "lib", "security", "cacerts");
		keyStore.load(Files.newInputStream(path), "changeit".toCharArray());
		return keyStore;
	}
}
