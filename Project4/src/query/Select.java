package query;

import parser.AST_Select;
import bufmgr.*;
import diskmgr.*;
import heap.*;
import index.*;
import relop.*;
import global.*;
import parser.*;

/**
 * Execution plan for selecting tuples.
 */
class Select implements Plan {

  private boolean isExplain;  //whether this is an explain query
  private Predicate[][] preds;  //All predicates from the tree

  private String[] tables;  //tables to be selected from
  private Schema[] schemas; //schemas of those tables that are selected
  private Schema schema;  //The final schema built from all schemas of those tables

  private String[] columns; //columns from AST tree
  private Integer[] fieldNos; //field number of the column
  /**
   * Optimizes the plan, given the parsed query.
   * 
   * @throws QueryException if validation fails
   */
  public Select(AST_Select tree) throws QueryException {

    //tables, schemas, schema
    this.tables = tree.getTables();
    this.schemas = new Schema[this.tables.length];

    //set current schema to null first and run through to table to build the final schema
    //schema is guaranteed to be not null after iteration unless there are no tables
    this.schema = null;
    for(int i = 0; i < this.tables.length; i++){
      if(this.schema == null){   //if first table (schema s1 doesn't exist)
        this.schema = QueryCheck.tableExists(this.tables[i]); //set schema to be first table's schema
        this.schemas[i] = this.schema;  //update schemas
      }else{
        this.schemas[i] = QueryCheck.tableExists(this.tables[i]); //update schemas[i] first
        this.schema = Schema.join(this.schema,this.schemas[i]); //extend schema
      }
    }

    //columns and filedNos
    columns = tree.getColumns();
    fieldNos = new Integer[columns.length];
    for(int i = 0; i < columns.length; i++){
      fieldNos[i] = QueryCheck.columnExists(this.schema,columns[i]);
    }


    //isExplain & preds
    this.isExplain = tree.isExplain;
    preds = tree.getPredicates();
    QueryCheck.predicates(this.schema, preds);
  } // public Select(AST_Select tree) throws QueryException

  /**
   * Executes the plan and prints applicable output.
   */
  public void execute() {

    //Create a blank iterator
    Iterator iter = null;

    //Iterate through all tables to update schema with simpleJoins
    for (int i = 0; i < this.tables.length; i++) {
      if (iter == null) {
        iter = new FileScan(this.schemas[i], new HeapFile(this.tables[i]));
      }else{
        Iterator currSchemaIter = new FileScan(this.schemas[i], new HeapFile(this.tables[i]));
        iter = new SimpleJoin(iter, currSchemaIter);
      }
    }

    //Iterate through all predicates to update schema with selections
    for (Predicate[] pred : preds) {
      iter = new Selection(iter, pred);
    }

    //Update schema with projection
    if (columns.length > 0) {
      iter = new Projection(iter, fieldNos);
    }

    if(iter != null) {
      if(this.isExplain) {  //if explain, explain(0)
        iter.explain(0);
        System.out.println("Select plan explained!");
      }else { //if not, execute and show affected rows
        int rowCnt = iter.execute();
        System.out.println(rowCnt + " rows affected!");
      }
    }
  } // public void execute()

} // class Select implements Plan
