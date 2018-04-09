/*
 * Given a URL, return the source codes
 */
package url;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CatchInf {
	public static void main(String[] args) throws IOException {
		String content = getHtml("http://webmm.ahrq.gov/perspective.aspx?perspectiveID=31");
		System.out.println(content);
	}
	
	
	public static String getHtml(String urlString) throws IOException {
	
		try {
			String web = null;	
		
			while(web == null) {
			
				StringBuffer html = new StringBuffer();
				URL url = new URL(urlString);
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				conn.setInstanceFollowRedirects(false);	
				
				InputStreamReader isr;
				
				isr = new InputStreamReader(conn.getInputStream());
				
				BufferedReader br = new BufferedReader(isr);
				String temp;
				
				//--- catch the jumped page
				temp = br.readLine();
				if(temp.contains("Object moved")) {
					temp = br.readLine();
					Pattern p = Pattern.compile("<a href=\"(.+)\">here");
					Matcher m;
					
					m = p.matcher(temp);
					
					if(m.find()) {						
						urlString = "http://webmm.ahrq.gov" + m.group(1).replaceAll("%2f", "/").replaceAll("%3f", "?").replaceAll("%3d", "=").replaceAll("%26", "&");
						url = new URL(urlString);
						conn = (HttpURLConnection) url.openConnection();
						conn.setInstanceFollowRedirects(false);
						isr = new InputStreamReader(conn.getInputStream());
						br = new BufferedReader(isr);
					}
				}
				//-------
				
				while ((temp = br.readLine()) != null) {
					html.append(temp).append("\n");
				}
			
				br.close();
				isr.close();
				web = html.toString();
			
		
			}	
			return web;
		}catch (java.net.ConnectException e) {
			return CatchInf.getHtml(urlString);
		}		
	}	
}