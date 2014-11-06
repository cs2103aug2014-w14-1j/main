package speed.UI;

//@author A0111660W
import java.util.ArrayList;

import javafx.collections.ListChangeListener;

import speed.Task.Task;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;

public class UI extends FlowPane {
	private ArrayList<UIObserver> uiObserver;
	private UIKeyEventHandler uiKeyEventHandler = new UIKeyEventHandler(this);
	private ObservableList<Task> dataToDisplay;
	protected ArrayList<Task> displayTasks = new ArrayList<Task>();
	protected Task taskUserSelected = null;

	// topmost Container
	protected VBox taskView;

	// component 1 of taskView
	private HBox split;

	// view 1 of split
	private VBox taskTableView;
	protected TableView<Task> taskTable;
	// End view 1 of split

	protected UITaskDetailsView taskDetailsView;

	// rest of components of taskView
	protected TextField userCommands;
	protected ArrayList<String> userCommandsHistory;
	protected int userCommandsHistoryCounter;

	// Dimensions
	private static final double WIDTH_OF_PROGRAM = 950;
	private static final double WIDTH_OF_TASKVIEW_INSET_SPACING = 20;

	private static final double WIDTH_OF_TASKTABLE = 590;
	private static final double HEIGHT_OF_TASKTABLE = 500;
	private static final double WIDTH_OF_TASKLBLCOL = 40;
	private static final double WIDTH_OF_TASKNAMECOL = 300;
	private static final double WIDTH_OF_TASKSTARTENDDATECOL = 200;

	private static final double HEIGHT_OF_USERCOMMANDS = 10;
	private static final double WIDTH_OF_USERCOMMANDS = WIDTH_OF_PROGRAM
			- WIDTH_OF_TASKVIEW_INSET_SPACING - WIDTH_OF_TASKVIEW_INSET_SPACING
			- WIDTH_OF_TASKLBLCOL;

	// Program values
	private static final String PROGRAM_NAME = "SPEED";
	private static final String EMPTY_STRING = "";
	private static final String ADDBLANKNEXTLINE = "\n";

	// CSS
	private static final String CSS_MAIN_TASKVIEW = "myStyles.css";
	private static final String CSS_USERCOMMANDS = "inputText";
	private static final String CSS_TASKIDCOL = "task-id-column";

	public UI() {
		initTaskView(); // topmost Container(root)
		initUserCommandsHistory();

		initSplit(); // Split: HBox containing 2 views

		// taskTableView: contains taskTable. View 1 of
		// Split.*************************
		initTaskTable();
		initTaskTableView();

		// View 2 of split
		initTaskDetailsView();

		// adding Views to Split
		doSplitAddViews();

		initUserCommands();

		// Add Views to taskView(main view)
		doTaskViewAddViews();

		doDefaultUserCommands();
		initObservers();
	}

	private void initTaskDetailsView() {
		this.taskDetailsView = new UITaskDetailsView(this);
	}

	private void initTaskView() {
		taskView = new VBox();
		taskView.setPrefWidth(WIDTH_OF_PROGRAM);
		taskView.setPadding(new Insets(WIDTH_OF_TASKVIEW_INSET_SPACING,
				WIDTH_OF_TASKVIEW_INSET_SPACING,
				WIDTH_OF_TASKVIEW_INSET_SPACING,
				WIDTH_OF_TASKVIEW_INSET_SPACING));
		taskView.setSpacing(WIDTH_OF_TASKVIEW_INSET_SPACING);
	}

	private void initUserCommandsHistory() {
		userCommandsHistory = new ArrayList<String>();
	}

	private void initSplit() {
		split = new HBox();
		split.setSpacing(WIDTH_OF_TASKVIEW_INSET_SPACING);
	}

