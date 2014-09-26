package com.todolist;

import java.security.InvalidParameterException;
import java.util.Date;
import java.util.List;
import java.util.Random;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;


public class MainActivity extends FragmentActivity {
	// The main view for our activity, which is a pager
	// between a main page of our active todos, and separate page
	// of archived todos.
    SectionsPagerAdapter mSectionsPagerAdapter;
    ViewPager mViewPager;
    
    // The storage of our todo items
    TodoItemDataStore mDataStore;
    
    // The main model object for our todo data
    TodoItemManager mItemManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Set the title
        setTitle("Todo!");
        
        // Set up our state
        mDataStore = new TodoItemDataStore(this, "todo_data.sav");
        mItemManager = new TodoItemManager(mDataStore);
        
        // Create the main UI
        setContentView(R.layout.activity_main);
        
        // Create the Page adapter
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager)findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);
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
    	super.onPause();
    	
    	// When paused, save out the current data
    	mItemManager.saveTodoItemList();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // When the user requests the menu
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.action_todo_list) {
        	// Go to todo list
        	mViewPager.setCurrentItem(0);
        	return true;
        } else if (id == R.id.action_archive_list) {
        	// Go to archived list
        	mViewPager.setCurrentItem(1);
        	return true;
        } else if (id == R.id.action_new_todo) {
        	// Go into the "new/edit todo" activity
        	Intent newItemIntent = new Intent(this, NewTodoItemActivity.class);
        	// ITEMID = (-1) --> Make a new item rather than use an existing one
        	newItemIntent.putExtra(NewTodoItemActivity.EXTRA_ITEMID, NewTodoItemActivity.ITEMID_NEWITEM);
        	startActivity(newItemIntent);
        	return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {
        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
        	if (position == 0) {
	            return new TodoListFragment(getPageTitle(position).toString(), mItemManager, new TodoListFragment.TodoListFilter() {
					public boolean filter(TodoItem item) {
						return !item.isArchived(); // true --> show item
					}
				});
        	} else if (position == 1) {
	            return new TodoListFragment(getPageTitle(position).toString(), mItemManager, new TodoListFragment.TodoListFilter() {
					public boolean filter(TodoItem item) {
						return item.isArchived(); // true --> show item
					}
				});
        	} else {
        		throw new InvalidParameterException("Position: " + position);
        	}
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getString(R.string.title_mainlist);
                case 1:
                    return getString(R.string.title_archivelist);
            }
            return null;
        }
    }
}
