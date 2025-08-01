/*
* This source code file is the intellectual property of TRAX USA Corp.
* Copyright (c) 2025 TRAX USA Corp. All rights reserved.
*/
package trax.aero.data.client;

import java.sql.Connection;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

public class DataSourceClient {

    public static Connection getConnection() throws Exception {
        Connection connection;
        Context ctx;
        try {
            ctx = new InitialContext();

            DataSource ds; // (DataSource)ctx.lookup("ManHoursScheduleTraxDS");

            if (System.getProperty("jboss.server.config.dir") != null)
                ds = (DataSource) ctx.lookup("java:/TechdocDS");
            else ds = (DataSource) ctx.lookup("TechdocDS");
            connection = ds.getConnection();
        } catch (Exception e) {
            throw new Exception(
                    "\nGetting error trying to connect to the datasource. " + "\n error: " + e.getMessage());
        }
        return connection;
    }
}
