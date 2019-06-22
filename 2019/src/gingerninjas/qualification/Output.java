package gingerninjas.qualification;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import gingerninjas.BaseOutput;

public class Output extends BaseOutput
{
	private Input				input;
	private List<Slide>			slides;

	public void init(Input input)
	{
		this.input = input;
		this.reset();
		
	}

	public Output(File path, String name, boolean load) throws IOException
	{
		super(path, name, load);
	}

	@Override
	protected void write(BufferedWriter r) throws IOException
	{
		r.write(slides.size());
		r.write("\n");
		
		Iterator<Slide> i = slides.iterator();
		while(i.hasNext())
		{
			r.write(i.next().toString());
			if(i.hasNext())
				r.write(' ');
		}
		r.flush();
	}

	@Override
	protected void parse(BufferedReader reader) throws IOException
	{
		String line = null;

		line = reader.readLine(); // omit first line
		
		String[] splittedLine;
		int photoId;
		List<Photo> photos;

		line = reader.readLine();
		while(line != null)
		{
			splittedLine = line.split(" ");
			photos = new ArrayList<>(2);
			for(String idS: splittedLine)
			{
				photoId = Integer.parseInt(idS);
				photos.add(this.input.getPhotos().get(photoId));
			}
			this.slides.add(new Slide(photos, true));
		}
	}
	
	@Override
	public double getScore()
	{
		this.calcScore();
		return super.getScore();
	}

	public List<Slide> getSlides()
	{
		return slides;
	}
	
	public boolean addSlide(Slide slide)
	{
		if(slide.isUsed())
			return false;
		slide.setUsed(true);
		return this.slides.add(slide);
	}
	
	public void addSlides(List<Slide> slides)
	{
		for(Slide s: slides)
		{
			addSlide(s);
		}
	}

	public void calcScore()
	{
		int totalScore = 0;
		Slide previous = null;
		for(Slide s: this.slides)
		{
			if(previous != null)
			{
				totalScore += s.calcInterestFactor(previous);
			}
			previous = s;
		}
		this.score = totalScore;
	}
	
	public void reset()
	{
		this.slides = new ArrayList<>(this.input.getPhotos().size());
		for(Photo p: input.getPhotos())
			p.setUsed(false);
	}
	
	public static void main(String[] args) throws IOException
	{
		Output o = new Output(new File("."), "o", false);
		o.init(new Input(new File("Online Qualification Round/"), "A - Example"));
		
		Slide[] slides = new Slide[] {
			new Slide(o.input.getPhotos().get(0)),
			new Slide(o.input.getPhotos().get(3)),
			new Slide(o.input.getPhotos().get(1), o.input.getPhotos().get(2)),
		};
		
		o.slides = Arrays.asList(slides);
		System.out.println(o.getScore());
		
		Collections.shuffle(o.slides);
		System.out.println(o.getScore());
		
		Collections.shuffle(o.slides);
		System.out.println(o.getScore());
		
		Collections.shuffle(o.slides);
		System.out.println(o.getScore());
		
		Collections.shuffle(o.slides);
		System.out.println(o.getScore());
	}
}
