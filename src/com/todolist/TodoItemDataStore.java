package com.todolist;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collection;

import android.content.Context;

public class TodoItemDataStore {
	private Context mContext;
	private String mFileName;
		
	public TodoItemDataStore(Context ctx, String fileName) {
		mContext = ctx;
		mFileName = fileName;
	}
	
	void saveTodoItems(Collection<TodoItem> items) throws IOException {
		FileOutputStream outFile = mContext.openFileOutput(mFileName, Context.MODE_PRIVATE);
		ObjectOutputStream outStream = new ObjectOutputStream(outFile);
		outStream.writeObject(items);
		outFile.close();
	}
	
	@SuppressWarnings("unchecked")
	Collection<TodoItem> loadTodoItems() throws IOException {
		FileInputStream inFile = mContext.openFileInput(mFileName);
		ObjectInputStream inStream = new ObjectInputStream(inFile);
		try {
			return (Collection<TodoItem>)inStream.readObject();
		} catch (ClassNotFoundException ex) {
			throw new IOException("Out of bad TodoItem data format: " + ex.getMessage());
		}
	}
}
