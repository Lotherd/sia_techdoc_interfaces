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




public class ScreenSelectorBean {
    String frequency="";
    String screeningType="";
    String spotFunction="";
    String textContent;
  public void setFrequency(String frequency) { 
		this.frequency=frequency;
	} 
    @XmlAttribute(name = "Frequency")
    public String getFrequency() { 
		return frequency;
	} 
  public void setScreeningType(String screeningType) { 
		this.screeningType=screeningType;
	} 
    @XmlAttribute(name = "ScreeningType")
    public String getScreeningType() { 
		return screeningType;
	} 
  public void setSpotFunction(String spotFunction) { 
		this.spotFunction=spotFunction;
	} 
    @XmlAttribute(name = "SpotFunction")
    public String getSpotFunction() { 
		return spotFunction;
	} 
  public void setTextContent(String textContent) { 
		this.textContent=textContent;
	} 
    @XmlAttribute(name = "TextContent")
    public String getTextContent() { 
		return textContent;
	} 

}