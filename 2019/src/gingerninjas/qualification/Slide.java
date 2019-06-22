package gingerninjas.qualification;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class Slide
{
	private List<Photo> photos; 
	private Set<String> tags;

	public Slide(List<Photo> photos)
	{
		super();
		this.photos = photos;
	}
	
	public Slide(Photo photo)
	{
		if(photo.isVertical())
			throw new IllegalArgumentException("single photo must be horizontal");
		
		this.photos = new ArrayList<>();
		this.photos.add(photo);
	}
	
	public Slide(Photo photo1, Photo photo2)
	{
		if(photo1.isHorizontal())
			throw new IllegalArgumentException("photo 1 must be vertical");
		if(photo2.isHorizontal())
			throw new IllegalArgumentException("photo 2 must be vertical");
		
		this.photos = new ArrayList<>();
		this.photos.add(photo1);
		this.photos.add(photo2);
	}

	public List<Photo> getPhotos()
	{
		return photos;
	}
	
	public Set<String> getTags()
	{
		if(this.tags == null)
		{
			this.tags = new TreeSet<>();
			for(Photo p: this.photos)
				this.tags.addAll(p.getTags());
		}
		return this.tags;
	}
	
	public int calcInterestFactor(Slide other)
	{
		int common = 0;
		int unique1 = 0;
		int unique2 = 0;
		for(String tag1: this.tags)
		{
			if(other.tags.contains(tag1))
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
	
	public static void main(String[] args)
	{
		Slide s1 = new Slide(new Photo(1, 'v', "a", "b", "c"), new Photo(2, 'v', "b", "c", "d"));
		Slide s2 = new Slide(new Photo(3, 'h', "a", "b", "c", "d"));
		Slide s3 = new Slide(new Photo(4, 'v', "a", "b"), new Photo(5, 'v', "c", "d"));
		Slide s4 = new Slide(new Photo(6, 'h', "a", "b"));
		Slide s5 = new Slide(new Photo(7, 'h', "c", "d"));
		Slide s6 = new Slide(new Photo(8, 'h', "e"));
		
		System.out.println("---" + s1 + "--- " + s1.getTags());
		System.out.println("---" + s2 + "--- " + s2.getTags());
		System.out.println("---" + s3 + "--- " + s3.getTags());
		System.out.println("---" + s4 + "--- " + s4.getTags());
		System.out.println("---" + s5 + "--- " + s5.getTags());
		System.out.println("---" + s6 + "--- " + s6.getTags());
	}
}
