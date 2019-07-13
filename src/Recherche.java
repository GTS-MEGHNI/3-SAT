import java.util.ArrayList;
import java.util.Random;

import javafx.scene.control.Alert;

public class Recherche {

	protected Integer[][] DataClause;
	protected int NbrClauses;
	protected int NbrVariables;

	protected String solution = null;

	public Recherche(Integer[][] dataClause, int NbrClauses, int NbrVariables) {

		/// Reference
		this.DataClause = dataClause;
		///
		this.NbrClauses = NbrClauses;
		this.NbrVariables = NbrVariables;
	}

	// simplify alerts
	public static void ExitAlert(String title, String message, boolean exit) {
		Alert alert = new Alert(Alert.AlertType.ERROR);
		alert.setTitle(title);
		alert.setHeaderText(message);
		alert.showAndWait();
		if(exit) System.exit(0);
	}
	
	public static void InformAlert( String title, String message,boolean wait) {
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle(title);
		alert.setHeaderText(message);
		if(wait) alert.showAndWait();
		else alert.show();
		
		alert.close();
	}

	public boolean Isleaf(ArrayList<Integer> state) {
		if (!state.contains(-1))
			return true;
		else
			return false;
	}
	public boolean oldIsSolution(ArrayList<Integer> state) {
	//	System.out.println(NbrClauses+"==================================> "+this.NbrClauses);
		int literalNotTrue = 0;

		for (int i = 0; i < this.NbrClauses; i++) {
			literalNotTrue = 0;
			for (int j = 0; j < this.NbrVariables; j++) {
				if (DataClause[i][j] != -1) {
					if (DataClause[i][j] != state.get(j)) {
						literalNotTrue++;
					} else {
						break;
					}
				}
			}
			// we return false if one clause if not sat
			if (literalNotTrue == 3) {
				return false;
			}
		}
		return true;
	}
	public boolean IsSolution(ArrayList<Integer> state) {
		
		ArrayList<Boolean> checkClause = new ArrayList<>();
		for (int i = 0; i < this.NbrClauses; i++) {
			checkClause.clear();
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
			//This clause doesn't conatain a true value, we immediatly return a false statement
			if(!checkClause.contains(true))
				return false;
		}
		return true;
	}

	public int oldClauseCount(ArrayList<Integer> state) { // made it count only the non sat

		int literalNotTrue = 0;
		int counter = 0;

		for (int i = 0; i < this.NbrClauses; i++) {
			literalNotTrue = 0;
			for (int j = 0; j < this.NbrVariables; j++) {
				if (DataClause[i][j] != -1) {
					if (DataClause[i][j] != state.get(j)) {
						literalNotTrue++;
					} else {
						break;
					} // we don't verify the rest since
				}
			}
			if (literalNotTrue == 3) {
				counter++; // the same, but better
			}
		}
		return counter;
	}
	
	
	public int ClauseCount(ArrayList<Integer> state) { // made it count only the non sat
		// ---   WARNING: This only counts the NON sat clauses !!!   ---
		int counter = 0;
		ArrayList<Boolean> checkClause = new ArrayList<>();
		for (int i = 0; i < this.NbrClauses; i++) {
			checkClause.clear();
			for (int j = 0; j < this.NbrVariables; j++) {
				if (this.DataClause[i][j] != -1) {
					
					if(DataClause[i][j] == 1) {
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
			
			//we return number of false clauses
			if(!checkClause.contains(true))
				counter++;
		}
		return counter;
		
	}
	public ArrayList<ArrayList<Integer>> getSons(ArrayList<Integer> state) {

		ArrayList<Integer> index = new ArrayList<Integer>();
		ArrayList<Integer> son = new ArrayList<Integer>();
		ArrayList<ArrayList<Integer>> Sons = new ArrayList<>();

		/// We musn't generate a literal that already been valuated

		for (int j = 0; j < NbrVariables; j++) {

			/// We evaluate literal that hasn't been evaluated yet
			if (state.get(j) == -1)
				index.add(j);
		}

		Random literal = new Random();
		int rand = 0;

		// Choosing randomly a literal and change his state to 0 or 1
		if (index.size() == 1) {
			rand = 0;
		} else {
			rand = literal.nextInt(index.size());
		}
		///

		// Filling new state
		for (int i = 0; i < state.size(); i++) {
			son.add(state.get(i));
		}

		/// Giving a 0 value to the literal chosen
		son.set(index.get(rand), 0);

		/// Adding this new son to the Sons's list
		Sons.add(new ArrayList<>(son));

		/// Giving a 0 value to the literal chosen
		son.set(index.get(rand), 1);

		/// Adding this new son to the Sons's list
		Sons.add(new ArrayList<>(son));

		/// Updating random genrator list
		index.remove(index.get(rand));

		return Sons;
	}
	// converting a state to a series of nums
	
	public String ArrayToBinary(ArrayList<Integer> stat){
		String temp = new String();
		for (Integer integer : stat) {
			temp = temp+integer;
		}
		return temp;
	}
	
	public int getTheX_halfed(ArrayList<Integer> stat) {
		String temp = new String();
		temp = ArrayToBinary(stat);
		temp = temp.substring(0,9);
		return Integer.parseUnsignedInt(temp, 2);
	}
	
	public int getTheY_halfed(ArrayList<Integer> stat) {
		String temp = new String();
		temp = ArrayToBinary(stat);
		temp = temp.substring(10);
		return Integer.parseUnsignedInt(temp, 2);
	}
	
	public int getTheX(ArrayList<Integer> stat) {
	/*	String temp = new String();
		temp = ArrayToBinary(stat);
		int res = (int) (Integer.parseUnsignedInt(temp, 2) % 1024);
		return res;*/
		return 0;
	}

	public int getTheY(ArrayList<Integer> stat) {
		/*String temp = new String();
		temp = ArrayToBinary(stat);
		int res = (int) (Integer.parseUnsignedInt(temp, 2) / 1024);*/
		return 0;
	}

	/// G E T T E R S __ S E T T E R S
	public Integer[][] getDataClause() {
		return DataClause;
	}

	public void setDataClause(Integer[][] dataClause) {
		DataClause = dataClause;
	}

	public int getNbrClauses() {
		return NbrClauses;
	}

	public void setNbrClauses(int nbrClauses) {
		NbrClauses = nbrClauses;
	}

	public int getNbrVariables() {
		return NbrVariables;
	}

	public void setNbrVariables(int nbrVariables) {
		NbrVariables = nbrVariables;
	}

	public String getSolution() {
		return solution;
	}

	public void setSolution(String solution) {
		this.solution = solution;
	}
	/// end.

}
