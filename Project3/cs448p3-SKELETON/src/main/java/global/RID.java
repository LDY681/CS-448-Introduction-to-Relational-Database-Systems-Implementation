package global;

/**
 * A record is uniquely identified by its page number and slot number.
 */
public class RID {

  /** The record's page number. */
  public PageId pageno;

  /** The record's slot number. */
  public int slotno;

  // --------------------------------------------------------------------------

  /**
   * Default constructor.
   */
  public RID() {
    pageno = new PageId();
    slotno = 0;
  }

  /**
   * Copy constructor.
   */
  public RID(RID rid) {
    copyRID(rid);
  }

  /**
   * Constructs an RID from the given values.
   */
  public RID(PageId pageno, int slotno) {
    this.pageno = new PageId();
    this.pageno.pid = pageno.pid;
    this.slotno = slotno;
  }

  /**
   * Constructs an RID stored in the given data buffer.
   */
  public RID(byte[] data, short offset) {
    pageno = new PageId(Convert.getIntValue(offset, data));
    slotno = Convert.getIntValue(offset + 4, data);
  }

  /**
   * Writes the RID into the given data buffer.
   */
  public void writeData(byte[] data, short offset) {
    Convert.setIntValue(pageno.pid, offset, data);
    Convert.setIntValue(slotno, offset + 4, data);
  }

  /**
   * Gets the total length of the RID (in bytes).
   */
  public short getLength() {
    return 8;
  }

  /**
   * Makes a copy of the given RID.
   */
  public void copyRID(RID rid) {
    this.pageno = new PageId();
    this.pageno.pid = rid.pageno.pid;
    slotno = rid.slotno;
  }

  /**
   * Returns a hash code value for the RID.
   */
  public int hashCode() {
    // won't work if DB supports more than 65535 pages
    return ((pageno.pid & 0xFFFF) << 16) | (slotno & 0xFFFF);
  }

  /**
   * True if obj is an RID with the same values; false otherwise.
   */
  public boolean equals(Object obj) {
    if (obj instanceof RID) {
      RID rid = (RID) obj;
      return ((pageno.pid == rid.pageno.pid) && (slotno == rid.slotno));
    }
    return false;
  }

  /**
   * Returns a string representation of the PageId.
   */
  public String toString() {
    return pageno.toString() + ":" + Integer.toString(slotno);
  }

} // public class RID
