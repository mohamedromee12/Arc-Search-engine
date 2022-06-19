
public class rankObj {
	String url;
	String title;
	String disc;
	double weight = 1;  //intial value
	public void setURL (String s)
	{
		
	 this.url = s;
		
	}
	
	public void setTitle (String s)
	{
		
	 this.title = s;
		
	}
	
	public void setDisc (String s)
	{
		
	 this.disc = s;
		
	}
	
	public void addWeight (double w)
	{
		this.weight *= w;
	}
}
