package parser;

import java.util.ArrayList;

import relop.Predicate;

/**
 * AST node for OR expressions in WHERE clauses.
 */
public class AST_OrExpr extends SimpleNode {

  public AST_OrExpr(int id) {
    super(id);
  }

  public AST_OrExpr(MiniSql p, int id) {
    super(p, id);
  }

  /**
   * Returns the CNF version of the expression rooted at this node.
   */
  protected Predicate[][] getCNF() {

    // because MiniSQL doesn't support negation, conversion to CNF is done
    // simply by distributing the ORs over the ANDs; this is of course the
    // naive method that causes the formula to grow exponentially
    ArrayList<Predicate[]> cnf = new ArrayList<Predicate[]>();

    // special property of CNF: the number of terms in each conjunct equals the
    // number of original conjuncts (i.e. AND expressions); this should be the
    // capacity of each inner array
    cnf.add(new Predicate[children.length]);

    // iterate through each AST_AndExpr child node
    for (int i = 0; i < children.length; i++) {

      // get the 'm' predicates connected by ANDs
      AST_AndExpr child = (AST_AndExpr) children[i];
      Predicate[] preds = child.getPredicates();
      int m = preds.length;

      // make 'm-1' copies of the current 'n' CNF sets
      int n = cnf.size();
      for (int j = 1; j < m; j++) {
        for (int k = 0; k < n; k++) {
          cnf.add(cnf.get(k).clone());
        }
      }

      // merge each predicate with its own copy
      for (int j = 0; j < m; j++) {
        for (int k = 0; k < n; k++) {
          // four variables, isn't it beautiful?
          cnf.get(j * n + k)[i] = preds[j];
        }
      }

    } // for i

    // convert and return the resulting array
    Predicate[][] ret = new Predicate[cnf.size()][];
    for (int c = 0; c < ret.length; c++) {
      ret[c] = cnf.get(c);
    }
    return ret;

  } // protected Predicate[][] getCNF()

} // public class AST_OrExpr extends SimpleNode
