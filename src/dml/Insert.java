package dml;

import java.io.File;
import java.util.ArrayList;

import com.sleepycat.je.Cursor;
import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;
import com.sleepycat.je.OperationStatus;

import databaseoperation.DatabaseOperation;
import definition.TableDefinition;
import dml.types.DataType;

public class Insert {
	private String tableName;
	private ArrayList<Value> valueList = null;
	private ArrayList<String> columnNameList = null;

	public void setTableName(String str) {
		tableName = str;
	}

	public void setValueList(ArrayList<Value> valueList) {
		this.valueList = valueList;
	}

	public void setColumnNameList(ArrayList<String> columnNameList) {
		this.columnNameList = columnNameList;
	}

	public void executeInsert() {
		//TODO : TEST
//		System.out.println("executeInsert called");
//		System.out.println(tableName);
//		if (columnNameList != null) {
//			for (int i = 0 ; i < columnNameList.size() ; i++) {
//				System.out.println(columnNameList.get(i));
//			}
//		}
//		for (int i = 0 ; i < valueList.size() ; i++) {
//			valueList.get(i).print();
//		}

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

			if (schemaValue == null) {
				System.out.println("No such table");
				throw new Exception();
			}
			TableDefinition tableDefinition = new TableDefinition(tableName, schemaValue);
			
			
			ArrayList<Value> valueListOrdered = new ArrayList<Value>(); // valueList with ordering on table definition
			
			// ordering valueListOrdered
			if (columnNameList == null) { // columns not specified
				if (valueList.size() != tableDefinition.fieldDefinition.size()) { // check size
					System.out.println("Insertion has failed: Types are not matched");
					throw new Exception();
				}
				for (int i = 0 ; i < valueList.size() ; i++) { // same as valueList
					valueListOrdered.add(valueList.get(i));
				}
			}
			else { // columns specified
				// check if column exists
				for (int i = 0 ; i < columnNameList.size() ; i++) {
					if (tableDefinition.findColumn(columnNameList.get(i)) == -1){
						System.out.println("Insertion has failed: '"+columnNameList.get(i)+"' does not exist");
						throw new Exception();
					}
				}
				
				// check if size of valueList and columnNameList not match
				if (valueList.size() != columnNameList.size()) {
					System.out.println("Insertion has failed: Types are not matched");
					throw new Exception();
				}
				
				for (int i = 0 ; i < tableDefinition.fieldDefinition.size() ; i++) {
					int index = findValueIndexByColumnName(tableDefinition.fieldDefinition.get(i).columnName);
					
					if (index == -1) { // not found, put NULL
						valueListOrdered.add(new Value());
					}
					else {
						valueListOrdered.add(valueList.get(index)); // put valueList[index]
					}
				}
			}
			
			// check InsertTypeMismatchError and InsertColumnNonNullableError
			for (int i = 0 ; i < valueListOrdered.size() ; i++) {
				if (valueListOrdered.get(i).dataType == DataType.NULL) {
					if (tableDefinition.fieldDefinition.get(i).notNullFlag) {
						System.out.println("Insertion has failed: '"
								+ tableDefinition.fieldDefinition.get(i).columnName + "' is not nullable");
						throw new Exception();
					}
				}
				else if (valueListOrdered.get(i).dataType == DataType.INT) {
					if (!tableDefinition.fieldDefinition.get(i).dataType.equals("int")) {
						System.out.println("Insertion has failed: Types are not matched");
						throw new Exception();
					}
				}
				else if (valueListOrdered.get(i).dataType == DataType.CHAR) {
					if (!(tableDefinition.fieldDefinition.get(i).dataType.length() > 4)) {
						System.out.println("Insertion has failed: Types are not matched");
						throw new Exception();
					}
				}
				else if (valueListOrdered.get(i).dataType == DataType.DATE) {
					if (!tableDefinition.fieldDefinition.get(i).dataType.equals("date")) {
						System.out.println("Insertion has failed: Types are not matched");
						throw new Exception();
					}
				}
			}

			// truncate long chars
			for (int i = 0 ; i < valueListOrdered.size() ; i++) {
				if (valueListOrdered.get(i).dataType == DataType.CHAR
						&& valueListOrdered.get(i).data.length() > tableDefinition.fieldDefinition.get(i).getCharLength()) {
					System.out.println(valueListOrdered.get(i).data);
					valueListOrdered.get(i).data =
							valueListOrdered.get(i).data.substring(0, tableDefinition.fieldDefinition.get(i).getCharLength());
				}
			}

			// TODO : TEST
//			for (int i = 0 ; i < valueListOrdered.size() ; i++) {
//				valueListOrdered.get(i).print();
//			}


			// TODO :check InsertReferentialIntegrityError
			
			// check InsertDuplicatePrimaryKeyError and put data
			ArrayList<Byte> keyBytesArrayList = new ArrayList<Byte>();
			ArrayList<Byte> dataBytesArrayList = new ArrayList<Byte>();
			
			getByteRepresentation(tableDefinition, valueListOrdered, keyBytesArrayList, dataBytesArrayList);
			
			byte[] keyBytes = new byte[keyBytesArrayList.size()];
			for (int i = 0 ; i < keyBytes.length ; i++) {
				keyBytes[i] = keyBytesArrayList.get(i);
			}
			
			byte[] dataBytes = new byte[dataBytesArrayList.size()];
			for (int i = 0 ; i < dataBytes.length ; i++) {
				dataBytes[i] = dataBytesArrayList.get(i);
			}
			
			// TODO : TEST
//			for (int i = 0 ; i < keyBytes.length ; i++) {
//				System.out.print(keyBytes[i] + " ");
//			}
//			System.out.println();
//			for (int i = 0 ; i < dataBytes.length ; i++) {
//				System.out.print(dataBytes[i] + " ");
//			}
//			System.out.println();
			
			
			cursor2 = myDatabase2.openCursor(null, null);
			
			DatabaseEntry key = new DatabaseEntry(keyBytes);
			DatabaseEntry data = new DatabaseEntry(dataBytes);

			if (cursor2.putNoOverwrite(key, data) == OperationStatus.KEYEXIST)
				System.out.println("Insertion has failed: Primary key duplication");
			else
				System.out.println("The row is inserted");
			
		}
		catch (Exception e) {
//			e.printStackTrace();
		}
		
		if (cursor != null) cursor.close();
		if (cursor2 != null) cursor2.close();
		if (myDatabase != null) myDatabase.close();
		if (myDatabase2 != null) myDatabase2.close();
		if (myDbEnvironment != null) myDbEnvironment.close();
	}
	
	public int findValueIndexByColumnName(String columnName) {
		for (int i = 0 ; i < columnNameList.size() ; i++) {
			if (columnNameList.get(i).equals(columnName)) {
				return i;
			}
		}
		
		return -1;
	}
	
	public void getByteRepresentation(TableDefinition tableDefinition, ArrayList<Value> valueListOrdered,
			ArrayList<Byte> keyBytesArrayList, ArrayList<Byte> dataBytesArrayList) {
		for (int i = 0 ; i < valueListOrdered.size() ; i++) {
			byte[] byteArray = valueListOrdered.get(i).dataToByte(tableDefinition, i);
			
			if (tableDefinition.fieldDefinition.get(i).primaryKeyFlag) {
				for (int j = 0 ; j < byteArray.length ; j++) {
					keyBytesArrayList.add(new Byte(byteArray[j]));
				}
			}
			else {
				for (int j = 0 ; j < byteArray.length ; j++) {
					dataBytesArrayList.add(new Byte(byteArray[j]));
				}
			}
		}
	}
}