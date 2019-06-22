package gingerninjas.qualification;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Slide
{
	private List<Photo> photos; 

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
		List<String> tags = new ArrayList<>();
		System.out.println("---" + new Slide(new Photo(1, 'v', tags), new Photo(2, 'v', tags)) + "---");
		System.out.println("---" + new Slide(new Photo(3, 'h', tags)) + "---");
	}
}
