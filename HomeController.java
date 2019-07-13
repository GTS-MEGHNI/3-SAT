import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;

import com.jfoenix.controls.JFXSpinner;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Reflection;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Window;
import javafx.stage.Stage;

public class HomeController {
	
	@FXML
	private Stage stage;
	private Window window;
	FichierCnf file;
	FichierCnf allfiles;
	ArrayList<FichierCnf> DirFiles;
	Label timeLabel = new Label();
	Timer timi = new Timer();
	StopWatch task = new StopWatch();
	Integer[][] DataClause;

	
	
	public void setStage(Stage stage) {
		this.stage = stage;
	}
	
	
	// simplify alerts
	public static void FileAlert(String title, String message, boolean exit) {
		Alert alert = new Alert(Alert.AlertType.ERROR);
		alert.setTitle(title);
		alert.setHeaderText(message);
		alert.showAndWait();
		if(exit) System.exit(0);
	}
	
	
	public static ButtonType chartAlert(String title, String message) {
		Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
		alert.setTitle(title);
		alert.setHeaderText(message);						
		Optional<ButtonType> result = alert.showAndWait();
		return result.get();
	}
	
	
	public void importer() {
		
		FileChooser chooser = new FileChooser();
		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Cnf Files (*.cnf)", "*.cnf");
		chooser.getExtensionFilters().add(extFilter);
		List<File> liste = chooser.showOpenMultipleDialog(this.stage);
		///One file imported
		if(liste != null) {
			this.file = new FichierCnf(liste.get(0).getAbsolutePath());
			this.file.extraireClause();
			allfiles = new FichierCnf("");
			this.allfiles.combineCnf(this.file,0);

			if(liste.size() > 1) {
				//Many files
				allfiles = new FichierCnf("");	
				this.DirFiles = new ArrayList<FichierCnf>();
				for (File file : liste) {
					FichierCnf fichier = new FichierCnf(file.getAbsolutePath());
					this.DirFiles.add(fichier);
					this.DirFiles.get(liste.indexOf(file)).extraireClause();
					this.allfiles.combineCnf(fichier,liste.indexOf(file));
				}							
			}
		}
	}
			
	
	
	public void profondeurMax() {
		
		if(this.allfiles == null) {
			FileAlert("ERROR", "NO DATA FOUND", false);	importer();
			return;
		}
		
		Timer timi = new Timer();
		StopWatch task = new StopWatch();
		timi.schedule(task, 1000, 1000);
		GUI ui = new GUI(timeLabel,task);
			
		RechercheProfondeur rech = new RechercheProfondeur(this.allfiles.getDataClause(),
				this.allfiles.NbrClauses,this.allfiles.NbrVariables);
		ui.stage.show();
		Thread t = new Thread(new Runnable() {	
			public void run() {
				
				long startTime = System.currentTimeMillis();
				ArrayList<Integer> solution = rech.startDepthSearch();
				long endTime = System.currentTimeMillis();
				
				//Depth search terminated, call the GUI
				Platform.runLater(()-> {
					ui.showAlert(timi,"Solution est est\n"+solution.toString()
					+"\n Temps d'exï¿½cution :"+Long.toString(endTime-startTime)+"ms");
				});
			}
		});
		t.setDaemon(true);
		t.start();*/
	}
	

