import java.io.*;
import java.util.ArrayList;
import java.util.Calendar;

import org.json.JSONException;


public class Tests {

	public static void main(String[] args) {
		
		try {
			Storage storage = new Storage();
			
			//Task tests**********************************************************
			
			//Task 1: Normal task
			Task task1 = new Task();
			task1.setTaskName("Task 1: CS2103T finals");
			Calendar task1_date = Calendar.getInstance();
			task1_date.set(2014, Calendar.NOVEMBER, 26, 13, 00, 00);
			task1.setTaskDatesTimes(task1_date);
			task1.addTaskTags("school");
			task1.addTaskTags("CS2103T");
			task1.addTaskTags("exams");
			
			Calendar task1_test_start_date = Calendar.getInstance();
			task1_test_start_date.set(2014, Calendar.NOVEMBER, 26, 12, 59, 59);
			Calendar task1_test_end_date = Calendar.getInstance();
			task1_test_end_date.set(2014, Calendar.NOVEMBER, 26, 13, 00, 01);
			
			test(task1.getTaskName(), "Task 1: CS2103T finals");
			test(task1.getTaskDatesTimes().getStartDate(), task1_date);
			test(task1.getTaskDatesTimes().getEndDate(), task1_date);
			test(task1.getTaskDatesTimes().withinDateRange(task1_test_start_date, task1_test_end_date), true);
			test(task1.getTaskTags().size(), 3);
			test(task1.getTaskTags().get(0), "school");
			test(task1.getTaskTags().get(1), "CS2103T");
			test(task1.getTaskTags().get(2), "exams");
			
			//Task 2: Task with interval
			Task task2 = new Task();
			task2.setTaskName("Task 2: MA3110 Finals");
			Calendar task2_start_date = Calendar.getInstance();
			task2_start_date.set(2014, Calendar.NOVEMBER, 27, 13, 00, 00);
			Calendar task2_end_date = Calendar.getInstance();
			task2_end_date.set(2014, Calendar.NOVEMBER, 27, 15, 00, 00);
			task2.setTaskDatesTimes(task2_start_date, task2_end_date);
			task1.addTaskTags("school");
			task1.addTaskTags("MA3110");
			task1.addTaskTags("exams");
			
			Calendar task2_test_start_date = Calendar.getInstance();
			task2_test_start_date.set(2014, Calendar.NOVEMBER, 24, 12, 59, 59);
			Calendar task2_test_end_date = Calendar.getInstance();
			task2_test_end_date.set(2014, Calendar.NOVEMBER, 28, 13, 00, 01);
			
			test(task2.getTaskDatesTimes().getStartDate(), task2_start_date);
			test(task2.getTaskDatesTimes().getEndDate(), task2_end_date);
			test(task2.getTaskDatesTimes().withinDateRange(task2_test_start_date, task2_test_end_date), true);
			
			task2_test_start_date.set(2014, Calendar.NOVEMBER, 27, 13, 30, 00);
			test(task2.getTaskDatesTimes().withinDateRange(task2_test_start_date, task2_test_end_date), true);
			task2_test_end_date.set(2014, Calendar.NOVEMBER, 27, 14, 30, 00);
			test(task2.getTaskDatesTimes().withinDateRange(task2_test_start_date, task2_test_end_date), true);
			
			//Task 3: Overdue Task
			Task task3 = new Task();
			task3.setTaskName("100 pushups");
			Calendar task3_date = Calendar.getInstance();
			task3_date.set(2014, Calendar.OCTOBER, 8, 10, 00, 00);
			task3.setTaskDatesTimes(task3_date);
			task3.addTaskTags("exercise");
			
			test(task3.isOverdue(),true);
			
			//Task 4: Floating Task
			Task task4 = new Task();
			task4.setTaskName("Bake chocolate cake");
			task4.addTaskTags("baking");
			
			test(task4.isFloating(),true);
			
			//Task 5: Recurring Task
			
			Task task5 = new Task();
			task5.setTaskName("Casey's birthday");
			Calendar task5_start_date = Calendar.getInstance();
			task5_start_date.set(2014, Calendar.SEPTEMBER, 29, 00, 00, 00);
			Calendar task5_end_date = Calendar.getInstance();
			task5_end_date.set(2014, Calendar.SEPTEMBER, 29, 23, 59, 59);
			Calendar task5_limit = Calendar.getInstance();
			task5_limit.set(2017, Calendar.SEPTEMBER, 30, 00, 00, 00);
			task5.setTaskDatesTimes(task5_start_date, task5_end_date, "year", task5_limit);
			task5.updateRecur();
			test(task5.getTaskDatesTimes().getDates().size(), 4);
			
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
