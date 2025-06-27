package trax.aero.pojo.json;

import com.google.gson.annotations.SerializedName;

public class ATTACHMENT {

  @SerializedName("ATT-TYPE")
  String ATTTYPE;

  @SerializedName("ID")
  String ID;

  @SerializedName("NOOFPAGES")
  String NOOFPAGES;

  public String getATTTYPE() {
    return ATTTYPE;
  }

  public void setATTTYPE(String ATTTYPE) {
    this.ATTTYPE = ATTTYPE;
  }

  public String getID() {
    return ID;
  }

  public void setID(String ID) {
    this.ID = ID;
  }

  public String getNOOFPAGES() {
    return NOOFPAGES;
  }

  public void setNOOFPAGES(String NOOFPAGES) {
    this.NOOFPAGES = NOOFPAGES;
  }
}
