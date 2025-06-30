/*
* This source code file is the intellectual property of TRAX USA Corp.
* Copyright (c) 2025 TRAX USA Corp. All rights reserved.
*/
package trax.aero.pojo.json;

import com.google.gson.annotations.SerializedName;

public class JOBI {

    @SerializedName("JOBINBR")
    String JOBINBR;

    @SerializedName("JOBITITLE")
    String JOBITITLE;

    @SerializedName("LANGUAGE")
    String LANGUAGE;

    @SerializedName("ZONE")
    String ZONE;

    @SerializedName("INTERVAL")
    String INTERVAL;

    @SerializedName("HARDTIME")
    String HARDTIME;

    @SerializedName("DIMENSION")
    String DIMENSION;

    @SerializedName("PANEL")
    PANEL PANEL;

    @SerializedName("PLI")
    PLI PLI;

    public String getJOBINBR() {
        return JOBINBR;
    }

    public void setJOBINBR(String JOBINBR) {
        this.JOBINBR = JOBINBR;
    }

    public String getJOBITITLE() {
        return JOBITITLE;
    }

    public void setJOBITITLE(String JOBITITLE) {
        this.JOBITITLE = JOBITITLE;
    }

    public String getLANGUAGE() {
        return LANGUAGE;
    }

    public void setLANGUAGE(String LANGUAGE) {
        this.LANGUAGE = LANGUAGE;
    }

    public String getZONE() {
        return ZONE;
    }

    public void setZONE(String ZONE) {
        this.ZONE = ZONE;
    }

    public String getINTERVAL() {
        return INTERVAL;
    }

    public void setINTERVAL(String INTERVAL) {
        this.INTERVAL = INTERVAL;
    }

    public String getHARDTIME() {
        return HARDTIME;
    }

    public void setHARDTIME(String HARDTIME) {
        this.HARDTIME = HARDTIME;
    }

    public String getDIMENSION() {
        return DIMENSION;
    }

    public void setDIMENSION(String DIMENSION) {
        this.DIMENSION = DIMENSION;
    }

    public PANEL getPANEL() {
        return PANEL;
    }

    public void setPANEL(PANEL PANEL) {
        this.PANEL = PANEL;
    }

    public PLI getPLI() {
        return PLI;
    }

    public void setPLI(PLI PLI) {
        this.PLI = PLI;
    }
}
