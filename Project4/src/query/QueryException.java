package query;

/**
 * General exception for query execution issues.
 */
public class QueryException extends Exception {

  /**
   * Constructs a QueryException, given the error message.
   */
  public QueryException(String message) {
    super(message);
  }

} // public class QueryException extends Exception
