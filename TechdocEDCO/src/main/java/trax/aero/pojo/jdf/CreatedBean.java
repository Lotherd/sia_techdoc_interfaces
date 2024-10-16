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




public class CreatedBean {
    String agentName="";
    String agentVersion="";
    String iD="";
    String timeStamp="";
    String textContent;
  public void setAgentName(String agentName) { 
		this.agentName=agentName;
	} 
    @XmlAttribute(name = "AgentName")
    public String getAgentName() { 
		return agentName;
	} 
  public void setAgentVersion(String agentVersion) { 
		this.agentVersion=agentVersion;
	} 
    @XmlAttribute(name = "AgentVersion")
    public String getAgentVersion() { 
		return agentVersion;
	} 
  public void setID(String iD) { 
		this.iD=iD;
	} 
    @XmlAttribute(name = "ID")
    public String getID() { 
		return iD;
	} 
  public void setTimeStamp(String timeStamp) { 
		this.timeStamp=timeStamp;
	} 
    @XmlAttribute(name = "TimeStamp")
    public String getTimeStamp() { 
		return timeStamp;
	} 
  public void setTextContent(String textContent) { 
		this.textContent=textContent;
	} 
    @XmlAttribute(name = "TextContent")
    public String getTextContent() { 
		return textContent;
	} 

}