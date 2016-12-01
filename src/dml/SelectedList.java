package dml;

import java.util.ArrayList;

public class SelectedList {
	public boolean asteriskFlag;
	public ArrayList<SelectedColumn> selectedColumnList = null;
	
	public SelectedList() {
		this.asteriskFlag = true;
	}
	
	public SelectedList(ArrayList<SelectedColumn> selectedColumnList) {
		this.asteriskFlag = false;
		this.selectedColumnList = selectedColumnList;
	}
}
