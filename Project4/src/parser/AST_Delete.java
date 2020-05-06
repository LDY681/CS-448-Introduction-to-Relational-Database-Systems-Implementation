package parser;

import relop.Predicate;

/**
 * AST node for DELETE statements.
 */
public class AST_Delete extends SimpleNode {

  public AST_Delete(int id) {
    super(id);
  }

  public AST_Delete(MiniSql p, int id) {
    super(p, id);
  }

  /** Name of the table to delete from. */
  protected String fileName;

  /**
   * Gets the name of the table to delete from.
   */
  public String getFileName() {
    return fileName;
  }

  /**
   * Gets the WHERE clause predicates in Conjunctive Normal Form (a.k.a. product
   * of sums, i.e. AND expression of OR expressions).
   */
  public Predicate[][] getPredicates() {

    // special case: no predicates
    if (children == null) {
      return new Predicate[0][];
    }

    // otherwise, return them
    return ((AST_OrExpr) children[0]).getCNF();

  } // public Predicate[][] getPredicates()

} // public class AST_Delete extends SimpleNode
