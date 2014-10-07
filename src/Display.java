import java.util.Scanner;
import java.util.ArrayList;
import java.util.Calendar;

public class Display {
	
	Scanner scan;
	
	public Display() {
		scan = new Scanner(System.in);
	}
	
	public String get() {
		return scan.nextLine();
	}
	
	public void toDisplay(ArrayList<Task> tasklist) {
		for (Task task : tasklist) {
			print(task.getTaskId());
			print(task.getTaskName());
			ArrayList<Calendar> dates = task.getTaskDatesTimes();
			for (Calendar date : dates) {
				print(date.toString());
			}
		}
	}
	
	private void print(String string) {
		System.out.println(string);
	}
}