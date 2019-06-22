package gingerninjas.julian.pizza;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import gingerninjas.BaseSolver;

public class Solver extends BaseSolver<Input, Output>
{
	private static final int	NUM_TRIES			= 500;
	private static final int	AVAILABLE_TOPPINGS	= 2;
	private List<Integer>		validEdgeLengths;

	@Override
	protected Input createInput(String name)
	{
		return new Input(in.getName());
	}

	@Override
	protected Output createOutput(String name, Map<String, Object> args)
	{
		return new Output(in.getName(), args);
	}

	@Override
	protected Output solve(Input in)
	{

		Map<String, Object> args = new HashMap<>();
		Output output = createOutput(in.getName(), args);

		logger.info("rows: " + input.getRows());
		logger.info("cols: " + input.getCols());
		logger.info("minToppingAmount: " + input.getMinToppingAmount());
		logger.info("maxSize: " + input.getMaxSize());

		// max score berechnen
		int maxScore = 0;
		for(char[] c : input.getPizza())
			maxScore += c.length;
		this.input.setMaxScore(maxScore);

		validEdgeLengths = new ArrayList<Integer>();
		int minSize = input.getMinToppingAmount() * AVAILABLE_TOPPINGS;
		int maxSize = input.getMaxSize();
		for(int i = 1; i <= maxSize; i++)
		{
			for(int s = minSize; s <= maxSize; s++)
			{
				if(s % i == 0)
				{
					validEdgeLengths.add(i);
					break;
				}
			}
		}

		logger.debug("valid edge lengths: " + validEdgeLengths);

		List<Slice> slices;
		List<Slice> bestSlices = null;
		int score;
		int bestScore = 0;
		for(int i = 0; i < NUM_TRIES; i++)
		{
			slices = slice(input, 0, 0, input.getRows() - 1, input.getCols() - 1);
			score = getScore(slices);
			logger.info("cycle #" + i + " score=" + score + "   (best=" + bestScore + ")");
			if(score > bestScore)
			{
				bestScore = score;
				bestSlices = slices;
			}

			if(this.abortRequested)
				break;
		}

		if(bestSlices != null)
		{
			output.setSlices(bestSlices);
			output.setScore(bestScore);
		}

		logger.info("slices: " + output.getSlices().size());

		return output;
	}

