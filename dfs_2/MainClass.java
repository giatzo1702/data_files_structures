package myPackage;

/*
 * +----------------------------------------------
 * | Date: 18/04/2013                            |
 * |                                             |
 * | Author:                                     |
 * |       Giannis Tzortzis                      |
 * |       2008030061                            |
 * | Course:                                     |
 * |       Data & files Structures               |
 * | Info:                                       |
 * |       The purpose of the project is to ob-  |
 * |       serve the performance of a btree tha  |
 * |       t is implemented both running on the  |
 * |       main memory and hard disk.
 * +---------------------------------------------+
 */

import java.io.IOException;
import java.util.Random;

/********************************************************************************/
public class MainClass {

	/**
	 * @param args
	 */
/********************************************************************************/	
	public static void main(String[] args) throws IOException {
		int key = 0;
		B_tree tree = new B_tree();
		int[] buffer = new int[50];
		int[] buffer1 = new int[50];
		int[] buffer2 = new int[50];
		int[] buffer3 = new int[50];
		int[] buffer4 = new int[50];
		//tree.RandomizeTree(1000, 10000000);
		//DiskTree disk = new DiskTree();
		
		//disk.search(800, 0);
		//tree.printTree(tree.getRoot());
		tree.RandomizeTree(1000, 10000);
		tree.printTree(tree.getRoot());
		
		Random rand = new Random();
		int min = 1, max = 10000;
		
		for (int i=0; i<50; i++){
			key = rand.nextInt(max - min + 1) + min;
			tree.setNodeAccesses(0);
			tree.findNode(tree.getRoot(), key);
			buffer[i] = tree.getNodeAccesses();
		}
		//System.out.println("Average_1000: "+average(buffer));
		
		tree.RandomizeTree(10000, 100000);
        max = 100000;
		
		for (int i=0; i<50; i++){
			key = rand.nextInt(max - min + 1) + min;
			tree.setNodeAccesses(0);
			tree.findNode(tree.getRoot(), key);
			buffer1[i] = tree.getNodeAccesses();
		}
		
		tree.RandomizeTree(100000, 1000000);
        max = 100000;
		
		for (int i=0; i<50; i++){
			key = rand.nextInt(max - min + 1) + min;
			tree.setNodeAccesses(0);
			tree.findNode(tree.getRoot(), key);
			buffer2[i] = tree.getNodeAccesses();
		}
		
		tree.RandomizeTree(1000000, 100000);
        max = 1000000;
		
		for (int i=0; i<50; i++){
			key = rand.nextInt(max - min + 1) + min;
			tree.setNodeAccesses(0);
			tree.findNode(tree.getRoot(), key);
			buffer3[i] = tree.getNodeAccesses();
		}
		
		System.out.println("+--------------------+");
		System.out.println("| Average_1000:    "+average(buffer)+" |");
		System.out.println("| Average_10000:   "+average(buffer1)+" |");
		System.out.println("| Average_100000:  "+average(buffer2)+" |");
		System.out.println("| Average_1000000: "+average(buffer3)+" |");
		System.out.println("+--------------------+");
	}
/********************************************************************************/
	public static int average(int[] array){
		int s=0;
		for (int i=0; i<50; i++){
			s = s + array[i];
		}
		return s/50;
	}
}
