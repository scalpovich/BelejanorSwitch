package com.belejanor.switcher.memcached;

import java.sql.Connection;
import java.sql.SQLException;

import com.belejanor.switcher.logger.Logger;
import com.belejanor.switcher.logger.LoggerConfig.TypeMonitor;
import com.belejanor.switcher.sqlservices.DBCPDataSource;
import com.belejanor.switcher.sqlservices.DataSetMemoryLoader;

public class TrxCf_GRQ extends PrincipalTrx {

	private String tpp;
	private String usr;
	private String idm;
	private String ter;
	private String sid;
	private String rol;
	private String nvs;
	private String pwd;
	private String npw;
	private String ipa;
	private String are;
	private String cio;
	private String suc;
	private String ofc;
	private String msg;
	private String rev;
	private String can;
	private String fcn;
	private String mnr;
	
	public TrxCf_GRQ() {
		super();
	}
	public TrxCf_GRQ(String subsystem_pk, String transaction_pk, String version_pk, String tip_pk) {
		super(subsystem_pk, transaction_pk, version_pk, tip_pk);
	}
	public String getTpp() {
		return tpp;
	}
	public void setTpp(String tpp) {
		this.tpp = tpp;
	}
	public String getUsr() {
		return usr;
	}
	public void setUsr(String usr) {
		this.usr = usr;
	}
	public String getIdm() {
		return idm;
	}
	public void setIdm(String idm) {
		this.idm = idm;
	}
	public String getTer() {
		return ter;
	}
	public void setTer(String ter) {
		this.ter = ter;
	}
	public String getSid() {
		return sid;
	}
	public void setSid(String sid) {
		this.sid = sid;
	}
	public String getRol() {
		return rol;
	}
	public void setRol(String rol) {
		this.rol = rol;
	}
	public String getNvs() {
		return nvs;
	}
	public void setNvs(String nvs) {
		this.nvs = nvs;
	}
	public String getPwd() {
		return pwd;
	}
	public void setPwd(String pwd) {
		this.pwd = pwd;
	}
	public String getNpw() {
		return npw;
	}
	public void setNpw(String npw) {
		this.npw = npw;
	}
	public String getIpa() {
		return ipa;
	}
	public void setIpa(String ipa) {
		this.ipa = ipa;
	}
	public String getAre() {
		return are;
	}
	public void setAre(String are) {
		this.are = are;
	}
	public String getCio() {
		return cio;
	}
	public void setCio(String cio) {
		this.cio = cio;
	}
	public String getSuc() {
		return suc;
	}
	public void setSuc(String suc) {
		this.suc = suc;
	}
	public String getOfc() {
		return ofc;
	}
	public void setOfc(String ofc) {
		this.ofc = ofc;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public String getRev() {
		return rev;
	}
	public void setRev(String rev) {
		this.rev = rev;
	}
	public String getCan() {
		return can;
	}
	public void setCan(String can) {
		this.can = can;
	}
	public String getFcn() {
		return fcn;
	}
	public void setFcn(String fcn) {
		this.fcn = fcn;
	}
	
	public String getMnr() {
		return mnr;
	}
	public void setMnr(String mnr) {
		this.mnr = mnr;
	}
	public TrxCf_GRQ getDataTrxCf_GRQ(String subsystem_pk, String transaction_pk, String version_pk, String tip_pk){
		TrxCf_GRQ grq = null;
		Logger log = null;
		try {
			
			grq =   MemoryGlobal.ListTrxCf_GRQMem.stream().
					filter(p -> p.getSubsystem_pk().equals(subsystem_pk)
							 && p.getTransaction_pk().equals(transaction_pk)
							 && p.getVersion_pk().equals(version_pk)
							 && p.getTip_pk().equals(tip_pk))
					.findFirst().orElseGet(()-> null);
			
		} catch (Exception e) {
			log = new Logger();
			log.WriteLogMonitor("Error modulo TrxCf_GRQ::getDataTrxCf_GRQ ", TypeMonitor.error, e);
		}
		return grq;
	}
	
	public Runnable getDataGrq(){
		
			Runnable runnable = new Runnable() {
				@Override
				public void run() {

					Logger log;
					Connection conn = null;
					try {
						
						conn = DBCPDataSource.getConnection();
						String query = "SELECT * FROM TRXCF_GRQ";		
						DataSetMemoryLoader<TrxCf_GRQ> loader = 
					    new DataSetMemoryLoader<TrxCf_GRQ>
						(conn, TrxCf_GRQ.class, query);
						MemoryGlobal.ListTrxCf_GRQMem = loader.LoadDataClass();	
						
					} catch (SQLException e) {
						
						log = new Logger();
						log.WriteLogMonitor("Error modulo TrxCf_GRQ::getDataGrq() ", TypeMonitor.error, e);
						
					} catch (Exception e) {
						
						log = new Logger();
						log.WriteLogMonitor("Error modulo TrxCf_GRQ::getDataGrq() ", TypeMonitor.error, e);
						
					} finally {
						
						try {
							if(conn != null)
								conn.close();
						} catch (SQLException e) {
							e.printStackTrace();
						}
					}
				}
			};	
		
		return runnable;
	}
	
}
