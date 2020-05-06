package parser;

import java.util.ArrayList;

/**
 * AST node for ORDER BY clauses.
 */
public class AST_OrderBy extends SimpleNode {

  public AST_OrderBy(int id) {
    super(id);
  }

  public AST_OrderBy(MiniSql p, int id) {
    super(p, id);
  }

  /** Names of the columns. */
  protected ArrayList<String> names = new ArrayList<String>();

  /** Which columns are DESC. */
  protected ArrayList<Boolean> descs = new ArrayList<Boolean>();

  /**
   * Adds a new column name to the list.
   */
  protected void addName(String name) {
    names.add(name);
    descs.add(false);
  }

  /**
   * Sets the DESC state of the last column name added.
   */
  protected void setDesc() {
    descs.set(descs.size() - 1, true);
  }

} // public class AST_OrderBy extends SimpleNode
