/*
* This source code file is the intellectual property of TRAX USA Corp.
* Copyright (c) 2025 TRAX USA Corp. All rights reserved.
*/
package trax.aero.api.controller;

import javax.ejb.EJB;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import org.tinylog.Logger;
import trax.aero.data.ITechDocData;
import trax.aero.messaging.mq.MqUtilities;
import trax.aero.pojo.Print;

@Path("/Service")
public class TechDocController {

    @EJB ITechDocData data;

    @GET
    @Path("/health")
    @Produces(MediaType.TEXT_PLAIN)
    public Response health(String input) {
        try {
            return Response.ok(data.health()).build();
        } catch (Exception e) {
            return Response.status(Status.BAD_REQUEST)
                    .type(MediaType.TEXT_PLAIN)
                    .entity(e.getMessage())
                    .build();
        }
    }

    @POST
    @Path("/sendMqText")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    public Response insertXml(String input) {
        try {
            MqUtilities.sendMqText(input);
        } catch (Exception e) {
            Logger.error(e);
        }
        return Response.ok().build();
    }

    @POST
    @Path("/print")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response print(Print input) {
        try {
            Logger.info("Print WO:" + input.getWo() + ", PATH:" + input.getPath());
            data.sendPrintToOutBound(input);
        } catch (Exception e) {
            Logger.error(e);
        }
        return Response.ok().build();
    }
}
