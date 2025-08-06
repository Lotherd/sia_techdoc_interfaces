/*
* This source code file is the intellectual property of TRAX USA Corp.
* Copyright (c) 2025 TRAX USA Corp. All rights reserved.
*/
package trax.aero.utilities;

import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.tinylog.Logger;

public class StringUtilities {

    private static final SecureRandom random = new SecureRandom();

    public static String tirmString(String inputString) {
        if (inputString == null) {
            return null;
        } else {
            return inputString.substring(0, Math.min(inputString.length(), 8));
        }
    }

    public static String removeHypenString(String inputString) {
        if (inputString == null) {
            return null;
        } else {
            return inputString.replaceAll("-", "");
        }
    }

    public static String generateNumericString(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(random.nextInt(10)); // digits 0–9
        }
        return sb.toString();
    }

    public static String xmlCleaning(String xml) {
        xml = xml.replaceAll("&amp;apos;", "&apos;");
        xml = xml.replaceAll("&amp;gt;", "&gt;");
        xml = xml.replaceAll("&amp;lt;", "&lt;");
        xml = xml.replaceAll("&amp;quot;", "&quot;");
        xml = xml.replaceAll("&amp;re;", "&#xA;");
        return xml;
    }
    
    public static Date convertStringToDate(String string) {
        try {
            return new SimpleDateFormat("yyyyMMdd").parse(string);
        } catch (Exception e) {
            Logger.error(e);
            return null;
        }
    }
}
