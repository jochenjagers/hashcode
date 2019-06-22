package gingerninjas.jochen.pizza;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import gingerninjas.BaseOutput;

public class Output extends BaseOutput
{
	ArrayList<Slice> slices;

	public Output(File path, String name)
	{
		super(path, name);
		slices = new ArrayList<Slice>();
	}

	@Override
	protected void write(BufferedWriter r) throws IOException
	{
		int validSliceCount = 0;
		String buffer = "";
		for(Slice s : slices)
		{
			int points = s.getPoints();
			//logger.info(s);
			if(points > 0)
			{
				validSliceCount++;
				buffer += (s.y + " " + s.x + " " + (s.y + s.height - 1) + " " + (s.x + s.width - 1) + "\n");
			}
		}
		r.write(validSliceCount + "\n");
		r.write(buffer);
	}

	public void setSlices(ArrayList<Slice> slices)
	{
		this.slices = slices;

		this.score = 0;
		for(Slice slice : this.slices)
		{
			this.score += slice.getPoints();
		}
	}
	
	public void calcPoints()
	{
		this.score = 0;
		for(Slice s : slices)
		{
			if(s.getPoints() == 0) {
				logger.info("0 Punkte für: " + s);
			}
			this.score += s.getPoints();
		}
	}
}
