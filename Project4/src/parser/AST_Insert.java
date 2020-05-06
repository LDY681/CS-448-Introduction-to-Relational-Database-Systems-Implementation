package parser;

/**
 * AST node for INSERT statements.
 */
public class AST_Insert extends SimpleNode {

  public AST_Insert(int id) {
    super(id);
  }

  public AST_Insert(MiniSql p, int id) {
    super(p, id);
  }

  /** Name of the table to insert into. */
  protected String fileName;

  /**
   * Gets the name of the table to insert into.
   */
  public String getFileName() {
    return fileName;
  }

  /**
   * Gets the parsed values to insert.
   */
  public Object[] getValues() {

    // bundle all children into one value array
    Object[] values = new Object[children.length];
    for (int i = 0; i < children.length; i++) {
      AST_ExprValue vnode = (AST_ExprValue) children[i];
      values[i] = vnode.value;
    }
    return values;

  } // public Object[] getValues()

} // public class AST_Insert extends SimpleNode
