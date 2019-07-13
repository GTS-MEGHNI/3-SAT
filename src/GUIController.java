import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXSpinner;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;


public class GUIController implements Initializable{
	@FXML private Stage stage;
	@FXML private Pane MainPanel;
	@FXML private JFXComboBox<String> Bench;
	@FXML private JFXComboBox<String> instance;
	@FXML private JFXComboBox<String> method;
	@FXML private JFXButton btn;
	@FXML private JFXTextArea dataset;
	@FXML private Pane paneParams;
	@FXML private Label totalTime;
	@FXML private JFXSpinner spinner;
	@FXML private Label proc;
	@FXML private Pane paneSummary;
	@FXML private JFXTextField popField;
	@FXML private JFXTextField crossField;
	@FXML private JFXTextField maxIterField;
	@FXML private JFXTextField mutField;
	@FXML private Pane paneLog;
	@FXML private JFXTextArea areaLog;
	@FXML private ScrollPane lineChartScrollPane;
	@FXML private ImageView finishIcon;
	@FXML private Pane PsoParams;
	@FXML private JFXTextField swarmField;
	@FXML private JFXTextField SwarmIterField;
	@FXML private JFXTextField InertiaField;
	@FXML private JFXTextField c1Field;
	@FXML private JFXTextField c2Field;


	
	FichierCnf file;
	FichierCnf allfiles;
	ArrayList<FichierCnf> DirFiles;
	Label timeLabel = new Label();
	ArrayList<GeneticResultPerIteration> list;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		Bench.getItems().addAll("UF20-91","UF50-218","UF75-325");
		method.getItems().addAll("DFS","BFS","A*","GA","PSO");
		
