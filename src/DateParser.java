// @author A0116150X
import java.util.Calendar;
import java.util.GregorianCalendar;

public class DateParser extends DateTimeRegexHandler {

	private final String CURRENT_CENTURY = "20";
	private TimeParser timeParser = new TimeParser();

	private String currentDate;

	public Calendar parse(String datetime) {
		currentDate = datetime;
		return parseDate(datetime);
	}

	public Calendar parse(String datetime, int default_hour, int default_min, int default_second) {
		currentDate = datetime;
		Calendar dateCal = parseDate(datetime);
		return timeParser.parse(currentDate, dateCal, default_hour, default_min, default_second);
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
		} else if (dateMatches(date, NOW)) {
			return new GregorianCalendar(thisYear, thisMonth, thisDayOfMonth);
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
		parseDatePeriodAndSetDate(cal, isEndOfPeriod, periodLength, period);
		return cal;
	}

	private Calendar matchAfterBeforeDatePeriod(String date) {
		String[] parsedDate = dateMatch(date, AFTER_BEFORE_DATE_PERIOD);
		Calendar now = Calendar.getInstance();
		boolean add = parsedDate[1] == null || dateMatches(parsedDate[1], AFTER);
		int periodLength = Integer.parseInt(parsedDate[2].trim());
		periodLength = add ? periodLength : 0 - periodLength;
		String period = parsedDate[3];
		parseDatePeriodAndSetDate(now, false, periodLength, period);
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
		parseDatePeriodAndSetDate(now, false, periodLength, period);
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
		parseDatePeriodAndSetDate(cal, true, addition, period);
		return cal;
	}

	private void parseDatePeriodAndSetDate(Calendar cal, boolean isEndOfPeriod, int periodLength, String period) {
		if (dateMatches(period, DAY)) {
			cal.add(Calendar.DAY_OF_YEAR, periodLength);
		} else if (dateMatches(period, WEEK)) {
			cal.add(Calendar.WEEK_OF_YEAR, periodLength);
			setEndOfPeriod(isEndOfPeriod, cal, Calendar.DAY_OF_WEEK);
		} else if (dateMatches(period, MONTH)) {
			cal.add(Calendar.MONTH, periodLength);
			setEndOfPeriod(isEndOfPeriod, cal, Calendar.DAY_OF_MONTH);
		} else if (dateMatches(period, YEAR)) {
			cal.add(Calendar.YEAR, periodLength);
			setEndOfPeriod(isEndOfPeriod, cal, Calendar.DAY_OF_YEAR);
		}
	}

	private void setEndOfPeriod(boolean isEndOfPeriod, Calendar cal, int period) {
		if (isEndOfPeriod) {
			cal.set(period, cal.getActualMaximum(period));
		}
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
}
