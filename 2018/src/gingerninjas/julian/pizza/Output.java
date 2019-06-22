package gingerninjas.julian.pizza;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import gingerninjas.BaseOutput;

public class Output extends BaseOutput
{	
	private List<Slice> slices;
	
	public Output(File path, String name)
	{
		super(path, name);
		
		this.slices = new ArrayList<Slice>();
	}

	public List<Slice> getSlices()
	{
		return slices;
	}

	public void setSlices(List<Slice> slices)
	{
		this.slices = slices;
	}

	@Override
	protected void write(BufferedWriter r) throws IOException
	{
		r.write(slices.size() + "\n");
		for(Slice s: slices)
		{
			r.write(s.getStartRow() + " " + s.getStartCol() + " " + s.getEndRow() + " " + s.getEndCol() + "\n");
		}
	}
}
