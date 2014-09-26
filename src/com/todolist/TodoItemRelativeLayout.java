package com.todolist;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

public class TodoItemRelativeLayout extends RelativeLayout {
	public TodoItemRelativeLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	public TodoItemRelativeLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	public TodoItemRelativeLayout(Context context) {
		super(context);
	}
	
	private TodoItem mTodoItem;
	
	public TodoItem getTodoItem() {
		return mTodoItem;
	}
	public void setTodoItem(TodoItem item) {
		mTodoItem = item;
	}
}


