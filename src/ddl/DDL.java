package ddl;

import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.Cursor;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;
import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.LockMode;
import com.sleepycat.je.OperationStatus;

import java.io.File;
import java.util.ArrayList;

import databaseoperation.DatabaseOperation;
import definition.TableDefinition;
import dml.Conversion;
import dml.Value;
import dml.types.ThreeLogic;

public class DDL {
	// create table
	public static void executeCreateTable(TableDefinition tableDefinition) {
		// Environment & Database define
		Environment myDbEnvironment = null;
		Database myDatabase = null;

		/* OPENING DB */
		// Open Database Environment or if not, create one.
		EnvironmentConfig envConfig = new EnvironmentConfig();
		envConfig.setAllowCreate(true);
		myDbEnvironment = new Environment(new File("db/"), envConfig);

		// Open Database or if not, create one.
		DatabaseConfig dbConfig = new DatabaseConfig();
		dbConfig.setAllowCreate(true);
		dbConfig.setSortedDuplicates(true);
		myDatabase = myDbEnvironment.openDatabase(null, "dbschema", dbConfig);

		Cursor cursor = null;

		try {
			if (tableDefinition.checkValidity()) {
				cursor = myDatabase.openCursor(null, null);
				DatabaseEntry key = new DatabaseEntry(tableDefinition.getTableName().getBytes("UTF-8"));
				DatabaseEntry data = new DatabaseEntry(tableDefinition.getSchemaValue().getBytes("UTF-8"));

				if (cursor.putNoOverwrite(key, data) == OperationStatus.KEYEXIST)
					System.out.println("Create table has failed: table with the same name already exists");
				else
					System.out.println("'" + tableDefinition.getTableName() + "' table is created");
			} else {
				tableDefinition.printErrorMessage();
			}
		} catch (Exception e) {
			System.out.println("Create table has failed: Unexpected Error");
		}

		if (cursor != null) cursor.close();
		if (myDatabase != null) myDatabase.close();
		if (myDbEnvironment != null) myDbEnvironment.close();
	}

	
	// drop table
	public static void executeDropTable(String argTableName) {
		// Environment & Database define
		Environment myDbEnvironment = null;
		Database myDatabase = null;
		Database myDatabase2 = null;

		/* OPENING DB */
		// Open Database Environment or if not, create one.
		EnvironmentConfig envConfig = new EnvironmentConfig();
		envConfig.setAllowCreate(true);
		myDbEnvironment = new Environment(new File("db/"), envConfig);

		// Open Database or if not, create one.
		DatabaseConfig dbConfig = new DatabaseConfig();
		dbConfig.setAllowCreate(true);
		dbConfig.setSortedDuplicates(true);
		myDatabase = myDbEnvironment.openDatabase(null, "dbschema", dbConfig);
		
		Cursor cursor = null;
		Cursor cursor2 = null;
		Cursor cursor3 = null;

		try {
			cursor = myDatabase.openCursor(null, null);
			cursor2 = myDatabase.openCursor(null, null);
			
			DatabaseEntry KeyForSearch = new DatabaseEntry(argTableName.getBytes("UTF-8"));
			DatabaseEntry foundData = new DatabaseEntry();

			if (cursor.getSearchKey(KeyForSearch, foundData, null) == OperationStatus.SUCCESS) {
				myDatabase2 = myDbEnvironment.openDatabase(null, "dbdata."+argTableName, dbConfig);
				cursor3 = myDatabase2.openCursor(null, null);
				
				if (!checkIfReferenced(cursor2, argTableName)) {
					cursor.delete();
					if (cursor3.getFirst(KeyForSearch, foundData, LockMode.DEFAULT) == OperationStatus.SUCCESS) {
						do {
							cursor3.delete();
						} while (cursor3.getNext(KeyForSearch, foundData, LockMode.DEFAULT) == OperationStatus.SUCCESS);
					}
					
					System.out.println("'" + argTableName + "' table is dropped");
				} else {
					System.out.println("Drop table has failed: '" + argTableName + "' is referenced by other table");
				}
			} else
				System.out.println("No such table");
		} catch (Exception e) {
//			 e.printStackTrace();  
		}

		if (cursor != null) cursor.close();
		if (cursor2 != null) cursor2.close();
		if (cursor3 != null) cursor3.close();
		if (myDatabase != null) myDatabase.close();
		if (myDatabase2 != null) myDatabase2.close();
		if (myDbEnvironment != null) myDbEnvironment.close();
	}
	
