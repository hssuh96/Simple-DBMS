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
import dml.types.DataType;
import dml.types.ThreeLogic;

public class Select {
	private SelectedList selectedList;
	private ArrayList<ReferedTable> referedTableList;
	private EvaluationTree evaluationTree = null;
	
	public Select(SelectedList selectedList) {
		this.selectedList = selectedList;
	}
	
	public void setReferedTableList(ArrayList<ReferedTable> referedTableList) {
		this.referedTableList = referedTableList;
	}
	
	public void setEvaluationTree(EvaluationTree evaluationTree) {
		this.evaluationTree = evaluationTree;
	}
	
	public void executeSelect() {
		// Environment & Database define
		Environment myDbEnvironment = null;

		/* OPENING DB */
		// Open Database Environment or if not, create one.
		EnvironmentConfig envConfig = new EnvironmentConfig();
		envConfig.setAllowCreate(true);
		myDbEnvironment = new Environment(new File("db/"), envConfig);

		Database databaseSchema = null;
		
		// Open Database or if not, create one.
		DatabaseConfig dbConfig = new DatabaseConfig();
		dbConfig.setAllowCreate(true);
		dbConfig.setSortedDuplicates(true);
		databaseSchema = myDbEnvironment.openDatabase(null, "dbschema", dbConfig);
		
		Cursor cursor = null; 
		
		Database[] dataBaseData = new Database[referedTableList.size()];
		Cursor[] cursors = new Cursor[referedTableList.size()];
		
		try {
			cursor = databaseSchema.openCursor(null, null);
			
			ArrayList<TableDefinition> tableDefinitions = new ArrayList<TableDefinition>();
			
			// create tableDefinitions
			for (int i = 0 ; i < referedTableList.size() ; i++) {
				String schemaValue = DatabaseOperation.searchTableSchemaByName(cursor, referedTableList.get(i).tableName);
				
				if (schemaValue == null) {
					System.out.println("Selection has failed: '" + referedTableList.get(i).tableName + "' does not exist");
					throw new Exception();
				}
				else {
					tableDefinitions.add(new TableDefinition(referedTableList.get(i).tableName, schemaValue));
//					tableDefinitions.get(tableDefinitions.size()-1).print(); // TEST
				}
			}
			
			// columnsInfo is table renamed version
			ArrayList<ColumnInfo> columnsInfo = ColumnInfo.getColumnsInfoFromTableDef(tableDefinitions, referedTableList);
			WhereErrorFlag whereErrorFlag = new WhereErrorFlag();
			
			// TODO : TEST
//			for (int i = 0 ; i < columnsInfo.size() ; i++) {
//				columnsInfo.get(i).print();
//			}
			
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
			
			
			// create mapping information (name, index)
			SelectColumnResolveErrorFlag selectColumnResolveErrorFlag = new SelectColumnResolveErrorFlag();
			ArrayList<SelectColumnInfo> selectColumnsInfo = SelectColumnInfo.getSelectColumnsInfoFromSelectedList(
					selectedList, columnsInfo, selectColumnResolveErrorFlag);
			
			if (selectColumnResolveErrorFlag.selectColumnResolveErrorFlag) {
				System.out.println("Selection has failed: fail to resolve '"+ selectColumnResolveErrorFlag.columnName + "'");
				throw new Exception();
			}
			
			// save result tuples
			ArrayList<ArrayList<Value>> resultTuples = new ArrayList<ArrayList<Value>>();			
			
			
			// create database and open cursors
			for (int i = 0 ; i < referedTableList.size() ; i++) {
				dataBaseData[i] = myDbEnvironment.openDatabase(null, "dbdata."+referedTableList.get(i).tableName, dbConfig);
				cursors[i] = dataBaseData[i].openCursor(null, null);
			}
			
			DatabaseEntry[] foundKeys = new DatabaseEntry[referedTableList.size()];
			DatabaseEntry[] foundDatas = new DatabaseEntry[referedTableList.size()];
			
			// instantiate foundKeys and foundDatas
			for (int i = 0 ; i < foundKeys.length ; i++) {
				foundKeys[i] = new DatabaseEntry();
			}
			for (int i = 0 ; i < foundDatas.length ; i++) {
				foundDatas[i] = new DatabaseEntry();
			}
			
			
			// evaluate tuples and save result to resultTuples
			if (customGetFirst(cursors, foundKeys, foundDatas) == true) {
				do {
					byte[] keyBytes = concatBytes(foundKeys);
					byte[] dataBytes = concatBytes(foundDatas);
					
					ArrayList<Value> valueList = Conversion.bytesToValues(columnsInfo, keyBytes, dataBytes);
					
					// if condition satisfied
					if (evaluationTree == null || evaluationTree.evaluate(valueList) == ThreeLogic.TRUE) {
						ArrayList<Value> resultTuple = new ArrayList<Value>();
						
						for (int i = 0 ; i < selectColumnsInfo.size() ; i++) {
							resultTuple.add(valueList.get(selectColumnsInfo.get(i).index));
						}
						resultTuples.add(resultTuple);
					}
				} while (customGetNext(cursors, foundKeys, foundDatas) == true);
			}
			
			print(selectColumnsInfo, resultTuples);
		}
		catch (Exception e) {
//			e.printStackTrace();
		}
		
		if (cursor != null) cursor.close();
		if (databaseSchema != null) databaseSchema.close();
		for (int i = 0 ; i < referedTableList.size() ; i++) {
			if (cursors[i] != null) cursors[i].close();
			if (dataBaseData[i] != null) dataBaseData[i].close();
		}
		if (myDbEnvironment != null) myDbEnvironment.close();
	}
	
