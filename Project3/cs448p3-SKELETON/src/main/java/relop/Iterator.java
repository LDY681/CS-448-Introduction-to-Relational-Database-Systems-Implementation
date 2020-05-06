package relop;

/**
 * Minibase Iterators
 * Query execution is driven by a tree of relational operators, all of which are
 * implemented as iterators. Results are requested by successive "get next
 * tuple" calls on the root of the tree, which in turn makes similar calls to
 * child iterators throughout the tree. Intermediate nodes (i.e. join iterators)
 * drive the leaf-level nodes (i.e. file or index scan iterators).
 */
public abstract class Iterator {

  /** Schema for resulting tuples; must be set in all subclass constructors. */
  protected Schema schema;

  // --------------------------------------------------------------------------

  /**
   * Gives a one-line explanation of the iterator, repeats the call on any
   * child iterators, and increases the indent depth along the way.
   */
  public abstract void explain(int depth);

  /**
   * Outputs the indentation for the given depth.
   */
  protected void indent(int depth) {
    for (int i = 0; i < depth * 2; i++) {
      System.out.print(' ');
    }
  }

  /**
   * Restarts the iterator, i.e. as if it were just constructed.
   */
  public abstract void restart();

  /**
   * Returns true if the iterator is open; false otherwise.
   */
  public abstract boolean isOpen();

  /**
   * Closes the iterator, releasing any resources (i.e. pinned pages).
   */
  public abstract void close();

  /**
   * Returns true if there are more tuples, false otherwise.
   */
  public abstract boolean hasNext();

  /**
   * Gets the next tuple in the iteration.
   * 
   * @throws IllegalStateException if no more tuples
   */
  public abstract Tuple getNext();

  /**
   * Prints the schema, gets and prints all tuples, and closes the iterator.
   * 
   * @return number of tuples processed
   */
  public int execute() {
    result = ""; //Modification to store result of queries for test case comparison
    int cnt = 0;
    getSchema().print();
	Tuple next;
    while (hasNext()) {
      next = getNext();
      next.print();
	  //Modification to store result of queries for test case comparison
      result += next.toString() + "|"; 
      cnt++;
    }
	if(result.length() > 0)
		result = result.substring(0, result.length() - 1);
    close();
    return cnt;
  }

  /**
   * Called by the garbage collector when there are no more references to the
   * object; closes the iterator if it's still open.
   */
  protected void finalize() throws Throwable {
    if (isOpen()) {
      close();
    }
  }

public void setSchema(Schema schema) {
	this.schema = schema;
}

public Schema getSchema() {
	return schema;
}

// *** DO NOT CHANGE ANY EXISTING METHODS OR VARIABLES BELOW THIS LINE ***
//Store values for test cases only
//String format: row0|row1|row2|...rowN|
private String result;
public String getResult() { return result; }

} // public abstract class Iterator
