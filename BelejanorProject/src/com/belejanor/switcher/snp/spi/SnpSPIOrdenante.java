package com.belejanor.switcher.snp.spi;

import java.util.Date;

public class SnpSPIOrdenante implements Cloneable {

	private Date fecha;
	private String msgid;
	private Date credttm;
	private String instrid;
	private String endtoendid;
	private String txid;
	private double ammount;
	private Date feccont;
	private String ord_nm;
	private String ord_id;
	private String ord_txid;
	private String ord_account;
	private String ord_account_type;
	private String ord_account_type_money;
	private String inst_acc_bce;
	private String inst_acc_bce_type;
	private String inst_acc_bce_type_money;
	private String inst_ord_code;
	private String inst_ord_age;
	private String inst_rcp_code;
	private String rcp_nm;
	private String rcp_id;
	private String rcp_txid;
	private String rcp_account;
	private String rcp_account_type;
	private String rcp_account_type_money;
	private String purp_code;
	private String cod_error_auth;
	private String des_error_auth;
	private int logstatus;
	private double fitswitch_time;
	private double bdd_time;
	private double auth_time;
	private String cod_return_core;
	private String rever_flag;
	private String rever_return_core_code;
	private Date date_last_bce;
	private String msgid_last_be;
	private String status_bce;
	private String iso_message;
	private String status_reason_bce;
	private String error_code_prop;
	private String des_code_prop;
	private String isMayorista;
	private boolean ordenante;
	
	public SnpSPIOrdenante(){
		
		this.isMayorista = "LOW";
	}
	
