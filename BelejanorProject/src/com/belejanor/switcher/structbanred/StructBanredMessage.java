package com.belejanor.switcher.structbanred;

import java.io.Serializable;

@SuppressWarnings("serial")
public class StructBanredMessage implements Serializable {

	private TypeMessage typeMessage;
	private String secuentialMessage;
	private String proccodeMessage;
	private String bodyMessage;
	private byte[] bodyBytesMessage;
	private String bodyMessageResponse;
	private byte[] bodyBytesMessageResponse;
	
	public TypeMessage getTypeMessage() {
		return typeMessage;
	}
	public void setTypeMessage(TypeMessage typeMessage) {
		this.typeMessage = typeMessage;
	}
	public String getSecuentialMessage() {
		return secuentialMessage;
	}
	public String getProccodeMessage() {
		return proccodeMessage;
	}
	public void setProccodeMessage(String proccodeMessage) {
		this.proccodeMessage = proccodeMessage;
	}
	public void setSecuentialMessage(String secuentialMessage) {
		this.secuentialMessage = secuentialMessage;
	}
	public String getBodyMessage() {
		return bodyMessage;
	}
	public void setBodyMessage(String bodyMessage) {
		this.bodyMessage = bodyMessage;
	}
	public byte[] getBodyBytesMessage() {
		return bodyBytesMessage;
	}
	public void setBodyBytesMessage(byte[] bodyBytesMessage) {
		this.bodyBytesMessage = bodyBytesMessage;
	}
	public String getBodyMessageResponse() {
		return bodyMessageResponse;
	}
	public void setBodyMessageResponse(String bodyMessageResponse) {
		this.bodyMessageResponse = bodyMessageResponse;
	}
	public byte[] getBodyBytesMessageResponse() {
		return bodyBytesMessageResponse;
	}
	public void setBodyBytesMessageResponse(byte[] bodyBytesMessageResponse) {
		this.bodyBytesMessageResponse = bodyBytesMessageResponse;
	}
	
}