	public void profondeur() {
		
		Timer timi = new Timer();
		StopWatch task = new StopWatch();
		timi.schedule(task, 1000, 1000);
		
		GUI ui = new GUI(timeLabel,task);
		
		if(DirFiles == null) {
			//User select at least one file
			if(this.file == null || this.file.getDataClause() == null) {
				//No Data selected
				FileAlert("ERROR", "NO DATA FOUND", false);importer();		
			} else {
				RechercheProfondeur rech = new RechercheProfondeur(this.file.getDataClause(),
						this.file.NbrClauses,this.file.NbrVariables);
				ui.stage.show();
				Thread t = new Thread(new Runnable() {	
					public void run() {
						
						long startTime = System.currentTimeMillis();
						ArrayList<Integer> solution = rech.startDepthSearch();
						long endTime = System.currentTimeMillis();
						
						//Depth search terminated, call the GUI
						Platform.runLater(()-> {
							ui.showAlert(timi,"La Solution pour le fichier "+file.getName()+" est\n"+solution.toString()
							+"\n Temps d'exï¿½cution :"+Long.toString(endTime-startTime)+"ms");
						});
					}
				});
				t.setDaemon(true);
				t.start();
			}
		} else {
			ui.stage.show();
			Thread t = new Thread(new Runnable() {	
				public void run() {
					Create a hash table to store the :
					Key is the file name
					Value is the execution time
					HashMap<String,Long> timeMap = new HashMap<>();
					Create a hash table to store the result of each file
					 Key is the file name
					Value is the solution
					HashMap<String,String> solutionMap = new HashMap<>();
					for (FichierCnf fl : DirFiles) {
						
						RechercheProfondeur rech = new
								RechercheProfondeur(fl.getDataClause(),fl.NbrClauses,fl.NbrVariables);
						long startTime = System.currentTimeMillis();
						ArrayList<Integer> list = rech.startDepthSearch();
						long endTime = System.currentTimeMillis();
						timeMap.put(fl.getName(), endTime - startTime);
						solutionMap.put(fl.getName(), list.toString());
					}
									
					
					//If we are in this section then we finish all the files
					//Display information here	
					Platform.runLater(() -> {
						
						timi.cancel();

						ui.bar.setProgress(100);
						ui.label.setText("Recherche terminï¿½");
						ui.label.setLayoutX(175); 
						ButtonType result = chartAlert("Terminï¿½","Recherche terminï¿½, cliquer sur OK pour afficher les rï¿½sultats");						
						
						if(result == ButtonType.OK) {

							ui.barChart(timeMap);

							ui.tableView(solutionMap);     	   
						} else {
							ui.stage.close();
						}
					});
				}
			});
			t.setDaemon(true);
			t.start();
		}*/
	}

		
	
	public void astar() {
		
		HashMap<String,Long> timeMap = new HashMap<>();
		HashMap<String,String> solutionMap = new HashMap<>();
		GUI ui = new GUI(timeLabel,task);
		long timeStart = System.currentTimeMillis();


		
		if(DirFiles ==null||DirFiles.isEmpty())
		if(this.file == null || this.file.getDataClause() == null) {
			FileAlert("ERROR", "NO DATA FOUND", false); importer();
		} else {
		
		Runnable runnable = () -> {	
		RechercheAstar rech = new RechercheAstar(this.file.getDataClause(),this.file.NbrClauses,this.file.NbrVariables);
		
		long t1 = System.currentTimeMillis();
		ArrayList<Integer> list = rech.startAstarSearch(false); //the bool param (optional) is to show Game or not
		long t2 = System.currentTimeMillis();
		
		Platform.runLater(() -> {
			RechercheAstar.InformAlert("Solution","Fichier "+file.getName()+"\n"+list.toString()+"\ntemps:"+(t2-t1), true);
		});
		
		};
		Thread t = new Thread(runnable);
		t.setDaemon(true);
		t.start();
		} 
		else {
			Runnable runnable = () -> {	

				for (FichierCnf fl : this.DirFiles) {
					
					Runnable r2 = () -> {	

					RechercheAstar rech = new RechercheAstar(fl.getDataClause(), fl.NbrClauses, fl.NbrVariables);
					long t1 = System.currentTimeMillis();
					ArrayList<Integer> list = rech.startAstarSearch();
					long t2 = System.currentTimeMillis();
					timeMap.put(fl.getName(), t2 - t1);
					solutionMap.put(fl.getName(), list.toString());
				};
				Thread t2 = new Thread(r2);
				t2.setDaemon(true);
				t2.start();
				
				try {
					t2.join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}				
				}

			Platform.runLater(() -> {
				
				ui.bar.setProgress(100);
				ui.label.setText("Recherche terminï¿½");
				ui.label.setLayoutX(175);
				ButtonType result = chartAlert("Terminï¿½",
						"Recherche terminï¿½, cliquer sur OK pour afficher les rï¿½sultats");
				if (result == ButtonType.OK) {
					ui.barChart(timeMap);
					ui.tableView(solutionMap);
				} else {
					ui.stage.close();
				}
			});

			long finish =System.currentTimeMillis();
			System.out.println("execution took"+(finish-timeStart));
			};
			Thread t = new Thread(runnable);
			t.setDaemon(true);
			t.start();
		}

	}

	
	