	protected List<Slice> slice(Input input, int startRow, int startCol, int endRow, int endCol)
	{
		logger.debug("slicing: " + new Slice(startRow, startCol, endRow, endCol));
		List<Slice> slices = new LinkedList<Slice>();

		int rows;
		int cols;

		do
		{
			cols = endCol - startCol + 1;
			rows = endRow - startRow + 1;

			if(rows <= 0 || cols <= 0)
				break;

			NamedMap<Integer, List<Slice>> rowResultsTop = new NamedMap<Integer, List<Slice>>("top");
			NamedMap<Integer, List<Slice>> rowResultsBottom = new NamedMap<Integer, List<Slice>>("bottom");
			NamedMap<Integer, List<Slice>> colResultsLeft = new NamedMap<Integer, List<Slice>>("left");
			NamedMap<Integer, List<Slice>> colResultsRight = new NamedMap<Integer, List<Slice>>("right");
			NamedMap<Integer, List<Slice>> rowResultsTopReverse = new NamedMap<Integer, List<Slice>>("topReverse");
			NamedMap<Integer, List<Slice>> rowResultsBottomReverse = new NamedMap<Integer, List<Slice>>("bottomReverse");
			NamedMap<Integer, List<Slice>> colResultsLeftReverse = new NamedMap<Integer, List<Slice>>("leftReverse");
			NamedMap<Integer, List<Slice>> colResultsRightReverse = new NamedMap<Integer, List<Slice>>("rightReverse");

			List<NamedMap<Integer, List<Slice>>> allMaps = new ArrayList<NamedMap<Integer, List<Slice>>>(4);
			allMaps.add(rowResultsTop);
			allMaps.add(rowResultsBottom);
			allMaps.add(colResultsLeft);
			allMaps.add(colResultsRight);
			allMaps.add(rowResultsTopReverse);
			allMaps.add(rowResultsBottomReverse);
			allMaps.add(colResultsLeftReverse);
			allMaps.add(colResultsRightReverse);
			
			// etwas Zufall reinbringen
			Collections.shuffle(allMaps, this.random);
			Collections.shuffle(validEdgeLengths, this.random);

			int minUnusedCells = Integer.MAX_VALUE;
			int bestEdgeLength = 0;
			NamedMap<Integer, List<Slice>> bestResultList = null;

			int score;
			int size;
			int unusedCells = 0;
			boolean calculationDone = false;
			for(Integer e : validEdgeLengths)
			{
				for(NamedMap<Integer, List<Slice>> m : allMaps)
				{
					calculationDone = false;

					if(m == rowResultsTop && startRow + e - 1 <= endRow)
					{
						m.put(e, sliceRows(input, startRow, startCol, startRow + e - 1, endCol));
						score = getScore(m.get(e));
						size = (endCol - startCol + 1) * (e);
						unusedCells = size - score;
						logger.debug(e + "@top: unusedCells=" + unusedCells);
						calculationDone = true;
					}

					if(m == rowResultsBottom && endRow - e + 1 >= startRow)
					{
						m.put(e, sliceRows(input, endRow - e + 1, startCol, endRow, endCol));
						score = getScore(m.get(e));
						size = (endCol - startCol + 1) * (e);
						unusedCells = size - score;
						logger.debug(e + "@bottom: unusedCells=" + unusedCells);
						calculationDone = true;
					}

					if(m == colResultsLeft && startCol + e - 1 <= endCol)
					{
						m.put(e, sliceCols(input, startRow, startCol, endRow, startCol + e - 1));
						score = getScore(m.get(e));
						size = (endRow - startRow + 1) * (e);
						unusedCells = size - score;
						logger.debug(e + "@left: unusedCells=" + unusedCells);
						calculationDone = true;
					}

					if(m == colResultsRight && endCol - e + 1 >= startCol)
					{
						m.put(e, sliceCols(input, startRow, endCol - e + 1, endRow, endCol));
						score = getScore(m.get(e));
						size = (endRow - startRow + 1) * (e);
						unusedCells = size - score;
						logger.debug(e + "@right: unusedCells=" + unusedCells);
						calculationDone = true;
					}

					if(m == rowResultsTopReverse && startRow + e - 1 <= endRow)
					{
						m.put(e, sliceRowsReverse(input, startRow, startCol, startRow + e - 1, endCol));
						score = getScore(m.get(e));
						size = (endCol - startCol + 1) * (e);
						unusedCells = size - score;
						logger.debug(e + "@topReverse: unusedCells=" + unusedCells);
						calculationDone = true;
					}

					if(m == rowResultsBottomReverse && endRow - e + 1 >= startRow)
					{
						m.put(e, sliceRowsReverse(input, endRow - e + 1, startCol, endRow, endCol));
						score = getScore(m.get(e));
						size = (endCol - startCol + 1) * (e);
						unusedCells = size - score;
						logger.debug(e + "@bottomReverse: unusedCells=" + unusedCells);
						calculationDone = true;
					}

					if(m == colResultsLeftReverse && startCol + e - 1 <= endCol)
					{
						m.put(e, sliceColsReverse(input, startRow, startCol, endRow, startCol + e - 1));
						score = getScore(m.get(e));
						size = (endRow - startRow + 1) * (e);
						unusedCells = size - score;
						logger.debug(e + "@leftReverse: unusedCells=" + unusedCells);
						calculationDone = true;
					}

					if(m == colResultsRightReverse && endCol - e + 1 >= startCol)
					{
						m.put(e, sliceColsReverse(input, startRow, endCol - e + 1, endRow, endCol));
						score = getScore(m.get(e));
						size = (endRow - startRow + 1) * (e);
						unusedCells = size - score;
						logger.debug(e + "@rightReverse: unusedCells=" + unusedCells);
						calculationDone = true;
					}

					if(calculationDone)
					{
						if(unusedCells < minUnusedCells)
						{
							bestResultList = m;
							minUnusedCells = unusedCells;
							bestEdgeLength = e;
						}
					}
				}
			}

			logger.debug("best edge length: " + bestEdgeLength);
			logger.debug("minUnusedCells:   " + minUnusedCells);

			if(bestEdgeLength != 0)
			{
				logger.debug("best result list: " + bestResultList.getName());
				for(Slice s : bestResultList.get(bestEdgeLength))
				{
					logger.debug(s);
				}
				slices.addAll(bestResultList.get(bestEdgeLength));

				if(bestResultList == rowResultsTop || bestResultList == rowResultsTopReverse)
					startRow += bestEdgeLength;
				else if(bestResultList == rowResultsBottom || bestResultList == rowResultsBottomReverse)
					endRow -= bestEdgeLength;
				else if(bestResultList == colResultsRight || bestResultList == colResultsRightReverse)
					endCol -= bestEdgeLength;
				else if(bestResultList == colResultsLeft || bestResultList == colResultsLeftReverse)
					startCol += bestEdgeLength;
			}
			else
			{
				break;
			}
		} while(true);

		return slices;
	}

