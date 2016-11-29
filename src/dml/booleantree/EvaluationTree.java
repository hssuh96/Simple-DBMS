package dml.booleantree;

import java.util.ArrayList;

import definition.TableDefinition;
import dml.ColumnInfo;
import dml.Value;
import dml.WhereErrorFlag;
import dml.types.ThreeLogic;

public class EvaluationTree {
	private BaseNode root;
	
	public EvaluationTree(BaseNode root) {
		this.root = root;
	}
	
	public ThreeLogic evaluate(ArrayList<Value> valueList) {
		return root.evaluate(valueList);
	}
	
	public void checkErrorAndUpdateInfo(ArrayList<ColumnInfo> columnsInfo, WhereErrorFlag whereErrorFlag) {
		root.checkErrorAndUpdateInfo(columnsInfo, whereErrorFlag);
	}
}
