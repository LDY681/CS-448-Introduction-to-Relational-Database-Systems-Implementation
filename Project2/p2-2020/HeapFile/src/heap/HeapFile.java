package heap;

import global.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * <h3>Minibase Heap Files</h3>
 * A heap file is an unordered set of records, stored on a set of pages. This
 * class provides basic support for inserting, selecting, updating, and deleting
 * records. Temporary heap files are used for external sorting and in other
 * relational operators. A sequential scan of a heap file (via the Scan class)
 * is the most basic access method.
 */
public class HeapFile implements GlobalConst {
  /**
   * TODO: sorted arraylist, insertion/deletion/update by binary search to achieve logTime
   * If the given name already denotes a file, this opens it; otherwise, this
   * creates a new empty file. A null name produces a temporary heap file which
   * requires no DB entry.
   */

  protected ArrayList<PageId> pageIdList;  //list of pageId objects
  protected ArrayList<Integer> pidList;  //The actual page Id value, pageId.pid
  protected int recCount; //Number of records in a file
  protected HFPage curr;  //current page
  protected int fileStatus;  //0: normal, 1: deleted
  private String name;  //the name of the heap file

  public HeapFile(String name) {
    /** boolean PIN_MEMCPY = true; Replace the buffer frame with an existing memory page. & skip reading from page, page as output
     * boolean PIN_DISKIO = false; Replace an existing memory page with the current disk page. & read from page, page as input
     * boolean UNPIN_DIRTY = true; Forces the page to be written to disk when unpinned.
     * boolean UNPIN_CLEAN = false; Optimization to avoid writing to disk when unpinned.
     */

    //Initialize constructor
    this.name = name;
    this.fileStatus = 0;
    this.recCount = 0;
    pageIdList = new ArrayList<>();
    pidList = new ArrayList<>();

    //newPage: the first page in the file entry
    Page newPage = new Page();

    //if a page is not temporary
    if (name != null){
      //1.attempt to get from disk
      //Returns: PageId of the file's first page, or null if the file doesn't exist
      PageId pageno = Minibase.DiskManager.get_file_entry(name);

      //2.create to disk if not existed
      if (pageno == null){
        //System.out.println("file " + this.name + " doesn't exist. Add file to disk");
        //Allocates a set of new pages, and pins the first one in an appropriate frame in the buffer pool.
        //firstpg - holds the contents of the first page
        //run_size - number of pages to allocate
        pageno = Minibase.BufferManager.newPage(newPage,1);

        //Adds a file entry to the header page(s); each entry contains the name of the file and the PageId of the file's first page.
        Minibase.DiskManager.add_file_entry(name, pageno);

        //unpin the page (was pinned by newPage())
        Minibase.BufferManager.unpinPage(pageno, UNPIN_DIRTY);

        //add page and pageId to pageList and pageIdList maintained by us
        pageIdList.add(pageno);
        pidList.add(pageno.pid);

        /*Pins a disk page into the buffer pool. If the page is already pinned, this simply increments the pin count.
         Otherwise, this selects another page in the pool to replace, flushing it to disk if dirty.
          pageno - identifies the page to pin
          page - holds contents of the page, either an input or output param
          skipRead - PIN_MEMCPY (replace in pool); PIN_DISKIO (read the page in)
        */
        Minibase.BufferManager.pinPage(pageno, newPage, PIN_DISKIO);

        //curr construct as newPage, set page's pageno
        curr = new HFPage(newPage);
        curr.initDefaults();  //TODO: this is GOLD!!!!!!!!!
        curr.setCurPage(pageno);

        Minibase.BufferManager.unpinPage(pageno, UNPIN_DIRTY);
        recCount = 0;

        return;
      }

      //3.pageId is found on disk
      //System.out.println("file " + this.name + " is found. File entry is at page_" + pageno);

      //TODO: opening pre-existing page returns a null page
      Minibase.BufferManager.pinPage(pageno, newPage, PIN_DISKIO);
      curr = new HFPage(newPage);
      curr.setCurPage(pageno);
      curr.setData(newPage.getData());

      /*curr = new HFPage();
      Minibase.BufferManager.pinPage(pageno, curr, PIN_DISKIO);
      */

      pageIdList.add(pageno);
      pidList.add(pageno.pid);
      Minibase.BufferManager.unpinPage(pageno, UNPIN_CLEAN);

      //Skip the first page
      //First page's record count
      RID rid = curr.firstRecord(); //Gets the RID of the first record on the page, or null if none.
      while(rid != null){
        rid = curr.nextRecord(rid); //Gets the next RID after the given one, or null if no more.
        recCount++;
      }

      //traverse thru all other pages and build up pageIdList and pidList
      PageId next = curr.getNextPage();
      while(next.pid > 0){
        HFPage nextHF = new HFPage();
        Minibase.BufferManager.pinPage(next, nextHF, PIN_DISKIO);
        pageIdList.add(next);
        pidList.add(next.pid);

        //Increment count of all records in this page
        rid = nextHF.firstRecord();
        while(rid != null){
          rid = nextHF.nextRecord(rid);
          recCount++;
        }

        //Unpin the page and go to next page in the file entry
        Minibase.BufferManager.unpinPage(next, UNPIN_CLEAN);
        next = nextHF.getNextPage();
      }

      //System.out.println("Constructor done! pageIdList.size() " + pageIdList.size() + " Num of records "  + recCount);

      return;

    }else{

      //if a page is temporary
      PageId tempPage = Minibase.DiskManager.get_file_entry(null);
      curr = new HFPage(newPage);
      curr.setCurPage(tempPage);
      pageIdList.add(tempPage);
      pidList.add(tempPage.pid);
      Minibase.BufferManager.unpinPage(tempPage, UNPIN_DIRTY);

      return;

    }
  } //DONE

