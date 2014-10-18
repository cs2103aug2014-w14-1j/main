

public class TaskIDPair implements Comparable<TaskIDPair> {
	private Task task_;
	private	String ID_;
	
	public TaskIDPair(Task task, String ID) {
		task_ = task;
		ID_ = ID;
	}
	
	public Task getTask() {
		return task_;
	}
	
	public String getDisplayID() {
		return ID_;
	}
	
	@Override
	public int compareTo(TaskIDPair o) {
		return this.getDisplayID().compareTo(o.getDisplayID());
	}
}
