package com.belejanor.switcher.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.Normalizer;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class StringUtils {

    public static String truncateResponse(String desError){
		
		try {
			
			if(desError.length() <= 105){
				
				return desError;
			}else{
				
				return desError.substring(0, 105);
			}
			
		} catch (Exception e) {
			
			return desError;
		}
	}
	
	public static boolean isNullOrEmpty(String s) {
		s = s.replace("null", "");
	    if(s == null){
	    	return true;
	    }
	    if(s.length() == 0)
	    	return true;
	    return false;
	}
	public static boolean IsNullOrEmpty(String s) {
	    if(s == null){
	    	return true;
	    }
	    if(s.length() == 0)
	    	return true;
	    return false;
	}
	public static boolean isNullOrWhitespace(String s) {
	    return s == null || isWhitespace(s);

	}
	private static boolean isWhitespace(String s) {
	    int length = s.length();
	    if (length > 0) {
	        for (int i = 0; i < length; i++) {
	            if (!Character.isWhitespace(s.charAt(i))) {
	                return false;
	            }
	        }
	        return true;
	    }
	    return false;
	}
	public static String padRight(String s, int n, String value) {
	     return String.format("%1$-" + n + "s", s).replace(" ", value);
	}

	public static String padLeft(String s, int n, String value) {
	    return String.format("%1$" + n + "s", s).replace(" ", value);  
	}
	public static String Empty(){
		return "";
	}
	public static String Nullable(){
		return null;
	}
    public static byte[] compress(String string, String encoding) throws IOException {
		
		ByteArrayOutputStream os = new ByteArrayOutputStream(string.length());
		GZIPOutputStream gos = new GZIPOutputStream(os);
		gos.write(string.getBytes(encoding));
		gos.close();
		byte[] compressed = os.toByteArray();
		os.close();
		return compressed;
	}
	public static String decompress(byte[] compressed, String encoding) throws IOException {
		
		byte BUFFER_SIZE = 32;
		ByteArrayInputStream is = new ByteArrayInputStream(compressed);
		GZIPInputStream gis = new GZIPInputStream(is, BUFFER_SIZE);
		StringBuilder string = new StringBuilder();
		byte[] data = new byte[BUFFER_SIZE];

		for(int bytesRead = gis.read(data); bytesRead != -1; bytesRead = gis.read(data)) {
		string.append(new String(data, 0, bytesRead, encoding));
		}
		gis.close();
		is.close();
		return string.toString();
	}
	private static final String ORIGINAL
    = "�?áÉé�?íÓóÚúÑñÜü";
	private static final String REPLACEMENT
	    = "AaEeIiOoUuNnUu";
	public static String stripAccents(String str) {
		if (str == null) {
		    return null;
		}
		char[] array = str.toCharArray();
		for (int index = 0; index < array.length; index++) {
		    int pos = ORIGINAL.indexOf(array[index]);
		    if (pos > -1) {
		        array[index] = REPLACEMENT.charAt(pos);
		    }
		}
		return new String(array);
	}
    public static String replaceAcentsAndCharsEspecial(String cadena) {
		
		String cadenaNormalize = Normalizer.normalize(cadena, Normalizer.Form.NFD);   
		String cadenaSinAcentos = cadenaNormalize.replaceAll("[^\\p{ASCII}]", "");
		return cadenaSinAcentos;
	}
	public static String stripEspecial(String xml){
		
		try {
			
			String rep = " xmlns:urn=\"urn:iso:std:iso:20022:tech:xsd:pacs.002.001.05\" xmlns:ns5=\"http://www.bce.fin.ec/wsdl/snp/ReversoTecnicoSNPWS\" xmlns:ns8=\"urn:iso:std:iso:20022:tech:xsd:pacs.004.001.04\" xmlns:ns7=\"urn:iso:std:iso:20022:tech:xsd:pacs.003.001.04\" xmlns:urn1=\"urn:iso:std:iso:20022:tech:xsd:pacs.008.001.04\" xmlns:m=\"http://www.bce.fin.ec/wsdl/srr/TransferenciaElectronicaSRRWS\" xmlns:ns4=\"http://www.bce.fin.ec/wsdl/srr/VentanillaCompartidaSRRWS\"";
			String rep2 = " xmlns:urn=\"urn:iso:std:iso:20022:tech:xsd:pacs.002.001.05\" xmlns:ns6=\"http://www.bce.fin.ec/wsdl/snp/ReversoTecnicoSNPWS\" xmlns:ns5=\"http://www.bce.fin.ec/wsdl/srr/VentanillaCompartidaSRRWS\" xmlns:ns8=\"http://implementations.middleware.fitbank.com\" xmlns:ns9=\"urn:iso:std:iso:20022:tech:xsd:pacs.003.001.04\" xmlns:urn1=\"urn:iso:std:iso:20022:tech:xsd:pacs.008.001.04\" xmlns:ns10=\"urn:iso:std:iso:20022:tech:xsd:pacs.004.001.04\" xmlns:elec=\"http://electroniccash.middleware.fitbank.com\" xmlns:m=\"http://www.bce.fin.ec/wsdl/srr/TransferenciaElectronicaSRRWS\"";
			
			xml = xml.replace("ￃﾱ", "\u00f1");
			xml = xml.replace("ￃﾑ", "\u00d1");
			xml = xml.replace("ￃ�?", "\u00c1");
			xml = xml.replace("ￃﾉ", "\u00c9");
			xml = xml.replace("ￃ�?", "\u00cd");
			xml = xml.replace("ￃﾓ", "\u00d3");
			xml = xml.replace("ￃﾚ", "\u00da");
			xml = xml.replace("ￃﾡ", "\u00e1");
			xml = xml.replace("ￃﾩ", "\u00e9");
			xml = xml.replace("ￃﾭ", "\u00ed");
			xml = xml.replace("ￃﾳ", "\u00f3");
			xml = xml.replace("ￃﾺ", "\u00fa");
			xml = xml.replace("&lt;", "\u003c");
			xml = xml.replace("&gt;", "\u003e");
			xml = xml.replace("<*>", ".");
			xml = xml.replace("<**>", ".");
			xml = xml.replace("<<", "");
			xml = xml.replace(">>", "");
			xml = xml.replace("&#xd;", "");
			xml = xml.replace(rep, "");
			xml = xml.replace(rep2, "");
			
		} catch (Exception e) {e.printStackTrace();}
		
		return xml;
	}
	public static String trimEnd( String s,  String suffix) {

		 if (s.endsWith(suffix)) {

		   return s.substring(0, s.length() - suffix.length());

		 }
		 return s;
	}
	public static String TrimStart(String input, String charsToTrim)
	{
		return input.replaceAll("^[" + charsToTrim + "]+", "");	
	}
	private static final Pattern numberPattern = Pattern.compile("-?\\d+");
	public static boolean IsNumber(String string){
		return string != null && numberPattern.matcher(string).matches();
	}
	public static boolean isUpper(String data){
		
		int piv = 0;
		for (int i = 0; i < data.length(); i++) {
			
			if(Character.isUpperCase(data.charAt(i)))
				piv++;
		}
		if(piv == 0)
			return false;
		else
			return true;
	}
	
}