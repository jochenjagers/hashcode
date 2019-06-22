package gingerninjas.qualification;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
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
		line = reader.readLine();
		
		
		String[] splittedLine;
		int photoId;
		List<Photo> photos;
		while(line != null)
		{
			splittedLine = line.split(" ");
			photos = new ArrayList<>(2);
			for(String idS: splittedLine)
			{
				photoId = Integer.parseInt(idS);
				photos.add(this.input.getPhotos().get(photoId));
			}
			slides.add(new Slide(photos));
		}
	}
	
	@Override
	public double getScore()
	{
		this.calcScore();
		return super.getScore();
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
	}
}
