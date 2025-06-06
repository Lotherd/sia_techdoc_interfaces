package trax.aero.pojo.jdf;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;


public class ContactBean {
    String Class = "";
    String contactTypes = "";
    String iD = "";
    String status = "";
    String userID = "";
    String textContent;
    ComChannelBean comChannelBean;

    public void setClass(String Class) {
        this.Class = Class;
    }

    @XmlAttribute(name = "Class")
    public String getClasS() {
        return Class;
    }

    @XmlAttribute(name = "ContactTypes")
    public String getContactTypes() {
        return contactTypes;
    }

    public void setContactTypes(String contactTypes) {
        this.contactTypes = contactTypes;
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

    @XmlAttribute(name = "UserID")
    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    @XmlAttribute(name = "TextContent")
    public String getTextContent() {
        return textContent;
    }

    public void setTextContent(String textContent) {
        this.textContent = textContent;
    }

    @XmlElement(name = "ComChannel")
    public ComChannelBean getComChannelBean() {
        if (comChannelBean == null) comChannelBean = new ComChannelBean();
        return comChannelBean;
    }

    public void setComChannelBean(ComChannelBean comChannelBean) {
        this.comChannelBean = comChannelBean;
    }

}