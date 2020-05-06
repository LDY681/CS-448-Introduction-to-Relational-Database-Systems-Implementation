package parser;

/**
 * AST node for DESCRIBE statements.
 */
public class AST_Describe extends SimpleNode {

  public AST_Describe(int id) {
    super(id);
  }

  public AST_Describe(MiniSql p, int id) {
    super(p, id);
  }

  /** Name of the table to describe. */
  protected String fileName;

  /**
   * Gets the name of the table to describe.
   */
  public String getFileName() {
    return fileName;
  }

} // public class AST_Describe extends SimpleNode
