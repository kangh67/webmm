//create SQL file for the table of users
package database;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

public class SQL_users {
	//source xls file
	public static String sourceFile = "C:\\Users\\hkang1\\Google Drive\\AHRQ\\WEBMM\\raw\\users.xls";
	
	//SQL File address	
	public static String SQLFile = "C:\\Users\\hkang1\\Google Drive\\AHRQ\\WEBMM\\sql\\users.sql";
	
	public static void main(String[] args) throws IOException, BiffException {
		//get featureNmaes from the fist row
		ArrayList<String> featureNames = getFeatureNames();
		
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
	
	public static void writeSQL(ArrayList<String> featureNames) throws IOException, BiffException {
		DataOutputStream dos = new DataOutputStream(new FileOutputStream(new File(SQLFile)));
		Workbook book = Workbook.getWorkbook(new File(sourceFile));
		Sheet sheet = book.getSheet(0);
		String tableName = "users";
		
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
		dos.writeBytes(") ENGINE=InnoDB AUTO_INCREMENT=" + (sheet.getRows() - 1) + " DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;" + "\r\n");
		dos.writeBytes("\r\n");
		//-----------
		
		//write content for table
		dos.writeBytes("-- ----------------------------" + "\r\n");
		dos.writeBytes("-- Records " + "\r\n");
		dos.writeBytes("-- ----------------------------" + "\r\n");
		
		for(int i=1; i<sheet.getRows(); i++) {
			if(sheet.getCell(1, i).getContents().trim().length() >= 1) {
				dos.writeBytes("INSERT INTO `" + tableName + "` VALUES (");
				dos.writeBytes("'" + i + "'");
			
				for(int j=0; j<sheet.getColumns(); j++) 
					dos.writeBytes(", '" + isNA(sheet.getCell(j, i).getContents().trim()) + "'");
			
				dos.writeBytes(");" + "\r\n");
			}
		}
		//-------------
		
		book.close();
		dos.close();
	}
	
	public static String isNA(String text) {
		if(text.length() >= 1)
			return text.replaceAll("'", "''");
		else
			return "NA";
	}
}
