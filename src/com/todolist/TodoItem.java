package com.todolist;

import java.io.Serializable;
import java.util.Date;

public class TodoItem implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private int mId;
	private String mTitle;
	private String mBody;
	private boolean mDone;
	private boolean mArchived;
	private final Date mCreated;
	
	public TodoItem(int id, Date created, String title, String body, boolean done, boolean archived) {
		mId = id;
		mTitle = title;
		mBody = body;
		mDone = done;
		mArchived = archived;
		mCreated = created;
	}
	
	public String getTitle() {
		return mTitle;
	}
	public void setTitle(String title) {
		mTitle = title;
	}

	public String getBody() {
		return mBody;
	}
	public void setBody(String body) {
		mBody = body;
	}

	public boolean isDone() {
		return mDone;
	}
	public void setDone(boolean mDone) {
		this.mDone = mDone;
	}

	public boolean isArchived() {
		return mArchived;
	}
	public void setArchived(boolean archived) {
		mArchived = archived;
	}

	public Date getCreated() {
		return mCreated;
	}
	
	public void setId(int id) {
		mId = id;
	}
	public int getId() {
		return mId;
	}
}
