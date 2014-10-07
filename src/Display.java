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
		for (Task task : tasklist) {
			print(task.getTaskId());
			print(task.getTaskName());
			LinkedList<Calendar> dates = task.getTaskDatesTimes();
			for (Calendar date : dates) {
				print(date.toString());
			}
		}
	}
	
	public void print(String string) {
		System.out.println(string);
	}
}
