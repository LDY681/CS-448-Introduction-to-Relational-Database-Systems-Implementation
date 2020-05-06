package query;

import global.Minibase;
import heap.HeapFile;
import parser.AST_CreateTable;
import parser.ParseException;
import relop.Schema;

/**
 * Execution plan for creating tables.
 */
class CreateTable implements Plan {

  /** Name of the table to create. */
  protected String fileName;

  /** Schema of the table to create. */
  protected Schema schema;

  /**
   * Optimizes the plan, given the parsed query.
   * 
   * @throws QueryException if file name already exists or schema is invalid
   */
  public CreateTable(AST_CreateTable tree) throws QueryException {

    // make sure the file doesn't already exist
    fileName = tree.getFileName();
    QueryCheck.fileNotExists(fileName);

    // get and validate the requested schema
    try {
      schema = tree.getSchema();
    } catch (ParseException exc) {
      throw new QueryException(exc.getMessage());
    }

  } // public CreateTable(AST_CreateTable tree) throws QueryException

  /**
   * Executes the plan and prints applicable output.
   */
  public void execute() {

    // create the heap file
    new HeapFile(fileName);

    // add the schema to the catalog
    Minibase.SystemCatalog.createTable(fileName, schema);

    // print the output message
    System.out.println("Table created.");

  } // public void execute()

} // class CreateTable implements Plan
