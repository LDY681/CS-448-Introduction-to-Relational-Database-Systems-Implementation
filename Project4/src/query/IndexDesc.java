package query;

import relop.Tuple;

/**
 * Index descriptor, i.e. a strongly-typed version of an index catalog tuple.
 */
class IndexDesc {

  /** Name of the index file. */
  public String indexName;

  /** Name of the referenced table. */
  public String tableName;

  /** Name of the indexed column. */
  public String columnName;

  /**
   * Constructs an IndexDesc from the given index catalog tuple.
   */
  public IndexDesc(Tuple tuple) {
    indexName = tuple.getStringFld(0);
    tableName = tuple.getStringFld(1);
    columnName = tuple.getStringFld(2);
  }

} // class IndexDesc
