package global;

/**
 * Provides conversion routines for getting and setting data in byte arrays.
 */
public class Convert {

  /**
   * Reads from the given byte array at the specified position, and converts it
   * into a unicode character.
   */
  public static char getCharValue(int pos, byte[] data) {

    // ignoring that UTF-8 could be up to six bytes
    return (char) data[pos];

  } // public static char getCharValue(int pos, byte[] data)

  /**
   * Writes a unicode character into the given byte array at the specified
   * position.
   */
  public static void setCharValue(char value, int pos, byte[] data) {

    // ignoring that UTF-8 could be up to six bytes
    data[pos] = (byte) value;

  } // public static void setCharValue(char value, int pos, byte[] data)

  /**
   * Reads from the given byte array at the specified position, and converts it
   * into a short.
   */
  public static short getShortValue(int pos, byte[] data) {

    return (short) (((data[pos] & 0xff) << 8) | (data[pos + 1] & 0xff));

  } // public static short getShortValue(int pos, byte[] data)

  /**
   * Writes a short into the given byte array at the specified position.
   */
  public static void setShortValue(short value, int pos, byte[] data) {

    data[pos] = (byte) ((value >> 8) & 0xff);
    data[pos + 1] = (byte) (value & 0xff);

  } // public static void setShortValue(short value, int pos, byte[] data)

  /**
   * Reads from the given byte array at the specified position, and converts it
   * into an integer.
   */
  public static int getIntValue(int pos, byte[] data) {

    return ((data[pos] & 0xff) << 24) | ((data[pos + 1] & 0xff) << 16)
        | ((data[pos + 2] & 0xff) << 8) | (data[pos + 3] & 0xff);

  } // public static int getIntValue(int pos, byte[] data)

  /**
   * Writes an integer into the given byte array at the specified position.
   */
  public static void setIntValue(int value, int pos, byte[] data) {

    data[pos] = (byte) ((value >> 24) & 0xff);
    data[pos + 1] = (byte) ((value >> 16) & 0xff);
    data[pos + 2] = (byte) ((value >> 8) & 0xff);
    data[pos + 3] = (byte) (value & 0xff);

  } // public static void setIntValue(int value, int pos, byte[] data)

  /**
   * Reads from the given byte array at the specified position, and converts it
   * to a float.
   */
  public static float getFloatValue(int pos, byte[] data) {

    // let java do the IEEE 754 conversion
    return Float.intBitsToFloat(getIntValue(pos, data));

  } // public static float getFloatValue(int pos, byte[] data)

  /**
   * Writes a float into the given byte array at the specified position.
   */
  public static void setFloatValue(float value, int pos, byte[] data) {

    // let java do the IEEE 754 conversion
    setIntValue(Float.floatToIntBits(value), pos, data);

  } // public static void setFloatValue(float value, int pos, byte[] data)

  /**
   * Reads from the given byte array at the specified position, and converts it
   * to a string of given length.
   */
  public static String getStringValue(int pos, byte[] data, int length) {

    // validate the maximum length
    int buflen = data.length - pos;
    if (buflen < length) {
      length = buflen;
    }

    // is there any way in Java to avoid these mem copies?
    return new String(data, pos, length).trim();

  } // public static String getStringValue(int pos, byte[] data, int length)

  /**
   * Writes a string into the given byte array at the specified position.
   */
  public static void setStringValue(String value, int pos, byte[] data) {

    // is there any way in Java to avoid these mem copies?
    byte[] ba = value.getBytes();
    System.arraycopy(ba, 0, data, pos, ba.length);

  } // public static void setStringValue(String value, int pos, byte[] data)

} // public class Convert
