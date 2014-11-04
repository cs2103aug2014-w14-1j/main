import java.io.IOException;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Stack;
import java.util.TreeMap;


public class LogicHandler {	
	
	private class SimpleCommand {
		private ArrayList<Task> oldTasks_;
		private ArrayList<Task> newTasks_;
	
		public SimpleCommand(ArrayList<Task> oldTasks, ArrayList<Task> newTasks) {
			oldTasks_ = oldTasks;
			newTasks_ = newTasks;
		}
		
		public ArrayList<Task> getOldTasks() {
			return oldTasks_;
		}
		
		public ArrayList<Task> getNewTasks() {
			return newTasks_;
		}
	}

	private Storage storage_;
	private Stack<SimpleCommand> histories_;
	private Stack<SimpleCommand> future_;

	public LogicHandler(Storage storage) {
		storage_ = storage;
		histories_ = new Stack<SimpleCommand>();
		future_ = new Stack<SimpleCommand>();
	}
	
	private void addHistory(SimpleCommand command) {
		histories_.add(command);
		future_.clear();
	}
	
	public String executeCommand(TreeMap<String,Task> taskIDmap, Command command) throws Exception {
		Command.COMMAND_TYPE commandType = command.getCommandType();
		switch(commandType) {
		case ADD: {
			return executeAdd(taskIDmap, command);
			}
		case DELETE: {
			return executeDelete(taskIDmap, command);
			}
		case EDIT: {
			return executeUpdate(taskIDmap, command);
			}
		case UNDO: {
			return executeUndo(taskIDmap, command);
		}
		case REDO: {
			return executeRedo(taskIDmap, command);
		}
		case COMPLETE: {
			return executeComplete(taskIDmap, command);
		}
		case EXIT: {
			System.exit(0);
		}
		default:
			return "Unrecognized command type";
		}		
	}
	
	private String executeAdd(TreeMap<String,Task> taskIDmap, Command command) throws Exception {
		
		if (command.getTaskName().equals("")) {
			throw new Exception("Can not add a task with no descriptions");
		}
		
		Task task = new Task();
		task.setTaskName(command.getTaskName());
		
		task.setDates(command.getTaskStartDate(), command.getTaskEndDate(), command.getRecurPattern(), command.getRecurPeriod(), null);
		
		if (command.getTaskTagsToAdd()!=null) {
			for (String tag : command.getTaskTagsToAdd()) {
				task.addTag(tag);
			}
		}
		
		
		ArrayList<Task> newTasks = new ArrayList<Task>();
		newTasks.add(task);
		ArrayList<Task> oldTasks = new ArrayList<Task>();
		
		SimpleCommand addCommand = new SimpleCommand(oldTasks,newTasks);
		SimpleCommand undoCommand = new SimpleCommand(newTasks,oldTasks);
		
		addHistory(undoCommand);
		executeSimpleCommand(addCommand);
		
		return ("Successfully added new task: " + task.getTaskName());
		}
	
		
	private String executeDelete(TreeMap<String,Task> taskIDmap, Command command) throws Exception {
		
		String[] ids = command.getTaskIDsToDelete();
		
		ArrayList<String> allIDs = new ArrayList<String>();
		
		for (String id: ids) {
			if (isNumeric(id)) {
				allIDs.add("T" + id);
				allIDs.add("F" + id);
				allIDs.add("O" + id);
			} else {
				allIDs.add(id.toUpperCase());
			}
		}
		
		ArrayList<Task> tasks = new ArrayList<Task>();
		
		for (String id: allIDs ) {
			Task task = taskIDmap.get(id);
			if ((task!=null)&&(!tasks.contains(task))) {
				tasks.add(task);
			}
		}
		
		if (tasks.isEmpty()) {
			return "All ids are invalid";
		} else {
			ArrayList<Task> newTasks = new ArrayList<Task>();
			
			SimpleCommand deleteCommand = new SimpleCommand(tasks,newTasks);
			SimpleCommand undoCommand = new SimpleCommand(newTasks,tasks);
			addHistory(undoCommand);
			
			executeSimpleCommand(deleteCommand);
			
			return (tasks.size() + " tasks deleted from the Calendar");
		}		
	}
	
