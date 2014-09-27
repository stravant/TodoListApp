package com.todolist;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collection;

import android.content.Context;
import android.util.Log;

/*
 * The TodoItemDataStore class is a utility class that can be used to save collections
 * of TodoItems to files or load collections of TodoItems from files.
 * It also provides a default save location to use, DEFAULT_SAVE_FILE.
 */
public class TodoItemDataStore {
	public static final String DEFAULT_SAVE_FILE = "todo_save.sav";
	
	private Context mContext;
	private String mFileName;
		
	/*
	 * Constructor
	 * @param ctx      The context to operate on the save file in.
	 * @param fileName The name of the file to save / load using.
	 */
	public TodoItemDataStore(Context ctx, String fileName) {
		mContext = ctx;
		mFileName = fileName;
	}
	
	/*
	 * Saves a collection of TodoItems to the specified 
	 * TodoItemDataStore's source file.
	 * @param items The collection of TodoItems to be saved.
	 */
	void saveTodoItems(Collection<TodoItem> items) throws IOException {
		FileOutputStream outFile = mContext.openFileOutput(mFileName, Context.MODE_PRIVATE);
		ObjectOutputStream outStream = new ObjectOutputStream(outFile);
		outStream.writeObject(items);
		outFile.close();
	}
	
	/*
	 * Loads a collection of TodoItems from the TodoItemDataStore's
	 * source file.
	 * @return The loaded collection of TodoItems.
	 */
	@SuppressWarnings("unchecked")
	Collection<TodoItem> loadTodoItems() throws IOException {
		/*
		 * First, check if there is no saved data yet. In that case,
		 * return an empty collection.
		 * Code from:
		 * 	http://stackoverflow.com/questions/8867334/check-if-a-file-exists-before-calling-openfileinput
		 */
		File file = mContext.getFileStreamPath(mFileName);
		if(!file.exists()) {
			return new ArrayList<TodoItem>();
		}
		
		// If the file does exist, read in the collection
		FileInputStream inFile = mContext.openFileInput(mFileName);
		ObjectInputStream inStream = new ObjectInputStream(inFile);
		try {
			return (Collection<TodoItem>)inStream.readObject();
		} catch (ClassNotFoundException ex) {
			throw new IOException("Out of bad TodoItem data format: " + ex.getMessage());
		}
	}
}