	public void astar2() {
		HashMap<String,Long> timeMap = new HashMap<>();
		HashMap<String,String> solutionMap = new HashMap<>();
		GUI ui = new GUI(timeLabel,task);
		
		
		if(DirFiles ==null||DirFiles.isEmpty())
		if(this.file == null || this.file.getDataClause() == null) {
			FileAlert("ERROR", "NO DATA FOUND", false);importer();
		} else {	
			Runnable runnable = () -> {	

		RechercheAstar rech = new RechercheAstar(this.file.getDataClause(),file.NbrClauses,file.NbrVariables);
		long t1 = System.currentTimeMillis();
		ArrayList<ArrayList<Integer>> list = rech.startAstarSearch_with_sol_sum();
		long t2 = System.currentTimeMillis();
		

		Platform.runLater(() -> {
			String show = "";
			for (ArrayList<Integer> integer : list)
				show+= "\n" + integer.toString() ;
//			show+= "\n time:"+ (t2-t1) +"ms";
		RechercheAstar.InformAlert( ""+ list.size() +" solutions! disp after "+(t2-t1)+"ms", show,true);
		});
			};
			Thread t = new Thread(runnable);
			t.setDaemon(true);
			t.start();
		} else { // use for multiple files
			
			

			Runnable runnable = () -> {	
			for (FichierCnf fl : this.DirFiles) {
				RechercheAstar rech = new RechercheAstar(fl.getDataClause(),fl.NbrClauses,fl.NbrVariables);
				long t1 = System.currentTimeMillis();
				ArrayList<ArrayList<Integer>> list = rech.startAstarSearch_with_sol_sum();
				long t2 = System.currentTimeMillis();
				timeMap.put(fl.getName(), t2 - t1);
				solutionMap.put(fl.getName(), list.toString());
			}

	
			Platform.runLater(() -> {
				
				ui.bar.setProgress(100);
				ui.label.setText("Recherche terminï¿½");
				ui.label.setLayoutX(175);
				ButtonType result = chartAlert("Terminï¿½",
						"Recherche terminï¿½, cliquer sur OK pour afficher les rï¿½sultats");
				if (result == ButtonType.OK) {
					ui.barChart(timeMap);
					ui.tableView(solutionMap);
				} else {
					ui.stage.close();
				}
			});
			
			
			};

			Thread t = new Thread(runnable);
			t.setDaemon(true);
			t.start();
		}
		



	}
	
	public void MAXastar() {

		long timeStart = System.currentTimeMillis();
		if (DirFiles == null || DirFiles.isEmpty())
			if (this.file == null || this.file.getDataClause() == null) {
				FileAlert("ERROR", "NO DATA FOUND", false); importer();
			} else {

				Runnable runnable = () -> {

					RechercheAstar rech = new RechercheAstar(this.file.getDataClause(), file.NbrClauses,
							file.NbrVariables);
					long t1 = System.currentTimeMillis();
					ArrayList<Integer> list = rech.startAstarSearch(true);
					long t2 = System.currentTimeMillis();
					Platform.runLater( ()->{
						RechercheAstar.InformAlert("Solution",
								"Fichier " + file.getName() + "\n" + list.toString() + "\ntemps:" + (t2 - t1), true);
						});				};
				Thread t = new Thread(runnable);
				t.setDaemon(true);
				t.start();
			}
		else {

			Runnable runnable = () -> {

				boolean first = true;
				for (FichierCnf fl : this.DirFiles) {
					if (!first) {
						this.file.appendCnf(fl);
					} else
						first = false;
				}
				RechercheAstar rech = new RechercheAstar(this.file.getDataClause(), this.file.NbrClauses,
						this.file.NbrVariables);
				long t1 = System.currentTimeMillis();
				ArrayList<Integer> list = rech.startAstarSearch(true);
				long t2 = System.currentTimeMillis();
				
				Platform.runLater( ()->{
				RechercheAstar.InformAlert("Solution",
						"Fichier " + file.getName() + "\n" + list.toString() + "\ntemps:" + (t2 - t1), true);
				});
				
			
			};
			Thread t = new Thread(runnable);
			t.setDaemon(true);
			t.start();
		}
		long finish = System.currentTimeMillis();
		System.out.println("execution took" + (finish - timeStart));

	}
	
