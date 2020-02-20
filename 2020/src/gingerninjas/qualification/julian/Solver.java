package gingerninjas.qualification.julian;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import gingerninjas.qualification.Book;
import gingerninjas.qualification.Library;
import gingerninjas.qualification.QualiSolver;

public class Solver extends QualiSolver
{
	public static final double TARGET_SCORE = 0.8;
	public static final int MAX_TRIES = 100;
	
	@Override
	protected void solve()
	{
		input.reset();
		output.reset();
		
		b();
	}
	
	public void backwards()
	{
		LinkedList<Library> libsPlanned = new LinkedList<Library>();
		LinkedList<Library> libsAvailable = new LinkedList<Library>(input.getLibraries());

		for(Library l: libsAvailable)
		{
			l.sortBooksByScore();
		}

		Library lib;
		List<Book> books;
		
		while(libsAvailable.size() > 0)
		{
			for(Library l: libsAvailable)
			{
				l.updateDurationAndScore();
			}
			
			libsAvailable.sort(new Comparator<Library>() {
				@Override
				public int compare(Library o1, Library o2)
				{
					long delta = o2.getDuration() - o1.getDuration();
					if(delta > 0)
						return 1;
					else if(delta < 0)
						return -1;
					else 
						return 0;
				}
			});		
			
			lib = libsAvailable.pollFirst();
			logger.info("processing " + lib + ": duration=" + lib.getDuration());
			
			books = lib.getBooks();
			for(Book b: books)
			{
				b.setScanned(true);
				lib.getScannedBooks().add(b);
			}
			
			libsPlanned.addFirst(lib);
		}

		for(Library l: libsPlanned)
		{
			output.addLibrary(l);
		}
	}
	
	public void b()
	{
		int remaining = input.getDaysForScanning();
		
		List<Library> libs = input.getLibraries();
		List<Book> books;
		
		libs.sort(new Comparator<Library>() {
			@Override
			public int compare(Library o1, Library o2)
			{
				return o1.getSignupTime() - o2.getSignupTime();
			}
		});
		
		for(Library l: input.getLibraries())
		{
			remaining -= l.getSignupTime();
			if(remaining < 0)
				break;
			
			long capacity = l.getBooksPerDay() * (long) remaining;
			int scanned = 0;
			
			logger.info("processing " + l + ": remaining=" + remaining + ", capacity=" + capacity);
			
			if(output.addLibrary(l))
			{
				books = l.getBooks();
								
				for(Book b: books)
				{
					if(!b.isScanned() && scanned < capacity)
					{
						l.scanBook(b);
						scanned++;
					}
				}
			}
		}
	}
	
	public void quickAndDirty()
	{
		int remaining = input.getDaysForScanning();
		
		List<Library> libs = input.getLibraries();
		List<Book> books;
		
		libs.sort(new Comparator<Library>() {
			@Override
			public int compare(Library o1, Library o2)
			{
				long delta = o2.getMaxScore() - o1.getMaxScore();
				if(delta > 0)
					return 1;
				else if(delta < 0)
					return -1;
				else 
					return 0;
			}
		});
		
		for(Library l: input.getLibraries())
		{
			remaining -= l.getSignupTime();
			if(remaining < 0)
				break;
			
			long capacity = l.getBooksPerDay() * (long) remaining;
			int scanned = 0;
			
			logger.info("processing " + l + ": remaining=" + remaining + ", capacity=" + capacity);
			
			if(output.addLibrary(l))
			{
				books = l.getBooks();
				
				books.sort(new Comparator<Book>() {
					@Override
					public int compare(Book o1, Book o2)
					{
						return o2.getScore() - o1.getScore();
					}
				});			
				
				for(Book b: books)
				{
					if(!b.isScanned() && scanned < capacity)
					{
						l.scanBook(b);
						scanned++;
					}
				}
			}
		}
	}
}
