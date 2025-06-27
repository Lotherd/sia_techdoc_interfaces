package trax.aero.model;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Embeddable;

/** The primary key class for the TASK_CARD_EFFECTIVITY_HEAD database table. */
@Embeddable
public class TaskCardEffectivityHeadPK implements Serializable {
  // default serial version id, required for serializable classes.
  private static final long serialVersionUID = 1L;

  @Column(name = "TASK_CARD", insertable = false, updatable = false)
  private String taskCard;

  @Column(name = "AC_TYPE")
  private String acType;

  @Column(name = "AC_SERIES")
  private String acSeries;

  public TaskCardEffectivityHeadPK() {}

  public String getTaskCard() {
    return this.taskCard;
  }

  public void setTaskCard(String taskCard) {
    this.taskCard = taskCard;
  }

  public String getAcType() {
    return this.acType;
  }

  public void setAcType(String acType) {
    this.acType = acType;
  }

  public String getAcSeries() {
    return this.acSeries;
  }

  public void setAcSeries(String acSeries) {
    this.acSeries = acSeries;
  }

  public boolean equals(Object other) {
    if (this == other) {
      return true;
    }
    if (!(other instanceof TaskCardEffectivityHeadPK)) {
      return false;
    }
    TaskCardEffectivityHeadPK castOther = (TaskCardEffectivityHeadPK) other;
    return this.taskCard.equals(castOther.taskCard)
        && this.acType.equals(castOther.acType)
        && this.acSeries.equals(castOther.acSeries);
  }

  public int hashCode() {
    final int prime = 31;
    int hash = 17;
    hash = hash * prime + this.taskCard.hashCode();
    hash = hash * prime + this.acType.hashCode();
    hash = hash * prime + this.acSeries.hashCode();

    return hash;
  }
}
