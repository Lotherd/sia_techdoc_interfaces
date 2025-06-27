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
