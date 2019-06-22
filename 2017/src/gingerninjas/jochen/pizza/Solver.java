package gingerninjas.jochen.pizza;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.CountDownLatch;

import gingerninjas.BaseSolver;

public class Solver extends BaseSolver<Input, Output>
{
	long timestamp;
	
	@Override
	public Input parse(File in) throws IOException
	{
		Input input = new Input(in.getName());
		input.fromFile(in);
		return input;
	}

	@Override
	public void write(File out, Output output) throws IOException
	{
		output.toFile(out);
	}

	@Override
	public Output solve(Input in)
	{
		Map<String, Object> args = new HashMap<>();
		this.output = new Output(in.getName(), args);
		output.pizza = in.pizza;
		in.pizza.createSlices(this);

		return this.output;
	}

	@Override
	protected Input createInput(String name)
	{
		return new Input(in.getName());
	}

	@Override
	protected Output createOutput(String name, Map<String, Object> args)
	{
		return new Output(in.getName(), args);
	}

	public Random getRandom()
	{
		return random;
	}

	public void createInterimResult()
	{
		try
		{
			logger.info("writing output '" + out.getName() + "'");
			this.write(this.out, this.output);
			double percent = (this.output.getScore() / (double) this.input.getMaxScore()) * 100;
			logger.info("score: " + this.output.getScore() + " of " + this.input.getMaxScore() + " (" + String.format("%.2f", percent) + "%)");
		}
		catch(IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	public void solve(File in, long seed, CountDownLatch latch, long timestamp) {
		this.timestamp = timestamp;
		super.solve(in, seed, latch, timestamp);
	}

}
