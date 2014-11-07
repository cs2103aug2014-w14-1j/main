//@author A0112059N
package speed.controller;

import speed.parser.Command;
import speed.storage.Storage;
import speed.task.Task;

import java.util.ArrayList;
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
	private final String ERROR_NO_NAME = "Cannot add a task with no description.";
	private final String ERROR_NO_CHANGE = "No change was made.";
	private final String ERROR_INVALID_ID = "Invalid id to update";
	private final String ERROR_INVALID_IDS = "All ids are invalid";
	private final String ERROR_INVALID_UPDATE = "Invalid update command";
	
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
		
		String taskName = command.getTaskName();
		
		if (taskName == null) {
			return ERROR_NO_NAME;
		} 
		
		String[] name = taskName.split("\\s+");
		if ((name.length == 0) || (name[0].equals(""))) {
			return ERROR_NO_NAME;
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
			allIDs.add("T" + id);
			allIDs.add("F" + id);
			allIDs.add("O" + id);
			allIDs.add(id.toUpperCase());
		}
		
		ArrayList<Task> tasks = new ArrayList<Task>();
		
		for (String id: allIDs ) {
			Task task = taskIDmap.get(id);
			if ((task!=null)&&(!tasks.contains(task))) {
				Task parent_task = storage_.getParentTask(task);
				if (!tasks.contains(parent_task)) {
					tasks.add(parent_task);
				}
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
		
		if (id == null) {
			return ERROR_INVALID_UPDATE;
		}
		
		id = id.toUpperCase();
		
		if (!taskIDmap.containsKey(id)) {
			return ERROR_INVALID_ID;
		} 
		
		Task oldTask = taskIDmap.get(id);
		Task newTask = oldTask.clone();
		
		boolean unchanged = true;

		if (command.getTaskName()!=null && !command.getTaskName().equals("")) {
			newTask.setTaskName(command.getTaskName());
			unchanged = false;
		}

		if (command.getTaskEndDate()!=null) {
			newTask.setDates(command.getTaskStartDate(), command.getTaskEndDate(),command.getRecurPattern(),command.getRecurPeriod(),null);
			unchanged = false;
		}
		
		if (command.getTaskTagsToAdd()!=null) {
			for (String tag : command.getTaskTagsToAdd()) {
				newTask.addTag(tag);
			}
			unchanged = false;
		}
		
		if (command.getTaskTagsToRemove()!=null) {
			for (String tag : command.getTaskTagsToRemove()) {
				newTask.removeTag(tag);
			}
			unchanged = false;
		}
		
		if (unchanged) {
			return ERROR_NO_CHANGE;
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
		ArrayList<Task> newTasks = new ArrayList<Task> ();
		
		ArrayList<String> allIDs = new ArrayList<String>();
		
		for (String id: ids) {
			allIDs.add("T" + id);
			allIDs.add("F" + id);
			allIDs.add("O" + id);
			allIDs.add(id.toUpperCase());
		}
		
		for (String id: allIDs ) {
			Task task = taskIDmap.get(id);
			if ((task!=null)&&(!oldTasks.contains(task))) {
				Task parent_task = storage_.getParentTask(task);
				if (!oldTasks.contains(parent_task)) {
					oldTasks.add(parent_task);
				}
				Task completedTask = task.clone();
				completedTask.setCompleted();
				
				for (int i = 0; i < newTasks.size(); i++) {
					if (newTasks.get(i).getId() == completedTask.getId()) {
						newTasks.remove(i);
						i--;
					}
				}
				
				newTasks.add(completedTask);
			}
		}

		if (oldTasks.isEmpty()) {
			return ERROR_INVALID_IDS;
		} else {
		
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
}
