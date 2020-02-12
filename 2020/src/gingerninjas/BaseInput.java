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
	protected int						maxScore;

	public BaseInput(File path, String name) throws IOException
	{
		this.name = name;
		this.fromFile(new File(path, name+".in"));
	}

	public String getName()
	{
		return name;
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
