//@author A0112059N

package speed.controller;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.TreeMap;

import org.json.JSONException;
import org.junit.Test;

import speed.parser.Command;
import speed.parser.Command.COMMAND_TYPE;
import speed.storage.Storage;
import speed.task.Task;

public class ControllerTest {
	
	private static final String TASK_FILENAME = "Task.txt";
	private static final String FLOATING_TASK_FILENAME = "FloatingTask.txt";
	private static final String OVERDUE_TASK_FILENAME = "OverdueTask.txt";
	private static final String COMPLETED_TASK_FILENAME = "CompletedTask.txt";
	
	private final String ERROR_NO_NAME = "Cannot add a task with no description.";
	private final String ERROR_NO_CHANGE = "No change was made.";
	private final String ERROR_INVALID_ID = "Invalid id to update.";
	private final String ERROR_INVALID_IDS = "All ids are invalid.";
	private final String ERROR_INVALID_UPDATE = "Invalid update command";
	private final String MESSAGE_ADD = "Succesfully added a new task: ";
	private final String MESSAGE_DELETE = " deleted from the calendar.";
	private final String MESSAGE_UPDATE = "Updated successfully.";
	private final String MESSAGE_COMPLETE = " tasks completed.";
	private final String MESSAGE_UNDO = "Undo successfully.";
	private final String MESSAGE_REDO = "Redo successfully.";
	private final String UNAVAILABLE_UNDO = "Undo not available.";
	private final String UNAVAILABLE_REDO = "Redo not available.";
	
	
	private class SimulatedStorage extends Storage {
		
		private ArrayList<Task> tasks_;
		private ArrayList<String> keywords_;
		private ArrayList<String> tags_;
		private Calendar startDate_;
		private Calendar endDate_;

		public SimulatedStorage(String task_fn, String float_fn, String o_fn, String c_fn) throws Exception {
			super(task_fn, float_fn, o_fn, c_fn);
			this.tasks_ = new ArrayList<Task>();
			keywords_ = null;
			tags_ = null;
			startDate_ = null;
			endDate_ = null;
		}
		
		
		@Override
		public void insert(Task task) {
			tasks_.add(task);
		}
		
		@Override
		public void delete(Task task) {
			tasks_.remove(task);
		}
		
		@Override
		public Task getParentTask(Task task) {
			return task;
		}
		
		public ArrayList<Task> getTasks() {
			return tasks_;
		}
 		
		public ArrayList<Task> search(ArrayList<String> keywords, ArrayList<String> tags, Calendar startDate, Calendar endDate) {
			keywords_ = keywords;
			tags_ = tags;
			startDate_ = startDate;
			endDate_ = endDate;
			return new ArrayList<Task>();
		}
		
		public ArrayList<String> getKeywords() {
			return keywords_;
		}
		
		public ArrayList<String> getTags() {
			return tags_;
		}
		
		public Calendar getStartDate() {
			return startDate_;
		}
		
		public Calendar getEndDate() {
			return endDate_;
		}
	}
	
	private SimulatedStorage storage_;
	private LogicHandler logic_;
	private SearchHandler searcher_;
	private TreeMap<String,Task> taskIDmap_;
	
	
	
	public void init() throws Exception, JSONException {
		storage_ = new SimulatedStorage(TASK_FILENAME,FLOATING_TASK_FILENAME, OVERDUE_TASK_FILENAME,COMPLETED_TASK_FILENAME);
		logic_ = new LogicHandler(storage_);
		searcher_ = new SearchHandler(storage_);
		taskIDmap_ = new TreeMap<String,Task> ();
	}
	
	private Task search(String taskName, ArrayList<Task> al) {
		for (Task task: al) {
			if (task.getTaskName().equals(taskName)) 
				return task;
		}
		
		return null;
	}
	
