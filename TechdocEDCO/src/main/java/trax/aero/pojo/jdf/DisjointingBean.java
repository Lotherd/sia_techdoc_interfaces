package trax.aero.pojo.jdf;

import javax.xml.bind.annotation.XmlAttribute;

public class DisjointingBean {
  String offsetDirection = "";
  String textContent;

  @XmlAttribute(name = "OffsetDirection")
  public String getOffsetDirection() {
    return offsetDirection;
  }

  public void setOffsetDirection(String offsetDirection) {
    this.offsetDirection = offsetDirection;
  }

  @XmlAttribute(name = "TextContent")
  public String getTextContent() {
    return textContent;
  }

  public void setTextContent(String textContent) {
    this.textContent = textContent;
  }
}
