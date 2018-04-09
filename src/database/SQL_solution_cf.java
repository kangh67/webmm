//create SQL file for the table of solutions
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
import java.util.HashMap;

import contributing_factor.CF_unstructured;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import user.DBBean;

public class SQL_solution_cf {
	//assign a type, e.g., fall, pu
	public static String type = "fall";
	
	//source xls file
	public static String sourceFile = "C:\\Users\\hkang1\\Google Drive\\AHRQ\\solutions\\Solutions_" + type + "_forSQL.xls";
		
	//SQL File address	
	public static String SQLFile = "C:\\Users\\hkang1\\Google Drive\\AHRQ\\WEBMM\\sql\\solutions_" + type + "_cf.sql";
	
	public static void main(String[] args) throws IOException, BiffException, SQLException {
		//get HashMap<cf, ArrayList<sid>>
		HashMap<String, ArrayList<String>> cf_sid = getSolutionsByCF();
		
		//get sid for every db_id in contributing_factors_list, if no sid for certain db_id, "NA" is given;
		ArrayList<String[]> dbID_sid = matchToAllCFs(cf_sid);		
		
		//write SQL File
		writeSQL(dbID_sid);	
	}
	
	public static HashMap<String, ArrayList<String>> getSolutionsByCF() throws IOException, BiffException {
		HashMap<String, ArrayList<String>> res = new HashMap<String, ArrayList<String>>();
		
		DataOutputStream dos = new DataOutputStream(new FileOutputStream(new File(SQLFile)));
		Workbook book = Workbook.getWorkbook(new File(sourceFile));
		Sheet sheet = book.getSheet(0);
		
		for(int i=1; i<sheet.getRows(); i++) {
			String[] cfs = sheet.getCell(8, i).getContents().trim().split(";");
			String sid = sheet.getCell(0, i).getContents().trim();
			
			for(int j=0; j<cfs.length; j++) {
				if(cfs[j].length() < 1)
					continue;
				if(res.containsKey(cfs[j]))
					res.get(cfs[j]).add(sid);
				else {
					ArrayList<String> thiscf = new ArrayList<String>();
					thiscf.add(sid);
					res.put(cfs[j], thiscf);
				}
			}
		}
		
		book.close();
		dos.close();
		
		return res;
	}
	
	public static ArrayList<String[]> matchToAllCFs(HashMap<String, ArrayList<String>> cf_sid) throws SQLException {	
		ArrayList<String[]> res = new ArrayList<String[]>();
		DBBean dbb = new DBBean();
		String sql = "select * from contributing_factors_list";
		ResultSet rs = dbb.query(sql);
		
		while(rs.next()) {
			String thisSids = "";
			for(String cf : cf_sid.keySet()) {
				if(rs.getString("CFID").equals(cf) || rs.getString("CFID").startsWith(cf + "_")) {
					for(int i=0; i<cf_sid.get(cf).size(); i++)
						thisSids += ";" + cf_sid.get(cf).get(i);
					break;
				}
			}
			
			String[] s = new String[2];
			s[0] = rs.getString("db_id");
			
			if(thisSids.length() > 0)				
				s[1] = thisSids.substring(1);
			else
				s[1] = "NA";
			
			res.add(s);
		}
		
		rs.close();
		dbb.disConnect();	
		
		return res;
	}
	
	public static void writeSQL(ArrayList<String[]> allcf_sid) throws IOException, BiffException {
		DataOutputStream dos = new DataOutputStream(new FileOutputStream(new File(SQLFile)));
		String colName = "Solutions_" + type;
		
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
		
		//Update table
		dos.writeBytes("-- ----------------------------" + "\r\n");
		dos.writeBytes("-- Update Column " + colName + "\r\n");
		dos.writeBytes("-- ----------------------------" + "\r\n");
		
		for(int i=0; i<allcf_sid.size(); i++) {
			dos.writeBytes("update contributing_factors_list set " + colName + "='" + allcf_sid.get(i)[1] + "' where db_id='" + allcf_sid.get(i)[0] + "';" + "\r\n");
		}
		//-------------
		
		dos.close();
	}
	
	public static String isNA(String text) {
		if(text.length() >= 1)
			return CF_unstructured.polishText(text);
		else
			return "NA";
	}
}
