// @author A0116150X
package speed.parser;

import java.util.Calendar;

public class DateTimeParser extends DateTimeRegexHandler{

	/**
	 * Regex for parsing datetime in command
	 */
	private final String DATE_TIME_TO_TIME = "(?:(?:from\\s+)?"+
		DATE_FORMATS+"(?:,?\\s+)("+TIME_RANGE_12+"|"+TIME_RANGE_24+")"+"|"+
		"("+TIME_RANGE_12+"|"+TIME_RANGE_24+")(?:,?\\s+)"+DATE_FORMATS+"|"+
		"("+TIME_RANGE_12+"|"+TIME_RANGE_24+"))";
	private final String FROM_DATETIME = "(?:from\\s+)?("+DATETIME_FORMATS+")";
	private final String TO_DATETIME = "to\\s+("+DATETIME_FORMATS+")";
	private final String FROM_TO = FROM_DATETIME + "\\s+" + TO_DATETIME;
	private final String DUE = "(?:due(?:\\s+(?:on|in))?|by|in|on)\\s+(?:the\\s+)?("+DATETIME_FORMATS+")";
	private final String DUE_WHICH_PERIOD = "(?:due(?:\\s+(?:on|in))?|by|in|on)\\s+(?:the\\s+)?(\\d+\\s+(?:"+DATE_PERIOD+"|"+TIME_PERIOD+"))";
	private final String RECUR = "(?:recurs?\\s+)?(?:every)(?:\\s*)?(\\d\\s)?("+DAY+"|"+WEEK+"|"+MONTH+"|"+YEAR+"|"+DAY_NAMES+")";
	private final String SIMPLE_RECUR = "(?:recurs?\\s)?("+DAILY+"|"+WEEKLY+"|"+MONTHLY+"|"+YEARLY+")";
	private final String RECUR_DAY = "(?:recurs?\\s+)?(?:every\\s*?)"+DAY_NAMES+"\\s+((?:"+TIME_RANGE_12+"|"+TIME_RANGE_24+")|(?:"+TIME_12+"|"+TIME_24+"))";

	private final boolean isStartDate = true;
	private final boolean isEndDate = false;

	private DateParser dateParser = new DateParser();
	private String input;
	private String output;

	public String parseCommand(String command, Command.COMMAND_TYPE type, Command commandObj) {
		output = command;
		// Do not evaluate strings within quotation marks
		input = command.replaceAll("\"[^\"]+\"", "");
		if (dateMatches(input, DATE_TIME_TO_TIME)) {
			matchDateTimeToTime(type, commandObj);
		} else if (dateMatches(input, FROM_TO)) {
			matchFromDateTimeToDateTime(type, commandObj);
		} else if (dateMatches(input, DUE)) {
			matchDueDateTime(commandObj);
		} else if (dateMatches(input, DUE_WHICH_PERIOD)) {
			matchDueWhichPeriod(commandObj);
		} else if (dateMatches(input, DATETIME_FORMATS)) {
			matchAnyDateTime(type, commandObj);
		} else {
			matchOnlyRecurrances(type, commandObj);
		}
		return output;
	}

	private void matchDateTimeToTime(Command.COMMAND_TYPE type, Command commandObj) {
		String match = dateMatch(input, DATE_TIME_TO_TIME)[0];
		Calendar startDate = dateParser.parse(match);
		// defaults to today if there is no start date
		startDate = startDate == null ? Calendar.getInstance() : startDate;
		Calendar endDate = (Calendar) startDate.clone();
		parseDateTimeToTime(match, startDate, endDate);
		setCommandObjStartAndEndDate(type, commandObj, startDate, endDate);
		if (dateMatches(input, RECUR_DAY)) {
			String[] recurMatch = dateMatch(input, RECUR_DAY);
			// Removes time range from output
			output = output.replaceFirst(recurMatch[2], "");
		}
		parseRecur(commandObj);
		output = output.replaceFirst(match, "");
	}

	private void matchFromDateTimeToDateTime(Command.COMMAND_TYPE type, Command commandObj) {
		String match = dateMatch(input, FROM_TO)[0];
		String[] fromDate = dateMatch(match, FROM_DATETIME);
		Calendar startDate = dateParser.parse(fromDate[1], isStartDate, 0, 0, 0);
		String[] toDate = dateMatch(match, TO_DATETIME);
		Calendar endDate = dateParser.parse(toDate[1], isEndDate, 23, 59, 59);
		if (startDate != null && endDate != null) {
			setCommandObjStartAndEndDate(type, commandObj, startDate, endDate);
			output = output.replaceFirst(match, "");
			parseRecur(commandObj);
		}
	}

