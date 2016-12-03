package databaseoperation;


import java.io.File;
import java.io.UnsupportedEncodingException;

import com.sleepycat.je.Cursor;
import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;
import com.sleepycat.je.OperationStatus;

public class DatabaseOperation {
	// Search table by tableName and return schema value in String. return null if not found.
	public static String searchTableSchemaByName(Cursor cursor, String tableName) throws Exception {
		try {
			DatabaseEntry KeyForSearch = new DatabaseEntry(tableName.getBytes("UTF-8"));
			DatabaseEntry foundData = new DatabaseEntry();
			
			if (cursor.getSearchKey(KeyForSearch, foundData, null) == OperationStatus.SUCCESS) {
				return new String(foundData.getData(), "UTF-8");
			}
		} catch (UnsupportedEncodingException e) {
//			e.printStackTrace();
		}
		
		return null;
	}
}
