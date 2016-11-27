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
//	public static void openDatabase(Environment myDbEnvironment, Database myDatabase, String pathName, String fileName) {
//		// Open Database Environment or if not, create one.
//		EnvironmentConfig envConfig = new EnvironmentConfig();
//		envConfig.setAllowCreate(true);
//		myDbEnvironment = new Environment(new File(pathName), envConfig);
//			
//		// Open Database or if not, create one.
//		DatabaseConfig dbConfig = new DatabaseConfig();
//		dbConfig.setAllowCreate(true);
//		dbConfig.setSortedDuplicates(true);
//		myDatabase = myDbEnvironment.openDatabase(null, fileName, dbConfig);
//	}
//	
//	public static void close(Cursor cursor, Database myDatabase, Environment myDbEnvironment) {
//		if (cursor != null) cursor.close();
//		if (myDatabase != null) myDatabase.close();
//		if (myDbEnvironment != null) myDbEnvironment.close();
//	}
	
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
