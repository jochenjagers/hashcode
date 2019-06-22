package gingerninjas.jochen.pizza;

import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SliceRemover extends Thread
{
	protected transient final Logger		logger		= LogManager.getLogger(getClass());

	int										startRow;
	int										endRow;
	int										width;
	ArrayList<ArrayList<ArrayList<Slice>>>	data;
	BlockingQueue<Slice>					sleepQueue	= new LinkedBlockingQueue<>();

	boolean									running		= true;

	BlockingQueue<Integer>					resultQueue;

	SliceRemover(int startRow, int endRow, int width, ArrayList<ArrayList<ArrayList<Slice>>> data, BlockingQueue<Integer> queue)
	{
		this.startRow = startRow;
		this.endRow = endRow;
		this.data = data;
		this.resultQueue = queue;
		logger.info("Start: " + this.startRow + " End: " + this.endRow);
		this.width = width;
		this.start();
	}

	private void removeSlices(Slice s)
	{
		for(int y = Math.max(0, Math.max(s.y-s.pizza.maxSize, this.startRow)); y < Math.min(s.y+s.height+s.pizza.maxSize+2, this.endRow); ++y)
		{
			for(int x = Math.max(0, s.x - s.pizza.maxSize); x < Math.min(s.x+s.width+s.pizza.maxSize+1, this.width); ++x)
			{
				data.get(y).get(x).removeIf(i -> i.getPoints() == 0);
			}
		}
	}

	public void wakeUp(Slice newSlice)
	{
		try
		{
			sleepQueue.put(newSlice);
		}
		catch(InterruptedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void run()
	{
		while(this.running)
		{
			try
			{
				Slice s = sleepQueue.take();
				this.removeSlices(s);
			}
			catch(InterruptedException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try
			{
				resultQueue.put(0);
				//logger.info("remover fertig");
			}
			catch(InterruptedException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
