


public class Controller {
	
	private static String inputCommand_;
	private static Command currentCommand_;
	private static Command lastCommand_;
	private static int taskID_;
	
	public static void init() {
		currentCommand_ =  null;
		lastCommand_ = null;
	}
	
	public static void wakeUp() {
		//getInputString()
		//getCommand()
		//handleCommand()
	}
	
	private static void getInputString(String input) {
		inputCommand_ = input;
	}
	
	private static Command getCommand() {
		
	}
 
	private static void handleCommand() {
		
	}

	private static void genericAdd() {
		Task newTask = new Task();
		
	}
	
	private static void addNormal(Task newTask) {
		
		newTask.setTaskId(taskID_++);
		newTask.setTaskName(currentCommand_.getTaskName());
	}
	
	private static void addRecurring() {
		
	}
	
	private static void addFloating() {
		
	}

	private static void search() {
		//genericSearch(paras)
	}
	
	private static void list() {
		//genericSearch(paras)
	}
	
	private static void genericSearch() {
		
	} 
	
	private static Task select() {
		
	}
	
	private static Command revertLastAction() {
		
	}
	
	private static void undo() {
		
	}
	
	public static main(String[] args) {
		
	}

}
