package com.belejanor.switcher.memcached;

public class PrincipalTrx {

	private String Subsystem_pk;
	private String Transaction_pk;
	private String Version_pk;
	private String Tip_pk;
	public PrincipalTrx(String subsystem_pk, String transaction_pk, String version_pk, String tip_pk) {
		super();
		Subsystem_pk = subsystem_pk;
		Transaction_pk = transaction_pk;
		Version_pk = version_pk;
		Tip_pk = tip_pk;
	}
	public PrincipalTrx(){
		
	}
	public String getSubsystem_pk() {
		return Subsystem_pk;
	}
	public void setSubsystem_pk(String subsystem_pk) {
		Subsystem_pk = subsystem_pk;
	}
	public String getTransaction_pk() {
		return Transaction_pk;
	}
	public void setTransaction_pk(String transaction_pk) {
		Transaction_pk = transaction_pk;
	}
	public String getVersion_pk() {
		return Version_pk;
	}
	public void setVersion_pk(String version_pk) {
		Version_pk = version_pk;
	}
	public String getTip_pk() {
		return Tip_pk;
	}
	public void setTip_pk(String tip_pk) {
		Tip_pk = tip_pk;
	}
	
}
