package com.belejanor.switcher.memcached;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.belejanor.switcher.logger.Logger;
import com.belejanor.switcher.logger.LoggerConfig.TypeMonitor;
import com.belejanor.switcher.sqlservices.DBCPDataSource;
import com.belejanor.switcher.sqlservices.DataSetMemoryLoader;

public class TrxCf_Cam extends PrincipalTrx{

	private String tab_name;
	private String tab_alias;
	private int reg_nro;
	private String cam_name;
	private String cam_alias;
	private String oldval;
	private String val;
	private String pk;
	private String tipo;
	private String function_name;
	
	public TrxCf_Cam() {
		super();
		
	}
	public TrxCf_Cam(String subsystem_pk, String transaction_pk, String version_pk, String tip_pk) {
		super(subsystem_pk, transaction_pk, version_pk, tip_pk);
		
	}
	public String getTab_name() {
		return tab_name;
	}
	public void setTab_name(String tab_name) {
		this.tab_name = tab_name;
	}
	public String getTab_alias() {
		return tab_alias;
	}
	public void setTab_alias(String tab_alias) {
		this.tab_alias = tab_alias;
	}
	public int getReg_nro() {
		return reg_nro;
	}
	public void setReg_nro(int reg_nro) {
		this.reg_nro = reg_nro;
	}
	public String getCam_name() {
		return cam_name;
	}
	public void setCam_name(String cam_name) {
		this.cam_name = cam_name;
	}
	public String getCam_alias() {
		return cam_alias;
	}
	public void setCam_alias(String cam_alias) {
		this.cam_alias = cam_alias;
	}
	public String getOldval() {
		return oldval;
	}
	public void setOldval(String oldval) {
		this.oldval = oldval;
	}
	public String getVal() {
		return val;
	}
	public void setVal(String val) {
		this.val = val;
	}
	public String getPk() {
		return pk;
	}
	public void setPk(String pk) {
		this.pk = pk;
	}
	public String getTipo() {
		return tipo;
	}
	public void setTipo(String tipo) {
		this.tipo = tipo;
	}
	public String getFunction_name() {
		return function_name;
	}
	public void setFunction_name(String function_name) {
		this.function_name = function_name;
	}
	
	public List<TrxCf_Cam> getTrxCf_CamListObject(String subsystem_pk, String transaction_pk, String version_pk, String tip_pk){
		List<TrxCf_Cam> lista = null;
		Logger log = null;
		try {
			
			lista =   MemoryGlobal.ListTrxCf_Cam.stream().
					filter(p -> p.getSubsystem_pk().equals(subsystem_pk)
							 && p.getTransaction_pk().equals(transaction_pk)
							 && p.getVersion_pk().equals(version_pk)
							 && p.getTip_pk().equals(tip_pk))
					.peek(Objects::requireNonNull)
					.collect(Collectors.toList());
			
		} catch (Exception e) {
			
			log = new Logger();
			log.WriteLogMonitor("Error modulo TrxCf_Cam::getTrxCf_CamListObject ", TypeMonitor.error, e);
		}
		return lista;
	}
	
	public Runnable getDataCam(){
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				
				Logger log;
				Connection conn = null;
				try {
					
					conn = DBCPDataSource.getConnection();
					String query = "SELECT * FROM TRXCF_CAM";		
					DataSetMemoryLoader<TrxCf_Cam> loader = 
				    new DataSetMemoryLoader<TrxCf_Cam>
					(conn, TrxCf_Cam.class, query);
					MemoryGlobal.ListTrxCf_Cam = loader.LoadDataClass();		
					
				} catch (SQLException e) {
					
					log = new Logger();
					log.WriteLogMonitor("Error modulo TrxCf_Cam::getDataCam() ", TypeMonitor.error, e);
					
				} catch (Exception e) {
					
					log = new Logger();
					log.WriteLogMonitor("Error modulo TrxCf_Cam::getDataCam() ", TypeMonitor.error, e);
					
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
