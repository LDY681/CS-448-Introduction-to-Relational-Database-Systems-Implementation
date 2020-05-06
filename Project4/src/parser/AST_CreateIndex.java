package parser;

/**
 * AST node for CREATE INDEX statements.
 */
public class AST_CreateIndex extends SimpleNode {

  public AST_CreateIndex(int id) {
    super(id);
  }

  public AST_CreateIndex(MiniSql p, int id) {
    super(p, id);
  }

  /** Name of the index to create. */
  protected String fileName;

  /** Name of the table to index. */
  protected String ixTable;

  /** Name of the column to index. */
  protected String ixColumn;

  /**
   * Gets the name of the index to create.
   */
  public String getFileName() {
    return fileName;
  }

  /**
   * Gets the name of the table to index.
   */
  public String getIxTable() {
    return ixTable;
  }

  /**
   * Gets the name of the column to index.
   */
  public String getIxColumn() {
    return ixColumn;
  }

} // public class AST_CreateIndex extends SimpleNode
