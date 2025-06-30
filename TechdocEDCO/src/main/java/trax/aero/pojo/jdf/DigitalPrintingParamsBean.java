/*
* This source code file is the intellectual property of TRAX USA Corp.
* Copyright (c) 2025 TRAX USA Corp. All rights reserved.
*/
package trax.aero.pojo.jdf;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

public class DigitalPrintingParamsBean {
    String runIndex = "";
    String textContent;
    MediaRefBean mediaRefBean;

    @XmlAttribute(name = "RunIndex")
    public String getRunIndex() {
        return runIndex;
    }

    public void setRunIndex(String runIndex) {
        this.runIndex = runIndex;
    }

    @XmlAttribute(name = "TextContent")
    public String getTextContent() {
        return textContent;
    }

    public void setTextContent(String textContent) {
        this.textContent = textContent;
    }

    @XmlElement(name = "MediaRef")
    public MediaRefBean getMediaRefBean() {
        if (mediaRefBean == null) mediaRefBean = new MediaRefBean();
        return mediaRefBean;
    }

    public void setMediaRefBean(MediaRefBean mediaRefBean) {
        this.mediaRefBean = mediaRefBean;
    }
}
