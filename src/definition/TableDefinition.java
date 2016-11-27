package definition;

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

public class TableDefinition {
	private String tableName;
	private ArrayList<ColumnDefinition> fieldDefinition;
	private ArrayList<String> primaryKeyDefinition;
	private ArrayList<ForeignKeyDefinition> foreignKeyDefinitions;
	
	private boolean DuplicateColumnDefErrorFlag = false; // duplicated column definition
	private boolean DuplicatePrimaryKeyDefErrorFlag = false; // duplicated primary key definition
	
	// reference error
	private boolean ReferenceTypeErrorFlag = false;
	private boolean ReferenceNonPrimaryKeyErrorFlag = false;
	private boolean ReferenceColumnExistenceErrorFlag = false;
	private boolean ReferenceTableExistenceErrorFlag = false;
	
	private boolean NonExistingColumnDefErrorFlag = false;
	String NonExistingColumnName;
	private boolean CharLengthErrorFlag = false;
			
	
	public TableDefinition() {
		tableName = "";
		fieldDefinition = new ArrayList<ColumnDefinition>();
		primaryKeyDefinition = new ArrayList<String>();
		foreignKeyDefinitions = new ArrayList<ForeignKeyDefinition>();
	}
	
	public TableDefinition(String tableName, String schemaValue) {
		this.tableName = tableName;
		fieldDefinition = new ArrayList<ColumnDefinition>();
		primaryKeyDefinition = new ArrayList<String>();
		foreignKeyDefinitions = new ArrayList<ForeignKeyDefinition>();
		
		String[] arr = schemaValue.split(";");
		for (int i = 0 ; i < arr.length ; i++) {
			fieldDefinition.add(new ColumnDefinition(arr[i]));
		}
		
		for (int i = 0 ; i < fieldDefinition.size() ; i++) {
			ColumnDefinition column = fieldDefinition.get(i);
			if (column.primaryKeyFlag == true)
				primaryKeyDefinition.add(column.columnName);
			
			if (column.foreignKeyFlag == true) {
				String[] arr2 = column.referencedColumnName.split("\\.");
				addForeignKey(arr2[0], column.columnName, arr2[1]);
			}
		}
	}
	
	private void addForeignKey(String referencedTableName, String referencingColumnName,
			String referencedColumnName) {
		int index = -1;
		for (int i = 0 ; i < foreignKeyDefinitions.size() ; i++) {
			if (foreignKeyDefinitions.get(i).referencedTableName.equals(referencedTableName)) {
				index = i;
				break;
			}
		}
		
		if (index == -1) {
			ArrayList<String> referencingColumnNames = new ArrayList<String>();
			ArrayList<String> referencedColumnNames = new ArrayList<String>();
			referencingColumnNames.add(referencingColumnName);
			referencedColumnNames.add(referencedColumnName);
			
			foreignKeyDefinitions.add(new ForeignKeyDefinition(referencedTableName, 
					referencingColumnNames, referencedColumnNames));
		}
		else {
			foreignKeyDefinitions.get(index).referencingColumnNames.add(referencingColumnName);
			foreignKeyDefinitions.get(index).referencedColumnNames.add(referencedColumnName);
		}
	}
	
	public void setTableName(String str) {
		tableName = str;
	}
	
	public String getTableName() {
		return tableName;
	}
	
	// add column
	public void addColumnDefinition(String columnName, String dataType, boolean notNullFlag) {
		if(dataType.length() > 4 && Integer.parseInt(dataType.substring(5, dataType.length()-1)) < 1)
			CharLengthErrorFlag = true;
		
		fieldDefinition.add(new ColumnDefinition(columnName, dataType, notNullFlag));
	}
	
	// set primary key. If called more than twice, set DuplicatePrimaryKeyDefErrorFlag
	public void setPrimaryKeyDefinition(ArrayList<String> primaryKeyList) {
		if(primaryKeyDefinition.size() != 0)
			DuplicatePrimaryKeyDefErrorFlag = true;
		
		primaryKeyDefinition = primaryKeyList;
	}
	
	// add foreign key
	public void addForeignKeydefinition(String referencedTableName, ArrayList<String> referencingColumnNames,
			ArrayList<String> referencedColumnNames) {
		foreignKeyDefinitions.add(new ForeignKeyDefinition(referencedTableName, referencingColumnNames,
				referencedColumnNames));
	}
	
