package trax.aero.pojo.xml;

import javax.xml.bind.annotation.*;

/**
 * Java class for anonymous complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="PANELTEXT" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="PANELNBR" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="PANELSTATE" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
    name = "",
    propOrder = {"paneltext"})
public class PANEL {

  @XmlElement(name = "PANELTEXT")
  protected String paneltext;

  @XmlAttribute(name = "PANEL-NBR")
  protected String panelnbr;

  @XmlAttribute(name = "PANEL-STATE")
  protected String panelstate;

  /**
   * Gets the value of the paneltext property.
   *
   * @return possible object is {@link String }
   */
  public String getPANELTEXT() {
    return paneltext;
  }

  /**
   * Sets the value of the paneltext property.
   *
   * @param value allowed object is {@link String }
   */
  public void setPANELTEXT(String value) {
    this.paneltext = value;
  }

  /**
   * Gets the value of the panelnbr property.
   *
   * @return possible object is {@link String }
   */
  public String getPANELNBR() {
    return panelnbr;
  }

  /**
   * Sets the value of the panelnbr property.
   *
   * @param value allowed object is {@link String }
   */
  public void setPANELNBR(String value) {
    this.panelnbr = value;
  }

  /**
   * Gets the value of the panelstate property.
   *
   * @return possible object is {@link String }
   */
  public String getPANELSTATE() {
    return panelstate;
  }

  /**
   * Sets the value of the panelstate property.
   *
   * @param value allowed object is {@link String }
   */
  public void setPANELSTATE(String value) {
    this.panelstate = value;
  }
}
