package com.todolist;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

public class NewTodoItemActivity extends ActionBarActivity {
   
    // The storage of our todo items
    TodoItemDataStore mDataStore;
    
    // The main model object for our todo data
    TodoItemManager mItemManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Set up our state
        mDataStore = new TodoItemDataStore(this, "todo_data.sav");
        mItemManager = new TodoItemManager(mDataStore);
        
        // Create the main UI
        setContentView(R.layout.new_item_activity);
    }
    
    
    @Override
    protected void onStart() {
    	super.onStart();
    	
    	// When started, load in the data
    	mItemManager.loadTodoItemList();
    }
    
    @Override
    protected void onResume() {
    	super.onResume();
    	
    	// When resumed, reload the data, as we may
    	// have been in another activity that made our copy dirty
    	mItemManager.loadTodoItemList();
    }
    
    @Override
    protected void onPause() {
    	super.onStop();
    	
    	// When paused, save out the current data
    	mItemManager.saveTodoItemList();
    }
}
