package dml;

import java.util.ArrayList;

import definition.TableDefinition;
import dml.types.DataType;

public class Conversion {
	// valueListOrdered ordered by definition order.
	public static void getByteRepresentation(TableDefinition tableDefinition, ArrayList<Value> valueListOrdered,
			ArrayList<Byte> keyBytesArrayList, ArrayList<Byte> dataBytesArrayList) {
		for (int i = 0 ; i < valueListOrdered.size() ; i++) {
			byte[] byteArray = valueListOrdered.get(i).dataToByte(tableDefinition, i);

			if (tableDefinition.fieldDefinition.get(i).primaryKeyFlag) {
				for (int j = 0 ; j < byteArray.length ; j++) {
					keyBytesArrayList.add(new Byte(byteArray[j]));
				}
			}
			else {
				for (int j = 0 ; j < byteArray.length ; j++) {
					dataBytesArrayList.add(new Byte(byteArray[j]));
				}
			}
		}
	}
	
	public static byte[] getByteRepresentation(TableDefinition referedTableDefinition, ArrayList<Value> valueListOrdered) {
		ArrayList<Byte> byteArrayList = new ArrayList<Byte>();
		int k = 0;
		for (int i = 0 ; i < referedTableDefinition.fieldDefinition.size() ; i++) {
			if (referedTableDefinition.fieldDefinition.get(i).primaryKeyFlag) {
				byte[] byteArrayTemp = valueListOrdered.get(k).dataToByte(referedTableDefinition, i);
				k++;
				for (int j = 0 ; j < byteArrayTemp.length ; j++) {
					byteArrayList.add(new Byte(byteArrayTemp[j]));
				}
			}
		}
		
		byte[] resultByte = new byte[byteArrayList.size()];
		
		for (int i = 0 ; i < resultByte.length ; i++) {
			resultByte[i] = byteArrayList.get(i);
		}
		
		return resultByte;
	}
	
	public static ArrayList<Value> bytesToValues(ArrayList<ColumnInfo> columnsInfo,
			byte[] keyByteArray, byte[] dataByteArray) { // should give concatenated byte array if many tables
		ArrayList<Value> valueList = new ArrayList<Value>();
		
		IndexSaver index1 = new IndexSaver();
		IndexSaver index2 = new IndexSaver();
		
		String currentTableName = "";
		boolean PrimaryKeyExistFlag = true;
		
		for (int i = 0 ; i < columnsInfo.size() ; i++) {
			if (!columnsInfo.get(i).tableName.equals(currentTableName)) {
				if (!PrimaryKeyExistFlag)
					index1.index += 4;
				
				currentTableName = columnsInfo.get(i).tableName;
				PrimaryKeyExistFlag = false;
			}
			if (columnsInfo.get(i).primaryKeyFlag)
				PrimaryKeyExistFlag = true;
			
			valueList.add(getValue(keyByteArray, dataByteArray, index1, index2, columnsInfo.get(i)));
		}
		
		return valueList;
	}
	
	public static Value getValue(byte[] keyByteArray, byte[] dataByteArray,
			IndexSaver index1, IndexSaver index2, ColumnInfo columnInfo) {
		byte[] byteArray;
		IndexSaver index;
		
		if (columnInfo.primaryKeyFlag) {
			byteArray = keyByteArray;
			index = index1;
		}
		else {
			byteArray = dataByteArray;
			index = index2;
		}
		
		Value value = new Value();
		
		if (byteArray[index.index++] == 0) // NULL
			return value;
		
		if (columnInfo.dataType == DataType.INT) {
			value.setInt(String.valueOf(bytesToInt(subBytes(byteArray, index, columnInfo.dataLength))));
		}
		else if (columnInfo.dataType == DataType.DATE) {
			value.setDate(new String(subBytes(byteArray, index, columnInfo.dataLength)));
		}
		else { // CHAR
			value.setChar(new String(subBytes(byteArray, index, columnInfo.dataLength)), false);
		}
		
		return value;
	}
	
	public static int bytesToInt(byte[] bytes) {
		return ((((int)bytes[0] & 0xff) << 24) |
				(((int)bytes[1] & 0xff) << 16) |
				(((int)bytes[2] & 0xff) << 8) |
				(((int)bytes[3] & 0xff)));
	}
	
	public static byte[] subBytes(byte[] bytes, IndexSaver index, int length) {
		byte[] newBytes = new byte[length];
		for (int i = 0 ; i < length ; i++) {
			newBytes[i] = bytes[index.index+i];
		}
		index.index += length;
		
		return newBytes;
	}
}

class IndexSaver{
	public int index = 0;
}