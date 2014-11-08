//@author A0097299E
package speed.task;
import java.util.ArrayList;
import java.util.Calendar;

import java.text.SimpleDateFormat;

/*
 * This class is used to represent a user task, which can be modified and stored by other classes. The
 * Task class contains several fields meant to cater to as wide a range of applications as possible. The
 * primary fields are taskName (description of task), the date fields and tags.
 * 
 * Tasks are categorised into four types: Floating (no dates), Overdue (past the current time), Completed
 * (marked as completed), and Normal (otherwise). The four are mutually exclusive and spans all possible
 * tasks this software intends to cover. In addition, this class supports recurring tasks. This is represented
 * by the recurX fields, which are to be used by other classes to create recurring copies.
 */

public class Task {

	private static final SimpleDateFormat sdf = new SimpleDateFormat("EE d-MMM-yy H:mm a");
	private static final int RECUR_PATTERN_INVALID = -1;
	private static final int RECUR_PERIOD_VALID_MINIMUM = 1;
	
	private Integer id;
	private String taskName;
	private String displayId;
	private Calendar start_date;
	private Calendar end_date;
	private Calendar dateCompleted;
	private Integer recur_pattern;
	private Integer recur_period;
	private Calendar recur_limit;								//NOT USED as of V0.4. Always null
	private ArrayList<String> tags;

	//Constructors******************************************************************************
	
	/*
	 * No input constructor. This should be the only used constructor. Fields are then to be added
	 * by provided setter methods
	 */
	public Task() {
		this(null, "", "", null, null, null, RECUR_PATTERN_INVALID, RECUR_PERIOD_VALID_MINIMUM - 1,
				null, new ArrayList<String>());
	}

	//private constructor
	private Task(Integer taskId, String displayId, String taskName,
			Calendar taskStartDate, Calendar taskEndDate, Calendar taskDateCompleted,
			Integer recur, Integer recurPeriod, Calendar recurLimit, ArrayList<String> taskTag) {
		this.id = taskId;
		this.displayId = displayId;
		this.taskName = taskName;
		this.start_date = taskStartDate;
		this.end_date = taskEndDate;
		this.dateCompleted = taskDateCompleted;
		this.recur_pattern = recur;
		this.recur_period = recurPeriod;
		this.recur_limit = recurLimit;
		this.tags = taskTag;
	}

	// Task ID*************************************************************************
	
	/*
	 * This field is used for identifying tasks.
	 * 
	 * Note: This field is only used by the Storage, and has no consequences in the UI and Controller.
	 * Tasks with the same id are grouped as the same "family" of tasks. This has consequences in the
	 * Storage (recurring tasks).
	 */
	
	public void setId(int id) {
		this.id = id;
	}

	public int getId() {
		return this.id;
	}

	public boolean hasNoID() {
		return id == null || id < 0;
	}

	// Task DisplayID******************************************************************

	/*
	 * This field is a separate id, for identifying tasks.
	 * 
	 * Note: This field is only used by the UI and Controller, and has no consequence in
	 * the Storage. It is meant for display purposes. Multiple tasks can have the same
	 * displayId.
	 */
	
	public void setDisplayId(String id) {
		this.displayId = id;
	}

	public String getDisplayId() {
		return this.displayId;
	}

	// Task Name***********************************************************************
	
	/*
	 * This field contains the description of the task.
	 */
	
	public void setTaskName(String taskname) {
		this.taskName = taskname;
	}

	public String getTaskName() {
		return this.taskName;
	}
	
