package report;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import user.DBBean;

public class NewCase {	
	public String uni_id;
	public String reporter;
	public String caseName;
	public String initime;
	public String uptime;
	public String description;
	public String link;
	
	public boolean general = false;
	public ArrayList<String> route_gen;
	public HashMap<String, String> info_gen;
	
	public String title_bbp = "Blood or Blood Product";
	public boolean bbp = false;
	public ArrayList<String> route_bbp;
	public HashMap<String, String> info_bbp;
	
	public String title_hit = "Device or Medical/Surgical Supply, including Health Information Technology (HIT)";
	public boolean hit = false;
	public ArrayList<String> route_hit;
	public HashMap<String, String> info_hit;
	
	public String title_fall = "Fall";
	public boolean fall = false;
	public ArrayList<String> route_fall;
	public HashMap<String, String> info_fall;
	
	public String title_hai = "Healthcare-associated Infection";
	public boolean hai = false;
	public ArrayList<String> route_hai;
	public HashMap<String, String> info_hai;
	
	public String title_mos = "Medication or Other Substance";
	public boolean mos = false;
	public ArrayList<String> route_mos;
	public HashMap<String, String> info_mos;
	
	public String title_per = "Perinatal";
	public boolean per = false;
	public ArrayList<String> route_per;
	public HashMap<String, String> info_per;
	
	public String title_pu = "Pressure Ulcer";
	public boolean pu = false;
	public ArrayList<String> route_pu;
	public HashMap<String, String> info_pu;
	
	public String title_sa = "Surgery or Anesthesia (includes invasive procedure)";
	public boolean sa = false;
	public ArrayList<String> route_sa;
	public HashMap<String, String> info_sa;
	
	public String title_vt = "Venous Thromboembolism";
	public boolean vt = false;
	public ArrayList<String> route_vt;
	public HashMap<String, String> info_vt;	
	
	public boolean review = false;
	
	public NewCase() {}
	
	public void initialization() {
		this.uni_id = null;
		this.reporter = null;
		this.caseName = null;
		this.initime = null;
		this.uptime = null;
		this.description = null;
		this.link = null;
		
		this.general = false;
		this.route_gen = null;
		this.info_gen = null;
		
		this.bbp = false;
		this.route_bbp = null;
		this.info_bbp = null;
		
		this.hit = false;
		this.route_hit = null;
		this.info_hit = null;
		
		this.fall = false;
		this.route_fall = null;
		this.info_fall = null;
		
		this.hai = false;
		this.route_hai = null;
		this.info_hai = null;
		
		this.mos = false;
		this.route_mos = null;
		this.info_mos = null;
		
		this.per = false;
		this.route_per = null;
		this.info_per = null;
		
		this.pu = false;
		this.route_pu = null;
		this.info_pu = null;
		
		this.sa = false;
		this.route_sa = null;
		this.info_sa = null;
		
		this.vt = false;
		this.route_vt = null;
		this.info_vt = null;
		
		this.review = false;
	}
	
	public void setUniID(DBBean dbb) throws SQLException {		
		String sql = "select * from report_general";
		ResultSet rs = dbb.query(sql);
		
		int id = 1;
		
		while(rs.next()) {
			if(rs.getString("uni_id").startsWith("R")) {
				String thisid = rs.getString("uni_id").substring(1, rs.getString("uni_id").length());
				if(Integer.valueOf(thisid) >= id)
					id = Integer.valueOf(thisid) + 1;
			}
		}
		
		rs.close();		
		
		this.uni_id = "R" + String.valueOf(id);
	}
	
	public boolean getActivated(String formName) {
		if(formName.equals("general"))
			return this.general;
		else if(formName.equals("bbp"))
			return this.bbp;
		else if(formName.equals("hit"))
			return this.hit;
		else if(formName.equals("fall"))
			return this.fall;
		else if(formName.equals("hai"))
			return this.hai;
		else if(formName.equals("mos"))
			return this.mos;
		else if(formName.equals("per"))
			return this.per;
		else if(formName.equals("pu"))
			return this.pu;
		else if(formName.equals("sa"))
			return this.sa;
		else if(formName.equals("vt"))
			return this.vt;
		else
			return false;
	}
	
