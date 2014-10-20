import java.util.Calendar;
import java.util.LinkedList;
import java.util.Comparator;

//prototype
public class TaskDate {
	
	private static final String RECUR_YEAR = "year";
	private static final String RECUR_MONTH = "month";
	private static final String RECUR_WEEK = "week";
	private static final String RECUR_DAY = "day";
	private static final int RECUR_AMOUNT = 1;
	
	private LinkedList<DateNode> nodes;
	private String recur;
	private Calendar end_limit;
	
	public TaskDate(Calendar start_date, Calendar end_date, String recur, Calendar end_limit) {
		assert !end_date.before(start_date);
		this.nodes = new LinkedList<DateNode>();
		this.nodes.add(new DateNode(start_date, end_date));
		this.recur = recur;
		this.end_limit = end_limit;
		updateRecur();
	}
	
	public TaskDate(Calendar start_date, Calendar end_date) {
		this(start_date, end_date, "", null);
	}
	
	
	//Recurring***********************************************
	
	public boolean isRecur() {
		return !recur.equals("");
	}
	
	//assumes at least one datenode
	public void updateRecur() {
		if (recur.equals("") || end_limit == null || nodes.isEmpty()) {
			return;
		}
		
		DateNode latest_date = nodes.getLast();
		while (latest_date.getEndDate().before(end_limit)) {
			latest_date = increaseDate(latest_date);
			if (latest_date.getEndDate().before(end_limit)) {
				nodes.addLast(latest_date);
			}
		}
		
	}
	
	private DateNode increaseDate(DateNode date) {
		Calendar before = (Calendar) date.getStartDate().clone();
		Calendar after = (Calendar) date.getEndDate().clone();
		
		int field = getRecurField(recur);
		assert field != -1;
		
		before.add(getRecurField(recur), RECUR_AMOUNT);
		after.add(getRecurField(recur), RECUR_AMOUNT);
		
		assert before.before(after);
		
		return new DateNode(before, after);
	}
	
	private int getRecurField(String recur) {
		if (recur.equals(RECUR_YEAR)) {
			return Calendar.YEAR;
		}
		else if (recur.equals(RECUR_MONTH)) {
			return Calendar.MONTH;
		}
		else if (recur.equals(RECUR_WEEK)) {
			return Calendar.WEEK_OF_YEAR;
		}
		else if (recur.equals(RECUR_DAY)) {
			return Calendar.DAY_OF_YEAR;
		}
		
		return -1;
	}
	
	public void removeOldDates(Calendar dateCompleted) {
		assert dateCompleted != null;
		while (!nodes.isEmpty() && nodes.getFirst().getEndDate().before(dateCompleted)) {
			nodes.removeFirst();
		}
	}
	
	public boolean withinDateRange(Calendar start_date, Calendar end_date) {
		for (DateNode date : nodes) {
			if (date.withinDateRange(start_date, end_date)) {
				return true;
			}
		}
		return false;
	}
	
	public boolean isEmpty() {
		return nodes.isEmpty();
	}
	
	public LinkedList<String> getDates() {
		LinkedList<String> datesTranslated = new LinkedList<String>();
		for (DateNode date : nodes) {
			datesTranslated.add(date.getDates());
		}
		return datesTranslated;
	}
	
	public LinkedList<DateNode> getDateNodes() {
		return nodes;
	}
	
	public Calendar getStartDate() {
		return nodes.getFirst().getStartDate();
	}
	
	public Calendar getEndDate() {
		return nodes.getLast().getEndDate();
	}
	
	public String getFirstDate() {
		return nodes.getFirst().getDates();
	}
	
}

//Internal class DateNode*************************************************

class DateNode {
	
	Calendar start_date;
	Calendar end_date;
	int interval;
	
	public DateNode(Calendar start_date, Calendar end_date) {
		this.start_date = start_date;
		this.end_date = end_date;
		this.interval = computeInterval();
	}

	public Calendar getStartDate() {
		return start_date;
	}
	
	public Calendar getEndDate() {
		return end_date;
	}
	
	public String getDates() {
		if (start_date.equals(end_date)) {
			return start_date.getTime().toString();
		}
		return start_date.getTime().toString() + " - " + end_date.getTime().toString();
	}
	
	public int getInterval() {
		return interval;
	}
	
	public boolean endsAfter(Calendar search_date) {
		return end_date.after(search_date);
	}
	
	public boolean startsBefore(Calendar search_date) {
		return start_date.before(search_date);
	}
	
	//TBC
	private int computeInterval() {
		return -1;
	}
	
	public boolean withinDateRange(Calendar search_start_date, Calendar search_end_date) {
		if (search_start_date == null || endsAfter(search_start_date)) {
			if (search_end_date == null || startsBefore(search_end_date)) {
				return true;
			}
		}
		return false;
	}
	
}

class DateComparator implements Comparator<DateNode> {
	public int compare(DateNode d1, DateNode d2) {
		if (d1.getStartDate().before(d2.getStartDate())) {
			return -1;
		}
		return 1;
		
	}
}