package com.belejanor.switcher.implementations;

import javax.annotation.Resource;
import javax.jws.WebService;
import javax.servlet.http.HttpServletRequest;
import javax.xml.ws.WebServiceContext;
import org.apache.cxf.phase.PhaseInterceptorChain;
import org.apache.cxf.transport.http.AbstractHTTPDestination;

import com.belejanor.switcher.bridges.BridgeElectronicCash;
import com.belejanor.switcher.electroniccash.DTORequestCredit;
import com.belejanor.switcher.electroniccash.DTORequestDebit;
import com.belejanor.switcher.electroniccash.DTORequestIsValidAccount;
import com.belejanor.switcher.electroniccash.DTORequestRevert;
import com.belejanor.switcher.electroniccash.ITransactions;
import com.belejanor.switcher.electroniccashres.DTOResponseCredit;
import com.belejanor.switcher.electroniccashres.DTOResponseDebit;
import com.belejanor.switcher.electroniccashres.DTOResponseIsValidAccount;
import com.belejanor.switcher.electroniccashres.DTOResponseRevert;
import com.belejanor.switcher.logger.Logger;
import com.belejanor.switcher.logger.LoggerConfig.TypeMonitor;

@WebService(targetNamespace = "http://implementations.middleware.fitbank.com", portName = "ServicioTranferenciaEfiPort", 
serviceName = "ServicioTranferenciaEfiService")
public class ServicioTranferenciaEfi implements ITransactions{

	@Override
	public DTOResponseCredit credit(DTORequestCredit DTORequestCredit) {
		Logger log = null;
		try {
			
			String IP = ObtainIpClient();
			BridgeElectronicCash bridge = new BridgeElectronicCash();
			return bridge.CreditBridge(DTORequestCredit, IP);
			
		} catch (Exception e) {
			log = new Logger();
			log.WriteLogMonitor("Error modulo ServicioTranferenciaEfi::credit [ImplementsWeb] ", TypeMonitor.error, e);
			return null;
		}
	}

	@Override
	public DTOResponseRevert revert(DTORequestRevert DTORequestRevert) {
		Logger log = null;
		try {
			System.out.println("Entro DTOResponseRevert revert....(1)");
			String IP = ObtainIpClient();
			BridgeElectronicCash bridge = new BridgeElectronicCash();
			return bridge.RevertBridge(DTORequestRevert, IP);
			
		} catch (Exception e) {
			log = new Logger();
			log.WriteLogMonitor("Error modulo ServicioTranferenciaEfi::revert [ImplementsWeb] ", TypeMonitor.error, e);
			return null;
		}
	}

	@Override
	public DTOResponseDebit debit(DTORequestDebit DTORequestDebit) {
		Logger log = null;
		try {
			String IP = ObtainIpClient();
			BridgeElectronicCash bridge = new BridgeElectronicCash();
			return bridge.DebitBridge(DTORequestDebit, IP);
			
		 } catch (Exception e) {
			
			 log = new Logger();
			 log.WriteLogMonitor("Error modulo ServicioTranferenciaEfi::debit [ImplementsWeb] ", TypeMonitor.error, e);
			return null;
		 }
	}

	@Override
	public DTOResponseIsValidAccount isValidAccount(DTORequestIsValidAccount DTORequestIsValidAccount) {
		Logger log = null;	
		try {
			String IP = ObtainIpClient();
			BridgeElectronicCash bridge = new BridgeElectronicCash();
			return bridge.ValidAccountBridge(DTORequestIsValidAccount, IP);
			
		 } catch (Exception e) {
			
			 log = new Logger();
			 log.WriteLogMonitor("Error modulo ServicioTranferenciaEfi::isvalidaccount [ImplementsWeb] ", TypeMonitor.error, e);
			return null;
		 }
	}
	
	@Resource  
	WebServiceContext wsContext; 
	private String ObtainIpClient() {
		try {
			
			org.apache.cxf.message.Message message = PhaseInterceptorChain.getCurrentMessage();
			HttpServletRequest request = (HttpServletRequest)message.get(AbstractHTTPDestination.HTTP_REQUEST);
			return request.getRemoteAddr();
			
		} catch (Exception e) {
			
			return "undefined-error";
		}
		
	}

}