	private void initTaskTable() {
		taskTable = new TableView<Task>();
		taskTable.setPrefWidth(WIDTH_OF_TASKTABLE);
		taskTable.setPrefHeight(HEIGHT_OF_TASKTABLE);
		doBuildTaskTableCols(dataToDisplay);

		taskTable.getSelectionModel().selectedItemProperty()
				.addListener(new ChangeListener<Task>() {
					@Override
					public void changed(ObservableValue<? extends Task> arg0,
							Task arg1, Task arg2) {
						try {
							if (taskTable.getSelectionModel().getSelectedItem() != null) {
								taskUserSelected = taskTable
										.getSelectionModel().getSelectedItem();
								bindTaskDetailsView(taskUserSelected);

							}
						} catch (Exception e) {
							System.out.println("UI table changed Listener: "
									+ e);
						}
					}

				});

		taskTable.setOnKeyPressed(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent ke) {
				uiKeyEventHandler.doRequestedTaskTableKeyEvent(ke);
			}
		});
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void doBuildTaskTableCols(ObservableList<Task> data) {

		TableColumn<Task, String> taskLblCol = new TableColumn<Task, String>(
				"ID");
		taskLblCol.setMinWidth(WIDTH_OF_TASKLBLCOL);
		taskLblCol.setResizable(false);
		taskLblCol.setSortable(false);
		taskLblCol.getStyleClass().add(CSS_TASKIDCOL);

		taskLblCol
				.setCellValueFactory(new Callback<CellDataFeatures<Task, String>, ObservableValue<String>>() {
					@Override
					public ObservableValue<String> call(
							CellDataFeatures<Task, String> p) {
						return new SimpleStringProperty((p.getValue()
								.getDisplayId()));
					}
				});

		taskLblCol
				.setCellFactory(new Callback<TableColumn<Task, String>, TableCell<Task, String>>() {
					@Override
					public TableCell<Task, String> call(
							TableColumn<Task, String> arg0) {
						return new TaskLblColTableCell();
					}
				});

		TableColumn<Task, String> taskNameCol = new TableColumn<Task, String>(
				"Task Name");
		taskNameCol.setPrefWidth(WIDTH_OF_TASKNAMECOL);
		taskNameCol.setResizable(false);
		taskNameCol
				.setCellValueFactory(new Callback<CellDataFeatures<Task, String>, ObservableValue<String>>() {
					@Override
					public ObservableValue<String> call(
							CellDataFeatures<Task, String> p) {
						String taskName = p.getValue().getTaskName();
						String taskTags = ADDBLANKNEXTLINE + ADDBLANKNEXTLINE
								+ p.getValue().getTagsAsString();

						return new SimpleStringProperty((taskName + taskTags));
					}
				});

		TableColumn<Task, String> taskStartEndDateCol = new TableColumn<Task, String>(
				"Task Date");
		taskStartEndDateCol.setResizable(false);
		taskStartEndDateCol.setPrefWidth(WIDTH_OF_TASKSTARTENDDATECOL);
		taskStartEndDateCol
				.setCellValueFactory(new Callback<CellDataFeatures<Task, String>, ObservableValue<String>>() {
					@Override
					public ObservableValue<String> call(
							CellDataFeatures<Task, String> p) {

						return new SimpleStringProperty((p.getValue()
								.getDateAsString()));
					}
				});

		final TableColumn[] columns = { taskLblCol, taskNameCol,
				taskStartEndDateCol };

		taskTable.getColumns().setAll(columns);

		taskTable.getColumns().addListener(new ListChangeListener() {
			public boolean suspended;

			@Override
			public void onChanged(Change change) {
				change.next();
				if (change.wasReplaced() && !suspended) {
					this.suspended = true;
					taskTable.getColumns().setAll(columns);
					this.suspended = false;
				}
			}
		});

	}

	private void initTaskTableView() {
		taskTableView = new VBox();
		taskTableView.getChildren().add(taskTable);
	}

	private void doSplitAddViews() {
		split.getChildren().addAll(taskTableView, this.taskDetailsView);
	}

	private void initUserCommands() {
		userCommands = new TextField(EMPTY_STRING);
		userCommands.setId(CSS_USERCOMMANDS);
		userCommands.setMaxWidth(WIDTH_OF_USERCOMMANDS);
		userCommands.setTranslateX(WIDTH_OF_TASKLBLCOL);// to align the
														// userCommands and
														// taskTable.

		userCommands.setPrefHeight(HEIGHT_OF_USERCOMMANDS);

		userCommands.setOnKeyPressed(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent ke) {
				uiKeyEventHandler.doRequestedUserCommandsKeyEvent(ke);
			}
		});
	}

	private void doTaskViewAddViews() {
		taskView.getChildren().addAll(split, userCommands);
	}

	protected void doDefaultUserCommands() {
		userCommands.setText(EMPTY_STRING);
		userCommands.requestFocus();
	}

	// These methods allow the controller to observe the UI for updates to user
	// input
	private void initObservers() {
		uiObserver = new ArrayList<UIObserver>();
	}

	public void addUIObserver(UIObserver observer) {
		uiObserver.add(observer);
	}

	protected void notifyObservers() {
		for (UIObserver observer : uiObserver) {
			observer.update();
		}
	}

	private void bindTaskDetailsView(Task task) {
		this.taskDetailsView.bindTaskDetails(task);
	}

	// ******************************************************************

	// Functions that deal with displaying notifications to user and retrieving
	// user notifications
	public String getUserInput() {
		return userCommands.getText();
	}

	public void setNotificationToUser(String msg) {
		this.taskDetailsView.setNotificationToUser(msg);
	}

	// END - Functions that deal with displaying notifications to user and
	// retrieving
	// user notifications******************

	public void displayTasks(ArrayList<Task> taskAL) {
		this.displayTasks.removeAll(displayTasks);
		this.displayTasks = taskAL;
		if (dataToDisplay != null) {
			dataToDisplay.removeAll(dataToDisplay);
		}
		dataToDisplay = FXCollections.observableArrayList(displayTasks);
		taskTable.setItems(dataToDisplay);

		if (!dataToDisplay.isEmpty()) {
			this.taskUserSelected = dataToDisplay.get(0);
			bindTaskDetailsView(taskUserSelected);

		} else {
			this.taskDetailsView.blankTaskDetails();
		}

		doDefaultUserCommands();

	}

	// Allows Controller to pass stage for this UI to display Scene.
	public void showStage(Stage primaryStage) {
		Scene scene = new Scene(this.taskView);
		scene.getStylesheets().add(CSS_MAIN_TASKVIEW);
		primaryStage.setTitle(PROGRAM_NAME);
		primaryStage.setScene(scene);
		primaryStage.show();
		primaryStage.setResizable(false);
	}

	// *****************************************************************

}

