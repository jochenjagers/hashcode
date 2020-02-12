package gingerninjas.practice;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;

import gingerninjas.BaseInput;

public class Input extends BaseInput
{
	private int			slices;
	private int			types;
	private int[] 		slicesPerType;

	public Input(File path, String name) throws IOException
	{
		super(path, name);
	}

	public int getSlices() {
		return slices;
	}

	public int getTypes() {
		return types;
	}

	public int[] getSlicesPerType() {
		return slicesPerType;
	}

	@Override
	protected void parse(BufferedReader r) throws IOException
	{
		String line = null;

		line = r.readLine();
		String[] firstLine = line.split(" ");

		slices = Integer.parseInt(firstLine[0]);
		types = Integer.parseInt(firstLine[1]);

		slicesPerType = new int[types];

		String[] secondLine = line.split(" ");
		for(int i = 0; i < secondLine.length; i++)
		{
			slicesPerType[i] = Integer.parseInt(firstLine[i]);
		}
	}
}
