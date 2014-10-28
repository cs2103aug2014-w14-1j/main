import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Stack;
import java.util.TreeMap;

import org.json.JSONException;

public class LogicHandler {	
	
	private enum LOGIC_TYPE {
		ADD, DELETE, UPDATE
	}
	
	private class SimpleCommand {
		private LOGIC_TYPE logicType_;
		private ArrayList<Task> tasks_;
	
		public SimpleCommand(LOGIC_TYPE logicType, ArrayList<Task> tasks) {
			logicType_ = logicType;
			tasks_ = tasks;
		}
		
		public LOGIC_TYPE getLogicType() {
			return logicType_;
		}
		
		public ArrayList<Task> getTasks() {
			return tasks_;
		}
	}

	private Storage storage_;
	private Stack<SimpleCommand> histories_;

	public LogicHandler(Storage storage) {
		storage_ = storage;
		histories_ = new Stack<SimpleCommand>();
	}
	
	public String executeCommand(TreeMap<String,Task> taskIDmap, Command command) throws Exception {
		Command.COMMAND_TYPE commandType = command.getCommandType();
		switch(commandType) {
		case ADD: {
			return executeAdd(command);
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
		case COMPLETE: {
			return executeComplete(taskIDmap, command);
		}
		case EXIT: {
			System.exit(0);
		}
		default:
			return "";
		}		
	}
	
	private String executeAdd(Command command) throws Exception {
		
		if (command.getTaskName().equals("")) {
			throw new Exception("Can not add a task with no descriptions");
		}
		
		Task task = new Task();
		task.setTaskName(command.getTaskName());
		
		if (command.getTaskDueDate()!= null) {
			task.setDate(command.getTaskDueDate());
		}
		
		if (command.getTaskTags()!=null) {
			for (String tag : command.getTaskTags()) {
				task.addTag(tag);
			}
		}
		
		ArrayList<Task> tasks = new ArrayList<Task>();
		tasks.add(task);
		
		SimpleCommand addCommand = createAddCommand(tasks);
		SimpleCommand undoCommand = createDeleteCommand(tasks);
		histories_.add(undoCommand);
		
		executeSimpleCommand(addCommand);
		
		return ("Successfully added new task: " + task.getTaskName());
		
	} 
		
	private String executeDelete(TreeMap<String,Task> taskIDmap, Command command) throws Exception {
		
		String[] ids = command.getTaskIDsToDelete();
		
		ArrayList<Task> tasks = new ArrayList<Task>();
		
		for (String id: ids) {
			Task task = taskIDmap.get(id);
			if ((task!=null)|(tasks.contains(task))) {
				tasks.add(task);
			}
		}
		
		if (tasks.isEmpty()) {
			return "All ids are invalid";
		} else {
			SimpleCommand deleteCommand = createDeleteCommand(tasks);
			SimpleCommand undoCommand = createAddCommand(tasks);
			histories_.add(undoCommand);
			
			executeSimpleCommand(deleteCommand);
			
			return (tasks.size() + " tasks deleted from the Calendar");
		}		
	}
	
	private String executeUpdate(TreeMap<String, Task> taskIDmap, Command command) throws Exception {
		String id = command.getTaskID();
		if (!taskIDmap.containsKey(id)) {
			return "Invalid index to update.";
		} 
		
		Task oldTask = taskIDmap.get(id);
		Task newTask = oldTask.clone();

		if (!command.getTaskName().equals("")) {
			newTask.setTaskName(command.getTaskName());
		}

		if (command.getTaskDueDate() != null) {
			Calendar date = command.getTaskDueDate();
			newTask.setDate(date);
		}
		
		ArrayList<Task> updatedState = new ArrayList<Task> ();
		ArrayList<Task> oldState = new ArrayList<Task> ();
		
		updatedState.add(oldTask);
		updatedState.add(newTask);
		
		oldState.add(newTask);
		oldState.add(oldTask);
		
		SimpleCommand updateCommand = createUpdateCommand(updatedState);
		SimpleCommand undoCommand = createUpdateCommand(oldState);
		histories_.add(undoCommand);
		
		executeSimpleCommand(updateCommand);
		
		return "Updated successfully";
	}
	
	private String executeComplete(TreeMap<String,Task> taskIDmap, Command command) throws Exception {
		
		String[] ids = command.getTaskIDsToComplete();
		
		ArrayList<Task> tasks = new ArrayList<Task>();
		
		for (String id: ids) {
			Task task = taskIDmap.get(id);
			if ((task!=null)|(!tasks.contains(task))) {
				tasks.add(task);
			}
		}

		if (tasks.isEmpty()) {
			return "All ids are invalid";
		} else {
			ArrayList<Task> completedTasks = new ArrayList<Task> ();
			ArrayList<Task> incompleteTasks = new ArrayList<Task> ();
			
			for (Task task: tasks) {
				Task completedTask = task.clone();
				completedTask.setCompleted();
				
				completedTasks.add(task);
				completedTasks.add(completedTask);
				
				incompleteTasks.add(completedTask);
				incompleteTasks.add(task);
				}
			
			SimpleCommand completeCommand = createUpdateCommand(completedTasks);
			SimpleCommand undoCommand = createUpdateCommand(incompleteTasks);
			histories_.add(undoCommand);
			executeSimpleCommand(completeCommand);
			return (tasks.size() + " tasks completed.");
		}		
	}
	
	private String executeUndo(TreeMap<String, Task> taskIDmap, Command command) throws Exception {
		if (histories_.empty()) {
			return "Undo not available";
		} else {
			SimpleCommand undoCommand = histories_.pop();
			executeSimpleCommand(undoCommand);
			return "Undone successfully";
		}
	}
	
	private SimpleCommand createAddCommand(ArrayList<Task> tasks) {
		SimpleCommand addCommand = new SimpleCommand(LOGIC_TYPE.ADD, tasks);
		return addCommand;
	}
	
	private SimpleCommand createDeleteCommand(ArrayList<Task> tasks) {
		SimpleCommand deleteCommand = new SimpleCommand(LOGIC_TYPE.DELETE, tasks);
		return deleteCommand;
	}
	
	private SimpleCommand createUpdateCommand(ArrayList<Task> tasks) {
		SimpleCommand updateCommand = new SimpleCommand(LOGIC_TYPE.UPDATE, tasks);
		return updateCommand;
	}
	
	private void executeSimpleCommand(SimpleCommand command) throws Exception {
		LOGIC_TYPE logicType= command.getLogicType();
		ArrayList<Task> tasks = command.getTasks();
		
		switch(logicType) {
		case ADD: {
			for (Task task: tasks) {
				storage_.insert(task);
				}
			return;
			}
		case DELETE: {
			for (Task task: tasks) {
				storage_.delete(task);
				}
			return;
			}
		case UPDATE: {
			for (int i=0; i<tasks.size(); i+=2) {
				storage_.delete(tasks.get(i));
				storage_.insert(tasks.get(i+1));
				}
			}
		}
	}
}
