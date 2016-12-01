package dml;

import java.util.ArrayList;

import definition.TableDefinition;

public class SelectColumnInfo {
	String columnName; // the name will be displayed
	int index; // index in valueList
	
	public SelectColumnInfo(String columnName, int index) {
		this.columnName = columnName;
		this.index = index;
	}
	
	public static ArrayList<SelectColumnInfo> getSelectColumnsInfoFromSelectedList(SelectedList selectedList,
			ArrayList<ColumnInfo> columnsInfo, SelectColumnResolveErrorFlag selectColumnResolveErrorFlag) {
		ArrayList<SelectColumnInfo> selectColumnsInfo = new ArrayList<SelectColumnInfo>();
		
		if (selectedList.asteriskFlag) { // select *
			for (int i = 0 ; i < columnsInfo.size() ; i++) {
				selectColumnsInfo.add(new SelectColumnInfo(columnsInfo.get(i).columnName, i));
			}
		}
		else {
			for (int i = 0 ; i < selectedList.selectedColumnList.size() ; i++) {
				SelectedColumn selectedColumn = selectedList.selectedColumnList.get(i);
				int index = getIndexOnColumnsInfoByColumnName(columnsInfo, selectedColumn.columnName,
						selectColumnResolveErrorFlag);
				
				if (selectColumnResolveErrorFlag.selectColumnResolveErrorFlag)
					return null;
				
				if (selectedColumn.renameFlag)
					selectColumnsInfo.add(new SelectColumnInfo(selectedColumn.newName, index));
				else
					selectColumnsInfo.add(new SelectColumnInfo(columnsInfo.get(index).columnName, index));				
			}
		}
		
		return selectColumnsInfo;
	}
	
	public static int getIndexOnColumnsInfoByColumnName(ArrayList<ColumnInfo> columnsInfo, String FindingName,
			SelectColumnResolveErrorFlag selectColumnResolveErrorFlag) {
		int index = -1;
		
		String[] arr = FindingName.split("\\.");
		if (arr.length == 1) { // columnName
//			System.out.println(arr[0]); // TEST
			for (int i = 0 ; i < columnsInfo.size() ; i++) {
				if (columnsInfo.get(i).columnName.equals(arr[0])) { // find
					if (index != -1) { // ambiguous
						selectColumnResolveErrorFlag.selectColumnResolveErrorFlag = true;
						selectColumnResolveErrorFlag.columnName = FindingName;
						return -1;
					}
					else { // find
						index = i;
					}
				}
			}
			
			if (index == -1) { // not found
				selectColumnResolveErrorFlag.selectColumnResolveErrorFlag = true;
				selectColumnResolveErrorFlag.columnName = FindingName;
				return -1;
			}
		}
		else { // tableName.columnName
//			System.out.println(arr[0] + "  " + arr[1]); // TEST
			for (int i = 0 ; i < columnsInfo.size() ; i++) {
				if (columnsInfo.get(i).tableName.equals(arr[0]) && columnsInfo.get(i).columnName.equals(arr[1])) {
					if (index != -1) { // ambiguous
						selectColumnResolveErrorFlag.selectColumnResolveErrorFlag = true;
						selectColumnResolveErrorFlag.columnName = FindingName;
						return -1;
					}
					else {
						index = i;
					}
				}
			}
			
			if (index == -1) { // not found
				selectColumnResolveErrorFlag.selectColumnResolveErrorFlag = true;
				selectColumnResolveErrorFlag.columnName = FindingName;
				return -1;
			}
		}
		
		return index;
	}
}