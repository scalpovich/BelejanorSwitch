package com.belejanor.switcher.cscoreswitch;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.lang.reflect.FieldUtils;
import com.belejanor.switcher.cscoreswitch.Iso8583;

@SuppressWarnings("unused")
public class Tester {

	public static void main(String[] args) {
		
		
		try {
            
			Class<?> _class = Iso8583.class;//Class.forName("B");
            Field properties[] = _class.getDeclaredFields();
            for (int i = 0; i < properties.length; i++) {
                Field field = properties[i];
                System.out.println(field.getName() +" > "+field.getType());
            }
			/*String param = "com.fitbank.middleware.cscoreswitch.wIso8583.ISO_002_PAN";
			//String param = "com.fitbank.middleware.utils.GeneralUtils.GetSecuencial";
			
			String messageClass = param;				
			List<String> aa = Arrays.asList(messageClass.split("\\."));
			String methodName = aa.get(aa.size() -1);
			String classname = messageClass.replace("." + methodName, "");
			
			Class<?> class_ = Class.forName(classname);
			System.out.println(class_.getName());
			if(class_.isInstance(wIso8583.class)) 
				System.out.println("SI");
			else
				System.out.println("NO");*/
			
			
			
        } catch (Exception ex) {
        	
            ex.printStackTrace();
        }

	}
	
	public static Class<?> getPropertyType(Class<?> clazz,String property){
	 try{
		 //LinkedList<String> properties=new LinkedList<String>();
		 //properties.addAll(Arrays.asList(property.split("\\.")));
		 Field field = null;
		 while(!property.isEmpty()){
		 field = FieldUtils.getField(clazz,property,true);
		 clazz=field.getType();
	 }
		 return field.getType();
		 
	 }catch(Exception e){
		 
		 throw new RuntimeException(e);
	 }
    }
}
