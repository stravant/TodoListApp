package com.todolist;

import java.util.ArrayList;

import com.todolist.TodoItemManager.UpdatedListener;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import android.widget.TextView;

/* 
 * A fragment that displays some subset of the Todos in a given 
 * TodoItemManager using a filter that specifies which ones to
 * show.
 * Editing mode can be toggled on and off using a checkbox
 * at the top of the fragment.
 * In Editing mode, the user can move TodoItems between the
 * normal and archived lists, delete items from the current
 * list, or edit items in the current list, opening an TodoItem
 * editing activity.
 * In either mode the user can toggle a given item's done status.
 */
public class TodoListFragment extends Fragment {
	public interface TodoListFilter {
		public boolean filter(TodoItem item);
	}
	
	// The item manager to get the items from, and the
	// Filter to filter those items with.
	private TodoItemManager mItemManager;
	private TodoListFilter mItemFilter;
	private String mSectionTitle;
	
	// Listener on the item manager, so that we can update the list
	// when the TodoItemManager's list of items changes.
	private UpdatedListener mItemManagerUpdatedListener;
	
	// The ListView setup. A cached filtered list of TodoItems to be
	// displayed, and the ListView+Adapter to display them.
	private ArrayList<TodoItem> mCachedEntryList = new ArrayList<TodoItem>();
	private ListView mListView;
	private TodoFragmentListAdapter mListAdapter;
	
	// For convenience, a reference to the edit mode toggle checkbox.
	private CheckBox mEditCheckbox;
	
	/*
	 * Constructor.
	 * @param sectionTitle A description of what subset of the Todos are
	 *                     being displayed by this TodoListFragment.
	 * @param itemManager  The TodoItemManager providing the items to show.
	 * @param filter       The filter to filter the total list of TodoItems
	 *                     from the |itemManager| by.
	 */
	public TodoListFragment(String sectionTitle, TodoItemManager itemManager, TodoListFilter filter) {
		mSectionTitle = sectionTitle;
		mItemManager = itemManager;
		mItemFilter = filter;
	}
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
    	// Create the main view for the fragment
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        
        // Hook us up to listen for changes on the data set
        mItemManagerUpdatedListener = new TodoItemManager.UpdatedListener() {
			public void onUpdated() {
				updateCachedItemList();
			}
		};
        mItemManager.addUpdatedListener(mItemManagerUpdatedListener);
        
        // Get the edit CheckBox, and hook it up to changing our list adapters
        // display to show edit mode entries or normal entries.
        mEditCheckbox = ((CheckBox)rootView.findViewById(R.id.edit_checkbox));
        mEditCheckbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				mListAdapter.setEditable(isChecked);
				updateCachedItemList();
			}
		});
        
        // Set the section title
        TextView label = ((TextView)rootView.findViewById(R.id.section_label));
        label.setText(mSectionTitle);
        
        // Set up the ListView+Adapter
        mListView = ((ListView)rootView.findViewById(R.id.entry_list));
        mListAdapter = new TodoFragmentListAdapter(this, getActivity(), mListView, mCachedEntryList);
        mListAdapter.setEditable(mEditCheckbox.isChecked());
        mListView.setAdapter(mListAdapter);
        
        // Populate the items
        updateCachedItemList();
        
        return rootView;
    }
    
    /*
     * Update the internal cached list of TodoItems to show in this
     * view, and notify the ListView to update to reflect the change.
     */
    private void updateCachedItemList() {
    	mCachedEntryList.clear();
    	for (TodoItem item: mItemManager.getTodoItemList()) {
    		if (mItemFilter.filter(item)) {
    			mCachedEntryList.add(item);
    		}
    	}
    	mListAdapter.notifyDataSetChanged();
    }
    
    // Actions to be called on by the TodoFragmentListAdapter when 
    // the user interacts with items in the ListView.
    
    // The user wants to delete the item
    public void onDeleteItem(TodoItem item) {
    	mItemManager.removeTodoItem(item);
    	mItemManager.saveTodoItemList();
    }
    
    // The user wants to open the editing activity on the item
    public void onEditItem(TodoItem item) {
    	Intent newItemIntent = new Intent(getActivity(), NewOrEditTodoItemActivity.class);
    	newItemIntent.putExtra(NewOrEditTodoItemActivity.EXTRA_ITEMID, item.getId());
    	startActivity(newItemIntent);
    }
    
    // The user wants to swap the item between the archive and normal lists
    public void onArchiveItem(TodoItem item) {
    	item.setArchived(!item.isArchived());
    	mItemManager.markTodoItemDirty(item);
    	mItemManager.saveTodoItemList();
    }
    
    // The user wants to set the item as done or not done
    public void onSetDone(TodoItem item, boolean isDone) {
    	item.setDone(isDone);
    	mItemManager.markTodoItemDirty(item);
    	mItemManager.saveTodoItemList();
    }
    
    @Override
    public void onDestroyView() {
    	// When the fragment view is destroyed, stop listinging for updates
    	// to the TodoList data set.
    	mItemManager.removeUpdatedListener(mItemManagerUpdatedListener);
    	super.onDestroyView();
    }
}
