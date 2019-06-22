package gingerninjas.julian.pizza;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.ThreadContext;

import gingerninjas.BaseSolver;

public class Solver extends BaseSolver<Input, Output>
{
	private static final int	AVAILABLE_TOPPINGS	= 2;
	private Integer[]			validEdgeLengths;
	private int					ratingRange;
	private static final int	RANGE_STEP			= 1;

	private Slice[][]			slicesPerIndex;
	private List<Slice>			sliceList;

	@Override
	protected Input createInput(File path) throws IOException
	{
		return new Input(path, name);
	}

	@Override
	protected Output createOutput(File path, boolean load)
	{
		return new Output(path, name);
	}

	@Override
	protected void solve()
	{
		logger.info("rows: " + input.getRows());
		logger.info("cols: " + input.getCols());
		logger.info("minToppingAmount: " + input.getMinToppingAmount());
		logger.info("maxSize: " + input.getMaxSize());

		// max score berechnen
		int maxScore = 0;
		for(char[] c : input.getPizza())
			maxScore += c.length;
		this.input.setMaxScore(maxScore);

		List<Integer> validEdgeLengthsTmp = new ArrayList<Integer>();
		int minSize = input.getMinToppingAmount() * AVAILABLE_TOPPINGS;
		int maxSize = input.getMaxSize();
		for(int i = 1; i <= maxSize; i++)
		{
			for(int s = minSize; s <= maxSize; s++)
			{
				if(s % i == 0)
				{
					validEdgeLengthsTmp.add(i);
					break;
				}
			}
		}
		validEdgeLengths = validEdgeLengthsTmp.toArray(new Integer[0]);

		logger.debug("valid edge lengths: " + validEdgeLengthsTmp);
		ratingRange = validEdgeLengths[validEdgeLengths.length - 1];
		logger.debug("rating range:       " + ratingRange);

		// init
		sliceList = new LinkedList<Slice>();
		slicesPerIndex = new Slice[input.getRows()][input.getCols()];
		int score;
		List<Slice> possibles;

		// phase 1 - create random slices
		logger.debug("phase 1 - creating random slices");
		// phase 1.1 - shuffle all possible indexes
		List<Index> indexes = getIndexes(true);
		// Collections.shuffle(indexes, this.random);
		// phase 1.2 - create totally random slices
		for(Index i : indexes)
		{
			possibles = createPossibleSlices(i.r, i.c, i.r, i.c);
			logger.debug("row=" + i.r + " col=" + i.c + " possibles=" + possibles.size());
			applyPossibles(0, null, possibles, true);
		}
		score = getScore(sliceList);
		logger.debug("phase 1 - score=" + score);

		// if(logger.isDebugEnabled())
		printSlices();

		// phase 1 - create random slices
		logger.debug("phase 2 - rating indexes by quality");
		for(Index i : indexes)
		{
			rateIndex(i);
		}
		Collections.sort(indexes, new Comparator<Index>() {
			@Override
			public int compare(Index o1, Index o2)
			{
				return (int) Math.signum(o1.getQuality() - o2.getQuality());
			}
		});
		logger.debug("phase 2 - worst quality=" + sliceList.get(0).getQuality());

		// phase 3 - optimize
		logger.debug("phase 3 - optimize");
		int cycle = 0;
		int clearRange = RANGE_STEP;
		int previousScore;
		Index i;
		List<Index> blackList = new LinkedList<>();
		List<Slice> removed;
		boolean nothingDone = false;
		while(!abortRequested)
		{
			cycle++;
			nothingDone = false;

			logger.debug(sliceList);
			if(logger.isDebugEnabled())
				printSlices();

			// phase 3.1 - optimize current slice
			logger.debug("phase 3.1 - optimizing slice");
			i = indexes.get(0);
			removed = clear(i.r - clearRange, i.c - clearRange, i.r + clearRange, i.c + clearRange);
			previousScore = getScore(removed);
			possibles = createPossibleSlices(i.r - clearRange - 1, i.c - clearRange - 1, i.r + clearRange + 1, i.c + clearRange + 1);
			logger.debug("cycle #" + cycle + " index=" + i.r + "|" + i.c + " removed=" + removed + " score=" + previousScore + " new possibles=" + possibles.size());
			logger.debug(possibles);
			nothingDone = !applyPossibles(cycle, null, possibles, true);

			// phase 3.2 - rerate slices
			logger.debug("phase 3.2 - rerating affected slices by quality");
			rateRange(i.r - clearRange, i.c - clearRange, i.r + clearRange, i.c + clearRange);
			Collections.sort(indexes, new Comparator<Index>() {
				@Override
				public int compare(Index o1, Index o2)
				{
					return (int) Math.signum(o1.getQuality() - o2.getQuality());
				}
			});
			score = getScore(sliceList);
			logger.debug("phase 3.2 - new worst quality=" + indexes.get(0).getQuality());
			if(nothingDone)
			{
				if(!blackList.contains(i))
					blackList.add(i);
			}
			else if(score == (int) output.getScore())
			{
				if(!blackList.contains(i))
					blackList.add(i);
			}
			else
			{
//				blackList.clear();
			}
			// move blacklist to end of list
			indexes.removeAll(blackList);
			indexes.addAll(blackList);
			logger.debug("phase 3.2 - new peek quality=" + indexes.get(0).getQuality());

			// update result
			score = getScore(sliceList);
			logger.debug("cycle #" + cycle + " score=" + score + " previous=" + (int) output.getScore() + " range=" + clearRange + " change?" + !nothingDone + "   (best="
					+ highscore + ")");
			if(score - (int) output.getScore() > 0)
				logger.info("cycle #" + cycle + " score=" + score + " range=" + clearRange + " bl=" + blackList.size());
			output.setSlices(sliceList);
			output.setScore(score);

			intermediateCheck();

			if(score > highscore)
			{
				highscore = score;
				output.publish();
			}
			else if(cycle % 10000 == 0)
			{
				output.publish();
			}

			if(score == maxScore)
				break;
			if(blackList.size() == input.getMaxScore())
			{
				clearRange += RANGE_STEP;
				blackList.clear();
			}
			if(clearRange > Math.max(input.getRows(), input.getCols()))
			{
				break;
			}
		}

		// update result
		score = getScore(sliceList);
		logger.info("cycle #" + cycle + " score=" + score + "   previous=" + (int) output.getScore() + "   change?" + !nothingDone + "   (best="
				+ highscore + ")");
		output.setSlices(sliceList);
		output.setScore(score);
		output.publish();

		logger.info("slices: " + output.getSlices().size());
	}

