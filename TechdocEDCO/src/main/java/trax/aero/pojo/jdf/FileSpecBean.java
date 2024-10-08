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




public class FileSpecBean {
    String mimeType="";
    String uRL="";
    String textContent="";
  public void setMimeType(String mimeType) { 
		this.mimeType=mimeType;
	} 
    @XmlAttribute(name = "MimeType")
    public String getMimeType() { 
		return mimeType;
	} 
  public void setURL(String uRL) { 
		this.uRL=uRL;
	} 
    @XmlAttribute(name = "URL")
    public String getURL() { 
		return uRL;
	} 
  public void setTextContent(String textContent) { 
		this.textContent=textContent;
	} 
    @XmlAttribute(name = "TextContent")
    public String getTextContent() { 
		return textContent;
	} 

}