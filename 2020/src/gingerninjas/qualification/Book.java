package gingerninjas.qualification;

import java.util.ArrayList;
import java.util.List;

public class Book
{
	private int		id;
	private int		score;
	private boolean	scanned;
	private List<Library> libraries = new ArrayList<Library>();
	
	public Book(int id, int score)
	{
		this.id = id;
		this.score = score;
		this.scanned = false;
	}
	
	public int getId()
	{
		return id;
	}

	public int getScore()
	{
		return score;
	}

	public boolean isScanned()
	{
		return scanned;
	}

	public void addLibrary(Library library) {
		this.libraries.add(library);
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
		Book other = (Book) obj;
		if (id != other.id)
			return false;
		return true;
	}

	public void setScanned(boolean scanned)
	{
		this.scanned = scanned;
		for(Library lib : this.libraries) {
			lib.updateDurationAndScore();
		}
	}

	@Override
	public String toString()
	{
		return "Book [id=" + id + ", score=" + score + "]";
	}
}
