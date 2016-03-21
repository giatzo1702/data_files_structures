/*This function implements a search that uses file indexes. First of all we access
 * the file to fill a buffer with the indexes, using a given relation that genera
 * tes the h value. After that when we want to look for a key, the program compares
 * the key with the indexes to find the field where the key belongs to. Where the 
 * key is found th program uses binary search on pages to find the key. The progr-
 * am also uses cache from 4th part of the project.
 */

package myPackage;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/********************************************************************************/
public class SearchUsingIndexes {
	final int INDEX_ARRAY_SIZE = 256;
	final int NOT_EXIST = -1;
	final int MAYBE_EXIST = 0;
	final int BOUND_LIMIT = -1;
	final int EVRTHG_OK = 0;
	final int NOT_FOUND = -1;
	final int INT_SIZE = 4;
	final int ON = 1;
	final int OFF = 0;
	file fileObj;
	private DataInputStream inFile;
	private int[] indexesArray;
	private int[] pointerArray;
	private int fieldSize;
	private int[] readBuffer;
	private int fieldIndexFound;
	private int diskAccessMeter;
	private int fieldNo;
	private int lastFieldSize;
	private int lastPageIndex;
	private int leftElemNum;
	private int[] auxBuffer;
	SearchUsingCache cache;
	private int[] cacheBuffer;

/********************************************************************************/
	public SearchUsingIndexes(){	
		
	}
	
