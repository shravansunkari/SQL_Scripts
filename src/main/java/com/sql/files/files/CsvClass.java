package com.sql.files.files;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class CsvClass {

	public static BufferedWriter bw = null;
	public static FileWriter fw = null;
	
    public static void main(String[] args) {

    	
    	try {
			fw = new FileWriter("/home/srikanth/Desktop/water_supply_new_final.sql");
			bw = new BufferedWriter(fw);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
        String csvFile = "/home/srikanth/Desktop/new_data.csv";
        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = ";";
        try {

            br = new BufferedReader(new FileReader(csvFile));
            while ((line = br.readLine()) != null) {

                // use comma as separator
                String[] row = line.split(cvsSplitBy);
                
                
                String location_name="NA";
				String location_code="NA";
				long event_gen_ts=0L;
				long event_gen_day=0L;
				double water_quantity=0.0;
				int water_quantity_mu=18;
				double supply_block_count=0;
				double supply_block_capacity=0.0;
				int supply_block_mu=18;
				double impacted_population=0;
				String source="tanker";
				long insert_ts=0L;
				int user_session_id=0;
				try{
				
				location_name=getFormattedString(row[0]);
                location_code=getFormattedCode(row[1]);
                String date=row[2];
                if(row.length>3)
                impacted_population=Double.parseDouble(row[3].trim());
                if(row.length>5)
                	supply_block_count=Double.parseDouble(row[5].trim());
                if(row.length>4)
                supply_block_capacity=Double.parseDouble(row[4].trim());
                if(row.length>6)
                water_quantity=Double.parseDouble(row[6].trim());
                
                
                String result=getYYYYMMDD(date);
				if(result!=null && result.length()>0){
					try{
					event_gen_day = Long.parseLong(result);
					event_gen_ts = getTimeInMillisFromDate(result,"yyyyMMdd");
					}
					catch(Exception e){
						System.out.println("result ="+ result+" cell="+result);
						
					}
				}
				
                if(location_name.length()==0)
                	location_name="NA";
                if(location_code.length()==0)
                	location_code="NA";
                if(supply_block_capacity<1000)
                	supply_block_capacity*=1000.0;
                
                if (!location_name.equals("NA") || !location_code.equals("NA")) {
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
				String query="insert into business_data.water_supply_data(location_type_md_id, location_name, location_code, event_gen_ts, event_gen_day, water_quantity,"
						+"water_quantity_mu, supply_block_count, supply_block_capacity, supply_block_mu, impacted_population, source, insert_ts, user_session_id)"
						+"values("+"(select location_type_md_id from platform_data.location_type_md where name = 'HABITATION'), '"+location_name+"', '"
						+location_code+"', "+event_gen_ts+", "+event_gen_day+", "+water_quantity+", "+water_quantity_mu+", "
						+supply_block_count+", "+supply_block_capacity+", "+supply_block_mu+", "+impacted_population+", '"+source+"', "
						+"(select unix_timestamp()*1000)"+", "+user_session_id+");";
				
				bw.write(query+"\n");
                System.out.println("Done");
                
				}
				}
				catch(Exception e){
					e.printStackTrace();
					//System.out.println("err="+e.getMessage());
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

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
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
				if (sb.length() > 0 && sb.charAt(sb.length() - 1) != ' ') {
					sb.append(" ");
				}
			}
			else if(ch=='\''){
				
			}
			else {
				sb.append(ch);
			}
		}
		//System.out.println("\n\n\nLocation_code="+sb.toString().trim()+"\n\n\n");
		return sb.toString().trim();
	}
    public static String getFormattedCode(String name){
		name=name.trim();
		if(name.contains("&")){
			name=name.substring(0, name.indexOf('&'));
		}
		if(name.contains("and")){
			name=name.substring(0, name.indexOf("and"));
		}
		if(name.contains(",")){
			name=name.substring(0, name.indexOf(","));
		}
		for(int i=0;i<name.length();i++){
			if(name.charAt(i)=='\n' || name.charAt(i)==',' || name.charAt(i)=='.'){
				name=name.substring(0,i);
				break;
			}
		}
		
		int i=0;
		while(i<name.length() && !Character.isDigit(name.charAt(i))){
			i++;
		}
		name=name.substring(i);
		i=name.length()-1;
		while(i>=0 && !Character.isDigit(name.charAt(i))){
			i--;
		}
		if(i!=-1)
			name=name.substring(0,i);
		
		StringBuilder sb=new StringBuilder("");
		i=0;
		int j=0;
		while(j<name.length()){
			if(name.charAt(j)!=' ')
				sb.append(name.charAt(j));
			j++;
		}
		return sb.toString();
	}
	public static String getYYYYMMDD(String date){
		//01.03.2017   23/02/2017   18.02.
		String tmp_date=date;
		if(date.length()>0 && date.charAt(date.length()-1)=='/'){
			date=date.substring(0, date.length()-1);
		}
		//System.out.println("\nGot "+date);
		date=date.trim();
		if(date.contains("\n")){
			date=date.substring(0, date.indexOf('\n'));
		}
		StringBuilder sb=new StringBuilder("");
		if(date.contains(".-")){
			date=date.replace(".-","-");
		}
		//05.-04-2015
		//18.02.2016& 08.04.2016
		String[] tmp=date.split("&");
		
		if(tmp.length>0 && tmp[0].trim().length()==10)
			date=tmp[0].trim();
		else if(tmp.length>1 && tmp[1].trim().length()==10)
			date=tmp[1].trim();
		
		
		//if(date.contains("&") && date.length()==10){
			//return date.split("&")[0];
		//}
		if(date.length()>0 && date.charAt(date.length()-1)!='/' && date.contains("/")){
			String[] arr=date.split("/");
			if(arr.length>2 && arr[2].length()==2){
				arr[2]="20"+arr[2];
			}
			if(arr.length>1 && arr[1].length()==1){
				arr[1]="0"+arr[1];
			}
			if(arr.length>0 && arr[0].length()==1){
				arr[0]="0"+arr[0];
			}
			for(int i=arr.length-1 ;i>=0; i--){
				sb.append(arr[i]);
				//System.out.println("\n\n\n\nim in=="+arr[i]+"\n\n\n");
			}
			
		}else if(date.length()>0 && date.charAt(date.length()-1)!='.' && date.contains(".")){
			String[] arr=date.split("\\.");
			if(arr.length>2 && arr[2].length()==2){
				arr[2]="20"+arr[2];
			}
			if(arr.length>1 && arr[1].length()==1){
				arr[1]="0"+arr[1];
			}
			if(arr.length>0 && arr[0].length()==1){
				arr[0]="0"+arr[0];
			}
			for(int i=arr.length-1 ;i>=0; i--){
				sb.append(arr[i]);
			}
		}
		else if(date.length()>0 && date.charAt(date.length()-1)!='-' && date.contains("-")){
			String[] arr=date.split("-");
			if(arr.length>2 && arr[2].length()==2){
				arr[2]="20"+arr[2];
			}
			if(arr.length>1 && arr[1].length()==1){
				arr[1]="0"+arr[1];
			}
			if(arr.length>0 && arr[0].length()==1){
				arr[0]="0"+arr[0];
			}
			for(int i=arr.length-1 ;i>=0; i--){
				sb.append(arr[i]);
			}
		}
		else if(date.length()>0 && date.charAt(date.length()-1)!=',' && date.contains(",")){
			String[] arr=date.split(",");
			if(arr.length>2 && arr[2].length()==2){
				arr[2]="20"+arr[2];
			}
			if(arr.length>1 && arr[1].length()==1){
				arr[1]="0"+arr[1];
			}
			if(arr.length>0 && arr[0].length()==1){
				arr[0]="0"+arr[0];
			}
			for(int i=arr.length-1 ;i>=0; i--){
				sb.append(arr[i]);
			}
		}
		else{
			for(int i=0;i<date.length();i++){
				if(!Character.isDigit(date.charAt(i))){
					//System.out.println("\n\nReturing null for "+date+" tmp_date="+tmp_date);
					return null;
				}
			}
			sb.append(date);
		}
		//System.out.println("returning ==>"+sb.toString()+"\n\n");
		if(sb.length()>8){
			sb=new StringBuilder(sb.toString().substring(0,8));
		}
		if(sb.length()==7){
			sb=new StringBuilder("20"+sb.substring(1));
		}
		//System.out.println("returing "+sb.toString());
		
		return sb.toString();
		//return "20170320";
	}
	public static long getTimeInMillisFromDate(String modelDate,String format) throws ParseException{
		//Date YYYYMMDD
		DateFormat inputDF  = new SimpleDateFormat(format);
		Date date = inputDF.parse(modelDate);
	
		Calendar cal = Calendar.getInstance();
	    cal.setTime(date);
	    cal.set(Calendar.HOUR_OF_DAY, 23);
	 
		return cal.getTimeInMillis();
	}

}
