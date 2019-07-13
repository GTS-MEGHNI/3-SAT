import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import com.jfoenix.controls.JFXSpinner;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Reflection;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class RechercheLargeur extends Recherche {

	public RechercheLargeur(Integer[][] dataClause, int NbrClauses,int NbrVariables) {
		super(dataClause,NbrClauses,NbrVariables);
	}
	
	public ArrayList<Integer> startBreathSearch() {
		
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
					for(int i : state) {
						System.out.print(i+" ");
						solution += i+ " "; 
					}
					break;
				}
			} else {
				Sons = new ArrayList<>(getSons(state));
				
				ArbitraryBreathInsertion(Open, Sons);
			}
		}
		return state;
	}

		

	
	
	public void ArbitraryBreathInsertion(ArrayList<ArrayList<Integer>> dest, ArrayList<ArrayList<Integer>> source) {
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
			
			dest.add(source.get(index.get(rand)));
			index.remove(index.get(rand));
			
		}	
	}

}
