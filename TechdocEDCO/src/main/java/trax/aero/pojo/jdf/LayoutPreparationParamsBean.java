package trax.aero.pojo.jdf;

import javax.xml.bind.annotation.XmlAttribute;


public class LayoutPreparationParamsBean {
    String runIndex = "";
    String sides = "";
    String textContent;

    @XmlAttribute(name = "RunIndex")
    public String getRunIndex() {
        return runIndex;
    }

    public void setRunIndex(String runIndex) {
        this.runIndex = runIndex;
    }

    @XmlAttribute(name = "Sides")
    public String getSides() {
        return sides;
    }

    public void setSides(String sides) {
        this.sides = sides;
    }

    @XmlAttribute(name = "TextContent")
    public String getTextContent() {
        return textContent;
    }

    public void setTextContent(String textContent) {
        this.textContent = textContent;
    }

}