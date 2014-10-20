import java.util.*;
import java.io.*;

import org.json.JSONException;

import com.google.gson.Gson;


public class Storage {
	
	private ArrayList<Task> al_task;
	private ArrayList<Task> al_task_floating;
	private ArrayList<Task> al_task_overdue;
	private ArrayList<Task> al_task_completed;
	private FileHandler filehandler;
	private int counter;

	public Storage() throws IOException {
		al_task = new ArrayList<Task>();
		al_task_floating = new ArrayList<Task>();
		al_task_overdue = new ArrayList<Task>();
		al_task_completed = new ArrayList<Task>();
		filehandler = new FileHandler();
		initFiles();
		checkForOverdueTasks();
	}

	//Insert methods*********************************************************
	
	/*
	 * Assumption: This task is unique and does not already exist inside the
	 * lists. If it was an existing task, it should have been deleted
	 */
	
	public void insert(Task task) throws JSONException, IOException {
		insert(task, retrieveTaskList(task));
		save();
	}

	private void insert(Task task, ArrayList<Task> list) throws JSONException,
			IOException {
		
		if (task.hasNoID()) {
			assignID(task);
		}
		list.add(task);
		
	}
	
	private void assignID(Task task) throws IOException {
		counter = Integer.parseInt(filehandler.getTaskCounter()) + 1;
		task.setTaskId(counter);
		filehandler.writeTaskCounter(counter);
	}
	
	//Delete methods***************************************************
	
	/*
	 * Assumption: Task either does not exist in any of the lists, or exists
	 * only in 1 list. Insertion should not insert any duplicates
	 */
	
	public void delete(Task task) throws IOException{
		delete(task, retrieveTaskList(task));
		save();
	}

	private void delete(Task task, ArrayList<Task> list) throws IOException {
		list.remove(task);
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
	
	//Search methods**********************************
	
	/*
	 * Assumption: All tasks are unique. Only one result should be found
	 */
	public Task searchTaskByID(String id){
		ArrayList<Task> search_results = new ArrayList<Task>();
		
		searchForID(id, al_task, search_results);
		searchForID(id, al_task_floating, search_results);
		searchForID(id, al_task_overdue, search_results);
		
		if(search_results.isEmpty()){
			return null;
		}
		
		assert search_results.size() == 1;
		
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
				
		searchList(search_results, al_task_overdue, keywords, tags, start_date, end_date);
		searchList(search_results, al_task_floating, keywords, tags, start_date, end_date);
		searchList(search_results, al_task, keywords, tags, start_date, end_date);
		
		
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
	
	//Clear methods**************************************************************
	
	public void clearAll() throws FileNotFoundException {
		clear(al_task);
		clear(al_task_floating);
		clear(al_task_overdue);
		clear(al_task_completed);
		save();
	}
	
	private void clear(ArrayList<Task> filelist) {
		filelist.clear();
	}
	
	//Save methods**********************************************************
	
	private void save() throws FileNotFoundException {
		filehandler.writeFile(al_task);
		filehandler.writeFile(al_task_floating);
		filehandler.writeFile(al_task_overdue);
		filehandler.writeFile(al_task_completed);
	}
	
	//Miscellaneous methods*************************************************
	
	private void checkForOverdueTasks() throws FileNotFoundException {
		ArrayList<Task> task_to_overdue = new ArrayList<Task>();
		for (Task task : al_task) {
			if (task.isOverdue()) {
				task_to_overdue.add(task);
			}
		}
		al_task_overdue.addAll(task_to_overdue);
		al_task.removeAll(task_to_overdue);
		save();
	}
	
	private void initFiles() throws IOException {
		filehandler.readFile(al_task);
		filehandler.readFile(al_task_floating);
		filehandler.readFile(al_task_completed);
		filehandler.readFile(al_task_overdue);
	}

	private ArrayList<Task> retrieveTaskList(Task task) {
		if (task.isFloating()) {
			return al_task_floating;
		}
		else if (task.isOverdue()) {
			return al_task_overdue;
		}
		else if (task.isCompleted()) {
			return al_task_completed;
		}
		return al_task;
	}
	
	//File Operations********************************************************
	
	class FileHandler {
		
		private BufferedReader bufferedReader;
		private PrintWriter printWriter;
		private static final String FLOATING_TASK_FILENAME = "FloatingTask.txt";
		private static final String COMPLETED_TASK_FILENAME = "CompletedTask.txt";
		private static final String OVERDUE_TASK_FILENAME = "OverdueTask.txt";
		private static final String TASK_FILENAME = "Task.txt";
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
			} else {
				throw new Error("Invalid file to write to");
			}
			return filename;
		}
	}
	
}
