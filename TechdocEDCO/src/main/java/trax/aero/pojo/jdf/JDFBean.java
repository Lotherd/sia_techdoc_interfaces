/*
* This source code file is the intellectual property of TRAX USA Corp.
* Copyright (c) 2025 TRAX USA Corp. All rights reserved.
*/
package trax.aero.pojo.jdf;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "JDF")
public class JDFBean {
    String activation = "";
    String category = "";
    String descriptiveName = "";
    String iD = "";
    String status = "";
    String type = "";
    String types = "";
    String version = "";
    String textContent;
    AuditPoolBean auditPoolBean;
    CommentBean commentBean;
    ResourceLinkPoolBean resourceLinkPoolBean;
    ResourcePoolBean resourcePoolBean;

    @XmlAttribute(name = "Activation")
    public String getActivation() {
        return activation;
    }

    public void setActivation(String activation) {
        this.activation = activation;
    }

    @XmlAttribute(name = "Category")
    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    @XmlAttribute(name = "DescriptiveName")
    public String getDescriptiveName() {
        return descriptiveName;
    }

    public void setDescriptiveName(String descriptiveName) {
        this.descriptiveName = descriptiveName;
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

    @XmlAttribute(name = "Type")
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @XmlAttribute(name = "Types")
    public String getTypes() {
        return types;
    }

    public void setTypes(String types) {
        this.types = types;
    }

    @XmlAttribute(name = "Version")
    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    @XmlAttribute(name = "TextContent")
    public String getTextContent() {
        return textContent;
    }

    public void setTextContent(String textContent) {
        this.textContent = textContent;
    }

    @XmlElement(name = "AuditPool")
    public AuditPoolBean getAuditPoolBean() {
        if (auditPoolBean == null) auditPoolBean = new AuditPoolBean();
        return auditPoolBean;
    }

    public void setAuditPoolBean(AuditPoolBean auditPoolBean) {
        this.auditPoolBean = auditPoolBean;
    }

    @XmlElement(name = "Comment")
    public CommentBean getCommentBean() {
        if (commentBean == null) commentBean = new CommentBean();
        return commentBean;
    }

    public void setCommentBean(CommentBean commentBean) {
        this.commentBean = commentBean;
    }

    @XmlElement(name = "ResourceLinkPool")
    public ResourceLinkPoolBean getResourceLinkPoolBean() {
        if (resourceLinkPoolBean == null) resourceLinkPoolBean = new ResourceLinkPoolBean();
        return resourceLinkPoolBean;
    }

    public void setResourceLinkPoolBean(ResourceLinkPoolBean resourceLinkPoolBean) {
        this.resourceLinkPoolBean = resourceLinkPoolBean;
    }

    @XmlElement(name = "ResourcePool")
    public ResourcePoolBean getResourcePoolBean() {
        if (resourcePoolBean == null) resourcePoolBean = new ResourcePoolBean();
        return resourcePoolBean;
    }

    public void setResourcePoolBean(ResourcePoolBean resourcePoolBean) {
        this.resourcePoolBean = resourcePoolBean;
    }
}
