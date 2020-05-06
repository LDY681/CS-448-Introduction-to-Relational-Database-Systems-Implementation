package global;

import bufmgr.BufMgr;
import diskmgr.DiskMgr;

/**
 * Definitions for the running Minibase system, including references to static
 * layers and database-level attributes.
 */
public class Minibase {

  /** Name of the data file. */
  public static String DatabaseName;

  /** The Minibase Disk Space Manager. */
  public static DiskMgr DiskManager;

  /** The Minibase Buffer Manager. */
  public static BufMgr BufferManager;


  // --------------------------------------------------------------------------

  /**
   * Constructs and starts an instance of Minibase, given the configuration.
   *
   * @param dbname Name of the data file
   * @param num_pgs Number of pages to allocate
   * @param bufpoolsize Buffer pool size (in pages)
   * @param replacement_policy Buffer pool replacement policy
   * @param exists If the database already exists on disk
   */
  public Minibase(String dbname, int num_pgs, int bufpoolsize,
      String replacement_policy, boolean exists) {

    // simply initialize the database
    init(dbname, num_pgs, bufpoolsize, replacement_policy, exists);

  } // constructor

  /**
   * Initializes the current instance of Minibase with the given configuration.
   *
   * @param dbname Name of the data file
   * @param num_pgs Number of pages to allocate
   * @param bufpoolsize Buffer pool size (in pages)
   * @param replacement_policy Buffer pool replacement policy
   * @param exists If the database already exists on disk
   */
  public void init(String dbname, int num_pgs, int bufpoolsize,
      String replacement_policy, boolean exists) {

    // save the file name
    DatabaseName = dbname;

    // load the static layers
    try {
      DiskManager = new DiskMgr();
      BufferManager = new BufMgr(bufpoolsize);
    } catch (Exception exc) {
      haltSystem(exc);
    }

    // create or open the database
    try {
      if (exists) {
        DiskManager.openDB(dbname);

      } else {
        DiskManager.createDB(dbname, num_pgs);

        BufferManager.flushAllPages();
      }
    } catch (Exception exc) {
      haltSystem(exc);
    }

  } // init

  /**
   * Displays an unrecoverable error and halts the system.
   */
  public static void haltSystem(Exception exc) {
    System.err.println("\n*** Unrecoverable system error ***");
    exc.printStackTrace();
    Runtime.getRuntime().exit(1);
  }

} // public class Minibase
