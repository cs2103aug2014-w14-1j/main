import java.util.*;
import java.io.*;

import org.json.JSONException;

import com.google.gson.Gson;

/*
 * This class acts as the storage for the task manager software. It keeps track of lists of tasks
 * the user adds, and allows the user to retrieve them. In addition, the Storage generates recurring
 * instances of repeating tasks.
 * 
 * There are 4 types of task lists: Floating, Overdue, Completed and Task. See the Task class for
 * more details. They are implemented as PriorityQueues.
 * 
 * The Storage contains an internal FileHandler class which handles all file operations.
 * 
 * The Storage requires JSON and GSON libraries.
 * 
 * Limitations (described in developer guide):
 * When modifying a Task object, it is important to delete it from the Storage first before editing it,
 * then insert it back. This is to ensure the Task is kept in the right list.
 * 
 * Exceptions thrown by the Storage: IOException
 */

public class Storage {
	
	private static final int RECUR_YEAR_LIMIT = 3;				//the default limit for recurring tasks
	private static final int ID_COUNTER_INCREASE = 1;
	
	private FileHandler filehandler;
	
	private PriorityQueue<Task> al_task;
	private PriorityQueue<Task> al_task_floating;
	private PriorityQueue<Task> al_task_overdue;
	private PriorityQueue<Task> al_task_completed;
	private int id_counter;
	
	//Constructor***************************************************************************

	public Storage(String task_fn, String float_fn, String o_fn, String c_fn) throws IOException, JSONException {
		al_task = new PriorityQueue<Task>(new TaskComparator());
		al_task_floating = new PriorityQueue<Task>(new TaskComparator());
		al_task_overdue = new PriorityQueue<Task>(new TaskComparator());
		al_task_completed = new PriorityQueue<Task>(new TaskComparator());
		filehandler = new FileHandler(task_fn, float_fn, o_fn, c_fn);
		initFiles();
		id_counter = updateIndex();
		updateRecurringTasks();
		checkForOverdueTasks();
	}

	//Insert methods************************************************************************
	
	/*
	 * Inserts a task into its appropriate list. If it is a recurring task, generate
	 * repeating copies of it.
	 * 
	 * Assumption: This task is unique and does not already exist inside the lists
	 * If it was an existing task, it should have been deleted beforehand
	 */
	
	public void insert(Task task) throws IOException {
		checkForOverdueTasks();
		if (task.hasNoID()) {								//for new tasks with no ID
			assignID(task);
		}
		
		insert(task, retrieveTaskList(task));
		
		if (task.isRecur()) {
			ArrayList<Task> task_recur_chain = generateRecurringTasks(task);
			for (Task recur_task : task_recur_chain) {
				insert(recur_task, retrieveTaskList(recur_task));
			}
		}
		
		save();
	}
	
	private void assignID(Task task) {
		id_counter += ID_COUNTER_INCREASE;
		task.setId(id_counter);
	}

	private void insert(Task task, PriorityQueue<Task> list) {
		list.add(task);
	}
	
	/*
	 * Generates copies of a task and returns them in an ArrayList. The task is cloned, then
	 * the start and end dates are increased according to the recur pattern and period, until
	 * the last task's end date exceeds the date limit.
	 * 
	 * This method should not be called for a non-recurring task. This means the task must have
	 * non-null dates, appropriate recur pattern (Calendar field) and non-negative recur period
	 */
	private ArrayList<Task> generateRecurringTasks(Task task) {
		assert task.isRecur();
		
		ArrayList<Task> task_recur_chain = new ArrayList<Task>();
		
		Calendar limit = task.getRecurLimit();
		if (limit == null) {							//there is no limit: use default
			limit = getDefaultLimit();
		}
		
		//create new task instances. Increase their date
		while (task.getEndDate().before(limit)) {
			task = task.clone();
			
			Calendar start = task.getStartDate();
			start.add(task.getRecurPattern(), task.getRecurPeriod());
			
			Calendar end = task.getEndDate();
			end.add(task.getRecurPattern(), task.getRecurPeriod());
			if (end.after(limit)) {
				break;
			}
			
			task.setDates(start, end, task.getRecurPattern(), task.getRecurPeriod(), task.getRecurLimit());
			task_recur_chain.add(task);
		}
		return task_recur_chain;
	}
	
	/*
	 * Get default limit, equal to time of insertion + RECUR_YEAR_LIMIT
	 */
	private Calendar getDefaultLimit() {
		Calendar limit = Calendar.getInstance();
		limit.add(Calendar.YEAR, RECUR_YEAR_LIMIT);
		return limit;
	}
	
	//Delete methods***************************************************
	
