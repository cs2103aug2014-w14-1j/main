import java.util.ArrayList;
import java.util.Calendar;

import javafx.application.Application;
import javafx.stage.Stage;



public class UITest extends Application implements UIObserver {
	private UI ui = new UI();
		
	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage stage) throws Exception {
		ui.showStage(stage);
		ui.addUIObserver(this);
		displayTaskTest();
		
	}
	
	private void displayTaskTest(){
		ArrayList<Task> displayTasks = new ArrayList<Task>();
		for (int i = 0; i < 30; i++) {
			Task t = new Task();
			t.setId(i);
			t.setTaskName("Task" + i);
			Calendar task1_test_start_date = Calendar.getInstance();
			task1_test_start_date.set(2014, Calendar.NOVEMBER, 26, 12, 59, 59);
			t.addTaskDatesTimes(task1_test_start_date);
			t.setDisplayId("test");
			displayTasks.add(t);
		}
		this.ui.displayTasks(displayTasks);
	}

	@Override
	public void update() {
		String command = ui.getUserInput();
		ArrayList<Task> displayTasks = new ArrayList<Task>();
		if(command.equalsIgnoreCase("2"));
		for(int i = 0; i < 10; i ++ ){
			Task t = new Task();
			t.setId(i);
			t.setTaskName("2nd Iteration Task" + i);
			Calendar task1_test_start_date = Calendar.getInstance();
			task1_test_start_date.set(2014, Calendar.NOVEMBER, 20, 12, 59, 59);
			Calendar task1_test_end_date = Calendar.getInstance();
			task1_test_end_date.set(2014, Calendar.NOVEMBER, 20, 13, 59, 59);
			
			t.addTaskDatesTimes(task1_test_start_date, task1_test_end_date);
			t.setDisplayId("t");
			displayTasks.add(t);
		}
		this.ui.displayTasks(displayTasks);
		this.ui.setMessageToUser("Successful Testing");
		
	}
	
	
	

}
