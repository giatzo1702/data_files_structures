package myPackage;


public class TrieNode {
	final static int EMPTY = -200;
	private int key1;                 // 2 keys stored in each node
	private int key2;
	private int level;                // level of the current node
	private TrieNode leftChild;       // pointers to the children
	private TrieNode rightChild;
	
/********************************************************************************/
	public TrieNode(){
		this.key1 = EMPTY;
		this.key2 = EMPTY;
		this.level = EMPTY;
		this.leftChild = null;
		this.rightChild = null;
	}

/********************************************************************************/	
	public boolean isLeaf(){         // This functions returns true if the current
		                             // node is a leaf of trie.
		boolean temp = false;
		if ((this.getLeftChild() == null) && (this.getRightChild() == null)){
			temp = true;
		}
		return temp;
	}
	
/********************************************************************************/	
	public boolean isEmpty(){       // This functions returns true if the node
		                            // is empty
		boolean temp = false;
		if ((this.getKey1() == EMPTY) && (this.getKey2() == EMPTY)){
			temp = true;
		}
		return temp;
	}

/********************************************************************************/	
	public boolean isFull(){       // This functions returns true if the node
                                   // is full.
		boolean temp = false;
		if ((this.getKey1() != EMPTY) && (this.getKey2() != EMPTY)){
			temp = true;
		}
		return temp;
	}

/********************************************************************************/
	public void print(){           // Print function for the lements of node.
		System.out.println();
		System.out.println("*************");
		System.out.println("Node:");
		System.out.println("-----");
		System.out.println();
		System.out.println("  Key1:"+this.getKey1());
		System.out.println("  Key2:"+this.getKey2());
		System.out.println("  Level:"+this.getLevel());
		System.out.println("*************");
	}
	
/********************************************************************************/	
	public int getKey1() {
		return key1;
	}
	public void setKey1(int key) {
		this.key1 = key;
	}
	
	public int getKey2() {
		return key2;
	}
	public void setKey2(int key) {
		this.key2 = key;
	}

	public TrieNode getLeftChild() {
		return leftChild;
	}
	public void setLeftChild(TrieNode leftChild) {
		this.leftChild = leftChild;
	}

	public TrieNode getRightChild() {
		return rightChild;
	}
	public void setRightChild(TrieNode rightChild) {
		this.rightChild = rightChild;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}
	
}
