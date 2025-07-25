/*
* This source code file is the intellectual property of TRAX USA Corp.
* Copyright (c) 2025 TRAX USA Corp. All rights reserved.
*/
package trax.aero.core.startup;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import trax.aero.core.scheduler.TechDocProcessor;
import trax.aero.data.ITechDocData;

@Startup
@Singleton
public class ApplicationInitializer {

    TechDocProcessor processor = null;

    @EJB ITechDocData data;

    private ScheduledExecutorService scheduledServ;

    @PostConstruct
    public void start() {
        processor = new TechDocProcessor(data);

        if (scheduledServ == null) {
            int scheduledPoolSize = 1;
            this.scheduledServ = Executors.newScheduledThreadPool(scheduledPoolSize);
        }
        scheduledServ.scheduleAtFixedRate(
                processor, 30, Long.parseLong(System.getProperty("Techdoc_interval")), TimeUnit.SECONDS);
    }

    @PreDestroy
    public void stop() {
        if (!scheduledServ.isShutdown()) {
            scheduledServ.shutdown();
        }
    }
}
