package gingerninjas.julian.pizza;

public class Slice
{
	private int	startRow;
	private int	startCol;
	private int	endRow;
	private int	endCol;

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
}
