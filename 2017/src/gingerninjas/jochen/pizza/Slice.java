package gingerninjas.jochen.pizza;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sun.corba.se.impl.interceptors.SlotTable;

public class Slice
{
	protected transient final Logger	logger			= LogManager.getLogger(getClass());
	int									x;
	int									y;
	int									width;
	int									height;

	boolean								splitHorizontal	= true;
	boolean								overlap			= false;

	int									points			= -1;
	int									pizzaSliceCount	= -1;

	boolean								toppingValid;

	Pizza								pizza;
	boolean								onPizza			= false;

	int									size;

	public Slice(int x, int y, int width, int height, Pizza pizza)
	{
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.pizza = pizza;
		this.toppingValid = this.isToppingVaild();
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
		this.size = s.size;
	}

	public synchronized int getPoints()
	{

		if(this.points == 0) {
			return 0;
		}
		
		int result = 0;
		if(this.overlap || !this.toppingValid ||  (this.x + this.width > this.pizza.width) || (this.y + this.height > this.pizza.height)
				|| (this.size > this.pizza.maxSize)  )
		{
			// Fehlerfälle
			this.points = 0;
		}
		else if(this.pizzaSliceCount == this.pizza.slices.size())
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
			this.pizzaSliceCount = this.pizza.slices.size();
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

	private int searchSpitRow(Pizza p, Character t, Integer c)
	{
		int result = -1;

		int count = 0;
		int oldCount = 0;
		float oldVal = 0;
		for(int y = 0; y < this.height; ++y)
		{
			for(int x = 0; x < this.width; ++x)
			{
				if(p.topping[y + this.y][x + this.x] == t)
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
					if(oldCount >= p.toppingCount && (c - oldCount) >= p.toppingCount)
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
					if(count >= p.toppingCount && (c - count) >= p.toppingCount)
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

	private int searchSpitCol(Pizza p, Character t, Integer c)
	{
		int result = -1;

		int count = 0;
		int oldCount = 0;
		float oldVal = 0;
		for(int x = 0; x < this.width; ++x)
		{
			for(int y = 0; y < this.height; ++y)
			{
				if(p.topping[y + this.y][x + this.x] == t)
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
					if(oldCount >= p.toppingCount && (c - oldCount) >= p.toppingCount)
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
					if(count >= p.toppingCount && (c - count) >= p.toppingCount)
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

	private boolean split(Pizza pizza, Character splitTopping, Integer splitToppingCount)
	{
		boolean result = false;
		if(splitHorizontal)
		{
			int row = this.searchSpitRow(pizza, splitTopping, splitToppingCount);
			if(row > -1)
			{
				Slice newSlice = new Slice(x, y + row + 1, width, height - row - 1, this.pizza);
				newSlice.splitHorizontal = false;
				pizza.slices.add(newSlice);
				this.height = row + 1;
				result = true;
			}
		}
		else
		{
			int col = this.searchSpitCol(pizza, splitTopping, splitToppingCount);
			if(col > -1)
			{
				pizza.slices.add(new Slice(x + col + 1, y, width - col - 1, height, this.pizza));
				this.width = col + 1;
				result = true;
			}
		}
		splitHorizontal = !splitHorizontal;
		return result;
	}

	public boolean split(Pizza pizza)
	{
		boolean result = false;

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

		if((result = this.split(pizza, splitTopping, splitToppingCount)) == false)
		{
			result = this.split(pizza, splitTopping, splitToppingCount);
		}

		return result;
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
}
