package gingerninjas.qualification;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class Photo
{
	private int				id;
	private char			orientation;
	private HashSet<Integer>	tags;
	private boolean			used;

	private static  HashMap<String, Integer> tagMap = new HashMap<String, Integer>();
	
	public Photo(int id, char orientation, List<String> tags)
	{
		this.id = id;
		this.orientation = Character.toUpperCase(orientation);
		this.used = false;
		this.setTags(tags);
		
	}

	public Photo(int id, char orientation, String... tags)
	{
		this.id = id;
		this.orientation = Character.toUpperCase(orientation);
		this.setTags(Arrays.asList(tags));
		this.used = false;
	}

	private void setTags(Collection<String> tags) {
		this.tags = new HashSet<Integer>();
		synchronized (tagMap) {
			for(String t : tags) {
				if(!tagMap.containsKey(t)) {
					tagMap.put(t, tagMap.size());
				}
				this.tags.add(tagMap.get(t));
			}
		}
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

	public HashSet<Integer> getTags()
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
		for(Integer tag1 : this.getTags())
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
		return this.id + ": " + this.orientation + " Tags: " + this.tags.toString();
	}
}
