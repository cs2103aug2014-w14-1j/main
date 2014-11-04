import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.File;
import java.io.PrintWriter;
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
	
	//Test variables********************************************************
	
	private static final String TEST_TASK_FILENAME = "systestt.txt";
	private static final String TEST_FLOATING_TASK_FILENAME = "systestf.txt";
	private static final String TEST_OVERDUE_TASK_FILENAME = "systesto.txt";
	private static final String TEST_COMPLETED_TASK_FILENAME = "systestc.txt";
	
	private static final String TEST_INPUT_FILENAME = "systestinput.txt";
	private static final String TEST_EXPECTED_FILENAME = "systestexpected.txt";
	
	private ArrayList<Task> t_searchResults;
	private Parser t_parser;
	private TreeMap<String, Task> t_taskIDmap;
	private Storage t_storage;
	private LogicHandler t_logic;
	private SearchHandler t_searcher;
	
	//Methods**************************************************************

	public void proceedCommand(Command command) throws Exception {
		if (isTest(command)) {
			String msg = runSystemTest();
			display(msg);
		}
		else if (isLogic(command)) {
			String msg = logic_.executeCommand(taskIDmap_, command);
			display(msg);
			repeatLastSearch();
		} else {
			searchResults_ = searcher_.proceedCommand(command);
			createTaskIDmap();
			display(searchResults_.size() + " tasks found");
			UI_.displayTasks(searchResults_);
		}
	}
	
	private boolean isTest(Command command) {
		return command.getCommandType() == Command.COMMAND_TYPE.TEST;
	}
	
	private boolean isLogic(Command command) {
		assert command.getCommandType() != Command.COMMAND_TYPE.TEST;
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
	
	private void createTaskIDmap() {
		taskIDmap_ = new TreeMap<String, Task>();
		int id_number = 1;

		for (int i = 0; i < searchResults_.size(); i++) {
			Task task = searchResults_.get(i);
			String key = getChar(task) + Integer.toString(id_number);
			taskIDmap_.put(key, task);
			task.setDisplayId(key);
			id_number++;
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
		repeatLastSearch();
		display("Welcome to SPEED!");
	}
	
	//System Test****************************************************************************
	
	private String runSystemTest() {
		try {
			t_parser = new Parser();
			t_storage = new Storage(TEST_TASK_FILENAME, TEST_FLOATING_TASK_FILENAME,
					TEST_OVERDUE_TASK_FILENAME, TEST_COMPLETED_TASK_FILENAME);
			t_logic = new LogicHandler(t_storage);
			t_searcher = new SearchHandler(t_storage);
			
			t_searchResults = t_searcher.viewDefault();
			t_taskIDmap = new TreeMap<String, Task>();
			createTestTaskIDmap();
			
			BufferedReader t_reader = new BufferedReader(new FileReader(new File(TEST_INPUT_FILENAME)));
			String input = null;
			while ((input = t_reader.readLine()) != null) {
				Command t_command = t_parser.parseCommand(input);
				assert t_command.getCommandType() != Command.COMMAND_TYPE.TEST;
				if (isLogic(t_command)) {
					t_logic.executeCommand(t_taskIDmap, t_command);
					t_searchResults = t_searcher.repeatLastSearch();
					createTestTaskIDmap();
				}
				else {
					t_searchResults = t_searcher.proceedCommand(t_command);
					createTestTaskIDmap();
				}
			}
			
			t_reader.close();
			
			//check output
			BufferedReader e_reader = new BufferedReader(new FileReader(new File(TEST_EXPECTED_FILENAME)));
			
			File t_file = new File(TEST_TASK_FILENAME);
			t_reader = new BufferedReader(new FileReader(t_file));
			try {
				testFile(t_reader, e_reader);
			}
			catch (Exception e) {
				t_reader.close();
				e_reader.close();
				t_storage.clearAll();
				t_file.delete();
				return e.getMessage();
			}
			t_file.delete();
			t_reader.close();
			
			t_file = new File(TEST_FLOATING_TASK_FILENAME);
			t_reader = new BufferedReader(new FileReader(t_file));
			try {
				testFile(t_reader, e_reader);
			}
			catch (Exception e) {
				t_reader.close();
				e_reader.close();
				t_storage.clearAll();
				t_file.delete();
				return e.getMessage();
			}
			t_file.delete();
			t_reader.close();
			
			t_file = new File(TEST_OVERDUE_TASK_FILENAME);
			t_reader = new BufferedReader(new FileReader(t_file));
			try {
				
				testFile(t_reader, e_reader);
			}
			catch (Exception e) {
				t_reader.close();
				e_reader.close();
				t_storage.clearAll();
				t_file.delete();
				return e.getMessage();
			}
			t_file.delete();
			t_reader.close();
			
			t_file = new File(TEST_COMPLETED_TASK_FILENAME);
			t_reader = new BufferedReader(new FileReader(t_file));
			try {
				testFile(t_reader, e_reader);
			}
			catch (Exception e) {
				t_reader.close();
				e_reader.close();
				t_storage.clearAll();
				t_file.delete();
				return e.getMessage();
			}
			t_file.delete();
			t_reader.close();
			e_reader.close();
			t_storage.clearAll();
			return "Tests successful!";
		}
		catch (Exception e) {
			return e.getMessage();
		}
	}
	
	private void testFile(BufferedReader t_reader, BufferedReader e_reader) throws Exception {
		String input;
		while ((input = t_reader.readLine()) != null) {
			testOneLine(input, e_reader.readLine());
		}
	}
	
	private void testOneLine(String actual, String expected) throws Exception {
		if (!actual.equals(expected)) {
			throw new Exception("MISMATCH: " + actual + " - " + expected);
		}
	}
	
	private void createTestTaskIDmap() {
		t_taskIDmap = new TreeMap<String, Task>();
		int f = 1;
		int o = 1;
		int t = 1;

		for (int i = 0; i < t_searchResults.size(); i++) {
			Task task = t_searchResults.get(i);
			String c = getChar(task);
			if (c.equals("o")) {
				String key = c + Integer.toString(o);
				t_taskIDmap.put(key, task);
				task.setDisplayId(key);
				o++;
			} else if (c.equals("t")) {
				String key = c + Integer.toString(t);
				t_taskIDmap.put(key, task);
				task.setDisplayId(key);
				t++;
			} else {
				String key = c + Integer.toString(f);
				t_taskIDmap.put(key, task);
				task.setDisplayId(key);
				f++;
			}
		}
	}

}