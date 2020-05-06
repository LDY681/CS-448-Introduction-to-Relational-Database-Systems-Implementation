package tests;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.junit.After;
import org.junit.Before;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import global.AttrOperator;
import global.AttrType;
import global.RID;
import global.SearchKey;
import heap.HeapFile;
import index.HashIndex;
import relop.FileScan;
import relop.HashJoin;
import relop.IndexScan;
import relop.KeyScan;
import relop.Predicate;
import relop.Projection;
import relop.Schema;
import relop.Selection;
import relop.SimpleJoin;
import relop.Tuple;
import relop.Iterator;

import java.io.*;
import java.util.*;

import com.opencsv.CSVReader;
import org.json.simple.parser.JSONParser;

/**
 * Test suite for the relop layer.
 */
public class ROTest extends TestDriver {

	private static ROTest rot;

	/** The display name of the test suite. */
	private static final String TEST_NAME = "relational operator tests";

	/** Size of tables in test3. */
	private static final int SUPER_SIZE = 2000;

	private Scanner in;

	/** Drivers schema/table/index */
	private static Schema s_drivers;
	private static HeapFile f_drivers;
	private static HashIndex idx_drivers;
	
	private static Schema s_driversBig;
	private static HeapFile f_driversBig;
	private static HashIndex idx_driversBig;
	/** Rides schema/table/index */
	
	private static Schema s_rides;
	private static HeapFile f_rides;
	private static HashIndex idx_rides;

	private static Schema s_ridesBig;
	private static HeapFile f_ridesBig;
	private static HashIndex idx_ridesBig;

	/** Groups schema/table/index */
	private static Schema s_groups;
	private static HeapFile f_groups;
	private static HashIndex idx_groups;

	private static Schema s_groupsBig;
	private static HeapFile f_groupsBig;
	private static HashIndex idx_groupsBig;

	/** Expected result strings for test cases */
	private static HashMap<String, String> results;

	/** */
	private static void loadTable(Schema schema, HeapFile f_table, HashIndex idx_table, String table)
            throws FileNotFoundException, IOException, ParseException {

	    // initialize schema for table
        InputStream in = ClassLoader.getSystemResourceAsStream(table.concat(".json"));
        JSONObject jsonObj = (JSONObject) new JSONParser().parse(new InputStreamReader(in));
        JSONArray jsonFields = (JSONArray) jsonObj.get("schema");
        Field[] fields = new Field[jsonFields.size()];

        int key = ((Long) jsonObj.get("key")).intValue();
        int xx = 0;
        for (Object obj : jsonFields) {
            Field field = new Field((JSONObject) obj);
            fields[xx] = field; xx++;
            schema.initField(field.getNo(), field.getType(), field.getLength(), field.getName());
        }

	    // populate relation
        in = ClassLoader.getSystemResourceAsStream(table.concat(".csv"));
	    try (CSVReader csvReader = new CSVReader(new InputStreamReader(in))) {
	        String[] values = null;
            Tuple tuple = new Tuple(schema);
	        while((values = csvReader.readNext()) != null) {

	            for (int i=0; i<values.length; i++) {
	                tuple.setField(i, fields[i].getValue(values[i]));
                }

                idx_table.insertEntry(new SearchKey(fields[key].getValue(values[key])), f_table.insertRecord(tuple.getData()));

            }
        }

    }

	protected void execute_and_compare(String testDesc, String id, Iterator it)
	{
		it.execute();
		it.close();
		String[] sol = results.get(id).split("|");
		Arrays.sort(sol);
		String[] res = it.getResult().split("|");
		Arrays.sort(res);
		assertTrue("FAILURE: " + testDesc + " output {" + it.getResult() + "} did not match expected result, should be " + results.get(id), Arrays.equals(sol, res));
	}

	public static void main(String[] args) {
		Result result = JUnitCore.runClasses(tests.ROTest.class);

		for (Failure failure : result.getFailures()) {
			System.out.println(failure.toString());
		}

		System.out.println(result.wasSuccessful());
	}