	@Test
	public void testAdd() throws Exception {
		init();
		
		Calendar date1 = Calendar.getInstance();
		Calendar date2 = Calendar.getInstance();
		Calendar date3 = Calendar.getInstance();
		date1.set(0, 0, 0);
		date2.set(1, 1, 1);
		date3.set(2, 2, 2);
		assertEquals(date1.equals(date2),false);
		
		Command addNoName = new Command();
		addNoName.setCommandType(Command.COMMAND_TYPE.ADD);
		addNoName.setTaskName("");
		
		String response1 = logic_.executeCommand(null, addNoName);
		
		Command addFloating = new Command();
		addFloating.setCommandType(Command.COMMAND_TYPE.ADD);
		String ftaskName = "This is a floating task";
		addFloating.setTaskName(ftaskName);
		
		String response2 = logic_.executeCommand(null, addFloating);
		
		Command addOneDate = new Command();
		addOneDate.setCommandType(Command.COMMAND_TYPE.ADD);
		String oneDateTaskName = "This is a task with one date";
		addOneDate.setTaskName(oneDateTaskName);
		addOneDate.setTaskEndDate(date1);
		
		String response3 = logic_.executeCommand(null, addOneDate);
		
		Command addTwoDate = new Command();
		addTwoDate.setCommandType(Command.COMMAND_TYPE.ADD);
		String twoDateTaskName = "This is a task with two date";
		addTwoDate.setTaskName(twoDateTaskName);
		addTwoDate.setTaskStartDate(date1);
		addTwoDate.setTaskEndDate(date2);
		
		String response4 = logic_.executeCommand(null,addTwoDate);
		
		Command addWithTags = new Command();
		addWithTags.setCommandType(Command.COMMAND_TYPE.ADD);
		String taggedTaskName = "This is a task with taggings";
		addWithTags.setTaskName(taggedTaskName);
		addWithTags.setTaskEndDate(date2);
		String[] tags = new String[] {"tag 1", "tag 2"};
		addWithTags.setTaskTagsToAdd(tags);
		ArrayList<String> arTags = new ArrayList<String> ();
		arTags.add("tag 1");
		arTags.add("tag 2");
		
		String response5 = logic_.executeCommand(null, addWithTags);
		
		Command addRecur = new Command();
		addRecur.setCommandType(Command.COMMAND_TYPE.ADD);
		String recurTaskName = "This is a recurring task.";
		addRecur.setTaskName(recurTaskName);
		addRecur.setTaskEndDate(date3);
		addRecur.setRecurPattern(3);
		addRecur.setRecurPeriod(4);
		
		String response6 = logic_.executeCommand(null,addRecur);
		
		ArrayList<Task> tasks = storage_.getTasks();
		
		Task floatingTask = tasks.get(0); 
		Task oneDateTask = tasks.get(1);
		Task twoDateTask = tasks.get(2);
		Task taggedTask = tasks.get(3);
		Task recurringTask = tasks.get(4);

		//add with no name
		assertEquals("Add task with no name - error", ERROR_NO_NAME, response1);
		
		//add floating task
		assertEquals("Add task with no date - check response", MESSAGE_ADD + ftaskName, response2);
		assertEquals("Add task with no date - check start date", null, floatingTask.getStartDate());
		assertEquals("Add task with no date - check end date", null, floatingTask.getEndDate());
		assertEquals("Add task with no date - check taggings", new ArrayList<String> (), floatingTask.getTags());
		assertEquals("Add task with no date - check recurring", false, floatingTask.isRecur());
		assertEquals("Add task with no date - check floating", true, floatingTask.isFloating());
		
		//add task with one date
		assertEquals("Add task with one date - check response", MESSAGE_ADD + oneDateTaskName, response3);
		assertEquals("Add task with one date - check start date", date1, oneDateTask.getEndDate());
		assertEquals("Add task with one date - check end date", date1, oneDateTask.getEndDate());
		assertEquals("Add task with one date - check taggings", new ArrayList<String> (), oneDateTask.getTags());
		assertEquals("Add task with one date - check recurring", false, oneDateTask.isRecur());
		
		//add task with two date 
		assertEquals("Add task with two date - check response", MESSAGE_ADD + twoDateTaskName, response4);
		assertEquals("Add task with two date - check start date", date1, twoDateTask.getStartDate());
		assertEquals("Add task with two date - check end date", date2, twoDateTask.getEndDate());
		assertEquals("Add task with two date - check taggings", new ArrayList<String> (), twoDateTask.getTags());
		assertEquals("Add task with two date - check recurring", false, twoDateTask.isRecur());
		
		//add with tags
		assertEquals("Add task with tags - check response", MESSAGE_ADD + taggedTaskName, response5);
		assertEquals("Add task with tags - check start date", date2, taggedTask.getStartDate());
		assertEquals("Add task with tags - check end date", date2, taggedTask.getEndDate()); 
		assertEquals("Add task with tags - check taggings", arTags, taggedTask.getTags());
		assertEquals("Add task with tags - check recurring", false, taggedTask.isRecur());
	
		//add recurring tags
		assertEquals("Add recurring task - check response", MESSAGE_ADD + recurTaskName, response6);
		assertEquals("Add recurring task - check start date", date3, recurringTask.getStartDate());
		assertEquals("Add recurring task - check end date", date3, recurringTask.getEndDate());
		assertEquals("Add recurirng task - check recurring", true, recurringTask.isRecur());
		assertEquals("Add recurring task - check recur pattern", true, recurringTask.getRecurPattern() == 3);
		assertEquals("Add recurring task - check recur period", true, recurringTask.getRecurPeriod() == 4) ;
	}
	
