package dml;

import java.io.File;
import java.util.ArrayList;

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
import dml.types.ThreeLogic;

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
			
			
			// check WhereError
			ArrayList<ColumnInfo> columnsInfo = ColumnInfo.getColumnsInfoFromTableDef(tableDefinition);
			WhereErrorFlag whereErrorFlag = new WhereErrorFlag();
			
			if (evaluationTree != null) {
				evaluationTree.checkErrorAndUpdateInfo(columnsInfo, whereErrorFlag);
				
				if (whereErrorFlag.WhereTableNotSpecifiedFlag) {
					System.out.println("Where clause try to reference tables which are not specified");
					throw new Exception();
				}
				if (whereErrorFlag.WhereColumnNotExistFlag) {
					System.out.println("Where clause try to reference non existing column");
					throw new Exception();
				}
				if (whereErrorFlag.WhereAmbiguousReferenceFlag) {
					System.out.println("Where clause contains ambiguous reference");
					throw new Exception();
				}
				if (whereErrorFlag.WhereIncomparableErrorFlag) {
					System.out.println("Where clause try to compare incomparable values");
					throw new Exception();
				}
			}
			
			cursor2 = myDatabase2.openCursor(null, null);
			
			DatabaseEntry foundKey = new DatabaseEntry();
			DatabaseEntry foundData = new DatabaseEntry();
			
			int deletedCount = 0;
			
			if (cursor2.getFirst(foundKey, foundData, LockMode.DEFAULT) == OperationStatus.SUCCESS) {
				do {
					ArrayList<Value> valueList = Conversion.bytesToValues(columnsInfo,
							foundKey.getData(), foundData.getData());
					if (evaluationTree == null || evaluationTree.evaluate(valueList) == ThreeLogic.TRUE) {
						// TODO : should check referential integrity
						cursor2.delete();
						deletedCount++;
					}
				} while (cursor2.getNext(foundKey, foundData, LockMode.DEFAULT) == OperationStatus.SUCCESS);
			}
			
			System.out.println(deletedCount + "row(s) are deleted");
			
		} catch (Exception e) {
//			e.printStackTrace(); // TODO
		}

		if (cursor != null) cursor.close();
		if (cursor2 != null) cursor2.close();
		if (myDatabase != null) myDatabase.close();
		if (myDatabase2 != null) myDatabase2.close();
		if (myDbEnvironment != null) myDbEnvironment.close();
	}
}
