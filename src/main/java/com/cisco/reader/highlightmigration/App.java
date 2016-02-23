package com.cisco.reader.highlightmigration;

import java.io.IOException;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.ClassPathResource;

import com.cisco.reader.controllers.MigrationController;

/**
 * Hello world!
 *
 */
@ComponentScan
public class App 
{
	public static void main( String[] args )  throws IOException
    {
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(new ClassPathResource("spring-config.xml").getPath());
		MigrationController mController = context.getBean(MigrationController.class);    	 	 
       if(mController.migrateAnnotations("472/en/1.2.0", "472/en/1.2.1", "user1")){
       	System.out.println("done");
       } else {
       	System.out.println("failed");
       }
       
    }
}
