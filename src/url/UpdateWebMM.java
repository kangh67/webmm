/**
 * View case number in WebM&M
 * Input: the number of the latest case
 */
package url;

import java.io.IOException;

public class UpdateWebMM {
	public static void main(String[] args) throws IOException {
		existOrNot(100);
	}
	
	//judge case exist or not
	public static boolean existOrNot(int ID) throws IOException {
		String URL = "https://psnet.ahrq.gov/webmm/case/7";
		
		System.out.println(URL);
		String content = CatchInf.getHtml(URL);
		
		System.out.println(content);
		
		return true;
	}
}
