import java.io.IOException;
import java.util.ArrayList;
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
			return "";
			}
		case EDIT: {
			return "";
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
			task.addTaskDatesTimes(command.getTaskDueDate());
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
			if (task!=null) {
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
		
	
	private SimpleCommand createAddCommand(ArrayList<Task> tasks) {
		SimpleCommand addCommand = new SimpleCommand(LOGIC_TYPE.ADD, tasks);
		return addCommand;
	}
	
	private SimpleCommand createDeleteCommand(ArrayList<Task> tasks) {
		SimpleCommand deleteCommand = new SimpleCommand(LOGIC_TYPE.DELETE, tasks);
		return deleteCommand;
	}
	
	private SimpleCommand createUpdateCommand(Task oldTask, Task updatedTask) {
		ArrayList<Task> tasks = new ArrayList<Task> ();
		tasks.add(oldTask);
		tasks.add(updatedTask);
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
			storage_.delete(tasks.get(0));
			storage_.insert(tasks.get(1));
			return;
			}
		} 
	}
	
	
}
