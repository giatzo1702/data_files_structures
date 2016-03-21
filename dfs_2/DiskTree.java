package myPackage;
import java.io.*;

/********************************************************************************/
public class DiskTree {
	final int NO_BYTES_TO_SKIP = 0;
	final int NULL_VAL = -500;
	final int DISK_PAGE = 255;
	final int EMPTY = -200;
	final String FILE_NAME = "btree.bin";
	private DataOutputStream myStream;
	private DataInputStream inStream;
	private Node emptyNode;
	private B_tree tree;
	private Node foundNode;
	private int[] readBuffer;
	
	
/********************************************************************************/
	public DiskTree() throws IOException{
		foundNode = new Node();
		emptyNode = new Node();
		readBuffer = new int[DISK_PAGE];
		this.setFoundNode(null);
		tree = new B_tree();
		tree.RandomizeTree(100000, 100000);
		tree.printTree(tree.getRoot());
		myStream = new DataOutputStream(new FileOutputStream(FILE_NAME));
		writeTreeToDisk();
		myStream.close();
	}
	
/********************************************************************************/
	public void search(int key, int bytesToSkip) throws IOException{
		System.out.println("Looking for key "+key);
		loadBuffer(bytesToSkip);
		for (int j=0; j<DISK_PAGE; j++){
			System.out.println("Buffer["+j+"]= "+readBuffer[j]);
		}
		int i=0;
		int childPos;
		for (int j=0; j<tree.getOrder(); j++){
			//System.out.println("for loop");
			if (key == readBuffer[j]){
				System.out.println("The key was found. Yeah!");
				return;
			}
		}
		if ((key > readBuffer[0]) && (key <readBuffer[126]) &&
				                                    isLeaf(readBuffer)){
			System.out.println("The key does not exist in the tree.");
			return;
		}
		while((i<tree.getOrder()-1) && 
				          (key>readBuffer[i]) && (readBuffer[i] != EMPTY)){
			i++;
			//System.out.println("While loop");
		}
		//inStream = new DataInputStream(new FileInputStream(FILE_NAME));
		//System.out.println("Skip Bytes: "+((i+tree.getOrder()-1-1)*4));
		System.out.println("Skip integers: "+((i+tree.getOrder()-1-1)));
		//inStream.skipBytes((i+tree.getOrder()-1-1)*4);
		//childPos = inStream.readInt();
		childPos = readBuffer[i+tree.getOrder()-1-1];
		System.out.println("Child position: "+childPos);
		//inStream.close();
		if (!isLeaf(readBuffer)){
			search(key, (childPos-1)*DISK_PAGE*4);
		}
		else{
			System.out.println("The key does not exist in the tree.");
			return;
		}
	}
/********************************************************************************/
	public void loadBuffer(int bytesToSkip) throws IOException{
		// This function load the readBuffer with elements from a specified place
		// of the file.
		
		inStream = new DataInputStream(new FileInputStream(FILE_NAME));
		inStream.skipBytes(bytesToSkip);
		for (int i=0; i<DISK_PAGE; i++){
			readBuffer[i] = inStream.readInt();
		}
		inStream.close();
	}

/********************************************************************************/
	public void writeTreeToDisk() throws IOException{
		//Node foundNode = new Node();
		
		// This method is looking for each node of the tree, according to its 
		// diskPosition and then writes its elements to the file.
		for (int i=0; i<tree.getNodeCounter(); i++){
			findNextDiskPage(i, tree.getRoot());
			System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
			this.getFoundNode().printNode();
			if (this.getFoundNode() != null){
				writeNodeToDisk(this.getFoundNode());
			}
			else{
				writeWhitePage();
			}
		}
		
	}
	
/********************************************************************************/
	public void writeNodeToDisk(Node auxNode) throws IOException{
		System.out.println("Writing non empty page to disk."+auxNode.getOrder());
		
		
		// Writing the keys of node to file.
		for (int i=0; i<auxNode.getOrder()-1; i++){
			//System.out.println("elem to write: "+auxNode.getKey(i));
			myStream.writeInt(auxNode.getKey(i));
		}
		
		// Writing the place of children disk pages to the file.
		for (int i=0; i<auxNode.getOrder(); i++){
			if (auxNode.getChild(i) != null){
				myStream.writeInt(auxNode.getOrder()*
						                       auxNode.getDiskPosition()+i);
			}
			else{
				myStream.writeInt(NULL_VAL);
			}
		}
	}
	
/********************************************************************************/	
	public void writeWhitePage() throws IOException{
		System.out.println("Writing white page to disk.");
		for (int i=0; i<tree.getOrder()-1; i++){
			myStream.writeInt(EMPTY);
		}
		for (int i=0; i<tree.getOrder(); i++){
			myStream.writeInt(EMPTY);
		}
	}
	
/********************************************************************************/
	public void findNextDiskPage(int diskPos, Node auxNode){
		if (auxNode.getDiskPosition() == diskPos){
			this.setFoundNode(auxNode);
		}
		else{
			for (int i=0; i<tree.getOrder(); i++){
				if (auxNode.getChild(i) != null){
					findNextDiskPage(diskPos, auxNode.getChild(i));
				}
			}
		}
		
	}
	
/********************************************************************************/	
	public boolean isLeaf(int[] buffer){
		boolean var = true;
		Node aux = new Node();
		for (int i=aux.getOrder(); i<DISK_PAGE; i++){
			if (buffer[i] != NULL_VAL){
				var = false;
				return var;
			}
		}
		return var;
	}
	
/********************************************************************************/	
	public Node getFoundNode() {
		return foundNode;
	}

	public void setFoundNode(Node foundNode) {
		this.foundNode = foundNode;
	}
}
