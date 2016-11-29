package dml.booleantree;

import definition.TableDefinition;
import dml.Value;
import dml.types.NullOperatorType;

public class NullPredicateNode implements BaseNode {
	Value value;
	NullOperatorType nullOperatorType;
	
	public NullPredicateNode(Value value, NullOperatorType nullOperatorType) {
		this.value = value;
		this.nullOperatorType = nullOperatorType;
	}

	@Override
	public boolean evaluate(TableDefinition tableDefinition) {
		// TODO Auto-generated method stub
		return false;
	}
}
