package myPackage;

import java.util.Random;

/*
 * +----------------------------------------------
 * | Date: 06/06/2013                            |
 * |                                             |
 * | Author:                                     |
 * |       Giannis Tzortzis                      |
 * |       2008030061                            |
 * | Course:                                     |
 * |       Data & files Structures               |
 * | Info:                                       |
 * |       The purpose of the project is the imp |
 * |       lementation of dynamic hashing on the |
 * |       main memory, by using tries.          |
 * +---------------------------------------------+
 */

public class MyMain {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Random rand = new Random();
		int min, max;
		int key = 0;
		int hashTablePos = 0;
		Trie tree;
		
/********************************************************************************/		
		HashTable hashing = new HashTable(); 		
		min = 1;
		max = 1000;
		int[] depth = new int[100]; 
		int[] insComparisons = new int[100];
		
		for (int i=0; i<100; i++){
			key = rand.nextInt(max - min + 1) + min;
			hashTablePos = hashing.hashFunction(key);
			//System.out.println("Pos: "+hashTablePos);
			tree = hashing.getTrie(hashTablePos);
			tree.insertKeyToTrie(tree.getRoot(), key);
			tree.printTrie(tree.getRoot());
			depth[i] = tree.getMaxDepth();
			insComparisons[i] = tree.getInsertComparisons();
			System.out.println(tree.getMaxDepth());
		}
		System.out.println("Average Depth: "+avg(depth, 100));
		System.out.println("Average insert comparisons: "
													+avg(insComparisons, 100));
/********************************************************************************/
		int[] searchComparisons = new int[100];
		
		for (int i=0; i<100; i++){
			key = rand.nextInt(max - min + 1) + min;
			hashTablePos = hashing.hashFunction(key);
			//System.out.println("Pos: "+hashTablePos);
			tree = hashing.getTrie(hashTablePos);
			tree.lookForKeyInTrie(tree.getRoot(), key, 0);
			//tree.printTrie(tree.getRoot());
			searchComparisons[i] = tree.getSearchComparisons();
			
			System.out.println(tree.getMaxDepth());
		}
		System.out.println("Average search comparisons: "
												+avg(searchComparisons, 100));
		
/********************************************************************************/
		int[] deleteComparisons = new int[100];
		
		for (int i=0; i<100; i++){
			key = rand.nextInt(max - min + 1) + min;
			hashTablePos = hashing.hashFunction(key);
			//System.out.println("Pos: "+hashTablePos);
			tree = hashing.getTrie(hashTablePos);
			tree.lookForKeyInTrie(tree.getRoot(), key, 0);
			//tree.printTrie(tree.getRoot());
			deleteComparisons[i] = tree.getDeleteComparisons();
			
			System.out.println(tree.getMaxDepth());
		}
		
/********************************************************************************/		
		System.out.println("Average Depth: "+avg(depth, 100));
		System.out.println("Average insert comparisons: "
		                          +(avg(insComparisons, 100)+1));
		System.out.println("Average search comparisons: "
				                  +(avg(searchComparisons, 100)+1));
		System.out.println("Average delete comparisons: "
			                      +(avg(deleteComparisons, 100)+1));
		
	}
	
	
	public static int avg(int[] a, int n){
		int sum = 0;
		for(int i=0; i<n; i++){
			sum = sum + a[i];
		}
		return sum/n;
	}

}
