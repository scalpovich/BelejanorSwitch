package com.belejanor.switcher.utils;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventHandler;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.apache.commons.io.FileUtils;
import org.xml.sax.SAXException;

public class ValidationSchemas {

	
	public String validateSchemas(String xml, String xsd) {
		
		try {
			return validate(xml, readFileAsString(xsd));
		} catch (SAXException | IOException e) {
			
			e.printStackTrace();
			return null;
		}
	}

	private static String readFileAsString(String filePath) throws java.io.IOException {
        StringBuffer fileData = new StringBuffer(1000);
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        char[] buf = new char[1024];
        int numRead = 0;
        while ((numRead = reader.read(buf)) != -1) {
            String readData = String.valueOf(buf, 0, numRead);
            fileData.append(readData);
            buf = new char[1024];
        }
        reader.close();
        return fileData.toString();
    }
	
	private static String validate(String xml, String xsd) throws SAXException, IOException {
		 
		 
        boolean isValid = false;
        String errorMessage = null;
 
        // 1. Lookup a factory for the W3C XML Schema language
        SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        /**
         * 2. Compile the schema. Here the schema is loaded from a
         * javax.xml.transform.Source, but you could use a java.net.URL or a
         * java.io.File instead.
         */
        Schema schema = factory.newSchema(new StreamSource(new StringReader(xsd)));
 
        // 3. Get a validator from the schema.
        Validator validator = schema.newValidator();
 
        // 4. Parse the document you want to check.
        Source source = new StreamSource(new StringReader(xml));
 
        // 5. Check the document
        try {
            validator.validate(source);
            isValid = true;
        } catch (SAXException e) {
            errorMessage = e.toString();
        }
        if (isValid == true) {
            errorMessage = "Document is valid!";
        }
        return (errorMessage == null) ? "" : errorMessage;
    }
	
	
	public void validateSchema(final String xml, final String Xchema, final Class<?> rootClass) {
		
		MyValidationEventCollector vec = new MyValidationEventCollector();

	    validateXmlAgainstSchema(vec, xml, Xchema, rootClass);
	}
	
	private void validateXmlAgainstSchema(final MyValidationEventCollector vec, final String xmlFileName
			   , final String xsdSchemaName, final Class<?> rootClass) {
	    try (
	    	
	    	ByteArrayInputStream bb = new ByteArrayInputStream(xmlFileName.getBytes(StandardCharsets.UTF_8));
	    	InputStream xmlFileIs = bb;
	    	) 
	    {
	        final JAXBContext jContext = JAXBContext.newInstance(rootClass);
	        // Unmarshal the data from InputStream
	        final Unmarshaller unmarshaller = jContext.createUnmarshaller();

	        final SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
	        
	        File initialFile = new File(xsdSchemaName);
	        
	        final InputStream schemaAsStream = FileUtils.openInputStream(initialFile);
	        unmarshaller.setSchema(sf.newSchema(new StreamSource(schemaAsStream)));

	        unmarshaller.setEventHandler(vec);

	        unmarshaller.unmarshal(new StreamSource(xmlFileIs), rootClass).getValue();

	        for (String validationError : vec.getValidationErrors()) {
	        	if(validationError.contains("ns2:documentoSolicitud")) {
	        		
	        	}
	        	System.out.println(validationError);
	        }
	    } catch (final Exception e) {
	    	System.out.println("The validation of the XML file " + xmlFileName + " failed: " + e.getMessage());
	    }
	}

}
class MyValidationEventCollector implements ValidationEventHandler {
    private final List<String> validationErrors;

    public MyValidationEventCollector() {
        validationErrors = new ArrayList<>();
    }

    public List<String> getValidationErrors() {
        return Collections.unmodifiableList(validationErrors);
    }

    @Override
    public boolean handleEvent(final ValidationEvent event) {
        String pattern = "line {0}, column {1}, error message {2}";
        String errorMessage = MessageFormat.format(pattern, event.getLocator().getLineNumber(), event.getLocator().getColumnNumber(),
                event.getMessage());
        if (event.getSeverity() == ValidationEvent.FATAL_ERROR) {
            validationErrors.add(errorMessage);
        }
        return true; // you collect the validation errors in a List and handle them later
    }
}
