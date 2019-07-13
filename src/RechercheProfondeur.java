import java.util.ArrayList;
import java.util.Random;

import com.jfoenix.controls.JFXSpinner;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.effect.Reflection;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class RechercheProfondeur extends Recherche{
	

	
	public RechercheProfondeur(Integer[][] dataClause, int NbrClauses,int NbrVariables) {
		super(dataClause,NbrClauses,NbrVariables);
	}
	
	

	public ArrayList<Integer> startDepthSearch() {

		long finishTime	= 0;
		long startTime 	= System.currentTimeMillis();
		ArrayList<Integer> state = new ArrayList<>();
		///Initialize first state 
		for(int i = 0; i < NbrVariables; i++) {
			state.add(-1);
		}
		
		///Create list open and close
		ArrayList<ArrayList<Integer>> Open = new ArrayList<>();
		
		///Add first state in open list
		Open.add(state);
		int rand = 0;
	
		ArrayList<ArrayList<Integer>> Sons = new ArrayList<>();

		while(!Open.isEmpty()) {	
			
			Sons.clear();
			
			///Take first element of Open
			state = new ArrayList<>(Open.get(0));
			
			///Remove it from Open
			Open.remove(0);
				
			///Develop the current state if not a leaf
			if(Isleaf(state)) {

				if(IsSolution(state)) {
					break;
				}
			} else {
				Sons = new ArrayList<>(getSons(state));
				
				ArbitraryDepthInsertion(Open, Sons);
			}
		}
				
		return state;
	}
		
	public void ArbitraryDepthInsertion(ArrayList<ArrayList<Integer>> dest, ArrayList<ArrayList<Integer>> source) {
		ArrayList<Integer> index = new ArrayList<Integer>();
		
		for(int i = 0; i < source.size(); i++) {
			index.add(i);
		}
		Random literal = new Random();
		int rand = 0;
		while(index.size() > 0) {
			
			if(index.size() == 1) { 
				rand = 0;
			} else {
				rand = literal.nextInt(index.size());
			}
			
			dest.add(0, source.get(index.get(rand)));
			index.remove(index.get(rand));
			
		}	
	}
	

	

	
	
	

}
