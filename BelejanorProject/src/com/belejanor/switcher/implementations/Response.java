package com.belejanor.switcher.implementations;

public class Response<T> {

	private T object;
	private String secuencial; 

	public T getObject() {
		return object;
	}

	public void setObject(T object) {
		this.object = object;
	}

	public String getSecuencial() {
		return secuencial;
	}

	public void setSecuencial(String secuencial) {
		this.secuencial = secuencial;
	}
	
	
}
