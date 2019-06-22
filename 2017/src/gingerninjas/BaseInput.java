package gingerninjas;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class BaseInput
{
	protected transient final Logger	logger	= LogManager.getLogger(getClass());

	protected String					name;
	protected int						bestScore;
	protected int						maxScore;

	public BaseInput(String name)
	{
		this.name = name;
	}

	public String getName()
	{
		return name;
	}

	public int getBestScore()
	{
		return bestScore;
	}

	public void setBestScore(int bestScore)
	{
		this.bestScore = bestScore;
	}

	public int getMaxScore()
	{
		return maxScore;
	}

	public void setMaxScore(int maxScore)
	{
		this.maxScore = maxScore;
	}

	public void fromFile(File in) throws IOException
	{
		logger.info("reading input '" + name + "' from file '" + in.getPath() + "'");
		FileReader fr = null;
		BufferedReader br = null;
		try
		{
			fr = new FileReader(in);
			br = new BufferedReader(fr);

			parse(br);
		}
		finally
		{
			if(br != null)
			{
				try
				{
					br.close();
				}
				catch(IOException e)
				{
					// ignore
				}
			}
			if(fr != null)
			{
				try
				{
					fr.close();
				}
				catch(IOException e)
				{
					// ignore
				}
			}
		}
	}

	protected abstract void parse(BufferedReader r) throws IOException;
}
