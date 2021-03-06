package com.belejanor.switcher.parser;

import java.math.BigDecimal;
import java.text.Normalizer;
import java.util.Date;

import com.belejanor.switcher.utils.FormatUtils;

public class Tester {

	public static void main(String[] args) {
		
		//String a = "0";
		System.out.println(FormatUtils.DateToStringThreadSafe(new Date(), "YYYY-MM-dd HH-mm-ss.SSS"));
		
	}
	public static String limpiarAcentos(String cadena) {
	    String limpio =null;
	    if (cadena !=null) {
	        String valor = cadena;
	        valor = valor.toUpperCase();
	        // Normalizar texto para eliminar acentos, dieresis, cedillas y tildes
	        limpio = Normalizer.normalize(valor, Normalizer.Form.NFD);
	        // Quitar caracteres no ASCII excepto la enie, interrogacion que abre, exclamacion que abre, grados, U con dieresis.
	        limpio = limpio.replaceAll("[^\\p{ASCII}(N\u0303)(n\u0303)(\u00A1)(\u00BF)(\u00B0)(U\u0308)(u\u0308)]", "");
	        // Regresar a la forma compuesta, para poder comparar la enie con la tabla de valores
	        limpio = Normalizer.normalize(limpio, Normalizer.Form.NFC);
	    }
	    return limpio;
	}
	public static String replaceAcentsAndCharsEspecial(String cadena) {
		
		String cadenaNormalize = Normalizer.normalize(cadena, Normalizer.Form.NFD);   
		String cadenaSinAcentos = cadenaNormalize.replaceAll("[^\\p{ASCII}]", "");
		return cadenaSinAcentos;
	}
	
	@SuppressWarnings("unused")
	private static double truncateDecimal(double x,int numberofDecimals)
	{
		BigDecimal result = BigDecimal.ZERO;
	    if ( x > 0) {
	        result = new BigDecimal(String.valueOf(x)).setScale(numberofDecimals, BigDecimal.ROUND_FLOOR);
	    } else {
	    	result = new BigDecimal(String.valueOf(x)).setScale(numberofDecimals, BigDecimal.ROUND_CEILING);
	    }
	    return result.doubleValue();
	}

}
