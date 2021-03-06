package com.belejanor.switcher.sqlservices;

import java.sql.Connection;
import java.sql.SQLException;
import org.apache.commons.dbcp2.BasicDataSource;

import com.belejanor.switcher.logger.Logger;
import com.belejanor.switcher.logger.LoggerConfig.TypeMonitor;
import com.belejanor.switcher.memcached.MemoryGlobal;

public class DBCPDataSource {

	
	public BasicDataSource getDataSource(BasicDataSource dataSource) throws SQLException
	 {
	 
	        if (dataSource == null)
	        {
	            @SuppressWarnings("resource")
				BasicDataSource ds = new BasicDataSource();
	            ds.setUrl(MemoryGlobal.bddConnectionString);
	            ds.setDriverClassName("com.sybase.jdbc4.jdbc.SybDriver");
	            ds.setUsername(MemoryGlobal.bddUserName);
	            ds.setPassword(MemoryGlobal.bddPassword);
	            ds.setMinIdle(1);
	            ds.setMaxIdle(1);
	            ds.setMaxOpenPreparedStatements(1);
	            Logger log = new Logger();
	            log.WriteLogMonitor("*********************PRUEBA*******************", TypeMonitor.monitor, null);
	      
	 
	            dataSource = ds;
	        }
	        return dataSource;
	    }
     
    public static Connection getConnection() throws SQLException {
        return MemoryGlobal.dataSourceBDD.getConnection();
    }
     
    public DBCPDataSource(){ }
}