	public String getTitle(String formName) {
		if(formName.equals("general"))
			return "General";
		else if(formName.equals("bbp"))
			return this.title_bbp;
		else if(formName.equals("hit"))
			return this.title_hit;
		else if(formName.equals("fall"))
			return this.title_fall;
		else if(formName.equals("hai"))
			return this.title_hai;
		else if(formName.equals("mos"))
			return this.title_mos;
		else if(formName.equals("per"))
			return this.title_per;
		else if(formName.equals("pu"))
			return this.title_pu;
		else if(formName.equals("sa"))
			return this.title_sa;
		else if(formName.equals("vt"))
			return this.title_vt;
		else
			return "NA";
	}
	
	public ArrayList<String> getRoute(String formName) {
		if(formName.equals("general"))
			return this.route_gen;
		else if(formName.equals("bbp"))
			return this.route_bbp;
		else if(formName.equals("hit"))
			return this.route_hit;
		else if(formName.equals("fall"))
			return this.route_fall;
		else if(formName.equals("hai"))
			return this.route_hai;
		else if(formName.equals("mos"))
			return this.route_mos;
		else if(formName.equals("per"))
			return this.route_per;
		else if(formName.equals("pu"))
			return this.route_pu;
		else if(formName.equals("sa"))
			return this.route_sa;
		else if(formName.equals("vt"))
			return this.route_vt;
		else
			return null;
	}
	
	public HashMap<String, String> getInfo(String formName) {
		if(formName.equals("general"))
			return this.info_gen;
		else if(formName.equals("bbp"))
			return this.info_bbp;
		else if(formName.equals("hit"))
			return this.info_hit;
		else if(formName.equals("fall"))
			return this.info_fall;
		else if(formName.equals("hai"))
			return this.info_hai;
		else if(formName.equals("mos"))
			return this.info_mos;
		else if(formName.equals("per"))
			return this.info_per;
		else if(formName.equals("pu"))
			return this.info_pu;
		else if(formName.equals("sa"))
			return this.info_sa;
		else if(formName.equals("vt"))
			return this.info_vt;
		else
			return null;
	}
	
	public void iniCertainForm(String form) {
		if(form.equals("general")) {
			this.general = false;
			this.route_gen = new ArrayList<String>();
			this.info_gen = new HashMap<String, String>();
		}
		if(form.equals("fall")) {
			this.fall = false;
			this.route_fall = new ArrayList<String>();
			this.info_fall = new HashMap<String, String>();
		}
		if(form.equals("pu")) {
			this.pu = false;
			this.route_pu = new ArrayList<String>();
			this.info_pu = new HashMap<String, String>();
		}
	}
	
	public void saveInfo(String form, String question, String[] ans) {
		if(form.equals("general")) {
			String allans = ans[0];
			
			for(int i=1; i<ans.length; i++) 
				allans = allans + "||" + ans[i];
			
			this.route_gen.add(question);
			this.info_gen.put(question, allans);
			this.general = true;
		}
		if(form.equals("fall")) {
			String allans = ans[0];
			
			for(int i=1; i<ans.length; i++)
				allans = allans + "||" + ans[i];
			
			this.route_fall.add(question);
			this.info_fall.put(question, allans);
			this.fall = true;
		}
		if(form.equals("pu")) {
			String allans = ans[0];
			
			for(int i=1; i<ans.length; i++)
				allans = allans + "||" + ans[i];
			
			this.route_pu.add(question);
			this.info_pu.put(question, allans);
			this.pu = true;
		}
	}
	
