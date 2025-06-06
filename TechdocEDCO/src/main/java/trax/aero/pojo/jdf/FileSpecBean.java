package trax.aero.pojo.jdf;

import javax.xml.bind.annotation.XmlAttribute;


public class FileSpecBean {
    String mimeType = "";
    String uRL = "";
    String textContent;

    @XmlAttribute(name = "MimeType")
    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    @XmlAttribute(name = "URL")
    public String getURL() {
        return uRL;
    }

    public void setURL(String uRL) {
        this.uRL = uRL;
    }

    @XmlAttribute(name = "TextContent")
    public String getTextContent() {
        return textContent;
    }

    public void setTextContent(String textContent) {
        this.textContent = textContent;
    }

}