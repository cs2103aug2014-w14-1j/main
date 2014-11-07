//@author A0111660W
package speed.view;

import java.util.ArrayList;
import speed.task.Task;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class UI extends FlowPane {
	private ArrayList<UIObserver> uiObserver;
	private UIKeyEventHandler uiKeyEventHandler;
	private UITaskDetailsView uiTaskDetailsView;
	private UITaskTableView uiTaskTableView;
	private Task taskUserSelected = null;

	private VBox root;
	private HBox split;

	protected TextField userCommands;
	protected ArrayList<String> userCommandsHistory;
	protected int userCommandsHistoryCounter;

	// Dimensions
	protected static final double WIDTH_OF_PROGRAM = 960;
	protected static final double WIDTH_OF_TASKVIEW_INSET_SPACING = 20;
	protected static final double HEIGHT_OF_USERCOMMANDS = 10;
	protected static final double WIDTH_OF_USERCOMMANDS = WIDTH_OF_PROGRAM - 2
			* UITaskDetailsView.WIDTH_OF_TASKDETAILSVIEW_INSET_SPACING - 3
			* WIDTH_OF_TASKVIEW_INSET_SPACING
			- UITaskTableView.WIDTH_OF_TASKLBLCOL; // for Alignment

	// Program values
	private static final String PROGRAM_NAME = "SPEED";
	private static final String EMPTY_STRING = "";

	// CSS
	private static final String CSS_MAIN_TASKVIEW = "myStyles.css";
	private static final String CSS_USERCOMMANDS = "inputText";

	public UI() {
		initRoot();
		initUserCommandsHistory();
		initSplit();
		initUIKeyEventHandler();
		initTaskTableView();
		initTaskDetailsView();
		initUserCommands();
		initObservers();

		doSplitAddViews();
		doRootAddViews();
		doDefaultUserCommands();

	}

	// Initiate all Views and Default actions for the start up of UI.
	private void initRoot() {
		root = new VBox();
		root.setPrefWidth(WIDTH_OF_PROGRAM);
		root.setPadding(new Insets(WIDTH_OF_TASKVIEW_INSET_SPACING,
				WIDTH_OF_TASKVIEW_INSET_SPACING,
				WIDTH_OF_TASKVIEW_INSET_SPACING,
				WIDTH_OF_TASKVIEW_INSET_SPACING));
		root.setSpacing(WIDTH_OF_TASKVIEW_INSET_SPACING);
	}

	private void initUserCommandsHistory() {
		userCommandsHistory = new ArrayList<String>();
	}

	private void initSplit() {
		split = new HBox();
		split.setSpacing(WIDTH_OF_TASKVIEW_INSET_SPACING);
	}

	private void initUIKeyEventHandler() {
		this.uiKeyEventHandler = new UIKeyEventHandler(this);
	}

	private void initTaskTableView() {
		this.uiTaskTableView = new UITaskTableView(this);
	}

	private void initTaskDetailsView() {
		this.uiTaskDetailsView = new UITaskDetailsView();
	}

	private void initUserCommands() {
		userCommands = new TextField(EMPTY_STRING);
		userCommands.setId(CSS_USERCOMMANDS);
		userCommands.setMaxWidth(WIDTH_OF_USERCOMMANDS);
		userCommands.setTranslateX(UITaskTableView.WIDTH_OF_TASKLBLCOL + 2
				* UITaskDetailsView.WIDTH_OF_TASKDETAILSVIEW_INSET_SPACING
				+ WIDTH_OF_TASKVIEW_INSET_SPACING); // alignment

		userCommands.setPrefHeight(HEIGHT_OF_USERCOMMANDS);

		userCommands.setOnKeyPressed(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent ke) {
				uiKeyEventHandler.doRequestedUserCommandsKeyEvent(ke);
			}
		});
	}

	private void initObservers() {
		uiObserver = new ArrayList<UIObserver>();
	}

	private void doSplitAddViews() {
		split.getChildren().addAll(uiTaskTableView, uiTaskDetailsView);
	}

	private void doRootAddViews() {
		root.getChildren().addAll(split, userCommands);
	}

	protected void doDefaultUserCommands() {
		userCommands.setText(EMPTY_STRING);
		userCommands.requestFocus();
	}

	// *********************************ACCESSORS WITHIN UI PACKAGE**********************************************
	protected void setTaskUserSelected(Task task) {
		this.taskUserSelected = task;
	}

	protected Task getTaskUserSelected() {
		return this.taskUserSelected;
	}

	protected UITaskTableView getUITaskTableView() {
		if (this.uiTaskTableView == null) {
			this.uiTaskTableView = new UITaskTableView(this);
		}
		return this.uiTaskTableView;
	}

	protected UITaskDetailsView getUITaskDetailsView() {
		if (this.uiTaskDetailsView == null) {
			this.uiTaskDetailsView = new UITaskDetailsView();
		}
		return this.uiTaskDetailsView;
	}

	protected UIKeyEventHandler getUIKeyEventHandler() {
		if (this.uiKeyEventHandler == null) {
			this.uiKeyEventHandler = new UIKeyEventHandler(this);
		}
		return this.uiKeyEventHandler;
	}

	protected String getUserCommands() {
		return this.userCommands.getText();
	}

	protected void setUserCommands(String text) {
		this.userCommands.setText(text);
	}
	

	protected void notifyObservers() {
		for (UIObserver observer : uiObserver) {
			observer.update();
		}
	}

	// *********************************END - ACCESSORS WITHIN UI PACKAGE**********************************************
	
	//******************** ACCESSORS CREATED FOR OTHER PACKAGES*********************************************************
	public void addUIObserver(UIObserver observer) {
		uiObserver.add(observer);
	}
	
	public String getUserInput() {
		return getUserCommands();
	}

	public void setNotificationToUser(String msg) {
		getUITaskDetailsView().setNotificationToUser(msg);
	}

	public void displayTasks(ArrayList<Task> taskAL) {
		getUITaskTableView().displayTasks(taskAL);
		doDefaultUserCommands();
	}

	public void showStage(Stage primaryStage) {
		Scene scene = new Scene(this.root);
		scene.getStylesheets().add(CSS_MAIN_TASKVIEW);
		primaryStage.setTitle(PROGRAM_NAME);
		primaryStage.setScene(scene);
		primaryStage.show();
		primaryStage.setResizable(false);
	}
	//******************** END - ACCESSORS CREATED FOR OTHER PACKAGES*********************************************************
}
