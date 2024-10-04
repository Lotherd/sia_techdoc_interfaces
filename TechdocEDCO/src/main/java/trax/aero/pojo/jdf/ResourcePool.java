package trax.aero.pojo.jdf;

import java.util.ArrayList;

public class ResourcePool {

	CustomerInfo CustomerInfoObject;
	
	RunList RunListObject;
	
	LayoutElement LayoutElementObject;
	
	LayoutPreparationParams LayoutPreparationParamsObject;
	
	InterpretingParams InterpretingParamsObject;
	
	RenderingParams RenderingParamsObject;
	
	ScreeningParams ScreeningParamsObject;
	
	DigitalPrintingParams DigitalPrintingParamsObject;
	
	ArrayList < Component > Component = new ArrayList < Component > ();
	
	MailboxDetails MailboxDetailsObject;
	
	HoleMakingParams HoleMakingParamsObject;
	
	FoldingParams FoldingParamsObject;
	
	StitchingParams StitchingParamsObject;
	
	Media MediaObject;
	
	Contact ContactObject;


	 // Getter Methods 

	 public CustomerInfo getCustomerInfo() {
	  return CustomerInfoObject;
	 }

	 public RunList getRunList() {
	  return RunListObject;
	 }

	 public LayoutElement getLayoutElement() {
	  return LayoutElementObject;
	 }

	 public LayoutPreparationParams getLayoutPreparationParams() {
	  return LayoutPreparationParamsObject;
	 }

	 public InterpretingParams getInterpretingParams() {
	  return InterpretingParamsObject;
	 }

	 public RenderingParams getRenderingParams() {
	  return RenderingParamsObject;
	 }

	 public ScreeningParams getScreeningParams() {
	  return ScreeningParamsObject;
	 }

	 public DigitalPrintingParams getDigitalPrintingParams() {
	  return DigitalPrintingParamsObject;
	 }

	 public MailboxDetails getMailboxDetails() {
	  return MailboxDetailsObject;
	 }

	 public HoleMakingParams getHoleMakingParams() {
	  return HoleMakingParamsObject;
	 }

	 public FoldingParams getFoldingParams() {
	  return FoldingParamsObject;
	 }

	 public StitchingParams getStitchingParams() {
	  return StitchingParamsObject;
	 }

	 public Media getMedia() {
	  return MediaObject;
	 }

	 public Contact getContact() {
	  return ContactObject;
	 }

	 // Setter Methods 

	 public void setCustomerInfo(CustomerInfo CustomerInfoObject) {
	  this.CustomerInfoObject = CustomerInfoObject;
	 }

	 public void setRunList(RunList RunListObject) {
	  this.RunListObject = RunListObject;
	 }

	 public void setLayoutElement(LayoutElement LayoutElementObject) {
	  this.LayoutElementObject = LayoutElementObject;
	 }

	 public void setLayoutPreparationParams(LayoutPreparationParams LayoutPreparationParamsObject) {
	  this.LayoutPreparationParamsObject = LayoutPreparationParamsObject;
	 }

	 public void setInterpretingParams(InterpretingParams InterpretingParamsObject) {
	  this.InterpretingParamsObject = InterpretingParamsObject;
	 }

	 public void setRenderingParams(RenderingParams RenderingParamsObject) {
	  this.RenderingParamsObject = RenderingParamsObject;
	 }

	 public void setScreeningParams(ScreeningParams ScreeningParamsObject) {
	  this.ScreeningParamsObject = ScreeningParamsObject;
	 }

	 public void setDigitalPrintingParams(DigitalPrintingParams DigitalPrintingParamsObject) {
	  this.DigitalPrintingParamsObject = DigitalPrintingParamsObject;
	 }

	 public void setMailboxDetails(MailboxDetails MailboxDetailsObject) {
	  this.MailboxDetailsObject = MailboxDetailsObject;
	 }

	 public void setHoleMakingParams(HoleMakingParams HoleMakingParamsObject) {
	  this.HoleMakingParamsObject = HoleMakingParamsObject;
	 }

	 public void setFoldingParams(FoldingParams FoldingParamsObject) {
	  this.FoldingParamsObject = FoldingParamsObject;
	 }

	 public void setStitchingParams(StitchingParams StitchingParamsObject) {
	  this.StitchingParamsObject = StitchingParamsObject;
	 }

	 public void setMedia(Media MediaObject) {
	  this.MediaObject = MediaObject;
	 }

	 public void setContact(Contact ContactObject) {
	  this.ContactObject = ContactObject;
	 }
}
