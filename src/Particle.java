import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class Particle {

	private double velocity = 0;
	private ArrayList<Integer> personalBest; 
	// should be a known best position not a value (should be Array)
	private ArrayList<Integer> position ;
	private int inertia  = 0; 
	// TODO: is inertia the direction and force of movement ?
	
	
	public Particle() {
		// TODO: delete this, it exists just to avoid errors
	};
	
	public Particle(double velocity, ArrayList<Integer> position) {

		this.velocity = velocity;
		this.position = position;

	}



	
	
	public void initPosition(int NbrVariables) {
		this.position = new ArrayList<Integer>();
		
		for (int i = 0; i < NbrVariables; i++) {
			int rand = new Random().nextInt(2);
			this.position.add(rand);
		}
		
	}
	
	
	public void initParticle(int NbrVariables,int velocity) {
		
		this.initPosition(NbrVariables);		
		this.personalBest  = new ArrayList<Integer> (this.getPosition());
		this.randomizeVelocity(1, velocity);
		
	}
	
	
	
	public ArrayList<Integer> randomSet(double size, int nbVars) {
		// size is how much you want the set to contain
		
		ArrayList<Integer> list = new ArrayList<Integer>();
		ArrayList<Integer> newSet =  new ArrayList<Integer>();
		int chosen = 0; // is random from a set
		
		for (int i = 0; i < nbVars; i++) {
			list.add(i);
		}	// this loop just adds all num of vars
		
		for (int i = 0; (list.size()>0)&&(i < size); i++) {
			if(size>74)System.out.println("size: "+size);
			chosen = new Random().nextInt(list.size());
			newSet.add(list.get(chosen));
			list.remove(chosen);			
		}	// adds to the new set
		
		return newSet;
	}


	public void MoveParticle(Integer nbVars, ArrayList<Integer> gBest, double r1, double r2) {
		
		ArrayList<Integer> indexesToChange = new ArrayList<Integer>();
		ArrayList<Integer> fromGbest = new ArrayList<Integer>();
		ArrayList<Integer> fromPbest = new ArrayList<Integer>();
		// randomSet is like Random Indexes, just to not re-get the same
		
		// TODO: The velocity should be devided to 3, 1- for new, 2- from gBest, 3- from pBest
		
		
		// TODO: here the velocity is size of the set
		// we will distribute it to the two others
		// ratioP/=100; (this is now between 0-1)
		// ratioG/=100;
		
		
		double maxChange = this.velocity;
		double changeP = (int) (maxChange*r1);
		double changeG = (int) (maxChange*r2);
		double change1 = maxChange - changeP - changeG;
		
		indexesToChange = randomSet(change1, nbVars);
		fromPbest = indexesOfDifferences(this.personalBest,this.position);
		fromGbest = indexesOfDifferences(gBest,this.position);
		Collections.shuffle(fromPbest, new Random());
		Collections.shuffle(fromGbest, new Random());
		/*
		for (int i = 0; i < this.velocity*(ratioP/100); i++) {
			fromPbest.add(indexesToChange.get(0));
			indexesToChange.remove(0);
		}

		for (int i = 0; i < this.velocity*(ratioG/100); i++) {
			fromGbest.add(indexesToChange.get(0));
			indexesToChange.remove(0);
		}
		*/


		// these should chose random from gBest and pBest
		
		// TODO: or we chose from the indexes ( some get new some gBest some pBest)
		for (Integer index : indexesToChange) {
			Integer reverse = this.position.get(index)== 1 ? 0 : 1;
			this.position.set(index, reverse);
		}		
		
		for (Integer index : fromPbest) {
			this.position.set(index,this.personalBest.get(index));
			changeP--;
			if(changeP==0) break;
		}
		
		for (Integer index : fromGbest) {
			this.position.set(index, gBest.get(index));
			changeG--;
			if(changeG==0) break;
		}
		
		
		

	}	
	
	
	
	public void updateVelocity(double vMax,ArrayList<Integer> gBest, double c1, double c2, double r1, double r2) {
		// v( t+1 )= w*v( t )+c1 r1 *(Pbest-x(t))+ c2 r2 *(Gbest-x(t))
		double distP = distance(this.getPersonalBest(), this.getPosition());
		double distG = distance(gBest, this.getPosition());
				
		double v0 = this.getVelocity();
		double w = this.getInertia();
		double v1 =  w*v0 + c1*r1*(distP) + c2*r2*(distG);	

		// TODO: in case velocity goes higher than it should
		double maxIndex = gBest.size()-1;
		if (v1 > vMax) {
			vMax = v1;
			v1 = v1 * maxIndex / vMax;
		} else {
			// Particle gets a boost from survival instinct
			if (v1 < 1) {
				v1++;
			}
		}

		this.setVelocity(v1);
	}
	
	
	
	public int distance(ArrayList<Integer> first, ArrayList<Integer> second) {
		int diff = 0;
		for (int i = 0; i < first.size(); i++) {
			if(first.get(i) != second.get(i)) {
				diff++;
			}
		}
		return diff;
	}	
	public ArrayList<Integer> indexesOfDifferences(ArrayList<Integer> first, ArrayList<Integer> second) {
		ArrayList<Integer> diff = new ArrayList<Integer>();
		for (int i = 0; i < first.size(); i++) {
			if(first.get(i) != second.get(i)) {
				diff.add(i);
			}
		}
		return diff;
	}
	
	
	
	
	
	public void randomizeVelocity(int minV, int maxV) {
		// TODO: Since we don't want any to have a 0 velocity, we born the Random
		this.velocity = new Random().nextInt(maxV*100 - minV)/100 + minV;
	}
	
	
	
	public double getVelocity() {
		return velocity;
	}
	public void setVelocity(double velocity) {
		this.velocity = velocity;
	}
	
	public ArrayList<Integer> getPersonalBest() {
		return personalBest;
	}
	public void setPersonalBest(ArrayList<Integer> personalBest) {
		this.personalBest = personalBest;
	}
	public ArrayList<Integer> getPosition() {
		return this.position;
	}
	public void setPosition(ArrayList<Integer> position) {
		this.position = position;
	}

	public int getInertia() {
		return inertia;
	}

	public void setInertia(int inertia) {
		this.inertia = inertia;
	}
	
	
}
