package trax.aero.pojo.jdf;

import javax.xml.bind.annotation.XmlAttribute;

public class ScreenSelectorBean {
  String frequency = "";
  String screeningType = "";
  String spotFunction = "";
  String textContent;

  @XmlAttribute(name = "Frequency")
  public String getFrequency() {
    return frequency;
  }

  public void setFrequency(String frequency) {
    this.frequency = frequency;
  }

  @XmlAttribute(name = "ScreeningType")
  public String getScreeningType() {
    return screeningType;
  }

  public void setScreeningType(String screeningType) {
    this.screeningType = screeningType;
  }

  @XmlAttribute(name = "SpotFunction")
  public String getSpotFunction() {
    return spotFunction;
  }

  public void setSpotFunction(String spotFunction) {
    this.spotFunction = spotFunction;
  }

  @XmlAttribute(name = "TextContent")
  public String getTextContent() {
    return textContent;
  }

  public void setTextContent(String textContent) {
    this.textContent = textContent;
  }
}
