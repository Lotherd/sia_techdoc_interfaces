/*
* This source code file is the intellectual property of TRAX USA Corp.
* Copyright (c) 2025 TRAX USA Corp. All rights reserved.
*/
package trax.aero.pojo.jdf;

import javax.xml.bind.annotation.XmlAttribute;

public class MediaBean {
    String Class = "";
    String customMediaType = "";
    String descriptiveName = "";
    String dimension = "";
    String holeType = "";
    String iD = "";
    String mediaColorNameDetails = "";
    String mediaSetCount = "";
    String status = "";
    String weight = "";
    String textContent;

    public void setClass(String Class) {
        this.Class = Class;
    }

    @XmlAttribute(name = "Class")
    public String getClasS() {
        return Class;
    }

    @XmlAttribute(name = "CustomMediaType")
    public String getCustomMediaType() {
        return customMediaType;
    }

    public void setCustomMediaType(String customMediaType) {
        this.customMediaType = customMediaType;
    }

    @XmlAttribute(name = "DescriptiveName")
    public String getDescriptiveName() {
        return descriptiveName;
    }

    public void setDescriptiveName(String descriptiveName) {
        this.descriptiveName = descriptiveName;
    }

    @XmlAttribute(name = "Dimension")
    public String getDimension() {
        return dimension;
    }

    public void setDimension(String dimension) {
        this.dimension = dimension;
    }

    @XmlAttribute(name = "HoleType")
    public String getHoleType() {
        return holeType;
    }

    public void setHoleType(String holeType) {
        this.holeType = holeType;
    }

    @XmlAttribute(name = "ID")
    public String getID() {
        return iD;
    }

    public void setID(String iD) {
        this.iD = iD;
    }

    @XmlAttribute(name = "MediaColorNameDetails")
    public String getMediaColorNameDetails() {
        return mediaColorNameDetails;
    }

    public void setMediaColorNameDetails(String mediaColorNameDetails) {
        this.mediaColorNameDetails = mediaColorNameDetails;
    }

    @XmlAttribute(name = "MediaSetCount")
    public String getMediaSetCount() {
        return mediaSetCount;
    }

    public void setMediaSetCount(String mediaSetCount) {
        this.mediaSetCount = mediaSetCount;
    }

    @XmlAttribute(name = "Status")
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @XmlAttribute(name = "Weight")
    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    @XmlAttribute(name = "TextContent")
    public String getTextContent() {
        return textContent;
    }

    public void setTextContent(String textContent) {
        this.textContent = textContent;
    }
}
