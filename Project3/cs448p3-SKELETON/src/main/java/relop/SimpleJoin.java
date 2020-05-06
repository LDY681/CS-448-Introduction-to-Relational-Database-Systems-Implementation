package relop;

/**
 * The simplest of all join algorithms: nested loops (see textbook, 3rd edition,
 * section 14.4.1, page 454).
 */
public class SimpleJoin extends Iterator {

	private Iterator outer;
	private Iterator inner;
	private Predicate[] preds;
	
	private boolean startJoin = true;
	Tuple leftTuple;
	
	// boolean variable to indicate whether the pre-fetched tuple is consumed or not
	private boolean nextTupleIsConsumed;
	
	// pre-fetched tuple
	private Tuple nextTuple; 

	/**
	 * Constructs a join, given the left and right iterators and join predicates
	 * (relative to the combined schema).
	 */
	public SimpleJoin(Iterator left, Iterator right, Predicate... preds) {

		this.outer = left;
		this.inner = right;
		this.preds = preds;
		this.schema = Schema.join(left.schema, right.schema);
		
		nextTupleIsConsumed = true;
	}

	/**
	 * Gives a one-line explanation of the iterator, repeats the call on any
	 * child iterators, and increases the indent depth along the way.
	 */
	public void explain(int depth) {
		
		throw new UnsupportedOperationException("Not implemented");
	}

	/**
	 * Restarts the iterator, i.e. as if it were just constructed.
	 */
	public void restart() {

		outer.restart();
		nextTupleIsConsumed = true;
	}

	/**
	 * Returns true if the iterator is open; false otherwise.
	 */
	public boolean isOpen() {
		
		if (outer.isOpen())
			return true;

		return false;
	}

	/**
	 * Closes the iterator, releasing any resources (i.e. pinned pages).
	 */
	public void close() {

		outer.close();
		inner.close();
	}

	/**
	 * Returns true if there are more tuples, false otherwise.
	 * 
	 */
	public boolean hasNext() {

		if (!nextTupleIsConsumed)
			return true;
		
		if (!outer.hasNext())
		//if(!inner.hasNext() && !outer.hasNext()) // Piazza post 116
			return false;

		Tuple rightTuple;
		
		if (startJoin) {
			leftTuple = outer.getNext();
			startJoin = false;
		}

		while (true) {

			while (inner.hasNext()) {
				
				rightTuple = inner.getNext();

				// try to match
				nextTuple = Tuple.join(leftTuple, rightTuple, this.schema);
				for (int i = 0; i < preds.length; i++)
					if (preds[i].evaluate(nextTuple)) {
						nextTupleIsConsumed = false;
						return true;
					}
			}

			if (outer.hasNext()) {
				leftTuple = outer.getNext();
				inner.restart();
			}
			else
				return false;
		}
	}

	/**
	 * Gets the next tuple in the iteration.
	 * 
	 * @throws IllegalStateException if no more tuples
	 */
	public Tuple getNext() {
		
		nextTupleIsConsumed = true;
		return nextTuple;
	}
}
