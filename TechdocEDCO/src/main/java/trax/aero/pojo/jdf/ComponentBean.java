package trax.aero.pojo.jdf;

import javax.xml.bind.annotation.XmlAttribute;

public class ComponentBean {
  String Class = "";
  String componentType = "";
  String iD = "";
  String status = "";
  String textContent;

  public void setClass(String Class) {
    this.Class = Class;
  }

  @XmlAttribute(name = "Class")
  public String getClasS() {
    return Class;
  }

  @XmlAttribute(name = "ComponentType")
  public String getComponentType() {
    return componentType;
  }

  public void setComponentType(String componentType) {
    this.componentType = componentType;
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
}
