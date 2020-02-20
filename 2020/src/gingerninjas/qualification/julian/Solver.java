package gingerninjas.qualification.julian;

import java.util.Comparator;
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
		
		quickAndDirty();
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
