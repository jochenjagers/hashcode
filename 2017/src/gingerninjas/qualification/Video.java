package gingerninjas.qualification;

import java.util.ArrayList;
import java.util.List;

public class Video
{
	protected int					id;
	protected int					size;

	protected ThreadLocal<Double>	valuability	= new ThreadLocal<>();

	protected List<CacheServer>		cachedBy;

	public Video(int id, int size)
	{
		super();
		this.id = id;
		this.size = size;
		this.cachedBy = new ArrayList<>();
	}

	public int getId()
	{
		return id;
	}

	public int getSize()
	{
		return size;
	}

	public double getValuability()
	{
		return valuability.get();
	}

	public void setValuability(double valuability)
	{
		this.valuability.set(valuability);
	}

	public List<CacheServer> getCachedBy()
	{
		return cachedBy;
	}

	public void reset()
	{
		this.valuability.set(0.0);
	}

	@Override
	public int hashCode()
	{
		return id;
	}

	@Override
	public boolean equals(Object obj)
	{
		if(this == obj)
			return true;
		if(obj == null)
			return false;
		if(getClass() != obj.getClass())
			return false;
		Video other = (Video) obj;
		if(id != other.id)
			return false;
		return true;
	}

	@Override
	public String toString()
	{
		return "Video: " + id + " " + size;
	}
}
