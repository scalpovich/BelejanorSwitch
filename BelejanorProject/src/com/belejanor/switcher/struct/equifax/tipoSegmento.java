package com.belejanor.switcher.struct.equifax;

public enum tipoSegmento {

	AAA,AA,A;
	
	private String tipoSegmentoName = null;
	tipoSegmento(String tipoSegmentoName) {
        this.tipoSegmentoName = tipoSegmentoName;
    }
	tipoSegmento() {
		
	}
    public String tipoSegmentoName() {return tipoSegmentoName;}
	
	static public boolean isMember(String aName) {
		boolean r = false;
		tipoSegmento[] a = tipoSegmento.values();
	    for (tipoSegmento t : a) {
	    	   if (t.name().equals(aName)) {
	               r = true;
	               break;
	    	   }
	       r = false;
		}
	    return r;
	}
}
