package trax.aero.pojo.json;

import com.google.gson.annotations.SerializedName;


public class ADDATTR {

    @SerializedName("ATTR-NAME")
    String ATTRNAME;

    @SerializedName("ATTR-VALUE")
    String ATTRVALUE;

    public String getATTRNAME() {
        return ATTRNAME;
    }

    public void setATTRNAME(String ATTRNAME) {
        this.ATTRNAME = ATTRNAME;
    }

    public String getATTRVALUE() {
        return ATTRVALUE;
    }

    public void setATTRVALUE(String ATTRVALUE) {
        this.ATTRVALUE = ATTRVALUE;
    }

}