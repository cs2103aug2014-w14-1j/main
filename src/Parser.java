import java.util.Arrays;

public class Parser {

	private String command;

	public enum COMMAND_TYPE {
		ADD, EDIT, DELETE, LIST, SEARCH, COMPLETE, TAG, INVALID
	}

	private COMMAND_TYPE commandType;

	public enum RECURRING_TYPE {
		INTERVAL, DAY_OF_MONTH, WEEK_OF_MONTH, ANNUAL
	}

	private String[] addCommands = {"add"};
	private String[] editCommands = {"edit"};
	private String[] deleteCommands = {"delete"};
	private String[] listCommands = {"list"};
	private String[] searchCommands = {"search"};
	private String[] completeCommands = {"complete"};

	public void parseCommand(String userCommand) {
		command = userCommand;
		String commandTypeString = getFirstWord(command);
		commandType = parserCommandType(commandTypeString);
		System.out.println(isValidCommand());
	}

	public boolean isValidCommand() {
		return commandType != COMMAND_TYPE.INVALID;
	}
	
	private String getFirstWord(String input) {
		return input.split("\\s+")[0];
	}

	private COMMAND_TYPE parserCommandType(String commandTypeString) {
		if (isAddCommand(commandTypeString)) {
			return COMMAND_TYPE.ADD;
		} else if (isEditCommand(commandTypeString)) {
			return COMMAND_TYPE.EDIT;
		} else if (isDeleteCommand(commandTypeString)) {
			return COMMAND_TYPE.DELETE;
		} else if (isListCommand(commandTypeString)) {
			return COMMAND_TYPE.LIST;
		} else if (isSearchCommand(commandTypeString)) {
			return COMMAND_TYPE.SEARCH;
		} else if (isCompleteCommand(commandTypeString)) {
			return COMMAND_TYPE.COMPLETE;
		} else {
			return COMMAND_TYPE.INVALID;
		}
	}

	private boolean isAddCommand(String commandTypeString) {
		return containsCommand(commandTypeString, addCommands);
	}

	private boolean isEditCommand(String commandTypeString) {
		return containsCommand(commandTypeString, editCommands);
	}

	private boolean isDeleteCommand(String commandTypeString) {
		return containsCommand(commandTypeString, deleteCommands);
	}

	private boolean isListCommand(String commandTypeString) {
		return containsCommand(commandTypeString, listCommands);
	}

	private boolean isSearchCommand(String commandTypeString) {
		return containsCommand(commandTypeString, searchCommands);
	}

	private boolean isCompleteCommand(String commandTypeString) {
		return containsCommand(commandTypeString, completeCommands);
	}

	private boolean containsCommand(String commandTypeString, String[] commands) {
		return Arrays.asList(commands).contains(commandTypeString.toLowerCase());
	}
	}
}