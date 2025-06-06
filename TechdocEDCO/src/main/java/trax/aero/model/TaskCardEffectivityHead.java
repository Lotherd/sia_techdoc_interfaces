package trax.aero.model;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;


/**
 * The persistent class for the TASK_CARD_EFFECTIVITY_HEAD database table.
 */
@Entity
@Table(name = "TASK_CARD_EFFECTIVITY_HEAD")
@NamedQuery(name = "TaskCardEffectivityHead.findAll", query = "SELECT t FROM TaskCardEffectivityHead t")
public class TaskCardEffectivityHead implements Serializable {
    private static final long serialVersionUID = 1L;

    @EmbeddedId
    private TaskCardEffectivityHeadPK id;

    @Column(name = "BLOB_NO")
    private BigDecimal blobNo;

    @Column(name = "CREATED_BY")
    private String createdBy;

    @Temporal(TemporalType.DATE)
    @Column(name = "CREATED_DATE")
    private Date createdDate;

    @Column(name = "MODIFIED_BY")
    private String modifiedBy;

    @Temporal(TemporalType.DATE)
    @Column(name = "MODIFIED_DATE")
    private Date modifiedDate;

    private BigDecimal notes;

    private String override;

    @Column(name = "\"SELECT\"")
    private String select;

    //bi-directional many-to-one association to TaskCard
    @ManyToOne
    @JoinColumn(name = "TASK_CARD", insertable = false, updatable = false)
    private TaskCard taskCardBean;

    public TaskCardEffectivityHead() {
    }

    public TaskCardEffectivityHeadPK getId() {
        return this.id;
    }

    public void setId(TaskCardEffectivityHeadPK id) {
        this.id = id;
    }

    public BigDecimal getBlobNo() {
        return this.blobNo;
    }

    public void setBlobNo(BigDecimal blobNo) {
        this.blobNo = blobNo;
    }

    public String getCreatedBy() {
        return this.createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Date getCreatedDate() {
        return this.createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public String getModifiedBy() {
        return this.modifiedBy;
    }

    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    public Date getModifiedDate() {
        return this.modifiedDate;
    }

    public void setModifiedDate(Date modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    public BigDecimal getNotes() {
        return this.notes;
    }

    public void setNotes(BigDecimal notes) {
        this.notes = notes;
    }

    public String getOverride() {
        return this.override;
    }

    public void setOverride(String override) {
        this.override = override;
    }

    public String getSelect() {
        return this.select;
    }

    public void setSelect(String select) {
        this.select = select;
    }

    public TaskCard getTaskCardBean() {
        return this.taskCardBean;
    }

    public void setTaskCardBean(TaskCard taskCardBean) {
        this.taskCardBean = taskCardBean;
    }

}