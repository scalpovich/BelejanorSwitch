package com.belejanor.switcher.notifications;

import java.text.MessageFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Tester {

	public static void main(String[] args) {
		
		  // El email a validar
        //String email = "info@programacionextrema.com";
        //System.out.println(checkMail(email));
		String [] arr = new String[] {"42","Hola","segundo"};
		System.out.println(MessageFormat.format("String is {0}, number is {1}.", (Object[])arr));

	}
	
	public static boolean checkMail(String eMail) {
		
		try {
			
			 Pattern pattern = Pattern
		                .compile("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
		                        + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");
		 
		        Matcher mather = pattern.matcher(eMail);
		 
		        if (mather.find() == true) {
		            return true;
		        } else {
		            return false;
		        }
			
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

}
