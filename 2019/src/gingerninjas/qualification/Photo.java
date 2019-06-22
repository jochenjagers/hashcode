package gingerninjas.qualification;

import java.util.List;

public class Photo
{
	private int				id;
	private char			orientation;
	private List<String>	tags;

	public Photo(int id, char orientation, List<String> tags)
	{
		this.id = id;
		this.orientation = Character.toUpperCase(orientation);
		this.tags = tags;
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
	
	public String toString() {
		return this.id + ": " + this.orientation + " Tags: " + String.join(", ", this.tags);
	}
}
