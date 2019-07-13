import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.jfoenix.controls.JFXTextArea;

public class FichierCnf extends File{

	private static final long serialVersionUID = 1L;
	
	Integer[][] DataClause;
	private int NbrClauses;
	private int NbrVariables;
	
	
	public FichierCnf(String chemin,int NbrClauses, int NbrVariables) {
		super(chemin);
		this.NbrClauses = NbrClauses;
		this.NbrVariables = NbrVariables;
	}
	
	
	
	public Integer[][] getDataClause() {
		return DataClause;
	}

	public void setDataClause(Integer[][] dataClause) {
		DataClause = dataClause;
	}
	
	
	
	public void Initialize() {
		DataClause = new Integer[NbrClauses][NbrVariables];
		for(int i = 0; i < NbrClauses; i++) {
			for(int j = 0; j < NbrVariables; j++) {
				DataClause[i][j] = -1;
			}
		}
	}
	
	public void showDataClauses() {
		int k = 1;
		for(int i = 0; i < NbrClauses; i++) {
			System.out.println("Clause "+k);
			for(int j = 0; j < NbrVariables; j++) {
				if(DataClause[i][j] == 1) System.out.print(j+1+" ");
				else if(DataClause[i][j] == 0) System.out.print(-(j+1)+" ");
			}
			System.out.println("");
			k++; 
		}
	}
	
	public void extraireClause(JFXTextArea dataset) {
		// we need to set the numbers variables clauses here
		this.Initialize();
		BufferedReader lire = null;
		String line;
		//update 06 / 04 / 2019
		Pattern pattern = Pattern.compile("((-|)[0-9]+) ((-|)[0-9]+) ((-|)[0-9]+) 0");
		//
		Matcher matcher = null;
		int i = 0;
		
		try {
			lire = new BufferedReader(new FileReader(this.getAbsolutePath()));
			int k;
			while((line = lire.readLine()) != null) {
				
				//Update 06 / 04 / 2019
				line = line.trim().replaceAll(" +"," ");
				//
				matcher = pattern.matcher(line);
				if(matcher.matches()) {
					for(int j = 1; j <= 5; j = j + 2) {
						k = Integer.parseInt(matcher.group(j));
						if(k > 0) this.DataClause[i][k-1] = 1;
						else if(k < 0) this.DataClause[i][-k-1] = 0;
					}
					String clause = "Clause "+i+" : ";
					for(int j = 1; j <= 5; j = j + 2) {
						clause += matcher.group(j);
						if(j != 5) clause +=" v ";
					}
					dataset.appendText(clause+"\n");
					i++;
					
				}
		
			}
			lire.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void appendCnf(FichierCnf newFile) {
		Integer[][] result = new Integer[this.NbrClauses+ newFile.NbrClauses][NbrVariables];
		System.arraycopy(this.DataClause, 0, result, 0, this.DataClause.length);
		System.arraycopy(newFile.DataClause, 0, result, this.DataClause.length, newFile.DataClause.length);
		this.DataClause = new Integer[this.NbrClauses+ newFile.NbrClauses][NbrVariables];
		this.DataClause = result;
		this.NbrClauses= result.length;
	}
	
	
	public void combineCnf(FichierCnf newFile, int index) {
		
		int nbrFile = index + 1;
		
		if(nbrFile == 1) {
			//First file
			this.DataClause = new Integer[this.NbrClauses][NbrVariables];
			for(int i = 0; i < this.NbrClauses; i++) {
				for(int j = 0; j < this.NbrVariables; j++) {
					this.DataClause[i][j] = newFile.DataClause[i][j];
				}
			}
		} else {
			
			Integer[][] result = new Integer[this.NbrClauses+ newFile.NbrClauses][NbrVariables];
		
			//Copying object data to the result
			for(int i = 0; i < this.NbrClauses; i++) {
				for(int j = 0; j < this.NbrVariables; j++) {
					result[i][j] = this.DataClause[i][j];
				}
			}
			
			//Inserting new elements
			int a = 0;
			for(int i = this.NbrClauses; i < result.length; i++) {
				for(int j = 0; j < this.NbrVariables; j++) {
					result[i][j] = newFile.DataClause[a][j];
				}
				a++;
			}
			
			this.DataClause = new Integer[this.NbrClauses+ newFile.NbrClauses][NbrVariables];
			
			for(int i = 0; i < this.DataClause.length; i++) {
				for(int j = 0; j < this.NbrVariables; j++) {
					this.DataClause[i][j] = result[i][j];
				}
			}
			
			
			this.NbrClauses = this.DataClause.length;
			
		}
			
			
	
	
		
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


	
	



}
