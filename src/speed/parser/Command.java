//@author A0116150X
package speed.parser;

import java.util.ArrayList;
import java.util.Calendar;

public class Command {
	public enum COMMAND_TYPE {
		DEFAULT, ADD, EDIT, DELETE, LIST, SEARCH, COMPLETE, UNDO, REDO, EXIT, TEST
	}

	private COMMAND_TYPE commandType;
	private String taskID;
	private String taskName;
	private Calendar taskStartDate;
	private Calendar taskEndDate;
	private Calendar searchStartDate;
	private Calendar searchEndDate;
	private ArrayList<String> searchKeywords;
	private String[] searchTags;
	private String[] taskIDsToDelete;
	private String[] taskIDsToComplete;
	private String[] taskTagsToAdd;
	private String[] taskTagsToRemove;
	private int recurPattern = -1;
	private int recurPeriod;

	public void setCommandType(COMMAND_TYPE parsedCommandType) {
		commandType = parsedCommandType;
	}

	public COMMAND_TYPE getCommandType() {
		if (commandType == COMMAND_TYPE.DEFAULT) {
			return COMMAND_TYPE.ADD;
		} else {
			return commandType;
		}
	}

	public void setTaskID(String ID) {
		taskID = ID;
	}

	public String getTaskID() {
		return taskID;
	}

	public void setTaskName(String name) {
		taskName = name;
	}

	public String getTaskName() {
		return taskName;
	}

	public void setTaskStartDate(Calendar startDate) {
		taskStartDate = startDate;
	}

	public Calendar getTaskStartDate() {
		return taskStartDate;
	}

	public void setTaskEndDate(Calendar endDate) {
		taskEndDate = endDate;
	}

	public Calendar getTaskEndDate() {
		return taskEndDate;
	}

	public void setTaskIDsToDelete(String[] IDs) {
		taskIDsToDelete = IDs;
	}

	public String[] getTaskIDsToDelete() {
		return taskIDsToDelete;
	}
	public void setTaskIDsToComplete(String[] IDs) {
		taskIDsToComplete = IDs;
	}

	public String[] getTaskIDsToComplete() {
		return taskIDsToComplete;
	}

	public Calendar getSearchStartDate() {
		return searchStartDate;
	}

	public void setSearchStartDate(Calendar searchStartDate) {
		this.searchStartDate = searchStartDate;
	}

	public Calendar getSearchEndDate() {
		return searchEndDate;
	}

	public void setSearchEndDate(Calendar searchEndDate) {
		this.searchEndDate = searchEndDate;
	}

	public ArrayList<String> getSearchKeywords() {
		return searchKeywords;
	}

	public void setSearchKeywords(ArrayList<String> keywords) {
		searchKeywords = keywords;
	}

	public String[] getSearchTags() {
		return searchTags;
	}

	public void setSearchTags(String[] tags) {
		searchTags = tags;
	}

	public String[] getTaskTagsToAdd() {
		return taskTagsToAdd;
	}

	public void setTaskTagsToAdd(String[] tags) {
		taskTagsToAdd = tags;
	}

	public String[] getTaskTagsToRemove() {
		return taskTagsToRemove;
	}

	public void setTaskTagsToRemove(String[] tags) {
		taskTagsToRemove = tags;
	}

	public void setRecurPattern(int pattern) {
		recurPattern = pattern;
	}

	public int getRecurPattern() {
		return recurPattern;
	}

	public void setRecurPeriod(int period) {
		recurPeriod = period;
	}

	public int getRecurPeriod() {
		return recurPeriod;
	}
}