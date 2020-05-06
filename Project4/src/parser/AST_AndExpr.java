package parser;

import relop.Predicate;

/**
 * AST node for AND expressions in WHERE clauses.
 */
public class AST_AndExpr extends SimpleNode {

  public AST_AndExpr(int id) {
    super(id);
  }

  public AST_AndExpr(MiniSql p, int id) {
    super(p, id);
  }

  /**
   * Returns all predicates "AND-ed" at this node.
   */
  protected Predicate[] getPredicates() {

    // bundle all children into one predicate array
    Predicate[] preds = new Predicate[children.length];
    for (int i = 0; i < children.length; i++) {
      AST_ExprPred child = (AST_ExprPred) children[i];
      preds[i] = child.getPredicate();
    }
    return preds;

  } // protected Predicate[] getPredicates()

} // public class AST_AndExpr extends SimpleNode
