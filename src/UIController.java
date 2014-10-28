import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.TreeMap;

import javafx.application.Application;
import javafx.stage.Stage;

import org.json.JSONException;

public class UIController extends Application implements UIObserver {

	private String inputCommand_;
	private Command currentCommand_ = null;
	private Command lastSearchCommand_ = null;
	private int taskID_;
	private UI UI_;
	private ArrayList<Task> searchResults_;
	private Parser parser_;
	private TreeMap<String, Task> taskIDmap_;
	private Storage storage_;
	private ArrayList<String> displayIDs_;

	public void proceedCommand() throws Exception {
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
			edit();
			return;
		}
		case LIST:
			list();
			lastSearchCommand_ = currentCommand_;
			return;
		}
	}

	private void display(String result) {
		UI_.setMessageToUser(result);
	}

	private void add() throws Exception {
		Task newTask = new Task();
		newTask.setTaskName(currentCommand_.getTaskName());

		if (currentCommand_.getTaskDueDate() != null) {
			newTask.setDate(currentCommand_.getTaskDueDate());
		}
		if (currentCommand_.getTaskTags() != null) {
			for (String tag : currentCommand_.getTaskTags()) {
				newTask.addTag(tag);
			}
		}

		storage_.insert(newTask);
		display("Added new task to Calendar: " + newTask.getTaskName());

		repeatLastSearch();
	}

	private void delete() throws Exception {
		String[] ids = currentCommand_.getTaskIDsToDelete();
		for (int i = 0; i < ids.length; i++) {
			String id = ids[i];
			delete(id);
		}
		repeatLastSearch();
	}

	private void delete(String id) throws IOException {
		if (!taskIDmap_.containsKey(id)) {
			display("Invalid index to delete: " + id);
		} else {
			Task task = taskIDmap_.get(id);
			delete(task);
			display("Deleted from Calendar: " + task.getTaskName());
		}
	}

	private void delete(Task task) throws IOException {
		storage_.delete(task);
	}

	private void edit() throws Exception {

		String id = currentCommand_.getTaskID();
		if (!taskIDmap_.containsKey(id)) {
			display("Invalid index to update.");
		} else {
			update(taskIDmap_.get(id));
		}

		repeatLastSearch();
	}

	private void update(Task task) throws JSONException, IOException, FileNotFoundException {
		storage_.delete(task);

		if (!currentCommand_.getTaskName().equals("")) {
			task.setTaskName(currentCommand_.getTaskName());
		}

		if (currentCommand_.getTaskDueDate() != null) {
			Calendar date = currentCommand_.getTaskDueDate();
			task.setDate(date);
		}
		
		storage_.insert(task);
	}

	private void repeatLastSearch() throws Exception {
		if (lastSearchCommand_ == null) {
			viewDefault();
		} else {
			currentCommand_ = lastSearchCommand_;
			proceedCommand();
		}
	}

	//default view
	private void viewDefault() {
		
		searchResults_ = new ArrayList<Task>();
		searchResults_.addAll(storage_.defaultView());

		createTaskIDmap();
		UI_.displayTasks(searchResults_);
	}
	
	//view all
	private void viewAll() {
		searchResults_ = new ArrayList<Task>();
		searchResults_.addAll(storage_.search(null, null, null, null));
		
		createTaskIDmap();
		UI_.displayTasks(searchResults_);
	}

	private void createDisplayIDs() {

		ArrayList<String> ids = new ArrayList<String>();

		for (int i = 0; i < searchResults_.size(); i++) {
			Task task = searchResults_.get(i);
			String id = getChar(task) + Integer.toString(i + 1);
			ids.add(id);
		}
		displayIDs_ = ids;
	}

	private void createTaskIDmap() {
		taskIDmap_ = new TreeMap<String, Task>();
		int f = 1;
		int r = 1;
		int t = 1;

		for (int i = 0; i < searchResults_.size(); i++) {
			Task task = searchResults_.get(i);
			String c = getChar(task);
			if (c.equals("r")) {
				String key = c + Integer.toString(r);
				taskIDmap_.put(key, task);
				task.setDisplayId(key);
				r++;
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

	private String getChar(Task task) {
		if (task.isOverdue()) {
			return "r";
		} else if (task.isFloating()) {
			return "f";
		} else {
			return "t";
		}
	}

	private void search() {
		ArrayList<String> keywords = new ArrayList<String>();
		ArrayList<String> tags = new ArrayList<String>();
		Calendar start_date = null;
		Calendar end_date = null;

		searchResults_ = storage_.search(keywords, tags, start_date, end_date);
	}

	private void list() {
		ArrayList<String> keywords = new ArrayList<String>();
		ArrayList<String> tags = new ArrayList<String>();
		Calendar start_date = currentCommand_.getSearchStartDate();
		Calendar end_date = currentCommand_.getSearchEndDate();

		searchResults_ = storage_.search(keywords, tags, start_date, end_date);
		createTaskIDmap();
		UI_.displayTasks(searchResults_);
	}

	@Override
	public void update() {
		inputCommand_ = UI_.getUserInput();

		currentCommand_ = parser_.parseCommand(inputCommand_);

		try {
			proceedCommand();
		} catch (Exception e) {
			UI_.setMessageToUser(e.getMessage());
		}
	}

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage stage) throws Exception {
		this.storage_ = new Storage();
		this.parser_ = new Parser();
		this.UI_ = new UI();

		UI_.addUIObserver(this);
		UI_.showStage(stage);
		viewDefault();
	}
}