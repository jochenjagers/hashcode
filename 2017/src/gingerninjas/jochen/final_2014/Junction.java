package gingerninjas.jochen.final_2014;

import java.util.ArrayList;

public class Junction
{
	int name;
	ArrayList<Street> outgoing = new ArrayList<>();
	
	public Junction(int name)
	{
		this.name = name;
	}
}
