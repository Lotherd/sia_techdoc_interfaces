package trax.aero.pojo.json;

import com.google.gson.annotations.SerializedName;


public class MODEL {

    @SerializedName("MODELNBR")
    String MODELNBR;

    @SerializedName("EFFECTIVITY")
    EFFECTIVITY EFFECTIVITY;

    public String getMODELNBR() {
        return MODELNBR;
    }

    public void setMODELNBR(String MODELNBR) {
        this.MODELNBR = MODELNBR;
    }

    public EFFECTIVITY getEFFECTIVITY() {
        return EFFECTIVITY;
    }

    public void setEFFECTIVITY(EFFECTIVITY EFFECTIVITY) {
        this.EFFECTIVITY = EFFECTIVITY;
    }

}