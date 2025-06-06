package trax.aero.pojo.jdf;

import javax.xml.bind.annotation.XmlAttribute;


public class MailboxDetailsBean {
    String Class = "";
    String iD = "";
    String status = "";
    String useMailbox = "";
    String textContent;

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

    @XmlAttribute(name = "UseMailbox")
    public String getUseMailbox() {
        return useMailbox;
    }

    public void setUseMailbox(String useMailbox) {
        this.useMailbox = useMailbox;
    }

    @XmlAttribute(name = "TextContent")
    public String getTextContent() {
        return textContent;
    }

    public void setTextContent(String textContent) {
        this.textContent = textContent;
    }

}