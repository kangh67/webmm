package search;

public class SingleCase implements Comparable<SingleCase>{
	public String id;
	public Double score;
	public String name;
	public String author;
	public String date;
	
	public SingleCase(String id, Double score) {
		this.id = id;
		this.score = score;
	}
	
	public int compareTo(SingleCase sc) {
		if(this.score < sc.score) 
			return 1;
		else if(this.score > sc.score)
			return -1;
		else 
			return - this.id.toLowerCase().compareTo(sc.id.toLowerCase());
	}
}