	public List<Index> getIndexes(boolean spiral)
	{
		List<Index> indexes = new LinkedList<>();
		if(spiral)
		{
			int row = 0;
			int col = 0;
			int dr = 1;
			int dc = 0;
			int minr = 0;
			int maxr = input.getRows() - 1;
			int minc = 1;
			int maxc = input.getCols() - 1;

			do
			{
				indexes.add(new Index(row, col));

				row += dr;
				col += dc;

				if(dr == 1 && row == maxr)
				{
					dr = 0;
					dc = +1;
					maxr--;
				}
				else if(dr == -1 && row == minr)
				{
					dr = 0;
					dc = -1;
					minr++;
				}
				else if(dc == 1 && col == maxc)
				{
					dc = 0;
					dr = -1;
					maxc--;
				}
				else if(dc == -1 && col == minc)
				{
					dc = 0;
					dr = +1;
					minc++;
				}
			} while((minr <= maxr) && (minc <= maxc));
		}
		else
		{
			for(int row = 0; row < input.getRows(); row++)
			{
				for(int col = 0; col < input.getCols(); col++)
				{
					indexes.add(new Index(row, col));
				}
			}
		}
		return indexes;
	}

	public double rateSlice(Slice s)
	{
		int count = 0;
		int quality = 0;
		for(int row = Math.max(0, s.getStartRow() - ratingRange); row < s.getEndRow() + ratingRange && row < input.getRows() - 1; row++)
		{
			for(int col = Math.max(0, s.getStartCol() - ratingRange); col < s.getEndCol() + ratingRange && col < input.getCols() - 1; col++)
			{
				count++;
				if(slicesPerIndex[row][col] != null)
					quality++;
			}
		}
		s.setQuality(quality / (double) count);
		return s.getQuality();
	}

	public double rateIndex(Index i)
	{
		int count = 0;
		int quality = 0;
		for(int row = Math.max(0, i.r - ratingRange); row < i.r + ratingRange && row < input.getRows() - 1; row++)
		{
			for(int col = Math.max(0, i.c - ratingRange); col < i.c + ratingRange && col < input.getCols() - 1; col++)
			{
				count++;
				if(slicesPerIndex[row][col] != null)
					quality++;
			}
		}
		i.setQuality(quality / (double) count);
		return i.getQuality();
	}

