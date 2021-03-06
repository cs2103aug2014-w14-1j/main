//@author A0111660W
package speed.view;

import java.util.ArrayList;
import speed.task.Task;
import javafx.scene.control.TableView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

/* Description : 
 * This class mainly reacts to the key events on two components of the 
 * UI: userCommands:TextField and taskTable:Table and reacts accordingly.
 * It is responsible for Creation and reaction of all Hot Keys of the UI.
 */

class UIKeyEventHandler {
	private UI ui = null;

	// Class values
	private static final String PREVIOUS_USER_COMMAND = "previous";
	private static final String NEXT_USER_COMMAND = "next";
	private static final String REGEX_NUMBERS_ONLY = "^[0-9]*$";
	private static final String EMPTY_STRING = "";

	// Errors
	private static final String ERROR_USERCOMMAND_HOTKEY = "Unrecognized User Command Hot Key";
	private static final String ERROR_TASKTABLE_HOTKEY = "Unrecognized Task Table Hot Key";
	private static final String ERROR_RETRIEVECOMMANDHISTORY = "There should not be any other commands.";
	
	enum HotKeys {
		USERCOMMAND_ENTER, USERCOMMAND_PREVIOUSCOMMAND, USERCOMMAND_NEXTCOMMAND, 
		USERCOMMAND_UNDO, USERCOMMAND_REDO, USERCOMMAND_INVALID, TASKTABLE_DELETE, 
		TASKTABLE_INVALID, TASKTABLE_EDIT, TASKTABLE_UNDO, TASKTABLE_REDO
	};

	protected UIKeyEventHandler(UI ui) {
		this.ui = ui;
	}
	
	//****************************** Methods Accessible to UI PACKAGE***************************
	protected void doRequestedUserCommandsKeyEvent(KeyEvent ke) {
		HotKeys hotKey = determineHotKeyUserCommands(ke);

		switch (hotKey) {
		case USERCOMMAND_ENTER:
			doUserCommand();
			break;
		case USERCOMMAND_PREVIOUSCOMMAND:
			doRetrieveCommandHistory(PREVIOUS_USER_COMMAND);
			break;
		case USERCOMMAND_NEXTCOMMAND:
			doRetrieveCommandHistory(NEXT_USER_COMMAND);
			break;
		case USERCOMMAND_UNDO:
			doUndo();
			break;
		case USERCOMMAND_REDO:
			doRedo();
			break;
		case USERCOMMAND_INVALID:
			break;
		default:
			throw new Error(ERROR_USERCOMMAND_HOTKEY);
		}
	}
	
	protected void doRequestedTaskTableKeyEvent(KeyEvent ke) {
		HotKeys hotKey = determineHotKeyTaskTable(ke);

		switch (hotKey) {
		case TASKTABLE_DELETE:
			doTaskTableDelete();
			break;
		case TASKTABLE_EDIT:
			doDisplayQuickEditToUserCommand();
			break;
		case TASKTABLE_UNDO:
			doUndo();
			break;
		case TASKTABLE_REDO:
			doRedo();
			break;
		case TASKTABLE_INVALID:
			break;
		default:
			throw new Error(ERROR_TASKTABLE_HOTKEY);

		}
	}
	//****************************** END - Methods Accessible to UI PACKAGE***************************
	
	private HotKeys determineHotKeyUserCommands(KeyEvent keyEvent) {

		assert (keyEvent != null);
		KeyCode keyCode = keyEvent.getCode();

		if (keyCode.equals(KeyCode.ENTER)) {
			return HotKeys.USERCOMMAND_ENTER;
		} else if (keyCode.equals(KeyCode.UP)
				&& !ui.userCommandsHistory.isEmpty()) {
			return HotKeys.USERCOMMAND_PREVIOUSCOMMAND;
		} else if (keyCode.equals(KeyCode.DOWN)
				&& !ui.userCommandsHistory.isEmpty()) {
			return HotKeys.USERCOMMAND_NEXTCOMMAND;
		} else if (keyEvent.isControlDown()) {
			if (keyCode.equals(KeyCode.Z)) {
				return HotKeys.USERCOMMAND_UNDO;
			} else if (keyCode.equals(KeyCode.Y)) {
				return HotKeys.USERCOMMAND_REDO;
			} else {
				return HotKeys.USERCOMMAND_INVALID;
			}
		} else {
			return HotKeys.USERCOMMAND_INVALID;
		}
	}
	