	@Test
	public void testDelete() throws Exception {
		init();
		
		Task[] tasks = new Task[10];
		
		String[] ids = new String[] {"O0", "O1", "O2", "O3", "F4", "F5", "F6", "T7", "T8", "T9"};
		
		for (int i = 0; i < 10; i++) {
			tasks[i] = new Task();
			tasks[i].setTaskName("Dummy task" + (i+1));
			storage_.insert(tasks[i]);
			taskIDmap_.put(ids[i], tasks[i]);
		}
		
		ArrayList<Task> expected = new ArrayList<Task> ();
		
		for (int i = 0; i < 10; i++) {
			expected.add(tasks[i]);
		}
		
		//delete with all invalid IDs
		Command deleteInvalidIDs = new Command();
		deleteInvalidIDs.setCommandType(Command.COMMAND_TYPE.DELETE);
		deleteInvalidIDs.setTaskIDsToDelete(new String[] {"O4", "T5", "O11","T0"});
		
		String response1 = logic_.executeCommand(taskIDmap_, deleteInvalidIDs);
		ArrayList<Task> alTasks = storage_.getTasks();
		
		assertEquals("Delete with all invalid IDs - check response", ERROR_INVALID_IDS, response1);
		assertEquals("Delete with all invalid IDs - check tasks", expected, alTasks);
		
		//delete with numeric IDs
		Command deleteNumeric = new Command();
		deleteNumeric.setCommandType(Command.COMMAND_TYPE.DELETE); 
		deleteNumeric.setTaskIDsToDelete(new String[] {"1", "2"});
		
		String response2 = logic_.executeCommand(taskIDmap_, deleteNumeric);
		taskIDmap_.remove("O1");
		taskIDmap_.remove("O2");
		expected.remove(tasks[1]);
		expected.remove(tasks[2]);
	
		
		assertEquals("Delete with numeric IDs - check response", 2 + MESSAGE_DELETE, response2);
		assertEquals("Delete with numeric IDs - check storage",expected , alTasks);
		
		//delete with full IDs
		Command deleteFullID = new Command();
		deleteFullID.setCommandType(Command.COMMAND_TYPE.DELETE); 
		deleteFullID.setTaskIDsToDelete(new String[] {"O3", "F4", "T7"});
		
		String response3 = logic_.executeCommand(taskIDmap_, deleteFullID);
		taskIDmap_.remove("O3");
		taskIDmap_.remove("F4");
		taskIDmap_.remove("T7");
		expected.remove(tasks[3]);
		expected.remove(tasks[4]);
		expected.remove(tasks[7]);
		
		assertEquals("Delete with full IDs - check response", 3 + MESSAGE_DELETE, response3);
		assertEquals("Delete with full IDs - check storage",expected , alTasks);
		
		//delete mixed numeric 
		Command deleteMixNumeric = new Command();
		deleteMixNumeric.setCommandType(Command.COMMAND_TYPE.DELETE);
		deleteMixNumeric.setTaskIDsToDelete(new String[] {"0", "F5", "F6"});
		
		String response4 = logic_.executeCommand(taskIDmap_, deleteMixNumeric);
		taskIDmap_.remove("O0");
		taskIDmap_.remove("F5");
		taskIDmap_.remove("F6");
		expected.remove(tasks[0]);
		expected.remove(tasks[5]);
		expected.remove(tasks[6]);
		
		assertEquals("Delete with mixed IDs - check response", 3 + MESSAGE_DELETE, response4);
		assertEquals("Delete with mixed IDs - check storage", expected, alTasks);
		
		//delete both valid and invalid IDs
		Command deleteMixValid = new Command();
		deleteMixValid.setCommandType(Command.COMMAND_TYPE.DELETE);
		deleteMixValid.setTaskIDsToDelete(new String[] {"0", "T1", "F5", "F6", "T8", "T9"});

		String response5 = logic_.executeCommand(taskIDmap_, deleteMixValid);
		expected.remove(tasks[8]);
		expected.remove(tasks[9]);
		
		assertEquals("Delete with both valid and invalid IDs - check response", 2 + MESSAGE_DELETE, response5);
		assertEquals("Delete with both valid and invalid IDs - check storage", expected, alTasks);
	}
	
