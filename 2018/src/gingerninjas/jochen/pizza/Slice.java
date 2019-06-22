package gingerninjas.jochen.pizza;

import java.io.Serializable;
import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import gingerninjas.util.Copyable;
import sun.misc.Signal;


public class Slice implements Comparable<Slice>, Copyable<Slice>, Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -7564948953479100843L;
	
	protected transient final Logger	logger			= LogManager.getLogger(getClass());
	public int									x;
	int									y;
	int									width;
	int									height;

	boolean								splitHorizontal	= true;
	boolean								overlap			= false;

	int									points			= -1;
	int									pizzaSliceCount	= -1;

	boolean								toppingValid;

	transient Pizza								pizza;
	transient Output								output;
	boolean								onPizza			= false;

	int									size;

	public Slice(int x, int y, int width, int height, Pizza pizza, Output output)
	{
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.pizza = pizza;
		if(pizza != null) {
		this.toppingValid = this.isToppingVaild();
		}
		this.output = output;
		this.size = this.width * this.height;
	}

	public Slice(Slice s)
	{
		this.x = s.x;
		this.y = s.y;
		this.width = s.width;
		this.height = s.height;
		this.toppingValid = s.toppingValid;
		this.pizza = s.pizza;
		this.output = s.output;
		this.size = s.size;
		this.points = s.points;
	}

	public void maximize()
	{
		boolean resized = true;
		int p1 = this.getPoints();
		while(resized)
		{
			int oldX = x;
			int oldY = y;
			int oldWidth = width;
			int oldHeight = height;
			int oldPoints = this.getPoints();
			resized = true;

			// Rechts
			this.points = -1;
			this.width = Math.min(this.width + 1, pizza.width - this.x);
			if(this.getPoints() > oldPoints)
			{
				continue;
			}
			this.width = oldWidth;

			// Links
			if(this.x > 0)
			{
				this.points = -1;
				this.width++;
				this.x--;
				if(this.getPoints() > oldPoints)
				{
					continue;
				}
				this.width = oldWidth;
				this.x = oldX;
			}
			
			// Unten
			this.points = -1;
			this.height = Math.min(this.height + 1, pizza.height- this.y);
			if(this.getPoints() > oldPoints)
			{
				continue;
			}
			this.height = oldHeight;

			// Open
			if(this.y > 0)
			{
				this.points = -1;
				this.height++;
				this.y--;
				if(this.getPoints() > oldPoints)
				{
					continue;
				}
				this.height = oldHeight;
				this.y = oldY;
			}
			resized = false;
		}
		int p2 = this.getPoints();
		if(p1 != p2) {
			logger.info("MAXIMIZED: " + p1 + " - " + p2);
		}
	}

	public boolean pointOnSlice(int x, int y) {
		return(x >= this.x && x < this.x+this.width && y >= this.y && y < this.y + this.height);
	}
	
	@Override
	public boolean equals(Object obj)
	{
		Slice s = (Slice)obj;
		return (this.x == s.x && this.y == s.y && this.width == s.width && this.height == s.height);
	}

	public float getQuality(int dist) {
		int startY = Math.max(0, this.y-dist);
		int endY = Math.min(pizza.height, this.y + this.height + dist);
		int startX = Math.max(0, this.x-dist);
		int endX = Math.min(pizza.width, this.x + this.width + dist);
		int total = 0;
		float used = 0;
		//float middleX = x + (float)this.width/2;
		//float middleY = y + (float)this.height/2;
		for(int y = startY; y < endY; ++y) {
			for(int x = startX; x < endX; ++x) {
				++total;
				//double distance = Math.sqrt((middleX - x)*(middleX - x) + (middleY -y)*(middleY -y));
				//double normed = Math.min(1, distance / (dist + Math.max(this.width/2.0, this.height/2.0)))/2;
				//logger.info("normed: " + normed);
				//float factor = max(0, y-startY)
				if(pizza.used[y][x] == true) {
					used++;// += 1-normed;
				}
			}
		}
		return (float)used/(float)total;
	}
	
	public int getPoints()
	{

		if(this.points == 0)
		{
			return 0;
		}

		int result = 0;
		if(this.overlap || !this.toppingValid || (this.x + this.width > this.pizza.width) || (this.y + this.height > this.pizza.height)
				|| (this.size > this.pizza.maxSize))
		{
			// Fehlerfälle
			this.points = 0;
		}
		else if(this.pizzaSliceCount == this.output.slices.size() && this.points >= 0)
		{
			// Gecacheten Wert zurück geben
			result = this.points;
		}
		else
		{
			// wert neu berechnen
			// Wenn sich das Teil mit einem bereits vorhandenen überschneidet gibt es auch keine
			// punkte
			if(!this.onPizza)
			{
				for(int y = this.y; y < this.y + this.height; ++y)
				{
					for(int x = this.x; x < this.x + this.width; ++x)
						if(this.pizza.used[y][x])
						{
							
							this.overlap = true;
							return 0;
						}
				}
			}
			result = this.points = this.size;
			this.pizzaSliceCount = this.output.slices.size();

		}
		return result;

	}

	private boolean isToppingVaild()
	{
		HashMap<Character, Integer> toppings = this.countTopings();
		if(toppings.entrySet().size() < 2)
		{
			return false;
		}
		for(HashMap.Entry<Character, Integer> entry : toppings.entrySet())
		{
			if(entry.getValue() < this.pizza.toppingCount)
			{
				return false;
			}
		}
		return true;
	}

	private int searchSplitRow(Character t, Integer c)
	{
		int result = -1;

		int count = 0;
		int oldCount = 0;
		float oldVal = 0;
		for(int y = 0; y < this.height; ++y)
		{
			for(int x = 0; x < this.width; ++x)
			{
				if(pizza.topping[y + this.y][x + this.x] == t)
				{
					++count;
				}
			}
			float val = (float) count / c;
			if(val >= 0.5)
			{
				if(Math.abs(0.5 - oldVal) < Math.abs(0.5 - val))
				{
					// Vorgänger war näher an Mitte
					if(oldCount >= pizza.toppingCount && (c - oldCount) >= pizza.toppingCount)
					{
						// In der Mitte Schneiden. Es kann nur geschnitten werden, wenn für beide
						// Teile
						// noch genug toppin vorhanden ist
						result = y - 1;
						break;
					}
				}
				else
				{
					if(count >= pizza.toppingCount && (c - count) >= pizza.toppingCount)
					{
						// In der Mitte Schneiden. Es kann nur geschnitten werden, wenn für beide
						// Teile
						// noch genug toppin vorhanden ist
						result = y;
						break;
					}
				}
			}
			oldVal = val;
			oldCount = count;
		}

		return result;
	}

	private int searchSpitCol(Character t, Integer c)
	{
		int result = -1;

		int count = 0;
		int oldCount = 0;
		float oldVal = 0;
		for(int x = 0; x < this.width; ++x)
		{
			for(int y = 0; y < this.height; ++y)
			{
				if(pizza.topping[y + this.y][x + this.x] == t)
				{
					++count;
				}
			}
			float val = (float) count / c;
			if(val >= 0.5)
			{
				if(Math.abs(0.5 - oldVal) < Math.abs(0.5 - val))
				{
					// Vorgänger war näher an Mitte
					if(oldCount >= pizza.toppingCount && (c - oldCount) >= pizza.toppingCount)
					{
						// In der Mitte Schneiden. Es kann nur geschnitten werden, wenn für beide
						// Teile
						// noch genug toppin vorhanden ist
						result = x - 1;
						break;
					}
				}
				else
				{
					if(count >= pizza.toppingCount && (c - count) >= pizza.toppingCount)
					{
						// In der Mitte Schneiden. Es kann nur geschnitten werden, wenn für beide
						// Teile
						// noch genug toppin vorhanden ist
						result = x;
						break;
					}
				}
			}
			oldVal = val;
			oldCount = count;
		}

		return result;
	}

	private Slice split(Character splitTopping, Integer splitToppingCount)
	{
		Slice result = null;
		if(splitHorizontal)
		{
			int row = this.searchSplitRow(splitTopping, splitToppingCount);
			if(row > -1)
			{
				result = new Slice(x, y + row + 1, width, height - row - 1, this.pizza, this.output);
				result.splitHorizontal = false;
				this.height = row + 1;
			}
		}
		else
		{
			int col = this.searchSpitCol(splitTopping, splitToppingCount);
			if(col > -1)
			{
				result = new Slice(x + col + 1, y, width - col - 1, height, this.pizza, this.output);
				this.width = col + 1;
			}
		}
		splitHorizontal = !splitHorizontal;
		this.size = this.width * this.height;
		return result;
	}

	public Slice split()
	{
		// count toppings on slice
		HashMap<Character, Integer> toppingCounts = this.countTopings();

		// search for least significant topping
		Character splitTopping = ' ';
		Integer splitToppingCount = Integer.MAX_VALUE;
		for(HashMap.Entry<Character, Integer> entry : toppingCounts.entrySet())
		{
			if(entry.getValue() < splitToppingCount)
			{
				splitTopping = entry.getKey();
				splitToppingCount = entry.getValue();
			}
		}

		return this.split(splitTopping, splitToppingCount);
	}

	public HashMap<Character, Integer> countTopings()
	{
		HashMap<Character, Integer> toppingCounts = new HashMap<>();
		for(int x = 0; x < this.width; ++x)
		{
			for(int y = 0; y < this.height; ++y)
			{
				Integer count = toppingCounts.get(pizza.topping[y + this.y][x + this.x]);
				if(count == null)
				{
					count = new Integer(0);
				}
				count++;
				toppingCounts.put(pizza.topping[y + this.y][x + this.x], count);
			}
		}
		// toppingCounts.forEach((k, v) -> logger.info(k + " - " + v));
		return toppingCounts;
	}

	public String toString()
	{
		return "x: " + x + " y: " + y + " width: " + width + " height: " + height + " points: " + this.getPoints();
	}

	public boolean isOverlaping(Slice s)
	{
		boolean overlapx = (s.x < this.x && (s.x + s.width) > this.x) || (s.x < (this.x + this.width) && (s.x + s.width) > (this.x + this.width))
				|| (s.x > this.x && s.x < (this.x + this.width));
		boolean overlapy = (s.y < this.y && (s.y + s.height) > this.y)
				|| (s.y < (this.y + this.height) && (s.y + s.height) > (this.y + this.height) || (s.y > this.y && s.y < (this.y + this.height)));
		return overlapx && overlapy;
	}

	@Override
	public int compareTo(Slice o)
	{
		return this.getPoints() - o.getPoints();
	}

	@Override
	public Slice copy()
	{
		return new Slice(this);
	}

	public void invalidate()
	{
		this.points = -1;
	}
}