	private HotKeys determineHotKeyTaskTable(KeyEvent keyEvent) {

		assert (keyEvent != null);
		KeyCode keyCode = keyEvent.getCode();

		if (keyCode.equals(KeyCode.DELETE)) {
			return HotKeys.TASKTABLE_DELETE;
		} else if (keyEvent.isControlDown()) {
			if (keyCode.equals(KeyCode.E)) {
				return HotKeys.TASKTABLE_EDIT;
			} else if (keyCode.equals(KeyCode.Z)) {
				return HotKeys.TASKTABLE_UNDO;
			} else if (keyCode.equals(KeyCode.Y)) {
				return HotKeys.TASKTABLE_REDO;
			} else {
				return HotKeys.TASKTABLE_INVALID;
			}
		} else {
			return HotKeys.TASKTABLE_INVALID;
		}
	}

	private void doUserCommand() {
		String userInput = ui.getUserCommands();

		if (userInput.equals(EMPTY_STRING)) {// ignore invalid command
			return;
		}

		ui.userCommandsHistory.add(userInput);
		ui.userCommandsHistoryCounter = sizeToIndex(ui.userCommandsHistory
				.size());

		if (userInput.matches(REGEX_NUMBERS_ONLY)) {
			int taskNo = Integer.parseInt(userInput);
			int index = taskNoToIndex(taskNo);

			if (isValidIndex(index)) {
				TableView<Task> uiTaskTable = this.ui.getUITaskTableView().getTaskTable();
				uiTaskTable.requestFocus();
				uiTaskTable.getSelectionModel().select(index);
				uiTaskTable.getFocusModel().focus(index);
			}
			ui.setUserCommands(EMPTY_STRING);
		} else {
			ui.notifyObservers();
			ui.doDefaultUserCommands();
		}

	}

	private void doRetrieveCommandHistory(String command) {// updates the
															// userCommands
															// TextField for
															// previously
															// entered commands.
		if (command.equals(PREVIOUS_USER_COMMAND)) {
			if (isInvalidPreviousCommandExisted()) {
				// Ignore
			} else {
				ui.userCommands.setText(ui.userCommandsHistory
						.get(ui.userCommandsHistoryCounter));
				ui.userCommandsHistoryCounter--;
			}
		} else if (command.equals(NEXT_USER_COMMAND)) {
			if (isInvalidNextCommandExisted()) {
				ui.setUserCommands(EMPTY_STRING);
			} else {
				ui.userCommandsHistoryCounter++;
				ui.setUserCommands(ui.userCommandsHistory
						.get(ui.userCommandsHistoryCounter));
			}

		} else {
			throw new Error(ERROR_RETRIEVECOMMANDHISTORY);
		}
	}
	
	private void doUndo() {
		ui.setUserCommands("undo");
		ui.notifyObservers();
	}

	private void doRedo() {
		ui.setUserCommands("redo");
		ui.notifyObservers();
	}
	
	private void doTaskTableDelete() {
		Task taskUserSelected = this.ui.getTaskUserSelected();
		if (taskUserSelected != null) {
			this.ui.setUserCommands("delete "
					+ taskUserSelected.getDisplayId());
			ui.notifyObservers();
		}
	}

	private void doDisplayQuickEditToUserCommand() {
		ArrayList<Task> tasksList = this.ui.getUITaskTableView().getTasksListForDisplay();
		Task taskUserSelected = this.ui.getTaskUserSelected();
				
		if (!tasksList.isEmpty() || tasksList != null) {
			String textToDisplay = "edit " + taskUserSelected.getDisplayId()
					+ " " + taskUserSelected.getTaskName() + " ";
			ui.doDefaultUserCommands();
			this.ui.setUserCommands(textToDisplay);
			ui.userCommands.positionCaret(textToDisplay.length());
		}
	}

	
	private int sizeToIndex(int size) {
		if (size <= 0) {
			//ignore
		}
		return size - 1;
	}

	private int taskNoToIndex(int taskNo) {
		return taskNo - 1;
	}

	private boolean isValidIndex(int index) {
		ArrayList<Task> tasksList = this.ui.getUITaskTableView().getTasksListForDisplay();
		return index >= 0 && tasksList != null
				&& !tasksList.isEmpty() && index < tasksList.size();
	}

	private boolean isInvalidPreviousCommandExisted() {
		return ui.userCommandsHistoryCounter < 0;
	}

	private boolean isInvalidNextCommandExisted() {
		return ui.userCommandsHistoryCounter >= ui.userCommandsHistory.size() - 1;
	}
}
