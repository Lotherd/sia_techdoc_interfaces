package trax.aero.pojo.jdf;

import javax.xml.bind.annotation.XmlAttribute;



public class ComChannelBean {
    String channelType="";
    String locator="";
    String textContent="";
  public void setChannelType(String channelType) { 
		this.channelType=channelType;
	} 
    @XmlAttribute(name = "ChannelType")
    public String getChannelType() { 
		return channelType;
	} 
  public void setLocator(String locator) { 
		this.locator=locator;
	} 
    @XmlAttribute(name = "Locator")
    public String getLocator() { 
		return locator;
	} 
  public void setTextContent(String textContent) { 
		this.textContent=textContent;
	} 
    @XmlAttribute(name = "TextContent")
    public String getTextContent() { 
		return textContent;
	} 

}