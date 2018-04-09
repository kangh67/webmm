package test;

public class SingleCase implements Comparable<SingleCase>{
	public String name;
	public Double score;
	
	public SingleCase(String name, Double score) {
		this.name = name;
		this.score = score;
	}
	
	public int compareTo(SingleCase sc) {
		if(this.score < sc.score) 
			return 1;
		else if(this.score > sc.score)
			return -1;
		else 
			return - this.name.toLowerCase().compareTo(sc.name.toLowerCase());
	}
}