	@Test
	public void testUpdate() throws Exception {
		init();
		
		Calendar date1 = Calendar.getInstance();
		Calendar date2 = Calendar.getInstance();
		date1.set(0,0,0);
		date2.set(0,0,0);
		
		Command addTask = new Command();
		addTask.setCommandType(Command.COMMAND_TYPE.ADD);
		String oldTaskName = "This is the old task";
		String newTaskName = "This is the new task";
		addTask.setTaskName(oldTaskName);
		
		logic_.executeCommand(null, addTask);
		Task[] dummyTask = new Task[5];
		for (int i=0; i<5; i++) {
			dummyTask[i] = new Task();
			dummyTask[i].setTaskName("This is a dummy task" + 1);
			storage_.insert(dummyTask[i]);
		}
		
		ArrayList<Task> tasks = storage_.getTasks();
		String[] ids = new String[] {"T7","F1","F2","O3","O4","T0"};
	
		for (int i=0; i<6; i++) {
			taskIDmap_.put(ids[i], tasks.get(i));
		}
		
		Task oldTask = search(oldTaskName, tasks);
		oldTask.setId(0);
		
		assertEquals(oldTaskName, oldTask.getTaskName());
		assertEquals(null, oldTask.getStartDate());
		assertEquals(null, oldTask.getEndDate());
		assertEquals(true, oldTask.isFloating());
		assertEquals(new ArrayList<String> (), oldTask.getTags());
		
		//Update an invalid IDs
		Command updateInvalidID = new Command();
		updateInvalidID.setCommandType(Command.COMMAND_TYPE.EDIT);
		updateInvalidID.setTaskName(newTaskName);
		updateInvalidID.setTaskID("T1");
		
		String response1 = logic_.executeCommand(taskIDmap_, updateInvalidID);
		tasks = storage_.getTasks();
		
		assertEquals("Update invalid ids - check response", ERROR_INVALID_ID, response1);
		assertEquals("Update invalid ids - check storage size", tasks.size(), 6);
		assertEquals("Update invalid ids - check old task", oldTask, search(oldTaskName,tasks)); 
		
		//Update with no info
		Command updateNoChange = new Command();
		updateNoChange.setCommandType(Command.COMMAND_TYPE.EDIT);
		updateNoChange.setTaskID("T7");
		
		String response2 = logic_.executeCommand(taskIDmap_, updateNoChange);
		tasks = storage_.getTasks();
		
		assertEquals("Update with no info - check response", ERROR_NO_CHANGE, response2);
		assertEquals("Update with no info - check storage size", tasks.size(), 6);
		assertEquals("Update with no info - check old task", oldTask, search(oldTaskName,tasks)); 
		
		//Update time and date
		Command updateTime = new Command();
		updateTime.setCommandType(Command.COMMAND_TYPE.EDIT);
		updateTime.setTaskID("T7");
		updateTime.setTaskEndDate(date2);
		
		String response3 = logic_.executeCommand(taskIDmap_, updateTime);
		tasks = storage_.getTasks();
		Task newTask = search(oldTaskName, tasks);
		taskIDmap_.put("T7", newTask);
		
		assertEquals("Update date and time - check response", MESSAGE_UPDATE, response3);
		assertEquals("Update date and time - check task name", oldTaskName, newTask.getTaskName());
		assertEquals("Update date and time - check task start date", date2, newTask.getStartDate());
		assertEquals("Update date and time - check task end date", date2, newTask.getEndDate());
		assertEquals("Update date and time - check floating", false, newTask.isFloating());

		//Update add taggings and remove 
		Command updateTags = new Command();
		updateTags.setCommandType(Command.COMMAND_TYPE.EDIT);
		updateTags.setTaskID("T7");
		String[] tags = new String[] {"Tag 1", "Tag 2"};
		updateTags.setTaskTagsToAdd(tags);
		ArrayList<String> alTags = new ArrayList<String> ();
		for (String tag: tags) {
			alTags.add(tag);
		}
		
		String response4 = logic_.executeCommand(taskIDmap_, updateTags);
		tasks = storage_.getTasks();
		newTask = search(oldTaskName, tasks);
		taskIDmap_.put("T7", newTask);
		
		assertEquals("Update adding tags - check response", MESSAGE_UPDATE, response4);
		assertEquals("Update adding tags - check task name", oldTaskName, newTask.getTaskName());
		assertEquals("Update adding tags - check tags", alTags, newTask.getTags());
		
		//Remove some of the tags
		updateTags.setTaskTagsToAdd(new String[] {"tag 3", "tag 4"});
		updateTags.setTaskTagsToRemove(new String[] {"Tag 1"});
		alTags.add("tag 3");
		alTags.add("tag 4");
		alTags.remove("Tag 1");
		
		String response5 = logic_.executeCommand(taskIDmap_, updateTags);
		tasks = storage_.getTasks();
		newTask = search(oldTaskName, tasks);
		taskIDmap_.put("T7", newTask);
		
		assertEquals("Update adding tags - check response", MESSAGE_UPDATE, response5);
		assertEquals("Update adding tags - check task name", oldTaskName, newTask.getTaskName());
		assertEquals("Update adding tags - check tags", alTags, newTask.getTags());
		
		//Update Name
		Command updateName = new Command();
		updateName.setCommandType(Command.COMMAND_TYPE.EDIT);
		updateName.setTaskID("T7");
		updateName.setTaskName(newTaskName);
		
		String response6 = logic_.executeCommand(taskIDmap_,updateName);
		tasks = storage_.getTasks();
		newTask = search(newTaskName, tasks);
		
		assertEquals("Update name - check response", MESSAGE_UPDATE, response6);
		assertEquals("Update name - check old name", null, search(oldTaskName, tasks));
		assertEquals("Update name - check new name", newTaskName, newTask.getTaskName());
		assertEquals("Update name - check start date", date2, newTask.getStartDate());
		assertEquals("Update name - check end date", date2, newTask.getEndDate());
		assertEquals("Update name - check tags", alTags, newTask.getTags());
	}
	
