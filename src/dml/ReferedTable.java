package dml;

public class ReferedTable {
	public String tableName;
	public String newName;
	public boolean renameFlag = false;
	
	public ReferedTable(String tableName) {
		this.tableName = tableName;
	}
	
	public void setNewName(String newName) {
		this.newName = newName;
		this.renameFlag = true;
	}
 }
