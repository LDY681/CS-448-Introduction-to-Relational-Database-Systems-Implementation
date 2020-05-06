package heap;

import java.util.*;
import global.* ;
import chainexception.ChainException;

/**
 * A HeapScan object is created only through the function openScan() in the
 * HeapFile class. It supports the getNext interface which will simply retrieve
 * the next record in the file.
 */
public class HeapScan implements GlobalConst {

  HFPage currHF;  //current HFPage in the HeapFile
  RID currRid;  //current record's rid
  Iterator<PageId> pageIdIt;  //iterator through HeapFile's pageIdList

  /**
   * Constructs a file scan by pinning the directoy header page and initializing
   * iterator fields.
   */
  protected HeapScan(HeapFile hf) {

    //initialize iterator
    pageIdIt = hf.pageIdList.iterator();

    //initialize currHF to be the header page (first page in heapfile)
    Page newPage = new Page();
    PageId firstPageno = pageIdIt.next();
    Minibase.BufferManager.pinPage(firstPageno, newPage, false);  //Iterator.next() is the first item
    currHF = new HFPage(newPage);

    //initialize currRid to be the first record in header page
    currRid = currHF.firstRecord();

    //unpin it once done using newPage
    //Minibase.BufferManager.unpinPage(firstPageno, false);  //unpin firstPageno
  } //DONE

  /**
   * Called by the garbage collector when there are no more references to the
   * object; closes the scan if it's still open.
   */
  protected void finalize() throws Throwable {
    //PUT YOUR CODE HERE
    close();
    //System.out.println("Finalized!");
  } //DONE

  /**
   * Closes the file scan, releasing any pinned pages.
   */
  public void close() throws ChainException{
    //Reset class variables
    currHF = null;
    currRid = null;
    pageIdIt = null;
  } //DONE

  /**
   * Returns true if there are more records to scan, false otherwise.
   */
  public boolean hasNext() {
    //if current page is the last page
    return pageIdIt.hasNext();
  } //TODO: more records to scan, not more pageId to scan

  /**
   * Gets the next record in the file scan.
   * 
   * @param rid output parameter that identifies the returned record
   * @throws IllegalStateException if the scan has no more elements
   */
  public Tuple getNext(RID rid) {
    //PUT YOUR CODE HERE
    //if (!this.hasNext()){
    //  throw new IllegalStateException("the scan has no more elements");
    //}
    //else{

      //If there is more record in currentHF page
      if(currRid != null)
      {
        //System.out.println("currRid is NOT NULL: page_"  + currRid.pageno.pid + " slotno_" + currRid.slotno);
        rid.copyRID(currRid); //write currRid to output param rid
        Tuple tuple = new Tuple(currHF.selectRecord(currRid), 0, currHF.selectRecord(currRid).length);
        currRid = currHF.nextRecord(currRid); //update currRid to nextRecord
        return tuple;
      }

      //If there is no more record in currentHF page
      if(currRid == null){
        //We are done with currHf
        PageId currPageId = currHF.getCurPage();
        Minibase.BufferManager.unpinPage(currPageId, false);  //unpin current pageId's page

        //if there is more page to go next to
        if(pageIdIt.hasNext()){
          PageId nextPageId = pageIdIt.next();  //get next pageId
          Minibase.BufferManager.pinPage(nextPageId, currHF, false);  //pin next pageId's page and write to currHF
          currRid = currHF.firstRecord(); //update currRid to first record in newHF

          if(currRid == null){  //if nextPage has 0 record (maybe it's the last page)
            Minibase.BufferManager.unpinPage(nextPageId, false);  //unpin next pageId's page
            return null;
          }else{  //return the first tuple in next page
            rid.copyRID(currRid); //write currRid to output param rid
            Tuple tuple = new Tuple(currHF.selectRecord(currRid), 0, currHF.selectRecord(currRid).length);
            //System.out.println("currRid is NULL & Get Next Page's firstRecord: page_"  + currRid.pageno.pid + " slotno_" + currRid.slotno);
            currRid = currHF.nextRecord(currRid); //update currRid to nextRecord
            return tuple;
          }
        }else{
          //System.out.println("currRId is NULL & last page in list! currHF is : page_"  + currHF.getCurPage().pid);
          return null;
        }
      }
      return null;
    }
  //}

} // public class HeapScan implements GlobalConst
