package com.todolist;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import android.util.Log;

/*
 * Is a model that manages a collection a list of TodoItems, backed
 * by a TodoItemDataStore.
 * Notifies subscribed listeners when the underlying data set changes,
 * or when there is a failure to correctly operate on the underlying
 * data set.
 */
public class TodoItemManager {
	/*
	 * Listeners interfaces for objects that wish
	 * to subscribe to IO failed or data updated
	 * notifications.
	 */
	public interface IOFailedListener {
		public void onIOFailed();
	}
	public interface UpdatedListener {
		public void onUpdated();
	}
	
	// My TodoItem records
	private ArrayList<TodoItem> mTodoItemList = new ArrayList<TodoItem>();
	private int mNextTodoItemId = 0; // Unique ID that the next item to be created will use
	
	// True if IO has failed for this TodoItemManager. Data from it cannot
	// be trusted once this flag is true.
	private boolean mFailed = false;
	
	// The data source to read / write records using
	private TodoItemDataStore mTodoDataStore;
	
	// Listeners on my updated and failed events
	private List<IOFailedListener> mIOFailedListeners = new ArrayList<IOFailedListener>();
	private List<UpdatedListener> mUpdatedListeners = new ArrayList<UpdatedListener>();

	/*
	 * Constructor
	 * @param dataStore The dataStore to use as the data source for
	 *                  this view.
	 */
	public TodoItemManager(TodoItemDataStore dataStore) {
		mTodoDataStore = dataStore;
	}
	
	/*
	 * Load in a new copy of the TodoItems from the data source.
	 * Will notify any IOFailure listeners if the items are unable
	 * to be read, otherwise, it will notify any of the updated
	 * listeners.
	 * In the case of a failure the exposed item list is left empty.
	 */
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
			
			// Notify listeners
			notifyUpdated();
		} catch (IOException e) {
			mFailed = true;
			System.out.printf("Loading list items failed: " + e.getMessage());
			
			// Notify listeners of the failed load
			notifyIOFailed();
			
			// Leave the todo list empty
		}
		Log.i("test", "Load Todo Item List (" + mTodoItemList.size() + ")");
	}
	
	/*
	 * Save out the items that we are currently working with to the data store.
	 * Notifies any IOFailure listeners in the case of a to write to the data store.
	 * The record list is left unsaved but as it was before in the case of failure.
	 */
	public void saveTodoItemList() {
		Log.i("test", "Save Todo Item List (" + mTodoItemList.size() + ")");
		try {
			// Save my current list of items
			mTodoDataStore.saveTodoItems(mTodoItemList);
		} catch (IOException ex) {
			mFailed = true;
			System.out.println("Saving list items failed: " + ex.getMessage());
			notifyIOFailed();
		}
	}
	
	/*
	 * Has IO Failed for this TodoItemList?
	 */
	public boolean hasFailed() {
		return mFailed;
	}
	
	/*
	 * Get a READONLY list of TodoItem records that this TodoItemManager 
	 * is managing.
	 * @return A list of the TodoItem records.
	 */
	public List<TodoItem> getTodoItemList() {
		return Collections.unmodifiableList(mTodoItemList);
	}
	
	/*
	 * Get a TodoItem by it's unique ID
	 * @param id The unique ID of the item to get
	 * @return The TodoItem with that ID, or null if there was none
	 */
	public TodoItem getTodoItemById(int id) {
		for (TodoItem item: mTodoItemList) {
			if (item.getId() == id)
				return item;
		}
		return null;
	}
	
	/*
	 * Mark a given TodoItem as dirty, that is, that it has changed
	 * and needs to be saved the next time saveTodoItemList is called.
	 * Also notifies updated listeners of the change.
	 * @param item The item to mark as dirty.
	 */
	public void markTodoItemDirty(TodoItem item) {
		// Just notify updated for now. In the future we could be more smart
		// and provide info about which items updated in the update notification
		// but for now we don't need to.
		notifyUpdated();
	}
	
	/*
	 * Adds a new TodoItem to the list being managed. Allocates a new unique ID
	 * for the TodoItem in question. Notifies updated listeners that the item
	 * was added.
	 * @param item The item to add.
	 */
	public void addTodoItem(TodoItem item) {
		// Add the item
		item.setId(mNextTodoItemId++);
		mTodoItemList.add(item);
		
		// Notify listeners
		notifyUpdated();
	}
	
	/*
	 * Remove a TodoItem from the list being managed. Notifies the listeners that
	 * the item was removed.
	 * @param item The item to remove.
	 */
	public void removeTodoItem(TodoItem item) {
		// Remove the item
		mTodoItemList.remove(item);
		
		// Notify the listeners
		notifyUpdated();
	}
	
	/*
	 * Remove a TodoItem from the list being managed, by ID. If no TodoItem
	 * has that ID, then do nothing.
	 * @param id The ID of the item to remove.
	 * @see removeTodoItem
	 */
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
	
	// Utility function to notify updated / failed listeners.
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
	
	/*
	 * Add an updated listener to be called when the managed list changes.
	 * @param listener The listener to add.
	 */
	public void addUpdatedListener(UpdatedListener listener) {
		mUpdatedListeners.add(listener);
	}
	
	/*
	 * Remove an updated listener.
	 * @param listener The listener to remove.
	 */
	public void removeUpdatedListener(UpdatedListener listener) {
		mUpdatedListeners.remove(listener);
	}
	
	/*
	 * Add an IOFailed listener to be called when there is a failure
	 * to load / save using the underlying TodoItemDataStore.
	 */
	public void addLoadFailedListener(IOFailedListener listener) {
		mIOFailedListeners.add(listener);
	}
}