	@Test
	public void testComplete() throws Exception {
		init();
		
		Task[] tasks = new Task[10];
		
		String[] ids = new String[] {"O0", "O1", "O2", "O3", "F4", "F5", "F6", "T7", "T8", "T9"};
		
		for (int i = 0; i < 10; i++) {
			tasks[i] = new Task();
			tasks[i].setId(i);
			tasks[i].setTaskName("Dummy task" + (i));
			storage_.insert(tasks[i]);
			taskIDmap_.put(ids[i], tasks[i]);
		}
		
		//For the IDs part, complete is similar to delete.
		//Thus only test whether the tasks are completed.
		
		Command completeCommand = new Command();
		completeCommand.setCommandType(Command.COMMAND_TYPE.COMPLETE);
		completeCommand.setTaskIDsToComplete(new String[] {"1", "O2", "F4"});
		
		String response = logic_.executeCommand(taskIDmap_, completeCommand);
		ArrayList<Task> alTasks = storage_.getTasks();
		
		assertEquals("Complete - check response", 3 + MESSAGE_COMPLETE, response);
		assertEquals("Complete - check task 1", true, search("Dummy task1", alTasks).isCompleted());
		assertEquals("Complete - check task 2", true, search("Dummy task2", alTasks).isCompleted());
		assertEquals("Complete - check task 3", true, search("Dummy task4", alTasks).isCompleted());
	
	}
	
