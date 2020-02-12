package gingerninjas.qualification;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class Slide
{
	private List<Photo>	 photos;
	private Set<Integer> tags;
	private boolean		 used;

	public Slide(List<Photo> photos, boolean markUsed)
	{
		super();
		this.photos = photos;
		
		if(markUsed)
		{
			for(Photo p: this.photos)
				p.setUsed(used);
		}	
	}

	public Slide(Photo photo)
	{
		this(photo, true);
	}

	public Slide(Photo photo, boolean markUsed)
	{
		if(photo.isVertical())
			throw new IllegalArgumentException("single photo must be horizontal");
		if(photo.isUsed())
			throw new IllegalArgumentException("single photo already in use");
			

		this.photos = new ArrayList<>();
		this.photos.add(photo);
		
		if(markUsed)
			photo.setUsed(true);
	}

	public Slide(Photo photo1, Photo photo2)
	{
		this(photo1, photo2, true);
	}
	
	public Slide(Photo photo1, Photo photo2, boolean markUsed)
	{
		if(photo1.isHorizontal())
			throw new IllegalArgumentException("photo 1 must be vertical");
		if(photo2.isHorizontal())
			throw new IllegalArgumentException("photo 2 must be vertical");
		if(photo1.isUsed())
			throw new IllegalArgumentException("photo 1 already in use");
		if(photo2.isUsed())
			throw new IllegalArgumentException("photo 2 already in use");

		this.photos = new ArrayList<>();
		this.photos.add(photo1);
		this.photos.add(photo2);

		if(markUsed)
		{
		photo1.setUsed(true);
		photo2.setUsed(true);
		}
	}

	public List<Photo> getPhotos()
	{
		return photos;
	}

	public int getMaxScore() {
		return (int) Math.floor(this.getTags().size()/2);
	}
	
	public Set<Integer> getTags()
	{
		if(this.tags == null)
		{
			this.tags = new TreeSet<>();
			for(Photo p : this.photos)
				this.tags.addAll(p.getTags());
		}
		return this.tags;
	}

	public boolean isUsed()
	{
		return used;
	}

	public void setUsed(boolean used)
	{
		this.used = used;
		for(Photo p: this.photos)
			p.setUsed(used);
	}

	public int calcInterestFactor(Slide other)
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
		return Math.min(common, Math.min(unique1, unique2));
	}

	public String toString()
	{
		StringBuffer buffer = new StringBuffer();
		Iterator<Photo> i = photos.iterator();
		while(i.hasNext())
		{
			buffer.append(i.next().getId());
			if(i.hasNext())
				buffer.append(' ');
		}
		return buffer.toString();
	}
}
