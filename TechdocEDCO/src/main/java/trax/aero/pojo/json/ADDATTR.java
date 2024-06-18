package trax.aero.pojo.json;

import com.google.gson.annotations.SerializedName;

   
public class ADDATTR {

   @SerializedName("ATTR-NAME")
   String ATTRNAME;

   @SerializedName("ATTR-VALUE")
   String ATTRVALUE;


    public void setATTRNAME(String ATTRNAME) {
        this.ATTRNAME = ATTRNAME;
    }
    public String getATTRNAME() {
        return ATTRNAME;
    }
    
    public void setATTRVALUE(String ATTRVALUE) {
        this.ATTRVALUE = ATTRVALUE;
    }
    public String getATTRVALUE() {
        return ATTRVALUE;
    }
    
}