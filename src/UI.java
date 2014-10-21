import java.util.ArrayList;
import java.util.LinkedList;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
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
	private final TextField taskNametf = new TextField();
	private final Label taskStartDtesLbl = new Label("Task Dates: ");
	private final TextArea taskStartDtesta = new TextArea();
	private final Label taskReminderDtesLbl = new Label("Reminder Dates: ");
	private final TextArea taskReminderDtesta = new TextArea();
	private final Label taskTagsLbl = new Label("Task Tags: ");
	private final TextArea taskTagsta = new TextArea();
	private Task taskUserSelected = null;
	//

	// rest of components of taskView
	private TextField userCommands;
	private TextField messagesToUser;

	// Dimensions
	private static final double WIDTH_OF_PROGRAM = 1100;
	private static final double WIDTH_OF_SPLIT2 = 300;
	private static final double HEIGHT_OF_USERCOMMANDS = 10;
	private static final double SPACING = 20;

	// Parameters
	private static final int EARLIEST_DATE = 0;

	public UI() {
		taskView = new VBox();
		taskView.setPrefWidth(WIDTH_OF_PROGRAM);
		taskView.setPadding(new Insets(SPACING, SPACING, SPACING, SPACING));
		taskView.setSpacing(SPACING);

		// Split: HBox containing 2 views
		split = new HBox();
		split.setSpacing(SPACING);

		// taskTableView: contains taskTable, View 1 of
		// split.*************************
		taskTableView = new VBox();

		buildTaskTable();
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

		// mesagesToUser TextField
		messagesToUser = new TextField("* Add Use SPEED Today!");
		messagesToUser.setId("messageToUserText");
		messagesToUser.setDisable(true);

		// taskView
		taskView.getChildren().addAll(split, userCommands, messagesToUser);

		initUserCommands();
		initObservers();

		// to toggle action on "ENTER"
		userCommands.setOnKeyPressed(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent ke) {
				if (ke.getCode().equals(KeyCode.ENTER)) {
					notifyObservers();
					initUserCommands();
				}
			}

		});
	}

	// to Build the taskTable for taskTableView.
	private void buildTaskTable() {
		taskTable = new TableView<Task>();
		taskTable.setPrefWidth(800);
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
	}

	@SuppressWarnings("unchecked")
	private void buildColumns(ObservableList<Task> data) {

		TableColumn<Task, String> taskLblCol = new TableColumn<Task, String>(
				"Task ID");
		taskLblCol.setPrefWidth(60);
		taskLblCol.setResizable(false);
		taskLblCol
				.setCellValueFactory(new Callback<CellDataFeatures<Task, String>, ObservableValue<String>>() {
					@Override
					public ObservableValue<String> call(
							CellDataFeatures<Task, String> p) {
						return new SimpleStringProperty((p.getValue()
								.getDisplayId()));
					}
				});

		TableColumn<Task, String> taskNameCol = new TableColumn<Task, String>(
				"Task Name");
		taskNameCol.setPrefWidth(400);
		taskNameCol.setResizable(false);
		taskNameCol
				.setCellValueFactory(new Callback<CellDataFeatures<Task, String>, ObservableValue<String>>() {
					@Override
					public ObservableValue<String> call(
							CellDataFeatures<Task, String> p) {
						return new SimpleStringProperty((p.getValue()
								.getTaskName()));
					}
				});

		TableColumn<Task, String> taskStartEndDate = new TableColumn<Task, String>(
				"Task Date");
		taskStartEndDate.setResizable(false);
		taskStartEndDate.setPrefWidth(300);
		taskStartEndDate
				.setCellValueFactory(new Callback<CellDataFeatures<Task, String>, ObservableValue<String>>() {
					@Override
					public ObservableValue<String> call(
							CellDataFeatures<Task, String> p) {

						return new SimpleStringProperty((p.getValue()
								.getTaskDatesSorted().get(EARLIEST_DATE)));
					}
				});

		taskTable.getColumns().removeAll(taskTable.getColumns());
		taskTable.getColumns()
				.addAll(taskLblCol, taskNameCol, taskStartEndDate);

	}

	// End Build taskTable and taskTableView

	// Displays view 2 of split.
	private void initTaskDetailsView() {
		taskDetailsView = new VBox();
		taskDetailsView.setPrefWidth(WIDTH_OF_SPLIT2);
		taskDetailsView.setSpacing(10);

		taskIDtf.setId("view2Split");
		taskIDtf.setDisable(true);

		taskNametf.setId("view2Split");
		taskNametf.setDisable(true);

		taskStartDtesta.setId("view2Split");
		taskStartDtesta.setPrefHeight(90);
		taskStartDtesta.setDisable(true);

		taskReminderDtesta.setId("view2Split");
		taskReminderDtesta.setPrefHeight(90);
		taskReminderDtesta.setDisable(true);

		taskTagsta.setId("view2Split");
		taskTagsta.setPrefHeight(90);
		taskTagsta.setDisable(true);

		taskDetailsView.getChildren().addAll(taskIDLbl, taskIDtf, taskNameLbl,
				taskNametf, taskStartDtesLbl, taskStartDtesta,
				taskReminderDtesLbl, taskReminderDtesta, taskTagsLbl,
				taskTagsta);
	}

	// Binds row to task detail
	private void blankTaskDetails() {
		taskIDtf.setDisable(false);
		taskIDtf.setText("");
		taskIDtf.setDisable(true);

		taskNametf.setDisable(false);
		taskNametf.setText("");
		taskNametf.setDisable(true);

		taskStartDtesta.setDisable(false);
		taskStartDtesta.setText("");
		taskStartDtesta.setDisable(true);
		
//		taskReminderDtesta.setDisable(false);
//		taskReminderDtesta.setText("");
//		taskReminderDtesta.setDisable(true);
	}

	private void bindTaskDetails(Task task) {
		taskIDtf.setDisable(false);
		taskIDtf.setText(taskUserSelected.getDisplayId());
		taskIDtf.setDisable(true);

		taskNametf.setDisable(false);
		taskNametf.setText(taskUserSelected.getTaskName());
		taskNametf.setDisable(true);

		taskStartDtesta.setDisable(false);
		LinkedList<String> taskDates = taskUserSelected.getTaskDatesSorted();
		String taskdateStr = "";
		for (String s : taskDates) {
			taskdateStr = s + "\n";
		}
		taskStartDtesta.setText(taskdateStr);
		taskStartDtesta.setDisable(true);
		
		
		// private TextArea taskReminderDtesta = new TextArea();
		// private TextArea taskTagsta = new TextArea();

	}

	// End Displaying View 2

	private void initUserCommands() {
		userCommands.setText("");
		userCommands.requestFocus();
	}

	public String getUserInput() {
		return userCommands.getText();
	}

	public void setMessageToUser(String msg) {
		messagesToUser.setDisable(false);
		messagesToUser.setText(msg);
		messagesToUser.setDisable(true);
	}

	// Display tasks in the taskTable.
	public void displayTasks(ArrayList<Task> taskAL) {
		this.displayTasks = taskAL;
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
