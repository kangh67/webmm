package report;

public class CaseForRank implements Comparable<CaseForRank>{
	public String id;
	public String name;
	public Double score;	
	
	public CaseForRank(String id, String name, Double score) {
		this.id = id;
		this.name = name;
		this.score = score;
	}
	
	public int compareTo(CaseForRank sc) {
		if(this.score < sc.score) 
			return 1;
		else if(this.score > sc.score)
			return -1;
		else 
			return - this.id.toLowerCase().compareTo(sc.id.toLowerCase());
	}
}
