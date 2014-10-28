import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Calendar;
import org.junit.Test;

public class StorageJUnitTest {

	@Test
	public void test() {
		try {

			// Task tests**********************************************************
			
			/*
			 * Most of these are meant to test the date methods. All other Task
			 * fields are independent, therefore only checked once or twice to
			 * reduce the number of combinations required.
			 * 
			 * In addition, these tasks will be reused for later tests
			 * 
			 * WARNING: Due to time sensitivity of overdue tasks, it is assumed
			 * these tests are not used by 2015. Also all recurring tasks will
			 * only contribute to the normal tasks file (no overdue). However
			 * since retrieveTaskFile() is independent of recurrence generation,
			 * it is assumed that the result is the same for overdue tasks.
			*/
			
			System.out.println("Task tests");
			
			// Task 1: Normal task
			// Also checks methods related to taskName, keywords, tags are working
			// Assumes 1 date
			Task task1 = new Task();
			task1.setTaskName("Submit Developer Guide");
			Calendar task1_date = Calendar.getInstance();
			task1_date.set(2014, Calendar.NOVEMBER, 26, 13, 00, 00);
			task1.setDate(task1_date);
			task1.addTag("school");
			task1.addTag("CS2101");
			task1.addTag("CS2103T");

			Calendar task1_test_start_date = Calendar.getInstance();
			task1_test_start_date.set(2014, Calendar.NOVEMBER, 26, 12, 59, 59);
			Calendar task1_test_end_date = Calendar.getInstance();
			task1_test_end_date.set(2014, Calendar.NOVEMBER, 26, 13, 00, 01);

			test(task1.getTaskName(), "Submit Developer Guide");
			test(task1.getTags().size(), 3);
			test(task1.getTags().get(0), "school");
			test(task1.getTags().get(1), "CS2101");
			test(task1.getTags().get(2), "CS2103T");
			test(task1.getStartDate(), task1_date);
			test(task1.getEndDate(), task1_date);
			test(task1.getDate(), task1_date);
			test(task1.withinDateRange(task1_test_start_date,
					task1_test_end_date), true);
			task1_test_start_date.set(2014, Calendar.NOVEMBER, 26, 13, 00, 00);
			test(task1.withinDateRange(task1_test_start_date,
					task1_test_end_date), true);
			test(task1.withinDateRange(null, null), true);

			// Task 2: Task with interval
			Task task2 = new Task();
			task2.setTaskName("MA3110 finals");
			Calendar task2_start_date = Calendar.getInstance();
			task2_start_date.set(2014, Calendar.NOVEMBER, 27, 13, 00, 00);
			Calendar task2_end_date = Calendar.getInstance();
			task2_end_date.set(2014, Calendar.NOVEMBER, 27, 15, 00, 00);
			task2.setDates(task2_start_date, task2_end_date);
			task2.addTag("school");
			task2.addTag("MA3110");
			task2.addTag("exams");

			Calendar task2_test_start_date = Calendar.getInstance();
			task2_test_start_date.set(2014, Calendar.NOVEMBER, 24, 12, 59, 59);
			Calendar task2_test_end_date = Calendar.getInstance();
			task2_test_end_date.set(2014, Calendar.NOVEMBER, 28, 13, 00, 01);
			
			test(task2.getStartDate(), task2_start_date);
			
			test(task2.withinDateRange(task2_test_start_date,
					task2_test_end_date), true);
			task2_test_start_date.set(2014, Calendar.NOVEMBER, 27, 13, 30, 00);
			test(task2.withinDateRange(task2_test_start_date,
					task2_test_end_date), true);
			task2_test_end_date.set(2014, Calendar.NOVEMBER, 27, 14, 30, 00);
			test(task2.withinDateRange(task2_test_start_date,
					task2_test_end_date), true);
			test(task2.isFloating(), false);

			// Task 3: Overdue Task
			// Note: Overdue check generates an instance of the current date. Test
			// may therefore be sensitive to when it was conducted
			Task task3 = new Task();
			task3.setTaskName("100 pushups");
			Calendar task3_date = Calendar.getInstance();
			task3_date.set(2014, Calendar.OCTOBER, 8, 10, 00, 00);
			task3.setDate(task3_date);
			task3.addTag("exercise");

			test(task3.isOverdue(), true);

			// Task 4: Floating Task
			Task task4 = new Task();
			task4.setTaskName("Bake chocolate cake");
			task4.addTag("baking");
			
			//this is gibberish input and should not affect further tests, until a date is set
			task4.setRecur(Calendar.YEAR);
			Calendar task4_recur_limit = Calendar.getInstance();
			task4_recur_limit.set(2016, Calendar.JANUARY, 1, 0, 0, 0);
			task4.setRecurLimit(task4_recur_limit);

			test(task4.getStartDate(), null);
			test(task4.getEndDate(), null);
			test(task4.isRecur(), false);
			test(task4.isFloating(), true);
			
			/*
			 * No tests here. All recurring is only activated during Storage insertion
			 * Due to sensitivity of Calendar fields, tests will check all
			 * fields that can be recurred (YEAR, MONTH, WEEK, DAY)
			 */

			// Task 5: Recurring Task (YEAR)
			// no recur limit: Storage uses its own (TIME SENSITIVE)
			Task task5 = new Task();
			task5.setTaskName("Casey's birthday");
			task5.addTag("Casey");
			task5.addTag("birthdays");
			Calendar task5_start_date = Calendar.getInstance();
			task5_start_date.set(2014, Calendar.SEPTEMBER, 29, 00, 00, 00);
			Calendar task5_end_date = Calendar.getInstance();
			task5_end_date.set(2014, Calendar.SEPTEMBER, 29, 23, 59, 59);
			Calendar task5_completed = Calendar.getInstance();
			task5_completed.set(2014, Calendar.OCTOBER, 21, 00, 00, 00);
			task5.setDates(task5_start_date, task5_end_date);
			task5.setRecur(Calendar.YEAR);
			task5.setDateCompleted(task5_completed);

			// Task 6: Recurring Task (MONTH)
			//contains a limit
			Task task6 = new Task();
			task6.setTaskName("Get pay");
			Calendar task6_date = Calendar.getInstance();
			task6_date.set(2016, Calendar.JANUARY, 1, 00, 00, 00);
			Calendar task6_limit = Calendar.getInstance();
			task6_limit.set(2017, Calendar.DECEMBER, 30, 00, 00, 00);
			task6.setDate(task6_date);
			task6.setRecur(Calendar.MONTH);
			task6.setRecurLimit(task6_limit);

			// Task 7: Recurring Task (WEEK)

			Task task7 = new Task();
			task7.setTaskName("Go jogging");
			task7.addTag("exercise");
			Calendar task7_date = Calendar.getInstance();
			task7_date.set(2015, Calendar.OCTOBER, 5, 8, 00, 00);
			Calendar task7_limit = Calendar.getInstance();
			task7_limit.set(2016, Calendar.JANUARY, 30, 00, 00, 00);
			task7.setDate(task7_date);
			task7.setRecur(Calendar.WEEK_OF_YEAR);
			task7.setRecurLimit(task7_limit);

			// Task 8: Recurring Task (DAY)
			Task task8 = new Task();
			task8.setTaskName("Go to sleep by 11");
			Calendar task8_date = Calendar.getInstance();
			task8_date.set(2015, Calendar.NOVEMBER, 1, 23, 00, 00);
			Calendar task8_limit = Calendar.getInstance();
			task8_limit.set(2015, Calendar.DECEMBER, 1, 23, 00, 01);
			task8.setDate(task8_date);
			task8.setRecur(Calendar.DAY_OF_YEAR);
			task8.setRecurLimit(task8_limit);

			// Visual check to see if dates are sorted correctly

			/*
			LinkedList<String> task1_sorted_dates = task1.getTaskDatesSorted();
			for (String date : task1_sorted_dates) {
				System.out.println(date);
			}
			*/

			// completed tasks
			
			task3.setDateCompleted(Calendar.getInstance());
			test(task3.isCompleted(), true);
			test(task3.isOverdue(), false);
			test(task3.isFloating(), false);
			task3.setDateCompleted(null);
			test(task3.isCompleted(), false);
			test(task3.isOverdue(), true);
			
			task4.setDateCompleted(Calendar.getInstance());
			test(task4.isFloating(), false);
			test(task4.isCompleted(), true);
			task4.setDateCompleted(null);
			
			
			// TaskComparator test
			
			

			// Storage
			// tests*********************************************************************

			Storage storage = new Storage();
			storage.clearAll();

			// Insertion tests************************************
			// Implicitly tests retrieveTaskFile()
			System.out.println("Insertion tests");
			
			storage.insert(task1);
			test(storage.getTasksList().size(), 1);
			storage.insert(task2);

			// insert overdue task
			storage.insert(task3);
			test(storage.getOverdueTasksList().size(), 1);

			// insert floating task
			storage.insert(task4);
			test(storage.getFloatingTasksList().size(), 1);

			// insert recurring tasks. Storage should automatically generate
			// all recurring instances according to their or the default limit
			storage.insert(task5);
			test(storage.getCompletedTasksList().size(), 1);
			test(storage.getCompletedTasksList().get(0).getTaskName(), "Casey's birthday");
			test(storage.getCompletedTasksList().get(0).getDateAsString(), "Mon 29-09-14 00:00 - Mon 29-09-14 23:59");
			test(storage.getTasksList().size(), 5);
			test(storage.getTasksList().get(2).getTaskName(), "Casey's birthday");
			test(storage.getTasksList().get(2).getDateAsString(), "Tue 29-09-15 00:00 - Tue 29-09-15 23:59");
			test(storage.getTasksList().get(2).getId(), storage.getCompletedTasksList().get(0).getId());
			test(storage.getTasksList().get(3).getTaskName(), "Casey's birthday");
			test(storage.getTasksList().get(3).getDateAsString(), "Thu 29-09-16 00:00 - Thu 29-09-16 23:59");
			test(storage.getTasksList().get(4).getTaskName(), "Casey's birthday");
			test(storage.getTasksList().get(4).getDateAsString(), "Fri 29-09-17 00:00 - Fri 29-09-17 23:59");
			storage.insert(task6);
			test(storage.getTasksList().size(), 29);
			storage.insert(task7);
			test(storage.getTasksList().size(), 46);
			storage.insert(task8);
			test(storage.getTasksList().size(), 77);

			// Deletion tests**********************************
			System.out.println("Deletion tests");

			storage.delete(task3);
			test(storage.getOverdueTasksList().size(), 0);

			// deleting from empty list/non-existent task
			storage.delete(task3);
			test(storage.getOverdueTasksList().size(), 0);

			// testing reinsertion after modification
			// ***This is how the controller should modify task: delete, modify, then reinsert
			
			task3.setDates(null, null);
			storage.insert(task3);
			test(storage.getFloatingTasksList().size(), 2);
			storage.delete(task3);
			test(storage.getFloatingTasksList().size(), 1);
			
			task3.setDateCompleted(Calendar.getInstance());
			storage.insert(task3);
			test(storage.getCompletedTasksList().size(), 2);
			storage.delete(task3);
			task3.setDateCompleted(null);
			storage.insert(task3);
			test(storage.getFloatingTasksList().size(), 2);
			storage.delete(task3);
			task3.setDate(task3_date);
			storage.insert(task3);
			test(task3.isOverdue(), true);
			
			//delete recurring task
			storage.delete(task8);
			test(storage.getTasksList().size(), 46);
			storage.insert(task8);
			test(storage.getTasksList().size(), 77);

			// Search tests********************************
			
			// Note: search does not check completed tasks
			
			System.out.println("Search tests");
			
			// search with no date

			ArrayList<String> search1_keywords = new ArrayList<String>();
			search1_keywords.add("Casey");
			ArrayList<Task> search1 = storage.search(search1_keywords, null,
					null, null);
			test(search1.size(), 3);
			test(search1.get(0).getTaskName(), "Casey's birthday");
			test(search1.get(1).getTaskName(), "Casey's birthday");
			test(search1.get(2).getTaskName(), "Casey's birthday");

			// search no date, multiple keywords, from multiple lists
			// checks the order of the tasks (overdue, floating, normal)
			ArrayList<String> search2_keywords = new ArrayList<String>();
			search2_keywords.add("1");
			search2_keywords.add("0");
			ArrayList<Task> search2 = storage.search(search2_keywords, null,
					null, null);
			test(search2.size(), 2);
			test(search2.get(0).getTaskName(), "100 pushups");
			test(search2.get(1).getTaskName(), "MA3110 finals");

			// search tags in comination with keywords
			ArrayList<String> search2_tags = new ArrayList<String>();
			search2_tags.add("school");
			search2 = storage
					.search(search2_keywords, search2_tags, null, null);
			test(search2.size(), 1);
			test(search2.get(0).getTaskName(), "MA3110 finals");

			// search date
			Calendar search3_start = Calendar.getInstance();
			search3_start.set(2014, Calendar.OCTOBER, 1, 00, 00, 00);
			Calendar search3_end = Calendar.getInstance();
			search3_end.set(2015, Calendar.OCTOBER, 1, 00, 00, 00);
			ArrayList<Task> search3 = storage.search(null, null, search3_start,
					search3_end);
			test(search3.size(), 5);
			test(search3.get(0).getTaskName(), "Bake chocolate cake");		//can find floating through date
			test(search3.get(1).getTaskName(), "100 pushups");				//can find overdue through date
			test(search3.get(4).getTaskName(), "Casey's birthday");
			test(search3.get(4).getDateAsString(), "Tue 29-09-15 00:00 - Tue 29-09-15 23:59");
			
			

			// search parameters that should not find any task
			Calendar search4_start = Calendar.getInstance();
			search4_start.set(2014, Calendar.DECEMBER, 1, 00, 00, 00);
			Calendar search4_end = Calendar.getInstance();
			search4_end.set(2015, Calendar.JANUARY, 1, 00, 00, 00);
			ArrayList<String> search4_keywords = new ArrayList<String>();
			search4_keywords.add("finals");
			ArrayList<String> search4_tags = new ArrayList<String>();
			search4_tags.add("CS2103T");
			ArrayList<Task> search4 = storage.search(search4_keywords,
					search4_tags, search4_start, search4_end);
			test(search4.size(), 0);

			// delete, then search
			// then insert, search
			ArrayList<String> search5_keywords = new ArrayList<String>();
			search5_keywords.add("birthday");
			storage.delete(task5);
			ArrayList<Task> search5 = storage.search(search5_keywords, null,
					null, null);
			test(search5.size(), 0);
			storage.insert(task5);
			search5 = storage.search(search5_keywords, null, null, null);
			test(search5.size(), 3);

			/*
			 * //insert an already existing task storage.insert(task1);
			 * test(storage.getTasksFile().size(), 7);
			 */
			System.out.println("All tests successful");
		}
		
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void test(String a, String b) {
		assertEquals(b, a);
	}

	public void test(boolean a, boolean b) {
		assertEquals(b, a);
	}

	private void test(int a, int b) {
		assertEquals(b, a);
	}
	
	private void test(Calendar a, Calendar b) {
		assertEquals(b, a);
	}
}
