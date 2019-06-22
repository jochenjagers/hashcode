package gingerninjas.util;

import java.io.File;
import java.io.FileFilter;

public class PatternFileFilter implements FileFilter
{
	private String	filePattern;
	private String	filePatternPart1;
	private String	filePatternPart2;

	public PatternFileFilter(String filePattern)
	{
		this.filePattern = filePattern;

		int numStars = 0;

		for(int i = 0; i < filePattern.length(); i++)
			if(filePattern.charAt(i) == '*')
				numStars++;

		if(numStars == 0)
		{
			filePatternPart1 = "";
			filePatternPart2 = filePattern;
		}
		else if(numStars == 1)
		{
			filePatternPart1 = filePattern.substring(0, filePattern.indexOf('*'));
			filePatternPart2 = filePattern.substring(filePattern.indexOf('*') + 1);
		}
		else
		{
			throw new IllegalArgumentException("illegal filePattern: " + filePattern);
		}
	}

	@Override
	public boolean accept(File file)
	{
		return file.getName().startsWith(filePatternPart1) && file.getName().endsWith(filePatternPart2);
	}

	public static void main(String[] args)
	{
		PatternFileFilter allIn = new PatternFileFilter("*.in");
		PatternFileFilter allOut = new PatternFileFilter("*.out");
		PatternFileFilter allFileIn = new PatternFileFilter("file*.in");
		PatternFileFilter allFileOut = new PatternFileFilter("file*.out");
		PatternFileFilter fileName = new PatternFileFilter("somefile.*");
		PatternFileFilter singleFile = new PatternFileFilter("somefile.in");

		//@formatter:off
		File[] files = new File[] {
				new File("file1.in"),
				new File("file2.in"),
				new File("file3.out"),
				new File("file4.out"),
				new File("somefile.in"),
				new File("somefile.out")
			};
		//@formatter:on

		System.out.println("Checking " + allIn.filePattern);
		for(File f : files)
		{
			System.out.println("\t" + f.getName() + " --> " + allIn.accept(f));
		}

		System.out.println("Checking " + allOut.filePattern);
		for(File f : files)
		{
			System.out.println("\t" + f.getName() + " --> " + allOut.accept(f));
		}
		
		System.out.println("Checking " + allFileIn.filePattern);
		for(File f : files)
		{
			System.out.println("\t" + f.getName() + " --> " + allFileIn.accept(f));
		}
		
		System.out.println("Checking " + allFileOut.filePattern);
		for(File f : files)
		{
			System.out.println("\t" + f.getName() + " --> " + allFileOut.accept(f));
		}

		System.out.println("Checking " + fileName.filePattern);
		for(File f : files)
		{
			System.out.println("\t" + f.getName() + " --> " + fileName.accept(f));
		}

		System.out.println("Checking " + singleFile.filePattern);
		for(File f : files)
		{
			System.out.println("\t" + f.getName() + " --> " + singleFile.accept(f));
		}
	}
}
