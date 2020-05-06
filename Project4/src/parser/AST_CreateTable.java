package parser;

import global.GlobalConst;
import relop.Schema;

/**
 * AST node for CREATE TABLE statements.
 */
public class AST_CreateTable extends SimpleNode implements GlobalConst {

  public AST_CreateTable(int id) {
    super(id);
  }

  public AST_CreateTable(MiniSql p, int id) {
    super(p, id);
  }

  /** Name of the table to create. */
  protected String fileName;

  /**
   * Gets the name of the table to create.
   */
  public String getFileName() {
    return fileName;
  }

  /**
   * Gets the parsed schema for the table to create.
   * 
   * @throws ParseException if duplicate column names or invalid lengths
   */
  public Schema getSchema() throws ParseException {

    // create a new schema, given the number of columns
    String[] names = ((AST_IdentList) children[0]).getNames();
    Schema schema = new Schema(names.length);

    // build the schema by iterating through each column in the list
    int tuplen = 0;
    for (int i = 0; i < names.length; i++) {

      // prevent duplicate column names (naive n^2 algorithm)
      String name = names[i];
      for (int j = 0; j < i; j++) {
        if (name.equalsIgnoreCase(names[j])) {
          throw new ParseException("duplicate column '" + name + "'");
        }
      }

      // validate the column size
      AST_ColumnType cnode = (AST_ColumnType) children[0].jjtGetChild(i);
      if (cnode.size < 1) {
        throw new ParseException("column '" + name + "' too small");
      }
      if (cnode.size > MAX_COLSIZE) {
        throw new ParseException("column '" + name + "' too large");
      }

      // validate the tuple size
      tuplen += cnode.size;
      if (tuplen > MAX_TUPSIZE) {
        throw new ParseException("records will be too large");
      }

      // add the column to the schema
      schema.initField(i, cnode.type, cnode.size, name);

    } // for

    // return the resulting schema
    return schema;

  } // public Schema getSchema() throws ParseException

} // public class AST_CreateTable extends SimpleNode implements GlobalConst
