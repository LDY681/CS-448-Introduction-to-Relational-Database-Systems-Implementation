package heap;

import global.* ;
import chainexception.ChainException;
public class Tuple{
	
	/*FIXME this shouldn't be an arbitrary number*/
	public static final int max = 1024;

	public byte[] data;
	public int tupleLength;

	//initialize a blank tuple
	public Tuple(){
		data = new byte[max];
		tupleLength = max;
	}

	//init a tuple with fields
	public Tuple(byte[] data, int offset, int length){
		this.data = data;
		tupleLength = length;
	}

	public int getLength(){return tupleLength;}

	public byte[] getTupleByteArray(){return data;}

	public void updateByteArray(byte[] data, int length){
		tupleLength = length;
		data = data;
	}
}