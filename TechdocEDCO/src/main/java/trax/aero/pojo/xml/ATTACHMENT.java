/*
* This source code file is the intellectual property of TRAX USA Corp.
* Copyright (c) 2025 TRAX USA Corp. All rights reserved.
*/
package trax.aero.pojo.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

/**
 * Java class for anonymous complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="ATTTYPE" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="ID" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
public class ATTACHMENT {

    @XmlAttribute(name = "ATT-TYPE")
    protected String atttype;

    @XmlAttribute(name = "ID")
    protected String id;

    @XmlAttribute(name = "NOOFPAGES")
    protected String noofpages;

    /**
     * Gets the value of the atttype property.
     *
     * @return possible object is {@link String }
     */
    public String getATTTYPE() {
        return atttype;
    }

    /**
     * Sets the value of the atttype property.
     *
     * @param value allowed object is {@link String }
     */
    public void setATTTYPE(String value) {
        this.atttype = value;
    }

    /**
     * Gets the value of the id property.
     *
     * @return possible object is {@link String }
     */
    public String getID() {
        return id;
    }

    /**
     * Sets the value of the id property.
     *
     * @param value allowed object is {@link String }
     */
    public void setID(String value) {
        this.id = value;
    }

    public String getNoofpages() {
        return noofpages;
    }

    public void setNoofpages(String noofpages) {
        this.noofpages = noofpages;
    }
}
