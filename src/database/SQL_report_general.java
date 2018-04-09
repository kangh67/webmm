//create SQL file for the table of questions
package database;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import user.DBBean;

import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

public class SQL_report_general {
	//source xls file
	public static String sourceFile = "C:\\Users\\hkang1\\Google Drive\\AHRQ\\WEBMM\\raw\\report_general.xls";
	
	//SQL File address	
	public static String SQLFile = "C:\\Users\\hkang1\\Google Drive\\AHRQ\\WEBMM\\sql\\report_general.sql";
	
	public static void main(String[] args) throws IOException, BiffException, SQLException {
		//get featureNmaes from the fist row
		ArrayList<String> featureNames = getFeatureNames();
		
		//add general questions
		featureNames = addGeneralQuestions(featureNames);		
		
		//write SQL File
		writeSQL(featureNames);		
	}
	
	public static ArrayList<String> getFeatureNames() throws BiffException, IOException {
		ArrayList<String> featureNames = new ArrayList<String>();
		Workbook book = Workbook.getWorkbook(new File(sourceFile));
		Sheet sheet = book.getSheet(0);
		
		for(int i=0; i<sheet.getColumns(); i++) 
			if(sheet.getCell(i, 0).getContents().trim().length() > 1)
				featureNames.add(sheet.getCell(i,0).getContents().trim());		
		
		book.close();
		return featureNames;
	}
	
	public static ArrayList<String> addGeneralQuestions(ArrayList<String> featureName) throws SQLException {
		DBBean mydbb = new DBBean();
		String sql = "select * from questions where qid like '%herf%' OR qid like '%pif%' OR qid like '%sir%' OR qid='severity'";
		ResultSet rs = mydbb.query(sql);
		
		while(rs.next()) {
			//System.out.println(rs.getString("qid"));
			featureName.add(rs.getString("qid"));
			featureName.add(rs.getString("qid") + "_f");
		}
		
		rs.close();
		mydbb.disConnect();
		
		return featureName;
	}
	
	public static void writeSQL(ArrayList<String> featureNames) throws IOException, BiffException {
		DataOutputStream dos = new DataOutputStream(new FileOutputStream(new File(SQLFile)));		
		String tableName = "report_general";
		
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
		
		//---create table
		dos.writeBytes("-- ----------------------------" + "\r\n");
		dos.writeBytes("-- Table structure for " + tableName + "\r\n");
		dos.writeBytes("-- ----------------------------" + "\r\n");
		dos.writeBytes("CREATE TABLE `" + tableName + "` (" + "\r\n");
		dos.writeBytes("\t" + "`db_id` int(255) NOT NULL auto_increment," + "\r\n");
		
		for(int i=0; i<featureNames.size(); i++) 
			dos.writeBytes("\t" + "`" + featureNames.get(i) + "` text collate utf8_unicode_ci," + "\r\n");		
		
		dos.writeBytes("\t" + "PRIMARY KEY  (`db_id`)" + "\r\n");
		dos.writeBytes(") ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;" + "\r\n");
		dos.writeBytes("\r\n");
		//-----------			
		
		dos.close();
	}
	
	public static String isNA(String text) {
		if(text.length() >= 1)
			return text.replaceAll("'", "''");
		else
			return "NA";
	}
}
