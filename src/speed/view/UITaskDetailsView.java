package speed.view;

import org.controlsfx.control.NotificationPane;

import speed.task.Task;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;

class UITaskDetailsView extends VBox {
	private UI ui;
	// private VBox taskDetailsView;
	private NotificationPane notificationPane;

	protected Label taskIDLbl;
	protected TextField taskIDtf;
	protected Label taskNameLbl;
	protected TextArea taskNameta;
	protected Label taskStartDtesLbl;
	protected TextArea taskStartDtesta;
	protected Label taskTagsLbl;
	protected TextArea taskTagsta;

	// Dimensions
	private static final double HEIGHT_OF_TEXTAREAS = 60;
	private static final double HEIGHT_OF_TASKNAMETA = 110;
	private static final double WIDTH_OF_TASKDETAILSVIEW = 310;
	private static final double WIDTH_OF_TASKDETAILSVIEW_INSET_SPACING = 10;
	private static final double HEIGHT_OF_NOTIFICATIONPANE = 50;

	// CSS
	private static final String CSS_VIEW2COMPONENTS = "view2Split";
	private static final String CSS_TEXTAREA = "textarea";
	private static final String CSS_NOTIFICATIONPANE = "notificationpane";

	// Class Values
	private static final int TIMEOUT = 4000;
	private static final String EMPTY_STRING = "";

	// End view 2 of split

	public UITaskDetailsView(UI ui) {
		this.ui = ui;
		initTaskDetailsView();
		
	}

	// Displays view 2 of split.
	private void initTaskDetailsView() {
		this.setPrefWidth(WIDTH_OF_TASKDETAILSVIEW);
		this.setSpacing(WIDTH_OF_TASKDETAILSVIEW_INSET_SPACING);
		initNotificationPane();
		
		taskIDLbl = new Label("Task ID: ");

		taskIDtf = new TextField();
		taskIDtf.getStyleClass().add(CSS_VIEW2COMPONENTS);
		taskIDtf.setDisable(true);

		taskNameLbl = new Label("Task Name: ");
		taskNameta = new TextArea();
		taskNameta.getStyleClass().add(CSS_TEXTAREA);
		taskNameta.getStyleClass().add(CSS_VIEW2COMPONENTS);
		taskNameta.setPrefHeight(HEIGHT_OF_TASKNAMETA);
		taskNameta.setWrapText(true);
		taskNameta.setDisable(true);

		taskStartDtesLbl = new Label("Task Dates: ");
		taskStartDtesta = new TextArea();
		taskStartDtesta.getStyleClass().add(CSS_TEXTAREA);
		taskStartDtesta.getStyleClass().add(CSS_VIEW2COMPONENTS);
		taskStartDtesta.setPrefHeight(HEIGHT_OF_TEXTAREAS);
		taskStartDtesta.setWrapText(true);
		taskStartDtesta.setDisable(true);

		taskTagsLbl = new Label("Task Tags: ");
		taskTagsta = new TextArea();
		taskTagsta.getStyleClass().add(CSS_TEXTAREA);
		taskTagsta.getStyleClass().add(CSS_VIEW2COMPONENTS);
		taskTagsta.setPrefHeight(HEIGHT_OF_TEXTAREAS);
		taskTagsta.setWrapText(true);
		taskTagsta.setDisable(true);

		this.getChildren().addAll(taskIDLbl, taskIDtf, taskNameLbl, taskNameta,
				taskStartDtesLbl, taskStartDtesta, taskTagsLbl, taskTagsta,
				notificationPane);

	}

	protected void blankTaskDetails() {
		taskIDtf.setDisable(false);
		taskIDtf.setText(EMPTY_STRING);
		taskIDtf.setDisable(true);

		taskNameta.setDisable(false);
		taskNameta.setText(EMPTY_STRING);
		taskNameta.setDisable(true);

		taskStartDtesta.setDisable(false);
		taskStartDtesta.setText(EMPTY_STRING);
		taskStartDtesta.setDisable(true);

		taskTagsta.setDisable(false);
		taskTagsta.setText(EMPTY_STRING);
		taskTagsta.setDisable(true);
	}

	protected void bindTaskDetails(Task task) {
		taskIDtf.setDisable(false);
		taskIDtf.setText(ui.taskUserSelected.getDisplayId());
		taskIDtf.setDisable(true);

		taskNameta.setDisable(false);
		taskNameta.setText(ui.taskUserSelected.getTaskName());
		taskNameta.setDisable(true);

		taskStartDtesta.setDisable(false);
		taskStartDtesta.setText(ui.taskUserSelected.getDateAsString() + " "
				+ ui.taskUserSelected.getRecurAsString());
		taskStartDtesta.setDisable(true);

		taskTagsta.setDisable(false);
		taskTagsta.setText(ui.taskUserSelected.getTagsAsString());
		taskTagsta.setDisable(true);

	}

	private void initNotificationPane() {
		notificationPane = new NotificationPane(new FlowPane());
		notificationPane.getStyleClass().removeAll(
				notificationPane.getStyleClass());
		notificationPane.getStyleClass().add(CSS_NOTIFICATIONPANE);
		notificationPane.setShowFromTop(false);
		notificationPane.setDisable(true);
		notificationPane.setMinSize(WIDTH_OF_TASKDETAILSVIEW,
				HEIGHT_OF_NOTIFICATIONPANE);
	}

	protected void setNotificationToUser(String msg) {
		notificationPane.setDisable(false);
		notificationPane.show(msg);
		hideNotificationAfter(TIMEOUT);
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

}
