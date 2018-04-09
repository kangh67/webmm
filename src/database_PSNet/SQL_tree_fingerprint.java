package database_PSNet;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

public class SQL_tree_fingerprint {
	public static String input1 = "C:\\Users\\hkang1\\Google Drive\\AHRQ\\WEBMM\\PSNet\\1_Approach to Improving Safety.txt";
	public static String input2 = "C:\\Users\\hkang1\\Google Drive\\AHRQ\\WEBMM\\PSNet\\2_Clinical Area.txt";
	public static String input3 = "C:\\Users\\hkang1\\Google Drive\\AHRQ\\WEBMM\\PSNet\\3_Error Types.txt";
	public static String input4 = "C:\\Users\\hkang1\\Google Drive\\AHRQ\\WEBMM\\PSNet\\4_Safety Target.txt";
	public static String input5 = "C:\\Users\\hkang1\\Google Drive\\AHRQ\\WEBMM\\PSNet\\5_Setting of Care.txt";
	public static String input6 = "C:\\Users\\hkang1\\Google Drive\\AHRQ\\WEBMM\\PSNet\\6_Target Audience.txt";
	
	public static String backupFile_tree = "C:\\Users\\hkang1\\Google Drive\\AHRQ\\WEBMM\\PSNet\\treeSQL_backup.txt";
	public static String backupFile_fingerprint = "C:\\Users\\hkang1\\Google Drive\\AHRQ\\WEBMM\\PSNet\\caseFingerprintSQL_backup.txt";
	public static String SQLFile = "C:\\Users\\hkang1\\Google Drive\\AHRQ\\WEBMM\\sql_PSNet\\tree_fingerprint.sql";
	
	public static void main(String[] args) throws IOException {
		ArrayList<String> inputFiles = new ArrayList<String>();
		inputFiles.add(input1);
		inputFiles.add(input2);
		inputFiles.add(input3);
		inputFiles.add(input4);
		inputFiles.add(input5);
		inputFiles.add(input6);
		
		//create tree information list with tree structure
		ArrayList<String[]> tree = createInfoList(inputFiles);
		
		//write tree information in a file for back up
		writeBackup_tree(tree);
		
		//create fingerprint for every case
		ArrayList<String[]> caseFinger = createFingerprint(tree);
		
		//write case's fingerprints in a file for back up
		writeBackup_fingerprint(tree, caseFinger);
		
		//Create SQL file
		writeSQL(tree, caseFinger);
	}
	
	public static ArrayList<String[]> createInfoList(ArrayList<String> inputFiles) throws IOException {
		BufferedReader br = null;		
		ArrayList<String[]> treeInfo = new ArrayList<String[]>();
		
		for(int i=0; i<inputFiles.size(); i++) {
			br = new BufferedReader(new FileReader(inputFiles.get(i)));
			String[] thisCase = new String[10];
			
			/*
			 * The first line, titles of the 6 perspectives
			 */
			//db_id
			thisCase[0] = String.valueOf(treeInfo.size() + 1);
			//category
			thisCase[1] = String.valueOf(i + 1);
			//name
			if(i==0)
				thisCase[2] = "Approach to Improving Safety";
			else if(i==1)
				thisCase[2] = "Clinical Area";
			else if(i==2)
				thisCase[2] = "Error Types";
			else if(i==3)
				thisCase[2] = "Safety Target";
			else if(i==4)
				thisCase[2] = "Setting of Care";
			else if(i==5)
				thisCase[2] = "Target Audience";			
			//level
			thisCase[3] = "0";
			//father
			thisCase[4] = "NA";
			//son
			thisCase[5] = "NA";
			//son number
			thisCase[6] = "0";
			//caseID
			thisCase[7] = "NA";
			//cas number
			thisCase[8] = "NA";
			//Tipic ID
			thisCase[9] = "NA";
			
			treeInfo.add(thisCase);
			
			/*
			 * Other lines
			 */			
			String line = "";
			
			while((line = br.readLine()) != null) {
				String[] thisline = line.split("\t");
				thisCase = new String[10];
				
				//db_id
				thisCase[0] = String.valueOf(treeInfo.size() + 1);
				//category
				thisCase[1] = thisline[0];
				//name				
				thisCase[2] = thisline[1];		
				//level
				thisCase[3] = String.valueOf(countLevel(thisline[0]));
				//father
				thisCase[4] = findFather(thisline[0]);
				//son
				thisCase[5] = "NA";
				//son number
				thisCase[6] = "0";
				//caseID
				if(thisline[3] == null || thisline[3].equals(""))
					thisCase[7] = "NA";
				else
					thisCase[7] = thisline[3];
				//cas number
				thisCase[8] = thisline[4];
				//Tipic ID
				thisCase[9] = thisline[2];
				
				treeInfo.add(thisCase);
			}			
			
			br.close();
		}		
		
		//add sons and sons number
		treeInfo = countSons(treeInfo);
		
		//add root points' information
		treeInfo = processRootPoint(treeInfo);
		
		return treeInfo;
	}
	
