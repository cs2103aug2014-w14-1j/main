import java.io.*;
import java.util.ArrayList;
import java.util.Calendar;

import org.json.JSONException;


public class Tests {

	public static void main(String[] args) {
		
		try {
			Storage storage = new Storage();
			
			//Task**********************************************************
			
			//Task 1: Normal task
			Task task1 = new Task();
			task1.setTaskName("Task 1: CS2103T finals");
			Calendar task1_date = Calendar.getInstance();
			task1_date.set(2014, 11, 26, 13, 00, 00);
			task1.setTaskDatesTimes(task1_date);
			task1.addTaskTags("school");
			task1.addTaskTags("CS2103T");
			
			Calendar task1_test_start_date = Calendar.getInstance();
			task1_test_start_date.set(2014, 11, 26, 12, 59, 59);
			Calendar task1_test_end_date = Calendar.getInstance();
			task1_test_end_date.set(2014, 11, 26, 13, 00, 01);
			
			test(task1.getTaskName(), "Task 1: CS2103T finals");
			test(task1.getTaskDatesTimes().getStartDate(), task1_date);
			test(task1.getTaskDatesTimes().getEndDate(), task1_date);
			test(task1.getTaskDatesTimes().withinDateRange(task1_test_start_date, task1_test_end_date), true);
			test(task1.getTaskTags().size(), 2);
			test(task1.getTaskTags().get(0), "school");
			test(task1.getTaskTags().get(1), "CS2103T");
			
			//Task 2: Task with interval
			Task task2 = new Task();
			task2.setTaskName("Task 2: MA3110 Finals");
			Calendar task2_start_date = Calendar.getInstance();
			task2_start_date.set(2014, 11, 27, 13, 00, 00);
			Calendar task2_end_date = Calendar.getInstance();
			task2_end_date.set(2014, 11, 27, 15, 00, 00);
			task2.setTaskDatesTimes(task2_start_date, task2_end_date);
			
			Calendar task2_test_start_date = Calendar.getInstance();
			task2_test_start_date.set(2014, 11, 24, 12, 59, 59);
			Calendar task2_test_end_date = Calendar.getInstance();
			task2_test_end_date.set(2014, 11, 28, 13, 00, 01);
			
			test(task2.getTaskDatesTimes().getStartDate(), task2_start_date);
			test(task2.getTaskDatesTimes().getEndDate(), task2_end_date);
			test(task2.getTaskDatesTimes().withinDateRange(task2_test_start_date, task2_test_end_date), true);
			
			task2_test_start_date.set(2014, 11, 26, 13, 30, 00);
			task2_test_end_date.set(2014, 11, 26, 14, 30, 00);
			test(task2.getTaskDatesTimes().withinDateRange(task2_test_start_date, task2_test_end_date), true);
		}
		
		catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	private static boolean checkEquals(String a, String b) {
		return a.equals(b);
	}

	private static boolean checkEquals(boolean a, boolean b) {
		return a == b;
	}
	
	private static boolean checkEquals(int a, int b) {
		return a == b;
	}
	
	private static boolean checkEquals(Calendar a, Calendar b) {
		return a.equals(b);
	}
	
	private static void test(String a, String b) {
		if(!checkEquals(a, b)) {
			System.out.println("MISMATCH");
			System.out.println(a);
			System.out.println(b);
		}
	}
	
	private static void test(boolean a, boolean b) {
		if(!checkEquals(a, b)) {
			System.out.println("MISMATCH");
			System.out.println(a);
			System.out.println(b);
		}
	}

	private static void test(int a, int b) {
		if(!checkEquals(a, b)) {
			System.out.println("MISMATCH");
			System.out.println(a);
			System.out.println(b);
		}
	}
	
	private static void test(Calendar a, Calendar b) {
		if(!checkEquals(a, b)) {
			System.out.println("MISMATCH");
			System.out.println(a.getTime());
			System.out.println(b.getTime());
		}
	}
}
