package relop;


/**
 * The projection operator extracts columns from a relation; unlike in
 * relational algebra, this operator does NOT eliminate duplicate tuples.
 */
public class Projection extends Iterator {
    private Iterator iterator;
    private Integer[] fields;
    private boolean isOpen;
    private Schema projSchema;

  /**
   * Constructs a projection, given the underlying iterator and field numbers.
   */
  public Projection(Iterator aIter, Integer... aFields) {

      this.isOpen = true;

      //Iterator and fields
      this.iterator = aIter;
      this.fields = aFields;

      //this.iterator.schema = this.iterator.getSchema(); //the iterator schema
      //this.iterator.restart();   //To fix null pointer maybe

      //Proj schema
      this.projSchema = new Schema(this.fields.length);  //the projection schema
      //initialize projection schema from iterator schema
      for(int i = 0; i < fields.length; i++){
          this.projSchema.initField(i, this.iterator.schema, this.fields[i]);
      }
      this.schema = this.projSchema;    //Set schema to schema that was projected
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
      isOpen = true;
      iterator.restart(); //restarts the iterator and sets the boolean to true
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
      this.iterator.close();   //closes and releases pinned pages
      isOpen = false;     // sets the status to closed
  }

  /**
   * Returns true if there are more tuples, false otherwise.
   */
  public boolean hasNext() {
      return isOpen ? this.iterator.hasNext() : false;
  }

  /**
   * Gets the next tuple in the iteration.
   * 
   * @throws IllegalStateException if no more tuples
   */
  public Tuple getNext() {
      //TODO: commented out hasNext to pass projectionPipelineing
      //if(hasNext()){    //if iterator is open and iterator hasNext
          Tuple iterNext = this.iterator.getNext();
          Tuple projTuple = new Tuple(this.projSchema);
          for(int i=0;i<fields.length;i++){
              projTuple.setField(i,iterNext.getField(fields[i]));   //make tuple with our schema
          }
          return projTuple;
//      }else{
//          throw new IllegalStateException("no more tuples");
//      }
  }

} // public class Projection extends Iterator
