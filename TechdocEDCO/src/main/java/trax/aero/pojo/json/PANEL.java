/*
* This source code file is the intellectual property of TRAX USA Corp.
* Copyright (c) 2025 TRAX USA Corp. All rights reserved.
*/
package trax.aero.pojo.json;

import com.google.gson.annotations.SerializedName;

public class PANEL {

    @SerializedName("PANEL-NBR")
    String PANELNBR;

    @SerializedName("PANEL-STATE")
    String PANELSTATE;

    @SerializedName("PANELTEXT")
    String PANELTEXT;

    public String getPANELNBR() {
        return PANELNBR;
    }

    public void setPANELNBR(String PANELNBR) {
        this.PANELNBR = PANELNBR;
    }

    public String getPANELSTATE() {
        return PANELSTATE;
    }

    public void setPANELSTATE(String PANELSTATE) {
        this.PANELSTATE = PANELSTATE;
    }

    public String getPANELTEXT() {
        return PANELTEXT;
    }

    public void setPANELTEXT(String PANELTEXT) {
        this.PANELTEXT = PANELTEXT;
    }
}
