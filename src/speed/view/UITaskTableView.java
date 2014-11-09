//@author A0111660W
package speed.view;

import java.util.ArrayList;
import speed.task.Task;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.util.Callback;

class UITaskTableView extends VBox {
	private UI ui;
	private TableView<Task> taskTable;
	private ObservableList<Task> dataToDisplay;
	private ArrayList<Task> tasksListForDisplay = new ArrayList<Task>();

	// Dimensions
	protected static final double WIDTH_OF_TASKTABLE = 590;
	protected static final double HEIGHT_OF_TASKTABLE = 500;
	protected static final double WIDTH_OF_TASKLBLCOL = 40;
	protected static final double WIDTH_OF_TASKNAMECOL = 300;
	protected static final double WIDTH_OF_TASKSTARTENDDATECOL = 200;

	// CSS
	private static final String CSS_TASKIDCOL = "task-id-column";

	// Class Values
	private static final String ADDBLANKNEXTLINE = "\n";

	protected UITaskTableView(UI ui) {
		this.ui = ui;
		initTaskTable();
		setContent();
	}
	
	private void setContent(){
		this.getChildren().addAll(this.taskTable);
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
							Task taskUserSelected = taskTable
									.getSelectionModel().getSelectedItem();
							if (taskUserSelected != null) {
								doSetTaskUserSelected(taskUserSelected);
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
				doRequestUIKeyEventHandler(ke);
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
							CellDataFeatures<Task, String> row) {
						return new SimpleStringProperty((row.getValue()
								.getDisplayId()));
					}
				});

		taskLblCol
				.setCellFactory(new Callback<TableColumn<Task, String>, TableCell<Task, String>>() {
					@Override
					public TableCell<Task, String> call(
							TableColumn<Task, String> row) {
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
							CellDataFeatures<Task, String> row) {
						String taskName = row.getValue().getTaskName();
						String taskTags = ADDBLANKNEXTLINE + ADDBLANKNEXTLINE
								+ row.getValue().getTagsAsString();

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
							CellDataFeatures<Task, String> row) {

						return new SimpleStringProperty((row.getValue()
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

	private void doSetTaskUserSelected(Task task) {
		assert (task != null);
		this.ui.setTaskUserSelected(task);
		this.ui.getUITaskDetailsView().bindTaskDetails(task);
	}

	private void doRequestUIKeyEventHandler(KeyEvent ke) {
		this.ui.getUIKeyEventHandler().doRequestedTaskTableKeyEvent(ke);
	}

	//******************** ACCESSORS FOR UI PACKAGE ***************************************
	protected void displayTasks(ArrayList<Task> taskAL) {
		this.tasksListForDisplay.removeAll(tasksListForDisplay);
		this.tasksListForDisplay.addAll(taskAL);
		if (dataToDisplay != null) {
			dataToDisplay.removeAll(dataToDisplay);
		}
		dataToDisplay = FXCollections.observableArrayList(tasksListForDisplay);
		taskTable.setItems(dataToDisplay);

		if (dataToDisplay.isEmpty()) {
			this.ui.getUITaskDetailsView().blankTaskDetails();
		}

	}
	
	protected ArrayList<Task> getTasksListForDisplay(){
		return this.tasksListForDisplay;
	}
	
	protected TableView<Task> getTaskTable(){
		return this.taskTable;
	}
	//******************** END - ACCESSORS FOR UI PACKAGE ***************************************
	
	// @author A0111660W
	class TaskLblColTableCell extends TableCell<Task, String> {
		
		// CSS
		private static final String CSS_REMINDERTASKROW = "reminderTaskRow";
		private static final String CSS_OVERDUETASKROW = "overdueTaskRow";
		private static final String CSS_NORMALTASKROW = "normalTaskRow";
		private static final String CSS_COMPLETEDTASKROW = "completedTaskRow";
				
		// Program Variables
		private static final String EMPTY_STRING = "";
		private final String INVALID = null;
		private static final String FLOATING_TASK = "R";
		private static final String OVERDUE_TASK = "O";
		private static final String NORMAL_TASK = "T";
		private static final String COMPLETED_TASK = "C";

		@Override
		protected void updateItem(final String item, final boolean empty) {
			super.updateItem(item, empty);

			setText(empty ? EMPTY_STRING : item);
			getStyleClass().removeAll(CSS_REMINDERTASKROW, CSS_OVERDUETASKROW,
					CSS_NORMALTASKROW);
			updateStyles(empty ? INVALID : item);
		}

		private void updateStyles(String item) {
			if (item == INVALID) {
				return;
			}

			if (isFloating(item)) {
				getStyleClass().addAll(CSS_REMINDERTASKROW);
			} else if (isOverdue(item)) {
				getStyleClass().add(CSS_OVERDUETASKROW);
			} else if (isNormalTask(item)) {
				getStyleClass().add(CSS_NORMALTASKROW);
			} else if (isCompletedTask(item)){
				getStyleClass().add(CSS_COMPLETEDTASKROW);
			}
			else {
				//ignore
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
		
		private boolean isCompletedTask(String itemValue){
			return itemValue.contains(COMPLETED_TASK);
		}
	}
}
