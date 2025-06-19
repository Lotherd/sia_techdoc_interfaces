package trax.aero.start;

import trax.aero.data.IModelData;
import trax.aero.util.RunAble;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

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