	public void saveInfo_other(String form, String question, String[] ans, String oindex, String ocontent) {
		if(form.equals("general")) {
			String allans;
			if(ans[0].equals(oindex))
				allans = ans[0] + "$$" + ocontent;
			else
				allans = ans[0];
			
			for(int i=1; i<ans.length; i++) {
				if(ans[i].equals(oindex))
					allans = allans + "||" + ans[i] + "$$" + ocontent;
				else
					allans = allans + "||" + ans[i];
			}
			
			this.route_gen.add(question);
			this.info_gen.put(question, allans);
			this.general = true;
		}
		
		if(form.equals("fall")) {
			String allans;
			if(ans[0].equals(oindex))
				allans = ans[0] + "$$" + ocontent;
			else
				allans = ans[0];
			
			for(int i=1; i<ans.length; i++) {
				if(ans[i].equals(oindex))
					allans = allans + "||" + ans[i] + "$$" + ocontent;
				else
					allans = allans + "||" + ans[i];
			}
			
			this.route_fall.add(question);
			this.info_fall.put(question, allans);
			this.fall = true;
		}
		
		if(form.equals("pu")) {
			String allans;
			if(ans[0].equals(oindex))
				allans = ans[0] + "$$" + ocontent;
			else
				allans = ans[0];
			
			for(int i=1; i<ans.length; i++) {
				if(ans[i].equals(oindex))
					allans = allans + "||" + ans[i] + "$$" + ocontent;
				else
					allans = allans + "||" + ans[i];
			}
			
			this.route_pu.add(question);
			this.info_pu.put(question, allans);
			this.pu = true;
		}
	}
	
	public void setForms(String[] formIndex) {
		this.bbp = false;
		this.hit = false;
		this.fall = false;
		this.hai = false;
		this.mos = false;
		this.per = false;
		this.pu = false;
		this.sa = false;
		this.vt = false;
		for(int i=0; i<formIndex.length; i++) {
			if(formIndex[i].equals("0"))
				this.bbp = true;
			if(formIndex[i].equals("1"))
				this.hit = true;
			if(formIndex[i].equals("2"))
				this.fall = true;
			if(formIndex[i].equals("3"))
				this.hai = true;
			if(formIndex[i].equals("4"))
				this.mos = true;
			if(formIndex[i].equals("5"))
				this.per = true;
			if(formIndex[i].equals("6"))
				this.pu = true;
			if(formIndex[i].equals("7"))
				this.sa = true;
			if(formIndex[i].equals("8"))
				this.vt = true;
		}
	}
	
	public ArrayList<String> showAvaForms() {
		ArrayList<String> ava = new ArrayList<String>();
		if(this.bbp)
			ava.add("bbp");
		if(this.hit)
			ava.add("hit");
		if(this.fall)
			ava.add("fall");
		if(this.hai)
			ava.add("hai");
		if(this.mos)
			ava.add("mos");
		if(this.per)
			ava.add("per");
		if(this.pu)
			ava.add("pu");
		if(this.sa)
			ava.add("sa");
		if(this.vt)
			ava.add("vt");
		
		return ava;
	}
	
	public String getThisPage(String lastpage) {
		ArrayList<String> avaList = new ArrayList<String>();
		if(this.general)
			avaList.add("general");
		if(this.bbp)
			avaList.add("bbp");
		if(this.hit)
			avaList.add("hit");
		if(this.fall)
			avaList.add("fall");
		if(this.hai)
			avaList.add("hai");
		if(this.mos)
			avaList.add("mos");
		if(this.per)
			avaList.add("per");
		if(this.pu)
			avaList.add("pu");
		if(this.sa)
			avaList.add("sa");
		if(this.vt)
			avaList.add("vt");
		
		int index = avaList.indexOf(lastpage);
		
		if(index < avaList.size()-1)
			return avaList.get(index + 1);
		else
			return "done";
	}
	
	public String getFormRealName(String abbr) {
		if(abbr.equalsIgnoreCase("bbp"))
			return "Blood or Blood Product";
		else if(abbr.equalsIgnoreCase("hit"))
			return "Device or Medical/Surgical Supply, including Health Information Technology (HIT)";
		else if(abbr.equalsIgnoreCase("fall"))
			return "Fall";
		else if(abbr.equalsIgnoreCase("hai"))
			return "Healthcare-associated Infection";
		else if(abbr.equalsIgnoreCase("mos"))
			return "Medication or Other Substance";
		else if(abbr.equalsIgnoreCase("per"))
			return "Perinatal";
		else if(abbr.equalsIgnoreCase("pu"))
			return "Pressure Ulcer";
		else if(abbr.equalsIgnoreCase("sa"))
			return "Surgery or Anesthesia (includes invasive procedure)";
		else if(abbr.equalsIgnoreCase("vt"))
			return "Venous Thromboembolism";
		else
			return "Other";
	}
	
