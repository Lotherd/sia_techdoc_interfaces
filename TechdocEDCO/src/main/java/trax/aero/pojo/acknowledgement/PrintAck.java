/*
* This source code file is the intellectual property of TRAX USA Corp.
* Copyright (c) 2025 TRAX USA Corp. All rights reserved.
*/
package trax.aero.pojo.acknowledgement;

public class PrintAck {

    private String sapServiceOrder;
    private String jobcardNumber;
    private String jcTitle;
    private String sapGroupNumber;
    private String taskType;
    private String traxWoNumber;
    private String attachment;
    private String attachmentID;

    public String getSapServiceOrder() {
        return sapServiceOrder;
    }

    public void setSapServiceOrder(String sapServiceOrder) {
        this.sapServiceOrder = sapServiceOrder;
    }

    public String getJobcardNumber() {
        return jobcardNumber;
    }

    public void setJobcardNumber(String jobcardNumber) {
        this.jobcardNumber = jobcardNumber;
    }

    public String getJcTitle() {
        return jcTitle;
    }

    public void setJcTitle(String jcTitle) {
        this.jcTitle = jcTitle;
    }

    public String getSapGroupNumber() {
        return sapGroupNumber;
    }

    public void setSapGroupNumber(String sapGroupNumber) {
        this.sapGroupNumber = sapGroupNumber;
    }

    public String getTaskType() {
        return taskType;
    }

    public void setTaskType(String taskType) {
        this.taskType = taskType;
    }

    public String getTraxWoNumber() {
        return traxWoNumber;
    }

    public void setTraxWoNumber(String traxWoNumber) {
        this.traxWoNumber = traxWoNumber;
    }

    public String getAttachment() {
        return attachment;
    }

    public void setAttachment(String attachment) {
        this.attachment = attachment;
    }

    public String getAttachmentID() {
        return attachmentID;
    }

    public void setAttachmentID(String attachmentID) {
        this.attachmentID = attachmentID;
    }
}
