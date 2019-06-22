package gingerninjas.jochen.pizza;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;

import gingerninjas.BaseSolver;
import gingerninjas.util.SortedLinkedList;
import gingerninjas.util.uploader.Round;
import gingerninjas.util.uploader.Uploader;

public class Pizza2 extends BaseSolver<Pizza, Output>
{
	static ForkJoinPool	pool			= new ForkJoinPool(6);

	int					bestSolution	= 0;

	public void reset()
	{
		output.slices.clear();
		input.reset();
	}

	@SuppressWarnings("unchecked")
	CompletableFuture<Boolean>[] removeSlices(Slice s, SortedLinkedList<Slice>[][] data)
	{
		// long start = System.nanoTime();
		int startY = Math.max(s.y - s.pizza.maxSize, 0);
		int endY = Math.min(s.y + s.height + s.pizza.maxSize + 2, input.height);
		int startX = Math.max(0, s.x - s.pizza.maxSize);
		int endX = Math.min(s.x + s.width + s.pizza.maxSize + 1, input.width);
		LinkedList<CompletableFuture<Boolean>> result = new LinkedList<CompletableFuture<Boolean>>();
		for(int y = startY; y < endY; ++y)
		{
			for(int x = startX; x < endX; ++x)
			{
				final int fx = x;
				final int fy = y;
				result.add(CompletableFuture.supplyAsync(() -> {
					return data[fy][fx].removeIf(i -> i.getPoints() == 0);
				}, pool));
			}
		}
		return result.toArray(new CompletableFuture[result.size()]);
	}

	public int createPossibleSlices(int y, int x, SortedLinkedList<Slice>[][] data)
	{
		int result = 0;
		if(input.used[y][x] == true)
		{
			return 0;
		}
		for(int height = 1; height <= input.maxSize && height <= (input.height - y); ++height)
		{
			for(int width = 1; (width * height) <= input.maxSize && width <= (input.width - x); ++width)
			{
				Slice test = new Slice(x, y, width, height, input, output);

				int points = test.getPoints();
				if(points > 0)
				{
					for(int sliceY = y; sliceY < y + height; ++sliceY)
					{
						for(int sliceX = x; sliceX < x + width; ++sliceX)
						{
							data[sliceY][sliceX].add(test);
							result++;
						}
					}
				}
			}
		}
		return result;
	}

	public int addSlice(Slice s)
	{
		// logger.info("Add: " + s);
		s.invalidate();
		int result = s.getPoints();
		Slice n = new Slice(s);
		output.slices.add(n);
		n.onPizza = true;
		for(int y = s.y; y < s.y + s.height; ++y)
		{
			for(int x = s.x; x < s.x + s.width; ++x)
			{
				input.used[y][x] = true;
			}
		}
		return result;
	}

	public void removeSlice(Slice s)
	{
		// logger.info("Remove: " + s);
		output.slices.removeIf(i -> i.equals(s));
		s.onPizza = false;
		for(int y = s.y; y < s.y + s.height; ++y)
		{
			for(int x = s.x; x < s.x + s.width; ++x)
			{
				input.used[y][x] = false;
			}
		}
	}

	public void optimize2()
	{
		int points = 0;
		int distOffset = 0;
		while(bestSolution < input.getMaxScore())
		{
			for(int y = 0; y < input.height; ++y)
			{
				for(int x = 0; x < input.width; ++x)
				{
					if(input.used[y][x] == false)
					{
						Slice s = new Slice(x, y, 1, 1, input, output);
						for(int dist = 1 + distOffset; dist < 10 + distOffset; ++dist)
						{
							if(this.solveSub(s, dist))
							{
								// break;
							}
						}
						output.calcPoints();
						points = (int) output.getScore();
						logger.info("Lücke bei " + s);
						logger.info("Nach Verbesserung: " + points);
					}
				}
			}
			distOffset += 5;
		}
	}

