package com.todolist;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;
import android.support.v4.app.NavUtils;

/*
 * An activity in which the user specifies an email and settings
 * with which to export the TodoList via email to.
 * For now the only setting is "include archived" which is off by
 * default, as the user likely wants only the active todos when
 * getting a snapshot of the todo list with export.
 */
public class EmailExportActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Create the main UI
		setContentView(R.layout.activity_email_export);
		
		// Set up the events
		Button okayButton = (Button)findViewById(R.id.email_okay_button);
		Button cancelButton = (Button)findViewById(R.id.email_cancel_button);
		okayButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// Get the stuff
				CheckBox includeArchived = (CheckBox)findViewById(R.id.email_include_archived);
				EditText emailInput = (EditText)findViewById(R.id.email_email_text);
				trySendEmail(emailInput.getText().toString(), includeArchived.isChecked());
			}
		});
		cancelButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// Done the activity
				finish();
			}
		});
	}
	
	// Try to generate and send an email to the given address
	private void trySendEmail(String emailTo, final boolean includeArchived) {
		// Basic check for a valid email address
		if (emailTo.matches("\\s*") || !emailTo.contains("@")) {
			Toast.makeText(this, "Invalid Email Address", Toast.LENGTH_SHORT).show();
			return;
		}
		
		// Try to open the todo data
		TodoItemDataStore dataStore = new TodoItemDataStore(this, TodoItemDataStore.DEFAULT_SAVE_FILE);
        TodoItemManager itemManager = new TodoItemManager(dataStore);
        itemManager.loadTodoItemList();
        
        // Check if there is anything to send
        if (itemManager.getTodoItemList().size() == 0) {
        	Toast.makeText(this, "No Todos to send", Toast.LENGTH_SHORT).show();
        }
        
        // Generate an email body
        String emailBody = 
        	new TodoListEmailBuilder()
        		.setSource(itemManager.getTodoItemList())
        		.setFilter(new TodoListEmailBuilder.TodoItemFilter() {
					public boolean pass(TodoItem item) {
						return !item.isArchived() || includeArchived;
					}
				})
				.buildEmailBody();
        
        // Generate email title
        String emailTitle = "";
        
        // Send off the email
        sendEmail(emailTo, emailTitle, emailBody);
	}
	
	// Function used to actually invoke the email send after the parameters are
	// checked and the email body is generated.
	private void sendEmail(String emailTo, String emailTitle, String emailBody) {
		/*
		 * See StackOverflow: 
		 * http://stackoverflow.com/questions/2197741/how-can-i-send-emails-from-my-android-application
		 */
		Intent sendEmailIntent = new Intent(Intent.ACTION_SEND);
		sendEmailIntent.setType("message/rfc822");
		sendEmailIntent.putExtra(Intent.EXTRA_EMAIL  , new String[]{emailTo});
		sendEmailIntent.putExtra(Intent.EXTRA_SUBJECT, emailTitle);
		sendEmailIntent.putExtra(Intent.EXTRA_TEXT   , emailBody);
		try {
		    startActivity(Intent.createChooser(sendEmailIntent, "Send the email using..."));
		} catch (android.content.ActivityNotFoundException ex) {
		    Toast.makeText(this, "No Email Clients Available", Toast.LENGTH_SHORT).show();
		}
		
		finish();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// No menu to create here
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Add behavior so that the main button in our ActionBar will
		// bring us back to the main activity.
		switch (item.getItemId()) {
		case android.R.id.home:
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
