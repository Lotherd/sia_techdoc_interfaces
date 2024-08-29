package trax.aero.util;

import java.sql.Connection;
import java.util.logging.Logger;

import javax.lang.model.type.ErrorType;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import trax.aero.logger.LogManager;



public class DataSourceClient 
{  	

	static Logger logger = LogManager.getLogger("TechdocEDCO");
	
	
	public static Connection getConnection() throws Exception  
	{
		Connection connection;	
		Context ctx;
		try 
		{
			ctx = new InitialContext();
		
			DataSource ds = null;//(DataSource)ctx.lookup("ManHoursScheduleTraxDS");
			
			if(System.getProperty("jboss.server.config.dir") != null)
				ds = (DataSource)ctx.lookup("java:/TechdocDS");
			else
				ds = (DataSource)ctx.lookup("TechdocDS");
			connection = ds.getConnection();
		} catch (Exception e) {
			 logger.severe("An error ocurred trying connect to the DataSource: TraxStandaloneDS");
			 throw new Exception("\nGetting error trying to connect to the datasource. " + "\n error: " +  e.getMessage());
		}
	    return connection;
	}
}