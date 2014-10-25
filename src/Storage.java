import java.util.*;
import java.io.*;

import org.json.JSONException;

import com.google.gson.Gson;


public class Storage {
	
	private static final int RECUR_YEAR_LIMIT = 3;
	private static final int RECUR_INTERVAL = 1;
	
	private PriorityQueue<Task> al_task;
	private PriorityQueue<Task> al_task_floating;
	private PriorityQueue<Task> al_task_overdue;
	private PriorityQueue<Task> al_task_completed;
	private FileHandler filehandler;
	private int id_counter;

	public Storage() throws IOException {
		al_task = new PriorityQueue<Task>(new TaskComparator());
		al_task_floating = new PriorityQueue<Task>(new TaskComparator());
		al_task_overdue = new PriorityQueue<Task>(new TaskComparator());
		al_task_completed = new PriorityQueue<Task>(new TaskComparator());
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
		if (task.hasNoID()) {
			assignID(task);
		}
		insert(task, retrieveTaskList(task));
		
		//is this is a recurring task, generate copies and insert them too
		if (task.isRecur()) {
			generateRecurringTasks(task);
		}
		save();
	}

	private void insert(Task task, PriorityQueue<Task> list) throws JSONException,
			IOException {
		list.add(task);
	}
	
	private void assignID(Task task) {
		id_counter += 1;
		task.setId(id_counter);
	}
	
	private void generateRecurringTasks(Task task) throws IOException, JSONException {
		Calendar limit = task.getRecurLimit();
		if (limit == null) {							//there is no limit: use default
			limit = getDefaultLimit();
		}
		while (task.getEndDate().before(limit)) {		//assumption: recur must have enddate
			task = task.clone();
			Calendar start = task.getStartDate();
			if (start != null) {
				start.add(task.getRecur(), RECUR_INTERVAL);
			}
			Calendar end = task.getEndDate();
			if (end != null) {
				end.add(task.getRecur(), RECUR_INTERVAL);
				if (end.after(limit)) {
					break;
				}
			}
			//increase date
			task.setDates(start, end);
			insert(task, retrieveTaskList(task));
		}
	}
	
	//Get default limit, equal to time of insertion + RECUR_YEAR_LIMIT
	private Calendar getDefaultLimit() {
		Calendar limit = Calendar.getInstance();
		limit.add(Calendar.YEAR, RECUR_YEAR_LIMIT);
		return limit;
	}
	
	//Delete methods***************************************************
	
	/*
	 * Assumption: Task either does not exist in any of the lists, or exists
	 * only in 1 list. Insertion should not insert any duplicates
	 */
	
	public void delete(Task task) throws IOException{
		delete(task, retrieveTaskList(task));
		if (task.isRecur()) {
			deleteRecurChain(task);
		}
		save();
	}

	private void delete(Task task, PriorityQueue<Task> list) throws IOException {
		list.remove(task);
	}
	
	private void deleteRecurChain(Task task) throws IOException {
		ArrayList<Task> recur_chain = searchTaskByID(task.getId());
		for (Task other_task : recur_chain) {
			delete(other_task, retrieveTaskList(other_task));
		}
	}
	
	//Retrieval/search methods**********************************
	
	public ArrayList<Task> getTasksList() {
		ArrayList<Task> task = new ArrayList<Task>();
		Iterator<Task> iter = al_task.iterator();
		while (iter.hasNext()) {
			task.add(iter.next());
		}
		return task;
	}

	public ArrayList<Task> getFloatingTasksList() {
		ArrayList<Task> float_task = new ArrayList<Task>();
		Iterator<Task> iter = al_task_floating.iterator();
		while (iter.hasNext()) {
			float_task.add(iter.next());
		}
		return float_task;
	}

	public ArrayList<Task> getCompletedTasksList() {
		ArrayList<Task> completed_task = new ArrayList<Task>();
		Iterator<Task> iter = al_task_completed.iterator();
		while (iter.hasNext()) {
			completed_task.add(iter.next());
		}
		return completed_task;
	}

	public ArrayList<Task> getOverdueTasksList() {
		ArrayList<Task> overdue_task = new ArrayList<Task>();
		Iterator<Task> iter = al_task_overdue.iterator();
		while (iter.hasNext()) {
			overdue_task.add(iter.next());
		}
		return overdue_task;
	}
	
	/*
	 * Assumption: All tasks are unique. Only one result should be found
	 */
	public ArrayList<Task> searchTaskByID(Integer id){
		
		ArrayList<Task> search_results = new ArrayList<Task>();
		if (id == null) {
			return search_results;		//every task in storage should have an ID
		}
		
		searchForID(id, al_task, search_results);
		searchForID(id, al_task_floating, search_results);
		searchForID(id, al_task_overdue, search_results);
		
		if(search_results.isEmpty()){
			return null;
		}
		
		return search_results;
	}
	