	/*
	 * Deletes a task and all tasks with the same ID as it
	 * 
	 * Assumption 1: Task is in its appropriate file list. There should not
	 * be any exact duplicates of the task in different lists.
	 * 
	 * Assumption 2: If two or more tasks have the same ID, they belong to the
	 * same recur chain. This method will only check for other tasks with the
	 * same ID if the deleted task is recurring
	 */
	public void delete(Task task) throws IOException {
		checkForOverdueTasks();
		delete(task, retrieveTaskList(task));
		if (task.isRecur()) {
			deleteRecurChain(task);
		}
		save();
	}

	private void delete(Task task, PriorityQueue<Task> list) {
		list.remove(task);
	}
	
	private void deleteRecurChain(Task task) {
		ArrayList<Task> recur_chain = searchTaskByID(task.getId());
		for (Task other_task : recur_chain) {
			delete(other_task, retrieveTaskList(other_task));
		}
	}
	
	//Retrieval/search methods*************************************************************
	
	//Internal search methods. To be used by Storage only
	
	/*
	 * Searches for all tasks with the same ID.
	 * 
	 * Assumption: Does not search within completed task list. No reason to look for completed tasks
	 */
	private ArrayList<Task> searchTaskByID(Integer id){
		
		ArrayList<Task> search_results = new ArrayList<Task>();
		if (id == null || id < 0) {
			return search_results;								//every task in storage should have a nonnegative ID
		}
		
		searchTaskByID(id, al_task, search_results);
		searchTaskByID(id, al_task_floating, search_results);
		searchTaskByID(id, al_task_overdue, search_results);
		
		return search_results;
	}
	
	private ArrayList<Task> searchTaskByID(Integer id,PriorityQueue<Task> list, ArrayList<Task> searchResults){
		for(Task task: list){
			if(task.getId() == id){
				searchResults.add(task);
			}
		}
		return searchResults;
	}
	
	//External search methods. Can be called by other classes and tests
	
	public ArrayList<Task> getTasksList() {
		return translateQueueToList(al_task);
	}

	public ArrayList<Task> getFloatingTasksList() {
		return translateQueueToList(al_task_floating);
	}

	public ArrayList<Task> getCompletedTasksList() {
		return translateQueueToList(al_task_completed);
	}

	public ArrayList<Task> getOverdueTasksList() {
		return translateQueueToList(al_task_overdue);
	}
	
	private ArrayList<Task> translateQueueToList(PriorityQueue<Task> pq) {
		ArrayList<Task> list = new ArrayList<Task>();
		Iterator<Task> iter = pq.iterator();
		while (iter.hasNext()) {
			list.add(iter.next());
		}
		return list;
	}
	
	/*
	 * Main search method. Searches for all tasks with the specified parameters: keywords, tags,
	 * within a start and end date.
	 * 
	 * This method accepts null parameters.
	 * 
	 * Assumption: Does not search within completed task list. No reason to look for completed tasks
	 */
	public ArrayList<Task> search(ArrayList<String> keywords, ArrayList<String> tags, Calendar start_date, Calendar end_date) 
			throws IOException {
		checkForOverdueTasks();
		if (end_date!=null && start_date != null) {
			if (end_date.before(start_date)) {
				Calendar temp = start_date;
				start_date = end_date;
				end_date = temp;
			}
		}
		ArrayList<Task> search_results = new ArrayList<Task>();
				
		searchList(search_results, al_task_overdue, keywords, tags, start_date, end_date);
		searchList(search_results, al_task_floating, keywords, tags, start_date, end_date);
		searchList(search_results, al_task, keywords, tags, start_date, end_date);
		
		Collections.sort(search_results, new TaskComparator());
		return search_results;
	}
	
	/*
	 * Searches within a specified list and adds it to an input result list.
	 * Search parameters can be null
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
	
	//Clear methods**************************************************************
	
	public void clearAll() throws IOException {
		clear(al_task);
		clear(al_task_floating);
		clear(al_task_overdue);
		clear(al_task_completed);
		save();
	}
	
	private void clear(PriorityQueue<Task> tasklist) {
		tasklist.clear();
	}
	
	//Save methods***************************************************************
	
	private void save() throws IOException {
		filehandler.writeFile(al_task);
		filehandler.writeFile(al_task_floating);
		filehandler.writeFile(al_task_overdue);
		filehandler.writeFile(al_task_completed);
	}
	
	//Miscellaneous methods******************************************************
	
	
	/*
	 * Initialises the task lists by reading from the text files.
	 */
	private void initFiles() throws IOException {
		filehandler.readFile(al_task);
		filehandler.readFile(al_task_floating);
		filehandler.readFile(al_task_completed);
		filehandler.readFile(al_task_overdue);
	}

	/*
	 * Returns the appropriate list for an input task.
	 * This method is primarily used in insert() and delete()
	 */
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
	
