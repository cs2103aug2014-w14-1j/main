import java.util.Calendar;
import java.util.LinkedList;

//prototype
public class TaskDate {
	private LinkedList<DateNode> nodes;
	private String recur;
	
	public TaskDate(Calendar start_date, Calendar end_date, String recur, Calendar year_limit) {
		this.nodes = new LinkedList<DateNode>();
		this.nodes.add(new DateNode(start_date, end_date));
		this.recur = recur;
		updateRecur(year_limit);
	}
	
	public TaskDate(Calendar start_date, Calendar end_date) {
		this(start_date, end_date, "", null);
	}
	
	public boolean isRecur() {
		return !recur.equals("");
	}
	
	//assumes at least one datenode
	public void updateRecur(Calendar limit) {
		if (limit == null) return;
		DateNode first = nodes.getLast();
		while (first.getEndDate().before(limit)) {
			first = new DateNode((Calendar) first.getStartDate().clone(), (Calendar) first.getEndDate().clone());
			nodes.addLast(first);
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
	
	public Calendar getStartDate() {
		return nodes.getFirst().getStartDate();
	}
	
	public Calendar getEndDate() {
		return nodes.getLast().getEndDate();
	}
	
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