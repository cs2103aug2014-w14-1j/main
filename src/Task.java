
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;

public class Task {
	private String taskName;
	private String taskId;
	private LinkedList<Calendar> taskDatesTimes;
	private LinkedList<Calendar> taskReminderDatesTimes;
	private boolean taskFloating;
	private String taskRecur;
	private Calendar taskDateCompleted;
	private ArrayList<String> taskTag;
	
	/*
	 * No input constructor
	 */
	public Task() {
		taskId = "";
		taskName = "";
		taskDatesTimes = new LinkedList<Calendar>();
		taskReminderDatesTimes = new LinkedList<Calendar>();
		taskFloating = false;
		taskRecur = "";
		taskDateCompleted = null;
		taskTag = new ArrayList<String>();
	}
	
	public Task(String taskId, String taskName, LinkedList<Calendar> taskDatesTimes,
			LinkedList<Calendar> taskReminderDatesTimes, boolean taskFloating,
			String taskRecur, Calendar taskDateCompleted, ArrayList<String> taskTag) {
		this.taskId = taskId;
		this.taskName = taskName;
		this.taskDatesTimes = taskDatesTimes;
		this.taskReminderDatesTimes = taskReminderDatesTimes;
		this.taskFloating = taskFloating;
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
	public void setTaskDatesTimes(LinkedList<Calendar> al){
		this.taskDatesTimes = al;
	}
	public void removeTaskDatesTimes(Calendar date){
		this.taskDatesTimes.remove(date);
	}
	public void addTaskDatesTimes(Calendar date) {
		this.taskDatesTimes.add(date);
	}

	public LinkedList<Calendar> getTaskDatesTimes() {
		return this.taskDatesTimes;
	}
	// Task Dates and Times ********************************

	// Task Reminder Dates Times***********************************
	public void setTaskReminderDatesTimes(LinkedList<Calendar> al){
		this.taskReminderDatesTimes = al;
	}
	public void removeTaskReminderDatesTimes(Calendar date){
		this.taskReminderDatesTimes.remove(date);
	}
	public void addTaskReminderDatesTimes(Calendar date) {
		this.taskReminderDatesTimes.add(date);
	}

	public LinkedList<Calendar> getTaskReminderDatesTimes() {
		return this.taskReminderDatesTimes;
	}

	// Task Reminder Dates Times***********************************

	// Task Floating*******************************
	public void setTaskFloating(boolean b) {
		this.taskFloating = b;
	}

	public boolean isTaskFloating() {
		return this.taskFloating;
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

	// Task Recur***************************************

	// Task Completed*********************************************
	public void setTaskCompleted(Calendar c) {
		this.taskDateCompleted = c;
	}

	public Calendar getTaskDateCompleted() {
		return this.taskDateCompleted;
	}
	
	public boolean isCompleted() {
		return taskDateCompleted.after(taskDatesTimes.getLast());
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
		if (taskFloating) return true;		//autopass
		for (Calendar date : taskDatesTimes) {
			if (start_date == null || date.after(start_date)) {
				if (end_date == null || date.before(end_date)) {
					return true;
				}
			}
		}
		return false;
	}
}
