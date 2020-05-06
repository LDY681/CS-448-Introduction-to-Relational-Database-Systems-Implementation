package relop;

import global.AttrType;
import global.Convert;
import global.RID;
import heap.HeapFile;

/**
 * Each tuple in a relation is a collection of bytes that must fit within a
 * single page. The Tuple class provides a logical view of fields, allowing get
 * and set operations while automatically handling offsets and type conversions.
 */
public class Tuple {

  /** Page buffer containing this tuple. */
  protected byte[] data;

  /** Schema information for the fields. */
  protected Schema schema;

  // --------------------------------------------------------------------------

  /**
   * Creates a new empty tuple, given its schema.
   * 
   * @param schema logical information for the fields
   */
  public Tuple(Schema schema) {
    this.schema = schema;
    data = new byte[schema.getLength()];
  }

  /**
   * Creates a new tuple, given its schema and values.
   * 
   * @param schema logical information for the fields
   * @param values individual data values of the fields
   */
  public Tuple(Schema schema, Object... values) {
    this.schema = schema;
    data = new byte[schema.getLength()];
    setAllFields(values);
  }

  /**
   * Creates a new tuple, given existing values to wrap.
   * 
   * @param schema logical information for the fields
   * @param data record buffer containing the tuple
   */
  public Tuple(Schema schema, byte[] data) {
    this.schema = schema;
    this.data = data;
  }

  /**
   * Builds and returns a new tuple resulting from joining two tuples.
   * 
   * @param t1 the left tuple
   * @param t2 the right tuple
   * @param schema of the result
   */
  public static Tuple join(Tuple t1, Tuple t2, Schema schema) {

    // construct the new tuple
    int t1cnt = t1.schema.getCount();
    int t2cnt = t2.schema.getCount();
    Tuple tuple = new Tuple(schema);

    // copy all fields from t1 and t2
    int fldno = 0;
    for (int i = 0; i < t1cnt; i++) {
      tuple.setField(fldno++, t1.getField(i));
    }
    for (int i = 0; i < t2cnt; i++) {
      tuple.setField(fldno++, t2.getField(i));
    }

    // return the resulting tuple
    return tuple;

  } // public static Tuple join(Tuple t1, Tuple t2, Schema schema)

  /**
   * Gets the underlying data buffer.
   */
  public byte[] getData() {
    return data;
  }

  /**
   * Inserts the tuple into the given heap file.
   */
  public RID insertIntoFile(HeapFile file) {
    return file.insertRecord(data);
  }

  /**
   * Gets a field's value generically.
   */
  public Object getField(int fldno) {

    // refer to the schema for the appropriate conversion
    switch (schema.fieldType(fldno)) {

      case AttrType.INTEGER:
        return getIntFld(fldno);

      case AttrType.FLOAT:
        return getFloatFld(fldno);

      case AttrType.STRING:
        return getStringFld(fldno);

      default:
        throw new IllegalStateException("invalid attribute type");
    }

  } // public Object getField(int fldno)

  /**
   * Gets a field's value generically, by column name.
   */
  public Object getField(String fldName) {
    return getField(schema.fieldNumber(fldName));
  }

  /**
   * Sets a field's value generically.
   */
  public void setField(int fldno, Object val) {

    // refer to the schema for the appropriate conversion
    switch (schema.fieldType(fldno)) {

      case AttrType.INTEGER:
        setIntFld(fldno, (Integer) val);
        break;

      case AttrType.FLOAT:
        setFloatFld(fldno, (Float) val);
        break;

      case AttrType.STRING:
        setStringFld(fldno, (String) val);
        break;

      default:
        throw new IllegalStateException("invalid attribute type");
    }

  } // public void setField(int fldno, Object val)

  /**
   * Sets a field's value generically, by column name.
   */
  public void setField(String fldName, Object val) {
    setField(schema.fieldNumber(fldName), val);
  }

