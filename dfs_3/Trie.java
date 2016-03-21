package myPackage;


/*
 * In computer science, a trie, also called digital tree or prefix tree, is an
 * ordered tree data structure that is used to store a dynamic set or associat
 * ive array where the keys are usually strings. Unlike a binary search tree, 
 * no node in the tree stores the key associated with that node; instead, its 
 * position in the tree defines the key with which it is associated. All the d
 * escendants of a node have a common prefix of the string associated with tha
 * t node, and the root is associated with the empty string. Values are normal
 * ly not associated with every node, only with leaves and some inner nodes th
 * at correspond to keys of interest. For the space-optimized presentation of 
 * prefix tree, see compact prefix tree.
 *                                                                   Wikipedia
 */

public class Trie {
	final static int ROOT_LEVEL = 0;
	final static char ZERO_BIT = '0';
	final static int SEARCH = 0;
	final static int DELETION = 1;
	final static int EMPTY = -200;
	final static int FIRST_KEY = 0;
	final static int SECOND_KEY = 1;
	private int maxDepth;
	private int insertComparisons;
	private int searchComparisons;
	private int deleteComparisons;
	private TrieNode root;
	
	
/********************************************************************************/	
	public Trie(){
		this.root = new TrieNode();
		this.root.setLevel(ROOT_LEVEL);
		this.setMaxDepth(0);
		this.setDeleteComparisons(0);
		this.setInsertComparisons(0);
		this.setSearchComparisons(0);
	}

/********************************************************************************/
	//This function splits a node if it is full and a new key must be inserted
	public void splitNode(TrieNode nodeToSplit){
		TrieNode leftChild = new TrieNode();    
		TrieNode rightChild = new TrieNode();
		
		nodeToSplit.setLeftChild(leftChild);     // Setting the children
		nodeToSplit.setRightChild(rightChild);
		
		leftChild.setLevel(nodeToSplit.getLevel()+1); // Setting levels of chidren
		rightChild.setLevel(nodeToSplit.getLevel()+1);
		
		if (nodeToSplit.isFull()){
			
			auxSplit(nodeToSplit, FIRST_KEY);
			auxSplit(nodeToSplit, SECOND_KEY);
		}
	}
	
/********************************************************************************/	
	// Auxiliary function for splitting
	public void auxSplit(TrieNode auxNode, int keyNum){
		int oldKey;
		if (keyNum == FIRST_KEY){
			oldKey = auxNode.getKey1();
		}
		else{
			oldKey = auxNode.getKey2();
		}
		// After splitting this function inserts again the keys of the splitted
		// node to the right places 
		
		String binary = Integer.toBinaryString(oldKey);
		int lsbCurrent = binary.toCharArray().length - auxNode.getLevel()-1;
		
		this.setInsertComparisons(this.getInsertComparisons()+1);
		
		// Checking the bit of key according to the level of node
		if (binary.toCharArray()[lsbCurrent] == ZERO_BIT){
			insertKeyToNode(auxNode.getLeftChild(), oldKey);
		}
		else{
			insertKeyToNode(auxNode.getRightChild(), oldKey);
		}
	}
	
/********************************************************************************/
	//This function is looking for a given key in the trie
	public boolean lookForKeyInTrie(TrieNode auxNode, int key, int type){
		
		// If type = DELETION, current function looking for a key, that is gonna
		// be deleted, otherwise it is looking for a key just to find it
		
		boolean found = false;
		if (auxNode.isLeaf()){
			if (lookForKeyInNode(auxNode, key, type)){
				//case that key exists.
				found = true;
			}
		}
		else{
			// Traverse recursively the trie until a leaf is accessed 
			String binary = Integer.toBinaryString(key);
			int lsbCurrent = binary.toCharArray().length - auxNode.getLevel()-1;
			if (type == SEARCH){
				this.setSearchComparisons(this.getSearchComparisons()+1);
			}
			else{
				this.setDeleteComparisons(this.getDeleteComparisons()+1);
			}
			if (binary.toCharArray()[lsbCurrent] == ZERO_BIT){
				lookForKeyInTrie(auxNode.getLeftChild(), key, type);
			}
			else{
				lookForKeyInTrie(auxNode.getRightChild(), key, type);
			}
		}
		return found;
	}
	
/********************************************************************************/	
	public boolean lookForKeyInNode(TrieNode auxNode, int key, int type){
		boolean found = false;
		if (type == SEARCH){
			// Looking for a key in a specified node
			if ((key == auxNode.getKey1()) || (key == auxNode.getKey2())){
				found = true;
				System.out.println("Key found.Yeah...");
			}
		}
		if (type == DELETION){
			// Looking for key in a specified node
			// Delete key if it is found
			if (key == auxNode.getKey1()){
				System.out.println("Removing first key of the bucket...");
				auxNode.setKey1(EMPTY);
			}
			else if (key == auxNode.getKey2()){
				System.out.println("Removing second key of the bucket...");
				auxNode.setKey2(EMPTY);
			}
			else{
				System.out.println("Key is not found...");
			}
		}
		return found;
	}
	
/********************************************************************************/	
	public void deleteKeyFromTrie(int key){
		lookForKeyInTrie(this.getRoot(), key, DELETION);
	}
	
/********************************************************************************/
	//General insert function
	public void insertKeyToTrie(TrieNode auxNode, int key){
		//auxNode.print();
		if (auxNode.isLeaf()){
			if (auxNode == this.getRoot()){
				fixFirstLevel(key); // There is no key in trie
				return;
			}
			if (!auxNode.isFull()){
				insertKeyToNode(auxNode, key); // Non full node
			}
			else{
				leafCollisionHandler(auxNode, key); // full node
			}
		}
		else{
			//Traverse recursively the tie to attach a leaf
			String binary = Integer.toBinaryString(key);
			int lsbCurrent = binary.toCharArray().length - auxNode.getLevel()-1;
			this.setInsertComparisons(this.getInsertComparisons()+1);
			if (binary.toCharArray()[lsbCurrent] == ZERO_BIT){
				
				insertKeyToTrie(auxNode.getLeftChild(), key);
			}
			else{
				insertKeyToTrie(auxNode.getRightChild(), key);
			}
		}
	}
	
/********************************************************************************/	
	public void leafCollisionHandler(TrieNode auxNode, int key){
		
		// Detecting a collision...
		splitNode(auxNode);
		String binary = Integer.toBinaryString(key);
		int lsbCurrent = binary.toCharArray().length - auxNode.getLevel()-1;
		this.setInsertComparisons(this.getInsertComparisons()+1);
		if (binary.toCharArray()[lsbCurrent] == ZERO_BIT){
			if (!auxNode.getLeftChild().isFull()){
				insertKeyToNode(auxNode.getLeftChild(), key);
			}
			else{
				insertKeyToTrie(auxNode.getLeftChild(), key);
			}
		}
		else{
			if (!auxNode.getRightChild().isFull()){
				insertKeyToNode(auxNode.getRightChild(), key);
			}
			else{
				insertKeyToTrie(auxNode.getRightChild(), key);
			}
		}
	}
	
/********************************************************************************/	
	public void fixFirstLevel(int key){
		//System.out.println("Splitting root...");
		splitNode(this.getRoot());
		String binary = Integer.toBinaryString(key);
		int lsbPos = binary.toCharArray().length-1;
		//System.out.println("LSB_POSITION:"+binary.toCharArray()[lsbPos]);
		this.setInsertComparisons(this.getInsertComparisons()+1);
		if (binary.toCharArray()[lsbPos] == ZERO_BIT){
			//System.out.println("LEFTTTTTTT");
			insertKeyToNode(getRoot().getLeftChild(), key);
		}
		else{
			//System.out.println("RIGHTTTTTT");
			insertKeyToNode(getRoot().getRightChild(), key);
		}
	}
	
/********************************************************************************/	
	public void insertKeyToNode(TrieNode auxNode, int key){
		if (auxNode.isFull()){
			System.out.println("Node is full.");
			return;
		}
		else if (auxNode.isEmpty()){
			auxNode.setKey1(key);
		}
		else{
			if (auxNode.getKey1() == EMPTY){
				auxNode.setKey1(key);
			}
			else{
				auxNode.setKey2(key);
			}
		}
	}
	
/********************************************************************************/
	public void printTrie(TrieNode temp){
		temp.print();
		if (temp.getLevel() > this.getMaxDepth()){
			this.setMaxDepth(temp.getLevel()+1);
		}
		if (temp.getLeftChild() != null){
			System.out.println("Left");
			printTrie(temp.getLeftChild());
		}
		if (temp.getRightChild() != null){
			System.out.println("Right");
			printTrie(temp.getRightChild());
		}
	}
	
/********************************************************************************/
	public TrieNode getRoot() {
		return root;
	}
	
	public void setRoot(TrieNode root) {
		this.root = root;
	}

	public int getMaxDepth() {
		return maxDepth;
	}

	public void setMaxDepth(int maxDepth) {
		this.maxDepth = maxDepth;
	}

	public int getInsertComparisons() {
		return insertComparisons;
	}

	public void setInsertComparisons(int insertComparisons) {
		this.insertComparisons = insertComparisons;
	}

	public int getSearchComparisons() {
		return searchComparisons;
	}

	public void setSearchComparisons(int searchComparisons) {
		this.searchComparisons = searchComparisons;
	}

	public int getDeleteComparisons() {
		return deleteComparisons;
	}

	public void setDeleteComparisons(int deleteComparisons) {
		this.deleteComparisons = deleteComparisons;
	}

	
	
	
	
}
