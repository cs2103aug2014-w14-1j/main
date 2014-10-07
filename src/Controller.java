import java.util.ArrayList;



public class Controller {
	
	private static String inputCommand_;
	private static Command currentCommand_;
	private static Command lastCommand_;
	private static int taskID_;
	private static Display UI_;
	private static ArrayList<Task> searchResults_;
	private static Parser parser_;
	
	public static void init() {
		currentCommand_ =  null;
		lastCommand_ = null;
		UI_ = new Display();
		parser_ = new Parser();
	}
	
	public static void main(String args[]) {
		init();
		
		ArrayList<Task> tasks = new ArrayList<Task>();
		String[] names = { "task1", "task2", "task3", "task4"};
		for (String name: names) {
			Task newTask = new Task();
			newTask.setTaskName(name);
			tasks.add(newTask);
		}
		
		UI_.toDisplay(tasks);
		
		while (UI_.hasNextLine()) {
			inputCommand_= UI_.get();
			proceedCommand();
			display(inputCommand_);
		}
	}
	
	private static void proceedCommand() {
		currentCommand_ = new Command();
	}
	
	private static void display(String result) {
		UI_.println(result);
	}
	
	private static void add() {
		Task newTask = new Task();
	}
	
	private static void search() {
		searchResult();
	}
	/*
	private static void handleCommand() {
		COMMAND_TYPE commandType = currentCommand_.getCommandType();
		switch (commandType) {
		case COMMAND_TYPE.ADD: {
			genericAdd();
			}
		case COMMAND_TYPE
			delete();
		

		}
	}
	*/

	/*
	private static void addNormal(Task newTask) {
		
		newTask.setTaskId(taskID_++);
		newTask.setTaskName(currentCommand_.getTaskName());
	}
	*/
}
