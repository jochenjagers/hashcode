package gingerninjas.qualification.julian;

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
		
		for(Library l: input.getLibraries())
		{
			remaining -= l.getSignupTime();
			
			int capacity = l.getBooksPerDay() * remaining;
			int scanned = 0;
			if(output.addLibrary(l))
			{
				for(Book b: l.getBooks())
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
