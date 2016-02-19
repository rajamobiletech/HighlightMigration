package com.cisco.reader.highlightmigration;

import com.cisco.reader.controllers.MigrationController;
import java.io.*;
import java.net.*;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )  throws IOException
    {
    	 MigrationController mController = new MigrationController();
    	 
    	 
       if(mController.migrateAnnotations("472/en/1.2.0", "472/en/1.2.1", "user1")){
       	System.out.println("done");
//       	URL url = new URL("http://10.108.35.195:8888/api/users/user1/indexAnnotations");
//        String query = new String("{'bookPath':'402/zh-CN/2.0.0}'}");
//
//        //make connection
//        URLConnection urlc = url.openConnection();
//
//        //use post mode
//        urlc.setDoOutput(true);
//        urlc.setAllowUserInteraction(false);
//
//        //send query
//        PrintStream ps = new PrintStream(urlc.getOutputStream());
//        ps.print(query);
//        ps.close();
//
//        //get result
//        BufferedReader br = new BufferedReader(new InputStreamReader(urlc
//            .getInputStream()));
//        String l = null;
//        while ((l=br.readLine())!=null) {
//            System.out.println(l);
//        }
//        br.close();
       } else {
       	System.out.println("failed");
       }
       
    }
}
