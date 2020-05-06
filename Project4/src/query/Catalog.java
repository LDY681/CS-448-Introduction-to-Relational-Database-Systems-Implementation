package query;

import global.AttrType;
import global.GlobalConst;
import global.RID;
import heap.HeapFile;

import java.util.ArrayList;

import relop.FileScan;
import relop.Schema;
import relop.Tuple;

/**
 * <h3>Minibase System Catalog</h3>
 * Maintains metadata about every table, column, and index in the database; this
 * information itself is stored in tables (i.e. heap files).
 */
public class Catalog implements GlobalConst {

  /** Name of the relation catalog. */
  protected static final String REL_CAT = "mb_rel";

  /** Name of the attribute catalog. */
  protected static final String ATT_CAT = "mb_att";

  /** Name of the index catalog. */
  protected static final String IND_CAT = "mb_ind";

  // --------------------------------------------------------------------------

  /** Schema of the relation catalog. */
  protected Schema s_rel;

  /** Schema of the attribute catalog. */
  protected Schema s_att;

  /** Schema of the index catalog. */
  protected Schema s_ind;

  /** Heap file for the relation catalog. */
  protected HeapFile f_rel;

  /** Heap file for the attribute catalog. */
  protected HeapFile f_att;

  /** Heap file for the index catalog. */
  protected HeapFile f_ind;

  // --------------------------------------------------------------------------

  /**
   * Opens the system catalog if it exists, otherwise initializes an empty one.
   * 
   * @param exists true if the catalog tables already exist, false otherwise
   */
  public Catalog(boolean exists) {

    // initialize the hard-coded schemas
    initSchemas();

    // create or open the catalog files
    f_rel = new HeapFile(REL_CAT);
    f_att = new HeapFile(ATT_CAT);
    f_ind = new HeapFile(IND_CAT);

    // add default records, if needed
    if (!exists) {
      initCatFiles();
    }

  } // public Catalog()

  /**
   * Initializes hard-coded schema for each catalog file.
   */
  protected void initSchemas() {

    s_rel = new Schema(2);
    s_rel.initField(0, AttrType.STRING, NAME_MAXLEN, "relName");
    s_rel.initField(1, AttrType.INTEGER, 4, "recCount");

    s_att = new Schema(5);
    s_att.initField(0, AttrType.STRING, NAME_MAXLEN, "relName");
    s_att.initField(1, AttrType.INTEGER, 4, "fldno");
    s_att.initField(2, AttrType.INTEGER, 4, "type");
    s_att.initField(3, AttrType.INTEGER, 4, "length");
    s_att.initField(4, AttrType.STRING, NAME_MAXLEN, "attName");

    s_ind = new Schema(3);
    s_ind.initField(0, AttrType.STRING, NAME_MAXLEN, "indName");
    s_ind.initField(1, AttrType.STRING, NAME_MAXLEN, "relName");
    s_ind.initField(2, AttrType.STRING, NAME_MAXLEN, "attName");

  } // protected void initSchema()

  /**
   * Inserts initial records into the catalog files.
   */
  protected void initCatFiles() {

    // get the schema sizes
    int relcnt = s_rel.getCount();
    int attcnt = s_att.getCount();
    int indcnt = s_ind.getCount();

    // insert a row for each catalog relation
    Tuple tuple = new Tuple(s_rel);
    tuple.setAllFields(REL_CAT, 3);
    tuple.insertIntoFile(f_rel);
    tuple.setAllFields(ATT_CAT, relcnt + attcnt + indcnt);
    tuple.insertIntoFile(f_rel);
    tuple.setAllFields(IND_CAT, 0);
    tuple.insertIntoFile(f_rel);

    // insert a row for each catalog attribute
    tuple = new Tuple(s_att);
    for (int i = 0; i < relcnt; i++) {
      tuple.setAllFields(REL_CAT, i, s_rel.fieldType(i), s_rel.fieldLength(i),
          s_rel.fieldName(i));
      tuple.insertIntoFile(f_att);
    }
    for (int i = 0; i < attcnt; i++) {
      tuple.setAllFields(ATT_CAT, i, s_att.fieldType(i), s_att.fieldLength(i),
          s_att.fieldName(i));
      tuple.insertIntoFile(f_att);
    }
    for (int i = 0; i < indcnt; i++) {
      tuple.setAllFields(IND_CAT, i, s_ind.fieldType(i), s_ind.fieldLength(i),
          s_ind.fieldName(i));
      tuple.insertIntoFile(f_att);
    }

  } // protected void initCatFiles()

  // --------------------------------------------------------------------------

