package dml;

import java.util.Date;

import definition.TableDefinition;
import dml.types.DataType;

public class Value {
	public DataType dataType;
	public String data = null;
	
	public boolean isEqual(Value value) {
		if (dataType != value.dataType)
			return false;
		if (!data.equals(value.data))
			return false;
		return true;
	}
	
	public Value() {
		dataType = DataType.NULL;
	}
	
	public void setNull() {
		dataType = DataType.NULL;
		data = null;
//		print();
	}
	
	public void setInt(String str) {
		dataType = DataType.INT;
		data = str;
//		print();
	}
	
	public void setChar(String str, boolean quoteFlag) {
		dataType = DataType.CHAR;
		if (quoteFlag)
			data = str.substring(1, str.length() - 1);
		else
			data = str;
		
		for (int i = data.length()-1 ; i >= 0 ; i--) {
			if (data.charAt(i) == '\0')
				data = data.substring(0, i);
		}
		
//		print();
	}

	public void setDate(String str) {
		dataType = DataType.DATE;
		data = str;
//		print();
	}
	
	public void setColumn(String str) {
		dataType = DataType.COLUMN;
		data = str;
	}
	
	public int getInt() {
		return Integer.parseInt(data);
	}
	
	public String getChar() {
		return data;
	}

	public Date getDate() {
		return new Date(Integer.parseInt(data.substring(0, 4)) - 1900,
				Integer.parseInt(data.substring(5, 7)) - 1, Integer.parseInt(data.substring(8, 10)));
	}
	
	//TEST
//	public void print() {
//		System.out.println(dataType + " " + data);
//	}
	
	// 4byte for int, (n)byte for char(n), 10byte for date (NNNN-NN-NN). include NULL flag
	// NULL flag : 0 if null, 1 otherwise.
	public byte[] dataToByte(TableDefinition tableDefinition, int i){ 
		if (dataType == DataType.NULL) {
			byte[] result = {0};
			return result;
		}
		else if (dataType == DataType.INT) {
			byte[] result = new byte[5];
			result[0] = 1;
			int dataInt = Integer.parseInt(data);
			result[1] = (byte)(dataInt >> 24);
			result[2] = (byte)(dataInt >> 16);
			result[3] = (byte)(dataInt >> 8);
			result[4] = (byte)(dataInt);
			return result;
		}
		else if (dataType == DataType.CHAR) { // check char length from tableDefinition
			int charLength = tableDefinition.fieldDefinition.get(i).getCharLength();
			byte[] result = new byte[charLength + 1];
			
			if (charLength == -1)
				System.out.println("Unexpected ERROR: Data type is not char");
			
			result[0] = 1;
			
			byte[] stringByte = data.getBytes();
			if (stringByte.length > charLength)
				System.out.println("Unexpected ERROR: Data is longer than definition");
			
			for (int j = 0 ; j < stringByte.length ; j++) {
				result[j+1] = stringByte[j];
			}
			for (int j = stringByte.length ; j < charLength ; j++) {
				result[j+1] = 0;
			}
			
			return result;
		}
		else if (dataType == DataType.DATE) {
			byte[] result = new byte[data.length()+1];
			
			result[0] = 1;
			
			byte[] stringByte = data.getBytes();
			for (int j = 0 ; j < stringByte.length ; j++) {
				result[j+1] = stringByte[j];
			}
			
			return result;
		}
		
		return new byte[0];
	}
}
