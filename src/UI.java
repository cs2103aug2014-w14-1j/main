import java.util.ArrayList;

import javafx.collections.ListChangeListener;
import org.controlsfx.control.NotificationPane;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;

public class UI extends FlowPane {
	private ArrayList<UIObserver> uiObserver;
	// topmost Container
	private VBox taskView;

	// component 1 of taskView
	private HBox split;

	// view 1 of split
	private VBox taskTableView;
	private TableView<Task> taskTable;

	// view 2 of split
	private VBox taskDetailsView;

	private ObservableList<Task> dataToDisplay;
	private ArrayList<Task> displayTasks = new ArrayList<Task>();

	private final Label taskIDLbl = new Label("Task ID: ");
	private final TextField taskIDtf = new TextField();
	private final Label taskNameLbl = new Label("Task Name: ");
	private final TextArea taskNameta = new TextArea();
	private final Label taskStartDtesLbl = new Label("Task Dates: ");
	private final TextArea taskStartDtesta = new TextArea();
	private final Label taskTagsLbl = new Label("Task Tags: ");
	private final TextArea taskTagsta = new TextArea();
	private Task taskUserSelected = null;
	//

	// rest of components of taskView
	private TextField userCommands;
	private ArrayList<String> previousUserCommands;
	private int previousUserCommandsCounter;
	private NotificationPane notificationPane;

	// Dimensions
	private static final double WIDTH_OF_PROGRAM = 952;
	private static final double WIDTH_OF_SPLIT2 = 350;
	private static final double HEIGHT_OF_USERCOMMANDS = 10;
	private static final double SPACING = 20;

