package relop;

import global.SearchKey;

import java.util.ArrayDeque;
import java.util.Queue;

public class HashJoin extends Iterator {

	Iterator leftIter;
	Integer leftCol;

	Iterator rightIter;
	Integer rightCol;

	HashTableDup hashTableDup;	//The hash table stored in memory to be looked up

	private Queue<Tuple> nextTupleQueue;	//pre-fetched next Tuple queue

	public HashJoin(Iterator aIter1, Iterator aIter2, int aJoinCol1, int aJoinCol2){
		//Initialize fields
		this.leftIter = aIter1;
		this.rightIter = aIter2;
		this.leftCol = aJoinCol1;
		this.rightCol = aJoinCol2;

		this.nextTupleQueue = new ArrayDeque<Tuple>();

		this.schema = Schema.join(this.leftIter.schema, this.rightIter.schema);

		// Build the lookup table.
		this.hashTableDup = new HashTableDup();
		while (this.leftIter.hasNext()) {
			Tuple leftTuple = this.leftIter.getNext();
			SearchKey searchKey = new SearchKey(leftTuple.getField(aJoinCol1));	//generate searchKey
			this.hashTableDup.add(searchKey, leftTuple);	// add [searchKey, tuple] into look-up hashtable
		}

		this.leftIter.close();
	}

	@Override
	public void explain(int depth) {
		throw new UnsupportedOperationException("Not implemented");
	}

	@Override
	public void restart() {
		this.leftIter.restart();
		this.rightIter.restart();
	}

	@Override
	public boolean isOpen() {
		return this.rightIter.isOpen();	//if rightIter is open, then left must be open too
	}

	@Override
	public void close() {
		this.leftIter.close();
		this.rightIter.close();
	}

	@Override
	public boolean hasNext() {
		//If there are more tuples in next Tuple Lists
		if (this.nextTupleQueue.size() > 0) {
			return true;
		}else{
			//See if there are more tuples that can be probed and match with lookup
			while (this.rightIter.hasNext()){
				Tuple nextRight = this.rightIter.getNext();
				SearchKey searchKey = new SearchKey(nextRight.getField(this.rightCol));	//Get the key of next rightTuple
				Tuple[] matchTuples = this.hashTableDup.getAll(searchKey);	//Found possible matches from lookup tables

				//Make joined tuples and add them to nextTupleQueue
				for (Tuple matchTuple : matchTuples) {
					Tuple nextTuple = Tuple.join(matchTuple, nextRight, this.schema);
					this.nextTupleQueue.add(nextTuple);
				}

				//If there are next tuples in queue
				if (this.nextTupleQueue.size() != 0 ) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public Tuple getNext() {
		if (this.hasNext()){
			return this.nextTupleQueue.remove();
		}else{
			throw new IllegalStateException("no more tuples");
		}
	}
} // end class HashJoin;
