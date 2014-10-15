import org.ocpsoft.prettytime.nlp.PrettyTimeParser;
import org.ocpsoft.prettytime.shade.org.apache.commons.lang.StringUtils;

import java.util.*;

import static org.mentaregex.Regex.match;

public class Parser {

	private String command;
	private Command commandObj;
	private Command.COMMAND_TYPE commandType;
	private final int TYPO_DISTANCE = 1;

	private String[] addCommands = {"add","insert"};
	private String[] editCommands = {"edit","update","change","modify"};
	private String[] deleteCommands = {"delete","remove","destroy"};
	private String[] listCommands = {"list"};
	private String[] searchCommands = {"search"};
	private String[] completeCommands = {"complete"};
	private String[] exitCommands = {"quit"};

	public Command parseCommand(String userCommand) {
		command = userCommand;
		String commandTypeString = getFirstWord(command).toLowerCase();
		commandType = parserCommandType(commandTypeString);
		if (isValidCommand()) {
			return generateCommandObj();
		} else {
			return null;
		}
	}

	private boolean isValidCommand() {
		return commandType != Command.COMMAND_TYPE.INVALID;
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
		} else if (isExitCommand(commandTypeString)) {
			return Command.COMMAND_TYPE.EXIT;
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

	private boolean isExitCommand(String commandTypeString) {
		return containsCommand(commandTypeString, exitCommands);
	}

	private boolean containsCommand(String commandTypeString, String[] commands) {
		boolean result = false;
		for (String command: commands) {
			if (StringUtils.getLevenshteinDistance(commandTypeString, command) <= TYPO_DISTANCE) {
				result = true;
			}
		}
		return result;
	}

	private Command generateCommandObj() {
		commandObj = new Command();
		commandObj.setCommandType(commandType);
		String commandDetails = removeCommand();
		if (!commandDetails.equals("")) {
			switch (commandType) {
				case ADD:
					generateAddCommandObj(commandDetails);
					break;
				case EDIT:
					generateEditCommandObj(commandDetails);
					break;
				case DELETE:
					generateDeleteCommandObj(commandDetails);
					break;
			}
		}
		return commandObj;
	}

	private String removeCommand() {
		if (matches(command, "\\s+")) {
			return command.replaceFirst("^(\\w+)\\s+","");
		} else {
			return "";
		}
	}

	private String[] dateIdentifiers = {"to","until","til","till","by","due","on","from"};

	private void generateAddCommandObj(String commandDetails) {
		commandObj.setTaskDueDate(parseLatestDate(commandDetails));
		commandObj.setTaskName(removeLeadingAndClosingPunctuation(parseTaskName(commandDetails)));
	}

	private void generateEditCommandObj(String commandDetails) {
		String[] IDs = parseTaskID(commandDetails);
		commandObj.setTaskID(IDs[0]);
		commandDetails = removeTaskID(commandDetails);
		commandObj.setTaskDueDate(parseLatestDate(commandDetails));
		commandObj.setTaskName(removeLeadingAndClosingPunctuation(parseTaskName(commandDetails)));
	}

	private void generateDeleteCommandObj(String commandDetails) {
		commandObj.setTaskIDsToDelete(parseTaskID(commandDetails));
	}

	private Calendar parseLatestDate(String commandDetails) {
		List<Date> dates = new PrettyTimeParser().parse(commandDetails);
		if (dates.size() == 0) {
			return null;
		} else {
			Comparator<Date> dateComparator = new Comparator<Date>() {
				@Override
				public int compare(Date o1, Date o2) {
					return o2.compareTo(o1);
				}
			};
			dates.sort(dateComparator);
			return DateToCalendar(dates.get(0));
		}
	}

	public static Calendar DateToCalendar(Date date){
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return cal;
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
		return match(commandDetails, "/([TFRtfr]\\d+)/g");
	}

	private String removeTaskID(String commandDetails) {
		return commandDetails.split("\\s+", 2)[1];
	}

	private String removeLeadingAndClosingPunctuation(String input) {
		return input.replaceFirst("^[^a-zA-Z]+", "").replaceAll("[^a-zA-Z]+$", "");
	}
}