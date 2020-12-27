package com.belejanor.switcher.utils;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import com.belejanor.switcher.logger.Logger;
import com.belejanor.switcher.logger.LoggerConfig.TypeMonitor;

public class FormatUtils {

	public static enum TypeTemp { segundos, minutos, horas, dias, meses}; 
	
	public static java.sql.Date convertUtilToSql(java.util.Date uDate) {
        java.sql.Date sDate = new java.sql.Date(uDate.getTime());
        return sDate;
    }
	
	public static String bytesToHex(byte[] hashInBytes) {

        StringBuilder sb = new StringBuilder();
        for (byte b : hashInBytes) {
            sb.append(String.format("%02x", b));
            sb.append("-");
        }
        String a = sb.toString();
        a = StringUtils.trimEnd(a, "-");
        return a;
    }
	
	public static String byteToHex(byte _byte) {
		StringBuilder sb = new StringBuilder();
		 sb.append(String.format("%02x", _byte));
		 return sb.toString();
	}
	
	public static byte[] hexStringToByte(String s) {
	    int len = s.length();
	    byte[] data = new byte[len / 2];
	    for (int i = 0; i < len; i += 2) {
	        data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
	                             + Character.digit(s.charAt(i+1), 16));
	    }
	    return data;
	}
	
	public static Date sumarRestarHorasFecha(Date fecha, TypeTemp typeTemp, int valor){
		  
		Calendar calendar = Calendar.getInstance();	
		calendar.setTime(fecha);
		switch (typeTemp) {
		case segundos:
			 calendar.add(Calendar.SECOND, valor);
			break;
		case minutos:
			 calendar.add(Calendar.MINUTE, valor);
			break;
		case horas:
			 calendar.add(Calendar.HOUR, valor);
			 break;
		case dias:
			 calendar.add(Calendar.DAY_OF_YEAR, valor);
			 break;
		case meses:
			 calendar.add(Calendar.MONTH, valor);
			 break;
		default:
			break;
		}
		return calendar.getTime(); 
	 }
	
	public static String DateToString(Date date, String formatText){
		
		Logger log = null;
		try {
			DateFormat df = new SimpleDateFormat(formatText);
			Date today = date;        
			String reportDate = df.format(today);
			return reportDate;
		} catch (Exception e) {
			log = new Logger();
			log.WriteLogMonitor("Error modulo FormatUtils::DateToString ", TypeMonitor.error, e);
			return null;
		}
	}
    public static String DateToStringThreadSafe(Date date, String formatText){
		
    	ThreadLocal<SimpleDateFormat> tl = new ThreadLocal<SimpleDateFormat>();
    
    	    SimpleDateFormat sdf = tl.get();
    	    if(sdf == null) {
    	    	
    	        sdf = new SimpleDateFormat(formatText);
    	        tl.set(sdf);
    	    }
    	    return sdf.format(date);
	}
	public static String DateToString(String dateString, String formatSource, String formatTarget){
		
		Logger log = null;
		Date dateAux = null;
		DateFormat format = null;
		try {
			format = new SimpleDateFormat(formatSource);
			dateAux = format.parse(dateString);
			String fecha = FormatUtils.DateToString(dateAux, formatTarget);
			return fecha;
		} catch (Exception e) {
			log = new Logger();
			log.WriteLogMonitor("Error modulo FormatUtils::DateToString II", TypeMonitor.error, e);
			return null;
		}
	}
	public static Date StringToDate(String date, String format){
		
		Logger log = null;
		try {
			
			SimpleDateFormat formatter = new SimpleDateFormat(format,Locale.US);
			Date dater = formatter.parse(date);
			return dater;
			
		} catch (Exception e) {
			
			log = new Logger();
			log.WriteLogMonitor("Error modulo FormatUtils::StringToDate ", TypeMonitor.error, e);
			return null;
		}
	}
	public static Date StringToDateTrim(String date, String format){
		
		Logger log = null;
		try {
			
			String nuevo = "";
			for (char c : date.toCharArray()) {
				String f = String.valueOf(c);
				if(org.apache.commons.lang.StringUtils.isNotBlank(f))
					nuevo += f;
			}
			SimpleDateFormat formatter = new SimpleDateFormat(format,Locale.US);
			Date dater = formatter.parse(nuevo);
			return dater;
			
		} catch (Exception e) {
			
			log = new Logger();
			log.WriteLogMonitor("Error modulo FormatUtils::StringToDate ", TypeMonitor.error, e);
			return null;
		}
	}
	public static String StringToDecimalFormat(String value){
		
		String response =  "";
		Logger log = null;
		try {
			
			DecimalFormat df = new DecimalFormat("0.00");
			df.setMaximumFractionDigits(2);
			String stringLitersOfPetrol = value;
			Double litersOfPetrol=Double.parseDouble(stringLitersOfPetrol);
			stringLitersOfPetrol = df.format(litersOfPetrol);
			response = stringLitersOfPetrol;
			
		} catch (Exception e) {
			
			log = new Logger();
			log.WriteLogMonitor("Error modulo FormatUtils::StringToDecimalFormat ", TypeMonitor.error, e);
		}
		return response;
	}
	public static Date StringToDateIso(String date, String format){
		Logger log = null;
		try {
			
			Date _date;
		    SimpleDateFormat df = new SimpleDateFormat(format);
		    _date = df.parse(date);
			return _date;
			
		} catch (Exception e) {
			log = new Logger();
			log.WriteLogMonitor("Error modulo FormatUtils::StringToDate ", TypeMonitor.error, e);
			return new Date();
		}
	}
	
	public static double TruncateDouble(double numero, int digitos) {
        double resultado;
        resultado = numero * Math.pow(10, digitos);
        resultado = Math.round(resultado);
        resultado = resultado/Math.pow(10, digitos);
        return resultado;
    }
	
	public static XMLGregorianCalendar DateToXMLGregorian(Date fecha){
		
		Logger log = null;
		GregorianCalendar calendar = null;
	    XMLGregorianCalendar xgcal = null;
		try {
		        calendar = new GregorianCalendar();
		        calendar.setTime(fecha);
		        xgcal = DatatypeFactory.newInstance().newXMLGregorianCalendar(calendar);
		} catch (DatatypeConfigurationException e) {
			log = new Logger();
			log.WriteLogMonitor("Error modulo FormatUtils::XMLGregorianCalendar ", TypeMonitor.error, e);
		}
	    return xgcal;
	}
	
	public static XMLGregorianCalendar getDateSystemXMLGregorian(){
		
		Logger log = null;
		XMLGregorianCalendar gDateFormatted = null;
		try {
			
			String FORMATER = "yyyy-MM-dd'T'HH:mm:ss";
		    DateFormat format = new SimpleDateFormat(FORMATER);
		    Date date = new Date();
		    gDateFormatted =
		        DatatypeFactory.newInstance().newXMLGregorianCalendar(format.format(date));
			
		} catch (Exception e) {
			log = new Logger();
			log.WriteLogMonitor("Error modulo FormatUtils::getDateSystemXMLGregorian ", TypeMonitor.error, e);
		}
		
		return gDateFormatted;
	}
	
	public static XMLGregorianCalendar getDateXMLGregorianSimple(String fecha){
		
		XMLGregorianCalendar xmlDate = null;
		Logger log = null;
		try {
			
			Date dob=null;
			DateFormat df= new SimpleDateFormat("yyyy-MM-dd");
			dob=df.parse(fecha);
			GregorianCalendar cal = new GregorianCalendar();
			cal.setTime(dob);
			xmlDate = DatatypeFactory.newInstance()
					.newXMLGregorianCalendarDate(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, 
							cal.get(Calendar.DAY_OF_MONTH), DatatypeConstants.FIELD_UNDEFINED);
			
		} catch (Exception e) {
			
			log = new Logger();
			log.WriteLogMonitor("Error modulo FormatUtils::getDateXMLGregorianSimple ", TypeMonitor.error, e);
		}
		
		return xmlDate;
	}
	
	public static int[] getEdad(String formatDate, String fecNac){
		Logger log = null;
		int[] edad = new int[] {-1, -1, -1};
		try {
			
			DateTimeFormatter fmt = DateTimeFormatter.ofPattern(formatDate);
			LocalDate fechaNac = LocalDate.parse(fecNac, fmt);
			LocalDate ahora = LocalDate.now();
			Period periodo = Period.between(fechaNac, ahora);
			edad[0] = periodo.getYears();
			edad[1] = periodo.getMonths();
			edad[2] = periodo.getDays();
			
		} catch (Exception e) {
			
			log = new Logger();
			log.WriteLogMonitor("Error modulo FormatUtils::getEdad ", TypeMonitor.error, e);
		}
		return edad;
	}
	
	public static Timestamp convertStringToTimestamp(String str_date, String format) {
		Logger log = null;
	    try {
	    	
	      DateFormat formatter;
	      formatter = new SimpleDateFormat(format);
	      Date date = (Date) formatter.parse(str_date);
	      java.sql.Timestamp timeStampDate = new Timestamp(date.getTime());

	      return timeStampDate;
	    } catch (Exception e) {
	    	log = new Logger();
			log.WriteLogMonitor("Error modulo FormatUtils::convertStringToTimestamp ", TypeMonitor.error, e);
	      return null;
	    }
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
	
	public static Date getdateNow(String type) {
		
		if(type.equalsIgnoreCase("H"))
			return new Date();
		return new Date();
	}
	
	public static String filledWhitZeroes(int number) {
		
		return String.format("%0"+ (number) +"d",0);
	}

	public static String filledWhitSpaces(int number) {
		
			return String.format("%"+ (number) +"s","");
	}
	
}