	@Test
	public void testUndoRedo() throws Exception, Exception {
		init();
		
		Command undo = new Command();
		undo.setCommandType(Command.COMMAND_TYPE.UNDO);
		Command redo = new Command();
		redo.setCommandType(Command.COMMAND_TYPE.REDO);
		
		String response1 = logic_.executeCommand(taskIDmap_, undo);
		
		assertEquals("Undo not available - check response", UNAVAILABLE_UNDO, response1);
		
		Task[] tasks = new Task[10];
		
		String[] ids = new String[] {"O0", "O1", "O2", "O3", "F4", "F5", "F6", "T7", "T8", "T9"};
		
		for (int i = 0; i < 10; i++) {
			tasks[i] = new Task();
			tasks[i].setId(i);
			tasks[i].setTaskName("Dummy task" + (i));
			storage_.insert(tasks[i]);
			taskIDmap_.put(ids[i], tasks[i]);
		}
		
		//Undo add
		//create command
		Command add = new Command();
		add.setCommandType(Command.COMMAND_TYPE.ADD);
		String taskName = "This is the added task.";
		String newTaskName = "This is the new task";
		
		//add
		add.setTaskName(taskName);
		logic_.executeCommand(taskIDmap_,add);
		
		ArrayList<Task> alTasks = storage_.getTasks();
		Task addedTask = search(taskName, alTasks);
		
		assertEquals("Undo adding - check before", false, (addedTask == null));
		
		//undo
		String response2 = logic_.executeCommand(taskIDmap_, redo);
		String response3 = logic_.executeCommand(taskIDmap_, undo);
		
		alTasks = storage_.getTasks();
		addedTask = search(taskName, alTasks);
		
		assertEquals("Redo not available - check response", UNAVAILABLE_REDO, response2);
		assertEquals("Undo adding - check response", MESSAGE_UNDO, response3);
		assertEquals("Undo adding - check storage", null, addedTask);
		
		//redo
		String response4 = logic_.executeCommand(taskIDmap_,redo);
		alTasks = storage_.getTasks();
		addedTask = search(taskName, alTasks);
		assertEquals("Redo adding - check response", MESSAGE_REDO, response4);
		assertEquals("Redo adding - check storage", false, (addedTask == null));
		
		taskIDmap_.put("T10", addedTask);
		
		//Undo delete
		//create command
		Command delete = new Command();
		delete.setCommandType(Command.COMMAND_TYPE.DELETE);
		delete.setTaskIDsToDelete(new String[] {"1", "O2", "T7"});
		
		//delete
		logic_.executeCommand(taskIDmap_, delete);
		
		assertEquals("Undo delete - check before 1", false, alTasks.contains(tasks[1]));
		assertEquals("Undo delete - check before 2", false, alTasks.contains(tasks[2]));
		assertEquals("Undo delete - check before 3", false, alTasks.contains(tasks[7]));
		
		//undo
		String response5 = logic_.executeCommand(taskIDmap_, undo);
		
		assertEquals("Undo delete - check response", MESSAGE_UNDO, response5);
		assertEquals("Undo delete - check storage 1", true, alTasks.contains(tasks[1]));
		assertEquals("Undo delete - check storage 2", true, alTasks.contains(tasks[2]));
		assertEquals("Undo delete - check storage 3", true, alTasks.contains(tasks[7]));
		
		//redo
		String response6 = logic_.executeCommand(taskIDmap_, redo);
		
		assertEquals("Redo delete - check response", MESSAGE_REDO, response6);
		assertEquals("Redo delete - check storage 1", false, alTasks.contains(tasks[1]));
		assertEquals("Redo delete - check storage 2", false, alTasks.contains(tasks[2]));
		assertEquals("Redo delete - check storage 3", false, alTasks.contains(tasks[7]));
		
		//Undo update 
		//create command
		Command update = new Command();
		addedTask.setId(0); //a task need to have its id before it can be updated.
		update.setCommandType(Command.COMMAND_TYPE.EDIT);
		update.setTaskID("T10");
		update.setTaskName(newTaskName);
		//update
		logic_.executeCommand(taskIDmap_, update);
		
		assertEquals("Undo update - check before 1", true, search(taskName,alTasks) == null);
		assertEquals("Undo update - check before 2", true, search(newTaskName,alTasks) != null);
		
		//undo
		logic_.executeCommand(taskIDmap_, undo);
		assertEquals("Undo update - check storage 1", true, search(taskName,alTasks) != null);
		assertEquals("Undo update - check storage 2", true, search(newTaskName,alTasks) == null);
		
		//redo
		logic_.executeCommand(taskIDmap_, redo);
		assertEquals("Redo update - check storage 1", true, search(taskName,alTasks) == null);
		assertEquals("Redo update - check storage 2", true, search(newTaskName,alTasks) != null);
		
		Task newTask = search(newTaskName,alTasks);
		taskIDmap_.put("T10", newTask);
		
		//Undo complete
		//create command
		Command complete = new Command();
		complete.setCommandType(Command.COMMAND_TYPE.COMPLETE);
		complete.setTaskIDsToComplete(new String[] {"O3", "T10"});
		
		//complete
		logic_.executeCommand(taskIDmap_, complete);
		
		assertEquals("Undo complete - check before 1", true, search(newTaskName,alTasks).isCompleted());
		assertEquals("Undo complete - check before 2", true, search("Dummy task3",alTasks).isCompleted());
		
		//undo
		logic_.executeCommand(taskIDmap_,undo);
		assertEquals("Undo complete - check storage 1", false, search(newTaskName,alTasks).isCompleted());
		assertEquals("Undo complete - check storage 2", false, search("Dummy task3",alTasks).isCompleted());
	
		//redo
		logic_.executeCommand(taskIDmap_,redo);
		assertEquals("Redo complete - check storage 1", true, search(newTaskName,alTasks).isCompleted());
		assertEquals("Redo complete - check storage 2", true, search("Dummy task3",alTasks).isCompleted());
	}
	
