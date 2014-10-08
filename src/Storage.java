import java.util.*;
import java.io.*;

import org.json.JSONException;

import com.google.gson.Gson;


public class Storage {
	private ArrayList<Task> al_task;
	private ArrayList<Task> al_task_floating;
	private ArrayList<Task> al_task_overdue;
	private ArrayList<Task> al_task_completed;
	private ArrayList<Task> al_task_recurring;
	private FileHandler filehandler;
	private int counter;

	// Index for if the task does not exist in the ArrayList.
	private static final int DOES_NOT_EXIST = -1;

	public Storage() throws IOException {
		al_task = new ArrayList<Task>();
		al_task_floating = new ArrayList<Task>();
		al_task_overdue = new ArrayList<Task>();
		al_task_completed = new ArrayList<Task>();
		al_task_recurring = new ArrayList<Task>();
		filehandler = new FileHandler();
		initFiles();
		checkForOverdueTasks();
	}
	
	public void insert(Task task) throws JSONException, IOException {
		if (task.isTaskFloating()) {
			insert(task, al_task_floating);
		}
		//recurring
		else if (task.isRecur()) {
			insert(task, al_task_recurring);
		}
		//overdue
		else {
			insert(task, al_task);
		}
	}

	// if exists, replace task. Else add task.
	private void insert(Task task, ArrayList<Task> file) throws JSONException,
			IOException {
		filehandler.readFile(file);
		int taskIndex = getIndex(file, task);
		// add
		if (taskIndex == DOES_NOT_EXIST) {
			counter = Integer.parseInt(filehandler.getTaskCounter()) + 1;
			task.setTaskId(counter);
			file.add(task);
			filehandler.writeTaskCounter(counter);
		}
		// insert
		else {
			file.set(taskIndex, task);
		}
		filehandler.writeFile(file);
	}
	
	public void delete(Task task) throws IOException{
		if (task.isTaskFloating()) {
			delete(task, al_task_floating);
		}
		//recurring
		else if (task.isRecur()) {
			delete(task, al_task_recurring);
		}
		//overdue
		else {
			delete(task, al_task);
		}
		
	}