	public UI() {
		previousUserCommands = new ArrayList<String>();
		taskView = new VBox();
		taskView.setPrefWidth(WIDTH_OF_PROGRAM);
		taskView.setPadding(new Insets(SPACING, SPACING, SPACING, SPACING));
		taskView.setSpacing(SPACING);
		initNotificationPane();
		// Split: HBox containing 2 views
		split = new HBox();
		split.setSpacing(SPACING);

		// taskTableView: contains taskTable, View 1 of
		// split.*************************
		taskTableView = new VBox();

		initTaskTable();
		taskTableView.getChildren().add(taskTable);

		// View 2 of split
		initTaskDetailsView();

		// adding split pane
		split.getChildren().addAll(taskTableView, taskDetailsView);

		// userCommands TextField.
		userCommands = new TextField("");
		userCommands.setId("inputText");
		userCommands.setPrefWidth(WIDTH_OF_PROGRAM - SPACING - SPACING);
		userCommands.setPrefHeight(HEIGHT_OF_USERCOMMANDS);

		// taskView
		taskView.getChildren().addAll(split, userCommands);

		initUserCommands();
		initObservers();

		// to toggle action on "ENTER"
		userCommands.setOnKeyPressed(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent ke) {
				if (ke.getCode().equals(KeyCode.ENTER)) {
					previousUserCommands.add(userCommands.getText());
					previousUserCommandsCounter = previousUserCommands.size() - 1;
					notifyObservers();
					initUserCommands();
				} else if (ke.getCode().equals(KeyCode.UP) && !previousUserCommands.isEmpty()) {
					displayPreviousMessage("UP");

				} else if (ke.getCode().equals(KeyCode.DOWN) && !previousUserCommands.isEmpty()) {
					displayPreviousMessage("DOWN");
				}
				else{
					//ignore
				}
			}

		});

	}

	// to Build the taskTable for taskTableView.
	private void initTaskTable() {
		taskTable = new TableView<Task>();
		taskTable.setPrefWidth(580);
		taskTable.setPrefHeight(500);
		buildColumns(dataToDisplay);

		taskTable.getSelectionModel().selectedItemProperty()
				.addListener(new ChangeListener<Task>() {
					@Override
					public void changed(ObservableValue<? extends Task> arg0,
							Task arg1, Task arg2) {
						try {
							if (taskTable.getSelectionModel().getSelectedItem() != null) {
								taskUserSelected = taskTable
										.getSelectionModel().getSelectedItem();
								bindTaskDetails(taskUserSelected);
							}
						} catch (Exception e) {
							System.out.println("UI" + e);
							// ignore
						}
					}

				});

		taskTable.setOnKeyPressed(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent ke) {
				if (ke.getCode().equals(KeyCode.BACK_SPACE)
						|| ke.getCode().equals(KeyCode.DELETE)) {
					userCommands.setText("delete "
							+ taskUserSelected.getDisplayId());
					notifyObservers();
					initUserCommands();
				}
			}

		});
	}

	@SuppressWarnings("unchecked")
	private void buildColumns(ObservableList<Task> data) {

		TableColumn<Task, String> taskLblCol = new TableColumn<Task, String>(
				"ID");
		taskLblCol.setPrefWidth(40);
		taskLblCol.setResizable(false);
		taskLblCol.setSortable(false);
		taskLblCol.getStyleClass().add("task-id-column");
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
						return new BackgroundTableCell();
					}
				});

		TableColumn<Task, String> taskNameCol = new TableColumn<Task, String>(
				"Task Name");
		taskNameCol.setPrefWidth(300);
		taskNameCol.setResizable(false);
		taskNameCol
				.setCellValueFactory(new Callback<CellDataFeatures<Task, String>, ObservableValue<String>>() {
					@Override
					public ObservableValue<String> call(
							CellDataFeatures<Task, String> p) {
						String taskName = p.getValue().getTaskName();
						String taskTags = "\n\n"
								+ p.getValue().getTagsAsString();

						return new SimpleStringProperty((taskName + taskTags));
					}
				});

		TableColumn<Task, String> taskStartEndDate = new TableColumn<Task, String>(
				"Task Date");
		taskStartEndDate.setResizable(false);
		taskStartEndDate.setPrefWidth(200);
		taskStartEndDate
				.setCellValueFactory(new Callback<CellDataFeatures<Task, String>, ObservableValue<String>>() {
					@Override
					public ObservableValue<String> call(
							CellDataFeatures<Task, String> p) {

						return new SimpleStringProperty((p.getValue()
								.getDateAsString()));
					}
				});
		
		final TableColumn[] columns = {taskLblCol, taskNameCol, taskStartEndDate};

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

	// End Build taskTable and taskTableView

	// Displays view 2 of split.
	private void initTaskDetailsView() {
		taskDetailsView = new VBox();
		taskDetailsView.setPrefWidth(WIDTH_OF_SPLIT2);
		taskDetailsView.setSpacing(10);

		taskIDtf.getStyleClass().add("view2Split");
		taskIDtf.setDisable(true);

		taskNameta.getStyleClass().add("textarea");
		taskNameta.getStyleClass().add("view2Split");
		taskNameta.setPrefHeight(60);
		taskNameta.setWrapText(true);
		taskNameta.setDisable(true);

		taskStartDtesta.getStyleClass().add("textarea");
		taskStartDtesta.getStyleClass().add("view2Split");
		taskStartDtesta.setPrefHeight(60);
		taskStartDtesta.setWrapText(true);
		taskStartDtesta.setDisable(true);

		taskTagsta.getStyleClass().add("textarea");
		taskTagsta.getStyleClass().add("view2Split");
		taskTagsta.setPrefHeight(60);
		taskTagsta.setWrapText(true);
		taskTagsta.setDisable(true);

		taskDetailsView.getChildren().addAll(taskIDLbl, taskIDtf, taskNameLbl,
				taskNameta, taskStartDtesLbl, taskStartDtesta, taskTagsLbl,
				taskTagsta, notificationPane);
	}

	// Binds row to task detail
	private void blankTaskDetails() {
		taskIDtf.setDisable(false);
		taskIDtf.setText("");
		taskIDtf.setDisable(true);

		taskNameta.setDisable(false);
		taskNameta.setText("");
		taskNameta.setDisable(true);

		taskStartDtesta.setDisable(false);
		taskStartDtesta.setText("");
		taskStartDtesta.setDisable(true);

		taskTagsta.setDisable(false);
		taskTagsta.setText("");
		taskTagsta.setDisable(true);
	}

	private void bindTaskDetails(Task task) {
		taskIDtf.setDisable(false);
		taskIDtf.setText(taskUserSelected.getDisplayId());
		taskIDtf.setDisable(true);

		taskNameta.setDisable(false);
		taskNameta.setText(taskUserSelected.getTaskName());
		taskNameta.setDisable(true);

		taskStartDtesta.setDisable(false);
		taskStartDtesta.setText(taskUserSelected.getDateAsString());
		taskStartDtesta.setDisable(true);

		taskTagsta.setDisable(false);
		taskTagsta.setText(taskUserSelected.getTagsAsString());
		taskTagsta.setDisable(true);

	}

	// End Displaying View 2

	// Functions that deal with displaying notifications to user and retrieving
	// user notifications******************
	private void initUserCommands() {
		userCommands.setText("");
		userCommands.requestFocus();
	}

	public String getUserInput() {
		return userCommands.getText();
	}

	private void initNotificationPane() {
		notificationPane = new NotificationPane(new FlowPane());
		notificationPane.getStyleClass().removeAll(notificationPane.getStyleClass());
		notificationPane.getStyleClass().add("notificationpane");
		notificationPane.setShowFromTop(false);
		notificationPane.setDisable(true);
		notificationPane.setMinSize(WIDTH_OF_SPLIT2, 100);
	}

	public void setMessageToUser(String msg) {
		notificationPane.setDisable(false);
		notificationPane.show(msg);
		hideNotificationAfter(4000);
		notificationPane.setDisable(true);

	}

	private void hideNotificationAfter(int ms) {
		new java.util.Timer().schedule(new java.util.TimerTask() {
			@Override
			public void run() {
				notificationPane.hide();
			}
		}, ms);
	}

	private void displayPreviousMessage(String command) {
		if (command.equals("UP")) {
			if (previousUserCommandsCounter < 0) {
				// do nothing
			} else {
				userCommands.setText(previousUserCommands
						.get(previousUserCommandsCounter));
				previousUserCommandsCounter--;
			}
		} else if (command.equals("DOWN")) {
			if (previousUserCommandsCounter == previousUserCommands.size() - 1) {
				userCommands.setText("");
			} else {
				previousUserCommandsCounter++;
				userCommands.setText(previousUserCommands
						.get(previousUserCommandsCounter));
			}

		} else {
			// do nothing
		}

	}

	// Functions that deal with displaying notifications to user and retrieving
	// user notifications******************

	// Display tasks in the taskTable.
	public void displayTasks(ArrayList<Task> taskAL) {
		this.displayTasks.removeAll(displayTasks);
		this.displayTasks = taskAL;
		if (dataToDisplay != null) {
			dataToDisplay.removeAll(dataToDisplay);
		}
		dataToDisplay = FXCollections.observableArrayList(displayTasks);
		taskTable.setItems(dataToDisplay);

		if (dataToDisplay.size() > 0) {
			this.taskUserSelected = dataToDisplay.get(0);
			bindTaskDetails(taskUserSelected);

		} else {
			blankTaskDetails();
		}

		initUserCommands();

	}

	// Allows Controller to pass stage for this UI to display Scene.
	public void showStage(Stage primaryStage) {
		Scene scene = new Scene(this.taskView);
		scene.getStylesheets().add("myStyles.css");
		primaryStage.setTitle("SPEED");
		primaryStage.setScene(scene);
		primaryStage.show();
		primaryStage.setResizable(false);
	}

	// *****************************************************************

	// These methods allow the controller to observe the UI for updates to user
	// input
	private void initObservers() {
		uiObserver = new ArrayList<UIObserver>();
	}

	public void addUIObserver(UIObserver observer) {
		uiObserver.add(observer);
	}

	private void notifyObservers() {
		for (UIObserver observer : uiObserver) {
			observer.update();
		}
	}
	// ******************************************************************
}

class BackgroundTableCell extends TableCell<Task, String> {
	// CSS
	private static final String CSS_FLOATINGTASKROW = "floatingTaskRow";
	private static final String CSS_OVERDUETASKROW = "overdueTaskRow";
	private static final String CSS_NORMALTASKROW = "normalTaskRow";
	
	@Override
	protected void updateItem(final String item, final boolean empty) {
		super.updateItem(item, empty);

		setText(empty ? "" : item);
		getStyleClass().removeAll(CSS_FLOATINGTASKROW, CSS_OVERDUETASKROW,
				CSS_NORMALTASKROW);
		updateStyles(empty ? null : item);
	}

	private void updateStyles(String item) {
		if (item == null) {
			return;
		}

		if (item.contains("F")) {
			getStyleClass().addAll(CSS_FLOATINGTASKROW);
		} else if (item.contains("O")) {
			getStyleClass().add(CSS_OVERDUETASKROW);
		} else if (item.contains("T")) {
			getStyleClass().add(CSS_NORMALTASKROW);
		}
	}
}
