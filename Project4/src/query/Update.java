package query;

import global.Minibase;
import global.RID;
import global.SearchKey;
import heap.HeapFile;
import index.HashIndex;
import parser.AST_Update;
import relop.FileScan;
import relop.Iterator;
import relop.Predicate;
import relop.Schema;
import relop.Selection;
import relop.Tuple;

/**
 * Execution plan for updating tuples.
 */
class Update implements Plan {

  /** Name of the table to update. */
  protected String fileName;

  /** Schema of the table to update. */
  protected Schema schema;

  /** Field numbers to update. */
  protected int[] fldnos;

  /** Actual values to update in the table. */
  protected Object[] values;

  /** Predicates to evaluate before updating. */
  protected Predicate[][] preds;

  /**
   * Optimizes the plan, given the parsed query.
   * 
   * @throws QueryException if invalid column names, values, or pedicates
   */
  public Update(AST_Update tree) throws QueryException {

    // make sure the table exists
    fileName = tree.getFileName();
    schema = QueryCheck.tableExists(fileName);

    // get and validate the field numbers
    String[] fields = tree.getColumns();
    fldnos = QueryCheck.updateFields(schema, fields);

    // get and validate the column values
    values = tree.getValues();
    QueryCheck.updateValues(schema, fldnos, values);

    // get and validate the column predicates
    preds = tree.getPredicates();
    QueryCheck.predicates(schema, preds);

  } // public Update(AST_Update tree) throws QueryException

  /**
   * Executes the plan and prints applicable output.
   */
  public void execute() {

    // build a file scan to find RIDs to update
    HeapFile file = new HeapFile(fileName);
    FileScan scan = new FileScan(schema, file);

    // build a selection chain for the predicates
    Iterator iter = scan;
    for (int i = 0; i < preds.length; i++) {
      iter = new Selection(iter, preds[i]);
    }

    // only update indexes where keys are affected
    IndexDesc[] inds = Minibase.SystemCatalog.getIndexes(fileName, schema,
        fldnos);

    // update each tuple in place
    int updcnt = 0;
    while (iter.hasNext()) {

      // get the next tuple and its RID
      Tuple tuple = iter.getNext();
      RID rid = scan.getLastRID();

      // delete index entries with old key
      for (IndexDesc ind : inds) {
        SearchKey key = new SearchKey(tuple.getField(ind.columnName));
        new HashIndex(ind.indexName).deleteEntry(key, rid);
      }

      // update each field separately
      for (int i = 0; i < fldnos.length; i++) {
        tuple.setField(fldnos[i], values[i]);
      }
      file.updateRecord(rid, tuple.getData());
      updcnt++;

      // insert index entries with new key
      for (IndexDesc ind : inds) {
        SearchKey key = new SearchKey(tuple.getField(ind.columnName));
        new HashIndex(ind.indexName).insertEntry(key, rid);
      }
      break;

    } // while
    iter.close();

    // print the output message
    System.out.println(updcnt + " rows affected.");

  } // public void execute()

} // class Update implements Plan