	public void rateRange(int startRow, int startCol, int endRow, int endCol)
	{
		startRow -= ratingRange;
		if(startRow < 0)
			startRow = 0;

		startCol -= ratingRange;
		if(startCol < 0)
			startCol = 0;

		endRow += ratingRange;
		if(endRow > input.getRows() - 1)
			endRow = input.getRows() - 1;

		endCol += ratingRange;
		if(endCol > input.getCols() - 1)
			endCol = input.getCols() - 1;

		for(int row = startRow; row <= endRow; row++)
		{
			for(int col = startCol; col <= endCol; col++)
			{
				if(slicesPerIndex[row][col] != null)
					rateSlice(slicesPerIndex[row][col]);
			}
		}
	}

	public boolean checkSliceAgainstCombo(Slice s, List<Slice> combo)
	{
		for(Slice check : combo)
		{
			if(s.isOverlapping(check))
				return false;
		}
		return true;
	}

	public boolean checkSlice(Slice s)
	{
		if(s.getStartRow() < 0 || s.getEndRow() >= input.getRows())
			return false;
		if(s.getStartCol() < 0 || s.getEndCol() >= input.getCols())
			return false;
		for(int row = s.getStartRow(); row <= s.getEndRow(); row++)
		{
			for(int col = s.getStartCol(); col <= s.getEndCol(); col++)
			{
				if(slicesPerIndex[row][col] != null)
				{
					return false;
				}
			}
		}
		return true;
	}

	public boolean addSlice(Slice s)
	{
		if(!checkSlice(s))
			return false;
		for(int row = s.getStartRow(); row <= s.getEndRow(); row++)
		{
			for(int col = s.getStartCol(); col <= s.getEndCol(); col++)
			{
				slicesPerIndex[row][col] = s;
			}
		}
		this.sliceList.add(s);
		return true;
	}

	public void removeSlice(Slice s)
	{
		if(!this.sliceList.contains(s))
			return;
		for(int row = s.getStartRow(); row <= s.getEndRow(); row++)
		{
			for(int col = s.getStartCol(); col <= s.getEndCol(); col++)
			{
				if(slicesPerIndex[row][col] == s || slicesPerIndex[row][col].equals(s))
					slicesPerIndex[row][col] = null;
			}
		}
		this.sliceList.remove(s);
	}

	public List<Slice> clear(int startRow, int startCol, int endRow, int endCol)
	{
		if(startRow < 0)
			startRow = 0;
		if(startCol < 0)
			startCol = 0;
		if(endRow > input.getRows() - 1)
			endRow = input.getRows() - 1;
		if(endCol > input.getCols() - 1)
			endCol = input.getCols() - 1;

		List<Slice> removed = new LinkedList<>();

		for(int row = startRow; row <= endRow; row++)
		{
			for(int col = startCol; col <= endCol; col++)
			{
				if(slicesPerIndex[row][col] != null)
				{
					removed.add(slicesPerIndex[row][col]);
					removeSlice(slicesPerIndex[row][col]);
				}
			}
		}
		return removed;
	}

	public List<Slice> createPossibleSlices(int startRow, int startCol, int endRow, int endCol)
	{
		List<Slice> slices = new ArrayList<>();

		Slice s;
		int w, h;

		// logger.debug("r=" + i.r + ",c=" + i.c);
		for(int ih = 0; ih < validEdgeLengths.length; ih++)
		{
			h = validEdgeLengths[ih];
			for(int iw = 0; iw < validEdgeLengths.length; iw++)
			{
				w = validEdgeLengths[iw];
				if(w * h > input.getMaxSize())
					break;

				// logger.debug(w + " x " + h);
				for(int dr = -h + 1; dr <= endRow - startRow; dr++)
				{
					for(int dc = -w + 1; dc <= endCol - startCol; dc++)
					{
						s = new Slice(startRow + dr, startCol + dc, startRow + dr + h, startCol + dc + w);
						// logger.debug(s);
						if(!checkSlice(s))
							continue;
						if(!s.isValid(input))
							continue;
						slices.add(s);
					}
				}
			}
		}

		return slices;
	}

