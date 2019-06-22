package gingerninjas.jochen.pizza;

import java.io.BufferedReader;
import java.io.IOException;

import gingerninjas.BaseInput;

public class Input<T extends Pizza> extends BaseInput
{
	Pizza2 pizza;

	public Input(String name)
	{
		super(name);

		pizza = new Pizza2();
	}

	@Override
	protected void parse(BufferedReader r) throws IOException
	{
		int lineNumber = 0;
		String line = null;

		int rows = 0;
		int cols = 0;
		int toppingCount = 0;
		int maxSize = 0;

		while((line = r.readLine()) != null)
		{
			if(lineNumber == 0)
			{
				String[] values = line.split(" ");
				// Parse first line
				if(values.length != 4)
				{
					logger.error("Wrong file format");
					return;
				}
				rows = Integer.parseInt(values[0]);
				cols = Integer.parseInt(values[1]);
				toppingCount = Integer.parseInt(values[2]);
				maxSize = Integer.parseInt(values[3]);

				pizza.topping = new char[rows][cols];
			}
			else
			{
				for(int i = 0; i < line.length(); ++i)
				{
					pizza.topping[lineNumber - 1][i] = line.charAt(i);
				}
			}
			lineNumber++;
		}
		pizza.maxSize = maxSize;
		pizza.toppingCount = toppingCount;
		pizza.reset();
		pizza.height = rows;
		pizza.width = cols;
		
		logger.info("Input eingelesen: ");
		logger.info("Rows: " + rows);
		logger.info("Cols: " + cols);
		logger.info("ToppingCount: " + toppingCount);
		logger.info("MaxSize: " + maxSize);
		
		this.maxScore = rows*cols;
	}
}
