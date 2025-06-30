/*
* This source code file is the intellectual property of TRAX USA Corp.
* Copyright (c) 2025 TRAX USA Corp. All rights reserved.
*/
package trax.aero.pojo.jdf;

import javax.xml.bind.annotation.XmlAttribute;

public class StitchingParamsBean {
    String Class = "";
    String iD = "";
    String numberOfStitches = "";
    String status = "";
    String stitchType = "";
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

    @XmlAttribute(name = "NumberOfStitches")
    public String getNumberOfStitches() {
        return numberOfStitches;
    }

    public void setNumberOfStitches(String numberOfStitches) {
        this.numberOfStitches = numberOfStitches;
    }

    @XmlAttribute(name = "Status")
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @XmlAttribute(name = "StitchType")
    public String getStitchType() {
        return stitchType;
    }

    public void setStitchType(String stitchType) {
        this.stitchType = stitchType;
    }

    @XmlAttribute(name = "TextContent")
    public String getTextContent() {
        return textContent;
    }

    public void setTextContent(String textContent) {
        this.textContent = textContent;
    }
}
