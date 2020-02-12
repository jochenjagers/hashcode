package gingerninjas;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import gingerninjas.util.uploader.Uploader;

public abstract class BaseOutput
{
	protected transient final Logger	logger	= LogManager.getLogger(getClass());

	protected String					name;
	protected File						path;
	protected double					score;

	public BaseOutput(File path, String name)
	{
		this.name = name;
		this.path = path;
	}

	public BaseOutput(File path, String name, boolean load) throws IOException
	{
		this(path, name);

		if(load)
			this.load();
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

	public void publish()
	{
		Uploader.publishResult(this);
	}
	
	public void load() throws IOException
	{
		this.fromFile(new File(path, name + ".out"));
	}

	public void toFile() throws IOException
	{
		File out = new File(this.path, this.name + ".out");
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

	public void fromFile(File in) throws IOException
	{
		if(!in.exists())
		{
			logger.error("output '" + name + "' from file '" + in.getPath() + "' does not exist - no output will be loaded");
			return;
		}
		logger.info("reading output '" + name + "' from file '" + in.getPath() + "'");
		
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

	protected void parse(BufferedReader r) throws IOException
	{
		logger.error("operation not implemented - no parsing will be done!");
	}
}