	public void largeur() {
		
		
		Timer timi = new Timer();
		StopWatch task = new StopWatch();
		timi.schedule(task, 1000, 1000);
		
		GUI ui = new GUI(timeLabel,task);
		
		if(DirFiles == null) {
			//User select at least one file
			if(this.file == null || this.file.getDataClause() == null) {
				//No Data selected
				FileAlert("ERROR", "NO DATA FOUND", false);	importer();	
			} else {
				RechercheLargeur rech = new RechercheLargeur(this.file.getDataClause(),
						this.file.NbrClauses,this.file.NbrVariables);
				ui.stage.show();
				Thread t = new Thread(new Runnable() {	
					public void run() {
						
						long startTime = System.currentTimeMillis();
						ArrayList<Integer> solution = rech.startBreathSearch();
						long endTime = System.currentTimeMillis();
						
						//Depth search terminated, call the GUI
						Platform.runLater(()-> {
							ui.showAlert(timi,"La Solution pour le fichier "+file.getName()+" est\n"+solution.toString()
							+"\n Temps d'exï¿½cution :"+Long.toString(endTime-startTime)+"ms");
						});
					}
				});
				t.setDaemon(true);
				t.start();
			}
		} else {
			ui.stage.show();
			Thread t = new Thread(new Runnable() {	
				public void run() {
					/*Create a hash table to store the :
					Key is the file name
					Value is the execution time*/
					HashMap<String,Long> timeMap = new HashMap<>();
					/*Create a hash table to store the result of each file
					 Key is the file name
					Value is the solution*/
					HashMap<String,String> solutionMap = new HashMap<>();
					for (FichierCnf fl : DirFiles) {
						
						RechercheLargeur rech = new
								RechercheLargeur(fl.getDataClause(),fl.NbrClauses,fl.NbrVariables);
						long startTime = System.currentTimeMillis();
						ArrayList<Integer> list = rech.startBreathSearch();
						long endTime = System.currentTimeMillis();
						timeMap.put(fl.getName(), endTime - startTime);
						solutionMap.put(fl.getName(), list.toString());
					}
									
					
					//If we are in this section then we finish all the files
					//Display information here	
					Platform.runLater(() -> {
						timi.cancel();
						Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
						alert.setTitle("Terminé");
						ui.bar.setProgress(100);
						ui.label.setText("Recherche terminé");
						ui.label.setLayoutX(175); 	
						alert.setHeaderText("Recherche terminé, cliquer sur OK pour afficher les rï¿½sultats");						
						Optional<ButtonType> result = alert.showAndWait();
						
						if(result.get() == ButtonType.OK) {
							
							//BarChart
							ui.barChart(timeMap);
							
							//TableView
							ui.tableView(solutionMap);     	   
						}
					});
				}
			});
			t.setDaemon(true);
			t.start();
		}
	}
	
	public void genetic() {
		
		Timer timi = new Timer();
		StopWatch task = new StopWatch();
		timi.schedule(task, 1000, 1000);
		
		GUI ui = new GUI(timeLabel,task);
		
		if(DirFiles == null) {
			//User select at least one file
			if(this.file == null || this.file.getDataClause() == null) 
				FileAlert("ERROR", "NO DATA FOUND", false);
			 else {
				 GeneticResearch rech = new GeneticResearch(this.file.getDataClause(),this.file.NbrClauses,this.file.NbrVariables);
				ui.stage.show();
				Thread t = new Thread(new Runnable() {	
					public void run() {
						
						long startTime = System.currentTimeMillis();
						ArrayList<Integer> solution = rech.startSearch();
						long endTime = System.currentTimeMillis();
						
						//Depth search terminated, call the GUI
						Platform.runLater(()-> {
							ui.showAlert(timi,"La Solution pour le fichier "+file.getName()+" est\n"+solution.toString()
							+"\n Temps d'exécution :"+Long.toString(endTime-startTime)+"ms");
						});
					}
				});
				t.setDaemon(true);
				t.start();
			 }
		} else {
			GeneticResearch rech = new GeneticResearch(this.allfiles.getDataClause(),this.allfiles.NbrClauses,this.allfiles.NbrVariables);
		}
		

	}
	
	
}
		
		
		
		

	
	













