package trax.aero.start;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;

import org.tinylog.Logger;

import trax.aero.interfaces.IModelData;
import trax.aero.util.RunAble;

@Startup
@Singleton
public class Start {

    RunAble timer = null;
    @EJB
    IModelData data;
    private ScheduledExecutorService scheduledServ;

    @PostConstruct
    public void start() {
        timer = new RunAble(data);

        if (scheduledServ == null) {
            int scheduledPoolSize = 1;
            Logger.info("Creating default Scheduled Executor Service [poolSize =" + scheduledPoolSize + "]");
            this.scheduledServ = Executors.newScheduledThreadPool(scheduledPoolSize);
        }
        scheduledServ.scheduleAtFixedRate(timer, 30, Long.parseLong(System.getProperty("Techdoc_interval")), TimeUnit.SECONDS);

    }

    @PreDestroy
    public void stop() {
        if (!scheduledServ.isShutdown()) {
            scheduledServ.shutdown();
        }
    }

}
