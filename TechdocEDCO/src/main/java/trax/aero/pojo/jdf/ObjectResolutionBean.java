package trax.aero.pojo.jdf;

import javax.xml.bind.annotation.XmlAttribute;

public class ObjectResolutionBean {
  String resolution = "";
  String textContent;

  @XmlAttribute(name = "Resolution")
  public String getResolution() {
    return resolution;
  }

  public void setResolution(String resolution) {
    this.resolution = resolution;
  }

  @XmlAttribute(name = "TextContent")
  public String getTextContent() {
    return textContent;
  }

  public void setTextContent(String textContent) {
    this.textContent = textContent;
  }
}
