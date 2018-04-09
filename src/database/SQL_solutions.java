//create SQL file for the table of solutions
package database;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import contributing_factor.CF_unstructured;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

public class SQL_solutions {
	//assign a type, e.g., fall, pu
	public static String type = "pu";
	
	//source xls file
	public static String sourceFile = "C:\\Users\\hkang1\\Google Drive\\AHRQ\\solutions\\Solutions_" + type + "_forSQL.xls";
		
	//SQL File address	
	public static String SQLFile = "C:\\Users\\hkang1\\Google Drive\\AHRQ\\WEBMM\\sql\\solutions_" + type + ".sql";
	
	public static void main(String[] args) throws IOException, BiffException {
		//write SQL File
		writeSQL();	
	}
	
	public static void writeSQL() throws IOException, BiffException {
		DataOutputStream dos = new DataOutputStream(new FileOutputStream(new File(SQLFile)));
		Workbook book = Workbook.getWorkbook(new File(sourceFile));
		Sheet sheet = book.getSheet(0);
		String tableName = "solutions_" + type;
		
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
		
		dos.writeBytes("\t" + "`SID` text collate utf8_unicode_ci," + "\r\n");
		dos.writeBytes("\t" + "`Solution` text collate utf8_unicode_ci," + "\r\n");	
		dos.writeBytes("\t" + "`Contributing_factor` text collate utf8_unicode_ci," + "\r\n");	
		dos.writeBytes("\t" + "`General_or_specific` text collate utf8_unicode_ci," + "\r\n");
		dos.writeBytes("\t" + "`Criteria` text collate utf8_unicode_ci," + "\r\n");	
		dos.writeBytes("\t" + "`Logic` text collate utf8_unicode_ci," + "\r\n");
		dos.writeBytes("\t" + "`Resource` text collate utf8_unicode_ci," + "\r\n");	
		dos.writeBytes("\t" + "`Solution_type` text collate utf8_unicode_ci," + "\r\n");
		dos.writeBytes("\t" + "`CFs` text collate utf8_unicode_ci," + "\r\n");
		
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
			return CF_unstructured.polishText(text);
		else
			return "NA";
	}
}
