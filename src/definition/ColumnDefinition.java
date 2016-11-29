package definition;

import dml.types.DataType;

public class ColumnDefinition {
	public String columnName;
	public String dataType;
	public boolean notNullFlag; // true if not null
	public boolean primaryKeyFlag;
	public boolean foreignKeyFlag;
	
	public ColumnDefinition(String columnName, String dataType, boolean notNullFlag) {
		this.columnName = columnName;
		this.dataType = dataType;
		this.notNullFlag = notNullFlag;
		primaryKeyFlag = false;
		foreignKeyFlag = false;
	}
	
	public ColumnDefinition(String columnDesc) {
//		System.out.println(columnDesc);
		
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
		
		if (arr[4].equals("1"))
			foreignKeyFlag = true;
		else
			foreignKeyFlag = false;
	}
	
	public int getCharLength() {
		if(dataType.length() > 4)
			return Integer.parseInt(dataType.substring(5, dataType.length()-1));
		else
			return -1;
	}
	
	public DataType getDataType() {
		if (dataType.equals("int"))
			return DataType.INT;
		else if (dataType.equals("date"))
			return DataType.DATE;
		else
			return DataType.CHAR;
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