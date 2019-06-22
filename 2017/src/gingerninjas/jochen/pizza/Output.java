package gingerninjas.jochen.pizza;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Map;

import gingerninjas.BaseOutput;

public class Output extends BaseOutput
{
	Pizza2 pizza;

	public Output(String name, Map<String, Object> args)
	{
		super(name, args);
	}

	@Override
	protected void write(BufferedWriter r) throws IOException
	{
		this.score = 0;
		int validSliceCount = 0;
		String buffer = "";
		for(Slice s : pizza.slices)
		{
			int points = s.getPoints();
			logger.info(s);
			if(points > 0)
			{
				this.score += points;
				validSliceCount++;
				buffer += (s.y + " " + s.x + " " + (s.y + s.height - 1) + " " + (s.x + s.width - 1) + "\n");
			}
		}
		r.write(validSliceCount + "\n");
		r.write(buffer);
	}
}