	//write back up file for this tree structure
	public static void writeBackup_tree(ArrayList<String[]> treeInfo) throws IOException {
		DataOutputStream dos = new DataOutputStream(new FileOutputStream(new File(backupFile_tree)));
			
		dos.writeBytes("db_id" + "\t" + "CATE" + "\t" + "NAME" + "\t" + "LEVEL" + "\t" + "FATHER" + "\t" + "SON" + "\t" + "SON NUM" + "\t" + "CASE ID" + "\t" + "CASE NUM" + "\t" + "TOPIC ID" +"\r\n"); 
			
		for(int i=0; i<treeInfo.size(); i++) {
			dos.writeBytes(treeInfo.get(i)[0] + "\t");
			dos.writeBytes(treeInfo.get(i)[1] + "\t");
			dos.writeBytes(treeInfo.get(i)[2] + "\t");
			dos.writeBytes(treeInfo.get(i)[3] + "\t");
			dos.writeBytes(treeInfo.get(i)[4] + "\t");
			dos.writeBytes(treeInfo.get(i)[5] + "\t");
			dos.writeBytes(treeInfo.get(i)[6] + "\t");
			dos.writeBytes(treeInfo.get(i)[7] + "\t");
			dos.writeBytes(treeInfo.get(i)[8] + "\t");
			dos.writeBytes(treeInfo.get(i)[9] + "\r\n");
		}
			
		dos.close();
	}
	
