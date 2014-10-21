import java.util.ArrayList;
import javafx.beans.property.SimpleStringProperty;
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

	// rest of components of taskView
	private TextField userCommands;
	private TextField messagesToUser;

	// Dimensions
	private static final double WIDTH_OF_PROGRAM = 1100;
	private static final double WIDTH_OF_SPLIT2 = 300;
	private static final double HEIGHT_OF_USERCOMMANDS = 10;
	private static final double SPACING = 20;
	private ObservableList<Task> dataToDisplay;
	private ArrayList<Task> displayTasks = new ArrayList<Task>();

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
		taskDetailsView = new VBox();
		taskDetailsView.setPrefWidth(WIDTH_OF_SPLIT2);
		taskDetailsView.setSpacing(10);

		Label taskIDLbl = new Label("Task ID: ");
		TextField taskIDtf = new TextField("");
		taskIDtf.setId("view2Split");
		taskIDtf.setDisable(true);

		Label taskNameLbl = new Label("Task Name: ");
		TextField taskNametf = new TextField("test");
		taskNametf.setId("view2Split");
		taskNametf.setDisable(true);

		Label taskStartDtesLbl = new Label("Task Dates: ");
		TextArea taskStartDtesta = new TextArea();
		taskStartDtesta.setId("view2Split");
		taskStartDtesta.setPrefHeight(90);
		taskStartDtesta.setDisable(true);

		Label taskReminderDtesLbl = new Label("Reminder Dates: ");
		TextArea taskReminderDtesta = new TextArea();
		taskReminderDtesta.setId("view2Split");
		taskReminderDtesta.setPrefHeight(90);
		taskReminderDtesta.setDisable(true);

		Label taskTagsLbl = new Label("Task Tags: ");
		TextArea taskTagsta = new TextArea();
		taskTagsta.setId("view2Split");
		taskTagsta.setPrefHeight(90);
		taskTagsta.setDisable(true);

		taskDetailsView.getChildren().addAll(taskIDLbl, taskIDtf, taskNameLbl,
				taskNametf, taskStartDtesLbl, taskStartDtesta,
				taskReminderDtesLbl, taskReminderDtesta, taskTagsLbl,
				taskTagsta);

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

		TableColumn<Task, String> taskStartDate = new TableColumn<Task, String>(
				"Task Start Date");
		taskStartDate.setResizable(false);
		taskStartDate.setPrefWidth(150);
		taskStartDate
				.setCellValueFactory(new Callback<CellDataFeatures<Task,  String>, ObservableValue<String>>() {
					@Override
					public ObservableValue<String> call(
							CellDataFeatures<Task,  String> p) {						
						return new SimpleStringProperty((p.getValue().getTaskDatesSorted().get(0)));
					}
				});
		
		TableColumn<Task, String> taskDeadlineDate = new TableColumn<Task, String>(
				"Task Due Date");
		taskDeadlineDate.setResizable(false);
		taskDeadlineDate.setPrefWidth(150);
		taskDeadlineDate
				.setCellValueFactory(new Callback<CellDataFeatures<Task,  String>, ObservableValue<String>>() {
					@Override
					public ObservableValue<String> call(
							CellDataFeatures<Task,  String> p) {						
						return new SimpleStringProperty((p.getValue().getTaskDatesSorted().get(0)));
					}
				});
		
		taskTable.getColumns().removeAll(taskTable.getColumns());
		taskTable.getColumns().addAll(taskLblCol, taskNameCol, taskStartDate, taskDeadlineDate);

	}

	private void initUserCommands() {
		userCommands.setText("");
		userCommands.requestFocus();
	}

	public String getUserInput() {
		return userCommands.getText();
	}

	public void setMessageToUser(String msg) {
		messagesToUser.setDisable(false);
		messagesToUser.clear();
		messagesToUser.appendText(msg);
		messagesToUser.setDisable(true);
	}

	// Display tasks in the taskTable.
	public void displayTasks(ArrayList<Task> taskAL) {
		this.displayTasks = taskAL;
		dataToDisplay = FXCollections.observableArrayList(displayTasks);
		buildColumns(dataToDisplay);
		taskTable.setItems(dataToDisplay);
		userCommands.requestFocus();

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
