import java.lang.String;
import java.util.Calendar;
import java.util.LinkedList;

public class Parser {

	private String command;
	private COMMAND_TYPE commandType;
	private String taskID;
	private String taskName;
	private String taskStartDate;
	private String taskEndDate;
	private LinkedList<String> taskTags

	public final enum COMMAND_TYPE {
		ADD, EDIT, DELETE, LIST, SEARCH, COMPLETE, TAG
	}

	public final enum RECURRING_TYPE {
		INTERVAL, DAY_OF_MONTH, WEEK_OF_MONTH, ANNUAL
	}

	public void parseCommand() {

	}

	public boolean isValidCommand() {

	}

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