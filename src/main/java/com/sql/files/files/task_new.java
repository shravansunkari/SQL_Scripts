package com.sql.files.files;

import java.io.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import org.apache.poi.hssf.util.CellReference;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
//import org.apache.poi.xssf.usermodel.XSSFSheet;
//import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class task_new {

	public static BufferedWriter bw = null;
	public static FileWriter fw = null;
	public static int row_count = 0;
	public static int date_count = 0;
	public static int date_index_count = 0;
	public static int capacity_index = 0;

	public static void main(String[] args) {

		try {
			fw = new FileWriter("/home/srikanth/Desktop/water_supply_new.sql");
			bw = new BufferedWriter(fw);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		File folder = new File("/home/srikanth/Desktop/files_all");
		File[] listOfFiles = folder.listFiles();

		for (int i = 0; i < listOfFiles.length; i++) {
			if (listOfFiles[i].isFile()) {
				start("/home/srikanth/Desktop/files_all/", listOfFiles[i].getName());
				// System.out.println("File " + listOfFiles[i].getName());
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

		System.out.println("Total rows found " + row_count);
		System.out.println("Total dates found " + date_count);
		System.out.println("Total dates indexes found " + date_index_count);
		System.out.println("Total Capacity indexes found " + capacity_index);

		// start();
	}

	public static void start(String filePath, String fileName) {
		try {
			FileInputStream file = new FileInputStream(new File(filePath + fileName));

			// org.apache.poi.ss.usermodel.Workbook workbook =
			// WorkbookFactory.create(file);

			// FileInputStream file = new FileInputStream(new File(FILE PATH));

			// XSSFWorkbook workbook1 = new XSSFWorkbook(new FileInputStream(new
			// File(filePath + fileName)));

			Workbook workbook = WorkbookFactory.create(file);

			int noOfSheets = workbook.getNumberOfSheets();
			//noOfSheets=2;
			for (int i = 0; i < noOfSheets; i++) {

				Map<String, Integer> col_indexes = new HashMap<String, Integer>();
				org.apache.poi.ss.usermodel.Sheet sheet = workbook.getSheetAt(i);

				// Iterate through each rows one by one
				Iterator<Row> rowIterator = sheet.iterator();

				int index = -1;
				int row_num = 0;
				while (rowIterator.hasNext()) {
					Row row = rowIterator.next();

					Iterator<Cell> cellIterator = row.cellIterator();
					while (cellIterator.hasNext()) {
						Cell cell = cellIterator.next();
						index = cell.getColumnIndex();
						// System.out.print("\tindex="+index+"\t");
						switch (cell.getCellType()) {

						case Cell.CELL_TYPE_STRING:
							// System.out.println("File="+fileName+"\tCurrent
							// column==>"+cell.getStringCellValue());
							if (!col_indexes.containsKey("Habitation_name")
									&& (cell.getStringCellValue().equalsIgnoreCase("Habitation Name")
											|| cell.getStringCellValue().equalsIgnoreCase("Habitation ")
											|| cell.getStringCellValue().equalsIgnoreCase("Habitation"))) {
								col_indexes.put("Habitation_name", index);
							} else if (!col_indexes.containsKey("Habitation_code")
									&& (getFormattedString(cell.getStringCellValue()).contains("code")
											|| getFormattedString(cell.getStringCellValue()).contains("Code")
											|| getFormattedString(cell.getStringCellValue())
													.equalsIgnoreCase("Habitation code")
											|| getFormattedString(cell.getStringCellValue())
													.equalsIgnoreCase("Habitation Code"))) {
								col_indexes.put("Habitation_code", index);
							} else if (!col_indexes.containsKey("DateOfStartTrans")
									&& (getFormattedString(cell.getStringCellValue()).contains("Date")
											|| getFormattedString(cell.getStringCellValue()).equalsIgnoreCase("Date of Start of Transportation")
											|| getFormattedString(cell.getStringCellValue())
													.equalsIgnoreCase("Date of starting"))) {
								col_indexes.put("DateOfStartTrans", index);
								date_index_count++;
							} else if (!col_indexes.containsKey("QuaInKiloLit")
									&& (getFormattedString(cell.getStringCellValue()).contains("Quantity")
											|| getFormattedString(cell.getStringCellValue())
													.equalsIgnoreCase("Quantity in Kilo litres")
											|| getFormattedString(cell.getStringCellValue())
													.equalsIgnoreCase("Quantity supplied in Kilo litres daily"))) {
								col_indexes.put("QuaInKiloLit", index);
							} else if (!col_indexes.containsKey("NoOfTrips")
									&& (getFormattedString(cell.getStringCellValue()).contains("Trips")
											|| getFormattedString(cell.getStringCellValue())
													.equalsIgnoreCase("No.of Trips")
											|| getFormattedString(cell.getStringCellValue()).equalsIgnoreCase("No"))) {
								col_indexes.put("NoOfTrips", index);
							} else if (!col_indexes.containsKey("TankerCapacity")
									&& (getFormattedString(cell.getStringCellValue()).contains("Tanker")
											|| getFormattedString(cell.getStringCellValue())
													.equalsIgnoreCase("Tanker Capacity")
											|| getFormattedString(cell.getStringCellValue())
													.equalsIgnoreCase("Tanker Capacity in KL"))) {
								// System.out.println("Got tanked capacity");
								col_indexes.put("TankerCapacity", index);
								capacity_index++;
							}
							// Population served
							else if (!col_indexes.containsKey("population_served")
									&& (getFormattedString(cell.getStringCellValue()).contains("Population Served ")
											|| getFormattedString(cell.getStringCellValue())
													.contains("Population Served")
											|| getFormattedString(cell.getStringCellValue())
													.contains("Population served ")
											|| getFormattedString(cell.getStringCellValue())
													.equalsIgnoreCase("Population served"))) {
								// System.out.println("Got tanked capacity");
								col_indexes.put("population_served", index);
							}
							// System.out.println(cell.getStringCellValue());
							break;
						}
					}
					// System.out.println();
					row_num++;
					// if (index != -1)
					// break;

				}
				if (index == -1) {
					System.out.println("\n\nColumns not found in sheet num:" + (i + 1) + " file=" + fileName + "\n\n");
					return;
				}
				System.out.println("\nIndexes : in "+fileName);
				for(String key: col_indexes.keySet()){
				 System.out.println(key+"\t"+col_indexes.get(key));
				 }

				rowIterator = sheet.iterator();

				while (rowIterator.hasNext()) {
					boolean header_row = false;
					String location_name = "NA";
					String location_code = "NA";
					long event_gen_ts = 0L;
					long event_gen_day = 0L;
					double water_quantity = 0.0;
					int water_quantity_mu = 18;
					double supply_block_count = 0.0;
					double supply_block_capacity = 0.0;
					int supply_block_mu = 18;
					int impacted_population = 0;
					String source = "tanker";
					long insert_ts = 0L;
					int user_session_id = 0;
					Row row = rowIterator.next();

					// For each row, iterate through all the columns
					Iterator<Cell> cellIterator = row.cellIterator();
					while (cellIterator.hasNext()) {
						Cell cell = cellIterator.next();
						index = cell.getColumnIndex();
						if (col_indexes.containsKey("Habitation_name") && col_indexes.get("Habitation_name") == index) {
							switch (cell.getCellType()) {

							case Cell.CELL_TYPE_STRING:
								location_name = getFormattedString(cell.getStringCellValue());
								break;
							}
						} else if (col_indexes.containsKey("Habitation_code")
								&& col_indexes.get("Habitation_code") == index) {
							switch (cell.getCellType()) {

							case Cell.CELL_TYPE_STRING:
								location_code = getFormattedCode(cell.getStringCellValue());
								// System.out.println("\n\n\nLocation_code="+location_code+"\n\n\n");
								break;
							}
						} else if (col_indexes.containsKey("DateOfStartTrans")
								&& col_indexes.get("DateOfStartTrans") == index) {
							switch (cell.getCellType()) {

							case Cell.CELL_TYPE_STRING:
								if (cell.getStringCellValue().equalsIgnoreCase("Date of Start of Transportation")
										|| cell.getStringCellValue().equalsIgnoreCase("Human Population")) {
									header_row = true;
								}
								String result = getYYYYMMDD(cell.getStringCellValue());
								if (result != null && result.length() > 0) {
									try {
										event_gen_day = Long.parseLong(result);
										event_gen_ts = getTimeInMillisFromDate(result, "yyyyMMdd");
										if (event_gen_day == 0)
											System.out.println("\n\nFile= " + fileName + "  result =" + result
													+ " cell=" + cell.getStringCellValue());
										// System.exit(1);
									} catch (Exception e) {
										// System.out.println("result ="+
										// result+"
										// cell="+cell.getStringCellValue());

									}
								} else {
									// System.out.println("\n\n\nFile="+fileName+"
									// result="+result+"
									// cell="+cell.getStringCellValue()+" Did
									// not get\n\n\n");
								}
								// get event_gen_ts, event_gen_day
								// 01.03.2017 23/02/2017 18.02.
								break;
							}
						} else if (col_indexes.containsKey("QuaInKiloLit")
								&& col_indexes.get("QuaInKiloLit") == index) {
							switch (cell.getCellType()) {

							case Cell.CELL_TYPE_NUMERIC:
								water_quantity = cell.getNumericCellValue();
								break;
							}
						} else if (col_indexes.containsKey("population_served")
								&& col_indexes.get("population_served") == index) {
							switch (cell.getCellType()) {

							case Cell.CELL_TYPE_NUMERIC:
								impacted_population = (int) cell.getNumericCellValue();
								break;
							}
						} else if (col_indexes.containsKey("NoOfTrips") && col_indexes.get("NoOfTrips") == index) {
							switch (cell.getCellType()) {

							case Cell.CELL_TYPE_NUMERIC:
								supply_block_count = cell.getNumericCellValue();
								break;
							}
						} else if (col_indexes.containsKey("TankerCapacity")
								&& col_indexes.get("TankerCapacity") == index) {
							switch (cell.getCellType()) {

							case Cell.CELL_TYPE_NUMERIC:
								supply_block_capacity = cell.getNumericCellValue();
								break;
							case Cell.CELL_TYPE_STRING:
								if (!cell.getStringCellValue().contains("Capacity")
										&& Character.isDigit(cell.getStringCellValue().charAt(0))
										&& cell.getStringCellValue().indexOf('K') != -1) {

									// System.out.println("Value=
									// "+cell.getStringCellValue());
									String res = cell.getStringCellValue()
											.substring(0, cell.getStringCellValue().indexOf('K')).trim();
									if (res.contains("&")) {
										res = res.split("&")[0];
									}
									supply_block_capacity = Integer.parseInt(res.trim());
								}
								// supply_block_capacity =
								// Integer.parseInt(cell.getStringCellValue().substring(0,cell.getStringCellValue().indexOf('K')));
								break;
							}
						}

					}
					if (header_row == true)
						continue;
					// System.out.println(location_name+"\t"+location_code+"\t"+event_gen_ts+"\t"+event_gen_day+"\t"
					// +water_quantity+"\t"+water_quantity_mu+"\t"+supply_block_count+"\t"+supply_block_capacity
					// +supply_block_mu+"\t"+source+"\t"+insert_ts+"\t"+user_session_id);
					try {
						if (!location_name.contains("Habitation") && !location_code.contains("total")
								&& !location_code.contains("Total") && !location_code.contains("TOTAL")
								&& !location_name.contains("Total :")
								&& (!location_name.equals("NA") || !location_code.equals("NA"))) {
							if (water_quantity == 0.0)
								if(supply_block_capacity>1000)
									water_quantity = (supply_block_capacity * supply_block_count) / 1000.0;
								else{
									water_quantity = (supply_block_capacity * supply_block_count);
								}
							if (supply_block_count == 0.0) {
								// System.out.println("\n\nFile="+fileName+"\tlocation_name="+location_name);
							}
							if(supply_block_capacity>1000)
								supply_block_capacity = supply_block_capacity / 1000.0;
							
							String query = "insert into business_data.water_supply_data(location_type_md_id, location_name, location_code, event_gen_ts, event_gen_day, water_quantity,"
									+ "water_quantity_mu, supply_block_count, supply_block_capacity, supply_block_mu, impacted_population, source, insert_ts, user_session_id)"
									+ "values("
									+ "(select location_type_md_id from platform_data.location_type_md where name = 'HABITATION'), '"
									+ location_name + "', '" + location_code + "', " + event_gen_ts + ", "
									+ event_gen_day + ", " + water_quantity + ", " + water_quantity_mu + ", "
									+ supply_block_count + ", " + supply_block_capacity + ", " + supply_block_mu + ", "
									+ impacted_population + ", '" + source + "', " + "(select unix_timestamp()*1000)"
									+ ", " + user_session_id + ");";

							bw.write(query + "\n");
							row_count++;

							if (location_name.contains("Naraharipeta HW")) {
								System.out.println("\n\nFile " + fileName);
							}

							/**
							 * For debugging purpose Prints each row
							 * 
							 * //if(event_gen_day==0)
							 * //if(location_name.contains("Naraharipeta HW"))
							 * //if(supply_block_capacity==0.0)
							 * //if(fileName.equals("Chittoor.xlsx")) if(false)
							 * { cellIterator = row.cellIterator();
							 * System.out.println("File name= "+fileName); while
							 * (cellIterator.hasNext()) { Cell cell =
							 * cellIterator.next(); switch (cell.getCellType())
							 * { case Cell.CELL_TYPE_NUMERIC:
							 * System.out.print(cell.getNumericCellValue() +
							 * "\t"); break; case Cell.CELL_TYPE_STRING:
							 * System.out.print(cell.getStringCellValue() +
							 * "\t"); //System.out.println("index=" +
							 * cell.getColumnIndex());
							 * 
							 * break; case Cell.CELL_TYPE_BLANK:
							 * System.out.print("Blank\t"); break; case
							 * Cell.CELL_TYPE_FORMULA:
							 * System.out.print(cell.getCellFormula()+"\t");
							 * break; } } System.out.println();
							 * //System.exit(1); //System.out.println("\n\nFile
							 * ="+fileName+"Map="+col_indexes.get("DateOfStartTrans"));
							 * }
							 */
						}
					} catch (Exception e) {
						System.out.println(e.getMessage());
					}

				}

			}
			file.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static String getFormattedString(String name) {
		name = name.trim();
		StringBuilder sb = new StringBuilder("");
		for (int i = 0; i < name.length(); i++) {
			char ch = name.charAt(i);
			if (ch == '\n' || ch == '\t') {
				if (sb.length() != 0) {
					sb.append(" ");
				}
			} else if (ch == ' ') {
				// append space only if there is no space
				if (sb.length() > 0 && sb.charAt(sb.length() - 1) != ' ') {
					sb.append(" ");
				}
			} else if (ch == '\'') {
				// remove ' from the string
			} else {
				sb.append(ch);
			}
		}
		return sb.toString().trim();
	}

	public static String getFormattedCode(String name) {
		name = name.trim();
		for (int i = 0; i < name.length(); i++) {
			if (name.charAt(i) == '\n' || name.charAt(i) == ',') {
				name = name.substring(0, i);
				break;
			}
		}

		int i = 0;
		// remove non-digit characters at the beginning of the string
		while (i < name.length() && !Character.isDigit(name.charAt(i))) {
			i++;
		}
		name = name.substring(i);
		i = name.length() - 1;
		// remove non-digit characters at the end
		while (i >= 0 && !Character.isDigit(name.charAt(i))) {
			i--;
		}
		if (i != -1)
			name = name.substring(0, i);

		StringBuilder sb = new StringBuilder("");
		i = 0;
		int j = 0;
		// remove spaces within string
		while (j < name.length()) {
			if (name.charAt(j) != ' ')
				sb.append(name.charAt(j));
			j++;
		}
		return sb.toString();
	}

	public static String getYYYYMMDD(String date) {
		/**
		 * Formats of date
		 * 
		 * 01-03-2017 01.03.2017 23/02/2017 18.02. 05.-04-2015 18.02.2016& 08.04.2016
		 * 
		 * 2051021
		 */
		StringBuilder sb = new StringBuilder("");

		if (date.charAt(date.length() - 1) == '/') {
			date = date.substring(0, date.length() - 1);
		}
		date = date.trim();
		if (date.contains("\n")) {
			date = date.substring(0, date.indexOf('\n'));
		}

		if (date.contains(".-")) {
			date = date.replace(".-", "-");
		}
		String[] tmp = date.split("&");

		if (tmp.length > 0 && tmp[0].trim().length() == 10)
			date = tmp[0].trim();
		else if (tmp.length > 1 && tmp[1].trim().length() == 10)
			date = tmp[1].trim();

		if (date.contains("&") && date.length() == 10) {
			return date.split("&")[0];
		}

		if (date.length() > 0 && date.charAt(date.length() - 1) != '/' && date.contains("/")) {
			String[] arr = date.split("/");
			if (arr.length > 2 && arr[2].length() == 2) {
				arr[2] = "20" + arr[2];
			}
			if (arr.length > 1 && arr[1].length() == 1) {
				arr[1] = "0" + arr[1];
			}
			if (arr.length > 0 && arr[0].length() == 1) {
				arr[0] = "0" + arr[0];
			}
			for (int i = arr.length - 1; i >= 0; i--) {
				sb.append(arr[i]);
			}

		} else if (date.length() > 0 && date.charAt(date.length() - 1) != '.' && date.contains(".")) {
			String[] arr = date.split("\\.");
			if (arr.length > 2 && arr[2].length() == 2) {
				arr[2] = "20" + arr[2];
			}
			if (arr.length > 1 && arr[1].length() == 1) {
				arr[1] = "0" + arr[1];
			}
			if (arr.length > 0 && arr[0].length() == 1) {
				arr[0] = "0" + arr[0];
			}
			for (int i = arr.length - 1; i >= 0; i--) {
				sb.append(arr[i]);
			}
		} else if (date.length() > 0 && date.charAt(date.length() - 1) != '-' && date.contains("-")) {
			String[] arr = date.split("-");
			if (arr.length > 2 && arr[2].length() == 2) {
				arr[2] = "20" + arr[2];
			}
			if (arr.length > 1 && arr[1].length() == 1) {
				arr[1] = "0" + arr[1];
			}
			if (arr.length > 0 && arr[0].length() == 1) {
				arr[0] = "0" + arr[0];
			}
			for (int i = arr.length - 1; i >= 0; i--) {
				sb.append(arr[i]);
			}
		} else if (date.length() > 0 && date.charAt(date.length() - 1) != ',' && date.contains(",")) {
			String[] arr = date.split(",");
			if (arr.length > 2 && arr[2].length() == 2) {
				arr[2] = "20" + arr[2];
			}
			if (arr.length > 1 && arr[1].length() == 1) {
				arr[1] = "0" + arr[1];
			}
			if (arr.length > 0 && arr[0].length() == 1) {
				arr[0] = "0" + arr[0];
			}
			for (int i = arr.length - 1; i >= 0; i--) {
				sb.append(arr[i]);
			}
		} else {
			for (int i = 0; i < date.length(); i++) {
				if (!Character.isDigit(date.charAt(i))) {
					return null;
				}
			}
			sb.append(date);
		}
		if (sb.length() > 8) {
			sb = new StringBuilder(sb.toString().substring(0, 8));
		}
		date_count++;
		return sb.toString();
		// return "20170320";
	}

	// Generic method for all splits
	public static StringBuilder splitAndFormat(String date, String splitChar) {

		StringBuilder sb = new StringBuilder("");
		if (date.length() > 0 && date.charAt(date.length() - 1) != splitChar.charAt(0) && date.contains(splitChar)) {

			String[] arr = date.split(splitChar);

			if (arr.length > 2 && arr[2].length() == 2) {
				arr[2] = "20" + arr[2];
			}
			if (arr.length > 1 && arr[1].length() == 1) {
				arr[1] = "0" + arr[1];
			}
			if (arr.length > 0 && arr[0].length() == 1) {
				arr[0] = "0" + arr[0];
			}
			for (int i = arr.length - 1; i >= 0; i--) {
				sb.append(arr[i]);
			}
		}
		return sb;
	}

	public static long getTimeInMillisFromDate(String modelDate, String format) throws ParseException {
		// Date YYYYMMDD
		DateFormat inputDF = new SimpleDateFormat(format);
		Date date = inputDF.parse(modelDate);

		Calendar cal = Calendar.getInstance();
		cal.setTime(date);

		return cal.getTimeInMillis();
	}
}
