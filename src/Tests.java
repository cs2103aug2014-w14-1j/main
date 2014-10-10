import java.io.*;
import java.util.ArrayList;
import java.util.Calendar;

import org.json.JSONException;


public class Tests {

	public static void main(String[] args) {
		
		try {
			Storage storage = new Storage();
			
			
			//Test checkOverdue
			ArrayList<Task> overdue = storage.getOverdueTasksFile();
			/*
			for (Task task : overdue) {
				System.out.println(task.getTaskName());
			}
			*/
			
			test(overdue.size(), 2);
			
			storage.clearAll();
			
			
			
			Task task1 = new Task();
			task1.setTaskName("do 2103t tutorial");
			task1.addTaskDatesTimes(Calendar.getInstance());
			task1.addTaskTags("2103t");
			
			Task task2 = new Task();
			task2.setTaskName("complete search function");
			task2.addTaskDatesTimes(Calendar.getInstance());
			task2.addTaskTags("2103t");
			task2.addTaskTags("search");
			
			Task task3 = new Task();
			task3.setTaskName("ia ia");
			task3.addTaskTags("cthulhu");
			
			Task task4 = new Task();
			task4.setTaskName("Happy New Year!");
			Calendar calendar_newyear = Calendar.getInstance();
			calendar_newyear.set(2015, 1, 1, 0, 0, 0);
			task4.addTaskDatesTimes(calendar_newyear);
			task4.addTaskTags("2015");
			task4.addTaskTags("New Year");
			
			Task task6 = new Task();
			task6.setTaskName("ST2132 lecture");
			task6.addTaskTags("ST2132");
			Calendar tuesday_start = Calendar.getInstance();
			Calendar tuesday_end = Calendar.getInstance();
			Calendar friday_start = Calendar.getInstance();
			Calendar friday_end = Calendar.getInstance();
			tuesday_start.set(2014, Calendar.OCTOBER, 7, 8, 0, 0);
			tuesday_start.set(2014, Calendar.OCTOBER, 7, 10, 0, 0);
			friday_start.set(2014, Calendar.OCTOBER, 10, 8, 0, 0);
			friday_start.set(2014, Calendar.OCTOBER, 10, 10, 0, 0);
			task6.addTaskDatesTimes(new TaskDate(tuesday_start, tuesday_end));
			task6.addTaskDatesTimes(friday_start, friday_end);
			
			ArrayList<String> keywords1 = new ArrayList<String>();
			keywords1.add("complete");
			ArrayList<String> keywords2 = new ArrayList<String>();
			keywords2.add("ia");
			
			ArrayList<String> tags1 = new ArrayList<String>();
			tags1.add("2103t");
			ArrayList<String> tags2 = new ArrayList<String>();
			tags2.add("2015");
			
			//Search tests*****************************************************************
			ArrayList<String> empty_keywords = new ArrayList<String>();
			ArrayList<String> empty_tags = new ArrayList<String>();
			
			ArrayList<Task> search;
			
			try {
				storage.insert(task1);
				storage.insert(task2);
				storage.insert(task3);
				storage.insert(task4);
			}
			catch (JSONException e) {
				e.printStackTrace();
			}
			
			//search 1 keyword
			search = storage.search(keywords1, empty_tags, null, null);
			test("complete search function", search.get(0).getTaskName());
			test(1, search.size());
			
			//search 1 tag
			search = storage.search(empty_keywords, tags1, null, null);
			test("do 2103t tutorial\ncomplete search function",
					search.get(0).getTaskName() + "\n" + search.get(1).getTaskName());
			test(2, search.size());
			
			//search keyword found in multiple tasks
			search = storage.search(keywords2, empty_tags, null, null);
			test("do 2103t tutorial\nia ia",
					search.get(0).getTaskName() + "\n" + search.get(1).getTaskName());
			test(2,search.size());
			
			//search 2+ keywords
			keywords2.add("tutorial");
			search = storage.search(keywords2, empty_tags, null, null);
			test("do 2103t tutorial",
					search.get(0).getTaskName());
			test(1, search.size());
			
			//search 2+ tags
			
			//search keywords and tags together
			keywords2.remove(1);
			search = storage.search(keywords2, tags1, null, null);
			test("do 2103t tutorial",
					search.get(0).getTaskName());
			test(1, search.size());
			
			//search date
			Calendar start_date = Calendar.getInstance();
			start_date.set(2014, Calendar.OCTOBER, 6, 0, 00, 00);
			Calendar end_date = Calendar.getInstance();
			end_date.set(2015, Calendar.NOVEMBER, 1, 00, 00);
			
			//search date with tags
			search = storage.search(empty_keywords, tags2, start_date, end_date);
			test("Happy New Year!",
					search.get(0).getTaskName());
			test(1, search.size());
			
			//search for something that is outside date
			start_date.set(Calendar.YEAR, 2013);
			end_date.set(Calendar.YEAR, 2013);
			search = storage.search(empty_keywords, tags2, start_date, end_date);
			test(0, search.size());
			
			//search for something outside date
			
			//add something and search
			
			//delete something and search
			
		}
		
		catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	private static boolean checkEquals(String a, String b) {
		return a.equals(b);
	}
	
	private static boolean checkEquals(int a, int b) {
		return a == b;
	}
	
	private static void test(String a, String b) {
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
}
