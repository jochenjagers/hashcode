package gingerninjas.jochen.pizza;

import java.util.ArrayList;


public class TreeNode
{
	int decision;
	int iteration;
	ArrayList<TreeNode> children;
	TreeNode parent;
	
	boolean solved = false;
	
	int tries = 0;

	TreeNode(int decision, int iteration, TreeNode parent) {
		this.decision = decision;
		this.children = new ArrayList<>();
		this.iteration = iteration;
	}
	
	public void addChild(int decision, int iteration) {
		this.children.add(new TreeNode(decision,iteration, this));
	}
	
	public void checkSolved() {
		if(this.children.size() == 0) {
			// Wenn es keine Children gibt kann keine Aussage getroffen werden. Evtl Endknoten. Dann muss der solver das flag setzten
			return;
		}
		this.solved = true;
		for(TreeNode node : this.children) {
			this.solved &= node.solved;
		}
		if(this.parent != null && this.solved == true) {
			this.parent.checkSolved();
		}
	}

	public void setSolved(boolean b)
	{
		this.solved = b;
		this.checkSolved();
		
	}

}
