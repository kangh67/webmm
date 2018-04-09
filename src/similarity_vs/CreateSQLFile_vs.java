package similarity_vs;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import mysql.SQLCommands;

//generate SQL file to create 6 similarity matrix using vector space model measures
public class CreateSQLFile_vs {
	public static String SQLFile = "C:\\Users\\hkang1\\Google Drive\\AHRQ\\WEBMM\\sql_PSNet\\Matrix_vs.sql";
	
	//back up file for type 1
	public static String backupFile1 = "C:\\Users\\hkang1\\Google Drive\\AHRQ\\WEBMM\\PSNet\\Matrix_vs_1_backup.txt";
	
	//back up file for type 1
	public static String backupFile2 = "C:\\Users\\hkang1\\Google Drive\\AHRQ\\WEBMM\\PSNet\\Matrix_vs_2_backup.txt";
	
	//back up file for type 1
	public static String backupFile3 = "C:\\Users\\hkang1\\Google Drive\\AHRQ\\WEBMM\\PSNet\\Matrix_vs_3_backup.txt";
	
	//back up file for type 1
	public static String backupFile4 = "C:\\Users\\hkang1\\Google Drive\\AHRQ\\WEBMM\\PSNet\\Matrix_vs_4_backup.txt";
	
	//back up file for type 1
	public static String backupFile5 = "C:\\Users\\hkang1\\Google Drive\\AHRQ\\WEBMM\\PSNet\\Matrix_vs_5_backup.txt";
	
	//back up file for type 1
	public static String backupFile6 = "C:\\Users\\hkang1\\Google Drive\\AHRQ\\WEBMM\\PSNet\\Matrix_vs_6_backup.txt";
	
	public static void main(String[] args) throws Exception {
		SQLCommands sc = new SQLCommands();
		sc.getConn();
		
		//---for "Approach to Improving Safety"
		ArrayList<ArrayList<String>> matrix1 = getMatrix("1", sc);
		writeBackup(matrix1, backupFile1);
		//------------
		
		//---for "Clinical Area"
		ArrayList<ArrayList<String>> matrix2 = getMatrix("2", sc);
		writeBackup(matrix2, backupFile2);
		//------------
		
		//---for "Error Types"
		ArrayList<ArrayList<String>> matrix3 = getMatrix("3", sc);
		writeBackup(matrix3, backupFile3);
		//------------
		
		//---for "Safety Target"
		ArrayList<ArrayList<String>> matrix4 = getMatrix("4", sc);
		writeBackup(matrix4, backupFile4);
		//------------
		
		//---for "Setting of Care"
		ArrayList<ArrayList<String>> matrix5 = getMatrix("5", sc);
		writeBackup(matrix5, backupFile5);
		//------------
		
		//---for "Target Audience"
		ArrayList<ArrayList<String>> matrix6 = getMatrix("6", sc);
		writeBackup(matrix6, backupFile6);
		//------------
		
		
		
		//---write SQL File
		System.out.println("Now creating SQL file...");
		String tableName_1 = "matrix_vs_1";
		String tableName_2 = "matrix_vs_2";
		String tableName_3 = "matrix_vs_3";
		String tableName_4 = "matrix_vs_4";
		String tableName_5 = "matrix_vs_5";
		String tableName_6 = "matrix_vs_6";
		
		DataOutputStream dos = new DataOutputStream(new FileOutputStream(new File(SQLFile)));
		
		//Head
		dos.writeBytes("/*" + "\r\n");
		dos.writeBytes("MySQL Data Transfer" + "\r\n");
		dos.writeBytes("Source Host: localhost" + "\r\n");
		dos.writeBytes("Source Database: webmm" + "\r\n");
		dos.writeBytes("Target Host: localhost" + "\r\n");
		dos.writeBytes("Target Database: webmm" + "\r\n");
		SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
		dos.writeBytes("Date: " + df.format(new Date())+ "\r\n");
		dos.writeBytes("*/" + "\r\n");
		dos.writeBytes("\r\n");
		dos.writeBytes("SET FOREIGN_KEY_CHECKS=0;" + "\r\n");
		
		//body
		writeSQL(matrix1, tableName_1, dos);
		writeSQL(matrix2, tableName_2, dos);
		writeSQL(matrix3, tableName_3, dos);
		writeSQL(matrix4, tableName_4, dos);
		writeSQL(matrix5, tableName_5, dos);
		writeSQL(matrix6, tableName_6, dos);
		
		dos.close();
		//------------
		
		sc.disconnect();
		
		System.out.println("Done!");
	}
	
