package dml.booleantree;

import java.util.ArrayList;

import definition.TableDefinition;
import dml.ColumnInfo;
import dml.Value;
import dml.WhereErrorFlag;
import dml.types.ThreeLogic;

public interface BaseNode {
	public ThreeLogic evaluate(ArrayList<Value> valueList);
	
	public void checkErrorAndUpdateInfo(ArrayList<ColumnInfo> columnsInfo, WhereErrorFlag whereErrorFlag);
}
