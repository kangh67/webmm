package database;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import report.CalFingerprint;

import user.DBBean;

import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

public class UM2919_data {
	/**
	 * transfer the fall data from University of Missouri to another xls file which will be used to put into our database
	 */
	public static String rawFile = "C:\\Users\\hkang1.UTHSAHS\\Google Drive\\AHRQ\\WEBMM\\raw\\report formQi.xls";	
	public static String targetFile = "C:\\Users\\hkang1.UTHSAHS\\Google Drive\\AHRQ\\WEBMM\\raw\\UM2019_fall.txt";
	public static int line_rf = 394;	//including title line
	
	public static void main(String[] args) throws Exception {		
		ArrayList<ArrayList<String>> info = readFile();
		writeToFile(info);
		writeToDB();
	}
	
	public static HashMap<String, String> getAlfToNum() {
		HashMap<String, String> hm = new HashMap<String, String>();
		
		hm.put("a", "0");
		hm.put("b", "1");
		hm.put("c", "2");
		hm.put("d", "3");
		hm.put("e", "4");
		hm.put("f", "5");
		hm.put("g", "6");
		hm.put("h", "7");
		hm.put("i", "8");
		hm.put("j", "9");
		hm.put("k", "10");
		hm.put("l", "11");
		hm.put("m", "12");
		hm.put("n", "13");
		hm.put("o", "14");
		hm.put("p", "15");
		hm.put("q", "16");
		hm.put("r", "17");
		
		return hm;
	}
	