	public List<List<Slice>> findBestCombos(int cycle, List<Slice> possibles)
	{
		int bestScore, testScore;
		List<List<Slice>> bestCombos = new LinkedList<List<Slice>>();
		List<Slice> testCombo;
		Map<Integer, List<List<Slice>>> combos;
		List<List<Slice>> previousCombos;

		if(possibles.size() == 1)
		{
			testCombo = new LinkedList<>();
			testCombo.add(possibles.get(0));
			bestCombos.add(testCombo);
		}
		else if(possibles.size() > 1)
		{
			bestScore = 0;

			combos = new HashMap<>();
			combos.put(0, new LinkedList<>()); // add an empty list for 0 slice-combos for help
			combos.get(0).add(new LinkedList<Slice>()); // add an empty combo
			boolean comboFound;
			for(int i = 1; i <= possibles.size(); i++)
			{
				// find all combos with i slices
				comboFound = false;
				combos.put(i, new LinkedList<>());

				previousCombos = combos.get(i - 1);
				for(List<Slice> combo : previousCombos)
				{
					for(Slice candidate : possibles)
					{
						if(combo.contains(candidate))
							continue;
						if(!checkSliceAgainstCombo(candidate, combo))
							continue;

						testCombo = new LinkedList<>(combo);
						testCombo.add(candidate);
						combos.get(i).add(testCombo);

						comboFound = true;

						testScore = getScore(testCombo);
						logger.debug("cycle #" + cycle + " combo=" + testCombo + " score=" + testScore + " best=" + bestScore);
						if(testScore > bestScore)
						{
							bestScore = testScore;
							bestCombos.clear();
							bestCombos.add(testCombo);
						}
						else if(testScore == bestScore)
						{
							bestCombos.add(testCombo);
						}
					}
				}

				logger.debug("cycle #" + cycle + " combos with " + i + " slices = " + combos.get(i));

				// if we don't find a combo with i slices we won't find any with i+1...
				if(!comboFound)
					break;
			}
		}
		else
		{
			logger.error("something went wrong - no possibles found...");
		}
		return bestCombos;
	}

	public boolean applyPossibles(int cycle, Slice original, List<Slice> possibles, boolean findCombos)
	{
		boolean nothingDone = false;
		Slice candidate;
		List<List<Slice>> bestCombos;
		if(possibles.size() == 1)
		{
			candidate = possibles.get(0);
			addSlice(possibles.get(0));
			nothingDone = (candidate.equals(original));
		}
		else if(possibles.size() > 1 && findCombos)
		{
			bestCombos = findBestCombos(cycle, possibles);
			if(bestCombos.size() > 0)
			{
				List<Slice> bestCombo = bestCombos.get(this.random.nextInt(bestCombos.size()));
				logger.debug("cycle #" + cycle + " combo found=" + bestCombo + " score=" + getScore(bestCombo));
				for(Slice s2 : bestCombo)
					addSlice(s2);

				if(bestCombo.size() == 1 && bestCombo.get(0).equals(original))
					nothingDone = true;
			}
			else
			{
				logger.error("something went wrong - no combo found...");
				nothingDone = true;
			}
		}
		else if(possibles.size() > 1)
		{
			candidate = possibles.get(this.random.nextInt(possibles.size()));
			addSlice(candidate);
			nothingDone = (candidate.equals(original));
		}
		else
		{
			// logger.error("something went wrong - no possibles found...");
			nothingDone = true;
		}
		return !nothingDone;
	}

	private void intermediateCheck()
	{
		int[][] sliceCountPerIndex = new int[input.getRows()][input.getCols()];
		for(Slice s : sliceList)
		{
			for(int row = s.getStartRow(); row <= s.getEndRow(); row++)
			{
				for(int col = s.getStartCol(); col <= s.getEndCol(); col++)
				{
					sliceCountPerIndex[row][col]++;
				}
			}
		}

		for(int row = 0; row < input.getRows(); row++)
		{
			for(int col = 0; col < input.getCols(); col++)
			{
				if(sliceCountPerIndex[row][col] > 1)
					logger.error("overlap @ row=" + row + " col=" + col);
			}
		}
	}

	private void printSlices()
	{
		for(int row = 0; row < input.getRows(); row++)
		{
			for(int col = 0; col < input.getCols(); col++)
			{
				System.out.print((slicesPerIndex[row][col] != null) ? "x" : "o");
			}
			System.out.println("");
		}
	}

	private int getScore(List<Slice> slices)
	{
		int score = 0;
		for(Slice s : slices)
			score += s.getSize();
		return score;
	}

	private class Index
	{
		private int		r;
		private int		c;
		private double	q;

