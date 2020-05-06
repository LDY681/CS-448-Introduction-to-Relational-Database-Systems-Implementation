package relop;

import global.SearchKey;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;

/**
 * A extension to Java's hash table that allows duplicate keys.
 * http://mindprod.com/jgloss/hashtable.html
 */
@SuppressWarnings("unchecked")
class HashTableDup extends Hashtable {

	/**
	 * Maps the specified key to the specified value in this hashtable.
	 */
	public void add(SearchKey key, Tuple value) {

		// to conserve RAM, duplicate values are either stored as single objects,
		// pairs in an array, or multiples in an ArrayList
		Object existing = get(key);
		if (existing == null) {

			// store the single value
			put(key, value);

		} else if (existing instanceof Tuple) {

			// was a single object; make into a pair
			put(key, new Tuple[] { (Tuple) existing, value });

		} else if (existing instanceof Tuple[]) {

			// was a pair; make into an ArrayList of 3
			ArrayList<Tuple> a = new ArrayList<Tuple>();
			a.addAll(Arrays.asList((Tuple[]) existing));
			a.add(value);
			put(key, a);

		} else {

			// just add to tail end of existing ArrayList
			((ArrayList<Tuple>) existing).add(value);

		} // else

	} // public void add(SearchKey key, Tuple value)

	/**
	 * Returns the values to which the specified key is mapped in this hashtable.
	 */
	public Tuple[] getAll(SearchKey key) {

		// look up the key
		Object match = get(key);
		if (match == null) {

			// not found
			return null;

		} else if (match instanceof Tuple) {

			// return the single match
			return new Tuple[] { (Tuple) match };

		} else if (match instanceof Tuple[]) {

			// return the matches
			return (Tuple[]) match;

		} else {

			// convert ArrayList to Tuple[]
			ArrayList<Tuple> a = (ArrayList<Tuple>) match;
			return a.toArray(new Tuple[a.size()]);

		} // elsee

	} // public Tuple[] getAll(SearchKey key)

} // class HashTableDup extends Hashtable
