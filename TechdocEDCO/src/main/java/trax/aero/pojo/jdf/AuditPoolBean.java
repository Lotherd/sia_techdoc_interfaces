package trax.aero.pojo.jdf;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;


public class AuditPoolBean {
    String textContent;
    CreatedBean createdBean;

    @XmlAttribute(name = "TextContent")
    public String getTextContent() {
        return textContent;
    }

    public void setTextContent(String textContent) {
        this.textContent = textContent;
    }

    @XmlElement(name = "Created")
    public CreatedBean getCreatedBean() {
        if (createdBean == null) createdBean = new CreatedBean();
        return createdBean;
    }

    public void setCreatedBean(CreatedBean createdBean) {
        this.createdBean = createdBean;
    }

}