	@Test
	public void testSearch() throws Exception, Exception {
		init();
		
		Calendar date1 = Calendar.getInstance();
		Calendar date2 = Calendar.getInstance();
		date1.set(0,0,0);
		date2.set(1,1,1);
		
		ArrayList<String> keywords = new ArrayList<String> ();
		keywords.add("This is a key word");
		String[] tags = new String[] {"tag 1", "tag 2"};
		ArrayList<String> alTags = new ArrayList<String> ();
		alTags.add("tag 1");
		alTags.add("tag 2");
		
		//create a new search command
		Command search = new Command();
		search.setCommandType(Command.COMMAND_TYPE.SEARCH);
		search.setSearchKeywords(keywords);
		search.setSearchTags(tags);
		search.setSearchStartDate(date1);
		search.setSearchEndDate(date2);
			
		//checking whether all fields were passed to the storage correctly
		searcher_.proceedCommand(search);
		assertEquals("Search - check keywords", keywords, storage_.getKeywords());
		assertEquals("Search - check tags", alTags, storage_.getTags());
		assertEquals("Search - check start date", date1, storage_.getStartDate());
		assertEquals("Search - check start date", date2, storage_.getEndDate());

		//create a new list command
		Command list = new Command();
		list.setCommandType(Command.COMMAND_TYPE.LIST);
		list.setSearchKeywords(keywords);
		list.setSearchTags(tags);
		list.setSearchStartDate(date1);
		list.setSearchEndDate(date2);

		//verify only the dates are passed to the storage_
		searcher_.proceedCommand(list);
		assertEquals("List - check keywords", new ArrayList<String> (), storage_.getKeywords());
		assertEquals("List - check tags", new ArrayList<String> (), storage_.getTags());
		assertEquals("List - check start date", date1, storage_.getStartDate());
		assertEquals("List - check start date", date2, storage_.getEndDate());
	}
}
