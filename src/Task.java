import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.text.SimpleDateFormat;

public class Task {

	private static final SimpleDateFormat sdf = new SimpleDateFormat(
			"EE dd-MM-YY HH:mm");
	private Integer id;
	private String taskName;
	private String displayId;
	private Calendar startDate;
	private Calendar endDate;
	private Calendar dateCompleted;
	private Calendar reminderDate;
	private Integer recur;
	private Calendar recurLimit;
	private ArrayList<String> tags;

	/*
	 * No input constructor
	 */
	public Task() {
		this(null, "", "", null, null, null, null, null, null,
				new ArrayList<String>());
	}

	private Task(Integer taskId, String displayId, String taskName,
			Calendar taskStartDate, Calendar taskEndDate,
			Calendar taskReminderDate, Calendar taskDateCompleted,
			Integer recur, Calendar recurLimit, ArrayList<String> taskTag) {
		this.id = taskId;
		this.displayId = displayId;
		this.taskName = taskName;
		this.startDate = taskStartDate;
		this.endDate = taskEndDate;
		this.dateCompleted = taskDateCompleted;
		this.reminderDate = taskReminderDate;
		this.recur = recur;
		this.recurLimit = recurLimit;
		this.tags = taskTag;
	}

	// Task ID************************************
	public void setId(int id) {
		this.id = id;
	}

	public int getId() {
		return this.id;
	}

	public boolean hasNoID() {
		return id == null;
	}

	// Task DisplayID************************************

	public void setDisplayId(String id) {
		this.displayId = id;
	}

	public String getDisplayId() {
		return this.displayId;
	}

	// Task Name ************************************
	public void setTaskName(String taskname) {
		this.taskName = taskname;
	}

	public String getTaskName() {
		return this.taskName;
	}

	// Task Dates and Times ********************************

	// only Controller accesses setters: date input is in Calendar format

	public void setDates(Calendar startdate, Calendar enddate) {
		this.startDate = startdate;
		this.endDate = enddate;
	}

	public void setStartDate(Calendar startdate) {
		this.startDate = startdate;
	}

	public void setEndDate(Calendar enddate) {
		this.endDate = enddate;
	}

	public void setDate(Calendar date) {
		this.startDate = date;
		this.endDate = (Calendar) date.clone();
	}

	public Calendar getStartDate() {
		return this.startDate;
	}

	public Calendar getEndDate() {
		return this.endDate;
	}

	public Calendar getDate() {
		return this.endDate;
	}
	
	public boolean isFloating() {
		return this.startDate == null && this.endDate == null
				&& this.dateCompleted == null;
	}
	
	public boolean withinDateRange(Calendar start_date, Calendar end_date) {
		if (isFloating()) {
			return true; // autopass
		}
		if (start_date == null || start_date.before(this.endDate)
				|| start_date.equals(this.endDate)) {
			if (end_date == null || end_date.after(this.startDate)
					|| end_date.equals(this.startDate)) {
				return true;
			}
		}
		return false;
	}

	// Only UI accesses getters: Date output is in String format

	public String getStartDateAsString() {
		if (this.startDate == null) {
			return "";
		}
		return sdf.format(startDate.getTime());
	}

	public String getEndDateAsString() {
		if (this.endDate == null) {
			return "";
		}
		return sdf.format(endDate.getTime());
	}

	public String getDateAsString() {
		if (isFloating()) {
			return "";
		}
		if (this.startDate.equals(this.endDate)) {
			return sdf.format(endDate.getTime());
		}
		return sdf.format(startDate.getTime()) + " - "
				+ sdf.format(endDate.getTime());
	}

	// Task Completed*********************************************
	public void setDateCompleted(Calendar c) {
		this.dateCompleted = c;
	}

	public Calendar getDateCompleted() {
		return this.dateCompleted;
	}

	public boolean isCompleted() {
		if (this.dateCompleted == null) {
			return false;
		}
		if (this.endDate == null && this.dateCompleted != null) {
			return true;
		}
		return this.dateCompleted.after(this.endDate);
	}

	public boolean isOverdue() {
		if (isFloating()) {
			return false;
		}

		if (isCompleted()) {
			return false;
		}

		Calendar now = Calendar.getInstance();

		if (this.endDate.before(now)
				&& (this.dateCompleted == null || this.endDate
						.after(this.dateCompleted))) {
			return true;
		}

		return false;
	}

	public String getDateCompletedAsString() {
		return sdf.format(this.dateCompleted.getTime());
	}
	
	// Task Reminder Dates Times***********************************

	public void setReminderDate(Calendar date) {
		this.reminderDate = date;
	}

	public Calendar getReminderDate() {
		return this.reminderDate;
	}

	public String getReminderDateAsString() {
		return sdf.format(reminderDate.getTime());
	}

	// Task Recur***************************************

	public void setRecur(Integer field) {
		this.recur = field;
	}

	public Integer getRecur() {
		return this.recur;
	}

	public boolean isRecur() {
		return this.recur != null && this.startDate != null
				&& this.endDate != null;
	}

	public void setRecurLimit(Calendar limit) {
		this.recurLimit = limit;
	}

	public Calendar getRecurLimit() {
		return this.recurLimit;
	}

	// Task Tags**************************************
	public void addTag(String str) {
		this.tags.add(str);
	}

	public ArrayList<String> getTags() {
		return this.tags;
	}

	private boolean containsTag(String tag) {
		return tags.contains(tag);
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

	public String getTagsAsString() {
		String tagsUI = "";
		for (int i = 0; i < tags.size(); i++) {
			tagsUI += tags.get(i);
			if (i < tags.size() - 1) {
				tagsUI += ", ";
			}
		}
		return tagsUI;
	}

	// Additional methods*****************************************

	// Search methods********************
	
	private boolean containsKeyword(String keyword) {
		return taskName.contains(keyword);
	}

	public boolean containsKeywords(ArrayList<String> keywords) {
		if (keywords == null) {
			return true;
		}
		for (String keyword : keywords) {
			if (!containsKeyword(keyword)) {
				return false;
			}
		}
		return true;
	}

	// Clone
	// methods***************************************************************

	public Task clone() {
		Task task = new Task();
		task.setId(this.id);
		task.setTaskName(this.taskName);
		if (this.startDate != null) {
			task.setStartDate((Calendar) this.startDate.clone());
		}
		if (this.endDate != null) {
			task.setEndDate((Calendar) this.endDate.clone());
		}
		if (this.dateCompleted != null) {
			task.setDateCompleted((Calendar) this.dateCompleted.clone());
		}
		if (this.reminderDate != null) {
			task.setReminderDate((Calendar) this.reminderDate.clone());
		}
		task.setRecur(this.recur);
		if (this.recurLimit != null) {
			task.setRecurLimit((Calendar) this.recurLimit.clone());
		}
		for (String tag : tags) {
			task.addTag(tag);
		}
		return task;
	}
}

class TaskComparator implements Comparator<Task> {
	public int compare(Task a, Task b) {
		if (a.getStartDate() == null && b.getStartDate() == null) {
			return 0;
		}
		if (a.getStartDate() == null && b.getStartDate() != null) {
			return -1;
		}
		if (a.getStartDate() != null && b.getStartDate() == null) {
			return 1;
		}
		if (a.getStartDate().before(b.getStartDate())) {
			return -1;
		}
		if (a.getStartDate().equals(b.getStartDate())) {
			return 0;
		}
		return 1;
	}
}