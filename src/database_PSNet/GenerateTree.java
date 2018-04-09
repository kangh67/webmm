//generate excel documents to show the tree structure
package database_PSNet;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

public class GenerateTree {
	public static String sourceFile = "C:\\Users\\hkang1\\Desktop\\AHRQ\\WEBMM\\raw\\category tree.xls";
	public static String writeFile = "C:\\Users\\hkang1\\Desktop\\AHRQ\\WEBMM\\raw\\category tree_forshow.txt";
	
	public static void main (String[] args) throws BiffException, IOException {
		readAndWrite();
	}
	
	public static void readAndWrite() throws BiffException, IOException {
		//input stream
		Workbook book = Workbook.getWorkbook(new File(sourceFile));
		Sheet sheet = book.getSheet(0);
		
		//output stream
		DataOutputStream dos = new DataOutputStream(new FileOutputStream(new File(writeFile)));
		
		for(int i=1; i<sheet.getRows(); i++) {
			if(sheet.getCell(0,i).getContents().trim().length() == 1)
				dos.writeBytes(sheet.getCell(0,i).getContents() + " " + sheet.getCell(1,i).getContents() + "(" + sheet.getCell(3,i).getContents() +")" + "\r\n");
			else if(sheet.getCell(0,i).getContents().trim().length() == 3 || sheet.getCell(0,i).getContents().trim().length() == 4)
				dos.writeBytes("\t" + sheet.getCell(0,i).getContents() + " " + sheet.getCell(1,i).getContents() + "(" + sheet.getCell(3,i).getContents() +")" + "\r\n");
			else if(sheet.getCell(0,i).getContents().trim().length() == 5 || sheet.getCell(0,i).getContents().trim().length() == 6)
				dos.writeBytes("\t" + "\t" + sheet.getCell(0,i).getContents() + " " + sheet.getCell(1,i).getContents() + "(" + sheet.getCell(3,i).getContents() +")" + "\r\n");
			else if(sheet.getCell(0,i).getContents().trim().length() == 7 || sheet.getCell(0,i).getContents().trim().length() == 8)
				dos.writeBytes("\t" + "\t" + "\t" + sheet.getCell(0,i).getContents() + " " + sheet.getCell(1,i).getContents() + "(" + sheet.getCell(3,i).getContents() +")" + "\r\n");
		}
		
		dos.close();
		book.close();
	}
}
