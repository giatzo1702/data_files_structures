/*
 * +----------------------------------------------
 * | Date: 09/03/2013                            |
 * |                                             |
 * | Author:                                     |
 * |       Giannis Tzortzis                      |
 * |       2008030061                            |
 * | Course:                                     |
 * |       Data & files Structures               |
 * | Info:                                       |
 * |       The purpose of the project is to ob-  |
 * |       serve the eficiency of some search-   |
 * |       ing algorithms applied on disk pages  |
 * +---------------------------------------------+
 */
package myPackage;
import java.io.*;
import java.util.Random;

/********************************************************************************/
public class MainClass {
	
	public static void main(String[] args) throws IOException {
		
		final int ON = 1;
		final int OFF = 1;
		final int TIMES = 20;
		final String filename = "myfile.bin";
		
		file fileObj = new file(0);
		int key = 0;
		Random rand = new Random();
		int min = 1, max = 10000000;//fileObj.N;
		
		file newFile = new file(ON);
		
/********************************************************************************/		
		int[] SerialDiskAccessMeter = new int[TIMES];
		int[] BinaryDiskAccessMeter = new int[TIMES];
		int[] InterpolDiskAccessMeter = new int[TIMES];
		int[] CacheDiskAccessMeter = new int[TIMES];
		int[] IndexesDiskAccessMeter = new int[TIMES];
		
/********************************************************************************/		
		int SerialSearchAverage = 0;
		int BinarySearchAverage = 0;
		int InterpolSearchAverage = 0;
		int CacheSearchAverage = 0;
		int IndexesSearchAverage = 0;
		
/********************************************************************************/		
		PageSerialSearch SerialSearch;
		PageBinarySearch BinarySearch;
		PageInterpolationSearch InterpolSearch;
		SearchUsingCache cacheSearch = new SearchUsingCache();

		
/********************************************************************************/		
		for(int i=0; i<TIMES; i++){
			key = rand.nextInt(max - min + 1) + min;
			SerialSearch = new PageSerialSearch(filename, key);
			SerialDiskAccessMeter[i] = SerialSearch.getDiskAccessMeter();
		}
		SerialSearchAverage = accessesAverage(SerialDiskAccessMeter);
		System.out.println("##############################");
		System.out.println("Average: "+SerialSearchAverage);
		
/********************************************************************************/
		for(int i=0; i<TIMES; i++){
			key = rand.nextInt(max - min + 1) + min;
			BinarySearch = new PageBinarySearch(filename, key);
			BinaryDiskAccessMeter[i] = BinarySearch.getDiskAccessMeter();
		}
		BinarySearchAverage = accessesAverage(BinaryDiskAccessMeter);
		System.out.println("##############################");
		System.out.println("Average: "+BinarySearchAverage);
		
/********************************************************************************/
		for(int i=0; i<TIMES; i++){
			key = rand.nextInt(max - min + 1) + min;
			InterpolSearch = new PageInterpolationSearch(filename, key);
			InterpolDiskAccessMeter[i] = InterpolSearch.getDiskAccessMeter();
		}
		InterpolSearchAverage = accessesAverage(InterpolDiskAccessMeter);
		System.out.println("##############################");
		System.out.println("Average: "+InterpolSearchAverage);
		
/********************************************************************************/
		for(int i=0; i<TIMES; i++){
			key = rand.nextInt(max - min + 1) + min;
			cacheSearch.SearchPagesInCache(filename, key, OFF);
			CacheDiskAccessMeter[i] = cacheSearch.getDiskAccessMeter()-3;
		}
		cacheSearch.printCacheSearchInfo();
		CacheSearchAverage = accessesAverage(CacheDiskAccessMeter);
		System.out.println("##############################");
		System.out.println("Average: "+CacheSearchAverage);
		
/********************************************************************************/
		for(int i=0; i<TIMES; i++){
			key = rand.nextInt(max - min + 1) + min;
			cacheSearch.SearchPagesInCache(filename, key, ON);
			IndexesDiskAccessMeter[i] = cacheSearch.getDiskAccessMeter();
		}
		IndexesSearchAverage = accessesAverage(IndexesDiskAccessMeter);
		System.out.println("##############################");
		System.out.println("Average: "+IndexesSearchAverage);
		
/********************************************************************************/
		System.out.println();
		System.out.println("##############################");
		System.out.println("# Serial Search       : "+SerialSearchAverage+" #");
		System.out.println("# Binary Search       : "+BinarySearchAverage+"   #");
		System.out.println("# Interpolation Search: "+InterpolSearchAverage+"    #");
		System.out.println("# Search using cache  : "+CacheSearchAverage+"    #");
		System.out.println("# Search using indexes: "+IndexesSearchAverage+"    #");
		System.out.println("##############################");
		
		cacheSearch.printCacheSearchInfo();
	}	

	
	
	public static int accessesAverage(int[] buffer){
		int sum = 0, average = 0;
		final int TIMES = 20;
		
		for (int i=0; i<TIMES; i++){
			sum = sum + buffer[i];
		}
		
		average = sum / TIMES;
		return average;
	}
	

}
/********************************************************************************/

