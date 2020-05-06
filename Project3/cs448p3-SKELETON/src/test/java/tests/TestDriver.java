package tests;

import global.GlobalConst;
import global.Minibase;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;

/**
 * <h3>Minibase Test Driver</h3>
 * This base class contains common code to each layer's test suite.
 */
class TestDriver implements GlobalConst {

  /** Success value, for readability. */
  protected static final boolean PASS = true;

  /** Failure value, for readability. */
  protected static final boolean FAIL = false;

  // --------------------------------------------------------------------------

  /** Default database file name. */
  protected String DB_PATH = System.getProperty("user.name") + ".minibase";

  /** Default database size (in pages). */
  protected int DB_SIZE = 10000;

  /** Default buffer pool size (in pages) */
  protected int BUF_SIZE = 100;

  /** Default buffer pool replacement policy */
  protected String BUF_POLICY = "Clock";

  // --------------------------------------------------------------------------

  /** Random generator; use the same seed to make tests deterministic. */
  protected Random random;

  /**
   * Counter values saved with a particular description.
   */
  protected class CountData {

    public String desc;

    public int reads;

    public int writes;

    public int allocs;

    public int pinned;

  } // protected class CountData

  /**
   * Incremental history of the performance counters; odd elements are snapshots
   * before the tests, and even elements are after.
   */
  protected ArrayList<CountData> counts;

  // --------------------------------------------------------------------------

  /**
   * Deletes the database files from the disk.
   */
  protected void delete_minibase() {
    new File(DB_PATH).delete();
  }

  /**
   * Creates a new database on the disk.
   */
  protected void create_minibase() {
    System.out.println("Creating database...\nReplacer: " + BUF_POLICY);
    new Minibase(DB_PATH, DB_SIZE, BUF_SIZE, BUF_POLICY, false);
  }

  /**
   * Loads an existing database from the disk.
   */
  protected void load_minibase() {
    System.out.println("Loading database...\nReplacer: " + BUF_POLICY);
    new Minibase(DB_PATH, DB_SIZE, BUF_SIZE, BUF_POLICY, true);
  }

  // --------------------------------------------------------------------------

  /**
   * Resets the random generator to the default seed.
   */
  protected void initRandom() {
    random = new Random(74);
  }

  /**
   * Resets the performance counter history.
   */
  protected void initCounts() {
    counts = new ArrayList<CountData>();
  }

  /**
   * Saves the current performance counters, given the description.
   */
  protected void saveCounts(String desc) {

    // create the new count data
    CountData data = new CountData();
    counts.add(data);
    data.desc = desc;

    // save the counts (in correct order)
    Minibase.BufferManager.flushAllPages();
    data.reads = Minibase.DiskManager.getReadCount();
    data.writes = Minibase.DiskManager.getWriteCount();
    data.allocs = Minibase.DiskManager.getAllocCount();
    data.pinned = BUF_SIZE - Minibase.BufferManager.getNumUnpinned();

  } // protected void saveCounts(String desc)

  /**
   * Prints the performance counters (i.e. for the current test).
   */
  protected void printCounters() {

    CountData data = counts.get(counts.size() - 1);
    System.out.println();
    Minibase.BufferManager.flushAllPages();
    System.out.println("  *** Number of reads:  "
        + (Minibase.DiskManager.getReadCount() - data.reads));
    System.out.println("  *** Number of writes: "
        + (Minibase.DiskManager.getWriteCount() - data.writes));
    System.out.println("  *** Net total pages:  "
        + (Minibase.DiskManager.getAllocCount() - data.allocs));
    int numbufs = Minibase.BufferManager.getNumBuffers();
    System.out
        .println("  *** Remaining Pinned: "
            + (numbufs - Minibase.BufferManager.getNumUnpinned()) + " / "
            + numbufs);

  } // protected void printCounters()

  /**
   * Prints the complete history of the performance counters.
   * 
   * @param sepcnt how many lines to print before each separator
   */
  protected void printSummary(int sepcnt) {

    System.out.println();
    String seperator = "--------------------------------------";
    System.out.println(seperator);
    System.out.println("\tReads\tWrites\tAllocs\tPinned");
    int size = counts.size();
    for (int i = 1; i < size; i += 2) {

      if (i % (sepcnt * 2) == 1) {
        System.out.println(seperator);
      }

      CountData before = counts.get(i - 1);
      CountData after = counts.get(i);
      System.out.print(after.desc);

      System.out.print("\t" + (after.reads - before.reads));
      System.out.print("\t" + (after.writes - before.writes));
      System.out.print("\t" + (after.allocs - before.allocs));
      System.out.print("\t" + (after.pinned - before.pinned));
      System.out.println();

    } // for
    System.out.println(seperator);

  } // protected void printSummary(int sepcnt)

} // class TestDriver implements GlobalConst
