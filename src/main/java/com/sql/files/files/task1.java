package com.sql.files.files;

import java.io.*;
import java.util.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.WorkbookFactory;
//import org.apache.poi.xssf.usermodel.XSSFSheet;
//import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class task1 {
	public static void main(String[] args) {
		start();
	}

	public static void start() {
		try {
			FileInputStream file = new FileInputStream(new File("/home/srikanth/Desktop/antp.xls"));

			org.apache.poi.ss.usermodel.Workbook workbook = WorkbookFactory.create(file);
			
			org.apache.poi.ss.usermodel.Sheet sheet = workbook.getSheetAt(0);

			// Iterate through each rows one by one
			Iterator<Row> rowIterator = sheet.iterator();
			while (rowIterator.hasNext()) {
				Row row = rowIterator.next();
				// For each row, iterate through all the columns
				Iterator<Cell> cellIterator = row.cellIterator();
				
				while (cellIterator.hasNext()) {
					Cell cell = cellIterator.next();
					// Check the cell type and format accordingly
					//index 5 for Habitation Name
					if(cell.getColumnIndex()==5)
					{
						switch (cell.getCellType())
						{
						case Cell.CELL_TYPE_NUMERIC:
							//System.out.print(cell.getNumericCellValue() + "\t");
							break;
						case Cell.CELL_TYPE_STRING:
							//System.out.print(cell.getStringCellValue() + "\t");
							//System.out.println("index=" + cell.getColumnIndex());
							String habitation_name=getFormattedString(cell.getStringCellValue());
							String query="INSERT INTO platform_data.location_type_md"
									+"(name,description,insert_ts,deleted,user_session_id)"
									+"values('"+habitation_name+"','Habitation inside a village',(select unix_timestamp()*1000),0,0);";

							System.out.println(query);
							break;
						case Cell.CELL_TYPE_BLANK:
							//System.out.print("\t");
							break;
						}
					}
				}
			}
			file.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public static String getFormattedString(String name){
		StringBuilder sb=new StringBuilder("");
		for(int i=0;i<name.length();i++){
			char ch=name.charAt(i);
			if(ch=='\n' || ch=='\t'){
				sb.append(" ");
			}
			else if(ch==' '){
				if(sb.length()>0 && sb.charAt(sb.length()-1)!=' '){
					sb.append(" ");
				}
			}
			else{
				sb.append(ch);
			}
		}
		return sb.toString();
	}
}
