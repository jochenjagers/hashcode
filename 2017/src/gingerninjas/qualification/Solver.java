package gingerninjas.qualification;

import java.io.IOException;
import java.util.Map;
import java.util.Random;

import gingerninjas.BaseSolver;
import gingerninjas.util.uploader.Uploader;

public abstract class Solver extends BaseSolver<Input, Output>
{
	private static final int	NUM_TRIES			= 1; //Integer.MAX_VALUE;
	
	@Override
	protected Input createInput(String name)
	{
		return new Input(name);
	}

	@Override
	protected Output createOutput(String name, Map<String, Object> args)
	{
		return new Output(name, args);
	}

	@Override
	protected Output solve(Input in)
	{
		Random random = new Random(this.seed);
		
		Output output = null;
		Output bestOutput = null;
		for(int i = 0; i < NUM_TRIES; i++)
		{
			this.input.reset();
			
			output = solve(input, random);			
			
			logger.info("cycle #" + i + " score=" + output.getScore() + "   (best=" + (bestOutput != null ? bestOutput.getScore() : 0) + ")");	
				
			if(bestOutput == null || output.getScore() > bestOutput.getScore())
			{			
				bestOutput = output;
				
				try
				{
					output.toFile(this.out);
					
					Uploader uploader = new Uploader();
					uploader.uploadResults(this.timestamp, this.name);
				}
				catch(IOException e)
				{
					logger.error(e);
				}				

			}

			if(this.abortRequested)
				break;
		}

		logger.info("Output: " + output);
		
		return output;
	}
	
	protected abstract Output solve(Input input, Random random);
	
	protected int getScore(int totalRequests) {
		long localResult = 0;
		for(Endpoint e : input.endpoints) {
			localResult += e.getPoints();
		}
		logger.info("LocalResul: " + localResult);
		logger.info("totalRequests: " + totalRequests);
		return (int) Math.floor(localResult*1000/totalRequests);
	}
}
