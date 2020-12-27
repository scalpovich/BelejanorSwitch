package com.belejanor.switcher.struct.equifax;

public enum tipoModelo {

	Originacion, Comportamiento;
	private String tipoModeloName = null;
	tipoModelo(String tipoModeloName) {
        this.tipoModeloName = tipoModeloName;
    }
	tipoModelo() {
		
	}
    public String tipoModeloName() {return tipoModeloName;}
	
	static public boolean isMember(String aName) {
		boolean r = false;
		tipoModelo[] a = tipoModelo.values();
	    for (tipoModelo t : a) {
	    	   if (t.name().equals(aName)) {
	               r = true;
	               break;
	    	   }
	       r = false;
		}
	    return r;
	}
}
