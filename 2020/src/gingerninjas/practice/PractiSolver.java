package gingerninjas.practice;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;

import gingerninjas.BaseSolver;

public abstract class PractiSolver extends BaseSolver<Input, Output>
{
	@Override
	protected Input createInput(File path) throws IOException
	{
		return new Input(path, name);
	}

	@Override
	protected Output createOutput(File path, boolean load) throws IOException
	{
		Output o = new Output(path, name);
		if(load)
			o.load();
		return o;
	}
	
	public void updateScore() {
		int result = 0;
		for(int i : this.output.getTypes()) {
			result += this.input.getSlicesPerType()[i];
		}
		if(result > this.input.getMaxScore())
			result = 0;
		this.output.setScore(result);
	}
}
