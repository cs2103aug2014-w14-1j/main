import org.ocpsoft.prettytime.nlp.PrettyTimeParser;
import org.ocpsoft.prettytime.shade.org.apache.commons.lang.StringUtils;

import java.util.*;

import static org.mentaregex.Regex.match;
import static org.mentaregex.Regex.matches;

public class Parser {

	private String command;
	private Command commandObj;
	private Command.COMMAND_TYPE commandType;
	private final int TYPO_DISTANCE = 1;

	private String[] addCommands = {"add","insert"};
	private String[] editCommands = {"edit","update","change","modify"};
	private String[] deleteCommands = {"delete","remove","destroy"};
	private String[] listCommands = {"list"};
	private String[] searchCommands = {"search","find"};
	private String[] completeCommands = {"complete"};
	private String[] undoCommands = {"undo"};
	private String[] exitCommands = {"quit"};

	public Command parseCommand(String userCommand) {
		command = userCommand;
		String commandTypeString = getFirstWord(command).toLowerCase();
		commandType = parserCommandType(commandTypeString);
		return generateCommandObj();
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
		} else if (isUndoCommand(commandTypeString)) {
			return Command.COMMAND_TYPE.UNDO;
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

	private boolean isUndoCommand(String commandTypeString) {
		return containsCommand(commandTypeString, undoCommands);
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
				case LIST:
					generateListCommandObj(commandDetails);
					break;
				case SEARCH:
					generateSearchCommandObj(commandDetails);
					break;
				case COMPLETE:
					generateCompleteCommandObj(commandDetails);
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
		assert (!commandDetails.trim().equals("")) : "commandDetails is empty!";
		commandObj.setTaskDueDate(parseLatestDate(commandDetails));
		commandObj.setTaskName(removeLeadingAndClosingPunctuation(parseTaskName(commandDetails)));
	}

	private void generateEditCommandObj(String commandDetails) {
		assert (!commandDetails.trim().equals("")) : "commandDetails is empty!";
		String[] IDs = parseTaskID(commandDetails);
		commandObj.setTaskID(IDs[0]);
		commandDetails = removeTaskID(commandDetails);
		commandObj.setTaskDueDate(parseLatestDate(commandDetails));
		commandObj.setTaskName(removeLeadingAndClosingPunctuation(parseTaskName(commandDetails)));
	}

	private void generateDeleteCommandObj(String commandDetails) {
		assert (!commandDetails.trim().equals("")) : "commandDetails is empty!";
		commandObj.setTaskIDsToDelete(parseTaskID(commandDetails));
	}

	private void generateListCommandObj(String commandDetails) {
		assert (!commandDetails.trim().equals("")) : "commandDetails is empty!";
		ArrayList<Calendar> dates = parseDates(commandDetails);
		if (dates.size() == 1) {
			Calendar startDate = startOfDay(dates.get(0));
			commandObj.setSearchStartDate(startDate);
			Calendar endDate = endOfDay((Calendar) startDate.clone());
			commandObj.setSearchEndDate(endDate);
		} else if (dates.size() > 1) {
			commandObj.setSearchStartDate(dates.get(0));
			commandObj.setSearchEndDate(dates.get(dates.size()-1));
		}
	}

	private void generateSearchCommandObj(String commandDetails) {
		assert (!commandDetails.trim().equals("")) : "commandDetails is empty!";
		String[] array = commandDetails.split("\\s+");
		ArrayList<String> keywords = new ArrayList<String>();
		for (String keyword: array) {
			keywords.add(removeLeadingAndClosingPunctuation(keyword));
		}
		commandObj.setSearchKeywords(keywords);
	}

	private void generateCompleteCommandObj(String commandDetails) {
		assert (!commandDetails.trim().equals("")) : "commandDetails is empty!";
		commandObj.setTaskIDsToComplete(parseTaskID(commandDetails));
	}

	private Calendar startOfDay(Calendar cal) {
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		return cal;
	}

	private Calendar endOfDay(Calendar cal) {
		cal.set(Calendar.HOUR_OF_DAY, 23);
		cal.set(Calendar.MINUTE, 59);
		cal.set(Calendar.SECOND, 59);
		return cal;
	}

	private ArrayList<Calendar> parseDates(String input) {
		List<Date> dates = new PrettyTimeParser().parse(input);
		ArrayList<Calendar> result = new ArrayList<Calendar>();
		for (Date date: dates) {
			result.add(DateToCalendar(date));
		}
		return result;
	}

	private Calendar parseLatestDate(String commandDetails) {
		assert (!commandDetails.trim().equals("")) : "commandDetails is empty!";
		ArrayList<Calendar> dates = parseDates(commandDetails);
		if (dates.size() == 0) {
			return null;
		} else {
			Collections.sort(dates);
			return dates.get(dates.size()-1);
		}
	}

	public Calendar DateToCalendar(Date date){
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return cal;
	}

	private String parseTaskName(String commandDetails) {
		assert (!commandDetails.trim().equals("")) : "commandDetails is empty!";
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