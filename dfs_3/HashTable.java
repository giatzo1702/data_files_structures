package myPackage;

public class HashTable {
	final static int N = 100;
	private Trie[] staticTable;
	
/********************************************************************************/
	public HashTable(){
		staticTable = new Trie[N];
		initTable();
	}
	
	public void initTable(){
		for (int i=0; i<N; i++){
			staticTable[i] = new Trie();
		}
	}

/********************************************************************************/	
	public int hashFunction(int key){
		int pos = key%N;
		return pos;
	}

	public Trie[] getStaticTable() {
		return staticTable;
	}
	
	public void setStaticTable(Trie[] staticTable) {
		this.staticTable = staticTable;
	}
	
	public Trie getTrie(int i){
		return staticTable[i];
	}
	
	public static int getN() {
		return N;
	}

}
