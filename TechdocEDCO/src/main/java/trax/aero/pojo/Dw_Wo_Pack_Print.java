/*
* This source code file is the intellectual property of TRAX USA Corp.
* Copyright (c) 2025 TRAX USA Corp. All rights reserved.
*/
package trax.aero.pojo;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "dw_wo_pack_print")
public class Dw_Wo_Pack_Print {

    @XmlElement(name = "row")
    private Row RowObject;

    // Getter Methods

    public Row getRow() {
        return RowObject;
    }

    // Setter Methods

    public void setRow(Row rowObject) {
        this.RowObject = rowObject;
    }
}
