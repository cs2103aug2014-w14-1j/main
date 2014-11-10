// @author A0116150X
import java.util.Calendar;

public class TimeParser extends DateTimeRegexHandler {

	protected Calendar parse(String datetime, Calendar cal, int default_hour, int default_min, int default_second){
		Time time = parseTime(datetime);
		if (cal != null && time != null) {
			cal.set(Calendar.HOUR_OF_DAY, time.getHour());
			cal.set(Calendar.MINUTE, time.getMinute());
			cal.set(Calendar.SECOND, time.getSecond());
		} else if (cal != null) {
			cal.set(Calendar.HOUR_OF_DAY, default_hour);
			cal.set(Calendar.MINUTE, default_min);
			cal.set(Calendar.SECOND, default_second);
		} else if (time != null) {
			cal = Calendar.getInstance();
			cal.set(Calendar.HOUR_OF_DAY, time.getHour());
			cal.set(Calendar.MINUTE, time.getMinute());
			cal.set(Calendar.SECOND, time.getSecond());
		}
		return cal;
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
		} else if (dateMatches(time, NOW)) {
			t = new Time();
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
		int periodLength;
		String period;
		if (parsedTime[1] == null) {
			periodLength = Integer.parseInt(parsedTime[4].trim());
			period = parsedTime[5];
		} else {
			boolean add = dateMatches(parsedTime[1], AFTER);
			periodLength = Integer.parseInt(parsedTime[2].trim());
			periodLength = add ? periodLength : 0 - periodLength;
			period = parsedTime[3];
		}
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
