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
			for (Task task : overdue) {
				System.out.println(task.getTaskName());
			}
			
			storage.clearAll();
			
			System.out.println(overdue.size() == 0);
			
			//Search tests*****************************************************************
			ArrayList<String> empty_keywords = new ArrayList<String>();
			ArrayList<String> empty_tags = new ArrayList<String>();
			
			
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
			task4.setTaskName("ST2132 lecture");
			task4.addTaskTags("ST2132");
			Calendar tuesday = Calendar.getInstance();
			Calendar friday = Calendar.getInstance();
			tuesday.set(2014, Calendar.OCTOBER, 7, 8, 0, 0);
			friday.set(2014, Calendar.OCTOBER, 10, 8, 0, 0);
			task4.addTaskDatesTimes(tuesday);
			task4.addTaskDatesTimes(friday);
			
			ArrayList<String> keywords1 = new ArrayList<String>();
			keywords1.add("complete");
			ArrayList<String> keywords2 = new ArrayList<String>();
			keywords2.add("ia");
			
			ArrayList<String> tags = new ArrayList<String>();
			tags.add("2103t");
			
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
			ArrayList<Task> search1 = storage.search(keywords1, empty_tags, null, null);
			System.out.println(checkEquals("complete search function", search1.get(0).getTaskName()));
			System.out.println(checkEquals(1, search1.size()));
			
			//search 1 tag
			ArrayList<Task> search2 = storage.search(empty_keywords, tags, null, null);
			System.out.println(checkEquals("do 2103t tutorial\ncomplete search function",
					search2.get(0).getTaskName() + "\n" + search2.get(1).getTaskName()));
			System.out.println(checkEquals(2, search2.size()));
			
			//search keyword found in multiple tasks
			ArrayList<Task> search3 = storage.search(keywords2, empty_tags, null, null);
			System.out.println(checkEquals("do 2103t tutorial\nia ia",
					search3.get(0).getTaskName() + "\n" + search3.get(1).getTaskName()));
			System.out.println(checkEquals(2,search3.size()));
			
			//search 2+ keywords
			keywords2.add("tutorial");
			ArrayList<Task> search4 = storage.search(keywords2, empty_tags, null, null);
			System.out.println(checkEquals("do 2103t tutorial",
					search4.get(0).getTaskName()));
			System.out.println(checkEquals(1, search4.size()));
			
			//search keywords and tags together
			keywords2.remove(1);
			ArrayList<Task> search5 = storage.search(keywords2, tags, null, null);
			System.out.println(checkEquals("do 2103t tutorial",
					search5.get(0).getTaskName()));
			System.out.println(checkEquals(1, search5.size()));
			
			//search date
			Calendar start_date = Calendar.getInstance();
			start_date.set(2014, Calendar.OCTOBER, 6, 0, 00, 00);
			Calendar end_date = Calendar.getInstance();
			end_date.set(2014, Calendar.NOVEMBER, 1, 00, 00);
			
			//search date and tag
			tags.remove(0);
			tags.add("ST2132");
			ArrayList<Task> search7 = storage.search(empty_keywords, tags, start_date, end_date);
			System.out.println(checkEquals("ST2132 lecture",
					search7.get(0).getTaskName()));
			System.out.println(checkEquals(1, search7.size()));
			
			//First part of test overdue
			Task overduetask = new Task();
			Calendar overduedate = Calendar.getInstance();
			overduedate.set(1991, 3, 1);
			overduetask.addTaskDatesTimes(overduedate);
			overduetask.setTaskName("ohnoes");
			try {
				storage.insert(overduetask);
			}
			catch (JSONException e) {
			}
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

}
