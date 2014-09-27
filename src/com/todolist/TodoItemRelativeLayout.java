package com.todolist;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

/*
 * A relative layout which contains a reference to a TodoItem. This is used by
 * ListViews, whose recycled items need to have access to what TodoItem they
 * are currently representing, so that their event handlers know which
 * TodoItem to take their actions on.
 */
public class TodoItemRelativeLayout extends RelativeLayout {
	/*
	 * Forward Constructors to superclass
	 */
	public TodoItemRelativeLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	public TodoItemRelativeLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	public TodoItemRelativeLayout(Context context) {
		super(context);
	}
	
	/*
	 * The TodoItem reference.
	 */
	private TodoItem mTodoItem;
	
	public TodoItem getTodoItem() {
		return mTodoItem;
	}
	public void setTodoItem(TodoItem item) {
		mTodoItem = item;
	}
}