	public void optimize()
	{
		int points = 0;
		LinkedList<Slice> ignoredSlices = new LinkedList<>();

		while(bestSolution < input.getMaxScore())
		{
			float qual = 1;
			int badDist = 1;
			Slice badSlice = null;
			// int dist = (int) (random.nextFloat() * 10) + 5;
			for(int dist = 1; dist < 10; ++dist)
			{
				for(Slice s : output.slices)
				{
					float squal = s.getQuality(dist);
					if(squal < qual && !ignoredSlices.contains(s))
					{
						qual = squal;
						badSlice = s;
						badDist = dist;
					}
				}
			}
			logger.info("Schlechtestes Slice: " + qual + " -- Dist: " + badDist + " -- " + badSlice + " Points: " + output.getScore());
			if(this.solveSub(badSlice, badDist) == false)
			{
				boolean result = false;
				for(int i = 0; i < 5 && !result; ++i)
				{
					result |= this.solveSub(badSlice, badDist + i);
				}
				if(!result)
				{
					ignoredSlices.add(badSlice);
				}
			}
			output.calcPoints();
			points = (int) output.getScore();
			logger.info("Nach Verbesserung: " + points);
		}
	}

	public SortedLinkedList<Slice> clearArea(Slice s, int dist)
	{
		int startY = Math.max(0, s.y - dist);
		int endY = Math.min(input.height, s.y + s.height + dist);
		int startX = Math.max(0, s.x - dist);
		int endX = Math.min(input.width, s.x + s.width + dist);
		SortedLinkedList<Slice> result = new SortedLinkedList<>();
		for(int y = startY; y < endY; ++y)
		{
			for(int x = startX; x < endX; ++x)
			{
				for(Slice n : output.slices)
				{
					if(n.pointOnSlice(x, y))
					{
						removeSlice(n);
						result.add(n);
						break;
					}
				}
			}
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void solve()
	{
		if(1 == 1)
		{
			return;
		}

		TreeNode descisionTree = new TreeNode(0, 0, null);
		TreeNode activeNode = descisionTree;
		SortedLinkedList<Slice>[][] data = new SortedLinkedList[input.height][input.width];
		SortedLinkedList<Slice>[][] possibleSlices = new SortedLinkedList[input.height][input.width];

		TreeNode bestSolutionFinalNode = null;

		random.setSeed(System.currentTimeMillis());

		Integer count = 0;
		for(int y = 0; y < input.height; ++y)
		{
			for(int x = 0; x < input.width; ++x)
			{
				possibleSlices[y][x] = new SortedLinkedList<Slice>();
			}
		}
		for(int y = 0; y < input.height; ++y)
		{
			for(int x = 0; x < input.width; ++x)
			{
				count += this.createPossibleSlices(y, x, possibleSlices);
			}
		}
		logger.info(count + " Slices erzeugt");

		while(descisionTree.solved == false && output.getScore() < input.getMaxScore())
		{
			System.gc();
			int iteration = 0;
			activeNode = descisionTree;
			this.reset();

			logger.info("Copy Arrays");
			for(int y = 0; y < input.height; ++y)
			{
				for(int x = 0; x < input.width; ++x)
				{
					// TODO COPY GIBT ES NICHTMEHR. Aus svn wiederherstellen
	//				data[y][x] = possibleSlices[y][x].copy();
				}
			}
			logger.info("Copy Arrays");
			boolean firstDecision = true;
			while(true)
			{
				int minCount = Integer.MAX_VALUE;
				ArrayList<Slice> possibilities = new ArrayList<>();
				for(int y = 0; y < input.height; ++y)
				{
					for(int x = 0; x < input.width; ++x)
					{
						int posCount = data[y][x].size();
						if(posCount > 0 && posCount <= minCount)
						{
							if(posCount < minCount)
								possibilities = new ArrayList<>();

							minCount = posCount;
							if(data[y][x].size() > 0)
							{
								possibilities.add(data[y][x].getLast());
							}
						}
					}
					// logger.info(debugData);
				}

				if(possibilities.size() > 0)
				{
					if(activeNode.children.size() < possibilities.size())
					{
						for(int i = activeNode.children.size(); i < possibilities.size(); i++)
						{
							activeNode.addChild(i, iteration);
						}
					}
					else if(activeNode.children.size() > possibilities.size())
					{
						logger.info("blabla");
					}
					Slice newSlice = null;
					TreeNode newNode = null;
					int minTries = Integer.MAX_VALUE;
					ArrayList<TreeNode> nodes = new ArrayList<>();
					for(TreeNode node : activeNode.children)
					{
						if(!node.solved)
						{
							nodes.add(node);
						}
						// if(node.solved == false && node.tries < minTries)
						// {
						// newNode = node;
						// minTries = node.tries;
						// }
					}
					if(nodes.size() > 0)
					{
						int i = Math.round(random.nextFloat() * (nodes.size() - 1));
						newNode = nodes.get(i);
					}
					if(newNode != null)
					{
						activeNode = newNode;
						activeNode.tries++;
						newSlice = possibilities.get(activeNode.decision);
					}
					if(newSlice != null)
					{
						if(possibilities.size() > 1 && firstDecision)
						{
							logger.info("First Slice: " + newSlice);
							firstDecision = false;
							// logger.info("Möglichkeiten: " + possibilities.size());
						}
						// logger.info("Slice hinzugefügt " + newSlice.toString());
						// long start = System.nanoTime();
						this.addSlice(newSlice);
						// logger.info("Punkte: " + sum + " (" + sum * 100 / width / height + "%)");
						// Update possible slices
						CompletableFuture<Boolean>[] futures = this.removeSlices(newSlice, data);
						try
						{
							CompletableFuture.allOf(futures).get();
						}
						catch(InterruptedException | ExecutionException e)
						{
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

						// long end = System.nanoTime();
						// logger.info("Elapsed: " + (end-start)/1000000.0 + "ms");
					}
					else
					{
						// logger.info("Innerer Node vollständig gelöst. Children: " +
						// activeNode.children.size());
						break;
					}
				}
				else
				{
					output.calcPoints();
					int points = (int) output.getScore();
					logger.info("Kein weiteres Teil möglich. Aktuelle Lösung hat Punkte: " + points + " und " + output.slices.size() + " Slices");
					activeNode.setSolved(true);
					if(points > bestSolution)
					{
						bestSolution = points;
						bestSolutionFinalNode = activeNode;
						highscore = bestSolution;
						try
						{
							output.toFile();
						}
						catch(IOException e)
						{
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						output.publish();
					}

					// this.optimize();

					break;
				}
				iteration++;
			}
			descisionTree.checkSolved();
		}
		// this.slices.add(new Slice(0,0,1,2));
	}

	protected boolean solveSub(Slice s, int dist)
	{
		boolean result = false;
		TreeNode descisionTree = new TreeNode(0, 0, null);
		TreeNode activeNode = descisionTree;
		SortedLinkedList<Slice>[][] data = new SortedLinkedList[input.height][input.width];
		SortedLinkedList<Slice>[][] possibleSlices = new SortedLinkedList[input.height][input.width];

		boolean used[][] = new boolean[input.height][input.width];
		SortedLinkedList<Slice> newSlices = new SortedLinkedList<>();

		output.calcPoints();
		double bestSolution = output.getScore();
		SortedLinkedList<Slice> removedSlices = this.clearArea(s, dist);

		Integer count = 0;
		for(int y = 0; y < input.height; ++y)
		{
			for(int x = 0; x < input.width; ++x)
			{
				used[y][x] = input.used[y][x];
				possibleSlices[y][x] = new SortedLinkedList<Slice>();
			}
		}
		for(int y = 0; y < input.height; ++y)
		{
			for(int x = 0; x < input.width; ++x)
			{
				count += this.createPossibleSlices(y, x, possibleSlices);
			}
		}
		logger.info(count + " Slices erzeugt");
		int retryCount = 0;
		while(descisionTree.solved == false && output.getScore() < input.getMaxScore() && retryCount++ < 10)
		{
			System.gc();
			int iteration = 0;
			activeNode = descisionTree;
			for(Slice m : newSlices)
			{
				removeSlice(m);
			}
			newSlices.clear();

			for(int y = 0; y < input.height; ++y)
			{
				for(int x = 0; x < input.width; ++x)
				{
					if(used[y][x] != input.used[y][x])
					{
						logger.error("Anfangszustand ist unterschieldich!");
					}
					//					// TODO COPY GIBT ES NICHTMEHR. Aus svn wiederherstellen
					//data[y][x] = possibleSlices[y][x].copy();
				}
			}

			while(true)
			{
				int minCount = Integer.MAX_VALUE;
				ArrayList<Slice> possibilities = new ArrayList<>();
				for(int y = 0; y < input.height; ++y)
				{
					for(int x = 0; x < input.width; ++x)
					{
						int posCount = data[y][x].size();
						if(posCount > 0 && posCount <= minCount)
						{
							if(posCount < minCount)
								possibilities = new ArrayList<>();

							minCount = posCount;
							if(data[y][x].size() > 0)
							{
								possibilities.add(data[y][x].getLast());
							}
						}
					}
					// logger.info(debugData);
				}

				if(possibilities.size() > 0)
				{
					if(activeNode.children.size() < possibilities.size())
					{
						for(int i = activeNode.children.size(); i < possibilities.size(); i++)
						{
							activeNode.addChild(i, iteration);
						}
					}
					else if(activeNode.children.size() > possibilities.size())
					{
						logger.info("blabla");
					}
					Slice newSlice = null;
					TreeNode newNode = null;
					int minTries = Integer.MAX_VALUE;
					ArrayList<TreeNode> nodes = new ArrayList<>();
					for(TreeNode node : activeNode.children)
					{
						if(!node.solved)
						{
							nodes.add(node);
						}

						// if(node.solved == false && node.tries < minTries)
						// {
						// newNode = node;
						// minTries = node.tries;
						// }
						if(nodes.size() > 0)
						{
							int i = Math.round(random.nextFloat() * (nodes.size() - 1));
							newNode = nodes.get(i);
						}
					}
					if(newNode != null)
					{
						activeNode = newNode;
						activeNode.tries++;
						newSlice = possibilities.get(activeNode.decision);
					}
					if(newSlice != null)
					{
						this.addSlice(newSlice);
						newSlices.add(newSlice);
						CompletableFuture<Boolean>[] futures = this.removeSlices(newSlice, data);
						try
						{
							CompletableFuture.allOf(futures).get();
						}
						catch(InterruptedException | ExecutionException e)
						{
							e.printStackTrace();
						}

					}
					else
					{
						break;
					}
				}
				else
				{
					output.calcPoints();
					int points = (int) output.getScore();
					// logger.info("Kein weiteres Teil möglich. Aktuelle Lösung hat Punkte: " +
					// points + " und " + output.slices.size() + " Slices");
					activeNode.setSolved(true);
					if(points > bestSolution)
					{
						logger.info("Bessere Lösung gefunden. " + points + " > " + bestSolution);
						bestSolution = points;
						 if(bestSolution - 500 > highscore)
						 {
						output.publish();
						highscore = (int) bestSolution;
						 }
							// TODO COPY GIBT ES NICHTMEHR. Aus svn wiederherstellen
						//removedSlices = newSlices.copy();
						result = true;
					}
					break;
				}
				iteration++;
			}
			descisionTree.checkSolved();
		}

		// Keine Bessere Lösung gefunden. Alte Lösung wiederherstellen
		for(Slice n : newSlices)
		{
			removeSlice(n);
		}
		for(Slice n : removedSlices)
		{
			addSlice(n);
		}
		return result;
	}

	@Override
	protected Pizza createInput(File path) throws IOException
	{
		return new Pizza(path, name);
	}

	@Override
	protected Output createOutput(File path, boolean load)
	{
		return new Output(path, name);
	}

	public static void main(String[] args) throws NumberFormatException, IOException, InterruptedException
	{
		Pizza2 p2 = new Pizza2();
		CountDownLatch latch = new CountDownLatch(1);
		p2.solve(new File("Test Round", "4 - Big.in"), 12345, latch, true, false);
		p2.join();
		int lineNumber = 0;
		String line = null;

		FileReader fin = new FileReader("Test Round/4 - Big.out2");
		BufferedReader bif = new BufferedReader(fin);

		while((line = bif.readLine()) != null)
		{
			if(lineNumber == 0)
			{
			}
			else
			{
				String[] values = line.split(" ");
				// Parse first line
				if(values.length > 3)
				{
					int y = Integer.parseInt(values[0]);
					int x = Integer.parseInt(values[1]);
					int y2 = Integer.parseInt(values[2]);
					int x2 = Integer.parseInt(values[3]);
					p2.addSlice(new Slice(x, y, x2 - x + 1, y2 - y + 1, p2.input, p2.output));
				}

			}
			lineNumber++;
		}
		p2.optimize();
	}

}
