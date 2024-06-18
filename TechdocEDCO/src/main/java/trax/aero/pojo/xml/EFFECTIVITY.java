package trax.aero.pojo.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="JOBCARD" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="JOBI" minOccurs="0">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="PANEL" maxOccurs="unbounded" minOccurs="0">
 *                               &lt;complexType>
 *                                 &lt;complexContent>
 *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                                     &lt;sequence>
 *                                       &lt;element name="PANELTEXT" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                                     &lt;/sequence>
 *                                     &lt;attribute name="PANELNBR" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                                     &lt;attribute name="PANELSTATE" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                                   &lt;/restriction>
 *                                 &lt;/complexContent>
 *                               &lt;/complexType>
 *                             &lt;/element>
 *                             &lt;element name="PLI" minOccurs="0">
 *                               &lt;complexType>
 *                                 &lt;complexContent>
 *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                                     &lt;sequence>
 *                                       &lt;element name="PLITEXT" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                                       &lt;element name="SIMPLEREFERENCE" maxOccurs="unbounded" minOccurs="0">
 *                                         &lt;complexType>
 *                                           &lt;complexContent>
 *                                             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                                               &lt;attribute name="REFERENCENAME" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                                               &lt;attribute name="REFTEXT" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                                             &lt;/restriction>
 *                                           &lt;/complexContent>
 *                                         &lt;/complexType>
 *                                       &lt;/element>
 *                                       &lt;element name="TOOLSLIST" maxOccurs="unbounded" minOccurs="0">
 *                                         &lt;complexType>
 *                                           &lt;complexContent>
 *                                             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                                               &lt;attribute name="PRTNUMBER" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                                               &lt;attribute name="DESC" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                                               &lt;attribute name="QTY" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                                               &lt;attribute name="UNIT" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                                             &lt;/restriction>
 *                                           &lt;/complexContent>
 *                                         &lt;/complexType>
 *                                       &lt;/element>
 *                                       &lt;element name="MATERIAL" maxOccurs="unbounded" minOccurs="0">
 *                                         &lt;complexType>
 *                                           &lt;complexContent>
 *                                             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                                               &lt;attribute name="MPNNUMBER" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                                               &lt;attribute name="DESC" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                                               &lt;attribute name="QTY" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                                               &lt;attribute name="UNIT" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                                             &lt;/restriction>
 *                                           &lt;/complexContent>
 *                                         &lt;/complexType>
 *                                       &lt;/element>
 *                                       &lt;element name="ATTACHMENT" maxOccurs="unbounded" minOccurs="0">
 *                                         &lt;complexType>
 *                                           &lt;complexContent>
 *                                             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                                               &lt;attribute name="ATTTYPE" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                                               &lt;attribute name="ID" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                                             &lt;/restriction>
 *                                           &lt;/complexContent>
 *                                         &lt;/complexType>
 *                                       &lt;/element>
 *                                       &lt;element name="ADDATTR" maxOccurs="unbounded" minOccurs="0">
 *                                         &lt;complexType>
 *                                           &lt;complexContent>
 *                                             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                                               &lt;attribute name="ATTRNAME" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                                               &lt;attribute name="ATTRVALUE" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                                             &lt;/restriction>
 *                                           &lt;/complexContent>
 *                                         &lt;/complexType>
 *                                       &lt;/element>
 *                                     &lt;/sequence>
 *                                     &lt;attribute name="CHAPNBR" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                                     &lt;attribute name="SECTNBR" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                                     &lt;attribute name="SUBJNBR" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                                     &lt;attribute name="PLINBR" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                                     &lt;attribute name="SKILL" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                                     &lt;attribute name="LANGUAGE" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                                   &lt;/restriction>
 *                                 &lt;/complexContent>
 *                               &lt;/complexType>
 *                             &lt;/element>
 *                           &lt;/sequence>
 *                           &lt;attribute name="JOBINBR" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                           &lt;attribute name="JOBITITLE" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                           &lt;attribute name="LANGUAGE" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                           &lt;attribute name="ZONE" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                           &lt;attribute name="INTERVAL" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                           &lt;attribute name="HARDTIME" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                           &lt;attribute name="DIMENSION" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                 &lt;/sequence>
 *                 &lt;attribute name="JCNBR" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                 &lt;attribute name="JOBNBR" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                 &lt;attribute name="JCTITLE" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                 &lt;attribute name="CENTER" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                 &lt;attribute name="ISSUENBR" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                 &lt;attribute name="ISSUEDATE" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                 &lt;attribute name="WPID" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                 &lt;attribute name="WPNBR" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                 &lt;attribute name="WPTITLE" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                 &lt;attribute name="WPDATE" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                 &lt;attribute name="ENGINESN" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                 &lt;attribute name="ENGINEPOS" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                 &lt;attribute name="APU" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                 &lt;attribute name="APUSN" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                 &lt;attribute name="ORIGINATOR" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                 &lt;attribute name="SEQNBR" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                 &lt;attribute name="AREACODE" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                 &lt;attribute name="AREA" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                 &lt;attribute name="TRADECODE" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                 &lt;attribute name="TRADE" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                 &lt;attribute name="POS" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                 &lt;attribute name="MANHRS" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                 &lt;attribute name="WONBR" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                 &lt;attribute name="LANGUAGE" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                 &lt;attribute name="CREWSIZE" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                 &lt;attribute name="DURATION" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                 &lt;attribute name="INTERVAL" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                 &lt;attribute name="HARDTIME" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                 &lt;attribute name="DIMENSION" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                 &lt;attribute name="OPPORTUNITY" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                 &lt;attribute name="TYPE" type="{http://www.w3.org/2001/XMLSchema}string" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *       &lt;attribute name="REGNBR" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="CONFIG" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "jobcard"
})
public class EFFECTIVITY {



    @XmlElement(name = "JOBCARD")
    protected JOBCARD jobcard;
    @XmlAttribute(name = "REGNBR")
    protected String regnbr;
    @XmlAttribute(name = "CONFIG")
    protected String config;

    /**
     * Gets the value of the jobcard property.
     * 
     * @return
     *     possible object is
     *     {@link JOBCARD }
     *     
     */
    public JOBCARD getJOBCARD() {
        return jobcard;
    }

    /**
     * Sets the value of the jobcard property.
     * 
     * @param value
     *     allowed object is
     *     {@link JOBCARD }
     *     
     */
    public void setJOBCARD(JOBCARD value) {
        this.jobcard = value;
    }

    /**
     * Gets the value of the regnbr property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getREGNBR() {
        return regnbr;
    }

    /**
     * Sets the value of the regnbr property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setREGNBR(String value) {
        this.regnbr = value;
    }

    /**
     * Gets the value of the config property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCONFIG() {
        return config;
    }

    /**
     * Sets the value of the config property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCONFIG(String value) {
        this.config = value;
    }


    


}
