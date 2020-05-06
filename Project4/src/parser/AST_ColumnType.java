package parser;

/**
 * Column type in a CREATE TABLE statement.
 */
public class AST_ColumnType extends SimpleNode {

  public AST_ColumnType(int id) {
    super(id);
  }

  public AST_ColumnType(MiniSql p, int id) {
    super(p, id);
  }

  /** Attribute type of the column. */
  protected int type;

  /** Size of the column (in bytes). */
  protected int size;

} // public class AST_ColumnType extends SimpleNode
