package parser;

import java.util.ArrayList;

/**
 * Dynamic list of table or column identifiers.
 */
public class AST_IdentList extends SimpleNode {

  public AST_IdentList(int id) {
    super(id);
  }

  public AST_IdentList(MiniSql p, int id) {
    super(p, id);
  }

  /** Names of the columns/tables. */
  protected ArrayList<String> names = new ArrayList<String>();

  /**
   * Gets the names of the columns/tables.
   */
  public String[] getNames() {
    return names.toArray(new String[names.size()]);
  }

} // public class AST_ColumnList extends SimpleNode
