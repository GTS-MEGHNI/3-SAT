import java.util.ArrayList;

public class GeneticState {
	
	private ArrayList<Integer> state;
	private int fitnessValue;
	
	public GeneticState() {
		this.state = new ArrayList<Integer>();
		this.fitnessValue = 0;
	}
	
	public GeneticState(ArrayList<Integer> state, int fitnessValue) {
		this.state = new ArrayList<Integer>(state);
		this.fitnessValue = fitnessValue;
	}
	
	public ArrayList<Integer> getState() {
		return state;
	}

	public void setState(ArrayList<Integer> state) {
		this.state = state;
	}
	
	public int getfitnessValue() {
		return fitnessValue;
	}

	public void setfitnessValue(int fitnessValue) {
		this.fitnessValue = fitnessValue;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

}
