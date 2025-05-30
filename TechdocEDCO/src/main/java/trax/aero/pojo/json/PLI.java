package trax.aero.pojo.json;

import java.util.ArrayList;

import com.google.gson.annotations.SerializedName;


public class PLI {

    @SerializedName("CHAPNBR")
    String CHAPNBR;

    @SerializedName("SECTNBR")
    String SECTNBR;

    @SerializedName("SUBJNBR")
    String SUBJNBR;

    @SerializedName("PLINBR")
    String PLINBR;

    @SerializedName("SKILL")
    String SKILL;

    @SerializedName("LANGUAGE")
    String LANGUAGE;

    @SerializedName("PLITEXT")
    String PLITEXT;

    @SerializedName("SIMPLEREFERENCE")
    SIMPLEREFERENCE SIMPLEREFERENCE;

    @SerializedName("ADDATTR")
    ArrayList<ADDATTR> ADDATTR;

    public String getCHAPNBR() {
        return CHAPNBR;
    }

    public void setCHAPNBR(String CHAPNBR) {
        this.CHAPNBR = CHAPNBR;
    }

    public String getSECTNBR() {
        return SECTNBR;
    }

    public void setSECTNBR(String SECTNBR) {
        this.SECTNBR = SECTNBR;
    }

    public String getSUBJNBR() {
        return SUBJNBR;
    }

    public void setSUBJNBR(String SUBJNBR) {
        this.SUBJNBR = SUBJNBR;
    }

    public String getPLINBR() {
        return PLINBR;
    }

    public void setPLINBR(String PLINBR) {
        this.PLINBR = PLINBR;
    }

    public String getSKILL() {
        return SKILL;
    }

    public void setSKILL(String SKILL) {
        this.SKILL = SKILL;
    }

    public String getLANGUAGE() {
        return LANGUAGE;
    }

    public void setLANGUAGE(String LANGUAGE) {
        this.LANGUAGE = LANGUAGE;
    }

    public String getPLITEXT() {
        return PLITEXT;
    }

    public void setPLITEXT(String PLITEXT) {
        this.PLITEXT = PLITEXT;
    }

    public SIMPLEREFERENCE getSIMPLEREFERENCE() {
        return SIMPLEREFERENCE;
    }

    public void setSIMPLEREFERENCE(SIMPLEREFERENCE SIMPLEREFERENCE) {
        this.SIMPLEREFERENCE = SIMPLEREFERENCE;
    }

    public ArrayList<ADDATTR> getADDATTR() {
        return ADDATTR;
    }

    public void setADDATTR(ArrayList<ADDATTR> ADDATTR) {
        this.ADDATTR = ADDATTR;
    }

}