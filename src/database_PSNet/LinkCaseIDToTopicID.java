/**
 * For every Topic ID in the file "PSNet raw taxonomy_adLevels.xls", match caseIDs on it
 * 6 subSheets totally
 */
package database_PSNet;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import url.CatchInf;

public class LinkCaseIDToTopicID {
	public static String input = "C:\\Users\\hkang1\\Google Drive\\AHRQ\\WEBMM\\PSNet\\PSNet raw taxonomy_adLevels.xls";
	
	public static String output1 = "C:\\Users\\hkang1\\Google Drive\\AHRQ\\WEBMM\\PSNet\\1_Approach to Improving Safety.txt";
	public static String output2 = "C:\\Users\\hkang1\\Google Drive\\AHRQ\\WEBMM\\PSNet\\2_Clinical Area.txt";
	public static String output3 = "C:\\Users\\hkang1\\Google Drive\\AHRQ\\WEBMM\\PSNet\\3_Error Types.txt";
	public static String output4 = "C:\\Users\\hkang1\\Google Drive\\AHRQ\\WEBMM\\PSNet\\4_Safety Target.txt";
	public static String output5 = "C:\\Users\\hkang1\\Google Drive\\AHRQ\\WEBMM\\PSNet\\5_Setting of Care.txt";
	public static String output6 = "C:\\Users\\hkang1\\Google Drive\\AHRQ\\WEBMM\\PSNet\\6_Target Audience.txt";
	
	public static void main(String[] args) throws IOException, BiffException {
		findCaseIDs(1);	//Approach to Improving Safety
		findCaseIDs(2);	//Approach to Improving Safety
		findCaseIDs(3);	//Approach to Improving Safety
		findCaseIDs(4);	//Approach to Improving Safety
		findCaseIDs(5);	//Approach to Improving Safety
		findCaseIDs(6);	//Approach to Improving Safety
	}
	
	public static void findCaseIDs(Integer sheetIndex) throws IOException, BiffException {
		/*
		 * Determine output file
		 */
		DataOutputStream dos = null;
		if(sheetIndex == 1)
			dos = new DataOutputStream(new FileOutputStream(new File(output1)));
		else if(sheetIndex == 2)
			dos = new DataOutputStream(new FileOutputStream(new File(output2)));
		else if(sheetIndex == 3)
			dos = new DataOutputStream(new FileOutputStream(new File(output3)));
		else if(sheetIndex == 4)
			dos = new DataOutputStream(new FileOutputStream(new File(output4)));
		else if(sheetIndex == 5)
			dos = new DataOutputStream(new FileOutputStream(new File(output5)));
		else if(sheetIndex == 6)
			dos = new DataOutputStream(new FileOutputStream(new File(output6)));
		
		String url = "";		
		String content = "";
		
		Workbook book = Workbook.getWorkbook(new File(input));
		Sheet sheet = book.getSheet(sheetIndex - 1);
		
		System.out.println(sheetIndex + " Linking caseIDs to the " + (sheet.getRows() - 1) + " Topics of " + sheet.getName() + " ...");
		
		/*
		 * Row by row
		 */
		for(int i=1; i<sheet.getRows(); i++) {
			/*
			 * Figure out how many case IDs totally
			 */
			url = "https://psnet.ahrq.gov/search?f_topicIDs=" + sheet.getCell(2,i).getContents().trim() + "&f_resource_typeID=7";
			String caseIDs = "";
			
			try {
				content = CatchInf.getHtml(url);
			}catch(Exception e) {
				System.out.println("Exception when trying to get url information from " + url);;
			}
			
			Pattern p = Pattern.compile("([0-9]+) result");
			Matcher m;
			String hits = "0";
			
			m = p.matcher(content);
			
			/*
			 * If no caseID, hits = 0; else, change the pageSize to the caseID number, and collect all the caseIDs
			 */
			if (m.find() && !m.group(1).equals("0")){
				hits = (m.group(1));			
				url += "&pageSize=" + hits;
				
				try {
					content = CatchInf.getHtml(url);
				}catch(Exception e) {
					System.out.println("Exception when trying to get url information from " + url);;
				}
				
				p = Pattern.compile("href=\"/webmm/case/([0-9]+)/");
				m = p.matcher(content);
				
				m.find();
				caseIDs = m.group(1);
				
				while(m.find())
					caseIDs += ";" + m.group(1);
			}
			
			/*
			 * Output
			 */
			dos.writeBytes(sheet.getCell(0,i).getContents().trim() + "\t" + sheet.getCell(1,i).getContents().trim() + "\t" + sheet.getCell(2,i).getContents().trim() + "\t" + caseIDs + "\t" + hits + "\r\n");
		}		
		
		book.close();
		dos.close();
	}
}
