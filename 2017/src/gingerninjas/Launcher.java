package gingerninjas;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;

import javax.swing.JButton;
import javax.swing.JFrame;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;

import gingerninjas.util.PatternFileFilter;
import gingerninjas.util.ZipUtil;
import gingerninjas.util.uploader.Uploader;

public class Launcher
{
	private static final Logger logger = LogManager.getLogger(Launcher.class);

	public static void cleanUp(File file)
	{
		for(File f : file.listFiles())
		{
			if(f.isDirectory())
			{
				cleanUp(f);
			}
			else if(f.isFile() && (f.getName().endsWith(".out") || f.getName().endsWith(".log") || f.getName().endsWith(".zip")))
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
		}

		long timestamp = System.currentTimeMillis();
		
		logger.info("directory    = " + directory);
		logger.info("filePattern  = " + filePattern);
		logger.info("algorithm    = " + algorithm);
		logger.info("seed         = " + seed);
		logger.info("timestamp    = " + timestamp);
		logger.info("cleanup      = " + cleanUp);
		logger.info("endless mode = " + endlessMode);

		if(cleanUp)
		{
			// Lösche alle .log, .out und .zip Dateien
			cleanUp(new File("./"));
		}

		File zip = new File(timestamp + ".zip");
		ZipUtil.zip(new File(""), zip, false, null);
		
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

		List<BaseSolver<?, ?>> solvers = new ArrayList<BaseSolver<?, ?>>(inputFiles.length);
		final CountDownLatch latch = new CountDownLatch(inputFiles.length);
		BaseSolver<?, ?> s;
		for(File in : inputFiles)
		{
			try
			{
				logger.info("creating solver for input file '" + in.getPath() + "'");
				s = (BaseSolver<?, ?>) solverClass.newInstance();
				s.solve(in, seed, latch, timestamp);
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

		JFrame abortDialog = null;
		if(endlessMode)
		{
			JButton abortButton = new JButton("Berechnung abbrechen");
			abortButton.setBackground(Color.red);
			abortButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e)
				{
					ThreadContext.put("filename", "main.log");
					logger.info("abort requested...");
					for(BaseSolver<?, ?> s2 : solvers)
					{
						s2.abort();
					}
				}
			});
			
			abortDialog = new JFrame();
			abortDialog.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
			abortDialog.getContentPane().add(abortButton);
			abortDialog.setSize(200, 100);
			abortDialog.setVisible(true);
			abortDialog.setAlwaysOnTop(true);
			abortDialog.requestFocus();
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
		
		if(abortDialog != null)
		{
			abortDialog.setVisible(false);
			abortDialog.dispose();
		}

		// gesamtergebnis berechnen
		int totalScore = 0;
		int maxTotalScore = 0;
		for(BaseSolver<?, ?> s2 : solvers)
		{
			totalScore += s2.getOutput().getScore();
			maxTotalScore += s2.getInput().getMaxScore();
		}

		logger.info("total score: " + totalScore + " of " + maxTotalScore);

		// alle output files & code zippen


//
//		Uploader uploader = new Uploader();
//		uploader.uploadResults(timestamp);

	}
}
