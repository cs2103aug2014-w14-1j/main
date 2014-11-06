// @author A0116150X
package speed.parser;

import static org.mentaregex.Regex.match;
import static org.mentaregex.Regex.matches;

public class DateTimeRegexHandler {

	/**
	 * Regex for month names
	 */
	protected final String JAN = "Jan(?:uary)?";
	protected final String FEB = "Feb(?:ruary)?";
	protected final String MAR = "Mar(?:ch)?";
	protected final String APR = "Apr(?:il)?";
	protected final String MAY = "May";
	protected final String JUN = "Jun(?:e)?";
	protected final String JUL = "Jul(?:y)?";
	protected final String AUG = "Aug(?:ust)?";
	protected final String SEP = "Sep(?:tember)?";
	protected final String OCT = "Oct(?:ober)?";
	protected final String NOV = "Nov(?:ember)?";
	protected final String DEC = "Dec(?:ember)?";

	/**
	 * Regex for datetime measurements
	 */
	protected final String SECOND = "seconds?";
	protected final String MINUTE = "minutes?";
	protected final String HOUR = "hours?";
	protected final String DAY = "days?";
	protected final String WEEK = "weeks?";
	protected final String MONTH = "months?";
	protected final String YEAR = "years?";
	protected final String DATE_PERIOD = "("+DAY+"|"+WEEK+"|"+MONTH+"|"+YEAR+")";
	protected final String TIME_PERIOD = "("+SECOND+"|"+MINUTE+"|"+HOUR+")";

	/**
	 * Regex for recurrence in natural language
	 */
	protected final String DAILY = "daily";
	protected final String WEEKLY = "weekly";
	protected final String MONTHLY = "monthly";
	protected final String YEARLY = "yearly|annually";

	/**
	 * Regex for days in week
	 */
	protected final String MON = "mon(?:day)?";
	protected final String TUE = "tues|tue(?:sday)?";
	protected final String WED = "wed(?:nesday)?";
	protected final String THU = "thurs?|thu(?:rsday)?";
	protected final String FRI = "fri(?:day)?";
	protected final String SAT = "sat(?:urday)?";
	protected final String SUN = "sun(?:day)?";
	protected final String DAY_NAMES = "("+MON+"|"+TUE+"|"+WED+"|"+THU+"|"+FRI+"|"+SAT+"|"+SUN+")";

	/**
	 * Regex for natural language such as
	 * after 3 hours
	 * before 1 month
	 * 3 years time
	 */
	protected final String AFTER = "after";
	protected final String BEFORE = "before";
	protected final String AFTER_BEFORE_DATE_PERIOD = "(?:(?:"+
		"("+AFTER+"|"+BEFORE+")\\s+)(\\d+)\\s+"+DATE_PERIOD+")|(?:"+
		"(\\d+)\\s+"+DATE_PERIOD+"(?:\\s+time))";
	protected final String AFTER_BEFORE_TIME_PERIOD = "(?:(?:"+
		"("+AFTER+"|"+BEFORE+")\\s+)(\\d+)\\s+"+TIME_PERIOD+")|(?:"+
		"(\\d+)\\s+"+TIME_PERIOD+"(?:\\s+time))";

	/**
	 * Regex for natural language such as
	 * 5 minutes later
	 * 3 years ago
	 */
	protected final String LATER = "later|"+AFTER;
	protected final String EARLIER = "earlier|ago|"+BEFORE;
	protected final String DATE_PERIOD_LATER_EARLIER = "(\\d+)\\s+"+DATE_PERIOD+"\\s+("+LATER+"|"+EARLIER+")";
	protected final String TIME_PERIOD_LATER_EARLIER = "(\\d+)\\s+"+TIME_PERIOD+"\\s+("+LATER+"|"+EARLIER+")";

	/**
	 * Regex for natural language such as
	 * this monday
	 * next wed
	 * last week
	 */
	protected final String THIS = "this";
	protected final String NEXT = "next";
	protected final String PREVIOUS = "previous|last";
	protected final String WHICH_DAY = "(?:("+THIS+"|"+NEXT+"|"+PREVIOUS+")\\s+)?"+DAY_NAMES;
	protected final String WHICH_PERIOD = "("+THIS+"|"+NEXT+"|"+PREVIOUS+")\\s+"+DATE_PERIOD;

