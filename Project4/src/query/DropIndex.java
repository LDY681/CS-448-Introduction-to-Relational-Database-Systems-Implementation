package query;

import parser.AST_DropIndex;
import bufmgr.*;
import diskmgr.*;
import heap.*;
import index.*;
import relop.*;
import global.*;
import parser.*;

/**
 * Execution plan for dropping indexes.
 */
class DropIndex implements Plan {
  private String file;  //the file from the ASTtree
  /**
   * Optimizes the plan, given the parsed query.
   * 
   * @throws QueryException if index doesn't exist
   */
  public DropIndex(AST_DropIndex tree) throws QueryException {
    this.file = tree.getFileName(); //the file from the ASTtree
    try{
      QueryCheck.indexExists(this.file);
    } catch(QueryException e) {
      throw new QueryException("index '" + this.file + "' doesn't exists");
    }
  } // public DropIndex(AST_DropIndex tree) throws QueryException

  /**
   * Executes the plan and prints applicable output.
   */
  public void execute() {
    //deleteFile from hashIndex and drop it from SystemCatalog
    HashIndex hashIndex = new HashIndex(this.file);
    hashIndex.deleteFile();
    Minibase.SystemCatalog.dropIndex(this.file);

    //Each query should print a one-line message at the end
    System.out.println("index: " + this.file + "dropped!");

  } // public void execute()

} // class DropIndex implements Plan
