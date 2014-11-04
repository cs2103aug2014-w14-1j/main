import java.util.Calendar;

public class DateTimeParser extends DateTimeRegexHandler{

	/**
	 * Regex for parsing datetime in command
	 */
	private final String SIMPLE_FROM_TO = "(?:"+
		DATE_FORMATS+"(?:,?\\s+)("+TIME_RANGE_12+"|"+TIME_RANGE_24+")"+"|"+
		"("+TIME_RANGE_12+"|"+TIME_RANGE_24+")(?:,?\\s+)"+DATE_FORMATS+")";
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
		if (dateMatches(input, SIMPLE_FROM_TO)) {
			String match = dateMatch(input, SIMPLE_FROM_TO)[0];
			Calendar startDate = dateParser.parseDate(match);
			Calendar endDate = (Calendar) startDate.clone();
			parseSimpleFromToDateRange(match, startDate, endDate);
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
		} else if (dateMatches(input, FROM_TO)) {
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

	private void parseSimpleFromToDateRange(String match, Calendar startDate, Calendar endDate) {
		if (dateMatches(match, TIME_RANGE_12)) {
			parseDateRangeTime12Format(match, startDate, endDate);
		} else if (dateMatches(match, TIME_RANGE_24)) {
			parseDateRangeTime24Format(match, startDate, endDate);
		}
	}

	private void parseDateRangeTime12Format(String match, Calendar startDate, Calendar endDate) {
		String[] parsedTime = dateMatch(match, TIME_RANGE_12);
		int startHour = Integer.parseInt(parsedTime[1].trim());
		int startMinute = parsedTime[2] == null ? 0 : Integer.parseInt(parsedTime[2].trim());
		int startSecond = parsedTime[3] == null ? 0 : Integer.parseInt(parsedTime[3].trim());
		if (parsedTime[4] == null) {
			parsedTime[4] = parsedTime[8];
		}
		if (parsedTime[4].trim().equalsIgnoreCase(PM) && startHour < 12) {
			startHour += 12;
		} else if (parsedTime[4].trim().equalsIgnoreCase(AM) && startHour == 12) {
			startHour -= 12;
		}
		int endHour = Integer.parseInt(parsedTime[5].trim());
		int endMinute = parsedTime[6] == null ? 0 : Integer.parseInt(parsedTime[6].trim());
		int endSecond = parsedTime[7] == null ? 0 : Integer.parseInt(parsedTime[7].trim());
		if (parsedTime[8].trim().equalsIgnoreCase(PM) && endHour < 12) {
			endHour += 12;
		} else if (parsedTime[8].trim().equalsIgnoreCase(AM) && endHour == 12) {
			endHour -= 12;
		}
		setTimeOfDate(startDate, startHour, startMinute, startSecond);
		setTimeOfDate(endDate, endHour, endMinute, endSecond);
		if (startDate.after(endDate)) {
			endDate.add(Calendar.DAY_OF_YEAR, 1);
		}
	}

	private void parseDateRangeTime24Format(String match, Calendar startDate, Calendar endDate) {
		String[] parsedTime = dateMatch(match, TIME_RANGE_24);
		int startHour;
		int startMinute;
		int startSecond;
		int endHour;
		int endMinute;
		int endSecond;
		if (parsedTime[1] == null) {
			startHour = Integer.parseInt(parsedTime[4]);
			startMinute = Integer.parseInt(parsedTime[5]);
			startSecond = 0;
		} else {
			startHour = Integer.parseInt(parsedTime[1]);
			startMinute = Integer.parseInt(parsedTime[2]);
			startSecond = parsedTime[3] == null ? 0 : Integer.parseInt(parsedTime[3]);
		}
		if (parsedTime[6] == null) {
			endHour = Integer.parseInt(parsedTime[9]);
			endMinute = Integer.parseInt(parsedTime[10]);
			endSecond = 0;
		} else {
			endHour = Integer.parseInt(parsedTime[6]);
			endMinute = Integer.parseInt(parsedTime[7]);
			endSecond = parsedTime[8] == null ? 0 : Integer.parseInt(parsedTime[8]);
		}
		setTimeOfDate(startDate, startHour, startMinute, startSecond);
		setTimeOfDate(endDate, endHour, endMinute, endSecond);
		if (startDate.after(endDate)) {
			endDate.add(Calendar.DAY_OF_YEAR, 1);
		}
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

	private void setTimeOfDate(Calendar date, int hour, int minute, int second) {
		date.set(Calendar.HOUR_OF_DAY, hour);
		date.set(Calendar.MINUTE, minute);
		date.set(Calendar.SECOND, second);
	}

	private Calendar startOfDay(Calendar cal) {
		setTimeOfDate(cal, 0, 0, 0);
		return cal;
	}

	private Calendar endOfDay(Calendar cal) {
		setTimeOfDate(cal, 23, 59, 59);
		return cal;
	}
}