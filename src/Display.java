import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.TreeMap;

public class Display {
	
	Scanner scan;
	SimpleDateFormat sdf;
	
	public Display() {
		scan = new Scanner(System.in);
		sdf = new SimpleDateFormat("dd-MM-YYYY HH:mm");
	}
	
	public boolean hasNextLine() {
		return scan.hasNextLine();
	}
	
	public String get() {
		return scan.nextLine();
	}
	
	public void toDisplay(ArrayList<Task> tasklist) {
		if (tasklist.isEmpty()) {
			println("-- Found no tasks");
		} else {
			for (int i = 0; i < tasklist.size(); i++) {
				Task task = tasklist.get(i);
				println("Task ID: " + task.getDisplayId());
				toDisplay(task);
			}
		}
	}
	
	public void toDisplay(Task task) {

		println("-- Task name: " + task.getTaskName());

		println("-- Task date(s): ");
		println(task.getDateAsString());
	}
	
	public void print(Object obj) {
		System.out.print(obj);
	}
	
	public void println(Object obj) {
		System.out.println(obj);
	}
	
	public void printDate(Calendar date) {
		println(sdf.format(date.getTime()));
	}

}
