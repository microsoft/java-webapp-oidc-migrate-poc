package com.microsoft.aad.oidcpoc;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class PropertyReading implements ServletContextListener {

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		ServletContext ctx = sce.getServletContext();
		String configPropPath = ctx.getRealPath("/WEB-INF/config.properties");
		String localPropPath = ctx.getRealPath("/WEB-INF/local.properties");
		
        final Properties configProps = new Properties();
        final Properties localProps = new Properties();
        try {    
        	configProps.load(new FileInputStream(configPropPath));
        	localProps.load(new FileInputStream(localPropPath));
        } catch (final IOException e) {
            e.printStackTrace();
        }
        
        Set<String> names = configProps.stringPropertyNames();
        //Enumeration<String> name = ctx.getInitParameterNames();
        
        String res=null;
        for (String prop : names)
        {
        	//check environment var (Azure)
        	res = System.getenv(prop);
        	if (res == null)
        	{
        		//check local.properties file (local dev)
        		res = localProps.getProperty(prop);
        		if (res == null) 
        		{
        			//check web.xml (in case someone edits those defaults)
        			res = ctx.getInitParameter(prop); 
        		}
        	}
        	if (res!=null) {
            	//put value in system property bag
        		System.setProperty(prop, res);
        	}
        }
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		// TODO Auto-generated method stub
	}
}
