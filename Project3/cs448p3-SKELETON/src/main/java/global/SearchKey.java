package global;

/**
 * Provides a general and type-safe way to store and compare index search keys.
 */
public class SearchKey implements Comparable<SearchKey> {

  /** Internal type number for Integer keys. */
  protected static final byte INTEGER_KEY = 1;

  /** Internal type number for Float keys. */
  protected static final byte FLOAT_KEY = 2;

  /** Internal type number for String keys. */
  protected static final byte STRING_KEY = 3;

  // --------------------------------------------------------------------------

  /** The type of the key value. */
  protected byte type;

  /** The size of the key value (in bytes). */
  protected short size;

  /** The actual key value. */
  protected Object value;

  // --------------------------------------------------------------------------

  /**
   * Constructs a new integer search key.
   */
  public SearchKey(Integer value) {
    this.type = INTEGER_KEY;
    this.size = Integer.SIZE / Byte.SIZE;
    this.value = value;
  }

  /**
   * Constructs a new float search key.
   */
  public SearchKey(Float value) {
    this.type = FLOAT_KEY;
    this.size = Float.SIZE / Byte.SIZE;
    this.value = value;
  }

  /**
   * Constructs a new string search key.
   */
  public SearchKey(String value) {
    this.type = STRING_KEY;
    this.size = (short) value.length();
    this.value = value;
  }

  /**
   * Constructs a search key from a generic value.
   * 
   * @throws IllegalArgumentException if value's type is invalid
   */
  public SearchKey(Object value) {

    // Integer key?
    if (value instanceof Integer) {
      this.type = INTEGER_KEY;
      this.size = Integer.SIZE / Byte.SIZE;
      this.value = value;
    }

    // Float key?
    else if (value instanceof Float) {
      this.type = FLOAT_KEY;
      this.size = Float.SIZE / Byte.SIZE;
      this.value = value;
    }

    // String key?
    else if (value instanceof String) {
      this.type = STRING_KEY;
      this.size = (short) ((String) value).length();
      this.value = value;
    }

    // otherwise, none of the above
    else {
      throw new IllegalArgumentException("invalid key value type");
    }

  } // public SearchKey(Object value)

  /**
   * Copy constructor for a search key.
   */
  public SearchKey(SearchKey key) {

    // copy the type and size
    this.type = key.type;
    this.size = key.size;

    // copy the value
    switch (key.type) {
      case INTEGER_KEY:
        this.value = new Integer((Integer) key.value);
        break;
      case FLOAT_KEY:
        this.value = new Float((Float) key.value);
        break;
      case STRING_KEY:
        this.value = new String((String) key.value);
        break;
    }

  } // public SearchKey(SearchKey key)

  // --------------------------------------------------------------------------

  /**
   * Constructs a SearchKey stored in the given data buffer.
   */
  public SearchKey(byte[] data, short offset) {

    // extract the type and size
    type = data[offset];
    size = Convert.getShortValue(offset + 1, data);

    // extract the key value
    switch (type) {
      case INTEGER_KEY:
        value = new Integer(Convert.getIntValue(offset + 3, data));
        break;
      case FLOAT_KEY:
        value = new Float(Convert.getFloatValue(offset + 3, data));
        break;
      case STRING_KEY:
        value = Convert.getStringValue(offset + 3, data, size);
        break;
    }

  } // public SearchKey(byte[] data, short offset)

  /**
   * Writes the SearchKey into the given data buffer.
   */
  public void writeData(byte[] data, short offset) {

    // write the type and size
    data[offset] = type;
    Convert.setShortValue(size, offset + 1, data);

    // write the key value
    switch (type) {
      case INTEGER_KEY:
        Convert.setIntValue((Integer) value, offset + 3, data);
        break;
      case FLOAT_KEY:
        Convert.setFloatValue((Float) value, offset + 3, data);
        break;
      case STRING_KEY:
        Convert.setStringValue((String) value, offset + 3, data);
        break;
    }

  } // public void writeData(byte[] data, short offset)

  /**
   * Gets the total length of the search key (in bytes).
   */
  public short getLength() {
    return (short) (3 + size);
  }

  // --------------------------------------------------------------------------

  /**
   * Gets the hash value for the search key, given the depth (i.e. number of
   * bits to consider).
   */
  public int getHash(int depth) {

    // apply the appropriate calculation
    int mask = (1 << depth) - 1;
    switch (type) {

      default:
      case INTEGER_KEY:
        int ikey = ((Integer) value).intValue();
        return ikey & mask;

      case FLOAT_KEY:
        int fkey = Float.floatToIntBits((Float) value);
        return fkey & mask;

      case STRING_KEY:

        // reverse the first four bytes of the string
        byte[] s = ((String) value).getBytes();
        int skey = 0;
        int len = s.length > 4 ? 4 : s.length;
        for (int i = 0; i < len; i++) {
          skey |= (s[i] << (i * Byte.SIZE));
        }
        return skey & mask;

    } // switch

  } // public int getHash(int depth)

  /**
   * Returns true if the search key matches the given hash value, false
   * otherwise.
   */
  public boolean isHash(int hash) {

    // calculate the bit depth (i.e. the left-most '1' bit)
    int depth = (int) (Math.log(hash) / Math.log(2) + 1);

    // compare the hash codes
    return (getHash(depth) == hash);

  } // public boolean isHash(int hash)

  // --------------------------------------------------------------------------

  /**
   * Returns a generic hash code for the key value.
   */
  public int hashCode() {
    return value.hashCode();
  }

  /**
   * True if obj is a SearchKey with the same values; false otherwise.
   */
  public boolean equals(Object obj) {
    if (obj instanceof SearchKey) {
      SearchKey key = (SearchKey) obj;
      return (value.equals(key.value));
    }
    return false;
  }

  /**
   * Generically compares two search keys.
   * 
   * @return a negative integer, zero, or a positive integer as this object is
   *         less than, equal to, or greater than the specified object
   * @throws IllegalArgumentException if the search keys are not comparable
   */
  public int compareTo(SearchKey key) {

    // Integer comparison
    if (value instanceof Integer) {
      if (key.value instanceof Integer) {

        Integer ikey1 = (Integer) this.value;
        Integer ikey2 = (Integer) key.value;
        return ikey1.compareTo(ikey2);

      } else {
        throw new IllegalArgumentException("search keys are not comparable");
      }
    }

    // Float comparison
    if (value instanceof Float) {
      if (key.value instanceof Float) {

        Float fkey1 = (Float) this.value;
        Float fkey2 = (Float) key.value;
        return fkey1.compareTo(fkey2);

      } else {
        throw new IllegalArgumentException("search keys are not comparable");
      }
    }

    // default: String comparison
    if (key.value instanceof String) {

      String skey1 = (String) this.value;
      String skey2 = (String) key.value;
      return skey1.compareTo(skey2);

    } else {
      throw new IllegalArgumentException("search keys are not comparable");
    }

  } // public int compareTo(SearchKey key)

} // public class SearchKey implements Comparable<SearchKey>
