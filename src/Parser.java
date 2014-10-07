import java.util.*;

import org.ocpsoft.prettytime.nlp.PrettyTimeParser;
import static org.mentaregex.Regex.*;

public class Parser {

	private String command;
	private Command commandObj = new Command();
	private Command.COMMAND_TYPE commandType;

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
		if (isValidCommand()) {
			generateCommandObj();
		}
	}

	public boolean isValidCommand() {
		return commandType != Command.COMMAND_TYPE.INVALID;
	}
	
	public Command getCommandObj() {
		return commandObj;
	}
	
	private String getFirstWord(String input) {
		return input.split("\\s+")[0];
	}

	private Command.COMMAND_TYPE parserCommandType(String commandTypeString) {
		if (isAddCommand(commandTypeString)) {
			return Command.COMMAND_TYPE.ADD;
		} else if (isEditCommand(commandTypeString)) {
			return Command.COMMAND_TYPE.EDIT;
		} else if (isDeleteCommand(commandTypeString)) {
			return Command.COMMAND_TYPE.DELETE;
		} else if (isListCommand(commandTypeString)) {
			return Command.COMMAND_TYPE.LIST;
		} else if (isSearchCommand(commandTypeString)) {
			return Command.COMMAND_TYPE.SEARCH;
		} else if (isCompleteCommand(commandTypeString)) {
			return Command.COMMAND_TYPE.COMPLETE;
		} else {
			return Command.COMMAND_TYPE.INVALID;
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

	private void generateCommandObj() {
		commandObj.setCommandType(commandType);
		String commandDetails = command.replaceFirst("^(\\w+)\\s+","");
		switch (commandType) {
			case ADD:
				generateAddCommandObj(commandDetails);
				break;
			case EDIT:
				generateEditCommandObj(commandDetails);
				break;
		}
	}

	private String[] dateIdentifiers = {"to","until","til","till","by","due"};

	private void generateAddCommandObj(String commandDetails) {
		commandObj.setTaskDueDate(parseLatestDate(commandDetails));
		commandObj.setTaskName(parseTaskName(commandDetails));
	}

	private void generateEditCommandObj(String commandDetails) {
		String[] IDs = parseTaskID(commandDetails);
		commandObj.setTaskID(IDs[0]);
		commandDetails = removeTaskID(commandDetails);
		commandObj.setTaskDueDate(parseLatestDate(commandDetails));
		commandObj.setTaskName(parseTaskName(commandDetails));
	}

	private Date parseLatestDate(String commandDetails) {
		List<Date> dates = new PrettyTimeParser().parse(commandDetails);
		Comparator<Date> dateComparator = new Comparator<Date>() {
			@Override
			public int compare(Date o1, Date o2) {
				return o2.compareTo(o1);
			}
		};
		dates.sort(dateComparator);
		return dates.get(0);
	}

	private String parseTaskName(String commandDetails) {
		String taskName = commandDetails;
		String[] commandArray = commandDetails.split("\\s+");
		int dateIndex = -1;
		for (int i = 0; i < commandArray.length; i++) {
			if (Arrays.asList(dateIdentifiers).contains(commandArray[i])) {
				if (i+1 < commandArray.length) {
					List<Date> parse = new PrettyTimeParser().parse(commandArray[i+1]);
					if (parse.size() > 0) {
						dateIndex = i;
						break;
					}
				}
			} else {
				List<Date> parse = new PrettyTimeParser().parse(commandArray[i]);
				if (parse.size() > 0) {
					dateIndex = i;
					break;
		}
	}
	}
		if (dateIndex > -1) {
			taskName = "";
			for (int i = 0; i < dateIndex; i++) {
				taskName += commandArray[i];
				taskName += i < dateIndex ? " " : "";
			}
		}
		return taskName;
	}

	private String[] parseTaskID(String commandDetails) {
		return match(commandDetails, "/([TFR]\\d+)/g");
	}

	private String removeTaskID(String commandDetails) {
		return commandDetails.split("\\s+", 2)[1];
	}
}