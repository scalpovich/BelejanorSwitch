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

public class AdditionalIsoValues extends Thread {

	private String proccode;
	private String canal_cod;
	private int net_id;
	private double ammountdebit;
	private String iso_row;
	private String iso_value;
	private String comments;
	
	public AdditionalIsoValues(){
		
	}
	
	public AdditionalIsoValues(String Proccode, int NetId, String CanalCod, double AmmountDebit){
		
		this.proccode = Proccode;
		this.net_id = NetId;
		this.canal_cod = CanalCod;
		this.ammountdebit = AmmountDebit;
	}

	public String getProccode() {
		return proccode;
	}

	public void setProccode(String proccode) {
		this.proccode = proccode;
	}

	public String getCanal_cod() {
		return canal_cod;
	}

	public void setCanal_cod(String canal_cod) {
		this.canal_cod = canal_cod;
	}

	public int getNet_id() {
		return net_id;
	}

	public void setNet_id(int net_id) {
		this.net_id = net_id;
	}

	public double getAmmountdebit() {
		return ammountdebit;
	}

	public void setAmmountdebit(double ammountdebit) {
		this.ammountdebit = ammountdebit;
	}

	public String getIso_row() {
		return iso_row;
	}

	public void setIso_row(String iso_row) {
		this.iso_row = iso_row;
	}

	public String getIso_value() {
		return iso_value;
	}

	public void setIso_value(String iso_value) {
		this.iso_value = iso_value;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}
	
	@Override
	public void run(){
		
		Logger log;
		Connection conn = null;
		try {
			
			conn = DBCPDataSource.getConnection();
			String query = "SELECT * FROM ADDITIONAL_ISO_VALUES";		
			DataSetMemoryLoader<AdditionalIsoValues> loader = 
		    new DataSetMemoryLoader<AdditionalIsoValues>
			(conn, AdditionalIsoValues.class, query);
			MemoryGlobal.ListAdditionalIsoValuesMem = loader.LoadDataClass();		
			
			log = new Logger();
			log.WriteLogMonitor("[MEM_CAHED]: Cargando CAMPOS ISO ADICIONALES...", TypeMonitor.monitor, null);
			
		} catch (SQLException e) {
			
			log = new Logger();
			log.WriteLogMonitor("Error modulo AdditionalIsoValues::run() ", TypeMonitor.error, e);
			
		}catch (Exception e) {
			log = new Logger();
			log.WriteLogMonitor("Error modulo AdditionalIsoValues::run() ", TypeMonitor.error, e);
			
		}finally {
			
			try {
				if(conn != null)
					conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	public List<AdditionalIsoValues> getAdditionalIsoValuesListObject(String Proccode, int NetId, String CanalCod, double AmmountDebit){
		List<AdditionalIsoValues> lista = null;
		Logger log = null;
		try {
			
			lista =   MemoryGlobal.ListAdditionalIsoValuesMem.stream().
					  filter(p -> p.getProccode().equals(Proccode) && 
							    p.getCanal_cod().equals(CanalCod) && 
							    p.getAmmountdebit() == AmmountDebit && 
							    p.getNet_id() == NetId)
					.peek(Objects::requireNonNull)
				    .collect(Collectors.toList());
			
		} catch (Exception e) {
			log = new Logger();
			log.WriteLogMonitor("Error modulo AdditionalIsoValues::getAdditionalIsoValuesListObject ", TypeMonitor.error, e);
		}
		return lista;
	}
	
}
