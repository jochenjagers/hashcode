package gingerninjas.qualification.jochen;

import java.util.Comparator;
import java.util.List;
import java.util.ListIterator;

import gingerninjas.qualification.Book;
import gingerninjas.qualification.Library;
import gingerninjas.qualification.QualiSolver;

public class Solver extends QualiSolver {
	public static final double TARGET_SCORE = 0.8;
	public static final int MAX_TRIES = 100;

	@Override
	protected void solve() {
		input.reset();
		output.reset();

		quickAndDirty();
	}

	public void quickAndDirty() {
		//int remaining = input.getDaysForScanning();

		List<Library> libs = input.getLibraries();
		List<Book> books;

		for (Library l : libs) {
			l.sortBooksByScore();
		}
		boolean libAdded = true;
		int days = 0;
		while (days <= input.getDaysForScanning() && libs.size() > 0 && libAdded == true) {
			libAdded = false;
			logger.info("Days " + days);
			final int remaining = input.getDaysForScanning() - days;
			
			ListIterator<Library> iter = libs.listIterator();
			logger.info("Libs: " + libs.size());
			while(iter.hasNext()){
			    if(remaining - iter.next().getSignupTime() <= 0){
			        iter.remove();
			    }
			}
			if(libs.size() == 0) {
				break;
			}
			logger.info("Libs: " + libs.size());
			libs.sort(new Comparator<Library>() {
				@Override
				public int compare(Library o1, Library o2) {
					double delta = ((double)o2.getPossibleScore(remaining) / (o2.getSignupTime())) - ((double)o1.getPossibleScore(remaining) / (o1.getSignupTime()));
					if (delta > 0)
						return 1;
					else if (delta < 0)
						return -1;
					else
						return 0;
				}
			});
			int i = 0;
			Library l = libs.get(0);
			/*while(l.getSignupTime() > 210) {
				i++;
				l = libs.get(i);
			}*/
			
			//
			
			
			long capacity = l.getBooksPerDay() * ((long) remaining - l.getSignupTime());
			int scanned = 0;
			/* if(capacity > l.getBooks().size()) {
				logger.info("Skip Library because of too few books " + l.toString());
				libs.remove(0);
				continue;
			} */
			// logger.info("processing " + l + ": remaining=" + remaining + ", capacity=" +
			// capacity);
			
			if (output.addLibrary(l)) {
				logger.info("Add library " + l.getPossibleScore(remaining)+ " " + l.toString());
				books = l.getBooks();

				books.sort(new Comparator<Book>() {
					@Override
					public int compare(Book o1, Book o2) {
						return o2.getScore() - o1.getScore();
					}
				});

				for (Book b : books) {
					if (!b.isScanned() && scanned < capacity) {
						l.scanBook(b);
						scanned++;
					}
				}
				if (scanned == 0) {
					output.removeLibrary(l);
				} else {
					logger.info("Add library " + l.getPossibleScore(remaining)+ " " + l.toString());
					libAdded = true;
					days += l.getSignupTime();
				}
			}
			libs.remove(0);
		}
		/*
		days = 0;
		for(int i = 0; i < output.getLibraries().size() -1; ++i) {
			Library l1 = output.getLibraries().get(i);
			Library l2 = output.getLibraries().get(i+1);
			if(l1.getRemaining() > 0) {
			for(Book b : l1.getScannedBooks()) {
				b.setScanned(false);
			}
			l1.getScannedBooks().clear();
			for(Book b : l2.getScannedBooks()) {
				b.setScanned(false);
			}
			l2.getScannedBooks().clear();
			days += l2.getSignupTime();
			int scanned = 0;
			long capacity = l2.getBooksPerDay() * ((long) (input.getDaysForScanning()-days)- l2.getSignupTime());
			for (Book b : l2.getBooks()) {
				if (!b.isScanned() && scanned < capacity) {
					l2.scanBook(b);
					scanned++;
				}
			}
			days += l1.getSignupTime();
			scanned = 0;
			capacity = l1.getBooksPerDay() * ((long) (input.getDaysForScanning()-days)- l1.getSignupTime());
			for (Book b : l1.getBooks()) {
				if (!b.isScanned() && scanned < capacity) {
					l1.scanBook(b);
					scanned++;
				}
			}
			output.getLibraries().set(i+1, l1);
			output.getLibraries().set(i, l2);
			}
		}
		*/
	}
}
