package trax.aero.pojo.jdf;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

@XmlAccessorType(XmlAccessType.FIELD)
public class ResourceLinkPool {

	CustomerInfoLink CustomerInfoLinkObject;
	 RunListLink RunListLinkObject;
	 MediaLink MediaLinkObject;
	 LayoutPreparationParamsLink LayoutPreparationParamsLinkObject;
	 InterpretingParamsLink InterpretingParamsLinkObject;
	 RenderingParamsLink RenderingParamsLinkObject;
	 ScreeningParamsLink ScreeningParamsLinkObject;
	 DigitalPrintingParamsLink DigitalPrintingParamsLinkObject;
	 MailboxDetailsLink MailboxDetailsLinkObject;
	 HoleMakingParamsLink HoleMakingParamsLinkObject;
	 FoldingParamsLink FoldingParamsLinkObject;
	 ArrayList < Object > ComponentLink = new ArrayList < Object > ();
	 StitchingParamsLink StitchingParamsLinkObject;


	 // Getter Methods 

	 public CustomerInfoLink getCustomerInfoLink() {
	  return CustomerInfoLinkObject;
	 }

	 public RunListLink getRunListLink() {
	  return RunListLinkObject;
	 }

	 public MediaLink getMediaLink() {
	  return MediaLinkObject;
	 }

	 public LayoutPreparationParamsLink getLayoutPreparationParamsLink() {
	  return LayoutPreparationParamsLinkObject;
	 }

	 public InterpretingParamsLink getInterpretingParamsLink() {
	  return InterpretingParamsLinkObject;
	 }

	 public RenderingParamsLink getRenderingParamsLink() {
	  return RenderingParamsLinkObject;
	 }

	 public ScreeningParamsLink getScreeningParamsLink() {
	  return ScreeningParamsLinkObject;
	 }

	 public DigitalPrintingParamsLink getDigitalPrintingParamsLink() {
	  return DigitalPrintingParamsLinkObject;
	 }

	 public MailboxDetailsLink getMailboxDetailsLink() {
	  return MailboxDetailsLinkObject;
	 }

	 public HoleMakingParamsLink getHoleMakingParamsLink() {
	  return HoleMakingParamsLinkObject;
	 }

	 public FoldingParamsLink getFoldingParamsLink() {
	  return FoldingParamsLinkObject;
	 }

	 public StitchingParamsLink getStitchingParamsLink() {
	  return StitchingParamsLinkObject;
	 }

	 // Setter Methods 

	 public void setCustomerInfoLink(CustomerInfoLink CustomerInfoLinkObject) {
	  this.CustomerInfoLinkObject = CustomerInfoLinkObject;
	 }

	 public void setRunListLink(RunListLink RunListLinkObject) {
	  this.RunListLinkObject = RunListLinkObject;
	 }

	 public void setMediaLink(MediaLink MediaLinkObject) {
	  this.MediaLinkObject = MediaLinkObject;
	 }

	 public void setLayoutPreparationParamsLink(LayoutPreparationParamsLink LayoutPreparationParamsLinkObject) {
	  this.LayoutPreparationParamsLinkObject = LayoutPreparationParamsLinkObject;
	 }

	 public void setInterpretingParamsLink(InterpretingParamsLink InterpretingParamsLinkObject) {
	  this.InterpretingParamsLinkObject = InterpretingParamsLinkObject;
	 }

	 public void setRenderingParamsLink(RenderingParamsLink RenderingParamsLinkObject) {
	  this.RenderingParamsLinkObject = RenderingParamsLinkObject;
	 }

	 public void setScreeningParamsLink(ScreeningParamsLink ScreeningParamsLinkObject) {
	  this.ScreeningParamsLinkObject = ScreeningParamsLinkObject;
	 }

	 public void setDigitalPrintingParamsLink(DigitalPrintingParamsLink DigitalPrintingParamsLinkObject) {
	  this.DigitalPrintingParamsLinkObject = DigitalPrintingParamsLinkObject;
	 }

	 public void setMailboxDetailsLink(MailboxDetailsLink MailboxDetailsLinkObject) {
	  this.MailboxDetailsLinkObject = MailboxDetailsLinkObject;
	 }

	 public void setHoleMakingParamsLink(HoleMakingParamsLink HoleMakingParamsLinkObject) {
	  this.HoleMakingParamsLinkObject = HoleMakingParamsLinkObject;
	 }

	 public void setFoldingParamsLink(FoldingParamsLink FoldingParamsLinkObject) {
	  this.FoldingParamsLinkObject = FoldingParamsLinkObject;
	 }

	 public void setStitchingParamsLink(StitchingParamsLink StitchingParamsLinkObject) {
	  this.StitchingParamsLinkObject = StitchingParamsLinkObject;
	 }
}