	// return true if succeed, false otherwise.
	public boolean customGetFirst(Cursor[] cursors, DatabaseEntry[] foundKeys, DatabaseEntry[] foundDatas) {
		for (int i = 0 ; i < cursors.length ; i++) {
			if (cursors[i].getFirst(foundKeys[i], foundDatas[i], LockMode.DEFAULT) != OperationStatus.SUCCESS)
				return false;
		}
			
		return true;
	}

	//	return true if succeed, false otherwise.
	public boolean customGetNext(Cursor[] cursors, DatabaseEntry[] foundKeys, DatabaseEntry[] foundDatas) {
		for (int i = cursors.length-1 ; i >= 0 ; i--) {
			if (cursors[i].getNext(foundKeys[i], foundDatas[i], LockMode.DEFAULT) == OperationStatus.SUCCESS)
				return true;
			else // it was last one
				cursors[i].getFirst(foundKeys[i], foundDatas[i], LockMode.DEFAULT);
		}
		
		return false;
	}
	
	public byte[] concatBytes(DatabaseEntry[] databaseEntrys) {
		ArrayList<Byte> byteArrayList = new ArrayList<Byte>();
		for (int i = 0 ; i < databaseEntrys.length ; i++) {
			byte[] byteArray = databaseEntrys[i].getData();
			for (int j = 0 ; j < byteArray.length ; j++)
				byteArrayList.add(new Byte(byteArray[j]));
		}
		
		byte[] resultByte = new byte[byteArrayList.size()];
		
		for (int i = 0 ; i < resultByte.length ; i++) {
			resultByte[i] = byteArrayList.get(i);
		}
		
		return resultByte;
	}
	
	public void print(ArrayList<SelectColumnInfo> selectColumnsInfo, ArrayList<ArrayList<Value>> resultTuples) {
		// compute margin
		int[] margin = new int[selectColumnsInfo.size()];
		
		// initialize margin with column name size
		for (int i = 0 ; i < selectColumnsInfo.size() ; i++) {
			margin[i] = selectColumnsInfo.get(i).columnName.length();
		}
		
		// compare with value size and update margin
		for (int i = 0 ; i < resultTuples.size() ; i++) {
			ArrayList<Value> resultTuple = resultTuples.get(i);
			for (int j = 0 ; j < resultTuple.size() ; j++) {
				if (resultTuple.get(j).dataType == DataType.NULL) {
					if (margin[j] < 4)
						margin[j] = 4;
				}
				else {
					if (resultTuple.get(j).data.length() > margin[j]) {
						margin[j] = resultTuple.get(j).data.length();
					}
				}
			}
		}
		
		// create string line
		String stringLine = "";
		for (int i = 0 ; i < margin.length ; i++) {
			stringLine += "+";
			for (int j = 0 ; j < margin[i]+2 ; j++)
				stringLine += "-";
		}
		stringLine += "+";
		

		System.out.println(stringLine);
//		System.out.printf("| %-20s | %-15s | %-10s%-10s", "colum_name", "type", "null", "key");
		for (int i = 0 ; i < selectColumnsInfo.size() ; i++) {
			System.out.printf("| %-"+margin[i]+"s ", selectColumnsInfo.get(i).columnName);
		}
		System.out.println("|");
		System.out.println(stringLine);
		for (int i = 0 ; i < resultTuples.size() ; i++) {
			ArrayList<Value> resultTuple = resultTuples.get(i);
			for (int j = 0 ; j < resultTuple.size() ; j++) {
				System.out.printf("| %-"+margin[j]+"s ", resultTuple.get(j).data);
			}
			System.out.println("|");
		}
		System.out.println(stringLine);
	}
}
