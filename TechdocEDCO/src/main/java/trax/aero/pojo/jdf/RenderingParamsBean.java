package trax.aero.pojo.jdf;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;


public class RenderingParamsBean {
    String Class = "";
    String iD = "";
    String status = "";
    String textContent;
    ObjectResolutionBean objectResolutionBean;

    public void setClass(String Class) {
        this.Class = Class;
    }

    @XmlAttribute(name = "Class")
    public String getClasS() {
        return Class;
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

    @XmlElement(name = "ObjectResolution")
    public ObjectResolutionBean getObjectResolutionBean() {
        if (objectResolutionBean == null) objectResolutionBean = new ObjectResolutionBean();
        return objectResolutionBean;
    }

    public void setObjectResolutionBean(ObjectResolutionBean objectResolutionBean) {
        this.objectResolutionBean = objectResolutionBean;
    }

}