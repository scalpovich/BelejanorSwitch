package com.belejanor.switcher.cscoreswitch;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.apache.cxf.binding.soap.interceptor.SoapHeaderInterceptor;
//import org.apache.cxf.common.util.Base64Exception;
//import org.apache.cxf.common.util.Base64Utility;
import org.apache.cxf.configuration.security.AuthorizationPolicy;
import org.apache.cxf.endpoint.Endpoint;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.message.Exchange;
import org.apache.cxf.message.Message;
import org.apache.cxf.transport.Conduit;
import org.apache.cxf.ws.addressing.EndpointReferenceType;
import org.springframework.beans.factory.annotation.Required;

public class BasicAuthAuthorizationInterceptorBCE extends SoapHeaderInterceptor {

	private Map<String, String> users;

    @Required
    public void setUsers(Map<String, String> users) {
                    System.out.println("Inside setUsers Map");
                    this.users = users;
    }
    
    @SuppressWarnings("unchecked")
	@Override public void handleMessage(Message message) throws Fault {
        
        Map<String, List<String>> requestHeaders;
        requestHeaders = (Map<String, List<String>>) message.get(Message.PROTOCOL_HEADERS);

        List<String> authorizationLines = requestHeaders.get("authorization");
        List<String> aAuthorizationLines = requestHeaders.get("Authorization");

        if (authorizationLines != null)
        {
        	System.out.println("Yay 1");
        }
       
        if (aAuthorizationLines != null)
        {
        	System.out.println("Yay 2");
        }

		// This is supposed to be set by CXF, but i had to set it explicitly, so don't
		// need this anymore
        AuthorizationPolicy policy = message.get(AuthorizationPolicy.class);

		// If the policy is not set, the user did not specify credentials
		// A 401 is sent to the client to indicate that authentication is required
		if (policy == null) {
			System.out.println ("User attempted to log in with no credentials");
			sendErrorResponse(message, HttpURLConnection.HTTP_UNAUTHORIZED);
			return;
		}

		System.out.println ("Logging in use: " + policy.getUserName());

		// Verify the password
		String realPassword = users.get(policy.getUserName());
		if (realPassword == null || !realPassword.equals(policy.getPassword())) {
			System.out.println ("Invalid username or password for user: " + policy.getUserName());
			sendErrorResponse(message, HttpURLConnection.HTTP_FORBIDDEN);
	    }
    }
    
    private void sendErrorResponse(Message message, int responseCode) {
        Message outMessage = getOutMessage(message);
        outMessage.put(Message.RESPONSE_CODE, responseCode);
       
        // Set the response headers
        @SuppressWarnings("unchecked")
		Map<String, List<String>> responseHeaders =
            (Map<String, List<String>>)message.get(Message.PROTOCOL_HEADERS);
        if (responseHeaders != null) {
            responseHeaders.put("WWW-Authenticate", Arrays.asList(new String[]{"Basic realm=realm"}));
            responseHeaders.put("Content-Length", Arrays.asList(new String[]{"0"}));
        }
        message.getInterceptorChain().abort();
        try {
            getConduit(message).prepare(outMessage);
            close(outMessage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private Message getOutMessage(Message inMessage) {
        Exchange exchange = inMessage.getExchange();
        Message outMessage = exchange.getOutMessage();
        if (outMessage == null) {
            Endpoint endpoint = exchange.get(Endpoint.class);
            outMessage = endpoint.getBinding().createMessage();
            exchange.setOutMessage(outMessage);
        }
        outMessage.putAll(inMessage);
        return outMessage;
    }
    
    private Conduit getConduit(Message inMessage) throws IOException {
        
        System.out.println("inmessage is: " + inMessage);
		Exchange exchange = inMessage.getExchange();
		if (exchange == null )
		System.out.println("Exchnage is null");
		
		EndpointReferenceType target = exchange.get(EndpointReferenceType.class);
		if (target == null )
		System.out.println("target is null");
		
		Conduit conduit =
		exchange.getDestination().getBackChannel(inMessage);
		exchange.setConduit(conduit);
		
		return conduit;
    }
    
    private void close(Message outMessage) throws IOException {
        OutputStream os = outMessage.getContent(OutputStream.class);
        os.flush();
        os.close();
    }
	
}
