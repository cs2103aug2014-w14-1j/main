import java.util.ArrayList;
import java.util.Calendar;

public class Task {
	private static final String RECUR_YEAR = "year";
	private static final String RECUR_MONTH = "month";
	private static final String RECUR_WEEK = "week";
	private static final String RECUR_DAY = "day";
	
	private String taskName;
	private String taskId;
	private TaskDate taskDate;
	private String taskRecur;
	private Calendar taskDateCompleted;
	private ArrayList<String> taskTag;
	
	/*
	 * No input constructor
	 */
	public Task() {
		taskId = "";
		taskName = "";
		taskDate = null;
		taskRecur = "";
		taskDateCompleted = null;
		taskTag = new ArrayList<String>();
	}
	
	public Task(String taskId, String taskName, TaskDate taskDatesTimes, boolean taskFloating,
			String taskRecur, Calendar taskDateCompleted, ArrayList<String> taskTag) {
		this.taskId = taskId;
		this.taskName = taskName;
		this.taskDate = taskDatesTimes;
		this.taskRecur = taskRecur;
		this.taskDateCompleted = taskDateCompleted;
		this.taskTag = taskTag;
	}

	// Task ID************************************
	public void setTaskId(int id) {
		this.taskId = "" + id;
	}

	public String getTaskId() {
		return this.taskId;
	}

	// Task ID************************************

	// Task Name ************************************
	public void setTaskName(String taskname) {
		this.taskName = taskname;
	}

	public String getTaskName() {
		return this.taskName;
	}

	// Task Name ************************************

	// Task Dates and Times ********************************
	public void setTaskDatesTimes(TaskDate al) {
		this.taskDate = al;
	}
	
	public void setTaskDatesTimes(Calendar start_date, Calendar end_date) {
		this.taskDate = new TaskDate(start_date, end_date);
	}
	
	public void setTaskDatesTimes(Calendar date) {
		setTaskDatesTimes(date, date);
	}
	
	/*
	public void removeTaskDatesTimes(TaskDate date){
		
	}
	
	public void addTaskDatesTimes(TaskDate date) {
		
	}
	*/
	

	public TaskDate getTaskDatesTimes() {
		return this.taskDate;
	}
	
	// Task Dates and Times ********************************

	// Task Reminder Dates Times***********************************

	

	// Task Reminder Dates Times***********************************

	// Task Floating*******************************

	public boolean isFloating() {
		return this.taskDate == null;
	}

	// Task Floating*******************************

	// Task Recur***************************************
	public void setTaskRecur(String str) {
		this.taskRecur = str;
	}

	public String getTaskRecur() {
		return this.taskRecur;
	}
	
	public boolean isRecur() {
		return !this.taskRecur.equals("");
	}
	
	//TBC
	public void updateRecurDates(int YEAR_LIMIT) {
		if (taskRecur.equals(RECUR_YEAR)) {
		}
		
		else if (taskRecur.equals(RECUR_MONTH)) {
			
		}
		
		else if (taskRecur.equals(RECUR_WEEK)) {
			
		}
		
		else if (taskRecur.equals(RECUR_DAY)) {
			
		}
	}

	// Task Recur***************************************

	// Task Completed*********************************************
	public void setTaskCompleted(Calendar c) {
		this.taskDateCompleted = c;
	}

	public Calendar getTaskDateCompleted() {
		return this.taskDateCompleted;
	}
	
	public boolean isCompleted() {
		if (taskDateCompleted == null) {
			return false;
		}
		return taskDateCompleted.after(taskDate.getEndDate());
	}
	
	public boolean isOverdue() {
		Calendar now = Calendar.getInstance();
		if ( (taskDateCompleted == null || taskDateCompleted.before(taskDate.getEndDate()))
				&& now.after(taskDate.getEndDate()) ) {
			return true;
		}
		return false;
	}

	// Task Completed*********************************************

	// Task Tags**************************************
	public void addTaskTags(String str) {
		this.taskTag.add(str);
	}

	public ArrayList<String> getTaskTags() {
		return this.taskTag;
	}
	
	// Additional methods*****************************************
	
	// Search function********************
	
	private boolean containsKeyword(String keyword) {
		return taskName.contains(keyword);
	}
	
	private boolean containsTag(String tag) {
		return taskTag.contains(tag);
	}
	
	public boolean containsKeywords(ArrayList<String> keywords) {
		for (String keyword : keywords) {
			if (!containsKeyword(keyword)) {
				return false;
			}
		}
		return true;
	}
	
	public boolean containsTags(ArrayList<String> tags) {
		for (String tag : tags) {
			if (!containsTag(tag)) {
				return false;
			}
		}
		return true;
	}
	
	public boolean withinDateRange(Calendar start_date, Calendar end_date) {
		if (isFloating()) return true;		//autopass
		return taskDate.withinDateRange(start_date, end_date);
	}
}
