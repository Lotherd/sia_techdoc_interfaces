package trax.aero.pojo.jdf;

import java.sql.Date;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.xml.bind.annotation.*;

import com.fasterxml.jackson.annotation.*;


public class ResourceLinkPoolBean {
    String textContent;
    ArrayList<ComponentLinkBean> componentLinkBean;
    CustomerInfoLinkBean customerInfoLinkBean;
    DigitalPrintingParamsLinkBean digitalPrintingParamsLinkBean;
    FoldingParamsLinkBean foldingParamsLinkBean;
    HoleMakingParamsLinkBean holeMakingParamsLinkBean;
    InterpretingParamsLinkBean interpretingParamsLinkBean;
    LayoutPreparationParamsLinkBean layoutPreparationParamsLinkBean;
    MailboxDetailsLinkBean mailboxDetailsLinkBean;
    MediaLinkBean mediaLinkBean;
    RenderingParamsLinkBean renderingParamsLinkBean;
    RunListLinkBean runListLinkBean;
    ScreeningParamsLinkBean screeningParamsLinkBean;
    StitchingParamsLinkBean stitchingParamsLinkBean;

    @XmlAttribute(name = "TextContent")
    public String getTextContent() {
        return textContent;
    }

    public void setTextContent(String textContent) {
        this.textContent = textContent;
    }

    @XmlElement(name = "ComponentLink")
    public ArrayList<ComponentLinkBean> getComponentLinkBean() {

        return componentLinkBean;
    }

    public void setComponentLinkBean(ArrayList<ComponentLinkBean> componentLinkBean) {
        this.componentLinkBean = componentLinkBean;
    }

    @XmlElement(name = "CustomerInfoLink")
    public CustomerInfoLinkBean getCustomerInfoLinkBean() {
        if (customerInfoLinkBean == null) customerInfoLinkBean = new CustomerInfoLinkBean();
        return customerInfoLinkBean;
    }

    public void setCustomerInfoLinkBean(CustomerInfoLinkBean customerInfoLinkBean) {
        this.customerInfoLinkBean = customerInfoLinkBean;
    }

    @XmlElement(name = "DigitalPrintingParamsLink")
    public DigitalPrintingParamsLinkBean getDigitalPrintingParamsLinkBean() {
        if (digitalPrintingParamsLinkBean == null) digitalPrintingParamsLinkBean = new DigitalPrintingParamsLinkBean();
        return digitalPrintingParamsLinkBean;
    }

    public void setDigitalPrintingParamsLinkBean(DigitalPrintingParamsLinkBean digitalPrintingParamsLinkBean) {
        this.digitalPrintingParamsLinkBean = digitalPrintingParamsLinkBean;
    }

    @XmlElement(name = "FoldingParamsLink")
    public FoldingParamsLinkBean getFoldingParamsLinkBean() {
        if (foldingParamsLinkBean == null) foldingParamsLinkBean = new FoldingParamsLinkBean();
        return foldingParamsLinkBean;
    }

    public void setFoldingParamsLinkBean(FoldingParamsLinkBean foldingParamsLinkBean) {
        this.foldingParamsLinkBean = foldingParamsLinkBean;
    }

    @XmlElement(name = "HoleMakingParamsLink")
    public HoleMakingParamsLinkBean getHoleMakingParamsLinkBean() {
        if (holeMakingParamsLinkBean == null) holeMakingParamsLinkBean = new HoleMakingParamsLinkBean();
        return holeMakingParamsLinkBean;
    }

    public void setHoleMakingParamsLinkBean(HoleMakingParamsLinkBean holeMakingParamsLinkBean) {
        this.holeMakingParamsLinkBean = holeMakingParamsLinkBean;
    }

    @XmlElement(name = "InterpretingParamsLink")
    public InterpretingParamsLinkBean getInterpretingParamsLinkBean() {
        if (interpretingParamsLinkBean == null) interpretingParamsLinkBean = new InterpretingParamsLinkBean();
        return interpretingParamsLinkBean;
    }

    public void setInterpretingParamsLinkBean(InterpretingParamsLinkBean interpretingParamsLinkBean) {
        this.interpretingParamsLinkBean = interpretingParamsLinkBean;
    }

    @XmlElement(name = "LayoutPreparationParamsLink")
    public LayoutPreparationParamsLinkBean getLayoutPreparationParamsLinkBean() {
        if (layoutPreparationParamsLinkBean == null)
            layoutPreparationParamsLinkBean = new LayoutPreparationParamsLinkBean();
        return layoutPreparationParamsLinkBean;
    }

    public void setLayoutPreparationParamsLinkBean(LayoutPreparationParamsLinkBean layoutPreparationParamsLinkBean) {
        this.layoutPreparationParamsLinkBean = layoutPreparationParamsLinkBean;
    }

    @XmlElement(name = "MailboxDetailsLink")
    public MailboxDetailsLinkBean getMailboxDetailsLinkBean() {
        if (mailboxDetailsLinkBean == null) mailboxDetailsLinkBean = new MailboxDetailsLinkBean();
        return mailboxDetailsLinkBean;
    }

    public void setMailboxDetailsLinkBean(MailboxDetailsLinkBean mailboxDetailsLinkBean) {
        this.mailboxDetailsLinkBean = mailboxDetailsLinkBean;
    }

    @XmlElement(name = "MediaLink")
    public MediaLinkBean getMediaLinkBean() {
        if (mediaLinkBean == null) mediaLinkBean = new MediaLinkBean();
        return mediaLinkBean;
    }

    public void setMediaLinkBean(MediaLinkBean mediaLinkBean) {
        this.mediaLinkBean = mediaLinkBean;
    }

    @XmlElement(name = "RenderingParamsLink")
    public RenderingParamsLinkBean getRenderingParamsLinkBean() {
        if (renderingParamsLinkBean == null) renderingParamsLinkBean = new RenderingParamsLinkBean();
        return renderingParamsLinkBean;
    }

    public void setRenderingParamsLinkBean(RenderingParamsLinkBean renderingParamsLinkBean) {
        this.renderingParamsLinkBean = renderingParamsLinkBean;
    }

    @XmlElement(name = "RunListLink")
    public RunListLinkBean getRunListLinkBean() {
        if (runListLinkBean == null) runListLinkBean = new RunListLinkBean();
        return runListLinkBean;
    }

    public void setRunListLinkBean(RunListLinkBean runListLinkBean) {
        this.runListLinkBean = runListLinkBean;
    }

    @XmlElement(name = "ScreeningParamsLink")
    public ScreeningParamsLinkBean getScreeningParamsLinkBean() {
        if (screeningParamsLinkBean == null) screeningParamsLinkBean = new ScreeningParamsLinkBean();
        return screeningParamsLinkBean;
    }

    public void setScreeningParamsLinkBean(ScreeningParamsLinkBean screeningParamsLinkBean) {
        this.screeningParamsLinkBean = screeningParamsLinkBean;
    }

    @XmlElement(name = "StitchingParamsLink")
    public StitchingParamsLinkBean getStitchingParamsLinkBean() {
        if (stitchingParamsLinkBean == null) stitchingParamsLinkBean = new StitchingParamsLinkBean();
        return stitchingParamsLinkBean;
    }

    public void setStitchingParamsLinkBean(StitchingParamsLinkBean stitchingParamsLinkBean) {
        this.stitchingParamsLinkBean = stitchingParamsLinkBean;
    }

}