	private void matchDueDateTime(Command commandObj) {
		String[] dates = dateMatch(input, DUE);
		// parses first instance of any datetime format found
		Calendar dueDate = dateParser.parse(dates[1], isEndDate, 23, 59, 59);
		if (dueDate != null) {
			commandObj.setTaskEndDate(dueDate);
			output = output.replaceFirst(dates[0], "");
			parseRecur(commandObj);
		}
	}

	private void matchDueWhichPeriod(Command commandObj) {
		String[] dates = dateMatch(input, DUE_WHICH_PERIOD);
		dates[1] = dates[1] + " time";
		Calendar dueDate = dateParser.parse(dates[1], isEndDate, 23, 59, 59);
		if (dueDate != null) {
			commandObj.setTaskEndDate(dueDate);
			output = output.replaceFirst(dates[0], "");
			parseRecur(commandObj);
		}
	}

	private void matchAnyDateTime(Command.COMMAND_TYPE type, Command commandObj) {
		String[] dates = dateMatch(input, DATETIME_FORMATS);
		Calendar date = dateParser.parse(dates[0], isEndDate, 23, 59, 59);
		if (date != null) {
			setCommandObjEndDate(type, commandObj, date);
			if (dateMatches(input, RECUR_DAY)) {
				String[] recurMatch = dateMatch(input, RECUR_DAY);
				// Removes time range from output
				output = output.replaceFirst(recurMatch[2], "");
			}
			parseRecur(commandObj);
			output = output.replaceFirst(dates[0], "");
		}
	}

	private void matchOnlyRecurrances(Command.COMMAND_TYPE type, Command commandObj) {
		parseRecur(commandObj);
		if (hasRecurPattern(commandObj)) {
			Calendar startDate = startOfDay(Calendar.getInstance());
			Calendar endDate = endOfDay(Calendar.getInstance());
			setCommandObjStartAndEndDate(type, commandObj, startDate, endDate);
		}
	}

	private boolean hasRecurPattern(Command commandObj) {
		return commandObj.getRecurPattern() != -1;
	}

	private void setCommandObjEndDate(Command.COMMAND_TYPE type, Command commandObj, Calendar date) {
		switch (type) {
			case ADD:
			case EDIT:
			case DEFAULT:
				commandObj.setTaskEndDate(date);
				break;
			case LIST:
			case SEARCH:
				Calendar startDate = date;
				Calendar endDate = (Calendar) date.clone();
				setPeriodStartOrEndDate(startDate, isStartDate);
				setPeriodStartOrEndDate(endDate, isEndDate);
				commandObj.setSearchStartDate(startOfDay(startDate));
				commandObj.setSearchEndDate(endOfDay(endDate));
				break;
		}
	}

	private void setCommandObjStartAndEndDate(Command.COMMAND_TYPE type, Command commandObj, Calendar startDate, Calendar endDate) {
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
	}

	private void setPeriodStartOrEndDate(Calendar date, boolean isStartDate) {
		if (dateMatches(input, PERIOD_AFTER_DATE)) {
			// extract period from input
			String period = dateMatch(input, PERIOD_AFTER_DATE)[2];
			if (isStartDate) {
				setStartOfPeriod(date, period);
			} else {
				setEndOfPeriod(date, period);
			}
		} else if (dateMatches(input, WHICH_PERIOD)) {
			String period = input;
			if (isStartDate) {
				setStartOfPeriod(date, period);
			} else {
				setEndOfPeriod(date, period);
			}
		}
	}

	private void setStartOfPeriod(Calendar date, String period) {
		if (dateMatches(period, WEEK)) {
			dateParser.setStartOfPeriod(date, Calendar.DAY_OF_WEEK);
		} else if (dateMatches(period, MONTH)) {
			dateParser.setStartOfPeriod(date, Calendar.DAY_OF_MONTH);
		} else if (dateMatches(period, YEAR)) {
			dateParser.setStartOfPeriod(date, Calendar.DAY_OF_YEAR);
		}
	}

	private void setEndOfPeriod(Calendar date, String period) {
		if (dateMatches(period, WEEK)) {
			dateParser.setEndOfPeriod(date, Calendar.DAY_OF_WEEK);
		} else if (dateMatches(period, MONTH)) {
			dateParser.setEndOfPeriod(date, Calendar.DAY_OF_MONTH);
		} else if (dateMatches(period, YEAR)) {
			dateParser.setEndOfPeriod(date, Calendar.DAY_OF_YEAR);
		}
	}