	@BeforeClass
	public static void setupDB() throws Exception {
		// create a clean Minibase instance
		rot = new ROTest();
		rot.create_minibase();

		results = new HashMap<String, String>();

		try
		{
			Scanner in = new Scanner(new File("solution.txt"));
			String line;
			String[] res;
			while(in.hasNextLine())
			{
				line = in.nextLine();
				res = line.split("=");
				results.put(res[0],res[1]);

			}
		} catch (IOException e)
		{
			e.printStackTrace();
			System.exit(1);
		}

		// "Drivers" table
        s_drivers = new Schema(5);
        f_drivers = new HeapFile("drivers");
        idx_drivers = new HashIndex("drivers_idx");
        loadTable(s_drivers, f_drivers, idx_drivers, "drivers");


 		s_driversBig = new Schema(5);
        f_driversBig = new HeapFile("driversBig");
        idx_driversBig = new HashIndex("drivers_idxBig");
        loadTable(s_driversBig, f_driversBig, idx_driversBig, "driversBig");
        // "Rides" table
        s_rides = new Schema(4);
        f_rides = new HeapFile("rides");
        idx_rides = new HashIndex("rides_idx");
        loadTable(s_rides, f_rides, idx_rides, "rides");

        s_ridesBig = new Schema(4);
        f_ridesBig = new HeapFile("ridesBig");
        idx_ridesBig = new HashIndex("rides_idxBig");
        loadTable(s_ridesBig, f_ridesBig, idx_ridesBig, "ridesBig");

        // "Groups" table
        s_groupsBig = new Schema(2);
        f_groupsBig = new HeapFile("groupsBig");
        idx_groupsBig = new HashIndex("groups_idxBig");
        loadTable(s_groupsBig, f_groupsBig, idx_groupsBig, "groupsBig");

        s_groups = new Schema(2);
        f_groups = new HeapFile("groups");
        idx_groups = new HashIndex("groups_idx");
        loadTable(s_groups, f_groups, idx_groups, "groups");

		System.out.println();

	}

	@AfterClass
	public static void tearDownDB()
	{
		idx_drivers.deleteFile();
		idx_rides.deleteFile();
		idx_groups.deleteFile();
		f_rides.deleteFile();
		f_drivers.deleteFile();
		f_groups.deleteFile();
		rot.delete_minibase();
	}

	@Test	//PASS
	public void testFileScan() {
		//Scan drivers table
		Iterator fscan = new FileScan(s_drivers, f_drivers);
		execute_and_compare("Filescan", "filescan", fscan);
	}

	@Test	//PASS
	public void testIndexScan() {
		//Scan drivers index
		Iterator idxscan = new IndexScan(s_drivers, idx_drivers, f_drivers);
		execute_and_compare("IndexScan", "idxscan", idxscan);
	}

	@Test	//PASS
	public void testKeyScan() {
		//Scan drivers index for key 20f
		Iterator keyscan = new KeyScan(s_drivers, idx_drivers, new SearchKey(20f), f_drivers);
		execute_and_compare("KeyScan", "keyscan", keyscan);
	}

	@Test	//PASS
	public void testSelection() {
		//Selection drivers with age > 20
		Iterator selection = new Selection(new FileScan(s_drivers, f_drivers),
				new Predicate(AttrOperator.GT, AttrType.COLNAME, "age", AttrType.FLOAT, 20F));
		execute_and_compare("Selection", "selection", selection);
	}

	@Test	//PASS
	public void testSelectionMultiplePredicates() {
		Iterator selection_preds = new Selection(new FileScan(s_drivers, f_drivers),
				new Predicate(AttrOperator.GT, AttrType.COLNAME, "age", AttrType.FLOAT, 23F),
				new Predicate(AttrOperator.LT, AttrType.COLNAME, "age", AttrType.FLOAT, 19F));
		execute_and_compare("Selection Multipled Predicates", "selection_preds", selection_preds);
	}

	@Test	//PASS
	public void testProjection() {
		//Projection on Drivers: {FirstName, NumSeats}
		Iterator projection = new Projection(new FileScan(s_drivers, f_drivers), s_drivers.fieldNumber("FirstName"), s_drivers.fieldNumber("NumSeats"));
		execute_and_compare("Projection", "projection", projection);
	}

