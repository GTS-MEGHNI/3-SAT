import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;

import com.jfoenix.controls.JFXTextArea;

// TODO: In this class we will define the methods to accomplish the algorithm on the particles
/*
 *  for each particle i = 1, ..., S do
   Initialize the particle's position with a uniformly distributed random vector: xi ~ U(blo, bup)
   Initialize the particle's best known position to its initial position: pi â†� xi
   if f(pi) < f(g) then
       update the swarm's best known  position: g â†� pi
   Initialize the particle's velocity: vi ~ U(-|bup-blo|, |bup-blo|)
while a termination criterion is not met do:
   for each particle i = 1, ..., S do
      for each dimension d = 1, ..., n do
         Pick random numbers: rp, rg ~ U(0,1)
         Update the particle's velocity: vi,d â†� Ï‰ vi,d + Ï†p rp (pi,d-xi,d) + Ï†g rg (gd-xi,d)
      Update the particle's position: xi â†� xi + vi
      if f(xi) < f(pi) then
         Update the particle's best known position: pi â†� xi
         if f(pi) < f(g) then
            Update the swarm's best known position: g â†� pi
            				//TODO:
				// when swarm is large, small iterations seem irrelevant, best is obtained after sw= 2*iter at max
				// and it does not get better for small swarms
				// new tests should include more iterations and large swarms
				// others should be the parameters, even though current seem the best
				// the survival boost seems effective to get that extra step
				// 
				
 * */

// TODO: we have to finish this today
public class Swarm extends Recherche{
	

	private int swarmSize; //200
	private int maxIter; //100
	int maxVelocity = NbrVariables;  // TODO: just for testing !! this should be set for each particle
	  								 // they should add up to 100 for the total
	double r1,r2;					 // if not then, the remaining will have bits reversed
									 // they are not used now, we use c1, c2 (0,2)
	private double w; //0.9
	private double c1;//2.0;
	private double c2; //2.0;
	
	ArrayList<Integer> globalBest = null;
	ArrayList<Particle> swarm = null;
	ArrayList<GeneticResultPerIteration> list = new ArrayList<>();

	
	public Swarm(Integer[][] dataClause, int NbrClauses, int NbrVariables) {
		super(dataClause, NbrClauses, NbrVariables);
		// TODO Auto-generated constructor stub
	}
	
	public ArrayList<GeneticResultPerIteration> launch(JFXTextArea arealog) {
		ArrayList<Double> timePerEpoch = new ArrayList<Double>();
		ArrayList<Integer> fitnessPerEpoch = new ArrayList<Integer>();
		
		double t1 = System.currentTimeMillis();
		double t2 = System.currentTimeMillis();
		
		// this is to a list of time stamps after X iteration
		
		this.initSwarm();
		
		GeneticResultPerIteration resultPerIteration = new GeneticResultPerIteration();
		resultPerIteration.setFitnessValue(0);
		resultPerIteration.setIteration(0);
		resultPerIteration.setSolution("");
		resultPerIteration.setTime(0);
		list.add(resultPerIteration);
		long start = System.currentTimeMillis();

		for (int i = 1; i <= maxIter; i++) {
			
			for (Particle particle : this.swarm) {
				
				if (getFitnessValue(particle) == this.NbrClauses) {
					
					// """ This is to include the very last value for the graph
					t2 = System.currentTimeMillis();
					timePerEpoch.add(t2-t1);	
					fitnessPerEpoch.add(this.NbrClauses- ClauseCount(this.globalBest));
					arealog.appendText("Optimal solution found : "+particle.getPosition()+"\n");
					resultPerIteration.setFitnessValue(100);
					resultPerIteration.setIteration(1);
					resultPerIteration.setSolution(particle.getPosition().toString());
					resultPerIteration.setTime(System.currentTimeMillis() - start);
					list.add(resultPerIteration);
				
					return list;
				}
				 r1 =((double) new Random().nextInt(1000))/1000;
				 r2 =((double) new Random().nextInt(1000))/1000;

				//particle.randomizeVelocity(maxVelocity);
				particle.updateVelocity(this.NbrVariables, this.globalBest,c1,c2,r1,r2);
				particle.MoveParticle(this.NbrVariables, this.globalBest, r1,r2);
				// TODO: Update change velocity for each particle
				// TODO: change position for each particle
				// TODO: Update PersonalBest
				particle = updatePBest(particle);

			}
			setGlobalBest();
		// TODO: This controls how much data you want 	
			int percent  = 0;
			int score = 0;
				t2 = System.currentTimeMillis();
				timePerEpoch.add(t2-t1);	
				fitnessPerEpoch.add(this.NbrClauses- ClauseCount(this.globalBest));
				resultPerIteration = new GeneticResultPerIteration();
				resultPerIteration.setFitnessValue(percent = (int) (100*((double)(this.NbrClauses- ClauseCount(this.globalBest))/this.NbrClauses)));
				resultPerIteration.setIteration(i);
				resultPerIteration.setSolution(this.globalBest.toString());
				resultPerIteration.setTime((long) (t2 - t1));
				if(!this.IsRecurrent(resultPerIteration.getTime(), list))
					list.add(resultPerIteration);
				else {
					this.updateFit(resultPerIteration,resultPerIteration.getTime(),list);
				}
				
				arealog.appendText("Fitness(%) ==> "+percent+ "% || Fitness(value) : "+ (this.NbrClauses- ClauseCount(this.globalBest)) +" Iteration : "+ i 
						+ "Time : "+Float.toString((System.currentTimeMillis() - start) / 1000)+" sec \n");
		}

		Collections.sort(list,new Comparator<GeneticResultPerIteration>() {
			public int compare(GeneticResultPerIteration o1, GeneticResultPerIteration o2) {
				return Integer.valueOf(o1.getFitnessValue()).compareTo(o2.getFitnessValue());
			}		
		});
		
		return list;
	}
	

	
	public void initSwarm() {
		//this will be the first one (as I don't want gBest to not exist)
		Particle first = new Particle();
		first.initParticle(this.NbrVariables, maxVelocity);
		this.swarm = new ArrayList<Particle>();
		this.swarm.add(first);
		this.globalBest = new ArrayList<Integer>(first.getPosition());
		
		for (int i = 0; i < this.swarmSize-1; i++) {
			Particle particle = new Particle();
			particle.initParticle(this.NbrVariables, maxVelocity);
			this.swarm.add(particle);	
		}

		setGlobalBest();	
		// TODO: settings seem good

	}
	

