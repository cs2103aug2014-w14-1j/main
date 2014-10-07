import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;

import org.json.JSONException;



public class Controller {
	
	private static String inputCommand_;
	private static Command currentCommand_;
	private static Command lastCommand_;
	private static int taskID_;
	private static Display UI_;
	private static ArrayList<Task> searchResults_;
	private static Parser parser_;
	private static Storage storage_;
	
	public static void main(String args[]) throws Exception {
		init();
		
		String[] names = {"task 1", "task 2", "task 3", "task 4"};
		
		storage_.clearAll();
		
		for (String name: names) {
			Task newTask = new Task();
			newTask.setTaskName(name);
			newTask.addTaskDatesTimes(Calendar.getInstance());
			
			new LinkedList<Calendar>();
			storage_.insert(newTask);
		} 
		
		ArrayList<String> keywords = new ArrayList<String>();
		keywords.add("task");
		
		ArrayList<String> tags = new ArrayList<String>();
		Calendar start_date = null;
		Calendar end_date = null;
		
		searchResults_ = storage_.search(keywords,tags,null,null);
		UI_.print(Integer.toString(searchResults_.size()));
		
		UI_.toDisplay(searchResults_);
		
		delete(1);
		
		searchResults_ = storage_.search(keywords,tags,null,null);
		
		UI_.toDisplay(searchResults_);
		
		while (UI_.hasNextLine()) {
			inputCommand_= UI_.get();
			proceedCommand();
			display(inputCommand_);
		}
	}
	
	public static void init() throws Exception {
		currentCommand_ =  null;
		lastCommand_ = null;
		UI_ = new Display();
		parser_ = new Parser();
		storage_ = new Storage();
	}

	private static void proceedCommand() {
		lastCommand_ = currentCommand_;
		currentCommand_ = new Command();
	}
	
	private static void display(String result) {
		UI_.println(result);
	}
	
	private static void add() throws JSONException, IOException {
		Task newTask = new Task();
		newTask.setTaskName("new task");
		storage_.insert(newTask);
	}
	
	private static void search() {
		ArrayList<String> keywords = new ArrayList<String>();
		ArrayList<String> tags = new ArrayList<String>();
		Calendar start_date = null;
		Calendar end_date = null;
		
		searchResults_ = storage_.search(keywords, tags, start_date, end_date);
	}
	
	private static void delete(int index) throws Exception {
		
		if ((index<1)||(index>searchResults_.size())) {
			throw new Exception("Invalid index");
		}
		
		Task deletedTask = searchResults_.get(index - 1);
		storage_.delete(deletedTask);
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