	@Test	//PASS
	public void testHashJoin() {
		//HashJoin on Drivers X Rides on DriverID
		Iterator hashjoin = new HashJoin(new FileScan(s_drivers, f_drivers),
				new FileScan(s_rides, f_rides), 0, 0);
		execute_and_compare("Hash Join", "hashjoin", hashjoin);
	}

	@Test	//PASS
	public void testSelectionPipelining() {
		//Test all possible Iterator inputs to Selection
		Iterator sel_idx = new Selection(new IndexScan(s_drivers, idx_drivers, f_drivers),
				new Predicate(AttrOperator.EQ, AttrType.COLNAME, "FirstName", AttrType.STRING, "Walid"));
		execute_and_compare("Selection - Pipelining IndexScan", "sel_idx", sel_idx);
		Iterator sel_key = new Selection(new KeyScan(s_drivers, idx_drivers, new SearchKey(20F), f_drivers),
				new Predicate(AttrOperator.EQ, AttrType.COLNAME, "FirstName", AttrType.STRING, "Walid"));
		execute_and_compare("Selection - Pipelining Keyscan", "sel_key", sel_key);
		Iterator sel_sel = new Selection(new Selection(new FileScan(s_drivers, f_drivers),
				new Predicate(AttrOperator.EQ, AttrType.COLNAME, "Age", AttrType.FLOAT, 20F)),
				new Predicate(AttrOperator.EQ, AttrType.COLNAME, "FirstName", AttrType.STRING, "Walid"));
		execute_and_compare("Selection - Pipelining Selection", "sel_sel", sel_sel);
		Iterator sel_proj = new Selection(new Projection(
				new FileScan(s_drivers, f_drivers), s_drivers.fieldNumber("DriverId"), s_drivers.fieldNumber("FirstName")),
				new Predicate(AttrOperator.EQ, AttrType.COLNAME, "FirstName", AttrType.STRING, "Walid"));
		execute_and_compare("Selection - Pipelining Projection", "sel_proj", sel_proj);
	/*	Iterator sel_sj = new Selection(new SimpleJoin(new FileScan(s_drivers, f_drivers), new FileScan(s_rides, f_rides),
				new Predicate(AttrOperator.EQ, AttrType.FIELDNO, 0, AttrType.FIELDNO, 5)),
				new Predicate(AttrOperator.EQ, AttrType.COLNAME, "FirstName", AttrType.STRING, "Walid"));
		execute_and_compare("Selection - Pipelining Simple Join", "sel_sj", sel_sj);
	*/
		Iterator sel_hj = new Selection(new HashJoin(new FileScan(s_drivers, f_drivers), new FileScan(s_rides, f_rides),0,0),
				new Predicate(AttrOperator.EQ, AttrType.COLNAME, "FirstName", AttrType.STRING, "Walid"));
		execute_and_compare("Selection - Pipelining Hash Join", "sel_jh", sel_hj);
	}

