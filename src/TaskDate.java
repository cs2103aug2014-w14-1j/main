import java.util.Calendar;

//prototype
public class TaskDate {
	Calendar start_date;
	Calendar end_date;
	int interval;
	
	public TaskDate(Calendar start_date, Calendar end_date) {
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
}