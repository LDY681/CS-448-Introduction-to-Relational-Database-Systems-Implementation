package query;

import parser.AST_Insert;
import bufmgr.*;
import diskmgr.*;
import heap.*;
import index.*;
import relop.*;
import global.*;
import parser.*;

import java.rmi.server.ExportException;

/**
 * Execution plan for inserting tuples.
 */
class Insert implements Plan {
  private String file;  //tree.getFileName
  private Object[] values;  //tree.getValues
  private Schema schema;  //schema from file
  /**
   * Optimizes the plan, given the parsed query.
   * 
   * @throws QueryException if table doesn't exists or values are invalid
   */
  public Insert(AST_Insert tree) throws QueryException {
    this.file = tree.getFileName();  //file to be inserted to
    this.values = tree.getValues(); //values to be inserted
    this.schema = Minibase.SystemCatalog.getSchema(this.file);  //get schema from SystemCatalog by fileName

    //if table doesn't exists or values are invalid
    QueryCheck.tableExists(this.file);
    QueryCheck.insertValues(this.schema, this.values);

  } // public Insert(AST_Insert tree) throws QueryException

  /**
   * Executes the plan and prints applicable output.
   */
  public void execute() {
    HeapFile heapFile = new HeapFile(this.file);  //create heapFile from file
    Tuple tuple = new Tuple(this.schema, this.values);  //create tuple based on values
    RID rid = heapFile.insertRecord(tuple.getData()); //Add tuple into heapFile
    IndexDesc[] indexes = Minibase.SystemCatalog.getIndexes(this.file); //All indexes in system catalogs from the file

    //Iterator through all indexes from this file and insert the entry of this tuple
    for(int i = 0; i < indexes.length; i++) {
      HashIndex hashIndex = new HashIndex(indexes[i].indexName);
      SearchKey searchKey = new SearchKey(tuple.getField(indexes[i].columnName));
      hashIndex.insertEntry(searchKey, rid);
    }

    //Each query should print a one-line message at the end
    System.out.println("1 row affected. (Table: " + file + ")");

  } // public void execute()

} // class Insert implements Plan
