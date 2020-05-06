package parser;

import relop.Predicate;

/**
 * AST node for UPDATE statements.
 */
public class AST_Update extends SimpleNode {

  public AST_Update(int id) {
    super(id);
  }

  public AST_Update(MiniSql p, int id) {
    super(p, id);
  }

  /** Name of the table to update. */
  protected String fileName;

  /**
   * Gets the name of the table to update.
   */
  public String getFileName() {
    return fileName;
  }

  /**
   * Gets the names of the columns to update.
   */
  public String[] getColumns() {
    return ((AST_IdentList) children[0]).getNames();
  }

  /**
   * Gets the parsed values to update.
   */
  public Object[] getValues() {

    // bundle all grandchildren into one value array
    int cnt = children[0].jjtGetNumChildren();
    Object[] values = new Object[cnt];
    for (int i = 0; i < cnt; i++) {
      AST_ExprValue vnode = (AST_ExprValue) children[0].jjtGetChild(i);
      values[i] = vnode.value;
    }
    return values;

  } // public Object[] getValues()

  /**
   * Gets the WHERE clause predicates in Conjunctive Normal Form (a.k.a. product
   * of sums, i.e. AND expression of OR expressions).
   */
  public Predicate[][] getPredicates() {

    // special case: no predicates
    if (children.length < 2) {
      return new Predicate[0][];
    }

    // otherwise, return them
    return ((AST_OrExpr) children[1]).getCNF();

  } // public Predicate[][] getPredicates()

} // public class AST_Update extends SimpleNode
