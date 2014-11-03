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

public class MainController extends Application implements UIObserver {

	private static final String TASK_FILENAME = "Task.txt";
	private static final String FLOATING_TASK_FILENAME = "FloatingTask.txt";
	private static final String OVERDUE_TASK_FILENAME = "OverdueTask.txt";
	private static final String COMPLETED_TASK_FILENAME = "CompletedTask.txt";
	
	private String inputCommand_;
	private Command currentCommand_ = null;
	private UI UI_;
	private ArrayList<Task> searchResults_;
	private Parser parser_;
	private TreeMap<String, Task> taskIDmap_;
	private Storage storage_;
	private LogicHandler logic_;
	private SearchHandler searcher_;

	public void proceedCommand(Command command) throws Exception {

		if (isLogic(command)) {
			String msg = logic_.executeCommand(taskIDmap_, command);
			display(msg);
			repeatLastSearch();
		} else {
			searchResults_ = searcher_.proceedCommand(command);
			createTaskIDmap();
			UI_.displayTasks(searchResults_);
		}
	}
	
	private boolean isLogic(Command command) {
		if ((command.getCommandType() == Command.COMMAND_TYPE.SEARCH) | (command.getCommandType() == Command.COMMAND_TYPE.LIST)) {
			return false;
		} 
		return true;
	}

	private void display(String result) {
		UI_.setNotificationToUser(result);
	}

	private void repeatLastSearch() throws Exception {
		searchResults_ = searcher_.repeatLastSearch();
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

	private void createTaskIDmap() {
		taskIDmap_ = new TreeMap<String, Task>();
		int f = 1;
		int o = 1;
		int t = 1;

		for (int i = 0; i < searchResults_.size(); i++) {
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

	private String getChar(Task task) {
		if (task.isOverdue()) {
			return "O";
		} else if (task.isFloating()) {
			return "F";
		} else {
			return "T";
		}
	}

	@Override
	public void update() {
		inputCommand_ = UI_.getUserInput();
		currentCommand_ = parser_.parseCommand(inputCommand_);

		try {
			proceedCommand(currentCommand_);
		} catch (Exception e) {
			UI_.setNotificationToUser(e.getMessage());
		}
	}

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage stage) throws Exception {
		this.storage_ = new Storage(TASK_FILENAME,FLOATING_TASK_FILENAME,OVERDUE_TASK_FILENAME,COMPLETED_TASK_FILENAME);
		this.parser_ = new Parser();
		this.UI_ = new UI();
		this.logic_ = new LogicHandler(storage_);
		this.searcher_ = new SearchHandler(storage_);

		UI_.addUIObserver(this);
		UI_.showStage(stage);
		viewAll();
	}
}