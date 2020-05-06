package parser;

/**
 * Start node of the AST.
 */
public class AST_Start extends SimpleNode {

  public AST_Start(int id) {
    super(id);
  }

  public AST_Start(MiniSql p, int id) {
    super(p, id);
  }

  /** True if this is the "help" command; false otherwise. */
  public boolean isHelp = false;
  
  /** True if this is the "stats" command; false otherwise. */
  public boolean isStats = false;

  /** True if this is an "quit" command; false otherwise. */
  public boolean isQuit = false;

  /**
   * Gets the type of the parsed statement.
   * 
   * @see parser.MiniSqlTreeConstants
   */
  public int getType() {
    return ((SimpleNode) children[0]).id;
  }

  /**
   * Gets the default statement node.
   */
  public Node getStmt() {
    return children[0];
  }

} // public class AST_Start extends SimpleNode
