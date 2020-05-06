package query;

import global.Minibase;
import global.RID;
import global.SearchKey;
import heap.HeapFile;
import index.HashIndex;

import java.util.ArrayList;

import parser.AST_Delete;
import relop.FileScan;
import relop.Iterator;
import relop.Predicate;
import relop.Schema;
import relop.Selection;
import relop.Tuple;

/**
 * Execution plan for deleting tuples.
 */
class Delete implements Plan {

  /** Name of the table to delete from. */
  protected String fileName;

  /** Schema of the table to delete from. */
  protected Schema schema;

  /** Predicates to evaluate before deleting. */
  protected Predicate[][] preds;

  /**
   * Optimizes the plan, given the parsed query.
   * 
   * @throws QueryException if table doesn't exist or predicates are invalid
   */
  public Delete(AST_Delete tree) throws QueryException {

    // make sure the table exists
    fileName = tree.getFileName();
    schema = QueryCheck.tableExists(fileName);

    // get and validate the column predicates
    preds = tree.getPredicates();
    QueryCheck.predicates(schema, preds);

  } // public Delete(AST_Delete tree) throws QueryException

  /**
   * Executes the plan and prints applicable output.
   */
  public void execute() {

    // build a file scan to find RIDs to delete
    HeapFile file = new HeapFile(fileName);
    FileScan scan = new FileScan(schema, file);

    // build a selection chain for the predicates
    Iterator iter = scan;
    for (int i = 0; i < preds.length; i++) {
      iter = new Selection(iter, preds[i]);
    }

    // get the set of RIDs to delete
    ArrayList<RID> rids = new ArrayList<RID>();
    IndexDesc[] inds = Minibase.SystemCatalog.getIndexes(fileName);
    while (iter.hasNext()) {

      // get the next tuple and its RID
      Tuple tuple = iter.getNext();
      RID rid = scan.getLastRID();

      // add its RID and maintain any indexes
      rids.add(rid);
      for (IndexDesc ind : inds) {

        // get the search key from the old tuple
        SearchKey key = new SearchKey(tuple.getField(ind.columnName));

        // delete the entry from the index file
        new HashIndex(ind.indexName).deleteEntry(key, rid);

      } // for

    } // while
    iter.close();

    // delete the records
    for (RID rid : rids) {
      file.deleteRecord(rid);
    }

    // print the output message
    System.out.println(rids.size() + " rows affected.");

  } // public void execute()

} // class Delete implements Plan
