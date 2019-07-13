import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;

import com.jfoenix.controls.JFXTextArea;

import javafx.application.Platform;

public class GeneticResearch extends Recherche {
	
	public int getNB_POP() {
		return NB_POP;
	}

	public void setNB_POP(int nB_POP) {
		NB_POP = nB_POP;
	}

	public int getMAX_ITR() {
		return MAX_ITR;
	}

	public void setMAX_ITR(int mAX_ITR) {
		MAX_ITR = mAX_ITR;
	}

	public int getCROSS_RATE() {
		return CROSS_RATE;
	}

	public void setCROSS_RATE(int cROSS_RATE) {
		CROSS_RATE = cROSS_RATE;
	}

	public int getMUTATION_RATE() {
		return MUTATION_RATE;
	}

	public void setMUTATION_RATE(int mUTATION_RATE) {
		MUTATION_RATE = mUTATION_RATE;
	}

	private int NB_POP;
	private int MAX_ITR;
	private int CROSS_RATE ; //80
	private int MUTATION_RATE; //40
	private int FATHER_DIVIDER =  NbrVariables/2;
	private int MOTHER_DIVIDER = NbrVariables-FATHER_DIVIDER;   // Mother gets one more if Odd


	private int STAGNATION_LIMITER = 3;
	
	GeneticState father = null;
	GeneticState mother = null;
	ArrayList<GeneticState> Population = null;
	ArrayList<GeneticState> Initial_Population = null;
	
	int i = 1;
	
	public GeneticResearch(Integer[][] dataClause, int NbrClauses, int NbrVariables) {
		super(dataClause, NbrClauses, NbrVariables);
	}
	
	//------------------------------------------>M A I N __ F U N C T I O N<--------------------------------------------------
	public ArrayList<GeneticResultPerIteration> startSearch(JFXTextArea areaLog) {
		
		ArrayList<GeneticResultPerIteration> list = new ArrayList<>();
		
		
		//Engendrer al�atoirement une population initiale de solutions
		this.Initial_Population = this.CreatePop();
		
		//sort Initial Population
		Collections.sort(this.Initial_Population,new Comparator<GeneticState>() {
			public int compare(GeneticState o1, GeneticState o2) {
				return Integer.valueOf(o2.getfitnessValue()).compareTo(o1.getfitnessValue());
			}		
		});

		this.Population = new ArrayList<>(this.Initial_Population);
					
		//Calculer f(s) pour chaque solution de la population courante
		this.calculate_F(Population);
		
		//Calculer f(s) pour chaque solution de la population initiale
		this.calculate_F(this.Initial_Population);
		
		//V�rifier si la solution optimal existe dans cette population
		ArrayList<Integer> state = this.foundASolution(Population);
		
		if(state != null) {
		} else {
			father = new GeneticState();
			mother = new GeneticState();
			int Rc = 0;
			int Rm = 0;
			int Cpt = 0;
						
			long start = System.currentTimeMillis();
			
			GeneticResultPerIteration resultPerIteration = new GeneticResultPerIteration();
			resultPerIteration.setFitnessValue(0);
			resultPerIteration.setIteration(0);
			resultPerIteration.setSolution("");
			resultPerIteration.setTime(0);
			list.add(resultPerIteration);
		
			for(i = 1; i <= this.MAX_ITR; i++) {
											
				ArrayList<GeneticState> newSolutions = new ArrayList<>();
								
				for(int j = 1; j <= (this.CROSS_RATE * this.NB_POP) / 100; j++) {
					
					//S�lectionner 2 individus ou solutions avec remise
					this.getParents(Population);
															
					newSolutions.addAll(this.Cross(father, mother));
					
					state = this.EvaluerFitSons(newSolutions);
					
					//V�rifier si solution toruv�e dans l'un des fils

				}
								
				for(int j = 1; j <= (this.MUTATION_RATE * this.NB_POP) / 100; j++) {
					this.mutation();
				}
						
				//V�rification de la stagnation (no cross and no mutation)
				if(this.Initial_Population.equals(this.Population)) {	
					Cpt++;
					if(Cpt >= this.STAGNATION_LIMITER - 1) {
						this.fixStagnation();	
						state = this.UpdateAfterStagnation();
						/*if(state != null) {
							return;
						}*/
						Cpt = 0;			
					}				
				}	
				resultPerIteration = new GeneticResultPerIteration();
				resultPerIteration.setFitnessValue((Population.get(0).getfitnessValue() * 100) / NbrClauses);
				resultPerIteration.setIteration(i);
				resultPerIteration.setSolution(Population.get(0).getState().toString());
				resultPerIteration.setTime((System.currentTimeMillis() - start) / 1000);
				resultPerIteration.setNbrClause(Population.get(0).getfitnessValue());
				if(!this.IsRecurrent(resultPerIteration.getTime(), list))
					list.add(resultPerIteration);
				else {
					this.updateFit(resultPerIteration,resultPerIteration.getTime(),list);
				}
		
					Platform.runLater(() -> {
						areaLog.appendText("Fitness(%) ==>"+ (Population.get(0).getfitnessValue() * 100) / this.NbrClauses + "% || Fitness(value) : "+ Population.get(0).getfitnessValue() +" Iteration : "+i + 
							" Time : "+Float.toString((System.currentTimeMillis() - start) / 1000)+" s\n");
					});
					
			}
		}
		//Trier la population par ordre Croissant
		Collections.sort(list,new Comparator<GeneticResultPerIteration>() {
			public int compare(GeneticResultPerIteration o1, GeneticResultPerIteration o2) {
				return Integer.valueOf(o1.getFitnessValue()).compareTo(o2.getFitnessValue());
			}		
		});
		
		return list;
	}
	//-----------------------------------------------------------------------------------------------------------------------

