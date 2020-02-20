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
