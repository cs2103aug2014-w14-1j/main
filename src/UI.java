import java.util.ArrayList;

import com.sun.istack.internal.logging.Logger;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;


public class UI extends Application{
	HBox root; 
	
	VBox taskView;
	HBox seperateTaskView;
	VBox normTaskView;
	VBox floatTaskView;
	FlowPane normTaskListPane;
	FlowPane floatTaskListPane;
	TextField userCommands;
	
	VBox otherTaskView;
	TextArea overdueTasks;
	TextArea reminders;
	
	private static Logger log = Logger.getLogger(Application.class);
	
	private static final double WIDTH_OF_PROGRAM = 900;
	private static final double HEIGHT_OF_PROGRAM = 500;
	private static final double WIDTH_OF_NORMTASKVIEW = WIDTH_OF_PROGRAM * 7/10;
	private static final double HEIGHT_OF_USERCOMMANDS = 10;
	private static final double WIDTH_OF_OTHERTASKVIEW = WIDTH_OF_PROGRAM * 3/10;
	private static final double SPACING = 20;
		
	
	public static void main(String[] args){
		launch(args);
	}
	
	@Override
	public void start(Stage stage) throws Exception {
		initStage(stage);
		initUserCommands();
		log.info("Successful initiation of Stage");
		
	}
	
	/*Method handles the display and sizing of components*/
	
	private void initStage(Stage stage){
		ArrayList<String> al = new ArrayList<String>();
		for(int i = 0; i < 50; i ++){
		al.add("Test " + i);		
		}
		
		root = new HBox();
		root.setPrefSize(WIDTH_OF_PROGRAM, HEIGHT_OF_PROGRAM);
		
		//******************************************************************
		
		//Normal Tasks
		taskView = new VBox();
		taskView.setPrefWidth(WIDTH_OF_NORMTASKVIEW);
		taskView.setPadding(new Insets(SPACING,SPACING,SPACING,SPACING));
		taskView.setSpacing(SPACING);
		
		seperateTaskView = new HBox();
		normTaskView = new VBox();
		floatTaskView = new VBox();
		
		//normTaskView
		ObservableList<String> ol = FXCollections.observableArrayList(al);
		ListView<String> lv = new ListView<String>(ol);
		normTaskListPane = new FlowPane();
		normTaskListPane.getChildren().add(lv);
				
		Label normTaskLbl = new Label("Tasks");
		
		normTaskView.getChildren().add(normTaskLbl);
		normTaskView.getChildren().add(normTaskListPane);
		
		//floatTaskView
		ObservableList<String> ol2 = FXCollections.observableArrayList(al);
		ListView<String> lv2 = new ListView<String>(ol2);
		floatTaskListPane = new FlowPane();
		floatTaskListPane.getChildren().add(lv2);
		Label floatTaskLbl = new Label("Floating Tasks");
		
		floatTaskView.getChildren().add(floatTaskLbl);
		floatTaskView.getChildren().add(floatTaskListPane);
		
		//seperateTaskView
		seperateTaskView.getChildren().add(normTaskView);
		seperateTaskView.getChildren().add(floatTaskView);
		
		//userCommands text Area.
		userCommands = new TextField("");
		userCommands.setId("inputText");
		userCommands.setPrefHeight(HEIGHT_OF_USERCOMMANDS);
		
		//taskView
		taskView.getChildren().add(seperateTaskView);
		taskView.getChildren().add(userCommands);
		
		//******************************************************************
		
		otherTaskView = new VBox();
		otherTaskView.setPrefWidth(WIDTH_OF_OTHERTASKVIEW);
		otherTaskView.setPadding(new Insets(SPACING,SPACING,SPACING,SPACING));
		otherTaskView.setSpacing(10);
						
		Label overdueTaskLbl = new Label("Overdue Tasks");
		overdueTasks = new TextArea();
		Label remindersLbl = new Label("Reminders");
		reminders = new TextArea();
		
		otherTaskView.getChildren().add(overdueTaskLbl);
		otherTaskView.getChildren().add(overdueTasks);
		otherTaskView.getChildren().add(remindersLbl);
		otherTaskView.getChildren().add(reminders);
		//******************************************************************
		
		//to toggle action on "ENTER"
		userCommands.setOnKeyPressed(new EventHandler<KeyEvent>(){

			@Override
			public void handle(KeyEvent ke) {
				  if (ke.getCode().equals(KeyCode.ENTER))
		            {
		               System.out.println(userCommands.getText());
		               initUserCommands();
		            }
				
			}
			
		});
		
		//****************************************************
		
		//adding to main stage
		root.getChildren().add(taskView);
		root.getChildren().add(otherTaskView);
		
		Scene scene = new Scene(root);
		scene.getStylesheets().add("myStyles.css");
		stage.setTitle("SPEED");
		stage.setScene(scene);
		stage.show();
	}


	
	private void initUserCommands(){
		userCommands.setText("");
		userCommands.requestFocus();
		log.info("Ready to receive Input");
	}
	
}
