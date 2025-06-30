/*
* This source code file is the intellectual property of TRAX USA Corp.
* Copyright (c) 2025 TRAX USA Corp. All rights reserved.
*/
package trax.aero.pojo.json;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class OUTPUT {

    @SerializedName("MODEL")
    MODEL MODEL;

    @SerializedName("FILENAME")
    String FILENAME;

    @SerializedName("ATTACHMENT")
    List<ATTACHMENT> ATTACHMENT;

    public MODEL getMODEL() {
        return MODEL;
    }

    public void setMODEL(MODEL MODEL) {
        this.MODEL = MODEL;
    }

    public String getFILENAME() {
        return FILENAME;
    }

    public void setFILENAME(String FILENAME) {
        this.FILENAME = FILENAME;
    }

    public List<ATTACHMENT> getATTACHMENT() {
        return ATTACHMENT;
    }

    public void setATTACHMENT(List<ATTACHMENT> ATTACHMENT) {
        this.ATTACHMENT = ATTACHMENT;
    }
}
