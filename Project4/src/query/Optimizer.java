package query;

import parser.AST_CreateIndex;
import parser.AST_CreateTable;
import parser.AST_Delete;
import parser.AST_Describe;
import parser.AST_DropIndex;
import parser.AST_DropTable;
import parser.AST_Insert;
import parser.AST_Select;
import parser.AST_Start;
import parser.AST_Update;
import parser.MiniSqlTreeConstants;

/**
 * <h3>Minibase Query Optimizer</h3>
 * The optimizer takes a parsed MiniSQL query (in abstract syntax tree form) and
 * generates an execution plan for it, using the system catalogs for estimates.
 */
public class Optimizer implements MiniSqlTreeConstants {

  /**
   * Evaluates the given AST and returns an optimized execution plan.
   * 
   * @throws QueryException if parsed query is invalid
   */
  public static Plan evaluate(AST_Start tree) throws QueryException {

    // dispatch optimizing to the individual classes
    switch (tree.getType()) {

      case JJT_CREATEINDEX:
        // System.out.println("Creating index...");
        return new CreateIndex((AST_CreateIndex) tree.getStmt());

      case JJT_CREATETABLE:
        // System.out.println("Creating table...");
        return new CreateTable((AST_CreateTable) tree.getStmt());

      case JJT_DROPINDEX:
        // System.out.println("Dropping Index...");
        return new DropIndex((AST_DropIndex) tree.getStmt());

      case JJT_DROPTABLE:
        // System.out.println("Dropping Table...");
        return new DropTable((AST_DropTable) tree.getStmt());

      case JJT_DESCRIBE:
        // System.out.println("Describing...");
        return new Describe((AST_Describe) tree.getStmt());

      case JJT_INSERT:
        // System.out.println("Inserting...");
        return new Insert((AST_Insert) tree.getStmt());

      case JJT_SELECT:
        // System.out.println("Selecting...");
        return new Select((AST_Select) tree.getStmt());

      case JJT_UPDATE:
        // System.out.println("Updating...");
        return new Update((AST_Update) tree.getStmt());

      case JJT_DELETE:
        // System.out.println("Deleting...");
        return new Delete((AST_Delete) tree.getStmt());

      default:
        throw new QueryException("unsupported query type");

    } // switch

  } // public static Plan evaluate(AST_Start tree) throws QueryException

} // public class Optimizer implements MiniSqlTreeConstants
