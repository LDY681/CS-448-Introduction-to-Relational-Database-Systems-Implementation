package parser;

import relop.Predicate;

/**
 * Expression predicate in a WHERE clause.
 */
public class AST_ExprPred extends SimpleNode {

  public AST_ExprPred(int id) {
    super(id);
  }

  public AST_ExprPred(MiniSql p, int id) {
    super(p, id);
  }

  /** AttrOperator value of the predicate. */
  protected int oper;

  /**
   * Returns the predicate form of this AST node.
   */
  protected Predicate getPredicate() {

    // get the operand nodes
    AST_ExprValue lv = (AST_ExprValue) children[0];
    AST_ExprValue rv = (AST_ExprValue) children[1];

    // construct the real predicate
    return new Predicate(oper, lv.type, lv.value, rv.type, rv.value);

  } // protected Predicate getPredicate()

} // public class AST_ExprPred extends SimpleNode
