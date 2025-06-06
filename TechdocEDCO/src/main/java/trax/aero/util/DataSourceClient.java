package trax.aero.util;

import org.jboss.logging.Logger;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;
import java.sql.Connection;


public class DataSourceClient {

    private static final Logger logger = Logger.getLogger(DataSourceClient.class);

    public static Connection getConnection() throws Exception {
        Connection connection;
        Context ctx;
        try {
            ctx = new InitialContext();

            DataSource ds = null;//(DataSource)ctx.lookup("ManHoursScheduleTraxDS");

            if (System.getProperty("jboss.server.config.dir") != null)
                ds = (DataSource) ctx.lookup("java:/TechdocDS");
            else
                ds = (DataSource) ctx.lookup("TechdocDS");
            connection = ds.getConnection();
        } catch (Exception e) {
            logger.error("An error occurred trying connect to the DataSource: TraxStandaloneDS");
            throw new Exception("\nGetting error trying to connect to the datasource. " + "\n error: " + e.getMessage());
        }
        return connection;
    }
}