/*
* This source code file is the intellectual property of TRAX USA Corp.
* Copyright (c) 2025 TRAX USA Corp. All rights reserved.
*/
package trax.aero.pojo.jdf;

import javax.xml.bind.annotation.XmlAttribute;

public class RenderingParamsLinkBean {
    String usage = "";
    String rRef = "";
    String textContent;

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
}
