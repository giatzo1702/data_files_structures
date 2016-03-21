
/*This class is used to fill a binary file with N integers. Each integer is repre-
 * sented by 4 bytes in binary form, so the size of the file will be 40000000 byt-
 * es. For the filling of the file we use a buffer, that can store 256(page size)
 * integers. We fill the buffer and then copy its elements to the file until the 
 * file reach 10000000 integers. For the last page we use an auxiliary buffer th-
 * at has less than 256 integers.
 */

package myPackage;
import java.io.*;

/********************************************************************************/
public class file {
	final int ON = 1;
	final int N = 10000000;                //Number of integers
	final int PAGE_SIZE = 256;             //Size of the page
	final int PAGES = N/PAGE_SIZE;         //Number of pages except the last one
	final String FILE_NAME = "myfile.bin"; //Name of the file
	private DataOutputStream myFile;
	int[] myBuffer;                        //The basic buffer 
	
/********************************************************************************/	
	public file(int enable) throws IOException{
		myBuffer = new int[PAGE_SIZE];
		if (enable == ON){
			System.out.println("Filling the file.Please wait...");
			fillFile();
			System.out.println("The procedure has finished successfully!");
		}
	}

/********************************************************************************/	
	public void fillFile() throws IOException{
		myFile = new DataOutputStream(new FileOutputStream(FILE_NAME));
		int i,j;
		int elem;
		int[] auxBuffer;
		for (i=0; i<PAGES; i++){
			elem = 1;
			for (j=0; j<PAGE_SIZE; j++){    //Filling the buffer with 256 integers
				this.myBuffer[j] = elem + i*PAGE_SIZE;
				elem++;
			}
			for (j=0; j<PAGE_SIZE; j++){    //Copy elements from the buffer to 
											//the binary file.	
				myFile.writeInt(this.myBuffer[j]);
			}
		}
		int leftPages = N - i*PAGE_SIZE;    //The last page has less than 256 el-
		System.out.println(leftPages);      //ments.It has leftPages elements. 
		auxBuffer = new int[leftPages];     //We use auxBuffer to copy these el-
		                                    //ements to the disk.
		System.out.println("-------------- Last Page ---------------");
		for (j=0; j<leftPages; j++){
			auxBuffer[j] = j + 1 + i*PAGE_SIZE;
			//System.out.println(auxBuffer[j]);
		}
		for (j=0; j<leftPages; j++){
			myFile.writeInt(auxBuffer[j]);
			System.out.println(auxBuffer[j]);
		}
		myFile.close();
	}
	

/********************************************************************************/
	//Setters and getters
	public DataOutputStream getMyFile() {
		return myFile;
	}

	public void setMyFile(DataOutputStream myFile) {
		this.myFile = myFile;
	}

	public int[] getMyBuffer() {
		return myBuffer;
	}

	public void setMyBuffer(int[] myBuffer) {
		this.myBuffer = myBuffer;
	}

	public int getN() {
		return N;
	}

	public int getPAGE_SIZE() {
		return PAGE_SIZE;
	}

	public int getPAGES() {
		return PAGES;
	}

	public String getFILE_NAME() {
		return FILE_NAME;
	}
}
