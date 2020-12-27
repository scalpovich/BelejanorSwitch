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

public class MessageControl {

	private String proccode;
	private String canal_cod;
	private String net_id;
	private double ammountdebit;
	private String message_class;
	private String type_control;
	private int active_control;
	private String detail;
	
	public MessageControl(){
		super();
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



	public String getNet_id() {
		return net_id;
	}



	public void setNet_id(String net_id) {
		this.net_id = net_id;
	}



	public double getAmmountdebit() {
		return ammountdebit;
	}



	public void setAmmountdebit(double ammountdebit) {
		this.ammountdebit = ammountdebit;
	}



	public String getMessage_class() {
		return message_class;
	}



	public void setMessage_class(String message_class) {
		this.message_class = message_class;
	}



	public String getType_control() {
		return type_control;
	}



	public void setType_control(String type_control) {
		this.type_control = type_control;
	}



	public int getActive_control() {
		return active_control;
	}



	public void setActive_control(int active_control) {
		this.active_control = active_control;
	}



	public String getDetail() {
		return detail;
	}



	public void setDetail(String detail) {
		this.detail = detail;
	}



	public List<MessageControl> getMsgControlListObject(){
		List<MessageControl> lista = null;
		try {
			
			lista =   MemoryGlobal.ListMsgControlMem.stream().
					  peek(Objects::requireNonNull)
					  .collect(Collectors.toList());
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return lista;
	}
	public Runnable getDataMsgCtrl(){
		Runnable runnable = new Runnable() {
			@Override
			public void run() {

				Logger log;
				Connection conn = null;
				
				try {
					
					conn = DBCPDataSource.getConnection();
					String query = "SELECT * from MESSAGE_CONTROL";		
					DataSetMemoryLoader<MessageControl> loader = 
				    new DataSetMemoryLoader<MessageControl>
					(conn, MessageControl.class, query);
					MemoryGlobal.ListMsgControlMem = loader.LoadDataClass();
					
					log = new Logger();
					log.WriteLogMonitor("[MEM_CAHED]: Cargando MENSAJES DE CONTROL...", TypeMonitor.monitor, null);
					
				} catch (SQLException e) {
					
					log = new Logger();
					log.WriteLogMonitor("Error modulo MessageControl::getDataMsgCtrl() ", TypeMonitor.error, e);
					
				} catch (Exception e) {
					
					log = new Logger();
					log.WriteLogMonitor("Error modulo MessageControl::getDataMsgCtrl() ", TypeMonitor.error, e);
					
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
