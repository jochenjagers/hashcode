package gingerninjas.jochen.final_2014;

import java.util.ArrayList;
import java.util.HashMap;

public class Street implements Comparable<Street>
{
	int					name;
	Junction			from;
	Junction			to;
	ArrayList<Junction>	inbetween	= new ArrayList<>();
	boolean				oneDir;
	int					cost;
	int					points;

	int					visits;
	HashMap<Integer, HashMap<Integer, Float>> cache;
	
	public Street(int name, Junction from, Junction to, boolean oneDir, int cost, int points)
	{
		this.name = name;
		this.from = from;
		this.to = to;
		this.oneDir = oneDir;
		this.cost = cost;
		this.points = points;

		this.visits = 0;
	}

	private float getQualityRecursiv(int depth, int penalty)
	{
		float result = 0;
		if(depth != 0)
		{
			for(int i = 0; i < this.to.outgoing.size(); i++)
			{
				result += this.to.outgoing.get(i).getQualityRecursiv(depth - 1, penalty);
			}
			result /= this.to.outgoing.size();
		}
		return result + ((visits > 0)?-visits*penalty:(float)this.points/(float)this.cost);
	}

	public float getQuality(int penalty, int depth)
	{
		return this.getQualityRecursiv(depth, penalty);
	}

	@Override
	public int compareTo(Street o)
	{
		return o.visits - this.visits;
	}

	public boolean isDeadEnd()
	{
		return (this.to.outgoing.size() == 1) && (this.to.outgoing.get(0).name == this.from.name);
	}
}
