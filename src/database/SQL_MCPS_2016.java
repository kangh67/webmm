package database;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import contributing_factor.CF_structured;
import contributing_factor.CF_unstructured;
import report.CalFingerprint;
import user.DBBean;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

public class SQL_MCPS_2016 {
	/**
	 * transfer the fall data from Tina to another xls file which will be used to put into our database
	 */
	public static String rawFile_1 = "C:\\Users\\hkang1\\Google Drive\\Tina Data\\UT 2016 falls1.xls";
	public static int line_rf_1 = 650;	//including title line
	public static String rawFile_2 = "C:\\Users\\hkang1\\Google Drive\\Tina Data\\UT 2016 falls2.xls";
	public static int line_rf_2 = 1188;	//including title line
	public static String targetFile = "C:\\Users\\hkang1\\Google Drive\\AHRQ\\WEBMM\\raw\\MCPS_fall_2016.txt";
	public static String SQLFile = "C:\\Users\\hkang1\\Google Drive\\AHRQ\\WEBMM\\sql\\MCPS_fall_2016.sql";
	
	public static void main(String[] args) throws Exception {
		ArrayList<ArrayList<String>> info = readFile(rawFile_1, line_rf_1);
		ArrayList<ArrayList<String>> info_ = readFile(rawFile_2, line_rf_2);
		info.addAll(info_.subList(1, info_.size()));
		writeToFile(info);		
		writeSQL();
	}
	
