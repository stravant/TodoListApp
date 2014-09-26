package com.todolist;

import java.util.Date;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class NewTodoItemActivity extends FragmentActivity {
	public final static String EXTRA_ITEMID = "com.example.myfirstapp.ITEMID";
	public final static int ITEMID_NEWITEM = -1;
	
	// Modes we can be in, editing an existing item or creating a new one
	private final static int MODE_EDIT = 0;
	private final static int MODE_NEW = 1;
   
    // The storage of our todo items
    private TodoItemDataStore mDataStore;
    
    // The main model object for our todo data
    private TodoItemManager mItemManager;
    
    // Our current mode
    private int mMode;
    private TodoItem mEditItem;
    
    // Private controls that multiple methods need
    private EditText mTodoTitle;
    private EditText mTodoBody;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Set up our state
        mDataStore = new TodoItemDataStore(this, "todo_data.sav");
        mItemManager = new TodoItemManager(mDataStore);
        mItemManager.loadTodoItemList();
        
        // Get the thing to do
        int itemId = getIntent().getIntExtra(EXTRA_ITEMID, ITEMID_NEWITEM);
        if (itemId == ITEMID_NEWITEM) {
        	mMode = MODE_NEW;
        	mEditItem = new TodoItem(-1, new Date(), "", "", false, false);
        } else {
        	mMode = MODE_EDIT;
        	mEditItem = mItemManager.getTodoItemById(itemId);
        }
        
        // Create the main UI
        setContentView(R.layout.new_item_activity);
        
        // If we are an editor, edit
        TextView titleBar = (TextView)findViewById(R.id.new_item_header_text);
       	mTodoTitle = (EditText)findViewById(R.id.new_item_title_text);
        mTodoBody = (EditText)findViewById(R.id.new_item_body_text);
        if (mMode == MODE_EDIT) {
        	titleBar.setText("Editing existing Todo");
        	
        	// Populate the editor fields with the existing info
        	mTodoTitle.setText(mEditItem.getTitle());
        	mTodoBody.setText(mEditItem.getBody());
        } else {
        	// Otherwise, new
        	titleBar.setText("Create new Todo");
        }
        
        // Hook up buttons
        Button doneButton = (Button)findViewById(R.id.new_item_done_button);
        Button clearButton = (Button)findViewById(R.id.new_item_clear_button);
        doneButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// Commit changes from editor into the edit item
				commitChangesToItem();
				
				// If we are in new mode, then we need to add the item to the list
				// before saving it.
				if (mMode == MODE_NEW)
					mItemManager.addTodoItem(mEditItem);
				
				// Mark the edited item as dirty and save out the changes
				mItemManager.markTodoItemDirty(mEditItem);
				mItemManager.saveTodoItemList();
				
				// Then finish the activity
				finish();
			}
		});
        clearButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// Clear out the text fields and commit the change
				mTodoTitle.setText("");
				mTodoBody.setText("");
				commitChangesToItem();
				
				// If we are in edit mode, save
				if (mMode == MODE_EDIT) {
					mItemManager.markTodoItemDirty(mEditItem);
					mItemManager.saveTodoItemList();
				}
			}
		});
    }
    
    @Override
    protected void onResume() {
    	super.onResume();
    }
    
    @Override
    protected void onPause() {
    	super.onStop();
    	
    	// When paused, save out the current data if we are in edit mode
    	if (mMode == MODE_EDIT) {
	    	commitChangesToItem();
	    	mItemManager.saveTodoItemList();
    	}
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	// There aren't any menu actions in this activity
    	return true;
    }
    
    // Read in the user's current values for the Todo Item, and
    // write them into the TodoItem that we are working with.
    private void commitChangesToItem() {
    	mEditItem.setTitle(mTodoTitle.getText().toString());
    	mEditItem.setBody(mTodoBody.getText().toString());
    	mItemManager.markTodoItemDirty(mEditItem);
    }
}
