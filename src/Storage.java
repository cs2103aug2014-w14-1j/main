import java.util.*;
import java.io.*;
import org.json.*;

/*
 * Tasks contain
 * -ID
 * -Description
 * -Tags
 * -Date(s) and time(s)
 * -Recurring (Weekly, Yearly, Monthly)
 * -Overdue (boolean)
 */

public class Storage {
	private ArrayList<Task> al_task;
	private ArrayList<Task> al_task_floating;
	private ArrayList<Task> al_task_overdue;
	private int counter;
	
	public Storage(){
		
		//read files into arrayList.
	}

	public void add(JSONObject json) {

	}
	
	public void delete(JSONObject json) {
		
	}
	
	public void edit(JSONObject json) {
		
	}
	
	/*
	 * Searches for keywords, tags within date range.
	 */
	//public ArrayList<JSONObject> search(String keyword, String tag, ArrayList<String> dates) {
		//if no keyword, no tag and no date, throw error
		//if no date, search for all dates
//	}
}