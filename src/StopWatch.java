import java.util.TimerTask;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;

public class StopWatch extends TimerTask{
	
	int ms = 0, s = 0, m = 0, h = 0;
	int l = 0;
	SimpleStringProperty timee = new SimpleStringProperty("00:00:00");

	public void run() {
		
		Platform.runLater(()-> {
			s++;
			
			if(s >=59) {
				ms = 0;
				s = 0;
				m++;
			}
			if(m > 60) {
				ms = 0;
				s = 0;
				m = 0;
				h++;
			}
			if(s < 10) {
				if(m < 10) 
					timee.set("0"+Integer.toString(h)+":"+ "0"+Integer.toString(m)+ ":"+"0"+Integer.toString(s));
				else 
					timee.set("0"+Integer.toString(h)+":"+ Integer.toString(m)+ ":"+"0"+Integer.toString(s));	
			} else {
				if(m < 10) 
					timee.set("0"+Integer.toString(h)+":"+ "0"+Integer.toString(m)+ ":"+Integer.toString(s));
				else 
					timee.set("0"+Integer.toString(h)+":"+ Integer.toString(m)+ ":"+Integer.toString(s));
			}
		}); 
	}
		
	

}