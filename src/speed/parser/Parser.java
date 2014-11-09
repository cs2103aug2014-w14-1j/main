// @author A0116150X
package speed.parser;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;

import static org.mentaregex.Regex.match;
import static org.mentaregex.Regex.matches;

public class Parser {

	DateTimeParser dateTimeParser = new DateTimeParser();

	private String command;
	private Command commandObj;
	private Command.COMMAND_TYPE commandType;
	private final int TYPO_DISTANCE = 1;

	private String[] addCommands = {"add", "insert"};
	private String[] editCommands = {"edit", "update", "change", "modify", "make"};
	private String[] deleteCommands = {"delete", "remove", "destroy", "del", "rm"};
	private String[] listCommands = {"list", "ls", "speed/view"};
	private String[] searchCommands = {"search", "find"};
	private String[] completeCommands = {"complete", "done", "finish", "fin"};
	private String[] undoCommands = {"undo"};
	private String[] redoCommands = {"redo"};
	private String[] exitCommands = {"quit", "exit"};
	private String[] testCommands = {"runtest", "systest"};

	private String taskIDIdentifier = "[TROtro]";
	private String completed = "completed?(?:\\s+tasks?)?";
	private String floating = "floating(?:\\s+tasks?)?|reminders?";
	private String overdue = "overdue(?:\\s+tasks?)?";