  /**
   * TODO: what's the difference between deleteFile and finalize?
   * Called by the garbage collector when there are no more references to the
   * object; deletes the heap file if it's temporary.
   */
  protected void finalize() throws Throwable {
      //PUT YOUR CODE HERE
      if (name == null){  //deletes the heap file if it's temporary
        deleteFile();
      }
      //System.out.println("Finalized!");
  }

  /**
   * Deletes the heap file from the database, freeing all of its pages.
   */
  public void deleteFile() {
    if (fileStatus == 0){  //if normal, delete

      //freeing all pages in heap file
      for (int i = 0; i < pageIdList.size(); i++){
          Minibase.DiskManager.deallocate_page(pageIdList.get(i));
      }

      //Reset this page
      Minibase.DiskManager.delete_file_entry(name);
      recCount = 0;
      fileStatus = 1;
      pageIdList = null;
      pidList = null;
    }else{  //if already been deleted
      return;
    }
  } //DONE

  /**
   * Inserts a new record into the file and returns its RID.
   * 
   * @throws IllegalArgumentException if the record is too large
   */
  public RID insertRecord(byte[] record) throws Exception{
    //PUT YOUR CODE HERE

    //throws IllegalArgumentException if the record is too large
    if (record.length + HFPage.HEADER_SIZE > 1024){
      throw new SpaceNotAvailableException("The record is too large!");
    }

    //1.Search space to insert in pageIDList
    PageId mostSpacePage = pageIdList.get(pageIdList.size()-1); //get the last page in sorted pageIdList (with most space)
    Page currPage = new Page();

    //Get current index's HFPage
    Minibase.BufferManager.pinPage(mostSpacePage, currPage, PIN_DISKIO);
    HFPage currHF = new HFPage(currPage);

    currHF.setCurPage(mostSpacePage);
    //currHF.setData(currPage.getData());

    if (currHF.getFreeSpace() > record.length) { //if space is large enough
      RID rid = currHF.insertRecord(record);
      recCount++;
      Minibase.BufferManager.unpinPage(mostSpacePage, UNPIN_DIRTY);
      return rid;
    }else{  //if not, unpin and go to next index
      Minibase.BufferManager.unpinPage(mostSpacePage, UNPIN_CLEAN);
    }

//    for (int i = 0; i < pageIdList.size(); i++) {
//      PageId currPageId = pageIdList.get(i);
//      Page currPage = new Page();
//
//      //Get current index's HFPage
//      Minibase.BufferManager.pinPage(currPageId, currPage, PIN_DISKIO);
//      HFPage currHF = new HFPage(currPage);
//
//      currHF.setCurPage(currPageId);
//      //currHF.setData(currPage.getData());
//
//      if (currHF.getFreeSpace() > record.length) { //if space is large enough
//        RID rid = currHF.insertRecord(record);
//        recCount++;
//        Minibase.BufferManager.unpinPage(currPageId, UNPIN_DIRTY);
//        return rid;
//      }else{  //if not, unpin and go to next index
//        Minibase.BufferManager.unpinPage(currPageId, UNPIN_CLEAN);
//      }
//    }

    //2.No space in existing pageIdList, create new HFPage to link

    //Create a new page to insert record to
    Page newPage = new Page();
    PageId newPageid = Minibase.BufferManager.newPage(newPage, 1);
    HFPage newHF = new HFPage(newPage);

    //update newHF
    newHF.initDefaults();
    newHF.setCurPage(newPageid);
    curr.setNextPage(newPageid);
    newHF.setPrevPage(curr.getCurPage());
    RID newRid = newHF.insertRecord(record);

    //update heap file
    pageIdList.add(newPageid);
    pidList.add(newPageid.pid);
    curr = newHF;
    recCount++;

    Minibase.BufferManager.unpinPage(newPageid,  UNPIN_DIRTY);

    Collections.sort(pageIdList, new SortByFreeSpace());
    return newRid;
  } //DONE

