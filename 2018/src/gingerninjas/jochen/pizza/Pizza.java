package gingerninjas.jochen.pizza;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;

import gingerninjas.BaseInput;

public class Pizza extends BaseInput
{
	char[][]							topping;
	boolean[][]							used;
	int									maxSize			;
	int									toppingCount	;
	int									width;
	int									height;

	public Pizza(File path, String name) throws IOException
	{
		super(path, name);

	}
	
	public void reset() {
		for(int x = 0; x < this.width; ++x) {
			for(int y = 0; y < this.height; ++y) {
				this.used[y][x] = false;
			}
		}
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

				topping = new char[rows][cols];
				used = new boolean[rows][cols];
			}
			else
			{
				for(int i = 0; i < line.length(); ++i)
				{
					topping[lineNumber - 1][i] = line.charAt(i);
				}
			}
			lineNumber++;
		}
		this.maxSize = maxSize;
		this.toppingCount = toppingCount;
		this.height = rows;
		this.width = cols;
		this.maxScore =  this.width * this.height;
		this.reset();
		
		logger.info("Input eingelesen: ");
		logger.info("Rows: " + rows);
		logger.info("Cols: " + cols);
		logger.info("ToppingCount: " + toppingCount);
		logger.info("MaxSize: " + maxSize);
		
	}
}
