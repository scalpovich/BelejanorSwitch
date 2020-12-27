package com.belejanor.switcher.detail;

import javax.xml.bind.annotation.XmlElement;

public class GeneralRequestClass{
	
	private String tpp;
	private String usr;
	private String idm;
	private String ter;
	private String sid;
	private String rol;
	private String nvs;
	private String ipa;
	private String tip;
	private String pwd;
	private String npw;
	private String sub;
	private String trn;
	private String ver;
	private String are;
	private String cio;
	private String suc;
	private String ofc;
	private String msg;
	private String rev;
	private String can;
	private String fcn;
	private String nmr;
	
	public GeneralRequestClass(){
		
	}
	@XmlElement(name="TPP")
	public String getTpp() {
		return tpp;
	}
	public void setTpp(String tpp) {
		this.tpp = tpp;
	}
	@XmlElement(name="USR")
	public String getUsr() {
		return usr;
	}
	public void setUsr(String usr) {
		this.usr = usr;
	}
	@XmlElement(name="IDM")
	public String getIdm() {
		return idm;
	}
	public void setIdm(String idm) {
		this.idm = idm;
	}
	@XmlElement(name="TER")
	public String getTer() {
		return ter;
	}
	public void setTer(String ter) {
		this.ter = ter;
	}
	@XmlElement(name="SID")
	public String getSid() {
		return sid;
	}
	public void setSid(String sid) {
		this.sid = sid;
	}
	@XmlElement(name="ROL")
	public String getRol() {
		return rol;
	}
	public void setRol(String rol) {
		this.rol = rol;
	}
	@XmlElement(name="NVS")
	public String getNvs() {
		return nvs;
	}
	public void setNvs(String nvs) {
		this.nvs = nvs;
	}
	@XmlElement(name="IPA")
	public String getIpa() {
		return ipa;
	}
	public void setIpa(String ipa) {
		this.ipa = ipa;
	}
	@XmlElement(name="TIP")
	public String getTip() {
		return tip;
	}
	public void setTip(String tip) {
		this.tip = tip;
	}
	@XmlElement(name="PWD")
	public String getPwd() {
		return pwd;
	}
	public void setPwd(String pwd) {
		this.pwd = pwd;
	}
	@XmlElement(name="NPW")
	public String getNpw() {
		return npw;
	}
	public void setNpw(String npw) {
		this.npw = npw;
	}
	@XmlElement(name="SUB")
	public String getSub() {
		return sub;
	}
	public void setSub(String sub) {
		this.sub = sub;
	}
	@XmlElement(name="TRN")
	public String getTrn() {
		return trn;
	}
	public void setTrn(String trn) {
		this.trn = trn;
	}
	@XmlElement(name="VER")
	public String getVer() {
		return ver;
	}
	public void setVer(String ver) {
		this.ver = ver;
	}
	@XmlElement(name="ARE")
	public String getAre() {
		return are;
	}
	public void setAre(String are) {
		this.are = are;
	}
	@XmlElement(name="CIO")
	public String getCio() {
		return cio;
	}
	public void setCio(String cio) {
		this.cio = cio;
	}
	@XmlElement(name="SUC")
	public String getSuc() {
		return suc;
	}
	public void setSuc(String suc) {
		this.suc = suc;
	}
	@XmlElement(name="OFC")
	public String getOfc() {
		return ofc;
	}
	public void setOfc(String ofc) {
		this.ofc = ofc;
	}
	@XmlElement(name="MSG")
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	@XmlElement(name="REV")
	public String getRev() {
		return rev;
	}
	public void setRev(String rev) {
		this.rev = rev;
	}
	@XmlElement(name="CAN")
	public String getCan() {
		return can;
	}
	public void setCan(String can) {
		this.can = can;
	}
	@XmlElement(name="FCN")
	public String getFcn() {
		return fcn;
	}
	public void setFcn(String fcn) {
		this.fcn = fcn;
	}
	@XmlElement(name="NMR")
	public String getNmr() {
		return nmr;
	}
	public void setNmr(String nmr) {
		this.nmr = nmr;
	}
		
}

