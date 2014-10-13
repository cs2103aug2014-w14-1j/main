import java.util.Calendar;
import java.util.LinkedList;
import java.util.logging.*;

//prototype
public class TaskDate {
	private static final Logger dateLogger = Logger.getLogger(TaskDate.class.getName());
	
	private static final String RECUR_YEAR = "year";
	private static final String RECUR_MONTH = "month";
	private static final String RECUR_WEEK = "week";
	private static final String RECUR_DAY = "day";
	
	private LinkedList<DateNode> nodes;
	private String recur;
	private Calendar limit;
	
	public TaskDate(Calendar start_date, Calendar end_date, String recur, Calendar limit) {
		dateLogger.log(Level.INFO, "Creating new TaskDate");
		this.nodes = new LinkedList<DateNode>();
		this.nodes.add(new DateNode(start_date, end_date));
		this.recur = recur;
		this.limit = limit;
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
		dateLogger.log(Level.INFO, "Updating dates of recurring task");
		if (recur.equals("") || limit == null || nodes.isEmpty()) {
			dateLogger.log(Level.WARNING, Boolean.toString(recur.equals("")) + " " +
					Boolean.toString(limit == null) + " " +
					Boolean.toString(nodes.isEmpty()));
			return;
		}
		//testing purposes
		int old_size = nodes.size();
		
		DateNode first = nodes.getLast();
		while (first.getEndDate().before(limit)) {
			Calendar a = (Calendar) first.getStartDate().clone();
			Calendar b = (Calendar) first.getEndDate().clone();
			dateLogger.log(Level.INFO, a.getTime().toString() + "\n" + b.getTime().toString());
			if (recur.equals(RECUR_YEAR)) {
				a.add(Calendar.YEAR, 1);
				b.add(Calendar.YEAR, 1);
			}
			else if (recur.equals(RECUR_MONTH)) {
				a.add(Calendar.MONTH, 1);
				b.add(Calendar.MONTH, 1);
			}
			else if (recur.equals(RECUR_WEEK)) {
				a.add(Calendar.WEEK_OF_YEAR, 1);
				b.add(Calendar.WEEK_OF_YEAR, 1);
			}
			else if (recur.equals(RECUR_DAY)) {
				a.add(Calendar.DAY_OF_YEAR, 1);
				b.add(Calendar.DAY_OF_YEAR, 1);
			}
			first = new DateNode(a, b);
			nodes.addLast(first);
			
			assert nodes.size() > old_size;
		}
		dateLogger.log(Level.INFO, "Updated!");
	}
	
	public boolean withinDateRange(Calendar start_date, Calendar end_date) {
		for (DateNode date : nodes) {
			if (date.withinDateRange(start_date, end_date)) {
				return true;
			}
		}
		return false;
	}
	
	public LinkedList<DateNode> getDates() {
		assert nodes != null;
		return nodes;
	}
	
	public Calendar getStartDate() {
		return nodes.getFirst().getStartDate();
	}
	
	public Calendar getEndDate() {
		return nodes.getLast().getEndDate();
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
			if (start_date == null || endsAfter(search_start_date)) {
				if (end_date == null || startsBefore(search_end_date)) {
					return true;
				}
			}
			return false;
		}
		
	}
}