	private List<Slice> sliceRows(Input input, int startRow, int startCol, int endRow, int endCol)
	{
		logger.debug("slicing rows: " + new Slice(startRow, startCol, endRow, endCol));
		List<Slice> slices = new LinkedList<Slice>();
		int startI = startCol;
		int endI;
		Slice currentSlice = null;
		Slice previousSlice = null;
		Slice tmpSlice;

		while(startI <= endCol)
		{
			previousSlice = currentSlice;
			endI = startI;
			currentSlice = new Slice(startRow, startI, endRow, endI);
			while(!currentSlice.isValid(input) && currentSlice.getSize() < input.getMaxSize() && endI < endCol)
			{
				endI++;
				currentSlice.setEndCol(endI);
			}

			logger.debug(currentSlice + " valid?" + currentSlice.isValid(input) + " size=" + currentSlice.getSize() + " endI=" + endI);
			if(currentSlice.isValid(input))
			{
				slices.add(currentSlice);
				startI = endI + 1;
			}
			else if(previousSlice != null && previousSlice.getSize() < input.getMaxSize() && previousSlice.getEndCol() < endCol)
			{
				tmpSlice = new Slice(previousSlice.getStartRow(), previousSlice.getStartCol(), previousSlice.getEndRow(),
						previousSlice.getEndCol() + 1);
				logger.debug(tmpSlice + " valid?" + tmpSlice.isValid(input) + " size=" + tmpSlice.getSize());
				if(tmpSlice.isValid(input))
				{
					previousSlice.setStartRow(tmpSlice.getStartRow());
					previousSlice.setStartCol(tmpSlice.getStartCol());
					previousSlice.setEndRow(tmpSlice.getEndRow());
					previousSlice.setEndCol(tmpSlice.getEndCol());
					currentSlice = previousSlice;
				}
				startI++;
			}
			else
			{
				startI++;
			}
		}
		return slices;
	}

	private List<Slice> sliceCols(Input input, int startRow, int startCol, int endRow, int endCol)
	{
		logger.debug("slicing cols: " + new Slice(startRow, startCol, endRow, endCol));
		List<Slice> slices = new LinkedList<Slice>();
		int startI = startRow;
		int endI;
		Slice currentSlice = null;
		Slice previousSlice = null;
		Slice tmpSlice;

		while(startI <= endRow)
		{
			previousSlice = currentSlice;
			endI = startI;
			currentSlice = new Slice(startI, startCol, endI, endCol);
			while(!currentSlice.isValid(input) && currentSlice.getSize() < input.getMaxSize() && endI < endRow)
			{
				endI++;
				currentSlice.setEndRow(endI);
			}

			logger.debug(currentSlice + " valid?" + currentSlice.isValid(input) + " size=" + currentSlice.getSize() + " endI=" + endI);
			if(currentSlice.isValid(input))
			{
				slices.add(currentSlice);
				startI = endI + 1;
			}
			else if(previousSlice != null && previousSlice.getSize() < input.getMaxSize() && previousSlice.getEndRow() < endRow)
			{
				tmpSlice = new Slice(previousSlice.getStartRow(), previousSlice.getStartCol(), previousSlice.getEndRow() + 1,
						previousSlice.getEndCol());
				if(tmpSlice.isValid(input))
				{
					previousSlice.setStartRow(tmpSlice.getStartRow());
					previousSlice.setStartCol(tmpSlice.getStartCol());
					previousSlice.setEndRow(tmpSlice.getEndRow());
					previousSlice.setEndCol(tmpSlice.getEndCol());
					currentSlice = previousSlice;
				}
				startI++;
			}
			else
			{
				startI++;
			}
		}
		logger.debug(slices);
		return slices;
	}