	/*
	 * Checks for overdue tasks as of time when this method was called, removes them from
	 * the normal task list and inserts them in the overdue task.
	 * 
	 * Assumption 1: There should be no reason to shift overdue tasks back to normal tasks (time is always increasing)
	 * Assumption 2: No reason to check completed tasks
	 * Assumption 3: This is O(|tasklist.size|) timing, but in practice likely to be a small percentage
	 * because user should not have so many overdue tasks, unless he/she didn't use the program for a long time
	 */
	private void checkForOverdueTasks() throws IOException {
		ArrayList<Task> now_overdue_tasks = new ArrayList<Task>();
		while (!al_task.isEmpty() && al_task.peek().isOverdue()) {			//assumes priority queue is always sorted correctly
			now_overdue_tasks.add(al_task.poll());
		}
		al_task_overdue.addAll(now_overdue_tasks);
		save();
	}
	
	/*
	 * Looks for recurring tasks. If they have no specified recurring limit, update the
	 * default limit as of when this method was called and generate new instances to that
	 * limit.
	 * 
	 * NOT USED as of V0.4. Expensive operation that is of dubious value
	 */
	private void updateRecurringTasks() {
		for (int i = 0; i < id_counter; i++) {
			ArrayList<Task> searchlist = searchTaskByID(i);
			if (!searchlist.isEmpty()) {
				Task lasttask = searchlist.get(searchlist.size()-1);
				if (lasttask.isRecur() && lasttask.getRecurLimit()==null) {
					generateRecurringTasks(lasttask);
				}
			}
		}
	}
	
	/*
	 * Updates the ids of the existing tasks in the Storage. Returns the new id_count as a result, which
	 * should be equal to the number of used ids in Storage.
	 * This is to prevent the ids from growing too much.
	 * 
	 * The outcome of this method should be all ids are compacted, and there are no "holes" within the
	 * occupied ids.
	 * 
	 * Assumption: All ids >= 0. Negative ids are invalid and should not exist.
	 * 
	 * Note: Expensive operation
	 */
	private int updateIndex() {
		int latest_id = 0;
		int old_latest_id = getLatestID();
		LinkedList<Integer> holes = new LinkedList<Integer>();
		for (int i = 0; i < old_latest_id + 1; i++) {
			ArrayList<Task> tasks = searchTaskByID(i);
			if (tasks.isEmpty()) {
				holes.offer(i);
			}
			else {
				if (!holes.isEmpty()) {
					int newest_id = holes.poll();
					for (Task task : tasks) {
						task.setId(newest_id);
					}
					latest_id = newest_id;
				}
				else {
					latest_id = i;
				}
			}
		}
		return latest_id;
	}
	
	private int getLatestID() {
		int latest_id = getLatestID(al_task);
		int latest_id_floating = getLatestID(al_task_floating);
		int latest_id_overdue = getLatestID(al_task_overdue);
		int latest_id_completed = getLatestID(al_task_completed);
		return Math.max(Math.max(latest_id, latest_id_completed), Math.max(latest_id_overdue, latest_id_floating));
	}
	
	private int getLatestID(PriorityQueue<Task> list) {
		int latest_id = 0;
		for (Task task : list) {
			if (latest_id < task.getId()) {
				latest_id = task.getId();
			}
		}
		return latest_id;
	}
	
	//File Operations********************************************************
	
	/*
	 * All file operations are handled by the FileHandler class. It pairs the Storage's
	 * priority queues to files given by the input filenames.
	 */
	
	class FileHandler {
		
		private BufferedReader bufferedReader;
		private PrintWriter printWriter;
		private String task_filename;
		private String floating_task_filename;
		private String overdue_task_filename;
		private String completed_task_filename;
		
		// Constructor******************************************************
		
		public FileHandler(String t_fn, String f_fn, String o_fn, String c_fn) {
			this.task_filename = t_fn;
			this.floating_task_filename = f_fn;
			this.overdue_task_filename = o_fn;
			this.completed_task_filename = c_fn;
		}
		
		// Interfaces with the textFiles(databases)*************************

		private void readFile(PriorityQueue<Task> filelist) throws IOException {
			String filename = determineFileName(filelist);
			File file = new File(filename);
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
			bufferedReader.close();
		}
		
		private void writeFile(PriorityQueue<Task> fileToWrite)
				throws IOException {
			String filename = determineFileName(fileToWrite);
			File file = new File(filename);
			if (!file.exists()) {
				file.createNewFile();
			}
			// Start writing
			printWriter = new PrintWriter(new FileOutputStream(file));
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
				filename = task_filename;
			} else if (fileToWrite == al_task_floating) {
				filename = floating_task_filename;
			} else if (fileToWrite == al_task_completed) {
				filename = completed_task_filename;
			} else if (fileToWrite == al_task_overdue) {
				filename = overdue_task_filename;
			} else {
				throw new Error("Invalid file to write to");
			}
			return filename;
		}
	}
	
}