	private boolean containsKeyword(String keyword) {
		return taskName.toLowerCase().contains(keyword.toLowerCase());
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

	// Task Dates**********************************************************************
	
	/*
	 * Sets the field start_date, end_date, recur_pattern, recur_period and recur_limit.
	 * 
	 * start_date: starting date and time of the task.
	 * end_date: ending date and time of the task.
	 * recur_pattern: The type of recurring pattern, based on Calendar fields. Includes Calendar.YEAR,
	 * 		Calendar.MONTH, Calendar.WEEK_OF_YEAR, Calendar.DAY_OF_YEAR etc.
	 * recur_period: The period at which this task recurs. For example, every 2 years => pattern = YEAR, period = 2
	 * recur_limit: A date, which the recurring task ends by then. NOT USED as of V0.4.
	 * 
	 * ASSUMPTION 1: start_date and end_date are always either both null or both existing. No support
	 * for tasks with null start_date and non-null end_date, and vice versa.
	 * 
	 * ASSUMPTION 2: For tasks with a single date, the start_date and end_date are the same.
	 * 
	 * ASSUMPTION 3: recur_pattern is either INVALID or an appropriate Calendar field.
	 * 
	 * ASSUMPTION 4: recur_period is not negative.
	 */
	
	public void setDates(Calendar startdate, Calendar enddate, int recurpattern, int recurperiod, Calendar recurlimit) {
		
		//perform an internal check to see if end date is before start date. If it is, do a swap.
		if (enddate!= null && startdate!= null && enddate.before(startdate)) {
			Calendar temp = enddate;
			enddate = startdate;
			startdate = temp;
		}
		
		//perform a check and see if startdate is null when enddate is not. If so, clone startdate
		if (enddate!=null && startdate == null) {
			startdate = (Calendar) enddate.clone();
		}
		
		this.start_date = startdate;
		this.end_date = enddate;
		this.recur_pattern = recurpattern;
		this.recur_period = recurperiod;
		this.recur_limit = recurlimit;
	}

	/*
	 * Sets start_date and end_date. Recurring fields are immediately set to invalid,
	 * non-recurring values. This is for nonrecurring tasks.
	 * 
	 * NOT USED as of V0.4.
	 */
	public void setDates(Calendar startdate, Calendar enddate) {
		setDates(startdate, enddate, RECUR_PATTERN_INVALID, RECUR_PERIOD_VALID_MINIMUM - 1, null);
	}

	/*
	 * Sets a single date. end_date is set to be the same as the start_date. This is for
	 * tasks with only a single date.
	 * 
	 * NOT USED as of V0.4.
	 */
	public void setDate(Calendar date, int recur_pattern, int recur_period, Calendar recur_limit) {
		setDates(date, (Calendar) date.clone(), recur_pattern, recur_period, recur_limit);
	}

	/*
	 * Sets a single date without recurring fields.
	 * 
	 * NOT USED as of V0.4.
	 */
	public void setDate(Calendar date) {
		setDate(date, RECUR_PATTERN_INVALID, RECUR_PERIOD_VALID_MINIMUM, null);
	}
	
	/*
	 * Setters for individual fields.
	 * 
	 * WARNING: Can lead to potential abuse. Use with caution.
	 */
	private void setStartDate(Calendar startdate) {
		this.start_date = startdate;
	}

	private void setEndDate(Calendar enddate) {
		this.end_date = enddate;
	}
	
	public void setRecur(Integer pattern, Integer period) {
		this.recur_pattern = pattern;
		this.recur_period = period;
	}

	public void setRecurLimit(Calendar limit) {
		this.recur_limit = limit;
	}

	/*
	 * Getter methods
	 */
	
	public Calendar getStartDate() {
		return this.start_date;
	}

	public Calendar getEndDate() {
		return this.end_date;
	}
	
	/*
	 * Meant for dates with a single date (start_date same as end_date)
	 * NOT USED as of V0.4.
	 */
	public Calendar getDate() {
		return this.end_date;
	}
	
	public Integer getRecurPattern() {
		return this.recur_pattern;
	}
	
	public Integer getRecurPeriod() {
		return this.recur_period;
	}

	public Calendar getRecurLimit() {
		return this.recur_limit;
	}
	
	/*
	 * Checks whether this is a recurring task. Recurring tasks are classified as:
	 * Appropriate pattern: (Default invalid: -1)
	 * Appropriate period (>=1)
	 * Non-null start and end dates (not floating)
	 */
	public boolean isRecur() {
		return this.recur_pattern != RECUR_PATTERN_INVALID &&
				this.recur_period >= RECUR_PERIOD_VALID_MINIMUM &&
				this.start_date != null && this.end_date != null;
	}

	/*
	 * Gets the dates as String format.
	 * 
	 * Note: Primarily for the UI, so that it does not need to know how to read Calendar.
	 */

	public String getStartDateAsString() {
		if (this.start_date == null) {
			return "";
		}
		return sdf.format(start_date.getTime());
	}

	public String getEndDateAsString() {
		if (this.end_date == null) {
			return "";
		}
		return sdf.format(end_date.getTime());
	}

	public String getDateAsString() {
		if (isFloating()) {
			return "";
		}
		if (this.start_date == null) {
			return sdf.format(end_date.getTime());
		}
		if (this.end_date == null) {
			return sdf.format(start_date.getTime());
		}
		if (this.start_date.equals(this.end_date)) {
			return sdf.format(end_date.getTime());
		}
		return sdf.format(start_date.getTime()) + " -\n"
				+ sdf.format(end_date.getTime());
	}
	
	public String getRecurAsString() {
		String result = "every ";
		if (recur_period > 1) {
			result += recur_period + " ";
		}
		switch (this.recur_pattern) {
			case Calendar.YEAR:
				result += "year";
				break;
			case Calendar.MONTH:
				result += "month";
				break;
			case Calendar.WEEK_OF_YEAR:
				result += "week";
				break;
			case Calendar.DAY_OF_YEAR:
				result += "day";
				break;
			default:
				return "";
		}
		if (recur_period > 1) {
			result += "s";
		}
		return result;
	}

	// Task Completed******************************************************************
	
	/*
	 * The dateCompleted field checks whether this Task is completed. A task is considered
	 * completed if the dateCompleted is equal or after its end_date. If it is a floating
	 * task, a non-null dateCompleted field means it's completed. A null dateCompleted field
	 * immediately means incomplete.
	 */
	
	public void setDateCompleted(Calendar c) {
		this.dateCompleted = c;
	}
	
	/*
	 * Easier method so that external classes need not look for an appropriate date input 
	 */
	public void setCompleted() {
		Calendar dateCompleted = Calendar.getInstance();
		if (this.end_date != null) {
			dateCompleted = (Calendar) this.end_date.clone();
			dateCompleted.add(Calendar.SECOND, 1);
		}
		setDateCompleted(dateCompleted);
	}
	
	public void setIncomplete() {
		setDateCompleted(null);
	}

	public Calendar getDateCompleted() {
		return this.dateCompleted;
	}

	public boolean isCompleted() {
		if (this.dateCompleted == null) {
			return false;
		}
		if (this.end_date == null && this.dateCompleted != null) {		//assumption that start_date.equals == null and start_date <= end_date
			return true;
		}
		return this.dateCompleted.after(this.end_date);					//assumption that start_date.equals(end_date)
	}

	/*
	 * Returns dateCompleted as a String.
	 * 
	 * Note: Meant for UI
	 * 
	 * NOT USED as of V0.4.
	 */
	public String getDateCompletedAsString() {
		return sdf.format(this.dateCompleted.getTime());
	}
	
	// Other checks*******************************************************************************
	
	/*
	 * Floating task check. Floating tasks are considered incomplete tasks with null start and end dates.
	 * 
	 * ASSUMPTION: start_date == null iff end_date == null.
	 */
	public boolean isFloating() {
		return this.start_date == null && this.end_date == null
				&& this.dateCompleted == null;
	}

	/*
	 * Overdue task check. Floating and Completed tasks automatically fail this check: Floating
	 * will never be overdue, and Completed is completed. Generates an instance of the time now
	 * and compares with the end date.
	 */
	public boolean isOverdue() {
		if (isFloating()) {
			return false;
		}

		if (isCompleted()) {
			return false;
		}

		assert end_date != null;
		Calendar now = Calendar.getInstance();
		
		if (this.end_date.before(now)) {
			return true;
		}

		return false;
	}

	/*
	 * Checks whether the task is within two input dates. As long as one of the task dates is within
	 * this interval, this check returns true. Floating and Overdue tasks immediately pass this check.
	 */
	public boolean withinDateRange(Calendar start_date, Calendar end_date) {
		if (isFloating()) {
			return true;
		}
		if (isOverdue()) {
			return true;
		}
		if (start_date == null || start_date.before(this.end_date)
				|| start_date.equals(this.end_date)) {
			if (end_date == null || end_date.after(this.start_date)
					|| end_date.equals(this.start_date)) {
				return true;
			}
		}
		return false;
	}

	// Task Tags**********************************************************************
	public void addTag(String tag) {
		this.tags.add(tag);
		this.tags.sort(null);
	}
	
	public void removeTag(String tag) {
		this.tags.remove(tag);
	}

	public ArrayList<String> getTags() {
		return this.tags;
	}

	private boolean containsTag(String tag) {
		for (String t : this.tags) {
			if (t.toLowerCase().equals(tag.toLowerCase())) {
				return true;
			}
		}
		return false;
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

	// Clone methods***************************************************************

	public Task clone() {
		Task task = new Task();
		task.setId(this.id);
		task.setTaskName(this.taskName);
		if (this.start_date != null) {
			task.setStartDate((Calendar) this.start_date.clone());
		}
		if (this.end_date != null) {
			task.setEndDate((Calendar) this.end_date.clone());
		}
		if (this.dateCompleted != null) {
			task.setDateCompleted((Calendar) this.dateCompleted.clone());
		}
		task.setRecur(this.recur_pattern, this.recur_period);
		if (this.recur_limit != null) {
			task.setRecurLimit((Calendar) this.recur_limit.clone());
		}
		for (String tag : tags) {
			task.addTag(tag);
		}
		return task;
	}
}