	public Date getFecha() {
		return fecha;
	}
	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}
	public String getMsgid() {
		return msgid;
	}
	public void setMsgid(String msgid) {
		this.msgid = msgid;
	}
	public Date getCredttm() {
		return credttm;
	}
	public void setCredttm(Date credttm) {
		this.credttm = credttm;
	}
	public String getInstrid() {
		return instrid;
	}
	public void setInstrid(String instrid) {
		this.instrid = instrid;
	}
	public String getEndtoendid() {
		return endtoendid;
	}
	public void setEndtoendid(String endtoendid) {
		this.endtoendid = endtoendid;
	}
	public String getTxid() {
		return txid;
	}
	public void setTxid(String txid) {
		this.txid = txid;
	}
	public double getAmmount() {
		return ammount;
	}
	public void setAmmount(double ammount) {
		this.ammount = ammount;
	}
	public Date getFeccont() {
		return feccont;
	}
	public void setFeccont(Date feccont) {
		this.feccont = feccont;
	}
	public String getOrd_nm() {
		return ord_nm;
	}
	public void setOrd_nm(String ord_nm) {
		this.ord_nm = ord_nm;
	}
	public String getOrd_id() {
		return ord_id;
	}
	public void setOrd_id(String ord_id) {
		this.ord_id = ord_id;
	}
	public String getOrd_txid() {
		return ord_txid;
	}
	public void setOrd_txid(String ord_txid) {
		this.ord_txid = ord_txid;
	}
	public String getOrd_account() {
		return ord_account;
	}
	public void setOrd_account(String ord_account) {
		this.ord_account = ord_account;
	}
	public String getOrd_account_type() {
		return ord_account_type;
	}
	public void setOrd_account_type(String ord_account_type) {
		this.ord_account_type = ord_account_type;
	}
	public String getOrd_account_type_money() {
		return ord_account_type_money;
	}
	public void setOrd_account_type_money(String ord_account_type_money) {
		this.ord_account_type_money = ord_account_type_money;
	}
	public String getInst_acc_bce() {
		return inst_acc_bce;
	}
	public void setInst_acc_bce(String inst_acc_bce) {
		this.inst_acc_bce = inst_acc_bce;
	}
	public String getInst_acc_bce_type() {
		return inst_acc_bce_type;
	}
	public void setInst_acc_bce_type(String inst_acc_bce_type) {
		this.inst_acc_bce_type = inst_acc_bce_type;
	}
	public String getInst_acc_bce_type_money() {
		return inst_acc_bce_type_money;
	}
	public void setInst_acc_bce_type_money(String inst_acc_bce_type_money) {
		this.inst_acc_bce_type_money = inst_acc_bce_type_money;
	}
	public String getInst_ord_code() {
		return inst_ord_code;
	}
	public void setInst_ord_code(String inst_ord_code) {
		this.inst_ord_code = inst_ord_code;
	}
	public String getInst_ord_age() {
		return inst_ord_age;
	}
	public void setInst_ord_age(String inst_ord_age) {
		this.inst_ord_age = inst_ord_age;
	}
	public String getInst_rcp_code() {
		return inst_rcp_code;
	}
	public void setInst_rcp_code(String inst_rcp_code) {
		this.inst_rcp_code = inst_rcp_code;
	}
	public String getRcp_nm() {
		return rcp_nm;
	}
	public void setRcp_nm(String rcp_nm) {
		this.rcp_nm = rcp_nm;
	}
	public String getRcp_id() {
		return rcp_id;
	}
	public void setRcp_id(String rcp_id) {
		this.rcp_id = rcp_id;
	}
	public String getRcp_txid() {
		return rcp_txid;
	}
	public void setRcp_txid(String rcp_txid) {
		this.rcp_txid = rcp_txid;
	}
	public String getRcp_account() {
		return rcp_account;
	}
	public void setRcp_account(String rcp_account) {
		this.rcp_account = rcp_account;
	}
	public String getRcp_account_type() {
		return rcp_account_type;
	}
	public void setRcp_account_type(String rcp_account_type) {
		this.rcp_account_type = rcp_account_type;
	}
	public String getRcp_account_type_money() {
		return rcp_account_type_money;
	}
	public void setRcp_account_type_money(String rcp_account_type_money) {
		this.rcp_account_type_money = rcp_account_type_money;
	}
	public String getPurp_code() {
		return purp_code;
	}
	public void setPurp_code(String purp_code) {
		this.purp_code = purp_code;
	}
	public String getCod_error_auth() {
		return cod_error_auth;
	}
	public void setCod_error_auth(String cod_error_auth) {
		this.cod_error_auth = cod_error_auth;
	}
	public String getDes_error_auth() {
		return des_error_auth;
	}
	public void setDes_error_auth(String des_error_auth) {
		this.des_error_auth = des_error_auth;
	}
	public int getLogstatus() {
		return logstatus;
	}
	public void setLogstatus(int logstatus) {
		this.logstatus = logstatus;
	}
	public double getFitswitch_time() {
		return fitswitch_time;
	}
	public void setFitswitch_time(double fitswitch_time) {
		this.fitswitch_time = fitswitch_time;
	}
	public double getBdd_time() {
		return bdd_time;
	}
	public void setBdd_time(double bdd_time) {
		this.bdd_time = bdd_time;
	}
	public double getAuth_time() {
		return auth_time;
	}
	public void setAuth_time(double auth_time) {
		this.auth_time = auth_time;
	}
	public String getCod_return_core() {
		return cod_return_core;
	}
	public void setCod_return_core(String cod_return_code) {
		this.cod_return_core = cod_return_code;
	}
	public String getRever_flag() {
		return rever_flag;
	}
	public void setRever_flag(String rever_flag) {
		this.rever_flag = rever_flag;
	}
	public String getRever_return_core_code() {
		return rever_return_core_code;
	}
	public void setRever_return_core_code(String rever_return_core_code) {
		this.rever_return_core_code = rever_return_core_code;
	}
	public Date getDate_last_bce() {
		return date_last_bce;
	}
	public void setDate_last_bce(Date date_last_bce) {
		this.date_last_bce = date_last_bce;
	}
	public String getMsgid_last_be() {
		return msgid_last_be;
	}
	public void setMsgid_last_be(String msgid_last_be) {
		this.msgid_last_be = msgid_last_be;
	}
	public String getStatus_bce() {
		return status_bce;
	}
	public void setStatus_bce(String status_bce) {
		this.status_bce = status_bce;
	}
	public String getIso_message() {
		return iso_message;
	}
	public void setIso_message(String iso_message) {
		this.iso_message = iso_message;
	}
	
	public String getStatus_reason_bce() {
		return status_reason_bce;
	}

	public void setStatus_reason_bce(String status_reason_bce) {
		this.status_reason_bce = status_reason_bce;
	}

	public String getError_code_prop() {
		return error_code_prop;
	}

	public void setError_code_prop(String error_code_prop) {
		this.error_code_prop = error_code_prop;
	}

	public String getDes_code_prop() {
		return des_code_prop;
	}

	public void setDes_code_prop(String des_code_prop) {
		this.des_code_prop = des_code_prop;
	}
	
	public String getIsMayorista() {
		return isMayorista;
	}

	public void setIsMayorista(String isMayorista) {
		this.isMayorista = isMayorista;
	}
	

	public boolean isOrdenante() {
		return ordenante;
	}

	public void setOrdenante(boolean ordenante) {
		this.ordenante = ordenante;
	}

	@Override
	public Object clone() throws CloneNotSupportedException {		
		return super.clone();
	}
}
