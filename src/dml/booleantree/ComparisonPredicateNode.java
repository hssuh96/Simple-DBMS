package dml.booleantree;

import definition.TableDefinition;
import dml.Value;
import dml.types.ComparisonOperatorType;

public class ComparisonPredicateNode implements BaseNode {
	Value value1;
	ComparisonOperatorType operatorType;
	Value value2;
	
	public ComparisonPredicateNode(Value value1, ComparisonOperatorType operatorType, Value value2) {
		this.value1 = value1;
		this.operatorType = operatorType;
		this.value2 = value2;
	}
	
	@Override
	public boolean evaluate(TableDefinition tableDefinition) {
		// TODO Auto-generated method stub
		return false;
	}

}
