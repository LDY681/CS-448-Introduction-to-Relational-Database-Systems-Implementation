package query;

/**
 * Common interface for all minibase query execution plans.
 */
public interface Plan {

  /**
   * Executes the plan and prints applicable output.
   */
  public void execute();

} // public interface Plan
