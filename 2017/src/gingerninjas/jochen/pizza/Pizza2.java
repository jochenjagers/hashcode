package gingerninjas.jochen.pizza;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import gingerninjas.util.ZipUtil;
import gingerninjas.util.uploader.Uploader;

public class Pizza2 extends Pizza
{
	public void reset()
	{
		super.reset();
		slices.clear();
	}

	public void removeSlices(ArrayList<ArrayList<ArrayList<Slice>>> data)
	{
		for(int y = 0; y < this.height; ++y)
		{
			for(int x = 0; x < this.width; ++x)
			{
				data.get(y).get(x).removeIf(i -> i.getPoints() == 0);
			}
		}
	}

	public void showPossibleCount(ArrayList<ArrayList<ArrayList<Slice>>> data)
	{
		for(int y = 0; y < this.height; ++y)
		{
			String line = "";
			for(int x = 0; x < this.width; ++x)
			{
				line += data.get(y).get(x).size() + ";";
			}
			logger.info(line);
		}
	}

	public void createPossibleSlices(int y, int x, ArrayList<ArrayList<ArrayList<Slice>>> data)
	{
		if(used[y][x] == true)
		{
			return;
		}
		for(int height = 1; height <= this.maxSize && height <= (this.height - y); ++height)
		{
			for(int width = 1; (width * height) <= this.maxSize && width <= (this.width - x); ++width)
			{
				Slice test = new Slice(x, y, width, height, this);

				int points = test.getPoints();
				if(points > 0)
				{
					for(int sliceY = y; sliceY < y + height; ++sliceY)
					{
						for(int sliceX = x; sliceX < x + width; ++sliceX)
						{
							data.get(sliceY).get(sliceX).add(test);
						}
					}
				}
			}
		}
	}

	public void createSlices(Solver solver)
	{
		TreeNode descisionTree = new TreeNode(0, 0, null);
		TreeNode activeNode = descisionTree;
		ArrayList<ArrayList<ArrayList<Slice>>> data = new ArrayList<>(this.height);
		final int sliceRemoverCount = 4;
		ArrayList<SliceRemover> sliceRemover = new ArrayList<>();
		BlockingQueue<Integer> queue = new LinkedBlockingQueue<>();

		int bestSolution = 0;
		TreeNode bestSolutionFinalNode = null;

		float rows = this.height / (float) sliceRemoverCount;
		for(int i = 0; i < sliceRemoverCount; ++i)
		{
			sliceRemover.add(new SliceRemover(Math.round(i * rows), Math.min(this.height, Math.round((i + 1) * rows)), this.width, data, queue));
		}
		while(descisionTree.solved == false)
		{
			int iteration = 0;
			activeNode = descisionTree;
			this.reset();
			data.clear();
			for(int y = 0; y < this.height; ++y)
			{
				ArrayList<ArrayList<Slice>> row = new ArrayList<>(this.width);
				data.add(y, row);
				for(int x = 0; x < this.width; ++x)
				{
					ArrayList<Slice> col = new ArrayList<>();
					row.add(x, col);
				}
			}

			// Zuerst die möglichen slices berechnen
			for(int y = 0; y < this.height; ++y)
			{
				// logger.info("Row " + y + "/" + this.height);
				for(int x = 0; x < this.width; ++x)
				{
					this.createPossibleSlices(y, x, data);
				}
			}
			boolean firstDecision = true;
			while(true)
			{
				int minCount = Integer.MAX_VALUE;
				ArrayList<Slice> possibilities = new ArrayList<>();
				for(int y = 0; y < this.height; ++y)
				{
					for(int x = 0; x < this.width; ++x)
					{
						int posCount = data.get(y).get(x).size();
						if(posCount > 0 && posCount <= minCount)
						{
							if(posCount < minCount)
								possibilities = new ArrayList<>();

							minCount = posCount;
							int maxPoints = 0;
							Slice newSlice = null;
							for(Slice s : data.get(y).get(x))
							{
								int points = s.getPoints();
								if(points > maxPoints)
								{
									newSlice = s;
									maxPoints = points;
								}
							}
							if(newSlice != null)
							{
								possibilities.add(newSlice);
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
						if(!node.solved) {
							nodes.add(node);
						}
//						if(node.solved == false && node.tries < minTries)
//						{
//							newNode = node;
//							minTries = node.tries;
//						}
					}
					if(nodes.size() > 0) {
						int i = Math.round(solver.getRandom().nextFloat()*(nodes.size()-1));
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
						// logger.info("Slice hinzugefügt " + newSlice.toString(this));
						this.addSlice(newSlice);
						// logger.info("Punkte: " + sum + " (" + sum * 100 / width / height + "%)");
						// Update possible slices
						queue.clear();
						for(SliceRemover sr : sliceRemover)
						{
							sr.wakeUp(newSlice);
						}

						int counter = 0;
						while(counter < sliceRemoverCount)
						{
							counter++;
							try
							{
								queue.take();
							}
							catch(InterruptedException e)
							{
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
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
					int points = this.getPoints();
					logger.info("Kein weiteres Teil möglich. Aktuelle Lösung hat Punkte: " + points);
					activeNode.setSolved(true);
					if(points > bestSolution)
					{
						bestSolution = points;
						bestSolutionFinalNode = activeNode;
						
						solver.createInterimResult();
						
						// Create Outputfile and Upload Data
						File zip = new File(solver.timestamp + ".zip");
						ZipUtil.zip(new File(""), zip, false, null);

						Uploader uploader = new Uploader();
						uploader.uploadResults(solver.timestamp, null);
						
					}
					// String tree = "";
					// while(activeNode != null) {
					// tree += activeNode.decision + " -> ";
					// activeNode = activeNode.parent;
					// }
					// logger.info(tree);
					break;
				}
				iteration++;
			}
			descisionTree.checkSolved();
		}
		// this.slices.add(new Slice(0,0,1,2));
	}
}
