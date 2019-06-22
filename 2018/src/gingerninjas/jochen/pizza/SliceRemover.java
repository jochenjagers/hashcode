package gingerninjas.jochen.pizza;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SliceRemover extends Thread
{
	protected transient final Logger		logger			= LogManager.getLogger(getClass());

	static int								removerCount	= 0;
	static AtomicInteger					runningRemovers = new AtomicInteger(0);
	static Object							sync = new Object();

	BlockingQueue<Point>					queue;
	boolean									running			= true;
	ArrayList<ArrayList<LinkedList<Slice>>>	data;
	BlockingQueue<Integer>					waiter;
	Pizza									input;

	SliceRemover(Pizza input, BlockingQueue<Point> queue, BlockingQueue<Integer> waiter, ArrayList<ArrayList<LinkedList<Slice>>> data)
	{
		removerCount++;
		this.input = input;
		this.data = data;
		this.waiter = waiter;
		this.queue = queue;
		this.start();
	}

	public void run()
	{
		while(this.running)
		{
			try
			{
				Point p;
				synchronized(runningRemovers)
				{
					p = queue.take();
					runningRemovers.incrementAndGet();
				}
				data.get(p.y).get(p.x).removeIf(i -> i.getPoints() == 0);
				synchronized(sync)
				{
					runningRemovers.decrementAndGet();
					if(runningRemovers.get() == 0 && queue.size() == 0)
					{
						waiter.put(0);
					}
				}
			}
			catch(InterruptedException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
