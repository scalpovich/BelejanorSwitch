package com.belejanor.switcher.utils;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.concurrent.Callable;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import com.belejanor.switcher.logger.Logger;
import com.belejanor.switcher.logger.LoggerConfig.TypeMonitor;

public class SerializationExecutorString implements Callable<String>{
	
	private  Class<?> clazz;
	private  Object obj;

	public SerializationExecutorString(Class<?> clazz, final Object obj) {
		super();
		this.clazz = clazz;
		this.obj = obj;
	}

	public Class<?> getClazz() {
		return clazz;
	}

	public void setClazz(Class<?> clazz) {
		this.clazz = clazz;
	}

    public  String ObjectToString( final Object obj, final Class<?> zClass){
		
		Logger log = null;
		String xml = null;
		try {	
			
			JAXBContext context = JAXBContext.newInstance(new Class[] {
					zClass});
			Marshaller marshaller = context.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);	
			marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
			
			OutputStream output = new OutputStream() {
				private StringBuilder string = new StringBuilder();
				@Override
				public void write(int b) throws IOException {
					this.string.append((char) b );				
				}
				@Override
				public String toString(){
		            return this.string.toString();
		        }
			};	
			OutputStreamWriter oo = new OutputStreamWriter(output,"UTF-8");
			marshaller.marshal(obj, oo);
			xml = output.toString();
		}catch (JAXBException e){
			
			log = new Logger();
			log.WriteLogMonitor("Error modulo SerializationExecutor::ObjectToString ", TypeMonitor.error, e);
			
		} catch (Exception e) {
			
			log = new Logger();
			log.WriteLogMonitor("Error modulo SerializationExecutor::ObjectToString (General) ", TypeMonitor.error, e);
		}
		return StringUtils.stripEspecial(xml);
	}

	@Override
	public String call() throws Exception {
		
		return ObjectToString(obj, clazz);
	}
	
}