	// return true if this table references argTableName. return false otherwise.
	public boolean checkIfThisReferences(String argTableName) {
		for (int i = 0 ; i < foreignKeyDefinitions.size() ; i++) {
			if (foreignKeyDefinitions.get(i).referencedTableName.equals(argTableName))
				return true;
		}
		return false;
	}
	
	// check validity for column, primary key, foreign key definition
	public boolean checkValidity() {
		setDuplicateColumnDefErrorFlag();
		if (DuplicateColumnDefErrorFlag == true)
			return false;
		
		setPrimaryKeyFlag();
		setForeignKeyFlag();
		
		return !(DuplicateColumnDefErrorFlag || DuplicatePrimaryKeyDefErrorFlag
				|| ReferenceTypeErrorFlag || ReferenceNonPrimaryKeyErrorFlag
				|| ReferenceColumnExistenceErrorFlag || ReferenceTableExistenceErrorFlag
				|| NonExistingColumnDefErrorFlag || CharLengthErrorFlag);
	}
	
	// set DuplicateColumnDefErrorFlag
	private void setDuplicateColumnDefErrorFlag() {
		for (int i = 0 ; i < fieldDefinition.size()-1 ; i++) {
			for (int j = i+1 ; j < fieldDefinition.size() ; j++) {
				if (fieldDefinition.get(i).columnName.equals(fieldDefinition.get(j).columnName)) {
					DuplicateColumnDefErrorFlag = true;
					return;
				}
			}
		}
	}
	
	// find column by columnName and return index. return -1 if column not found.
	public int findColumn(String str) {
		for (int i = 0 ; i < fieldDefinition.size() ; i++) {
			if (fieldDefinition.get(i).columnName.equals(str)) {
				return i;
			}
		}
		
		return -1;
	}
	
	// set PrimaryKeyFlag for ColumnDefinition
	private void setPrimaryKeyFlag() {
		for(int i = 0 ; i < primaryKeyDefinition.size() ; i++) {
			int index = findColumn(primaryKeyDefinition.get(i));
			if (index == -1) {
				NonExistingColumnDefErrorFlag = true;
				NonExistingColumnName = primaryKeyDefinition.get(i);
				return;
			}
			
			fieldDefinition.get(index).primaryKeyFlag = true;
			fieldDefinition.get(index).notNullFlag = true;
		}
	}
	