	public String getMaxQues(String abbr) {
		if(abbr.equalsIgnoreCase("bbp"))
			return "7";
		else if(abbr.equalsIgnoreCase("hit"))
			return "16";
		else if(abbr.equalsIgnoreCase("fall"))
			return "13";
		else if(abbr.equalsIgnoreCase("hai"))
			return "8";
		else if(abbr.equalsIgnoreCase("mos"))
			return "17";
		else if(abbr.equalsIgnoreCase("per"))
			return "19";
		else if(abbr.equalsIgnoreCase("pu"))
			return "13";
		else if(abbr.equalsIgnoreCase("sa"))
			return "15";
		else if(abbr.equalsIgnoreCase("vt"))
			return "11";
		else
			return "1";
	}
	
	public boolean inHistory(String thisTable) {
		if((thisTable.contains("herf") || thisTable.contains("sir") || thisTable.contains("pif") || thisTable.contains("severity")) && this.route_gen != null) {
			if(this.route_gen.contains(thisTable))
				return true;
			else
				return false;
		}else if(thisTable.contains("bbp") && this.route_bbp != null) {
			if(this.route_bbp.contains(thisTable))
				return true;
			else
				return false;
		}else if(thisTable.contains("hit") && this.route_hit != null) {
			if(this.route_hit.contains(thisTable))
				return true;
			else
				return false;
		}else if(thisTable.contains("fall") && this.route_fall != null) {
			if(this.route_fall.contains(thisTable))
				return true;
			else
				return false;
		}else if(thisTable.contains("hai") && this.route_hai != null) {
			if(this.route_hai.contains(thisTable))
				return true;
			else
				return false;
		}else if(thisTable.contains("mos") && this.route_mos != null) {
			if(this.route_mos.contains(thisTable))
				return true;
			else
				return false;
		}else if(thisTable.contains("per") && this.route_per != null) {
			if(this.route_per.contains(thisTable))
				return true;
			else
				return false;
		}else if(thisTable.contains("pu") && this.route_pu != null) {
			if(this.route_pu.contains(thisTable))
				return true;
			else
				return false;
		}else if(thisTable.contains("sa") && this.route_sa != null) {
			if(this.route_sa.contains(thisTable))
				return true;
			else
				return false;
		}else if(thisTable.contains("vt") && this.route_vt != null) {
			if(this.route_vt.contains(thisTable))
				return true;
			else
				return false;
		}else
			return false;
	}
	
	public String getAnswerOfCertainQuestion(String thispage, String question) {
		if(thispage.equals("general")) {
			if(route_gen != null) 
				return info_gen.get(question);
			else
				return "NA";
		}else if(thispage.equals("fall")) {
			if(route_fall != null)
				return info_fall.get(question);
			else
				return "NA";
		}else if(thispage.equals("pu")) {
			if(route_pu != null)
				return info_pu.get(question);
			else
				return "NA";
		}else
			return "NA";
	}
	
	public boolean whetherReviewable() {
		if(this.general && this.route_gen == null)
			this.review = false;
		else if(this.bbp && this.route_bbp == null)
			this.review = false;
		else if(this.hit && this.route_hit == null)
			this.review = false;
		else if(this.fall && this.route_fall == null)
			this.review = false;
		else if(this.hai && this.route_hai == null)
			this.review = false;
		else if(this.mos && this.route_mos == null)
			this.review = false;
		else if(this.per && this.route_per == null)
			this.review = false;
		else if(this.pu && this.route_pu == null)
			this.review = false;
		else if(this.sa && this.route_sa == null)
			this.review = false;
		else if(this.vt && this.route_vt == null)
			this.review = false;
		return this.review;				
	}
	
	public String getShown(String nextq, ArrayList<String> route) {
		if(route == null)
			return nextq;
		
		String beShow;
		if(route.contains(nextq)) {
			beShow = nextq;
			for(int i=route.indexOf(nextq)+1; i<route.size(); i++) {
				beShow = beShow + ";" + route.get(i);
			}
			return beShow;
		}else
			return nextq;			
	}	
	
