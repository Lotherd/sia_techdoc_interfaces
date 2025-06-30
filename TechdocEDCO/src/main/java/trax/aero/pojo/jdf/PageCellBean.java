/*
* This source code file is the intellectual property of TRAX USA Corp.
* Copyright (c) 2025 TRAX USA Corp. All rights reserved.
*/
package trax.aero.pojo.jdf;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

public class PageCellBean {
    String textContent;
    ImageShiftBean imageShiftBean;

    @XmlAttribute(name = "TextContent")
    public String getTextContent() {
        return textContent;
    }

    public void setTextContent(String textContent) {
        this.textContent = textContent;
    }

    @XmlElement(name = "ImageShift")
    public ImageShiftBean getImageShiftBean() {
        if (imageShiftBean == null) imageShiftBean = new ImageShiftBean();
        return imageShiftBean;
    }

    public void setImageShiftBean(ImageShiftBean imageShiftBean) {
        this.imageShiftBean = imageShiftBean;
    }
}