		public Index(int r, int c)
		{
			super();
			this.r = r;
			this.c = c;
		}

		public int getR()
		{
			return r;
		}

		public int getC()
		{
			return c;
		}

		public double getQuality()
		{
			return q;
		}

		public void setQuality(double q)
		{
			this.q = q;
		}
	}

	public static void main(String[] args) throws IOException
	{
		ThreadContext.put("filename", "main.log");

		File in = new File("Test Round/3 - Medium.in");

		Solver s = new Solver();
		s.name = in.getName().replace(".in", "");
		s.input = s.createInput(in.getParentFile());
		s.output = s.createOutput(in.getParentFile(), false);
		s.sliceList = new LinkedList<Slice>();
		s.slicesPerIndex = new Slice[s.input.getRows()][s.input.getCols()];

		System.out.println("rows=" + s.input.getRows());
		System.out.println("cols=" + s.input.getCols());

		s.addSlice(new Slice(5, 5, 10, 10));
		System.out.println(s.sliceList);
		s.addSlice(new Slice(1, 1, 4, 4));
		System.out.println(s.sliceList);
		s.addSlice(new Slice(1, 1, 4, 5));
		System.out.println(s.sliceList);
		s.addSlice(new Slice(1, 1, 5, 4));
		System.out.println(s.sliceList);
		s.addSlice(new Slice(1, 1, 5, 5));
		System.out.println(s.sliceList);
		s.addSlice(new Slice(11, 1, 15, 4));
		System.out.println(s.sliceList);
		s.addSlice(new Slice(10, 1, 15, 4));
		System.out.println(s.sliceList);
		s.addSlice(new Slice(11, 1, 15, 5));
		System.out.println(s.sliceList);
		s.addSlice(new Slice(10, 1, 15, 5));
		System.out.println(s.sliceList);
		s.addSlice(new Slice(1, 11, 4, 15));
		System.out.println(s.sliceList);
		s.addSlice(new Slice(1, 11, 5, 15));
		System.out.println(s.sliceList);
		s.addSlice(new Slice(1, 10, 4, 15));
		System.out.println(s.sliceList);
		s.addSlice(new Slice(1, 10, 5, 15));
		System.out.println(s.sliceList);
		s.addSlice(new Slice(11, 11, 15, 15));
		System.out.println(s.sliceList);
		s.addSlice(new Slice(10, 11, 15, 15));
		System.out.println(s.sliceList);
		s.addSlice(new Slice(11, 10, 15, 15));
		System.out.println(s.sliceList);
		s.addSlice(new Slice(10, 10, 15, 15));
		System.out.println(s.sliceList);
		s.addSlice(new Slice(2, 5, 5, 10));
		System.out.println(s.sliceList);

		s.printSlices();

		s.removeSlice(new Slice(1, 1, 4, 4));
		System.out.println(s.sliceList);

		s.printSlices();

		System.out.println(s.checkSlice(new Slice(1, 1, 4, 4)));
		System.out.println(s.checkSlice(new Slice(1, 1, 4, 5)));
		System.out.println(s.checkSlice(new Slice(1, 1, 5, 4)));
		System.out.println(s.checkSlice(new Slice(1, 1, 5, 5)));
		System.out.println(s.checkSlice(new Slice(11, 1, 15, 4)));
		System.out.println(s.checkSlice(new Slice(10, 1, 15, 4)));
		System.out.println(s.checkSlice(new Slice(11, 1, 15, 5)));
		System.out.println(s.checkSlice(new Slice(10, 1, 15, 5)));
		System.out.println(s.checkSlice(new Slice(1, 11, 4, 15)));
		System.out.println(s.checkSlice(new Slice(1, 11, 5, 15)));
		System.out.println(s.checkSlice(new Slice(1, 10, 4, 15)));
		System.out.println(s.checkSlice(new Slice(1, 10, 5, 15)));
		System.out.println(s.checkSlice(new Slice(11, 11, 15, 15)));
		System.out.println(s.checkSlice(new Slice(10, 11, 15, 15)));
		System.out.println(s.checkSlice(new Slice(11, 10, 15, 15)));
		System.out.println(s.checkSlice(new Slice(10, 10, 15, 15)));

		System.out.println("---");
		s.intermediateCheck();
		System.out.println("---");

		s.sliceList.add(new Slice(2, 2, 14, 14));

		System.out.println("---");
		s.intermediateCheck();
		System.out.println("---");
	}
}
