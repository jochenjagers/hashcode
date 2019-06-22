package gingerninjas;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.CountDownLatch;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;

public abstract class BaseSolver<I extends BaseInput, O extends BaseOutput> extends Thread
{
	public static final String	LOGGER_PREFIX	= "solver";
	protected Logger			logger			= LogManager.getLogger(LOGGER_PREFIX);
	protected long				seed;
	protected Random			random;
	protected String			name			= null;
	protected File				in				= null;
	protected File				out				= null;
	protected File				log				= null;
	protected I					input			= null;
	protected O					output			= null;
	protected CountDownLatch	latch			= null;
	protected long				timestamp		= 0;
	protected boolean			abortRequested	= false;

	public BaseSolver()
	{
		super();
	}

	public File getIn()
	{
		return in;
	}

	public File getOut()
	{
		return out;
	}

	public BaseInput getInput()
	{
		return input;
	}

	public BaseOutput getOutput()
	{
		return output;
	}

	public void solve(File in, long seed, CountDownLatch latch, long timestamp)
	{
		this.name = in.getName().substring(0, in.getName().lastIndexOf('.'));
		this.seed = seed;
		this.random = new Random(seed);
		this.latch = latch;
		this.in = in;
		this.timestamp = timestamp;

		String outName = in.getName().replace(".in", "") + "." + timestamp + ".out";

		String logName = in.getName().replace(".in", "") + "." + timestamp + ".log";

		this.out = new File(in.getParentFile(), outName);
		this.log = new File(in.getParentFile(), logName);

		// this.logger = createLogger(name, new File(in.getParentFile(), logName));

		this.setName(name);
		super.start();
	}

	@Override
	public synchronized void start()
	{
		throw new IllegalArgumentException("using solve(..) instead of start() is required");
	}

	@Override
	public void run()
	{
		try
		{
			ThreadContext.put("filename", log.getPath());
			// Appender appender = new FileAppender();
			// ((org.apache.logging.log4j.core.Logger) logger).addAppender(appender);

			logger.info("parsing input '" + in.getName() + "'");
			this.input = this.parse(in);

			logger.info("solving...");
			this.output = this.solve(input);

			logger.info("writing output '" + out.getName() + "'");
			this.write(this.out, this.output);

			double percent = (this.output.score / (double) this.input.maxScore) * 100;
			logger.info("score: " + this.output.score + " of " + this.input.maxScore + " (" + String.format("%.2f", percent) + "%)");
		}
		catch(IOException e)
		{
			// TODO
		}
		finally
		{
			latch.countDown();
		}
	}

	protected abstract I createInput(String name);

	protected abstract O createOutput(String name, Map<String, Object> args);

	protected I parse(File in) throws IOException
	{
		I input = createInput(in.getName());
		input.fromFile(in);
		return input;
	}

	protected void write(File out, O output) throws IOException
	{
		output.toFile(out);
	}

	protected abstract O solve(I in);

	public void abort()
	{
		this.abortRequested = true;
	}
}
