import java.util.Calendar;
import java.util.GregorianCalendar;

import static org.mentaregex.Regex.match;
import static org.mentaregex.Regex.matches;

public class DateParser {

	/**
	 * Regex for month names
	 */
	private final String JAN = "Jan(?:uary)?";
	private final String FEB = "Feb(?:ruary)?";
	private final String MAR = "Mar(?:ch)?";
	private final String APR = "Apr(?:il)?";
	private final String MAY = "May";
	private final String JUN = "Jun(?:e)?";
	private final String JUL = "Jul(?:y)?";
	private final String AUG = "Aug(?:ust)?";
	private final String SEP = "Sep(?:tember)?";
	private final String OCT = "Oct(?:ober)?";
	private final String NOV = "Nov(?:ember)?";
	private final String DEC = "Dec(?:ember)?";

	/**
	 * Regex for datetime measurements
	 */
	private final String SECOND = "seconds?";
	private final String MINUTE = "minutes?";
	private final String HOUR = "hours?";
	private final String DAY = "days?";
	private final String WEEK = "weeks?";
	private final String MONTH = "months?";
	private final String YEAR = "years?";
	private final String DATE_PERIOD = "("+DAY+"|"+WEEK+"|"+MONTH+"|"+YEAR+")";
	private final String TIME_PERIOD = "("+SECOND+"|"+MINUTE+"|"+HOUR+")";

	/**
	 * Regex for recurrence in natural language
 	 */
	private final String DAILY = "daily";
	private final String WEEKLY = "weekly";
	private final String MONTHLY = "monthly";
	private final String YEARLY = "yearly|annually";

	/**
	 * Regex for days in week
	 */
	private final String MON = "mon(?:day)?";
	private final String TUE = "tues|tue(?:sday)?";
	private final String WED = "wed(?:nesday)?";
	private final String THU = "thurs?|thu(?:rsday)?";
	private final String FRI = "fri(?:day)?";
	private final String SAT = "sat(?:urday)?";
	private final String SUN = "sun(?:day)?";
	private final String DAY_NAMES = "("+MON+"|"+TUE+"|"+WED+"|"+THU+"|"+FRI+"|"+SAT+"|"+SUN+")";

	/**
	 * Regex for natural language such as
	 * after 3 hours
	 * before 1 month
	 * 3 years time
	 */
	private final String AFTER = "after";
	private final String BEFORE = "before";
	private final String AFTER_BEFORE_DATE_PERIOD = "(?:("+AFTER+"|"+BEFORE+")\\s+)?(\\d+)\\s+"+DATE_PERIOD+"(?:\\s+time)?";
	private final String AFTER_BEFORE_TIME_PERIOD = "(?:("+AFTER+"|"+BEFORE+")\\s+)?(\\d+)\\s+"+TIME_PERIOD+"(?:\\s+time)?";

	/**
	 * Regex for natural language such as
	 * 5 minutes later
	 * 3 years ago
	 */
	private final String LATER = "later";
	private final String EARLIER = "earlier|ago";
	private final String DATE_PERIOD_LATER_EARLIER = "(\\d+)\\s+"+DATE_PERIOD+"\\s+("+LATER+"|"+EARLIER+")";
	private final String TIME_PERIOD_LATER_EARLIER = "(\\d+)\\s+"+TIME_PERIOD+"\\s+("+LATER+"|"+EARLIER+")";

	/**
	 * Regex for natural language such as
	 * this monday
	 * next wed
	 * last week
	 */
	private final String THIS = "this";
	private final String NEXT = "next";
	private final String PREVIOUS = "previous|last";
	private final String WHICH_DAY = "(?:("+THIS+"|"+NEXT+"|"+PREVIOUS+")\\s+)?"+DAY_NAMES;
	private final String WHICH_PERIOD = "("+THIS+"|"+NEXT+"|"+PREVIOUS+")\\s+"+DATE_PERIOD;

