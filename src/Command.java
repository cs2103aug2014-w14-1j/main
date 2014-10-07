import java.util.LinkedList;

public class Command {
	public enum COMMAND_TYPE {
		ADD, EDIT, DELETE, LIST, SEARCH, COMPLETE, TAG, INVALID
	}

	private COMMAND_TYPE commandType;
	private String taskID;
	private String taskName;
	private String taskStartDate;
	private String taskEndDate;
	private LinkedList<String> taskTags;

	public COMMAND_TYPE getCommandType() {

	}

	public String getTaskName() {

	}

	public String getTaskId() {

	}

	public Calendar getTaskStartDate() {

	}

	public Calendar getTaskEndDate() {

	}

	public LinkedList<String> getTaskTags() {

	}

	public RECURRING_TYPE getRecurringType() {

	}

	public int getRecurringTaskInterval() {

	}

	public int getRecurringTaskDayOfMonth() {

	}

	public int getRecurringTaskWeekOfMonth() {

	}

	public int getRecurringTaskWeekOfMonthDay() {

	}

	public int getRecurringTaskMonthInterval() {

	}

	public int getRecurringTaskAnnualMonth() {

	}

	public int getRecurringTaskAnnualDay() {

	}

	public String getSearchTerm() {

	}

	public Calendar getSearchRangeStartDate() {

	}

	public Calendar getSearchRangeEndDate() {

	}

	public ArrayList<String> getSearchRangeTags() {

	}
}