package query;

import global.AttrType;
import global.Minibase;
import relop.Predicate;
import relop.Schema;

/**
 * Uses the catalogs to perform final type checking for query statements.
 */
class QueryCheck {

  /**
   * Private constructor (static class).
   */
  private QueryCheck() {
  }

  /**
   * Checks whether a file name does not exist.
   */
  public static void fileNotExists(String fileName) throws QueryException {

    // check for the file entry
    if (Minibase.DiskManager.get_file_entry(fileName) != null) {
      throw new QueryException("file '" + fileName + "' already exists");
    }

  } // public static void fileExists(String fileName, boolean exists)

  /**
   * Checks whether a table exists.
   * 
   * @return schema of the table
   */
  public static Schema tableExists(String tableName) throws QueryException {

    // check for the catalog entry
    if (Minibase.SystemCatalog.getFileRID(tableName, true) == null) {
      throw new QueryException("table '" + tableName + "' doesn't exist");
    }
    return Minibase.SystemCatalog.getSchema(tableName);

  } // public static Schema tableExists(String tableName) throws QueryException

  /**
   * Checks whether an index exists.
   */
  public static void indexExists(String indexName) throws QueryException {

    // check for the catalog entry
    if (Minibase.SystemCatalog.getFileRID(indexName, false) == null) {
      throw new QueryException("index '" + indexName + "' doesn't exist");
    }

  } // public static void indexExists(String indexName) throws QueryException

  /**
   * Checks whether a column exists.
   * 
   * @return field number of the column
   */
  public static int columnExists(Schema schema, String columnName)
      throws QueryException {

    // check for the catalog entry
    int fldno = schema.fieldNumber(columnName);
    if (fldno < 0) {
      throw new QueryException("column '" + columnName + "' doesn't exist");
    }
    return fldno;

  } // public static int columnExists(String tableName, String columnName)

  /**
   * Checks whether values to insert matches the table schema.
   * 
   * @throws QueryException if wrong number of columns or invalid values
   */
  public static void insertValues(Schema schema, Object[] values)
      throws QueryException {

    // validate the number of values
    if (schema.getCount() != values.length) {
      throw new QueryException("invalid number of values");
    }

    // type check each value
    for (int i = 0; i < values.length; i++) {
      if (AttrType.getType(values[i]) != schema.fieldType(i)) {
        throw new QueryException("invalid column value '" + values[i] + "'");
      }
    }

  } // public static void insertValues(Schema schema, Object[] values)

  /**
   * Checks whether field to update match the table schema.
   * 
   * @return the field numbers of the columns to update.
   * @throws QueryException if a column name doesn't exist
   */
  public static int[] updateFields(Schema schema, String[] fields)
      throws QueryException {

    // convert field names to field numbers
    int[] fldnos = new int[fields.length];
    for (int i = 0; i < fields.length; i++) {
      fldnos[i] = schema.fieldNumber(fields[i]);
      if (fldnos[i] < 0) {
        throw new QueryException("invalid column '" + fields[i] + "'");
      }
    }
    return fldnos;

  } // public static int[] updateFields(Schema schema, String[] fields)

  /**
   * Checks whether values to update matches the table schema.
   * 
   * @throws QueryException if wrong number of columns or invalid values
   */
  public static void updateValues(Schema schema, int[] fldnos, Object[] values)
      throws QueryException {

    // type check each value
    for (int i = 0; i < values.length; i++) {
      if (AttrType.getType(values[i]) != schema.fieldType(fldnos[i])) {
        throw new QueryException("invalid column value '" + values[i] + "'");
      }

    } // for

  } // public static void insertValues(Schema schema, Object[] values)

  /**
   * Checks whether selection predicates (in CNF) match the given schema.
   * 
   * @throws QueryException if any predicates are invalid
   */
  public static void predicates(Schema schema, Predicate[][] preds)
      throws QueryException {

    // for each predicate in each conjunct
    for (int i = 0; i < preds.length; i++) {
      for (int j = 0; j < preds[i].length; j++) {
        if (preds[i][j].validate(schema) == false) {
          throw new QueryException("invalid predicate '"
              + preds[i][j].toString() + "'");
        }
      }
    }

  } // public static void predicates(Schema schema, Predicate[][] preds)

} // class QueryCheck
