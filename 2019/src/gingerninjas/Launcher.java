package gingerninjas;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;

import gingerninjas.util.PatternFileFilter;
import gingerninjas.util.ZipUtil;

public class Launcher
{
	private static final Logger logger = LogManager.getLogger(Launcher.class);

	public static void cleanUp(File file, boolean keepOut)
	{
		for(File f : file.listFiles())
		{
			if(f.isDirectory())
			{
				cleanUp(f, keepOut);
			}
			else if(f.isFile() && f.getName().endsWith(".out"))
			{
				if(keepOut)
				{
					logger.info("Keep " + f.getName());
				}
				else
				{
					logger.info("Delete " + f.getName());
					f.delete();
				}
			}
			else if(f.isFile() && (f.getName().endsWith(".log") || f.getName().endsWith(".zip")))
			{
				logger.info("Delete " + f.getName());
				f.delete();
			}
		}
	}

	public static void main(String[] args)
	{

		ThreadContext.put("filename", "main.log");
		logger.info("-------------------------------------------------------");
		logger.info("gingerninjas #hashcode launcher");
		logger.info("-------------------------------------------------------");

		String algorithm = null;
		String directory = "/";
		String filePattern = "*.in";
		Long seed = new Random().nextLong();
		boolean cleanUp = false;
		boolean endlessMode = false;
		boolean startWith0Highscore = false;
		boolean loadPreviousOutput = false;

		for(String arg : args)
		{
			if(arg.startsWith("-a="))
				algorithm = arg.substring("-a=".length());
			else if(arg.startsWith("-d="))
				directory = arg.substring("-d=".length());
			else if(arg.startsWith("-f="))
				filePattern = arg.substring("-f=".length());
			else if(arg.startsWith("-s="))
				seed = Long.parseLong(arg.substring("-s=".length()));
			else if(arg.startsWith("-c"))
				cleanUp = true;
			else if(arg.startsWith("-e"))
				endlessMode = true;
			else if(arg.startsWith("-0"))
				startWith0Highscore = true;
			else if(arg.startsWith("-l"))
				loadPreviousOutput = true;
		}


		logger.info("directory    = " + directory);
		logger.info("filePattern  = " + filePattern);
		logger.info("algorithm    = " + algorithm);
		logger.info("seed         = " + seed);
		logger.info("cleanup      = " + cleanUp);
		logger.info("endless mode = " + endlessMode);
		logger.info("start w/ 0   = " + startWith0Highscore);
		logger.info("load prev.   = " + loadPreviousOutput);

		if(cleanUp)
		{
			// Lösche alle .log, .out und .zip Dateien
			cleanUp(new File("./"), loadPreviousOutput);
		}

		if(algorithm == null)
			throw new IllegalArgumentException("algorithm is null!");

		Class<?> solverClass = null;
		try
		{
			solverClass = Class.forName(algorithm);
		}
		catch(Exception e)
		{
			throw new RuntimeException("cound not resolve algorithm class", e);
		}

		File dir = new File(directory);
		File[] inputFiles = dir.listFiles(new PatternFileFilter(filePattern));
		
		File zip = new File("source.zip");
		ZipUtil.zip(new File(""), zip, false, null);

		List<BaseSolver<?, ?>> solvers = new ArrayList<BaseSolver<?, ?>>(inputFiles.length);
		final CountDownLatch latch = new CountDownLatch(inputFiles.length);
		BaseSolver<?, ?> s;

		for(File in : inputFiles)
		{
			try
			{
				logger.info("creating solver for input file '" + in.getPath() + "'");
				s = (BaseSolver<?, ?>) solverClass.newInstance();
				s.solve(in, seed, latch, startWith0Highscore, loadPreviousOutput);
				solvers.add(s);
			}
			catch(InstantiationException e)
			{
				logger.error(e);
			}
			catch(IllegalAccessException e)
			{
				logger.error(e);
			}
		}

		if(endlessMode)
		{
			int read = 1;
			// Ein Enter beendet den Endlessmode
			while(read > 0 && read != '\n')
			{
				try
				{
					read = System.in.read();
				}
				catch(IOException e)
				{
					read = -1;
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			logger.info("abort requested...");
		}
		else
		{
			logger.info("single run - immediate abort...");
		}
		
		for(BaseSolver<?, ?> s2 : solvers)
		{
			s2.abort();
		}

		try
		{
			// wait for all solvers to finish
			latch.await();
		}
		catch(InterruptedException e)
		{
			logger.error("could not wait for all solvers to finish", e);
		}

		
		// gesamtergebnis berechnen
		int totalScore = 0;
		int maxTotalScore = 0;
		for(BaseSolver<?, ?> s2 : solvers)
		{
			try
			{
				s2.getOutput().toFile();
			}
			catch(IOException e)
			{
			}
			totalScore += s2.getOutput().getScore();
			maxTotalScore += s2.getInput().getMaxScore();
		}

		logger.info("total score: " + totalScore + " of " + maxTotalScore);
	}
}
