import java.util.LinkedList;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.Calendar;

public class Display {
	
	Scanner scan;
	
	public Display() {
		scan = new Scanner(System.in);
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
			int index = 1;
			for (Task task : tasklist) {
				println("Task index: " + Integer.toString(index));
				index++;
				toDisplay(task);
			}
		}
	}
	
	public void toDisplay(Task task) {

		println("-- Task name: " + task.getTaskName());

		println("-- Task date(s): ");
		LinkedList<Calendar> dates = task.getTaskDatesTimes();
		for (Calendar date : dates) {
			println(date.toString());
			}
		println("");

	}
	
	public void print(String string) {
		System.out.print(string);
	}
	
	public void println(String string) {
		System.out.println(string);
	}
}
