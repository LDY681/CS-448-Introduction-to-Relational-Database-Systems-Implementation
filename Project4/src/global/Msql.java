package global;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import parser.AST_Start;
import parser.MiniSql;
import parser.MiniSqlTreeConstants;
import parser.ParseException;
import parser.TokenMgrError;
import query.Optimizer;
import query.Plan;
import query.QueryException;

/**
 * Command line MiniSQL front end, used interactively or driven by scripts.
 */
public class Msql implements MiniSqlTreeConstants {

  /** Default database file name. */
  protected static String PATH = System.getProperty("user.name") + ".minibase";

  /** Default database size (in pages). */
  protected static int DB_SIZE = 10000;

  /** Default buffer pool size (in pages) */
  protected static int BUF_SIZE = 100;
  
  /**Default prefetch size */
  protected static int PRE_SIZE = 10;

  /** Command line prompt, when interactive. */
  protected static String PROMPT = "\nMSQL> ";

  // --------------------------------------------------------------------------

  /**
   * Interactively executes MiniSQL statements.
   */
  public static void main(String[] args) {
    MiniSql parser;
    AST_Start node;

    if (args.length == 1) {
    	try {
			System.setIn(new FileInputStream(args[0]));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    // print the welcome message and load the database
    System.out.println("Minibase SQL Utility 1.0");
    if (new File(PATH).exists()) {
      System.out.println("Loading database...");
      new Minibase(PATH, DB_SIZE, BUF_SIZE, PRE_SIZE, "Clock", true);
    } else {
      System.out.println("Creating database...");
      new Minibase(PATH, DB_SIZE, BUF_SIZE, PRE_SIZE, "Clock", false);
    }

    // initialize the performance counters
    int allocs = Minibase.DiskManager.getAllocCount();
    int reads = Minibase.DiskManager.getReadCount();
    int writes = Minibase.DiskManager.getWriteCount();

    // run until user quits or system crashes
    parser = new MiniSql(System.in);
    while (true) {

      // display the prompt and parse the input
      System.out.print(PROMPT);
      try {
        node = parser.Start();
        System.out.println();
      } catch (TokenMgrError err) {
        System.out.println("\nERROR: " + err.getMessage());
        parser.ReInit(System.in);
        continue;
      } catch (ParseException exc) {
        System.out.print("\nERROR: " + exc.getMessage());
        if (exc.currentToken.kind == 0) {
          System.out.println();
          break;
        }
        parser.ReInit(System.in);
        continue;
      }

      // handle special commands
      if (node.isHelp) {
        System.out.println("HACK: just enter ';' to see available commands.");
        continue;
      }
      if (node.isStats) {

        // get the new stats
        Minibase.BufferManager.flushAllPages();
        int reads2 = Minibase.DiskManager.getReadCount();
        int writes2 = Minibase.DiskManager.getWriteCount();
        int allocs2 = Minibase.DiskManager.getAllocCount();
        int pinned = BUF_SIZE - Minibase.BufferManager.getNumUnpinned();

        // print the differences
        System.out.println("reads  = " + (reads2 - reads));
        System.out.println("writes = " + (writes2 - writes));
        System.out.println("allocs = " + (allocs2 - allocs));
        System.out.println("pinned = " + pinned);

        // update the saved stats
        reads = Minibase.DiskManager.getReadCount(); // ignore getAllocCount
        writes = writes2;
        allocs = allocs2;
        continue;

      }
      if (node.isQuit) {
        break;
      }

      // generate the plan and execute the query
      try {
        Plan plan = Optimizer.evaluate(node);
        plan.execute();
      } catch (QueryException exc) {
        System.out.println("ERROR: " + exc.getMessage());
        continue;
      } catch (RuntimeException exc) {
        exc.printStackTrace();
        System.out.println();
        break;
      }

    } // while (true)

    // close the database and exit
    System.out.println("Closing database...");
    Minibase.DiskManager.closeDB();

  } // public static void main(String[] args)

} // public class Msql implements MiniSqlTreeConstants
