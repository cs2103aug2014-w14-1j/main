import java.util.ArrayList;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class UI extends Application {
	private ArrayList<UIObserver> uiObserver;
		
	private VBox taskView;
	
	private HBox split;
	//view 1
	private VBox taskTableView;
	private TableView<Task> taskTable;
	//view 2
	private VBox taskDetailsView;
	//rest of taskView
	private TextField userCommands;
	private TextField messagesToUser;

	private static final double WIDTH_OF_PROGRAM = 1100;
	private static final double WIDTH_OF_SPLIT2 = 300;
	private static final double HEIGHT_OF_USERCOMMANDS = 10;
	private static final double SPACING = 20;
	private ObservableList<Task> dataToDisplay;
	private ArrayList<Task> displayTasks;
	

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage stage) throws Exception {
		// Test
		displayTasks = new ArrayList<Task>();
		for (int i = 0; i < 30; i++) {
			Task t = new Task();
			t.setTaskId(i);
			t.setTaskName("Task" + i);
			t.setDisplayId("t" + i);
			displayTasks.add(t);
		}
		initStage(stage);
		initObservers();
		initUserCommands();

	}

	/* Method handles the display and sizing of components */

	@SuppressWarnings("unchecked")
	private void initStage(Stage stage) {
		// ******************************************************************
		
		
		//taskView: root view
		taskView = new VBox();
		taskView.setPrefWidth(WIDTH_OF_PROGRAM);
		taskView.setPadding(new Insets(SPACING, SPACING, SPACING, SPACING));
		taskView.setSpacing(SPACING);
			
		//Split: HBox containing 2 views
		split = new HBox();
		split.setSpacing(SPACING);
		
		// taskTableView: contains taskTable, View 1 of split.*************************
		taskTableView = new VBox();
		
		// taskTable
		taskTable = new TableView<Task>();
		taskTable.setPrefWidth(720);
		taskTable.setPrefHeight(500);
	
				
		dataToDisplay = FXCollections.observableArrayList(displayTasks);
		
		//width adds to 710
		TableColumn<Task, String> taskLblCol = new TableColumn<Task, String>(
				"Task ID");
		taskLblCol.setPrefWidth(60);
		taskLblCol.setResizable(false);
		taskLblCol.setCellValueFactory(new PropertyValueFactory<Task, String>(
				"taskDisplayId"));

		TableColumn<Task, String> taskNameCol = new TableColumn<Task, String>(
				"Task Name");
		taskNameCol.setPrefWidth(400);
		taskNameCol.setResizable(false);
		taskNameCol.setCellValueFactory(new PropertyValueFactory<Task, String>(
				"taskName"));

		TableColumn<Task, String> taskReminderCol = new TableColumn<Task, String>(
				"Task Start Date");
		taskReminderCol.setResizable(false);
		taskReminderCol.setPrefWidth(125);

		TableColumn<Task, String> taskDeadlineCol = new TableColumn<Task, String>(
				"Task Due Date");
		taskDeadlineCol.setPrefWidth(125);
		taskDeadlineCol.setResizable(false);

		taskTable.setItems(dataToDisplay);
		taskTable.getColumns().addAll(taskLblCol, taskNameCol, taskReminderCol,
				taskDeadlineCol);//, taskTags);
		
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
		
		taskDetailsView.getChildren().addAll(taskIDLbl,taskIDtf, taskNameLbl, taskNametf,  
				taskStartDtesLbl, taskStartDtesta, taskReminderDtesLbl, taskReminderDtesta,
				taskTagsLbl, taskTagsta);
		
		
		//adding split pane
		split.getChildren().addAll(taskTableView, taskDetailsView);
		
		// userCommands TextField.
		userCommands = new TextField("");
		userCommands.setId("inputText");
		userCommands.setPrefWidth(WIDTH_OF_PROGRAM - SPACING - SPACING);
		userCommands.setPrefHeight(HEIGHT_OF_USERCOMMANDS);
		 	
		//mesagesToUser TextField
		messagesToUser = new TextField("* Add Use SPEED Today!");
		messagesToUser.setId("messageToUserText");
		messagesToUser.setDisable(true);
		
		
		// taskView
		taskView.getChildren().addAll(split, userCommands, messagesToUser);

		// adding to Scene
		Scene scene = new Scene(taskView);
		scene.getStylesheets().add("myStyles.css");
		stage.setTitle("SPEED");
		stage.setScene(scene);
		stage.show();

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

		// ****************************************************

	}

	private void initUserCommands() {
		userCommands.setText("");
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

	private void notifyObservers() {
		for (UIObserver observer : uiObserver) {
			observer.update();
		}
	}

	public String getUserInput() {
		return userCommands.getText();
	}
	
	public void setMessageToUser(String msg){
		messagesToUser.setDisable(false);
		messagesToUser.clear();
		messagesToUser.appendText(msg);
		messagesToUser.setDisable(true);
	}
	
	public void displayTasks(ArrayList<Task> taskAL){
		this.displayTasks = taskAL;		
	}
	// ****************************************************************************
}
