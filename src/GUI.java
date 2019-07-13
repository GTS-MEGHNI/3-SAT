import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Timer;
import com.jfoenix.controls.JFXSpinner;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Reflection;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class GUI {
	
	Stage stage = new Stage();
    Pane layout = new Pane();
	JFXSpinner bar = new JFXSpinner();
	Label label = new Label("Veuillez partiener s'il vous plait");
	
	
	public GUI(Label timeLabel, StopWatch task) {
		label.setEffect(new DropShadow());
		timeLabel.textProperty().bind(task.timee);
		label.setFont(Font.font ("raleway", 20)); label.setLayoutX(125); 
		label.setLayoutY(300);
		timeLabel.setFont(Font.font ("Serif", 30)); timeLabel.setLayoutX(200);  timeLabel.setLayoutY(340);
		bar.setProgress(-1); bar.setEffect(new Reflection()); bar.setLayoutX(170); 
		bar.setLayoutY(125);  bar.setPrefHeight(200);  bar.setPrefWidth(150);
		layout.getChildren().addAll(bar,label,timeLabel);
		stage.setScene(new Scene(layout,500,500)); stage.setResizable(false);
		stage.setTitle("Recherche en profondeur en cours..."); stage.initModality(Modality.APPLICATION_MODAL);
	}
	
	public void showAlert(Timer timi,String header) {
		timi.cancel();
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle("Terminé");
		bar.setProgress(100);
		label.setText("Recherche terminé");
		label.setLayoutX(175); 			
		alert.setHeaderText(header);
				
		alert.showAndWait();
		
		stage.close();
	}
	
	public void barChart(HashMap<String,Long> timeMap, ScrollPane scrollPaneBarChart) {
		
		//Bar chart
		Stage stage2 = new Stage();
		CategoryAxis xAxis = new CategoryAxis();
		NumberAxis yAxis = new NumberAxis();
		BarChart<String,Number> bc = new BarChart<String,Number>(xAxis,yAxis);
		bc.setTitle("Temps d'exécution");
		bc.setMinHeight(700);
		bc.setMinWidth((500 *timeMap.size() ) / 11);
	    xAxis.setLabel("Fichiers");       
	    yAxis.setLabel("Temps d'exécution");
	    XYChart.Series series1 = new XYChart.Series();
	    series1.setName("Temps d'exécution");    
	     
	    for(String KeySet : timeMap.keySet()) {
	    	 series1.getData().add(new XYChart.Data(KeySet, timeMap.get(KeySet)));
	     } 
	    bc.getData().addAll(series1);
	    scrollPaneBarChart.setContent(bc);
	    scrollPaneBarChart.setOpacity(1);
	}
	
	public void tableView(HashMap<String,String> solutionMap, ScrollPane scrollPaneTable) {
		
		//Solutions
	    Stage stage3 = new Stage();
	    
		ObservableList<ArrayList<String>> liste = FXCollections.observableArrayList();
		
		
		for(String str : solutionMap.keySet()) {
			liste.add(new ArrayList(Arrays.asList(str,solutionMap.get(str))));
		}
		
		TableView<ArrayList<String>> table = new TableView();
		TableColumn<ArrayList<String>, String> doc  = new TableColumn<>("Fichier");
		TableColumn<ArrayList<String>, String> nombre = new TableColumn<>("Solution");

		doc.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(0)));
		nombre.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(1)));
		table.setItems(liste);
		table.getColumns().addAll(doc,nombre);
		table.setLayoutX(0);
		table.setLayoutY(0);
		table.setMinWidth(1155);
		table.setMinHeight(650);
		
		scrollPaneTable.setContent(table);
		scrollPaneTable.setOpacity(1);
	}

	
	
	
public void lineChart(ArrayList<GeneticResultPerIteration> list, ScrollPane scrollPaneLineChartGenetic) {
		
		Stage stage = new Stage();
		
		NumberAxis xAxis = new NumberAxis();
		NumberAxis yAxis = new NumberAxis();
		
		xAxis.setLabel("Time (s)");
		yAxis.setLabel("Fitness %");
		
		LineChart<Number,Number> lineChart = new LineChart<Number,Number>(xAxis,yAxis);
		
		
		lineChart.setTitle("Fitess developement per time");
		 XYChart.Series series = new XYChart.Series();
        
		 series.setName("file name");
		 series.getData().add(new XYChart.Data(0, 0));

		 for(GeneticResultPerIteration a : list)
			 series.getData().add(new XYChart.Data(a.getTime(), a.getFitnessValue()));
		 
	    scrollPaneLineChartGenetic.setContent(lineChart);
	    lineChart.getData().addAll(series);
	    scrollPaneLineChartGenetic.setOpacity(1);
		
		
	}

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
