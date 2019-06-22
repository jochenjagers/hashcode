package gingerninjas;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class BaseOutput
{
	protected transient final Logger	logger	= LogManager.getLogger(getClass());
	
	protected String				name;
	protected double					score;
	protected Map<String, Object>	args;

	public BaseOutput(String name, Map<String, Object> args)
	{
		this.name = name;
		this.args = args;
	}

	public String getName()
	{
		return name;
	}

	public double getScore()
	{
		return score;
	}

	public void setScore(double score)
	{
		this.score = score;
	}

	public Map<String, Object> getArgs()
	{
		return args;
	}

	public void toFile(File out) throws IOException
	{
		logger.info("writing output '" + name + "' to file '" + out.getPath() + "'");
		FileWriter fw = null;
		BufferedWriter bw = null;
		try
		{
			fw = new FileWriter(out);
			bw = new BufferedWriter(fw);

			write(bw);
		}
		finally
		{
			if(bw != null)
			{
				try
				{
					bw.flush();
					bw.close();
				}
				catch(IOException e)
				{
					// ignore
				}
			}
			if(fw != null)
			{
				try
				{
					fw.flush();
					fw.close();
				}
				catch(IOException e)
				{
					// ignore
				}
			}
		}
	}

	protected abstract void write(BufferedWriter r) throws IOException;

}
