package relop;

import global.AttrType;

/**
 * Each tuple has a schema that defines the logical view of the raw bytes; it
 * describes the types, lengths, offsets, and names of a tuple's fields.
 */
public class Schema {

  /** Minimum column width for output. */
  public static final int MIN_WIDTH = 10;

  // --------------------------------------------------------------------------

  /** Attribute types of the fields. */
  protected int[] types;

  /** Variable lengths of the fields. */
  protected int[] lengths;

  /** Relative offsets of the fields. */
  protected int[] offsets;

  /** Column names of the fields. */
  protected String[] names;

  // --------------------------------------------------------------------------

  /**
   * Constructs a schema for the given number of fields.
   */
  public Schema(int fldcnt) {
    types = new int[fldcnt];
    lengths = new int[fldcnt];
    offsets = new int[fldcnt];
    names = new String[fldcnt];
  }

  /**
   * Sets the type, length, and name for the given field; this automatically
   * calculates its offset, provided that fields are set in ascending order.
   */
  public void initField(int fldno, int type, int length, String name) {

    // save the type, length, and name
    types[fldno] = type;
    lengths[fldno] = length;
    names[fldno] = name;

    // calculate the relative offset
    if (fldno > 0) {
      offsets[fldno] = offsets[fldno - 1] + lengths[fldno - 1];
    } else {
      offsets[fldno] = 0;
    }

  } // public void initField(int fldno, int type, int length, String name)

  /**
   * Copies a field from another schema.
   * 
   * @param fldno number of this field to set
   * @param schema contains the info to copy
   * @param srcno number of the field to copy
   */
  public void initField(int fldno, Schema schema, int srcno) {
    initField(fldno, schema.types[srcno], schema.lengths[srcno],
        schema.names[srcno]);
  }

  /**
   * Builds and returns a new schema resulting from joining two schemas.
   * 
   * @param s1 the left schema
   * @param s2 the right schema
   */
  public static Schema join(Schema s1, Schema s2) {

    // construct the new schema
    int s1cnt = s1.getCount();
    int s2cnt = s2.getCount();
    Schema schema = new Schema(s1cnt + s2cnt);

    // copy all fields from s1 and s2
    int fldno = 0;
    for (int i = 0; i < s1cnt; i++) {
      schema.initField(fldno++, s1, i);
    }
    for (int i = 0; i < s2cnt; i++) {
      schema.initField(fldno++, s2, i);
    }

    // return the resulting schema
    return schema;

  } // public static Schema join(Schema s1, Schema s2)

  // --------------------------------------------------------------------------

  /**
   * Gets the number of fields in the schema.
   */
  public int getCount() {
    // all arrays are same length
    return types.length;
  }

  /**
   * Gets the size of a tuple (in bytes).
   */
  public int getLength() {
    int len = 0;
    for (int i = 0; i < lengths.length; i++) {
      len += lengths[i];
    }
    return len;
  }

  /**
   * Gets the type of the given field.
   */
  public int fieldType(int fldno) {
    return types[fldno];
  }

  /**
   * Gets the length of the given field.
   */
  public int fieldLength(int fldno) {
    return lengths[fldno];
  }

  /**
   * Gets the offset of the given field.
   */
  public int fieldOffset(int fldno) {
    return offsets[fldno];
  }

  /**
   * Gets the name of the given field.
   */
  public String fieldName(int fldno) {
    return names[fldno];
  }

  /**
   * Gets the number of the field with the given name, or -1 if not found.
   */
  public int fieldNumber(String name) {
    for (int i = 0; i < names.length; i++) {
      if (names[i].equalsIgnoreCase(name)) {
        return i;
      }
    }
    return -1;
  }

  /**
   * Prints the schema (i.e. the first lines of typical query output).
   */
  public void print() {

    // print and space the column names
    int len = 0;
    for (int i = 0; i < types.length; i++) {

      // print the column name
      System.out.print(names[i]);

      // figure out the padding
      int collen = Math.max(names[i].length(), MIN_WIDTH);
      if (types[i] == AttrType.STRING) {
        collen = Math.max(lengths[i], collen);
      }
      len += collen;

      // pad the output to the field length
      for (int j = 0; j < collen - names[i].length(); j++) {
        System.out.print(' ');
      }

    } // for

    // print the line separator
    System.out.println();
    for (int i = 0; i < len; i++) {
      System.out.print('-');
    }
    System.out.println();

  } // public void print()

} // public class Schema
