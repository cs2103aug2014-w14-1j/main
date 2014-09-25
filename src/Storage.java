import java.util.*;
import java.io.*;

import org.json.JSONException;

import com.google.gson.Gson;

public class Storage {
	private ArrayList<Task> al_task;
	private ArrayList<Task> al_task_floating;
	private ArrayList<Task> al_task_overdue;
	private ArrayList<Task> al_task_completed;
	private int counter;

	private BufferedReader bufferedReader;
	private PrintWriter printWriter;
	private static final String FLOATING_TASK_FILENAME = "C:\\Users\\Daryl\\git\\2103Proj\\src\\FloatingTask.txt";
	private static final String COMPLETED_TASK_FILENAME = "C:\\Users\\Daryl\\git\\2103Proj\\src\\CompletedTask.txt";
	private static final String OVERDUE_TASK_FILENAME = "C:\\Users\\Daryl\\git\\2103Proj\\src\\OverdueTask.txt";
	private static final String TASK_FILENAME = "C:\\Users\\Daryl\\git\\2103Proj\\src\\Task.txt";
	private static final String COUNT_TASK_FILENAME = "C:\\Users\\Daryl\\git\\2103Proj\\src\\TaskCount.txt";

	// Index for if the task does not exist in the ArrayList.
	private static final int DOES_NOT_EXIST = -1;

	public Storage() throws IOException {
		al_task = new ArrayList<Task>();
		al_task_floating = new ArrayList<Task>();
		al_task_overdue = new ArrayList<Task>();
		al_task_completed = new ArrayList<Task>();
		initFiles();
	}

	// if exists, replace task. Else add task.
	public void insert(Task task, ArrayList<Task> file) throws JSONException,
			IOException {
		readFile(file);
		int taskIndex = getIndex(file, task);

		// add
		if (taskIndex == DOES_NOT_EXIST) {
			counter = Integer.parseInt(getTaskCounter()) + 1;
			task.setTaskId(counter);
			file.add(task);
			writeTaskCounter(counter);
		}
		// insert
		else {
			file.set(taskIndex, task);
		}
		writeFile(file);
	}

	public void delete(Task task, ArrayList<Task> file) throws IOException {
		readFile(file);
		int taskIndex = getIndex(file, task);
		if (taskIndex != DOES_NOT_EXIST) {
			file.remove(taskIndex);
		}
		writeFile(file);
	}

	public ArrayList<Task> getTasksFile() {
		return this.al_task;
	}

	public ArrayList<Task> getFloatingTasksFile() {
		return this.al_task_floating;
	}

	public ArrayList<Task> getCompletedTasksFile() {
		return this.al_task_completed;
	}

	public ArrayList<Task> getOverdueTasksFile() {
		return this.al_task_overdue;
	}
	
	//Methods Not Accessible to Storage instance.****************************
	private void initFiles() throws IOException {
		readFile(al_task);
		readFile(al_task_floating);
		readFile(al_task_completed);
		readFile(al_task_overdue);
	}

	private int getIndex(ArrayList<Task> file, Task task) {
		for (int i = 0; i < file.size(); i++) {
			if (task.getTaskId().equals(file.get(i).getTaskId())) {
				return i;
			}
		}
		return DOES_NOT_EXIST;
	}

	// Interfaces with the textFiles(databases)*************************
	private String getTaskCounter() throws IOException {
		bufferedReader = new BufferedReader(new FileReader(COUNT_TASK_FILENAME));
		
		String result = bufferedReader.readLine();
		if(result == null){
			return "0";
		}
		else{
			return result;
		}
	}

	private void writeTaskCounter(int count) throws FileNotFoundException {
		printWriter = new PrintWriter(new FileOutputStream(COUNT_TASK_FILENAME));
		printWriter.println("" + count);
		printWriter.close();
	}

	private void readFile(ArrayList<Task> file) throws IOException {
		String fileName = determineFileName(file);
		bufferedReader = new BufferedReader(new FileReader(fileName));
		Gson gson = new Gson();
		String line = null;
		file.removeAll(file);
		while ((line = bufferedReader.readLine()) != null) {
			Task task = gson.fromJson(line, Task.class);
			file.add(task);
		}

	}

	private void writeFile(ArrayList<Task> fileToWrite)
			throws FileNotFoundException {

		String filename = determineFileName(fileToWrite);
		// Start writing
		printWriter = new PrintWriter(new FileOutputStream(filename));
		Gson gson = new Gson();
		for (Task task : fileToWrite) {
			String write = gson.toJson(task);
			printWriter.println(write);
		}
		printWriter.close();
	}

	// Interfaces with the textFiles(databases)*************************
	private String determineFileName(ArrayList<Task> fileToWrite) {
		String filename = "";
		if (fileToWrite.equals(al_task)) {
			filename = TASK_FILENAME;
		} else if (fileToWrite.equals(al_task_floating)) {
			filename = FLOATING_TASK_FILENAME;
		} else if (fileToWrite.equals(al_task_completed)) {
			filename = COMPLETED_TASK_FILENAME;
		} else if (fileToWrite.equals(al_task_overdue)) {
			filename = OVERDUE_TASK_FILENAME;
		} else {
			throw new Error("Invalid file to write to");
		}
		return filename;
	}
}
