package dml;

import java.io.File;

import com.sleepycat.je.Cursor;
import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;
import com.sleepycat.je.LockMode;
import com.sleepycat.je.OperationStatus;

import databaseoperation.DatabaseOperation;
import definition.TableDefinition;
import dml.booleantree.EvaluationTree;

public class Delete {
	private String tableName;
	private EvaluationTree evaluationTree = null;
	
	// constructors
	public Delete(String tableName) {
		this.tableName = tableName;
	}
	
	public Delete(String tableName, EvaluationTree evaluationTree) {
		this.tableName = tableName;
		this.evaluationTree = evaluationTree;
	}
	
	
	public void executeDelete() {
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
		
		// Open Database or if not, create one.
		DatabaseConfig dbConfig2 = new DatabaseConfig();
		dbConfig2.setAllowCreate(true);
		dbConfig2.setSortedDuplicates(true);
		myDatabase2 = myDbEnvironment.openDatabase(null, "dbdata."+tableName, dbConfig);

		Cursor cursor = null;
		Cursor cursor2 = null;
		
		try {
			cursor = myDatabase.openCursor(null, null);
			
			String schemaValue = DatabaseOperation.searchTableSchemaByName(cursor, tableName);

			// check NoSuchTable
			if (schemaValue == null) {
				System.out.println("No such table");
				throw new Exception();
			}
			TableDefinition tableDefinition = new TableDefinition(tableName, schemaValue);
			
			
			// check WhereIncomparableError
			
			
			
			cursor2 = myDatabase2.openCursor(null, null);
			
//			DatabaseEntry foundKey = new DatabaseEntry();
//			DatabaseEntry foundData = new DatabaseEntry();

//			if (cursor.getFirst(foundKey, foundData, LockMode.DEFAULT) == OperationStatus.SUCCESS) {
//				do {
//					String keyString = new String(foundKey.getData(), "UTF-8");
//					System.out.println(keyString);
//				} while (cursor.getNext(foundKey, foundData, LockMode.DEFAULT) == OperationStatus.SUCCESS);
//			} else
//				System.out.println("There is no table");
		} catch (Exception e) {
		}

		if (cursor != null) cursor.close();
		if (cursor2 != null) cursor2.close();
		if (myDatabase != null) myDatabase.close();
		if (myDatabase2 != null) myDatabase2.close();
		if (myDbEnvironment != null) myDbEnvironment.close();
	}
}
