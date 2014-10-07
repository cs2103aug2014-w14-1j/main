

public class Controller {
	
	private static String inputCommand_;
	private static Command currentCommand_;
	private static Command lastCommand_;
	private static int taskID_;
	private static Display UI_;
	
	public static void init() {
		currentCommand_ =  null;
		lastCommand_ = null;
		UI_ = new Display();
	}
	
	public static void main(String args[]) {
		init();
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
		UI_.print(result);
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
