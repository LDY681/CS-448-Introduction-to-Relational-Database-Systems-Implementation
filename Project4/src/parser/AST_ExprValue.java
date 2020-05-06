package parser;

/**
 * Value (simple expression), or operand (in a WHERE cluase).
 */
public class AST_ExprValue extends SimpleNode {

  public AST_ExprValue(int id) {
    super(id);
  }

  public AST_ExprValue(MiniSql p, int id) {
    super(p, id);
  }

  /** AttrType of the value/operand. */
  protected int type;

  /** Parsed value of the value/operand. */
  protected Object value;

} // public class AST_ExprValue extends SimpleNode