	private ArrayList<Task> searchForID(Integer id,PriorityQueue<Task> list, ArrayList<Task> resultsList){
		for(Task task: list){
			if(task.getId() == id){
				resultsList.add(task);
			}
		}
		
		return resultsList;
	}
	
	/*
	 * Driver search method. Search for all tasks with the specified parameters
	 */
	public ArrayList<Task> search(ArrayList<String> keywords, ArrayList<String> tags, Calendar start_date, Calendar end_date) {
		ArrayList<Task> search_results = new ArrayList<Task>();
				
		searchList(search_results, al_task_overdue, keywords, tags, start_date, end_date);
		searchList(search_results, al_task_floating, keywords, tags, start_date, end_date);
		searchList(search_results, al_task, keywords, tags, start_date, end_date);
		
		Collections.sort(search_results, new TaskComparator());
		return search_results;
	}
	
	/*
	 * Search function which only searches within a specified list and adds it
	 * to an input search list. Assumes all parameters are given
	 */
	
	private void searchList(ArrayList<Task> search_result, PriorityQueue<Task> list,
			ArrayList<String> keywords, ArrayList<String> tags, Calendar start_date, Calendar end_date) {
		
		for (Task task : list) {
			if (task.withinDateRange(start_date, end_date)) {
				if ( task.containsKeywords(keywords) && task.containsTags(tags) ) {
					search_result.add(task);
				}
			}
		}
	}

	private ArrayList<Task> searchTaskByReminder(Calendar start_date, Calendar end_date) {
		ArrayList<Task> search_results = new ArrayList<Task>();
		searchTaskByReminder(search_results, al_task_overdue, start_date, end_date);
		searchTaskByReminder(search_results, al_task_floating, start_date, end_date);
		searchTaskByReminder(search_results, al_task, start_date, end_date);
		Collections.sort(search_results, new TaskComparator());
		return search_results;
	}
	
	private void searchTaskByReminder(ArrayList<Task> search_results, PriorityQueue<Task> tasklist,
			Calendar start_date, Calendar end_date) {
		for (Task task : tasklist) {
			if (task.getReminderDate().after(start_date) || task.getReminderDate().equals(start_date)) {
				if (task.getReminderDate().before(end_date) || task.getReminderDate().equals(end_date)) {
					search_results.add(task);
				}
			}
		}
	}


	public ArrayList<Task> defaultView() {
		ArrayList<Task> search_results = new ArrayList<Task>();
		search_results.addAll(getOverdueTasksList());
		search_results.addAll(getFloatingTasksList());
		Calendar start = Calendar.getInstance();
		start.set(Calendar.HOUR_OF_DAY, 0);
		start.set(Calendar.MINUTE, 0);
		start.set(Calendar.SECOND, 0);
		Calendar end = Calendar.getInstance();
		end.set(Calendar.HOUR_OF_DAY, 23);
		end.set(Calendar.MINUTE, 59);
		end.set(Calendar.SECOND, 59);
		search_results.addAll(search(null, null, start, end));
		search_results.addAll(searchTaskByReminder(start, end));
		return search_results;
	}

	
	//Clear methods**************************************************************
	
	public void clearAll() throws FileNotFoundException, IOException {
		clear(al_task);
		clear(al_task_floating);
		clear(al_task_overdue);
		clear(al_task_completed);
		save();
	}
	
	private void clear(PriorityQueue<Task> filelist) {
		filelist.clear();
	}
	
	//Save methods**********************************************************
	
	private void save() throws FileNotFoundException, IOException {
		filehandler.writeFile(al_task);
		filehandler.writeFile(al_task_floating);
		filehandler.writeFile(al_task_overdue);
		filehandler.writeFile(al_task_completed);
		filehandler.writeTaskCounter(id_counter);
	}
	
	//Miscellaneous methods*************************************************
	
	private void checkForOverdueTasks() throws FileNotFoundException, IOException {
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
		id_counter = filehandler.getTaskCounter();
	}

	private PriorityQueue<Task> retrieveTaskList(Task task) {
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
		private int getTaskCounter() throws IOException {
			File countFile = new File(COUNT_TASK_FILENAME);
			if (!countFile.exists()) {
				countFile.createNewFile();
			}
			bufferedReader = new BufferedReader(new FileReader(countFile));
			
			String result = bufferedReader.readLine();
			if(result == null){
				return 0;
			}
			else{
				return Integer.parseInt(result);
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

		private void readFile(PriorityQueue<Task> filelist) throws IOException {
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
		
		private void writeFile(PriorityQueue<Task> fileToWrite)
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
		private String determineFileName(PriorityQueue<Task> fileToWrite) {
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
