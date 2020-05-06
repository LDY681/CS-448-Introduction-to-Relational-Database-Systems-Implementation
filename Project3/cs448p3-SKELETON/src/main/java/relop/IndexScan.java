package relop;

import global.RID;
import global.SearchKey;
import heap.HeapFile;
import index.BucketScan;
import index.HashIndex;

/**
 * Wrapper for bucket scan, an index access method.
 */
public class IndexScan extends Iterator {
    private HeapFile file; // needed for restart(), getFile();
    private BucketScan scan;
    private boolean isOpen;
    private HashIndex index;
    private RID rid;

  /**
   * Constructs an index scan, given the hash index and schema.
   */
  public IndexScan(Schema schema, HashIndex index, HeapFile file) {
      this.schema = schema;
      this.file = file;
      this.index = index;
      this.scan = index.openScan();
      isOpen = true;
      rid = null;
  }

  /**
   * Gives a one-line explaination of the iterator, repeats the call on any
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
      scan.close();
      scan = index.openScan();
      rid = null;
  }

  /**
   * Returns true if the iterator is open; false otherwise.
   */
  public boolean isOpen()  {
      return isOpen;
  }


    /**
   * Closes the iterator, releasing any resources (i.e. pinned pages).
   */
  public void close()  {
      scan.close();
      isOpen = false;
      rid = null;
  }

  /**
   * Returns true if there are more tuples, false otherwise.
   */
  public boolean hasNext()   {
      return scan.hasNext();
  }

  /**
   * Gets the next tuple in the iteration.
   * 
   * @throws IllegalStateException if no more tuples
   */
  public Tuple getNext() {
      rid =  scan.getNext();
      if( rid == null){
          throw new IllegalStateException("no more tuples!");
      }else{
          byte [] data = file.selectRecord(rid);
          if( data == null) {
              throw new IllegalStateException("no more tuples!");
          }else{
              Tuple nextTup = new Tuple(schema, data);
              return nextTup;
          }
      }
  }

  /**
   * Gets the key of the last tuple returned.
   */
  public SearchKey getLastKey() {
      return scan.getLastKey();
  }

  /**
   * Returns the hash value for the bucket containing the next tuple, or maximum
   * number of buckets if none.
   */
  public int getNextHash() {
      return scan.getNextHash();
  }

} // public class IndexScan extends Iterator
