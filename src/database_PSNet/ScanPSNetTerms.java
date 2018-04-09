/**
 * Scan PSNet and update the latest taxonomy at https://psnet.ahrq.gov/topics
 * The output is a TXT file PSNet raw taxonomy.txt
 * It contains 7 topics from totally 8, but Origin/Sponsor is not available for WebM&M cases
 * The output of this code should be further organized to a XLS file, delete Origin/Sponsor if necessary.
 */
package database_PSNet;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import url.CatchInf;

public class ScanPSNetTerms {
	public static String output = "C:\\Users\\hkang1\\Google Drive\\AHRQ\\WEBMM\\PSNet\\PSNet raw taxonomy.txt";
	
	public static void main(String[] args) throws IOException {
		scanAndGetTerms();
	}
	
	public static void scanAndGetTerms() throws IOException {
		/*
		 * Extract page source
		 */
		String url = "https://psnet.ahrq.gov/topics";		
		String content = "";
		
		try {
			content = CatchInf.getHtml(url);
		}catch(Exception e) {
			System.out.println("Exception when trying to get url information from " + url);;
		}
		
		/*
		 * Find all TopicIDs and Topic names, write to the output file
		 */
		Pattern p = Pattern.compile("f_topicIDs=([0-9]+\">.+)</a>");
		Matcher m;
		
		m = p.matcher(content);		
		
		DataOutputStream dos = new DataOutputStream(new FileOutputStream(new File(output)));
		
		while(m.find()) 
			dos.writeBytes(m.group(1).replace("\">", "\t").replaceAll("&amp;", "&").replaceAll("&#39;", "'") + "\r\n");			
		
		dos.close();
	}
}