	public void createRouteAndInfo(String formName) {
		if(formName.equals("general")) {
			this.route_gen = new ArrayList<String>();
			this.info_gen = new HashMap<String, String>();
		}
		if(formName.equals("bbp")) {
			this.route_bbp = new ArrayList<String>();
			this.info_bbp = new HashMap<String, String>();
		}
		if(formName.equals("hit")) {
			this.route_hit = new ArrayList<String>();
			this.info_hit = new HashMap<String, String>();
		}
		if(formName.equals("fall")) {
			this.route_fall = new ArrayList<String>();
			this.info_fall = new HashMap<String, String>();
		}
		if(formName.equals("hai")) {
			this.route_hai = new ArrayList<String>();
			this.info_hai = new HashMap<String, String>();
		}
		if(formName.equals("mos")) {
			this.route_mos = new ArrayList<String>();
			this.info_mos = new HashMap<String, String>();
		}
		if(formName.equals("per")) {
			this.route_per = new ArrayList<String>();
			this.info_per = new HashMap<String, String>();
		}
		if(formName.equals("pu")) {
			this.route_pu = new ArrayList<String>();
			this.info_pu = new HashMap<String, String>();
		}
		if(formName.equals("sa")) {
			this.route_sa = new ArrayList<String>();
			this.info_sa = new HashMap<String, String>();
		}
		if(formName.equals("vt")) {
			this.route_vt = new ArrayList<String>();
			this.info_vt = new HashMap<String, String>();
		}
	}
	
	public void putRouteAndInfo(String formName, String key, String value) {
		if(formName.equals("general")) {
			this.route_gen.add(key);
			this.info_gen.put(key, value);
		}
		if(formName.equals("bbp")) {
			this.route_bbp.add(key);
			this.info_bbp.put(key, value);
		}
		if(formName.equals("hit")) {
			this.route_hit.add(key);
			this.info_hit.put(key, value);
		}
		if(formName.equals("fall")) {
			this.route_fall.add(key);
			this.info_fall.put(key, value);
		}
		if(formName.equals("hai")) {
			this.route_hai.add(key);
			this.info_hai.put(key, value);
		}
		if(formName.equals("mos")) {
			this.route_mos.add(key);
			this.info_mos.put(key, value);
		}
		if(formName.equals("per")) {
			this.route_per.add(key);
			this.info_per.put(key, value);
		}
		if(formName.equals("pu")) {
			this.route_pu.add(key);
			this.info_pu.put(key, value);
		}
		if(formName.equals("sa")) {
			this.route_sa.add(key);
			this.info_sa.put(key, value);
		}
		if(formName.equals("vt")) {
			this.route_vt.add(key);
			this.info_vt.put(key, value);
		}
	}
	
	public void setCertainFormTrueOrFalse(String formName, boolean TF) {
		if(formName.equals("general"))
			this.general = TF;
		if(formName.equals("bbp"))
			this.bbp = TF;
		if(formName.equals("hit"))
			this.hit = TF;
		if(formName.equals("fall"))
			this.fall = TF;
		if(formName.equals("hai"))
			this.hai = TF;
		if(formName.equals("mos"))
			this.mos = TF;
		if(formName.equals("per"))
			this.per = TF;
		if(formName.equals("pu"))
			this.pu = TF;
		if(formName.equals("sa"))
			this.sa = TF;
		if(formName.equals("vt"))
			this.vt = TF;
	}
	
	public String getContinuePosition() {
		if(this.general == true && route_gen == null)
			return "general";
		else if(this.bbp == true && route_bbp == null)
			return "bbp";
		else if(this.hit == true && route_hit == null)
			return "hit";
		else if(this.fall == true && route_fall == null)
			return "fall";
		else if(this.hai == true && route_hai == null)
			return "hai";
		else if(this.mos == true && route_mos == null)
			return "mos";
		else if(this.per == true && route_per == null)
			return "per";
		else if(this.pu == true && route_pu == null)
			return "pu";
		else if(this.sa == true && route_sa == null)
			return "sa";
		else if(this.vt == true && route_vt == null)
			return "vt";
		else
			return "review";
	}
}
