package com.belejanor.switcher.crypto;

public class GetPinBlock {
	  
	private DesEncriptor desEncriptor = new DesEncriptor();
	  
	  public GetPinBlock() {}
	  
	  public String getPinBlock(String pin) throws Exception {
	    if (pin.length() == 0)
	    {
	      String pinBlock = "201: No ingreso el PIN";
	      return pinBlock;
	    }
	    if (pin.length() != 5)
	    {
	      String pinBlock = "202: El PIN debe contener 5 caracteres";
	      return pinBlock;
	    }
	    try
	    {
	      Integer.parseInt(pin);
	      
	      String rellenoPin = rellenarPin(pin);
	      String pinBlock = desEncriptor.encrypt3DES(rellenoPin, "FAFAFF4564E67E87");
	      return pinBlock.toUpperCase();
	    } catch (NumberFormatException nfe) {}
	    return "203: El PIN debe ser numérico";
	  }
	  
	  private String rellenarPin(String pin)
	  {
	    StringBuffer cadena = new StringBuffer();
	    
	    int size = 16 - pin.length();
	    cadena.append(pin);
	    for (int i = 0; i < size; i++)
	    {
	      cadena.append('F');
	    }
	    return String.valueOf(cadena);
	  }
	  
}
