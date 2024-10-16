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




public class HoleMakingParamsBean {
    String Class="";
    String iD="";
    String noOp="";
    String status="";
    String textContent;
  public void setClass(String Class) { 
		this.Class=Class;
	} 
    @XmlAttribute(name = "Class")
    public String getClasS() { 
		return Class;
	} 
  public void setID(String iD) { 
		this.iD=iD;
	} 
    @XmlAttribute(name = "ID")
    public String getID() { 
		return iD;
	} 
  public void setNoOp(String noOp) { 
		this.noOp=noOp;
	} 
    @XmlAttribute(name = "NoOp")
    public String getNoOp() { 
		return noOp;
	} 
  public void setStatus(String status) { 
		this.status=status;
	} 
    @XmlAttribute(name = "Status")
    public String getStatus() { 
		return status;
	} 
  public void setTextContent(String textContent) { 
		this.textContent=textContent;
	} 
    @XmlAttribute(name = "TextContent")
    public String getTextContent() { 
		return textContent;
	} 

}