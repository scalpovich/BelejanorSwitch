package com.belejanor.switcher.cscoreswitch;

import java.sql.SQLException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;

import com.belejanor.switcher.logger.Logger;
import com.belejanor.switcher.logger.LoggerConfig;
import com.belejanor.switcher.logger.LoggerConfig.TypeMonitor;
import com.belejanor.switcher.memcached.MemoryGlobal;
import com.belejanor.switcher.queueadmin.ReceiveAsyncMessage;
import com.belejanor.switcher.scheduler.InitSchedule;
import com.belejanor.switcher.snp.spi.ProcessorLotes;

@WebServlet("/GlobalServletInit")
public class GlobalServletInit extends HttpServlet implements ServletContextListener {
	private static final long serialVersionUID = 1L;
    private Logger log;
    private Thread tt;
    private Thread ts;
    private Thread bd;
    //private Thread tp;
	
    public GlobalServletInit() {
        super();
        log = new Logger();
    }	
	public void init(ServletConfig config) throws ServletException {
	
		ProcessorLotes scheduleLotesSNP = new ProcessorLotes();
		InitSchedule schedule = null;
		MemoryGlobal.flagSystemReady = false;
		MemoryGlobal.currentPath = this.getClass().getClassLoader().getResource("").getPath().replace("%20", " ");
		System.out.println("***************** CURRENT PATH *************   " + MemoryGlobal.currentPath);
		String splitter = "\\/";
        String [] pather = MemoryGlobal.currentPath.split(splitter);
        MemoryGlobal.currentPath = MemoryGlobal.currentPath.replace(pather[pather.length - 1] + "/", "");
	
		System.out.println("Servlet Init Java......");
		LoggerConfig logConfig = new LoggerConfig();
		logConfig.InitLoggerService(config);	
		
		log.WriteLogMonitor("Cargando variables Globales......", TypeMonitor.monitor, null);
		System.out.println("Cargando variables Globales......");
		if(MemoryGlobal.LoadParamsConfig()){
			if(MemoryGlobal.flagUseQueueInit){
				
				tt = new Thread(new ReceiveAsyncMessage(MemoryGlobal.queueNameIni, 
						    MemoryGlobal.sessionQueueIni));
				tt.setDaemon(true);
				tt.start();
			}
			/*Cola de Store And Forward*/
			ts = new Thread(new ReceiveAsyncMessage(MemoryGlobal.queueNameSf, 
		    MemoryGlobal.sessionQueueSf));
			ts.setDaemon(true);
            ts.start();
            
            /*Cola de Procesamiento*/
            /*tp = new Thread(new ReceiveAsyncMessage(MemoryGlobal.queueNameProcessor, 
		    MemoryGlobal.sessionQueueProcessor));
			tp.setDaemon(true);
            tp.start();*/
            
            
			System.out.println("Variables cargadas exitosamente...");
			log.WriteLogMonitor("Variables cargadas exitosamente...", TypeMonitor.monitor, null);
			System.out.println("Creando Pool conexion a la BDD");
			log.WriteLogMonitor("Creando Pool conexion a la BDD...", TypeMonitor.monitor, null);
			if(MemoryGlobal.OpenConnBDD()){
				System.out.println("Conexion Sybase(ASE) exitosa!!!");
				log.WriteLogMonitor("Conexion Sybase(ASE) exitosa!!!", TypeMonitor.monitor, null);
				System.out.println("Cargando Memoria Cache......");
				log.WriteLogMonitor("Cargando Memoria Cache......", TypeMonitor.monitor, null);
				if(MemoryGlobal.LoadMemory()){
					log.WriteLogMonitor("Carga de Memoria Cache exitosa!!!", TypeMonitor.monitor, null);
					log.WriteLogMonitor("En espera de Transacciones...\n", TypeMonitor.monitor, null);
					System.out.println("Carga de Memoria Cache exitosa......");				
					MemoryGlobal.flagSystemReady = true;
					log.WriteLogMonitor("Current Path ===> " + MemoryGlobal.currentPath, TypeMonitor.monitor, null);
					/** Pendiente proceso en paralelo para inicializacion**/
					if(MemoryGlobal.sendMsgControlFlag)
						MemoryGlobal.InitMessageControl();
					if(MemoryGlobal.SnpExecuteScheduleLotesFlag) {
						
						scheduleLotesSNP.setDaemon(true);
						scheduleLotesSNP.start();
					}
					if(MemoryGlobal.flagBatch) {
			            /*BatchDay*/
						schedule = new InitSchedule();
			            bd = new Thread(schedule.runChargeTask());
			            //bd.setDaemon(true);
			            bd.start();
					}
				}
				else{ 
					System.out.println("[ERROR:] Problemas al cargar memoria cache del Sistema!!!");
					log.WriteLogMonitor("[ERROR:] Problemas al cargar memoria cache del Sistema!!!", TypeMonitor.monitor, null);
					log.WriteLogMonitor("[ERROR:] Problemas al cargar memoria cache del Sistema!!!", TypeMonitor.error, null);
					MemoryGlobal.flagSystemReady = false;
				}
			}
			else{
				System.out.println("[ERROR:] Problemas al conectar a la BDD Oracle!!!");
				log.WriteLogMonitor("[ERROR:] Problemas al conectar a la BDD Oracle!!!", TypeMonitor.monitor, null);
				log.WriteLogMonitor("[ERROR:] Problemas al conectar a la BDD Oracle!!!", TypeMonitor.error, null);
				MemoryGlobal.flagSystemReady = false;
				return;
			}	
		}
		else{
			System.out.println("[ERROR:] Problemas al conectar a la BDD Oracle!!!");
			MemoryGlobal.flagSystemReady = false;
			return;
		}
			
	}
	@Override
	public void contextDestroyed(ServletContextEvent arg0) {		
		System.out.println("Termina Middleware!!!!!!!!!!!!!!!");
		log.WriteLogMonitor("Termina Middleware!!!!!!!!!!!!!!!", TypeMonitor.error, null);
		log.WriteLogMonitor("Termina Middleware!!!!!!!!!!!!!!!", TypeMonitor.monitor, null);
		try {
			MemoryGlobal.conn.close();
			if(!MemoryGlobal.serviceSchedule.isShutdown())
				MemoryGlobal.serviceSchedule.shutdown();
			
			if(MemoryGlobal.flagUseQueueInit){
				if(MemoryGlobal.sessionQueueIni!=null)
					MemoryGlobal.sessionQueueIni.close();
				if(MemoryGlobal.sessionQueueSf!=null)
					MemoryGlobal.sessionQueueSf.close();
			}
			
			if(tt != null)
				tt.interrupt();
			if(ts != null)
				ts.interrupt();
			
		} catch (SQLException e) {			
			log.WriteLogMonitor("Error modulo [contextDestroyed] Servlet ", TypeMonitor.error, e);
			log.WriteLogMonitor("Error modulo [contextDestroyed] Servlet", TypeMonitor.monitor, null);
		}catch (Exception e) {
			log.WriteLogMonitor("Error modulo [contextDestroyed] Servlet ", TypeMonitor.error, e);
			e.printStackTrace();
		}
		
	}
	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		
		System.out.println("Inicia Sistema...... Middleware FitBank 2.0");
	}
	
	
}
