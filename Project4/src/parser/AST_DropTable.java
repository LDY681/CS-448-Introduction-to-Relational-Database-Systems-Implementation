package parser;

/**
 * AST node for DROP TABLE statements.
 */
public class AST_DropTable extends SimpleNode {

  public AST_DropTable(int id) {
    super(id);
  }

  public AST_DropTable(MiniSql p, int id) {
    super(p, id);
  }

  /** Name of the table to drop. */
  protected String fileName;

  /**
   * Gets the name of the table to drop.
   */
  public String getFileName() {
    return fileName;
  }

} // public class AST_DropTable extends SimpleNode
