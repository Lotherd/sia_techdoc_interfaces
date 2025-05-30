package trax.aero.pojo.acknowledgement;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "JOBCARD")
@XmlAccessorType(XmlAccessType.FIELD)
public class JOBCARD {

    @XmlAttribute(name = "WP-NBR")
    private String wpnbr;

    @XmlAttribute(name = "SEQ-NBR")
    private String seqnbr;

    @XmlAttribute(name = "LOGIN-NAME")
    private String loginname;

    @XmlAttribute(name = "IDOC-DATE")
    private String idocdate;

    @XmlAttribute(name = "IDOC-TIME")
    private String idoctime;

    @XmlAttribute(name = "STATUS")
    private String status;

    @XmlAttribute(name = "REMARKS")
    private String remarks;

    public String getWpnbr() {
        return wpnbr;
    }

    public void setWpnbr(String wpnbr) {
        this.wpnbr = wpnbr;
    }

    public String getSeqnbr() {
        return seqnbr;
    }

    public void setSeqnbr(String seqnbr) {
        this.seqnbr = seqnbr;
    }

    public String getLoginname() {
        return loginname;
    }

    public void setLoginname(String loginname) {
        this.loginname = loginname;
    }

    public String getIdocdate() {
        return idocdate;
    }

    public void setIdocdate(String idocdate) {
        this.idocdate = idocdate;
    }

    public String getIdoctime() {
        return idoctime;
    }

    public void setIdoctime(String idoctime) {
        this.idoctime = idoctime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