	/*public void buildCache(String filename, int key) throws IOException{
		
		cache = new SearchUsingCache(filename, key, OFF);
		cache.initCache(OFF, filename, key, ON);
	}*/
	
/********************************************************************************/
	public void initSearchUsingIndexes(String filename, int key) 
	                                                        throws IOException{
		fileObj = new file(0);
		this.setDiskAccessMeter(0);
		indexesArray = new int[fileObj.getPAGE_SIZE()];
		pointerArray = new int[fileObj.getPAGE_SIZE()];
		readBuffer = new int[fileObj.getPAGE_SIZE()];
		lastPageIndex = this.fileObj.PAGES*this.fileObj.PAGE_SIZE;
		leftElemNum = (this.fileObj.N)-(lastPageIndex);
		auxBuffer = new int[leftElemNum];
		this.setFieldNo(0);
		
		fillIndexesArray(filename);
		lastFieldSize = (fileObj.getN() - pointerArray[255])/
		                                              fileObj.getPAGE_SIZE();
		System.out.println("Field size: "+this.getFieldSize());
		this.setFieldIndexFound(indexesSearch(key, filename));
		searchKindHandler(key, filename);
	}
	
/********************************************************************************/
	// The last field has more elements than the others. So this handler is used
	//to find the kind of the field, where the key belongs to
	public void searchKindHandler(int key, String filename) throws IOException{
		if (this.getFieldIndexFound() == NOT_EXIST){
			return;
		}
		else{
			if (this.getFieldIndexFound() == pointerArray[INDEX_ARRAY_SIZE-1]){
				System.out.println("Field Index found: "+fieldIndexFound);
				lastFieldSearch(key, this.getFieldIndexFound(), filename);
				System.out.println("Disk accesses: "+
						                    this.getDiskAccessMeter());
				System.out.println("Last field size: "
						 +this.getLastFieldSize()/fileObj.getPAGE_SIZE());
			}
			else{
				System.out.println("Field Index found: "+
						                       this.getFieldIndexFound());
				fieldSearch(key, this.getFieldIndexFound(), filename);
				System.out.println("Disk accesses: "
						                      +this.getDiskAccessMeter());
				System.out.println("Last field size: "
						 +this.getLastFieldSize()/fileObj.getPAGE_SIZE());
			}
		}
	}
	
	
/********************************************************************************/	
	// This function is used to fill the buffer we the indexes.
	public void fillIndexesArray(String filename) throws IOException{
		int h, temp = 0;
		for (int i=0; i<this.fileObj.getPAGE_SIZE(); i++){
			inFile = new DataInputStream(new FileInputStream(filename));
			h = i * ((fileObj.getPAGES()/fileObj.getPAGE_SIZE())-1);
			if (i == 1){
				this.fieldSize = h;
			}
			pointerArray[i] = h * fileObj.getPAGE_SIZE();
			//System.out.println("H: "+h);
			accessPageIndexEnd(h*fileObj.getPAGE_SIZE());
			indexesArray[i] = inFile.readInt();
			inFile.close();
		}
	}
	
/********************************************************************************/	
	//Compare the key with the indexes.
	public int indexesSearch(int key, String filename) throws IOException{
		int i=0;
		
		if (lowerBoundHandler(key, filename) == NOT_EXIST){
			return NOT_EXIST;
		}
		System.out.println("Looking for the field...");
		while((key > indexesArray[i]) && (i < INDEX_ARRAY_SIZE)){
			//System.out.println("Key: "+key+" indexes: "+indexesArray[i]);
			if (i == INDEX_ARRAY_SIZE-1){
				this.setFieldNo(i+1);
				//System.out.println("----------Pointer: "+pointerArray[i]);
				return pointerArray[INDEX_ARRAY_SIZE-1];
			}
			i++;
		}
		this.setFieldNo(i);
		//System.out.println("----------Pointer: "+pointerArray[i]);
		return pointerArray[i-1];
	}
	
/********************************************************************************/
	//Looking in a field.
	public void fieldSearch(int key, int fieldIndex, String filename)
	                                                       throws IOException{
		
		int start = 0;
		int end = this.getFieldSize();
		System.out.println("---------End: "+end);
		int midPage = 0, midPageIndex = 0, midPageEnd = 0;
		
		while (start <= end){
			inFile = new DataInputStream(new FileInputStream(filename));
			
			midPage = start + (end-start)/2;
			midPageIndex = midPage * fileObj.getPAGE_SIZE();
			midPageEnd = midPageIndex + fileObj.getPAGE_SIZE();
			System.out.println("Mid page: "+midPage);
			
			accessPageIndexEnd(fieldIndex + midPageIndex + 1);
			fillBasicBuffer();
			
			if (key < readBuffer[0]){
				end = midPage - 1;
				inFile.close();
			}
			else if (key > readBuffer[255]){
				start = midPage + 1;
				inFile.close();
			}
			else{
				System.out.println("YES");
				LookingOnPage(midPage, key);
				return;
			}
		}
		
		
	}
	
/********************************************************************************/
	//Looking in a field in the last field
	public void lastFieldSearch(int key, int fieldIndex, String filename)
	                                                       throws IOException{
		
		int start = 0;
		int end = this.getLastFieldSize();
		
		while (start <= end){
			if (start == this.getLastFieldSize()-1){
				inFile.close();
				upperBoundHandler(key, filename);
				return;
			}
			inFile = new DataInputStream(new FileInputStream(filename));
			
			int midPage = start + (end-start)/2;
			int midPageIndex = midPage * fileObj.getPAGE_SIZE();
			int midPageEnd = midPageIndex + fileObj.getPAGE_SIZE();
			System.out.println("Mid page: "+midPage);
			
			accessPageIndexEnd(fieldIndex + midPageIndex + 1);
			fillBasicBuffer();
			
			if (key < readBuffer[0]){
				end = midPage - 1;
				inFile.close();
			}
			else if (key > readBuffer[255]){
				start = midPage + 1;
				inFile.close();
			}
			else{
				//System.out.println("YES");
				LookingOnPage(midPage, key);
				return;
			}
		}
		
		
	}
	
/********************************************************************************/
	//Bound handler
	public void upperBoundHandler(int key, String filename) throws IOException{
		inFile = new DataInputStream(new FileInputStream(filename));
		int lastPage = ((INDEX_ARRAY_SIZE-1)*this.getFieldSize()+
				                               this.getLastFieldSize())*256;
		System.out.println("Last: "+lastPage);
		accessPageIndexEnd(lastPage);
		LookingOnLastPage(key);
	}
	
/********************************************************************************/
	//Bound handler
	public int lowerBoundHandler(int key, String filename) throws IOException{
		inFile = new DataInputStream(new FileInputStream(filename));
		if (key < inFile.readInt()){
			inFile.close();
			System.out.println("The key does not exist!!!");
			return NOT_EXIST;
		}
		return MAYBE_EXIST;
	}
		
/********************************************************************************/
	//This function is running a binary search in the specified disk page, which
	//is saved in the readBuffer.
	public void LookingOnPage(int pageNo, int key) throws IOException{
		int i, result;
		System.out.println("Looking for key:"+key+" on page:"+(pageNo+1)+
				                               " of field: "+this.getFieldNo());
		result = myBinarySearch(key, readBuffer);
		if (result != -1){
			System.out.println("Key found on page:"+(pageNo+1)+" of field: "
					                                        +this.getFieldNo());
			System.out.println("-----------------------------------------");
			System.out.println();
			cacheBuffer = new int[fileObj.getPAGE_SIZE()];
			this.setCacheBuffer(readBuffer);
			inFile.close();
			return;
		}
		else{
			System.out.println("Key is not found");
			System.out.println("-----------------------------------------");
			System.out.println();
			inFile.close();
			return;
		}
	}
	
	
/********************************************************************************/
	//This function is running a binary search in the last disk page, which
	//is saved in the auxBuffer.
	public void LookingOnLastPage(int key) throws IOException{
		int i, result;
		this.setDiskAccessMeter(this.getDiskAccessMeter()+1);
		System.out.println("Looking for key:"+key+" on last page.");
		for (i=0; i<this.getLeftElemNum(); i++){
			auxBuffer[i] = inFile.readInt();
		}
		result = myBinarySearch(key, auxBuffer);
		if (result != -1){
			System.out.println("Key found on last page");
			System.out.println("-----------------------------------------");
			System.out.println();
			cacheBuffer = new int[this.getLeftElemNum()];
			this.setCacheBuffer(auxBuffer);
			inFile.close();
			return;
		}
		else{
			System.out.println("Key is not found");
			System.out.println("-----------------------------------------");
			System.out.println();
			inFile.close();
			return;
		}
	}
/********************************************************************************/	
	public void accessPageIndexEnd(int intNum) throws IOException
	{
		//This function is skipping intNum integers in the file, to access page's
		//end.
		int i;
		//System.out.println(" Accessing page's index...");
		inFile.skipBytes(intNum*4);
	}
	
/********************************************************************************/	
	public void fillBasicBuffer() throws IOException{
		//Loading readBuffer.
		int i;
		this.setDiskAccessMeter(this.getDiskAccessMeter()+1);
		for (i=0; i<this.fileObj.PAGE_SIZE; i++){
			readBuffer[i] = inFile.readInt();
		}
	}
	
/********************************************************************************/	
	//This function implements binary search on a given buffer. 
	public int myBinarySearch(int key, int[] auxBuffer) {
        int lo = 0;
        int hi = auxBuffer.length - 1;
        while (lo <= hi) {
            // Key is in a[lo..hi] or not present.
            int mid = lo + (hi - lo) / 2;
            if      (key < auxBuffer[mid]) hi = mid - 1;
            else if (key > auxBuffer[mid]) lo = mid + 1;
            else return mid;
        }
        return NOT_FOUND;
    }

/********************************************************************************/

