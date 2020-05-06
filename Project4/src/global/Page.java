package global;

/**
 * Lowest-level view of a disk page.
 */
public class Page implements GlobalConst {

  /** The actual byte array for the page. */
  protected byte[] data;

  // --------------------------------------------------------------------------

  /**
   * Default constructor; creates a blank page.
   */
  public Page() {
    data = new byte[PAGE_SIZE];
  }

  /**
   * Constructor that wraps the given byte array.
   */
  public Page(byte[] data) {
    setData(data);
  }

  /**
   * Get accessor for the data byte array.
   */
  public byte[] getData() {
    return data;
  }

  /**
   * Set accessor for the data byte array.
   * 
   * @throws IllegalArgumentException if the data array size is invalid
   */
  public void setData(byte[] data) {
    if (data.length != PAGE_SIZE) {
      Minibase.haltSystem(new IllegalArgumentException(
          "Invalid page buffer size"));
    }
    this.data = data;
  }

  /**
   * Sets this page's data array to share the given page's data array.
   */
  public void setPage(Page page) {
    this.data = page.data;
  }

  /**
   * Copies the contents of the given page's buffer into this page's buffer.
   */
  public void copyPage(Page page) {
    System.arraycopy(page.data, 0, this.data, 0, PAGE_SIZE);
  }

  // --------------------------------------------------------------------------

  /**
   * Gets a char at the given page offset.
   */
  public char getCharValue(int offset) {
    return Convert.getCharValue(offset, data);
  }

  /**
   * Sets a char at the given page offset.
   */
  public void setCharValue(char value, int offset) {
    Convert.setCharValue(value, offset, data);
  }

  /**
   * Gets a short at the given page offset.
   */
  public short getShortValue(int offset) {
    return Convert.getShortValue(offset, data);
  }

  /**
   * Sets a short at the given page offset.
   */
  public void setShortValue(short value, int offset) {
    Convert.setShortValue(value, offset, data);
  }

  /**
   * Gets an int at the given page offset.
   */
  public int getIntValue(int offset) {
    return Convert.getIntValue(offset, data);
  }

  /**
   * Sets an int at the given page offset.
   */
  public void setIntValue(int value, int offset) {
    Convert.setIntValue(value, offset, data);
  }

  /**
   * Gets a float at the given page offset.
   */
  public float getFloatValue(int offset) {
    return Convert.getFloatValue(offset, data);
  }

  /**
   * Sets a float at the given page offset.
   */
  public void setFloatValue(float value, int offset) {
    Convert.setFloatValue(value, offset, data);
  }

  /**
   * Gets a string at the given page offset, given the maximum length.
   */
  public String getStringValue(int offset, int length) {
    return Convert.getStringValue(offset, data, length);
  }

  /**
   * Sets a string at the given page offset.
   */
  public void setStringValue(String value, int offset) {
    Convert.setStringValue(value, offset, data);
  }

} // public class Page implements GlobalConst
