package parser;

/**
 * AST node for DROP INDEX statements.
 */
public class AST_DropIndex extends SimpleNode {

  public AST_DropIndex(int id) {
    super(id);
  }

  public AST_DropIndex(MiniSql p, int id) {
    super(p, id);
  }

  /** Name of the index to drop. */
  protected String fileName;

  /**
   * Gets the name of the index to drop.
   */
  public String getFileName() {
    return fileName;
  }

} // public class AST_DropIndex extends SimpleNode
