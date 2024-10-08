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




public class LayoutPreparationParamsBean {
    String runIndex="";
    String sides="";
    String textContent="";
  public void setRunIndex(String runIndex) { 
		this.runIndex=runIndex;
	} 
    @XmlAttribute(name = "RunIndex")
    public String getRunIndex() { 
		return runIndex;
	} 
  public void setSides(String sides) { 
		this.sides=sides;
	} 
    @XmlAttribute(name = "Sides")
    public String getSides() { 
		return sides;
	} 
  public void setTextContent(String textContent) { 
		this.textContent=textContent;
	} 
    @XmlAttribute(name = "TextContent")
    public String getTextContent() { 
		return textContent;
	} 

}