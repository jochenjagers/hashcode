package gingerninjas.qualification;

public class Book
{
	private int		id;
	private int		score;
	private boolean	scanned;

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
	}

	@Override
	public String toString()
	{
		return "Book [id=" + id + ", score=" + score + "]";
	}
}
