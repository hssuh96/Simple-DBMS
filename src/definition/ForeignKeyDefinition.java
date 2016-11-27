package definition;

import java.util.ArrayList;

class ForeignKeyDefinition {
	public String referencedTableName;
	public ArrayList<String> referencingColumnNames;
	public ArrayList<String> referencedColumnNames;
	
	public ForeignKeyDefinition(String referencedTableName, ArrayList<String> referencingColumnNames,
			ArrayList<String> referencedColumnNames) {
		this.referencedTableName = referencedTableName;
		this.referencingColumnNames = referencingColumnNames;
		this.referencedColumnNames = referencedColumnNames;
	}
	
//	public void print() { // for debugging purpose. 나중에 제거할 것
//		System.out.print("foreign key: ");
//		for (int i = 0 ; i < referencingColumnNames.size() ; i++) {
//			System.out.print(referencingColumnNames.get(i) + ", ");
//		}
//		
//		System.out.print(" references  table: " + referencedTableName + "  attributes: ");
//		
//		for (int i = 0 ; i < referencedColumnNames.size() ; i++) {
//			System.out.print(referencedColumnNames.get(i) + ", ");
//		}
//	}
}