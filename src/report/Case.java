package report;

import java.util.ArrayList;
import java.util.HashMap;

public class Case {
	public ArrayList<String> route = new ArrayList<String>();
	
	public HashMap<String, String> info = new HashMap<String, String>();
	
	boolean fall = false;
	public HashMap<String, String> fall_info = new HashMap<String, String>();
	
	
	public Case() {}
	
	//delete every element after q
	public void refresh(String q) {
		if(this.route.contains(q)) {
			int index = this.route.indexOf(q);
			if(index != (this.route.size() - 1))
				for(int i=index+1; this.route.size()>i;)
					this.route.remove(i);
		}else
			route.add(q);
	}
	
	public String getPreRoute() {
		if(this.route.size() ==1)
			return "new";
		else
			return this.route.get(this.route.size() - 2);
	}
	
	public static void main(String[] args) {
		Case c = new Case();
		c.route.add("aaa");
		c.route.add("bbb");
		c.route.add("ccc");
		c.route.add("ddd");
		c.route.add("eee");
		c.route.add("fff");
		
		c.refresh("aaa");
		
		System.out.println(c.route);
		System.out.println(c.getPreRoute());
	}
}
