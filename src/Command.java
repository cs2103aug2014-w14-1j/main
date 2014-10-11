import java.util.Calendar;
import java.util.LinkedList;

public class Command {
	public enum COMMAND_TYPE {
		ADD, EDIT, DELETE, LIST, SEARCH, COMPLETE, TAG, INVALID, EXIT
	}

	private COMMAND_TYPE commandType;
	private String taskID;
	private String taskName;
	private Calendar taskDueDate;
	private String[] taskIDsToDelete;
	private LinkedList<String> taskTags;

	public void setCommandType(COMMAND_TYPE parsedCommandType) {
		commandType = parsedCommandType;
	}

	public COMMAND_TYPE getCommandType() {
		return commandType;
	}

	public void setTaskID(String ID) {
		taskID = ID;
	}

	public String getTaskID() {
		return taskID;
	}

	public void setTaskName(String name) {
		taskName = name;
	}

	public String getTaskName() {
		return taskName;
	}

	public void setTaskDueDate(Calendar date) {
		taskDueDate = date;
	}

	public Calendar getTaskDueDate() {
		return taskDueDate;
	}

	public void setTaskIDsToDelete(String[] IDs) {
		taskIDsToDelete = IDs;
	}

	public String[] getTaskIDsToDelete() {
		return taskIDsToDelete;
	}
}