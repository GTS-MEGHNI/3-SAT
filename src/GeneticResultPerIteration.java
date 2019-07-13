import java.util.ArrayList;

public class GeneticResultPerIteration {
	
	private int fitnessValue;
	private int iteration;
	private long time;
	private int nbrClause;
	private String solution;
	
	
	public GeneticResultPerIteration() {
		this.fitnessValue = -1;
		this.iteration = -1;
		this.time = -1;
	}
	
	public int getFitnessValue() {
		return fitnessValue;
	}


	public void setFitnessValue(int fitnessValue) {
		this.fitnessValue = fitnessValue;
	}


	public int getIteration() {
		return iteration;
	}


	public void setIteration(int iteration) {
		this.iteration = iteration;
	}


	public long getTime() {
		return time;
	}


	public void setTime(long time) {
		this.time = time;
	}

	public String getSolution() {
		return this.solution;
	}

	public void setSolution(String solution) {
		this.solution = solution;
	}

	public int getNbrClause() {
		return nbrClause;
	}

	public void setNbrClause(int nbrClause) {
		this.nbrClause = nbrClause;
	}
	
}
