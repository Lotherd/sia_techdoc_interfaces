/*
* This source code file is the intellectual property of TRAX USA Corp.
* Copyright (c) 2025 TRAX USA Corp. All rights reserved.
*/
package trax.aero.pojo.jdf;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

public class CustomerInfoBean {
    String Class = "";
    String customerJobName = "";
    String iD = "";
    String status = "";
    String textContent;
    ContactRefBean contactRefBean;

    public void setClass(String Class) {
        this.Class = Class;
    }

    @XmlAttribute(name = "Class")
    public String getClasS() {
        return Class;
    }

    @XmlAttribute(name = "CustomerJobName")
    public String getCustomerJobName() {
        return customerJobName;
    }

    public void setCustomerJobName(String customerJobName) {
        this.customerJobName = customerJobName;
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

    @XmlElement(name = "ContactRef")
    public ContactRefBean getContactRefBean() {
        if (contactRefBean == null) contactRefBean = new ContactRefBean();
        return contactRefBean;
    }

    public void setContactRefBean(ContactRefBean contactRefBean) {
        this.contactRefBean = contactRefBean;
    }
}
