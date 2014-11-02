import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.TreeMap;

import org.json.JSONException;

public class Controller {
	
	private static String inputCommand_;
	private static Command currentCommand_;
	private static Command lastCommand_;
	private static int taskID_;
	private static Display UI_;
	private static ArrayList<Task> searchResults_;
	private static Parser parser_;
	private static ArrayList<String> displayIDs_;
	private static TreeMap<String,Task> taskIDmap_;
	private static Storage storage_;
	
	public static void init() throws Exception {
		currentCommand_ =  null;
		lastCommand_ = null;
		UI_ = new Display();
		parser_ = new Parser();
		storage_ = new Storage();
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
		case EDIT: {
			update();
			return;
			}
		case LIST:
			list();
			return;
		}	
	}

	private static void display(Object result) {
		UI_.println(result);
	}
	
	private static void add() throws JSONException, IOException {
		Task newTask = new Task();
		newTask.setTaskName(currentCommand_.getTaskName());
		
		if (currentCommand_.getTaskDueDate()!= null) {
			newTask.setDate(currentCommand_.getTaskDueDate());
		}
	
		storage_.insert(newTask);
		UI_.println("Added to Calendar: ");
		UI_.toDisplay(newTask);
		
		viewToday();
	}
	
	private static void delete() throws Exception {
		String[] ids = currentCommand_.getTaskIDsToDelete();
		for (int i = 0; i < ids.length; i++) {
			String id = ids[i];
			delete(id);
		}
		viewToday();
	}
	
	private static void delete(String id) throws IOException {
		if (!taskIDmap_.containsKey(id)) {
			UI_.println("Invalid index to delete: " + id);
		} else {
			delete(taskIDmap_.get(id));
		}
	}
	
	private static void delete(Task task) throws IOException  {
		storage_.delete(task);
		UI_.println("Deleted from Calendar :");
		UI_.toDisplay(task);
	}
	
	private static void update() throws FileNotFoundException {
		
		String id = currentCommand_.getTaskID();
		if (!taskIDmap_.containsKey(id)) {
			UI_.println("Invalid index to update.");
		} else {
			update(taskIDmap_.get(id));
		}
		viewToday();
	}
	
	private static void update(Task task) throws FileNotFoundException {
		
		if (!currentCommand_.getTaskName().equals("")) {
			task.setTaskName(currentCommand_.getTaskName());
		}
		
		if (currentCommand_.getTaskDueDate()!=null) {
			Calendar date = currentCommand_.getTaskDueDate();
			task.setDate(date);
		}
	}
	private static void viewToday() {
		ArrayList<String> keywords = new ArrayList<String>();
		ArrayList<String> tags = new ArrayList<String>();
		
		Calendar c = new GregorianCalendar();
		
		Calendar start_date = Calendar.getInstance();
		start_date.setTime(c.getTime());
		start_date.set(start_date.HOUR, -12);
		start_date.set(start_date.MINUTE, 0);
		start_date.set(start_date.SECOND, 0);

		Calendar end_date = Calendar.getInstance();
		end_date.setTime(c.getTime());
		end_date.set(end_date.HOUR, 11);
		end_date.set(end_date.MINUTE, 59);
		end_date.set(end_date.SECOND, 59);
	
		
		searchResults_ = storage_.search(keywords, tags, start_date, end_date);
		
		UI_.println("Today Tasks: ");
		
		createTaskIDmap();
		UI_.toDisplay(searchResults_);
	}
	
	private static void createDisplayIDs() {
		
		ArrayList<String> ids = new ArrayList<String>();
		
		for (int i=0; i < searchResults_.size(); i++) {
			Task task = searchResults_.get(i);
			String id = getChar(task) + Integer.toString(i+1);
			ids.add(id);
		}
		displayIDs_ = ids;
	}
	
	private static void createTaskIDmap() {
		taskIDmap_ = new TreeMap<String,Task>(); 
		int f = 1;
		int o = 1;
		int t = 1;
		
		for (int i=0; i<searchResults_.size(); i++) {
			Task task = searchResults_.get(i);
			String c = getChar(task);
			if (c.equals("o")) {
				String key = c + Integer.toString(o);
				taskIDmap_.put(key, task);
				task.setDisplayId(key);
				o++;
			} else if (c.equals("t")) {
				String key = c + Integer.toString(t);
				taskIDmap_.put(key, task);
				task.setDisplayId(key);
				t++;
			} else {
				String key = c + Integer.toString(f);
				taskIDmap_.put(key, task);
				task.setDisplayId(key);
				f++;
			}
		}
	}
	
	private static String getChar(Task task) {
		if (task.isOverdue()) {
			return "o";
		} else if (task.isFloating()) {
			return "f";
		} else {
			return "t";
		}
	}
	
	private static void search() {
		ArrayList<String> keywords = new ArrayList<String>();
		ArrayList<String> tags = new ArrayList<String>();
		Calendar start_date = null;
		Calendar end_date = null;
	
		searchResults_ = storage_.search(keywords, tags, start_date, end_date);
	}
	
	private static void list() {
		ArrayList<String> keywords = new ArrayList<String>();
		ArrayList<String> tags = new ArrayList<String> ();
		Calendar start_date = currentCommand_.getSearchStartDate();
		Calendar end_date = currentCommand_.getSearchEndDate();
		
		searchResults_ = storage_.search(keywords, tags, start_date, end_date);
		createTaskIDmap();
		UI_.toDisplay(searchResults_);
	}

	public static void main(String args[]) throws Exception {
		
		init();
		
		viewToday();
		
		UI_.print("Please insert command: ");
		
		
		while (UI_.hasNextLine()) {
			
			inputCommand_= UI_.get();
			currentCommand_ = parser_.parseCommand(inputCommand_);

			proceedCommand();
	
			UI_.print("Please insert command: ");
		}
	}
}
