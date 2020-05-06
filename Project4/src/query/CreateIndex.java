package query;

import parser.AST_CreateIndex;
import bufmgr.*;
import diskmgr.*;
import heap.*;
import index.*;
import relop.*;
import global.*;
import parser.*;

/**
 * Execution plan for creating indexes.
 */
class CreateIndex implements Plan {
  private String file;  //the file from the ASTtree
  private String ixTable;  //The getIxTable from ASTtree
  private String ixColumn;  //The getIxColumn from ASTtree
  private Schema schema;  //schema of ixTable
  /**
   * Optimizes the plan, given the parsed query.
   * 
   * @throws QueryException if index already exists or table/column invalid
   */
  public CreateIndex(AST_CreateIndex tree) throws QueryException {
    this.file = tree.getFileName(); //the file from the ASTtree
    QueryCheck.fileNotExists(file); //throws error if fileName of this index exists in DiskManager

    this.ixTable = tree.getIxTable(); //The getIxTable from ASTtree
    this.ixColumn = tree.getIxColumn(); //The getIxColumn from ASTtree

    //Check if table/column is invalid.
    this.schema = QueryCheck.tableExists(this.ixTable); //if table is valid
    QueryCheck.columnExists(this.schema, this.ixColumn);  //check column is valid

    //Do the opposite to index
    boolean indexExists = true;
    try {
      QueryCheck.indexExists(this.file);  //if QueryCheck says the index entry in the SystemCatalog doesn't exist
    } catch(QueryException e) {
      indexExists = false;  //set indexExists to false
    }
    if (indexExists) {
      throw new QueryException("index already exists");
    }

  } // public CreateIndex(AST_CreateIndex tree) throws QueryException

  /**
   * Executes the plan and prints applicable output.
   */
  public void execute() {

    HeapFile heapFile = new HeapFile(this.ixTable); //create heap file based on indexTable
    FileScan fileScan = new FileScan(this.schema, heapFile); //create fileScan based on schema and heapFile
    HashIndex hashIndex = new HashIndex(this.file); //create a new hashIndex to be populated

    //iterate through the fileScan
    int fieldNumber = this.schema.fieldNumber(this.ixColumn); //get fieldNumber from the column location in the schema
    while(fileScan.hasNext()){
      Tuple tuple = fileScan.getNext();
      SearchKey searchKey = new SearchKey(tuple.getField(fieldNumber));
      RID lastRID = fileScan.getLastRID();
      hashIndex.insertEntry(searchKey, lastRID);  //insert an entry pair (key, rid) into hashIndex
    }

    //now close fileScan
    fileScan.close();

    //and add Index into SytemCatalog
    Minibase.SystemCatalog.createIndex(this.file,this.ixTable,this.ixColumn);

    //Each query should print a one-line message at the end
    System.out.println("Index created!");

  } // public void execute()

} // class CreateIndex implements Plan