	/**
	 * Regex for broken down datetime formats
	 */
	protected final String DATE_CONNECTOR = "[- /.]";
	protected final String ORDINALS = "(?:st|nd|rd|th)?";
	protected final String YY = "(\\d\\d)";
	protected final String YYYY = "((?:19|20)\\d\\d)";
	protected final String MM = "(0[1-9]|1[012])";
	protected final String M = "(0?[1-9]|1[012])";
	protected final String MMM = "("+JAN+"|"+FEB+"|"+MAR+"|"+APR+"|"+MAY+"|"+JUN+"|"+JUL+"|"+AUG+"|"+SEP+"|"+OCT+"|"+NOV+"|"+DEC+")";
	protected final String DD = "(0[1-9]|[12][0-9]|3[01])";
	protected final String D = "(0?[1-9]|[12][0-9]|3[01])";
	protected final String PM = "pm";
	protected final String AM = "am";
	protected final String TIME_12 = "(?:(0?[1-9]|1[012])(?:[:\\.]([0-5][0-9]))?(?::([0-5][0-9]))?)\\s*(am|pm)";
	protected final String TIME_24 = "(?:(2[0-3]|1[0-9]|0?[0-9])[:\\.]([0-5][0-9])(?::([0-5][0-9]|[0-9]))?|(2[0-3]|1[0-9]|0[0-9])([0-5][0-9]))";
	protected final String TIME_RANGE_12 = "(?:(0?[1-9]|1[012])(?:[:\\.]([0-5][0-9]))?(?::([0-5][0-9]))?)\\s*(am|pm)?(?:\\s+to\\s+|\\s*-\\s*)"+TIME_12;
	protected final String TIME_RANGE_24 = TIME_24+"(?:\\s+to\\s+|\\s*-\\s*)"+TIME_24;

	/**
	 * Regex for common date formats
	 */
	protected final String DD_MM_YYYY = D + DATE_CONNECTOR + M + DATE_CONNECTOR + YYYY;
	protected final String DD_MMM_YYYY = D + ORDINALS + DATE_CONNECTOR + MMM + DATE_CONNECTOR + YYYY;
	protected final String DDMMYYYY = DD + MM + YYYY;
	protected final String DD_MM_YY = D + DATE_CONNECTOR + M + DATE_CONNECTOR + YY;
	protected final String DD_MMM_YY = D + ORDINALS + DATE_CONNECTOR + MMM + DATE_CONNECTOR + YY;
	protected final String DDMMYY = DD + MM + YY;

	protected final String MM_DD_YYYY = M + DATE_CONNECTOR + D + DATE_CONNECTOR + YYYY;
	protected final String MMM_DD_YYYY = MMM + DATE_CONNECTOR + D + ORDINALS + DATE_CONNECTOR + YYYY;
	protected final String MMDDYYYY = MM + DD + YYYY;
	protected final String MM_DD_YY = M + DATE_CONNECTOR + D + DATE_CONNECTOR + YY;
	protected final String MMM_DD_YY = MMM + DATE_CONNECTOR + D + ORDINALS + DATE_CONNECTOR + YY;
	protected final String MMDDYY =  MM + DD + YY;

	protected final String DD_MM = D + DATE_CONNECTOR + M;
	protected final String DD_MMM = D + ORDINALS + "(?:" + DATE_CONNECTOR + "|" + "\\s+of\\s+)" + MMM;

	protected final String MM_DD = M + DATE_CONNECTOR + D;
	protected final String MMM_DD =  MMM + DATE_CONNECTOR + D + ORDINALS;

	protected final String NOW = "now";
	protected final String TODAY = "today";
	protected final String TOMORROW = "tomorrow|tmr|tmrw|tml";
	protected final String YESTERDAY = "yesterday|yda|yta|ytd";

	/**
	 * Regex for combination of all common date formats
	 */
	protected final String SIMPLE_DATE_FORMATS = "(?:"+
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
		NOW+"|"+
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
	protected final String FROM = "from";
	protected final String PERIOD_AFTER_DATE = "(the|a|\\d+)\\s+"+DATE_PERIOD+"\\s+("+FROM+"|"+AFTER+"|"+BEFORE+")\\s+("+SIMPLE_DATE_FORMATS+")";

	/**
	 * Regex for all date formats
	 */
	protected final String DATE_FORMATS = "(?:" +
		PERIOD_AFTER_DATE+"|"+
		DATE_PERIOD_LATER_EARLIER+"|"+
		AFTER_BEFORE_DATE_PERIOD+"|"+
		SIMPLE_DATE_FORMATS+")";

	/**
	 * Regex for all time formats
	 */
	protected final String TIME_FORMATS = "(?:"+
		TIME_12+"|"+
		TIME_24+"|"+
		TIME_PERIOD_LATER_EARLIER+"|"+
		AFTER_BEFORE_TIME_PERIOD+")";

	/**
	 * Regex for all datetime formats
	 */
	protected final String DATETIME_FORMATS = "(?:"+
		DATE_FORMATS+"(?:,?\\s+)"+TIME_FORMATS+"|"+
		TIME_FORMATS+"(?:,?\\s+)"+DATE_FORMATS+"|"+
		DATE_FORMATS+"|"+
		TIME_FORMATS+")";

	protected String[] dateMatch(String input, String regex) {
		return match(input, "/(\\b" + regex + "\\b)/ig");
	}

	protected boolean dateMatches(String input, String regex) {
		return matches(input, "/\\b" + regex + "\\b/ig");
	}
}
