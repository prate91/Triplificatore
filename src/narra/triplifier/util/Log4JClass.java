package narra.triplifier.util;

import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import narra.triplifier.Triplify;

public class Log4JClass {

	private Logger log = null;
	private static Log4JClass l4jc = null;
	
	private Log4JClass(){
		Properties properties = new Properties();
		try {
			properties.load(Triplify.class.getClassLoader().getResourceAsStream("log4j.properties"));
		}
		catch (IOException e) {
			System.out.println("\nINSERT - Errore nel caricamento della configurazione");
		}

		PropertyConfigurator.configure(properties);

		//load configuration File
		//PropertyConfigurator.configure("log4j.properties");
		//get Logger Instance
		log = Logger.getLogger(Log4JClass.class);
		l4jc = this;
	}//end constructor
	
	private static Log4JClass getIstance(){
		if(l4jc==null) new Log4JClass();
		return l4jc;
	}
	
	public static Logger getLogger(){
		getIstance();
		return l4jc.log;
	}
	
}
