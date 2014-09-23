import java.util.ArrayList;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;


public class Task {
	private JSONObject obj;
	
	private static final String TASK_NAME = "taskname";
	private static final String TASK_ID = "ID";
	private static final String TASK_DATES_TIMES = "taskDteTimes";
	private static final String TASK_REMINDER_DATES_TIMES = "reminderDteTimes";
	private static final String TASK_FLOATING = "isFloating";
	private static final String TASK_RECUR = "recurTasks"; 
	private static final String TASK_COMPLETED = "isCompleted";
	private static final String TASK_TAGS = "tags";
	
	private static final String emptyVal = "";
	
	public Task() throws JSONException{
		obj = new JSONObject();
		obj.put(TASK_ID, emptyVal);
		obj.put(TASK_NAME, emptyVal);
		obj.put(TASK_DATES_TIMES, new ArrayList<Date>());
		obj.put(TASK_REMINDER_DATES_TIMES, new ArrayList<Date>());
		obj.put(TASK_FLOATING, false);
		obj.put(TASK_RECUR, emptyVal);
		obj.put(TASK_COMPLETED, false);
		obj.put(TASK_TAGS, new ArrayList<String>());
	}
	
}
