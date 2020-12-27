package com.belejanor.switcher.cscoreswitch;

import java.io.IOException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.belejanor.switcher.logger.Logger;
import com.belejanor.switcher.logger.LoggerConfig.TypeMonitor;
import com.belejanor.switcher.memcached.MemoryGlobal;
import com.belejanor.switcher.tcpadmin.SSLServer;
import com.belejanor.switcher.tcpadmin.TCPServer;
import com.belejanor.switcher.tcpadmin.TCPServerApache;
import com.belejanor.switcher.tcpserver.TCPServerRunner;
import com.belejanor.switcher.tcpserver.TCPServerRunnerBinary;
import com.belejanor.switcher.tcpserver.TCPServerRunnerText;

/**
 * Servlet implementation class TCPServlet
 */
@WebServlet("/TCPServlet")
public class TCPServlet extends HttpServlet implements ServletContextListener {
	
	private static final long serialVersionUID = 1L;
	private Logger log;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public TCPServlet() {
        super();
        log = new Logger();
    }

    public void init(ServletConfig config) throws ServletException {
    	
    	switch (MemoryGlobal.typeTCPServerService) {
		case 1:
			
			TCPServer tcp = new TCPServer();
	    	tcp.setDaemon(true);
	    	tcp.start();
	    	log.WriteLogMonitor("Iniciando TCPServer [1]", TypeMonitor.monitor, null);
			break;
		case 2:
			
			TCPServerApache tcpa = new TCPServerApache();
	    	tcpa.setDaemon(true);
	    	tcpa.start();
	    	log.WriteLogMonitor("Iniciando TCPServer [2]", TypeMonitor.monitor, null);
	    	break;
	    	
		case 3:
			
			SSLServer tcpSSL = new SSLServer();
			tcpSSL.setDaemon(true);
			tcpSSL.start();
			log.WriteLogMonitor("Iniciando TCPServer [3] SSL", TypeMonitor.monitor, null);
	    	break;
	    	
		case 4:
			
			TCPServerRunner tcpRun = new TCPServerRunner();
			tcpRun.setDaemon(true);
			tcpRun.start();
			log.WriteLogMonitor("Iniciando TCPServer [4]", TypeMonitor.monitor, null);
			break;

		case 5:
			
			TCPServerRunner tcpRunn = new TCPServerRunner();
			tcpRunn.setDaemon(true);
			TCPServerRunnerText tcpRunnTxt = new TCPServerRunnerText();
			tcpRunnTxt.setDaemon(true);
			tcpRunn.start();
			tcpRunnTxt.start();
			log.WriteLogMonitor("Iniciando TCPServer [5]", TypeMonitor.monitor, null);
			break;
			
		case 6:
			
			TCPServerRunnerBinary tcpRunnBin = new TCPServerRunnerBinary();
			tcpRunnBin.setDaemon(true);
			tcpRunnBin.start();
			
			log.WriteLogMonitor("Iniciando TCPServer Binary [6]", TypeMonitor.monitor, null);
			break;
			
		default:
			log.WriteLogMonitor("No se ha iniciado componentes TCPServer", TypeMonitor.monitor, null);
			break;
		}
    	
    }
    
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		//TCPServer tcp = new TCPServer();
				//tcp.CloseServer();
				TCPServerApache tcp = new TCPServerApache();
				tcp.CloseServer();
	}

	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	
}
