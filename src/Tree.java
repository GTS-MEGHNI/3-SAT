import java.util.ArrayList;

public class Tree {

	int x = 0, y = 0;
	ArrayList<Integer> var = null;
	Tree son[];
	Tree root;
	public Tree() {
		for (int i = 0; i < son.length; i++) {
			this.son[i] = null;
			this.root = null;
		}	
	}

	public Tree(Tree root, Tree sons) {
		for (int i = 0; i < this.son.length; i++) {
			this.son[i] = new Tree();
			this.root = root;
			
		}
	}
	
	
}
