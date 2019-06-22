package gingerninjas.julian.pizza;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;

import gingerninjas.BaseInput;

public class Input extends BaseInput
{
	private int			rows;
	private int			cols;
	private char[][]	pizza;
	private int			minToppingAmount;
	private int			maxSize;

	public Input(File path, String name) throws IOException
	{
		super(path, name);
	}

	public int getRows()
	{
		return rows;
	}

	public int getCols()
	{
		return cols;
	}

	public char[][] getPizza()
	{
		return pizza;
	}

	public int getMinToppingAmount()
	{
		return minToppingAmount;
	}

	public int getMaxSize()
	{
		return maxSize;
	}

	@Override
	protected void parse(BufferedReader r) throws IOException
	{
		String line = null;

		line = r.readLine();
		String[] firstLine = line.split(" ");

		rows = Integer.parseInt(firstLine[0]);
		cols = Integer.parseInt(firstLine[1]);
		minToppingAmount = Integer.parseInt(firstLine[2]);
		maxSize = Integer.parseInt(firstLine[3]);

		pizza = new char[rows][];

		int row = 0;
		while((line = r.readLine()) != null)
		{
			pizza[row] = line.toCharArray();
			row++;
		}
	}
}