		Bench.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {

			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				
				instance.getItems().clear();
				
				File folder = new File(newValue);
				 
		        String[] files = folder.list();
		 
		        instance.setPromptText("Select an instance");
		        
		        for (String file : files)
		        {
		        	instance.getItems().add(file);
		        }
				
			}			
		});
		
		instance.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {

			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				
				if(newValue != null) {
					
					File folder = new File(Bench.getSelectionModel().getSelectedItem()+"/"+newValue);
					
					System.out.println(folder.getAbsolutePath());
					
					Pattern pattern = Pattern.compile("UF([0-9]+)-([0-9]+)");
					
					Matcher matcher = pattern.matcher(Bench.getSelectionModel().getSelectedItem());
					
					matcher.matches();
					
					file = new FichierCnf(folder.getAbsolutePath(),Integer.parseInt(matcher.group(2)),Integer.parseInt(matcher.group(1)));
					
					dataset.clear();
					
					file.extraireClause(dataset);
					
				}			
			}
		});
		
		method.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {

			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				if(newValue == "GA") {
					PsoParams.setDisable(true);
					PsoParams.setOpacity(0);
					paneParams.setDisable(false);
					paneParams.setOpacity(1);
				} else if(newValue == "PSO") {
					paneParams.setDisable(true);
					paneParams.setOpacity(0);
					PsoParams.setDisable(false);
					PsoParams.setOpacity(1);		
				} else {
					paneParams.setDisable(true);
					paneParams.setOpacity(0);
					PsoParams.setDisable(true);
					PsoParams.setOpacity(0);	
				}			
			}
		});
			
		
	}

	public void start() {
		
		areaLog.clear();
		
		
		if(method.getSelectionModel().getSelectedItem() == "GA") {
			genetic();
		} else if(method.getSelectionModel().getSelectedItem() == "DFS") {
			depth();
		} else if(method.getSelectionModel().getSelectedItem() == "PSO") {
			PSO();
		} else if(method.getSelectionModel().getSelectedItem() == "A*") {
			Astar();
		}
		
		
	}

	public Stage getStage() {
		return stage;
	}

	public void setStage(Stage stage) {
		this.stage = stage;
	}
	
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
	

	

	public void depth() {
		
		RechercheProfondeur rech = new RechercheProfondeur(this.file.getDataClause(),
				this.file.getNbrClauses(),this.file.getNbrVariables());
		finishIcon.setOpacity(0);
		proc.setLayoutX(800);
		proc.textProperty().bind(new SimpleStringProperty("Processing ... please wait"));
		lineChartScrollPane.setOpacity(0);
		lineChartScrollPane.setDisable(true);

		Thread t = new Thread(new Runnable() {	
			public void run() {
				HashMap<Long,Long> lineGraphMap = new HashMap<>();
				
				long startTime = System.currentTimeMillis();
				ArrayList<Integer> solution = rech.startDepthSearch();
				long endTime = System.currentTimeMillis();
				
				
				//Depth search terminated, call the GUI
				Platform.runLater(()-> {
					
					spinner.setOpacity(0);
					lineChartScrollPane.setDisable(true);
					proc.setLayoutX(1000);
					proc.textProperty().bind(new SimpleStringProperty("Done !"));
					finishIcon.setOpacity(1);
					
					BufferedWriter write = null;
					try {
						write = new BufferedWriter(new FileWriter("DFS.txt", true));
						write.write(file.getName() + " " + Long.toString(endTime-startTime) + " " + solution.toString());
						write.write(System.getProperty("line.separator"));
						write.close();
						
					} catch (IOException e) { e.printStackTrace(); } 
				
					Alert alert = new Alert(Alert.AlertType.INFORMATION);
					alert.setTitle("Done");
					alert.setHeaderText("Solution has been extracted to DFS.txt file");		
					alert.showAndWait();
					
				
					
				});
			}
		});
		t.setDaemon(true);
		t.start();
		spinner.setOpacity(1);
		proc.setOpacity(1);
		
	}
	
	

	public void Astar() {
		
		RechercheAstar rech = new RechercheAstar(this.file.getDataClause(),
				this.file.getNbrClauses(),this.file.getNbrVariables());
		finishIcon.setOpacity(0);
		proc.setLayoutX(800);
		lineChartScrollPane.setOpacity(0);
		lineChartScrollPane.setDisable(true);
		proc.textProperty().bind(new SimpleStringProperty("Processing ... please wait"));

		Thread t = new Thread(new Runnable() {	
			public void run() {				
				long startTime = System.currentTimeMillis();
				ArrayList<Integer> solution = rech.startAstarSearch();
				long endTime = System.currentTimeMillis();
					
				//Depth search terminated, call the GUI
				Platform.runLater(()-> {
					
					spinner.setOpacity(0);
					lineChartScrollPane.setDisable(true);
					proc.setLayoutX(1000);
					proc.textProperty().bind(new SimpleStringProperty("Done !"));
					finishIcon.setOpacity(1);
					
					BufferedWriter write = null;
					try {
						write = new BufferedWriter(new FileWriter("Astar.txt", true));
						write.write(file.getName() + " " + Long.toString(endTime-startTime) + " " + solution.toString());
						write.write(System.getProperty("line.separator"));
						write.close();
						
					} catch (IOException e) { e.printStackTrace(); } 
				
					Alert alert = new Alert(Alert.AlertType.INFORMATION);
					alert.setTitle("Done");
					alert.setHeaderText("Solution has been extracted to Astar.txt file");		
					alert.showAndWait();
				});
			}
		});
		t.setDaemon(true);
		t.start();
		spinner.setOpacity(1);
		proc.setOpacity(1);	
	}
	
	
	public void genetic() {
	
		GeneticResearch rech = new GeneticResearch(this.file.getDataClause(),
				this.file.getNbrClauses(),this.file.getNbrVariables());
		
		finishIcon.setOpacity(0);
		proc.setLayoutX(800);
		proc.textProperty().bind(new SimpleStringProperty("Processing ... please wait"));
		lineChartScrollPane.setOpacity(0);
		lineChartScrollPane.setDisable(true);
		
		
		rech.setCROSS_RATE(Integer.parseInt(crossField.getText()));
		rech.setMAX_ITR(Integer.parseInt(maxIterField.getText()));
		rech.setMUTATION_RATE(Integer.parseInt(mutField.getText()));
		rech.setNB_POP(Integer.parseInt(popField.getText()));
		
		Thread thread = new Thread(new Runnable() {		
			public void run() {
				long startTime = System.currentTimeMillis();
				list = new ArrayList<>(rech.startSearch(areaLog));
				long endTime = System.currentTimeMillis();
				BufferedWriter write = null;
				try {
					write = new BufferedWriter(new FileWriter("GA.txt", true));
					write.write(file.getName() + " " + list.get(list.size() - 1).getFitnessValue() + " " +
					list.get(list.size() - 1).getNbrClause() + " " + rech.getNB_POP() + " " + rech.getCROSS_RATE() + " " +rech.getMUTATION_RATE() + " " +
					rech.getMAX_ITR() + " " + Long.toString(endTime - startTime) + " " + list.get(list.size() - 1).getSolution());
					write.write(System.getProperty("line.separator"));
					write.close();
					
				} catch (IOException e) { e.printStackTrace(); } 
				
				
				Platform.runLater(()-> {
					spinner.setOpacity(0);
					proc.setOpacity(0);
					
					CategoryAxis xAxis = new CategoryAxis();
			        NumberAxis yAxis = new NumberAxis();
			        xAxis.setLabel("Number of Month");
			        //creating the chart
			        LineChart<String,Number> lineChart = new LineChart<String,Number>(xAxis,yAxis);
			        BufferedReader reader = null;
			        try {
						reader = new BufferedReader(new FileReader(new File("GA.txt")));
					} catch (FileNotFoundException e) { e.printStackTrace(); }
			                
			        lineChart.setTitle("Comparing PSO and GA with Fitness score");
			        //defining a series
			        XYChart.Series series = new XYChart.Series();
			        series.setName("GA");
			        //populating the series with data
			        
			        String line;
			        int x = 0;
			        try {
			        	line = reader.readLine();
						while((line = reader.readLine()) != null) {
							String[] array = line.split(" ");
					        series.getData().add(new XYChart.Data(array[0], Integer.parseInt(array[1]))); 	
					        lineChart.setMinWidth(lineChart.getWidth() + x);
					        x += 65;
						}
					} catch (IOException e) { e.printStackTrace(); }      
			        lineChart.getData().add(series);
			        lineChartScrollPane.setDisable(false);
			        lineChartScrollPane.setContent(lineChart);
			        lineChartScrollPane.setOpacity(1);	
			        
			        Alert alert = new Alert(Alert.AlertType.INFORMATION);
					alert.setTitle("Done");
					alert.setHeaderText("Solution has been extracted to GA.txt file");		
					alert.showAndWait();
				});
			}
		});
		thread.setDaemon(true);
		thread.start();		
		spinner.setOpacity(1);
		proc.setOpacity(1);
	}
	

	
	public void PSO() {
		
		Swarm swarm = new Swarm(this.file.getDataClause(), this.file.getNbrClauses(),this.file.getNbrVariables());

		finishIcon.setOpacity(0);
		proc.setLayoutX(800);
		lineChartScrollPane.setOpacity(0);
		lineChartScrollPane.setDisable(true);
		proc.textProperty().bind(new SimpleStringProperty("Processing ... please wait"));
		
		swarm.setC1(Double.parseDouble(c1Field.getText()));
		swarm.setC2(Double.parseDouble(c2Field.getText()));
		swarm.setMaxIter(Integer.parseInt(SwarmIterField.getText()));
		swarm.setW(Double.parseDouble(InertiaField.getText()));
		swarm.setSwarmSize(Integer.parseInt(swarmField.getText()));
		
		Thread thread = new Thread(new Runnable() {
			public void run() {
				long startTime = System.currentTimeMillis();
				areaLog.clear();
				list = new ArrayList<>();
				list = swarm.launch(areaLog);	
				long endTime = System.currentTimeMillis();
				BufferedWriter write = null;
				try {
					write = new BufferedWriter(new FileWriter("PSO.txt", true));
					write.write(file.getName() + " " + list.get(list.size() - 1).getFitnessValue() + " " +
					swarm.getSwarmSize() + " " + swarm.getMaxIter() + " " + swarm.getW() + " " + swarm.getC1() + " " +
					swarm.getC2() + " " + Long.toString(endTime - startTime) + " " + list.get(list.size() - 1).getSolution());
					write.write(System.getProperty("line.separator"));
					write.close();
					
				} catch (IOException e) { e.printStackTrace(); } 
				
				Platform.runLater(()-> {
					spinner.setOpacity(0);
					proc.setOpacity(0);
					
					CategoryAxis xAxis = new CategoryAxis();
			        NumberAxis yAxis = new NumberAxis();
			        xAxis.setLabel("Number of Month");
			        //creating the chart
			        LineChart<String,Number> lineChart = new LineChart<String,Number>(xAxis,yAxis);
			        BufferedReader reader = null;
			        BufferedReader reader1 = null;

			        try {
						reader = new BufferedReader(new FileReader(new File("GA.txt")));
						reader1 = new BufferedReader(new FileReader(new File("PSO.txt")));
					} catch (FileNotFoundException e) { e.printStackTrace(); }
			                
			        lineChart.setTitle("Comparing PSO and GA with Fitness score");
			        //defining a series
			        XYChart.Series seriesGA = new XYChart.Series();
			        seriesGA.setName("GA");
			        XYChart.Series seriesPSO = new XYChart.Series();
			        seriesPSO.setName("PSO");
			        //populating the series with data
			        
			        String line, line1;			 
			        int x = 0;
			        try {
			        	line = reader.readLine();
			        	line1 = reader1.readLine();
						while((line = reader.readLine()) != null) {
							String[] array = line.split(" ");
							seriesGA.getData().add(new XYChart.Data(array[0], Integer.parseInt(array[1]))); 
							lineChart.setMinWidth(lineChart.getWidth() + x);
					        x += 65;
						}
						while((line1 = reader1.readLine()) != null) {
							String[] array = line1.split(" ");
							seriesPSO.getData().add(new XYChart.Data(array[0], Integer.parseInt(array[1]))); 
							lineChart.setMinWidth(lineChart.getWidth() + x);
						}
					} catch (IOException e) { e.printStackTrace(); }      
			        lineChart.getData().add(seriesGA);
			        lineChart.getData().add(seriesPSO);

			        lineChartScrollPane.setDisable(false);
			        lineChartScrollPane.setContent(lineChart);
			        lineChartScrollPane.setOpacity(1);	
			        
			        Alert alert = new Alert(Alert.AlertType.INFORMATION);
					alert.setTitle("Done");
					alert.setHeaderText("Solution has been extracted to PSO.txt file");		
					alert.showAndWait();
				});

			}}); 
		thread.setDaemon(true); 
		thread.start();
		spinner.setOpacity(1);
		proc.setOpacity(1);
			
		
	}
	
	//---------------------------------------> G E T T E R S __ S E T T E R S <----------------------------------------------
	
	public Pane getMainPanel() {
		return MainPanel;
	}




}

