	//generate fingerprint for every cases
	public static ArrayList<String[]> createFingerprint(ArrayList<String[]> treeInfo) throws IOException{
		//---feature size for each subtype
		int treeSize1 = getTreeSize(input1);
		int treeSize2 = getTreeSize(input2);
		int treeSize3 = getTreeSize(input3);
		int treeSize4 = getTreeSize(input4);
		int treeSize5 = getTreeSize(input5);
		int treeSize6 = getTreeSize(input6);
		
		//---get all case IDs
		ArrayList<Integer> caseID = new ArrayList<Integer>();
			
		for(int i=0; i<treeInfo.size(); i++) {
			if(!treeInfo.get(i)[7].equals("NA")) {
				String[] thiscase = treeInfo.get(i)[7].split(";");
				
				for(int j=0; j<thiscase.length; j++) {
					if(!caseID.contains(Integer.valueOf(thiscase[j]))) 
						caseID.add(Integer.valueOf(thiscase[j]));				
				}
			}
		}
			
		Collections.sort(caseID);
		//---------
			
		//---Initialize the cases with db_id and case ID		
		ArrayList<String[]> caseFinger = new ArrayList<String[]>();
		int featureSize = treeInfo.size() + 2 + 6;
			
		for(int i=0; i<caseID.size(); i++) {
			String[] thiscase = new String[featureSize];
			thiscase[0] = String.valueOf(i + 1);
			thiscase[1] = String.valueOf(caseID.get(i));
				
			for(int j=2; j<treeInfo.size()+2; j++)
				thiscase[j] = "0";
				
			caseFinger.add(thiscase);
		}
		//---------
			
		//---insert fingerprint for every cell
		for(int i=0; i<caseFinger.size(); i++) {			
			for(int j=0; j<treeInfo.size(); j++) {
				if(!treeInfo.get(j)[7].equals("NA")) {
					String[] thiscase = treeInfo.get(j)[7].split(";");
					for(int k=0; k<thiscase.length; k++) {
						if(thiscase[k].equals(caseFinger.get(i)[1])) {
							caseFinger.get(i)[j + 2] = "1";
							break;
						}
					}
				}
			}
		}
		//-------		
		
		//---insert 6 integreted fingerprints
		for(int i=0; i<caseFinger.size(); i++) {
			String f1 = "";
			String f2 = "";
			String f3 = "";
			String f4 = "";
			String f5 = "";
			String f6 = "";			
			
			for(int j=2; j<2+treeSize1; j++)
				f1 += caseFinger.get(i)[j];
			for(int j=2+treeSize1; j<2+treeSize1+treeSize2; j++)
				f2 += caseFinger.get(i)[j];
			for(int j=2+treeSize1+treeSize2; j<2+treeSize1+treeSize2+treeSize3; j++)
				f3 += caseFinger.get(i)[j];
			for(int j=2+treeSize1+treeSize2+treeSize3; j<2+treeSize1+treeSize2+treeSize3+treeSize4; j++)
				f4 += caseFinger.get(i)[j];
			for(int j=2+treeSize1+treeSize2+treeSize3+treeSize4; j<2+treeSize1+treeSize2+treeSize3+treeSize4+treeSize5; j++)
				f5 += caseFinger.get(i)[j];
			for(int j=2+treeSize1+treeSize2+treeSize3+treeSize4+treeSize5; j<2+treeSize1+treeSize2+treeSize3+treeSize4+treeSize5+treeSize6; j++)
				f6 += caseFinger.get(i)[j];		
				
			caseFinger.get(i)[caseFinger.get(i).length-6] = f1;
			caseFinger.get(i)[caseFinger.get(i).length-5] = f2;
			caseFinger.get(i)[caseFinger.get(i).length-4] = f3;
			caseFinger.get(i)[caseFinger.get(i).length-3] = f4;
			caseFinger.get(i)[caseFinger.get(i).length-2] = f5;
			caseFinger.get(i)[caseFinger.get(i).length-1] = f6;			
		}		
		//------
			
		return caseFinger;
	}
	
