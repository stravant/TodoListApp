package com.todolist;

import java.util.ArrayList;
import java.util.List;

import com.todolist.TodoItemManager.UpdatedListener;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import android.widget.TextView;

/* A fragment that displays some subset of the Todos in a given 
 * TodoItemManager.
 * */
public class TodoListFragment extends Fragment {
	public interface TodoListFilter {
		public boolean filter(TodoItem item);
	}
	
	// The specifications for this todo list display
	private TodoItemManager mItemManager;
	private TodoListFilter mItemFilter;
	private String mSectionTitle;
	
	// Listeners on the item manager
	private UpdatedListener mItemManagerUpdatedListener;
	
	// The list of items we are showing and the view to view those elements
	private ArrayList<TodoItem> mCachedEntryList = new ArrayList<TodoItem>();
	private ListView mListView;
	private TodoFragmentListAdapter mListAdapter;
	
	// Edit toggle
	private CheckBox mEditCheckbox;
	
	// Constructor
	public TodoListFragment(String sectionTitle, TodoItemManager itemManager, TodoListFilter filter) {
		mSectionTitle = sectionTitle;
		mItemManager = itemManager;
		mItemFilter = filter;
	}
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
    	Log.i("test", "Fragment::CreateView");
    	
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        
        // Hook us up to listen for changes on the data set
        mItemManagerUpdatedListener = new TodoItemManager.UpdatedListener() {
			public void onUpdated() {
				updateCachedItemList();
			}
		};
        mItemManager.addUpdatedListener(mItemManagerUpdatedListener);
        
        // Get the edit checkbox
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
        
        // Set up the list view
        mListView = ((ListView)rootView.findViewById(R.id.entry_list));
        mListAdapter = new TodoFragmentListAdapter(this, getActivity(), mListView, mCachedEntryList);
        mListAdapter.setEditable(mEditCheckbox.isChecked());
        mListView.setAdapter(mListAdapter);
        
        // Populate the items
        updateCachedItemList();
        
        return rootView;
    }
    
    // Update the internal cached entry list to show when the data set changes
    private void updateCachedItemList() {
    	Log.i("test", "updateCachedItemList");
    	mCachedEntryList.clear();
    	for (TodoItem item: mItemManager.getTodoItemList()) {
    		if (mItemFilter.filter(item)) {
    			mCachedEntryList.add(item);
    		}
    	}
    	mListAdapter.notifyDataSetChanged();
    }
    
    // Used by the TodoFragmentListAdapter when the user interacts with items
    public void onDeleteItem(TodoItem item) {
    	mItemManager.removeTodoItem(item);
    	mItemManager.saveTodoItemList();
    }
    public void onEditItem(TodoItem item) {
    	Intent newItemIntent = new Intent(getActivity(), NewTodoItemActivity.class);
    	newItemIntent.putExtra(NewTodoItemActivity.EXTRA_ITEMID, item.getId());
    	startActivity(newItemIntent);
    }
    public void onArchiveItem(TodoItem item) {
    	item.setArchived(!item.isArchived());
    	mItemManager.markTodoItemDirty(item);
    	mItemManager.saveTodoItemList();
    }
    public void onSetDone(TodoItem item, boolean isDone) {
    	item.setDone(isDone);
    	mItemManager.markTodoItemDirty(item);
    	mItemManager.saveTodoItemList();
    }
    
    @Override
    public void onDestroyView() {
    	Log.i("test", "Fragment::DestroyView");
    	
    	// No longer track updates
    	mItemManager.removeUpdatedListener(mItemManagerUpdatedListener);
    	
    	super.onDestroyView();
    }
}
