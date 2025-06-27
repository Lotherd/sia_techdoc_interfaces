package trax.aero.pojo.json;

import com.google.gson.annotations.SerializedName;

public class SIMPLEREFERENCE {

  @SerializedName("REFERENCENAME")
  String REFERENCENAME;

  @SerializedName("REF-TEXT")
  String REFTEXT;

  public String getREFERENCENAME() {
    return REFERENCENAME;
  }

  public void setREFERENCENAME(String REFERENCENAME) {
    this.REFERENCENAME = REFERENCENAME;
  }

  public String getREFTEXT() {
    return REFTEXT;
  }

  public void setREFTEXT(String REFTEXT) {
    this.REFTEXT = REFTEXT;
  }
}
