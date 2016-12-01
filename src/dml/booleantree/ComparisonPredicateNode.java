package dml.booleantree;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import definition.TableDefinition;
import dml.ColumnInfo;
import dml.Value;
import dml.WhereErrorFlag;
import dml.types.ComparisonOperatorType;
import dml.types.DataType;
import dml.types.ThreeLogic;

public class ComparisonPredicateNode implements BaseNode {
	Value value1;
	ComparisonOperatorType operatorType;
	Value value2;
	
	DataType operationDataType; // evaluate() returns Unknown when operationDataType is NULL
	int index1 = -1;
	int index2 = -1;
	
	public ComparisonPredicateNode(Value value1, ComparisonOperatorType operatorType, Value value2) {
		this.value1 = value1;
		this.operatorType = operatorType;
		this.value2 = value2;
	}
	
	@Override
	public ThreeLogic evaluate(ArrayList<Value> valueList) {
		if (operationDataType == DataType.INT) {
			int operand1, operand2;
			
			if (value1.dataType == DataType.COLUMN) {
				if (valueList.get(index1).dataType == DataType.NULL)
					return ThreeLogic.UNKNOWN;
				operand1 = valueList.get(index1).getInt();
			}
			else
				operand1 = value1.getInt();
			if (value2.dataType == DataType.COLUMN) {
				if (valueList.get(index2).dataType == DataType.NULL)
					return ThreeLogic.UNKNOWN;
				operand2 = valueList.get(index2).getInt();
			}
			else
				operand2 = value2.getInt();
			
			switch(operatorType) {
			case EQUAL_TO:
				if (operand1 == operand2)
					return ThreeLogic.TRUE;
				else
					return ThreeLogic.FALSE;
			case NOT_EQUAL:
				if (operand1 != operand2)
					return ThreeLogic.TRUE;
				else
					return ThreeLogic.FALSE;
			case LESS_THAN:
				if (operand1 < operand2)
					return ThreeLogic.TRUE;
				else
					return ThreeLogic.FALSE;
			case GREATER_THAN:
				if (operand1 > operand2)
					return ThreeLogic.TRUE;
				else
					return ThreeLogic.FALSE;
			case GREATER_THAN_OR_EQUAL_TO:
				if (operand1 >= operand2)
					return ThreeLogic.TRUE;
				else
					return ThreeLogic.FALSE;
			case LESS_THAN_OR_EQUAL_TO:
				if (operand1 <= operand2)
					return ThreeLogic.TRUE;
				else
					return ThreeLogic.FALSE;
			default:
				return ThreeLogic.UNKNOWN;
			}
		}
		else if (operationDataType == DataType.CHAR) {
			String operand1, operand2;

			if (value1.dataType == DataType.COLUMN) {
				if (valueList.get(index1).dataType == DataType.NULL)
					return ThreeLogic.UNKNOWN;
				operand1 = valueList.get(index1).getChar();
			}
			else
				operand1 = value1.getChar();
			if (value2.dataType == DataType.COLUMN) {
				if (valueList.get(index2).dataType == DataType.NULL)
					return ThreeLogic.UNKNOWN;
				operand2 = valueList.get(index2).getChar();
			}
			else
				operand2 = value2.getChar();
			
			switch(operatorType) {
			case EQUAL_TO:
				if (operand1.equals(operand2))
					return ThreeLogic.TRUE;
				else
					return ThreeLogic.FALSE;
			case NOT_EQUAL:
				if (!operand1.equals(operand2))
					return ThreeLogic.TRUE;
				else
					return ThreeLogic.FALSE;
			case LESS_THAN:
				if (operand1.compareTo(operand2) < 0)
					return ThreeLogic.TRUE;
				else
					return ThreeLogic.FALSE;
			case GREATER_THAN:
				if (operand1.compareTo(operand2) > 0)
					return ThreeLogic.TRUE;
				else
					return ThreeLogic.FALSE;
			case GREATER_THAN_OR_EQUAL_TO:
				if (operand1.compareTo(operand2) >= 0)
					return ThreeLogic.TRUE;
				else
					return ThreeLogic.FALSE;
			case LESS_THAN_OR_EQUAL_TO:
				if (operand1.compareTo(operand2) <= 0)
					return ThreeLogic.TRUE;
				else
					return ThreeLogic.FALSE;
			default:
				return ThreeLogic.UNKNOWN;
			}
		}
		else if (operationDataType == DataType.DATE) {
			Calendar operand1 = Calendar.getInstance();
			Calendar operand2 = Calendar.getInstance();
			
			Date operand1Date, operand2Date;

			if (value1.dataType == DataType.COLUMN) {
				if (valueList.get(index1).dataType == DataType.NULL)
					return ThreeLogic.UNKNOWN;
				operand1Date = valueList.get(index1).getDate();
			}
			else
				operand1Date = value1.getDate();
			if (value2.dataType == DataType.COLUMN) {
				if (valueList.get(index2).dataType == DataType.NULL)
					return ThreeLogic.UNKNOWN;
				operand2Date = valueList.get(index2).getDate();
			}
			else
				operand2Date = value2.getDate();
			
			operand1.setTime(operand1Date);
			operand1.set(Calendar.HOUR_OF_DAY, 0);
			operand1.set(Calendar.MINUTE, 0);
			operand1.set(Calendar.SECOND, 0);
			operand1.set(Calendar.MILLISECOND, 0);

			operand2.setTime(operand2Date);
			operand2.set(Calendar.HOUR_OF_DAY, 0);
			operand2.set(Calendar.MINUTE, 0);
			operand2.set(Calendar.SECOND, 0);
			operand2.set(Calendar.MILLISECOND, 0);
			
			switch(operatorType) {
			case EQUAL_TO:
				if (operand1.equals(operand2))
					return ThreeLogic.TRUE;
				else
					return ThreeLogic.FALSE;
			case NOT_EQUAL:
				if (!operand1.equals(operand2))
					return ThreeLogic.TRUE;
				else
					return ThreeLogic.FALSE;
			case LESS_THAN:
				if (operand1.compareTo(operand2) < 0)
					return ThreeLogic.TRUE;
				else
					return ThreeLogic.FALSE;
			case GREATER_THAN:
				if (operand1.compareTo(operand2) > 0)
					return ThreeLogic.TRUE;
				else
					return ThreeLogic.FALSE;
			case GREATER_THAN_OR_EQUAL_TO:
				if (operand1.compareTo(operand2) >= 0)
					return ThreeLogic.TRUE;
				else
					return ThreeLogic.FALSE;
			case LESS_THAN_OR_EQUAL_TO:
				if (operand1.compareTo(operand2) <= 0)
					return ThreeLogic.TRUE;
				else
					return ThreeLogic.FALSE;
			default:
				return ThreeLogic.UNKNOWN;
			}
		}
		else {
			System.out.println("Invalid State");
			return ThreeLogic.UNKNOWN;
		}
	}

