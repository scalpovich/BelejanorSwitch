package com.belejanor.switcher.struct.equifax;

public enum tipoDecision {

	Aprobado,Rechazado,Analista;
	
	private String tipoDecisionName = null;
	tipoDecision(String tipoDecisionName) {
        this.tipoDecisionName = tipoDecisionName;
    }
	tipoDecision() {
		
	}
    public String tipoDecisionName() {return tipoDecisionName;}
	
	static public boolean isMember(String aName) {
		boolean r = false;
		tipoDecision[] a = tipoDecision.values();
	    for (tipoDecision t : a) {
	    	   if (t.name().equals(aName)) {
	               r = true;
	               break;
	    	   }
	       r = false;
		}
	    return r;
	}
}