	public int getFieldSize() {
		return fieldSize;
	}
	public void setFieldSize(int fieldSize) {
		this.fieldSize = fieldSize;
	}

	public int getDiskAccessMeter() {
		return diskAccessMeter;
	}
	public void setDiskAccessMeter(int diskAccessMeter) {
		this.diskAccessMeter = diskAccessMeter;
	}
	public int getFieldNo() {
		return fieldNo;
	}
	public void setFieldNo(int fieldNo) {
		this.fieldNo = fieldNo;
	}
	public int getLastFieldSize() {
		return lastFieldSize;
	}
	public void setLastFieldSize(int lastFieldSize) {
		this.lastFieldSize = lastFieldSize;
	}
	public int getLastPageIndex() {
		return lastPageIndex;
	}
	public void setLastPageIndex(int lastPageIndex) {
		this.lastPageIndex = lastPageIndex;
	}
	public int getLeftElemNum() {
		return leftElemNum;
	}
	public void setLeftElemNum(int leftElemNum) {
		this.leftElemNum = leftElemNum;
	}


	public int getFieldIndexFound() {
		return fieldIndexFound;
	}


	public void setFieldIndexFound(int fieldIndexFound) {
		this.fieldIndexFound = fieldIndexFound;
	}
	public int[] getCacheBuffer() {
		return cacheBuffer;
	}
	public void setCacheBuffer(int[] cacheBuffer) {
		this.cacheBuffer = cacheBuffer;
	}
}
