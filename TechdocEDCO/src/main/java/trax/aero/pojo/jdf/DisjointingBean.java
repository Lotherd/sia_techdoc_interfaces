/*
* This source code file is the intellectual property of TRAX USA Corp.
* Copyright (c) 2025 TRAX USA Corp. All rights reserved.
*/
package trax.aero.pojo.jdf;

import javax.xml.bind.annotation.XmlAttribute;

public class DisjointingBean {
    String offsetDirection = "";
    String textContent;

    @XmlAttribute(name = "OffsetDirection")
    public String getOffsetDirection() {
        return offsetDirection;
    }

    public void setOffsetDirection(String offsetDirection) {
        this.offsetDirection = offsetDirection;
    }

    @XmlAttribute(name = "TextContent")
    public String getTextContent() {
        return textContent;
    }

    public void setTextContent(String textContent) {
        this.textContent = textContent;
    }
}
