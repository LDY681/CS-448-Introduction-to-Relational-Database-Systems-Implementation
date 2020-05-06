package query;

import global.Minibase;
import heap.HeapFile;
import index.HashIndex;
import parser.AST_DropTable;

/**
 * Execution plan for dropping tables.
 */
class DropTable implements Plan {

  /** Name of the table to drop. */
  protected String fileName;

  /**
   * Optimizes the plan, given the parsed query.
   * 
   * @throws QueryException if table doesn't exist
   */
  public DropTable(AST_DropTable tree) throws QueryException {

    // make sure the table exists
    fileName = tree.getFileName();
    QueryCheck.tableExists(fileName);

  } // public DropTable(AST_DropTable tree) throws QueryException

  /**
   * Executes the plan and prints applicable output.
   */
  public void execute() {

    // drop all indexes on the table
    IndexDesc[] inds = Minibase.SystemCatalog.getIndexes(fileName);
    for (IndexDesc ind : inds) {
      new HashIndex(ind.indexName).deleteFile();
      Minibase.SystemCatalog.dropIndex(ind.indexName);
    }

    // delete the heap file and catalog entry
    new HeapFile(fileName).deleteFile();
    Minibase.SystemCatalog.dropTable(fileName);

    // print the output message
    System.out.println("Table dropped.");

  } // public void execute()

} // class DropTable implements Plan
