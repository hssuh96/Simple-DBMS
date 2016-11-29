package dml.booleantree;

import definition.TableDefinition;

public interface BaseNode {
	public boolean evaluate(TableDefinition tableDefinition);
}
