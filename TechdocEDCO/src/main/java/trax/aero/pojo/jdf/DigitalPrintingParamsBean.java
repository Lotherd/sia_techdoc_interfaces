package trax.aero.pojo.jdf;

import java.sql.Date;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.xml.bind.annotation.*;

import com.fasterxml.jackson.annotation.*;


public class DigitalPrintingParamsBean {
    String runIndex = "";
    String textContent;
    MediaRefBean mediaRefBean;

    @XmlAttribute(name = "RunIndex")
    public String getRunIndex() {
        return runIndex;
    }

    public void setRunIndex(String runIndex) {
        this.runIndex = runIndex;
    }

    @XmlAttribute(name = "TextContent")
    public String getTextContent() {
        return textContent;
    }

    public void setTextContent(String textContent) {
        this.textContent = textContent;
    }

    @XmlElement(name = "MediaRef")
    public MediaRefBean getMediaRefBean() {
        if (mediaRefBean == null) mediaRefBean = new MediaRefBean();
        return mediaRefBean;
    }

    public void setMediaRefBean(MediaRefBean mediaRefBean) {
        this.mediaRefBean = mediaRefBean;
    }

}