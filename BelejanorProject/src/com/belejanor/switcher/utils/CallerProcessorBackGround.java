package com.belejanor.switcher.utils;

import java.util.Date;

public class CallerProcessorBackGround<T> {

	@SuppressWarnings("hiding")
	public <T>String GeneraSecuencialAlfaNumerico(T longitud) {
	
		int length = Integer.parseInt((String) longitud);
		return GeneralUtils.GetSecuencial(length);
	}
	
	@SuppressWarnings("hiding")
	public <T> String GeneraSecuencialNumerico(T longitud) {
		
		int length = Integer.parseInt((String) longitud);
		String value = GeneralUtils.GetSecuencialNumeric(length);
		return value;
		
	}
	
	@SuppressWarnings("hiding")
	public <T>Date GetDate(T type) {
		Date d = new Date();
		if(((String) type).equalsIgnoreCase("H"))
			d = new Date();
		return d;
	}
}