	@Override
	public void checkErrorAndUpdateInfo(ArrayList<ColumnInfo> columnsInfo, WhereErrorFlag whereErrorFlag) {
		if (value1.dataType == DataType.COLUMN) { // Data Type is COLUMN
//			System.out.println(value1.data); // TEST
			String[] arr = value1.data.split("\\.");
			if (arr.length == 1) { // columnName
				for (int i = 0 ; i < columnsInfo.size() ; i++) {
					if (columnsInfo.get(i).columnName.equals(arr[0])) { // find
						if (index1 != -1) { // ambiguous
							whereErrorFlag.WhereAmbiguousReferenceFlag = true;
							return;
						}
						else { // find
							index1 = i;
							operationDataType = columnsInfo.get(i).dataType;
						}
					}
				}
				
				if (index1 == -1) { // not found
					whereErrorFlag.WhereColumnNotExistFlag = true;
					return;
				}
			}
			else { // tableName.columnName
				boolean tableFoundFlag = false;
				
				for (int i = 0 ; i < columnsInfo.size() ; i++) {
					if (columnsInfo.get(i).tableName.equals(arr[0])) { // table name find
						tableFoundFlag = true;
						
						if (columnsInfo.get(i).columnName.equals(arr[1])) { // find
							if (index1 != -1) { // ambiguous
								whereErrorFlag.WhereAmbiguousReferenceFlag = true;
								return;
							}
							else { // find
								index1 = i;
								operationDataType = columnsInfo.get(i).dataType;
							}
						}
					}
				}
				
				if (!tableFoundFlag) {
					whereErrorFlag.WhereTableNotSpecifiedFlag = true;
					return;
				}
				
				if (index1 == -1) { // not found
					whereErrorFlag.WhereColumnNotExistFlag = true;
					return;
				}
			}
		}
		else { // data type is NULL, INT, CHAR, DATE
			operationDataType = value1.dataType;
		}
		
		
		
		if (value2.dataType == DataType.COLUMN) { // just check when data type is column
			String[] arr = value2.data.split("\\.");
			if (arr.length == 1) { // columnName
				for (int i = 0 ; i < columnsInfo.size() ; i++) {
					if (columnsInfo.get(i).columnName.equals(arr[0])) { // find
						if (index2 != -1) { // ambiguous
							whereErrorFlag.WhereAmbiguousReferenceFlag = true;
							return;
						}
						else { // find
							index2 = i;
						}
					}
				}
			}
			else { // tableName.columnName
				boolean tableFoundFlag = false;
				
				for (int i = 0 ; i < columnsInfo.size() ; i++) {
					if (columnsInfo.get(i).tableName.equals(arr[0])) { // table name find
						tableFoundFlag = true;
						
						if (columnsInfo.get(i).columnName.equals(arr[1])) { // find
							if (index2 != -1) { // ambiguous
								whereErrorFlag.WhereAmbiguousReferenceFlag = true;
								return;
							}
							else { // find
								index2 = i;
							}
						}
					}
				}
				
				if (!tableFoundFlag) {
					whereErrorFlag.WhereTableNotSpecifiedFlag = true;
					return;
				}
			}
			
			if (index2 == -1) { // not found
				whereErrorFlag.WhereColumnNotExistFlag = true;
				return;
			}
			else {
				if (operationDataType != DataType.NULL && operationDataType != columnsInfo.get(index2).dataType) {
					whereErrorFlag.WhereIncomparableErrorFlag = true;
					return;
				}
			}
		}
		else { // data type is NULL, INT, CHAR, DATE
			if (operationDataType != DataType.NULL && operationDataType != value2.dataType) {
				whereErrorFlag.WhereIncomparableErrorFlag = true;
				return;
			}
		}
	}
	
}
