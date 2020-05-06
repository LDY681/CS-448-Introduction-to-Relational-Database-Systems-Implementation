package query;

import global.AttrType;
import parser.AST_Describe;
import relop.Schema;

/**
 * Execution plan for describing tables.
 */
class Describe implements Plan {

  /** Name of the table to describe. */
  protected String fileName;

  /** Schema of the table to describe. */
  protected Schema schema;

  /**
   * Optimizes the plan, given the parsed query.
   * 
   * @throws QueryException if table doesn't exist
   */
  public Describe(AST_Describe tree) throws QueryException {

    // make sure the table exists
    fileName = tree.getFileName();
    schema = QueryCheck.tableExists(fileName);

  } // public Describe(AST_Describe tree) throws QueryException

  /**
   * Executes the plan and prints applicable output.
   */
  public void execute() {

    // print the table name
    System.out.println(fileName);
    for (int i = 0; i < fileName.length(); i++) {
      System.out.print('-');
    }
    System.out.println();

    // print the schema (long format)
    for (int i = 0; i < schema.getCount(); i++) {
      System.out.println(schema.fieldName(i) + " "
          + AttrType.toString(schema.fieldType(i)) + "("
          + schema.fieldLength(i) + ")");
    }

  } // public void execute()

} // class Describe implements Plan