	public Command parseCommand(String userCommand) {
		command = userCommand;
		String commandTypeString = getFirstWord(command).trim().toLowerCase();
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
		} else if (isRedoCommand(commandTypeString)) {
			return Command.COMMAND_TYPE.REDO;
		} else if (isExitCommand(commandTypeString)) {
			return Command.COMMAND_TYPE.EXIT;
		} else if (isTestCommand(commandTypeString)) {
			return Command.COMMAND_TYPE.TEST;
		} else {
			return Command.COMMAND_TYPE.DEFAULT;
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

	private boolean isRedoCommand(String commandTypeString) {
		return containsCommand(commandTypeString, redoCommands);
	}

	private boolean isExitCommand(String commandTypeString) {
		return containsCommand(commandTypeString, exitCommands);
	}

	private boolean isTestCommand(String commandTypeString) {
		return containsCommand(commandTypeString, testCommands);
	}

	private boolean containsCommand(String commandTypeString, String[] commands) {
		boolean result = false;
		for (String command : commands) {
			if (isFalsePositive("edit", "exit", command, commandTypeString)) {
				return false;
			} else if (isFalsePositive("fin", "find", command, commandTypeString)) {
				return false;
			} else if (StringUtils.getLevenshteinDistance(commandTypeString, command) <= TYPO_DISTANCE) {
				result = true;
			}
		}
		return result;
	}

	private boolean isFalsePositive(String correctA, String correctB, String checkA, String checkB) {
		if (checkA.equalsIgnoreCase(correctA) && checkB.equalsIgnoreCase(correctB)) {
			return true;
		} else if (checkA.equalsIgnoreCase(correctB) && checkB.equalsIgnoreCase(correctA)) {
			return true;
		} else {
			return false;
		}
	}

	private Command generateCommandObj() {
		commandObj = new Command();
		commandObj.setCommandType(commandType);
		String commandDetails = removeCommand();
		if (!commandDetails.equals("")) {
			switch (commandType) {
				case ADD:
				case DEFAULT:
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
		if (commandType == Command.COMMAND_TYPE.DEFAULT) {
			return command;
		} else if (matches(command, "\\s+")) {
			return command.replaceFirst("^(\\w+)\\s+", "");
		} else {
			return "";
		}
	}

	private void generateAddCommandObj(String commandDetails) {
		assert (!commandDetails.trim().equals("")) : "commandDetails is empty!";
		commandObj.setTaskTagsToAdd(parseTaskTagsAddition(commandDetails));
		commandDetails = removeTaskTagsAddition(commandDetails);
		commandDetails = dateTimeParser.parseCommand(commandDetails, commandType, commandObj);
		commandObj.setTaskName(parseTaskName(commandDetails));
	}

	private void generateEditCommandObj(String commandDetails) {
		assert (!commandDetails.trim().equals("")) : "commandDetails is empty!";
		String[] IDs = parseTaskID(commandDetails);
		if (IDs != null) {
		commandObj.setTaskID(IDs[0]);
		}
		commandDetails = removeTaskID(commandDetails);
		commandObj.setTaskTagsToRemove(parseTaskTagsRemoval(commandDetails));
		commandDetails = removeTaskTagsRemoval(commandDetails);
		commandObj.setTaskTagsToAdd(parseTaskTagsAddition(commandDetails));
		commandDetails = removeTaskTagsAddition(commandDetails);
		commandDetails = dateTimeParser.parseCommand(commandDetails, commandType, commandObj);
		commandObj.setTaskName(parseTaskName(commandDetails));
	}

	private void generateDeleteCommandObj(String commandDetails) {
		assert (!commandDetails.trim().equals("")) : "commandDetails is empty!";
		commandObj.setTaskIDsToDelete(parseMultipleTaskID(commandDetails));
	}

	private void generateListCommandObj(String commandDetails) {
		assert (!commandDetails.trim().equals("")) : "commandDetails is empty!";
		commandDetails = parseTaskTypesToList(commandDetails);
		dateTimeParser.parseCommand(commandDetails, commandType, commandObj);
	}

	private void generateSearchCommandObj(String commandDetails) {
		assert (!commandDetails.trim().equals("")) : "commandDetails is empty!";
		commandObj.setSearchTags(parseTaskTagsAddition(commandDetails));
		commandDetails = removeTaskTagsAddition(commandDetails);
		commandDetails = dateTimeParser.parseCommand(commandDetails, commandType, commandObj);
		String[] array = commandDetails.trim().split("\\s+");
		ArrayList<String> keywords = new ArrayList<String>();
		for (String keyword : array) {
			keywords.add(removeLeadingAndClosingPunctuation(keyword));
		}
		commandObj.setSearchKeywords(keywords);
	}

	private void generateCompleteCommandObj(String commandDetails) {
		assert (!commandDetails.trim().equals("")) : "commandDetails is empty!";
		commandObj.setTaskIDsToComplete(parseMultipleTaskID(commandDetails));
	}

	private String parseTaskName(String commandDetails) {
		return removeLeadingAndClosingPunctuation(commandDetails).replaceAll("\"", "");
	}

	private String[] parseTaskID(String commandDetails) {
		return match(commandDetails, "/("+taskIDIdentifier+"\\d+)/g");
	}

	private String[] parseMultipleTaskID(String commandDetails) {
		ArrayList<String> IDs = parseRangeIDs(commandDetails);
		commandDetails = commandDetails.replaceAll("("+taskIDIdentifier+"?(\\d+))[\\s+]?(?:-|to)[\\s+]?("+taskIDIdentifier+"?(\\d+))", "");
		String[] singleIDs = match(commandDetails, "/\\b("+taskIDIdentifier+"?\\d+)\\b/g");
		if (singleIDs != null) {
			IDs.addAll(Arrays.asList(singleIDs));
		}
		String[] allIDs = IDs.size() == 0 ? null : IDs.toArray(new String[IDs.size()]);
		return allIDs;
	}

	private String parseTaskTypesToList(String commandDetails) {
		ArrayList<String> type = new ArrayList<String>();
		if (matches(commandDetails, "/("+completed+")/ig")) {
			type.add("complete");
			commandDetails = commandDetails.replaceAll(completed, "");
		}
		if (matches(commandDetails, "/("+floating+")/ig")) {
			type.add("floating");
			commandDetails = commandDetails.replaceAll(floating, "");
		}
		if (matches(commandDetails, "/("+overdue+")/ig")) {
			type.add("overdue");
			commandDetails = commandDetails.replaceAll(overdue, "");
		}
		commandObj.setSearchType(type);
		return commandDetails;
	}

	private ArrayList<String> parseRangeIDs(String commandDetails) {
		ArrayList<String> IDs = new ArrayList<String>();
		String[] rangeIDs = match(commandDetails, "/("+taskIDIdentifier+"?(\\d+))[\\s+]?(?:-|to)[\\s+]?("+taskIDIdentifier+"?(\\d+))/g");
		if (rangeIDs != null) {
			for (int i = 0; i < rangeIDs.length; i += 4) {
				int start = Integer.parseInt(rangeIDs[i + 1]);
				int end = Integer.parseInt(rangeIDs[i + 3]);
				String startID = rangeIDs[i];
				String endID = rangeIDs[i + 2];
				if (start > end) {
					start = Integer.parseInt(rangeIDs[i + 3]);
					end = Integer.parseInt(rangeIDs[i + 1]);
					startID = rangeIDs[i + 2];
					endID = rangeIDs[i];
				}
				IDs.add(startID);
				for (Integer j = start + 1; j < end; j++) {
					IDs.add(j.toString());
				}
				IDs.add(endID);
			}
		}
		return IDs;
	}

	private String removeTaskID(String commandDetails) {
		return commandDetails.replaceFirst(""+taskIDIdentifier+"\\d+", "");
	}

	private String[] parseTaskTagsAddition(String commandDetails) {
		return match(commandDetails, "/(\\B@[a-zA-Z0-9-]+)/g");
	}

	private String[] parseTaskTagsRemoval(String commandDetails) {
		String[] removalMatches = match(commandDetails, "/((?:removes?|rm)\\s?(?:@[a-zA-Z0-9-]+\\s?)+)/g");
		ArrayList<String> tagMatches = new ArrayList<String>();
		if (removalMatches != null) {
			for (String match : removalMatches) {
				tagMatches.addAll(Arrays.asList(parseTaskTagsAddition(match)));
			}
			String[] tags = new String[tagMatches.size()];
			for (int i = 0; i < tagMatches.size(); i++) {
				tags[i] = tagMatches.get(i);
			}
			return tags;
		} else {
			return null;
		}
	}

	private String removeTaskTagsAddition(String commandDetails) {
		return commandDetails.replaceAll("\\B@[a-zA-Z0-9-]+", "");
	}

	private String removeTaskTagsRemoval(String commandDetails) {
		return commandDetails.replaceAll("(?:removes?|rm)\\s?(?:@[a-zA-Z0-9-]+\\s?)+", "");
	}

	private String removeLeadingAndClosingPunctuation(String input) {
		return input.replaceFirst("^[^0-9a-zA-Z]+", "").replaceAll("[^0-9a-zA-Z]+$", "");
	}
}