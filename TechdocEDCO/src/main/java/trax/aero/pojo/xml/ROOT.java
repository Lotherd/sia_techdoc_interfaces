/*
* This source code file is the intellectual property of TRAX USA Corp.
* Copyright (c) 2025 TRAX USA Corp. All rights reserved.
*/
package trax.aero.pojo.xml;

import java.util.ArrayList;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "ROOT")
public class ROOT {

    private ArrayList<MODEL> MODELS;

    @XmlElement(name = "MODEL")
    public ArrayList<MODEL> getMODELS() {
        return MODELS;
    }

    public void setMODELS(ArrayList<MODEL> mODELS) {
        MODELS = mODELS;
    }
}
