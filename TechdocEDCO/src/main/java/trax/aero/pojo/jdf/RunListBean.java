/*
* This source code file is the intellectual property of TRAX USA Corp.
* Copyright (c) 2025 TRAX USA Corp. All rights reserved.
*/
package trax.aero.pojo.jdf;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

public class RunListBean {
    String Class = "";
    String iD = "";
    String status = "";
    String textContent;
    LayoutElementRefBean layoutElementRefBean;

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

    @XmlAttribute(name = "TextContent")
    public String getTextContent() {
        return textContent;
    }

    public void setTextContent(String textContent) {
        this.textContent = textContent;
    }

    @XmlElement(name = "LayoutElementRef")
    public LayoutElementRefBean getLayoutElementRefBean() {
        if (layoutElementRefBean == null) layoutElementRefBean = new LayoutElementRefBean();
        return layoutElementRefBean;
    }

    public void setLayoutElementRefBean(LayoutElementRefBean layoutElementRefBean) {
        this.layoutElementRefBean = layoutElementRefBean;
    }
}
