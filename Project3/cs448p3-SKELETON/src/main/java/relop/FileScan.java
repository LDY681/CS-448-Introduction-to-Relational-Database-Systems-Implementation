package relop;

import global.RID;
import heap.HeapFile;
import heap.HeapScan;

/**
 * Wrapper for heap file scan, the most basic access method. This "iterator"
 * version takes schema into consideration and generates real tuples.
 */
public class FileScan extends Iterator {
	
	private HeapFile file = null; // needed for restart(), getFile();
	private HeapScan scan = null;
	private RID rid = null;
	private boolean isOpen;

  /**
   * Constructs a file scan, given the schema and heap file.
   */
  public FileScan(Schema aSchema, HeapFile aFile) {
	this.schema = aSchema;
	this.file = aFile;
	this.scan = file.openScan();
	this.rid = new RID();
	isOpen = true;
  }

  /**
   * Gives a one-line explanation of the iterator, repeats the call on any
   * child iterators, and increases the indent depth along the way.
   */
  public void explain(int depth) {
	  for(int i = 0; i < depth; i++){
		  System.out.printf("    ");
	  }
	  System.out.printf("FileScan\n");
  }

  /**
   * Restarts the iterator, i.e. as if it were just constructed.
   */
  public void restart() {
	  scan.close();
	  scan = file.openScan();
	  //rid = new RID();
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
  public boolean hasNext() {
	  return scan.hasNext();
  }

  /**
   * Gets the next tuple in the iteration.
   * 
   * @throws IllegalStateException if no more tuples
   */
  public Tuple getNext() {
	  return new Tuple(schema, scan.getNext(rid));
  }

  /**
   * Gets the RID of the last tuple returned.
   */
  public RID getLastRID() {
	  return rid;
  }
  
  // getter; added so HashJoin doesn't have to copy the file;
  public HeapFile getFile(){
	  return file;
  }

} // public class FileScan extends Iterator
