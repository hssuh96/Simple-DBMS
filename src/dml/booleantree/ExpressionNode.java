package dml.booleantree;

import definition.TableDefinition;
import dml.types.BooleanOperatorType;

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
	}
	
	@Override
	public boolean evaluate(TableDefinition tableDefinition) {
		// TODO Auto-generated method stub
		return false;
	}

}