	//write back up file for case fingerprint
	public static void writeBackup_fingerprint(ArrayList<String[]> treeInfo, ArrayList<String[]> caseFinger) throws IOException {
		DataOutputStream dos = new DataOutputStream(new FileOutputStream(new File(backupFile_fingerprint)));
			
		dos.writeBytes("db_id" + "\t" + "CASE ID");
			
		for(int i=0; i<treeInfo.size(); i++)
			dos.writeBytes("\t" + treeInfo.get(i)[1]);
			
		dos.writeBytes("\t" + "F1");
		dos.writeBytes("\t" + "F2");
		dos.writeBytes("\t" + "F3");
		dos.writeBytes("\t" + "F4");
		dos.writeBytes("\t" + "F5");
		dos.writeBytes("\t" + "F6");
			
		dos.writeBytes("\r\n");		
			
		for(int i=0; i<caseFinger.size(); i++) {
			dos.writeBytes(caseFinger.get(i)[0]);		
				
			for(int j=1; j<caseFinger.get(i).length; j++)
				dos.writeBytes("\t" + caseFinger.get(i)[j]);	
				
			dos.writeBytes("\r\n");	
		}
			
		dos.close();
	}
	public static void writeSQL(ArrayList<String[]> treeInfo, ArrayList<String[]> caseFinger) throws IOException {
		DataOutputStream dos = new DataOutputStream(new FileOutputStream(new File(SQLFile)));
		String name_table1 = "treeinfo";
		String name_table2 = "casefinger";
		
		//---Head
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
		//-------------
		
		//---create table1: treeInfo
		dos.writeBytes("-- ----------------------------" + "\r\n");
		dos.writeBytes("-- Table structure for " + name_table1 + "\r\n");
		dos.writeBytes("-- ----------------------------" + "\r\n");
		dos.writeBytes("CREATE TABLE `" + name_table1 + "` (" + "\r\n");
		dos.writeBytes("\t" + "`db_id` int(255) NOT NULL auto_increment," + "\r\n");
		dos.writeBytes("\t" + "`cate` text collate utf8_unicode_ci," + "\r\n");
		dos.writeBytes("\t" + "`name` text collate utf8_unicode_ci," + "\r\n");
		dos.writeBytes("\t" + "`level` text collate utf8_unicode_ci," + "\r\n");
		dos.writeBytes("\t" + "`father` text collate utf8_unicode_ci," + "\r\n");
		dos.writeBytes("\t" + "`son` text collate utf8_unicode_ci," + "\r\n");
		dos.writeBytes("\t" + "`sonNum` text collate utf8_unicode_ci," + "\r\n");
		dos.writeBytes("\t" + "`caseID` text collate utf8_unicode_ci," + "\r\n");
		dos.writeBytes("\t" + "`caseNum` text collate utf8_unicode_ci," + "\r\n");
		dos.writeBytes("\t" + "`topicID` text collate utf8_unicode_ci," + "\r\n");
		dos.writeBytes("\t" + "PRIMARY KEY  (`db_id`)" + "\r\n");
		dos.writeBytes(") ENGINE=InnoDB AUTO_INCREMENT=" + treeInfo.size() + " DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;" + "\r\n");
		dos.writeBytes("\r\n");
		//-----------
		
		//---create table2: caseFingerprint
		dos.writeBytes("-- ----------------------------" + "\r\n");
		dos.writeBytes("-- Table structure for " + name_table2 + "\r\n");
		dos.writeBytes("-- ----------------------------" + "\r\n");
		dos.writeBytes("CREATE TABLE `" + name_table2 + "` (" + "\r\n");
		dos.writeBytes("\t" + "`db_id` int(255) NOT NULL auto_increment," + "\r\n");
		dos.writeBytes("\t" + "`caseID` text collate utf8_unicode_ci," + "\r\n");
		
		for(int i=0; i<treeInfo.size(); i++) 
			dos.writeBytes("\t" + "`C" + treeInfo.get(i)[1] + "` char(2) collate utf8_unicode_ci," + "\r\n");
		
		dos.writeBytes("\t" + "`f1` text collate utf8_unicode_ci," + "\r\n");
		dos.writeBytes("\t" + "`f2` text collate utf8_unicode_ci," + "\r\n");
		dos.writeBytes("\t" + "`f3` text collate utf8_unicode_ci," + "\r\n");
		dos.writeBytes("\t" + "`f4` text collate utf8_unicode_ci," + "\r\n");
		dos.writeBytes("\t" + "`f5` text collate utf8_unicode_ci," + "\r\n");
		dos.writeBytes("\t" + "`f6` text collate utf8_unicode_ci," + "\r\n");
		dos.writeBytes("\t" + "PRIMARY KEY  (`db_id`)" + "\r\n");
		dos.writeBytes(") ENGINE=InnoDB AUTO_INCREMENT=" + caseFinger.size() + " DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;" + "\r\n");
		dos.writeBytes("\r\n");
		//-----------
		
		//write content for table1
		dos.writeBytes("-- ----------------------------" + "\r\n");
		dos.writeBytes("-- Records " + "\r\n");
		dos.writeBytes("-- ----------------------------" + "\r\n");
		
		for(int i=0; i<treeInfo.size(); i++) {
			dos.writeBytes("INSERT INTO `" + name_table1 + "` VALUES (");
			dos.writeBytes("'" + treeInfo.get(i)[0] + "'");
			
			for(int j=1; j<treeInfo.get(i).length; j++) 
				dos.writeBytes(", '" + treeInfo.get(i)[j].replaceAll("\\'", "\\\\'") + "'");
			
			dos.writeBytes(");" + "\r\n");
		}
		//-------------
		
		//write content for table2
		for(int i=0; i<caseFinger.size(); i++) {
			dos.writeBytes("INSERT INTO `" + name_table2 + "` VALUES (");
			dos.writeBytes("'" + caseFinger.get(i)[0] + "'");
			
			for(int j=1; j<caseFinger.get(i).length; j++) 
				dos.writeBytes(", '" + caseFinger.get(i)[j] + "'");
			
			dos.writeBytes(");" + "\r\n");
		}
		//-------------
		dos.close();
	}	
	
