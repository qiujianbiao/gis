package com.ericsson.fms.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PropUtil {

	private final static Logger log = LoggerFactory.getLogger(PropUtil.class);
	
	public static Properties properties;
	
	public static void init(){
		properties = getProperties("application.properties");
	}
	
	public static String getProperty(String key,String value){
		if(properties==null){
			init();
		}
		return properties.getProperty(key,value);
	}
	
	public static String getProperty(String key){
		if(properties==null){
			init();
		}
		return properties.getProperty(key);
	}
	
	public static Properties getProperties(String fileName) {
		log.info("getProperties fileName="+fileName);
		InputStream is = PropUtil.class.getClassLoader().getResourceAsStream(fileName);
		Properties p = new Properties();
		try {
			p.load(is);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return p;
	}

	public static void saveProp(Properties prop, String fileName) throws Exception {
		URL confURL = PropUtil.class.getClassLoader().getResource(fileName);
		File file = new File(confURL.getPath());
		try {
			FileOutputStream outputFile = new FileOutputStream(file);
			prop.store(outputFile, "");
			outputFile.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}
	
}