	@Test	//PASS
	public void testProjectionPipelining() {
		//Test all possible Iterator inputs to HashJoin
		Iterator proj_idx = new Projection(new IndexScan(s_drivers, idx_drivers, f_drivers),
				s_drivers.fieldNumber("DriverId"), s_drivers.fieldNumber("Age"));
		execute_and_compare("Projection - Pipelining IndexScan", "proj_idx", proj_idx);
		Iterator proj_key = new Projection(new KeyScan(s_drivers, idx_drivers, new SearchKey(20F), f_drivers),
				s_drivers.fieldNumber("DriverId"), s_drivers.fieldNumber("Age"));
		execute_and_compare("Projection - Pipelining KeyScan", "proj_key", proj_key);	//TODO: output not expected
		Iterator proj_sel = new Projection(new Selection(new FileScan(s_drivers, f_drivers),
				new Predicate(AttrOperator.EQ, AttrType.COLNAME, "Age", AttrType.FLOAT, 20F)),
				s_drivers.fieldNumber("DriverId"), s_drivers.fieldNumber("Age"));
		execute_and_compare("Projection - Pipelining Selection", "proj_sel", proj_sel);
		Iterator proj_proj = new Projection(new Projection(new FileScan(s_drivers, f_drivers),
				s_drivers.fieldNumber("DriverId"), s_drivers.fieldNumber("FirstName"), s_drivers.fieldNumber("Age")),
				0, 2);
		execute_and_compare("Projection - Pipelining Projection", "proj_proj", proj_proj);
		/*
		Iterator proj_sj = new Projection(new SimpleJoin(new FileScan(s_drivers, f_drivers), new FileScan(s_rides, f_rides),
				new Predicate(AttrOperator.EQ, AttrType.FIELDNO, 0, AttrType.FIELDNO, 5)),
				0, 3);
		execute_and_compare("Projection - Pipelining Simple Join", "proj_sj", proj_sj);
		*/
		Iterator proj_hj = new Projection(new HashJoin(new FileScan(s_drivers, f_drivers), new FileScan(s_rides, f_rides),0,0),
				0, 3);
		execute_and_compare("Projection - Pipelining Hash Join", "proj_hj", proj_hj);
	}

	@Test	//PASS
	public void testHashjoinPipelining() {
		//Test all possible Iterator inputs to HashJoin
		/*
		Iterator hj_sel_sj = new HashJoin(new Selection(new FileScan(s_drivers, f_drivers),
				new Predicate(AttrOperator.GTE, AttrType.COLNAME, "DriverId", AttrType.INTEGER, 1)),
				new SimpleJoin(new FileScan(s_rides, f_rides), new FileScan(s_groups, f_groups),
						new Predicate(AttrOperator.EQ, AttrType.COLNAME, "GroupId", AttrType.COLNAME, "DriverId")),0,0);
		execute_and_compare("Hash Join - Pipelining Selection/Simple Join", "hj_sel_sj", hj_sel_sj);
		*/
		Iterator hj_proj_hj = new HashJoin(new Projection(new FileScan(s_drivers, f_drivers), s_drivers.fieldNumber("DriverId"), s_drivers.fieldNumber("Age")),
				new HashJoin(new FileScan(s_rides, f_rides), new FileScan(s_groups, f_groups), 1, 0), 0, 0);
		execute_and_compare("Hash Join - Pipelining Projection/Hash Join", "hj_proj_hj", hj_proj_hj);
		Iterator hj_iscan_kscan = new HashJoin(new IndexScan(s_drivers, idx_drivers, f_drivers),
				new KeyScan(s_rides, idx_rides, new SearchKey(1), f_rides), 0, 0);
		execute_and_compare("Hash Join - Pipelining IndexScan/KeyScan", "hj_iscan_kscan", hj_iscan_kscan);
	}

	//This is an EXTRA test to do Drivers X Drivers using HashJoin and we will calibrate this by changing the value of HashTableDup in the HashTableDup class
	@Test	//PASS
	public void testHashJoinOnBigTables() {
		//HashJoin on Drivers X Rides on DriverID
		//Anytime the HashTableDup count goes over the threshold, a runtimeexception is thrown
				Iterator hashSame = new HashJoin(new FileScan(s_drivers, f_drivers),
				new FileScan(s_drivers, f_drivers), 0, 0);
		execute_and_compare("Hash Join", "hashSame", hashSame);
	}

	//This is second EXTRA test to do DriversBig X DriversBig using HashJoin and we will calibrate this by changing the value of HashTableDup in the HashTableDup class
	//Anytime the HashTableDup count goes over the threshold, a runtimeexception is thrown
	//In addition, any call to Simplejoin throws a RunTimeException
	@Test	//PASS
	public void testHashJoinOnEqualTables() {
		//HashJoin on Drivers X Rides on DriverID
		Iterator hashEqual = new HashJoin(new FileScan(s_driversBig, f_driversBig),
				new FileScan(s_driversBig, f_driversBig), 0, 0);
		execute_and_compare("Hash Join", "hashEqual", hashEqual);
	}

} // class ROTest extends TestDriver