	/**
	 * Regex for broken down datetime formats
	 */
	private final String DATE_CONNECTOR = "[- /.]";
	private final String ORDINALS = "(?:st|nd|rd|th)?";
	private final String YY = "(\\d\\d)";
	private final String YYYY = "((?:19|20)\\d\\d)";
	private final String MM = "(0[1-9]|1[012])";
	private final String M = "(0?[1-9]|1[012])";
	private final String MMM = "("+JAN+"|"+FEB+"|"+MAR+"|"+APR+"|"+MAY+"|"+JUN+"|"+JUL+"|"+AUG+"|"+SEP+"|"+OCT+"|"+NOV+"|"+DEC+")";
	private final String DD = "(0[1-9]|[12][0-9]|3[01])";
	private final String D = "(0?[1-9]|[12][0-9]|3[01])";
	private final String TIME_12 = "(?:(0?[1-9]|1[012])(?:[:\\.]([0-5][0-9]))?(?::([0-5][0-9]))?)\\s*(pm|am)";
	private final String TIME_24 = "(?:(2[0-3]|1[0-9]|0?[0-9])[:\\.]([0-5][0-9])(?::([0-5][0-9]|[0-9]))?|(2[0-3]|1[0-9]|0[0-9])([0-5][0-9]))";
	private final String PM = "pm";
	private final String AM = "am";

	/**
	 * Regex for common date formats
	 */
	private final String DD_MM_YYYY = D + DATE_CONNECTOR + M + DATE_CONNECTOR + YYYY;
	private final String DD_MMM_YYYY = D + ORDINALS + DATE_CONNECTOR + MMM + DATE_CONNECTOR + YYYY;
	private final String DDMMYYYY = DD + MM + YYYY;
	private final String DD_MM_YY = D + DATE_CONNECTOR + M + DATE_CONNECTOR + YY;
	private final String DD_MMM_YY = D + ORDINALS + DATE_CONNECTOR + MMM + DATE_CONNECTOR + YY;
	private final String DDMMYY = DD + MM + YY;

	private final String MM_DD_YYYY = M + DATE_CONNECTOR + D + DATE_CONNECTOR + YYYY;
	private final String MMM_DD_YYYY = MMM + DATE_CONNECTOR + D + ORDINALS + DATE_CONNECTOR + YYYY;
	private final String MMDDYYYY = MM + DD + YYYY;
	private final String MM_DD_YY = M + DATE_CONNECTOR + D + DATE_CONNECTOR + YY;
	private final String MMM_DD_YY = MMM + DATE_CONNECTOR + D + ORDINALS + DATE_CONNECTOR + YY;
	private final String MMDDYY =  MM + DD + YY;

	private final String DD_MM = D + DATE_CONNECTOR + M;
	private final String DD_MMM = D + ORDINALS + "(?:" + DATE_CONNECTOR + "|" + "\\s+of\\s+)" + MMM;

	private final String MM_DD = M + DATE_CONNECTOR + D;
	private final String MMM_DD =  MMM + DATE_CONNECTOR + D + ORDINALS;

	private final String TODAY = "today";
	private final String TOMORROW = "tomorrow|tmr|tmrw|tml";
	private final String YESTERDAY = "yesterday|yda|yta|ytd";

	/**
	 * Regex for combination of all common date formats
	 */
	private final String SIMPLE_DATE_FORMATS = "(?:"+
		DD_MM_YYYY+"|"+
		DD_MMM_YYYY+"|"+
		DDMMYYYY+"|"+
		DD_MM_YY+"|"+
		DD_MMM_YY+"|"+
		DDMMYY+"|"+
		MM_DD_YYYY+"|"+
		MMM_DD_YYYY+"|"+
		MMDDYYYY+"|"+
		MM_DD_YY+"|"+
		MMM_DD_YY+"|"+
		MMDDYY+"|"+
		DD_MM+"|"+
		DD_MMM+"|"+
		MM_DD+"|"+
		MMM_DD+"|"+
		TODAY+"|"+
		TOMORROW+"|"+
		YESTERDAY+"|"+
		WHICH_DAY+"|"+
		WHICH_PERIOD+")";

