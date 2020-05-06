package global;

/**
 * Global constants; implement this interface to access them conveniently.
 */
public interface GlobalConst {

  //
  // Disk Manager Constants
  //

  /** Size of a page, in bytes. */
  public static final int PAGE_SIZE = 1024;

  /** Page number of an invalid page (i.e. null pointer). */
  public static final int INVALID_PAGEID = -1;

  /** Page number of the first page in a database file. */
  public static final int FIRST_PAGEID = 0;

  /** Maximum size of a name (i.e. of files or attributes). */
  public static final int NAME_MAXLEN = 50;

  //
  // Buffer Manager Constants
  //

  /** Replace the buffer frame with an existing memory page. */
  public static final boolean PIN_MEMCPY = true;

  /** Replace an existing memory page with the current disk page. */
  public static final boolean PIN_DISKIO = false;

  /** Forces the page to be written to disk when unpinned. */
  public static final boolean UNPIN_DIRTY = true;

  /** Optimization to avoid writing to disk when unpinned. */
  public static final boolean UNPIN_CLEAN = false;

  //
  // Heap File Constants
  //

  /** Length of an "empty" slot in a heap file page. */
  public static final int EMPTY_SLOT = -1;

  //
  // System Catalog Constants
  //

  /** Maximum length of a column (in bytes). */
  public static final int MAX_COLSIZE = 1001;

  /** Maximum length of a tuple (in bytes). */
  public static final int MAX_TUPSIZE = 1004;

} // public interface GlobalConst