	public ArrayList<GeneticState> CreatePop() {
		
		ArrayList<GeneticState> Pop = new ArrayList<>();
		GeneticState current = new GeneticState();

		for(int i = 1; i <= this.NB_POP; i++) {
			current = new GeneticState();
			for (int j = 1; j <= NbrVariables; j++) {	
				current.getState().add(new Random().nextInt(2));	
			}
			Pop.add(current);
		}
		
		return Pop;
	}
	
	public void calculate_F(ArrayList<GeneticState> Pop) {
		
		for(GeneticState state : Pop) {
			state.setfitnessValue(this.getFitnessValue(state.getState()));
		}	
		
		//Trier la population par ordre D�croissant
		Collections.sort(Pop,new Comparator<GeneticState>() {
			public int compare(GeneticState o1, GeneticState o2) {
				return Integer.valueOf(o2.getfitnessValue()).compareTo(o1.getfitnessValue());
			}		
		});
			
	}
	
	public int getFitnessValue(ArrayList<Integer> state) {
		// TODO: I really don't know why you absolloutely LOVE to rewrite existing code!!!
		//		 This is not healthy at all !!!
		// the same functions is called ClauseCount, and it is in the class Recherche
		
		
		ArrayList<Boolean> checkClause = new ArrayList<>();
		int Nbr_Sat = 0;
		
		for (int i = 0; i < this.NbrClauses; i++) {
			checkClause = new ArrayList<>();
			for (int j = 0; j < this.NbrVariables; j++) {
				if (this.DataClause[i][j] != -1) {
					
					if(this.DataClause[i][j] == 1) {
						//the literal doesn't have a neg sign
						if(state.get(j) == 1) {
							//the literal is evaluated to true
							checkClause.add(true);
						} else {
							//the literal is evaluated to false
							checkClause.add(false);
						}
					} else {
						//the literal have a neg sign
						if(state.get(j) == 1) {
							//the literal is evaluated to false
							checkClause.add(false);
						} else {
							//the literal is evaluated to true
							checkClause.add(true);
						}
					}
				}
			}
			if(checkClause.contains(true)) //this clause is satisfiable 
				Nbr_Sat++;		
		}
		//return true;
		return Nbr_Sat++;
		//TODO: normalement without ++
	}
	
	public ArrayList<Integer> foundASolution(ArrayList<GeneticState> Population) {
		
		for(GeneticState state : Population) {
			if(state.getfitnessValue() == this.NbrClauses)
				return state.getState();
		}
		return null;
	}
	
