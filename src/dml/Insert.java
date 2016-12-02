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
import definition.ForeignKeyDefinition;
import definition.TableDefinition;
import dml.types.DataType;

public class Insert {
	private String tableName;
	private ArrayList<Value> valueList = null;
	private ArrayList<String> columnNameList = null;
	private ArrayList<String> newColumnNameList = null;

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
		//TEST
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

		Database myDatabase3 = null;
		
		Cursor cursor = null;
		Cursor cursor2 = null;
		Cursor cursorReferTable = null;

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
				newColumnNameList = new ArrayList<String>();
				if (valueList.size() != tableDefinition.fieldDefinition.size()) { // check size
					System.out.println("Insertion has failed: Types are not matched");
					throw new Exception();
				}
				for (int i = 0 ; i < valueList.size() ; i++) { // same as valueList
					valueListOrdered.add(valueList.get(i));
					newColumnNameList.add(tableDefinition.fieldDefinition.get(i).columnName);
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
					int index = findValueIndexByColumnName(columnNameList, tableDefinition.fieldDefinition.get(i).columnName);
					
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
					valueListOrdered.get(i).data =
							valueListOrdered.get(i).data.substring(0, tableDefinition.fieldDefinition.get(i).getCharLength());
				}
			}

			//TEST
//			for (int i = 0 ; i < valueListOrdered.size() ; i++) {
//				valueListOrdered.get(i).print();
//			}


			// check InsertReferentialIntegrityError
			for (int i = 0 ; i < tableDefinition.foreignKeyDefinitions.size() ; i++) {
				ForeignKeyDefinition foreignKeyDefinition = tableDefinition.foreignKeyDefinitions.get(i);
				ArrayList<Value> foreignKeyValueListOrdered = new ArrayList<Value>();
				
				String referedTableschemaValue = DatabaseOperation.searchTableSchemaByName(cursor,
						foreignKeyDefinition.referencedTableName);
				
				if (schemaValue == null) {
					System.out.println("Unexpected Error : there is no refered table");
					throw new Exception();
				}
				TableDefinition referedTableDefinition = new TableDefinition(foreignKeyDefinition.referencedTableName,
						referedTableschemaValue);
				
				boolean foreignKeyNullFlag = false;
				// get foreignKeyValueListOrdered
				for (int j = 0 ; j < referedTableDefinition.fieldDefinition.size() ; j++) {
					if (referedTableDefinition.fieldDefinition.get(j).primaryKeyFlag) {
						String currColumnName = findColumnNameOfThisForeignKey(foreignKeyDefinition,
								referedTableDefinition.fieldDefinition.get(j).columnName);
						int index = findValueIndexByColumnName(newColumnNameList, currColumnName);
						if (index == -1) { // not found, put NULL
							foreignKeyValueListOrdered.add(new Value());
							foreignKeyNullFlag = true;
						}
						else {
							if (valueList.get(index).dataType == DataType.NULL)
								foreignKeyNullFlag = true;
							foreignKeyValueListOrdered.add(valueList.get(index)); // put valueList[index]
						}
					}
				}
				if (!foreignKeyNullFlag) {
					byte[] foreignKeyBytes = Conversion.getByteRepresentation(referedTableDefinition, foreignKeyValueListOrdered);
					
					myDatabase3 = myDbEnvironment.openDatabase(null, "dbdata."+referedTableDefinition.tableName, dbConfig);
					cursorReferTable = myDatabase3.openCursor(null, null);
					
					DatabaseEntry KeyForSearch = new DatabaseEntry(foreignKeyBytes);
					DatabaseEntry foundData = new DatabaseEntry();
					
					if (cursorReferTable.getSearchKey(KeyForSearch, foundData, null) != OperationStatus.SUCCESS) {
						System.out.println("Insertion has failed: Referential integrity violation");
						throw new Exception();
					}
				}
			}
			
			// check InsertDuplicatePrimaryKeyError and put data
			ArrayList<Byte> keyBytesArrayList = new ArrayList<Byte>();
			ArrayList<Byte> dataBytesArrayList = new ArrayList<Byte>();
			
			Conversion.getByteRepresentation(tableDefinition, valueListOrdered, keyBytesArrayList, dataBytesArrayList);
			
			byte[] keyBytes = new byte[keyBytesArrayList.size()];
			for (int i = 0 ; i < keyBytes.length ; i++) {
				keyBytes[i] = keyBytesArrayList.get(i);
			}
			
			byte[] dataBytes = new byte[dataBytesArrayList.size()];
			for (int i = 0 ; i < dataBytes.length ; i++) {
				dataBytes[i] = dataBytesArrayList.get(i);
			}
			
			// TEST
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
			
//			System.out.println(new String(key.getData())); //TEST
			
			if (key.getData().length == 0) {
				byte[] keyAlternative = new byte[4];
				int dataInt = 0;
				keyAlternative[0] = (byte)(dataInt >> 24);
				keyAlternative[1] = (byte)(dataInt >> 16);
				keyAlternative[2] = (byte)(dataInt >> 8);
				keyAlternative[3] = (byte)(dataInt);
				
				while(cursor2.putNoOverwrite(new DatabaseEntry(keyAlternative), data) == OperationStatus.KEYEXIST) {
					dataInt++;
					keyAlternative[0] = (byte)(dataInt >> 24);
					keyAlternative[1] = (byte)(dataInt >> 16);
					keyAlternative[2] = (byte)(dataInt >> 8);
					keyAlternative[3] = (byte)(dataInt);
				}
				
				System.out.println("The row is inserted");
			}
			else {
				if (cursor2.putNoOverwrite(key, data) == OperationStatus.KEYEXIST)
					System.out.println("Insertion has failed: Primary key duplication");
				else
					System.out.println("The row is inserted");
			}
		}
		catch (Exception e) {
//			e.printStackTrace();
		}
		
		if (cursor != null) cursor.close();
		if (cursor2 != null) cursor2.close();
		if (cursorReferTable != null) cursorReferTable.close();
		if (myDatabase != null) myDatabase.close();
		if (myDatabase2 != null) myDatabase2.close();
		if (myDatabase3 != null) myDatabase3.close();
		if (myDbEnvironment != null) myDbEnvironment.close();
	}
	
	public int findValueIndexByColumnName(ArrayList<String> columnNameList, String columnName) {
		for (int i = 0 ; i < columnNameList.size() ; i++) {
			if (columnNameList.get(i).equals(columnName)) {
				return i;
			}
		}
		
		return -1;
	}
	
	public String findColumnNameOfThisForeignKey(ForeignKeyDefinition foreignKeyDefinition, String referedColumnName) {
		for (int i = 0 ; i < foreignKeyDefinition.referencedColumnNames.size() ; i++) {
			if (foreignKeyDefinition.referencedColumnNames.get(i).equals(referedColumnName))
				return foreignKeyDefinition.referencingColumnNames.get(i);
		}
		
		return null;
	}
}