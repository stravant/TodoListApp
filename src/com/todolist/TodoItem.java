package com.todolist;

import java.io.Serializable;
import java.util.Date;

/*
 * A TodoItem. Is a POD structure that can be automatically serialized using
 * the java serializable API.
 * The only non-POD like field is the unique ID, |mId|, which is for use
 * by a TodoItemManager / TodoItemDataStore in tracking their TodoItems.
 * Fields:
 *    - Title (get/setTitle): The brief heading describing a todo item
 *    - Body (get/setBody): The detailed text of a todo
 *    - Done (is/setDone): Has the todo been completed?
 *    - Archived (is/setArchived): Has the todo been archived (hidden from the main list)?
 *    - Created (get/setUpdate): The date / time when the todo was created 
 *    - Id (get/setId): A unique ID for the TodoItem. This is unique within the scope of a 
 *                      TodoItemManager, which allocates these IDs for its managed TodoItems.
 */
public class TodoItem implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private int mId;
	private String mTitle;
	private String mBody;
	private boolean mDone;
	private boolean mArchived;
	private Date mCreated;
	
	/*
	 * Default constructor, used by most of the code.
	 * The TodoItem should be constructed by setting its pieces most of the time,
	 * rather than by passing all of the data to the constructor.
	 */
	public TodoItem() {
		this(-1, new Date(), "", "", false, false);
	}
	
	/*
	 * Full constructor, with all fields.
	 */
	public TodoItem(int id, Date created, String title, String body, boolean done, boolean archived) {
		mId = id;
		mTitle = title;
		mBody = body;
		mDone = done;
		mArchived = archived;
		mCreated = created;
	}
	
	/*
	 * Get / Set the TodoItem title.
	 */
	public String getTitle() {
		return mTitle;
	}
	public void setTitle(String title) {
		mTitle = title;
	}

	/*
	 * Get / Set the TodoItem body
	 */
	public String getBody() {
		return mBody;
	}
	public void setBody(String body) {
		mBody = body;
	}

	/*
	 * Get / Set the done flag
	 */
	public boolean isDone() {
		return mDone;
	}
	public void setDone(boolean mDone) {
		this.mDone = mDone;
	}

	/*
	 * Get / Set the archived flag
	 */
	public boolean isArchived() {
		return mArchived;
	}
	public void setArchived(boolean archived) {
		mArchived = archived;
	}

	/*
	 * Get / Update the created date.
	 */
	public void setCreatedNow() {
		mCreated = new Date();
	}
	public Date getCreated() {
		return mCreated;
	}
	
	/* 
	 * Get / Set the unique ID. For use by the TodoItemManager 
	 * instances only.
	 */
	public void setId(int id) {
		mId = id;
	}
	public int getId() {
		return mId;
	}
}
