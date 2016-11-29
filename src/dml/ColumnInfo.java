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
}
