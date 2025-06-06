package trax.aero.pojo.jdf;

import javax.xml.bind.annotation.XmlAttribute;


public class ContactRefBean {
    String rRef = "";
    String textContent;

    @XmlAttribute(name = "rRef")
    public String getRRef() {
        return rRef;
    }

    public void setRRef(String rRef) {
        this.rRef = rRef;
    }

    @XmlAttribute(name = "TextContent")
    public String getTextContent() {
        return textContent;
    }

    public void setTextContent(String textContent) {
        this.textContent = textContent;
    }

}