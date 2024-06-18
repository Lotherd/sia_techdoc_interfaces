package trax.aero.pojo.json;

import com.google.gson.annotations.SerializedName;

   
public class EFFECTIVITY {

   @SerializedName("REGNBR")
   String REGNBR;

   @SerializedName("CONFIG")
   String CONFIG;

   @SerializedName("JOBCARD")
   JOBCARD JOBCARD;


    public void setREGNBR(String REGNBR) {
        this.REGNBR = REGNBR;
    }
    public String getREGNBR() {
        return REGNBR;
    }
    
    public void setCONFIG(String CONFIG) {
        this.CONFIG = CONFIG;
    }
    public String getCONFIG() {
        return CONFIG;
    }
    
    public void setJOBCARD(JOBCARD JOBCARD) {
        this.JOBCARD = JOBCARD;
    }
    public JOBCARD getJOBCARD() {
        return JOBCARD;
    }
    
}