	// return true if argTableName is referenced by other table. return false otherwise.
	private static boolean checkIfReferenced(Cursor cursor, String argTableName) throws Exception {
		DatabaseEntry foundKey = new DatabaseEntry();
		DatabaseEntry foundData = new DatabaseEntry();

		// iterating over tables and check if it references argTableName
		if (cursor.getFirst(foundKey, foundData, LockMode.DEFAULT) == OperationStatus.SUCCESS) {
			do {
				String keyString = new String(foundKey.getData(), "UTF-8");
				String dataString = new String(foundData.getData(), "UTF-8");
				
				// create TableDefinition Object by dataString
				TableDefinition tableDefinition = new TableDefinition(keyString, dataString);
				
				if (tableDefinition.checkIfThisReferences(argTableName))
					return true;
				
			} while (cursor.getNext(foundKey, foundData, LockMode.DEFAULT) == OperationStatus.SUCCESS);
		}
		
		return false;
	}

	
	// desc table
	public static void executeDescTable(String argTableName) {
		// Environment & Database define
		Environment myDbEnvironment = null;
		Database myDatabase = null;

		/* OPENING DB */
		// Open Database Environment or if not, create one.
		EnvironmentConfig envConfig = new EnvironmentConfig();
		envConfig.setAllowCreate(true);
		myDbEnvironment = new Environment(new File("db/"), envConfig);

		// Open Database or if not, create one.
		DatabaseConfig dbConfig = new DatabaseConfig();
		dbConfig.setAllowCreate(true);
		dbConfig.setSortedDuplicates(true);
		myDatabase = myDbEnvironment.openDatabase(null, "dbschema", dbConfig);

		Cursor cursor = null;

		try {
			cursor = myDatabase.openCursor(null, null);
			
			String schemaValue = DatabaseOperation.searchTableSchemaByName(cursor, argTableName);
			
			if (schemaValue == null)
				System.out.println("No such table");
			else {
				TableDefinition tableDefinition = new TableDefinition(argTableName, schemaValue);
				
				tableDefinition.print();
			}
		} catch (Exception e) {
//			e.printStackTrace();
		}

		if (cursor != null) cursor.close();
		if (myDatabase != null) myDatabase.close();
		if (myDbEnvironment != null) myDbEnvironment.close();
	}

	
	// show tables
	public static void executeShowTable() {
		// Environment & Database define
		Environment myDbEnvironment = null;
		Database myDatabase = null;

		/* OPENING DB */
		// Open Database Environment or if not, create one.
		EnvironmentConfig envConfig = new EnvironmentConfig();
		envConfig.setAllowCreate(true);
		myDbEnvironment = new Environment(new File("db/"), envConfig);

		// Open Database or if not, create one.
		DatabaseConfig dbConfig = new DatabaseConfig();
		dbConfig.setAllowCreate(true);
		dbConfig.setSortedDuplicates(true);
		myDatabase = myDbEnvironment.openDatabase(null, "dbschema", dbConfig);

		Cursor cursor = null;

		try {
			cursor = myDatabase.openCursor(null, null);
			DatabaseEntry foundKey = new DatabaseEntry();
			DatabaseEntry foundData = new DatabaseEntry();

			if (cursor.getFirst(foundKey, foundData, LockMode.DEFAULT) == OperationStatus.SUCCESS) {
				System.out.println("----------------");

				do {
					String keyString = new String(foundKey.getData(), "UTF-8");
					System.out.println(keyString);
				} while (cursor.getNext(foundKey, foundData, LockMode.DEFAULT) == OperationStatus.SUCCESS);

				System.out.println("----------------");
			} else
				System.out.println("There is no table");

		} catch (Exception e) {
		}

		if (cursor != null) cursor.close();
		if (myDatabase != null) myDatabase.close();
		if (myDbEnvironment != null) myDbEnvironment.close();
	}
}
