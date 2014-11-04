import java.util.Calendar;

public class DateTimeParser extends DateTimeRegexHandler{

	/**
	 * Regex for parsing datetime in command
	 */
	private final String FROM_DATETIME = "(?:from\\s+)?("+DATETIME_FORMATS+")";
	private final String TO_DATETIME = "to\\s+("+DATETIME_FORMATS+")";
	private final String FROM_TO = FROM_DATETIME + "\\s+" + TO_DATETIME;
	private final String DUE = "(?:due(?:\\s+(?:on|in))?|by|in) (?:the )?("+DATETIME_FORMATS+")";
	private final String RECUR = "(?:recurs?\\s)?(?:every\\s?)(\\d\\s)?("+DAY+"|"+WEEK+"|"+MONTH+"|"+YEAR+")";
	private final String SIMPLE_RECUR = "(?:recurs?\\s)?("+DAILY+"|"+WEEKLY+"|"+MONTHLY+"|"+YEARLY+")";

	private DateParser dateParser = new DateParser();
	private String input;
	private String output;

	public String parseCommand(String command, Command.COMMAND_TYPE type, Command commandObj) {
		output = command;
		input = command.replaceAll("\"[^\"]+\"", "");
		if (dateMatches(input, FROM_TO)) {
			String match = dateMatch(input, FROM_TO)[0];
			String[] fromDate = dateMatch(match, FROM_DATETIME);
			Calendar startDate = dateParser.parse(fromDate[1], 0, 0, 0);
			String[] toDate = dateMatch(match, TO_DATETIME);
			Calendar endDate = dateParser.parse(toDate[1], 23, 59, 59);
			if (startDate != null && endDate != null) {
				switch (type) {
					case ADD:
					case EDIT:
					case DEFAULT:
						commandObj.setTaskStartDate(startDate);
						commandObj.setTaskEndDate(endDate);
						break;
					case LIST:
					case SEARCH:
						commandObj.setSearchStartDate(startDate);
						commandObj.setSearchEndDate(endDate);
						break;
				}
				output = output.replaceFirst(match, "");
				parseRecur(commandObj);
			}
		} else if (dateMatches(input, DUE)) {
			String[] dates = dateMatch(input, DUE);
			Calendar dueDate = dateParser.parse(dates[1], 23, 59, 59);
			if (dueDate != null) {
				commandObj.setTaskEndDate(dueDate);
				output = output.replaceFirst(dates[0], "");
				parseRecur(commandObj);
			}
		} else if (dateMatches(input, DATETIME_FORMATS)) {
			String[] dates = dateMatch(input, DATETIME_FORMATS);
			Calendar date = dateParser.parse(dates[0], 23, 59, 59);
			if (date != null) {
				switch (type) {
					case ADD:
					case EDIT:
					case DEFAULT:
						commandObj.setTaskEndDate(date);
						break;
					case LIST:
					case SEARCH:
						commandObj.setSearchStartDate(startOfDay(date));
						commandObj.setSearchEndDate(endOfDay((Calendar) date.clone()));
						break;
				}
				output = output.replaceFirst(dates[0], "");
				parseRecur(commandObj);
			}
		}
		return output.replaceAll("\"", "");
	}

	private void parseRecur(Command commandObj) {
		int recurPeriod = 1;
		int recurPattern = -1;
		if (dateMatches(input, RECUR)) {
			String[] recur = dateMatch(input, RECUR);
			recurPattern = parseRecurPattern(recur[2]);
			if (recur[1] != null) {
				recurPeriod = Integer.parseInt(recur[1].trim());
			}
			output = output.replaceFirst(RECUR, "");
		} else if (dateMatches(input, SIMPLE_RECUR)) {
			String[] recur = dateMatch(input, SIMPLE_RECUR);
			recurPattern = parseRecurPattern(recur[1]);
			output = output.replaceFirst(SIMPLE_RECUR, "");
		}
		commandObj.setRecurPattern(recurPattern);
		commandObj.setRecurPeriod(recurPeriod);
	}

	private int parseRecurPattern(String pattern) {
		int recurPattern = -1;
		if (dateMatches(pattern, DAY) || dateMatches(pattern, DAILY)) {
			recurPattern = Calendar.DAY_OF_YEAR;
		} else if (dateMatches(pattern, WEEK) || dateMatches(pattern, WEEKLY)) {
			recurPattern = Calendar.WEEK_OF_YEAR;
		} else if (dateMatches(pattern, MONTH) || dateMatches(pattern, MONTHLY)) {
			recurPattern = Calendar.MONTH;
		} else if (dateMatches(pattern, YEAR) || dateMatches(pattern, YEARLY)) {
			recurPattern = Calendar.YEAR;
		}
		return recurPattern;
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
}
