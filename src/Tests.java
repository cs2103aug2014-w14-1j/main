import java.util.ArrayList;
import java.util.Calendar;


public class Tests {

	public static void main(String[] args) {
		
		try {
			Storage storage = new Storage();
			
			//Task tests**********************************************************
			
			//Task 1: Normal task
			System.out.println("Task 1");
			Task task1 = new Task();
			task1.setTaskName("Task 1: CS2103T finals");
			Calendar task1_date = Calendar.getInstance();
			task1_date.set(2014, Calendar.NOVEMBER, 26, 13, 00, 00);
			task1.addTaskDatesTimes(task1_date);
			task1.addTaskTags("school");
			task1.addTaskTags("CS2103T");
			task1.addTaskTags("exams");
			
			Calendar task1_test_start_date = Calendar.getInstance();
			task1_test_start_date.set(2014, Calendar.NOVEMBER, 26, 12, 59, 59);
			Calendar task1_test_end_date = Calendar.getInstance();
			task1_test_end_date.set(2014, Calendar.NOVEMBER, 26, 13, 00, 01);
			
			test(task1.getTaskName(), "Task 1: CS2103T finals");
			test(task1.getTaskStartDateTime(), task1_date);
			test(task1.getTaskEndDateTime(), task1_date);
			test(task1.withinDateRange(task1_test_start_date, task1_test_end_date), true);
			test(task1.getTaskTags().size(), 3);
			test(task1.getTaskTags().get(0), "school");
			test(task1.getTaskTags().get(1), "CS2103T");
			test(task1.getTaskTags().get(2), "exams");
			
			//Task 2: Task with interval
			System.out.println("Task 2");
			Task task2 = new Task();
			task2.setTaskName("Task 2: MA3110 Finals");
			Calendar task2_start_date = Calendar.getInstance();
			task2_start_date.set(2014, Calendar.NOVEMBER, 27, 13, 00, 00);
			Calendar task2_end_date = Calendar.getInstance();
			task2_end_date.set(2014, Calendar.NOVEMBER, 27, 15, 00, 00);
			task2.addTaskDatesTimes(task2_start_date, task2_end_date);
			task1.addTaskTags("school");
			task1.addTaskTags("MA3110");
			task1.addTaskTags("exams");
			
			Calendar task2_test_start_date = Calendar.getInstance();
			task2_test_start_date.set(2014, Calendar.NOVEMBER, 24, 12, 59, 59);
			Calendar task2_test_end_date = Calendar.getInstance();
			task2_test_end_date.set(2014, Calendar.NOVEMBER, 28, 13, 00, 01);
			
			test(task2.getTaskStartDateTime(), task2_start_date);
			test(task2.getTaskEndDateTime(), task2_end_date);
			test(task2.withinDateRange(task2_test_start_date, task2_test_end_date), true);
			
			task2_test_start_date.set(2014, Calendar.NOVEMBER, 27, 13, 30, 00);
			test(task2.withinDateRange(task2_test_start_date, task2_test_end_date), true);
			task2_test_end_date.set(2014, Calendar.NOVEMBER, 27, 14, 30, 00);
			test(task2.withinDateRange(task2_test_start_date, task2_test_end_date), true);
			
			//Task 3: Overdue Task
			System.out.println("Task 3");
			Task task3 = new Task();
			task3.setTaskName("100 pushups");
			Calendar task3_date = Calendar.getInstance();
			task3_date.set(2014, Calendar.OCTOBER, 8, 10, 00, 00);
			task3.addTaskDatesTimes(task3_date);
			task3.addTaskTags("exercise");
			
			test(task3.isOverdue(),true);
			
			System.out.println("Task 4");
			//Task 4: Floating Task
			Task task4 = new Task();
			task4.setTaskName("Bake chocolate cake");
			task4.addTaskTags("baking");
			
			test(task4.isFloating(),true);
			
			//Task 5: Recurring Task (YEAR) + Date completed
			
			System.out.println("Task 5");
			Task task5 = new Task();
			task5.setTaskName("Casey's birthday");
			task5.addTaskTags("Casey");
			task5.addTaskTags("birthdays");
			Calendar task5_start_date = Calendar.getInstance();
			task5_start_date.set(2014, Calendar.SEPTEMBER, 29, 00, 00, 00);
			Calendar task5_end_date = Calendar.getInstance();
			task5_end_date.set(2014, Calendar.SEPTEMBER, 29, 23, 59, 59);
			Calendar task5_limit = Calendar.getInstance();
			task5_limit.set(2017, Calendar.SEPTEMBER, 30, 00, 00, 00);
			task5.addTaskDatesTimes(task5_start_date, task5_end_date, "year", task5_limit);
			Calendar task5_completed = Calendar.getInstance();
			task5.setTaskCompleted(task5_completed);
			task5.updateRecur();		//this is a redundant check, it should auto-update
			test(task5.getTaskDateTime(0).size(), 3);
			
			//Task 6: Recurring Task (MONTH)
			
			System.out.println("Task 6");
			Task task6 = new Task();
			task6.setTaskName("1st of month");
			Calendar task6_date = Calendar.getInstance();
			task6_date.set(2014, Calendar.JANUARY, 1, 00, 00, 00);
			Calendar task6_limit = Calendar.getInstance();
			task6_limit.set(2015, Calendar.DECEMBER, 30, 00, 00, 00);
			task6.addTaskDatesTimes(task6_date, "month", task6_limit);
			test(task6.getTaskDateTime(0).size(), 24);
			
			
			//Task 7: Recurring Task (WEEK)
			
			System.out.println("Task 7");
			Task task7 = new Task();
			task7.setTaskName("Go jogging");
			task7.addTaskTags("exercise");
			Calendar task7_date = Calendar.getInstance();
			task7_date.set(2014, Calendar.OCTOBER, 5, 00, 00, 00);
			Calendar task7_limit = Calendar.getInstance();
			task7_limit.set(2015, Calendar.JANUARY, 30, 00, 00, 00);
			task7.addTaskDatesTimes(task7_date, "week", task7_limit);
			test(task7.getTaskDateTime(0).size(), 17);
			
			//Task 8: Recurring Task (DAY)
			System.out.println("Task 8");
			Task task8 = new Task();
			task8.setTaskName("Go to sleep by 11");
			Calendar task8_date = Calendar.getInstance();
			task8_date.set(2014, Calendar.NOVEMBER, 1, 23, 00, 00);
			Calendar task8_limit = Calendar.getInstance();
			task8_limit.set(2014, Calendar.DECEMBER, 1, 23, 00, 01);
			task8.addTaskDatesTimes(task8_date, "day", task8_limit);
			test(task8.getTaskDateTime(0).size(), 31);
			
			//Task 9: Task with two different dates (RECURRING)
			System.out.println("Task 9");
			Task task9 = new Task();
			task9.setTaskName("ST2132 Lecture");
			task9.addTaskTags("school");
			task9.addTaskTags("ST2132");
			Calendar task9_tue_date1 = Calendar.getInstance();
			task9_tue_date1.set(2014, Calendar.AUGUST, 12, 8, 00, 00);
			Calendar task9_tue_date2 = Calendar.getInstance();
			task9_tue_date2.set(2014, Calendar.AUGUST, 12, 10, 00, 00);
			Calendar task9_fri_date1 = Calendar.getInstance();
			task9_fri_date1.set(2014, Calendar.AUGUST, 15, 8, 00, 00);
			Calendar task9_fri_date2 = Calendar.getInstance();
			task9_fri_date2.set(2014, Calendar.AUGUST, 15, 10, 00, 00);
			Calendar task9_limit = Calendar.getInstance();
			task9_limit.set(2014, Calendar.NOVEMBER, 15, 00, 00, 00);
			task9.addTaskDatesTimes(task9_tue_date1, task9_tue_date2, "week", task9_limit);
			task9.addTaskDatesTimes(task9_fri_date1, task9_fri_date2, "week", task9_limit);
			test(task9.getTaskDateTime(0).size(), 14);
			test(task9.getTaskDateTime(1).size(), 14);
			
			
			//Storage tests*****************************************************
			
			storage.clearAll();
			
			//testing insertion
			storage.insert(task1);
			test(storage.getTasksFile().size(), 1);
			
			storage.insert(task2);
			test(storage.getTasksFile().size(), 2);
			
			//insert overdue task
			storage.insert(task3);
			test(storage.getOverdueTasksFile().size(), 1);
			
			//insert floating task
			storage.insert(task4);
			test(storage.getFloatingTasksFile().size(), 1);
			
			//insert recurring task
			storage.insert(task5);
			test(storage.getTasksFile().size(), 3);
			
			storage.insert(task6);
			test(storage.getTasksFile().size(), 4);
			
			storage.insert(task7);
			test(storage.getTasksFile().size(), 5);
			
			storage.insert(task8);
			test(storage.getTasksFile().size(), 6);
			
			storage.insert(task9);
			test(storage.getTasksFile().size(), 7);
			
			/*
			//insert an already existing task
			storage.insert(task1);
			test(storage.getTasksFile().size(), 7);
			*/
			System.out.println("All tests successful");
		}
		
		catch (Exception e) {
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
			System.exit(1);
		}
	}
	
	private static void test(boolean a, boolean b) {
		if(!checkEquals(a, b)) {
			System.out.println("MISMATCH");
			System.out.println(a);
			System.out.println(b);
			System.exit(1);
		}
	}

	private static void test(int a, int b) {
		if(!checkEquals(a, b)) {
			System.out.println("MISMATCH");
			System.out.println(a);
			System.out.println(b);
			System.exit(1);
		}
	}
	
	private static void test(Calendar a, Calendar b) {
		if(!checkEquals(a, b)) {
			System.out.println("MISMATCH");
			System.out.println(a.getTime());
			System.out.println(b.getTime());
			System.exit(1);
		}
	}
}