	private List<Slice> sliceRowsReverse(Input input, int startRow, int startCol, int endRow, int endCol)
	{
		logger.debug("slicing rows: " + new Slice(startRow, startCol, endRow, endCol));
		List<Slice> slices = new LinkedList<Slice>();
		int startI;
		int endI = endCol;
		Slice currentSlice = null;
		Slice previousSlice = null;
		Slice tmpSlice;

		while(endI >= startCol)
		{
			previousSlice = currentSlice;
			startI = endI;
			currentSlice = new Slice(startRow, startI, endRow, endI);
			while(!currentSlice.isValid(input) && currentSlice.getSize() < input.getMaxSize() && startI > startCol)
			{
				startI--;
				currentSlice.setStartCol(startI);
			}

			logger.debug(currentSlice + " valid?" + currentSlice.isValid(input) + " size=" + currentSlice.getSize() + " endI=" + endI);
			if(currentSlice.isValid(input))
			{
				slices.add(currentSlice);
				endI = startI - 1;
			}
			else if(previousSlice != null && previousSlice.getSize() < input.getMaxSize() && previousSlice.getStartCol() > startCol)
			{
				tmpSlice = new Slice(previousSlice.getStartRow(), previousSlice.getStartCol() - 1, previousSlice.getEndRow(),
						previousSlice.getEndCol());
				logger.debug(tmpSlice + " valid?" + tmpSlice.isValid(input) + " size=" + tmpSlice.getSize());
				if(tmpSlice.isValid(input))
				{
					previousSlice.setStartRow(tmpSlice.getStartRow());
					previousSlice.setStartCol(tmpSlice.getStartCol());
					previousSlice.setEndRow(tmpSlice.getEndRow());
					previousSlice.setEndCol(tmpSlice.getEndCol());
					currentSlice = previousSlice;
				}
				endI--;
			}
			else
			{
				endI--;
			}
		}
		return slices;
	}

	private List<Slice> sliceColsReverse(Input input, int startRow, int startCol, int endRow, int endCol)
	{
		logger.debug("slicing cols: " + new Slice(startRow, startCol, endRow, endCol));
		List<Slice> slices = new LinkedList<Slice>();
		int startI;
		int endI = endRow;
		Slice currentSlice = null;
		Slice previousSlice = null;
		Slice tmpSlice;

		while(endI >= startRow)
		{
			previousSlice = currentSlice;
			startI = endI;
			currentSlice = new Slice(startI, startCol, endI, endCol);
			while(!currentSlice.isValid(input) && currentSlice.getSize() < input.getMaxSize() && startI > startCol)
			{
				startI--;
				currentSlice.setEndRow(endI);
			}

			logger.debug(currentSlice + " valid?" + currentSlice.isValid(input) + " size=" + currentSlice.getSize() + " endI=" + endI);
			if(currentSlice.isValid(input))
			{
				slices.add(currentSlice);
				endI = startI -1;
			}
			else if(previousSlice != null && previousSlice.getSize() < input.getMaxSize() && previousSlice.getStartRow() > startRow)
			{
				tmpSlice = new Slice(previousSlice.getStartRow() -1, previousSlice.getStartCol(), previousSlice.getEndRow(),
						previousSlice.getEndCol());
				if(tmpSlice.isValid(input))
				{
					previousSlice.setStartRow(tmpSlice.getStartRow());
					previousSlice.setStartCol(tmpSlice.getStartCol());
					previousSlice.setEndRow(tmpSlice.getEndRow());
					previousSlice.setEndCol(tmpSlice.getEndCol());
					currentSlice = previousSlice;
				}
				endI--;
			}
			else
			{
				endI--;
			}
		}
		logger.debug(slices);
		return slices;
	}

	private int getScore(List<Slice> slices)
	{
		int score = 0;
		for(Slice s : slices)
			score += s.getSize();
		return score;
	}

	private class NamedMap<K, V> extends HashMap<K, V>
	{
		private static final long	serialVersionUID	= 1L;
		private String				name;

		public NamedMap(String name)
		{
			super();
			this.name = name;
		}

		public String getName()
		{
			return name;
		}
	}
}