  /**
   * Reads a record from the file, given its id.
   * 
   * @throws IllegalArgumentException if the rid is invalid
   */
  public Tuple getRecord(RID rid) throws Exception{
    //PUT YOUR CODE
    PageId pageno = rid.pageno;
    if (!pidList.contains(pageno.pid)){
      //if we didn't find the page
      throw new IllegalArgumentException("The rid is invalid");
    }else{
      //if we find the page
      for (int i =0; i < pageIdList.size(); i++){
        if (pageIdList.get(i).pid == pageno.pid){
          //set newHF HFPage with this page
          HFPage newHF = new HFPage();
          Minibase.BufferManager.pinPage(pageno,newHF,false);
          Minibase.BufferManager.unpinPage(pageno, false);
          //find record by HFPage.selectRecord and return as tuple
          try {
            byte[] record = newHF.selectRecord(rid);
            Tuple recTuple = new Tuple(record, 0, record.length);
            return recTuple;
          }catch (IllegalArgumentException e){  //if selectRecord throws IllegalArgumentException
            throw new IllegalArgumentException("The rid is invalid");
          }
        }
      }
    }
    throw new IllegalArgumentException("The rid is invalid");
  } //DONE

  /**
   * Updates the specified record in the heap file.
   * 
   * @throws IllegalArgumentException if the rid or new record is invalid
   */

  public boolean updateRecord(RID rid, Tuple newRecord) throws Exception{
    PageId pageno = rid.pageno;
    HFPage newHF = new HFPage();
    Minibase.BufferManager.pinPage(pageno, newHF, false);
    try{
      newHF.updateRecord(rid, newRecord);
      Minibase.BufferManager.unpinPage(pageno, false);
    } catch (IllegalArgumentException e){
      throw new InvalidUpdateException();
    }

    return true;
  } //DONE


  /**
   * Deletes the specified record from the heap file.
   * 
   * @throws IllegalArgumentException if the rid is invalid
   */
  public boolean deleteRecord(RID rid) throws Exception{
    PageId pageno = rid.pageno;
    HFPage newHF = new HFPage();
    Minibase.BufferManager.pinPage(pageno, newHF, false);
    try{
      newHF.deleteRecord(rid);
      Minibase.BufferManager.unpinPage(pageno, false);
    } catch (IllegalArgumentException e){
      throw new IllegalArgumentException("The rid is invalid");
    }

    return true;
  }

  /**
   * Gets the number of records in the file.
   */
  public int getRecCnt() {
    //PUT YOUR CODE HERE
    return recCount;
  } //DONE

  /**
   * Initiates a sequential scan of the heap file.
   */
  public HeapScan openScan() {
    return new HeapScan(this);
  } //DONE

  /**
   * Returns the name of the heap file.
   */
  public String toString() {
    //PUT YOUR CODE HERE
    return this.name;
  } //DONE

} // public class HeapFile implements GlobalConst

class SortByFreeSpace implements Comparator<PageId>
{
  public int compare(PageId a, PageId b)
  {
    //Get pageA's freespace

    short freeA;
    HFPage hfA = new HFPage();
    Minibase.BufferManager.pinPage(a, hfA, global.GlobalConst.PIN_DISKIO);
    Minibase.BufferManager.unpinPage(a, global.GlobalConst.UNPIN_CLEAN);
    freeA = hfA.getFreeSpace();
    //System.out.println("freeA is: "+ freeA);
    //Get pageB's freespace
    HFPage hfB = new HFPage();
    short freeB;
    Minibase.BufferManager.pinPage(b, hfB, global.GlobalConst.PIN_DISKIO);
    Minibase.BufferManager.unpinPage(b, global.GlobalConst.UNPIN_CLEAN);
    freeB = hfB.getFreeSpace();
    //System.out.println("freeB is: "+ freeB);
    return freeA - freeB;
  }
}
