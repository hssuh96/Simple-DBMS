package dml.booleantree;

import definition.TableDefinition;

public class EvaluationTree {
	private BaseNode root;
	
	public EvaluationTree(BaseNode root) {
		this.root = root;
	}
	
	public boolean evaluate(TableDefinition tableDefinition) {
		return root.evaluate(tableDefinition);
	}
	
	public boolean checkInComparableError(TableDefinition tableDefinition) {
		return false;
	}
}