  /**
   * Returns generic values for all fields.
   */
  public Object[] getAllFields() {

    Object[] values = new Object[schema.getCount()];
    for (int i = 0; i < values.length; i++) {
      values[i] = getField(i);
    }
    return values;

  } // public Object[] getAllFields()

  /**
   * Sets all fields, given generic values.
   */
  public void setAllFields(Object... values) {

    for (int i = 0; i < values.length; i++) {
      setField(i, values[i]);
    }

  } // public void setAllFields(Object... values)

  /**
   * Gets an integer field.
   */
  public int getIntFld(int fldno) {
    return Convert.getIntValue(schema.fieldOffset(fldno), data);
  }

  /**
   * Sets an integer field.
   */
  public void setIntFld(int fldno, int val) {
    Convert.setIntValue(val, schema.fieldOffset(fldno), data);
  }

  /**
   * Gets a float field.
   */
  public float getFloatFld(int fldno) {
    return Convert.getFloatValue(schema.fieldOffset(fldno), data);
  }

  /**
   * Sets a float field.
   */
  public void setFloatFld(int fldno, float val) {
    Convert.setFloatValue(val, schema.fieldOffset(fldno), data);
  }

  /**
   * Gets a string field.
   */
  public String getStringFld(int fldno) {
    return Convert.getStringValue(schema.fieldOffset(fldno), data, schema
        .fieldLength(fldno));
  }

  /**
   * Sets a string field (automatically truncates, if necessary).
   */
  public void setStringFld(int fldno, String val) {

    // truncate the string if too long
    int len = schema.fieldLength(fldno);
    if (val.length() > len) {
      val = val.substring(0, len - 1);
    }

    // set the string and zero out the rest
    int off = schema.fieldOffset(fldno);
    Convert.setStringValue(val, off, data);
    for (int i = val.length(); i < len; i++) {
      data[off + i] = 0;
    }

  } // public void setStringFld(int fldno, String val)

  /**
   * Prints the tuple in a human-readable format.
   */
  public void print() {

    // print everything as a string, with padding
    String str = null;
    int cnt = schema.getCount();
    for (int i = 0; i < cnt; i++) {
      switch (schema.fieldType(i)) {

        case AttrType.INTEGER:
          str = Integer.toString(getIntFld(i));
          System.out.print(str);
          padOutput(0, str.length());
          break;

        case AttrType.FLOAT:
          str = Float.toString(getFloatFld(i));
          System.out.print(str);
          padOutput(0, str.length());
          break;

        case AttrType.STRING:
          str = getStringFld(i);
          System.out.print(str);
          padOutput(schema.fieldLength(i), str.length());
          break;

      }
    }
    System.out.println();

  } // public void print()

  /**
   * Pads the current output to make columns line up.
   * 
   * @param fieldLen minimum length of the current field
   * @param outputLen length of the current output so far
   */
  protected void padOutput(int fieldLen, int outputLen) {

    fieldLen = Math.max(Schema.MIN_WIDTH, fieldLen);
    for (int j = 0; j < fieldLen - outputLen; j++) {
      System.out.print(' ');
    }

  } // protected void padOutput(int fieldLen, int outputLen)

  
  // *** DO NOT CHANGE ANY EXISTING METHODS OR VARIABLES BELOW THIS LINE ***
  //String format: field0|field1|field2|...fieldN|
  public String toString()
  {
    String res = "";
    for(int i = 0; i < schema.getCount(); i++)
    {
      switch(schema.fieldType(i))
      {
        case AttrType.FLOAT:
	    res += Float.toString(getFloatFld(i)) + "";
          break;
        case AttrType.INTEGER:
	    res += Integer.toString(getIntFld(i)) + ":";
          break;
        case AttrType.STRING:
	    res += getStringFld(i) + ":";
	  break;
      }
    }
    if(res.length() > 0) res = res.substring(0, res.length()-1);
    return res;
  }
} // public class Tuple