	private void parseDateTimeToTime(String match, Calendar startDate, Calendar endDate) {
		if (dateMatches(match, TIME_RANGE_12)) {
			parseDateRangeTime12Format(match, startDate, endDate);
		} else if (dateMatches(match, TIME_RANGE_24)) {
			parseDateRangeTime24Format(match, startDate, endDate);
		}
	}

	private void parseDateRangeTime12Format(String match, Calendar startDate, Calendar endDate) {
		String[] parsedTime = dateMatch(match, TIME_RANGE_12);
		setStartOfDateRangeTime12Format(startDate, parsedTime);
		setEndOfDateRangeTime12Format(endDate, parsedTime);
		if (startDate.after(endDate)) {
			// Set end date to the next day if start time is after end time
			endDate.add(Calendar.DAY_OF_YEAR, 1);
		}
	}

	private void setStartOfDateRangeTime12Format(Calendar startDate, String[] parsedTime) {
		int startHour = Integer.parseInt(parsedTime[1]);
		int startMinute = parsedTime[2] == null ? 0 : Integer.parseInt(parsedTime[2]);
		int startSecond = parsedTime[3] == null ? 0 : Integer.parseInt(parsedTime[3]);
		if (parsedTime[4] == null) {
			// Sets am/pm of start time to that of end time
			parsedTime[4] = parsedTime[8];
		}
		if (parsedTime[4].equalsIgnoreCase(PM) && startHour < 12) {
			startHour += 12;
		} else if (parsedTime[4].equalsIgnoreCase(AM) && startHour == 12) {
			startHour -= 12;
		}
		setTimeOfDate(startDate, startHour, startMinute, startSecond);
	}

	private void setEndOfDateRangeTime12Format(Calendar endDate, String[] parsedTime) {
		int endHour = Integer.parseInt(parsedTime[5]);
		int endMinute = parsedTime[6] == null ? 0 : Integer.parseInt(parsedTime[6]);
		int endSecond = parsedTime[7] == null ? 0 : Integer.parseInt(parsedTime[7]);
		if (parsedTime[8].equalsIgnoreCase(PM) && endHour < 12) {
			endHour += 12;
		} else if (parsedTime[8].equalsIgnoreCase(AM) && endHour == 12) {
			endHour -= 12;
		}
		setTimeOfDate(endDate, endHour, endMinute, endSecond);
	}

	private void parseDateRangeTime24Format(String match, Calendar startDate, Calendar endDate) {
		String[] parsedTime = dateMatch(match, TIME_RANGE_24);
		setStartOfDateRangeTime24Format(startDate, parsedTime);
		setEndOfDateRangeTime24Format(endDate, parsedTime);
		if (startDate.after(endDate)) {
			// Set end date to the next day if start time is after end time
			endDate.add(Calendar.DAY_OF_YEAR, 1);
		}
	}

	private void setStartOfDateRangeTime24Format(Calendar startDate, String[] parsedTime) {
		if (isArmyTimeFormat(parsedTime[1])) {
			setDateRangeTime24Format(startDate, parsedTime[4], parsedTime[5], parsedTime[3]);
		} else {
			setDateRangeTime24Format(startDate, parsedTime[1], parsedTime[2], parsedTime[3]);
		}
	}

	private void setEndOfDateRangeTime24Format(Calendar endDate, String[] parsedTime) {
		if (isArmyTimeFormat(parsedTime[6])) {
			setDateRangeTime24Format(endDate, parsedTime[9], parsedTime[10], parsedTime[8]);
		} else {
			setDateRangeTime24Format(endDate, parsedTime[6], parsedTime[7], parsedTime[8]);
		}
	}

	private boolean isArmyTimeFormat(String firstMatchOfTime24Regex) {
		return firstMatchOfTime24Regex == null;
	}

	private void setDateRangeTime24Format(Calendar date, String hour, String minute, String second) {
		int endHour = Integer.parseInt(hour);
		int endMinute = Integer.parseInt(minute);
		int endSecond = second == null ? 0 : Integer.parseInt(second);
		setTimeOfDate(date, endHour, endMinute, endSecond);
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
		} else if (dateMatches(pattern, WEEK) || dateMatches(pattern, WEEKLY) || dateMatches(pattern, DAY_NAMES)) {
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
