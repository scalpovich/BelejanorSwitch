package com.belejanor.switcher.cscoreswitch;

import java.io.Serializable;

@SuppressWarnings("serial")
public class ContainerIsoQueue<T> implements Serializable{

	public ContainerIsoQueue(T iso, String value){
		
		this.iso  = (T) iso;
		this.IP = value;
	}
	public ContainerIsoQueue(){
		
	}
	
	private T iso;
	private String IP;
	private String secuencial;

	public T getIso() {
		return iso;
	}

	public void setIso(T iso) {
		this.iso = iso;
	}

	public String getIP() {
		return IP;
	}

	public void setIP(String iP) {
		IP = iP;
	}
	public String getSecuencial() {
		return secuencial;
	}
	public void setSecuencial(String secuencial) {
		this.secuencial = secuencial;
	}
	
	
}
