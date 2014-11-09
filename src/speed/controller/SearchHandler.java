//@author A0112059N
package speed.controller;

import speed.parser.Command;
import speed.storage.Storage;
import speed.task.Task;

import java.util.ArrayList;
import java.util.Calendar;

public class SearchHandler {
	private Storage storage_;
	Command lastSearchCommand_;
	
	public SearchHandler(Storage storage) {
		storage_ = storage;
		lastSearchCommand_ = null;
	}
	
	public ArrayList<Task> proceedCommand(Command command) throws Exception {
		if (command.getCommandType() == Command.COMMAND_TYPE.SEARCH) {
			return search(command);
		} else if (command.getCommandType() == Command.COMMAND_TYPE.LIST) {
			return list(command);
		} else {
			return new ArrayList<Task>(); 
		}
	}

	public ArrayList<Task> search(Command command) throws Exception {
		lastSearchCommand_ = command;
		
		ArrayList<String> keywords = command.getSearchKeywords();
		ArrayList<String> tags = new ArrayList<String>();
		if (command.getSearchTags()!=null) {
			for (String tag : command.getSearchTags()) {
				tags.add(tag);
			}
		}
		Calendar start_date = command.getSearchStartDate();
		Calendar end_date = command.getSearchEndDate();

		return storage_.search(keywords, tags, start_date, end_date);		
	}
	
	public ArrayList<Task> list(Command command) throws Exception {
		
		if (command.getSearchType() == null) { 
		
			lastSearchCommand_ = command;
		 
			ArrayList<String> keywords = new ArrayList<String>();
			ArrayList<String> tags = new ArrayList<String>();
			Calendar start_date = command.getSearchStartDate();
			Calendar end_date = command.getSearchEndDate();

			return storage_.search(keywords, tags, start_date, end_date);
		
		} else {
			
			boolean overdue = false;
			boolean reminder = false;
			boolean complete = false;
			
			ArrayList<String> types = command.getSearchType();
			
			for (String type: types) {
				if (type.equalsIgnoreCase("overdue")) {
					overdue = true;
				} else if (type.equalsIgnoreCase("floating")) {
					reminder = true;
				} else if (type.equalsIgnoreCase("complete")) {
					complete = true;
				}
				
				System.out.println(type);
			}
			
			ArrayList<Task> result = new ArrayList<Task> ();
			
			if (complete) {
				result.addAll(storage_.getCompletedTasksList());
			}
			
			if (reminder) {
				result.addAll(storage_.getFloatingTasksList());
			}
			
			if (overdue) {
				result.addAll(storage_.getOverdueTasksList());
			}
			
			return result;
			
		}
	}		
	
	public ArrayList<Task> repeatLastSearch() throws Exception {
		if (lastSearchCommand_ == null) {
			return viewDefault();
		} else {
			return proceedCommand(lastSearchCommand_);
		}
	}
	
	public ArrayList<Task> viewDefault() throws Exception {
		
		ArrayList<Task> searchResults = new ArrayList<Task>();
		searchResults.addAll(storage_.search(null,null,null,null));
		
		return searchResults;
	}
}
