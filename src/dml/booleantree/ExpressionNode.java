package dml.booleantree;

import java.util.ArrayList;

import definition.TableDefinition;
import dml.ColumnInfo;
import dml.Value;
import dml.WhereErrorFlag;
import dml.types.BooleanOperatorType;
import dml.types.ThreeLogic;

public class ExpressionNode implements BaseNode {
	BaseNode left;
	BooleanOperatorType booleanOperatorType;
	BaseNode right;
	
	public ExpressionNode(BaseNode left, BooleanOperatorType booleanOperatorType, BaseNode right) { // AND, OR
		this.left = left;
		this.booleanOperatorType = booleanOperatorType;
		this.right = right;
	}
	
	public ExpressionNode(BaseNode left, BooleanOperatorType booleanOperatorType) { // NOT
		this.left = left;
		this.booleanOperatorType = booleanOperatorType;
		this.right = null;
	}
	
	@Override
	public ThreeLogic evaluate(ArrayList<Value> valueList) {
		ThreeLogic result1 = left.evaluate(valueList);
		
		if (booleanOperatorType == BooleanOperatorType.NOT) {
			if (result1 == ThreeLogic.TRUE)
				return ThreeLogic.FALSE;
			else if (result1 == ThreeLogic.FALSE)
				return ThreeLogic.TRUE;
			else
				return ThreeLogic.UNKNOWN;
		}
		else {
			ThreeLogic result2 = right.evaluate(valueList);
			if (booleanOperatorType == BooleanOperatorType.AND) { // AND
				if (result1 == ThreeLogic.FALSE)
					return ThreeLogic.FALSE;
				if (result2 == ThreeLogic.FALSE)
					return ThreeLogic.FALSE;
				
				if (result1 == ThreeLogic.TRUE && result2 == ThreeLogic.TRUE)
					return ThreeLogic.TRUE;
				else
					return ThreeLogic.UNKNOWN;
			}
			else { // OR
				if (result1 == ThreeLogic.TRUE)
					return ThreeLogic.TRUE;
				if (result2 == ThreeLogic.TRUE)
					return ThreeLogic.TRUE;
				
				if (result1 == ThreeLogic.FALSE && result2 == ThreeLogic.FALSE)
					return ThreeLogic.FALSE;
				else
					return ThreeLogic.UNKNOWN;
			}
		}
	}

	@Override
	public void checkErrorAndUpdateInfo(ArrayList<ColumnInfo> columnsInfo, WhereErrorFlag whereErrorFlag) {
		if (left != null)
			left.checkErrorAndUpdateInfo(columnsInfo, whereErrorFlag);
		if (right != null)
			right.checkErrorAndUpdateInfo(columnsInfo, whereErrorFlag);
	}

}
