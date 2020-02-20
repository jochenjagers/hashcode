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
		book.setScanned(true);
		return this.scannedBooks.add(book);
	}
}
