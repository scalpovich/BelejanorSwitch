package com.belejanor.switcher.utils;

import java.math.BigDecimal;

public class NumbersUtils {
	
	public static String DecimalToStringNumberIso(double valor, int relleno) {
		
		return String.format("%0"+ (relleno  + 1) +".2f", valor).replace(",", "").replace(".", "");
	}
	public static double truncateDecimal(double x,int numberofDecimals)
	{
		BigDecimal result = BigDecimal.ZERO;
	    if ( x < 0) {
	        result = new BigDecimal(String.valueOf(x)).setScale(numberofDecimals, BigDecimal.ROUND_FLOOR);
	    } else {
	    	result = new BigDecimal(String.valueOf(x)).setScale(numberofDecimals, BigDecimal.ROUND_HALF_DOWN);
	    }
	    return result.doubleValue();
	}
	public static Double formatearDecimales(Double numero, Integer numeroDecimales) {
		return Math.round(numero * Math.pow(10, numeroDecimales)) / Math.pow(10, numeroDecimales);
	}
	public static double Redondear(double numero,int digitos)
	{
	      int cifras=(int) Math.pow(10,digitos);
	      return Math.rint(numero*cifras)/cifras;
	}
	public static boolean isNumeric(String cadena) {

        boolean resultado;

        try {
        	Long.parseLong(cadena);
            resultado = true;
        } catch (NumberFormatException excepcion) {
            resultado = false;
        }

        return resultado;
    }
}