  /**
   * Gets the RID of the file's catalog entry, or null if it doesn't exist.
   * 
   * @param fileName name of the table/index file
   * @param isTable true if a table, false if an index
   */
  public RID getFileRID(String fileName, boolean isTable) {

    // create the appropriate scan
    FileScan scan;
    if (isTable) {
      scan = new FileScan(s_rel, f_rel);
    } else {
      scan = new FileScan(s_ind, f_ind);
    }

    // find the relation catalog row
    while (scan.hasNext()) {
      Tuple tuple = scan.getNext();
      if (tuple.getStringFld(0).equalsIgnoreCase(fileName)) {
        RID rid = scan.getLastRID();
        scan.close();
        return rid;
      }
    }

    // otherwise, record not found
    scan.close();
    return null;

  } // public RID getFileEntry(String fileName, boolean isTable)

  /**
   * Adds a newly created table to the catalog.
   */
  public void createTable(String fileName, Schema schema) {

    // add the relation catalog row
    Tuple tuple = new Tuple(s_rel);
    tuple.setAllFields(fileName, 0);
    tuple.insertIntoFile(f_rel);

    // add the attribute catalog rows
    tuple = new Tuple(s_att);
    for (int i = 0; i < schema.getCount(); i++) {
      tuple.setAllFields(fileName, i, schema.fieldType(i), schema
          .fieldLength(i), schema.fieldName(i));
      tuple.insertIntoFile(f_att);
    }

  } // public void createTable(String fileName, Schema schema)

  /**
   * Gets the Schema for the given table.
   */
  public Schema getSchema(String fileName) {

    // scan the applicable catalog tuples
    ArrayList<Tuple> tuples = new ArrayList<Tuple>();
    FileScan scan = new FileScan(s_att, f_att);
    while (scan.hasNext()) {
      Tuple tuple = scan.getNext();
      if (tuple.getStringFld(0).equalsIgnoreCase(fileName)) {
        tuples.add(tuple);
      }
    }
    scan.close();

    // build the schema from the resulting tuples
    Schema schema = new Schema(tuples.size());
    for (int i = 0; i < tuples.size(); i++) {
      Tuple tuple = tuples.get(i);
      schema.initField(i, tuple.getIntFld(2), tuple.getIntFld(3), tuple
          .getStringFld(4));
    }
    return schema;

  } // public Schema getSchema(String fileName)

  /**
   * Removes an existing table from the catalog.
   */
  public void dropTable(String fileName) {

    // find and remove the relation catalog row
    RID rid = getFileRID(fileName, true);
    f_rel.deleteRecord(rid);

    // find and remove all attribute catalog rows
    ArrayList<RID> rids = new ArrayList<RID>();
    FileScan scan = new FileScan(s_att, f_att);
    while (scan.hasNext()) {
      Tuple tuple = scan.getNext();
      if (tuple.getStringFld(0).equalsIgnoreCase(fileName)) {

        // save the catalog RID for later deletion
        rids.add(scan.getLastRID());
      }
    }
    scan.close();
    for (int i = 0; i < rids.size(); i++) {
      f_att.deleteRecord(rids.get(i));
    }

  } // public void dropTable(String fileName)

  /**
   * Adds a newly created index to the catalog.
   */
  public void createIndex(String fileName, String ixTable, String ixColumn) {

    // add the index catalog row
    Tuple tuple = new Tuple(s_ind);
    tuple.setAllFields(fileName, ixTable, ixColumn);
    tuple.insertIntoFile(f_ind);

  } // public void createIndex(String fileName, String ixTable, String ixColumn)

  /**
   * Gets any indexes on a given table.
   */
  public IndexDesc[] getIndexes(String fileName) {
    return getIndexes(fileName, null, null);
  }

  /**
   * Gets any indexes on a given set of columns.
   */
  public IndexDesc[] getIndexes(String fileName, Schema schema, int[] fldnos) {

    // scan the index catalog for the table
    FileScan scan = new FileScan(s_ind, f_ind);
    ArrayList<IndexDesc> inds = new ArrayList<IndexDesc>();

    // for each index on the given table
    while (scan.hasNext()) {
      Tuple tuple = scan.getNext();
      if (tuple.getStringFld(1).equalsIgnoreCase(fileName)) {

        // add the index, if applicable
        if (schema == null) {
          inds.add(new IndexDesc(tuple));
        } else {

          // only add indexes on columns in the set
          int keyfld = schema.fieldNumber(tuple.getStringFld(2));
          for (int i = 0; i < fldnos.length; i++) {
            if (fldnos[i] == keyfld) {
              inds.add(new IndexDesc(tuple));
              break;
            }
          }
        } // else

      }
    } // while

    // close the scan and return the indexes
    scan.close();
    return inds.toArray(new IndexDesc[inds.size()]);

  } // public IndexDesc[] getIndexes(String fileName, Schema, int[] fldnos)

  /**
   * Removes an existing index from the catalog.
   */
  public void dropIndex(String fileName) {

    // find and remove the index catalog row
    RID rid = getFileRID(fileName, false);
    f_ind.deleteRecord(rid);

  } // public void dropIndex(String fileName)

} // public class Catalog implements GlobalConst
