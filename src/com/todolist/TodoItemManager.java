package com.todolist;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class TodoItemManager {
	// IOFailed and Updated listeners
	public interface IOFailedListener {
		public void onIOFailed();
	}
	public interface UpdatedListener {
		public void onUpdated();
	}
	
	// My TodoItem records
	private ArrayList<TodoItem> mTodoItemList = new ArrayList<TodoItem>();
	private int mNextTodoItemId = 0;
	
	// The data source to read / write records using
	private TodoItemDataStore mTodoDataStore;
	
	// Listeners on my updated and failed events
	private List<IOFailedListener> mIOFailedListeners = new ArrayList<IOFailedListener>();
	private List<UpdatedListener> mUpdatedListeners = new ArrayList<UpdatedListener>();

	// Constructor, from a given TodoItem data store
	public TodoItemManager(TodoItemDataStore dataStore) {
		mTodoDataStore = dataStore;
	}
	
	// Load in items from the data store
	// Notify IOFailure listeners and leave the record list empty on failure
	public void loadTodoItemList() {
		// Clear out the current, likely out of date data
		mTodoItemList.clear();
		try {
			// Add all of the items to our list, tracking what ID to give the next
			// added item.
			for (TodoItem item: mTodoDataStore.loadTodoItems()) {
				mTodoItemList.add(item);
				mNextTodoItemId = Math.max(mNextTodoItemId, item.getId() + 1);
			}
		} catch (IOException e) {
			System.out.printf("Loading list items failed: " + e.getMessage());
			
			// Notify listeners of the failed load
			notifyIOFailed();
			
			// Leave the todo list empty
		}
	}
	
	// Save out items to the data store
	// Notify IOFailure listeners and leave the record list unsaved on failure
	public void saveTodoItemList() {
		try {
			// Save my current list of items
			mTodoDataStore.saveTodoItems(mTodoItemList);
		} catch (IOException ex) {
			System.out.println("Saving list items failed: " + ex.getMessage());
			notifyIOFailed();
		}
	}
	
	// Get the item list
	// Note: Returns an immutable view of the item list, the add / remove methods
	// must be used to modify the underlying data.
	public List<TodoItem> getTodoItemList() {
		return Collections.unmodifiableList(mTodoItemList);
	}
	
	// Mark a given item as dirty, triggering an Updated message.
	public void markTodoItemDirty(TodoItem item) {
		// Just notify updated for now. In the future we could be more smart
		// and provide info about which items updated in the update notification
		// but for now we don't need to.
		notifyUpdated();
	}
	
	// Add a new todo record
	// Sets the todo item's ID, adds it to the item list, and notifies listeners
	public void addTodoItem(TodoItem item) {
		// Add the item
		item.setId(mNextTodoItemId++);
		mTodoItemList.add(item);
		
		// Notify listeners
		notifyUpdated();
	}
	
	// Removes a todo record
	public void removeTodoItem(TodoItem item) {
		// Remove the item
		mTodoItemList.remove(item);
		
		// Notify the listeners
		notifyUpdated();
	}
	
	// Remove a todo record by ID
	public void removeTodoItemById(int id) {
		// Iterate and find items with the ID
		Iterator<TodoItem> it = mTodoItemList.iterator();
		while (it.hasNext()) {
			TodoItem item = it.next();
			if (item.getId() == id) {
				it.remove();
				break;
			}
		}
		
		// Notify the listeners
		notifyUpdated();
	}
	
	// Notification for our listeners
	private void notifyUpdated() {
		for (UpdatedListener listener: mUpdatedListeners) {
			listener.onUpdated();
		}
	}
	private void notifyIOFailed() {
		for (IOFailedListener listener: mIOFailedListeners) {
			listener.onIOFailed();
		}
	}
	public void addUpdatedListener(UpdatedListener listener) {
		mUpdatedListeners.add(listener);
	}
	public void removeUpdatedListener(UpdatedListener listener) {
		mUpdatedListeners.remove(listener);
	}
	public void addLoadFailedListener(IOFailedListener listener) {
		mIOFailedListeners.add(listener);
	}
}