	public void getParents(ArrayList<GeneticState> population) {
		
		ArrayList<Integer> index = new ArrayList<Integer>();
		for(int i = 0; i < population.size(); i++) {
			index.add(i);
		}
		Random literal = new Random();
		int rand = literal.nextInt(index.size());
		
		father = new GeneticState(population.get(index.get(rand)).getState(),population.get(rand).getfitnessValue());
	
		index.remove(index.get(rand));
		
		rand = literal.nextInt(index.size());
		
		mother = new GeneticState(population.get(index.get(rand)).getState(),population.get(rand).getfitnessValue());	
	}
	
	public ArrayList<GeneticState> Cross(GeneticState father,GeneticState mother) {
		
		ArrayList<Integer> state = new ArrayList<>();
		ArrayList<GeneticState> Sons = new ArrayList<>();
		
		//first son
		for(int i = 0; i < this.FATHER_DIVIDER; i++) 
			state.add(father.getState().get(i));
		for(int i = this.FATHER_DIVIDER; i < this.NbrVariables; i++)
			state.add(mother.getState().get(i));
		///
		Sons.add(new GeneticState(state,0));
		
		//second son
		state = new ArrayList<>();
		for(int i = 0; i < this.MOTHER_DIVIDER; i++)
			state.add(mother.getState().get(i));
		for(int i = this.MOTHER_DIVIDER; i < this.NbrVariables; i++)
			state.add(father.getState().get(i));
		///
		Sons.add(new GeneticState(state,0));

		return Sons;
	}
	
	public ArrayList<Integer> EvaluerFitSons(ArrayList<GeneticState> newSolutions) {
		
		int fitValue = 0;
		ArrayList<GeneticState> toAdd = new ArrayList<>();
	
		for(GeneticState state : newSolutions) {
			
			fitValue = getFitnessValue(state.getState());
			
			if(fitValue == this.NbrClauses)
				return state.getState();
			
			if(fitValue > this.Population.get(Population.size() - 1).getfitnessValue()) {
				this.Population.remove(Population.size() - 1);	
				toAdd.add(new GeneticState(state.getState(),fitValue));
			}
		}
		
		//Ajouter les �l�ments � ajouter
		this.Population.addAll(toAdd);
		
		//Trier la population par ordre D�croissant
		Collections.sort(this.Population,new Comparator<GeneticState>() {
			public int compare(GeneticState o1, GeneticState o2) {
				return Integer.valueOf(o2.getfitnessValue()).compareTo(o1.getfitnessValue());
			}		
		});
	
		return null;
	}
	
	public void mutation() {
		
		//Choisir un individu al�atoire pour la mutation
		int randSol = new Random().nextInt(this.Population.size());
		
		GeneticState state = this.Population.get(randSol);
				
		//Choisir un g�ne al�atoire
		int randGene = new Random().nextInt(this.NbrVariables);
		
		//Alt�rer valeur de g�ne
		int val = state.getState().get(randGene) == 0 ? 1 : 0;
		
		this.Population.get(randSol).getState().set(randGene, val);
		
	}
	
	public void fixStagnation() {
		
		
		//Prendre un nombre al�atoire de solution et inverser leur contenu
		ArrayList<Integer> state,index = new ArrayList<Integer>();
		for(int i = 0; i < this.Population.size(); i++) {
			index.add(i);
		}
		int Nbr_Sol = new Random().nextInt(this.NB_POP) + 1;
		int sol = 0;
			
		for(int i = 1; i <= Nbr_Sol; i++) {
			// TODO: has to get better (create a Set)

			sol = new Random().nextInt(index.size());
			
			state = new ArrayList<>(this.Population.get(index.get(sol)).getState());
						
			for(int j = 0; j < state.size(); j++) 
				state.set(j, (state.get(j) == 1) ? 0 : 1);

			this.Population.get(index.get(sol)).setState(state);	
		}
	}
	
	
	public ArrayList<Integer> UpdateAfterStagnation() {
		
		//Trier la population par ordre D�croissant
		Collections.sort(this.Population,new Comparator<GeneticState>() {
			public int compare(GeneticState o1, GeneticState o2) {
				return Integer.valueOf(o2.getfitnessValue()).compareTo(o1.getfitnessValue());
			}		
		});
		
		if(this.Population.get(0).getfitnessValue() == this.NbrClauses)
			return this.Population.get(0).getState();	
		
		return null;
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
			
		
		
			
	
	
	
	
	
	
	
	
	
	
	
}