	public static ArrayList<ArrayList<String>> readFile() throws BiffException, IOException{
		ArrayList<ArrayList<String>> info = new ArrayList<ArrayList<String>>();
		HashMap<String, String> alfToNum = getAlfToNum();
		
		Workbook book = Workbook.getWorkbook(new File(rawFile));
		Sheet sheet = book.getSheet(0);
		
		//title line
		ArrayList<String> title = new ArrayList<String>();
		title.add("uni_id");	//0
		title.add("title");	//1
		title.add("des");	//2
		title.add("herf_2");	//3
		title.add("herf_3");	//4 date
		title.add("herf_7");	//5
		title.add("fall_1");	//6
		title.add("fall_2");	//7
		title.add("fall_3");	//8
		title.add("fall_4");	//9
		title.add("fall_5");	//10
		title.add("fall_6");	//11
		title.add("fall_7");	//12
		title.add("fall_8");	//13
		title.add("fall_9");	//14
		title.add("fall_10");	//15
		title.add("fall_11");	//16
		title.add("fall_12");	//17
		title.add("fall_13");	//18
		
		info.add(title);
		
		//remove redundancy for ID
		ArrayList<String> IDList = new ArrayList<String>();
		
		//info
		int i = 1;
		while(i < line_rf) {
			//remove redundancy for ID
			if(IDList.contains(sheet.getCell(0,i).getContents().trim())) {
				i ++;
				continue;
			}
			//remove invalid entry
			if(sheet.getCell(7,i).getContents().trim().length() < 1) {
				i++;
				continue;
			}
			
			//if no redundancy
			IDList.add(sheet.getCell(0,i).getContents().trim());
			
			ArrayList<String> thisInfo = new ArrayList<String>();
			
			thisInfo.add("UM" + sheet.getCell(0,i).getContents().trim());	//0: uni_id
			thisInfo.add("UM" + sheet.getCell(0,i).getContents().trim());	//1: title = uni_id
			thisInfo.add(sheet.getCell(2,i).getContents().trim().replaceAll("\r\n", " ").replaceAll("\t", " "));	//2: description
			thisInfo.add("0");	//3: herf_2
			thisInfo.add(sheet.getCell(5,i).getContents().trim());	//4: herf_3
			thisInfo.add("2");	//5: herf_7
			
			//6: fall_1
			String fall_1 = sheet.getCell(7,i).getContents().trim();
			if(alfToNum.containsKey(fall_1))
				thisInfo.add(alfToNum.get(fall_1));
			else
				thisInfo.add("NA");				
			
			//7: fall_2
			String fall_2 = sheet.getCell(9,i).getContents().trim();
			if(alfToNum.containsKey(fall_2))
				thisInfo.add(alfToNum.get(fall_2));
			else
				thisInfo.add("NA");
		
			//8: fall_3
			String fall_3 = sheet.getCell(11,i).getContents().trim();
			if(alfToNum.containsKey(fall_3))
				thisInfo.add(alfToNum.get(fall_3));
			else
				thisInfo.add("NA");
			
			//9: fall_4
			String fall_4 = sheet.getCell(13,i).getContents().trim();
			if(alfToNum.containsKey(fall_4))
				thisInfo.add(alfToNum.get(fall_4));
			else
				thisInfo.add("NA");
			
			//10: fall_5
			String fall_5 = sheet.getCell(15,i).getContents().trim();
			if(fall_5 != null) {
				String[] temp5 = fall_5.split("\\|\\|");
				String real5 = "";
				for(int j=0; j<temp5.length; j++) {
					if(temp5[j].trim().contains("$$")) 
						temp5[j] = alfToNum.get(temp5[j].split("\\$\\$")[0].trim()) + "$$" + temp5[j].split("\\$\\$")[1].trim();
					else 
						temp5[j] = alfToNum.get(temp5[j].trim());	
				
					if(j == 0)
						real5 = temp5[j];
					else
						real5 = real5 + "||" + temp5[j];
				}				
				if(real5 == null)
					thisInfo.add("NA");
				else
					thisInfo.add(real5);
			}else
				thisInfo.add("NA");
			
			//11: fall_6
			String fall_6 = sheet.getCell(17,i).getContents().trim();
			if(fall_6 == null || fall_6.equals(""))
				thisInfo.add("NA");
			else if(fall_6.contains("$$"))
				thisInfo.add("11$$" + fall_6.split("\\$\\$")[1].trim());
			else
				thisInfo.add(alfToNum.get(fall_6));			
			
			
			//12: fall_7 (column=T, i=19)
			String fall_7 = sheet.getCell(19,i).getContents().trim();
			if(alfToNum.containsKey(fall_7))
				thisInfo.add(alfToNum.get(fall_7));
			else
				thisInfo.add("NA");
			
			//13: fall_8 (column=V, i=21)
			String fall_8 = sheet.getCell(21,i).getContents().trim();
			if(alfToNum.containsKey(fall_8))
				thisInfo.add(alfToNum.get(fall_8));
			else
				thisInfo.add("NA");
			
			//14: fall_9 (column=X, i=23)
			String fall_9 = sheet.getCell(23,i).getContents().trim();
			if(fall_9 != null) {
				String[] temp9 = fall_9.split("\\|\\|");
				String real9 = "";
				for(int j=0; j<temp9.length; j++) {
					if(temp9[j].trim().contains("$$")) 
						temp9[j] = alfToNum.get(temp9[j].split("\\$\\$")[0].trim()) + "$$" + temp9[j].split("\\$\\$")[1].trim();
					else 
						temp9[j] = alfToNum.get(temp9[j].trim());	
				
					if(j == 0)
						real9 = temp9[j];
					else
						real9 = real9 + "||" + temp9[j];
				}			
				if(real9 == null)
					thisInfo.add("NA");
				else
					thisInfo.add(real9);
			}else
				thisInfo.add("NA");
			
			//15: fall_10 (column=Z i=25)
			String fall_10 = sheet.getCell(25,i).getContents().trim();
			if(fall_10 != null) {
				String[] temp10 = fall_10.split("\\|\\|");
				String real10 = "";
				for(int j=0; j<temp10.length; j++) {
					if(temp10[j].trim().contains("$$")) 
						temp10[j] = alfToNum.get(temp10[j].split("\\$\\$")[0].trim()) + "$$" + temp10[j].split("\\$\\$")[1].trim();
					else 
						temp10[j] = alfToNum.get(temp10[j].trim());	
				
					if(j == 0)
						real10 = temp10[j];
					else
						real10 = real10 + "||" + temp10[j];
				}			
				if(real10 == null)
					thisInfo.add("NA");
				else
					thisInfo.add(real10);
			}else
				thisInfo.add("NA");
			
			//16: fall_11 (column=AB, i=27)
			String fall_11 = sheet.getCell(27,i).getContents().trim();			
			if(alfToNum.containsKey(fall_11))
				thisInfo.add(alfToNum.get(fall_11));
			else
				thisInfo.add("NA");
			
			//17: fall_12 (column=AD, i=29)
			String fall_12 = sheet.getCell(29,i).getContents().trim();
			if(alfToNum.containsKey(fall_12))
				thisInfo.add(alfToNum.get(fall_12));
			else
				thisInfo.add("NA");
			
			//18: fall_13 (column=AF, i=31)
			String fall_13 = sheet.getCell(31,i).getContents().trim();
			if(alfToNum.containsKey(fall_13))
				thisInfo.add(alfToNum.get(fall_13));
			else
				thisInfo.add("NA");
			
			
			info.add(thisInfo);
			i ++;
		}			
		
		book.close();
		
		return info;
	}
	
