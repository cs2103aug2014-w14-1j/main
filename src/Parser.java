import java.lang.String;
import java.util.Calendar;
import java.util.LinkedList;

public class Parser {

	private String command;

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
}