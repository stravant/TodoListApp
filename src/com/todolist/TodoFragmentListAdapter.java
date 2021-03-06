package com.todolist;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

public class TodoFragmentListAdapter extends ArrayAdapter<TodoItem> {
	private TodoListFragment mTarget;
	private boolean mIsEditMode = false;
	
	public TodoFragmentListAdapter(TodoListFragment target, Context c, ListView list, List<TodoItem> items) {
		super(c, R.layout.todo_list_item, items);
		mTarget = target;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final TodoItem item = getItem(position);
		
		// Make a new view if there was not an old one for this item
		if (convertView == null) {
			convertView = LayoutInflater.from(getContext()).inflate(R.layout.todo_list_item, parent, false);
			
			// For new items, set up the events:
			final TodoItemRelativeLayout itemLayout = ((TodoItemRelativeLayout)convertView);
			ImageButton editButton = ((ImageButton)convertView.findViewById(R.id.entry_edit_button));
			ImageButton archiveButton = ((ImageButton)convertView.findViewById(R.id.entry_archive_button));
			ImageButton deleteButton = ((ImageButton)convertView.findViewById(R.id.entry_delete_button));
			
			// Set the events to call back to our target TodoListFragment
			editButton.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					mTarget.onEditItem(itemLayout.getTodoItem());
				}
			});
			archiveButton.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					mTarget.onArchiveItem(itemLayout.getTodoItem());
				}
			});
			deleteButton.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					mTarget.onDeleteItem(itemLayout.getTodoItem());
				}
			});
			
			// Set up the item done checkbox
			CheckBox doneCheckbox = ((CheckBox)convertView.findViewById(R.id.entry_done_checkbox));
			doneCheckbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					mTarget.onSetDone(itemLayout.getTodoItem(), isChecked);
				}
			});
		}
		
		// Set what item this view is referencing (For it's events)
		final TodoItemRelativeLayout itemLayout = ((TodoItemRelativeLayout)convertView);
		itemLayout.setTodoItem(item);
		
		// Get the parts of the view
		CheckBox doneCheckbox = ((CheckBox)convertView.findViewById(R.id.entry_done_checkbox));
		TextView entryTitle = ((TextView)convertView.findViewById(R.id.entry_title));
		TextView entryBody = ((TextView)convertView.findViewById(R.id.entry_body));
		ViewGroup editControlSet = ((ViewGroup)convertView.findViewById(R.id.entry_edit_control_set));
		
		// Set the basic stuff
		doneCheckbox.setChecked(item.isDone());
		entryBody.setText(item.getBody());
		
		// Set the entry title based on whether we are archived or not
		if (item.isArchived()) {
			entryTitle.setTextColor(mTarget.getResources().getColor(R.color.archive_text_color));
			entryTitle.setText(item.getTitle() + " (archived)");
		} else {
			entryTitle.setTextColor(entryBody.getTextColors());
			entryTitle.setText(item.getTitle());
		}
		
		// If we are in edit mode, show the edit controls
		if (mIsEditMode) {
			// In edit mode, fade the main info a bit and show edit controls
			editControlSet.setVisibility(View.VISIBLE);
			//mainInfoContainer.setAlpha(0.8f);
		} else {
			// In normal mode, the edit controls are not shown
			editControlSet.setVisibility(View.GONE);
			//mainInfoContainer.setAlpha(1.0f);			
		}
		
		return convertView;
	}
	
	public void setEditable(boolean state) {
		mIsEditMode = state;
		notifyDataSetChanged();
	}
}
