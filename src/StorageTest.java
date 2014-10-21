import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Calendar;


public class StorageTest {

	public static void main(String[] args) {
		
		try {
			
			//Task tests**********************************************************
			
			//Task 1: Normal task
			System.out.println("Task 1");
			Task task1 = new Task();
			task1.setTaskName("CS2103T finals");
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
			
			test(task1.getTaskName(), "CS2103T finals");
			test(task1.withinDateRange(task1_test_start_date, task1_test_end_date), true);
			test(task1.getTaskTags().size(), 3);
			test(task1.getTaskTags().get(0), "school");
			test(task1.getTaskTags().get(1), "CS2103T");
			test(task1.getTaskTags().get(2), "exams");
			test(task1.getTaskDatesSorted().size(), 1);
			test(task1.getTaskDatesSorted().getFirst(), task1_date.getTime().toString());
			test(task1.withinDateRange(null, null), true);
			
			//Task 2: Task with interval
			System.out.println("Task 2");
			Task task2 = new Task();
			task2.setTaskName("MA3110 finals");
			Calendar task2_start_date = Calendar.getInstance();
			task2_start_date.set(2014, Calendar.NOVEMBER, 27, 13, 00, 00);
			Calendar task2_end_date = Calendar.getInstance();
			task2_end_date.set(2014, Calendar.NOVEMBER, 27, 15, 00, 00);
			task2.addTaskDatesTimes(task2_start_date, task2_end_date);
			task2.addTaskTags("school");
			task2.addTaskTags("MA3110");
			task2.addTaskTags("exams");
			
			Calendar task2_test_start_date = Calendar.getInstance();
			task2_test_start_date.set(2014, Calendar.NOVEMBER, 24, 12, 59, 59);
			Calendar task2_test_end_date = Calendar.getInstance();
			task2_test_end_date.set(2014, Calendar.NOVEMBER, 28, 13, 00, 01);
			
			test(task2.withinDateRange(task2_test_start_date, task2_test_end_date), true);
			
			task2_test_start_date.set(2014, Calendar.NOVEMBER, 27, 13, 30, 00);
			test(task2.withinDateRange(task2_test_start_date, task2_test_end_date), true);
			task2_test_end_date.set(2014, Calendar.NOVEMBER, 27, 14, 30, 00);
			test(task2.withinDateRange(task2_test_start_date, task2_test_end_date), true);
			
			test(task2.getTaskDatesSorted().size(), 1);
			test(task2.getTaskDatesSorted().getFirst(), task2_start_date.getTime().toString()
					+ " - " + task2_end_date.getTime().toString());
			
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
			
			//Task 5: Recurring Task (YEAR)
			
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
			test(task5.getTaskDatesSorted().size(), 4);
			
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
			
			//Visual check to see if dates are sorted correctly
			
			/*
			LinkedList<String> task1_sorted_dates = task1.getTaskDatesSorted();
			for (String date : task1_sorted_dates) {
				System.out.println(date);
			}
			
			LinkedList<String> task9_sorted_dates = task9.getTaskDatesSorted();
			for (String date : task9_sorted_dates) {
				System.out.println(date);
			}
			*/
			
			
			//completed tasks
			
			//Storage tests*****************************************************
			
			Storage storage = new Storage();
			storage.clearAll();
			
			//testing insertion
			System.out.println("Insertion");
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
			
			//testing deletion
			System.out.println("Deletion");
			
			storage.delete(task3);
			test(storage.getOverdueTasksFile().size(), 0);
			
			//deleting from empty list/non-existent task
			storage.delete(task3);
			test(storage.getOverdueTasksFile().size(), 0);
			
			//testing reinsert after modification. Also tests setting taskDateCompleted
			task3.setTaskCompleted(Calendar.getInstance());		//warning, old date has been purged
			storage.insert(task3);
			test(storage.getCompletedTasksFile().size(), 1);
			storage.delete(task3);
			test(storage.getCompletedTasksFile().size(), 0);
			task3.setTaskCompleted(null);						//this has now become a floating task
			storage.insert(task3);
			test(storage.getFloatingTasksFile().size(), 2);
			storage.delete(task3);
			test(storage.getFloatingTasksFile().get(0).getTaskName(), "Bake chocolate cake");
			task3.addTaskDatesTimes(task3_date);
			storage.insert(task3);
			
			//testing search
			
			//search with no date
			System.out.println("Search");
			
			ArrayList<String> search1_keywords = new ArrayList<String>();
			search1_keywords.add("Casey");
			ArrayList<Task> search1 = storage.search(search1_keywords, null, null, null);
			test(search1.size(), 1);
			test(search1.get(0).getTaskName(), "Casey's birthday");
			
			//search no date, multiple keywords, from multiple lists
			ArrayList<String> search2_keywords = new ArrayList<String>();
			search2_keywords.add("1");
			search2_keywords.add("2");
			ArrayList<Task> search2 = storage.search(search2_keywords, null, null, null);
			test(search2.size(), 2);
			test(search2.get(0).getTaskName(), "CS2103T finals");
			test(search2.get(1).getTaskName(), "ST2132 Lecture");
			search2_keywords.remove(1);
			search2 = storage.search(search2_keywords, null, null, null);
			test(search2.size(), 6);
			test(search2.get(0).getTaskName(), "100 pushups");
			test(search2.get(1).getTaskName(), "CS2103T finals");
			test(search2.get(2).getTaskName(), "MA3110 finals");
			test(search2.get(3).getTaskName(), "1st of month");
			test(search2.get(4).getTaskName(), "Go to sleep by 11");
			test(search2.get(5).getTaskName(), "ST2132 Lecture");
			
			//now search tags
			ArrayList<String> search2_tags = new ArrayList<String>();
			search2_tags.add("school");
			search2 = storage.search(search2_keywords, search2_tags, null, null);
			test(search2.size(), 3);
			test(search2.get(0).getTaskName(), "CS2103T finals");
			
			//search date
			Calendar search3_start = Calendar.getInstance();
			search3_start.set(2014, Calendar.OCTOBER, 1, 00, 00, 00);
			Calendar search3_end = Calendar.getInstance();
			search3_end.set(2014, Calendar.NOVEMBER, 1, 00, 00, 00);
			ArrayList<Task> search3 = storage.search(null, null, search3_start, search3_end);
			test(search3.size(), 5);
			test(search3.get(0).getTaskName(), "100 pushups");
			test(search3.get(1).getTaskName(), "Bake chocolate cake");
			
			//search with all fields. Expect nothing
			Calendar search4_start = Calendar.getInstance();
			search4_start.set(2014, Calendar.DECEMBER, 1, 00, 00, 00);
			Calendar search4_end = Calendar.getInstance();
			search4_end.set(2015, Calendar.JANUARY, 1, 00, 00, 00);
			ArrayList<String> search4_keywords = new ArrayList<String>();
			search4_keywords.add("finals");
			ArrayList<String> search4_tags = new ArrayList<String>();
			search4_tags.add("CS2103T");
			ArrayList<Task> search4 = storage.search(search4_keywords, search4_tags, search4_start, search4_end);
			test(search4.size(),0);
			
			//delete, then search
			//then insert, search
			ArrayList<String> search5_keywords = new ArrayList<String>();
			search5_keywords.add("birthday");
			storage.delete(task5);
			ArrayList<Task> search5 = storage.search(search5_keywords, null, null, null);
			test(search5.size(), 0);
			storage.insert(task5);
			search5 = storage.search(search5_keywords, null, null, null);
			test(search5.size(), 1);
			
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
}
