package com.belejanor.switcher.memcached;

import java.sql.Connection;
import java.sql.SQLException;

import com.belejanor.switcher.logger.Logger;
import com.belejanor.switcher.logger.LoggerConfig.TypeMonitor;
import com.belejanor.switcher.sqlservices.DBCPDataSource;
import com.belejanor.switcher.sqlservices.DataSetMemoryLoader;

public class ChannelsFit1 {

	private String cCanal;
	private String ctransaccionAtm;
	private String descripcion;
	private String cSubsistema_FIT;
	private String cTransaccion_FIT;
	private String versionTransaccion;
	private String sComponente;
	private String des2;
	
	
	public ChannelsFit1() {
		super();
	}

	
	
	public String getcCanal() {
		return cCanal;
	}

	public void setcCanal(String cCanal) {
		this.cCanal = cCanal;
	}

	public String getCtransaccionAtm() {
		return ctransaccionAtm;
	}

	public void setCtransaccionAtm(String ctransaccionAtm) {
		this.ctransaccionAtm = ctransaccionAtm;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public String getcSubsistema_FIT() {
		return cSubsistema_FIT;
	}

	public void setcSubsistema_FIT(String cSubsistema_FIT) {
		this.cSubsistema_FIT = cSubsistema_FIT;
	}

	public String getcTransaccion_FIT() {
		return cTransaccion_FIT;
	}

	public void setcTransaccion_FIT(String cTransaccion_FIT) {
		this.cTransaccion_FIT = cTransaccion_FIT;
	}

	public String getVersionTransaccion() {
		return versionTransaccion;
	}

	public void setVersionTransaccion(String versionTransaccion) {
		this.versionTransaccion = versionTransaccion;
	}

	public String getsComponente() {
		return sComponente;
	}

	public void setsComponente(String sComponente) {
		this.sComponente = sComponente;
	}

	public String getDes2() {
		return des2;
	}

	public void setDes2(String des2) {
		this.des2 = des2;
	}


	public ChannelsFit1 getChannelsFit1_Object(String cCanal, String cTransaccionAtm){
		ChannelsFit1 lista = null;
		Logger log = null;
		try {
			
			lista =   MemoryGlobal.ListChannelsFit1Mem.stream().
					filter(p -> p.getcCanal().equals(cCanal)
							 && p.getCtransaccionAtm().equals(cTransaccionAtm)
						  )
					.findFirst().orElseGet(() -> null);
			
		} catch (Exception e) {
			
			log = new Logger();
			log.WriteLogMonitor("Error modulo ChannelsFit1::getChannelsFit1_Object ", TypeMonitor.error, e);
		}
		return lista;
	}
	
	public Runnable getDataChannelsFit1(){
		
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				
				Logger log;
				Connection conn = null;
				
				String query = "select a.ccanal,a.ctransaccionatm,b.descripcion,a.csubsistema_fit, a.ctransaccion_fit,  " +
						       "c.VERSIONTRANSACCION, a.scomponente, upper(c.descripcion) as des2 " +
						       "from tatmcargostransaccionemisor a, tatmtipostransaccion b, tsubsistematransacciones c " +
							   "WHERE " +
                               "a.ctransaccionatm = b.ctransaccionatm " +
                               "and a.csubsistema_fit = c.csubsistema " +
                               "and a.ctransaccion_fit = c.ctransaccion " +
                               "and a.versiontransaccion_fit = c.versiontransaccion " +
                               "order by 1,2 ";		
				try {
					conn = DBCPDataSource.getConnection();
					DataSetMemoryLoader<ChannelsFit1> loader = 
				    new DataSetMemoryLoader<ChannelsFit1>
					(conn, ChannelsFit1.class, query);
					MemoryGlobal.ListChannelsFit1Mem = loader.LoadDataClass();	
					
					log = new Logger();
					log.WriteLogMonitor("[MEM_CAHED]: Cargando CANALES DEL SISEMA...", TypeMonitor.monitor, null);
					
				} catch (SQLException e) {
					
					log = new Logger();
					log.WriteLogMonitor("Error modulo ChannelsFit1::getDataChannelsFit1() ", TypeMonitor.error, e);
					
				}catch (Exception e) {
					
					log = new Logger();
					log.WriteLogMonitor("Error modulo ChannelsFit1::getDataChannelsFit1() ", TypeMonitor.error, e);
					
				}finally {
					
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
