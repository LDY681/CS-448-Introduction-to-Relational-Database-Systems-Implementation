package global;

/**
 * Although a PageId is simply an integer, wrapping it provides convenient
 * methods and allows returning it via method parameters.
 */
public class PageId implements GlobalConst {

  /** The actual page id value. */
  public int pid;

  // --------------------------------------------------------------------------

  /**
   * Default constructor.
   */
  public PageId() {
    pid = INVALID_PAGEID;
  }

  /**
   * Constructs a PageId from the given value.
   */
  public PageId(int pid) {
    this.pid = pid;
  }

  /**
   * Makes a copy of the given PageId.
   */
  public void copyPageId(PageId pageNo) {
    this.pid = pageNo.pid;
  }

  /**
   * Returns a hash code value for the PageId.
   */
  public int hashCode() {
    return pid;
  }

  /**
   * True if obj is a PageId with the same pid; false otherwise.
   */
  public boolean equals(Object obj) {
    if (obj instanceof PageId) {
      PageId pageno = (PageId) obj;
      return (this.pid == pageno.pid);
    }
    return false;
  }

  /**
   * Returns a string representation of the PageId.
   */
  public String toString() {
    return Integer.toString(pid);
  }

} // public class PageId implements GlobalConst
