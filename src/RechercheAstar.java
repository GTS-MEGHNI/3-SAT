import java.util.ArrayList;
import java.util.Comparator;
import java.util.Optional;
import java.util.Random;


public class RechercheAstar extends Recherche {

	public RechercheAstar(Integer[][] dataClause, int NbrClauses, int NbrVariables) {
		super(dataClause, NbrClauses, NbrVariables);
	}
	
	
	public ArrayList<Integer> startAstarSearch(){
		return startAstarSearch(false);
	}
	public ArrayList<Integer> startAstarSearch(boolean showGame) { // A*
		long startTime = System.currentTimeMillis();
		ArrayList<AstarState> Open = new ArrayList<>();
		ArrayList<AstarState> Closed = new ArrayList<>();
		ArrayList<Integer> current = new ArrayList<>();
		AstarState State = new AstarState();
		for (int i = 0; i < NbrVariables; i++) {	// generating the father
			current.add(new Random().nextInt(2));	// he is a random dude!
		}

		State = new AstarState(current, ClauseCount(current), 0); // G is 0
		int TheX  = getTheX(State.getState());
		int TheY  = getTheY(State.getState());
		int maxFPS = 60;
		Game game = new Game("\"SearchSpace\"", 1024,1024,TheX,TheY,maxFPS); 
		if(showGame)game.start();
		
		int startH = State.getHeuristicValue(); // for the progress bar
		
		Open.add(State);							// the first one in!
		while (!Open.isEmpty()) { // gotta test 'em all
			if (IsSolution(State.getState())) { // if you get here, congrats!!
				// ExitAlert last parameter set to true to wait 
				game.setSolX(getTheX(State.getState()));
				game.setSolY(getTheY(State.getState()));
				return State.getState();
			} else {
				/*System.out.println(" " + Open.get(0).getState().toString() + "h="
						+ Open.get(0).getHeuristicValue()+" + g="+Open.get(0).getG());*/
				Closed.add(State);  // I mean it is pretty obvious
				Open.remove(0);		// here we rm the first, after the sort the new State is index0
				generateSons(State, Open, Closed); // unique children! HA! go to Open
				Open.sort(CompareHeuristic);					// here we only chose the best
				State = Open.get(0); // I don't really know how to handle pointers, but hey
				game.setSearchX(getTheX(State.getState()));
				game.setSearchY(getTheY(State.getState()));
				if(startH!=0) game.setProg((startH-State.getHeuristicValue())*1024/startH);
				game.setProgress(""+State.getHeuristicValue() +" left from "+this.NbrClauses +"clauses");
		//		State.setState((Open.get(0).getState()));		// HERE, only number 1, I mean 0...
		//		State.setHeuristicValue(Open.get(0).getHeuristicValue());	// the new guy
			}
		}
		return null;
	}

	public void generateSons(AstarState State,ArrayList<AstarState> Open,ArrayList<AstarState> Closed) {

		ArrayList<Integer> stat 	= new ArrayList<>();
		AstarState New 				= new AstarState();
		Integer val = 0;
		int index 	= 0;
		
		for (Integer i : State.getState()) {
			stat.addAll(State.getState());
			val = (boolean) (!(i == 1)) ? 1 : 0; // to reverse a boolean value
			stat.set(index++, val);
			New = new AstarState(stat, ClauseCount(stat), State.getG()+1);
			// to calculate the f= g+h; 
			if (!StateIsThere(Closed, New.getState())) {
				if (!StateIsThere(Open, New.getState())) {
					Open.add(New);

					 
				}else {New.setHeuristicValue(State.getG());}
			}
			stat.clear();
		}

		/* I changed Sons to minimize time, so the verification is done 
		 * we don't have an array of Sons
		 *  they are verified then added to open
		 *  don't know if it is the best though
		 * */
		

	}
	 
	/* the Method Contains gives a very flawed result
	 * That is why, I made this, also, Absolutely no repetition
	 * I spent the whole day, WHOLE because of this
	 * If only is did not exist, I would've just made this
	 * */
	public boolean StateIsThere(ArrayList<AstarState> StateList,ArrayList<Integer> State) {
		for (AstarState s : StateList) {
			if(s.getState().equals(State)) return true;
		}
		return false;
	}
	

	public static Comparator<AstarState> CompareHeuristic = new Comparator<AstarState>() {
		public int compare(AstarState A1, AstarState A2) {

	//		int As1 = A1.getHeuristicValue()+A1.getG();
	//		int As2 = A2.getHeuristicValue()+A2.getG();
			int As1 = A1.getHeuristicValue();
			int As2 = A2.getHeuristicValue();
			// ascending order
			if (As1 > As2) 		return 1 ;
			else if (As1 < As2) return -1;
			else 				return 0 ;
		}
	};

	
	public ArrayList<ArrayList<Integer>> startAstarSearch_with_sol_sum() { // A*
		boolean first		= true;
		long 	limit		= 500;
		long 	finishTime	= 0;
		long 	controlTime	= 0;
		long 	startTime 	= System.currentTimeMillis();
		ArrayList<ArrayList<Integer>> 	Solution 	= new ArrayList<>();
		ArrayList<AstarState> Open 		= new ArrayList<>();
		ArrayList<AstarState> Closed	= new ArrayList<>();
		ArrayList<Integer> 	 current 	= new ArrayList<>();
		AstarState State 				= null;
		String show 					= new String();
		
		for (int i = 0; i < NbrVariables; i++) {	// generating the father
			current.add(new Random().nextInt(2));	// he is a random dude!
		}

		State = new AstarState(current, ClauseCount(current),0);
		Open.add(State);							// the first one in!
		
		while (!Open.isEmpty()&&(controlTime<limit)) { // gotta test 'em all

			if (IsSolution(State.getState())) {		// if you get here, congrats!!
				// all this to control time
				finishTime = System.currentTimeMillis() - startTime;
				if(0.5*limit<finishTime) limit+=(2*limit+2*finishTime);
				first =false; // end of all this
				System.out.println(State.getState() +" Time: "+ finishTime);
				Solution.add(State.getState());
				Closed.add(State);
				Open.remove(0);
				generateSons(State, Open, Closed); // in case it is not fully developped
				Open.sort(CompareHeuristic);					// here we only chose the best
				if(Open.isEmpty()) break;
				State = Open.get(0);
			} else {
				Closed.add(State);  // I mean it is pretty obvious
				Open.remove(0);		// here we rm the first, after the sort the new State is index0
				generateSons(State, Open, Closed); // unique children! HA! go to Open
				Open.sort(CompareHeuristic);					// here we only chose the best
				if(Open.isEmpty()) break;
				State = Open.get(0); // I don't really know how to handle pointers, but hey
			}
			
			if(!first) controlTime = System.currentTimeMillis()- startTime;
		}

		//System.out.println("Open: "+Open.size() + " Closed: " + Closed.size()+". "+Solution.size());
		return Solution;

	}

	
	
}
