package gingerninjas.jochen.pizza;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.CountDownLatch;

import gingerninjas.BaseSolver;

public class SimpleSplitSolver extends BaseSolver<Pizza, Output>
{

	@Override
	public void solve()
	{

		boolean split = true;
		ArrayList<Slice> slices = new ArrayList<Slice>();

		slices.add(new Slice(0, 0, input.width, input.height, input, output));
		
		while(split) {
			split = false;
			for(int i = 0; i < slices.size(); ++i)
			{
				Slice newSlice = slices.get(i).split();
				if(newSlice != null) {
					split = true;
					slices.add(newSlice);
				}
			}
		}
		
		output.setSlices(slices);
		/*
		 * this.output = new Output(in.getName(), args);
		 * output.pizza = in.pizza;
		 * in.pizza.createSlices(this);
		 * return this.output;
		 */
	}

	@Override
	protected Pizza createInput(File path) throws IOException
	{
		return new Pizza(path, name);
	}

	@Override
	protected Output createOutput(File path, boolean load)
	{
		return new Output(path, name);
	}

	public void createInterimResult()
	{
		/*
		 * logger.info("writing output '" + out.getName() + "'");
		 * this.write(this.out, this.output);
		 * double percent = (this.output.getScore() / (double) this.input.getMaxScore()) * 100;
		 * logger.info("score: " + this.output.getScore() + " of " + this.input.getMaxScore() + " ("
		 * + String.format("%.2f", percent) + "%)");
		 */

	}
}
