package trax.aero.pojo.jdf;

import java.sql.Date;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.xml.bind.annotation.*;

import com.fasterxml.jackson.annotation.*;


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