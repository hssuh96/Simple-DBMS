package dml;

import java.util.ArrayList;

import definition.ColumnDefinition;
import definition.TableDefinition;
import dml.types.DataType;

public class ColumnInfo {
	public String tableName;
	public String columnName;
	public DataType dataType;
	public int dataLength;
	public boolean primaryKeyFlag;
	
	public ColumnInfo(String tableName, String columnName, DataType dataType, int dataLength, boolean primaryKeyFlag) {
		this.tableName = tableName;
		this.columnName = columnName;
		this.dataType = dataType;
		this.dataLength = dataLength;
		this.primaryKeyFlag = primaryKeyFlag;
	}
	
	public static ArrayList<ColumnInfo> getColumnsInfoFromTableDef(TableDefinition tableDefinition) {
		ArrayList<ColumnInfo> columnsInfo = new ArrayList<ColumnInfo>();
		for (int i = 0 ; i < tableDefinition.fieldDefinition.size() ; i++) {
			ColumnDefinition columnDefinition = tableDefinition.fieldDefinition.get(i);
			
			int dataLength;
			
			if (columnDefinition.getDataType() == DataType.INT)
				dataLength = 4;
			else if (columnDefinition.getDataType() == DataType.DATE)
				dataLength = 10;
			else
				dataLength = columnDefinition.getCharLength();
				
			columnsInfo.add(new ColumnInfo(tableDefinition.tableName,
					columnDefinition.columnName, columnDefinition.getDataType(), dataLength, columnDefinition.primaryKeyFlag));
		}
		
		return columnsInfo;
	}
	
	public static ArrayList<ColumnInfo> getColumnsInfoFromTableDef(ArrayList<TableDefinition> tableDefinitions,
			ArrayList<ReferedTable> referedTableList) {
		ArrayList<ColumnInfo> columnsInfo = new ArrayList<ColumnInfo>();
		
		for (int i = 0 ; i < tableDefinitions.size() ; i++) {
			TableDefinition tableDefinition = tableDefinitions.get(i);
			for (int j = 0 ; j < tableDefinition.fieldDefinition.size() ; j++) {
				ColumnDefinition columnDefinition = tableDefinition.fieldDefinition.get(j);
				
				int dataLength;
				
				if (columnDefinition.getDataType() == DataType.INT)
					dataLength = 4;
				else if (columnDefinition.getDataType() == DataType.DATE)
					dataLength = 10;
				else
					dataLength = columnDefinition.getCharLength();
				
				String tableName = tableDefinition.tableName;
				
				if (referedTableList.get(i).renameFlag)
					tableName = referedTableList.get(i).newName;
				
				columnsInfo.add(new ColumnInfo(tableName,
						columnDefinition.columnName, columnDefinition.getDataType(), dataLength, columnDefinition.primaryKeyFlag));
			}
		}
		
		return columnsInfo;
	}
	
	public static ArrayList<ColumnInfo> getColumnsInfoFromTableDef(ArrayList<TableDefinition> tableDefinitions) {
		ArrayList<ColumnInfo> columnsInfo = new ArrayList<ColumnInfo>();
		
		for (int i = 0 ; i < tableDefinitions.size() ; i++) {
			TableDefinition tableDefinition = tableDefinitions.get(i);
			for (int j = 0 ; j < tableDefinition.fieldDefinition.size() ; j++) {
				ColumnDefinition columnDefinition = tableDefinition.fieldDefinition.get(j);
				
				int dataLength;
				
				if (columnDefinition.getDataType() == DataType.INT)
					dataLength = 4;
				else if (columnDefinition.getDataType() == DataType.DATE)
					dataLength = 10;
				else
					dataLength = columnDefinition.getCharLength();
				
				String tableName = tableDefinition.tableName;
				
				columnsInfo.add(new ColumnInfo(tableName,
						columnDefinition.columnName, columnDefinition.getDataType(), dataLength, columnDefinition.primaryKeyFlag));
			}
		}		
		
		return columnsInfo;
	}
	
	public void print() {
		System.out.println("table name: " + tableName + " column name: " + columnName + " dataType: " + dataType);
	}
}