	/**
	 * Regex for natural language such as
	 * the day after tmr
	 * a week from today
	 */
	private final String FROM = "from";
	private final String PERIOD_AFTER_DATE = "(the|a|\\d+)\\s+"+DATE_PERIOD+"\\s+("+FROM+"|"+AFTER+"|"+BEFORE+")\\s+("+SIMPLE_DATE_FORMATS+")";

	/**
	 * Regex for all date formats
	 */
	private final String DATE_FORMATS = "(?:" +
		PERIOD_AFTER_DATE+"|"+
		DATE_PERIOD_LATER_EARLIER+"|"+
		AFTER_BEFORE_DATE_PERIOD+"|"+
		SIMPLE_DATE_FORMATS+")";

	/**
	 * Regex for all time formats
	 */
	private final String TIME_FORMATS = "(?:"+
		TIME_12+"|"+
		TIME_24+"|"+
		TIME_PERIOD_LATER_EARLIER+"|"+
		AFTER_BEFORE_TIME_PERIOD+")";

	/**
	 * Regex for all datetime formats
	 */
	private final String DATETIME_FORMATS = "(?:"+
		DATE_FORMATS+"(?:,?\\s+)"+TIME_FORMATS+"|"+
		TIME_FORMATS+"(?:,?\\s+)"+DATE_FORMATS+"|"+
		DATE_FORMATS+"|"+
		TIME_FORMATS+")";

	/**
	 * Regex for parsing datetime in command
	 */
	private final String FROM_DATETIME = "(?:from\\s+)?("+DATETIME_FORMATS+")";
	private final String TO_DATETIME = "to\\s+("+DATETIME_FORMATS+")";
	private final String FROM_TO = FROM_DATETIME + "\\s+" + TO_DATETIME;
	private final String DUE = "(?:due(?:\\s+(?:on|in))?|by|in) (?:the )?("+DATETIME_FORMATS+")";
	private final String RECUR = "(?:recurs?\\s)?(?:every\\s?)(\\d\\s)?("+DAY+"|"+WEEK+"|"+MONTH+"|"+YEAR+")";
	private final String SIMPLE_RECUR = "(?:recurs?\\s)?("+DAILY+"|"+WEEKLY+"|"+MONTHLY+"|"+YEARLY+")";

	private final String CURRENT_CENTURY = "20";

	private String input;
	private String output;
	private String currentDate;