	public void setPBestforAll() {
		// TODO: Depreciated, Use updatePBest() instead (faster by running on the same loop)
		// this is a workaround to set personal best
		// Remember, Clause count is reversed ==> 0 means all clauses are SAT
		for (Particle particle : this.swarm) {
			
			if (ClauseCount(particle.getPersonalBest()) >
				ClauseCount((particle.getPosition())) ) {

				particle.setPersonalBest(particle.getPosition());
			}

		}
	}
	
	

	public Particle updatePBest(Particle particle) {
		// gets and returns to the same Particle because it uses external methods
		if (ClauseCount(particle.getPersonalBest()) >
		ClauseCount((particle.getPosition())) ) {
		particle.setPersonalBest(particle.getPosition());
		}
		
		return particle;
	}
	
	
	public void smartVelocity(Particle particle) {
		
		if (ClauseCount(particle.getPersonalBest()) >
		ClauseCount((particle.getPosition())) ) {
		particle.setPersonalBest(particle.getPosition());
		}
	}
	
	
	public void setGlobalBest() {
		for (Particle current : swarm) {
			
			if (ClauseCount(this.globalBest)	>
				ClauseCount((current.getPersonalBest()))) {
				this.globalBest = new ArrayList<Integer>(current.getPersonalBest());
			}	
		}
	}

	public int getFitnessValue(Particle particle) {
		
		return NbrClauses - ClauseCount(particle.getPosition());
	}

	
	public boolean IsRecurrent(long time, ArrayList<GeneticResultPerIteration> list) {
		// TODO: What does it do ?

		for(GeneticResultPerIteration geneticResultPerIteration: list)
			if(geneticResultPerIteration.getTime() == time) return true;		
		return false;
	}
	
	public void updateFit(GeneticResultPerIteration item, long time, ArrayList<GeneticResultPerIteration> list) {
		for(int j = 0; j < list.size(); j++) 
			if(list.get(j).getTime() == time && time != 0) 
				list.set(j, item);
	}
	
	public int getSwarmSize() {
		return swarmSize;
	}
	public void setSwarmSize(int swarmSize) {
		this.swarmSize = swarmSize;
	}
	public int getMaxIter() {
		return maxIter;
	}
	public void setMaxIter(int maxIter) {
		this.maxIter = maxIter;
	}
	public double getW() {
		return w;
	}
	public void setW(double w) {
		this.w = w;
	}
	public double getC1() {
		return c1;
	}
	public void setC1(double c1) {
		this.c1 = c1;
	}
	public double getC2() {
		return c2;
	}
	public void setC2(double c2) {
		this.c2 = c2;
	}

	
	
}
