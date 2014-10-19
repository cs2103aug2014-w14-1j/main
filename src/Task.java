import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Calendar;

import org.ocpsoft.prettytime.shade.edu.emory.mathcs.backport.java.util.Collections;

public class Task {
	
	private String taskName;
	private String taskId;
	private LinkedList<TaskDate> taskDates;
	private Calendar taskDateCompleted;
	private ArrayList<String> taskTag;
	
	/*
	 * No input constructor
	 */
	public Task() {
		this("", "", new LinkedList<TaskDate>(), "", null, new ArrayList<String>());
	}
	
	private Task(String taskId, String taskName, LinkedList<TaskDate> taskDatesTimes,
			String taskRecur, Calendar taskDateCompleted, ArrayList<String> taskTag) {
		this.taskId = taskId;
		this.taskName = taskName;
		this.taskDates = taskDatesTimes;
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
	
	public boolean hasNoID() {
		return taskId.equals("");
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

	//recurring adds
	public void addTaskDatesTimes(Calendar start_date, Calendar end_date, String recur, Calendar limit) {
		this.taskDates.add(new TaskDate(start_date, end_date, recur, limit));
	}
	
	public void addTaskDatesTimes(Calendar date, String recur, Calendar limit) {
		addTaskDatesTimes(date, date, recur, limit);
	}
	
	//non-recurring adds
	public void addTaskDatesTimes(Calendar start_date, Calendar end_date) {
		this.taskDates.add(new TaskDate(start_date, end_date));
	}
	
	public void addTaskDatesTimes(Calendar date) {
		addTaskDatesTimes(date, date);
	}
	
	public void clearTaskDatesTimes() {
		taskDates.clear();
	}
	
	public LinkedList<String> getTaskDateTime(int i) {
		return taskDates.get(i).getDates();
	}
	
	public LinkedList<String> getTaskDatesSorted() {
		LinkedList<DateNode> taskStartDates = new LinkedList<DateNode>();
		DateComparator dateComparator = new DateComparator();
		LinkedList<String> taskStartDatesTranslated = new LinkedList<String>();
		for(int i = 0; i < taskDates.size(); i++) {
			taskStartDates.addAll(taskDates.get(i).getDateNodes());
		}
		Collections.sort(taskStartDates, dateComparator);
		for (DateNode date : taskStartDates) {
			taskStartDatesTranslated.add(date.getDates());
		}
		return taskStartDatesTranslated;
	}
	
	// Task Dates and Times ********************************

	// Task Reminder Dates Times***********************************

	

	// Task Reminder Dates Times***********************************

	// Task Floating*******************************

	public boolean isFloating() {
		return this.taskDates.isEmpty();
	}

	// Task Floating*******************************

	// Task Recur***************************************
	
	public void updateRecur() {
		for (TaskDate date : taskDates) {
			date.updateRecur();
			if (taskDateCompleted != null) {
				date.removeOldDates(taskDateCompleted);
			}
		}
	}

	// Task Recur***************************************

	// Task Completed*********************************************
	public void setTaskCompleted(Calendar c) {
		this.taskDateCompleted = c;
	}

	public String getTaskDateCompleted() {
		return this.taskDateCompleted.getTime().toString();
	}
	
	public boolean isCompleted() {
		if (taskDateCompleted == null) {
			return false;
		}
		return taskDateCompleted.after(taskDates.getLast().getEndDate());
	}
	
	public boolean isOverdue() {
		Calendar now = Calendar.getInstance();
		for (TaskDate date: taskDates) {
			if ( (taskDateCompleted == null || taskDateCompleted.before(date.getEndDate()))
					&& now.after(date.getEndDate()) ) {
				return true;
			}
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
		if (isFloating()) {
			return true;		//autopass
		}
		assert !taskDates.isEmpty();
		for (TaskDate date : taskDates) {
			if (date.withinDateRange(start_date, end_date)) {
				return true;
			}
		}
		return false;
	}
}
