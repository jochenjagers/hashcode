package gingerninjas.qualification;

import java.io.File;
import java.io.IOException;

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
}
