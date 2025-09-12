/*
* This source code file is the intellectual property of TRAX USA Corp.
* Copyright (c) 2025 TRAX USA Corp. All rights reserved.
*/
package trax.aero.utilities;

import java.util.ArrayList;
import trax.aero.pojo.jdf.*;

public class JdfUtilities {

    public String createJdf(String fileName) {
        JDFBean jdf = getJdfBean(fileName);

        // DONE
        jdf.getCommentBean().setName("oce:TicketVersion");
        jdf.getCommentBean().setComment("4.00");

        jdf.getResourcePoolBean().setContactBean(new ContactBean());
        jdf.getResourcePoolBean().getContactBean().setClass("Parameter");
        jdf.getResourcePoolBean().getContactBean().setComChannelBean(new ComChannelBean());
        jdf.getResourcePoolBean().getContactBean().getComChannelBean().setChannelType("ComputerName");
        jdf.getResourcePoolBean().getContactBean().getComChannelBean().setLocator("NB10832");
        jdf.getResourcePoolBean().getContactBean().setContactTypes("Owner Delivery");
        jdf.getResourcePoolBean().getContactBean().setID("contact_1");
        jdf.getResourcePoolBean().getContactBean().setStatus("Available");
        jdf.getResourcePoolBean().getContactBean().setUserID("LA04533");

        jdf.getResourcePoolBean().setCustomerInfoBean(new CustomerInfoBean());
        jdf.getResourcePoolBean().getCustomerInfoBean().setClass("Parameter");
        jdf.getResourcePoolBean().getCustomerInfoBean().setContactRefBean(new ContactRefBean());
        jdf.getResourcePoolBean().getCustomerInfoBean().getContactRefBean().setRRef("contact_1");
        jdf.getResourcePoolBean()
                .getCustomerInfoBean()
                .setCustomerJobName("jc684990001697300773176947073.ps");
        jdf.getResourcePoolBean().getCustomerInfoBean().setID("info_1");
        jdf.getResourcePoolBean().getCustomerInfoBean().setStatus("Available");

        jdf.getResourcePoolBean().setDigitalPrintingParamsBean(new DigitalPrintingParamsBean());
        jdf.getResourcePoolBean().getDigitalPrintingParamsBean().setMediaRefBean(new MediaRefBean());
        jdf.getResourcePoolBean().getDigitalPrintingParamsBean().getMediaRefBean().setRRef("04 - ");
        jdf.getResourcePoolBean().getDigitalPrintingParamsBean().setRunIndex("0 ~ -1");
        jdf.getResourcePoolBean()
                .getDigitalPrintingParamsBean()
                .setOutputBin("StackerStapler_LowerTray");

        jdf.getResourcePoolBean().setFoldingParamsBean(new FoldingParamsBean());
        jdf.getResourcePoolBean().getFoldingParamsBean().setClass("Parameter");
        jdf.getResourcePoolBean().getFoldingParamsBean().setID("ProcessID_8");
        jdf.getResourcePoolBean().getFoldingParamsBean().setNoOp("true");
        jdf.getResourcePoolBean().getFoldingParamsBean().setStatus("Available");

        jdf.getResourcePoolBean().setHoleMakingParamsBean(new HoleMakingParamsBean());
        jdf.getResourcePoolBean().getHoleMakingParamsBean().setClass("Parameter");
        jdf.getResourcePoolBean().getHoleMakingParamsBean().setID("ProcessID_7");
        jdf.getResourcePoolBean().getHoleMakingParamsBean().setNoOp("true");
        jdf.getResourcePoolBean().getHoleMakingParamsBean().setStatus("Available");

        jdf.getResourcePoolBean().setInterpretingParamsBean(new InterpretingParamsBean());
        jdf.getResourcePoolBean().getInterpretingParamsBean().setClass("Parameter");
        jdf.getResourcePoolBean().getInterpretingParamsBean().setID("ProcessID_2");
        jdf.getResourcePoolBean().getInterpretingParamsBean().setStatus("Available");

        jdf.getResourcePoolBean().setLayoutElementBean(new LayoutElementBean());
        jdf.getResourcePoolBean().getLayoutElementBean().setClass("Parameter");
        jdf.getResourcePoolBean().getLayoutElementBean().setFileSpecBean(new FileSpecBean());
        jdf.getResourcePoolBean().getLayoutElementBean().getFileSpecBean().setMimeType("postscript");
        jdf.getResourcePoolBean()
                .getLayoutElementBean()
                .getFileSpecBean()
                .setURL("cid:doc@hostname.com");
        jdf.getResourcePoolBean().getLayoutElementBean().setID("file_1");
        jdf.getResourcePoolBean().getLayoutElementBean().setStatus("Available");

        jdf.getResourcePoolBean().setLayoutPreparationParamsBean(new LayoutPreparationParamsBean());
        jdf.getResourcePoolBean().getLayoutPreparationParamsBean().setRunIndex("0 ~ -1");
        jdf.getResourcePoolBean().getLayoutPreparationParamsBean().setSides("OneSidedFront");

        jdf.getResourcePoolBean().setMailboxDetailsBean(new MailboxDetailsBean());
        jdf.getResourcePoolBean().getMailboxDetailsBean().setClass("Parameter");
        jdf.getResourcePoolBean().getMailboxDetailsBean().setID("ProcessID_6");
        jdf.getResourcePoolBean().getMailboxDetailsBean().setStatus("Available");
        jdf.getResourcePoolBean().getMailboxDetailsBean().setUseMailbox("false");

        jdf.getResourcePoolBean().setMediaBean(new MediaBean());
        jdf.getResourcePoolBean().getMediaBean().setClass("Consumable");
        jdf.getResourcePoolBean().getMediaBean().setCustomMediaType("Plain paper");
        jdf.getResourcePoolBean().getMediaBean().setDescriptiveName("");
        jdf.getResourcePoolBean().getMediaBean().setDimension("595.3 841.9");
        jdf.getResourcePoolBean().getMediaBean().setHoleType("None");
        jdf.getResourcePoolBean().getMediaBean().setID("04 - ");
        jdf.getResourcePoolBean().getMediaBean().setMediaColorNameDetails("White");
        jdf.getResourcePoolBean().getMediaBean().setMediaSetCount("1");
        jdf.getResourcePoolBean().getMediaBean().setStatus("Available");
        jdf.getResourcePoolBean().getMediaBean().setWeight("80");
        jdf.getResourcePoolBean().getMediaBean().setMediaSetCount("1");

        jdf.getResourcePoolBean().setRenderingParamsBean(new RenderingParamsBean());
        jdf.getResourcePoolBean().getRenderingParamsBean().setClass("Parameter");
        jdf.getResourcePoolBean().getRenderingParamsBean().setID("ProcessID_3");
        jdf.getResourcePoolBean()
                .getRenderingParamsBean()
                .setObjectResolutionBean(new ObjectResolutionBean());
        jdf.getResourcePoolBean()
                .getRenderingParamsBean()
                .getObjectResolutionBean()
                .setResolution("600 2400");
        jdf.getResourcePoolBean().getRenderingParamsBean().setStatus("Available");

        jdf.getResourcePoolBean().setRunListBean(new RunListBean());
        jdf.getResourcePoolBean().getRunListBean().setClass("Parameter");
        jdf.getResourcePoolBean().getRunListBean().setID("runlist_id");
        jdf.getResourcePoolBean().getRunListBean().setLayoutElementRefBean(new LayoutElementRefBean());
        jdf.getResourcePoolBean().getRunListBean().getLayoutElementRefBean().setRRef("file_1");
        jdf.getResourcePoolBean().getRunListBean().setStatus("Available");

        jdf.getResourcePoolBean().setScreeningParamsBean(new ScreeningParamsBean());
        jdf.getResourcePoolBean().getScreeningParamsBean().setClass("Parameter");
        jdf.getResourcePoolBean().getScreeningParamsBean().setID("ProcessID_4");
        jdf.getResourcePoolBean()
                .getScreeningParamsBean()
                .setScreenSelectorBean(new ScreenSelectorBean());
        jdf.getResourcePoolBean().getScreeningParamsBean().getScreenSelectorBean().setFrequency("200");
        jdf.getResourcePoolBean()
                .getScreeningParamsBean()
                .getScreenSelectorBean()
                .setScreeningType("AM");
        jdf.getResourcePoolBean()
                .getScreeningParamsBean()
                .getScreenSelectorBean()
                .setSpotFunction("Line");
        jdf.getResourcePoolBean().getScreeningParamsBean().setStatus("Incomplete");

        jdf.getResourcePoolBean().setStitchingParamsBean(new StitchingParamsBean());
        jdf.getResourcePoolBean().getStitchingParamsBean().setClass("Parameter");
        jdf.getResourcePoolBean().getStitchingParamsBean().setID("ProcessID_8");
        jdf.getResourcePoolBean().getStitchingParamsBean().setNumberOfStitches("1");
        jdf.getResourcePoolBean().getStitchingParamsBean().setStitchType("Corner");
        jdf.getResourcePoolBean().getStitchingParamsBean().setStatus("Available");

        jdf.getResourcePoolBean().setComponentBean(new ArrayList<>());
        jdf.getResourcePoolBean().getComponentBean().add(new ComponentBean());
        jdf.getResourcePoolBean().getComponentBean().get(0).setClass("Quantity");
        jdf.getResourcePoolBean().getComponentBean().get(0).setComponentType("PartialProduct");
        jdf.getResourcePoolBean().getComponentBean().get(0).setID("Product_5");
        jdf.getResourcePoolBean().getComponentBean().get(0).setStatus("Unavailable");

        jdf.getResourcePoolBean().getComponentBean().add(new ComponentBean());
        jdf.getResourcePoolBean().getComponentBean().get(1).setClass("Quantity");
        jdf.getResourcePoolBean().getComponentBean().get(1).setComponentType("PartialProduct");
        jdf.getResourcePoolBean().getComponentBean().get(1).setID("Product_6");
        jdf.getResourcePoolBean().getComponentBean().get(1).setStatus("Unavailable");

        jdf.getResourcePoolBean().getComponentBean().add(new ComponentBean());
        jdf.getResourcePoolBean().getComponentBean().get(2).setClass("Quantity");
        jdf.getResourcePoolBean().getComponentBean().get(2).setComponentType("PartialProduct");
        jdf.getResourcePoolBean().getComponentBean().get(2).setID("Product_7");
        jdf.getResourcePoolBean().getComponentBean().get(2).setStatus("Unavailable");

        jdf.getResourcePoolBean().getComponentBean().add(new ComponentBean());
        jdf.getResourcePoolBean().getComponentBean().get(3).setClass("Quantity");
        jdf.getResourcePoolBean().getComponentBean().get(3).setComponentType("PartialProduct");
        jdf.getResourcePoolBean().getComponentBean().get(3).setID("Product_8");
        jdf.getResourcePoolBean().getComponentBean().get(3).setStatus("Unavailable");

        jdf.getResourcePoolBean().getComponentBean().add(new ComponentBean());
        jdf.getResourcePoolBean().getComponentBean().get(4).setClass("Quantity");
        jdf.getResourcePoolBean().getComponentBean().get(4).setComponentType("PartialProduct");
        jdf.getResourcePoolBean().getComponentBean().get(4).setID("Product_9");
        jdf.getResourcePoolBean().getComponentBean().get(4).setStatus("Unavailable");

        jdf.getResourcePoolBean().getComponentBean().add(new ComponentBean());
        jdf.getResourcePoolBean().getComponentBean().get(5).setClass("Quantity");
        jdf.getResourcePoolBean().getComponentBean().get(5).setComponentType("FinalProduct");
        jdf.getResourcePoolBean().getComponentBean().get(5).setID("Product_Final");
        jdf.getResourcePoolBean().getComponentBean().get(5).setStatus("Unavailable");

        // DONE
        jdf.getResourceLinkPoolBean().setCustomerInfoLinkBean(new CustomerInfoLinkBean());
        jdf.getResourceLinkPoolBean().getCustomerInfoLinkBean().setRRef("info_1");
        jdf.getResourceLinkPoolBean().getCustomerInfoLinkBean().setUsage("Input");

        jdf.getResourceLinkPoolBean()
                .setDigitalPrintingParamsLinkBean(new DigitalPrintingParamsLinkBean());
        jdf.getResourceLinkPoolBean().getDigitalPrintingParamsLinkBean().setUsage("Input");
        jdf.getResourceLinkPoolBean().getDigitalPrintingParamsLinkBean().setRRef("ProcessID_5");

        jdf.getResourceLinkPoolBean().setFoldingParamsLinkBean(new FoldingParamsLinkBean());
        jdf.getResourceLinkPoolBean().getFoldingParamsLinkBean().setUsage("Input");
        jdf.getResourceLinkPoolBean().getFoldingParamsLinkBean().setRRef("ProcessID_8");

        jdf.getResourceLinkPoolBean().setHoleMakingParamsLinkBean(new HoleMakingParamsLinkBean());
        jdf.getResourceLinkPoolBean().getHoleMakingParamsLinkBean().setUsage("Input");
        jdf.getResourceLinkPoolBean().getHoleMakingParamsLinkBean().setRRef("ProcessID_7");

        jdf.getResourceLinkPoolBean().setInterpretingParamsLinkBean(new InterpretingParamsLinkBean());
        jdf.getResourceLinkPoolBean().getInterpretingParamsLinkBean().setRRef("ProcessID_2");
        jdf.getResourceLinkPoolBean().getInterpretingParamsLinkBean().setUsage("Input");

        jdf.getResourceLinkPoolBean()
                .setLayoutPreparationParamsLinkBean(new LayoutPreparationParamsLinkBean());
        jdf.getResourceLinkPoolBean().getLayoutPreparationParamsLinkBean().setRRef("ProcessID_0");
        jdf.getResourceLinkPoolBean().getLayoutPreparationParamsLinkBean().setUsage("Input");

        jdf.getResourceLinkPoolBean().setMailboxDetailsLinkBean(new MailboxDetailsLinkBean());
        jdf.getResourceLinkPoolBean().getMailboxDetailsLinkBean().setRRef("ProcessID_6");
        jdf.getResourceLinkPoolBean().getMailboxDetailsLinkBean().setUsage("Input");

        jdf.getResourceLinkPoolBean().setMediaLinkBean(new MediaLinkBean());
        jdf.getResourceLinkPoolBean().getMediaLinkBean().setRRef("04 - ");
        jdf.getResourceLinkPoolBean().getMediaLinkBean().setUsage("Input");

        jdf.getResourceLinkPoolBean().setRenderingParamsLinkBean(new RenderingParamsLinkBean());
        jdf.getResourceLinkPoolBean().getRenderingParamsLinkBean().setRRef("ProcessID_3");
        jdf.getResourceLinkPoolBean().getRenderingParamsLinkBean().setUsage("Input");

        jdf.getResourceLinkPoolBean().setRunListLinkBean(new RunListLinkBean());
        jdf.getResourceLinkPoolBean().getRunListLinkBean().setRRef("runlist_id");
        jdf.getResourceLinkPoolBean().getRunListLinkBean().setUsage("Input");

        jdf.getResourceLinkPoolBean().setScreeningParamsLinkBean(new ScreeningParamsLinkBean());
        jdf.getResourceLinkPoolBean().getScreeningParamsLinkBean().setUsage("Input");
        jdf.getResourceLinkPoolBean().getScreeningParamsLinkBean().setRRef("ProcessID_4");

        jdf.getResourceLinkPoolBean().setStitchingParamsLinkBean(new StitchingParamsLinkBean());
        jdf.getResourceLinkPoolBean().getStitchingParamsLinkBean().setUsage("Input");
        jdf.getResourceLinkPoolBean().getStitchingParamsLinkBean().setRRef("ProcessID_9");

        jdf.getResourceLinkPoolBean().setComponentLinkBean(new ArrayList<>());
        jdf.getResourceLinkPoolBean().getComponentLinkBean().add(new ComponentLinkBean());

        jdf.getResourceLinkPoolBean().getComponentLinkBean().get(0).setCombinedProcessIndex("9");
        jdf.getResourceLinkPoolBean().getComponentLinkBean().get(0).setOrientation("Flip90");
        jdf.getResourceLinkPoolBean().getComponentLinkBean().get(0).setRRef("Product_8");
        jdf.getResourceLinkPoolBean().getComponentLinkBean().get(0).setUsage("Input");

        jdf.getResourceLinkPoolBean().getComponentLinkBean().add(new ComponentLinkBean());
        jdf.getResourceLinkPoolBean().getComponentLinkBean().get(1).setCombinedProcessIndex("9");
        jdf.getResourceLinkPoolBean().getComponentLinkBean().get(1).setOrientation("Flip90");
        jdf.getResourceLinkPoolBean().getComponentLinkBean().get(1).setRRef("Product_9");
        jdf.getResourceLinkPoolBean().getComponentLinkBean().get(1).setUsage("Output");

        jdf.getResourceLinkPoolBean().getComponentLinkBean().add(new ComponentLinkBean());
        jdf.getResourceLinkPoolBean().getComponentLinkBean().get(2).setAmount("1");
        jdf.getResourceLinkPoolBean().getComponentLinkBean().get(2).setRRef("Product_Final");
        jdf.getResourceLinkPoolBean().getComponentLinkBean().get(2).setUsage("Output");

        // DONE
        jdf.getAuditPoolBean().setCreatedBean(new CreatedBean());
        jdf.getAuditPoolBean().getCreatedBean().setAgentName("varioPRINT 135 PS3 GB ");
        jdf.getAuditPoolBean().getCreatedBean().setAgentVersion("15.2.96.66 cmrepro@RD-AS65");
        jdf.getAuditPoolBean().getCreatedBean().setID("audit_1");
        jdf.getAuditPoolBean().getCreatedBean().setTimeStamp("2014-09-19T06:54:18Z");

        //
        return null;
    }

    private static JDFBean getJdfBean(String fileName) {
        JDFBean jdf = new JDFBean();
        // DONE
        jdf.setActivation("Active");
        jdf.setAuditPoolBean(new AuditPoolBean());
        jdf.setCategory("DigitalPrinting");
        jdf.setCommentBean(new CommentBean());
        jdf.setDescriptiveName(fileName);
        jdf.setID("jdf_1");
        jdf.setResourceLinkPoolBean(new ResourceLinkPoolBean());
        jdf.setResourcePoolBean(new ResourcePoolBean());
        jdf.setStatus("Waiting");
        jdf.setType("Combined");
        jdf.setTypes(
                "LayoutPreparation Imposition Interpreting Rendering Screening DigitalPrinting oce:Mailbox HoleMaking Folding Stitching");
        jdf.setVersion("1.3");
        return jdf;
    }
}
