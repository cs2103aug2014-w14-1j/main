public class Parser {

	private String command;

	public enum COMMAND_TYPE {
		ADD, EDIT, DELETE, LIST, SEARCH, COMPLETE, TAG
	}

	public enum RECURRING_TYPE {
		INTERVAL, DAY_OF_MONTH, WEEK_OF_MONTH, ANNUAL
	}

	public void parseCommand(String userCommand) {
		command = userCommand;
	}

	public boolean isValidCommand() {
		return false;
	}
	
	private String getFirstWord(String input) {
		return input.split("\\s+")[0];

	}
}