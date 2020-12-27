package com.belejanor.switcher.memcached;

import java.sql.Connection;
import java.sql.SQLException;

import com.belejanor.switcher.logger.Logger;
import com.belejanor.switcher.logger.LoggerConfig.TypeMonitor;
import com.belejanor.switcher.sqlservices.DBCPDataSource;
import com.belejanor.switcher.sqlservices.DataSetMemoryLoader;

public class DispositivosFit1 {

	private String cCanal;
	private String cDispositivo;
	private String cTerminal;
	private String enProceso;
	private String cUsuario;
	
	public DispositivosFit1() {
		super();
	}

	
	public String getcCanal() {
		return cCanal;
	}

	public void setcCanal(String cCanal) {
		this.cCanal = cCanal;
	}

	public String getcDispositivo() {
		return cDispositivo;
	}

	public void setcDispositivo(String cDispositivo) {
		this.cDispositivo = cDispositivo;
	}

	public String getcTerminal() {
		return cTerminal;
	}

	public void setcTerminal(String cTerminal) {
		this.cTerminal = cTerminal;
	}

	public String getEnProceso() {
		return enProceso;
	}

	public void setEnProceso(String enProceso) {
		this.enProceso = enProceso;
	}

	public String getcUsuario() {
		return cUsuario;
	}

	public void setcUsuario(String cUsuario) {
		this.cUsuario = cUsuario;
	}


	public DispositivosFit1 getDispositivosFit1_Object(String cCanal, String cDispositivo){
		DispositivosFit1 lista = null;
		Logger log = null;
		try {
			
			lista =   MemoryGlobal.ListDisppsitivoFit1Fit1Mem.stream().
					filter(p -> p.getcCanal().equals(cCanal)
							 && p.getcDispositivo().equals(cDispositivo)
						  )
					.findFirst().orElseGet(() -> null);
			
		} catch (Exception e) {
			
			log = new Logger();
			log.WriteLogMonitor("Error modulo DispositivosFit1::getDispositivosFit1_Object ", TypeMonitor.error, e);
		}
		return lista;
	}
	
	public Runnable getDataDispisitivosFit1(){
		Runnable runnable = new Runnable() {
			@Override
			public void run() {

				String query = "select CCANAL, CDISPOSITIVO, CTERMINAL, ENPROCESO, CUSUARIO " +
							   "from TDISPOSITIVO " +
							   "order by 1 ";	
				
				Connection conn = null;
				Logger log;
				
				try {
					
					conn = DBCPDataSource.getConnection();
					DataSetMemoryLoader<DispositivosFit1> loader = 
						    new DataSetMemoryLoader<DispositivosFit1>
							(conn, DispositivosFit1.class, query);
							MemoryGlobal.ListDisppsitivoFit1Fit1Mem = loader.LoadDataClass();
							
							log = new Logger();
							log.WriteLogMonitor("[MEM_CAHED]: Cargando DISPOSITIVOS Y TERMINALES...", TypeMonitor.monitor, null);
					
				} catch (SQLException e) {
					
					log = new Logger();
					log.WriteLogMonitor("Error modulo DispositivosFit1::getDataDispisitivosFit1() ", TypeMonitor.error, e);
					
				} catch (Exception e) {
					
					log = new Logger();
					log.WriteLogMonitor("Error modulo DispositivosFit1::getDataDispisitivosFit1() ", TypeMonitor.error, e);
					
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
