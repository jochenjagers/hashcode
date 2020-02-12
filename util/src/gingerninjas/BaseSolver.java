package gingerninjas;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.CountDownLatch;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;

import gingerninjas.util.uploader.DataSet;
import gingerninjas.util.uploader.Round;
import gingerninjas.util.uploader.Uploader;

public abstract class BaseSolver<I extends BaseInput, O extends BaseOutput> extends Thread
{
	public static final String		LOGGER_PREFIX	= "solver";
	protected Logger				logger			= LogManager.getLogger(LOGGER_PREFIX);
	protected long					seed;
	protected Random				random;
	protected String				name			= null;
	protected File					log				= null;
	protected I						input			= null;
	protected O						output			= null;
	protected CountDownLatch		latch			= null;
	protected boolean				abortRequested	= false;
	protected Map<String, Object>	args			= new HashMap<>();
	protected int					highscore		= 0;
	
	public BaseSolver()
	{
		super();
	}

	public I getInput()
	{
		return input;
	}

	public O getOutput()
	{
		return output;
	}

	public void solve(File in, long seed, CountDownLatch latch, boolean skipHighscore, boolean loadPreviousOutput)
	{
		this.name = in.getName().replace(".in", "");
		this.seed = seed;
		this.random = new Random(seed);
		this.latch = latch;

		ArrayList<Round> rounds  = Uploader.getRounds();
		for(Round round : rounds) {
			if(round.isRunning()) {
				Uploader.loadHighScores(round);
				for(DataSet d : round.getDatasets()) {
					if(d.getName().equalsIgnoreCase(this.name)) {
						this.highscore = d.getHighScore();
					}
				}
			}
		}
		
		try
		{
			this.input = this.createInput(in.getParentFile());
			this.output = this.createOutput(in.getParentFile(), loadPreviousOutput);

			this.log = new File(in.getParentFile(), this.name + ".log");

			this.setName(name);
			super.start();
		}
		catch(IOException e)
		{
			// TODO Auto-generated catch block
			logger.error("Input oder Output für " + this.name + " konnten nicht erstellt werden. Solver wird nicht gestartet.");
			e.printStackTrace();
		}
	}

	@Override
	public synchronized void start()
	{
		throw new IllegalArgumentException("using solve(..) instead of start() is required");
	}

	@Override
	public void run()
	{
		ThreadContext.put("filename", log.getPath());

		logger.info("solving...");
		this.solve();
		double score = this.output.getScore();
		logger.info("score: " + score + " of " + this.input.maxScore + " ("
				+ String.format("%.2f", (score / this.input.maxScore * 100)) + "%)");
		
		// Ausgabe ggf. an den Server übertragen
		output.publish();

		latch.countDown();
	}

	protected abstract I createInput(File path) throws IOException;

	protected abstract O createOutput(File path, boolean load) throws IOException;

	protected abstract void solve();

	public void abort()
	{
		this.abortRequested = true;
	}
}