	private String executeUpdate(TreeMap<String, Task> taskIDmap, Command command) throws Exception {
		String id = command.getTaskID();
		id = id.toUpperCase();
		
		if (!taskIDmap.containsKey(id)) {
			return "Invalid index to update.";
		} 
		
		Task oldTask = taskIDmap.get(id);
		Task newTask = oldTask.clone();

		if (!command.getTaskName().equals("")) {
			newTask.setTaskName(command.getTaskName());
		}

		if (command.getTaskStartDate()!=null) {
			assert command.getTaskEndDate() != null;
			newTask.setDates(command.getTaskStartDate(), command.getTaskEndDate(),command.getRecurPattern(),command.getRecurPeriod(),null);
		}
		
		if (command.getTaskTagsToAdd()!=null) {
			for (String tag : command.getTaskTagsToAdd()) {
				newTask.addTag(tag);
			}
		}
		
		if (command.getTaskTagsToRemove()!=null) {
			for (String tag : command.getTaskTagsToRemove()) {
				newTask.removeTag(tag);
			}
		}
		
		ArrayList<Task> oldTasks = new ArrayList<Task> ();
		ArrayList<Task> newTasks = new ArrayList<Task> ();
		
		oldTasks.add(oldTask);
		newTasks.add(newTask);
		
		SimpleCommand updateCommand = new SimpleCommand(oldTasks,newTasks);
		SimpleCommand undoCommand = new SimpleCommand(newTasks,oldTasks);
		
		addHistory(undoCommand);
		
		executeSimpleCommand(updateCommand);
		
		return "Updated successfully";
	}
	
	private String executeComplete(TreeMap<String,Task> taskIDmap, Command command) throws Exception {
		
		String[] ids = command.getTaskIDsToComplete();
		
		ArrayList<Task> oldTasks = new ArrayList<Task>();
		
		ArrayList<String> allIDs = new ArrayList<String>();
		
		for (String id: ids) {
			if (isNumeric(id)) {
				allIDs.add("T" + id);
				allIDs.add("F" + id);
				allIDs.add("O" + id);
			} else {
				allIDs.add(id.toUpperCase());
			}
		}
		
		for (String id: allIDs ) {
			Task task = taskIDmap.get(id);
			if ((task!=null)&&(!oldTasks.contains(task))) {
				oldTasks.add(task);
			}
		}

		if (oldTasks.isEmpty()) {
			return "All ids are invalid";
		} else {
			ArrayList<Task> newTasks = new ArrayList<Task> ();
			
			for (Task task: oldTasks) {
				Task completedTask = task.clone();
				completedTask.setCompleted();
				newTasks.add(completedTask);
				}
		
			SimpleCommand completeCommand = new SimpleCommand(oldTasks,newTasks);
			SimpleCommand undoCommand = new SimpleCommand(newTasks,oldTasks);
			
			addHistory(undoCommand);
			executeSimpleCommand(completeCommand);
			return (oldTasks.size() + " tasks completed.");
		}		
	}

	
	private String executeUndo(TreeMap<String, Task> taskIDmap, Command command) throws Exception {
		if (histories_.empty()) {
			return "Undo not available";
		} else {
			SimpleCommand undoCommand = histories_.pop();
			future_.add(undoCommand);
			executeSimpleCommand(undoCommand);
			return "Undo successfully";
		}
	}
	
	private String executeRedo(TreeMap<String, Task> taskIDmap, Command command) throws Exception {
		if (future_.isEmpty()) {
			return "Redo not available";
		} else {
			SimpleCommand undoCommand = future_.pop();
			histories_.add(undoCommand);
			reverse(undoCommand);
		
			return "Redo successfully";
		}
	}
	
	private void executeSimpleCommand(SimpleCommand command) throws Exception {
		ArrayList<Task> oldTasks = command.getOldTasks();
		ArrayList<Task> newTasks = command.getNewTasks();
		
		for (Task task: oldTasks) {
			storage_.delete(task);
		}
		
		for (Task task: newTasks) {
			storage_.insert(task);
		}	
	}
	
	private void reverse(SimpleCommand command) throws Exception {
		ArrayList<Task> oldTasks = command.getOldTasks();
		ArrayList<Task> newTasks = command.getNewTasks();
		
		for (Task task: newTasks) {
			storage_.delete(task);
		}
		
		for (Task task: oldTasks) {
			storage_.insert(task);
		}	
	}
	
	private static boolean isNumeric(String str)
	{
	  NumberFormat formatter = NumberFormat.getInstance();
	  ParsePosition pos = new ParsePosition(0);
	  formatter.parse(str, pos);
	  return str.length() == pos.getIndex();
	}
}
