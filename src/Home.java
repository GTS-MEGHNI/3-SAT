import javax.swing.text.JTextComponent.KeyBinding;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class Home extends Application {

	public void start(Stage stage) throws Exception {
		
		FXMLLoader loader = new FXMLLoader(getClass().getResource("newGUI.fxml"));
		Parent root = loader.load();
		GUIController controller = new GUIController();
		controller.setStage(stage);
		stage.setScene(new Scene(root));
		stage.setTitle("SAT-3");
		stage.setFullScreen(true);
		stage.show();
		//stage.setOnCloseRequest(e-> stage.close());
	    stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
		       public void handle(WindowEvent e) {
		          Platform.exit();
		          System.exit(0);
		       }
	    });
	}
	

}
