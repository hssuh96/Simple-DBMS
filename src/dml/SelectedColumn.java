package dml;

public class SelectedColumn {
	public String columnName;
	public String newName;
	public boolean renameFlag = false;
	
	public SelectedColumn(String columnName) {
		this.columnName = columnName;
	}
	
	public void setNewName(String newName) {
		this.newName = newName;
		this.renameFlag = true;
	}
}
