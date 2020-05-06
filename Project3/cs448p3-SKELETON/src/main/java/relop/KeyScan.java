package relop;

import global.RID;
import global.SearchKey;
import heap.HeapFile;
import index.HashIndex;
import index.HashScan;

/**
 * Wrapper for hash scan, an index access method.
 */
public class KeyScan extends Iterator {
    private HeapFile file; // needed for restart(), getFile();
    private HashScan scan;
    private boolean isOpen;
    private HashIndex index;
    private SearchKey key;

  /**
   * Constructs an index scan, given the hash index and schema.
   */
  public KeyScan(Schema aSchema, HashIndex aIndex, SearchKey aKey, HeapFile aFile) {
      this.schema = aSchema;
      this.file = aFile;
      this.index = aIndex;
      this.key = aKey;
      this.scan = index.openScan(key);
      isOpen = true;
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
      scan.close();
      scan = index.openScan(key);
      isOpen = true;
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
      scan.close();
      isOpen = false;
  }

  /**
   * Returns true if there are more tuples, false otherwise.
   */
  public boolean hasNext()  {
      return scan.hasNext();
  }

  /**
   * Gets the next tuple in the iteration.
   * 
   * @throws IllegalStateException if no more tuples
   */
  public Tuple getNext() {
      RID rid = scan.getNext();
      byte[] data = file.selectRecord(rid);

      if(data == null) {
          throw new IllegalStateException("no more tuples!");
      }else{
          Tuple nextTup = new Tuple(schema, data);
          return nextTup;
      }
  }

} // public class KeyScan extends Iterator
