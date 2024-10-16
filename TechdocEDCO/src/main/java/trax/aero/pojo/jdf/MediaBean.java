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




public class MediaBean {
    String Class="";
    String customMediaType="";
    String descriptiveName="";
    String dimension="";
    String holeType="";
    String iD="";
    String mediaColorNameDetails="";
    String mediaSetCount="";
    String status="";
    String weight="";
    String textContent;
  public void setClass(String Class) { 
		this.Class=Class;
	} 
    @XmlAttribute(name = "Class")
    public String getClasS() { 
		return Class;
	} 
  public void setCustomMediaType(String customMediaType) { 
		this.customMediaType=customMediaType;
	} 
    @XmlAttribute(name = "CustomMediaType")
    public String getCustomMediaType() { 
		return customMediaType;
	} 
  public void setDescriptiveName(String descriptiveName) { 
		this.descriptiveName=descriptiveName;
	} 
    @XmlAttribute(name = "DescriptiveName")
    public String getDescriptiveName() { 
		return descriptiveName;
	} 
  public void setDimension(String dimension) { 
		this.dimension=dimension;
	} 
    @XmlAttribute(name = "Dimension")
    public String getDimension() { 
		return dimension;
	} 
  public void setHoleType(String holeType) { 
		this.holeType=holeType;
	} 
    @XmlAttribute(name = "HoleType")
    public String getHoleType() { 
		return holeType;
	} 
  public void setID(String iD) { 
		this.iD=iD;
	} 
    @XmlAttribute(name = "ID")
    public String getID() { 
		return iD;
	} 
  public void setMediaColorNameDetails(String mediaColorNameDetails) { 
		this.mediaColorNameDetails=mediaColorNameDetails;
	} 
    @XmlAttribute(name = "MediaColorNameDetails")
    public String getMediaColorNameDetails() { 
		return mediaColorNameDetails;
	} 
  public void setMediaSetCount(String mediaSetCount) { 
		this.mediaSetCount=mediaSetCount;
	} 
    @XmlAttribute(name = "MediaSetCount")
    public String getMediaSetCount() { 
		return mediaSetCount;
	} 
  public void setStatus(String status) { 
		this.status=status;
	} 
    @XmlAttribute(name = "Status")
    public String getStatus() { 
		return status;
	} 
  public void setWeight(String weight) { 
		this.weight=weight;
	} 
    @XmlAttribute(name = "Weight")
    public String getWeight() { 
		return weight;
	} 
  public void setTextContent(String textContent) { 
		this.textContent=textContent;
	} 
    @XmlAttribute(name = "TextContent")
    public String getTextContent() { 
		return textContent;
	} 

}