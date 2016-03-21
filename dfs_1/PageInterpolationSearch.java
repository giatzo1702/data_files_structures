/*This class implements the interpolation search. That kind of search looks like 
 * binary search. The basic difference is that interpolation search is trying to
 * guess where the key is.  It parallels how humans search through a telephone 
 * book for a particular name, the key value by which the book's entries are ord
 * ered.
 */

package myPackage;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/********************************************************************************/
public class PageInterpolationSearch {
	final int BOUND_LIMIT = -1;
	final int EVRTHG_OK = 0;
	final int NOT_FOUND = -1;
	final int INT_SIZE = 4;
	final int ON = 1;
	final int OFF = 0;
	private file fileObj;
	private DataInputStream inFile;
	private int[] readBuffer; //This buffer is used to read a specified page from
						      //the disk.
	private int[] auxBuffer;  //Auxiliary buffer is used for searching in the la-
	                          //st page. This buffer has less than PAGE_SIZE ele-
	                          //ments.
	private int lastPageIndex, leftElemNum, lastPageNo;
	private int diskAccessMeter; //This variable is used to count disk accesses.
	private int[] cacheBuffer; //This buffer is used by the class SearchUsingCa-
	                           //che.
	
	private int L, U, n, low, high;
	
	
/********************************************************************************/
	//Constructor of the class
	public PageInterpolationSearch(String filename, int key) throws IOException{
		System.out.println();
		System.out.println("Interpolation Search on disk pages ");
		System.out.println("*************************************************");
		System.out.println();
		//Initializing the elements of the class.
		fileObj = new file(0);
		readBuffer = new int[this.fileObj.PAGE_SIZE];
		lastPageIndex = this.fileObj.PAGES*this.fileObj.PAGE_SIZE;
		leftElemNum = (this.fileObj.N)-(lastPageIndex);
		
		auxBuffer = new int[leftElemNum];
		lastPageNo = this.fileObj.getPAGES();
		diskAccessMeter = 0;
		n = this.fileObj.getPAGES();
		init(filename);
		basicPageBinaryInterSearchMethod(filename, key);
		System.out.println("Disk accesses:"+this.getDiskAccessMeter());
	}
	
/********************************************************************************/	
	public void init(String filename) throws IOException{
		this.setLow(1);
		this.setHigh(this.fileObj.getN());
	}

/********************************************************************************/
	//Recursive interpolation search on disk pages.
	public void basicPageBinaryInterSearchMethod(String filename, 
			                                       int key) throws IOException{
		
		inFile = new DataInputStream(new FileInputStream(filename));
		
		//System.out.println("----------------------------------------");
		
		int size;
		size = (((this.getHigh()-this.fileObj.getPAGE_SIZE())-this.getLow()))
		                                          /this.fileObj.getPAGE_SIZE();
		System.out.println("Size: " +size);
		
		accessPageIndex(this.getLow()-1);
		this.setL(inFile.readInt());  //Setting the L
		System.out.println("L: " +this.getL());
		accessPageIndex(this.getHigh()-this.getLow()-1);
		this.setU(inFile.readInt());  //Setting U
		System.out.println("U: " +this.getU());
		inFile.close();
		
		int h = hHandler(size, key); //Auxiliary handler that counts "h"
		
		// The current disk page.
		int lookUpPage = (this.getLow()/this.fileObj.getPAGE_SIZE()) + h;
		
		// Handler for the bounds.
		int bound = boundHandler(lookUpPage, key, filename);
		if (bound != EVRTHG_OK){return;}
		
		System.out.println("LookUpPage: " +(lookUpPage+1));
		System.out.println("H: " +h);
		int lookUpPageIndex = (lookUpPage) * this.fileObj.getPAGE_SIZE();
		int lookUpPageEnd = lookUpPageIndex + this.fileObj.getPAGE_SIZE();
		System.out.println(" Index: " +(lookUpPageIndex+" End:"+lookUpPageEnd));
		System.out.println();
		
		inFile = new DataInputStream(new FileInputStream(filename));
		
		accessPageIndex(lookUpPageIndex);
		fillBasicBuffer();
		
		if (key < readBuffer[0]){
			this.setHigh(lookUpPageEnd - 1);
			inFile.close();
			basicPageBinaryInterSearchMethod(filename, key);
		}
		else if (key > readBuffer[this.fileObj.getPAGE_SIZE()-1]){
			this.setLow(lookUpPageIndex);
			inFile.close();
			basicPageBinaryInterSearchMethod(filename, key);
		}
		else{
			LookingOnPage(lookUpPage, key);
		}
		
	}
	
/********************************************************************************/	
// This function is used to handle some special situations. For example key can 
	//be smaller than the first integer of the file. Without this handler, the 
	//program would stuck.
public int boundHandler(int page, int key, String filename) throws IOException{
	inFile = new DataInputStream(new FileInputStream(filename));
	if (page == this.getLastPageIndex()/this.fileObj.getPAGE_SIZE()){
		System.out.println("Start == End");
		accessPageIndex(this.getLastPageIndex() + 1);
		LookingOnLastPage(key);
		return BOUND_LIMIT;
	}
	if (key < inFile.readInt()){
		System.out.println("Key does not exist!!!");
		inFile.close();
		return BOUND_LIMIT;
	}
	return EVRTHG_OK;
}

/********************************************************************************/
/*This function is used to control h, the result of the basic relation using int-
 * erpolation search. When number of pages is less or equal to 4 there is a lit-
 * tle bug, because of the division that takes place in the equation. In that ca-
 * se h=0, so the program crashes. Finally we use this handler to make h=1, in 
 * such cases. So the procedure goes on and the program is able to check the la-
 * st 4 pages.
 */
public int hHandler(int pages, int key){
	int h;
	if (pages <= 4){
		h = 1;
	}
	else{
		h = (int)((pages+1)*((long)key - this.getL())/
										(this.getU()-this.getL()));
	}
	return h;
}
	
/********************************************************************************/
	//This function is running a binary search in the specified disk page, which
	//is saved in the readBuffer.
	public void LookingOnPage(int pageNo, int key) throws IOException{
		int i, result;
		System.out.println("Looking for key:"+key+" on page:"+(pageNo+1));
		result = myBinarySearch(key, readBuffer);
		if (result != -1){
			System.out.println("Key found on page:"+(pageNo+1));
			inFile.close();
			cacheBuffer = new int[fileObj.getPAGE_SIZE()];
			this.setCacheBuffer(readBuffer);
			return;
		}
		else{
			System.out.println("Key is not found");
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
		System.out.println("Looking for key:"+key+" on last page:"
													+(this.getLastPageNo()+1));
		for (i=0; i<this.getLeftElemNum(); i++){
			auxBuffer[i] = inFile.readInt();
		}
		result = myBinarySearch(key, auxBuffer);
		if (result != -1){
			System.out.println("Key found on last page");
			inFile.close();
			cacheBuffer = new int[this.getLeftElemNum()];
			this.setCacheBuffer(auxBuffer);
			return;
		}
		else{
			System.out.println("Key is not found");
			inFile.close();
			return;
		}
	}
	
/********************************************************************************/	
	public void accessPageIndex(int intNum) throws IOException
	{
		//This function is skipping intNum integers in the file, to access page's
		//index.
		int i;
		//System.out.println(" Accessing page's index...");
		inFile.skipBytes((intNum-1)*4);
		//inFile.readInt();
	}
	
/********************************************************************************/	
	public void fillBasicBuffer() throws IOException{
		//Loading readBuffer.
		int i;
		
		this.setDiskAccessMeter(this.getDiskAccessMeter()+1);
		for (i=0; i<this.fileObj.PAGE_SIZE; i++){
			readBuffer[i] = inFile.readInt();
		}
		cacheBuffer = new int[this.fileObj.getPAGE_SIZE()];
		this.setCacheBuffer(readBuffer);
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
	
	public int getLastPageNo() {
		return lastPageNo;
	}
	public void setLastPageNo(int lastPageNo) {
		this.lastPageNo = lastPageNo;
	}

	public int getDiskAccessMeter() {
		return diskAccessMeter;
	}
	public void setDiskAccessMeter(int diskAccessMeter) {
		this.diskAccessMeter = diskAccessMeter;
	}

	public int getL() {
		return L;
	}
	public void setL(int l) {
		L = l;
	}

	public int getU() {
		return U;
	}
	public void setU(int u) {
		U = u;
	}

	public int getLow() {
		return low;
	}
	public void setLow(int low) {
		this.low = low;
	}

	public int getHigh() {
		return high;
	}
	public void setHigh(int high) {
		this.high = high;
	}

	public int getN() {
		return n;
	}

	public void setN(int n) {
		this.n = n;
	}

	public int[] getCacheBuffer() {
		return cacheBuffer;
	}

	public void setCacheBuffer(int[] cacheBuffer) {
		this.cacheBuffer = cacheBuffer;
	}
	
}
