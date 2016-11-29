package dml.booleantree;

import java.util.ArrayList;

import definition.TableDefinition;
import dml.ColumnInfo;
import dml.Value;
import dml.WhereErrorFlag;
import dml.types.DataType;
import dml.types.NullOperatorType;
import dml.types.ThreeLogic;

public class NullPredicateNode implements BaseNode {
	Value value;
	NullOperatorType nullOperatorType;
	
	int index = -1;
	
	public NullPredicateNode(Value value, NullOperatorType nullOperatorType) {
		this.value = value;
		this.nullOperatorType = nullOperatorType;
	}

	@Override
	public ThreeLogic evaluate(ArrayList<Value> valueList) {
		if (nullOperatorType == NullOperatorType.IS_NULL && valueList.get(index).dataType == DataType.NULL)
			return ThreeLogic.TRUE;
		if (nullOperatorType == NullOperatorType.IS_NOT_NULL && valueList.get(index).dataType != DataType.NULL)
			return ThreeLogic.TRUE;
		
		return ThreeLogic.FALSE;
	}

	@Override
	public void checkErrorAndUpdateInfo(ArrayList<ColumnInfo> columnsInfo, WhereErrorFlag whereErrorFlag) {
		String[] arr = value.data.split("\\.");
		if (arr.length == 1) { // columnName
//			System.out.println(arr[0]); // TODO : TEST
			for (int i = 0 ; i < columnsInfo.size() ; i++) {
				if (columnsInfo.get(i).columnName.equals(arr[0])) { // find
					if (index != -1) { // ambiguous
						whereErrorFlag.WhereAmbiguousReferenceFlag = true;
						return;
					}
					else { // find
						index = i;
					}
				}
			}
			
			if (index == -1) { // not found
				whereErrorFlag.WhereColumnNotExistFlag = true;
				return;
			}
		}
		else { // tableName.columnName
//			System.out.println(arr[0] + "  " + arr[1]); // TODO : TEST
			boolean tableFoundFlag = false;
			
			for (int i = 0 ; i < columnsInfo.size() ; i++) {
				if (columnsInfo.get(i).tableName.equals(arr[0])) { // table name find
					tableFoundFlag = true;
					
					if (columnsInfo.get(i).columnName.equals(arr[1])) { // find
						if (index != -1) { // ambiguous
							whereErrorFlag.WhereAmbiguousReferenceFlag = true;
							return;
						}
						else { // find
							index = i;
						}
					}
				}
				
				if (!tableFoundFlag) {
					whereErrorFlag.WhereTableNotSpecifiedFlag = true;
					return;
				}
				
				if (index == -1) { // not found
					whereErrorFlag.WhereColumnNotExistFlag = true;
					return;
				}
			}
		}
	}
}