	private void delete(Task task, ArrayList<Task> file) throws IOException {
		filehandler.readFile(file);
		int taskIndex = getIndex(file, task);
		if (taskIndex != DOES_NOT_EXIST) {
			file.remove(taskIndex);
		}
		filehandler.writeFile(file);
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
	
	public ArrayList<Task> getRecurringTasksFile() {
		return this.al_task_recurring;
	}
	
	//Search method**********************************
	
	//returns only 1 task as Id is unique. Return null if empty.
	public Task searchTaskByID(String id){
		ArrayList<Task> search_results = new ArrayList<Task>();
		
		searchForID(id, al_task, search_results);
		searchForID(id, al_task_floating, search_results);
		searchForID(id, al_task_overdue, search_results);
		searchForID(id, al_task_recurring, search_results);
		
		if(search_results.isEmpty()){
			return null;
		}
		//only one item so index 0.
		return search_results.get(0);		
	}
	
	private ArrayList<Task> searchForID(String id,ArrayList<Task> list, ArrayList<Task> resultsList){
		for(Task task: list){
			if(task.getTaskId().equals(id)){
				resultsList.add(task);
			}
		}
		
		return resultsList;
	}
	/*
	 * Driver search method. Search for all tasks with the specified parameters
	 * Current limitations:
	 * -Can only search 1 keyword and 1 tag
	 * -Can only search a date range
	 * -Missing default start and end date
	 */
	public ArrayList<Task> search(ArrayList<String> keywords, ArrayList<String> tags, Calendar start_date, Calendar end_date) {
		ArrayList<Task> search_results = new ArrayList<Task>();
				
		searchList(search_results, al_task, keywords, tags, start_date, end_date);
		searchList(search_results, al_task_floating, keywords, tags, start_date, end_date);
		searchList(search_results, al_task_overdue, keywords, tags, start_date, end_date);
		searchList(search_results, al_task_recurring, keywords, tags, start_date, end_date);
		
		return search_results;
	}
	
	/*
	 * Search function which only searches within a specified list and adds it
	 * to an input search list. Assumes all parameters are given
	 */
	private void searchList(ArrayList<Task> search_result, ArrayList<Task> list,
			ArrayList<String> keywords, ArrayList<String> tags, Calendar start_date, Calendar end_date) {
		
		for (Task task : list) {
			if (task.withinDateRange(start_date, end_date)) {
				if ( task.containsKeywords(keywords) && task.containsTags(tags) ) {
					search_result.add(task);
				}
			}
		}
	}
	
	//Clear method**********************************
	
	public void clearAll() throws FileNotFoundException {
		clear(al_task);
		clear(al_task_floating);
		clear(al_task_overdue);
		clear(al_task_recurring);
		save();
	}
	
	private void clear(ArrayList<Task> filelist) {
		filelist.clear();
	}
	
	public void save() throws FileNotFoundException {
		filehandler.writeFile(al_task);
		filehandler.writeFile(al_task_floating);
		filehandler.writeFile(al_task_overdue);
		filehandler.writeFile(al_task_recurring);
	}
	
	//*********************
	
	private void checkForOverdueTasks() throws FileNotFoundException {
		Calendar now = Calendar.getInstance();
		ArrayList<Task> task_to_overdue = new ArrayList<Task>();
		for (Task task : al_task) {
			for (Calendar date : task.getTaskDatesTimes()) {
				if ( task.getTaskDateCompleted() == null ||
						(date.after(task.getTaskDateCompleted()) && date.before(now)) ) {
					if (!task_to_overdue.contains(task)) {
						task_to_overdue.add(task);
					}
				}
			}
		}
		al_task_overdue.addAll(task_to_overdue);
		al_task.removeAll(task_to_overdue);
		save();
	}
	
	//Methods Not Accessible to Storage instance.****************************
	private void initFiles() throws IOException {
		filehandler.readFile(al_task);
		filehandler.readFile(al_task_floating);
		filehandler.readFile(al_task_completed);
		filehandler.readFile(al_task_overdue);
		filehandler.readFile(al_task_recurring);
	}

	private int getIndex(ArrayList<Task> file, Task task) {
		for (int i = 0; i < file.size(); i++) {
			if (task.getTaskId().equals(file.get(i).getTaskId())) {
				return i;
			}
		}
		return DOES_NOT_EXIST;
	}


	
	
	class FileHandler {
		
		private BufferedReader bufferedReader;
		private PrintWriter printWriter;
		private static final String FLOATING_TASK_FILENAME = "FloatingTask.txt";
		private static final String COMPLETED_TASK_FILENAME = "CompletedTask.txt";
		private static final String OVERDUE_TASK_FILENAME = "OverdueTask.txt";
		private static final String TASK_FILENAME = "Task.txt";
		private static final String RECURRING_TASK_FILENAME = "RecurringTask.txt";
		private static final String COUNT_TASK_FILENAME = "TaskCount.txt";
		
		// Interfaces with the textFiles(databases)*************************
		private String getTaskCounter() throws IOException {
			File countFile = new File(COUNT_TASK_FILENAME);
			if (!countFile.exists()) {
				countFile.createNewFile();
			}
			bufferedReader = new BufferedReader(new FileReader(countFile));
			
			String result = bufferedReader.readLine();
			if(result == null){
				return "0";
			}
			else{
				return result;
			}
		}

		private void writeTaskCounter(int count) throws FileNotFoundException, IOException {
			File countFile = new File(COUNT_TASK_FILENAME);
			if (!countFile.exists()) {
				countFile.createNewFile();
			}
			printWriter = new PrintWriter(new FileOutputStream(countFile));
			printWriter.println("" + count);
			printWriter.close();
		}

		private void readFile(ArrayList<Task> filelist) throws IOException {
			String fileName = determineFileName(filelist);
			File file = new File(fileName);
			if (!file.exists()) {
				file.createNewFile();
			}
			bufferedReader = new BufferedReader(new FileReader(file));
			Gson gson = new Gson();
			String line = null;
			filelist.removeAll(filelist);
			while ((line = bufferedReader.readLine()) != null) {
				Task task = gson.fromJson(line, Task.class);
				filelist.add(task);
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
			if (fileToWrite == al_task) {
				filename = TASK_FILENAME;
			} else if (fileToWrite == al_task_floating) {
				filename = FLOATING_TASK_FILENAME;
			} else if (fileToWrite == al_task_completed) {
				filename = COMPLETED_TASK_FILENAME;
			} else if (fileToWrite == al_task_overdue) {
				filename = OVERDUE_TASK_FILENAME;
			} else if (fileToWrite == al_task_recurring) {
				filename = RECURRING_TASK_FILENAME;
			} else {
				throw new Error("Invalid file to write to");
			}
			return filename;
		}
	}
	
}
