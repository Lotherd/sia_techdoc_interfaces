package trax.aero.pojo.jdf;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

public class ScreeningParamsBean {
  String Class = "";
  String iD = "";
  String status = "";
  String textContent;
  ScreenSelectorBean screenSelectorBean;

  public void setClass(String Class) {
    this.Class = Class;
  }

  @XmlAttribute(name = "Class")
  public String getClasS() {
    return Class;
  }

  @XmlAttribute(name = "ID")
  public String getID() {
    return iD;
  }

  public void setID(String iD) {
    this.iD = iD;
  }

  @XmlAttribute(name = "Status")
  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  @XmlAttribute(name = "TextContent")
  public String getTextContent() {
    return textContent;
  }

  public void setTextContent(String textContent) {
    this.textContent = textContent;
  }

  @XmlElement(name = "ScreenSelector")
  public ScreenSelectorBean getScreenSelectorBean() {
    if (screenSelectorBean == null) screenSelectorBean = new ScreenSelectorBean();
    return screenSelectorBean;
  }

  public void setScreenSelectorBean(ScreenSelectorBean screenSelectorBean) {
    this.screenSelectorBean = screenSelectorBean;
  }
}
