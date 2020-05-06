package relop;

/**
 * The selection operator specifies which tuples to retain under a condition; in
 * Minibase, this condition is simply a set of independent predicates logically
 * connected by OR operators.
 */
public class Selection extends Iterator {
    private Iterator iterator;
    private Predicate[] predicates;
    private boolean isOpen;
    private Tuple next;

    /**
     * Constructs a selection, given the underlying iterator and predicates.
     */
    public Selection(Iterator aIter, Predicate... aPreds) {
        this.iterator = aIter;
        this.predicates = aPreds;
        this.schema = aIter.getSchema();
        this.iterator.restart();
        isOpen = true;
    }

    /**
     * Gives a one-line explanation of the iterator, repeats the call on any
     * child iterators, and increases the indent depth along the way.
     */
    public void explain(int depth) {
        throw new UnsupportedOperationException("Not implemented");
        //Your code here
    }

    /**
     * Restarts the iterator, i.e. as if it were just constructed.
     */
    public void restart() {
        iterator.restart();
        isOpen = true;  //as if it were just constructed, so I assume isOpen should be changed to true as well?
    }

    /**
     * Returns true if the iterator is open; false otherwise.
     */
    public boolean isOpen() {
        return isOpen;
    }

    /**
     * Closes the iterator, releasing any resources (i.e. pinned pages).
     */
    public void close() {
        iterator.close();
        isOpen = false;
    }

    /**
     * Returns true if there are more tuples, false otherwise.
     */
    public boolean hasNext() {
        while (iterator.hasNext()) {
            Tuple temp = iterator.getNext();

            //if meetPredicates then set next and return true
            if (meetPredicates(temp)){
                next = temp;
                return true;
            }
        }

        //if !meetPredicates, reset next and return false
        next = null;
        return false;
    }

    /**
     * Gets the next tuple in the iteration.
     *
     * @throws IllegalStateException if no more tuples
     */
    public Tuple getNext() {
        if(next != null) {
            return next;
        }else{
            throw new IllegalStateException("no more tuples");      //if no more tuple to return
        }
    }

    //whether the next tuple satisfy predicates
    public boolean meetPredicates(Tuple tuple) {
        for(Predicate p : predicates) {
            if(p.evaluate(tuple) == true) {
                return true;
            }
        }
        return false;
    }

}// public class Selection extends Iterator