	public static ArrayList<ArrayList<String>> getMatrix(String type, SQLCommands sc) throws Exception {
		ArrayList<ArrayList<String>> matrix = new ArrayList<ArrayList<String>>();
		ArrayList<String> caseIDList = new ArrayList<String>();		
		
		ResultSet rsAll = sc.browse();
		
		//get a list with all caseIDs
		while(rsAll.next())
			caseIDList.add(rsAll.getString("caseID"));
		
		rsAll.close();
		
		int db_id = 0;	
		ResultSet rs1 = null;
		ResultSet rs2 = null;
		
		for(int i=0; i<caseIDList.size(); i++) {
			ArrayList<String> thisline = new ArrayList<String>();
			
			thisline.add(String.valueOf(++ db_id));
			thisline.add(caseIDList.get(i));
			
			System.out.println("Calculating similarity list for case " + caseIDList.get(i) + "(type " + type + ")...");
			
			for(int j=0; j<caseIDList.size(); j++) {
				rs1 = sc.getCaseByID(caseIDList.get(i));
				rs1.next();
				String finger1 = rs1.getString("f" + type);
				
				rs2 = sc.getCaseByID(caseIDList.get(j));				
				rs2.next();			
				String finger2 = rs2.getString("f" + type);
				
				rs1.close();
				rs2.close();
				
				double score = CalculateInside_vs.getScore_vs(finger1, finger2);
				thisline.add(String.valueOf(score));					
			}
			
			matrix.add(thisline);
		}	
		
		return matrix;
	}
	
	//write back up file for type [1-6] 
	public static void writeBackup(ArrayList<ArrayList<String>> matrix, String fileName) throws IOException {
		DataOutputStream dos = new DataOutputStream(new FileOutputStream(new File(fileName)));
			
		dos.writeBytes("db_id" + "\t" + "CASE ID");
		
		for(int i=0; i<matrix.size(); i++)
			dos.writeBytes("\t" + "N" + matrix.get(i).get(1));
		
		dos.writeBytes("\r\n");
		
		for(int i=0; i<matrix.size(); i++) {
			dos.writeBytes(matrix.get(i).get(0));
			
			for(int j=1; j<matrix.get(i).size(); j++) 
				dos.writeBytes("\t" + matrix.get(i).get(j));
			
			dos.writeBytes("\r\n");
		}
		
		dos.close();
	}
	
	public static void writeSQL(ArrayList<ArrayList<String>> matrix, String tableName, DataOutputStream dos) throws IOException {
		//---create table
		dos.writeBytes("-- ----------------------------" + "\r\n");
		dos.writeBytes("-- Table structure for " + tableName + "\r\n");
		dos.writeBytes("-- ----------------------------" + "\r\n");
		dos.writeBytes("CREATE TABLE `" + tableName + "` (" + "\r\n");
		dos.writeBytes("\t" + "`db_id` int(16) NOT NULL auto_increment," + "\r\n");
		dos.writeBytes("\t" + "`caseID` char(20) collate utf8_unicode_ci," + "\r\n");
		dos.writeBytes("\t" + "`scores` text collate utf8_unicode_ci," + "\r\n");		
		
		dos.writeBytes("\t" + "PRIMARY KEY  (`db_id`)" + "\r\n");
		dos.writeBytes(") ENGINE=InnoDB AUTO_INCREMENT=" + matrix.size() + " DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;" + "\r\n");
		dos.writeBytes("\r\n");
		//-----------
		
		//write content for table
		dos.writeBytes("-- ----------------------------" + "\r\n");
		dos.writeBytes("-- Records " + "\r\n");
		dos.writeBytes("-- ----------------------------" + "\r\n");
		
		for(int i=0; i<matrix.size(); i++) {
			dos.writeBytes("INSERT INTO `" + tableName + "` VALUES (");
			dos.writeBytes("'" + matrix.get(i).get(0) + "', '" + matrix.get(i).get(1) + "', '" + matrix.get(i).get(2));
			
			for(int j=3; j<matrix.get(i).size(); j++) 
				dos.writeBytes(";" + matrix.get(i).get(j));
			
			dos.writeBytes("');" + "\r\n");
		}
		//-------------
	}
}
