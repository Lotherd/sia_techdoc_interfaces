package trax.aero.pojo.jdf;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

public class ResourcePoolBean {
  String textContent;
  ArrayList<ComponentBean> componentBean;
  ContactBean contactBean;
  CustomerInfoBean customerInfoBean;
  DigitalPrintingParamsBean digitalPrintingParamsBean;
  FoldingParamsBean foldingParamsBean;
  HoleMakingParamsBean holeMakingParamsBean;
  InterpretingParamsBean interpretingParamsBean;
  LayoutElementBean layoutElementBean;
  LayoutPreparationParamsBean layoutPreparationParamsBean;
  MailboxDetailsBean mailboxDetailsBean;
  MediaBean mediaBean;
  RenderingParamsBean renderingParamsBean;
  RunListBean runListBean;
  ScreeningParamsBean screeningParamsBean;
  StitchingParamsBean stitchingParamsBean;

  @XmlAttribute(name = "TextContent")
  public String getTextContent() {
    return textContent;
  }

  public void setTextContent(String textContent) {
    this.textContent = textContent;
  }

  @XmlElement(name = "Component")
  public List<ComponentBean> getComponentBean() {

    return componentBean;
  }

  public void setComponentBean(ArrayList<ComponentBean> componentBean) {
    this.componentBean = componentBean;
  }

  @XmlElement(name = "Contact")
  public ContactBean getContactBean() {
    if (contactBean == null) contactBean = new ContactBean();
    return contactBean;
  }

  public void setContactBean(ContactBean contactBean) {
    this.contactBean = contactBean;
  }

  @XmlElement(name = "CustomerInfo")
  public CustomerInfoBean getCustomerInfoBean() {
    if (customerInfoBean == null) customerInfoBean = new CustomerInfoBean();
    return customerInfoBean;
  }

  public void setCustomerInfoBean(CustomerInfoBean customerInfoBean) {
    this.customerInfoBean = customerInfoBean;
  }

  @XmlElement(name = "DigitalPrintingParams")
  public DigitalPrintingParamsBean getDigitalPrintingParamsBean() {
    if (digitalPrintingParamsBean == null)
      digitalPrintingParamsBean = new DigitalPrintingParamsBean();
    return digitalPrintingParamsBean;
  }

  public void setDigitalPrintingParamsBean(DigitalPrintingParamsBean digitalPrintingParamsBean) {
    this.digitalPrintingParamsBean = digitalPrintingParamsBean;
  }

  @XmlElement(name = "FoldingParams")
  public FoldingParamsBean getFoldingParamsBean() {
    if (foldingParamsBean == null) foldingParamsBean = new FoldingParamsBean();
    return foldingParamsBean;
  }

  public void setFoldingParamsBean(FoldingParamsBean foldingParamsBean) {
    this.foldingParamsBean = foldingParamsBean;
  }

  @XmlElement(name = "HoleMakingParams")
  public HoleMakingParamsBean getHoleMakingParamsBean() {
    if (holeMakingParamsBean == null) holeMakingParamsBean = new HoleMakingParamsBean();
    return holeMakingParamsBean;
  }

  public void setHoleMakingParamsBean(HoleMakingParamsBean holeMakingParamsBean) {
    this.holeMakingParamsBean = holeMakingParamsBean;
  }

  @XmlElement(name = "InterpretingParams")
  public InterpretingParamsBean getInterpretingParamsBean() {
    if (interpretingParamsBean == null) interpretingParamsBean = new InterpretingParamsBean();
    return interpretingParamsBean;
  }

  public void setInterpretingParamsBean(InterpretingParamsBean interpretingParamsBean) {
    this.interpretingParamsBean = interpretingParamsBean;
  }

  @XmlElement(name = "LayoutElement")
  public LayoutElementBean getLayoutElementBean() {
    if (layoutElementBean == null) layoutElementBean = new LayoutElementBean();
    return layoutElementBean;
  }

  public void setLayoutElementBean(LayoutElementBean layoutElementBean) {
    this.layoutElementBean = layoutElementBean;
  }

  @XmlElement(name = "LayoutPreparationParams")
  public LayoutPreparationParamsBean getLayoutPreparationParamsBean() {
    if (layoutPreparationParamsBean == null)
      layoutPreparationParamsBean = new LayoutPreparationParamsBean();
    return layoutPreparationParamsBean;
  }

  public void setLayoutPreparationParamsBean(
      LayoutPreparationParamsBean layoutPreparationParamsBean) {
    this.layoutPreparationParamsBean = layoutPreparationParamsBean;
  }

  @XmlElement(name = "MailboxDetails")
  public MailboxDetailsBean getMailboxDetailsBean() {
    if (mailboxDetailsBean == null) mailboxDetailsBean = new MailboxDetailsBean();
    return mailboxDetailsBean;
  }

  public void setMailboxDetailsBean(MailboxDetailsBean mailboxDetailsBean) {
    this.mailboxDetailsBean = mailboxDetailsBean;
  }

  @XmlElement(name = "Media")
  public MediaBean getMediaBean() {
    if (mediaBean == null) mediaBean = new MediaBean();
    return mediaBean;
  }

  public void setMediaBean(MediaBean mediaBean) {
    this.mediaBean = mediaBean;
  }

  @XmlElement(name = "RenderingParams")
  public RenderingParamsBean getRenderingParamsBean() {
    if (renderingParamsBean == null) renderingParamsBean = new RenderingParamsBean();
    return renderingParamsBean;
  }

  public void setRenderingParamsBean(RenderingParamsBean renderingParamsBean) {
    this.renderingParamsBean = renderingParamsBean;
  }

  @XmlElement(name = "RunList")
  public RunListBean getRunListBean() {
    if (runListBean == null) runListBean = new RunListBean();
    return runListBean;
  }

  public void setRunListBean(RunListBean runListBean) {
    this.runListBean = runListBean;
  }

  @XmlElement(name = "ScreeningParams")
  public ScreeningParamsBean getScreeningParamsBean() {
    if (screeningParamsBean == null) screeningParamsBean = new ScreeningParamsBean();
    return screeningParamsBean;
  }

  public void setScreeningParamsBean(ScreeningParamsBean screeningParamsBean) {
    this.screeningParamsBean = screeningParamsBean;
  }

  @XmlElement(name = "StitchingParams")
  public StitchingParamsBean getStitchingParamsBean() {
    if (stitchingParamsBean == null) stitchingParamsBean = new StitchingParamsBean();
    return stitchingParamsBean;
  }

  public void setStitchingParamsBean(StitchingParamsBean stitchingParamsBean) {
    this.stitchingParamsBean = stitchingParamsBean;
  }
}
