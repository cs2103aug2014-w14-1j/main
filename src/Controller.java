import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
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
		
		viewToday();
		
		UI_.print("Please insert command: ");
		
		while (UI_.hasNextLine()) {
			
			inputCommand_= UI_.get();
			parser_.parseCommand(inputCommand_);
			
			if (!parser_.isValidCommand()) {
				UI_.println("Invalid command.");
			} else {
				currentCommand_ = parser_.getCommandObj();
				proceedCommand();
			}
			
			UI_.print("Please insert command: ");
		}
	}
	
	public static void proceedCommand() throws Exception {
		Command.COMMAND_TYPE commandType = currentCommand_.getCommandType();
		
		switch (commandType) {
		case ADD: {
			add();
			return;
			}
		
		case DELETE: {
			delete();
			return;
			}
		}	
	}

	public static void init() throws Exception {
		currentCommand_ =  null;
		lastCommand_ = null;
		UI_ = new Display();
		parser_ = new Parser();
		storage_ = new Storage();
	}
	
	private static void display(String result) {
		UI_.println(result);
	}
	
	private static void add() throws JSONException, IOException {
		Task newTask = new Task();
		newTask.setTaskName(currentCommand_.getTaskName());
		
		if (currentCommand_.getTaskDueDate()!= null) {
			newTask.addTaskDatesTimes(currentCommand_.getTaskDueDate());
		}
	
		storage_.insert(newTask);
		storage_.save();
		UI_.println("Added to Calendar: ");
		UI_.toDisplay(newTask);
		
		viewToday();
	}
	
	private static void delete() throws Exception {
		delete(1);
	}
	
	private static void delete(int index) throws IOException  {
		
		if ((index<1)||(index>searchResults_.size())) {
			UI_.println ("Invalid delete index");
		}
		
		Task deletedTask = searchResults_.get(index - 1);
		storage_.delete(deletedTask);
		storage_.save();
		
		UI_.println("Deleted from Calendar :");
		UI_.toDisplay(deletedTask);
		
		viewToday();
	}

	private static void viewToday() {
		ArrayList<String> keywords = new ArrayList<String>();
		ArrayList<String> tags = new ArrayList<String>();
		
		Calendar c = new GregorianCalendar();
		
		Calendar start_date = Calendar.getInstance();
		start_date.setTime(c.getTime());
		start_date.set(start_date.HOUR, 0);
		start_date.set(start_date.MINUTE, 0);
		start_date.set(start_date.SECOND, 0);
		
		Calendar end_date = Calendar.getInstance();
		end_date.setTime(c.getTime());
		end_date.set(end_date.HOUR, 23);
		end_date.set(end_date.MINUTE, 59);
		end_date.set(end_date.SECOND, 59);
		
		searchResults_ = storage_.search(keywords, tags, start_date, end_date);
		
		UI_.println("Today Tasks: ");
		UI_.toDisplay(searchResults_);
	}
	
	private static void search() {
		ArrayList<String> keywords = new ArrayList<String>();
		ArrayList<String> tags = new ArrayList<String>();
		Calendar start_date = null;
		Calendar end_date = null;
	
		searchResults_ = storage_.search(keywords, tags, start_date, end_date);
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
