import java.util.Calendar;
import java.util.GregorianCalendar;

import static org.mentaregex.Regex.match;
import static org.mentaregex.Regex.matches;

public class DateParser {
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

	private final String DATE_CONNECTOR = "[- /.]";
	private final String ORDINALS = "(?:st|nd|rd|th)?";
	private final String YY = "(\\d\\d)";
	private final String YYYY = "((?:19|20)\\d\\d)";
	private final String MM = "(0[1-9]|1[012])";
	private final String M = "(0?[1-9]|1[012])";
	private final String MMM = "("+JAN+"|"+FEB+"|"+MAR+"|"+APR+"|"+MAY+"|"+JUN+"|"+JUL+"|"+AUG+"|"+SEP+"|"+OCT+"|"+NOV+"|"+DEC+")";
	private final String DD = "(0[1-9]|[12][0-9]|3[01])";
	private final String D = "(0?[1-9]|[12][0-9]|3[01])";
	private final String TIME_12 = "(?:(0?[1-9]|1[012])(?:[\\:\\.]([0-5][0-9]))?(?:\\:([0-5][0-9]))?)\\s*(pm|am)";
	private final String TIME_24 = "(?:(2[0-3]|1[0-9]|0?[0-9])[\\:\\.]([0-5][0-9])(?:\\:([0-5][0-9]|[0-9]))?|(2[0-3]|1[0-9]|0[0-9])([0-5][0-9]))";

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
	private final String DD_MMM = D + ORDINALS + DATE_CONNECTOR + MMM;

	private final String MM_DD = M + DATE_CONNECTOR + D;
	private final String MMM_DD =  MMM + DATE_CONNECTOR + D + ORDINALS;

	private final String TODAY = "today";
	private final String TOMORROW = "tomorrow|tmr|tmrw|tml";
	private final String YESTERDAY = "yesterday|yda|yta";

	private final String CURRENT_CENTURY = "20";
	private final String PM = "pm";

	private final String FROM_TO = "from([- /.\\s\\w]+) to ([- /.\\s\\w]+)";
	private final String DUE = "(?:due(?: on)?|by) (?:the )?([0-9a-zA-Z-\\./ ]+)[\\W\\D\\S]*";

	private String command;
	private String currentDate;

	public String parseCommand(String input, Command.COMMAND_TYPE type, Command commandObj) {
		command = input;
		if (dateMatches(input, FROM_TO)) {
			String[] dates = dateMatch(input, FROM_TO);
			Calendar startDate = parseDateTime(dates[1], 0, 0, 0);
			Calendar endDate = parseDateTime(dates[2], 23, 59, 59);
			if (startDate != null && endDate != null) {
				switch (type) {
					case ADD:
					case EDIT:
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
		} else if (dateMatches(input, DUE)) {
			String[] dates = dateMatch(input, DUE);
			Calendar dueDate = parseDateTime(dates[1], 23, 59, 59);
			if (dueDate != null) {
				commandObj.setTaskDueDate(dueDate);
			}
		} else {
			Calendar date = parseDateTime(input, 23, 59, 59);
			if (date != null) {
				switch (type) {
					case ADD:
					case EDIT:
						commandObj.setTaskDueDate(date);
						break;
					case LIST:
					case SEARCH:
						commandObj.setSearchStartDate(startOfDay(date));
						commandObj.setSearchEndDate(endOfDay((Calendar) date.clone()));
						break;
				}
			}
		}
		return command;
	}

	private Calendar parseDateTime(String datetime, int default_hour, int default_min, int default_second) {
		Calendar dateCal = parseDate(datetime);
		if (currentDate != null) {
			datetime = datetime.replaceFirst(currentDate, "");
			currentDate = null;
		}
		Time time = parseTime(datetime);
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
		Calendar cal = matchDayMonthYear(date);
		cal = cal == null ? matchDayMonth(date) : cal;
		cal = cal == null ? matchMonthDayYear(date) : cal;
		cal = cal == null ? matchMonthDay(date) : cal;
		cal = cal == null ? matchNaturalLanguage(date) : cal;
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
			currentDate = parsedDate[0];
			command = command.replaceFirst(currentDate, "");
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
			currentDate = parsedDate[0];
			command = command.replaceFirst(currentDate, "");
			int year = Calendar.getInstance().get(Calendar.YEAR);
			int month = Integer.parseInt(parsedDate[2]) - 1;
			int day = Integer.parseInt(parsedDate[1]);
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
			currentDate = parsedDate[0];
			command = command.replaceFirst(currentDate, "");
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
			currentDate = parsedDate[0];
			command = command.replaceFirst(currentDate, "");
			int year = Calendar.getInstance().get(Calendar.YEAR);
			int month = Integer.parseInt(parsedDate[1]) - 1;
			int day = Integer.parseInt(parsedDate[2]);
			return new GregorianCalendar(year, month, day);
		}
	}

	private Calendar matchNaturalLanguage(String date) {
		Calendar now = Calendar.getInstance();
		int thisYear = now.get(Calendar.YEAR);
		int thisMonth = now.get(Calendar.MONTH);
		int thisDayOfMonth = now.get(Calendar.DAY_OF_MONTH);
		if (dateMatches(date, TODAY)) {
			currentDate = dateMatch(date, TODAY)[0];
			command = command.replaceFirst(currentDate, "");
			return new GregorianCalendar(thisYear, thisMonth, thisDayOfMonth);
		} else if (dateMatches(date, YESTERDAY)) {
			currentDate = dateMatch(date, YESTERDAY)[0];
			command = command.replaceFirst(currentDate, "");
			return new GregorianCalendar(thisYear, thisMonth, thisDayOfMonth - 1);
		} else if (dateMatches(date, TOMORROW)) {
			currentDate = dateMatch(date, TOMORROW)[0];
			command = command.replaceFirst(currentDate, "");
			return new GregorianCalendar(thisYear, thisMonth, thisDayOfMonth + 1);
		} else {
			return null;
		}
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
			String[] parsedTime = dateMatch(time, TIME_12);
			int hour = Integer.parseInt(parsedTime[1]);
			if (parsedTime[4].trim().equalsIgnoreCase(PM)) {
				hour += 12;
			}
			int minute = parsedTime[2] == null ? 0 : Integer.parseInt(parsedTime[2]);
			int second = parsedTime[3] == null ? 0 : Integer.parseInt(parsedTime[3]);
			t = new Time(hour, minute, second);
			command = command.replaceFirst(parsedTime[0], "");
		} else if (dateMatches(time, TIME_24)) {
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
			t = new Time(hour, minute, second);
			command = command.replaceFirst(parsedTime[0], "");
		}
		return t;
	}

	private String removeDate(String input) {
		String output = input;
		String[] dateFormats = {
			DD_MM_YYYY,
			DD_MMM_YYYY,
			DDMMYYYY,
			DD_MM_YY,
			DD_MMM_YY,
			DDMMYY,
			DD_MM,
			DD_MMM,
			MM_DD_YYYY,
			MMM_DD_YYYY,
			MMDDYYYY,
			MM_DD_YY,
			MMM_DD_YY,
			MMDDYY,
			MM_DD,
			MMM_DD
		};
		for (String regex : dateFormats) {
			output = output.replaceAll(regex, "");
		}
		return output;
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

		public Time(int hour, int minute, int second) {
			this.hour = hour;
			this.minute = minute;
			this.second = second;
		}

		public int getHour() {
			return hour;
		}

		public int getMinute() {
			return minute;
		}

		public int getSecond() {
			return second;
		}
	}
}