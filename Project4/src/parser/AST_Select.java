package parser;

import global.SortKey;

import java.util.ArrayList;

import relop.Predicate;

/**
 * AST node for SELECT statements.
 */
public class AST_Select extends SimpleNode {

  public AST_Select(int id) {
    super(id);
  }

  public AST_Select(MiniSql p, int id) {
    super(p, id);
  }

  /** True if this is an EXPLAIN statement; false otherwise. */
  public boolean isExplain = false;

  /** True if this is a DISTINCT selection; false otherwise. */
  public boolean isDistinct = false;

  /**
   * Gets the column names in the projection list (length 0 if '*').
   */
  public String[] getColumns() {
    return ((AST_IdentList) children[0]).getNames();
  }

  /**
   * Gets the tables to select from.
   */
  public String[] getTables() {
    return ((AST_IdentList) children[1]).getNames();
  }

  /**
   * Gets the WHERE clause predicates in Conjunctive Normal Form (a.k.a. product
   * of sums, i.e. AND expression of OR expressions).
   */
  public Predicate[][] getPredicates() {

    // special case: no predicates
    if ((children.length < 3) || (children[2] instanceof AST_OrderBy)) {
      return new Predicate[0][];
    }

    // otherwise, get and return them
    return ((AST_OrExpr) children[2]).getCNF();

  } // public Predicate[][] getPredicates()

  /**
   * Gets the columns to sort on.
   */
  public SortKey[] getOrders() {

    // find order by clause, if any
    int ix = 0;
    if ((children.length == 3) && (children[2] instanceof AST_OrderBy)) {
      ix = 2;
    } else if (children.length == 4) {
      ix = 3;
    } else {
      // special case: no order by clause
      return new SortKey[0];
    }

    // construct and return the orderings
    ArrayList<String> names = ((AST_OrderBy) children[ix]).names;
    ArrayList<Boolean> descs = ((AST_OrderBy) children[ix]).descs;
    SortKey[] orders = new SortKey[names.size()];
    for (int i = 0; i < orders.length; i++) {
      orders[i] = new SortKey(names.get(i), descs.get(i));
    }
    return orders;

  } // public SortKey[] getOrders()

} // public class AST_Select extends SimpleNode
