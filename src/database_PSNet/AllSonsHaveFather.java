//in order to judge whether all the leafs(sons) have their branches(fathers);
package database_PSNet;

import java.io.File;
import java.io.IOException;

import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

public class AllSonsHaveFather {
	public static String sourceFile = "C:\\Users\\hkang1.UTHSAHS\\Google Drive\\AHRQ\\WEBMM\\raw\\category tree.xls";
	
	public static void main(String[] args) throws BiffException, IOException {
		scanAllLevels();
	}
	
	public static void scanAllLevels() throws BiffException, IOException {
		Workbook book = Workbook.getWorkbook(new File(sourceFile));
		Sheet sheet = book.getSheet(0);
		
		String level2 = "";
		String level3 = "";
		
		int i = 1;
		while(i<sheet.getRows()) {
			String title = sheet.getCell(0,i).getContents().trim();
			if(title.length() == 1) {
				level2 = "";
				level3 = "";
				i ++;
				continue;
			}else if(title.length() == 3 || title.length() == 4) {
				level2 = sheet.getCell(2,i).getContents().trim();
				level3 = "";
				i ++;
				continue;
			}else if(title.length() == 5 || title.length() == 6) {
				level3 = sheet.getCell(2,i).getContents().trim();
				judgeSon(title, level3, level2);
				i ++;
				continue;
			}else if(title.length() == 7 || title.length() == 8) {
				judgeSon(title, sheet.getCell(2,i).getContents().trim(), level3);
				i ++;
				continue;
			}
		}
		
		
		book.close();
	}
	
	public static void judgeSon(String sonID, String son, String father) {
		String[] allSons = son.split(";");
		String[] allFathers = father.split(";");
		
		for(int i=0; i<allSons.length; i ++) {
			int contain = 0;
			for(int j=0; j<allFathers.length; j++) {
				if(allSons[i].equals(allFathers[j])) {
					contain = 1;
					break;
				}
			}
			if(contain == 0) {
				System.out.println(sonID + ":" + allSons[i]);
				break;
			}
		}
	}
}
