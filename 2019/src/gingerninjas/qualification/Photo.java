package gingerninjas.qualification;

import java.util.Arrays;
import java.util.List;

public class Photo
{
	private int				id;
	private char			orientation;
	private List<String>	tags;
	private boolean			used;

	public Photo(int id, char orientation, List<String> tags)
	{
		this.id = id;
		this.orientation = Character.toUpperCase(orientation);
		this.tags = tags;
		this.used = false;
	}

	public Photo(int id, char orientation, String... tags)
	{
		this.id = id;
		this.orientation = Character.toUpperCase(orientation);
		this.tags = Arrays.asList(tags);
		this.used = false;
	}

	public int getId()
	{
		return id;
	}

	public char getOrientation()
	{
		return orientation;
	}

	public boolean isVertical()
	{
		return orientation == 'V';
	}

	public boolean isHorizontal()
	{
		return orientation == 'H';
	}

	public List<String> getTags()
	{
		return tags;
	}

	public boolean isUsed()
	{
		return used;
	}
	
	public void setUsed(boolean used)
	{
		this.used = used;
	}

	public int compareTags(Photo other)
	{
		int common = 0;
		int unique1 = 0;
		int unique2 = 0;
		for(String tag1 : this.getTags())
		{
			if(other.getTags().contains(tag1))
				common++;
			else
				unique1++;
		}
		unique2 = other.tags.size() - common;
		return common;
	}

	public String toString()
	{
		return this.id + ": " + this.orientation + " Tags: " + String.join(", ", this.tags);
	}
}
