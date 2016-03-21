package myPackage;


/********************************************************************************/
public class Node {
	
	final int EMPTY = -200;
	final int NO_ELEMS = 0;
	final int N = 256;                // Page size: 1024bytes = 256integers
	private int n;                    // Order of the tree
	private int[] keys;               // Keys of the node
	private Node[] childPtrs;         // Pointers to "children"
	private Node parentPtr;           // Pointer to "parent"
	private int keyCounter;
	private int childrenCounter;
	private int levelOfNode;
	private int diskPosition;
	

/********************************************************************************/

	public Node(){
		this.n = N/2;         // N = n*4bytes+(n-1)*4bytes+4bytes
	                              // n*4bytes:     bytes of pointers to children
		                          // (n-1)*4bytes: bytes of the keys
		                          // 4bytes:       bytes of pointer to parent
		this.setOrder(n);
		this.setKeys(new int[n]);
		this.setChildPtrs(new Node[n+1]);
		this.setKeyCounter(NO_ELEMS);
		this.setChildrenCounter(NO_ELEMS);
		this.setLevelOfNode(0);
		initKeys();
		initPointers();
	}
	
/********************************************************************************/	
	public void initKeys(){
		for (int i=0; i<(n-1); i++){
			keys[i] = EMPTY;
		}
	}
	
/********************************************************************************/	
	public void initPointers(){
		for (int i=0; i<n; i++){
			childPtrs[i] = null;
		}
		parentPtr = null;
	}
	
/********************************************************************************/
	public boolean isLeaf(){   // This function checks if the current node is a 
		                       // leaf of the tree.
		boolean var = true;
		for (int i=0; i<n; i++){
			if (childPtrs[i] != null){
				var = false;
				return var;
			}
		}
		return var;
	}

/********************************************************************************/
	public boolean isFull(){  // This function checks if the current node is full
		boolean var = true;
		if (this.keyCounter < (this.getOrder()-1)){
			var = false;
		}
		return var;
	}
	
/********************************************************************************/	
	public boolean isRoot()  // This function checks if the current node is root
	{
		boolean var = true;
		if (parentPtr != null)
		{
			var = false;
		}
		return var;
	}
	
/********************************************************************************/
	public void printNode(){
		System.out.println("   ********************");
		System.out.println("   Level: "+this.getLevelOfNode());
		System.out.println("   Disk pos: "+this.getDiskPosition());
		System.out.println("   Keys: "+this.getKeyCounter());
		//System.out.println("   Children: "+this.getChildrenCounter());
		System.out.println("      Node:");
		System.out.println("      ------");
		for (int i=0; i<this.getOrder()-1; i++){
			System.out.println("        Elem: "+keys[i]);
		}
		System.out.println("   ********************");
		System.out.println();
	}

/********************************************************************************/
	public void setKey(int key, int pos){
		keys[pos] = key;
	}
	
/********************************************************************************/
	public int getKey(int pos){
		return keys[pos];
	}
	
/********************************************************************************/
	public void setChild(Node child, int pos){
		childPtrs[pos] = child;
	}
	
/********************************************************************************/
	public Node getChild(int pos){
		return childPtrs[pos];
	}
	
/********************************************************************************/	
	public int getOrder() {
		return n;
	}

	public void setOrder(int n) {
		this.n = n;
	}

	public int getKeyCounter() {
		return keyCounter;
	}

	public void setKeyCounter(int keyCounter) {
		this.keyCounter = keyCounter;
	}

	public int getPageSize() {
		return N;
	}
	
	public Node getParentPtr() {
		return parentPtr;
	}

	public void setParentPtr(Node parentPtr) {
		this.parentPtr = parentPtr;
	}

	public int[] getKeys() {
		return keys;
	}

	public void setKeys(int[] keys) {
		this.keys = keys;
	}

	public Node[] getChildPtrs() {
		return childPtrs;
	}

	public void setChildPtrs(Node[] childPtrs) {
		this.childPtrs = childPtrs;
	}

	public int getLevelOfNode() {
		return levelOfNode;
	}

	public void setLevelOfNode(int levelOfNode) {
		this.levelOfNode = levelOfNode;
	}
	public int getChildrenCounter() {
		return childrenCounter;
	}
	
	public int getDiskPosition() {
		return diskPosition;
	}

	public void setDiskPosition(int diskPosition) {
		this.diskPosition = diskPosition;
	}

	public void setChildrenCounter(int childrenCounter) {
		this.childrenCounter = childrenCounter;
	}

	
}
