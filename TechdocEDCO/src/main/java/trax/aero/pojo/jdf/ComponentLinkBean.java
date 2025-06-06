package trax.aero.pojo.jdf;

import javax.xml.bind.annotation.XmlAttribute;


public class ComponentLinkBean {
    String combinedProcessIndex = "";
    String amount = "";
    String orientation = "";
    String usage = "";
    String rRef = "";
    String textContent;

    @XmlAttribute(name = "CombinedProcessIndex")
    public String getCombinedProcessIndex() {
        return combinedProcessIndex;
    }

    public void setCombinedProcessIndex(String combinedProcessIndex) {
        this.combinedProcessIndex = combinedProcessIndex;
    }

    @XmlAttribute(name = "Orientation")
    public String getOrientation() {
        return orientation;
    }

    public void setOrientation(String orientation) {
        this.orientation = orientation;
    }

    @XmlAttribute(name = "Usage")
    public String getUsage() {
        return usage;
    }

    public void setUsage(String usage) {
        this.usage = usage;
    }

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

    @XmlAttribute(name = "Amount")
    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

}