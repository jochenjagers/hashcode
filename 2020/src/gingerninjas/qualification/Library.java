package gingerninjas.qualification;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Library
{
	private int			id;
	private int			signupTime;
	private int			booksPerDay;
	private List<Book>	books;
	private List<Book>	scannedBooks;
	private double		maxScore;
	private long		duration;
	private double 		rating;

	public Library(int id, int signupTime, int booksPerDay)
	{
		super();
		this.id = id;
		this.signupTime = signupTime;
		this.booksPerDay = booksPerDay;
		this.books = new ArrayList<Book>();
		this.scannedBooks = new ArrayList<Book>();
		
	}

	public int getId() {
		return id;
	}

	public void sortBooksByScore() {
		books.sort(new Comparator<Book>() {
			@Override
			public int compare(Book o1, Book o2) {
				long delta = o2.getScore() - o1.getScore();
				if (delta > 0)
					return 1;
				else if (delta < 0)
					return -1;
				else
					return 0;
			}
		});
	}

	public int getSignupTime() {
		return signupTime;
	}

	public int getBooksPerDay() {
		return booksPerDay;
	}

	public List<Book> getBooks() {
		return books;
	}

	public List<Book> getScannedBooks() {
		return scannedBooks;
	}

	public double getMaxScore() {
		return maxScore;
	}

	public void setMaxScore(long maxScore) {
		this.maxScore = maxScore;
	}

	public boolean addBook(Book book) {
		book.addLibrary(this);
		return this.books.add(book);
	}

	public boolean scanBook(Book book) {
		if (book.isScanned())
			return false;

		book.setScanned(true);
		return this.scannedBooks.add(book);
	}

	public double getRating()
	{
		return rating;
	}

	public void setRating(double rating)
	{
		this.rating = rating;
	}

	public long getDuration() {
		return duration;
	}

	public void updateDurationAndScore() {
		int score = 0;
		for (Book b : this.getBooks()) {
			if (b.isScanned())
				continue;
			score += b.getScore();
		}
		this.maxScore = (double) score;

		this.duration = (int) Math.ceil(this.getBooks().size() / (double) this.booksPerDay) + this.signupTime;
	}

	public long getPossibleScore(int remainingDays) {
		long result = 0;
		remainingDays -= this.signupTime;
		long capacity = remainingDays * booksPerDay;
		int i = 0;
		while (capacity > 0 && i < this.books.size()) {
			if (!this.books.get(i).isScanned()) {
				result += this.books.get(i).getScore();
				capacity--;
			}
			i++;
		}
		return result;
	}
	
	public void calcFreeTime(int remainingDays) {
		
	}

	public long getMaxScannedBooks(int remainingDays) {
		remainingDays -= this.signupTime;
		long capacity = remainingDays * booksPerDay;
		long books = 0;
		for (Book b : this.getBooks()) {
			if (!b.isScanned()) {
				books++;
			}
		}
		return Math.min(capacity, books);
	}

	public void reset() {
		this.scannedBooks = new ArrayList<Book>();
		for (Book b : this.books) {
			b.setScanned(false);
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Library other = (Library) obj;
		if (id != other.id)
			return false;
		return true;
	}
	
	public long getRemaining() {
		int scanned = 0;
		for(Book b : this.books) {
			if(b.isScanned()) {
				scanned++;
			}
		}
		return (this.books.size()-scanned);
	}

	@Override
	public String toString() {
		
		return "Library [id=" + id + ", #booksPerDay=" + booksPerDay + ", #books=" + books.size() + ", #scanned="
				+ scannedBooks.size() + ", remaining= "+this.getRemaining()+", maxScore=" + this.maxScore + ", " + "signupTime="+this.signupTime+"]";
	}
}
