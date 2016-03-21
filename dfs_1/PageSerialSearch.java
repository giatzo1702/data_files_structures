/*This class implements a serial search on disk pages. That means that the program
 * reads a disk page from the file and is looking for the key on it. The program
 * does the same thing serially until it finds the specified key. The key can also 
 * not found.
 */

package myPackage;
import java.io.*;

/********************************************************************************/
public class PageSerialSearch {
	final int NOT_FOUND = -1;
	private file fileObj;
	private int[] buffer;
	private int[] auxBuffer;
	private DataInputStream inFile;
	private int diskAccessMeter;
	
/********************************************************************************/
	public PageSerialSearch(String filename, int key) throws IOException{
		System.out.println();
		System.out.println("Serial Search on disk pages");
		System.out.println("*************************************************");
		fileObj = new file(0);
		buffer = new int[this.fileObj.PAGE_SIZE];
		this.setDiskAccessMeter(0);
		SearchPages(key, filename);
		System.out.println("Disk accesses:"+this.getDiskAccessMeter());
	}
	
/********************************************************************************/
	public void SearchPages(int key, String filename) throws IOException{
		inFile = new DataInputStream(new FileInputStream(filename));
		int i, j, result;
		int lastPageElems;
		for (i=0; i<this.fileObj.PAGES; i++){
			System.out.println("Looking for key:"+key+" on page:"+(i+1));
			this.setDiskAccessMeter(this.getDiskAccessMeter()+1);
			//Reading a page from the disk.
			for (j=0; j<this.fileObj.PAGE_SIZE; j++){
				buffer[j] = inFile.readInt();
			}
			//Looking on the page for the key.
			result = myBinarySearch(key, buffer);
			if (result != NOT_FOUND){
				System.out.println("Found on page:"+(i+1));
				//System.out.println("Disk accesses:"+this.getDiskAccessMeter());
				inFile.close();
				return;
			}
		}
		
		// Looking for the key on the last page that has less than 256 integers
		lastPageElems = (this.fileObj.N)-(i*this.fileObj.PAGE_SIZE);
		auxBuffer = new int[lastPageElems];
		System.out.println("Looking for key:"+key+" on page:"+(i+1));
		this.setDiskAccessMeter(this.getDiskAccessMeter()+1);
		for (j=0; j<lastPageElems; j++){
			auxBuffer[j] = inFile.readInt();
		}
		result = myBinarySearch(key, auxBuffer);
		if (result != NOT_FOUND){
			System.out.println("Found on page:"+(i+1));
			inFile.close();
			return;
		}
		System.out.println("The key is not found.");
		inFile.close();
	}
	
/********************************************************************************/
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
	public file getFileObj() {
		return fileObj;
	}

	public void setFileObj(file fileObj) {
		this.fileObj = fileObj;
	}

	public int[] getBuffer() {
		return buffer;
	}

	public void setBuffer(int[] buffer) {
		this.buffer = buffer;
	}
	
	public int getDiskAccessMeter() {
		return diskAccessMeter;
	}

	public void setDiskAccessMeter(int diskAccessMeter) {
		this.diskAccessMeter = diskAccessMeter;
	}
}
