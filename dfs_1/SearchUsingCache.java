/*This class implements a search using cache. Cache is implemented as a queue that
 * contains buffers(disk pages). So given a specified key the program is looking 
 * for that in cache. If the key exist in cache, that means we 've got a hit,
 * and there are no disk accesses. Otherwise we 've got a miss and the program 
 * uses interpolation search to look for the key in file. If interpolation search 
 * finds the key in a disk page, then the program updates the cache by inse-
 * rting that page in the cache. 
 */ 

package myPackage;

import java.io.IOException;
import java.util.*;

/********************************************************************************/
public class SearchUsingCache {
	final int ZERO_ACCESSES = 0;
	final int NOT_FOUND = -1;
	final int FOUND = 1;
	final int EMPTY = 0;
	final int FULL = 100;
	final int OFF = 0;
	final int ON = 1;
	file fileObj;
	PageInterpolationSearch cacheSearch;
	Queue<int[]> cache;
	private int cacheSize;
	private int diskAccessMeter;
	private String status;
	private int hit;
	private int miss;
	private SearchUsingIndexes indexSearchObj;
	



/********************************************************************************/
	public SearchUsingCache()
	                                                    throws IOException{
		fileObj = new file(OFF);
		cache = new LinkedList<int[]>();
		//initCache(OFF, filename, key);
		//indexSearch = new SearchUsingIndexes();
		this.setDiskAccessMeter(0);
		this.setHit(0);
		this.setMiss(0);
	}
	

/********************************************************************************/
	//This function is used if we want to load cache with some pages, so that we
	//have more hits.
	public void initCache(int enable, String filename, int key, int indexSearch) 
	                                                       throws IOException{
		if (enable == ON){
			
			CachingPages(filename);
		}
		else{
			SearchPagesInCache(filename, key, indexSearch);
			printCacheSearchInfo();
		}
	}
	
/********************************************************************************/	
	public void SearchPagesInCache(String filename, int key, int indexSearch)
	                                                          throws IOException{
		int found = NOT_FOUND;
		int[] auxBuffer;
		int result;
		//Looking for the key in cache
		System.out.println("Searching using cache");
		System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++");
		System.out.println("*************************************************");
		System.out.println("Searching for the key in cache.");
		for(int i=0; i<cache.size(); i++){
			auxBuffer = cache.remove();
			System.out.println();
			
			result = myBinarySearch(key, auxBuffer);
			if (result != NOT_FOUND){
				//We found the key in cahche so we have got a hit
				System.out.println("We have a hit! The key exists in cache.");
				found = FOUND;
			}
			cache.add(auxBuffer);
		}
		if (found == FOUND){
			//Case that we have got a hit
			this.setStatus("hit");
			this.setHit(this.getHit()+1);
			this.setDiskAccessMeter(0);
			return;
		}
		else
		{
			//Case that we have got a miss
			if (indexSearch == ON){
				//This is used in the 5th part of the project
				this.setStatus("miss");
				this.setMiss(this.getMiss()+1);
				System.out.println("We have a miss. " +
						                    "The key does not exist in cache.");
				indexSearchObj = new SearchUsingIndexes();
				indexSearchObj.initSearchUsingIndexes(filename, key);
				this.updateCache(indexSearchObj.getCacheBuffer());
				this.setDiskAccessMeter(indexSearchObj.getDiskAccessMeter());
			}
			else{
				//We have got a miss, so we use interpolation search to find the
				//key in the given file.
				this.setStatus("miss");
				this.setMiss(this.getMiss()+1);
				System.out.println("We have a miss. " +
						                    "The key does not exist in cache.");
				cacheSearch = new PageInterpolationSearch(filename, key);
				//Updating the cache.
				this.updateCache(cacheSearch.getCacheBuffer());
				this.setDiskAccessMeter(cacheSearch.getDiskAccessMeter());
				
			}
		}
	}

/********************************************************************************/
	//This function generates random keys and uses interpolation search to find
	//disk pages which contain those keys. Those pages are inserted in cache.
	public void CachingPages(String filename) throws IOException{
		int key = 0;
		Random rand = new Random();
		int min = 0, max = this.fileObj.N;
		System.out.println();
		System.out.println("Caching pages...");
		System.out.println("*************************************************");
		for (int i=0; i<FULL; i++){
			key = rand.nextInt(max - min + 1) + min;
			cacheSearch = new PageInterpolationSearch(filename, key);
			this.cache.add(cacheSearch.getCacheBuffer());
		}
		
	}
	
	
/********************************************************************************/
	//This function is used to update the cache. If the cache has less than 100
	//elements then we just put a new page in it. Otherwise we remove the oldest
	//page and add the new one.
	public void updateCache(int[] newBuffer){
		System.out.println();
		System.out.println("Searching using cache");
		System.out.println("*************************************************");
		if (cache.size() < FULL){
			System.out.println("Caching a new page...");
			cache.add(newBuffer);
		}
		else{
			System.out.println("Removing the oldest buffer...");
			cache.remove();
			System.out.println("Adding the new buffer to cache...");
			cache.add(newBuffer);
		}
	}
	

/********************************************************************************/
	public void printCache(){
		int[] buffer;
		System.out.println("Printing Cache...");
		System.out.println("---------------------------");
	
		for (int j=0; j<cache.size(); j++){
			buffer = new int[cache.iterator().next().length];
			buffer = cache.remove();
			for(int i=0; i<buffer.length; i++){
				System.out.println("buffer["+(i+1)+"]: "+buffer[i]);
			}
			System.out.println("---------------------------");
			System.out.println();
			cache.add(buffer);
		}
		//cache.remove();
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
	public void printCacheSearchInfo(){
		System.out.println();
		System.out.println("*************************************************");
		System.out.println("* ----------------- Cache Info ------------------");
		System.out.println("*");
		//System.out.println("* Key: "+key);
		System.out.println("* Cache hits: "+this.getHit());
		System.out.println("* Cache misses: "+this.getMiss());
		System.out.println("* Disk Accesses: "+this.getDiskAccessMeter());
		System.out.println("* Cache Size: "+this.cache.size());
		System.out.println("*************************************************");
	}
/********************************************************************************/	
	public int getCacheSize() {
		return cacheSize;
	}
	public void setCacheSize(int cacheSize) {
		this.cacheSize = cacheSize;
	}

	public Queue<int[]> getCache() {
		return cache;
	}
	public void setCache(Queue<int[]> cache) {
		this.cache = cache;
	}

	public int getDiskAccessMeter() {
		return diskAccessMeter;
	}
	public void setDiskAccessMeter(int diskAccessMeter) {
		this.diskAccessMeter = diskAccessMeter;
	}


	public String getStatus() {
		return status;
	}


	public void setStatus(String status) {
		this.status = status;
	}
	

	public int getHit() {
		return hit;
	}


	public void setHit(int hit) {
		this.hit = hit;
	}


	public int getMiss() {
		return miss;
	}


	public void setMiss(int miss) {
		this.miss = miss;
	}
}
