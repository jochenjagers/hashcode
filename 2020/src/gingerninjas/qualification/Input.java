package gingerninjas.qualification;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;

import gingerninjas.BaseInput;

public class Input extends BaseInput
{
	private List<Book>		books;
	private List<Library>	libraries;
	private int				daysForScanning;

	public Input(File path, String name) throws IOException
	{
		super(path, name);
	}

	public List<Book> getBooks()
	{
		return books;
	}

	public List<Library> getLibraries()
	{
		return libraries;
	}

	public int getDaysForScanning()
	{
		return daysForScanning;
	}

	@Override
	protected void parse(BufferedReader reader) throws IOException
	{
		String line = null;
		String[] splitted;

		// read first line
		line = reader.readLine();
		splitted = line.split(" ");

		int numberOfBooks = Integer.parseInt(splitted[0]);
		int numberOfLibraries = Integer.parseInt(splitted[1]);
		this.daysForScanning = Integer.parseInt(splitted[2]);
		
		this.books = new ArrayList<Book>(numberOfBooks);
		this.libraries = new ArrayList<Library>(numberOfLibraries);

		// read book scores
		line = reader.readLine();
		splitted = line.split(" ");

		int maxScore = 0;
		int score;
		for(int b = 0; b < numberOfBooks; b++)
		{
			score = Integer.parseInt(splitted[b]);
			books.add(new Book(b,score));
			maxScore += score;
		}		
		
		this.setMaxScore(maxScore);
		
		// read libraries
		Library lib;
		int numberOfBooksInLib;
		int signupTime;
		int booksPerDay;
		int bookId;
		for(int l = 0; l < numberOfLibraries; l++)
		{
			// read Library info
			line = reader.readLine();
			splitted = line.split(" ");
			
			numberOfBooksInLib = Integer.parseInt(splitted[0]);
			signupTime = Integer.parseInt(splitted[1]);
			booksPerDay = Integer.parseInt(splitted[2]);
			
			lib = new Library(l, signupTime, booksPerDay);

			// read books
			line = reader.readLine();
			splitted = line.split(" ");
			
			for(int b = 0; b < numberOfBooksInLib; b++)
			{
				bookId = Integer.parseInt(splitted[b]);
					
				lib.addBook(this.books.get(bookId));
			}
			
			lib.updateDurationAndScore();
			this.libraries.add(lib);
		}
	}

	public void reset()
	{
		for(Library l : this.libraries)
			l.reset();
		for(Book b : this.books)
			b.setScanned(false);
	}

	@Override
	public String toString()
	{
		return "Input [#books=" + this.books.size() + ", #libraries=" + this.libraries.size() + ", #day=" + this.daysForScanning + "]";
	}

	public static void main(String[] args) throws IOException
	{
		Input test = new Input(new File("Online Qualification Round/"), "A - example");

		LogManager.getLogger().info("A - example: " + test.toString());
	}
}