	public static ArrayList<ArrayList<String>> readFile(String file, int line) throws BiffException, IOException{
		ArrayList<ArrayList<String>> info = new ArrayList<ArrayList<String>>();
		
		Workbook book = Workbook.getWorkbook(new File(file));
		Sheet sheet = book.getSheet(0);
		
		//title line
		ArrayList<String> title = new ArrayList<String>();
		title.add("uni_id");	//0
		title.add("title");	//1
		title.add("date");	//2
		title.add("des");	//3
		title.add("herf_2");	//4
		title.add("severity");	//5
		title.add("sir_7");	//6
		title.add("sir_9");	//7
		title.add("herf_7");	//8
		title.add("fall_1");	//9
		title.add("fall_2");	//10
		title.add("fall_3");	//11
		title.add("fall_4");	//12
		title.add("fall_5");	//13
		title.add("fall_6");	//14
		title.add("fall_7");	//15
		title.add("fall_8");	//16
		title.add("fall_9");	//17
		title.add("fall_10");	//18
		title.add("fall_11");	//19
		title.add("fall_12");	//20
		title.add("fall_13");	//21
		
		info.add(title);
		
		//info
		for(int i=1; i<line; i++) {
			ArrayList<String> thisInfo = new ArrayList<String>();
			
			thisInfo.add("MCPS16_" + sheet.getCell(0,i).getContents().trim());	//0: uni_id	A
			thisInfo.add("MCPS16_" + sheet.getCell(0,i).getContents().trim());	//1: title = uni_id
			thisInfo.add(sheet.getCell(1,i).getContents().trim());	//2: date	B
			String des = sheet.getCell(4,i).getContents().trim();
			des += sheet.getCell(28,i).getContents().trim();
			thisInfo.add(des);	//3: des	E+AC
			
			//4: herf_2	D
			String herf2 = sheet.getCell(3,i).getContents().trim();
			if(herf2.contains("Incident:"))
				thisInfo.add("0");
			else if(herf2.contains("Near Miss:"))
				thisInfo.add("1");
			else if(herf2.contains("Unsafe Condition:"))
				thisInfo.add("2");
			else
				thisInfo.add("NA");
			
			//5: severity	C
			String severity = sheet.getCell(2,i).getContents().trim();
			if(severity.equalsIgnoreCase("No harm"))
				thisInfo.add("0");
			else if(severity.equalsIgnoreCase("Mild harm"))
				thisInfo.add("1");
			else if(severity.equalsIgnoreCase("Moderate harm"))
				thisInfo.add("2");
			else if(severity.equalsIgnoreCase("Severe harm"))
				thisInfo.add("3");
			else if(severity.equalsIgnoreCase("Unknown"))
				thisInfo.add("4");
			else
				thisInfo.add("NA");
			
			//6: sir_7	AD
			String sir7 = sheet.getCell(29,i).getContents().trim();
			if(sir7.equalsIgnoreCase("Yes"))
				thisInfo.add("0");
			else if(sir7.equalsIgnoreCase("No"))
				thisInfo.add("1");
			else if(sir7.equalsIgnoreCase("Unknown"))
				thisInfo.add("2");
			else
				thisInfo.add("NA");
			
			//6: sir_9	multi AE+AF
			String sir9 = sheet.getCell(30,i).getContents().trim();
			String data = "";
			if(sir9.contains("Culture of safety"))
				data += "||0";
			if(sir9.contains("Physical surroundings"))
				data += "||1";
			if(sir9.contains("Competence"))
				data += "||2";
			if(sir9.contains("Training"))
				data += "||3";
			if(sir9.contains("Clinical supervision"))
				data += "||4";
			if(sir9.contains("Managerial supervision"))
				data += "||5";
			if(sir9.contains("Presence of policies"))
				data += "||6";
			if(sir9.contains("Clarity of policies"))
				data += "||7";
			if(sir9.contains("Availability"))
				data += "||8";
			if(sir9.contains("Accuracy"))
				data += "||9";
			if(sir9.contains("Legibility"))
				data += "||10";
			if(sir9.contains("Supervisor to staff"))
				data += "||11";
			if(sir9.contains("Among staff or team members"))
				data += "||12";
			if(sir9.contains("Staff to patient"))
				data += "||13";
			if(sir9.contains("Fatigue"))
				data += "||14";
			if(sir9.contains("Stress"))
				data += "||15";
			if(sir9.contains("Inattention"))
				data += "||16";
			if(sir9.contains("Cognitive factors"))
				data += "||17";
			if(sir9.contains("Health issues"))
				data += "||18";
			if(!sheet.getCell(31,i).getContents().trim().equalsIgnoreCase("Not Answered"))
				data += "||19$$" + sheet.getCell(31,i).getContents().trim();
			
			if(!data.equals(""))			
				thisInfo.add(data.substring(2));	//delete the first ||
			else
				thisInfo.add("NA");
			
			//8: herf_7
			thisInfo.add("2");
			
			//9: fall_1	F
			String fall_1 = sheet.getCell(5,i).getContents().trim();
			if(fall_1.equalsIgnoreCase("Unassisted"))
				thisInfo.add("0");
			else if(fall_1.equalsIgnoreCase("Assisted"))
				thisInfo.add("1");
			else if(fall_1.equalsIgnoreCase("Unknown"))
				thisInfo.add("2");
			else
				thisInfo.add("NA");
			
			//10: fall_2	G
			String fall_2 = sheet.getCell(6,i).getContents().trim();
			if(fall_2.equalsIgnoreCase("Yes"))
				thisInfo.add("0");
			else if(fall_2.equalsIgnoreCase("No"))
				thisInfo.add("1");
			else if(fall_2.equalsIgnoreCase("Unknown"))
				thisInfo.add("2");
			else
				thisInfo.add("NA");
		
			//11: fall_3	H
			String fall_3 = sheet.getCell(7,i).getContents().trim();
			if(fall_2.equalsIgnoreCase("Yes")) {
				if(fall_3.equalsIgnoreCase("Staff"))
					thisInfo.add("0");
				else if(fall_3.contains("Visitor"))
					thisInfo.add("1");
				else
					thisInfo.add("NA");					
			}else
				thisInfo.add("NA");	
			
			//12: fall_4	I
			String fall_4 = sheet.getCell(8,i).getContents().trim();
			if(fall_4.equalsIgnoreCase("Yes"))
				thisInfo.add("0");
			else if(fall_4.equalsIgnoreCase("No"))
				thisInfo.add("1");
			else if(fall_4.equalsIgnoreCase("Unknown"))
				thisInfo.add("2");
			else
				thisInfo.add("NA");
			
			//13: fall_5	J+K
			String fall_5 = sheet.getCell(9,i).getContents().trim();
			if(fall_4.equalsIgnoreCase("Yes")) {
				if(fall_5.contains("Dislocation"))
					thisInfo.add("0");
				else if(fall_5.contains("Fracture"))
					thisInfo.add("1");
				else if(fall_5.contains("Intracranial"))
					thisInfo.add("2");
				else if(fall_5.contains("Laceration"))
					thisInfo.add("3");
				else if(fall_5.contains("Skin tear"))
					thisInfo.add("4");
				else if(!sheet.getCell(10,i).getContents().trim().equalsIgnoreCase("Not Answered"))
					thisInfo.add("5$$" + sheet.getCell(10,i).getContents().trim());
				else
					thisInfo.add("NA");					
			}else
				thisInfo.add("NA");
			
			//14: fall_6	L+M
			String fall_6 = sheet.getCell(11,i).getContents().trim();
			if(fall_6.contains("Ambulating without"))
				thisInfo.add("0");
			else if(fall_6.contains("Ambulating with assistance"))
				thisInfo.add("1");
			else if(fall_6.contains("Changing position"))
				thisInfo.add("2");
			else if(fall_6.contains("Dressing"))
				thisInfo.add("3");
			else if(fall_6.contains("Navigating"))
				thisInfo.add("4");
			else if(fall_6.contains("Reaching"))
				thisInfo.add("5");
			else if(fall_6.contains("Showering"))
				thisInfo.add("6");
			else if(fall_6.contains("Toileting"))
				thisInfo.add("7");
			else if(fall_6.contains("Transferring to or from bed"))
				thisInfo.add("8");
			else if(fall_6.contains("Undergoing a diagnostic"))
				thisInfo.add("9");
			else if(fall_6.contains("Unknown"))
				thisInfo.add("10");
			else if(!sheet.getCell(12,i).getContents().trim().equalsIgnoreCase("Not Answered"))
				thisInfo.add("11$$" + sheet.getCell(12,i).getContents().trim());
			else
				thisInfo.add("10");
			
			//15: fall_7 (column=N, i=13)
			String fall_7 = sheet.getCell(13,i).getContents().trim();
			if(fall_7.equalsIgnoreCase("Yes"))
				thisInfo.add("0");
			else if(fall_7.equalsIgnoreCase("No"))
				thisInfo.add("1");
			else if(fall_7.equalsIgnoreCase("Unknown"))
				thisInfo.add("2");
			else
				thisInfo.add("NA");
			
			//16: fall_8 (column=O, i=14)
			String fall_8 = sheet.getCell(14,i).getContents().trim();
			if(fall_7.equalsIgnoreCase("Yes")) {
				if(fall_8.equalsIgnoreCase("Yes"))
					thisInfo.add("0");
				else if(fall_8.equalsIgnoreCase("No"))
					thisInfo.add("1");
				else if(fall_8.equalsIgnoreCase("Unknown"))
					thisInfo.add("2");
				else
					thisInfo.add("NA");			
			}else
				thisInfo.add("NA");
			
			//17: fall_9 multi (column=P,Q, i=15,16)
			String fall_9 = sheet.getCell(15,i).getContents().trim();
			data = "";
			if(fall_9.contains("History of previous fall"))
				data = data + "||0";
			if(fall_9.contains("Prosthesis or specialty"))
				data = data + "||1";
			if(fall_9.contains("Sensory impairment"))
				data = data + "||2";
			if(!sheet.getCell(16,i).getContents().trim().equalsIgnoreCase("Not Answered"))
				data = data + "||5$$" + sheet.getCell(16,i).getContents().trim();
			
			if(!data.equals(""))			
				thisInfo.add(data.substring(2));	//delete the first ||			
			else if(fall_9.contains("None"))
				thisInfo.add("3");
			else
				thisInfo.add("4");
			
			//18: fall_10 multi (column=R,S i=17,18)
			String fall_10 = sheet.getCell(17,i).getContents().trim();
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
			if(!sheet.getCell(18,i).getContents().trim().equalsIgnoreCase("Not Answered"))
				data = data + "||17$$" + sheet.getCell(18,i).getContents().trim();
			
			if(!data.equals(""))			
				thisInfo.add(data.substring(2));	//delete the first ||			
			else if(fall_10.contains("None"))
				thisInfo.add("15");
			else
				thisInfo.add("16");
			
			//19: fall_11 (column=T, i=19)
			String fall_11 = sheet.getCell(19,i).getContents().trim();
			if(fall_11.equalsIgnoreCase("Yes"))
				thisInfo.add("0");
			else if(fall_11.equalsIgnoreCase("No"))
				thisInfo.add("1");
			else if(fall_11.equalsIgnoreCase("Unknown"))
				thisInfo.add("2");
			else
				thisInfo.add("NA");	
			
			//20: fall_12 (column=U, i=20)
			String fall_12 = sheet.getCell(20,i).getContents().trim();
			if(fall_11.equalsIgnoreCase("Yes")) {
				if(fall_12.equalsIgnoreCase("Yes"))
					thisInfo.add("0");
				else if(fall_12.equalsIgnoreCase("No"))
					thisInfo.add("1");
				else if(fall_12.equalsIgnoreCase("Unknown"))
					thisInfo.add("2");
				else
					thisInfo.add("NA");		
			}else
				thisInfo.add("NA");
			
			//21: fall_13 (column=AB, i=27)
			String fall_13 = sheet.getCell(27,i).getContents().trim();
			if(fall_13.equalsIgnoreCase("Yes"))
				thisInfo.add("0");
			else if(fall_13.equalsIgnoreCase("No"))
				thisInfo.add("1");
			else if(fall_13.equalsIgnoreCase("Unknown"))
				thisInfo.add("2");
			else
				thisInfo.add("NA");	
			
			
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
	
	public static void writeSQL() throws Exception {
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
			String initime = thisline[2];
			String uptime = df.format(new Date());	
			String reporter = "Missouri Center for Patient Safety";
			String rstatus = "2";
			String formn = "fall";
			String des = thisline[3];
			String herf_2 = thisline[4];
			String severity = thisline[5];
			String sir_7 = thisline[6];
			String sir_9 = thisline[7];
			String herf_7 = thisline[8];
			
			//---fingerprint			
			String herf_2_f = CalFingerprint.getFPofOneQuestion(dbb, "herf_2", herf_2);
			String severity_f = CalFingerprint.getFPofOneQuestion(dbb, "severity", severity);
			String sir_7_f = CalFingerprint.getFPofOneQuestion(dbb, "sir_7", sir_7);
			String sir_9_f = CalFingerprint.getFPofOneQuestion(dbb, "sir_9", sir_9);
			String herf_7_f = CalFingerprint.getFPofOneQuestion(dbb, "herf_7", herf_7);			
			
			String sql = "INSERT INTO report_general (uni_id,initime,uptime,reporter,rstatus,formn,rname,des,link,";
			
			if(!herf_2.equals("NA"))
				sql += "herf_2,";
			sql += "herf_2_f,";
			
			if(!severity.equals("NA"))
				sql += "severity,";
			sql += "severity_f,";
			
			if(!sir_7.equals("NA"))
				sql += "sir_7,";
			sql += "sir_7_f,";
			
			if(!sir_9.equals("NA"))
				sql += "sir_9,";
			
			sql += "sir_9_f,herf_7,herf_7_f) VALUES ('" + uni_id + "','" + initime + "','" + uptime + "','" + reporter + "','" + rstatus + "','" + formn + "','" + rname + "','" + CF_unstructured.polishText(des) + "','NA','";
			
			if(!herf_2.equals("NA"))
				sql += herf_2 + "','";
			sql += herf_2_f + "','";
			
			if(!severity.equals("NA"))
				sql += severity + "','";
			sql += severity_f + "','";
			
			if(!sir_7.equals("NA"))
				sql += sir_7 + "','";
			sql += sir_7_f + "','";
			
			if(!sir_9.equals("NA"))
				sql += sir_9 + "','";
			
			sql += sir_9_f + "','" + herf_7 + "','" + herf_7_f + "');";
			
			dos.writeBytes(sql + "\r\n");
		    //-------------------			
		 	
		 	//----report_fall		 	
		 	ArrayList<String> qtitle = new ArrayList<String>();
		 	ArrayList<String> qcontent = new ArrayList<String>();
		 	
		 	//to calculate contributing factors_str
		 	HashMap<String, String> ans = new HashMap<String, String>();
		 	
		 	//answers of fall_1~fall_13
		 	for(int i=9; i<thisline.length; i++) {
		 		if(!thisline[i].equals("NA")) {
		 			qtitle.add("fall_" + (i-8));
		 			qcontent.add(thisline[i]);
		 			ans.put("fall_" + (i-8), thisline[i]);
		 		}
		 	}
		 	//answer of sir_7
		 	if(!thisline[6].equals("NA"))
		 		ans.put("sir_7", thisline[6]);
		 	//answer of sir_9
		 	if(!thisline[7].equals("NA"))
		 		ans.put("sir_9", thisline[7]);
		 	
		 	ArrayList<String> cfs_str = CF_structured.getCFs(dbb, ans);
		 	String cf_str = "NA";
		 	if(cfs_str.size() > 0) {
		 		cf_str = cfs_str.get(0);
		 		for(int i=1; i<cfs_str.size(); i++)
		 			cf_str += "||" + cfs_str.get(i);
		 	}
		 	
		 	//get fingerprint of contributing factors_str
		 	String fingerprint_str = CF_structured.CFsToFingerprint(dbb, cf_str);
		 	
		 	//to calculate contributing factors_unstr
		 	String cf_unstr = CF_unstructured.getCFs_oneLineString(dbb, des);
		 	
		 	//get fingerprint of contributing factors_unstr
		 	String fingerprint_unstr = CF_structured.CFsToFingerprint(dbb, cf_unstr);
		 	
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
		 	dos.writeBytes(sql + ",'" + cf_str + "','" + fingerprint_str + "','" + cf_unstr + "','" + fingerprint_unstr + "');" + "\r\n");
		 	//----------------
		}
		
		br.close();
		dos.close();
		dbb.disConnect();
	}
}
