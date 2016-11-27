package definition;

public class ColumnDefinition {
	public String columnName;
	public String dataType;
	public boolean notNullFlag;
	public boolean primaryKeyFlag;
	public boolean foreignKeyFlag;
	public String referencedColumnName; // tableName.columnName format
	
	public ColumnDefinition(String columnName, String dataType, boolean notNullFlag) {
		this.columnName = columnName;
		this.dataType = dataType;
		this.notNullFlag = notNullFlag;
		primaryKeyFlag = false;
		foreignKeyFlag = false;
		referencedColumnName = null;
	}
	
	public ColumnDefinition(String columnDesc) {
		String[] arr = columnDesc.split("/");
		
		columnName = arr[0];
		
		dataType = arr[1];
		
		if (arr[2].equals("1"))
			notNullFlag = true;
		else
			notNullFlag = false;
		
		if (arr[3].equals("1"))
			primaryKeyFlag = true;
		else
			primaryKeyFlag = false;
		
		if (arr[4].equals("1")) {
			foreignKeyFlag = true;
			referencedColumnName = arr[5];
		}
		else {
			foreignKeyFlag = false;
			referencedColumnName = null;
		}
	}
	
	public void print() {
		System.out.printf("%-20s%-15s", columnName, dataType);
		
		if (notNullFlag)
			System.out.printf("%-10s", "N");
		else
			System.out.printf("%-10s", "Y");
		
		if (primaryKeyFlag && foreignKeyFlag)
			System.out.printf("%-10s", "PRI/FOR");
		if (primaryKeyFlag && !foreignKeyFlag)
			System.out.printf("%-10s", "PRI");
		if (!primaryKeyFlag && foreignKeyFlag)
			System.out.printf("%-10s", "FOR");
		
		System.out.println();
	}
}