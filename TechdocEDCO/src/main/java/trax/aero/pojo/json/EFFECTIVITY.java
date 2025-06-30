/*
* This source code file is the intellectual property of TRAX USA Corp.
* Copyright (c) 2025 TRAX USA Corp. All rights reserved.
*/
package trax.aero.pojo.json;

import com.google.gson.annotations.SerializedName;

public class EFFECTIVITY {

    @SerializedName("REGNBR")
    String REGNBR;

    @SerializedName("CONFIG")
    String CONFIG;

    @SerializedName("JOBCARD")
    JOBCARD JOBCARD;

    public String getREGNBR() {
        return REGNBR;
    }

    public void setREGNBR(String REGNBR) {
        this.REGNBR = REGNBR;
    }

    public String getCONFIG() {
        return CONFIG;
    }

    public void setCONFIG(String CONFIG) {
        this.CONFIG = CONFIG;
    }

    public JOBCARD getJOBCARD() {
        return JOBCARD;
    }

    public void setJOBCARD(JOBCARD JOBCARD) {
        this.JOBCARD = JOBCARD;
    }
}
