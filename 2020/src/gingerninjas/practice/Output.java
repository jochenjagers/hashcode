package gingerninjas.practice;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import gingerninjas.BaseOutput;

public class Output extends BaseOutput
{	
	private List<Integer> types;
	
	public Output(File path, String name)
	{
		super(path, name);
		
		this.types = new ArrayList<Integer>();
	}

	public List<Integer> getTypes() {
		return types;
	}

	public void setTypes(List<Integer> types) {
		this.types = types;
	}

	@Override
	protected void write(BufferedWriter r) throws IOException
	{		
		r.write(types.size() + "\n");
		for(int i = 0; i < types.size(); i++)
		{
			if(i != 0)
				r.write(" ");
			r.write("" + types.get(i));
		}
	}
}
