package database_PSNet;

import java.io.File;
import java.io.IOException;
import java.util.Vector;

import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

public class TestOneCase {	
	public static String rawFile = "C:\\Users\\hkang1\\Desktop\\AHRQ\\WEBMM\\raw\\category tree.xls";
	
	public static void main(String[] args) throws BiffException, IOException {
		getTerms("12");
		
		/*----
		System.out.println(getVectorSimilarity("337", "337"));
		System.out.println(getVectorSimilarity("337", "224"));
		System.out.println(getVectorSimilarity("337", "6"));
		System.out.println(getVectorSimilarity("337", "248"));
		----*/
		
		
	}
	
	public static void getTerms(String caseNum) throws BiffException, IOException {
		Workbook book = Workbook.getWorkbook(new File(rawFile));
		Sheet sheet = book.getSheet(0);
		
		System.out.println("start from here...");
		
		for(int i=1; i<sheet.getRows(); i++) {
			String level = sheet.getCell(0,i).getContents().trim();
			String name = sheet.getCell(1,i).getContents().trim();
			String[] cases = sheet.getCell(2,i).getContents().trim().split(";");
			
			for(int j=0; j<cases.length; j++) {
				if(cases[j].equals(caseNum)) {
					System.out.println(level + " " + name);
					break;
				}
			}
		}
		
		System.out.println("end here...");
		
		book.close();
	}
	
	public static Double getVectorSimilarity(String case1, String case2) throws BiffException, IOException {
		Vector<Integer> v1 = getVector(case1);
		Vector<Integer> v2 = getVector(case2);
		
		//System.out.println(v1);
		//System.out.println(v2);
		
		return mulVector(v1, v2) / absMulVector(v1, v2);
	}
	
	public static Vector<Integer> getVector(String caseNum) throws BiffException, IOException {
		Workbook book = Workbook.getWorkbook(new File(rawFile));
		Sheet sheet = book.getSheet(0);
		
		Vector<Integer> v = new Vector<Integer>();
		
		//System.out.println("case: " + caseNum);
		
		for(int i=2; i<50; i++) {
			//String level = sheet.getCell(0,i).getContents().trim();
			String[] cases = sheet.getCell(2,i).getContents().trim().split(";");
			
			boolean flag = false;
			for(int j=0; j<cases.length; j++) {
				if(cases[j].equals(caseNum)) {
					//System.out.println(level);
					flag = true;
					v.add(1);
					break;
				}
			}
			if(!flag)
				v.add(0);
		}		
		
		//System.out.println(caseNum + ":" + v);
		
		book.close();
		
		return v;
	}
	
	public static Integer mulVector(Vector<Integer> v1, Vector<Integer> v2) {
		int result = 0;
		for(int i=0; i<v1.size(); i++)
			result += v1.get(i) * v2.get(i);		
		
		return result;
	}
	
	public static double absMulVector(Vector<Integer> v1, Vector<Integer> v2) {
		double abs1 = 0;
		double abs2 = 0;
		
		for(int i=0; i<v1.size(); i++) {
			abs1 += Math.pow(v1.get(i), 2);
			abs2 += Math.pow(v2.get(i), 2);
		}
		
		return Math.sqrt(abs1) * Math.sqrt(abs2);
	}
	
}
