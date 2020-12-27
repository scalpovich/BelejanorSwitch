package com.belejanor.switcher.struct.equifax;

public enum tipoConsulta {

	Titular, Garante,Codeudor;
	
	private String tipoConsultaName = null;
	tipoConsulta(String tipoConsultaName) {
        this.tipoConsultaName = tipoConsultaName;
    }
	tipoConsulta() {
		
	}
    public String tipoConsultaName() {return tipoConsultaName;}
	
	static public boolean isMember(String aName) {
		boolean r = false;
		tipoConsulta[] a = tipoConsulta.values();
	    for (tipoConsulta t : a) {
	    	   if (t.name().equals(aName)) {
	               r = true;
	               break;
	    	   }
	       r = false;
		}
	    return r;
	}
}