	public static void writeToFile(ArrayList<ArrayList<String>> info) throws IOException {
		DataOutputStream dos = new DataOutputStream(new FileOutputStream(new File(targetFile)));
		
		for(int i=0; i<info.size(); i++) {
			dos.writeBytes(info.get(i).get(0));
			for(int j=1; j<info.get(i).size(); j++) 
				dos.writeBytes("\t" + info.get(i).get(j));			
			
			dos.writeBytes("\r\n");
		}
		
		dos.close();
	}
	
	public static void writeToDB() throws Exception {
		BufferedReader br = new BufferedReader(new FileReader(targetFile));		
		DBBean dbb = new DBBean();		
		Connection con= dbb.getConn();
		String line = "";
		
		line = br.readLine();
		while((line = br.readLine()) != null) {
			//----report_general
			String[] thisline = line.split("\t");
			String uni_id = thisline[0];
			String rname = thisline[0];
			String des = thisline[2];
			SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
			String uptime = df.format(new Date());
			String initime = thisline[4];
			String reporter = "University of Missouri";
			String rstatus = "2";
			String formn = "fall";
			String herf_2 = thisline[3];
			String herf_7 = thisline[5];
			
			//---fingerprint
			String herf_2_f = CalFingerprint.getFPofOneQuestion(dbb, "herf_2", herf_2);
			String herf_7_f = CalFingerprint.getFPofOneQuestion(dbb, "herf_7", herf_7);
			//---
			
			String sql = "insert into report_general (uni_id,initime,uptime,reporter,rstatus,formn,rname,des,herf_2,herf_2_f,herf_7,herf_7_f)values(?,?,?,?,?,?,?,?,?,?,?,?)";
		    PreparedStatement ps = con.prepareStatement(sql);
		    
		    ps.setString(1, uni_id);
		    ps.setString(2, initime);
		    ps.setString(3, uptime);
		    ps.setString(4, reporter);
		 	ps.setString(5, rstatus);
		 	ps.setString(6, formn);
		 	ps.setString(7, rname);
		 	ps.setString(8, des);
		 	ps.setString(9, herf_2);
		 	ps.setString(10, herf_2_f);
		 	ps.setString(11, herf_7);
		 	ps.setString(12, herf_7_f);		 	
		 	
		 	ps.executeUpdate();
		 	ps.close();			
			//---------
		 	
		 	//----report_fall		 	
		 	ArrayList<String> qtitle = new ArrayList<String>();
		 	ArrayList<String> qcontent = new ArrayList<String>();
		 	
		 	for(int i=6; i<thisline.length; i++) {
		 		if(!thisline[i].equals("NA")) {
		 			qtitle.add("fall_" + (i-5));
		 			qcontent.add(thisline[i]);
		 		}
		 	}		 	
		 	
		 	sql = "insert into report_fall (uni_id";
		 	for(int i=0; i<qtitle.size(); i++)
		 		sql = sql + "," + qtitle.get(i);
		 	
		 	for(int j=1; j<=13; j++)
		 		sql = sql + ",fall_" + j + "_f";
		 	
		 	sql = sql + ")values(?";
		 	for(int i=0; i<qcontent.size(); i++)
		 		sql = sql + ",?";
		 	
		 	for(int j=1; j<=13; j++)
		 		sql = sql + ",?";
		 	
		 	sql = sql + ")";
		 	
		 	ps = con.prepareStatement(sql);
		 	
		 	ps.setString(1, uni_id);
		 	int i=0;		 	
		 	for(i=0; i<qcontent.size(); i++) 		 		
		 		ps.setString(i+2, qcontent.get(i));		 	
		 	
		 	//fingerprint
		 	for(int j=1; j<=13; j++) {		 		
		 		if(qtitle.contains("fall_" + j))
		 			ps.setString(i+2, CalFingerprint.getFPofOneQuestion(dbb, "fall_" + j, qcontent.get(qtitle.indexOf("fall_" + j))));		 			
		 		else
		 			ps.setString(i+2, CalFingerprint.getFPofOneQuestion(dbb, "fall_" + j, null));
		 		i ++;
		 	}		 	
		 	
		 	ps.executeUpdate();
		 	ps.close();			 	
		 	//----
		 	
		 	System.out.println(uni_id + " finished!");
		}
		
		con.close();		
		br.close();
		dbb.disConnect();
	}
}
