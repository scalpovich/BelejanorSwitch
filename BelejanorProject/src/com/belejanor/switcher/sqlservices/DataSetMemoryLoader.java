package com.belejanor.switcher.sqlservices;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;

import com.belejanor.switcher.logger.Logger;
import com.belejanor.switcher.logger.LoggerConfig.TypeMonitor;

public class DataSetMemoryLoader<T> {

	private Connection conn;
	private Class<?> clase;
	private String QueryString;
	private Logger log;
	
	public DataSetMemoryLoader(Connection c, Class<?> classLoader, String Query){
		this.conn = c;
		this.clase = classLoader;
		this.QueryString = Query;
		this.log = new Logger();
	}
	@SuppressWarnings("unchecked")
	public List<T> LoadDataClass(){
		
		    List<T> lista = null;						
			QueryRunner run = new QueryRunner();   
			T bean = null;
			try {	
				bean = (T) this.clase.newInstance();
			} catch (InstantiationException | IllegalAccessException e1) {				
				e1.printStackTrace();
			}		
			ResultSetHandler<List<T>> rsh = new BeanListHandler<T>((Class<T>) bean.getClass());		
			try {
				lista = run.query(this.conn,this.QueryString, rsh);
			} catch (SQLException ex) {				
				log.WriteLogMonitor("Error modulo DataSetMemoryLoader::LoadDataClass ", TypeMonitor.error, ex);
			}	
			return lista;
	}
}