// @author A0111660W
class TaskLblColTableCell extends TableCell<Task, String> {
	// CSS
	private static final String CSS_FLOATINGTASKROW = "floatingTaskRow";
	private static final String CSS_OVERDUETASKROW = "overdueTaskRow";
	private static final String CSS_NORMALTASKROW = "normalTaskRow";

	// Errors
	private static final String ERROR_NO_OTHER_TASKTYPE = "There are no other task types!";

	// Program Variables
	private static final String EMPTY_STRING = "";
	private static final String INVALID = null;
	private static final String FLOATING_TASK = "F";
	private static final String OVERDUE_TASK = "O";
	private static final String NORMAL_TASK = "T";

	@Override
	protected void updateItem(final String item, final boolean empty) {
		super.updateItem(item, empty);

		setText(empty ? EMPTY_STRING : item);
		getStyleClass().removeAll(CSS_FLOATINGTASKROW, CSS_OVERDUETASKROW,
				CSS_NORMALTASKROW);
		updateStyles(empty ? INVALID : item);
	}

	private void updateStyles(String item) {
		if (item == INVALID) {
			return;
		}

		if (isFloating(item)) {
			getStyleClass().addAll(CSS_FLOATINGTASKROW);
		} else if (isOverdue(item)) {
			getStyleClass().add(CSS_OVERDUETASKROW);
		} else if (isNormalTask(item)) {
			getStyleClass().add(CSS_NORMALTASKROW);
		} else {
			throw new Error(ERROR_NO_OTHER_TASKTYPE);
		}
	}

	private boolean isFloating(String itemValue) {
		return itemValue.contains(FLOATING_TASK);
	}

	private boolean isOverdue(String itemValue) {
		return itemValue.contains(OVERDUE_TASK);
	}

	private boolean isNormalTask(String itemValue) {
		return itemValue.contains(NORMAL_TASK);
	}
}