   // set ForeignKeyFlag for ColumnDefinition
	private void setForeignKeyFlag() {
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
			
			for(int i = 0 ; i < foreignKeyDefinitions.size() ; i++) { // for each foreign key definition
				ForeignKeyDefinition foreignKeyDefinition = foreignKeyDefinitions.get(i);
				
				// get schemaValue for referencedTable
				String schemaValue = DatabaseOperation.searchTableSchemaByName(cursor,
						foreignKeyDefinition.referencedTableName);
				
				// ReferenceTableExistenceError
				if (schemaValue == null) {
					ReferenceTableExistenceErrorFlag = true;
					break;
				}
				
				// create TableDefinition Object by schemaValue
				TableDefinition referencedTableDefinition = new TableDefinition(
						foreignKeyDefinition.referencedTableName, schemaValue);
				
				// check if referencingColumnNames and referencedColumnNames has different size
				if (!ReferenceTypeErrorFlag && foreignKeyDefinition.referencingColumnNames.size() !=
						foreignKeyDefinition.referencedColumnNames.size()) {
					ReferenceTypeErrorFlag = true;
					break;
				}
				
				// check for each attribute in foreignKeyDefinition and update
				for (int j = 0 ; j < foreignKeyDefinition.referencingColumnNames.size() ; j++) {
					String referencingColumnName = foreignKeyDefinition.referencingColumnNames.get(j);
					String referencedColumnName = foreignKeyDefinition.referencedColumnNames.get(j);
					
					int referencingColumnIndex = this.findColumn(referencingColumnName);
					int referencedColumnIndex = referencedTableDefinition.findColumn(referencedColumnName);
					
					// check NonExistingColumnDefError
					if (referencingColumnIndex == -1) {
						NonExistingColumnDefErrorFlag = true;
						NonExistingColumnName = referencingColumnName;
						break;
					}
					
					// check ReferenceColumnExistenceError
					if (referencedColumnIndex == -1) {
						ReferenceColumnExistenceErrorFlag = true;
						break;
					}
					
					// check ReferenceTypeError
					if (!this.fieldDefinition.get(referencingColumnIndex).dataType.equals(
							referencedTableDefinition.fieldDefinition.get(referencedColumnIndex).dataType)) {
						ReferenceTypeErrorFlag = true;
					}
					
					// check ReferenceNonPrimaryKeyError (is this attribute primary key)
					if (!referencedTableDefinition.fieldDefinition.get(referencedColumnIndex).primaryKeyFlag) {
						ReferenceNonPrimaryKeyErrorFlag = true;
					}
					
					this.fieldDefinition.get(referencingColumnIndex).foreignKeyFlag = true;
					this.fieldDefinition.get(referencingColumnIndex).referencedColumnName =
							foreignKeyDefinition.referencedTableName + "." + referencedColumnName;
				}
				
				//TODO
				// check ReferenceNonPrimaryKeyError (is referencedColumnNames contains all attributes of primary key)
				if (foreignKeyDefinition.referencedColumnNames.size() !=
						referencedTableDefinition.primaryKeyDefinition.size()) {
					ReferenceNonPrimaryKeyErrorFlag = true;
					break;
				}
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if (cursor != null) cursor.close();
		if (myDatabase != null) myDatabase.close();
		if (myDbEnvironment != null) myDbEnvironment.close();
	}
	
	// get value String for database
	public String getSchemaValue() {
		String str = "";
		
		for (int i = 0 ; i < fieldDefinition.size() ; i++) {
			ColumnDefinition column = fieldDefinition.get(i);
			
			str += column.columnName + "/";
			str += column.dataType + "/";
			
			if (column.notNullFlag)
				str += "1/";
			else
				str += "0/";
			
			if (column.primaryKeyFlag)
				str += "1/";
			else
				str += "0/";
			
			if (column.foreignKeyFlag) {
				str += "1/";
				str += column.referencedColumnName;
				str += ";";
			}
			else {
				str += "0;";
			}
		}
		
//		System.out.println("schema value: " + str);
		return str;
	}
	
	public void printErrorMessage() {
		if (DuplicateColumnDefErrorFlag)
			System.out.println("Create table has failed: column definition is duplicated");
		
		if (DuplicatePrimaryKeyDefErrorFlag)
			System.out.println("Create table has failed: primary key definition is duplicated");
		
		if (ReferenceTypeErrorFlag)
			System.out.println("Create table has failed: foreign key references wrong type");
		
		if (ReferenceNonPrimaryKeyErrorFlag)
			System.out.println("Create table has failed: foreign key references non primary key column");
		
		if (ReferenceColumnExistenceErrorFlag)
			System.out.println("Create table has failed: foreign key references non existing column");
		
		if (ReferenceTableExistenceErrorFlag)
			System.out.println("Create table has failed: foreign key references non existing table");
		
		if (NonExistingColumnDefErrorFlag)
			System.out.println("Create table has failed: '" + NonExistingColumnName
					+"' does not exists in column definition");
		
		if (CharLengthErrorFlag)
			System.out.println("Char length should be over 0");
	}
	
	public void print() {
		System.out.println("-------------------------------------------------");
		System.out.println("table_name [" + tableName + "]");
		System.out.printf("%-20s%-15s%-10s%-10s", "colum_name", "type", "null", "key");
		System.out.println();
		
		for (int i = 0 ; i < fieldDefinition.size() ; i++) {
			fieldDefinition.get(i).print();
		}
		
		System.out.println("-------------------------------------------------");
		
//		System.out.print("primary key: ");
//		for (int i = 0 ; i < primaryKeyDefinition.size() ; i++) {
//			System.out.print(primaryKeyDefinition.get(i) + ", ");
//		}
//		System.out.println();
//		
//		for (int i = 0 ; i < foreignKeyDefinitions.size() ; i++) {
//			foreignKeyDefinitions.get(i).print();
//			System.out.println();
//		}
	}
}
