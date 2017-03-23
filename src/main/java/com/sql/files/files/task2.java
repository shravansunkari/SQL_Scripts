package com.sql.files.files;

import java.io.*;
import java.util.*;

import org.apache.poi.hssf.util.CellReference;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.WorkbookFactory;
//import org.apache.poi.xssf.usermodel.XSSFSheet;
//import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class task2 {
	
	public static BufferedWriter bw = null;
	public static FileWriter fw = null;
	public static int row_count=0;
	public static void main(String[] args) {
		
		try {
			fw= new FileWriter("/home/srikanth/Desktop/location_type_md.sql");
			bw= new BufferedWriter(fw);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		File folder = new File("/home/srikanth/Desktop/files_all");
		File[] listOfFiles = folder.listFiles();

		    for (int i = 0; i < listOfFiles.length; i++) {
		      if (listOfFiles[i].isFile()) {
		    	  start("/home/srikanth/Desktop/files_all/",listOfFiles[i].getName());
		        //System.out.println("File " + listOfFiles[i].getName());
		      } else if (listOfFiles[i].isDirectory()) {
		       // System.out.println("Directory " + listOfFiles[i].getName());
		      }
		    }
		 try {
			bw.flush();
			bw.close();
			fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 
		 System.out.println("Total rows found "+row_count);
		//start();
	}

	public static void start(String filePath, String fileName) {
		try {
			FileInputStream file = new FileInputStream(new File(filePath+fileName));

			org.apache.poi.ss.usermodel.Workbook workbook = WorkbookFactory.create(file);
			
			int noOfSheets= workbook.getNumberOfSheets();
			for(int i=0; i<noOfSheets;i++){
			org.apache.poi.ss.usermodel.Sheet sheet = workbook.getSheetAt(i);
			
			workbook.getNumberOfSheets();

			// Iterate through each rows one by one
			Iterator<Row> rowIterator = sheet.iterator();
			
			int index=-1;
			int row_num=0;
			while( rowIterator.hasNext()){
				Row row = rowIterator.next();
				
				Iterator<Cell> cellIterator = row.cellIterator();
				while (cellIterator.hasNext()) {
					Cell cell = cellIterator.next();
					
					switch (cell.getCellType())
					{
					case Cell.CELL_TYPE_NUMERIC:
						//System.out.print(cell.getNumericCellValue() + "\t");
						break;
					case Cell.CELL_TYPE_STRING:
						if(cell.getStringCellValue().equalsIgnoreCase("Habitation Name") || cell.getStringCellValue().equalsIgnoreCase("Habitation ") || cell.getStringCellValue().equalsIgnoreCase("Habitation")){
							index=cell.getColumnIndex();
						}
						//System.out.println(cell.getStringCellValue());
						break;
					case Cell.CELL_TYPE_BLANK:
						//System.out.print("\t");
						break;
					}
				}
				row_num++;
				if(index!=-1)
					break;
			
			}
			if(index==-1){
				System.out.println("\n\nHabitation_name not found in "+fileName+"\n\n");
				return;
			}
			rowIterator = sheet.iterator();
			int cur_row=-1;
			while (rowIterator.hasNext()) {
				Row row = rowIterator.next();
				if(cur_row == row_num){
					//continue;
				}
				cur_row++;
				// For each row, iterate through all the columns
				Iterator<Cell> cellIterator = row.cellIterator();
				
				while (cellIterator.hasNext()) {
					Cell cell = cellIterator.next();
					// Check the cell type and format accordingly_
					//index 5 for Habitation Name
					
					if(cell.getColumnIndex()==index)
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
									+"values('HABITATION','Habitation inside a village',(select unix_timestamp()*1000),0,0);";
							//System.out.println(fileName+"\t"+query);
							
							if(!habitation_name.contains("TOTAL") && !habitation_name.contains("Total") && !habitation_name.contains("total")){
								bw.write(query+"\n");
								row_count++;
							}
							
							break;
						case Cell.CELL_TYPE_BLANK:
							//System.out.print("\t");
							break;
						}
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
		name=name.trim();
		StringBuilder sb=new StringBuilder("");
		for(int i=0;i<name.length();i++){
			char ch=name.charAt(i);
			if(ch=='\n' || ch=='\t'){
				if(sb.length()!=0){
					sb.append(" ");
				}			
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
		return sb.toString().trim();
	}
}
