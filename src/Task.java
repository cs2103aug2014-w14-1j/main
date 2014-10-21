import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Calendar;

import org.ocpsoft.prettytime.shade.edu.emory.mathcs.backport.java.util.Collections;

public class Task {
	
	private String taskId;
	private String taskName;
	private String taskDisplayId;
	private LinkedList<TaskDate> taskDates;
	private LinkedList<Calendar> taskReminderDates;
	private Calendar taskDateCompleted;
	private ArrayList<String> taskTag;
	
	/*
	 * No input constructor
	 */
	public Task() {
		this("", "", "", new LinkedList<TaskDate>(), new LinkedList<Calendar>(), null, new ArrayList<String>());
	}
	
	private Task(String taskId, String displayId, String taskName, LinkedList<TaskDate> taskDatesTimes,
			LinkedList<Calendar> reminderDates, Calendar taskDateCompleted, ArrayList<String> taskTag) {
		this.taskId = taskId;
		this.taskDisplayId = displayId;
		this.taskName = taskName;
		this.taskDates = taskDatesTimes;
		this.taskReminderDates = reminderDates;
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
	
	// Task DisplayID************************************

	public void setDisplayId(String id) {
		this.taskDisplayId = id;
	}

	public String getDisplayId() {
		return this.taskDisplayId;
	}

	// Task Name ************************************
	public void setTaskName(String taskname) {
		this.taskName = taskname;
	}

	public String getTaskName() {
		return this.taskName;
	}

	// Task Dates and Times ********************************
	
	//only Controller accesses setters: date input is in Calendar format

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
	
	//Only UI accesses getters: Date output is in String format
	
	public LinkedList<String> getTaskDateTime(int i) {
		return taskDates.get(i).getDates();
	}
	
	public LinkedList<String> getTaskDatesSorted() {
		LinkedList<DateNode> taskDates = getDateNodesSorted(); 
		LinkedList<String> taskStartDatesTranslated = new LinkedList<String>();
		for (DateNode date : taskDates) {
			taskStartDatesTranslated.add(date.getDatesAsString());
		}
		return taskStartDatesTranslated;
	}

	private LinkedList<DateNode> getDateNodesSorted() {
		LinkedList<DateNode> taskSortedDates = new LinkedList<DateNode>();
		DateComparator dateComparator = new DateComparator();
		
		for(int i = 0; i < taskDates.size(); i++) {
			taskSortedDates.addAll(taskDates.get(i).getDateNodes());
		}
		Collections.sort(taskSortedDates, dateComparator);
		return taskSortedDates;
	}
	
	// Task Reminder Dates Times***********************************

	public void addTaskReminderDate(Calendar date) {
		taskReminderDates.add(date);
	}
	
	public LinkedList<Calendar> getTaskReminderDates() {
		return taskReminderDates;
	}

	// Task Floating*******************************

	public boolean isFloating() {
		return this.taskDates.isEmpty() && this.taskDateCompleted == null;
	}

	// Task Recur***************************************
	
	public void updateRecur() {
		for (TaskDate date : taskDates) {
			date.updateRecur();
		}
	}

	// Task Completed*********************************************
	public void setTaskCompleted(Calendar c) {
		this.taskDateCompleted = c;
		removeOldDates();
	}

	public String getTaskDateCompleted() {
		return this.taskDateCompleted.getTime().toString();
	}
	
	public boolean isCompleted() {
		if (taskDateCompleted == null) {
			return false;
		}
		if (taskDates.isEmpty()) {
			return true;
		}
		return taskDateCompleted.after(taskDates.getLast().getEndDate());
	}
	
	private void removeOldDates() {
		if (taskDateCompleted != null) {
			for (TaskDate date : taskDates) {
				date.removeOldDates(taskDateCompleted);
				if (date.isEmpty()) {
					taskDates.remove(date);
				}
			}
		}
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
		if (keywords==null) {
			return true;
		}
		for (String keyword : keywords) {
			if (!containsKeyword(keyword)) {
				return false;
			}
		}
		return true;
	}
	
	public boolean containsTags(ArrayList<String> tags) {
		if (tags == null) {
			return true;
		}
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
