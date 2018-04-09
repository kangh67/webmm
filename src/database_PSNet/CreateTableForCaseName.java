//Read information from case name.xls, then write to database directly
package database_PSNet;

import java.io.File;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.util.ArrayList;

import mysql.SQLCommands;

import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

public class CreateTableForCaseName {
	//source
	public static String fileName = "C:\\Users\\hkang1\\Google Drive\\AHRQ\\WEBMM\\PSNet\\case name.xls";
	
	public static void main(String[] args) throws Exception {
		ArrayList<String[]> caseInf = readXLS(fileName);
		writeToDB(caseInf);		
	}
	
	public static ArrayList<String[]> readXLS(String fileName) throws BiffException, IOException {
		ArrayList<String[]> caseInf = new ArrayList<String[]>();
		Workbook book = Workbook.getWorkbook(new File(fileName));
		Sheet sheet = book.getSheet(0);
		
		for(int i=1; i<sheet.getRows(); i++) {
			String[] thisline = new String[4];
			
			thisline[0] = sheet.getCell(0,i).getContents().trim();//case ID
			thisline[1] = sheet.getCell(1,i).getContents().trim();//case name
			thisline[2] = sheet.getCell(2,i).getContents().trim();//case author
			thisline[3] = sheet.getCell(3,i).getContents().trim();//case date
			
			caseInf.add(thisline);
		}
		
		book.close();
		return caseInf;
	}
	
	public static void writeToDB(ArrayList<String[]> caseInf) throws Exception {
		SQLCommands sc = new SQLCommands();		
		sc.getConn();
		
		int i = 0;		
		for(i=0; i<caseInf.size(); i++) {			
			String caseID = caseInf.get(i)[0];
			String name = caseInf.get(i)[1];
			String author = caseInf.get(i)[2];			
			String date = caseInf.get(i)[3];			
			
			String sql = "insert into caseinfo(caseID,name,author,uploadDate)values(?,?,?,?)";
		    PreparedStatement ps = sc.conn.prepareStatement(sql);
		    
		    ps.setString(1, caseID);
		    ps.setString(2, name);
		    ps.setString(3, author);
		 	ps.setString(4, date);		 	
		 	
		 	ps.executeUpdate();
		 	ps.close();
		 	System.out.println(caseID + "\t" + "uploading ...");
		}
		
		sc.disconnect();
		
		System.out.println("Finished! " + i + "\t" + "cases totally");
	}
}
