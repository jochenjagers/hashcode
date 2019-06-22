package gingerninjas.qualification;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;

import gingerninjas.BaseSolver;

public abstract class QualiSolver extends BaseSolver<Input, Output>
{
	@Override
	protected Input createInput(File path) throws IOException
	{
		return new Input(path, name);
	}

	@Override
	protected Output createOutput(File path, boolean load) throws IOException
	{
		Output o = new Output(path, name, false);
		o.init(this.input);
		if(load)
			o.load();
		return o;
	}
	
	public int calcInterestFactor(LinkedList<Slide> first, LinkedList<Slide> second)
	{
		int common = 0;
		int unique1 = 0;
		int unique2 = 0;
		for(Integer tag1 : first.getLast().getTags())
		{
			if(second.getFirst().getTags().contains(tag1))
				common++;
			else
				unique1++;
		}
		unique2 = second.getFirst().getTags().size() - common;
		return Math.min(common, Math.min(unique1, unique2));
	}
}
