//@author A0097299E
package speed.storage;

import com.google.gson.Gson;
import org.json.JSONException;
import speed.task.Task;
import speed.task.TaskComparator;

import java.io.*;
import java.util.*;

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
	
	private PriorityQueue<Task> list_task;
	private PriorityQueue<Task> list_floating;
	private PriorityQueue<Task> list_overdue;
	private PriorityQueue<Task> list_completed;
	private ArrayList<LinkedList<Task>> id_table;
	private int id_counter;
	
	//Constructor***************************************************************************

	public Storage(String task_fn, String float_fn, String o_fn, String c_fn) throws IOException, JSONException {
		list_task = new PriorityQueue<Task>(new TaskComparator());
		list_floating = new PriorityQueue<Task>(new TaskComparator());
		list_overdue = new PriorityQueue<Task>(new TaskComparator());
		list_completed = new PriorityQueue<Task>(new TaskComparator());
		filehandler = new FileHandler(task_fn, float_fn, o_fn, c_fn);
		initFiles();
		maintainListIntegrity();
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
		if (task.hasNoId()) {								//for new tasks with no ID
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
		
		assert !task.hasNoId();
		
		list.add(task);
		if (task.getId() >= id_table.size()) {
			id_table.add(new LinkedList<Task>());
		}
		id_table.get(task.getId()).add(task);
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
		
		assert !task.hasNoId();
		
		LinkedList<Task> recur_chain = searchTaskByID(task.getId());
		for (Task other_task : recur_chain) {
			delete(other_task, retrieveTaskList(other_task));
		}
		recur_chain.clear();
		save();
	}

	private void delete(Task task, PriorityQueue<Task> list) {
		list.remove(task);
	}

	//Retrieval/search methods*************************************************************
	
	//Internal search methods. To be used by Storage only
	
	/*
	 * Searches for all tasks with the same ID.
	 * 
	 * Assumption: Does not search within completed task list. No reason to look for completed tasks
	 */
	private LinkedList<Task> searchTaskByID(Integer id){
		
		if (id == null || id < 0) {
			return new LinkedList<Task>();								//every task in storage should have a nonnegative ID
		}
		
		return id_table.get(id);
	}
		
	public Task getParentTask(Task task) {
		
		if (task == null) {
			return null;
		}
		
		assert !task.hasNoId();
		
		return searchTaskByID(task.getId()).get(0);
	}
	
	//External search methods. Can be called by other classes and tests
	
	public ArrayList<Task> getTasksList() {
		return translateQueueToList(list_task);
	}

	public ArrayList<Task> getFloatingTasksList() {
		return translateQueueToList(list_floating);
	}

	public ArrayList<Task> getCompletedTasksList() {
		return translateQueueToList(list_completed);
	}

	public ArrayList<Task> getOverdueTasksList() {
		return translateQueueToList(list_overdue);
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
				
		searchList(search_results, list_overdue, keywords, tags, start_date, end_date);
		searchList(search_results, list_floating, keywords, tags, start_date, end_date);
		searchList(search_results, list_task, keywords, tags, start_date, end_date);
		
		Collections.sort(search_results, new TaskComparator());
		return search_results;
	}
	
	/*
	 * Searches within a specified list and adds it to an input result list.
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
		clear(list_task);
		clear(list_floating);
		clear(list_overdue);
		clear(list_completed);
		save();
		id_counter = 0;			//after save, in case there is an IOException
	}
	
	private void clear(PriorityQueue<Task> tasklist) {
		tasklist.clear();
	}
	
	//Save methods***************************************************************
	
	private void save() throws IOException {
		filehandler.writeFile(list_task);
		filehandler.writeFile(list_floating);
		filehandler.writeFile(list_overdue);
		filehandler.writeFile(list_completed);
	}
	
	//Miscellaneous methods******************************************************
	
	
	/*
	 * Initialises the task lists by reading from the text files.
	 */
	private void initFiles() throws IOException {
		filehandler.readFile(list_task);
		filehandler.readFile(list_floating);
		filehandler.readFile(list_completed);
		filehandler.readFile(list_overdue);
	}

	/*
	 * Returns the appropriate list for an input task.
	 * This method is primarily used in insert() and delete()
	 */
	private PriorityQueue<Task> retrieveTaskList(Task task) {
		if (task.isFloating()) {
			return list_floating;
		}
		else if (task.isOverdue()) {
			return list_overdue;
		}
		else if (task.isCompleted()) {
			return list_completed;
		}
		return list_task;
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
		while (!list_task.isEmpty() && list_task.peek().isOverdue()) {			//assumes priority queue is always sorted correctly
			now_overdue_tasks.add(list_task.poll());
		}
		list_overdue.addAll(now_overdue_tasks);
		save();
	}
	
	/*
	 * Looks for recurring tasks. If they have no specified recurring limit, update the
	 * default limit as of when this method was called and generate new instances to that
	 * limit.
	 * 
	 * Note: Expensive operation that is of dubious value
	 */
	private void updateRecurringTasks() {
		for (int i = 0; i < id_counter; i++) {
			LinkedList<Task> searchlist = searchTaskByID(i);
			if (!searchlist.isEmpty()) {
				Task lasttask = searchlist.get(searchlist.size()-1);
				if (lasttask.isRecur() && lasttask.getRecurLimit()==null) {
					generateRecurringTasks(lasttask);
				}
			}
		}
	}
	
	/*
	 * Builds the ID table to link families of tasks together and identify them quickly.
	 * Does not include completed tasks.
	 */
	private void buildIDTable() {
		id_table = new ArrayList<LinkedList<Task>>();
		id_counter = getLatestID();
		for (int i = 0; i <= id_counter; i++) {
			id_table.add(new LinkedList<Task>());
		}
		for (Task task : list_overdue) {
			id_table.get(task.getId()).add(task);
		}
		for (Task task : list_floating) {
			id_table.get(task.getId()).add(task);
		}
		for (Task task : list_task) {
			id_table.get(task.getId()).add(task);
		}
	}

	/*
	 * Updates the ids of the existing tasks in the Storage. Updates id_count as a result, which
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
	private void compactIndex() {
		int holes = 0;
		for (int i = 1; i < id_table.size(); i++) {
			LinkedList<Task> task_family = searchTaskByID(i);
			if (task_family.isEmpty()) {
				holes++;
			}
			else {
				for (Task task : task_family) {
					task.setId(task.getId() - holes);
				}
			}
		}
		for (int i = 1; i < id_table.size(); i++) {
			if (id_table.get(i).isEmpty()) {
				id_table.remove(i);
			}
		}
		id_counter = getLatestID();
	}
	
	private int getLatestID() {
		int latest_id = getLatestID(list_task);
		int latest_id_floating = getLatestID(list_floating);
		int latest_id_overdue = getLatestID(list_overdue);
		return Math.max(latest_id, Math.max(latest_id_overdue, latest_id_floating));
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
	
	//Fix methods***************************************************************************
	
	/*
	 * Through the course of developing this software, there have been often bugs where the
	 * dates on a task suddenly disappeared and appear in wrong folders. Until all bugs are
	 * fixed, providing methods to attempt a fix.
	 * 
	 * Tasks with no ids are always destroyed as they cannot fit in the ID table.
	 * Tasks in the wrong lists are resorted
	 */
	public void maintainListIntegrity() {
		LinkedList<Task> no_id_tasks = new LinkedList<Task>();
		LinkedList<Task> wrong_tasks = new LinkedList<Task>();
		
		for (Task task : list_task) {
			if (task.hasNoId()) {
				no_id_tasks.add(task);
			}
			else if (task.isCompleted() || task.isFloating() || task.isOverdue()) {
				wrong_tasks.add(task);
			}
		}
		
		for (Task task : list_floating) {
			if (task.hasNoId()) {
				no_id_tasks.add(task);
			}
			else if (!task.isFloating()) {
				wrong_tasks.add(task);
			}
		}
		
		for (Task task : list_overdue) {
			if (!task.isOverdue()) {
				wrong_tasks.add(task);
			}
		}
		
		no_id_tasks.clear();
		for (Task task : list_completed) {
			if (!task.isCompleted()) {
				wrong_tasks.add(task);
			}
		}
		
		list_floating.removeAll(no_id_tasks);
		list_overdue.removeAll(no_id_tasks);
		list_task.removeAll(no_id_tasks);
		list_completed.removeAll(no_id_tasks);
		
		list_floating.removeAll(wrong_tasks);
		list_overdue.removeAll(wrong_tasks);
		list_task.removeAll(wrong_tasks);
		list_completed.removeAll(wrong_tasks);
		
		for (Task task : wrong_tasks) {
			retrieveTaskList(task).add(task);
		}
		buildIDTable();
		compactIndex();
	}
	
	//@author A0111660W
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
			if (fileToWrite == list_task) {
				filename = task_filename;
			} else if (fileToWrite == list_floating) {
				filename = floating_task_filename;
			} else if (fileToWrite == list_completed) {
				filename = completed_task_filename;
			} else if (fileToWrite == list_overdue) {
				filename = overdue_task_filename;
			} else {
				throw new Error("Invalid file to write to");
			}
			return filename;
		}
	}
	
}
