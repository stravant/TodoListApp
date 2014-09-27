package com.todolist;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/*
 * An activity that lets the user edit an existing todo item, or create a
 * new todo item.
 */
public class NewOrEditTodoItemActivity extends FragmentActivity {
	public final static String EXTRA_ITEMID = "com.todolist.ITEMID";
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
        
        // Load in the TodoItem data set.
        mDataStore = new TodoItemDataStore(this, TodoItemDataStore.DEFAULT_SAVE_FILE);
        mItemManager = new TodoItemManager(mDataStore);
        mItemManager.loadTodoItemList();
        
        // Get the thing to do
        int itemId = getIntent().getIntExtra(EXTRA_ITEMID, ITEMID_NEWITEM);
        if (itemId == ITEMID_NEWITEM) {
        	mMode = MODE_NEW;
        	mEditItem = new TodoItem();
        } else {
        	mMode = MODE_EDIT;
        	mEditItem = mItemManager.getTodoItemById(itemId);
        }
        
        // Create the main UI
        setContentView(R.layout.new_item_activity);
        
        // If we are in edit mode, populate the fields with the existing data
        // from the edited TodoItem.
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
        Button cancelButton = (Button)findViewById(R.id.new_item_cancel_button);
        doneButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// Commit changes from editor into the edit item
				commitChangesToItem();
				
				// If we didn't actually enter anything into the todo, then
				// we should just discard it, even if we pressed "done".
				if (mEditItem.getTitle().equals("") && mEditItem.getBody().equals("")) {
					// In edit mode, we actually need to remove the item to discard it.
					if (mMode == MODE_EDIT) {
						mItemManager.removeTodoItem(mEditItem);
						mItemManager.saveTodoItemList();
					}
					finish();
					return;
				}
				
				// If we are in new mode, then we need to add the item to the list
				// before saving it.
				if (mMode == MODE_NEW) {
					mItemManager.addTodoItem(mEditItem);
				}
				
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
        /*
         * Cancel button is only available in new mode. For edited TodoItems,
         * edits are saved as you type them, so there is no "cancelling" an 
         * edit.
         */
        if (mMode == MODE_NEW) {
        	cancelButton.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					// Just exit the activity. The user can also achieve this
					// action just by clicking the back button again in the
					// new todo pane, but the button is there too in case they
					// are feeling lost.
					finish();
				}
			});
        } else {
        	cancelButton.setVisibility(View.GONE);
        }
    }
    
    @Override
    protected void onResume() {
    	// We don't need to do anything on resume. There aren't any
    	// Activities that can be entered from here that will change
    	// Our data, so we don't need to reload it on resume.
    	super.onResume();
    }
    
    @Override
    protected void onPause() {
    	super.onPause();
    	
    	// When paused, save out the current data if we are in edit mode
    	if (mMode == MODE_EDIT) {
	    	commitChangesToItem();
	    	mItemManager.saveTodoItemList();
    	} else {
    		// In new item mode, we don't commit the new item
    	}
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	// There aren't any menu actions in this activity
    	return true;
    }
    
    /*
     * Read in the user's current values for the Todo Item, and
     * write them into the TodoItem that we are working with.
     */
    private void commitChangesToItem() {
    	mEditItem.setTitle(mTodoTitle.getText().toString());
    	mEditItem.setBody(mTodoBody.getText().toString());
    	mItemManager.markTodoItemDirty(mEditItem);
    }
}
