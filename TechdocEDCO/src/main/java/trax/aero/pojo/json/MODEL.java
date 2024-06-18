package trax.aero.pojo.json;

import com.google.gson.annotations.SerializedName;

   
public class MODEL {

   @SerializedName("MODELNBR")
   String MODELNBR;

   @SerializedName("EFFECTIVITY")
   EFFECTIVITY EFFECTIVITY;


    public void setMODELNBR(String MODELNBR) {
        this.MODELNBR = MODELNBR;
    }
    public String getMODELNBR() {
        return MODELNBR;
    }
    
    public void setEFFECTIVITY(EFFECTIVITY EFFECTIVITY) {
        this.EFFECTIVITY = EFFECTIVITY;
    }
    public EFFECTIVITY getEFFECTIVITY() {
        return EFFECTIVITY;
    }
    
}