	public String parseCommand(String command, Command.COMMAND_TYPE type, Command commandObj) {
		output = command;
		input = command.replaceAll("\"[^\"]+\"", "");
		if (dateMatches(input, FROM_TO)) {
			String match = dateMatch(input, FROM_TO)[0];
			String[] fromDate = dateMatch(match, FROM_DATETIME);
			Calendar startDate = parseDateTime(fromDate[1], 0, 0, 0);
			String[] toDate = dateMatch(match, TO_DATETIME);
			Calendar endDate = parseDateTime(toDate[1], 23, 59, 59);
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
			Calendar dueDate = parseDateTime(dates[1], 23, 59, 59);
			if (dueDate != null) {
				commandObj.setTaskEndDate(dueDate);
				output = output.replaceFirst(dates[0], "");
				parseRecur(commandObj);
			}
		} else if (dateMatches(input, DATETIME_FORMATS)) {
			String[] dates = dateMatch(input, DATETIME_FORMATS);
			Calendar date = parseDateTime(dates[0], 23, 59, 59);
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

	private Calendar parseDateTime(String datetime, int default_hour, int default_min, int default_second) {
		currentDate = datetime;
		Calendar dateCal = parseDate(datetime);
		Time time = parseTime(currentDate);
		if (dateCal != null && time != null) {
			dateCal.set(Calendar.HOUR_OF_DAY, time.getHour());
			dateCal.set(Calendar.MINUTE, time.getMinute());
			dateCal.set(Calendar.SECOND, time.getSecond());
		} else if (dateCal != null) {
			dateCal.set(Calendar.HOUR_OF_DAY, default_hour);
			dateCal.set(Calendar.MINUTE, default_min);
			dateCal.set(Calendar.SECOND, default_second);
		} else if (time != null) {
			dateCal = Calendar.getInstance();
			dateCal.set(Calendar.HOUR_OF_DAY, time.getHour());
			dateCal.set(Calendar.MINUTE, time.getMinute());
			dateCal.set(Calendar.SECOND, time.getSecond());
		}
		return dateCal;
	}

	private Calendar parseDate(String date) {
		Calendar cal = matchNaturalLanguage(date);
		cal = cal == null ? matchDayMonthYear(date) : cal;
		cal = cal == null ? matchDayMonth(date) : cal;
		cal = cal == null ? matchMonthDayYear(date) : cal;
		cal = cal == null ? matchMonthDay(date) : cal;
		return cal;
	}

	private Calendar matchDayMonthYear(String date) {
		String[] parsedDate = null;
		if (dateMatches(date, DD_MM_YYYY)) {
			parsedDate = dateMatch(date, DD_MM_YYYY);
		} else if (dateMatches(date, DD_MMM_YYYY)) {
			parsedDate = dateMatch(date, DD_MMM_YYYY);
			parsedDate[2] = toNumeral(parsedDate[2]);
		} else if (dateMatches(date, DDMMYYYY)) {
			parsedDate = dateMatch(date, DDMMYYYY);
		} else if (dateMatches(date, DD_MM_YY)) {
			parsedDate = dateMatch(date, DD_MM_YY);
			parsedDate[3] = CURRENT_CENTURY + parsedDate[3];
		} else if (dateMatches(date, DD_MMM_YY)) {
			parsedDate = dateMatch(date, DD_MMM_YY);
			parsedDate[2] = toNumeral(parsedDate[2]);
			parsedDate[3] = CURRENT_CENTURY + parsedDate[3];
		} else if (dateMatches(date, DDMMYY)) {
			parsedDate = dateMatch(date, DDMMYY);
			parsedDate[3] = CURRENT_CENTURY + parsedDate[3];
		}
		if (parsedDate == null) {
			return null;
		} else {
			currentDate = currentDate.replaceFirst(parsedDate[0], "");
			int year = Integer.parseInt(parsedDate[3]);
			int month = Integer.parseInt(parsedDate[2]) - 1;
			int day = Integer.parseInt(parsedDate[1]);
			return new GregorianCalendar(year, month, day);
		}
	}

	private Calendar matchDayMonth(String date) {
		String[] parsedDate = null;
		if (dateMatches(date, DD_MM)) {
			parsedDate = dateMatch(date, DD_MM);
		} else if (dateMatches(date, DD_MMM)) {
			parsedDate = dateMatch(date, DD_MMM);
			parsedDate[2] = toNumeral(parsedDate[2]);
		}
		if (parsedDate == null) {
			return null;
		} else {
			currentDate = currentDate.replaceFirst(parsedDate[0], "");
			Calendar now = Calendar.getInstance();
			int day = Integer.parseInt(parsedDate[1]);
			int month = Integer.parseInt(parsedDate[2]) - 1;
			int year = now.get(Calendar.YEAR);
			if (now.get(Calendar.MONTH) > month) {
				year += 1;
			} else if (now.get(Calendar.MONTH) == month && now.get(Calendar.DAY_OF_MONTH) > day) {
				year += 1;
			}
			return new GregorianCalendar(year, month, day);
		}
	}

	private Calendar matchMonthDayYear(String date) {
		String[] parsedDate = null;
		if (dateMatches(date, MM_DD_YYYY)) {
			parsedDate = dateMatch(date, MM_DD_YYYY);
		} else if (dateMatches(date, MMM_DD_YYYY)) {
			parsedDate = dateMatch(date, MMM_DD_YYYY);
			parsedDate[1] = toNumeral(parsedDate[1]);
		} else if (dateMatches(date, MMDDYYYY)) {
			parsedDate = dateMatch(date, MMDDYYYY);
		} else if (dateMatches(date, MM_DD_YY)) {
			parsedDate = dateMatch(date, MM_DD_YY);
			parsedDate[3] = CURRENT_CENTURY + parsedDate[3];
		} else if (dateMatches(date, MMM_DD_YY)) {
			parsedDate = dateMatch(date, MMM_DD_YY);
			parsedDate[3] = CURRENT_CENTURY + parsedDate[3];
			parsedDate[1] = toNumeral(parsedDate[1]);
		} else if (dateMatches(date, MMDDYY)) {
			parsedDate = dateMatch(date, MMDDYY);
			parsedDate[3] = CURRENT_CENTURY + parsedDate[3];
		}
		if (parsedDate == null) {
			return null;
		} else {
			currentDate = currentDate.replaceFirst(parsedDate[0], "");
			int year = Integer.parseInt(parsedDate[3]);
			int month = Integer.parseInt(parsedDate[1]) - 1;
			int day = Integer.parseInt(parsedDate[2]);
			return new GregorianCalendar(year, month, day);
		}
	}

	private Calendar matchMonthDay(String date) {
		String[] parsedDate = null;
		if (dateMatches(date, MM_DD)) {
			parsedDate = dateMatch(date, MM_DD);
		} else if (dateMatches(date, MMM_DD)) {
			parsedDate = dateMatch(date, MMM_DD);
			parsedDate[1] = toNumeral(parsedDate[1]);
		}
		if (parsedDate == null) {
			return null;
		} else {
			currentDate = currentDate.replaceFirst(parsedDate[0], "");
			Calendar now = Calendar.getInstance();
			int day = Integer.parseInt(parsedDate[2]);
			int month = Integer.parseInt(parsedDate[1]) - 1;
			int year = now.get(Calendar.YEAR);
			if (now.get(Calendar.MONTH) > month) {
				year += 1;
			} else if (now.get(Calendar.MONTH) == month && now.get(Calendar.DAY_OF_MONTH) > day) {
				year += 1;
			}
			return new GregorianCalendar(year, month, day);
		}
	}

	private Calendar matchNaturalLanguage(String date) {
		Calendar now = Calendar.getInstance();
		int thisYear = now.get(Calendar.YEAR);
		int thisMonth = now.get(Calendar.MONTH);
		int thisDayOfMonth = now.get(Calendar.DAY_OF_MONTH);
		if (dateMatches(date, PERIOD_AFTER_DATE)) {
			return matchPeriodAfterDate(date);
		} else if (dateMatches(date, DATE_PERIOD_LATER_EARLIER)) {
			return matchDatePeriodLaterEarlier(date);
		} else if (dateMatches(date, AFTER_BEFORE_DATE_PERIOD)) {
			return matchAfterBeforeDatePeriod(date);
		} else if (dateMatches(date, TODAY)) {
			currentDate = currentDate.replaceFirst(dateMatch(date, TODAY)[0], "");
			return new GregorianCalendar(thisYear, thisMonth, thisDayOfMonth);
		} else if (dateMatches(date, YESTERDAY)) {
			currentDate = currentDate.replaceFirst(dateMatch(date, YESTERDAY)[0], "");
			return new GregorianCalendar(thisYear, thisMonth, thisDayOfMonth - 1);
		} else if (dateMatches(date, TOMORROW)) {
			currentDate = currentDate.replaceFirst(dateMatch(date, TOMORROW)[0], "");
			return new GregorianCalendar(thisYear, thisMonth, thisDayOfMonth + 1);
		} else if (dateMatches(date, WHICH_DAY)) {
			return matchWhichDay(date);
		} else if (dateMatches(date, WHICH_PERIOD)) {
			return matchWhichPeriod(date);
		} else {
			return null;
		}
	}

	private Calendar matchPeriodAfterDate(String date) {
		String[] parsedDate = dateMatch(date, PERIOD_AFTER_DATE);
		Calendar cal = parseDate(parsedDate[4]);
		boolean add = parsedDate[3] == null || dateMatches(parsedDate[3], FROM+"|"+AFTER);
		boolean isEndOfPeriod = false;
		int periodLength;
		if (dateMatches(parsedDate[1], "a")) {
			periodLength = 1;
		} else if (dateMatches(parsedDate[1], "the")) {
			periodLength = 1;
			isEndOfPeriod = true;
		} else {
			periodLength = Integer.parseInt(parsedDate[1].trim());
		}
		periodLength = add ? periodLength : 0 - periodLength;
		String period = parsedDate[2];
		if (dateMatches(period, DAY)) {
			cal.add(Calendar.DAY_OF_YEAR, periodLength);
		} else if (dateMatches(period, WEEK)) {
			cal.add(Calendar.WEEK_OF_YEAR, periodLength);
			if (isEndOfPeriod) {
				cal.set(Calendar.DAY_OF_WEEK, cal.getActualMaximum(Calendar.DAY_OF_WEEK));
			}
		} else if (dateMatches(period, MONTH)) {
			cal.add(Calendar.MONTH, periodLength);
			if (isEndOfPeriod) {
				cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
			}
		} else if (dateMatches(period, YEAR)) {
			cal.add(Calendar.YEAR, periodLength);
			if (isEndOfPeriod) {
				cal.set(Calendar.DAY_OF_YEAR, cal.getActualMaximum(Calendar.DAY_OF_YEAR));
			}
		}
		return cal;
	}

	private Calendar matchAfterBeforeDatePeriod(String date) {
		String[] parsedDate = dateMatch(date, AFTER_BEFORE_DATE_PERIOD);
		Calendar now = Calendar.getInstance();
		boolean add = parsedDate[1] == null || dateMatches(parsedDate[1], AFTER);
		int periodLength = Integer.parseInt(parsedDate[2].trim());
		periodLength = add ? periodLength : 0 - periodLength;
		String period = parsedDate[3];
		if (dateMatches(period, DAY)) {
			now.add(Calendar.DAY_OF_YEAR, periodLength);
		} else if (dateMatches(period, WEEK)) {
			now.add(Calendar.WEEK_OF_YEAR, periodLength);
		} else if (dateMatches(period, MONTH)) {
			now.add(Calendar.MONTH, periodLength);
		} else if (dateMatches(period, YEAR)) {
			now.add(Calendar.YEAR, periodLength);
		}
		currentDate = currentDate.replaceFirst(parsedDate[0], "");
		return now;
	}

	private Calendar matchDatePeriodLaterEarlier(String date) {
		String[] parsedDate = dateMatch(date, DATE_PERIOD_LATER_EARLIER);
		Calendar now = Calendar.getInstance();
		boolean add = parsedDate[3] == null || dateMatches(parsedDate[3], LATER);
		int periodLength = Integer.parseInt(parsedDate[1].trim());
		periodLength = add ? periodLength : 0 - periodLength;
		String period = parsedDate[2];
		if (dateMatches(period, DAY)) {
			now.add(Calendar.DAY_OF_YEAR, periodLength);
		} else if (dateMatches(period, WEEK)) {
			now.add(Calendar.WEEK_OF_YEAR, periodLength);
		} else if (dateMatches(period, MONTH)) {
			now.add(Calendar.MONTH, periodLength);
		} else if (dateMatches(period, YEAR)) {
			now.add(Calendar.YEAR, periodLength);
		}
		currentDate = currentDate.replaceFirst(parsedDate[0], "");
		return now;
	}

	private Calendar matchWhichDay(String date) {
		String[] parsedDate = dateMatch(date, WHICH_DAY);
		Calendar now = Calendar.getInstance();
		String whichDay = parsedDate[1];
		int day = checkDay(parsedDate[2]);
		if (whichDay == null) {
			if (now.get(Calendar.DAY_OF_WEEK) > day) {
				now.add(Calendar.WEEK_OF_YEAR, 1);
			}
		} else if (dateMatches(whichDay, NEXT)) {
			now.add(Calendar.WEEK_OF_YEAR, 1);
		} else if (dateMatches(whichDay, PREVIOUS)) {
			now.add(Calendar.WEEK_OF_YEAR, -1);
		}
		now.set(Calendar.DAY_OF_WEEK, day);
		currentDate = currentDate.replaceFirst(parsedDate[0], "");
		return now;
	}

	private Calendar matchWhichPeriod(String date) {
		String[] parsedDate = dateMatch(date, WHICH_PERIOD);
		Calendar cal = Calendar.getInstance();
		String whichPeriod = parsedDate[1];
		String period = parsedDate[2];
		int addition = 0;
		if (dateMatches(whichPeriod, NEXT)) {
			addition = 1;
		} else if (dateMatches(whichPeriod, PREVIOUS)) {
			addition = -1;
		}
		if (dateMatches(period, DAY)) {
			cal.add(Calendar.DAY_OF_YEAR, addition);
		} else if (dateMatches(period, WEEK)) {
			cal.add(Calendar.WEEK_OF_YEAR, addition);
			cal.set(Calendar.DAY_OF_WEEK, cal.getActualMaximum(Calendar.DAY_OF_WEEK));
		} else if (dateMatches(period, MONTH)) {
			cal.add(Calendar.MONTH, addition);
			cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
		} else if (dateMatches(period, YEAR)) {
			cal.add(Calendar.YEAR, addition);
			cal.set(Calendar.DAY_OF_YEAR, cal.getActualMaximum(Calendar.DAY_OF_YEAR));
		}
		return cal;
	}

	private int checkDay(String day) {
		int calendarDay = -1;
		if (dateMatches(day, MON)) {
			calendarDay = Calendar.MONDAY;
		} else if (dateMatches(day, TUE)) {
			calendarDay = Calendar.TUESDAY;
		} else if (dateMatches(day, WED)) {
			calendarDay = Calendar.WEDNESDAY;
		} else if (dateMatches(day, THU)) {
			calendarDay = Calendar.THURSDAY;
		} else if (dateMatches(day, FRI)) {
			calendarDay = Calendar.FRIDAY;
		} else if (dateMatches(day, SAT)) {
			calendarDay = Calendar.SATURDAY;
		} else if (dateMatches(day, SUN)) {
			calendarDay = Calendar.SUNDAY;
		}
		return calendarDay;
	}

	private String toNumeral(String month) {
		String numeral = "";
		if (dateMatches(month, JAN)) {
			numeral = "1";
		} else if (dateMatches(month, FEB)) {
			numeral = "2";
		} else if (dateMatches(month, MAR)) {
			numeral = "3";
		} else if (dateMatches(month, APR)) {
			numeral = "4";
		} else if (dateMatches(month, MAY)) {
			numeral = "5";
		} else if (dateMatches(month, JUN)) {
			numeral = "6";
		} else if (dateMatches(month, JUL)) {
			numeral = "7";
		} else if (dateMatches(month, AUG)) {
			numeral = "8";
		} else if (dateMatches(month, SEP)) {
			numeral = "9";
		} else if (dateMatches(month, OCT)) {
			numeral = "10";
		} else if (dateMatches(month, NOV)) {
			numeral = "11";
		} else if (dateMatches(month, DEC)) {
			numeral = "12";
		}
		return numeral;
	}

	private Time parseTime(String time) {
		Time t = null;
		if (dateMatches(time, TIME_12)) {
			t = parseTime12Format(time);
		} else if (dateMatches(time, TIME_24)) {
			t = parseTime24Format(time);
		} else if (dateMatches(time, TIME_PERIOD_LATER_EARLIER)) {
			t = parseTimePeriodLaterEarlier(time);
		} else if (dateMatches(time, AFTER_BEFORE_TIME_PERIOD)) {
			t = parseAfterBeforeTimePeriod(time);
		}
		return t;
	}

	private Time parseTime12Format(String time) {
		String[] parsedTime = dateMatch(time, TIME_12);
		int hour = Integer.parseInt(parsedTime[1]);
		if (parsedTime[4].trim().equalsIgnoreCase(PM) && hour < 12) {
			hour += 12;
		} else if (parsedTime[4].trim().equalsIgnoreCase(AM) && hour == 12) {
			hour -= 12;
		}
		int minute = parsedTime[2] == null ? 0 : Integer.parseInt(parsedTime[2]);
		int second = parsedTime[3] == null ? 0 : Integer.parseInt(parsedTime[3]);
		return new Time(hour, minute, second);
	}

	private Time parseTime24Format(String time) {
		String[] parsedTime = dateMatch(time, TIME_24);
		int hour;
		int minute;
		if (parsedTime[1] == null) {
			hour = Integer.parseInt(parsedTime[4]);
			minute = Integer.parseInt(parsedTime[5]);
		} else {
			hour = Integer.parseInt(parsedTime[1]);
			minute = Integer.parseInt(parsedTime[2]);
		}
		int second = parsedTime[3] == null ? 59 : Integer.parseInt(parsedTime[3]);
		return new Time(hour, minute, second);
	}

	private Time parseTimePeriodLaterEarlier(String time) {
		String[] parsedTime = dateMatch(time, TIME_PERIOD_LATER_EARLIER);
		boolean add = parsedTime[3] == null || dateMatches(parsedTime[3], LATER);
		int periodLength = Integer.parseInt(parsedTime[1].trim());
		periodLength = add ? periodLength : 0 - periodLength;
		String period = parsedTime[2];
		return parseTimePeriodAndSetTime(period, periodLength);
	}

	private Time parseAfterBeforeTimePeriod(String time) {
		String[] parsedTime = dateMatch(time, AFTER_BEFORE_TIME_PERIOD);
		boolean add = parsedTime[1] == null || dateMatches(parsedTime[1], AFTER);
		int periodLength = Integer.parseInt(parsedTime[2].trim());
		periodLength = add ? periodLength : 0 - periodLength;
		String period = parsedTime[3];
		return parseTimePeriodAndSetTime(period, periodLength);
	}

	private Time parseTimePeriodAndSetTime(String period, int periodLength) {
		Time t = new Time();
		if (dateMatches(period, SECOND)) {
			t.setSecond(t.getSecond() + periodLength);
		} else if (dateMatches(period, MINUTE)) {
			t.setMinute(t.getMinute() + periodLength);
		} else if (dateMatches(period, HOUR)) {
			t.setHour(t.getHour() + periodLength);
		}
		return t;
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

	private String[] dateMatch(String input, String regex) {
		return match(input, "/(\\b" + regex + "\\b)/ig");
	}

	private boolean dateMatches(String input, String regex) {
		return matches(input, "/\\b" + regex + "\\b/ig");
	}

	private class Time {
		private int hour;
		private int minute;
		private int second;

		public Time() {
			Calendar now = Calendar.getInstance();
			this.hour = now.get(Calendar.HOUR_OF_DAY);
			this.minute = now.get(Calendar.MINUTE);
			this.second = now.get(Calendar.SECOND);
		}

		public Time(int hour, int minute, int second) {
			this.hour = hour;
			this.minute = minute;
			this.second = second;
		}

		public void setHour(int hour) {
			this.hour = hour;
		}

		public int getHour() {
			return hour;
		}

		public void setMinute(int minute) {
			this.minute = minute;
		}

		public int getMinute() {
			return minute;
		}

		public void setSecond(int second) {
			this.second = second;
		}

		public int getSecond() {
			return second;
		}
	}
}
