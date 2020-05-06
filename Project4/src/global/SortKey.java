package global;

/**
 * One term in an ORDER BY clause; i.e. relations can be sorted on multiple
 * (distinct) attributes, as well as in ascending or descending order.
 */
public class SortKey {

  /** Column name or field number. */
  public Object field;

  /** True if descending order, false if ascending order. */
  public boolean isDesc;

  /**
   * Constructs a SortKey from the given values.
   */
  public SortKey(Object field, boolean isDesc) {
    this.field = field;
    this.isDesc = isDesc;
  }

} // public class SortKey
