//@author A0097299E
package speed.task;

import java.util.Comparator;

 public class TaskComparator implements Comparator<Task> {
	 public int compare(Task a, Task b) {
		if (!a.isCompleted() && b.isCompleted()) {
			return -1;
		}
		if (a.isCompleted() && !b.isCompleted()) {
			return 1;
		}
		if (a.isOverdue() && !b.isOverdue()) {
			return -1;
		}
		if (!a.isOverdue() && b.isOverdue()) {
			return 1;
		}
		if (a.getStartDate() == null && b.getStartDate() == null) {
			return a.getTaskName().compareTo(b.getTaskName());
		}
		if (a.getStartDate() == null && b.getStartDate() != null) {
			return -1;
		}
		if (a.getStartDate() != null && b.getStartDate() == null) {
			return 1;
		}
		if (a.getStartDate().before(b.getStartDate())) {
			return -1;
		}
		if (a.getStartDate().equals(b.getStartDate())) {
			return a.getTaskName().compareTo(b.getTaskName());
		}
		return 1;
	}
}