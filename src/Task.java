
import java.util.ArrayList;
import java.util.Date;

public class Task {
	private String taskName;
	private String taskId;
	private ArrayList<Date> taskDatesTimes;
	private ArrayList<Date> taskReminderDatesTimes;
	private boolean taskFloating;
	private String taskRecur;
	private boolean taskCompleted;
	private ArrayList<String> taskTag;
		
	public Task() {
		taskId = "";
		taskName = "";
		taskDatesTimes = new ArrayList<Date>();
		taskReminderDatesTimes = new ArrayList<Date>();
		taskFloating = false;
		taskRecur = "";
		taskCompleted = false;
		taskTag = new ArrayList<String>();
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
	public void setTaskDatesTimes(ArrayList<Date> al){
		this.taskDatesTimes = al;
	}
	public void removeTaskDatesTimes(Date date){
		this.taskDatesTimes.remove(date);
	}
	public void addTaskDatesTimes(Date date) {
		this.taskDatesTimes.add(date);
	}

	public ArrayList<Date> getTaskDatesTimes() {
		return this.taskDatesTimes;
	}
	// Task Dates and Times ********************************

	// Task Reminder Dates Times***********************************
	public void setTaskReminderDatesTimes(ArrayList<Date> al){
		this.taskReminderDatesTimes = al;
	}
	public void removeTaskReminderDatesTimes(Date date){
		this.taskReminderDatesTimes.remove(date);
	}
	public void addTaskReminderDatesTimes(Date date) {
		this.taskReminderDatesTimes.add(date);
	}

	public ArrayList<Date> getTaskReminderDatesTimes() {
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

	// Task Recur***************************************

	// Task Completed*********************************************
	public void setTaskCompleted(boolean b) {
		this.taskCompleted = b;
	}

	public boolean getTaskCompleted() {
		return this.taskCompleted;
	}

	// Task Completed*********************************************

	// Task Tags**************************************
	public void addTaskTags(String str) {
		this.taskTag.add(str);
	}

	public ArrayList<String> getTaskTags() {
		return this.taskTag;
	}
	
}
