package gingerninjas.jochen.pizza;

import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Pizza
{
	protected transient final Logger	logger			= LogManager.getLogger(getClass());
	ArrayList<Slice>					slices			= new ArrayList<Slice>();
	char[][]							topping;
	boolean[][]							used;
	int									maxSize			= 0;
	int									toppingCount	= 0;

	int									width;
	int									height;

	public void reset()
	{
		slices.clear();
		used = new boolean[height][width];
		for(int y = 0; y < height; ++y)
		{
			for(int x = 0; x < width; ++x)
			{
				used[y][x] = false;
			}
		}

		if(this.topping.length > 0)
		{
			slices.add(new Slice(0, 0, this.topping[0].length, this.topping.length, this));
		}
	}

	public int addSlice(Slice s)
	{
		int result = s.getPoints();
		Slice n = new Slice(s);
		this.slices.add(n);
		n.onPizza = true;
		for(int y = s.y; y < s.y + s.height; ++y)
		{
			for(int x = s.x; x < s.x + s.width; ++x)
			{
				used[y][x] = true;
			}
		}
		return result;
	}

	public void createSlices()
	{
		boolean split = true;

		while(split)
		{
			split = false;
			for(int i = 0; i < slices.size(); ++i)
			{
				split |= slices.get(i).split(this);
			}
		}
		// for(Slice s : slices)
		// {
		// logger.info(s);
		// }
	}

	public int getMaxPoints()
	{
		if(this.topping.length > 0)
		{
			return this.topping.length * this.topping[0].length;
		}
		return 0;
	}

	public int getPoints()
	{
		int result = 0;
		for(Slice s : slices)
		{
			result += s.getPoints();
		}
		return result;
	}
}
