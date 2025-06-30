/*
* This source code file is the intellectual property of TRAX USA Corp.
* Copyright (c) 2025 TRAX USA Corp. All rights reserved.
*/
package trax.aero.util;

public class StringOptimizer {

    public static StringBuilder stringToBuilder(String str) {
        return str != null ? new StringBuilder(str) : null;
    }

    public static String builderToString(StringBuilder builder) {
        return builder != null ? builder.toString() : null;
    }

    public static boolean isNullOrEmpty(String string) {
        return (string == null || string.trim().isEmpty());
    }
}
