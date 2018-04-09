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

public class SQL_report_detail {	
	//edit here
	public static String form = "fall";
	
	//SQL File address	
	public static String SQLFile = "C:\\Users\\hkang1\\Google Drive\\AHRQ\\WEBMM\\sql\\report_" + form + ".sql";
	
	public static void main(String[] args) throws IOException, SQLException {		
		
		//add questions
		ArrayList<String> featureNames = addQuestions();		
		
		//write SQL File
		writeSQL(featureNames);		
	}	
	
	public static ArrayList<String> addQuestions() throws SQLException {
		DBBean mydbb = new DBBean();
		String sql = "select * from questions where qid like '%" + form + "%'";
		ResultSet rs = mydbb.query(sql);
		
		ArrayList<String> featureName = new ArrayList<String>();
		featureName.add("uni_id");
		
		while(rs.next()) {
			featureName.add(rs.getString("qid"));
			featureName.add(rs.getString("qid") + "_f");
		}
		
		rs.close();
		mydbb.disConnect();
		
		return featureName;
	}
	
	public static void writeSQL(ArrayList<String> featureNames) throws IOException {
		DataOutputStream dos = new DataOutputStream(new FileOutputStream(new File(SQLFile)));		
		String tableName = "report_" + form;
		
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
		dos.writeBytes("\t" + "`cf_str` text collate utf8_unicode_ci," + "\r\n");
		dos.writeBytes("\t" + "`fingerprint_str` text collate utf8_unicode_ci," + "\r\n");
		dos.writeBytes("\t" + "`cf_unstr` text collate utf8_unicode_ci," + "\r\n");
		dos.writeBytes("\t" + "`fingerprint_unstr` text collate utf8_unicode_ci," + "\r\n");
		
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
