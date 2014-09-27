package com.todolist;

import java.util.List;

/*
 * A builder class that takes a TodoList, and turns it into
 * a textual representation for the purposes of sending in
 * an Email.
 */
public class TodoListEmailBuilder {
	/*
	 * A filter representing which TodoItems from the source
	 * to include in the email.
	 */
	public interface TodoItemFilter {
		public boolean pass(TodoItem item);
	}
	
	// The builder's TodoItem source and filter.
	private List<TodoItem> mSourceItemList;
	private TodoItemFilter mFilter = new TodoItemFilter() {
		public boolean pass(TodoItem item) {
			return true;
		}
	};
	
	public TodoListEmailBuilder() {}
	
	/*
	 * Set what source TodoItem list to get our TodoItems from
	 */
	public TodoListEmailBuilder setSource(List<TodoItem> list) {
		mSourceItemList = list;
		return this;
	}
	
	/*
	 * Set what Filter to use to filter the source items.
	 */
	public TodoListEmailBuilder setFilter(TodoItemFilter filter) {
		mFilter = filter;
		return this;
	}
	
	/*
	 * Build the email body from the given builder parameters.
	 */
	public String buildEmailBody() {
		// Header
		String body = "==== Todo List ====\n\n";
		
		// For each todo item
		for (TodoItem item: mSourceItemList) {
			if (mFilter.pass(item)) {
				// Add a done box
				if (item.isDone()) {
					body = body + "[X] ";
				} else {
					body = body + "[ ] ";
				}
				
				// Add title
				body = body + item.getTitle();
				
				// If archived note that
				if (item.isArchived()) {
					body = body + " (archived)";
				}
				
				// Add body
				body = body + "\n    " + item.getBody();
				
				// Next entry
				body = body + "\n\n";
			}
		}
		
		// Trim off the two extra newlines
		body = body.substring(0, body.length() - 3);
				
		// Footer
		body = body + "===================\n";
		
		// Return the result
		return body;
	}
}
