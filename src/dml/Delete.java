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
import definition.ForeignKeyDefinition;
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
		Database myDatabase3 = null;

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
		Cursor cursor3 = null;
		
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
			int notDeletedCount = 0;
			
			if (cursor2.getFirst(foundKey, foundData, LockMode.DEFAULT) == OperationStatus.SUCCESS) {
				do {
					ArrayList<Value> valueList = Conversion.bytesToValues(columnsInfo,
							foundKey.getData(), foundData.getData());
					if (evaluationTree == null || evaluationTree.evaluate(valueList) == ThreeLogic.TRUE) {
						if (checkReferentialIntegrity(cursor, myDbEnvironment, dbConfig,
								myDatabase3, cursor3, tableName, columnsInfo, valueList)) {
							cursor2.delete();
							cascadeDelete(cursor, myDbEnvironment, dbConfig,
									myDatabase3, cursor3, tableName, columnsInfo, valueList);
							deletedCount++;
						}
						else {
							notDeletedCount++;
						}
					}
				} while (cursor2.getNext(foundKey, foundData, LockMode.DEFAULT) == OperationStatus.SUCCESS);
			}
			
			System.out.println(deletedCount + " row(s) are deleted");
			
			if (notDeletedCount > 0)
				System.out.println(notDeletedCount + " row(s) are not deleted due to referential integrity");
			
		} catch (Exception e) {
//			e.printStackTrace(); // TEST
		}

		if (cursor != null) cursor.close();
		if (cursor2 != null) cursor2.close();
		if (cursor3 != null) cursor3.close();
		if (myDatabase != null) myDatabase.close();
		if (myDatabase2 != null) myDatabase2.close();
		if (myDatabase3 != null) myDatabase3.close();
		if (myDbEnvironment != null) myDbEnvironment.close();
	}
	
	// check if the table that referencing tableName has Not Null foreign key exists. if not, return true.
	// return true if can delete, false if not.
	public static boolean checkReferentialIntegrity(Cursor cursor, Environment myDbEnvironment,
			DatabaseConfig dbConfig, Database myDatabase3, Cursor cursor3, String tableName,
			ArrayList<ColumnInfo> columnsInfo, ArrayList<Value> valueList) throws Exception {
		// TEST
//		for (int i = 0 ; i < valueList.size() ; i++) {
//			System.out.print(valueList.get(i).data + "		");
//		}
//		System.out.println();
		
		DatabaseEntry foundKey = new DatabaseEntry();
		DatabaseEntry foundData = new DatabaseEntry();

		// iterating over tables and check if it references tableName
		if (cursor.getFirst(foundKey, foundData, LockMode.DEFAULT) == OperationStatus.SUCCESS) {
			do {
				String keyString = new String(foundKey.getData(), "UTF-8");
				String dataString = new String(foundData.getData(), "UTF-8");
				
				// create TableDefinition Object by dataString
				TableDefinition tableDefinition = new TableDefinition(keyString, dataString);
				int index = -1;
				
				for (int i = 0 ; i < tableDefinition.foreignKeyDefinitions.size() ; i++) {
					ForeignKeyDefinition foreignKeyDefinition = tableDefinition.foreignKeyDefinitions.get(i);
					if (foreignKeyDefinition.referencedTableName.equals(tableName)) {
						index = i;
					}
				}
				
				if (index != -1) {
					ForeignKeyDefinition foreignKeyDefinition = tableDefinition.foreignKeyDefinitions.get(index);
					
					myDatabase3 = myDbEnvironment.openDatabase(null, "dbdata."+keyString, dbConfig);
					cursor3 = myDatabase3.openCursor(null, null);
					
					DatabaseEntry foundKey2 = new DatabaseEntry();
					DatabaseEntry foundData2 = new DatabaseEntry();
					
					ArrayList<ColumnInfo> columnsInfo2 = ColumnInfo.getColumnsInfoFromTableDef(tableDefinition);
					
					if (cursor3.getFirst(foundKey2, foundData2, LockMode.DEFAULT) == OperationStatus.SUCCESS) {
						do {
							String keyString2 = new String(foundKey2.getData(), "UTF-8");
							
							ArrayList<Value> valueList2 = Conversion.bytesToValues(columnsInfo2,
									foundKey2.getData(), foundData2.getData());
							
							// TEST
//							for (int i = 0 ; i < valueList2.size() ; i++) {
//								System.out.print(valueList2.get(i).data + "		");
//							}
							
							if (isForeignKeyEqual(foreignKeyDefinition, columnsInfo, valueList, columnsInfo2, valueList2)) {
								for (int j = 0 ; j < foreignKeyDefinition.referencingColumnNames.size() ; j++) {
									int index2 = tableDefinition.findColumn(foreignKeyDefinition.referencingColumnNames.get(j));
									if (tableDefinition.fieldDefinition.get(index2).notNullFlag) {
										if (cursor3 != null) cursor3.close();
										if (myDatabase3 != null) myDatabase3.close();
										return false;
									}
								}
							}
							
						} while (cursor3.getNext(foundKey2, foundData2, LockMode.DEFAULT) == OperationStatus.SUCCESS);
					}
					
					// re arrange valueList
					
					if (cursor3 != null) cursor3.close();
					if (myDatabase3 != null) myDatabase3.close();
				}
				
			} while (cursor.getNext(foundKey, foundData, LockMode.DEFAULT) == OperationStatus.SUCCESS);
		}
		
		return true;
	}
	
	// columnsInfo is referenced and columnsInfo2 is referencing
	public static boolean isForeignKeyEqual(ForeignKeyDefinition foreignKeyDefinition,
			ArrayList<ColumnInfo> columnsInfo, ArrayList<Value> valueList,
			ArrayList<ColumnInfo> columnsInfo2, ArrayList<Value> valueList2) {
		for (int i = 0 ; i < foreignKeyDefinition.referencingColumnNames.size() ; i++) {
			int index1 = ColumnInfo.getIndexFromColumnsInfoByName(columnsInfo,
					foreignKeyDefinition.referencedColumnNames.get(i));
			
			int index2 = ColumnInfo.getIndexFromColumnsInfoByName(columnsInfo2,
					foreignKeyDefinition.referencingColumnNames.get(i));
			
//			System.out.println(valueList.get(index1).data + "  " + valueList2.get(index2).data);
//			System.out.println(valueList.get(index1).isEqual(valueList2.get(index2)));
//			System.out.println(valueList.get(index1).data.length() + "  " + valueList2.get(index2).data.length());
			if (!valueList.get(index1).isEqual(valueList2.get(index2))) {
				return false;
			}			
		}
			
		return true;
	}
	
	public static void cascadeDelete(Cursor cursor, Environment myDbEnvironment,
	DatabaseConfig dbConfig, Database myDatabase3, Cursor cursor3, String tableName,
	ArrayList<ColumnInfo> columnsInfo, ArrayList<Value> valueList) throws Exception {
	// TEST
//		for (int i = 0 ; i < valueList.size() ; i++) {
//			System.out.print(valueList.get(i).data + "		");
//		}
//		System.out.println();
		
		DatabaseEntry foundKey = new DatabaseEntry();
		DatabaseEntry foundData = new DatabaseEntry();

		// iterating over tables and check if it references tableName
		if (cursor.getFirst(foundKey, foundData, LockMode.DEFAULT) == OperationStatus.SUCCESS) {
			do {
				String keyString = new String(foundKey.getData(), "UTF-8");
				String dataString = new String(foundData.getData(), "UTF-8");
				
				// create TableDefinition Object by dataString
				TableDefinition tableDefinition = new TableDefinition(keyString, dataString);
				int index = -1;
				
				for (int i = 0 ; i < tableDefinition.foreignKeyDefinitions.size() ; i++) {
					ForeignKeyDefinition foreignKeyDefinition = tableDefinition.foreignKeyDefinitions.get(i);
					if (foreignKeyDefinition.referencedTableName.equals(tableName)) {
						index = i;
					}
				}
				
				if (index != -1) {
					ForeignKeyDefinition foreignKeyDefinition = tableDefinition.foreignKeyDefinitions.get(index);
					
					myDatabase3 = myDbEnvironment.openDatabase(null, "dbdata."+keyString, dbConfig);
					cursor3 = myDatabase3.openCursor(null, null);
					
					DatabaseEntry foundKey2 = new DatabaseEntry();
					DatabaseEntry foundData2 = new DatabaseEntry();
					
					ArrayList<ColumnInfo> columnsInfo2 = ColumnInfo.getColumnsInfoFromTableDef(tableDefinition);
					
					if (cursor3.getFirst(foundKey2, foundData2, LockMode.DEFAULT) == OperationStatus.SUCCESS) {
						do {
							String keyString2 = new String(foundKey2.getData(), "UTF-8");
							
							ArrayList<Value> valueList2 = Conversion.bytesToValues(columnsInfo2,
									foundKey2.getData(), foundData2.getData());
							
							// TEST
//							for (int i = 0 ; i < valueList2.size() ; i++) {
//								System.out.print(valueList2.get(i).data + "		");
//							}
							
							if (isForeignKeyEqual(foreignKeyDefinition, columnsInfo, valueList, columnsInfo2, valueList2)) {
								for (int j = 0 ; j < foreignKeyDefinition.referencingColumnNames.size() ; j++) {
									cursor3.delete();
									int index2 = tableDefinition.findColumn(foreignKeyDefinition.referencingColumnNames.get(j));
									valueList2.get(index2).setNull();
									
									// check InsertDuplicatePrimaryKeyError and put data
									ArrayList<Byte> keyBytesArrayList = new ArrayList<Byte>();
									ArrayList<Byte> dataBytesArrayList = new ArrayList<Byte>();

									Conversion.getByteRepresentation(tableDefinition, valueList2, keyBytesArrayList, dataBytesArrayList);

									byte[] keyBytes = new byte[keyBytesArrayList.size()];
									for (int i = 0 ; i < keyBytes.length ; i++) {
										keyBytes[i] = keyBytesArrayList.get(i);
									}

									byte[] dataBytes = new byte[dataBytesArrayList.size()];
									for (int i = 0 ; i < dataBytes.length ; i++) {
										dataBytes[i] = dataBytesArrayList.get(i);
									}
									
									DatabaseEntry key = new DatabaseEntry(keyBytes);
									DatabaseEntry data = new DatabaseEntry(dataBytes);
									
									cursor3.put(key, data);
								}
							}
							
						} while (cursor3.getNext(foundKey2, foundData2, LockMode.DEFAULT) == OperationStatus.SUCCESS);
					}
					
					if (cursor3 != null) cursor3.close();
					if (myDatabase3 != null) myDatabase3.close();
				}
				
			} while (cursor.getNext(foundKey, foundData, LockMode.DEFAULT) == OperationStatus.SUCCESS);
		}
	}
}
