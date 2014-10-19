import java.util.ArrayList;

import javafx.application.Application;
import javafx.stage.Stage;



public class UITest extends Application{
	private UI ui = new UI();
		
	public static void main(String[] args) {
		launch(args);

	}

	@Override
	public void start(Stage stage) throws Exception {
		ui.showStage(stage);
		displayTaskTest();
		
	}
	
	private void displayTaskTest(){
		ArrayList<Task> displayTasks = new ArrayList<Task>();
		for (int i = 0; i < 30; i++) {
			Task t = new Task();
			t.setTaskId(i);
			t.setTaskName("Task" + i);
			t.setDisplayId("test");
			displayTasks.add(t);
		}
		this.ui.displayTasks(displayTasks);
	}
	
	
	

}
