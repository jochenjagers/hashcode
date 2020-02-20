package gingerninjas.qualification;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import gingerninjas.BaseOutput;

public class Output extends BaseOutput
{
	private Input				input;
	private List<Library>		libraries;
	private int 				daysUsed = 0;
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
		r.write(Integer.toString(libraries.size()) + "\n");
		
		Iterator<Library> i = libraries.iterator();
		while(i.hasNext())
		{
			Library lib = i.next();
			r.write(lib.getId() + " " + lib.getScannedBooks().size() + "\n");
			String line = "";
			for(Book b : lib.getScannedBooks()) {
				line += b.getId() + " ";
			}
			r.write(line.trim() + "\n");
		}
		r.flush();
	}

	@Override
	protected void parse(BufferedReader reader) throws IOException
	{
		/*
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
		*/
	}
	
	@Override
	public double getScore()
	{
		this.calcScore();
		return super.getScore();
	}

	public boolean addLibrary(Library lib)
	{
		if(libraries.contains(lib)) {
			logger.warn("Library " + lib.getId() + " already in result set");
			return false;
		}
		if(this.daysUsed + lib.getSignupTime() > input.getDaysForScanning()) {
			logger.warn("Library can not be added because out of time.");
			return false;
		}
		return libraries.add(lib);
	}
	/*
	public void addSlides(List<Slide> slides)
	{
		for(Slide s: slides)
		{
			addSlide(s);
		}
	}
	 */
	public void calcScore()
	{
		HashSet<Book> books = new HashSet<Book>();
		int day = 0;
		int maxDays = input.getDaysForScanning();

		for(Library lib : libraries) {
			day += lib.getSignupTime();
			int remainingDays = maxDays - day;
			int scannedBooks = remainingDays * lib.getBooksPerDay();
			if(lib.getScannedBooks().size() > scannedBooks) {
				logger.warn("Scanned more books than time availibe in library " + lib.toString());
			}
			int maxBooks = Math.min(scannedBooks, lib.getScannedBooks().size());
			for(int i = 0; i < maxBooks; ++i) {
				if(!books.add(lib.getScannedBooks().get(i))) {
					logger.warn("Book " + lib.getScannedBooks().get(i) + " doube scanned!");
				}
			}
		}
		
		int totalScore = 0;
		for(Book b : books) {
			totalScore += b.getScore();
		}
		this.score = totalScore;
	}
	
	public void reset()
	{
		this.libraries = new ArrayList<>();
	}
	
	public static void main(String[] args) throws IOException
	{
		/*
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
		*/
	}
}