	//judge the level by counting how many "_"
	public static Integer countLevel(String category) {
		int count = 0;
		for(int i=0; i<category.length(); i++)
			if(category.substring(i, i+1).equals("_"))
				count ++;
			
		return count;
	}
	
	//find category's father
	public static String findFather(String category) {
		if(!category.contains("_"))
			return "NA";
			
		int last_ = category.length();
			
		for(int i=category.length()-1; i>=0; i--)
			if(category.substring(i, i+1).equals("_")) {
				last_ = i;
				break;
			}
			
		return category.substring(0, last_);
	}
	
	//find category's sons and sons' number
	public static ArrayList<String[]> countSons(ArrayList<String[]> treeInfo) {
		for(int i=0; i<treeInfo.size(); i++) {
			String sons = "NA";
			int countSons = 0;
				
			for(int j=i+1; j<treeInfo.size(); j++) {				
				if(treeInfo.get(j)[1].startsWith(treeInfo.get(i)[1] + "_") && countLevel(treeInfo.get(j)[1]) - 1 == countLevel(treeInfo.get(i)[1])) {
					if(countSons == 0) 
						sons = treeInfo.get(j)[1];
					else
						sons += ";" + treeInfo.get(j)[1];
					countSons ++;				
				}
			}
				
			treeInfo.get(i)[5] = sons;
			treeInfo.get(i)[6] = String.valueOf(countSons);
		}
			
		return treeInfo;
	}
	
	//count caseIDs for root point and fill these information to the list
	public static ArrayList<String[]> processRootPoint(ArrayList<String[]> treeInfo) {
		for(int i=0; i<treeInfo.size(); i++) {
			if(treeInfo.get(i)[3].equals("0")) {
				ArrayList<Integer> caseID = new ArrayList<Integer>();
				int j = i + 1;
					
				while(!treeInfo.get(j)[3].equals("0")) {
					if(treeInfo.get(j)[3].equals("1") && !treeInfo.get(j)[7].equals("NA")) {
						String[] thiscase = treeInfo.get(j)[7].split(";");
							
						for(int k=0; k<thiscase.length; k++)
							if(!caseID.contains(Integer.valueOf(thiscase[k])))
								caseID.add(Integer.valueOf(thiscase[k]));
					}
					j ++;
						
					if(j == treeInfo.size())
						break;
				}
					
				Collections.sort(caseID);
				Collections.reverse(caseID);
					
				treeInfo.get(i)[7] = String.valueOf(caseID.get(0));
				for(int c=1; c<caseID.size(); c++)
					treeInfo.get(i)[7] += ";" + caseID.get(c);
					
				treeInfo.get(i)[8] = String.valueOf(caseID.size());
					
				i = j - 1;
			}
		}
		return treeInfo;
	}
	
	public static int getTreeSize(String inputFile) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(inputFile));
		int length = 1;		
		
		while((br.readLine()) != null)
			length ++;	
		
		br.close();
		
		return length;
	}
}
