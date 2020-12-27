package com.belejanor.switcher.banred.pagodirecto.struct;


public enum TypeTransactionCode {

	CONTROLMESSAGE_2099("2099+"), 
	CONTROLMESSAGE_2001("2001+"),
	CONSULTA_PAGO_DIRECTO("0163+"),
	PAGO_TARJETA_CREDITO("0239+"),
	TRANSFERENCIA_CTA_CORRIENTE("0439+"),
	TRANSFERENCIA_CTA_AHORROS("0539+"),
	REVERSO_REAL_TRANSFER_CTA_AHO("0524-"),
	REVERSO_REAL_TRANSFER_CTA_CORR("0424-"),
	REVERSO_REAL_TRANSFER_TRJ_CRED("0224-"),
	REVERSO_COND_TRANSFER_TRJ_CRED("0239-"),
	REVERSO_COND_TRANSFER_CTA_AHO("0539-"),
	REVERSO_COND_TRANSFER_CTA_CORR("0439-");
	
	
	private String value;
    private TypeTransactionCode(String value)
    {
      this.value = value;
    }

    @Override
    public String toString()
    {
      return this.value; 
    }
    

	public static TypeTransactionCode fromDisplayString(String displayString)
    {
        for(TypeTransactionCode type : TypeTransactionCode.values()) {
        	
        	if(type.value.equals(displayString)) {
        		
        		return type;
        	}
        }
        return null;
    }
	
	public static TypeTransactionCode getByName(String name){
		for(TypeTransactionCode prop : values()){
		      if(prop.toString().equals(name)){
		        return prop;
		      }
		 }
		 return null;
	  }
}
