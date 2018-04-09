package database;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import contributing_factor.CF_structured;
import report.CalFingerprint;

import user.DBBean;

import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

public class SQL_MCPS_2014 {
	/**
	 * transfer the fall data from Tina to another xls file which will be used to put into our database
	 */
	public static String rawFile = "C:\\Users\\hkang1\\Google Drive\\Tina Data\\2014 Fall Report Form for UT.xls";
	public static int line_rf = 374;	//including title line
	public static String targetFile = "C:\\Users\\hkang1\\Google Drive\\AHRQ\\WEBMM\\raw\\MCPS_fall_2014.txt";
	public static String SQLFile = "C:\\Users\\hkang1\\Google Drive\\AHRQ\\WEBMM\\sql\\MCPS_fall_2014.sql";
	
	public static void main(String[] args) throws Exception {
		ArrayList<ArrayList<String>> info = readFile();
		writeToFile(info);
		//writeToDB();
		writeSQL();
	}
	
	public static ArrayList<ArrayList<String>> readFile() throws BiffException, IOException{
		ArrayList<ArrayList<String>> info = new ArrayList<ArrayList<String>>();
		
		Workbook book = Workbook.getWorkbook(new File(rawFile));
		Sheet sheet = book.getSheet(0);
		
		//title line
		ArrayList<String> title = new ArrayList<String>();
		title.add("uni_id");	//0
		title.add("title");	//1
		title.add("herf_2");	//2
		title.add("herf_7");	//3
		title.add("fall_1");	//4
		title.add("fall_2");	//5
		title.add("fall_3");	//6
		title.add("fall_4");	//7
		title.add("fall_5");	//8
		title.add("fall_6");	//9
		title.add("fall_7");	//10
		title.add("fall_8");	//11
		title.add("fall_9");	//12
		title.add("fall_10");	//13
		title.add("fall_11");	//14
		title.add("fall_12");	//15
		title.add("fall_13");	//16
		
		info.add(title);
		
		//info
		for(int i=1; i<line_rf; i++) {
			ArrayList<String> thisInfo = new ArrayList<String>();
			
			thisInfo.add("MCPS14_" + sheet.getCell(0,i).getContents().trim());	//0: uni_id
			thisInfo.add("MCPS14_" + sheet.getCell(0,i).getContents().trim());	//1: title = uni_id
			thisInfo.add("0");	//2: herf_2
			thisInfo.add("2");	//3: herf_7
			
			//4: fall_1
			String fall_1 = sheet.getCell(1,i).getContents().trim();
			if(fall_1.equalsIgnoreCase("Unassisted"))
				thisInfo.add("0");
			else if(fall_1.equalsIgnoreCase("Assisted"))
				thisInfo.add("1");
			else
				thisInfo.add("2");
			
			//5: fall_2
			String fall_2 = sheet.getCell(2,i).getContents().trim();
			if(fall_2.equalsIgnoreCase("Yes"))
				thisInfo.add("0");
			else if(fall_2.equalsIgnoreCase("No"))
				thisInfo.add("1");
			else 
				thisInfo.add("2");
		
			//6: fall_3
			String fall_3 = sheet.getCell(3,i).getContents().trim();
			if(fall_2.equalsIgnoreCase("Yes")) {
				if(fall_3.equalsIgnoreCase("Staff"))
					thisInfo.add("0");
				else if(fall_3.equalsIgnoreCase("Visitor, family, or another patient, but not staff"))
					thisInfo.add("1");
				else
					thisInfo.add("NA");					
			}else
				thisInfo.add("NA");	
			
			//7: fall_4
			String fall_4 = sheet.getCell(4,i).getContents().trim();
			if(fall_4.equalsIgnoreCase("Yes"))
				thisInfo.add("0");
			else if(fall_4.equalsIgnoreCase("No"))
				thisInfo.add("1");
			else 
				thisInfo.add("2");
			
			//8: fall_5
			String fall_5 = sheet.getCell(5,i).getContents().trim();
			if(fall_4.equalsIgnoreCase("Yes")) {
				if(fall_5.contains("Dislocation"))
					thisInfo.add("0");
				else if(fall_5.contains("Fracture"))
					thisInfo.add("1");
				else if(fall_5.contains("Intracranial injury"))
					thisInfo.add("2");
				else if(fall_5.contains("Laceration requiring sutures"))
					thisInfo.add("3");
				else if(fall_5.contains("Skin tear"))
					thisInfo.add("4");
				else if(!sheet.getCell(6,i).getContents().trim().equalsIgnoreCase("Not Answered"))
					thisInfo.add("5$$" + sheet.getCell(6,i).getContents().trim());
				else
					thisInfo.add("NA");					
			}else
				thisInfo.add("NA");
			
			//9: fall_6
			String fall_6 = sheet.getCell(7,i).getContents().trim();
			if(fall_6.contains("Ambulating without"))
				thisInfo.add("0");
			else if(fall_6.contains("Ambulating with assistance"))
				thisInfo.add("1");
			else if(fall_6.contains("Changing position"))
				thisInfo.add("2");
			else if(fall_6.contains("Dressing or undressing"))
				thisInfo.add("3");
			else if(fall_6.contains("Navigating bedrails"))
				thisInfo.add("4");
			else if(fall_6.contains("Reaching for an item"))
				thisInfo.add("5");
			else if(fall_6.contains("Showering or bathing"))
				thisInfo.add("6");
			else if(fall_6.contains("Toileting"))
				thisInfo.add("7");
			else if(fall_6.contains("Transferring to or from bed"))
				thisInfo.add("8");
			else if(fall_6.contains("Undergoing a diagnostic"))
				thisInfo.add("9");
			else if(fall_6.contains("Unknown"))
				thisInfo.add("10");
			else if(!sheet.getCell(8,i).getContents().trim().equalsIgnoreCase("Not Answered"))
				thisInfo.add("11$$" + sheet.getCell(8,i).getContents().trim());
			else
				thisInfo.add("10");
			
			//10: fall_7 (column=J, i=9)
			String fall_7 = sheet.getCell(9,i).getContents().trim();
			if(fall_7.equalsIgnoreCase("Yes"))
				thisInfo.add("0");
			else if(fall_7.equalsIgnoreCase("No"))
				thisInfo.add("1");
			else 
				thisInfo.add("2");
			
			//11: fall_8 (column=K, i=10)
			String fall_8 = sheet.getCell(10,i).getContents().trim();
			if(fall_7.equalsIgnoreCase("Yes")) {
				if(fall_8.equalsIgnoreCase("Yes"))
					thisInfo.add("0");
				else if(fall_8.equalsIgnoreCase("No"))
					thisInfo.add("1");
				else
					thisInfo.add("2");				
			}else
				thisInfo.add("NA");
			
			//12: fall_9 multi (column=L,M, i=11,12)
			String fall_9 = sheet.getCell(11,i).getContents().trim();
			String data = "";
			if(fall_9.contains("History of previous fall"))
				data = data + "||0";
			if(fall_9.contains("Prosthesis or specialty"))
				data = data + "||1";
			if(fall_9.contains("Sensory impairment"))
				data = data + "||2";
			if(!sheet.getCell(12,i).getContents().trim().equalsIgnoreCase("Not Answered"))
				data = data + "||5$$" + sheet.getCell(12,i).getContents().trim();
			
			if(!data.equals(""))			
				thisInfo.add(data.substring(2));	//delete the first ||			
			else if(fall_9.contains("None"))
				thisInfo.add("3");
			else
				thisInfo.add("4");
			
			//13: fall_10 multi (column=N,O i=13,14)
			String fall_10 = sheet.getCell(13,i).getContents().trim();
			data = "";
			if(fall_10.contains("Assistive device"))
				data = data + "||0";
			if(fall_10.contains("Bed or chair alarm"))
				data = data + "||1";
			if(fall_10.contains("Bed in low position"))
				data = data + "||2";
			if(fall_10.contains("Call light"))
				data = data + "||3";
			if(fall_10.contains("Change in medication"))
				data = data + "||4";
			if(fall_10.contains("Non-slip floor mats"))
				data = data + "||5";
			if(fall_10.contains("Hip and"))
				data = data + "||6";
			if(fall_10.contains("Non-slip footwear"))
				data = data + "||7";
			if(fall_10.contains("Patient and family education"))
				data = data + "||8";
			if(fall_10.contains("Patient sitting close to the") || fall_10.contains("Patient situated close to the"))
				data = data + "||9";
			if(fall_10.contains("Physical"))
				data = data + "||10";
			if(fall_10.contains("Sitter"))
				data = data + "||11";
			if(fall_10.contains("Supplemental environmental"))
				data = data + "||12";
			if(fall_10.contains("Toileting regimen"))
				data = data + "||13";
			if(fall_10.contains("Visible identification"))
				data = data + "||14";
			if(!sheet.getCell(14,i).getContents().trim().equalsIgnoreCase("Not Answered"))
				data = data + "||17$$" + sheet.getCell(14,i).getContents().trim();
			
			if(!data.equals(""))			
				thisInfo.add(data.substring(2));	//delete the first ||			
			else if(fall_10.contains("None"))
				thisInfo.add("15");
			else
				thisInfo.add("16");
			
			//14: fall_11 (column=P, i=15)
			String fall_11 = sheet.getCell(15,i).getContents().trim();
			if(fall_11.equalsIgnoreCase("Yes"))
				thisInfo.add("0");
			else if(fall_11.equalsIgnoreCase("No"))
				thisInfo.add("1");
			else 
				thisInfo.add("2");
			
			//15: fall_12 (column=Q, i=16)
			String fall_12 = sheet.getCell(16,i).getContents().trim();
			if(fall_11.equalsIgnoreCase("Yes")) {
				if(fall_12.equalsIgnoreCase("Yes"))
					thisInfo.add("0");
				else if(fall_12.equalsIgnoreCase("No"))
					thisInfo.add("1");
				else
					thisInfo.add("2");	
			}else
				thisInfo.add("NA");
			
			//16: fall_13 (column=R, i=17)
			String fall_13 = sheet.getCell(17,i).getContents().trim();
			if(fall_13.equalsIgnoreCase("Yes"))
				thisInfo.add("0");
			else if(fall_13.equalsIgnoreCase("No"))
				thisInfo.add("1");
			else 
				thisInfo.add("2");
			
			
			info.add(thisInfo);
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
			SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
			String initime = df.format(new Date());	
			String uptime = initime;
			String reporter = "Missouri Center for Patient Safety";
			String rstatus = "2";
			String formn = "fall";
			String herf_2 = thisline[2];
			String herf_7 = thisline[3];
			
			//---fingerprint
			String herf_2_f = CalFingerprint.getFPofOneQuestion(dbb, "herf_2", herf_2);
			String herf_7_f = CalFingerprint.getFPofOneQuestion(dbb, "herf_7", herf_7);
			//---
			
			String sql = "insert into report_general (uni_id,initime,uptime,reporter,rstatus,formn,rname,herf_2,herf_2_f,herf_7,herf_7_f)values(?,?,?,?,?,?,?,?,?,?,?)";
		    PreparedStatement ps = con.prepareStatement(sql);
		    
		    ps.setString(1, uni_id);
		    ps.setString(2, initime);
		    ps.setString(3, uptime);
		    ps.setString(4, reporter);
		 	ps.setString(5, rstatus);
		 	ps.setString(6, formn);
		 	ps.setString(7, rname);
		 	ps.setString(8, herf_2);
		 	ps.setString(9, herf_2_f);
		 	ps.setString(10, herf_7);
		 	ps.setString(11, herf_7_f);		 	
		 	
		 	ps.executeUpdate();
		 	ps.close();			
			//---------
		 	
		 	//----report_fall		 	
		 	ArrayList<String> qtitle = new ArrayList<String>();
		 	ArrayList<String> qcontent = new ArrayList<String>();
		 	
		 	for(int i=4; i<thisline.length; i++) {
		 		if(!thisline[i].equals("NA")) {
		 			qtitle.add("fall_" + (i-3));
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
		
		br.close();
		con.close();
		dbb.disConnect();
		
	}
	
	public static void writeSQL() throws IOException, BiffException, SQLException {
		DataOutputStream dos = new DataOutputStream(new FileOutputStream(new File(SQLFile)));
		BufferedReader br = new BufferedReader(new FileReader(targetFile));	
		DBBean dbb = new DBBean();	
		
		//---Head
		dos.writeBytes("/*" + "\r\n");
		dos.writeBytes("MySQL Data Transfer" + "\r\n");
		dos.writeBytes("Source Host: localhost" + "\r\n");
		dos.writeBytes("Source Database: common_formats" + "\r\n");
		dos.writeBytes("Target Host: localhost" + "\r\n");
		dos.writeBytes("Target Database: common_formats" + "\r\n");
		SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
		dos.writeBytes("Date: " + df.format(new Date())+ "\r\n");
		dos.writeBytes("*/" + "\r\n");
		dos.writeBytes("\r\n");
		dos.writeBytes("SET FOREIGN_KEY_CHECKS=0;" + "\r\n");
		//-------------
		
		//---Insert
		dos.writeBytes("-- ----------------------------" + "\r\n");
		dos.writeBytes("-- Insert to Table " + "\r\n");
		dos.writeBytes("-- ----------------------------" + "\r\n");		
		
		String line = br.readLine();
		while((line = br.readLine()) != null) {
			//----report_general
			String[] thisline = line.split("\t");
			String uni_id = thisline[0];
			String rname = thisline[0];
			df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
			String initime = "01/01/2014";	
			String uptime = df.format(new Date());	
			String reporter = "Missouri Center for Patient Safety";
			String rstatus = "2";
			String formn = "fall";
			String herf_2 = thisline[2];
			String herf_7 = thisline[3];
			
			//---fingerprint
			String herf_2_f = CalFingerprint.getFPofOneQuestion(dbb, "herf_2", herf_2);
			String herf_7_f = CalFingerprint.getFPofOneQuestion(dbb, "herf_7", herf_7);			
			
			String sql = "INSERT INTO report_general (uni_id,initime,uptime,reporter,rstatus,formn,rname,des,link,herf_2,herf_2_f,severity_f,sir_7_f,sir_9_f,herf_7,herf_7_f) VALUES ('" + uni_id + "','" + initime + "','" + uptime + "','" + reporter + "','" + rstatus + "','" + formn + "','" + rname + "','NA','NA','" + herf_2 + "','" + herf_2_f + "','00000','000','00000000000000000000','" + herf_7 + "','" + herf_7_f + "');";
			dos.writeBytes(sql + "\r\n");
		    //-------------------			
		 	
		 	//----report_fall		 	
		 	ArrayList<String> qtitle = new ArrayList<String>();
		 	ArrayList<String> qcontent = new ArrayList<String>();
		 	
		 	//to calculate contributing factors_str
		 	HashMap<String, String> ans = new HashMap<String, String>();
		 	
		 	for(int i=4; i<thisline.length; i++) {
		 		if(!thisline[i].equals("NA")) {
		 			qtitle.add("fall_" + (i-3));
		 			qcontent.add(thisline[i]);
		 			ans.put("fall_" + (i-3), thisline[i]);
		 		}
		 	}
		 	
		 	ArrayList<String> cfs_str = CF_structured.getCFs(dbb, ans);
		 	String cf_str = "NA";
		 	if(cfs_str.size() > 0) {
		 		cf_str = cfs_str.get(0);
		 		for(int i=1; i<cfs_str.size(); i++)
		 			cf_str += "||" + cfs_str.get(i);
		 	}
		 	
		 	//get fingerprint of contributing factors_str
		 	String fingerprint_str = CF_structured.CFsToFingerprint(dbb, cf_str);
		 	
		 	sql = "INSERT INTO report_fall (uni_id";
		 	
		 	for(int i=0; i<qtitle.size(); i++)
		 		sql += "," + qtitle.get(i);
		 	
		 	for(int i=0; i<13; i++)
		 		sql += ",fall_" + (i+1) + "_f";
		 	
		 	sql += ",cf_str,fingerprint_str,cf_unstr,fingerprint_unstr) VALUES ('" + uni_id + "'";
		 	
		 	for(int i=0; i<qcontent.size(); i++)
		 		sql += ",'" + qcontent.get(i).replaceAll("\\'", "\\\\'") + "'";
		 	
		 	for(int i=0; i<13; i++) {
		 		String fingerprint = "";
		 		if(qtitle.contains("fall_" + (i+1)))
		 			fingerprint = CalFingerprint.getFPofOneQuestion(dbb, "fall_" + (i+1), qcontent.get(qtitle.indexOf("fall_" + (i+1))));		 			
		 		else
		 			fingerprint = CalFingerprint.getFPofOneQuestion(dbb, "fall_" + (i+1), null);
		 		
		 		sql += ",'" + fingerprint + "'";		 		
		 	}
		 	dos.writeBytes(sql + ",'" + cf_str + "','" + fingerprint_str + "','NA','NA');" + "\r\n");
		 	//----------------
		}
		
		br.close();
		dos.close();
		dbb.disConnect();
	}
}
