package gingerninjas.qualification;

import java.util.ArrayList;
import java.util.List;

public class Library
{
	private int			id;
	private int			signupTime;
	private int			booksPerDay;
	private List<Book>	books;
	private List<Book>	scannedBooks;
	
	public Library(int id, int signupTime, int booksPerDay)
	{
		super();
		this.id = id;
		this.signupTime = signupTime;
		this.booksPerDay = booksPerDay;
		this.books = new ArrayList<Book>();
		this.scannedBooks = new ArrayList<Book>();
	}

	public int getId()
	{
		return id;
	}

	public int getSignupTime()
	{
		return signupTime;
	}

	public int getBooksPerDay()
	{
		return booksPerDay;
	}

	public List<Book> getBooks()
	{
		return books;
	}
	
	public List<Book> getScannedBooks()
	{
		return scannedBooks;
	}

	public boolean addBook(Book book)
	{
		return this.books.add(book);
	}

	public boolean scanBook(Book book)
	{
		if(book.isScanned())
			return false;
		
		book.setScanned(true);
		return this.scannedBooks.add(book);
	}
	
	public void reset()
	{
		this.scannedBooks = new ArrayList<Book>();
		for(Book b: this.books)
		{
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

	@Override
	public String toString()
	{
		return "Library [id=" + id + ", #booksPerDay=" + booksPerDay + ", #books=" + books.size()  + ", #scanned=" + scannedBooks.size() + "]";
	}
}
