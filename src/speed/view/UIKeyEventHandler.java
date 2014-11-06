//@author A0111660W
package speed.view;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

/* Description : 
 * This class mainly reacts to the key events on two components of the 
 * UI: userCommands TextField and taskTable Table and reacts accordingly.
 * It is responsible for Creation and reaction of all Hot Keys of the UI.
 */

public class UIKeyEventHandler {
	private UI ui = null;

	// Class variables
	private static final String PREVIOUS_USER_COMMAND = "previous";
	private static final String NEXT_USER_COMMAND = "next";
	private static final String REGEX_NUMBERS_ONLY = "^[0-9]*$";
	private static final String EMPTY_STRING = "";

	// Errors
	private static final String ERROR_USERCOMMAND_HOTKEY = "Unrecognized User Command Hot Key";
	private static final String ERROR_RETRIEVECOMMANDHISTORY = "There should not be any other commands.";
	private static final String ERROR_SIZEOUTOFBOUNDS = "Size should not be out of bounds";
	private static final String ERROR_TASKTABLE_HOTKEY = "Unrecognized Task Table Hot Key";

	enum HotKeys {
		USERCOMMAND_ENTER, USERCOMMAND_PREVIOUSCOMMAND, USERCOMMAND_NEXTCOMMAND, USERCOMMAND_UNDO, USERCOMMAND_REDO, USERCOMMAND_INVALID, TASKTABLE_DELETE, TASKTABLE_INVALID, TASKTABLE_EDIT, TASKTABLE_UNDO, TASKTABLE_REDO
	};

	public UIKeyEventHandler(UI ui) {
		this.ui = ui;

	}

	// Functions from userCommandsKeyListener
	public void doRequestedUserCommandsKeyEvent(KeyEvent ke) {
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

	private void doUserCommand() {
		String userCommand = ui.userCommands.getText();

		if (userCommand.equals(EMPTY_STRING)) {// ignore as it is an invalid
												// command
			return;
		}

		ui.userCommandsHistory.add(userCommand);
		ui.userCommandsHistoryCounter = sizeToIndex(ui.userCommandsHistory
				.size());

		if (userCommand.matches(REGEX_NUMBERS_ONLY)) {
			int taskNo = Integer.parseInt(userCommand);
			int index = taskNoToIndex(taskNo);

			if (isValidIndex(index)) {
				ui.taskTable.requestFocus();
				ui.taskTable.getSelectionModel().select(index);
				ui.taskTable.getFocusModel().focus(index);
			}
			ui.userCommands.setText(EMPTY_STRING);
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
				ui.userCommands.setText(EMPTY_STRING);
			} else {
				ui.userCommandsHistoryCounter++;
				ui.userCommands.setText(ui.userCommandsHistory
						.get(ui.userCommandsHistoryCounter));
			}

		} else {
			throw new Error(ERROR_RETRIEVECOMMANDHISTORY);
		}
	}

	private void doUndo() {
		ui.userCommands.setText("undo");
		ui.notifyObservers();
	}

	private void doRedo() {
		ui.userCommands.setText("redo");
		ui.notifyObservers();
	}

	private int sizeToIndex(int size) {
		if (size <= 0) {
			throw new Error(ERROR_SIZEOUTOFBOUNDS);
		}
		return size - 1;
	}

	private int taskNoToIndex(int taskNo) {
		return taskNo - 1;
	}

	private boolean isValidIndex(int index) {
		return index >= 0 && ui.displayTasks != null
				&& !ui.displayTasks.isEmpty() && index < ui.displayTasks.size();
	}

	private boolean isInvalidPreviousCommandExisted() {
		return ui.userCommandsHistoryCounter < 0;
	}

	private boolean isInvalidNextCommandExisted() {
		return ui.userCommandsHistoryCounter >= ui.userCommandsHistory.size() - 1;
	}

	// END - Functions from
	// userCommandsKeyListener*********************************

	// Functions from ui.taskTable KeyListener
	public void doRequestedTaskTableKeyEvent(KeyEvent ke) {
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

	private void doTaskTableDelete() {
		if (ui.taskUserSelected == null) {
			return;
		}
		ui.userCommands.setText("delete " + ui.taskUserSelected.getDisplayId());
		ui.notifyObservers();
	}

	private void doDisplayQuickEditToUserCommand() {
		String textToDisplay = "edit " + ui.taskIDtf.getText() + " "
				+ ui.taskNameta.getText() + " ";
		ui.doDefaultUserCommands();
		ui.userCommands.setText(textToDisplay);
		ui.userCommands.positionCaret(textToDisplay.length());
	}

	// END - Functions from taskTable
	// KeyListener***************************************

}
