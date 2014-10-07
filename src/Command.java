import java.util.LinkedList;

public class Command {
	public enum COMMAND_TYPE {
		ADD, EDIT, DELETE, LIST, SEARCH, COMPLETE, TAG, INVALID
	}

	private COMMAND_TYPE commandType;
	private String taskID;
	private String taskName;
	private String taskDueDate;
	private LinkedList<String> taskTags;

	public void setCommandType(COMMAND_TYPE parsedCommandType) {
		commandType = parsedCommandType;
	}

	public COMMAND_TYPE getCommandType() {
		return commandType;
	}
}