import java.util.ArrayList;
import java.util.Calendar;


public class SearchHandler {
	private Storage storage_;
	Command lastSearchCommand_;
	
	public SearchHandler(Storage storage) {
		storage_ = storage;
		lastSearchCommand_ = null;
	}
	
	public ArrayList<Task> proceedCommand(Command command) {
		if (command.getCommandType() == Command.COMMAND_TYPE.SEARCH) {
			return search(command);
		} else if (command.getCommandType() == Command.COMMAND_TYPE.LIST) {
			return list(command);
		} else {
			return new ArrayList<Task>(); 
		}
	}

	public ArrayList<Task> search(Command command) {
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
	
	public ArrayList<Task> list(Command command) {
		lastSearchCommand_ = command;
		
		ArrayList<String> keywords = new ArrayList<String>();
		ArrayList<String> tags = new ArrayList<String>();
		Calendar start_date = command.getSearchStartDate();
		Calendar end_date = command.getSearchEndDate();

		return storage_.search(keywords, tags, start_date, end_date);
	}		
	
	public ArrayList<Task> repeatLastSearch() {
		if (lastSearchCommand_ == null) {
			return viewDefault();
		} else {
			return proceedCommand(lastSearchCommand_);
		}
	}
	
	public ArrayList<Task> viewDefault() {
		
		ArrayList<Task> searchResults = new ArrayList<Task>();
		searchResults.addAll(storage_.defaultView());
		
		return searchResults;
	}
}
