package gingerninjas.julian.pizza;

public class Slice
{
	private int		startRow;
	private int		startCol;
	private int		endRow;
	private int		endCol;
	private double	quality;

	public Slice(int startRow, int startCol, int endRow, int endCol)
	{
		super();
		this.startRow = startRow;
		this.startCol = startCol;
		this.endRow = endRow;
		this.endCol = endCol;
	}

	public int getStartRow()
	{
		return startRow;
	}

	public void setStartRow(int startRow)
	{
		this.startRow = startRow;
	}

	public int getStartCol()
	{
		return startCol;
	}

	public void setStartCol(int startCol)
	{
		this.startCol = startCol;
	}

	public int getEndRow()
	{
		return endRow;
	}

	public void setEndRow(int endRow)
	{
		this.endRow = endRow;
	}

	public int getEndCol()
	{
		return endCol;
	}

	public void setEndCol(int endCol)
	{
		this.endCol = endCol;
	}

	public double getQuality()
	{
		return quality;
	}

	public void setQuality(double quality)
	{
		this.quality = quality;
	}

	public int getSize()
	{
		return (endCol - startCol + 1) * (endRow - startRow + 1);
	}

	public int countTopping(Input input, char topping)
	{
		int toppingAmount = 0;

		for(int row = startRow; row <= endRow; row++)
		{
			for(int col = startCol; col <= endCol; col++)
			{
				if(input.getPizza()[row][col] == topping)
					toppingAmount++;
			}
		}

		return toppingAmount;
	}

	public boolean isOverlapping(Slice s)
	{
		return s.endCol >= this.startCol && s.startCol <= this.endCol && s.endRow >= this.startRow && s.startRow <= this.endRow;
	}

	public boolean isValid(Input input)
	{
		int numT = countTopping(input, 'T');
		int numM = countTopping(input, 'M');

		return numT >= input.getMinToppingAmount() && numM >= input.getMinToppingAmount() && getSize() <= input.getMaxSize();
	}

	public String toString()
	{
		return "slice(" + startRow + " " + startCol + " " + endRow + " " + endCol + ")";
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + endCol;
		result = prime * result + endRow;
		result = prime * result + startCol;
		result = prime * result + startRow;
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if(this == obj)
			return true;
		if(obj == null)
			return false;
		if(getClass() != obj.getClass())
			return false;
		Slice other = (Slice) obj;
		if(endCol != other.endCol)
			return false;
		if(endRow != other.endRow)
			return false;
		if(startCol != other.startCol)
			return false;
		if(startRow != other.startRow)
			return false;
		return true;
	}

	public static void main(String[] args)
	{
		// test
		Slice s = new Slice(5, 5, 10, 10);

		System.out.println(new Slice(1, 1, 4, 4).isOverlapping(s));
		System.out.println(new Slice(1, 1, 4, 5).isOverlapping(s));
		System.out.println(new Slice(1, 1, 5, 4).isOverlapping(s));
		System.out.println(new Slice(1, 1, 5, 5).isOverlapping(s));
		System.out.println(new Slice(11, 1, 15, 4).isOverlapping(s));
		System.out.println(new Slice(10, 1, 15, 4).isOverlapping(s));
		System.out.println(new Slice(11, 1, 15, 5).isOverlapping(s));
		System.out.println(new Slice(10, 1, 15, 5).isOverlapping(s));
		System.out.println(new Slice(1, 11, 4, 15).isOverlapping(s));
		System.out.println(new Slice(1, 11, 5, 15).isOverlapping(s));
		System.out.println(new Slice(1, 10, 4, 15).isOverlapping(s));
		System.out.println(new Slice(1, 10, 5, 15).isOverlapping(s));
		System.out.println(new Slice(11, 11, 15, 15).isOverlapping(s));
		System.out.println(new Slice(10, 11, 15, 15).isOverlapping(s));
		System.out.println(new Slice(11, 10, 15, 15).isOverlapping(s));
		System.out.println(new Slice(10, 10, 15, 15).isOverlapping(s));
	}
}
