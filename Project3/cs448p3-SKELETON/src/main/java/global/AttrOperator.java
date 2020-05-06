package global;

/**
 * Enumeration class for predicate operators.
 */
public class AttrOperator {

  public static final int EQ = 11;
  public static final int NEQ = 12;
  public static final int GT = 13;
  public static final int GTE = 14;
  public static final int LT = 15;
  public static final int LTE = 16;

  // --------------------------------------------------------------------------

  /**
   * Private constructor (static class).
   */
  private AttrOperator() {}

  /**
   * Returns the constant value for the string representation (i.e. inverse of
   * the toString() method).
   */
  public static int toValue(String str) {

    // it may have been better to use the parse tree node, but this
    // makes it easier to release class projects independently
    if (str.equals("=")) {
      return EQ;
    } else if (str.equals("<>")) {
      return NEQ;
    } else if (str.equals(">")) {
      return GT;
    } else if (str.equals(">=")) {
      return GTE;
    } else if (str.equals("<")) {
      return LT;
    } else if (str.equals("<=")) {
      return LTE;
    } else {
      throw new IllegalArgumentException("unknown operator");
    }

  } // public static int toValue(String str)

  /**
   * Returns a string representation of an AttrOperator.
   */
  public static String toString(int value) {

    switch (value) {

      case EQ:
        return "=";

      case NEQ:
        return "<>";

      case GT:
        return ">";

      case GTE:
        return ">=";

      case LT:
        return "<";

      case LTE:
        return "<=";

    } // switch

    return ("Unexpected AttrOperator " + value);

  } // public static String toString(int value)

} // public class AttrOperator
