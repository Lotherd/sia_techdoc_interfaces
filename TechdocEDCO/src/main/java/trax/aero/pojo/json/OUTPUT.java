package trax.aero.pojo.json;
import java.util.List;

import com.google.gson.annotations.SerializedName;

   
public class OUTPUT {

   @SerializedName("MODEL")
   MODEL MODEL;

   @SerializedName("FILENAME")
   String FILENAME;

   @SerializedName("ATTACHMENT")
   List<ATTACHMENT> ATTACHMENT;


    public void setMODEL(MODEL MODEL) {
        this.MODEL = MODEL;
    }
    public MODEL getMODEL() {
        return MODEL;
    }
    
    public void setFILENAME(String FILENAME) {
        this.FILENAME = FILENAME;
    }
    public String getFILENAME() {
        return FILENAME;
    }
    
    public void setATTACHMENT(List<ATTACHMENT> ATTACHMENT) {
        this.ATTACHMENT = ATTACHMENT;
    }
    public List<ATTACHMENT> getATTACHMENT() {
        return ATTACHMENT;
    }
    
}