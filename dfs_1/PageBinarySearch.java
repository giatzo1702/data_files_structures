/*This class implements Binary Search on disk pages. When the page, where the key
 * must be in, is attached the program starts a binary search in the speciefied 
 * disk page.*/

package myPackage;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/********************************************************************************/
public class PageBinarySearch {
	final int NOT_FOUND = -1;
	final int BOUND_LIMIT = -1;
	final int EVRTHG_OK = 0;
	final int ON = 1;
	final int OFF = 0;
	private file fileObj;
	private DataInputStream inFile;
	private int[] readBuffer; //This buffer is used to read a specified page from
						      //the disk.
	private int[] auxBuffer;  //Auxiliary buffer is used for searching in the la-
	                          //st page. This buffer has less than PAGE_SIZE ele-
	                          //ments.
	private int lastPage, leftElemNum, lastPageNo;
	private int diskAccessMeter; //This variable is used to count disk accesses.
	private int firstNum;
	
	
/********************************************************************************/
	//Constructor of the class
	public PageBinarySearch(String filename, int key) throws IOException{
		System.out.println();
		System.out.println("Binary Search on disk pages");
		System.out.println("*************************************************");
		System.out.println();
		//Initializing the elements of the class.
		fileObj = new file(0);
		readBuffer = new int[this.fileObj.PAGE_SIZE];
		lastPage = this.fileObj.PAGES*this.fileObj.PAGE_SIZE;
		leftElemNum = (this.fileObj.N)-(lastPage);
		auxBuffer = new int[leftElemNum];
		lastPageNo = this.fileObj.getPAGES();
		diskAccessMeter = 0;
		this.setFirstNum(filename);
		basicPageBinarySearchMethod(filename, 0, lastPageNo, key);
		System.out.println("Disk accesses:"+this.getDiskAccessMeter());
	}
	
	
/********************************************************************************/
	//Recursive binary search on disk pages.
	public void basicPageBinarySearchMethod(String filename, int start, int end,
											int key) throws IOException{
		inFile = new DataInputStream(new FileInputStream(filename));
		
		int bound = boundHandler(start, key);
		if (bound != EVRTHG_OK){return;}
		
		int midPage = start + (end-start)/2;
		int midPageIndex = ((midPage) * (this.fileObj.PAGE_SIZE));
		int midPageEnd = midPageIndex + this.fileObj.PAGE_SIZE;
		
		//System.out.println("----------------------------------------");
		System.out.println("Page:"+(midPage+1));
		System.out.println(" Index:"+midPageIndex+" End:"+midPageEnd);
		
		//Accessing midPage's index.
		accessPageIndex(midPageIndex - 1);
		
		fillBasicBuffer();
		System.out.println();
		
		if (key < readBuffer[0]){
			//Case that the key is in the left part.
			end = midPage - 1;
			inFile.close(); //We close the stream because in the next recursion
							//we want to access new midPage.
			basicPageBinarySearchMethod(filename, start, end, key);
		}
		else if (key > readBuffer[255]){
			//Case that the key is in the left part.
			start = midPage+1;
			inFile.close();
			basicPageBinarySearchMethod(filename, start, end, key);
		}
		else{
			//Case that:
			//a) key is bigger than the integer, which is in the midPage-
			//Index position of the file, and
			//b) key is less than the integer, which is in the midPageEnd
			//position of the file.
			LookingOnPage(midPage, key);
		}
		
		
	}
	
/********************************************************************************/	
	public int boundHandler(int start, int key) throws IOException{
		if (start == this.getLastPageNo()){
			//In this case the program has to start searching in the last page, 
			//which has less than PAGE_SIZE elements. So we call the alternative
			//function "LookingOnLastPage".
			System.out.println("Start == End");
			accessPageIndex(this.getLastPage());
			LookingOnLastPage(key);
			return BOUND_LIMIT;
		}
		if (key < this.getFirstNum()){
			
			System.out.println("Key does not exist.");
			return BOUND_LIMIT;
		}
		
		return EVRTHG_OK;
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
		System.out.println(" Accessing page's index...");
		inFile.skipBytes(intNum*4);
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
	public int getLastPage() {
		return lastPage;
	}
	public void setLastPage(int lastPage) {
		this.lastPage = lastPage;
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


	public int getFirstNum() {
		return firstNum;
	}


	public void setFirstNum(String filename) throws IOException {
		inFile = new DataInputStream(new FileInputStream(filename));
		this.firstNum = inFile.readInt();
		inFile.close();
	}
	
	
	
}
