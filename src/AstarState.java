import java.util.ArrayList;

public class AstarState {

	private ArrayList<Integer> state;
	private int g;
	private int heuristicValue;

	public AstarState() {
		this.state = new ArrayList<Integer>();
		this.heuristicValue = -1;
	}

	public AstarState(ArrayList<Integer> state, int heuristicValue,int g) {
		
		this.state = new ArrayList<Integer>(state);
		this.heuristicValue = heuristicValue;
		this.g = g;
	}

	public ArrayList<Integer> getState() {
		return state;
	}

	public void setState(ArrayList<Integer> state) {
		this.state = state;
	}

	public int getHeuristicValue() {
		return heuristicValue;
	}

	public void setHeuristicValue(int heuristicValue) {
		this.heuristicValue = heuristicValue;
	}

	public int getG() {
		return g;
	}

	public void setG(int g) {
		this.g = g;
	}

}
